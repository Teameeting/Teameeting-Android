package org.dync.teameeting.msgs;

import android.app.Activity;

import org.dync.teameeting.sdkmsgclientandroid.jni.JMClientApp;
import org.dync.teameeting.sdkmsgclientandroid.jni.JMClientHelper;
import org.dync.teameeting.sdkmsgclientandroid.jni.NativeContextRegistry;
import org.dync.teameeting.sdkmsgclientandroid.util.MsgUtils;

/**
 * Created by hp on 12/24/15.
 */
public abstract class MsgClient implements JMClientHelper{

    protected NativeContextRegistry mNativeContext;
    protected JMClientApp           mMApp;
    protected Activity              mActivity;

    public MsgClient(Activity activity) {
        MsgUtils.assertIsTrue(null != activity);
        mActivity = activity;

        mNativeContext = new NativeContextRegistry();
        mNativeContext.register(mActivity);

        mMApp = new JMClientApp(this);
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

    protected int MCInit(String strServer, int nPort) {
       if (null != mMApp) {
           return mMApp.Init(strServer, nPort);
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

    protected int MCLogin(String strUserid, String strPass) {
        if (null != mMApp) {
            return mMApp.Login(strUserid, strPass);
        } else {
            return -1;
        }
    }

    protected int MCSndMsg(String strUserid, String strPass, String strRoomid, String strMsg) {
        if (null != mMApp) {
            return mMApp.SndMsg(strUserid, strPass, strRoomid, strMsg);
        } else {
            return -1;
        }
    }

    protected int MCGetMsg(String strUserid, String strPass) {
        if (null != mMApp) {
            return mMApp.GetMsg(strUserid, strPass);
        } else {
            return -1;
        }
    }

    protected int MCLogout(String strUserid, String strPass) {
        if (null != mMApp) {
            return mMApp.Logout(strUserid, strPass);
        } else {
            return -1;
        }
    }

    protected int MCOptRoom(int cmd, String strUserid, String strPass, String strRoomid, String strRemain) {
        if (null != mMApp) {
            return mMApp.OptRoom(cmd, strUserid, strPass, strRoomid, strRemain);
        } else {
            return -1;
        }
    }

    protected int MCSndMsgTo(String strUserid, String strPass, String strRoomid, String strMsg, String[] arrUser) {
        if (null != mMApp) {
            return mMApp.SndMsgTo(strUserid, strPass, strRoomid, strMsg, arrUser);
        } else {
            return -1;
        }
    }
}
