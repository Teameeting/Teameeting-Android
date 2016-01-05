package org.dync.teameeting.sdkmsgclientandroid.jni;

/**
 * Created by hp on 12/24/15.
 */
public class JMClientType {
    //* Connection Status
    public static final int NOT_CONNECTED = 0;
    public static final int RESOLVING = 1;
    public static final int CONNECTTING = 2;
    public static final int CONNECTED = 3;


    //* MsgClient Callback Code
    public static final int CODE_OK = 0;
    public static final int CODE_INVPARAMS = 1;
    public static final int CODE_ERRCONNINFO = 2;
    public static final int CODE_ERRMODUINFO = 3;
    public static final int CODE_ERRTOJSON = 4;
    public static final int CODE_NEXISTROOM = 5;
    public static final int CODE_NEXISTMEM = 6;
    public static final int CODE_EXISTROOM = 7;
    public static final int CODE_EXISTMEM = 8;
    public static final int CODE_INVALID = 9;

    //* MsgClient Opt Room Cmd
    public static final int TMCMD_ENTER = 1;
    public static final int TMCMD_LEAVE = 2;
    public static final int TMCMD_CREATE = 3;
    public static final int TMCMD_DESTROY = 4;
    public static final int TMCMD_START = 5;
    public static final int TMCMD_STOP = 6;
    public static final int TMCMD_DCOMM = 7;
    public static final int TMCMD_REFRESH = 8;
    public static final int TMCMD_MEETCMD_INVALID = 9;
}
