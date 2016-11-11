package org.dync.teameeting.ui.helper;

import android.content.ClipboardManager;
import android.content.Context;

import org.dync.teameeting.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DialogHelper {
    static SweetAlertDialog netErrorSweetAlertDialog = null;
    static int i = -1;

    @SuppressWarnings("deprecation")
    public static void onClickCopy(Context context, String conpyUrl) {
        ClipboardManager cmb = (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(conpyUrl);
        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(context.getString(R.string.dialog_copy_success)).setContentText(context.getString(R.string.dialog_pase_send_friend)).show();
    }


    public static SweetAlertDialog createPrivateDilaog(Context context) {
        SweetAlertDialog sb = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
        sb.setTitleText(context.getString(R.string.dialog_open_prive))
                .setContentText(context.getString(R.string.dialog_prive_not_share))
                .setCancelText(context.getString(R.string.dialog_cancel))
                .setConfirmText(context.getString(R.string.dialog_confirm))
                .showCancelButton(true);
        return sb;
    }

    public static SweetAlertDialog createNetErroDilaog(Context context, SweetAlertDialog.OnSweetClickListener sweetClickListener) {
        SweetAlertDialog netErrorSweetAlertDialog = new SweetAlertDialog(context,
                SweetAlertDialog.WARNING_TYPE).setTitleText(context.getString(R.string.dialog_network_disconnect))
                .setConfirmText(context.getString(R.string.dialog_try)).setContentText(context.getString(R.string.dialog_please_conn_network))
                .setConfirmClickListener(sweetClickListener);
        return netErrorSweetAlertDialog;
    }

    public static SweetAlertDialog createAnyRTCLeave(Context context, SweetAlertDialog.OnSweetClickListener sweetClickListener) {
        SweetAlertDialog netErrorSweetAlertDialog = new SweetAlertDialog(context,
                SweetAlertDialog.WARNING_TYPE).setTitleText(context.getString(R.string.meeting_dialog_anyrtc_leave_title))
                .setConfirmText(context.getString(R.string.dialog_ok)).setContentText(context.getString(R.string.meeting_dialog_anyrtc_leave))
                .setConfirmClickListener(sweetClickListener);
        return netErrorSweetAlertDialog;
    }

    public static SweetAlertDialog createWarningCancel(Context context) {
        SweetAlertDialog sb = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
        sb.setTitleText(context.getString(R.string.dialog_suer_next_meeting))
                .setContentText(context.getString(R.string.dialog_meeting_exist))
                .setCancelText(context.getString(R.string.dialog_cancel))
                .setConfirmText(context.getString(R.string.dialog_confirm))
                .showCancelButton(true);
        return sb;
    }


}
