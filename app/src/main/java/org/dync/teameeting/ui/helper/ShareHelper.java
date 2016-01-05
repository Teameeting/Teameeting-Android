package org.dync.teameeting.ui.helper;

import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

/**
 * 
 * @author zhulang <br/>
 *         org.dync.teameeting.helper ShareHelper create at 2015-12-22
 *         上午10:59:20
 */
public class ShareHelper
{
	private Context context;

	public ShareHelper(Context context)
	{
		this.context = context;
	}

	public void shareSMS(String smsMessage, String webUrl)
	{
		String smsBody = smsMessage + "链接地址ַ:" + webUrl;
		Uri smsToUri = Uri.parse("smsto:");  
		Intent sendIntent = new Intent(Intent.ACTION_VIEW, smsToUri);

		sendIntent.putExtra("sms_body", smsBody);
		sendIntent.setType("vnd.android-dir/mms-sms");
		
		context.startActivity(sendIntent);
	}

	public void shareWeiXin(String msgTitle, String msgText, String webUrl)
	{
		msgText = msgText + webUrl;
		ShareItem share = new ShareItem("分享到.....",
				"com.tencent.mm.ui.tools.ShareImgUI", "com.tencent.mm");

		shareMsg(msgTitle, msgText, share);
	}

	/**
	 * 
	 * 
	 * @param context
	 * @param msgTitle
	 * @param msgText
	 * @param share
	 */
	private void shareMsg(String msgTitle, String msgText, ShareItem share)
	{
		if (!share.packageName.isEmpty() && !isAvilible(share.packageName))
		{
			Toast.makeText(context, "请安装微信" + share.title, Toast.LENGTH_SHORT)
					.show();
			return;
		}

		Intent intent = new Intent("android.intent.action.SEND");
		if (msgText.equals(""))
		{
			intent.setType("text/plain");
		}

		intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
		intent.putExtra(Intent.EXTRA_TEXT, msgText);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if (!share.packageName.isEmpty())
		{
			intent.setComponent(new ComponentName(share.packageName,
					share.activityName));
			context.startActivity(intent);
		} else
		{
			context.startActivity(Intent.createChooser(intent, msgTitle));
		}
	}

	/**
	 * 
	 * @param context
	 * @param packageName
	 * @return
	 */
	public boolean isAvilible(String packageName)
	{
		PackageManager packageManager = context.getPackageManager();

		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
		for (int i = 0; i < pinfo.size(); i++)
		{
			if (((PackageInfo) pinfo.get(i)).packageName
					.equalsIgnoreCase(packageName))
				return true;
		}
		return false;
	}

	private class ShareItem
	{
		String title;

		String activityName;
		String packageName;

		public ShareItem(String title, String activityName, String packageName)
		{
			this.title = title;
			this.activityName = activityName;
			this.packageName = packageName;
		}
	}

}
