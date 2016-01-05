package org.dync.teameeting.http;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.loopj.android.http.TextHttpResponseHandler;
import com.ypy.eventbus.EventBus;

import org.apache.http.Header;
import org.dync.teameeting.TeamMeetingApp;
import org.dync.teameeting.structs.EventType;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 小白龙 on 2015/12/28 0028.
 */
public class TmTextHttpResponseHandler extends TextHttpResponseHandler
{
    public boolean mDebug = TeamMeetingApp.mIsDebug;
    public Gson gson = null;
    public Bundle bundle;
    public Message msg;


    public TmTextHttpResponseHandler()
    {
        super("UTF-8");
        gson = new Gson();
        msg = new Message();
        bundle = new Bundle();
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable)
    {
        // 网络问题。  or.服务器挂了。
        msg.what = EventType.MSG_RESPONS_ESTR_NULl.ordinal();
        EventBus.getDefault().post(msg);
        if (mDebug)
        {
            Log.e("TextHttpResponseHandler", "onFailure: responseString" + responseString);
            Log.e("TextHttpResponseHandler", "网络问题。  or.服务器挂了。");
        }
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, String responseString)
    {
        JSONObject jsonObject = null;
        String message = null;
        try
        {
            jsonObject = new JSONObject(responseString);

            int code = jsonObject.getInt("code");
            message = jsonObject.getString("message");

            this.onSuccess(statusCode, code, message, responseString, headers);

        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }


    public void onSuccess(int statusCode, int code, String message, String responseString, Header[] headers)
    {
        Log.w("TextHttpResponseHandler", "onSuccess(int, Header[], JSONArray) was not overriden, but callback was received");
    }

}
