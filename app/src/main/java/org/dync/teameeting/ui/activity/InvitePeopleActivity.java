package org.dync.teameeting.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import org.dync.teameeting.R;
import org.dync.teameeting.TeamMeetingApp;
import org.dync.teameeting.bean.MeetingListEntity;
import org.dync.teameeting.structs.ExtraType;
import org.dync.teameeting.structs.ShareUrl;
import org.dync.teameeting.ui.helper.ShareHelper;
import org.dync.teameeting.widgets.BottomMenu;
import org.dync.teameeting.widgets.BottomMenu.OnTouchSpeedListener;

public class InvitePeopleActivity extends Activity {

    private final static String TAG = "MainActivity";
    private boolean mDebug = TeamMeetingApp.mIsDebug;
    private TextView mCloseTV, mMessageInviteTV, mWeixinInviteTV, mCopyLinkTV;
    private Context mContext;
    private String mShareUrl;
    private ShareHelper mShareHelper;
    private MeetingListEntity mMeetingEntity;
    private String mMeetingId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_people);
        inintView();
        inintDate();

    }


    private void inintDate() {

        mContext = InvitePeopleActivity.this;
        Intent intent = getIntent();
        mMeetingId = intent.getStringExtra("meetingId");

        if (mDebug) {
            Log.e(TAG, " mMeetingId " + mMeetingId);
        }

        mShareUrl = ShareUrl.SHARE_URL + mMeetingId;
        mShareHelper = new ShareHelper(mContext);
    }

    private void inintView() {

        mCloseTV = (TextView) findViewById(R.id.tv_close);
        mMessageInviteTV = (TextView) findViewById(R.id.tv_invite_message);
        mWeixinInviteTV = (TextView) findViewById(R.id.tv_invite_weixin);
        mCopyLinkTV = (TextView) findViewById(R.id.tv_copy_link);

        mCloseTV.setOnClickListener(mOnClickListener);
        mMessageInviteTV.setOnClickListener(mOnClickListener);
        mWeixinInviteTV.setOnClickListener(mOnClickListener);
        mCopyLinkTV.setOnClickListener(mOnClickListener);

        BottomMenu bottomMenu = (BottomMenu) findViewById(R.id.bottomMenu);
        bottomMenu.setOnTouchQuickSpeedListener(onTouchSpeedListener);

    }

    /**
     * Touch slide Listener
     */
    OnTouchSpeedListener onTouchSpeedListener = new OnTouchSpeedListener() {
        @Override
        public void touchSpeed(int velocityX, int velocityY) {
            finishActivity();
        }
    };
    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.tv_close:

                    finishActivity();
                    return;
                case R.id.tv_invite_message:

                    mShareHelper.shareSMS(mContext, "", mShareUrl);
                    finishActivity();
                    break;
                case R.id.tv_invite_weixin:
                    mShareHelper.shareWeiXin(mShareUrl);
                    finishActivity();
                    break;
                case R.id.tv_copy_link:
                    Intent intent = new Intent();
                    intent.putExtra("shareUrl", mShareUrl);
                    setResult(ExtraType.RESULT_CODE_ROOM_SETTING_COPY_LINK, intent);

                    finish();
                    break;

                default:
                    break;
            }

        }
    };

    public void finishActivity() {
        finish();
        overridePendingTransition(R.anim.activity_close_enter,
                R.anim.activity_close_exit);

    }

    ;
}
