package org.dync.teameeting.sdkmsgclientandroid.jni;

/**
 * Created by hp on 12/24/15.
 */
public interface JMClientHelper {
    public void OnReqLogin(int code, String status, String userid);
    public void OnRespLogin(int code, String status, String userid);
    public void OnReqSndMsg(String msg);
    public void OnRespSndMsg(String msg);
    public void OnReqGetMsg(String msg);
    public void OnRespGetMsg(String msg);
    public void OnReqLogout(int code, String status, String userid);
    public void OnRespLogout(int code, String status, String userid);
    public void OnMsgServerConnected();
    public void OnMsgServerDisconnect();
    public void OnMsgServerConnectionFailure();
}
