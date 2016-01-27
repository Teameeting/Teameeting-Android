package org.dync.teameeting.receiver;

import org.dync.teameeting.TeamMeetingApp;
import org.dync.teameeting.structs.EventType;
import org.dync.teameeting.structs.NetType;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;

import de.greenrobot.event.EventBus;


public class NetWorkReceiver extends BroadcastReceiver {
    private static final String TAG = "NetWorkReceiver";
    private static final boolean mDebug = TeamMeetingApp.mIsDebug;
    /*
     * HACKISH: These constants aren't yet available in my API level (7), but I
     * need to handle these cases if they come up, on newer versions
     */
    public static final int NETWORK_TYPE_EHRPD = 14; // Level 11
    public static final int NETWORK_TYPE_EVDO_B = 12; // Level 9
    public static final int NETWORK_TYPE_HSPAP = 15; // Level 13
    public static final int NETWORK_TYPE_IDEN = 11; // Level 8
    public static final int NETWORK_TYPE_LTE = 13; // Level 11

    private ConnectivityManager mConnectivity;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if (null == mConnectivity) {
            mConnectivity = (ConnectivityManager) TeamMeetingApp.getTeamMeetingApp()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
        }


        NetworkInfo info = mConnectivity.getActiveNetworkInfo();

        Message msg = new Message();
        msg.what = EventType.MSG_NET_WORK_TYPE.ordinal();
        Bundle bundle = new Bundle();

        if (info == null || !mConnectivity.getBackgroundDataSetting()) {
            bundle.putInt("net_type", NetType.TYPE_NULL.ordinal());
            TeamMeetingApp.getmSelfData().setmIsNetConnected(false);
            msg.setData(bundle);
            Log.e(TAG, "========>" + NetType.values()[bundle.getInt("net_type")].toString());
            EventBus.getDefault().post(msg);
            return;
        }

        int netType = info.getType();
        int netSubtype = info.getSubtype();

        if (netType == ConnectivityManager.TYPE_WIFI) {
            if (info.isConnected()) {
                Log.e(TAG, "========>wifi1" + NetType.values()[bundle.getInt("net_type")].toString());
                bundle.putInt("net_type", NetType.TYPE_WIFI.ordinal());
                TeamMeetingApp.getmSelfData().setmIsNetConnected(true);
            } else {
                Log.e(TAG, "========>wifi2" + NetType.values()[bundle.getInt("net_type")].toString());
                bundle.putInt("net_type", NetType.TYPE_WIFI_NULL.ordinal());
                TeamMeetingApp.getmSelfData().setmIsNetConnected(false);
            }
        } else if (netType == ConnectivityManager.TYPE_MOBILE) {
            TeamMeetingApp.getmSelfData().setmIsNetConnected(true);
            switch (netSubtype) {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    bundle.putInt("net_type", NetType.TYPE_2G.ordinal()); // ~
                    // 50-100
                    break; // kbps
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    bundle.putInt("net_type", NetType.TYPE_2G.ordinal()); // ~ 14-64
                    break; // kbps
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    bundle.putInt("net_type", NetType.TYPE_2G.ordinal()); // ~
                    // 50-100
                    break; // kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    bundle.putInt("net_type", NetType.TYPE_3G.ordinal()); // ~
                    // 400-1000
                    break; // kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    bundle.putInt("net_type", NetType.TYPE_3G.ordinal()); // ~
                    // 600-1400
                    break; // kbps
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    bundle.putInt("net_type", NetType.TYPE_2G.ordinal()); // ~ 100
                    break; // kbps
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    bundle.putInt("net_type", NetType.TYPE_3G.ordinal()); // ~ 2-14
                    break; // Mbps
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    bundle.putInt("net_type", NetType.TYPE_3G.ordinal()); // ~
                    // 700-1700
                    break; // kbps
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    bundle.putInt("net_type", NetType.TYPE_3G.ordinal()); // ~ 1-23
                    break; // Mbps
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    bundle.putInt("net_type", NetType.TYPE_3G.ordinal()); // ~
                    // 400-7000
                    break; // kbps
                // NOT AVAILABLE YET IN API LEVEL 7
                case NETWORK_TYPE_EHRPD:
                    bundle.putInt("net_type", NetType.TYPE_3G.ordinal()); // ~ 1-2
                    break; // Mbps
                case NETWORK_TYPE_EVDO_B:
                    bundle.putInt("net_type", NetType.TYPE_3G.ordinal()); // ~ 5
                    break; // Mbps
                case NETWORK_TYPE_HSPAP:
                    bundle.putInt("net_type", NetType.TYPE_3G.ordinal()); // ~ 10-20
                    break; // Mbps
                case NETWORK_TYPE_IDEN:
                    bundle.putInt("net_type", NetType.TYPE_2G.ordinal());// ~25 kbps
                    break;
                case NETWORK_TYPE_LTE:
                    bundle.putInt("net_type", NetType.TYPE_4G.ordinal()); // ~ 10+
                    break; // Mbps
                // Unknown
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                    bundle.putInt("net_type", NetType.TYPE_2G.ordinal());
                    break;
            }
        } else {
            TeamMeetingApp.getmSelfData().setmIsNetConnected(true);
            bundle.putInt("net_type", NetType.TYPE_UNKNOWN.ordinal());
        }
        if (mDebug)
            Log.e(TAG,
                    "========>123"
                            + NetType.values()[bundle.getInt("net_type")]
                            .toString());

        msg.setData(bundle);

        EventBus.getDefault().post(msg);

    }

}
