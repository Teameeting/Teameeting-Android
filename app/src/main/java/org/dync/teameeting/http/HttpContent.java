package org.dync.teameeting.http;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by Zlang on 2015/12/25 00:25.
 * <p/>
 * net Content
 */
public class HttpContent {
    public static final String NODE_URL = "http://restful.teameeting.cn:8055/";
    //public static final String NODE_URL = "http://192.168.7.49:8055/";//123.59.68.21:8055
    public static final String SERVICE_URL = "message.anyrtc.io";
   // public static final String SERVICE_URL = "192.168.7.43";
    public static final Integer MSG_SERVICE_POINT = 6630;
    private static AsyncHttpClient client = new AsyncHttpClient();

    static {
        client.addHeader("Accept", "application/json");
    }

    public static void get(String url, AsyncHttpResponseHandler responseHandler) {
        url = getAbsoluteUrl(url);
        client.get(url, responseHandler);
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return NODE_URL + relativeUrl;
    }
}
