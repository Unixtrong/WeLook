package com.aloong.welook.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.aloong.welook.R;
import com.aloong.welook.handler.PicLoader;

public class SingleLargePicActivity extends AppCompatActivity implements View.OnClickListener {
    private String mPicUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_large_pic);
        Intent intent = getIntent();
        mPicUrl = intent.getStringExtra("url");
        ImageView imageView = (ImageView) findViewById(R.id.singleLargePic);
        imageView.setOnClickListener(SingleLargePicActivity.this);
        String largeUrl = mPicUrl.replace("bmiddle", "large").replace("wap720", "large").replace("thumbnail", "large");
        PicLoader.getInstance().handleBitmap(imageView, largeUrl, SingleLargePicActivity.this, 1080, 1080);
    }

    @Override
    public void onClick(View v) {
        finish();
    }
}
