package org.dync.teameeting;

import android.app.Application;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.util.Log;
import android.widget.Toast;

import org.anyrtc.Anyrtc;
import org.dync.teameeting.bean.SelfData;
import org.dync.teameeting.sdkmsgclientandroid.msgs.TMMsgSender;
import org.dync.teameeting.receiver.NetWorkReceiver;
import org.dync.teameeting.utils.ScreenUtils;

import cn.jpush.android.api.JPushInterface;

public class TeamMeetingApp extends Application
{

    public static boolean mIsDebug = true;// debug deal with
    private static final String TAG = "Application";
    private static final boolean mDebug = true;
    private static TeamMeetingApp mTeamMeetingApp;

    private static SelfData mSelfData;
    private NetWorkReceiver mNetReceiver;
    public static boolean isPad = false;
    private static TMMsgSender mMsgSender;

    @Override
    public void onCreate()
    {
        // TODO Auto-generated method stub
        super.onCreate();
        mSelfData = new SelfData();
        registerReceiver();

        isPad = ScreenUtils.isPad(this);
        if (mDebug)
        {
            Log.e(TAG, "onCreate: isPad" + isPad);
        }
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

        Anyrtc.InitAnyrtc("mzw0001", "defq34hj92mxxjhaxxgjfdqi1s332dd", "d74TcmQDMB5nWx9zfJ5al7JdEg3XwySwCkhdB9lvnd1", "org.dync.app");
    }

    /**
     * registerReceiver
     */
    private void registerReceiver()
    {
        mNetReceiver = new NetWorkReceiver();
        IntentFilter netFilter = new IntentFilter();
        netFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetReceiver, netFilter);

    }

    public TeamMeetingApp()
    {
        super();
        mTeamMeetingApp = this;
    }


    public static TeamMeetingApp getTeamMeetingApp() {

        return mTeamMeetingApp;
    }




    /*chat message deal with*/
    public static TMMsgSender getmMsgSender(){
        return  mMsgSender;
    }

    public  void setmMsgSender(TMMsgSender msgSender){
        mMsgSender = msgSender;
    }



    public static void setSelfData(SelfData selfData)
    {
        mSelfData = selfData;
    }

    public static SelfData getmSelfData()
    {
        return mSelfData;
    }

    /**
     * get the Token from manifest.xml
     *
     * @return the value of Token
     */
    public String getToken()
    {
        Bundle bundle = null;
        String tokenKey = "";
        try
        {
            ApplicationInfo info = getPackageManager().getApplicationInfo(getPackageName(),
                    PackageManager.GET_META_DATA);
            bundle = info.metaData;
            if (bundle != null)
            {
                tokenKey = bundle.getString("TOKEN");
            }
        } catch (PackageManager.NameNotFoundException localNameNotFoundException1)
        {

        }
        return tokenKey;
    }

    public void Destroy()
    {
        unregisterReceiver(mNetReceiver);
    }

    /**
     * Judge if device has SD card
     */
    public boolean hasSdcard()
    {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED))
        {
            return true;
        } else
        {
            return false;
        }
    }

    public void showToast(String msg)
    {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void showToast(int string)
    {
        Toast.makeText(this, getString(string), Toast.LENGTH_SHORT).show();
    }

    public String getVersionName()
    {
        PackageInfo pkgInfo = null;
        try
        {
            pkgInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (NameNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return pkgInfo.versionName;
    }

    public int getVersionCode()
    {
        PackageInfo pkgInfo = null;
        try
        {
            pkgInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (NameNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return pkgInfo.versionCode;
    }

    /**
     * get the device id unique
     *
     * @return the device id
     */
    public String getDevId()
    {
        return Secure.getString(getContentResolver(), Secure.ANDROID_ID);
    }

}
