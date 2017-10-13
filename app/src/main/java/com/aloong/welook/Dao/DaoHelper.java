package com.aloong.welook.Dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.aloong.welook.consts.TableComments;
import com.aloong.welook.consts.TableFeed;
import com.aloong.welook.consts.TableUser;

/**
 * Created by aloong on 2017/9/6.
 */

public class DaoHelper extends SQLiteOpenHelper {
    public DaoHelper(Context context) {
        super(context, "welook_db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableFeedSql = "CREATE TABLE " + TableFeed.TABLE_NAME + "(" + TableFeed._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TableFeed.COL_FEED_ID + " INTEGER, "
                + TableFeed.COL_IS_RETWEETED + " INTEGER, "
                + TableFeed.COL_ATTITUDES + " INTEGER, "
                + TableFeed.COL_COMMENTS + " INTEGER, "
                + TableFeed.COL_REPOSTS + " INTEGER, "
                + TableFeed.COL_USER_ID + " INTEGER, "
                + TableFeed.COL_TEXT + " TEXT, "
                + TableFeed.COL_SOURCE + " TEXT, "
                + TableFeed.COL_PICS + " TEXT, "
                + TableFeed.COL_RETWEETED_ID + " INTEGER DEFAULT 0, "
                + TableFeed.COL_CREATEDAT + " INTEGER, "
                + TableFeed.COL_TYPE + " INTEGER, "
                + TableFeed.COL_SINGLEPIC + " TEXT, UNIQUE ("
                + TableFeed.COL_FEED_ID + ", " + TableFeed.COL_IS_RETWEETED + ") ON CONFLICT REPLACE)";
        db.execSQL(createTableFeedSql);

        String createTableUserSql = "CREATE TABLE " + TableUser.TABLE_NAME + "(" + TableUser._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TableUser.COL_NAME + " TEXT, "
                + TableUser.COL_USERID + " INTEGER, "
                + TableUser.COL_HEADURL + " TEXT, UNIQUE ("
                + TableUser.COL_USERID + ") ON CONFLICT REPLACE)";
        db.execSQL(createTableUserSql);

        String createTableCommentsSql = "CREATE TABLE " + TableComments.TABLE_NAME + "(" + TableComments._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TableComments.COL_REPLY + " INTEGER, "
                + TableComments.COL_CREATEDAT + " INTEGER, "
                + TableComments.COL_TEXT + " TEXT, "
                + TableComments.COL_USER_ID + " INTEGER, "
                + TableComments.COL_COMMENT_ID + " INTEGER, "
                + TableComments.COL_STATUS + " INTEGER, "
                + TableComments.COL_SOURCE + " TEXT, UNIQUE ("
                + TableComments.COL_COMMENT_ID + ") ON CONFLICT REPLACE)";
        db.execSQL(createTableCommentsSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
