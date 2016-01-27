package org.dync.teameeting.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.dync.teameeting.R;
import org.dync.teameeting.TeamMeetingApp;
import org.dync.teameeting.bean.MeetingListEntity;
import org.dync.teameeting.sdkmsgclientandroid.msgs.TMMsgSender;
import org.dync.teameeting.structs.EventType;
import org.dync.teameeting.structs.JoinActType;

import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;

public class JoinMeetingActivity extends BaseActivity implements View.OnClickListener {
    private final static String TAG = "JoinMeetingActivity";
    private boolean mDebug = TeamMeetingApp.mIsDebug;
    private EditText mEtMeetingId;
    private ImageButton mIbtnJoinMeeting, mIbtnback;
    private TMMsgSender mMsgSender;
    private String mMeetingId;
    private String mMeetingName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_meeting);
        inintView();
        inintdata();
    }

    private void inintView() {
        mEtMeetingId = (EditText) findViewById(R.id.et_meeting_id);


        mIbtnJoinMeeting = (ImageButton) findViewById(R.id.ibtn_join_meeting);
        mIbtnback = (ImageButton) findViewById(R.id.ibtn_back);
        mIbtnback.setOnClickListener(this);
        mIbtnJoinMeeting.setOnClickListener(this);
        mEtMeetingId.setOnEditorActionListener(mOnEditorActionListener);

    }


    private void inintdata() {
        mMsgSender = TeamMeetingApp.getmMsgSender();

        mEtMeetingId.setFocusable(true);
        mEtMeetingId.setFocusableInTouchMode(true);
        mEtMeetingId.requestFocus();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                InputMethodManager inputManager =
                        (InputMethodManager) mEtMeetingId.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(mEtMeetingId, 0);
            }

        }, 998);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.ibtn_back:
                finish();
                break;
            case R.id.ibtn_join_meeting:
                meetingDealWith();
                break;

        }


    }

    TextView.OnEditorActionListener mOnEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            meetingDealWith();
            return false;
        }
    };

    /**
     *
     */
    private void meetingDealWith() {
        mMeetingId = mEtMeetingId.getText().toString();
        if (mMeetingId.length() == 12) {
            if (mDebug)
                Log.e(TAG, "meetingDealWith: " + getSign() + " mMeetingId " + mMeetingId);
            int position = TeamMeetingApp.getmSelfData().getMeetingIdPosition(mMeetingId);
            if (position >= 0) {
                Message msg = new Message();
                msg.what = EventType.JOIN_MEETINGID_EXIST.ordinal();
                msg.obj = position;
                EventBus.getDefault().post(msg);
                finish();
            } else {
                mNetWork.getMeetingInfo(mMeetingId, JoinActType.JOIN_START_ACTIVITY);
            }


        } else {
            Toast.makeText(JoinMeetingActivity.this, R.string.str_meeting_id_error, Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * joinMeeting
     */
    private void joinMeeting() {
        String userId = TeamMeetingApp.getTeamMeetingApp().getDevId();
        Intent intent = new Intent(JoinMeetingActivity.this, MeetingActivity.class);
        intent.putExtra("meetingId", mMeetingId);
        intent.putExtra("userId", userId);
        intent.putExtra("meetingName", mMeetingName);
        startActivity(intent);
        finish();
    }


    @Override
    public void onEventMainThread(Message msg) {
        {
            String join_insert_type;
            switch (EventType.values()[msg.what]) {

                case MSG_GET_MEETING_INFO_SUCCESS:
                    if (mDebug)
                        Log.e(TAG, "MSG_GET_MEETING_INFO_SUCCESS");

                    getMeetingInfoSuccess(msg);

                    break;
                case MSG_GET_MEETING_INFO_FAILED:
                    if (mDebug)
                        Log.e(TAG, "MSG_GET_MEETING_INFO_FAILED");
                    join_insert_type = msg.getData().getString(JoinActType.JOIN_TYPE);
                    if (join_insert_type == JoinActType.JOIN_START_ACTIVITY) {
                        Toast.makeText(JoinMeetingActivity.this, msg.getData().getString("message"), Toast.LENGTH_SHORT).show();
                    }

                    break;
                case MSG_INSERT_USER_MEETING_ROOM_SUCCESS:
                    if (mDebug)
                        Log.e(TAG, "MSG_INSERT_USER_MEETING_ROOM_SUCCESS");
                    join_insert_type = msg.getData().getString(JoinActType.JOIN_INSERT_TYPE);
                    if (join_insert_type == JoinActType.JOIN_INSERT_START_ACTIVITY) {
                        joinMeeting();
                    }

                    break;
                case MSG_INSERT_USER_MEETING_ROOM_FAILED:
                    if (mDebug)
                        Log.e(TAG, "MSG_INSERT_USER_MEETING_ROOM_FAILED");
                    join_insert_type = msg.getData().getString(JoinActType.JOIN_INSERT_TYPE);
                    if (join_insert_type == JoinActType.JOIN_INSERT_START_ACTIVITY) {
                        Toast.makeText(JoinMeetingActivity.this, msg.getData().getString("message"), Toast.LENGTH_SHORT).show();
                    }
                    break;

                case MSG_NET_WORK_TYPE:
                    if (mDebug)
                        Log.e(TAG, "MSG_NET_WORK_TYPE");
                    int type = msg.getData().getInt("net_type");

                    break;
                case MSG_RESPONS_ESTR_NULl:
                    if (mDebug)
                        Log.e(TAG, "MSG_RESPONS_ESTR_NULl");

                    break;
                default:
                    break;
            }
        }


    }

    private void getMeetingInfoSuccess(Message msg) {
        MeetingListEntity meetingListEntity = TeamMeetingApp.getmSelfData().getMeetingListEntity();
        int usable = meetingListEntity.getMeetusable();
        mMeetingName = meetingListEntity.getMeetname();
        switch (usable) {
            case 0://no
                Toast.makeText(JoinMeetingActivity.this, R.string.str_meeting_deleted, Toast.LENGTH_SHORT).show();
                break;
            case 1://yes
                if (msg.getData().getString(JoinActType.JOIN_TYPE) == JoinActType.JOIN_START_ACTIVITY) {
                    mNetWork.insertUserMeetingRoom(getSign(), mMeetingId, JoinActType.JOIN_INSERT_START_ACTIVITY);
                }
                break;
            case 2://private
                Toast.makeText(JoinMeetingActivity.this, R.string.str_meeting_privated, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
