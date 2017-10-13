package com.aloong.welook.ui;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.CycleInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ListView;

import com.aloong.welook.R;
import com.aloong.welook.entity.Feed;
import com.aloong.welook.entity.Result;
import com.aloong.welook.handler.PerferenceHepler;
import com.aloong.welook.handler.RequestWeiBoAPI;
import com.aloong.welook.ui.adapter.FeedAdapter;
import com.aloong.welook.handler.SQLHandler;
import com.aloong.welook.utils.Tools;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by aloong on 2017/8/18.
 */

public class MainFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private List<Feed> mData = new ArrayList<>();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    public RecyclerView mFeedInfoRv;
    private FloatingActionButton mFAB;
    private FeedAdapter mAdapter;
    private Animation mRotateAnimation;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mFeedInfoRv = (RecyclerView) view.findViewById(R.id.feed_info);
        mFeedInfoRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.main_srl);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_blue_light),
                getResources().getColor(android.R.color.holo_blue_dark));
        mFAB = (FloatingActionButton) view.findViewById(R.id.fab_main);
        mRotateAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_fab_rotate);
        mRotateAnimation.setInterpolator(new LinearInterpolator());
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFAB.startAnimation(mRotateAnimation);
                onRefresh();
            }
        });
        initData();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isSuccess = false;
                try {
                    Result<List<Feed>> listWeiBoResult = RequestWeiBoAPI.requestFeed(PerferenceHepler.loadTruePwd(getContext()), 50);
                    // 批量插入
                    mData = listWeiBoResult.getBody();
                    mAdapter.setData(mData);
                    SQLHandler.getInstance().insertTableFeedList(mData);
                    isSuccess = listWeiBoResult.isSuccess();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                final boolean finalIsSuccess = isSuccess;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (finalIsSuccess) {
                            mAdapter.updateRequestDate(new Date());
                            mAdapter.notifyDataSetChanged();
                        } else {
                        }
                        mSwipeRefreshLayout.setRefreshing(false);
                        mRotateAnimation.cancel();
                    }
                });
            }
        }).start();
    }

    private void initData() {
        mAdapter = new FeedAdapter(getContext());
        mFeedInfoRv.setAdapter(mAdapter);
        new Thread(new Runnable() {
            @Override
            public void run() {
                mData = SQLHandler.getInstance().queryTableFeed();
                mAdapter.setData(mData);
                getActivity().runOnUiThread(new UpdateListRunnable());
                try {
                    Result<List<Feed>> listWeiBoResult =
                            RequestWeiBoAPI.requestFeed(PerferenceHepler.loadTruePwd(getContext()), 50);
                    mData = listWeiBoResult.getBody();
                    mAdapter.setData(mData);
                    getActivity().runOnUiThread(new UpdateListRunnable());
                    SQLHandler.getInstance().insertTableFeedList(mData);
                } catch (IOException | JSONException e) {
                    Tools.logD("网络异常，请稍后重试");
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private class UpdateListRunnable implements Runnable {

        @Override
        public void run() {
            mAdapter.notifyDataSetChanged();
        }
    }
}
