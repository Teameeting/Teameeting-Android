package org.dync.teameeting.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import org.dync.teameeting.R;
import org.dync.teameeting.TeamMeetingApp;
import org.dync.teameeting.sdkmsgclientandroid.msgs.TMMsgSender;
import org.dync.teameeting.structs.EventType;
import org.dync.teameeting.structs.NetType;
import org.dync.teameeting.ui.helper.DialogHelper;
import org.dync.teameeting.utils.LocalUserInfo;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.jpush.android.api.CustomPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener;

/**
 * @author zhangqilu org.dync.teammeeting.activity StartFlashActivity create at
 *         2015-12-11 17:00:42
 */

public class StartFlashActivity extends BaseActivity {

    private static final String TAG = "StartFlashActivity";
    public SweetAlertDialog mNetErrorSweetAlertDialog;

    private ImageView mView;
    private Context context;
    private TMMsgSender mMsgSender;
    //private final String mServer = "180.150.179.128";
    private final String mServer = "192.168.7.39";

    private final int mPort = 6630;
    private String mUserid;
    private String mSign;
    private ProgressBar mLoadingProgress;
    private String mUrlMeetingId = null;
    private String mNname = "nick name";

    private static final String JPUSH = "JPush";

    Button mSetTag;
    Button mSetAlias;

    private static final int MSG_SET_ALIAS = 1001;
    private static final int MSG_SET_TAGS = 1002;


    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    Log.d(JPUSH, "Set alias in handler.");
                    JPushInterface.setAliasAndTags(getApplicationContext(), (String) msg.obj, null,
                            mAliasCallback);
                    break;
                case MSG_SET_TAGS:
                    Log.d(JPUSH, "Set tags in handler.");
                    JPushInterface.setAliasAndTags(getApplicationContext(), null,
                            (Set<String>) msg.obj, mTagsCallback);
                    break;
                default:
                    Log.i(JPUSH, "Unhandled msg - " + msg.what);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_flash);

        inintView();
        initData();
        setTag();
        //setPushNotificationBuilderIcon();
    }


    private void setTag() {

        String tag = TeamMeetingApp.getTeamMeetingApp().getDevId();

        // 检查 tag 的有效性
        if (TextUtils.isEmpty(tag)) {
            Toast.makeText(StartFlashActivity.this, "tag不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        Logger.e(tag);
        // ","隔开的多个 转换成 Set
        String[] sArray = tag.split(",");
        Set<String> tagSet = new LinkedHashSet<String>();
        for (String sTagItme : sArray) {
            if (!isValidTagAndAlias(sTagItme)) {
                Toast.makeText(StartFlashActivity.this, "格式不对", Toast.LENGTH_SHORT).show();
                return;
            }
            tagSet.add(sTagItme);
        }

        // 调用JPush API设置Tag
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_TAGS, tagSet));

    }

    /**
     * set Push Style
     */
    public void setPushNotificationBuilderIcon() {
        CustomPushNotificationBuilder builder = new CustomPushNotificationBuilder(
                this, R.layout.customer_notitfication_layout, R.id.icon,
                R.id.title, R.id.text);

        builder.statusBarDrawable = R.drawable.ic_richpush_actionbar_back;
        builder.layoutIconDrawable = R.drawable.ic_richpush_actionbar_back;
        JPushInterface.setPushNotificationBuilder(2, builder);

    }

    /**
     * inint View
     */
    private void inintView() {
        mView = (ImageView) findViewById(R.id.splash_image);
        mLoadingProgress = (ProgressBar) findViewById(R.id.pb_loading);
    }

    /**
     * inintData
     */
    private void initData() {
        context = this;
        mNetErrorSweetAlertDialog = DialogHelper.createNetErroDilaog(this, sweetClickListener);

        mUserid = TeamMeetingApp.getTeamMeetingApp().getDevId();

        Animation loadAnimation = AnimationUtils.loadAnimation(this,
                R.anim.splash);
        loadAnimation.setAnimationListener(mAnimationListener);
        mView.setAnimation(loadAnimation);


      /*  URL   join   meeting   */
        Intent intent = getIntent();
        String action = intent.getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
            Uri uri = intent.getData();
            if (uri != null) {
                String content = uri.toString();

                mUrlMeetingId = content.substring(13);
                if (mDebug)
                    Log.e(TAG, "initData: " + uri.toString() + " content " + content);
            }
        }


    }

    /**
     * chatMessageInint
     */
    private void chatMessageInint() {

        mMsgSender = new TMMsgSender(this, TeamMeetingApp.getmChatMessageClient());
        TeamMeetingApp.getTeamMeetingApp().setmMsgSender(mMsgSender);
        int msg = mMsgSender.TMInit(mUserid, mSign, mNname, mServer, mPort);
        if (msg >= 0) {
            if (mDebug)
                Log.e(TAG, "Chat Message Inint successed");
        } else if (mDebug) {
            Log.e(TAG, "Chat Message Inint failed");
        }
    }

    private AnimationListener mAnimationListener = new AnimationListener() {
        @Override
        public void onAnimationStart(Animation arg0) {

        }

        @Override
        public void onAnimationRepeat(Animation arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onAnimationEnd(Animation arg0) {
            mNetWork.init(mUserid, "2", "2", "2", "TeamMeeting");
        }
    };


    /**
     * interfacejump
     */
    private void interfacejump(Message msg) {

        mLoadingProgress.setVisibility(View.GONE);
        boolean firstLogin = LocalUserInfo.getInstance(StartFlashActivity.this)
                .getUserInfoBoolean(LocalUserInfo.FIRST_LOGIN);
        Intent intent;
        if (firstLogin) {
            intent = new Intent(StartFlashActivity.this, GuideActivity.class);
            LocalUserInfo.getInstance(StartFlashActivity.this).setUserInfoBoolean("firstLogin", false);
        } else {
            intent = new Intent(StartFlashActivity.this, MainActivity.class);
            intent.putExtra("urlMeetingId", mUrlMeetingId);
        }
        startActivity(intent);
        finish();
    }


    OnSweetClickListener sweetClickListener = new OnSweetClickListener() {
        @Override
        public void onClick(SweetAlertDialog sweetAlertDialog) {
            sweetAlertDialog.dismiss();
            initNetWork();
        }
    };

    public void netWorkTypeStart(int type) {

        if (type == NetType.TYPE_NULL.ordinal()) {
            mNetErrorSweetAlertDialog.show();
        } else {
            // initNetWork();
        }
    }



    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    Log.i(JPUSH, logs);
                    break;

                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.i(JPUSH, logs);
                    if (isConnected(getApplicationContext())) {
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias),
                                1000 * 60);
                    } else {
                        Log.i(JPUSH, "No network");
                    }
                    break;

                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e(JPUSH, logs);
            }

            showToast(logs, getApplicationContext());
        }

    };

    private final TagAliasCallback mTagsCallback = new TagAliasCallback() {

        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    Log.i(JPUSH, logs);
                    break;

                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.i(JPUSH, logs);
                    if (isConnected(getApplicationContext())) {
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_TAGS, tags),
                                1000 * 60);
                    } else {
                        Log.i(JPUSH, "No network");
                    }
                    break;

                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e(JPUSH, logs);
            }

            showToast(logs, getApplicationContext());
        }

    };

    // ******************************Utils*******************************
    // 校验Tag Alias 只能是数字,英文字母和中文
    public static boolean isValidTagAndAlias(String s) {
        Pattern p = Pattern.compile("^[\u4E00-\u9FA50-9a-zA-Z_-]{0,}$");
        Matcher m = p.matcher(s);
        return m.matches();
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager conn = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = conn.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }

    public static void showToast(final String toast, final Context context) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }).start();
    }



    /**
     * For EventBus callback.
     */
    public void onEventMainThread(Message msg) {
        switch (EventType.values()[msg.what]) {
            case MSG_ININT_SUCCESS:
                if (mDebug)
                    Log.e(TAG, "MSG_ININT_SUCCESS");
                mSign = TeamMeetingApp.getmSelfData().getAuthorization();
                mNetWork.getRoomLists(mSign, 1 + "", 20 + "");
                chatMessageInint();

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
                if (mDebug) {
                    Log.e(TAG, "MSG_MESSAGE_LOGIN_SUCCESS");
                }


                break;
            case MSG_MESSAGE_LOGIN_FAILED:
                if (mDebug) {
                    Log.e(TAG, "MSG_MESSAGE_LOGIN_FAILED");
                }

                break;
            case MSG_MESSAGE_SERVER_CONNECTED:
                if (mDebug) {
                    Log.e(TAG, "MSG_MESSAGE_SERVER_CONNECTED");
                }


                break;

            default:
                break;
        }
    }

}
