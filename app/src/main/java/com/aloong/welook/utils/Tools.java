package com.aloong.welook.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

import java.io.Closeable;
import java.io.IOException;


import static android.content.pm.ApplicationInfo.FLAG_LARGE_HEAP;

/**
 * Created by aloong on 2017/8/4.
 */

public class Tools {

    public static void toastS(Context context, String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }
    public static void toastS(Context context,int id){
        Toast.makeText(context,id,Toast.LENGTH_SHORT).show();
    }

    public static void logD(String msg){
        Log.d("WL",msg);
    }

    public static void  closeStream(Closeable stream){
        try {
            if (stream!=null){
                stream.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static int calculateMemoryCacheSize(Context context) {
        ActivityManager am = getService(context, Context.ACTIVITY_SERVICE);
        boolean largeHeap = (context.getApplicationInfo().flags & FLAG_LARGE_HEAP) != 0;
        int memoryClass = largeHeap ? am.getLargeMemoryClass() : am.getMemoryClass();
        return (int) (1024L * 1024L * memoryClass / 7);
    }
    @SuppressWarnings("unchecked")
    public static <T> T getService(Context context, String service) {
        return (T) context.getSystemService(service);
    }

    public static int dip(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
    }

    public abstract static class OnRecycleScrollListener extends RecyclerView.OnScrollListener {

        private boolean firstLoaded = false;
        private boolean isLoading = false;
        private boolean loadFinish = false;
        private Context ctx;
        private RecyclerView viewList;

        protected OnRecycleScrollListener(Context context, RecyclerView list) {
            this.ctx = context;
            this.viewList = list;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (!firstLoaded) {
//                NoticeUtil.showProgressDlg(ctx);
            }

            if (!viewList.canScrollVertically(1) && !isLoading && !loadFinish) {
                isLoading = true;
                onLoadMore();
            }

            if (!viewList.canScrollVertically(-1)) {
                onTop();
            }else {
                onNoStatus();
            }
        }

        public abstract void onLoadMore();

        public abstract void onTop();

        public abstract void onNoStatus();

        public void onOnceLoadComplete(boolean isLoadFinish) {
            firstLoaded = true;
            isLoading = false;
            loadFinish = isLoadFinish;
//            NoticeUtil.stopProgressDlg();
        }
    }
}
