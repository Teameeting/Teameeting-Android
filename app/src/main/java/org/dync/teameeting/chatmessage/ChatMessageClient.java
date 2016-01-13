package org.dync.teameeting.chatmessage;

import android.content.Context;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.ypy.eventbus.EventBus;

import org.dync.teameeting.TeamMeetingApp;
import org.dync.teameeting.bean.ReqSndMsgEntity;
import org.dync.teameeting.db.CRUDChat;
import org.dync.teameeting.sdkmsgclientandroid.jni.JMClientHelper;
import org.dync.teameeting.structs.EventType;

import java.security.PublicKey;
import java.util.ArrayList;

import javax.security.auth.login.LoginException;

/**
 * Created by zhangqilu on 2016/1/8.
 */
public class ChatMessageClient implements JMClientHelper {

    private Message mMessage;
    private String TAG = "ChatMessageClient";
    private boolean mDebug = TeamMeetingApp.mIsDebug;
    private ArrayList<ChatMessageObserver> mObServers = new ArrayList<ChatMessageObserver>();
    private Context context;

    public ChatMessageClient(Context context) {
        this.context = context;
    }

    /**
     * regiseter
     *
     * @param observer
     */
    public synchronized void registerObserver(ChatMessageObserver observer) {
        if (observer != null && !mObServers.contains(observer)) {
            mObServers.add(observer);
        }
    }

    public synchronized void unregisterObserver(ChatMessageObserver observer) {
        if (observer != null && mObServers.contains(observer)) {
            mObServers.remove(observer);
        }
    }


    public synchronized void notifyMeetingNumChange(ReqSndMsgEntity reqSndMsg) {
        for (ChatMessageObserver observer : mObServers) {
            observer.onMeetingNumChange(reqSndMsg);
        }
    }

    /**
     * notify
     *
     * @param reqSndMsg
     */
    public synchronized void notifyRequestMessage(ReqSndMsgEntity reqSndMsg) {
        for (ChatMessageObserver observer : mObServers) {
            observer.OnReqSndMsg(reqSndMsg);
        }
    }

    public interface ChatMessageObserver {
        public void OnReqSndMsg(ReqSndMsgEntity reqSndMsg);
        public void onMeetingNumChange(ReqSndMsgEntity reqSndMsg);
    }


    //

    /**
     * implement for JMClientHelper
     */

    @Override
    public void OnSndMsg(String msg) {
        String s = "ChatMessageClient" + msg;
        if (mDebug)
            Logger.e(msg);
        if (msg != null) {
            mMessage = new Message();

            Gson gson = new Gson();
            ReqSndMsgEntity reqSndMsgEntity = gson.fromJson(msg, ReqSndMsgEntity.class);

            if (mDebug) {
                Logger.e(reqSndMsgEntity.getFrom() + "---" + TeamMeetingApp.getTeamMeetingApp().getDevId());
            }

            if (reqSndMsgEntity.getFrom() != TeamMeetingApp.getTeamMeetingApp().getDevId()) {
                if (reqSndMsgEntity.getCmd() == 1) {
                    notifyMeetingNumChange(reqSndMsgEntity);
                    // men enter
                    mMessage.what = EventType.MCCMD_ENTER.ordinal();

                } else if (reqSndMsgEntity.getCmd() == 2) {
                    notifyMeetingNumChange(reqSndMsgEntity);
                    // men leaver
                    mMessage.what = EventType.MCCMD_LEAVE.ordinal();
                } else if (reqSndMsgEntity.getCmd() == 3) {
                    notifyRequestMessage(reqSndMsgEntity);
                    CRUDChat.queryInsert(context, reqSndMsgEntity);
                    mMessage.what = EventType.MSG_MESSAGE_RECEIVE.ordinal();

                }
                EventBus.getDefault().post(mMessage);

            } else {

            }

        }


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
            Log.e(TAG, "OnMsgServerConnectionFailure: ");
        }

    }

    @Override
    public void OnMsgServerState(int connStatus) {

        if (mDebug) {
            Log.e(TAG, "OnMsgServerState: " + connStatus);
        }
    }
}
