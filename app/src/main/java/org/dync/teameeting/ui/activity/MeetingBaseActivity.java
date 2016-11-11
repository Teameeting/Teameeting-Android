/**
 * BaseAcitvity.java [V 1.0.0]
 * classes:org.dync.teammeeting.BaseAcitvity
 * Zlang Create at 2015-11-30.下午2:48:42
 */
package org.dync.teameeting.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.RelativeLayout;

import org.dync.teameeting.R;
import org.dync.teameeting.TeamMeetingApp;
import org.dync.teameeting.bean.ReqSndMsgEntity;
import org.dync.teameeting.chatmessage.ChatMessageClient;
import org.dync.teameeting.chatmessage.IChatMessageInteface;
import org.dync.teameeting.http.NetWork;

import de.greenrobot.event.EventBus;

/**
 * @author ZLang <br/>
 *         create at 2015-11-30 下午2:48:42
 */
public class MeetingBaseActivity extends Activity implements IChatMessageInteface {
    public String TAG = "MeetingBaseActivity";
    public boolean mDebug = TeamMeetingApp.mIsDebug;
    private RelativeLayout mainView;
    private ChatMessageClient mChatMessageClinet;
    String mSign;
    NetWork mNetWork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNetWork = new NetWork();
        mainView = (RelativeLayout) View.inflate(this, R.layout.activity_meeting, null);
        setContentView(mainView);
        registerObserverClinet();
    }

    private void registerObserverClinet() {
        mChatMessageClinet = TeamMeetingApp.getmChatMessageClient();
        mChatMessageClinet.registerObserver(chatMessageObserver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TeamMeetingApp.getTeamMeetingApp().stopMediaplayer();
    }

    /***
     * The message the subscriber
     */
    ChatMessageClient.ChatMessageObserver chatMessageObserver = new ChatMessageClient.ChatMessageObserver() {
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

    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mChatMessageClinet.unregisterObserver(chatMessageObserver);
        EventBus.getDefault().unregister(this);
    }

    public String getSign() {
        return TeamMeetingApp.getmSelfData().getAuthorization();
    }

    public int controllerMoveDistance(View chactView) {
        return chactView.getWidth() / 2;
    }

    @Override
    public void onRequesageMsg(ReqSndMsgEntity requestMsg) {

    }
}
