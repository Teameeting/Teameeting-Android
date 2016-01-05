package org.dync.teameeting.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import org.dync.teameeting.R;
import org.dync.teameeting.http.NetWork;
import org.dync.teameeting.TeamMeetingApp;
import org.dync.teameeting.msgs.TMMsgSender;
import org.dync.teameeting.ui.helper.DialogHelper;
import org.dync.teameeting.structs.EventType;
import org.dync.teameeting.structs.NetType;
import org.dync.teameeting.utils.LocalUserInfo;

import cn.jpush.android.api.CustomPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;
import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener;

/**
 * @author zhangqilu org.dync.teammeeting.activity StartFlashActivity create at
 *         2015-12-11 17:00:42
 */

public class StartFlashActivity extends BaseActivity
{

    private static final String TAG = "StartFlashActivity";

    public SweetAlertDialog mNetErrorSweetAlertDialog;

    private ImageView mView;
    private Context context;
    public  static TMMsgSender mMsgSender;
    private final String mServer = "192.168.7.39";
    private final int mPort = 9210;
    private final String mPass = "123456";
    private String mUserid ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_flash);
        mView = (ImageView) findViewById(R.id.splash_image);
        context = this;
        initData();

        // 设置推送的样式
        //setPushNotificationBuilderIcon();
    }


    public void setPushNotificationBuilderIcon()
    {
        CustomPushNotificationBuilder builder = new CustomPushNotificationBuilder(
                this, R.layout.customer_notitfication_layout, R.id.icon,
                R.id.title, R.id.text);
        // 指定定制的 Notification Layout
        builder.statusBarDrawable = R.drawable.ic_richpush_actionbar_back;
        // 指定层状态栏小图标
        builder.layoutIconDrawable = R.drawable.ic_richpush_actionbar_back;
        // 指定下拉状态栏时显示的通知图标
        JPushInterface.setPushNotificationBuilder(2, builder);

    }

    /**
     * inintData
     */
    private void initData()
    {
        mNetErrorSweetAlertDialog = DialogHelper.createNetErroDilaog(this, sweetClickListener);

        mUserid = TeamMeetingApp.getTeamMeetingApp().getDevId();
        /*message inint */
        mMsgSender = new TMMsgSender(this);
        int msg = mMsgSender.TMInit(mServer, mPort);
        if(msg ==0){
            if(mDebug)
                Log.e(TAG, "Message Inint successed");

        }
        else if(mDebug){
            Log.e(TAG, "Message Inint failed");
        }



        Animation loadAnimation = AnimationUtils.loadAnimation(this,
                R.anim.splash);
        loadAnimation.setAnimationListener(mAnimationListener);
        mView.setAnimation(loadAnimation);

    }

    private AnimationListener mAnimationListener = new AnimationListener()
    {
        @Override
        public void onAnimationStart(Animation arg0)
        {

        }

        @Override
        public void onAnimationRepeat(Animation arg0)
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void onAnimationEnd(Animation arg0)
        {


            mNetWork.init(mUserid, "2", "2", "2", "TeamMeeting");
        }
    };


    /**
     * interfacejump
     */
    private void interfacejump(Message msg )
    {

        Bundle bundle = msg.getData();
        String meetingListStr = bundle.getString(NetWork.MEETING_LIST);

        boolean firstLogin = LocalUserInfo.getInstance(StartFlashActivity.this)
                .getUserInfoBoolean(LocalUserInfo.FIRST_LOGIN);
         Intent intent ;
        if (true)
        {
            intent = new Intent(StartFlashActivity.this, GuideActivity.class);
            intent.putExtra(NetWork.MEETING_LIST,meetingListStr);
            LocalUserInfo.getInstance(StartFlashActivity.this).setUserInfoBoolean("firstLogin", true);

        } else
        {

            intent = new Intent(StartFlashActivity.this, MainActivity.class);
            intent.putExtra(NetWork.MEETING_LIST,meetingListStr);
        }

        startActivity(intent);
        finish();
    }


    OnSweetClickListener sweetClickListener = new OnSweetClickListener()
    {
        @Override
        public void onClick(SweetAlertDialog sweetAlertDialog)
        {
            sweetAlertDialog.dismiss();
            initNetWork();
        }
    };

    /**
     * For EventBus callback.
     */
    public void onEventMainThread(Message msg)
    {
        switch (EventType.values()[msg.what])
        {
            case MSG_ININT_SUCCESS:
                if (mDebug)
                    Log.e(TAG, "MSG_ININT_SUCCESS");
                String sign = TeamMeetingApp.getmSelfData().getAuthorization();
                mNetWork.getRoomLists(sign, 1 + "", 20 + "");
                break;
            case MSG_ININT_FAILED:
                if (mDebug)
                    Log.e(TAG, "MSG_ININT_FAILED");
                break;
            case MSG_SIGNOUT_SUCCESS:
                if (mDebug)
                    Log.e(TAG, "MSG_SIGNOUT_SUCCESS");
                finish();
                System.exit(0);
                break;
            case MSG_SIGNOUT_FAILED:
                if (mDebug)
                    Log.e(TAG, "MSG_SIGNOUT_FAILED");
                break;
            case MSG_GET_ROOM_LIST_SUCCESS:
                if (mDebug)
                    Log.e(TAG, "MSG_GET_ROOM_LIST_SUCCESS");
                interfacejump(msg);
                break;
            case MSG_GET_ROOM_LIST_FAILED:
                if (mDebug)
                    Log.e(TAG, "MSG_GET_ROOM_LIST_FAILED");
                break;
            case MSG_NET_WORK_TYPE:
                if (mDebug)
                    Log.e(TAG, "MSG_NET_WORK_TYPE");
                int type = msg.getData().getInt("net_type");
                netWorkTypeStart(type);
                break;
            case MSG_RESPONS_ESTR_NULl:
                if (mDebug)
                    Log.e(TAG, "MSG_NET_WORK_TYPE");
                mNetErrorSweetAlertDialog.show();
                break;
            case MSG_MESSAGE_LOGIN_SUCCESS:
                if(mDebug){
                    Log.e(TAG,"MSG_MESSAGE_LOGIN_SUCCESS");
                }


                break;
            case MSG_MESSAGE_LOGIN_FAILED:
                if(mDebug){
                    Log.e(TAG,"MSG_MESSAGE_LOGIN_FAILED");
                }

                break;
            case MSG_MESSAGE_SERVER_CONNECTED:
                if(mDebug){
                    Log.e(TAG,"MSG_MESSAGE_SERVER_CONNECTED");
                }
                mMsgSender.TMLogin(mUserid, mPass);

                break;

            default:
                break;
        }
    }

    public void netWorkTypeStart(int type)
    {

        if (type == NetType.TYPE_NULL.ordinal())
        {
            mNetErrorSweetAlertDialog.show();
        } else
        {
           // initNetWork();
        }
    }


}
