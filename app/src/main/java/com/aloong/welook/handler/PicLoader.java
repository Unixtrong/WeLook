package com.aloong.welook.handler;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import com.aloong.welook.utils.HttpUtils;
import com.aloong.welook.utils.Tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by aloong on 2017/9/4.
 */

public class PicLoader {

    private static PicLoader sPicLoader;
    private ExecutorService mExecutor = Executors.newCachedThreadPool();
    private LruCache<String, Bitmap> mLruCache;

    public static PicLoader getInstance() {
        return sPicLoader;
    }

    private PicLoader(Context context) {
        mLruCache = new LruCache<>(Tools.calculateMemoryCacheSize(context));
    }

    public static void init(Context context) {
        sPicLoader = new PicLoader(context);
    }

    private static void bindBitmap(final ImageView iv, final Bitmap bitmap, Context context) {
        if (bitmap != null) {
            if (context instanceof Activity) {
                Activity activity = (Activity) context;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iv.setImageBitmap(bitmap);
                    }
                });
            }
        }
    }

    public void handleBitmap(final ImageView iv, final String url, final Context context, final int customHeight, final int customWidth) {
        final String key = getKey(url);
        Bitmap cacheBitmap = mLruCache.get(key);
        if (cacheBitmap == null) {
            File file = new File(context.getExternalCacheDir(), key);
            if (!file.exists()) {
                mExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            File file = downloadBitmap(context, url);
                            if (file != null && file.exists()) {
                                Bitmap bitmap1 = PicLoader.decodeBitmap(context, url, customHeight, customWidth);
                                if (bitmap1 != null) {
                                    bindBitmap(iv, bitmap1, context);
                                    mLruCache.put(key, bitmap1);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                Bitmap bitmap = PicLoader.decodeBitmap(context, url, customHeight, customWidth);
                if (bitmap == null) {
                    file.delete();
                    handleBitmap(iv, url, context, customHeight, customWidth);
                    return;
                }
                bindBitmap(iv, bitmap, context);
                mLruCache.put(key, bitmap);
            }
        } else {
            bindBitmap(iv, cacheBitmap, context);
        }
    }

    public static File downloadBitmap(Context context, String imageUrl) {
        File file = new File(context.getExternalCacheDir(), PicLoader.getKey(imageUrl));
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            inputStream = HttpUtils.httpRequest(imageUrl);
            fileOutputStream = new FileOutputStream(file);// 抛异常，文件未找到
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, len);
            }
            return file;
        } catch (IOException e) {
            Tools.logD("downloadBitmap获得 " + imageUrl + "的流失败");
            e.printStackTrace();
        } finally {
            Tools.closeStream(inputStream);
            Tools.closeStream(fileOutputStream);
        }
        return null;
    }

    private static BitmapFactory.Options calculateInSampleSize(BitmapFactory.Options options, int customHeight, int customWidth) {
        int originHeight = options.outHeight;
        int originWidth = options.outWidth;
        int inSampleSize = 1;
        if (originWidth > customWidth || originHeight > customHeight) {
            int heightRatio = Math.round((float) originHeight / (float) customHeight);
            int widthRatio = Math.round((float) originWidth / (float) customWidth);
            inSampleSize = Math.max(heightRatio, widthRatio);
        }
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        return options;
    }

    public static Bitmap decodeBitmap(Context context, String url, int customHeight, int customWidth) {
        File mntCache = context.getExternalCacheDir();
        File file = new File(mntCache, getKey(url));
        String path = file.getAbsolutePath();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options = calculateInSampleSize(options, customHeight, customWidth);
        return BitmapFactory.decodeFile(path, options);
    }

    private static String getKey(String url) {
        return url.replace(".jpg", "9999").replace("/", "1111");
    }
}
