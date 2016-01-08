package org.dync.teameeting.sdkmsgclientandroid.msgs;

import android.content.Context;
import android.os.Message;

import org.dync.teameeting.sdkmsgclientandroid.jni.JMClientHelper;


/**
 * Created by hp on 12/24/15.
 */
public class TMMsgSender extends MsgClient {

    private Message mMessage;
    private String TAG = "TMMsgSender";

    public TMMsgSender(Context context, JMClientHelper helper) {
        super(context, helper);
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

    public int TMNotifyMsg(String strUserid, String strPass, String strRoomid, String strMsg) {
        return this.MCNotifyMsg(strUserid, strPass, strRoomid, strMsg);
    }
}
