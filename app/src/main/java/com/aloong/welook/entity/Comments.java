package com.aloong.welook.entity;

import android.text.TextUtils;

import com.aloong.welook.utils.Tools;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by aloong on 2017/8/29.
 */

public class Comments {

    private static DateFormat DATA_FORMAT = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.US);
    public static final int TYPE_FIRST = 1;
    public static final int TYPE_SECOND = 2;

    private Date mTime;
    private String mSource;
    private String mComment;
    private User mCommenter;
    private Feed mStatus;
    private Feed mReplyComment;
    private int mType;
    private long mCommentId;

    public Comments() {
    }

    public static Comments fill(JSONObject jsonObject) {
        Comments comments = new Comments();
        if (jsonObject.has("id")){
            comments.setCommentId(jsonObject.optLong("id"));
        }
        if (jsonObject.has("reply_comment")) {
            comments.setType(TYPE_SECOND);
            comments.setReplyComment(Feed.fill(jsonObject.optJSONObject("reply_comment")));
        }
        if (jsonObject.has("created_at")) {
            String timeStr = jsonObject.optString("created_at");
            try {
                comments.setDate(DATA_FORMAT.parse(timeStr));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (jsonObject.has("text")) {
            comments.setComment(jsonObject.optString("text"));
        }
        if (jsonObject.has("source")) {
            String sourceStr = jsonObject.optString("source");
            if (!TextUtils.isEmpty(sourceStr)) {
                String source = sourceStr.substring(sourceStr.indexOf(">") + 1, sourceStr.lastIndexOf("<"));
                comments.setSource(source);
            }
        }
        if (jsonObject.has("user")) {
            comments.setCommenter(User.fill(jsonObject.optJSONObject("user")));
        }
        if (jsonObject.has("status")) {
            comments.setStatus(Feed.fill(jsonObject.optJSONObject("status")));
        }
        return comments;
    }

    public static List<Comments> fillList(JSONArray jsonArray) {
        List<Comments> CommentsList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            CommentsList.add(fill(jsonArray.optJSONObject(i)));
        }
        return CommentsList;
    }

    public Date getDate() {
        return mTime;
    }

    public Comments setDate(Date time) {
        mTime = time;
        return this;
    }

    public Feed getReplyComment() {
        return mReplyComment;
    }

    public Comments setReplyComment(Feed feed) {
        mReplyComment = feed;
        return this;
    }

    public String getSource() {
        return mSource;
    }

    public Comments setSource(String source) {
        mSource = source;
        return this;
    }

    public String getComment() {
        return mComment;
    }

    public Comments setComment(String comment) {
        mComment = comment;
        return this;
    }

    public User getCommenter() {
        return mCommenter;
    }

    public Comments setCommenter(User user) {
        mCommenter = user;
        return this;
    }

    public Feed getStatus() {
        return mStatus;
    }

    public Comments setStatus(Feed status) {
        mStatus = status;
        return this;
    }

    public int getType() {
        return mType;
    }

    public Comments setType(int type) {
        mType = type;
        return this;
    }

    public long getCommentId() {
        return mCommentId;
    }

    public Comments setCommentId(long CommentId) {
        mCommentId = CommentId;
        return this;
    }
}
