package org.dync.teameeting.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import org.dync.teameeting.R;

public class DialogActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //case R.id.btn_ok:
              //  finish();
               // break;
        }
    }
}
