package com.aloong.welook.compo;

import android.app.Application;

import com.aloong.welook.consts.Constants;
import com.aloong.welook.handler.PicLoader;
import com.aloong.welook.handler.SQLHandler;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AuthInfo;

/**
 * Created by aloong on 2017/8/4.
 */

public class WeLookApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        WbSdk.install(this,new AuthInfo(this, Constants.APP_KEY,Constants.REDIRECT_URL,Constants.SCOPE));
        PicLoader.init(this);
        SQLHandler.init(this);
    }
}
