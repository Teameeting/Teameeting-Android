package org.dync.teameeting.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.dync.teameeting.R;
import org.dync.teameeting.TeamMeetingApp;
import org.dync.teameeting.bean.MeetingListEntity;
import org.dync.teameeting.http.HttpContent;
import org.dync.teameeting.structs.EventType;
import org.dync.teameeting.structs.ExtraType;
import org.dync.teameeting.structs.HttpApiTpye;
import org.dync.teameeting.structs.Intent_KEY;
import org.dync.teameeting.ui.helper.Anims;
import org.dync.teameeting.ui.helper.ShareHelper;
import org.dync.teameeting.widgets.BottomMenu;
import org.dync.teameeting.widgets.BottomMenu.OnTouchSpeedListener;
import org.dync.teameeting.widgets.SlideSwitch;
import org.dync.teameeting.widgets.SlideSwitch.SlideListener;

public class RoomSettingActivity extends BaseActivity implements View.OnClickListener {
    private Context context;
    private Boolean mDebug = TeamMeetingApp.mIsDebug;
    private String TAG = "RoomSettingActivity";
    private TextView mTvRoomName;
    private TextView mTvJoninRoom;
    private TextView mTvIniviteMessage;
    private TextView mTvInviteWeixin;
    private TextView mTvCopyLink;
    private LinearLayout mLlNotifications;
    private LinearLayout mLlMeetingPrivate;
    private TextView mTvRenameRoom;
    private TextView mTvDeleteRoom;
    private TextView mTvClose;
    private SlideSwitch mSlideSwitch;
    private SlideSwitch mSlideSwitchPrivate;
    private MeetingListEntity mMeetingEntity;
    private String mMeetingName;
    private String mMeetingId;

    private int mPosition;

    private ShareHelper mShareHelper;

    private String mShareUrl = "Empty url";
    private ImageView ivNotifation;

    private boolean mMeetingPrivateFlag = false;
    private int mOwner;
    private View mvRoomName;
    private View vJoninRoom;
    private View mvIniviteMessage;
    private View vInviteWeixin;
    private View mvInviteWeiXin;
    private View mvCopyLink;
    private View mvNotifications;
    private View mvRenameRoom;
    private View mvDeleteRoom;
    private View mvMeetingPrivate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_setting);
        mShareHelper = new ShareHelper(RoomSettingActivity.this);
        context = this;
        initData();
        initLayout();

        inintSwitchState();
        initwidgetState();
    }


    private void initData() {
        Intent intent = getIntent();
        mPosition = intent.getIntExtra(Intent_KEY.POSITION, 0);
        Bundle extras = intent.getExtras();
        mMeetingEntity = (MeetingListEntity) extras.getSerializable(Intent_KEY.MEETING_ENTY);

        mMeetingId = mMeetingEntity.getMeetingid();
        mMeetingName = mMeetingEntity.getMeetname();
        mOwner = mMeetingEntity.getOwner();

        if (mDebug) {
            Log.e(TAG, mMeetingEntity.toString());
        }

        mSign = getSign();
        mShareUrl = "Let us see in a meeting!:" + "http://115.28.70.232/share_meetingRoom/#" + mMeetingId;
    }

    void initLayout() {
        mTvRoomName = (TextView) findViewById(R.id.tv_room_name);
        mvRoomName = findViewById(R.id.v_room_name);

        mTvRoomName.setText(mMeetingName);
        mTvJoninRoom = (TextView) findViewById(R.id.tv_join_room);
        vJoninRoom = findViewById(R.id.v_join_room);
        mTvJoninRoom.setOnClickListener(this);

        mTvIniviteMessage = (TextView) findViewById(R.id.tv_invite_message);
        mvIniviteMessage = findViewById(R.id.v_invite_message);
        mTvIniviteMessage.setOnClickListener(this);

        mTvInviteWeixin = (TextView) findViewById(R.id.tv_invite_weixin);
        mvInviteWeiXin = findViewById(R.id.v_invite_weixin);
        mTvInviteWeixin.setOnClickListener(this);

        mTvCopyLink = (TextView) findViewById(R.id.tv_copy_link);
        mvCopyLink = findViewById(R.id.v_copy_link);
        mTvCopyLink.setOnClickListener(this);

        mLlNotifications = (LinearLayout) findViewById(R.id.ll_notifications);
        mvNotifications = findViewById(R.id.v_notifications);
        mLlNotifications.setOnClickListener(this);
        // mLlNotifications.setOnTouchListener();

        mTvRenameRoom = (TextView) findViewById(R.id.tv_rename_room);
        mvRenameRoom = findViewById(R.id.v_rename_room);
        mTvRenameRoom.setOnClickListener(this);

        mTvDeleteRoom = (TextView) findViewById(R.id.tv_delete_room);
        mvDeleteRoom = findViewById(R.id.v_delete_room);
        mTvDeleteRoom.setOnClickListener(this);

        mTvClose = (TextView) findViewById(R.id.tv_close);
        mTvClose.setOnClickListener(this);
        mSlideSwitch = (SlideSwitch) findViewById(R.id.ss_SlideSwitch);
        mSlideSwitch.setSlideListener(slideNotificationListener);

        mSlideSwitchPrivate = (SlideSwitch) findViewById(R.id.ss_SlideSwitch_private);

        mSlideSwitchPrivate.setSlideListener(mslideMeetingPrivateListener);

        mLlMeetingPrivate = (LinearLayout) findViewById(R.id.ll_private);
        mvMeetingPrivate = findViewById(R.id.v_private);
        mLlMeetingPrivate.setOnClickListener(this);

        ivNotifation = (ImageView) findViewById(R.id.iv_notifications);

        BottomMenu bottomMenu = (BottomMenu) findViewById(R.id.bottomMenu);
        bottomMenu.setOnTouchQuickSpeedListener(onTouchSpeedListener);


    }

    private void inintSwitchState() {
        boolean state = mMeetingEntity.getPushable() == 1 ? true : false;
        mSlideSwitch.setState(state);
        if (!state) {
            Anims.ScaleAnim(ivNotifation, 0, 1, 10);
        }

        mMeetingPrivateFlag = mMeetingEntity.getMeetusable() == 2 ? true : false;
        mSlideSwitchPrivate.setState(mMeetingPrivateFlag);

    }


    private void initwidgetState() {
        int visible;
        if (mOwner == 1) {
            visible = View.VISIBLE;
        } else {
            visible = View.GONE;
        }
        viewVisilility(visible);
    }

    public void viewVisilility(int visible) {

        mTvIniviteMessage.setVisibility(visible);
        mvIniviteMessage.setVisibility(visible);

        mTvInviteWeixin.setVisibility(visible);
        mvInviteWeiXin.setVisibility(visible);

        mTvCopyLink.setVisibility(visible);
        mvCopyLink.setVisibility(visible);

        mTvRenameRoom.setVisibility(visible);
        mvRenameRoom.setVisibility(visible);
        mLlMeetingPrivate.setVisibility(visible);
        mvMeetingPrivate.setVisibility(visible);

    }


    /**
     * 　Touch slide Listener
     */
    OnTouchSpeedListener onTouchSpeedListener = new OnTouchSpeedListener() {

        @Override
        public void touchSpeed(int velocityX, int velocityY) {
            setResult(ExtraType.RESULT_CODE_ROOM_SETTING_CLOSE);
            finishActivity();
        }
    };


    private void meetingPrivateUIUpdate() {
        if (mMeetingPrivateFlag) {
            mTvIniviteMessage.setTextColor(getResources().getColor(R.color.darkGray));
            mTvInviteWeixin.setTextColor(getResources().getColor(R.color.darkGray));
            mTvCopyLink.setTextColor(getResources().getColor(R.color.darkGray));
            mTvIniviteMessage.setClickable(false);
            mTvInviteWeixin.setClickable(false);
            mTvCopyLink.setClickable(false);
        } else {
            mTvIniviteMessage.setTextColor(getResources().getColor(R.color.white));
            mTvInviteWeixin.setTextColor(getResources().getColor(R.color.white));
            mTvCopyLink.setTextColor(getResources().getColor(R.color.white));
            mTvIniviteMessage.setClickable(true);
            mTvInviteWeixin.setClickable(true);
            mTvCopyLink.setClickable(true);
        }
    }

    /**
     * mslideMeetingPrivateListener
     */
    SlideListener mslideMeetingPrivateListener = new SlideListener() {
        public void open() {
            mNetWork.updateRoomEnable(mSign, mMeetingId, HttpApiTpye.RoomEnablePrivate, mPosition);
            mMeetingPrivateFlag = true;
            meetingPrivateUIUpdate();
        }

        @Override
        public void close() {
            mNetWork.updateRoomEnable(mSign, mMeetingId, HttpApiTpye.RoomEnableYes, mPosition);
            mMeetingPrivateFlag = false;
            meetingPrivateUIUpdate();
        }
    };

    /**
     * slideNotificationListener
     */

    SlideListener slideNotificationListener = new SlideListener() {
        @Override
        public void open() {

            mNetWork.updateRoomPushable(mSign, mMeetingId, HttpApiTpye.pushableYes, mPosition);
            Anims.ScaleAnim(ivNotifation, 1, 0, 500);
        }

        @Override
        public void close() {

            mNetWork.updateRoomPushable(mSign, mMeetingId, HttpApiTpye.pushableNO, mPosition);
            Anims.ScaleAnim(ivNotifation, 0, 1, 500);
        }
    };


    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.tv_close:
                setResult(ExtraType.RESULT_CODE_ROOM_SETTING_CLOSE);
                if (mDebug)
                    Log.e(TAG, "onClick: setResult");

                finishActivity();
                return;
            case R.id.tv_join_room:
                statrMeetingActivity(mMeetingName, mMeetingId);
                finishActivity();
                break;
            case R.id.tv_invite_message:
                // SMS

                mShareHelper.shareSMS(this, "", mShareUrl);

                break;
            case R.id.tv_invite_weixin:
                // weixin

                mShareHelper.shareWeiXin("Share into ... ", "", mShareUrl);
                finishActivity();
                break;
            case R.id.tv_copy_link:

                intent = new Intent();
                intent.putExtra("shareUrl", mShareUrl);
                setResult(ExtraType.RESULT_CODE_ROOM_SETTING_COPY_LINK, intent);
                finish();

                break;
            case R.id.ll_notifications:
                if (mSlideSwitch.isOpen) {
                    mSlideSwitch.moveToDest(false);
                } else {
                    mSlideSwitch.moveToDest(true);
                }
                break;
            case R.id.ll_private:
                if (mSlideSwitchPrivate.isOpen) {
                    mSlideSwitchPrivate.moveToDest(false);
                } else {
                    mSlideSwitchPrivate.moveToDest(true);
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


    private void statrMeetingActivity(String meetingName, String meetingId) {
        Intent intent = new Intent(context, MeetingActivity.class);
        intent.putExtra("meetingName", meetingName);
        intent.putExtra("meetingId", meetingId);
        intent.putExtra("userId", TeamMeetingApp.getTeamMeetingApp().getDevId());
        //  startActivityForResult(intent, ExtraType.REQUEST_CODE_ROOM_MEETING);
        startActivity(intent);
    }

    private void finishActivity() {
        finish();
        overridePendingTransition(R.anim.activity_close_enter,
                R.anim.activity_close_exit);
    }

    @Override
    public void onEventMainThread(Message msg) {
        switch (EventType.values()[msg.what]) {
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
