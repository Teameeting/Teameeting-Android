package org.dync.teameeting.http;

import android.util.Log;

import com.loopj.android.http.RequestParams;
import com.orhanobut.logger.Logger;

import org.apache.http.Header;
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

import java.util.List;

import de.greenrobot.event.EventBus;

public class NetWork {
    private static final String TAG = "NetWork";

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
        HttpContent.post(url, params, new TmTextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, int code, String message, String responseString, Header[] headers) {
                if (mDebug)
                    Logger.e(responseString);

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

    /**
     * getRoomLists
     *
     * @param sign
     * @param pageNum
     * @param pageSize
     */
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

    /**
     * applyRoom
     *
     * @param sign
     * @param meetingname
     * @param meetingtype
     * @param meetdesc
     * @param meetenable
     * @param pushable
     * @return
     */
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
                if (code == 200) {
                    TeamMeetingApp.getmSelfData().getMeetingLists().get(position).setMeetenable(enable);
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
                        msg.what = EventType.MSG_UPDATE_MEET_ROOM_NAME_SUCCESS.ordinal();
                    } else {
                        msg.what = EventType.MSG_UPDATE_MEET_ROOM_NAME_FAILED.ordinal();
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

    public void getMeetingInfo(final String meetingid, final String joinType) {
        getMeetingInfo(meetingid, -1, joinType);
    }

    /**
     * TODO Obtain information on the conference room A single 16 submit response Settings 16
     *
     * @param meetingid
     */
    public void getMeetingInfo(final String meetingid, final int position, final String joinType) {
        String url = "meeting/getMeetingInfo/" + meetingid;
        HttpContent.get(url, new TmTextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, int code, String message, String responseString, Header[] headers) {
                super.onSuccess(statusCode, code, message, responseString, headers);

                if (code == 200) {
                    if (mDebug)
                        Log.e(TAG, "onSuccess: getMeetingInfo" + responseString);
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
                bundle.putInt("position",position);
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
                        JSONObject jsonObject = new JSONObject(responseString);
                        long jointime = jsonObject.getLong("jointime");
                        List<MeetingListEntity> meetingLists = TeamMeetingApp.getmSelfData().getMeetingLists();

                        MeetingListEntity meetingListEntity = meetingLists.get(position);
                        meetingListEntity.setJointime(jointime);
                        meetingLists.remove(position);
                        meetingLists.add(0, meetingListEntity);
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
     * updateNickname
     *
     * @param sign
     * @param nickname
     */

    public void updateNickname(final String sign, final String nickname) {
        String url = "users/updateNickname";

        RequestParams params = new RequestParams();
        params.put("sign", sign);
        params.put("nickname", nickname);

        HttpContent.post(url, params, new TmTextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, int code, String message, String responseString, Header[] headers) {
                super.onSuccess(statusCode, code, message, responseString, headers);
                if (mDebug)
                    Log.e(TAG, "onSuccess: pushMeetingMsg" + responseString);
                if (code == 200) {
                    msg.what = EventType.MSG_UPDATE_NICKNAME_SUCCESS.ordinal();
                } else {
                    msg.what = EventType.MSG_UPDATE_NICKNAME_FAILED
                            .ordinal();
                }
                bundle.putString("message", message);
                msg.setData(bundle);
                EventBus.getDefault().post(msg);
            }
        });
    }
}
