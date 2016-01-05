package org.dync.teameeting.sdkmsgclientandroid.jni;


/**
 * Created by hp on 12/24/15.
 */
public class JMClientApp {
    /**
     * 构造访问jni底层库的对象
     */

    private final long fNativeAppId;

    public JMClientApp(JMClientHelper helper) {
        fNativeAppId = Create(helper);
    }

    private static native long Create(JMClientHelper helper);

    public native int ConnStatus();
    public native int Init(String strServer, int port);
    public native int Unin();

    public native int Login(String strUserid, String strPass);
    public native int SndMsg(String strUserid, String strPass, String strRoomid, String strMsg);
    public native int GetMsg(String strUserid, String strPass);
    public native int Logout(String strUserid, String strPass);
    public native int OptRoom(int cmd, String strUserid, String strPass, String strRoomid, String strRemain);
    public native int SndMsgTo(String strUserid, String strPass, String strRoomid, String strMsg, String[] arrUser);



    /**
     * 销毁APP
     */
    public native void Destroy();
}
