package org.dync.teameeting.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.dync.teameeting.R;
import org.dync.teameeting.TeamMeetingApp;
import org.dync.teameeting.bean.MeetingListEntity;
import org.dync.teameeting.structs.EventType;
import org.dync.teameeting.structs.ExtraType;
import org.dync.teameeting.structs.HttpApiTpye;
import org.dync.teameeting.structs.Intent_KEY;
import org.dync.teameeting.structs.ShareUrl;
import org.dync.teameeting.ui.helper.Anims;
import org.dync.teameeting.ui.helper.DialogHelper;
import org.dync.teameeting.ui.helper.ShareHelper;
import org.dync.teameeting.widgets.BottomMenu;
import org.dync.teameeting.widgets.BottomMenu.OnTouchSpeedListener;
import org.dync.teameeting.widgets.SlideSwitch;
import org.dync.teameeting.widgets.SlideSwitch.SlideListener;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.greenrobot.event.EventBus;


public class RoomSettingActivity extends BaseActivity implements View.OnClickListener {
    private static String TAG = "RoomSettingActivity";
    private Context context;
    private Boolean mDebug = TeamMeetingApp.mIsDebug;
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
    private View mvInviteWeiXin;
    private View mvCopyLink;
    private View mvNotifications;
    private View mvRenameRoom;
    private View mvDeleteRoom;
    private View mvMeetingPrivate;
    private boolean mNotificationsStates;
    private boolean isStartActivity = false;

    /**
     * ?Touch slide Listener
     */
    OnTouchSpeedListener onTouchSpeedListener = new OnTouchSpeedListener() {
        @Override
        public void touchSpeed(int velocityX, int velocityY) {
            setResult(ExtraType.RESULT_CODE_ROOM_SETTING_CLOSE);
            finishActivity();
        }
    };

    /**
     * mslideMeetingPrivateListener
     */
    SlideListener mslideMeetingPrivateListener = new SlideListener() {
        public void open() {
            mNetWork.updateRoomEnable(mSign, mMeetingId, HttpApiTpye.RoomEnablePrivate, mPosition);

            mMeetingPrivateFlag = true;
            if (isStartActivity){
                privateDilaog.show();
            }
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

            Anims.ScaleAnim(ivNotifation, 1, 0, 100);

            mNotificationsStates = true;
        }

        @Override
        public void close() {
            mNetWork.updateRoomPushable(mSign, mMeetingId, HttpApiTpye.pushableNO, mPosition);
            ivNotifation.setVisibility(View.VISIBLE);
            if (mNotificationsStates)
                Anims.ScaleAnim(ivNotifation, 0, 1, 100);
            mNotificationsStates = false;
        }
    };
    private SweetAlertDialog privateDilaog;

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

    @Override
    protected void onResume() {
        super.onResume();
        mTvJoninRoom.requestFocus();
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
        mShareUrl = ShareUrl.SHARE_URL + mMeetingId;

        createPrivateDialog();
    }

    private void createPrivateDialog() {
        privateDilaog = DialogHelper.createPrivateDilaog(context);
        privateDilaog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                mMeetingPrivateFlag = true;
                sweetAlertDialog.dismiss();
            }
        });
        privateDilaog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                mMeetingPrivateFlag = false;
                sweetAlertDialog.dismiss();
                mSlideSwitchPrivate.setState(mMeetingPrivateFlag);
            }
        });
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
        if (mMeetingEntity.getPushable() == 0) {
            ivNotifation.setVisibility(View.VISIBLE);
            mNotificationsStates = false;
        } else {
            ivNotifation.setVisibility(View.GONE);
            mNotificationsStates = true;
        }
        mSlideSwitch.setState(mNotificationsStates);
        mMeetingPrivateFlag = (mMeetingEntity.getMeetenable() == 2) ? true : false;
        mSlideSwitchPrivate.setState(mMeetingPrivateFlag);
        isStartActivity=true;
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

    @Override
    public void onClick(View view) {
        Intent intent = null;

        switch (view.getId()) {
            case R.id.tv_close:
                setResult(ExtraType.RESULT_CODE_ROOM_SETTING_CLOSE);

                if (mDebug) {
                    Log.e(TAG, "onClick: setResult");
                }

                finishActivity();

                return;

            case R.id.tv_join_room:
                statrMeetingActivity();
                finishActivity();

                break;

            case R.id.tv_invite_message:
                // SMS
                mShareHelper.shareSMS(this, "", mShareUrl);
                break;

            case R.id.tv_invite_weixin:
                // weixin
                mShareHelper.shareWeiXin(mShareUrl);
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

    private void statrMeetingActivity() {
        Message msg = Message.obtain();
        msg.obj = mPosition;
        msg.what = EventType.MSG_ROOMSEETING_ENTER_ROOM.ordinal();
        EventBus.getDefault().post(msg);

       /* Intent intent = new Intent(context, MeetingActivity.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable("meetingListEntity", mMeetingEntity);
        intent.putExtras(bundle);
        startActivity(intent);*/
    }

    private void finishActivity() {
        finish();
        overridePendingTransition(R.anim.activity_close_enter,
                R.anim.activity_close_exit);
    }

    public void onEventMainThread(Message msg) {
        switch (EventType.values()[msg.what]) {
            case MSG_UPDATE_ROOM_PUSHABLE_SUCCESS:

                if (mDebug) {
                    Log.e(TAG, "onEventMainThread: MSG_UPDATE_ROOM_PUSHABLE_SUCCESS");
                }

                break;

            case MSG_UPDATE_ROOM_PUSHABLE_FAILED:

                if (mDebug) {
                    Log.e(TAG, "onEventMainThread: MSG_UPDATE_ROOM_PUSHABLE_FAILED");
                }

                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(ExtraType.RESULT_CODE_ROOM_SETTING_CLOSE);
            finishActivity();
        }
        return super.onKeyDown(keyCode, event);
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
}
