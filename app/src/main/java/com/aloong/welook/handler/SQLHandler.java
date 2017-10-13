package com.aloong.welook.handler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.aloong.welook.Dao.DaoHelper;
import com.aloong.welook.consts.TableComments;
import com.aloong.welook.consts.TableFeed;
import com.aloong.welook.consts.TableUser;
import com.aloong.welook.entity.Comments;
import com.aloong.welook.entity.Feed;
import com.aloong.welook.entity.User;
import com.aloong.welook.utils.Tools;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by aloong on 2017/9/6.
 */

public class SQLHandler {
    private SQLiteDatabase mRb;
    private SQLiteDatabase mWb;
    private static SQLHandler sSQLHandler;


    public static SQLHandler getInstance() {
        return sSQLHandler;
    }

    private SQLHandler(Context context) {
        DaoHelper daoHelper = new DaoHelper(context);
        mRb = daoHelper.getReadableDatabase();
        mWb = daoHelper.getWritableDatabase();
    }

    public static void init(Context context) {
        sSQLHandler = new SQLHandler(context);
    }

    private void insertTableComments(Comments comments) {
        ContentValues cv = new ContentValues();
        cv.put(TableComments.COL_USER_ID, comments.getCommenter().getUserId());
        insertTableUser(comments.getCommenter());
        cv.put(TableComments.COL_CREATEDAT, comments.getDate().getTime());
        cv.put(TableComments.COL_SOURCE, comments.getSource());
        if (comments.getReplyComment() != null) {
            cv.put(TableComments.COL_REPLY, comments.getReplyComment().getFeedId());
            insertTableFeed(comments.getReplyComment(), 1);
        }
        cv.put(TableComments.COL_TEXT, comments.getComment());
        cv.put(TableComments.COL_STATUS, comments.getStatus().getFeedId());
        insertTableFeed(comments.getStatus(), 1);
        cv.put(TableComments.COL_COMMENT_ID, comments.getCommentId());
        mWb.insert(TableComments.TABLE_NAME, null, cv);
    }

    public void insertTableCommentsList(List<Comments> commentsList) {
        if (commentsList != null) {
            for (int i = 0; i < commentsList.size(); i++) {
                insertTableComments(commentsList.get(i));
            }
        }
    }

    private void insertTableUser(User user) {
        ContentValues cv = new ContentValues();
        cv.put(TableUser.COL_USERID, user.getUserId());
        cv.put(TableUser.COL_NAME, user.getUserName());
        cv.put(TableUser.COL_HEADURL, user.getUserHeadUrl());
        mWb.insert(TableUser.TABLE_NAME, null, cv);
    }

    private void insertTableFeed(Feed feed, int isRetweet) {
        ContentValues cv = new ContentValues();
        cv.put(TableFeed.COL_FEED_ID, feed.getFeedId());
        if (feed.getUser() != null) {
            cv.put(TableFeed.COL_USER_ID, feed.getUser().getUserId());
            insertTableUser(feed.getUser());
        }
        cv.put(TableFeed.COL_TEXT, feed.getEssay());
        cv.put(TableFeed.COL_SOURCE, feed.getFeedSource());
        if (feed.getPicsUrl() != null) {
            String pics = feed.getPicsUrl()[0];
            for (int i = 1; i < feed.getPicsUrl().length; i++) {
                pics = pics + "," + feed.getPicsUrl()[i];
            }
            cv.put(TableFeed.COL_PICS, pics);
        }
        cv.put(TableFeed.COL_CREATEDAT, feed.getDate().getTime());
        cv.put(TableFeed.COL_REPOSTS, feed.getRepostsCount());
        cv.put(TableFeed.COL_COMMENTS, feed.getCommentsCount());
        cv.put(TableFeed.COL_ATTITUDES, feed.getAttitudesCount());
        cv.put(TableFeed.COL_SINGLEPIC, feed.getSinglePicUrl());
        cv.put(TableFeed.COL_TYPE, feed.getType());
        cv.put(TableFeed.COL_IS_RETWEETED, isRetweet);
        Feed retweet = feed.getRetweet();
        if (retweet != null) {
            cv.put(TableFeed.COL_RETWEETED_ID, retweet.getFeedId());
            insertTableFeed(retweet, 1);
        }
        mWb.insert(TableFeed.TABLE_NAME, null, cv);
    }

    public void insertTableFeedList(List<Feed> feedList) {
        for (int i = 0; i < feedList.size(); i++) {
            insertTableFeed(feedList.get(i), 0);
        }
    }

    private Feed curToFeed(Cursor cur) {
        Feed feed = new Feed();
        if (cur != null) {
            int type = cur.getInt(cur.getColumnIndex(TableFeed.COL_TYPE));
            feed.setType(type);
            long id = cur.getLong(cur.getColumnIndex(TableFeed.COL_FEED_ID));
            feed.setFeedId(id);
            long userId = cur.getLong(cur.getColumnIndex(TableFeed.COL_USER_ID));
            feed.setUser(queryUser(userId));
            String text = cur.getString(cur.getColumnIndex(TableFeed.COL_TEXT));
            feed.setEssay(text);
            String source = cur.getString(cur.getColumnIndex(TableFeed.COL_SOURCE));
            feed.setFeedSource(source);
            String pics = cur.getString(cur.getColumnIndex(TableFeed.COL_PICS));
            if (pics != null) {
                String[] picUrls = pics.split(",");
                feed.setPicsUrl(picUrls);
            }
            long createAt = cur.getLong(cur.getColumnIndex(TableFeed.COL_CREATEDAT));
            feed.setDate(new Date(createAt));
            int reposts = cur.getInt(cur.getColumnIndex(TableFeed.COL_REPOSTS));
            feed.setRepostsCount(reposts);
            int comments = cur.getInt(cur.getColumnIndex(TableFeed.COL_COMMENTS));
            feed.setCommentsCount(comments);
            int attitudes = cur.getInt(cur.getColumnIndex(TableFeed.COL_ATTITUDES));
            feed.setAttitudesCount(attitudes);
            String singlePicUrl = cur.getString(cur.getColumnIndex(TableFeed.COL_SINGLEPIC));
            feed.setSinglePicUrl(singlePicUrl);
            long retweetdeId = cur.getLong(cur.getColumnIndex(TableFeed.COL_RETWEETED_ID));
            if (retweetdeId != 0) {
                feed.setRetweetId(retweetdeId);
                feed.setRetweet(queryFeed(retweetdeId));
            }
        }
        return feed;
    }

    public List<Feed> queryTableFeed() {
        List<Feed> listFeed = new ArrayList<>();
        String selection = TableFeed.COL_IS_RETWEETED + " = ? ";
        String[] selectionArgs = {String.valueOf(0)};
        String order = TableFeed.COL_FEED_ID + " DESC";
        Cursor cur = mRb.query(TableFeed.TABLE_NAME, null, selection, selectionArgs, null, null, order);
        while (cur.moveToNext()) {
            Feed feed = curToFeed(cur);
            listFeed.add(feed);
        }
//        if (cur.moveToFirst()) {
//            do {
//                Feed feed = curToFeed(cur);
//                listFeed.add(feed);
//            } while (cur.moveToNext());
//        }
        cur.close();
        return listFeed;
    }

    private Feed queryFeed(long feedId) {
        String selection = TableFeed.COL_FEED_ID + " = ? ";
        String[] selectionArgs = {String.valueOf(feedId)};
        Cursor cur = mRb.query(TableFeed.TABLE_NAME, null, selection, selectionArgs, null, null, null);
        Feed feed = new Feed();
        if (cur.moveToFirst()) {
            feed = curToFeed(cur);
        }
        cur.close();
        return feed;
    }

    public List<Comments> queryComments() {
        List<Comments> commentsList = new ArrayList<>();
        String order = TableComments.COL_CREATEDAT + " desc";
        Cursor cur = mRb.query(TableComments.TABLE_NAME, null, null, null, null, null, order);
        if (cur.moveToFirst()) {
            do {
                Comments comments = new Comments();
                long id = cur.getLong(cur.getColumnIndex(TableComments.COL_COMMENT_ID));
                comments.setCommentId(id);
                Feed reply = queryFeed(cur.getLong(cur.getColumnIndex(TableComments.COL_REPLY)));
                comments.setReplyComment(reply);
                Feed status = queryFeed(cur.getLong(cur.getColumnIndex(TableComments.COL_STATUS)));
                comments.setStatus(status);
                User commenter = queryUser(cur.getLong(cur.getColumnIndex(TableComments.COL_USER_ID)));
                comments.setCommenter(commenter);
                String source = cur.getString(cur.getColumnIndex(TableComments.COL_SOURCE));
                comments.setSource(source);
                String comment = cur.getString(cur.getColumnIndex(TableComments.COL_TEXT));
                comments.setComment(comment);
                long createAt = cur.getLong(cur.getColumnIndex(TableComments.COL_CREATEDAT));
                comments.setDate(new Date(createAt));
                commentsList.add(comments);
            } while (cur.moveToNext());
        }
        cur.close();
        return commentsList;
    }

    private User queryUser(long userId) {
        String selection = TableUser.COL_USERID + " = ? ";
        String[] selectionArgs = {String.valueOf(userId)};
        User user = new User();
        Cursor cur = mRb.query(TableUser.TABLE_NAME, null, selection, selectionArgs, null, null, null);
        if (cur.moveToFirst()) {
            long id = cur.getLong(cur.getColumnIndex(TableUser.COL_USERID));
            user.setUserId(id);
            String name = cur.getString(cur.getColumnIndex(TableUser.COL_NAME));
            user.setUserName(name);
            String headUrl = cur.getString(cur.getColumnIndex(TableUser.COL_HEADURL));
            user.setUserHeadUrl(headUrl);
        }
        cur.close();
        return user;
    }

}
