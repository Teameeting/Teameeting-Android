package org.dync.teameeting.sdkmsgclientandroid.msgs;

import android.content.Context;

import org.dync.teameeting.sdkmsgclientandroid.jni.JMClientHelper;


/**
 * Created by hp on 12/24/15.
 */

/**
 * TMMsgSender is used to send msg to other
 **/

public class TMMsgSender extends MsgClient {

    private String TAG = "TMMsgSender";

    /**
     * constructor for TMMsgSender
     *
     * params:
     *      context:the context
     *      helper:the JMClientHelper
     **/
    public TMMsgSender(Context context, JMClientHelper helper) {
        super(context, helper);
    }

    /**
     * the status of connection between msgclient and msgserver
     **/
    public int TMConnStatus() {
        return this.MCConnStatus();
    }

    /**
     * init
     *
     * params:
     *      strUid:user identifier, it can be uuid or device id
     *      strToken:token from http server
     *      strServer:server ip
     *      nPort:server port
     **/
    public int TMInit(String strUid, String strToken, String strServer, int nPort) {
        return this.MCInit(strUid, strToken, strServer, nPort);
    }

    /**
     * unin
     **/
    public int TMUnin() {
        return this.MCUnin();
    }

    /**
     * send msg to all member in the meeting room
     * params:
     *      strRoomid:the room id
     *      strMsg:the msg to send
     **/
    public int TMSndMsg(String strRoomid, String strMsg) {
        return this.MCSndMsg(strRoomid, strMsg);
    }

    /**
     * get msg from server
     * params:
     *      cmd:
     **/
    public int TMGetMsg(int cmd) {
        return this.MCGetMsg(cmd);
    }

    /**
     * operation for room
     * params:
     *      cmd:the cmd for room, enter or leave
     *      strRoomid:the room id
     *      strRemain:remain
     **/
    public int TMOptRoom(int cmd, String strRoomid, String strRemain) {
        return this.MCOptRoom(cmd, strRoomid, strRemain);
    }

    /**
     * send msg to some member in meeting room(not used now)
     * params:
     *      strRoomid:the room id
     *      strMsg:the msg to send
     *      arrUser:the members to send to
     **/
    public int TMSndMsgTo(String strRoomid, String strMsg, String[] arrUser) {
        return  this.MCSndMsgTo(strRoomid, strMsg, arrUser);
    }

    /**
     * notify others with self publish id
     * params:
     *      strRoomid:the room id
     *      strMsg:the publish id
     **/
    public int TMNotifyMsg(String strRoomid, String strMsg) {
        return this.MCNotifyMsg(strRoomid, strMsg);
    }
}
