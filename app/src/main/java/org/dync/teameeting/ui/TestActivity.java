package org.dync.teameeting.ui;

import org.dync.teameeting.R;
import org.dync.teameeting.TeamMeetingApp;
import org.dync.teameeting.http.NetWork;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class TestActivity extends Activity
{
	Context context = this;
	private NetWork netWork;
	private String sign;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_activity);
		context = this;
		sign = TeamMeetingApp.getMyself().getmAuthorization();
		netWork = new NetWork();
		Init();
	}

	private void Init()
	{
		Button btButton = (Button) findViewById(R.id.send);
		btButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Toast.makeText(context, "测试", 0).show();
				pushTest();
			}
		});
	}

	void pushTest()
	{
		/*
		 * // 推送测试 sign = TeamMeetingApp.getMyself().getmAuthorization();
		 * //mNetWork.pushMeetingMsg(sign, meetingId, "推送消息", "推送概要");
		 * List<String> targetid = new ArrayList<String>();
		 * targetid.add(mUserId); mNetWork.pushCommonMsg(sign, targetid, "推送消息",
		 * "推送概要"); if (mDebug) { Log.i(TAG, "发送"); }
		 */
		// netWork.updatePushtoken(sign, "haha");//更新token
		// netWork.insertUserMeetingRoom(sign, "400000000062", "6");
		
		netWork.insertUserMeetingRoom(sign, "400000000062");
	}
}
