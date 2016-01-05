package org.dync.rtk.util;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.dync.rtk.util.LooperExecutor;
import org.dync.rtk.util.RtkNetwork.HttpMsg;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.ResponseHandlerInterface;
import com.loopj.android.http.SyncHttpClient;

import android.content.Context;
import android.util.Log;

/**
 * 呼叫建立时的网络实现 By Dync.inc - 2015/7/20
 * 
 * @author Maozongwu
 * 
 */
public abstract class JNetwork {
	private boolean mDebug = false;
	private static final String TAG = "JNetwork";
	private static final String kPostJnice = "http://%s:%d/jnice";
	private static final String kPostJniceSession = "http://%s:%d/jnice/%s";
	private static final String kGetJnice = "http://%s:%d/jnice/%s?maxEv=%d&syncSeqn=%d";

	// Private method to create random identifiers (e.g., transaction)
	protected static String randomString(int len) {
		String charSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		String randomString = "";
		for (int i = 0; i < len; i++) {
			int randomPoz = (int) Math.floor(Math.random() * charSet.length());
			randomString += charSet.substring(randomPoz, randomPoz + 1);
		}
		return randomString;
	}

	public static interface JniceEvent {
		
		public void OnReceiveMessage(String userId,String content);
		
		public void OnJoinOk(String userId);

		public void OnJoinNeedPwd(String roomId);

		public void OnJoinFailed(int status, String errInfo);

		public void OnLeave(String info);

		public void OnSysError(String errInfo);
		
		public void OnPublishAck(String svrId, String jniceId, String channelId);
		
		public void OnPublishOk(String jniceId, String answer);
		
		public void OnSubscribeOk(String jniceId, String offer);
		
		public void OnSubscirbeAnswer(String jniceId, String answer);
		
		public void OnPeerTrickle(String jniceId, String trickle);
		
		public void OnMemberJoin(String userId, String nickName);
		
		public void OnMemberLeave(String userId);
		
		public void OnChannelOpen(String userId, String svrId, String channelId);
		
		public void OnChannelClose(String userId, String channelId);
	}

	protected final Context mContext;
	protected final JniceEvent mEvent;
	protected boolean mConnected = false;
	private LooperExecutor mLooperExecutor;
	private SyncHttpClient mCtrlClient;
	private LinkedList<String> wsSendQueue;
	private String mPostUrl;
	private String mPostUrlSession;
	private String mStrIp;
	private int mPort;

	// 与服务器断开后需要重置
	private String mSessionId;
	private int mMaxEv = 5;
	private int mSeqn = 0;
	private int mReConnectCount = 0;

	public JNetwork(Context context, JniceEvent event, String ip, int port) {
		RtkUtils.assertIsTrue(null != context);
		RtkUtils.assertIsTrue(null != event);
		mContext = context;
		mEvent = event;
		mPostUrl = String.format(kPostJnice, ip, port);

		{// * New all value;
			mLooperExecutor = new LooperExecutor();
			mLooperExecutor.requestStart();
			mCtrlClient = new SyncHttpClient();
			wsSendQueue = new LinkedList<String>();
		}
	}

	protected void DoClose() {
		if (mLooperExecutor != null) {
			mLooperExecutor.requestStop();
			mLooperExecutor = null;
		}
	}

	public abstract void OnTryJoinOK(boolean needPwd);

	public abstract void OnLeave(int status, String errInfo);

	public abstract void OnMessage(String message);

	protected void TryJoin(String appid, String appkey, final String roomid) {
		RtkUtils.assertIsTrue(null != appid && appid.length() > 0);
		RtkUtils.assertIsTrue(null != appkey && appkey.length() > 0);
		RtkUtils.assertIsTrue(null != roomid && roomid.length() > 0);
		AsyncHttpClient httpClient = new AsyncHttpClient();
		httpClient.setTimeout(30 * 1000);
		JSONObject json = new JSONObject();
		try {
			json.put("jnice", "try_join");
			json.put("appId", appid);
			json.put("appKey", appkey);
			json.put("roomId", roomid);
			json.put("transaction", randomString(12));

			httpClient.post(mContext, mPostUrl,
					new StringEntity(json.toString()), "text/plain",
					new AsyncHttpResponseHandler() {

						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] errorResponse, Throwable e) {
							// TODO Auto-generated method stub
							String errorResponseString = null;
							if (errorResponse != null) {
								errorResponseString = new String(errorResponse);
							}
						}

						@Override
						public void onSuccess(int statusCode, Header[] headers,
								byte[] response) {
							// TODO Auto-generated method stub
							String content = new String(response);
							try {
								JSONObject respJson = new JSONObject(content);
								mStrIp = respJson.getString("roomip");
								mPort = respJson.getInt("roomport");
								OnTryJoinOK(respJson.getBoolean("roompwd"));
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void DoJoin(String roomid, String roompwd, String nickname) {
		RtkUtils.assertIsTrue(null != roomid && roomid.length() > 0);
		RtkUtils.assertIsTrue(null != nickname && nickname.length() > 0);

		AsyncHttpClient httpClient = new AsyncHttpClient();
		httpClient.setTimeout(30 * 1000);
		JSONObject json = new JSONObject();
		try {
			json.put("jnice", "join");
			json.put("roomId", roomid);
			json.put("nickname", nickname);
			if (roompwd != null && roompwd.length() > 0)
				json.put("roomPwd", roompwd);
			json.put("transaction", randomString(12));

			String url = String.format(kPostJnice, mStrIp, mPort);
			httpClient.post(mContext, url, new StringEntity(json.toString()),
					"text/plain", new AsyncHttpResponseHandler() {

						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] errorResponse, Throwable e) {
							// TODO Auto-generated method stub
							String errorResponseString = new String(
									errorResponse);
							mEvent.OnJoinFailed(statusCode, errorResponseString);
						}

						@Override
						public void onSuccess(int statusCode, Header[] headers,
								byte[] response) {
							// TODO Auto-generated method stub
							String content = new String(response);
							Log.e(TAG, " content "+content);
							try {
								JSONObject respJson = new JSONObject(content);
								mConnected = true;
								mSessionId = respJson.getString("sessionId");
								String userId = respJson.getString("userId");
								LongPoll();
								mEvent.OnJoinOk(userId);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void DoPostSync(String content) {
		//content = content.replace("\r\n", "\\r\\n");
		synchronized (wsSendQueue) {
			wsSendQueue.add(content);
		}

		mLooperExecutor.execute(new Runnable() {
			@Override
			public void run() {
				notifySendMsg();
			}
		});
	}
	
	private void notifySendMsg() {
		String item = null;
		synchronized (wsSendQueue) {
			if (wsSendQueue.size() > 0) {
				item = wsSendQueue.remove(0);
			}
		}
		if (item == null) {
			return;
		}
		try {
			String url = String
					.format(kPostJniceSession, mStrIp, mPort, mSessionId);
			mCtrlClient.post(mContext, url, new StringEntity(item), "text/plain",
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
	
	protected void DoPost(String content, ResponseHandlerInterface handler) {
		AsyncHttpClient httpClient = new AsyncHttpClient();
		httpClient.setTimeout(30 * 1000);
		String url = String
				.format(kPostJniceSession, mStrIp, mPort, mSessionId);
		try {
			httpClient.post(mContext, url, new StringEntity(content),
					"text/plain", handler);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void DoLeave() {
		mCtrlClient.cancelAllRequests(true);
		synchronized (wsSendQueue) {
			wsSendQueue.clear();
		}
		String url = String
				.format(kPostJniceSession, mStrIp, mPort, mSessionId);
		AsyncHttpClient httpClient = new AsyncHttpClient();
		httpClient.setTimeout(30 * 1000);
		JSONObject json = new JSONObject();
		try {
			json.put("jnice", "leave");
			json.put("transaction", randomString(12));

			httpClient.post(mContext, url, new StringEntity(json.toString()),
					"text/plain", new AsyncHttpResponseHandler() {

						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] errorResponse, Throwable e) {
							// TODO Auto-generated method stub
							String errorResponseString = null;
							if (errorResponse != null)
								errorResponseString = new String(errorResponse);
							mEvent.OnLeave(errorResponseString);
						}

						@Override
						public void onSuccess(int statusCode, Header[] headers,
								byte[] response) {
							// TODO Auto-generated method stub
							String responseString = new String(response);
							mEvent.OnLeave(responseString);
						}
					});

		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			mSessionId = "";
			mSeqn = 0;
			mReConnectCount = 0;
		}
	}
	
	private void ReLongPoll() {
		if (mConnected) {
			LongPoll();
		}
	}

	private void LongPoll() {

		String url = String.format(kGetJnice, mStrIp, mPort, mSessionId,
				mMaxEv, mSeqn);
		AsyncHttpClient handClient = new AsyncHttpClient();
		handClient.setTimeout(60 * 1000);
		handClient.get(url, new AsyncHttpResponseHandler() {
			@Override
			public void onFailure(int statusCode, Header[] arg1,
					byte[] errorResponse, Throwable arg3) {
				// TODO Auto-generated method stub
				if (mDebug) {
					Log.e(TAG, " LongPoll " + " onFailure: " + statusCode);
				}
				// Connection fails even three times
				if (statusCode == 0 && mReConnectCount < 3) {
					mReConnectCount += 1;
					ReLongPoll();
				} else {
					if (null != errorResponse) {
						String errorResponseString = new String(errorResponse);
						OnLeave(statusCode, errorResponseString);
					} else {
						OnLeave(statusCode, null);
					}
				}
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] response) {
				// TODO Auto-generated method stub
				// Pragma
				// The number of connection reset to 0
				mReConnectCount = 0;
				String resp = new String(response);
				if (mDebug) {
					Log.e(TAG, " LongPoll " + " response " + resp);
				}
				try {
					JSONArray respArr = new JSONArray(resp);
					for (int i = 0; i < respArr.length(); i++) {
						JSONObject jsonObj = respArr.getJSONObject(i);
						mSeqn = jsonObj.getInt("syncSeqn");

						OnMessage(jsonObj.getString("data"));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				ReLongPoll();
			}
		});
	}
}
