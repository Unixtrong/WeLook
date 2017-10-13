package com.aloong.welook.handler;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;

/**
 * Created by aloong on 2017/8/7.
 */

public class PerferenceHepler {
    public static void saveTruePwd(Context context, Oauth2AccessToken oauth2AccessToken) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("truePassword", oauth2AccessToken.getToken());
        editor.commit();
    }

    public static String loadTruePwd(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString("truePassword", "?");
    }
}
