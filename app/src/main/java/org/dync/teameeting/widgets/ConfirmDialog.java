package org.dync.teameeting.widgets;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.dync.teameeting.R;

/**
 * 确定在右，取消在左
 */
public class ConfirmDialog extends AlertDialog implements View.OnClickListener {


    private String title;
    private String userName;
    private String ok;
    private boolean isdefaut = true;

    private final OnDialogButtonClickListener listener;
    private EditText evUsername;
    private TextView tvTitleText;

    public ConfirmDialog(Context context, String title, String userName, String ok, OnDialogButtonClickListener listener) {
        super(context);
        this.title = title;
        this.userName = userName;
        this.ok = ok;
        this.listener = listener;
    }

    public ConfirmDialog(Context context, String userName, OnDialogButtonClickListener listener) {
        super(context);
        this.isdefaut = false;
        this.userName = userName;
        this.listener = listener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_ecaluation);
        tvTitleText = (TextView) findViewById(R.id.tv_title_text);
        evUsername = (EditText) findViewById(R.id.et_user_name);
        Button btn_ok = (Button) findViewById(R.id.confirm_button);
        if (isdefaut) {
            tvTitleText.setText(title);
            btn_ok.setText(ok);
        }
        evUsername.setText(userName);
        btn_ok.setOnClickListener(this);
    }


    /**
     * 对话框按钮单击的监听器
     */
    public interface OnDialogButtonClickListener {

        /**
         * 当确定按钮被单击的时候会执行
         */
        void onOkClick(Dialog dialog, View v, String username);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm_button: // 确定
                if (listener != null) {
                    listener.onOkClick(this, v, evUsername.getText().toString().trim());
                }
                cancel();
                break;
        }
    }

}
