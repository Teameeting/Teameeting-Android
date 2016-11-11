package org.dync.teameeting.utils;

import android.content.Context;

import android.support.v4.view.MotionEventCompat;

import android.view.MotionEvent;

import android.widget.Toast;


/**
 *
 * @author ZLang <br/>
 *         create at 2015-12-1 ??10:35:11
 */
public class Utils {
	public static Toast mToast;

	/**
	 * @param mContext
	 * @param msg
	 */
	public static void showToast(Context mContext, String msg) {
		if (mToast == null) {
			mToast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);
		}

		mToast.setText(msg);
		mToast.show();
	}

	/**
	 *
	 * @param event
	 * @return
	 */
	public static String getActionName(MotionEvent event) {
		String action = "unknow";

		switch (MotionEventCompat.getActionMasked(event)) {
			case MotionEvent.ACTION_DOWN:
				action = "ACTION_DOWN";

				break;

			case MotionEvent.ACTION_MOVE:
				action = "ACTION_MOVE";

				break;

			case MotionEvent.ACTION_UP:
				action = "ACTION_UP";

				break;

			case MotionEvent.ACTION_CANCEL:
				action = "ACTION_CANCEL";

				break;

			case MotionEvent.ACTION_OUTSIDE:
				action = "ACTION_SCROLL";

				break;

			default:
				break;
		}

		return action;
	}
}
