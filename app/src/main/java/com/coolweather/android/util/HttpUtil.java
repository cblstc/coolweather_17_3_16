package com.coolweather.android.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * 和服务器交互的类
 * Created by Administrator on 2017/3/16.
 */

public class HttpUtil {

    /**
     * 和服务器交互
     * @param address  请求地址
     * @param callback 回调？
     */
    public static void sendOkHttpRequest(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}
