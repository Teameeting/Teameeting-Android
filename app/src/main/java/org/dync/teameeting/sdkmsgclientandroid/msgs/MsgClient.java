package org.dync.teameeting.sdkmsgclientandroid.msgs;

import android.content.Context;

import org.dync.teameeting.sdkmsgclientandroid.jni.JMClientApp;
import org.dync.teameeting.sdkmsgclientandroid.jni.JMClientHelper;
import org.dync.teameeting.sdkmsgclientandroid.jni.NativeContextRegistry;
import org.dync.teameeting.sdkmsgclientandroid.util.MsgUtils;

/**
 * Created by hp on 12/24/15.
 */
public abstract class MsgClient{

    protected NativeContextRegistry mNativeContext;
    protected JMClientApp           mMApp;
    protected Context               mContext;

    public MsgClient(Context context, JMClientHelper helper) {
        MsgUtils.assertIsTrue(null != context && null != helper);
        mContext = context;


        mNativeContext = new NativeContextRegistry();
        mNativeContext.register(mContext);

        mMApp = new JMClientApp(helper);
    }

    public void Destroy() {
        if (null != mMApp) {
            mMApp.Destroy();
            mMApp = null;
        }
        if (null != mNativeContext) {
            mNativeContext.unRegister();
            mNativeContext = null;
        }
    }

    protected int MCConnStatus() {
       return mMApp.ConnStatus();
    }

    protected int MCInit(String strUid, String strToken, String strServer, int nPort) {
       if (null != mMApp) {
           return mMApp.Init(strUid, strToken, strServer, nPort);
       }  else {
           return -1;
       }
    }

    protected int MCUnin() {
        if (null != mMApp) {
            return mMApp.Unin();
        } else {
            return -1;
        }
    }

    protected int MCSndMsg(String strRoomid, String strMsg) {
        if (null != mMApp) {
            return mMApp.SndMsg(strRoomid, strMsg);
        } else {
            return -1;
        }
    }

    protected int MCGetMsg(int cmd) {
        if (null != mMApp) {
            return mMApp.GetMsg(cmd);
        } else {
            return -1;
        }
    }

    protected int MCOptRoom(int cmd, String strRoomid, String strRemain) {
        if (null != mMApp) {
            return mMApp.OptRoom(cmd, strRoomid, strRemain);
        } else {
            return -1;
        }
    }

    protected int MCSndMsgTo(String strRoomid, String strMsg, String[] arrUser) {
        if (null != mMApp) {
            return mMApp.SndMsgTo(strRoomid, strMsg, arrUser);
        } else {
            return -1;
        }
    }

    protected int MCNotifyMsg(String strRoomid, String strMsg) {
        if (null != mMApp) {
            return mMApp.NotifyMsg(strRoomid, strMsg);
        } else {
            return -1;
        }
    }
}
