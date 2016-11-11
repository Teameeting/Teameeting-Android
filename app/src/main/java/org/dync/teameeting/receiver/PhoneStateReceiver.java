package org.dync.teameeting.receiver;

import android.app.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.telephony.TelephonyManager;

import android.util.Log;


public class PhoneStateReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
			TelephonyManager tManager = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);

			switch (tManager.getCallState()) {
				case TelephonyManager.CALL_STATE_RINGING:
					Log.e("PhoneStateReveiver",
							"phoneNumber:  " +
									intent.getStringExtra("incoming_number"));

					try {
						//RtkApp.the().getCore().getITelephony().endCall();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					break;

				case TelephonyManager.CALL_STATE_OFFHOOK:
					break;

				case TelephonyManager.CALL_STATE_IDLE:
					break;
			}
		}
	}
}
