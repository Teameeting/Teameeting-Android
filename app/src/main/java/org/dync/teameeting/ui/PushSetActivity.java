package org.dync.teameeting.ui;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import org.dync.teameeting.R;
import org.dync.teameeting.TeamMeetingApp;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

public class PushSetActivity extends Activity implements OnClickListener {
    private static final String JPUSH = "JPush";

    Button mSetTag;
    Button mSetAlias;

    private static final int MSG_SET_ALIAS = 1001;
    private static final int MSG_SET_TAGS = 1002;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    Log.d(JPUSH, "Set alias in handler.");
                    JPushInterface.setAliasAndTags(getApplicationContext(), (String) msg.obj, null,
                            mAliasCallback);
                    break;
                case MSG_SET_TAGS:
                    Log.d(JPUSH, "Set tags in handler.");
                    JPushInterface.setAliasAndTags(getApplicationContext(), null,
                            (Set<String>) msg.obj, mTagsCallback);
                    break;
                default:
                    Log.i(JPUSH, "Unhandled msg - " + msg.what);
            }
        }
    };

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_push_set);
        init();
        initListener();
    }

    private void initListener() {
        // TODO Auto-generated method stub
        mSetTag.setOnClickListener(this);
        mSetAlias.setOnClickListener(this);
    }

    private void init() {
        mSetTag = (Button) findViewById(R.id.bt_tag);
        EditText viewById = (EditText) findViewById(R.id.et_tag);
        String tag = TeamMeetingApp.getTeamMeetingApp().getDevId();
        viewById.setText(tag);
        mSetAlias = (Button) findViewById(R.id.bt_alias);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_tag:
                setTag();
                break;
            case R.id.bt_alias:
                setAlias();
                break;
        }
    }

    private void setTag() {
        EditText tagEdit = (EditText) findViewById(R.id.et_tag);
        String tag = tagEdit.getText().toString().trim();

        // 检查 tag 的有效性
        if (TextUtils.isEmpty(tag)) {
            Toast.makeText(PushSetActivity.this, "tag不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        Logger.e(tag);
        // ","隔开的多个 转换成 Set
        String[] sArray = tag.split(",");
        Set<String> tagSet = new LinkedHashSet<String>();
        for (String sTagItme : sArray) {
            if (!isValidTagAndAlias(sTagItme)) {
                Toast.makeText(PushSetActivity.this, "格式不对", Toast.LENGTH_SHORT).show();
                return;
            }
            tagSet.add(sTagItme);
        }

        // 调用JPush API设置Tag
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_TAGS, tagSet));
    }

    private void setAlias() {
        EditText aliasEdit = (EditText) findViewById(R.id.et_alias);
        String alias = aliasEdit.getText().toString().trim();
        if (TextUtils.isEmpty(alias)) {
            Toast.makeText(PushSetActivity.this, "alias不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isValidTagAndAlias(alias)) {
            Toast.makeText(PushSetActivity.this, "格式不对", Toast.LENGTH_SHORT).show();
            return;
        }

        // 调用JPush API设置Alias
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, alias));
    }

    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    Log.i(JPUSH, logs);
                    break;

                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.i(JPUSH, logs);
                    if (isConnected(getApplicationContext())) {
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias),
                                1000 * 60);
                    } else {
                        Log.i(JPUSH, "No network");
                    }
                    break;

                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e(JPUSH, logs);
            }

            showToast(logs, getApplicationContext());
        }

    };

    private final TagAliasCallback mTagsCallback = new TagAliasCallback() {

        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    Log.i(JPUSH, logs);
                    break;

                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.i(JPUSH, logs);
                    if (isConnected(getApplicationContext())) {
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_TAGS, tags),
                                1000 * 60);
                    } else {
                        Log.i(JPUSH, "No network");
                    }
                    break;

                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e(JPUSH, logs);
            }

            showToast(logs, getApplicationContext());
        }

    };

    // ******************************Utils*******************************
    // 校验Tag Alias 只能是数字,英文字母和中文
    public static boolean isValidTagAndAlias(String s) {
        Pattern p = Pattern.compile("^[\u4E00-\u9FA50-9a-zA-Z_-]{0,}$");
        Matcher m = p.matcher(s);
        return m.matches();
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager conn = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = conn.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }

    public static void showToast(final String toast, final Context context) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }).start();
    }

}
