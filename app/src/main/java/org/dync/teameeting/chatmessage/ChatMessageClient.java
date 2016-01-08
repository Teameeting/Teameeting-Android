package org.dync.teameeting.chatmessage;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.ypy.eventbus.EventBus;

import org.dync.teameeting.TeamMeetingApp;
import org.dync.teameeting.dao.ChatEnity;
import org.dync.teameeting.sdkmsgclientandroid.jni.JMClientHelper;
import org.dync.teameeting.structs.EventType;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by admin on 2016/1/8.
 */
public class ChatMessageClient  implements JMClientHelper{

    private Message mMessage;
    private String TAG = "ChatMessageClient";
    private boolean mDebug = TeamMeetingApp.mIsDebug;

    /**
     * implement for JMClientHelper
     * */
    @Override
    public void OnReqLogin(int code, String status, String userid) {
        String s = "OnReqLogin status:"+status+", userid"+userid;
        if (mDebug){
            Log.e(TAG, "OnReqLogin: "+s);
        }
    }

    @Override
    public void OnRespLogin(int code, String status, String userid) {
        String s = "OnRespLogin status:"+status+", userid"+userid;
        mMessage = new Message();
        if(code==0){

            mMessage.what = EventType.MSG_MESSAGE_LOGIN_SUCCESS.ordinal();
        }else{
            mMessage.what = EventType.MSG_MESSAGE_LOGIN_FAILED.ordinal();
        }

        EventBus.getDefault().post(mMessage);

        if (mDebug){
            Log.e(TAG, "OnRespLogin: "+s);
        }
    }

    @Override
    public void OnReqSndMsg(String msg)
    {
        String s = "OnReqSndMsg msg:" + msg;

        ChatEnity chatEnity = new ChatEnity();
        try
        {
            JSONObject json = new JSONObject(msg);
            String content = json.getString("cont");
            String pass = json.getString("pass");
            String ntime = json.getString("ntime");

            chatEnity.setContent(content);
            chatEnity.setSendtime(ntime);
            chatEnity.setName(pass);



            mMessage = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("message", content);
            bundle.putSerializable("ChatEnity", chatEnity);

            // bundle.putString("name",from);

            mMessage.setData(bundle);
            mMessage.what = EventType.MSG_MESSAGE_RECEIVE.ordinal();
            EventBus.getDefault().post(mMessage);

        } catch (JSONException e)
        {
            e.printStackTrace();
        }

        if (mDebug){
            Log.e(TAG, "OnReqSndMsg: "+s);
        }



    }

    @Override
    public void OnRespSndMsg(String msg) {
        String s = "OnRespSndMsg msg:" + msg;

        if (mDebug){
            Log.e(TAG, "OnRespSndMsg: "+s);
        }

    }

    @Override
    public void OnReqGetMsg(String msg) {
        String s = "OnReqGetMsg msg:"+ msg;
        if (mDebug){
            Log.e(TAG, "OnReqGetMsg: "+s);
        }

    }

    @Override
    public void OnRespGetMsg(String msg) {
        String s = "OnRespGetMsg msg:"+ msg;

        if (mDebug){
            Log.e(TAG, "OnRespGetMsg: "+s);
        }

    }

    @Override
    public void OnReqLogout(int code, String status, String userid) {
        String s = "OnReqLogout status:"+status+", userid"+userid;


        if (mDebug){
            Log.e(TAG, "OnReqLogout: "+s);
        }

    }

    @Override
    public void OnRespLogout(int code, String status, String userid) {
        String s = "OnRespLogout status:"+status+", userid"+userid;

        mMessage = new Message();
        if(code==0){

            mMessage.what = EventType.MSG_MESSAGE_LOGOUT_SUCCESS.ordinal();
        }else{
            mMessage.what = EventType.MSG_MESSAGE_LOGOUT_FAILED.ordinal();
        }

        EventBus.getDefault().post(mMessage);

        if (mDebug){
            Log.e(TAG, "OnRespLogout: "+s);
        }


    }

    @Override
    public void OnMsgServerConnected() {

        mMessage = new Message();
        mMessage.what = EventType.MSG_MESSAGE_SERVER_CONNECTED.ordinal();
        EventBus.getDefault().post(mMessage);

        if (mDebug){
            Log.e(TAG, "OnMsgServerConnected: ");
        }

    }

    @Override
    public void OnMsgServerDisconnect() {

        if (mDebug){
            Log.e(TAG, "OnMsgServerDisconnect: ");
        }

    }

    @Override
    public void OnMsgServerConnectionFailure() {


        if (mDebug){
            Log.e(TAG, "OnMsgServerConnectionFailure: ");
        }

    }
}
