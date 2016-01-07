package org.dync.teameeting.msgs;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;

import com.ypy.eventbus.EventBus;

import org.dync.teameeting.structs.EventType;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by hp on 12/24/15.
 */
public class TMMsgSender extends MsgClient {

    private Message mMessage;
    private String TAG = "TMMsgSender";

    public TMMsgSender(Activity activity) {
        super(activity);
    }

    /**
     * provide for client to imvoke
     * */

    public int TMConnStatus() {
        return this.MCConnStatus();
    }

    public int TMInit(String strServer, int nPort) {
        return this.MCInit(strServer, nPort);
    }

    public int TMUnin() {
        return this.MCUnin();
    }


    public int TMLogin(String strUserid, String strPass) {
        return this.MCLogin(strUserid, strPass);
    }

    public int TMSndMsg(String strUserid, String strPass, String strRoomid, String strMsg) {
        return this.MCSndMsg(strUserid, strPass, strRoomid, strMsg);
    }

    public int TMGetMsg(String strUserid, String strPass) {
        return this.MCGetMsg(strUserid, strPass);
    }

    public int TMLogout(String strUserid, String strPass) {
        return this.MCLogout(strUserid, strPass);
    }

    public int TMOptRoom(int cmd, String strUserid, String strPass, String strRoomid, String strRemain) {
        return this.MCOptRoom(cmd, strUserid, strPass, strRoomid, strRemain);
    }

    public int TMSndMsgTo(String strUserid, String strPass, String strRoomid, String strMsg, String[] arrUser) {
        return  this.MCSndMsgTo(strUserid, strPass, strRoomid, strMsg, arrUser);
    }


    /**
     * implement for JMClientHelper
     * */
    @Override
    public void OnReqLogin(int code, String status, String userid) {
        String s = "OnReqLogin status:"+status+", userid"+userid;
        System.out.println(s);


    }

    @Override
    public void OnRespLogin(int code, String status, String userid) {
        String s = "OnRespLogin status:"+status+", userid"+userid;
        System.out.println(s);
        mMessage = new Message();
        if(code==0){

            mMessage.what = EventType.MSG_MESSAGE_LOGIN_SUCCESS.ordinal();
        }else{
            mMessage.what = EventType.MSG_MESSAGE_LOGIN_FAILED.ordinal();
        }

        EventBus.getDefault().post(mMessage);
    }

    @Override
    public void OnReqSndMsg(String msg) {
        String s = "OnReqSndMsg msg:" + msg;
        System.out.println(s);

        try {
            JSONObject json = new JSONObject(msg);
            String content = json.getString("cont");
            Log.e(TAG, "OnReqSndMsg: " +content);

            mMessage = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("message",content);
            // bundle.putString("name",from);
            mMessage.setData(bundle);
            mMessage.what = EventType.MSG_MESSAGE_RECEIVE.ordinal();
            EventBus.getDefault().post(mMessage);

        } catch (JSONException e) {
            e.printStackTrace();
        }



    }

    @Override
    public void OnRespSndMsg(String msg) {
        String s = "OnRespSndMsg msg:" + msg;
        System.out.println(s);

    }

    @Override
    public void OnReqGetMsg(String msg) {
        String s = "OnReqGetMsg msg:"+ msg;
        System.out.println(s);

    }

    @Override
    public void OnRespGetMsg(String msg) {
        String s = "OnRespGetMsg msg:"+ msg;
        System.out.println(s);

    }

    @Override
    public void OnReqLogout(int code, String status, String userid) {
        String s = "OnReqLogout status:"+status+", userid"+userid;
        System.out.println(s);

    }

    @Override
    public void OnRespLogout(int code, String status, String userid) {
        String s = "OnRespLogout status:"+status+", userid"+userid;
        System.out.println(s);
        mMessage = new Message();
        if(code==0){

            mMessage.what = EventType.MSG_MESSAGE_LOGOUT_SUCCESS.ordinal();
        }else{
            mMessage.what = EventType.MSG_MESSAGE_LOGOUT_FAILED.ordinal();
        }

        EventBus.getDefault().post(mMessage);


    }

    @Override
    public void OnMsgServerConnected() {
        String s = "OnMsgServerConnected was called";
        System.out.println(s);
        mMessage = new Message();


        mMessage.what = EventType.MSG_MESSAGE_SERVER_CONNECTED.ordinal();

        EventBus.getDefault().post(mMessage);

    }

    @Override
    public void OnMsgServerDisconnect() {
        String s = "OnMsgServerDisconnect was called";
        System.out.println(s);

    }

    @Override
    public void OnMsgServerConnectionFailure() {
        String s = "OnMsgServerConnectionFailure was called";
        System.out.println(s);

    }
}
