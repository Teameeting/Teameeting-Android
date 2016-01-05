package org.dync.teameeting.ui.activity;

import org.apache.http.Header;
import org.dync.teameeting.R;
import org.dync.teameeting.TeamMeetingApp;
import org.dync.teameeting.bean.MeetingList;
import org.dync.teameeting.http.HttpContent;
import org.dync.teameeting.http.NetWork;
import org.dync.teameeting.structs.EventType;
import org.dync.teameeting.structs.Intent_KEY;
import org.dync.teameeting.ui.helper.Anims;
import org.dync.teameeting.ui.helper.ShareHelper;
import org.dync.teameeting.structs.ExtraType;
import org.dync.teameeting.widgets.BottomMenu;
import org.dync.teameeting.widgets.BottomMenu.OnTouchSpeedListener;
import org.dync.teameeting.widgets.SlideSwitch;
import org.dync.teameeting.widgets.SlideSwitch.SlideListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import javax.security.auth.login.LoginException;

public class RoomSettingActivity extends BaseActivity implements android.view.View.OnClickListener
{
    private Context context;
    private Boolean mDebug = TeamMeetingApp.mIsDebug;
    private String TAG = "RoomSettingActivity";
    private TextView mTvRoomName;
    private TextView mTvJoninRoom;
    private TextView mTvIniviteMessage;
    private TextView mTvInviteWeixin;
    private TextView mTvCopyLink;
    private LinearLayout mLlNotifications;
    private TextView mTvRenameRoom;
    private TextView mTvDeleteRoom;
    private TextView mTvClose;
    private SlideSwitch mSlideSwitch;
   MeetingList.MeetingListEntity mMeetingEntity;
    private String mMeetingName;
    private String mMeetingId;

    private int mPosition;

    private ShareHelper mShareHelper;

    private String mShareUrl = "没有设置连接";
    private ImageView ivNotifation;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_setting);
        mShareHelper = new ShareHelper(RoomSettingActivity.this);
        context = this;
        initData();
        initLayout();
        initNotifationState();
    }

    private void initData()
    {
        Intent intent = getIntent();
        mPosition = intent.getIntExtra(Intent_KEY.POSITION, 0);
        Bundle extras = intent.getExtras();
        mMeetingEntity = (MeetingList.MeetingListEntity) extras.getSerializable(Intent_KEY.MEETING_ENTY);
        mMeetingId = mMeetingEntity.getMeetingid();
        if (mDebug)
        {
            Log.e(TAG, mMeetingEntity.toString());
        }
    }

    void initLayout()
    {
        mTvRoomName = (TextView) findViewById(R.id.tv_room_name);
        mTvRoomName.setText(mMeetingName);
        mTvJoninRoom = (TextView) findViewById(R.id.tv_join_room);
        mTvJoninRoom.setOnClickListener(this);

        mTvIniviteMessage = (TextView) findViewById(R.id.tv_invite_message);
        mTvIniviteMessage.setOnClickListener(this);

        mTvInviteWeixin = (TextView) findViewById(R.id.tv_invite_weixin);
        mTvInviteWeixin.setOnClickListener(this);

        mTvCopyLink = (TextView) findViewById(R.id.tv_copy_link);
        mTvCopyLink.setOnClickListener(this);

        mLlNotifications = (LinearLayout) findViewById(R.id.ll_notifications);
        mLlNotifications.setOnClickListener(this);
        // mLlNotifications.setOnTouchListener();

        mTvRenameRoom = (TextView) findViewById(R.id.tv_rename_room);
        mTvRenameRoom.setOnClickListener(this);

        mTvDeleteRoom = (TextView) findViewById(R.id.tv_delete_room);
        mTvDeleteRoom.setOnClickListener(this);

        mTvClose = (TextView) findViewById(R.id.tv_close);
        mTvClose.setOnClickListener(this);
        mSlideSwitch = (SlideSwitch) findViewById(R.id.ss_SlideSwitch);
        mSlideSwitch.setSlideListener(slideListener);



        ivNotifation = (ImageView) findViewById(R.id.iv_notifications);

        BottomMenu bottomMenu = (BottomMenu) findViewById(R.id.bottomMenu);
        bottomMenu.setOnTouchQuickSpeedListener(onTouchSpeedListener);
    }

    private void initNotifationState()
    {
        boolean state = mMeetingEntity.getPushable() == 1 ? true : false;
        mSlideSwitch.setState(state);
        if (!state)
        {
            Anims.ScaleAnim(ivNotifation,0,1,10);
        }


    }

    /**
     * 　Touch slide Listener
     */
    OnTouchSpeedListener onTouchSpeedListener = new OnTouchSpeedListener()
    {

        @Override
        public void touchSpeed(int velocityX, int velocityY)
        {
            setResult(ExtraType.RESULT_CODE_ROOM_SETTING_CLOSE);
            finishActivity();
        }
    };

    /**
     * slideListener
     */

    SlideListener slideListener = new SlideListener()
    {
        @Override
        public void open()
        {
            mSign = getSign();
            mNetWork.updateRoomPushable(mSign,mMeetingId, 1 + "");
            Anims.ScaleAnim(ivNotifation,1,0,500);
            Toast.makeText(context, "打开推送", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void close()
        {
            mSign = getSign();
            mNetWork.updateRoomPushable(mSign, mMeetingId,0 + "");
            Anims.ScaleAnim(ivNotifation,0,1,500);
            String url = "meeting/getMeetingInfo/"+ mMeetingId;

            Log.e(TAG, "close: mMeetingId"+mMeetingId);
            HttpContent.get(url, new TextHttpResponseHandler()
            {
                @Override
                public void onFailure(int i, Header[] headers, String respospone, Throwable throwable)
                {
                    Log.e(TAG, "onFailure:  respospone" + respospone);
                }

                @Override
                public void onSuccess(int i, Header[] headers, String respospone)
                {
                    Log.e(TAG, "onSuccess:  respospone" + respospone);
                }
            });

        }
    };

    @Override
    public void onClick(View view)
    {
        Intent intent = null;
        switch (view.getId())
        {
            case R.id.tv_close:
                setResult(ExtraType.RESULT_CODE_ROOM_SETTING_CLOSE);
                if (mDebug)
                    Log.e(TAG, "onClick: setResult");

                finishActivity();
                return;
            case R.id.tv_join_room:

                intent = new Intent(RoomSettingActivity.this, MeetingActivity.class);
                intent.putExtra("meetingName", mMeetingName);
                finishActivity();
                break;
            case R.id.tv_invite_message:
                // SMS
                mShareHelper.shareSMS("邀请你加入我们的会议", mShareUrl);
                finishActivity();

                break;
            case R.id.tv_invite_weixin:
                // weixin
                mShareHelper
                        .shareWeiXin("分享到... ", "诚挚邀请你加入我们会议吧！点击链接：", mShareUrl);
                finishActivity();
                break;
            case R.id.tv_copy_link:
                setResult(ExtraType.RESULT_CODE_ROOM_SETTING_COPY_LINK);
                finish();

                break;
            case R.id.ll_notifications:
                if (mSlideSwitch.isOpen)
                {
                    mSlideSwitch.moveToDest(false);
                } else
                {
                    mSlideSwitch.moveToDest(true);
                }
                break;
            case R.id.tv_rename_room:

                intent = new Intent();
                intent.putExtra("position", mPosition);
                intent.putExtra("meetingId", mMeetingId);
                intent.putExtra("meetingName", mMeetingName);
                setResult(ExtraType.RESULT_CODE_ROOM_SETTING_RENAME, intent);

                finishActivity();
                break;
            case R.id.tv_delete_room:
                intent = new Intent();
                intent.putExtra("position", mPosition);
                intent.putExtra("meetingId", mMeetingId);
                setResult(ExtraType.RESULT_CODE_ROOM_SETTING_DELETE, intent);
                finishActivity();
                break;

            default:
                break;
        }

    }

    private void finishActivity()
    {
        finish();
        overridePendingTransition(R.anim.activity_close_enter,
                R.anim.activity_close_exit);
    }

    @Override
    public void onEventMainThread(Message msg)
    {
        switch (EventType.values()[msg.what])
        {
            case MSG_UPDATE_ROOM_PUSHABLE_SUCCESS:
                if (mDebug)
                    Log.e(TAG, "onEventMainThread: MSG_UPDATE_ROOM_PUSHABLE_SUCCESS");
                break;
            case MSG_UPDATE_ROOM_PUSHABLE_FAILED:
                if (mDebug)
                    Log.e(TAG, "onEventMainThread: MSG_UPDATE_ROOM_PUSHABLE_FAILED");
                break;
        }
    }
}
