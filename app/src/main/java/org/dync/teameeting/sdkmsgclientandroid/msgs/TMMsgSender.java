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

    public int TMInit(String strUid, String strToken, String strServer, int nPort) {
        return this.MCInit(strUid, strToken, strServer, nPort);
    }

    public int TMUnin() {
        return this.MCUnin();
    }

    public int TMSndMsg(String strRoomid, String strMsg) {
        return this.MCSndMsg(strRoomid, strMsg);
    }

    public int TMGetMsg() {
        return this.MCGetMsg();
    }

    public int TMOptRoom(int cmd, String strRoomid, String strRemain) {
        return this.MCOptRoom(cmd, strRoomid, strRemain);
    }

    public int TMSndMsgTo(String strRoomid, String strMsg, String[] arrUser) {
        return  this.MCSndMsgTo(strRoomid, strMsg, arrUser);
    }

    public int TMNotifyMsg(String strRoomid, String strMsg) {
        return this.MCNotifyMsg(strRoomid, strMsg);
    }
}
