package com.aloong.welook.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by aloong on 2017/8/11.
 */

public class headImageView extends android.support.v7.widget.AppCompatImageView {
    private Paint mPaint;
    private Bitmap mSrcBitmap;
    private Bitmap mMaskBitmap;
    private Xfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);

    public headImageView(Context context) {
        super(context);
        init();
    }


    public headImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public headImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        Bitmap maskBitmap = getMaskBitmap();
        Bitmap combineBitmap = combineBitmap(drawable, maskBitmap);
        canvas.drawBitmap(combineBitmap, 0, 0, mPaint);
    }

    private Bitmap combineBitmap(Drawable drawable, Bitmap maskBitmap) {
        if (mSrcBitmap == null ||mSrcBitmap.isRecycled()) {
            mSrcBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(mSrcBitmap);
        drawable.setBounds(0, 0, getWidth(), getWidth());
        drawable.draw(canvas);
        mPaint.setXfermode(xfermode);
        canvas.drawBitmap(maskBitmap, 0, 0, mPaint);
        mPaint.setXfermode(null);
        return mSrcBitmap;
    }

    public Bitmap getMaskBitmap() {
        if (mMaskBitmap == null || mMaskBitmap.isRecycled()) {
            mMaskBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(mMaskBitmap);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.BLACK);
            canvas.drawCircle(getWidth()/2,getHeight()/2,getWidth()/2,paint);
        }
        return mMaskBitmap;
    }
}