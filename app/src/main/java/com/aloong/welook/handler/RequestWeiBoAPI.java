package com.aloong.welook.handler;

import com.aloong.welook.entity.Feed;
import com.aloong.welook.entity.Result;
import com.aloong.welook.utils.CodeUtils;
import com.aloong.welook.utils.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * Created by aloong on 2017/8/6.
 */

public class RequestWeiBoAPI {
    private static final String BASE_URL = "https://api.weibo.com/2/";

    public static Result<List<Feed>> requestFeed(String token, int count) throws IOException, JSONException {
        String path = BASE_URL+"statuses/home_timeline.json?access_token=" + token + "&count="+count;
        String jsonStr = CodeUtils.readIS(HttpUtils.httpRequest(path));
        return Result.fill(new JSONObject(jsonStr), new Result.Parser<List<Feed>>() {
            @Override
            public List<Feed> parse(JSONObject jsonObject) {
                return Feed.fillList(jsonObject.optJSONArray("statuses"));
            }
        });
    }
}
