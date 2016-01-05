package org.dync.teameeting.ui.activity;

import org.dync.teameeting.R;
import org.dync.teameeting.ui.helper.ShareHelper;
import org.dync.teameeting.structs.ExtraType;
import org.dync.teameeting.widgets.BottomMenu;
import org.dync.teameeting.widgets.BottomMenu.OnTouchSpeedListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class InvitePeopleActivity extends Activity
{

	private final static String TAG = "MainActivity";
	private TextView mCloseTV, mMessageInviteTV, mWeixinInviteTV, mCopyLinkTV;
	Context context;
	private String mShareUrl;
	private ShareHelper mShareHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		context = InvitePeopleActivity.this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invite_people);
		Intent intent = getIntent();
		mShareUrl = intent.getStringExtra("roomUrl");

		inint();

	}

	private void inint()
	{
		mShareHelper = new ShareHelper(InvitePeopleActivity.this);
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
	OnTouchSpeedListener onTouchSpeedListener = new OnTouchSpeedListener()
	{
		@Override
		public void touchSpeed(int velocityX, int velocityY)
		{
			finishActivity();
		}
	};
	private OnClickListener mOnClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View view)
		{

			switch (view.getId())
			{
			case R.id.tv_close:

				finishActivity();
				return;
			case R.id.tv_invite_message:
				finishActivity();
				mShareHelper.shareSMS("邀请你加入我们的会议", mShareUrl);
				break;
			case R.id.tv_invite_weixin:
				mShareHelper.shareWeiXin("分享到... ", "诚挚邀请你加入我们会议吧！点击链接：",
						mShareUrl);
				finishActivity();
				break;
			case R.id.tv_copy_link:
				setResult(ExtraType.RESULT_CODE_ROOM_SETTING_COPY_LINK);
				finish();
				break;

			default:
				break;
			}

		}
	};

	public void finishActivity()
	{
		finish();
		overridePendingTransition(R.anim.activity_close_enter,
				R.anim.activity_close_exit);

	};
}
