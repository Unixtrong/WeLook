package com.aloong.welook.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aloong.welook.R;
import com.aloong.welook.entity.Comments;
import com.aloong.welook.entity.User;
import com.aloong.welook.handler.PicLoader;
import com.aloong.welook.utils.Tools;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by aloong on 2017/8/29.
 */

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentsHolder> {
    private Context mContext;
    private List<Comments> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private LruCache<String, Bitmap> mLruCache;


    private static final DateFormat DATA_FORMAT = new SimpleDateFormat("yy-MM-dd HH:mm", Locale.getDefault());

    public CommentsAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mLruCache = new LruCache<>(Tools.calculateMemoryCacheSize(mContext));
    }


    public void setData(List<Comments> data){
        mData.clear();
        mData.addAll(data);
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).getType();
    }

    @Override
    public CommentsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case Comments.TYPE_FIRST:
                return new FirstCommentHolder(mInflater.inflate(R.layout.comments_info_first_item, parent, false));
            case Comments.TYPE_SECOND:
                return new SecondCommentHolder(mInflater.inflate(R.layout.comments_info_second_item, parent, false));
            default:
                return new FirstCommentHolder(mInflater.inflate(R.layout.comments_info_first_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(CommentsAdapter.CommentsHolder holder, int position) {
        holder.bind(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    private String getDisplayTime(Comments comment) {
        String displayTime;
        // 显示xx年xx月几点几分
        displayTime = DATA_FORMAT.format(comment.getDate());
        return displayTime;
    }

    private void handleHeadImage(final ImageView imageView, User user) {
        final String headUrl = user.getUserHeadUrl().replace(".50/", ".120/");
        final String key = headUrl.replace(".jpg", "tttt").replace("/", "tttt");
        Bitmap bitmap = PicLoader.decodeBitmap(mContext, headUrl,120,120);
        // 判断内存里是否存在头像，需要使用 LruCache 缓存 Bitmap
        if (mLruCache.get(key) == null) {
            // 内存不存在时，判断本地文件里是否存在
            if (bitmap == null) {
                // 本地文件不存在时，通过线程下载远端图片文件
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            File file = PicLoader.downloadBitmap(mContext, headUrl);
                            if (file != null && file.exists()) {
                                Bitmap bitmap1 = PicLoader.decodeBitmap(mContext, headUrl,120,120);
                                if (bitmap1 != null) {
                                    mLruCache.put(key, bitmap1);
                                    bindBitmap(imageView, mLruCache.get(key));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            } else {
                mLruCache.put(key, bitmap);
                bindBitmap(imageView, mLruCache.get(key));
            }
        } else {
            bindBitmap(imageView, mLruCache.get(key));
        }
    }

    private void bindBitmap(final ImageView iv, final Bitmap bitmap) {
        if (bitmap != null) {
            if (mContext instanceof Activity) {
                Activity activity = (Activity) mContext;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iv.setImageBitmap(bitmap);
                    }
                });
            }
        }
    }

    abstract class CommentsHolder extends RecyclerView.ViewHolder {
        private ImageView mIvHead;
        private TextView mTvCommenterName;
        private TextView mTime;
        private TextView mSource;
        private TextView mReply;
        private TextView mComment;

        CommentsHolder(View itemView) {
            super(itemView);
            mIvHead = (ImageView) itemView.findViewById(R.id.comment_info_item_head);
            mTvCommenterName = (TextView) itemView.findViewById(R.id.comment_info_item_name);
            mTime = (TextView) itemView.findViewById(R.id.comment_info_item_time);
            mSource = (TextView) itemView.findViewById(R.id.comment_info_item_source);
            mComment = (TextView) itemView.findViewById(R.id.comment_info_item_text);
            mReply = (TextView) itemView.findViewById(R.id.comment_info_item_reply);
        }

        void bind(Comments comment) {
            handleHeadImage(mIvHead, comment.getCommenter());
            mTvCommenterName.setText(comment.getCommenter().getUserName());
            mTime.setText(getDisplayTime(comment));
            String source = comment.getSource();
            if (!TextUtils.isEmpty(source)) {
                mSource.setText(source);
            } else {
                mSource.setText("");
            }
            mComment.setText(comment.getComment());
            mReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mReply.setBackgroundColor(Color.rgb(230, 230, 230));
                }
            });
        }

    }

    private class FirstCommentHolder extends CommentsHolder {
        private ImageView mHead;
        private TextView mName;
        private TextView mEssay;

        FirstCommentHolder(View itemView) {
            super(itemView);
            mHead = (ImageView) itemView.findViewById(R.id.first_item_head);
            mName = (TextView) itemView.findViewById(R.id.first_item_name);
            mEssay = (TextView) itemView.findViewById(R.id.first_item_essay);
        }

        @Override
        void bind(Comments comment) {
            super.bind(comment);
            if (comment.getStatus().getSinglePicUrl() != null) {
                handleSinglePic(mHead,comment);
            } else {
                handleHeadImage(mHead, comment.getStatus().getUser());
            }
            mName.setText(comment.getStatus().getUser().getUserName());
            mEssay.setText(comment.getStatus().getEssay());
        }
    }

    private class SecondCommentHolder extends CommentsHolder {
        private ImageView mHead;
        private TextView mName;
        private TextView mEssay;
        private TextView mReplyCommenterNameText;

        SecondCommentHolder(View itemView) {
            super(itemView);
            mHead = (ImageView) itemView.findViewById(R.id.first_item_head);
            mName = (TextView) itemView.findViewById(R.id.first_item_name);
            mEssay = (TextView) itemView.findViewById(R.id.first_item_essay);
            mReplyCommenterNameText = (TextView) itemView.findViewById(R.id.second_replyCommenterName_text);
        }

        @Override
        void bind(Comments comment) {
            super.bind(comment);
            if (comment.getStatus().getSinglePicUrl() != null) {
                handleSinglePic(mHead, comment);
            } else {
                handleHeadImage(mHead, comment.getStatus().getUser());
            }
            mName.setText("@"+comment.getStatus().getUser().getUserName());
            mEssay.setText(comment.getStatus().getEssay());
            mReplyCommenterNameText.setText("@" + comment.getReplyComment().getUser().getUserName()+":" + comment.getReplyComment().getEssay());
        }
    }
        private void handleSinglePic(final ImageView iv, Comments comments) {
            iv.setImageBitmap(null);
            final String singlePicUrl = comments.getStatus().getSinglePicUrl().replace("thumbnail", "bmiddle");
            final String key = singlePicUrl.replace(".jpg", "9999").replace("/", "1111");
            final Bitmap bitmap = PicLoader.decodeBitmap(mContext, singlePicUrl,500,500);
            if (mLruCache.get(key) == null) {
                if (bitmap == null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                File file = PicLoader.downloadBitmap(mContext, singlePicUrl);
                                if (file != null && file.exists()) {
                                    Bitmap bitmap1 = PicLoader.decodeBitmap(mContext, singlePicUrl,500,500);
                                    if (bitmap1 != null) {
                                        mLruCache.put(key, bitmap1);
                                        bindBitmap(iv, mLruCache.get(key));
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } else {
                    mLruCache.put(key, bitmap);
                    bindBitmap(iv, mLruCache.get(key));
                }
            } else {
                bindBitmap(iv, mLruCache.get(key));
            }
        }
}
