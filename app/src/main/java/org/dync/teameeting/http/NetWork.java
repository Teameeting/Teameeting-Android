package org.dync.teameeting.http;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.loopj.android.http.RequestParams;
import com.orhanobut.logger.Logger;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.dync.teameeting.TeamMeetingApp;
import org.dync.teameeting.bean.MeetingList;
import org.dync.teameeting.bean.MeetingListEntity;
import org.dync.teameeting.bean.MessageList;
import org.dync.teameeting.bean.MessageListEntity;
import org.dync.teameeting.bean.SelfData;
import org.dync.teameeting.structs.EventType;
import org.dync.teameeting.structs.JoinActType;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.greenrobot.event.EventBus;

public class NetWork {

    private static final String TAG = "NetWork";
    private static final boolean mDebug = TeamMeetingApp.mIsDebug;

    // public static final String VIDEO_URL = "123.59.68.21";//公网
    // public static final String VIDEO_URL = "192.168.7.45";//内网

    // public static final String NODE_URL =
    // "http://123.59.68.21:7080/1.0/";//公网
    public static final String NODE_URL = "http://192.168.7.45:8055/";// 内网

    public static final String RETURN_TYPE_JSON = "application/json"; // 返回json
    public static final String RETURN_TYPE_XML = "application/xml"; // 返回xml

    /**
     * Http
     */
    private DefaultHttpClient mHttpClient;

	/*
     * request
	 * =========================Service========================================
	 */

    /**
     * @param params
     * @return
     */
    private List<NameValuePair> getNameValuePairList(Map<String, String> params) {
        List<NameValuePair> listParams = new ArrayList<NameValuePair>();
        Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, String> enter = iterator.next();
            listParams.add(new BasicNameValuePair(enter.getKey(), enter
                    .getValue()));
        }
        return listParams;
    }

    /**
     * get Response String
     *
     * @param params
     * @param url
     * @return String
     */
    private String getResponseStr(Map<String, String> params, String url) {
        String enityStr = null;
        HttpPost httpPost = new HttpPost(NODE_URL + url);
        httpPost.addHeader("Accept", RETURN_TYPE_JSON);
        Message msg = new Message();
        List<NameValuePair> listParams = getNameValuePairList(params);
        try {
            HttpEntity requestHttpEntity = new UrlEncodedFormEntity(listParams);
            httpPost.setEntity(requestHttpEntity);
            HttpResponse httpResponse = mHttpClient.execute(httpPost);

            int responseCode = httpResponse.getStatusLine().getStatusCode();
            if (mDebug)
                Log.e(TAG, "responseCode" + responseCode);
            if (responseCode == 200) {
                enityStr = EntityUtils.toString(httpResponse.getEntity());
                return enityStr;
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        msg.what = EventType.MSG_RESPONS_ESTR_NULl.ordinal();
        EventBus.getDefault().post(msg);
        return null;
    }

    /**
     * init
     *
     * @param userid
     * @param uactype
     * @param uregtype
     * @param ulogindev
     * @param upushtoken
     */

    public void init(final String userid, final String uactype,
                     final String uregtype, final String ulogindev,
                     final String upushtoken) {
        String url = "users/init";
        RequestParams params = new RequestParams();
        params.put("userid", userid);
        params.put("uactype", uactype);
        params.put("uregtype", uregtype);
        params.put("ulogindev", ulogindev);
        params.put("upushtoken", upushtoken);

        final Bundle bundle = new Bundle();
        final Message msg = new Message();
        HttpContent.post(url, params, new TmTextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, int code, String message, String responseString, Header[] headers) {
                if (code == 200) {
                    SelfData selfData = gson.fromJson(responseString, SelfData.class);
                    TeamMeetingApp.setSelfData(selfData);
                    msg.what = EventType.MSG_ININT_SUCCESS.ordinal();
                    if (mDebug) {
                        Log.i(TAG, "getInformation" + selfData.getInformation().toString());
                    }
                } else {
                    msg.what = EventType.MSG_ININT_FAILED.ordinal();
                }
                msg.setData(bundle);
                EventBus.getDefault().post(msg);

            }
        });
    }


    public void getRoomLists(final String sign, final String pageNum, final String pageSize) {
        String url = "meeting/getRoomList";
        RequestParams params = new RequestParams();
        params.put("sign", sign);
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);

        HttpContent.post(url, params, new TmTextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, int code, String message, String responseString, Header[] headers) {
                if (mDebug) {
                    Logger.e(responseString);
                }
                if (statusCode == 200) {
                    msg.what = EventType.MSG_GET_ROOM_LIST_SUCCESS.ordinal();
                    MeetingList meetingList = gson.fromJson(responseString, MeetingList.class);
                    if (meetingList != null) {
                        TeamMeetingApp.getmSelfData().setMeetingLists(meetingList.getMeetingList());
                    }
                } else {
                    msg.what = EventType.MSG_GET_ROOM_LIST_FAILED.ordinal();
                }
                bundle.putString("message", message);
                msg.setData(bundle);
                EventBus.getDefault().post(msg);

            }
        });

    }


    /**
     * updatePushtoken
     *
     * @param sign
     * @param upushtoken
     */


    public void updatePushtoken(final String sign, final String upushtoken) {
        String url = "users/updatePushtoken";
        RequestParams params = new RequestParams();
        params.put("sign", sign);
        params.put("upushtoken", upushtoken);

        HttpContent.post(url, params, new TmTextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, int code, String message, String responseString, Header[] headers) {
                super.onSuccess(statusCode, code, message, responseString, headers);

                if (mDebug) {
                    Log.e(TAG, "onSuccess:updatePushtoken " + responseString);
                }
                if (code == 200) {
                    msg.what = EventType.MSG_UPDAT_EPUSH_TOKEN_SUCCESS
                            .ordinal();
                } else {
                    msg.what = EventType.MSG_UPDAT_EPUSH_TOKEN_FAILED
                            .ordinal();
                }
                bundle.putString("message", message);
                msg.setData(bundle);
                EventBus.getDefault().post(msg);

            }
        });


    }

    /**
     * signOut
     *
     * @param sign
     */
    public void signOut(final String sign) {
        String url = "users/signout";
        RequestParams params = new RequestParams();
        params.put("sign", sign);

        HttpContent.post(url, params, new TmTextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, int code, String message, String responseString, Header[] headers) {
                if (mDebug)
                    Log.e(TAG, "onSuccess: signOut" + responseString);
                if (code == 200) {
                    msg.what = EventType.MSG_SIGNOUT_SUCCESS.ordinal();
                } else {
                    msg.what = EventType.MSG_SIGNOUT_FAILED.ordinal();
                }

                bundle.putString("message", message);
                msg.setData(bundle);
                EventBus.getDefault().post(msg);

            }
        });
    }

    public MeetingListEntity applyRoom(final String sign, final String meetingname,
                                       final String meetingtype, final String meetdesc, final String meetenable,
                                       final String pushable) {
        applyRoom(sign, meetingname, meetingtype, meetdesc, meetenable, pushable, 1, 0);

        return null;
    }

    /**
     * applyRoom
     *
     * @param sign
     * @param meetingname
     * @param meetingtype
     * @param meetdesc
     * @param meetenable
     * @param pushable
     */

    public MeetingListEntity applyRoom(final String sign, final String meetingname,
                                       final String meetingtype, final String meetdesc, final String meetenable,
                                       final String pushable, final int netCachCreate, final int position) {
        String url = "meeting/applyRoom";
        RequestParams params = new RequestParams();
        params.put("sign", sign);
        params.put("meetingname", meetingname);
        params.put("meetingtype", meetingtype);
        params.put("meetdesc", meetdesc);
        params.put("meetenable", meetenable);
        params.put("pushable", pushable);

        HttpContent.post(url, params, new TmTextHttpResponseHandler() {


            @Override
            public void onSuccess(int statusCode, int code, String message, String responseString, Header[] headers) {
                if (mDebug)
                    Log.e(TAG, "onSuccess: applyRoom" + responseString);
                if (code == 200) {
                    try {
                        JSONObject json = new JSONObject(responseString);
                        String meetingInfo = json.getString("meetingInfo");
                        MeetingListEntity meeting = gson.fromJson(meetingInfo, MeetingListEntity.class);

                        meeting.setCreatetime(meeting.getJointime());
                        meeting.setOwner(1);
                        meeting.setMemnumber(0);
                        meeting.setUserid(TeamMeetingApp.getTeamMeetingApp().getDevId());
                        List<MeetingListEntity> meetingLists = TeamMeetingApp.getmSelfData().getMeetingLists();
                        if (meetingLists.size() >= 20) {
                            meetingLists.remove(19);
                        }
                        meetingLists.remove(position);
                        meetingLists.add(0, meeting);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    msg.what = EventType.MSG_APPLY_ROOM_SUCCESS.ordinal();
                } else {
                    msg.what = EventType.MSG_APPLY_ROOMT_FAILED.ordinal();
                }
                if (netCachCreate == 1) {
                    bundle.putString("message", message);
                    msg.setData(bundle);
                    EventBus.getDefault().post(msg);
                }


            }
        });
        return null;
    }

    /**
     * deleteRoom
     *
     * @param sign
     * @param meetingid
     */

    public void deleteRoom(final String sign, final String meetingid) {

        String url = "meeting/deleteRoom";
        RequestParams params = new RequestParams();
        params.put("sign", sign);
        params.put("meetingid", meetingid);

        HttpContent.post(url, params, new TmTextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, int code, String message, String responseString, Header[] headers) {
                if (mDebug)
                    Log.e(TAG, "onSuccess: deleteRoom" + responseString);
                if (code == 200) {
                    msg.what = EventType.MSG_DELETE_ROOM_SUCCESS
                            .ordinal();
                } else {
                    msg.what = EventType.MSG_DELETE_ROOM_FAILED
                            .ordinal();
                }
                bundle.putString("meetingid", meetingid);
                bundle.putString("message", message);
                msg.setData(bundle);
                EventBus.getDefault().post(msg);

            }
        });


    }

    /**
     * updateRoomMinuxMemNumber
     *
     * @param sign
     * @param meetingid
     */
    public void updateRoomMinuxMemNumber(final String sign,
                                         final String meetingid) {
        String url = "meeting/updateRoomMinuxMemNumber";

        RequestParams params = new RequestParams();
        params.put("sign", sign);
        params.put("meetingid", meetingid);

        HttpContent.post(url, params, new TmTextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, int code, String message, String responseString, Header[] headers) {
                super.onSuccess(statusCode, code, message, responseString, headers);
                if (mDebug)
                    Log.e(TAG, "onSuccess: updateRoomMinuxMemNumber" + responseString);
                if (code == 200) {
                    msg.what = EventType.MSG_UPDATE_ROOM_Minux_MEM_NUMBER_SUCCESS
                            .ordinal();
                } else {
                    msg.what = EventType.MSG_UPDATE_ROOM_Minux_MEM_NUMBER_FAILED
                            .ordinal();
                }
                bundle.putString("message", message);
                msg.setData(bundle);
                EventBus.getDefault().post(msg);

            }
        });


    }


    /**
     * updateRoomAddMemNumber
     *
     * @param sign
     * @param meetingid
     */
    public void updateRoomAddMemNumber(final String sign, final String meetingid) {
        String url = "meeting/updateRoomAddMemNumber";
        RequestParams params = new RequestParams();
        params.put("sign", sign);
        params.put("meetingid", meetingid);

        HttpContent.post(url, params, new TmTextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, int code, String message, String responseString, Header[] headers) {
                super.onSuccess(statusCode, code, message, responseString, headers);
                if (mDebug)
                    Log.e(TAG, "onSuccess: updateRoomAddMemNumber" + responseString);
                if (code == 200) {
                    msg.what = EventType.MSG_UPDATE_ROOM_ADD_MEM_NUMBER_SUCCESS
                            .ordinal();
                } else {
                    msg.what = EventType.MSG_UPDATE_ROOM_ADD_MEM_NUMBER_FAILED
                            .ordinal();
                }
                bundle.putString("message", message);
                msg.setData(bundle);
                EventBus.getDefault().post(msg);

            }
        });
    }


    /**
     * updateRoomPushable 05
     *
     * @param sign
     * @param meetingid
     * @param pushable
     */
    public void updateRoomPushable(final String sign, final String meetingid, final int pushable, final int position) {
        String url = "meeting/updateRoomPushable";
        RequestParams params = new RequestParams();
        params.put("sign", sign);
        params.put("meetingid", meetingid);
        params.put("pushable", pushable);

        HttpContent.post(url, params, new TmTextHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, int code, String message, String responseString, Header[] headers) {
                super.onSuccess(statusCode, code, message, responseString, headers);
                if (mDebug)
                    Log.e(TAG, "onSuccess: updateRoomPushable123" + responseString);
                if (code == 200) {

                    List<MeetingListEntity> meetingLists = TeamMeetingApp.getmSelfData().getMeetingLists();

                    meetingLists.get(position).setPushable(pushable);

                    msg.what = EventType.MSG_UPDATE_ROOM_PUSHABLE_SUCCESS.ordinal();
                } else {
                    msg.what = EventType.MSG_UPDATE_ROOM_PUSHABLE_FAILED.ordinal();
                }
                bundle.putString("message", message);
                msg.setData(bundle);
                EventBus.getDefault().post(msg);
            }

        });
    }

    /**
     * updateRoomEnable
     *
     * @param sign
     * @param meetingid
     * @param enable
     */
    public void updateRoomEnable(final String sign, final String meetingid, final int enable, final int position) {
        String url = "meeting/updateRoomEnable";
        RequestParams params = new RequestParams();
        params.put("sign", sign);
        params.put("meetingid", meetingid);
        params.put("enable", enable);

        HttpContent.post(url, params, new TmTextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, int code, String message, String responseString, Header[] headers) {
                super.onSuccess(statusCode, code, message, responseString, headers);
                if (mDebug)
                    Log.e(TAG, "onSuccess: updateRoomEnable" + responseString);
                TeamMeetingApp.getmSelfData().getMeetingLists().get(position).setMeetusable(enable);
                if (code == 200) {
                    msg.what = EventType.MSG_UPDATE_ROOM_ENABLE_SUCCESS.ordinal();
                } else {
                    msg.what = EventType.MSG_UPDATE_ROOM_ENABLE_FAILED.ordinal();
                }
                bundle.putString("message", message);
                msg.setData(bundle);
                EventBus.getDefault().post(msg);

            }
        });


    }

    /**
     * updateRoomMemNumber
     *
     * @param sign
     * @param meetingid
     * @param meetingMemNumber
     */

    public void updateRoomMemNumber(final String sign, final String meetingid,
                                    final String meetingMemNumber) {
        new Thread() {
            @Override
            public synchronized void run() {
                // TODO Auto-generated method stub
                super.run();

                Map<String, String> params = new HashMap<String, String>();
                params.put("sign", sign);
                params.put("meetingid", meetingid);
                params.put("meetingMemNumber", meetingMemNumber);

                try {
                    String ss = getResponseStr(params,
                            "meeting/updateRoomMemNumber");
                    if (mDebug)
                        Log.e(TAG, "ss " + ss);
                    if (ss != null) {
                        JSONObject jsonObject = new JSONObject(ss);
                        Bundle bundle = new Bundle();
                        Message msg = new Message();
                        int code = jsonObject.getInt("code");
                        String message = jsonObject.getString("message");
                        if (code == 200) {
                            msg.what = EventType.MSG_UPDATE_ROOM_ENABLE_SUCCESS
                                    .ordinal();
                        } else {
                            msg.what = EventType.MSG_UPDATE_ROOM_ENABLE_FAILED
                                    .ordinal();
                        }

                        bundle.putString("message", message);
                        msg.setData(bundle);
                        EventBus.getDefault().post(msg);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }.start();

    }


    /**
     * updateMeetRoomName
     *
     * @param sign
     * @param meetingid
     * @param roomName
     */
    public void updateMeetRoomName(final String sign, final String meetingid,
                                   final String roomName) {
        String url = "meeting/updateMeetRoomName";
        RequestParams params = new RequestParams();
        params.put("sign", sign);
        params.put("meetingid", meetingid);
        params.put("meetingname", roomName);

        HttpContent.post(url, params, new TmTextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, int code, String message, String responseString, Header[] headers) {
                super.onSuccess(statusCode, code, message, responseString, headers);
                if (mDebug)

                    if (code == 200) {
                        msg.what = EventType.MSG_UPDATE_MEET_ROOM_NAME_SUCCESS
                                .ordinal();
                    } else {
                        msg.what = EventType.MSG_UPDATE_MEET_ROOM_NAME_FAILED
                                .ordinal();
                    }

                bundle.putString("message", message);
                msg.setData(bundle);
                EventBus.getDefault().post(msg);

            }
        });


    }

    /**
     * getMeetingMsgList 10
     *
     * @param sign
     * @param meetingid
     * @param pageNum
     * @param pageSize
     */

    public void getMeetingMsgList(final String sign, final String meetingid,
                                  final String pageNum, final String pageSize) {

        String url = "meeting/getMeetingMsgList";
        RequestParams params = new RequestParams();
        params.put("sign", sign);
        params.put("meetingid", meetingid);
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);

        HttpContent.post(url, params, new TmTextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, int code, String message, String responseString, Header[] headers) {
                super.onSuccess(statusCode, code, message, responseString, headers);
                if (mDebug)
                    Log.e(TAG, "onSuccess: getMeetingMsgList" + responseString);
                if (mDebug)
                    if (code == 200) {

                        MessageList messageList = gson.fromJson(responseString, MessageList.class);
                        List<MessageListEntity> messageListEntity = messageList.getMessageList();
                        TeamMeetingApp.getmSelfData().setMessageListEntityList(messageListEntity);

                        msg.what = EventType.MSG_GET_MEETING_MSG_LIST_SUCCESS.ordinal();
                    } else {
                        msg.what = EventType.MSG_GET_MEETING_MSG_LIST_FAILED.ordinal();
                    }

                bundle.putString("message", message);
                msg.setData(bundle);
                EventBus.getDefault().post(msg);

            }
        });
    }

    /**
     * insertMeetingMsg 11
     *
     * @param meetingid
     * @param messageid
     * @param messagetype
     * @param sessionid
     * @param strMsg
     * @param userid
     */
    public void insertMeetingMsg(final String meetingid,
                                 final String messageid, final String messagetype,
                                 final String sessionid, final String strMsg, final String userid) {
        new Thread() {
            @Override
            public synchronized void run() {

                Map<String, String> params = new HashMap<String, String>();
                params.put("meetingid", meetingid);
                params.put("messageid", messageid);
                params.put("messagetype", messagetype);
                params.put("sessionid", sessionid);
                params.put("strMsg", strMsg);
                params.put("userid", userid);

                try {

                    String ss = getResponseStr(params,
                            "meeting/insertMeetingMsg");

                    if (mDebug)
                        Log.e(TAG, "ss " + ss);
                    if (ss != null) {
                        JSONObject jsonObject = new JSONObject(ss);
                        Bundle bundle = new Bundle();
                        Message msg = new Message();
                        int code = jsonObject.getInt("code");
                        String message = jsonObject.getString("message");
                        if (code == 200) {
                            msg.what = EventType.MSG_INSERT_MEETING_MSG_SUCCESS
                                    .ordinal();

                        } else {
                            msg.what = EventType.MSG_INSERT_MEETING_MSG_FAILED
                                    .ordinal();
                        }

                        bundle.putString("message", message);
                        msg.setData(bundle);
                        EventBus.getDefault().post(msg);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }.start();

    }

    /**
     * insertSessionMeetingInfo 12
     *
     * @param meetingid
     * @param sessionid
     * @param sessionstatus
     * @param sessiontype
     * @param sessionnumber
     */

    public void insertSessionMeetingInfo(final String meetingid,
                                         final String sessionid, final String sessionstatus,
                                         final String sessiontype, final String sessionnumber) {
        new Thread() {
            @Override
            public synchronized void run() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("meetingid", meetingid);
                params.put("sessionid", sessionid);
                params.put("sessionstatus", sessionstatus);
                params.put("sessiontype", sessiontype);
                params.put("sessionnumber", sessionnumber);

                try {

                    String ss = getResponseStr(params,
                            "meeting/insertSessionMeetingInfo");

                    if (mDebug)
                        Log.e(TAG, "ss " + ss);
                    if (ss != null) {
                        JSONObject jsonObject = new JSONObject(ss);
                        Bundle bundle = new Bundle();
                        Message msg = new Message();
                        int code = jsonObject.getInt("code");
                        String message = jsonObject.getString("message");
                        if (code == 200) {

                            msg.what = EventType.MSG_INSERT_SESSION_MEETING_INFO_SUCCESS
                                    .ordinal();

                        } else {

                            msg.what = EventType.MSG_INSERT_SESSION_MEETING_INFO_FAILED
                                    .ordinal();
                        }

                        bundle.putString("message", message);
                        msg.setData(bundle);
                        EventBus.getDefault().post(msg);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }.start();

    }

    /**
     * updateSessionMeetingStatus 13
     *
     * @param sessionid
     * @param sessionstatus
     */

    public void updateSessionMeetingStatus(final String sessionid,
                                           final String sessionstatus) {
        new Thread() {
            @Override
            public synchronized void run() {

                Map<String, String> params = new HashMap<String, String>();
                params.put("sessionid", sessionid);
                params.put("sessionstatus", sessionstatus);
                try {
                    String ss = getResponseStr(params,
                            "meeting/updateSessionMeetingStatus");

                    if (mDebug)
                        Log.e(TAG, "ss " + ss);
                    if (ss != null) {
                        JSONObject jsonObject = new JSONObject(ss);
                        Bundle bundle = new Bundle();
                        Message msg = new Message();
                        int code = jsonObject.getInt("code");
                        String message = jsonObject.getString("message");
                        if (code == 200) {
                            msg.what = EventType.MSG_UPDATE_SESSION_MEETING_STATUS_SUCCESS
                                    .ordinal();
                        } else {

                            msg.what = EventType.MSG_UPDATE_SESSION_MEETING_STATUS_FAILED
                                    .ordinal();
                        }

                        bundle.putString("message", message);
                        msg.setData(bundle);
                        EventBus.getDefault().post(msg);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }.start();

    }

    /**
     * updateSessionMeetingEndtime14
     *
     * @param sessionid
     */

    public void updateSessionMeetingEndtime(final String sessionid) {
        new Thread() {
            @Override
            public synchronized void run() {
                super.run();

                Map<String, String> params = new HashMap<String, String>();
                params.put("sessionid", sessionid);

                try {
                    String ss = getResponseStr(params,
                            "meeting/updateSessionMeetingEndtime");

                    if (mDebug)
                        Log.e(TAG, "ss " + ss);
                    if (ss != null) {
                        JSONObject jsonObject = new JSONObject(ss);
                        Bundle bundle = new Bundle();
                        Message msg = new Message();
                        int code = jsonObject.getInt("code");
                        String message = jsonObject.getString("message");
                        if (code == 200) {

                            msg.what = EventType.MSG_UPDATE_SESSION_MEETING_ENDTIME_SUCCESS
                                    .ordinal();

                        } else {

                            msg.what = EventType.MSG_UPDATE_SESSION_MEETING_ENDTIME_FAILED
                                    .ordinal();
                        }

                        bundle.putString("message", message);
                        msg.setData(bundle);
                        EventBus.getDefault().post(msg);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }.start();

    }

    /**
     * updateSessionMeetingNumber15
     *
     * @param sessionid
     * @param sessionnumber
     */

    public void updateSessionMeetingNumber(final String sessionid,
                                           final String sessionnumber) {
        new Thread() {
            @Override
            public synchronized void run() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("sessionid", sessionid);
                params.put("sessionnumber", sessionnumber);

                try {

                    String ss = getResponseStr(params,
                            "meeting/updateSessionMeetingNumber");

                    if (mDebug)
                        Log.e(TAG, "ss " + ss);
                    if (ss != null) {
                        JSONObject jsonObject = new JSONObject(ss);
                        Bundle bundle = new Bundle();
                        Message msg = new Message();
                        int code = jsonObject.getInt("code");
                        String message = jsonObject.getString("message");
                        if (code == 200) {
                            msg.what = EventType.MSG_UPDATE_SESSION_MEETING_NUMBER_SUCCESS
                                    .ordinal();

                        } else {

                            msg.what = EventType.MSG_UPDATE_SESSION_MEETING_NUMBER_FAILED
                                    .ordinal();
                        }

                        bundle.putString("message", message);
                        msg.setData(bundle);

                        EventBus.getDefault().post(msg);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }.start();

    }


    /**
     * TODO Obtain information on the conference room A single 16 submit response Settings 16
     *
     * @param meetingid
     */
    public void getMeetingInfo(final String meetingid, final String joinType) {
        String url = "meeting/getMeetingInfo/" + meetingid;
        HttpContent.get(url, new TmTextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, int code, String message, String responseString, Header[] headers) {
                super.onSuccess(statusCode, code, message, responseString, headers);
                if (mDebug)
                    Log.e(TAG, "onSuccess: getMeetingInfo" + responseString);
                if (code == 200) {
                    try {

                        JSONObject json = new JSONObject(responseString);
                        String info = json.getString("meetingInfo");

                        MeetingListEntity meetingInfo = gson.fromJson(info, MeetingListEntity.class);


                        TeamMeetingApp.getmSelfData().setMeetingListEntity(meetingInfo);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    msg.what = EventType.MSG_GET_MEETING_INFO_SUCCESS.ordinal();
                } else {
                    msg.what = EventType.MSG_GET_MEETING_INFO_FAILED.ordinal();
                }
                bundle.putString(JoinActType.JOIN_TYPE, joinType);
                bundle.putString("message", message);
                bundle.putString("meetingid", meetingid);
                msg.setData(bundle);
                EventBus.getDefault().post(msg);
            }
        });

    }

    /**
     * updateUserMeetingJointime 18
     *
     * @param sign
     * @param meetingid
     */
    public void updateUserMeetingJointime(final String sign, final String meetingid, final int position) {

        RequestParams params = new RequestParams();
        params.put("sign", sign);
        params.put("meetingid", meetingid);

        String url = "meeting/updateUserMeetingJointime";
        HttpContent.post(url, params, new TmTextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, int code, String message, String responseString, Header[] headers) {
                super.onSuccess(statusCode, code, message, responseString, headers);
                if (mDebug)
                    Log.e(TAG, "onSuccess: updateUserMeetingJointime" + responseString);
                if (code == 200) {
                    try {
                        //更新当前room的值
                        JSONObject jsonObject = new JSONObject(responseString);
                        long jointime = jsonObject.getLong("jointime");
                        List<MeetingListEntity> meetingLists = TeamMeetingApp.getmSelfData().getMeetingLists();

                        MeetingListEntity meetingListEntity = meetingLists.get(position);
                        meetingListEntity.setJointime(jointime);
                        meetingLists.remove(position);
                        meetingLists.add(0, meetingListEntity);
                        //Logger.e(meetingLists.size()+"------"+ TeamMeetingApp.getmSelfData().getMeetingLists().size());
                        //TeamMeetingApp.getmSelfData().setMeetingLists(meetingLists);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    msg.what = EventType.MSG_UP_DATE_USER_MEETING_JOIN_TIME_SUCCESS.ordinal();


                } else {
                    msg.what = EventType.MSG_UP_DATE_USER_MEETING_JOIN_TIME_FAILED.ordinal();
                }
                bundle.putString("message", message);
                msg.setData(bundle);
                EventBus.getDefault().post(msg);
            }
        });
    }

    /**
     * insertUserMeetingRoom
     *
     * @param sign
     * @param meetingid
     */

    public void insertUserMeetingRoom(final String sign, final String meetingid, final String join_insert_type) {

        String url = "meeting/insertUserMeetingRoom";
        RequestParams params = new RequestParams();
        params.put("sign", sign);
        params.put("meetingid", meetingid);

        HttpContent.post(url, params, new TmTextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, int code, String message, String responseString, Header[] headers) {
                super.onSuccess(statusCode, code, message, responseString, headers);

                if (mDebug)
                    Log.e(TAG, "onSuccess: insertUserMeetingRoom" + responseString);
                if (code == 200) {
                    TeamMeetingApp.getmSelfData().addMeetingHeardEntity();
                    msg.what = EventType.MSG_INSERT_USER_MEETING_ROOM_SUCCESS.ordinal();
                } else {
                    msg.what = EventType.MSG_INSERT_USER_MEETING_ROOM_FAILED
                            .ordinal();
                }

                bundle.putString("meetingid", meetingid);
                bundle.putString("message", message);
                bundle.putString(JoinActType.JOIN_INSERT_TYPE, join_insert_type);
                msg.setData(bundle);

                EventBus.getDefault().post(msg);

            }
        });

    }

    /**
     * pushMeetingMsg 20
     *
     * @param sign
     * @param meetingid
     * @param pushMsg
     * @param notification
     */

    public void pushMeetingMsg(final String sign, final String meetingid,
                               final String pushMsg, final String notification) {
        String url = "jpush/pushMeetingMsg";

        RequestParams params = new RequestParams();
        params.put("sign", sign);
        params.put("meetingid", meetingid);
        params.put("pushMsg", pushMsg);
        params.put("notification", notification);

        HttpContent.post(url, params, new TmTextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, int code, String message, String responseString, Header[] headers) {
                super.onSuccess(statusCode, code, message, responseString, headers);
                if (mDebug)
                    Log.e(TAG, "onSuccess: pushMeetingMsg" + responseString);
                if (code == 200) {
                    msg.what = EventType.MSG_PUSH_MEETING_MSG_SUCCESS
                            .ordinal();
                } else {
                    msg.what = EventType.MSG_PUSH_MEETING_MSG_FAILED
                            .ordinal();
                }

                bundle.putString("message", message);
                msg.setData(bundle);
                // 测试
                EventBus.getDefault().post(msg);

            }
        });


    }

    /**
     * pushCommonMsg
     *
     * @param sign
     * @param targetid
     * @param pushMsg
     * @param notification
     */

    public void pushCommonMsg(final String sign, final List<String> targetid,
                              final String pushMsg, final String notification) {
        new Thread() {
            @Override
            public synchronized void run() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("sign", sign);
                params.put("pushMsg", pushMsg);
                params.put("targetid", targetid.toString());
                params.put("notification", notification);
                try {
                    String ss = getResponseStr(params, "jpush/pushCommonMsg");
                    if (mDebug)
                        Log.e(TAG, "ss " + ss);
                    if (ss != null) {
                        JSONObject jsonObject = new JSONObject(ss);
                        Bundle bundle = new Bundle();
                        Message msg = new Message();
                        int code = jsonObject.getInt("code");
                        String message = jsonObject.getString("message");
                        if (code == 200) {
                            msg.what = EventType.MSG_PUSH_COMMO_NSG_SUCCESS
                                    .ordinal();
                        } else {
                            msg.what = EventType.MSG_PUSH_OMMO_NMSG_FAILED
                                    .ordinal();
                        }
                        bundle.putString("message", message);
                        msg.setData(bundle);
                        EventBus.getDefault().post(msg);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }.start();
    }

}
