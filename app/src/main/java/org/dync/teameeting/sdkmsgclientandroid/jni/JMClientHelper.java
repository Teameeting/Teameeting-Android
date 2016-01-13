package org.dync.teameeting.sdkmsgclientandroid.jni;

/**
 * Created by hp on 12/24/15.
 */
public interface JMClientHelper {

    /**
     * when you invoke TMSndMsg, you will recv response on this callback
     * you can alse recv msgs from others in this callback
     * also you will recv all the 'SndMsg' callback here
     * e.g. TMOptRoom(MCCMD_ENTER, @"roomid", @"")
     * TMOptRoom(MCCMD_LEAVE, @"roomid", @"")
     **/
    public void OnSndMsg(String msg);

    /**
     * this callback is not used now.
     **/
    public void OnGetMsg(String msg);

    /**
     * after the msgclient connect to server
     * this callback will be invoked
     **/
    public void OnMsgServerConnected();

    /**
     * when the msgclient disconnect from server
     * this callback will be invoked
     **/
    public void OnMsgServerDisconnect();

    /**
     * when the msgclient connect to server failed
     * this callback will be invoked
     **/
    public void OnMsgServerConnectionFailure();

    /**
     * when the state between server and client has changed
     * this callback will be invoked
     *
     * params:
     *      state: the state of connection between msgclient and msgserver
     **/
    public void OnMsgServerState(int connStatus);
}
