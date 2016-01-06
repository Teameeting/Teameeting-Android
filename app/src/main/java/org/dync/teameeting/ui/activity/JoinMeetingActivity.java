package org.dync.teameeting.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.dync.teameeting.R;
import org.dync.teameeting.TeamMeetingApp;
import org.dync.teameeting.sdkmsgclientandroid.jni.JMClientType;

public class JoinMeetingActivity extends Activity implements View.OnClickListener{
    private final static String TAG = "JoinMeetingActivity";
    private boolean mDebug = TeamMeetingApp.mIsDebug;
    private EditText mEtMeetingId;
    private ImageButton mIbtnJoinMeeting,mIbtnback;
    private String mPass="123456";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_meeting);
        inintView();
    }

    private  void inintView(){
        mEtMeetingId = (EditText) findViewById(R.id.et_meeting_id);
        mIbtnJoinMeeting = (ImageButton) findViewById(R.id.ibtn_join_meeting);
        mIbtnback = (ImageButton) findViewById(R.id.ibtn_back);
        mIbtnback.setOnClickListener(this);
        mIbtnJoinMeeting.setOnClickListener(this);
        mEtMeetingId.setOnEditorActionListener(mOnEditorActionListener);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.ibtn_back:
                finish();
                break;
            case R.id.ibtn_join_meeting:


                joinMeeting();


                break;


        }


    }


    TextView.OnEditorActionListener mOnEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {


            joinMeeting();
            return false;
        }
    };

    /**
     * joinMeeting
     */
    private void joinMeeting(){

        String meetingId = mEtMeetingId.getText().toString();
        if(meetingId.length()!=0&&meetingId!=null){
            String userId = TeamMeetingApp.getTeamMeetingApp().getDevId();
            int code = StartFlashActivity.mMsgSender.TMOptRoom(JMClientType.TMCMD_ENTER,userId, mPass, meetingId,"");
            if(code==0){
                if(mDebug){
                    Log.e(TAG, "onItemClickListener: "+"TMEnterRoom Successed");
                }
            }else if(mDebug){
                Log.e(TAG, "onItemClickListener: "+"TMEnterRoom Failed");
            }
            Intent intent = new Intent(JoinMeetingActivity.this,MeetingActivity.class);
            intent.putExtra("meetingId", meetingId);
            intent.putExtra("userId", userId);
            startActivity(intent);
        }
        else{

        }

    }


}
