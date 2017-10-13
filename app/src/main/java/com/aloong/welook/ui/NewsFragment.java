package com.aloong.welook.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aloong.welook.R;
import com.aloong.welook.entity.Comments;
import com.aloong.welook.entity.Result;
import com.aloong.welook.handler.PerferenceHepler;
import com.aloong.welook.handler.RequestCommentsToMeAPI;
import com.aloong.welook.handler.SQLHandler;
import com.aloong.welook.ui.adapter.CommentsAdapter;
import com.aloong.welook.utils.Tools;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by aloong on 2017/8/18.
 */

public class NewsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private List<Comments> mData = new ArrayList<>();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    public RecyclerView mFeedInfoRv;
    private FloatingActionButton mFAB;
    private CommentsAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        mFeedInfoRv = (RecyclerView) view.findViewById(R.id.news_info);
        mFeedInfoRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.news_srl);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_blue_light),
                getResources().getColor(android.R.color.holo_blue_dark));
        mFAB = (FloatingActionButton) view.findViewById(R.id.fab_news);
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    private void initData() {
        mAdapter = new CommentsAdapter(getContext());
        mFeedInfoRv.setAdapter(mAdapter);
        mFeedInfoRv.addOnScrollListener(new Tools.OnRecycleScrollListener(getContext(),mFeedInfoRv) {
            @Override
            public void onLoadMore() {

            }

            @Override
            public void onTop() {
                mSwipeRefreshLayout.setEnabled(true);
            }

            @Override
            public void onNoStatus() {
                mSwipeRefreshLayout.setEnabled(false);
            }
        });
//        mFeedInfoRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                if (!mFeedInfoRv.canScrollVertically(-1)){
//                    mSwipeRefreshLayout.setEnabled(true);
//                }else {
//                    mSwipeRefreshLayout.setEnabled(false);
//                }
//            }
//        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                mData = SQLHandler.getInstance().queryComments();
                mAdapter.setData(mData);
                getActivity().runOnUiThread(new UpdateListRunnable());
                try {
                    Result<List<Comments>> commentsList = RequestCommentsToMeAPI.requestComments
                            (PerferenceHepler.loadTruePwd(getContext()), 20);
                    mData = commentsList.getBody();
                    mAdapter.setData(mData);
                    getActivity().runOnUiThread(new UpdateListRunnable());
                    SQLHandler.getInstance().insertTableCommentsList(mData);
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

    @Override
    public void onRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isSuccess = false;
                try {
                    Result<List<Comments>> commentsList = RequestCommentsToMeAPI.requestComments
                            (PerferenceHepler.loadTruePwd(getContext()), 20);
                    mData = commentsList.getBody();
                    mAdapter.setData(mData);
                    SQLHandler.getInstance().insertTableCommentsList(mData);
                    isSuccess = commentsList.isSuccess();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                final boolean finalIsSuccess = isSuccess;
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (finalIsSuccess) {
                            mAdapter.notifyDataSetChanged();
                        } else {
                            Tools.toastS(getContext(), "网络异常，请稍后重试");
                        }
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }
}
