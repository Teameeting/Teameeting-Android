package org.dync.teameeting.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.dync.teameeting.R;
import org.dync.teameeting.TeamMeetingApp;
import org.dync.teameeting.sdkmsgclientandroid.jni.JMClientType;
import org.dync.teameeting.sdkmsgclientandroid.msgs.TMMsgSender;
import org.dync.teameeting.structs.EventType;

public class JoinMeetingActivity extends BaseActivity implements View.OnClickListener{
    private final static String TAG = "JoinMeetingActivity";
    private boolean mDebug = TeamMeetingApp.mIsDebug;
    private EditText mEtMeetingId;
    private ImageButton mIbtnJoinMeeting,mIbtnback;
    private TMMsgSender mMsgSender;
    private String mMeetingId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_meeting);
        inintView();
        inintdata();
    }

    private  void inintView(){
        mEtMeetingId = (EditText) findViewById(R.id.et_meeting_id);
        mIbtnJoinMeeting = (ImageButton) findViewById(R.id.ibtn_join_meeting);
        mIbtnback = (ImageButton) findViewById(R.id.ibtn_back);
        mIbtnback.setOnClickListener(this);
        mIbtnJoinMeeting.setOnClickListener(this);
        mEtMeetingId.setOnEditorActionListener(mOnEditorActionListener);

    }


    private void inintdata(){
        mMsgSender=TeamMeetingApp.getmMsgSender();

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){

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
    private void meetingDealWith(){
        mMeetingId = mEtMeetingId.getText().toString();
        if(mMeetingId.length()==12){
            Log.e(TAG, "meetingDealWith: "+getSign()+" mMeetingId "+mMeetingId );
            mNetWork.getMeetingInfo(mMeetingId);
        }
        else{
            Toast.makeText(JoinMeetingActivity.this,R.string.str_meeting_id_error,Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * joinMeeting
     */
    private void joinMeeting(){

            String userId = TeamMeetingApp.getTeamMeetingApp().getDevId();
            int code = mMsgSender.TMOptRoom(JMClientType.MCCMD_ENTER, mMeetingId,"");
            if(code==0){
                if(mDebug){
                    Log.e(TAG, "joinMeeting: "+"TMEnterRoom Successed");
                }
            }else if(mDebug){
                Log.e(TAG, "joinMeeting: "+"TMEnterRoom Failed");
            }
            Intent intent = new Intent(JoinMeetingActivity.this,MeetingActivity.class);
            intent.putExtra("meetingId", mMeetingId);
            intent.putExtra("userId", userId);
            startActivity(intent);

    }


    @Override
    public void onEventMainThread(Message msg) {
        {
            switch (EventType.values()[msg.what]) {

                case MSG_GET_MEETING_INFO_SUCCESS:
                    if (mDebug)
                        Log.e(TAG, "MSG_GET_MEETING_INFO_SUCCESS");
                    int usable = msg.getData().getInt("usable");
                    switch (usable){
                        case 0://no
                            Toast.makeText(JoinMeetingActivity.this,R.string.str_meeting_deleted,Toast.LENGTH_SHORT).show();
                            break;

                        case 1://yes
                            mNetWork.insertUserMeetingRoom(getSign(),mMeetingId);
                            break;

                        case 2://private
                            Toast.makeText(JoinMeetingActivity.this,R.string.str_meeting_privated,Toast.LENGTH_SHORT).show();
                            break;
                    }

                    break;
                case MSG_GET_MEETING_INFO_FAILED:
                    if (mDebug)
                        Log.e(TAG, "MSG_GET_MEETING_INFO_FAILED");
                    Toast.makeText(JoinMeetingActivity.this,msg.getData().getString("message"),Toast.LENGTH_SHORT).show();
                    break;
                case MSG_INSERT_USER_MEETING_ROOM_SUCCESS:
                    if (mDebug)
                        Log.e(TAG, "MSG_INSERT_USER_MEETING_ROOM_SUCCESS");
                    joinMeeting();
                    break;
                case MSG_INSERT_USER_MEETING_ROOM_FAILED:
                    if (mDebug)
                        Log.e(TAG, "MSG_INSERT_USER_MEETING_ROOM_FAILED");
                    Toast.makeText(JoinMeetingActivity.this,msg.getData().getString("message"),Toast.LENGTH_SHORT).show();
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
}
