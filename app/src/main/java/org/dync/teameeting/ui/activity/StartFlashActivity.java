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
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import org.dync.teameeting.R;
import org.dync.teameeting.TeamMeetingApp;
import org.dync.teameeting.bean.MeetingListEntity;
import org.dync.teameeting.http.HttpContent;
import org.dync.teameeting.receiver.MyReceiver;
import org.dync.teameeting.sdkmsgclient.msgs.TMMsgSender;
import org.dync.teameeting.structs.EventType;
import org.dync.teameeting.structs.JoinActType;
import org.dync.teameeting.structs.NetType;
import org.dync.teameeting.ui.helper.DialogHelper;
import org.dync.teameeting.utils.LocalUserInfo;
import org.dync.teameeting.utils.StringHelper;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener;
import de.greenrobot.event.EventBus;

/**
 * @author zhangqilu org.dync.teammeeting.activity StartFlashActivity create at
 *         2015-12-11 17:00:42
 */
public class StartFlashActivity extends BaseActivity {
    private static final String TAG = "StartFlashActivity";
    private static final String JPUSH = "JPush";
    private static final int MSG_SET_ALIAS = 1001;
    private static final int MSG_SET_TAGS = 1002;
    public SweetAlertDialog mNetErrorSweetAlertDialog;
    private ImageView mView;
    private Context context;
    private TMMsgSender mMsgSender;
    private boolean isNotifactionChack = false;

    private final String mServer = HttpContent.SERVICE_URL;
    private final int mPort = HttpContent.MSG_SERVICE_POINT;
    private String mUserid;
    private String mSign;
    private ProgressBar mLoadingProgress;
    private String mUrlMeetingId = null;
    private Integer mTags = 0;
    private String mMeetingName;
    private String mAnyrtcid;

    private String mNname = "nick name";
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case MSG_SET_ALIAS: {
                    Log.d(JPUSH, "Set alias in handler.");
                    JPushInterface.setAliasAndTags(getApplicationContext(),
                            (String) msg.obj, null, mAliasCallback);
                }

                break;

                case MSG_SET_TAGS: {
                    String tag = TeamMeetingApp.getTeamMeetingApp().getDevId();
                    Set<String> tagSet = new HashSet<String>();
                    tagSet.add(tag);
                    JPushInterface.setAliasAndTags(getApplicationContext(),
                            tag, tagSet, mTagsCallback);
                }

                break;

                default:
                    Log.i(JPUSH, "Unhandled msg - " + msg.what);
            }
        }
    };

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

    OnSweetClickListener sweetClickListener = new OnSweetClickListener() {
        @Override
        public void onClick(SweetAlertDialog sweetAlertDialog) {
            sweetAlertDialog.dismiss();
            initNetWork();
        }
    };

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG, "onRestart: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDebug)
            Log.e(TAG, "onResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mDebug)
            Log.e(TAG, "onPause: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mDebug)
            Log.e(TAG, "onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDebug)
            Log.e(TAG, "onDestroy: ");
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
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(
                                MSG_SET_ALIAS, alias), 1000 * 60);
                    } else {
                        Log.i(JPUSH, "No network");
                    }

                    break;

                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e(JPUSH, logs);
            }
            // showToast(logs, getApplicationContext());
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
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_TAGS, tags), 1000 * 60);
                    } else {
                        Log.i(JPUSH, "No network");
                    }
                    break;

                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e(JPUSH, logs);
            }
            //showToast(logs, getApplicationContext());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_flash);

        if (TeamMeetingApp.isInitFalg) {
            //App run
            onStartAgain(getIntent());
            finish();
            return;
        }
        Log.e(TAG, "------------------------------onCreate: ");
        inintView();
        initData();
        setTag();
    }

    /**
     * againg App
     * @param intent
     */
    private void onStartAgain(Intent intent) {
        String action = intent.getAction();
        Uri uri = intent.getData();
        if (mDebug) {
            Log.e(TAG, "onStartAgain: action --" + action + "uri---" + uri);
        }

        if (Intent.ACTION_VIEW.equals(action) && uri != null) {
            String urlMeetingid = StringHelper.uriToMeetingId(uri.toString()).trim();
            Log.e(TAG, "onStartAgain: " + uri.toString() + "--uriMeetingid" + urlMeetingid);

            if (urlMeetingid != null) {
                List<String> meetingActivityList = TeamMeetingApp.getMeetingActivityList();
                if (meetingActivityList.size() > 0) {
                    String runMeetingId = meetingActivityList.get(0);
                    Log.e(TAG, urlMeetingid + "urlMeetingid---onStartAgain: --runMeetingId" + runMeetingId);
                    if (runMeetingId.equals(urlMeetingid)) {
                        startActivity(MeetingActivity.class);
                        return;
                    } else {
                        Message msg = new Message();
                        msg.what= EventType.MSG_URL_MEETING_EXIT.ordinal();
                        EventBus.getDefault().post(msg);
                    }
                }
            }


            intent.setClass(this, MainActivity.class);
            intent.putExtra("urlMeetingId", urlMeetingid);
            startActivity(intent);

        } else {
            Log.e(TAG, "onStartAgain: 启动异常");
        }

    }

    private void startActivity(Class<?> activityCls) {
        Intent intent = new Intent(StartFlashActivity.this, activityCls);
        startActivity(intent);
    }




    private void setTag() {
        mHandler.sendEmptyMessage(MSG_SET_TAGS);
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
        Animation loadAnimation = AnimationUtils.loadAnimation(this, R.anim.splash);
        loadAnimation.setAnimationListener(mAnimationListener);
        mView.setAnimation(loadAnimation);

        Intent intent = getIntent();
        String action = intent.getAction();
        Log.e(TAG, "initData: " + action);
        if (Intent.ACTION_VIEW.equals(action)) {
            Uri uri = intent.getData();
            if (uri != null) {
                String content = uri.toString();
                mUrlMeetingId = StringHelper.uriToMeetingId(content);
                isNotifactionChack = false;
                if (mDebug) {
                    Log.e(TAG, "initData: " + uri.toString() + " content " + content + "--mUrlMeetingId" + mUrlMeetingId);
                }
            }
        } else if (MyReceiver.ACTIVITY_ACTION_NOTIFACTION.equals(action)) {
            Bundle bundle = intent.getExtras();
            String notifaction = bundle.getString(JPushInterface.EXTRA_EXTRA);
            isNotifactionChack = true;
            JSONObject json = null;
            try {
                json = new JSONObject(notifaction);
                mUrlMeetingId = json.getString("roomid");
                mTags = json.getInt("tags");
                if (mDebug) {
                    Log.e(TAG, "meetingId" + mUrlMeetingId);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    // 当前所连接的网络可用
                    Log.e(TAG, "isNetworkAvailable: true");
                    return true;
                }
            }
        }
        Log.e(TAG, "isNetworkAvailable: false");
        return false;
    }


    /**
     * @param msg
     */
    private void setBundle(Message msg) {
        JSONObject json = new JSONObject();
        try {
            json.put("roomid", mUrlMeetingId);
            json.put("tags", 0);
            Bundle bundle = new Bundle();
            bundle.putString(JPushInterface.EXTRA_EXTRA, json.toString());
            Log.e(TAG, "setBundle: json.tostring" + json.toString());
            msg.setData(bundle);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * chatMessageInint
     */
    private void chatMessageInint() {
        mNname = TeamMeetingApp.getmSelfData().getInformation().getUname();

        if (mDebug) {
            Log.e(TAG, "chatMessageInint: " + mMsgSender);
        }

        if (mMsgSender == null) {
            mMsgSender = new TMMsgSender(TeamMeetingApp.getTeamMeetingApp().getContext(), TeamMeetingApp.getmChatMessageClient());
        }

        TeamMeetingApp.getTeamMeetingApp().setmMsgSender(mMsgSender);

        if (mNname == null) {
            mNname = "null name";
        }

        if (mDebug) {
            Log.e(TAG, "chatMessageInint: mNname " + mNname);
        }

        int msg = mMsgSender.TMInit(mUserid, mSign, mNname, mServer, mPort);

        if (msg >= 0) {
            if (mDebug) {
                Log.e(TAG, "Chat Message Inint successed");
            }
        } else if (mDebug) {
            Log.e(TAG, "Chat Message Inint failed");
        }
    }

    /**
     * interfacejump
     */
    private void interfacejump(Message msg) {
        mLoadingProgress.setVisibility(View.GONE);

        boolean firstLogin = LocalUserInfo.getInstance(StartFlashActivity.this)
                .getUserInfoBoolean(LocalUserInfo.FIRST_LOGIN);
        Intent intent;

        if (firstLogin) {
            LocalUserInfo.getInstance(context).setUserInfoInt(LocalUserInfo.NOTIFIACTION_TAGS, mTags);
            intent = new Intent(StartFlashActivity.this, GuideActivity.class);
            LocalUserInfo.getInstance(StartFlashActivity.this).setUserInfoBoolean(LocalUserInfo.FIRST_LOGIN, false);
        } else {
            intent = new Intent(StartFlashActivity.this, MainActivity.class);
            intent.putExtra("isNotifactionChack", isNotifactionChack);
            intent.putExtra("urlMeetingId", mUrlMeetingId);
            intent.putExtra("tags", mTags);
        }

        startActivity(intent);
        finish();
    }

    public void netWorkTypeStart(int type) {
        if (type == NetType.TYPE_NULL.ordinal()) {
            mNetErrorSweetAlertDialog.show();
        } else {
            // initNetWork();
        }
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = conn.getActiveNetworkInfo();

        return ((info != null) && info.isConnected());
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
     * get meetng information success
     *
     * @param msg
     */
    private void getMeetingInfoSuccess(Message msg) {
        MeetingListEntity meetingListEntity = TeamMeetingApp.getmSelfData().getMeetingListEntity();
        Logger.e(meetingListEntity.toString());
        int usable = meetingListEntity.getMeetenable();
        mAnyrtcid = meetingListEntity.getAnyrtcid();
        mMeetingName = meetingListEntity.getMeetname();
        switch (usable) {
            case 0://no
                Toast.makeText(StartFlashActivity.this, R.string.str_meeting_deleted, Toast.LENGTH_SHORT).show();
                break;
            case 1://yes
                if (msg.getData().getString(JoinActType.JOIN_TYPE) == JoinActType.JOIN_URL_ACTVITY) {
                    mNetWork.insertUserMeetingRoom(getSign(), mUrlMeetingId, JoinActType.JOIN_INSERT_LINK_JOIN_ACTIVITY);
                }
                break;
            case 2://private
                Toast.makeText(StartFlashActivity.this, R.string.str_meeting_privated, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void joinMeeting(Message msg) {

        msg.what = EventType.MSG_URL_START_MEETING.ordinal();
        EventBus.getDefault().post(msg);
    }


    /**
     * For EventBus callback.
     */
    public void onEventMainThread(Message msg) {
        String join_insert_type;
        String meetingId;
        switch (EventType.values()[msg.what]) {
            case MSG_ININT_SUCCESS:
                if (mDebug) {
                    Log.i(TAG, "MSG_ININT_SUCCESS");
                }
                mSign = TeamMeetingApp.getmSelfData().getAuthorization();
                mNetWork.getRoomLists(mSign, 1 + "", 20 + "");
                chatMessageInint();
                break;

            case MSG_ININT_FAILED:
                if (mDebug) {
                    Log.e(TAG, "MSG_ININT_FAILED");
                }
                break;

            case MSG_SIGNOUT_SUCCESS:
                if (mDebug) {
                    Log.e(TAG, "MSG_SIGNOUT_SUCCESS");
                }
                finish();
                System.exit(0);
                break;

            case MSG_SIGNOUT_FAILED:

                if (mDebug) {
                    Log.e(TAG, "MSG_SIGNOUT_FAILED");
                }

                break;

            case MSG_GET_ROOM_LIST_SUCCESS:

                if (mDebug) {
                    Log.e(TAG, "MSG_GET_ROOM_LIST_SUCCESS");
                }

                interfacejump(msg);

                break;

            case MSG_GET_ROOM_LIST_FAILED:

                if (mDebug) {
                    Log.e(TAG, "MSG_GET_ROOM_LIST_FAILED");
                }

                break;

            case MSG_NET_WORK_TYPE:

                if (mDebug) {
                    Log.e(TAG, "MSG_NET_WORK_TYPE");
                }

                int type = msg.getData().getInt("net_type");
                netWorkTypeStart(type);

                break;

            case MSG_RESPONS_ESTR_NULl:

                if (mDebug) {
                    Log.e(TAG, "MSG_RESPONS_ESTR_NULl");
                }

                mNetErrorSweetAlertDialog.show();

                break;

            case MSG_GET_MEETING_INFO_SUCCESS:
                if (mDebug) {
                    Log.e(TAG, "MSG_GET_MEETING_INFO_SUCCESS ");
                }
                if (msg.getData().getString(JoinActType.JOIN_TYPE).equals(JoinActType.JOIN_URL_ACTVITY)) {
                    getMeetingInfoSuccess(msg);
                }

                break;

            default:
                break;
        }
    }


}
