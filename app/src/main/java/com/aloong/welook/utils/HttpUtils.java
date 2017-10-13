package com.aloong.welook.utils;

import android.content.Intent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by aloong on 2017/8/6.
 */

public class HttpUtils {
    public static InputStream httpRequest(String path) throws IOException {
        URL url = new URL(path);
        Tools.logD("httpRequest:"+url);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setConnectTimeout(10 * 1000);
        urlConnection.setReadTimeout(10 * 1000);
        int responseCode = urlConnection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            return urlConnection.getInputStream();
        }return null;

    }

}
