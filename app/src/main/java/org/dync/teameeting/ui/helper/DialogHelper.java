package org.dync.teameeting.ui.helper;

import android.content.ClipboardManager;
import android.content.Context;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DialogHelper
{
    static  SweetAlertDialog netErrorSweetAlertDialog=null;
	@SuppressWarnings("deprecation")
	public static void onClickCopy(Context context, String conpyUrl)
	{
		// TextView tvCopyTextView = (TextView) findViewById(R.id.tv_copy_link);
		ClipboardManager cmb = (ClipboardManager) context
				.getSystemService(Context.CLIPBOARD_SERVICE);
		cmb.setText(conpyUrl);
		new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
				.setTitleText("复制成功").setContentText("粘贴给朋友邀请加入会议!").show();
	}
  //
    public  static  SweetAlertDialog createNetErroDilaog(Context context)
    {
        if(netErrorSweetAlertDialog==null)
          return createNetErroDilaog(context,null);
        else
            return netErrorSweetAlertDialog;
    }
  //
    public static SweetAlertDialog createNetErroDilaog(Context context, SweetAlertDialog.OnSweetClickListener sweetClickListener)
    {
        SweetAlertDialog netErrorSweetAlertDialog = new SweetAlertDialog(context,
                SweetAlertDialog.ERROR_TYPE).setTitleText("网络已断开...")
                .setConfirmText("ok").setContentText("请连接网络!")
                .setConfirmClickListener(sweetClickListener);
        return netErrorSweetAlertDialog;
    }



}
