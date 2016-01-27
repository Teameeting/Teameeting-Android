package org.dync.teameeting.ui.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.dync.teameeting.R;
import org.dync.teameeting.TeamMeetingApp;
import org.dync.teameeting.bean.ReqSndMsgEntity;
import org.dync.teameeting.chatmessage.ChatMessageClient;
import org.dync.teameeting.chatmessage.IChatMessageInteface;
import org.dync.teameeting.http.NetWork;

import cn.jpush.android.api.JPushInterface;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.greenrobot.event.EventBus;

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
        if (pDialog.isShowing()) {
            return;
        }
        pDialog.setCancelable(false);
        pDialog.setTitleText(getString(R.string.dialog_loading));
        pDialog.setContentText(getString(R.string.dialog_net_exception));
        pDialog.showCancelButton(false);
        pDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
        pDialog.show();

        countDownTimer = new CountDownTimer(800 * 7, 800) {
            public void onTick(long millisUntilFinished) {

                if (netTyp) {
                    pDialog.cancel();
                    pDialog.dismiss();
                    this.cancel();
                }
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
                i = -1;
                if (netTyp == false) {
                    pDialog.cancel();
                    pDialog.dismiss();

                    final SweetAlertDialog sb = new SweetAlertDialog(BaseActivity.this, SweetAlertDialog.WARNING_TYPE);
                    sb.setCancelable(false);
                    sb.setTitleText(getString(R.string.dialog_net_conn_failure))
                            .setContentText(getString(R.string.dialog_please_conn_network))
                            .setCancelText(getString(R.string.dialog_exit))
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.cancel();
                                    sweetAlertDialog.dismiss();
                                    finish();
                                }
                            })
                            .setConfirmText(getString(R.string.dialog_try))
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.cancel();
                                    sweetAlertDialog.dismiss();
                                    progressDiloag();
                                }
                            })
                            .showCancelButton(true)
                            .show();

                }

            }
        }.start();
    }


}
