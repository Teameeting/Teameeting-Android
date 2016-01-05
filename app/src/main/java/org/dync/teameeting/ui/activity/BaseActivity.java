package org.dync.teameeting.ui.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.ypy.eventbus.EventBus;

import org.dync.teameeting.http.NetWork;
import org.dync.teameeting.TeamMeetingApp;

import cn.jpush.android.api.JPushInterface;

public class BaseActivity extends Activity
{
    public NetWork mNetWork;
    public String mSign;
    public boolean mDebug = TeamMeetingApp.mIsDebug;
    private String TAG = "BaseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mNetWork = new NetWork();
        EventBus.getDefault().register(this);
        if (!TeamMeetingApp.isPad)
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        if (mDebug)
        {
            Log.e(TAG, "onCreate: BaseActivity");
        }
    }


    public void initNetWork()
    {
        String userid = TeamMeetingApp.getTeamMeetingApp().getDevId();
        mNetWork.init(userid, "2", "2", "2", "TeamMeeting");
    }

    public String getSign()
    {
       return TeamMeetingApp.getmSelfData().getAuthorization();
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        // init();
        if (mDebug)
        {
            Log.i(TAG, "onResume: ");
        }
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (mDebug)
        {
            Log.i(TAG, "onPause: ");
        }
        JPushInterface.onPause(this);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if (mDebug)
        {
            Log.i(TAG, "onStop: ");
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (mDebug)
        {
            Log.i(TAG, "onDestroy: ");
        }
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(Message msg)
    {

    }

}
