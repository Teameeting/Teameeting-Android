package org.dync.teameeting.sdkmsgclientandroid.jni;

/**
 * Created by hp on 12/24/15.
 */
public class JMClientType {
    //* Connection Status
    public static final int CSNOT_CONNECTED = 0;
    public static final int CSCONNECTTING = 1;
    public static final int CSCONNECTED = 2;

    //* MsgClient Callback Error Code
    public static final int ERR_OK = 0;
    public static final int ERR_INVPARAMS = 1;
    public static final int ERR_ERRCONNINFO = 2;
    public static final int ERR_ERRMODUINFO = 3;
    public static final int ERR_ERRTOJSON = 4;
    public static final int ERR_NEXISTROOM = 5;
    public static final int ERR_NEXISTMEM = 6;
    public static final int ERR_EXISTROOM = 7;
    public static final int ERR_EXISTMEM = 8;
    public static final int ERR_INVALID = 9;

    //* MsgClient Opt Room Cmd
    public static final int MCCMD_ENTER = 1;
    public static final int MCCMD_LEAVE = 2;
    public static final int MCCMD_DCOMM = 3;
    public static final int MCMD_MEETCMD_INVALID = 4;

    //* MsgClient send msg tags
    public static final int MCSENDTAGS_TALK=1;
    public static final int MCSENDTAGS_ENTER=2;
    public static final int MCSENDTAGS_LEAVE=3;
    public static final int MCSENDTAGS_SUBSCRIBE=4;
    public static final int MCSENDTAGS_UNSUBSCRIBE=5;
    public static final int MCSENDTAGS_INVALID=6;

    //* MsgClient message type
    public static final int MCMESSAGETYPE_REQUEST=1;
    public static final int MCMESSAGETYPE_RESPONSE=2;
    public static final int MCMESSAGETYPE_INVALID=3;

    //* MsgClient get msg cmd
    public static final int MCCMD_GET_INVALID=1;
}
