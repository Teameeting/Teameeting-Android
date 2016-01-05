package org.dync.rtk.util;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.dync.rtk.util.LooperExecutor;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import android.content.Context;
import android.util.Log;

/**
 * 呼叫建立时的网络实现 By Dync.inc - 2015/7/20
 * 
 * @author Maozongwu
 *
 */
public class RtkNetwork {
	private boolean mDebug = false;
	private static final String TAG = "RtkP2PClient";
	/**
	 * "GET /sign_in?%s HTTP/1.0\r\n\r\n"
	 */
	private static final String kSignIn = "http://%s:%d/sign_in?%s";
	/**
	 * "GET /sign_out?peer_id=%s HTTP/1.0\r\n\r\n"
	 */
	private static final String kSignOut = "http://%s:%d/sign_out?peer_id=%s";
	private static final String kMessage = "http://%s:%d/message?peer_id=%s&to=%s";
	/**
	 * "GET /wait?peer_id=%s HTTP/1.0\r\n\r\n"
	 */
	private static final String kWait = "http://%s:%d/wait?peer_id=%s";

	/**
	 * Peer connection events.
	 */
	public static interface RtkSocketClientEvents {
		public void onConnected(String uid, String roomId);

		public void onMessage(String peerId, String message);

		public void onDisconnected(int errCode, String errResponse);

		public void onMemberJoin(String peerId);

		public void onMemberLevea(String peerId);

		public void onJoinOk();

	}

	public class HttpMsg {
		public String url;
		public String content;
	}

	private final Context mContext;
	private LooperExecutor mLooperExecutor;
	private SyncHttpClient mCtrlClient;
	private AsyncHttpClient mHandClient;
	private String mMyId;
	private String mStrIp;
	private int mPort;
	private boolean bConnected = false;
	private RtkSocketClientEvents mEvents;
	private final LinkedList<HttpMsg> wsSendQueue;
	private int mReConnectCount;

	public RtkNetwork(Context context, RtkSocketClientEvents event) {
		RtkUtils.assertIsTrue(null != context);
		RtkUtils.assertIsTrue(null != event);
		mContext = context;
		mEvents = event;
		{// * New all value;
			mLooperExecutor = new LooperExecutor();
			mLooperExecutor.requestStart();
			wsSendQueue = new LinkedList<HttpMsg>();
			mCtrlClient = new SyncHttpClient();
		}
	}

	public void Login(String ip, int port, final String roomId) {
		RtkUtils.assertIsTrue(null != ip && ip.length() > 0);
		RtkUtils.assertIsTrue(0 != port);
		this.mStrIp = ip;
		this.mPort = port;
		String url = String.format(kSignIn, ip, port, roomId);
		AsyncHttpClient httpClient = new AsyncHttpClient();
		httpClient.setTimeout(15 * 1000);
		httpClient.get(url, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				String errorResponseString = null;
				if (errorResponse != null) {
					errorResponseString = new String(errorResponse);
				}
				mEvents.onDisconnected(statusCode, errorResponseString);
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// TODO Auto-generated method stub
				String content = new String(response);
				String[] allUserArray = content.split("\n");

				for (int i = 1; i < allUserArray.length; ++i) {
					if (allUserArray[i].length() > 0) {
						handleServerNotification(allUserArray[i]);
					}
				}

				// 获取自己的ID
				String[] arr = allUserArray[0].split(",");
				mMyId = arr[1];
				bConnected = true;
				mEvents.onConnected(mMyId, roomId);

				startHangingGet();

				mEvents.onJoinOk();
			}
		});
	}

	public void Logout() {
		if (!bConnected)
			return;
		bConnected = false;
		mCtrlClient.cancelAllRequests(true);
		mHandClient.cancelAllRequests(true);
		synchronized (wsSendQueue) {
			wsSendQueue.clear();
		}
		String url = String.format(kSignOut, mStrIp, mPort, mMyId);
		AsyncHttpClient httpClient = new AsyncHttpClient();
		httpClient.setTimeout(15 * 1000);
		httpClient.get(url, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				String errorResponseString = null;
				if (errorResponse != null)
					errorResponseString = new String(errorResponse);
				mEvents.onDisconnected(statusCode, errorResponseString);
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// TODO Auto-generated method stub
				String errorResponseString = new String(response);
				mEvents.onDisconnected(statusCode, errorResponseString);
			}
		});

		mLooperExecutor.requestStop();
		mLooperExecutor = null;
	}

	public void SendMessage(final String peerId, final String msg) {
		if (!bConnected)
			return;
		String url = String.format(kMessage, mStrIp, mPort, mMyId, peerId);
		HttpMsg httpMsg = new HttpMsg();
		httpMsg.url = url;
		httpMsg.content = msg.replace("\r\n", "\\r\\n");
		synchronized (wsSendQueue) {
			wsSendQueue.add(httpMsg);
		}

		mLooperExecutor.execute(new Runnable() {
			@Override
			public void run() {
				notifySendMsg();
			}
		});
	}

	private void notifySendMsg() {
		HttpMsg item = null;
		synchronized (wsSendQueue) {
			if (wsSendQueue.size() > 0) {
				item = wsSendQueue.remove(0);
			}
		}
		if (item == null) {
			return;
		}
		try {
			mCtrlClient.post(mContext, item.url, new StringEntity(item.content), "text/plain",
					new AsyncHttpResponseHandler() {

						@Override
						public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
							// TODO Auto-generated method stub
						}
					});
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private void startHangingGet() {
		String url = String.format(kWait, mStrIp, mPort, mMyId);
		mHandClient = new AsyncHttpClient();
		mHandClient.setTimeout(60 * 1000);
		mHandClient.get(url, new AsyncHttpResponseHandler() {
			@Override
			public void onFailure(int statusCode, Header[] arg1, byte[] errorResponse, Throwable arg3) {
				// TODO Auto-generated method stub
				if (mDebug) {
					Log.e(TAG, " startHangingGet " + " onFailure: " + statusCode);
				}

				// Connection fails even three times
				if (statusCode == 0 && mReConnectCount < 3) {
					startHangingGet();					
				} else {
					mReConnectCount += 1;
					if (null != errorResponse) {
						String errorResponseString = new String(errorResponse);
						mEvents.onDisconnected(statusCode, errorResponseString);
					} else {
						mEvents.onDisconnected(statusCode, null);
					}
				}
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// TODO Auto-generated method stub
				// Pragma
				// The number of connection reset to 0
				mReConnectCount = 0;
				String peerId = "";
				String resp;
				for (int i = 0; i < headers.length; i++) {
					if (headers[i].getName().equals("Pragma")) {
						peerId = headers[i].getValue();
						break;
					}
				}
				resp = new String(response);
				if (mDebug) {
					Log.e(TAG, " startHangingGet " + " response " + resp);
				}
				if (mMyId.equals(peerId)) {
					// A notification about a new member or a member that just
					// disconnected.
					if (mDebug) {
						Log.e(TAG, " myId.equals(peerId) ");
					}
					handleServerNotification(resp);

				} else {
					mEvents.onMessage(peerId, resp);
				}

				restartHangingGet();
			}

		});
	}

	private void restartHangingGet() {
		if (bConnected) {
			startHangingGet();
		}
	}

	private void handleServerNotification(String data) {

		// console.log("Server notification: " + data);
		String[] parsed = data.split(",");
		String peerState = "1";
		String join = parsed[2].trim();
		if (mDebug) {
			Log.e(TAG, " handleServerNotification " + data);
		}

		if (peerState.equals(join)) {
			mEvents.onMemberJoin(parsed[1]);
		} else {
			mEvents.onMemberLevea(parsed[1]);
		}

	}

}
