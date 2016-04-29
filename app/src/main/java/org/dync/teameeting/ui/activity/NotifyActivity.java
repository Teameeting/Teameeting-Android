package org.dync.teameeting.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.dync.teameeting.R;
import org.dync.teameeting.TeamMeetingApp;
import org.dync.teameeting.http.NetWork;
import org.dync.teameeting.structs.EventType;
import org.dync.teameeting.structs.JoinActType;

import java.util.List;

import de.greenrobot.event.EventBus;
import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by liuxiaozhong on 2016/4/15.
 */
public class NotifyActivity extends Activity {
    MaterialDialog dialog;
    String roomId;
    public NetWork mNetWork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);
        init();

    }

    private void init() {
        Intent i = getIntent();
        String roomMaster = i.getStringExtra("roomMaster");
        roomId = i.getStringExtra("roomId");
        String roomName = i.getStringExtra("roomName");
        View view = LayoutInflater.from(this).inflate(R.layout.item_notify, null);
        TextView title = (TextView) view.findViewById(R.id.tv_notify_title);
        TextView room = (TextView) view.findViewById(R.id.tv_notify_roomname);
        title.setText(roomMaster + "邀请您加入会议");
        room.setText(roomName);
        dialog = new MaterialDialog(this)
                .setTitle("通知")
                .setContentView(view)
                .setPositiveButton("入会", new PositiveButtonOnclick())
                .setNegativeButton("忽略", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        finish();
                    }
                });
        dialog.show();
    }

    public class PositiveButtonOnclick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            meetingDealWith();
            finish();
        }
    }

    private void meetingDealWith() {
        int position = TeamMeetingApp.getmSelfData().getMeetingIdPosition(roomId);
        if (position >= 0) {
            Message msg = new Message();
            List<String> activityList = TeamMeetingApp.getMeetingActivityList();
          if (activityList.size()>0){
              String mMeetingId = activityList.get(0);
             if (mMeetingId!=null){
                 msg.what = EventType.MSG_NOTIFY_OFF.ordinal();
             }
          }else {
              msg.what = EventType.MSG_ROOMSEETING_ENTER_ROOM.ordinal();
          }
            msg.obj = position;
            EventBus.getDefault().post(msg);
            finish();
        } else {
            mNetWork.getMeetingInfo(roomId, JoinActType.JOIN_START_ACTIVITY);
        }

    }

}
