package com.aloong.welook.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Parcel;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.ParcelableSpan;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aloong.welook.R;
import com.aloong.welook.entity.Feed;
import com.aloong.welook.entity.User;
import com.aloong.welook.ui.LargePicActivity;
import com.aloong.welook.handler.PicLoader;
import com.aloong.welook.ui.SingleLargePicActivity;
import com.aloong.welook.utils.Tools;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by aloong on 2017/8/5.
 */

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.WLHolder> {

    private Context mContext;
    private List<Feed> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private int mWelookColor;

    private Date mRequestDate;
    private static final DateFormat DATA_FORMAT = new SimpleDateFormat("yy-MM-dd HH:mm", Locale.getDefault());
    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public void updateRequestDate(Date date) {
        mRequestDate = date;
    }

    public FeedAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mRequestDate = new Date();
    }

    public void setData(List<Feed> data) {
        mData.clear();
        mData.addAll(data);
    }

    @Override
    public int getItemViewType(int position) {
        Feed feed = mData.get(position);
        return feed.getType();
    }

    @Override
    public WLHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Tools.logD("viewTypr: = " + viewType);
        switch (viewType) {
            case Feed.TYPE_RETWEETTEXT:
                return new TextHolder(mInflater.inflate(R.layout.feed_info_retweettext_item, parent, false));
            case Feed.TYPE_SINGLEPIC:
                return new SinglePicHolder(mInflater.inflate(R.layout.feed_info_singlepic_item, parent, false));
            case Feed.TYPE_MULTPICS:
                return new MultPicsHolder(mInflater.inflate(R.layout.feed_info_multipics_item, parent, false));
            case Feed.TYPE_RETWEETSINGLEPIC:
                return new RetweetSinglePicHolder(mInflater.inflate(R.layout.feed_info_retweet_singlepic_item, parent, false));
            case Feed.TYPE_RETWEETMULTPICS:
                return new RetweetMultPicsHolder(mInflater.inflate(R.layout.feed_info_retweet_multpics_item, parent, false));
            default:
                return new TextHolder(mInflater.inflate(R.layout.feed_info_retweettext_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(WLHolder holder, int position) {
        holder.bind(mData.get(position));
    }

    private String getDisplayTime(Feed feed) {
        //微博发布时间
        long feedTime = feed.getDate().getTime();
        //用户最后一次请求时间和微博发布时间的间隔
        long timeDiff = mRequestDate.getTime() - feedTime;
        //用户最后一次请求的时间对应的天数
        long requestDay = (mRequestDate.getTime() + TimeUnit.HOURS.toMillis(8)) / TimeUnit.DAYS.toMillis(1);
        //微博发布时间的对应天数
        long feedDay = (feedTime + TimeUnit.HOURS.toMillis(8)) / TimeUnit.DAYS.toMillis(1);

        String displayTime;
        if (requestDay == feedDay) {
            //当天发的微博
            if (timeDiff < TimeUnit.MINUTES.toMillis(1)) {
                //请求时间和微博发布时间小于1分钟 显示xx秒前
                displayTime = TimeUnit.MILLISECONDS.toSeconds(timeDiff) + "秒前";
            } else {
                //请求时间和微博发布时间大于1分钟小于一小时 显示xx分钟前
                if (timeDiff < TimeUnit.HOURS.toMillis(1)) {
                    displayTime = TimeUnit.MILLISECONDS.toMinutes(timeDiff) + "分钟前";
                } else {
                    //请求时间和微博发布时间超过一小时 显示xx小时：xx分钟
                    displayTime = TIME_FORMAT.format(feed.getDate());
                }
            }

        } else {
            if (requestDay - feedDay == 1) {
                //请求时间跟微博发布时间隔了一天 显示昨天几点几分
                displayTime = "昨天" + TIME_FORMAT.format(feed.getDate());
            } else if (requestDay - feedDay == 2) {
                //请求时间跟微博发布时间隔了两天 显示前天几点几分
                displayTime = "前天" + TIME_FORMAT.format(feed.getDate());
            } else {
                //请求时间跟微博发布时间隔了超过2天 显示xx年xx月几点几分
                displayTime = DATA_FORMAT.format(feed.getDate());
            }
        }
        return displayTime;
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    abstract class WLHolder extends RecyclerView.ViewHolder {
        ImageView mIvHead;
        TextView mTvName;
        TextView mTvTime;
        TextView mTvEssay;
        TextView mTvSource;
        TextView mTvRepost;
        TextView mTvComment;
        TextView mTvAttited;

        WLHolder(View itemView) {
            super(itemView);
            mIvHead = (ImageView) itemView.findViewById(R.id.feed_info_item_head);
            mTvName = (TextView) itemView.findViewById(R.id.feed_info_item_name);
            mTvTime = (TextView) itemView.findViewById(R.id.feed_info_item_time);
            mTvEssay = (TextView) itemView.findViewById(R.id.feed_info_item_essay);
            mTvSource = (TextView) itemView.findViewById(R.id.feed_info_item_source);
            mTvAttited = (TextView) itemView.findViewById(R.id.feed_info_item_attitudes);
            mTvComment = (TextView) itemView.findViewById(R.id.feed_info_item_comments);
            mTvRepost = (TextView) itemView.findViewById(R.id.feed_info_item_repost);
        }

        void bind(final Feed feed) {
            handleHeadImage(mIvHead, feed.getUser());
            String name = feed.getUser().getUserName();
            mTvName.setText(name);
            String displayTime = getDisplayTime(feed);
            mTvTime.setText(displayTime);
            String essay = feed.getEssay();
            regex(mTvEssay, essay);
            String source = feed.getFeedSource();
            if (!TextUtils.isEmpty(source)) {
                mTvSource.setText(source);
            } else {
                mTvSource.setText("");
            }
            int attitude = feed.getAttitudesCount();
            mTvAttited.setText(attitude + "");
            int comments = feed.getCommentsCount();
            mTvComment.setText(comments + "");
            int reposts = feed.getRepostsCount();
            mTvRepost.setText(reposts + "");
            mIvHead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    click(v, feed.getUser().getUserHeadUrl().replace(".50/", ".720/"));
                }

                private void click(View v, String url) {
                    LayoutInflater inflater = LayoutInflater.from(mContext);
                    LinearLayout root = new LinearLayout(mContext);
                    View popupWindowView = inflater.inflate(R.layout.feed_info_showbigmap, root);

                    ImageView imageView = (ImageView) popupWindowView.findViewById(R.id.showbigmap_iv);
                    int MATCH = LinearLayout.LayoutParams.MATCH_PARENT;
                    final PopupWindow popupWindow = new PopupWindow(popupWindowView, MATCH, MATCH);
                    popupWindow.setOutsideTouchable(true);
                    popupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
                    popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
                    handleBigPic(imageView, url);
                    popupWindowView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                        }
                    });
                }
            });
        }
    }

    private class TextHolder extends WLHolder {

        LinearLayout mRetweetLayout;
        TextView mTvRetweetUserName;
        TextView mTvRetweetEssay;


        TextHolder(View itemView) {
            super(itemView);
            mRetweetLayout = (LinearLayout) itemView.findViewById(R.id.retweetLayout);
            mTvRetweetUserName = (TextView) itemView.findViewById(R.id.retweet_user_name);
            mTvRetweetEssay = (TextView) itemView.findViewById(R.id.retweet_user_essay);
        }

        void bind(Feed feed) {
            super.bind(feed);
            if (feed.getRetweet() != null) {
                mRetweetLayout.setVisibility(View.VISIBLE);
                if (feed.getRetweet().getUser() != null) {
                    if (feed.getRetweet().getUser().getUserName() != null) {
                        mTvRetweetUserName.setVisibility(View.VISIBLE);
                        mTvRetweetUserName.setText(mContext.getString(R.string.retweet_username,
                                feed.getRetweet().getUser().getUserName()));
                    } else {
                        mTvRetweetUserName.setVisibility(View.GONE);
                    }
                }
                if (feed.getRetweet().getEssay() != null) {
                    mTvRetweetEssay.setVisibility(View.VISIBLE);
//                    mTvRetweetEssay.setText(feed.getRetweet().getEssay());
                    regex(mTvRetweetEssay, feed.getRetweet().getEssay());
                } else {
                    mTvRetweetEssay.setVisibility(View.GONE);
                }
            } else {
                mRetweetLayout.setVisibility(View.GONE);
            }
        }
    }

    private class SinglePicHolder extends WLHolder {
        ImageView mSinglePicIv;

        SinglePicHolder(View itemView) {
            super(itemView);
            mSinglePicIv = (ImageView) itemView.findViewById(R.id.feed_info_singlePicIv);
        }

        void bind(final Feed feed) {
            super.bind(feed);
            try {
                handleSinglePic(mSinglePicIv, feed);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mSinglePicIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("url", feed.getPicsUrl()[0]);
                    intent.setClass(mContext, SingleLargePicActivity.class);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    private class RetweetSinglePicHolder extends WLHolder {
        TextView mTvRetweetUserName;
        TextView mTvRetweetEssay;
        ImageView mSinglePic;

        RetweetSinglePicHolder(View itemView) {
            super(itemView);
            mSinglePic = (ImageView) itemView.findViewById(R.id.feed_info_singlePicIv);
            mTvRetweetUserName = (TextView) itemView.findViewById(R.id.retweet_user_name);
            mTvRetweetEssay = (TextView) itemView.findViewById(R.id.retweet_user_essay);
        }

        void bind(final Feed feed) {
            super.bind(feed);
            if (feed.getRetweet() != null) {
                if (feed.getRetweet().getUser().getUserName() != null) {
                    mTvRetweetUserName.setVisibility(View.VISIBLE);
                    mTvRetweetUserName.setText(mContext.getString(R.string.retweet_username,
                            feed.getRetweet().getUser().getUserName()));
                } else {
                    mTvRetweetUserName.setVisibility(View.GONE);
                }
                if (feed.getRetweet().getEssay() != null) {
                    mTvRetweetEssay.setVisibility(View.VISIBLE);
                    regex(mTvRetweetEssay, feed.getRetweet().getEssay());
                } else {
                    mTvRetweetEssay.setVisibility(View.GONE);
                }
            }
            Tools.logD("转发单图的地址： = " + feed.getPicsUrl()[0]);
            try {
                handleSinglePic(mSinglePic, feed);
                mSinglePic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.putExtra("url", feed.getPicsUrl()[0]);
                        intent.setClass(mContext, SingleLargePicActivity.class);
                        mContext.startActivity(intent);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class MultPicsHolder extends WLHolder {
        LinearLayout mFirstLineLL;
        ImageView mMultPicsOne;
        ImageView mMultPicsTwo;
        ImageView mMultPicsThree;
        LinearLayout mSecondLineLL;
        ImageView mMultPicsFour;
        ImageView mMultPicsFive;
        ImageView mMultPicsSix;
        LinearLayout mThirdineLL;
        ImageView mMultPicsSeven;
        ImageView mMultPicsEight;
        ImageView mMultPicsNine;

        MultPicsHolder(View itemView) {
            super(itemView);
            mFirstLineLL = (LinearLayout) itemView.findViewById(R.id.multpics_iv_linera_01);
            mMultPicsOne = (ImageView) itemView.findViewById(R.id.multpics_iv_01);
            mMultPicsTwo = (ImageView) itemView.findViewById(R.id.multpics_iv_02);
            mMultPicsThree = (ImageView) itemView.findViewById(R.id.multpics_iv_03);
            mSecondLineLL = (LinearLayout) itemView.findViewById(R.id.multpics_iv_linera_02);
            mMultPicsFour = (ImageView) itemView.findViewById(R.id.multpics_iv_04);
            mMultPicsFive = (ImageView) itemView.findViewById(R.id.multpics_iv_05);
            mMultPicsSix = (ImageView) itemView.findViewById(R.id.multpics_iv_06);
            mThirdineLL = (LinearLayout) itemView.findViewById(R.id.multpics_iv_linera_03);
            mMultPicsSeven = (ImageView) itemView.findViewById(R.id.multpics_iv_07);
            mMultPicsEight = (ImageView) itemView.findViewById(R.id.multpics_iv_08);
            mMultPicsNine = (ImageView) itemView.findViewById(R.id.multpics_iv_09);
        }

        void bind(final Feed feed) {
            super.bind(feed);
            final String[] pics = feed.getPicsUrl();
            ImageView a[] = {mMultPicsOne, mMultPicsTwo, mMultPicsThree, mMultPicsFour, mMultPicsFive,
                    mMultPicsSix, mMultPicsSeven, mMultPicsEight, mMultPicsNine};
            for (int j = 0; j < 9; j++) {
                a[j].setVisibility(View.GONE);
            }
            for (int i = 0; i < pics.length; i++) {
                String pic = pics[i];
                ImageView imageView = a[i];
                imageView.setVisibility(View.VISIBLE);
                handleMultPics(imageView, pic);
                final int finalI = i;
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.putExtra("url", pics);
                        intent.putExtra("position", finalI);
                        intent.setClass(mContext, LargePicActivity.class);
                        mContext.startActivity(intent);
                    }
                });
            }
        }

    }

    private class RetweetMultPicsHolder extends WLHolder {
        TextView mTvRetweetUserName;
        TextView mTvRetweetEssay;
        LinearLayout mFirstLineLL;
        ImageView mMultPicsOne;
        ImageView mMultPicsTwo;
        ImageView mMultPicsThree;
        LinearLayout mSecondLineLL;
        ImageView mMultPicsFour;
        ImageView mMultPicsFive;
        ImageView mMultPicsSix;
        LinearLayout mThirdineLL;
        ImageView mMultPicsSeven;
        ImageView mMultPicsEight;
        ImageView mMultPicsNine;

        RetweetMultPicsHolder(View itemView) {
            super(itemView);
            mTvRetweetUserName = (TextView) itemView.findViewById(R.id.retweet_user_name);
            mTvRetweetEssay = (TextView) itemView.findViewById(R.id.retweet_user_essay);
            mFirstLineLL = (LinearLayout) itemView.findViewById(R.id.multpics_iv_linera_01);
            mMultPicsOne = (ImageView) itemView.findViewById(R.id.multpics_iv_01);
            mMultPicsTwo = (ImageView) itemView.findViewById(R.id.multpics_iv_02);
            mMultPicsThree = (ImageView) itemView.findViewById(R.id.multpics_iv_03);
            mSecondLineLL = (LinearLayout) itemView.findViewById(R.id.multpics_iv_linera_02);
            mMultPicsFour = (ImageView) itemView.findViewById(R.id.multpics_iv_04);
            mMultPicsFive = (ImageView) itemView.findViewById(R.id.multpics_iv_05);
            mMultPicsSix = (ImageView) itemView.findViewById(R.id.multpics_iv_06);
            mThirdineLL = (LinearLayout) itemView.findViewById(R.id.multpics_iv_linera_03);
            mMultPicsSeven = (ImageView) itemView.findViewById(R.id.multpics_iv_07);
            mMultPicsEight = (ImageView) itemView.findViewById(R.id.multpics_iv_08);
            mMultPicsNine = (ImageView) itemView.findViewById(R.id.multpics_iv_09);
        }

        void bind(final Feed feed) {
            super.bind(feed);
            if (feed.getRetweet() != null) {
                if (feed.getRetweet().getUser().getUserName() != null) {
                    mTvRetweetUserName.setVisibility(View.VISIBLE);
                    mTvRetweetUserName.setText(mContext.getString(R.string.retweet_username,
                            feed.getRetweet().getUser().getUserName()));
                } else {
                    mTvRetweetUserName.setVisibility(View.GONE);
                }
                if (feed.getRetweet().getEssay() != null) {
                    mTvRetweetEssay.setVisibility(View.VISIBLE);
                    // mTvRetweetEssay.setText(feed.getRetweet().getEssay());
                    regex(mTvRetweetEssay, feed.getRetweet().getEssay());
                } else {
                    mTvRetweetEssay.setVisibility(View.GONE);
                }
            }
            final String[] pics = feed.getRetweet().getPicsUrl();
            ImageView iv[] = {mMultPicsOne, mMultPicsTwo, mMultPicsThree, mMultPicsFour, mMultPicsFive,
                    mMultPicsSix, mMultPicsSeven, mMultPicsEight, mMultPicsNine};
            if (pics.length == 9) {
                for (int i = 0; i < pics.length; i++) {
                    String pic = pics[i];
                    ImageView imageView = iv[i];
                    imageView.setVisibility(View.VISIBLE);
                    handleMultPics(imageView, pic);
                    final int finalI = i;
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.putExtra("url", pics);
                            intent.putExtra("position", finalI);
                            intent.setClass(mContext, LargePicActivity.class);
                            mContext.startActivity(intent);
                        }
                    });
                }
            } else {
                for (int j = pics.length; j < 9; j++) {
                    iv[j].setVisibility(View.GONE);
                }
                for (int i = 0; i < pics.length; i++) {
                    String pic = pics[i];
                    ImageView imageView = iv[i];
                    imageView.setVisibility(View.VISIBLE);
                    handleMultPics(imageView, pic);
                    final int finalI = i;
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.putExtra("url", pics);
                            intent.putExtra("position", finalI);
                            intent.setClass(mContext, LargePicActivity.class);
                            mContext.startActivity(intent);
                        }
                    });
                }
            }
        }


    }

    private void regex(TextView tv, String originText) {
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        mWelookColor = ContextCompat.getColor(mContext, R.color.welookColor);
        Pattern pattern = Pattern.compile("(#[\\w ]+#)|(https?://[\\w./?=&]+)|(@\\w+)");
        Matcher matcher = pattern.matcher(originText);
        SpannableStringBuilder builder = new SpannableStringBuilder(originText);
        while (matcher.find()) {
            String topic = matcher.group(1);
            if (topic != null) {
                ForegroundColorSpan colorSpan = new ForegroundColorSpan(mWelookColor);
                builder.setSpan(colorSpan, matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            String url = matcher.group(2);
            if (url != null) {
                String displayUrl = "网页链接";
//                ForegroundColorSpan colorSpan = new ForegroundColorSpan(mWelookColor);
                WeLookUrlSpan urlSpan = new WeLookUrlSpan(url);
                builder.setSpan(urlSpan, matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            }
            String userName = matcher.group(3);
            if (userName != null) {
                ForegroundColorSpan colorSpan = new ForegroundColorSpan(mWelookColor);
                builder.setSpan(colorSpan, matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv.setText(builder);
            }
        }
        tv.setText(builder);
    }

    private class WeLookUrlSpan extends URLSpan implements ParcelableSpan {

        WeLookUrlSpan(String url) {
            super(url);
        }

        WeLookUrlSpan(Parcel src) {
            super(src);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
//            super.updateDrawState(ds);
            ds.setColor(mWelookColor);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
        }

        @Override
        public int describeContents() {
            return super.describeContents();
        }

        public final Creator<WeLookUrlSpan> CREATOR = new Creator<WeLookUrlSpan>() {
            @Override
            public WeLookUrlSpan createFromParcel(Parcel in) {
                return new WeLookUrlSpan(in);
            }

            @Override
            public WeLookUrlSpan[] newArray(int size) {
                return new WeLookUrlSpan[size];
            }
        };
    }

    private void handleHeadImage(ImageView imageView, User user) {
        String headUrl = user.getUserHeadUrl().replace(".50/", ".120/");
        Tools.logD("头像地址：=" + headUrl);
        int customHeight = 50;
        int customWidth = 50;
        PicLoader.getInstance().handleBitmap(imageView, headUrl, mContext, customHeight, customWidth);
    }

    private void handleBigPic(ImageView iv, String url) {
        iv.setBackgroundColor(Color.parseColor("#e6e6e6"));
        iv.setImageResource(0);
        final String singlePicUrl = url.replace("thumbnail", "large");
        PicLoader.getInstance().handleBitmap(iv, singlePicUrl, mContext, 1080, 1080);
    }

    private void handleSinglePic(ImageView iv, Feed feed) {
        iv.setBackgroundColor(Color.parseColor("#e6e6e6"));
        iv.setImageResource(0);
        final String singlePicUrl = feed.getPicsUrl()[0].replace("thumbnail", "wap720");
        PicLoader.getInstance().handleBitmap(iv, singlePicUrl, mContext, 900, 900);
    }

    private void handleMultPics(ImageView iv, String picurl) {
        iv.setBackgroundColor(Color.parseColor("#e6e6e6"));
        iv.setImageResource(0);
        final String singlePicUrl = picurl.replaceFirst("thumbnail", "bmiddle");
        PicLoader.getInstance().handleBitmap(iv, singlePicUrl, mContext, 500, 500);
    }

}

