package com.aloong.welook.entity;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by aloong on 2017/8/5.
 */

public class Feed {

    private static DateFormat DATA_FORMAT = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.US);

    public static final int TYPE_TEXT = 0;
    public static final int TYPE_SINGLEPIC = 1;
    public static final int TYPE_MULTPICS = 2;
    public static final int TYPE_RETWEETTEXT = 3;
    public static final int TYPE_RETWEETSINGLEPIC = 4;
    public static final int TYPE_RETWEETMULTPICS = 5;
    private int mType;
    private long mFeedId;
    private long mUserId;
    private long mRetweetId;
    private User mUser;
    private String mEssay;
    private String[] mPicsUrl;
    private Date mTime;
    private Feed mFeedRetweet;
    private String mSource;
    private int mRepostsCount;
    private int mCommentsCount;
    private int mAttitudesCount;
    private String mSinglePicUrl;

    public static Feed fill(JSONObject json) {
        Feed feed = new Feed();
        if (json.has("id")) {
            feed.setFeedId(json.optLong("id"));
        }
        if (json.has("bmiddle_pic")) {
            feed.setSinglePicUrl(json.optString("bmiddle_pic"));
        }
        if (json.has("source")) {
            String sourceStr = json.optString("source");
            if (!TextUtils.isEmpty(sourceStr)) {
                String source = sourceStr.substring(sourceStr.indexOf(">") + 1, sourceStr.lastIndexOf("<"));
                feed.setFeedSource(source);
            }
        }
        if (json.has("reposts_count")) {
            feed.setRepostsCount(json.optInt("reposts_count"));
        }
        if (json.has("comments_count")) {
            feed.setCommentsCount(json.optInt("comments_count"));
        }
        if (json.has("attitudes_count")) {
            feed.setAttitudesCount(json.optInt("attitudes_count"));
        }
        if (json.has("user")) {
            feed.setUser(User.fill(json.optJSONObject("user")));
            feed.setUserId(feed.getUser().getUserId());
        }
        if (json.has("text")) {
            feed.setEssay(json.optString("text"));
        }
        if (json.has("created_at")) {
            String creatAt = json.optString("created_at");
            try {
                feed.setDate(DATA_FORMAT.parse(creatAt));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (json.has("pic_urls")) {
            JSONArray jsonArray = json.optJSONArray("pic_urls");
            String[] pics = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                pics[i] = jsonArray.optJSONObject(i).optString("thumbnail_pic");
            }
            if (pics.length != 0) {
                feed.setPicsUrl(pics);
                if (pics.length== 1) {
                    feed.setType(TYPE_SINGLEPIC);
                } else {
                    feed.setType(TYPE_MULTPICS);
                }
            }
        } else {
            feed.setType(TYPE_TEXT);
        }
        if (json.has("retweeted_status")) {
            Feed retweet = Feed.fill(json.optJSONObject("retweeted_status"));
            feed.setRetweet(retweet);
            feed.setRetweetId(feed.getRetweet().getFeedId());
            if (retweet.getType() == TYPE_SINGLEPIC) {
                feed.setType(TYPE_RETWEETSINGLEPIC);
            } else {
                feed.setType(TYPE_RETWEETTEXT);
            }
            if (retweet.getType() == TYPE_MULTPICS) {
                feed.setType(TYPE_RETWEETMULTPICS);
            } else {
                feed.setType(TYPE_RETWEETTEXT);
            }
        } else {
        }
        return feed;
    }

    public static List<Feed> fillList(JSONArray jsonArray) {
        List<Feed> feedList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            feedList.add(fill(jsonArray.optJSONObject(i)));
        }
        return feedList;
    }

    public User getUser() {
        return mUser;
    }

    public Feed setUser(User user) {
        mUser = user;
        return this;
    }

    public String getEssay() {
        return mEssay;
    }

    public Feed setEssay(String news) {
        mEssay = news;
        return this;
    }

    public Date getDate() {
        return mTime;
    }

    public Feed setDate(Date time) {
        mTime = time;
        return this;
    }

    public Feed getRetweet() {
        return mFeedRetweet;
    }

    public Feed setRetweet(Feed retweet) {
        mFeedRetweet = retweet;
        return this;
    }

    public long getFeedId() {
        return mFeedId;
    }

    public Feed setFeedId(long id) {
        mFeedId = id;
        return this;
    }

    public String getFeedSource() {
        return mSource;
    }

    public Feed setFeedSource(String source) {
        mSource = source;
        return this;
    }

    public int getRepostsCount() {
        return mRepostsCount;
    }

    public Feed setRepostsCount(int count) {
        mRepostsCount = count;
        return this;
    }

    public int getCommentsCount() {
        return mCommentsCount;
    }

    public Feed setCommentsCount(int count) {
        mCommentsCount = count;
        return this;
    }

    public int getAttitudesCount() {
        return mAttitudesCount;
    }

    public Feed setAttitudesCount(int count) {
        mAttitudesCount = count;
        return this;
    }

    public String[] getPicsUrl() {
        return mPicsUrl;
    }

    public Feed setPicsUrl(String[] pics) {
        mPicsUrl = pics;
        return this;
    }

    public int getType() {
        return mType;
    }

    public Feed setType(int type) {
        mType = type;
        return this;
    }

    public String getSinglePicUrl() {
        return mSinglePicUrl;
    }

    public Feed setSinglePicUrl(String url) {
        mSinglePicUrl = url;
        return this;
    }

    public long getUserId() {
        return mUserId;
    }

    public Feed setUserId(long mUserId) {
        mUserId = mUserId;
        return this;
    }

    public long getRetweetId() {
        return mRetweetId;
    }

    public Feed setRetweetId(long mRetweetId) {
        mRetweetId = mRetweetId;
        return this;
    }
}
