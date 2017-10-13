package com.aloong.welook.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.aloong.welook.R;
import com.aloong.welook.handler.PerferenceHepler;
import com.aloong.welook.utils.Tools;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbAuthListener;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;

public class LoginActivity extends AppCompatActivity implements WbAuthListener {

    private SsoHandler mSsoHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mSsoHandler = new SsoHandler(this);
        haveTruePwd(PerferenceHepler.loadTruePwd(this));
        //clearSp();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mSsoHandler.authorizeCallBack(requestCode,resultCode,data);
    }

    @Override
    public void onSuccess(Oauth2AccessToken oauth2AccessToken) {
        //验证成功
        boolean result = oauth2AccessToken.isSessionValid();
        if (result){
            toMainAcitivity();
            Tools.toastS(this,getString(R.string.tell_seccess));
            PerferenceHepler.saveTruePwd(this,oauth2AccessToken);
            finish();
        }else {
            Tools.toastS(this,getString(R.string.tell_session_invalidation));
        }
    }

    @Override
    public void cancel() {
        //验证取消
        Tools.toastS(this,getString(R.string.tell_cancel));
    }

    @Override
    public void onFailure(WbConnectErrorMessage wbConnectErrorMessage) {
        //验证失败
        Tools.toastS(this,getString(R.string.tell_fail));
    }



    private void clearSp(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor =sp.edit();
        editor.remove("truePassword");
        editor.apply();
    }

    public void loginWeibo(){
        mSsoHandler.authorizeWeb(this);
    }

    private void toMainAcitivity() {
        Intent intent = new Intent();
        intent.setClass(this,MainActivity.class);
        startActivity(intent);
    }

    private void haveTruePwd(String spTruePwd) {
        //如果传回来？证明没有验证成功记录 重登录微博网页登录微博
        if (spTruePwd.equals("?")){
            loginWeibo();
        }else {
            //进入主页面
            toMainAcitivity();
            finish();
        }
    }

    public void reLogin(View view) {
        loginWeibo();
    }
}
