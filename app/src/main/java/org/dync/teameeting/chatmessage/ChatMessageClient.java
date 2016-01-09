package org.dync.teameeting.chatmessage;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.ypy.eventbus.EventBus;

import org.dync.teameeting.TeamMeetingApp;

import org.dync.teameeting.bean.ChatMessage;
import org.dync.teameeting.bean.ReqSndMsgEntity;
import org.dync.teameeting.db.chatdao.ChatCacheEntity;
import org.dync.teameeting.sdkmsgclientandroid.jni.JMClientHelper;
import org.dync.teameeting.structs.EventType;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by admin on 2016/1/8.
 */
public class ChatMessageClient implements JMClientHelper {

    private Message mMessage;
    private String TAG = "ChatMessageClient";
    private boolean mDebug = TeamMeetingApp.mIsDebug;
    private static ChatMessageClient mInstance = new ChatMessageClient();
    private ArrayList<ChatMessageObserver> mObServers = new ArrayList<ChatMessageObserver>();


    private ChatMessageClient() {
    }

    public static ChatMessageClient getInstance() {
        return mInstance;
    }


    /**
     * regiseter
     * @param observer
     */
    public synchronized void registerObserver(ChatMessageObserver observer){
        if (observer!=null && !mObServers.contains(observer)){
            mObServers.add(observer);
        }
    }

    public synchronized void unregisterObserver(ChatMessageObserver observer){
        if (observer!=null && mObServers.contains(observer)){

            mObServers.remove(observer);
        }
    }

    /**
     * notify
     * @param reqSndMsg
     */
    public synchronized void notifyRequestMessage(ReqSndMsgEntity reqSndMsg){
        for (ChatMessageObserver observer : mObServers){
            observer.OnReqSndMsg(reqSndMsg);
        }
    }

    public interface ChatMessageObserver {
        public void OnReqSndMsg(ReqSndMsgEntity reqSndMsg);
    }


    //

    /**
     * implement for JMClientHelper
     */

    @Override
    public void OnReqSndMsg(String msg) {
        String s = "OnReqSndMsg msg:" + msg;

        //   DyncLang   TODO: 2016/1/9 0009
        /**
         *
         *        Monday , passing messages using the Observer pattern
         *        Replace Event Bus
         *
         *
         */
        if (msg!=null) {
            Gson gson = new Gson();
            ReqSndMsgEntity reqSndMsgEntity = gson.fromJson(msg, ReqSndMsgEntity.class);
            //
            notifyRequestMessage(reqSndMsgEntity);

            // Monday delete
            mMessage = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("message", reqSndMsgEntity.getCont());
            mMessage.setData(bundle);
            mMessage.what = EventType.MSG_MESSAGE_RECEIVE.ordinal();
            EventBus.getDefault().post(mMessage);
        }





        if (mDebug) {
            Log.e(TAG, "OnReqSndMsg: " + s);
        }


    }

    @Override
    public void OnRespSndMsg(String msg) {
        String s = "OnRespSndMsg msg:" + msg;

        if (mDebug) {
            Log.e(TAG, "OnRespSndMsg: " + s);
        }

    }

    @Override
    public void OnReqGetMsg(String msg) {
        String s = "OnReqGetMsg msg:" + msg;
        if (mDebug) {
            Log.e(TAG, "OnReqGetMsg: " + s);
        }

    }

    @Override
    public void OnRespGetMsg(String msg) {
        String s = "OnRespGetMsg msg:" + msg;

        if (mDebug) {
            Log.e(TAG, "OnRespGetMsg: " + s);
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
}
