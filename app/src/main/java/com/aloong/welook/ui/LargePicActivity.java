package com.aloong.welook.ui;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.aloong.welook.R;
import com.aloong.welook.handler.PicLoader;
import com.aloong.welook.utils.Tools;

public class LargePicActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {
    private ViewPager mVP;
    private LinearLayout mLL;
    private String[] mPicUrls;
    private int mPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_large_pic);
        Intent intent = getIntent();
        mPicUrls = intent.getStringArrayExtra("url");
        mPosition = intent.getIntExtra("position", 0);
        PagerAdapter adapter = new PagerAdapter() {
            private ImageView[] ivs = new ImageView[mPicUrls.length];
            private LayoutInflater layoutInflater = LayoutInflater.from(LargePicActivity.this);

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
//                super.destroyItem(container, position, object);
                View view = (View) object;
                container.removeView(view);
            }


            @Override
            public Object instantiateItem(ViewGroup container, int position) {
//                return super.instantiateItem(container, position);
                //大图IV
                if (ivs[position] == null) {
                    ivs[position] = (ImageView) layoutInflater.inflate(R.layout.layout_large_imageview, null);
                    ivs[position].setOnClickListener(LargePicActivity.this);
                }
                ImageView iv = ivs[position];
                    String largeUrl = mPicUrls[position].replace("bmiddle", "large").replace("wap720", "large").replace("thumbnail", "large");
                    PicLoader.getInstance().handleBitmap(iv, largeUrl, LargePicActivity.this, 1080, 1080);
                    container.addView(iv);
                return iv;
            }

            @Override
            public int getCount() {
                return mPicUrls.length;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        };
        initView();
        mVP.setAdapter(adapter);
        mVP.setCurrentItem(mPosition);

    }

    private void initView() {
        mVP = (ViewPager) findViewById(R.id.largePic_vp);
        mLL = (LinearLayout) findViewById(R.id.largePic_ll);
        mVP.addOnPageChangeListener(this);
        for (int i = 0; i < mPicUrls.length; i++) {
            //底部指示圆点
            ImageView circleIv = new ImageView(this);
            circleIv.setImageResource(R.drawable.selector_bottom_circle);
            circleIv.setEnabled(false);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMarginStart(Tools.dip(this, 10));
            mLL.addView(circleIv, layoutParams);
        }
        mLL.getChildAt(mPosition).setEnabled(true);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < mPicUrls.length; i++) {
            mLL.getChildAt(i).setEnabled(position == i);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        finish();
    }
}
