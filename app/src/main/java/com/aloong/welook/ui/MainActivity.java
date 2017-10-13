package com.aloong.welook.ui;

import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

//import com.sina.weibo.sdk.demo.R;
import com.aloong.welook.R;
import com.aloong.welook.utils.Tools;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {
    private TextView mTopMain;
    private TextView mTopMessage;
    private TextView mTopFound;
    private TextView[] mTop;
    private ImageView mMianOwnHeadPic;
    private DrawerLayout mLeftDraw;
    private MainFragment mMainFragment;
    private NewsFragment mNewsFragment;
    private FoundFragment mFoundFragment;
    private ViewPager mVP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.left_drawerlayout);
        initView();

    }



    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < 3; i++) {
            if (position == i) {
                mTop[i].setTextColor(Color.argb(255, 255, 255, 255));
            } else {
                mTop[i].setTextColor(Color.argb(180, 255, 255, 255));
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.top_mainAcitvity:
                if (mVP.getCurrentItem() == 0) {
                    //返回顶部
                    mMainFragment.mFeedInfoRv.scrollToPosition(0);
                } else {
                    mVP.setCurrentItem(0);
                }
                break;
            case R.id.top_message:
                if (mVP.getCurrentItem()==1){
                    mNewsFragment.mFeedInfoRv.scrollToPosition(0);
                }
                mVP.setCurrentItem(1);
                break;
            case R.id.top_found:
                mVP.setCurrentItem(2);
                break;
        }
    }

    private class MainFragmentPagerAdapter extends FragmentPagerAdapter {

        MainFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                default:
                    if (mMainFragment == null) {
                        mMainFragment = new MainFragment();
                    }
                    return mMainFragment;
                case 1:
                    if (mNewsFragment == null) {
                        mNewsFragment = new NewsFragment();
                    }
                    return mNewsFragment;
                case 2:
                    if (mFoundFragment == null) {
                        mFoundFragment = new FoundFragment();
                    }
                    return mFoundFragment;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    private void initView() {
        mVP = (ViewPager) findViewById(R.id.main_VP);
        MainFragmentPagerAdapter adapter = new MainFragmentPagerAdapter(this.getSupportFragmentManager());
        mVP.setAdapter(adapter);
        mVP.addOnPageChangeListener(this);
        mLeftDraw = (DrawerLayout) findViewById(R.id.lefe_drawerlayout);
        mMianOwnHeadPic = (ImageView) findViewById(R.id.main_ownheadPic);
        mTopMain = (TextView) findViewById(R.id.top_mainAcitvity);
        mTopMessage = (TextView) findViewById(R.id.top_message);
        mTopFound = (TextView) findViewById(R.id.top_found);
        mTop = new TextView[]{mTopMain, mTopMessage, mTopFound};
        for (int i = 0; i < mTop.length; i++) {
            mTop[i].setOnClickListener(this);
        }
    }
}


