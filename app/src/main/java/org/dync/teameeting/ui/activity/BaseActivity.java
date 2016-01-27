package org.dync.teameeting.ui.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.ypy.eventbus.EventBus;

import org.dync.teameeting.R;
import org.dync.teameeting.TeamMeetingApp;
import org.dync.teameeting.bean.ReqSndMsgEntity;
import org.dync.teameeting.chatmessage.ChatMessageClient;
import org.dync.teameeting.chatmessage.IChatMessageInteface;
import org.dync.teameeting.http.NetWork;
import org.dync.teameeting.structs.NetType;

import cn.jpush.android.api.JPushInterface;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class BaseActivity extends Activity implements IChatMessageInteface {
    public NetWork mNetWork;
    public String mSign;
    public boolean mDebug = TeamMeetingApp.mIsDebug;
    private String TAG = "BaseActivity";
    private ChatMessageClient mChatMessageClinet;
    int i = -1;
    public SweetAlertDialog pDialog;
    public boolean netTyp = false;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNetWork = new NetWork();
        EventBus.getDefault().register(this);
        if (!TeamMeetingApp.isPad) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        registerObserverClinet();

        createDialog();
    }

    private void createDialog() {
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);

    }

    private void registerObserverClinet() {
        mChatMessageClinet = TeamMeetingApp.getmChatMessageClient();
        mChatMessageClinet.registerObserver(new ChatMessageClient.ChatMessageObserver() {
            @Override
            public void OnReqSndMsg(final ReqSndMsgEntity reqSndMsg) {
                if (Looper.myLooper() != Looper.getMainLooper()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onRequesageMsg(reqSndMsg);
                        }
                    });
                } else {
                    onRequesageMsg(reqSndMsg);
                }
            }

        });
    }

    public void initNetWork() {
        String userid = TeamMeetingApp.getTeamMeetingApp().getDevId();
        mNetWork.init(userid, "2", "2", "2", "TeamMeeting");
    }

    public String getSign() {
        return TeamMeetingApp.getmSelfData().getAuthorization();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // init();
        if (mDebug) {
            Log.i(TAG, "onResume: ");
        }
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mDebug) {
            Log.i(TAG, "onPause: ");
        }
        JPushInterface.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mDebug) {
            Log.i(TAG, "onStop: ");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDebug) {
            Log.i(TAG, "onDestroy: ");
        }
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(Message msg) {

    }

    @Override
    public void onRequesageMsg(ReqSndMsgEntity requestMsg) {

    }

    void progressDiloag() {
        pDialog.setCancelable(false);
        pDialog.setTitleText("链接中...");
        pDialog.setContentText("网络异常");
        pDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
        pDialog.show();

        countDownTimer = new CountDownTimer(800 * 7, 800) {
            public void onTick(long millisUntilFinished) {

                // you can change the progress bar color by ProgressHelper every 800 millis
                if (netTyp) {
                    Log.e(TAG, "onTick: " + netTyp);
                    pDialog.cancel();
                    pDialog.dismiss();
                    this.cancel();
                }
                i++;
                switch (i) {
                    case 0:
                        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.blue_btn_bg_color));
                        break;
                    case 1:
                        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.material_deep_teal_50));
                        break;
                    case 2:
                        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.success_stroke_color));
                        break;
                    case 3:
                        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.material_deep_teal_20));
                        break;
                    case 4:
                        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.material_blue_grey_80));
                        break;
                    case 5:
                        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.warning_stroke_color));
                        break;
                    case 6:
                        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.success_stroke_color));
                        break;
                }
            }

            public void onFinish() {

                Log.e(TAG, "onFinish: netType " + netTyp);
                i = -1;

                if (netTyp == false) {
                    pDialog.setTitleText("链接失败!")
                            .setContentText("请检查您的网络")
                            .setConfirmText("重试")
                            .setCancelText("退出")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    Log.e(TAG, "onClick: " + "单击对话框");
                                    sweetAlertDialog.cancel();
                                    sweetAlertDialog.dismiss();
                                    progressDiloag();
                                }
                            })
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    finish();
                                }
                            }).changeAlertType(SweetAlertDialog.WARNING_TYPE);

                }

            }
        }.start();
    }


    /**
     * netWork can user
     *
     * @param type
     */

    public void netWorkTypeStart(int type) {
        switch (NetType.values()[type]) {
            case TYPE_WIFI:
                if (mDebug)
                    Log.e(TAG, "TYPE_WIFI ");
                netTyp = true;
                // netCatchGreatRoom();
                break;
            case TYPE_4G:
                if (mDebug)
                    Log.e(TAG, "TYPE_4G ");
                netTyp = true;
                break;
            case TYPE_3G:
                if (mDebug)
                    Log.e(TAG, "TYPE_3G ");
                netTyp = true;
                break;
            case TYPE_2G:
                if (mDebug)
                    Log.e(TAG, "TYPE_2G ");
                netTyp = true;
                break;

            case TYPE_NULL:
                if (mDebug)
                    Log.e(TAG, "TYPE_NULL ");
                netTyp = false;
                progressDiloag();
                break;
            case TYPE_UNKNOWN:
                netTyp = false;
                if (mDebug)
                    Log.e(TAG, "TYPE_UNKNOWN: ");

            default:
                break;
        }

    }


}
