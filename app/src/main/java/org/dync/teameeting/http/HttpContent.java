package org.dync.teameeting.http;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by 小白龙 on 2015/12/25 0025.
 */
public class HttpContent {
    // private static final String BASE_URL = "http://api.twitter.com/1/";
    //public static final String NODE_URL = "http://123.59.68.21:8055/";
    public static final String NODE_URL = "http://192.168.7.45:8055/";//123.59.68.21:8055

    public static final String RETURN_TYPE_JSON = "application/json"; // 返回json
    public static final String RETURN_TYPE_XML = "application/xml"; // 返回xml\

    private static AsyncHttpClient client = new AsyncHttpClient();

    static {
        client.addHeader("Accept", "application/json");
    }

    public static void get(String url, AsyncHttpResponseHandler responseHandler) {
        url = getAbsoluteUrl(url);
        Log.e("xbl", "get: url" + url);
        client.get(url, responseHandler);
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        //return relativeUrl;
        return NODE_URL + relativeUrl;
    }
}
