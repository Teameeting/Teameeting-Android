package org.dync.teameeting.chatmessage;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import org.dync.teameeting.R;
import org.dync.teameeting.TeamMeetingApp;
import org.dync.teameeting.bean.MeetingListEntity;
import org.dync.teameeting.bean.ReqSndMsgEntity;
import org.dync.teameeting.db.CRUDChat;
import org.dync.teameeting.sdkmsgclient.jni.JMClientHelper;
import org.dync.teameeting.sdkmsgclient.jni.JMClientType;
import org.dync.teameeting.structs.EventType;
import org.dync.teameeting.structs.HttpApiTpye;
import org.dync.teameeting.ui.activity.NotifyActivity;
import org.dync.teameeting.ui.helper.ActivityTaskHelp;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.data.JPushLocalNotification;
import de.greenrobot.event.EventBus;


public class ChatMessageClient implements JMClientHelper {
    private Message mMessage;
    private String TAG = this.getClass().getSimpleName();
    private boolean mDebug = TeamMeetingApp.mIsDebug;
    private ArrayList<ChatMessageObserver> mObServers = new ArrayList<ChatMessageObserver>();
    private Context context;
    ReqSndMsgEntity reqSndMsgEntity;
    private MediaPlayer mediaPlayer;

    public ChatMessageClient(Context context) {
        this.context = context;
    }

    /**
     * regiseter
     *
     * @param observer
     */
    public synchronized void registerObserver(ChatMessageObserver observer) {
        if ((observer != null) && !mObServers.contains(observer)) {
            mObServers.add(observer);
        }
    }

    public synchronized void unregisterObserver(ChatMessageObserver observer) {
        if ((observer != null) && mObServers.contains(observer)) {
            mObServers.remove(observer);
        }
    }

    /**
     * notify
     *
     * @param reqSndMsg
     */
    public synchronized void notifyRequestMessage(ReqSndMsgEntity reqSndMsg) {
        for (ChatMessageObserver observer : mObServers) {
            Log.e(TAG, "notifyRequestMessage: " + 1);
            observer.OnReqSndMsg(reqSndMsg);
        }
    }

    //

    /**
     * implement for JMClientHelper
     */


    public void OnSndMsg(String msg) {
        if (mDebug) {
            Logger.e(msg);
        }
        if (msg != null) {
            senMag(msg);
        }
    }

    private void senMag(String msg) {
        Gson gson = new Gson();
        reqSndMsgEntity = gson.fromJson(msg, ReqSndMsgEntity.class);

        if (mDebug) {
            Log.e(TAG, reqSndMsgEntity.getFrom() + "-" + TeamMeetingApp.getTeamMeetingApp().getDevId());
        }

        if (reqSndMsgEntity.getTags() == JMClientType.MCSENDTAGS_TALK) {
            CRUDChat.queryInsert(context, reqSndMsgEntity);
        }
        notifyRequestMessage(reqSndMsgEntity);
        if(roomItemisNotifiation(reqSndMsgEntity.getRoom())){
            if (!ActivityTaskHelp.isPackageNameonResume(context, context.getPackageName())) {
                sendPushNotifiaction(reqSndMsgEntity);
            } else if (reqSndMsgEntity.getTags() == JMClientType.MCSENDTAGS_CALL) {
                List<String> activityList = TeamMeetingApp.getMeetingActivityList();
                if (activityList.size() > 0) {
                    String mMeetingId = activityList.get(0);
                    if (mMeetingId != null) {
                        if (!mMeetingId.equals(reqSndMsgEntity.getRoom())) {
                            sendNotify();
                        }
                    }
                } else {
                    sendNotify();
                }

            }
        }

    }

    private void sendNotify() {
        Intent intent = new Intent(context, NotifyActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        intent.putExtra("roomMaster", reqSndMsgEntity.getNname());
        intent.putExtra("roomId", reqSndMsgEntity.getRoom());
        intent.putExtra("roomName", reqSndMsgEntity.getRname());
        context.startActivity(intent);
    }


    int i = 11111111;

    public void sendPushNotifiaction(ReqSndMsgEntity reqSndMsgEntity) {

        int tags = 0;
        String title;
        if (reqSndMsgEntity.getTags() == JMClientType.MCSENDTAGS_TALK) {
            tags = 1;
            title = reqSndMsgEntity.getRname() + " - " + reqSndMsgEntity.getNname() + ":" + reqSndMsgEntity.getCont();
        } else if (reqSndMsgEntity.getTags() == JMClientType.MCSENDTAGS_ENTER) {
            tags = 2;
            title = reqSndMsgEntity.getNname() + context.getString(R.string.notifi_str_enter_room);
            title = title.replace("room name", reqSndMsgEntity.getRname());
        } else if (reqSndMsgEntity.getTags() == JMClientType.MCSENDTAGS_CALL) {
            TeamMeetingApp.getTeamMeetingApp().startMediaPlayer();
            title = reqSndMsgEntity.getNname() + context.getString(R.string.notifi_str_notifation_room);
            title = title.replace("room name", reqSndMsgEntity.getRname());
        } else {
            return;
        }


        JPushLocalNotification ln = new JPushLocalNotification();
        ln.setBuilderId(0);
        ln.setTitle("Teameeting");
        ln.setContent(title);
        i++;
        ln.setNotificationId(i);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("tags", tags);
        map.put("roomid", reqSndMsgEntity.getRoom());

        JSONObject json = new JSONObject(map);
        ln.setExtras(json.toString());
        JPushInterface.addLocalNotification(context.getApplicationContext(), ln);

    }

    public boolean roomItemisNotifiation(String meetingID) {
        List<MeetingListEntity> messageListEntityList = TeamMeetingApp.getmSelfData().getMeetingLists();
        if (messageListEntityList != null && messageListEntityList.size() > 0) {
            for (MeetingListEntity meetingListEntity : messageListEntityList) {
                if (meetingID.equals(meetingListEntity.getMeetingid()) &&
                        meetingListEntity.getPushable() == HttpApiTpye.pushableYes) {
                    return true;
                }
            }
        }
        return false;
    }

    private void callRingStart() {

        if (mediaPlayer != null) {
            mediaPlayer.reset();
        }
        mediaPlayer = MediaPlayer.create(context, R.raw.ring);
        mediaPlayer.start();
    }


    @Override
    public void OnGetMsg(String msg) {
        String s = "OnReqGetMsg msg:" + msg;

        if (mDebug) {
            Log.e(TAG, "OnReqGetMsg: " + s);
        }
    }

    @Override
    public void OnMsgServerConnected() {
        mMessage = new Message();
        mMessage.what = EventType.MSG_MESSAGE_SERVER_CONNECTED.ordinal();
        EventBus.getDefault().post(mMessage);

        if (mDebug) {
            Log.e(TAG, "OnMsgServerConnected: ");
        }
    }

    @Override
    public void OnMsgServerDisconnect() {
        if (mDebug) {
            Log.e(TAG, "OnMsgServerDisconnect: ");
        }
    }

    @Override
    public void OnMsgServerConnectionFailure() {
        if (mDebug) {
            Log.i(TAG, "OnMsgServerConnectionFailure: ");
        }
    }

    @Override
    public void OnMsgServerState(int connStatus) {
        if (mDebug) {
            Log.i(TAG, "OnMsgServerState: " + connStatus);
        }
    }


    public interface ChatMessageObserver {
        void OnReqSndMsg(ReqSndMsgEntity reqSndMsg);
    }
}
