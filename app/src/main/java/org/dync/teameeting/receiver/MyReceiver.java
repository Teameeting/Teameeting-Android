package org.dync.teameeting.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import org.dync.teameeting.TeamMeetingApp;
import org.dync.teameeting.structs.EventType;
import org.dync.teameeting.ui.activity.StartFlashActivity;
import org.dync.teameeting.ui.helper.ActivityTaskHelp;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.jpush.android.api.JPushInterface;
import de.greenrobot.event.EventBus;

public class MyReceiver extends BroadcastReceiver {
    private boolean mDebug = TeamMeetingApp.mIsDebug;
    private static final String TAG = "JPush";
    public static final String ACTIVITY_ACTION_NOTIFACTION = "action_notifation";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            chackNotifiaction(context, bundle);
        }

        /*
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            chackNotifiaction(context, bundle);
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            Log.e(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
        } else {
            Log.e(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }*/
    }

    /**
     * on click ontifiatin
     *
     * @param context
     * @param bundle
     */
    private void chackNotifiaction(Context context, Bundle bundle) {

        String notifaction = bundle.getString(JPushInterface.EXTRA_EXTRA);

        int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
        JPushInterface.clearNotificationById(TeamMeetingApp.getTeamMeetingApp(), notifactionId);
        Boolean userInfoBoolean = TeamMeetingApp.isInitFalg;
        if (mDebug) {
            Log.e(TAG, "chackNotifiaction: IsAppRun" + userInfoBoolean);
            Log.e(TAG, "notifaction" + notifaction + "notifactionID" + notifactionId);
        }

        if (userInfoBoolean) {
            runAppFacation(context, bundle);
        } else {
            startfalshActivity(context, bundle);
        }
    }

    public void startfalshActivity(Context context, Bundle bundle) {
        Intent i = new Intent(context, StartFlashActivity.class);
        i.setAction(ACTIVITY_ACTION_NOTIFACTION);
        i.putExtras(bundle);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(i);
    }

    private void runAppFacation(Context context, Bundle bundle) {
        String notifaction = bundle.getString(JPushInterface.EXTRA_EXTRA);
        Message msg = new Message();
        msg.setData(bundle);

        String meetingId = null;
        try {
            JSONObject json = new JSONObject(notifaction);
            meetingId = json.getString("roomid");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        List<String> activityList = TeamMeetingApp.getMeetingActivityList();
        if (mDebug) {
            //fan
            Log.e
                    (TAG, "activityList Size: " + activityList.size());
        }
        if (activityList.size() == 1) {
            String notifiMeetingId = activityList.get(0);
            boolean packageNameonResume = ActivityTaskHelp.isPackageNameonResume(context, null);
            if (mDebug) {
                Log.e(TAG, "MeetingId: " + notifiMeetingId + "packageNameonResume--" + packageNameonResume);
            }
            if (notifiMeetingId.equals(meetingId)) {
                msg.what = EventType.MSG_NOTIFICATION_MEETINGID_EQUAL.ordinal();
                Log.e(TAG, "----- meetingId equqals ");
            } else {
                msg.what = EventType.MSG_NOTIFICATION_MEETING_CLOSE_MAIN.ordinal();
                Log.e(TAG, "----- meetingId not equals");
            }
        } else {
            msg.what = EventType.MSG_NOTIFICATION_MAIN.ordinal();
            Log.e(TAG, "---- activityList === 0 ");
        }
        EventBus.getDefault().post(msg);
    }


}
