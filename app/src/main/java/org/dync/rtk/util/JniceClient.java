package org.dync.rtk.util;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import com.loopj.android.http.AsyncHttpResponseHandler;

import android.content.Context;
import android.util.Log;

public class JniceClient extends JNetwork {
	private String mRoomId;
	private String mNickname;

	public JniceClient(Context context, JniceEvent event, String ip, int port) {
		super(context, event, ip, port);
	}

	public void Close() {
		Leave();
		this.DoClose();
	}

	public void Join(String appid, String appkey, String roomid, String nickname) {
		if (!mConnected) {
			mRoomId = roomid;
			mNickname = nickname;
			this.TryJoin(appid, appkey, roomid);
		} else {

		}
	}

	public void Leave() {
		//if (mConnected) 
		{
			this.DoLeave();
			mConnected = false;
		}
	}

	public void Publish() {
		JSONObject json = new JSONObject();
		try {
			json.put("jnice", "publish");
			json.put("roomId", mRoomId);
			json.put("bitrate", 256);
			json.put("audio", true);
			json.put("video", true);
			json.put("transaction", randomString(12));
			this.DoPost(json.toString(), new AsyncHttpResponseHandler() {

				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] errorResponse, Throwable e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onSuccess(int statusCode, Header[] headers,
						byte[] response) {
					// TODO Auto-generated method stub

				}
			});
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void Unpublish(String jniceId) {
		JSONObject json = new JSONObject();

		try {
			json.put("jnice", "unpublish");
			json.put("jniceId", jniceId);
			json.put("transaction", randomString(12));
			this.DoPost(json.toString(), new AsyncHttpResponseHandler() {

				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] errorResponse, Throwable e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onSuccess(int statusCode, Header[] headers,
						byte[] response) {
					// TODO Auto-generated method stub

				}
			});
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void Subscribe(String serverId, String publishId) {
		JSONObject json = new JSONObject();

		try {
			json.put("jnice", "subscribe");
			json.put("serverId", serverId);
			json.put("publishId", publishId);
			json.put("audio", true);
			json.put("video", true);
			json.put("transaction", randomString(12));
			this.DoPost(json.toString(), new AsyncHttpResponseHandler() {

				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] errorResponse, Throwable e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onSuccess(int statusCode, Header[] headers,
						byte[] response) {
					// TODO Auto-generated method stub

				}
			});
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void Unsubscribe(String jniceId) {
		JSONObject json = new JSONObject();

		try {
			json.put("jnice", "unsubscribe");
			json.put("jniceId", jniceId);
			json.put("transaction", randomString(12));
			this.DoPost(json.toString(), new AsyncHttpResponseHandler() {

				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] errorResponse, Throwable e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onSuccess(int statusCode, Header[] headers,
						byte[] response) {
					// TODO Auto-generated method stub

				}
			});
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void SendSdpInfo(String jniceId, String jsep) {
		JSONObject jsonObj = new JSONObject();
		JSONObject jsonData = new JSONObject();
		try {
			jsonObj.put("jnice", "sdpInfo");
			jsonObj.put("jniceId", jniceId);
			{
				JSONTokener jsonParser = new JSONTokener(jsep);
				final JSONObject json = (JSONObject) jsonParser.nextValue();
				final String type = json.has("type") ? json.getString("type")
						: "";

				if (type != null && type.length() > 0) {
					jsonData.put("janus", "message");
					JSONObject jsonReq = new JSONObject();
					if (type.equals("offer")) {
						jsonReq.put("request", "configure");
						jsonReq.put("audio", true);
						jsonReq.put("video", true);
						jsonData.put("body", jsonReq);
					} else if (type.equals("answer")) {
						jsonReq.put("request", "start");
						jsonReq.put("room", mRoomId);
						jsonData.put("body", jsonReq);
					}
					jsonData.put("transaction", randomString(12));
					jsonData.put("jsep", json);
				} else {
					jsonData.put("janus", "trickle");
					jsonData.put("transaction", randomString(12));
					jsonData.put("candidate", json);

				}
			}
			jsonObj.put("jsep", jsonData.toString());
			jsonObj.put("transaction", randomString(12));

			this.DoPostSync(jsonObj.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void OnTryJoinOK(boolean needPwd) {
		// TODO Auto-generated method stub

		this.DoJoin(mRoomId, "123", mNickname);
	}

	@Override
	public void OnLeave(int status, String errInfo) {
		// TODO Auto-generated method stub
		mConnected = false;
	}
	
	
	public void SendMessage(String roomId,String peerId,String  jsep)
	{
		JSONObject json = new JSONObject();
		try {
			json.put("jnice", "message");
			json.put("roomId", roomId);
			json.put("to", peerId);
			json.put("jsep", jsep);
			json.put("transaction", randomString(12));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.e("MeetingActivity", json.toString());
		this.DoPostSync(json.toString());
	}

	@Override
	public void OnMessage(String message) {
		// TODO Auto-generated method stub
		try {
			JSONObject json = new JSONObject(message);
			if (json.getString("jnice").equals("publish")) {
				if (json.getString("result").equals("Ack")) {
					mEvent.OnPublishAck(json.getString("svrId"),
							json.getString("jniceId"),
							json.getString("channelId"));
				} else if (json.getString("result").equals("OK")) {
					mEvent.OnPublishOk(json.getString("jniceId"),
							json.getString("jsep"));
				}
			} else if (json.getString("jnice").equals("subscribe")) {
				if (json.getString("result").equals("OK")) {
					mEvent.OnSubscribeOk(json.getString("jniceId"),
							json.getString("jsep"));
				} else if (json.getString("result").equals("Answer")) {
					mEvent.OnSubscirbeAnswer(json.getString("jniceId"),
							json.getString("jsep"));
				} else if (json.getString("result").equals("Fail")) {

				}
			} else if(json.getString("jnice").equals("trickle")) {
				mEvent.OnPeerTrickle(json.getString("jniceId"), json.getString("jsep"));
			} else if(json.getString("jnice").equals("channel")) {
				if (json.getString("status").equals("open")) {
					Log.e("OnChannelOpen", "OnChannelOpen 1");
					mEvent.OnChannelOpen(json.getString("member"), json.getString("svrId"), "" + json.getLong("channelId"));
					
				} else if (json.getString("status").equals("close")) {
					mEvent.OnChannelClose(json.getString("member"), "" + json.getLong("channelId"));
				}
			} else if(json.getString("jnice").equals("status")) {
				if(json.getString("status").equals("online")) {
					JSONArray jsepArr = json.getJSONArray("jsep");
					for(int i = 0; i < jsepArr.length(); i++) {
						String [] ss = jsepArr.optString(i).split("&");
						mEvent.OnMemberJoin(ss[0], ss[1]);
						if(ss.length > 2) {
							JSONTokener jsonParser = new JSONTokener(ss[2]);
							JSONArray pubArr = (JSONArray) jsonParser.nextValue();
							for(int j = 0; j < pubArr.length(); j++) {
								JSONObject pub = pubArr.optJSONObject(j);
								mEvent.OnChannelOpen(ss[0], pub.getString("svrId"), "" + pub.getLong("channelId"));
								Log.e("OnChannelOpen", "OnChannelOpen 2");
							}
						}
					} 
				} else if(json.getString("status").equals("offline")) {
					JSONArray jsepArr = json.getJSONArray("jsep");
					for(int i = 0; i < jsepArr.length(); i++) {
						String userId = jsepArr.optString(i);
						mEvent.OnMemberLeave(userId);
					}
				}
				//
			}//处理消息接收
			else if(json.getString("jnice").equals("message"))
			{
				Log.e(" message ", " json.toString() "+json.toString());
				String userId = json.getString("from");
				String content = json.getString("jsep");
				mEvent.OnReceiveMessage(userId,content);
			}
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
