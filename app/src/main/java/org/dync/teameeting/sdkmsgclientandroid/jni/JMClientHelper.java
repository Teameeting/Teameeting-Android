package org.dync.teameeting.sdkmsgclientandroid.jni;

/**
 * Created by hp on 12/24/15.
 */
public interface JMClientHelper {
    public void OnReqSndMsg(String msg);
    public void OnRespSndMsg(String msg);
    public void OnReqGetMsg(String msg);
    public void OnRespGetMsg(String msg);
    public void OnMsgServerConnected();
    public void OnMsgServerDisconnect();
    public void OnMsgServerConnectionFailure();
}
