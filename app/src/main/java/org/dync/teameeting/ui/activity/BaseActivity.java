package org.dync.teameeting.ui.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.ypy.eventbus.EventBus;

import org.dync.teameeting.TeamMeetingApp;
import org.dync.teameeting.bean.ReqSndMsgEntity;
import org.dync.teameeting.chatmessage.ChatMessageClient;
import org.dync.teameeting.chatmessage.IChatMessageInteface;
import org.dync.teameeting.http.NetWork;

import cn.jpush.android.api.JPushInterface;

public class BaseActivity extends Activity implements IChatMessageInteface {
    public NetWork mNetWork;
    public String mSign;
    public boolean mDebug = TeamMeetingApp.mIsDebug;
    private String TAG = "BaseActivity";
    private ChatMessageClient mChatMessageClinet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNetWork = new NetWork();
        EventBus.getDefault().register(this);
        if (!TeamMeetingApp.isPad) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        registerObserverClinet();
    }

    private void registerObserverClinet() {
        mChatMessageClinet = TeamMeetingApp.getmChatMessageClient();
        mChatMessageClinet.registerObserver(new ChatMessageClient.ChatMessageObserver() {
            @Override
            public void OnReqSndMsg(final ReqSndMsgEntity reqSndMsg) {
                if (Looper.myLooper() != Looper.getMainLooper()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onRequesageMsg(reqSndMsg);
                        }
                    });
                } else {
                    onRequesageMsg(reqSndMsg);
                }
            }

            @Override
            public void onMeetingNumChange(ReqSndMsgEntity reqSndMsg) {

            }
        });
    }

    public void initNetWork() {
        String userid = TeamMeetingApp.getTeamMeetingApp().getDevId();
        mNetWork.init(userid, "2", "2", "2", "TeamMeeting");
    }

    public String getSign() {
        return TeamMeetingApp.getmSelfData().getAuthorization();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // init();
        if (mDebug) {
            Log.i(TAG, "onResume: ");
        }
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mDebug) {
            Log.i(TAG, "onPause: ");
        }
        JPushInterface.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mDebug) {
            Log.i(TAG, "onStop: ");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDebug) {
            Log.i(TAG, "onDestroy: ");
        }
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(Message msg) {

    }

    @Override
    public void onRequesageMsg(ReqSndMsgEntity requestMsg) {

    }

    public void onMeetingNumChange(ReqSndMsgEntity requestMsg) {
    }

}
