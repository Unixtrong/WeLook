package com.aloong.welook.handler;

import com.aloong.welook.entity.Feed;
import com.aloong.welook.entity.Result;
import com.aloong.welook.utils.CodeUtils;
import com.aloong.welook.utils.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by aloong on 2017/8/17.
 */

public class RequestUserShow {
    private static final String BASE_URL = "https://api.weibo.com/2/";

    public static Result<Feed> requestFeed(String token, String screenName) throws IOException, JSONException {
        String path = BASE_URL+"users/show.json?access_token=" + token + "&screen_name="+screenName;
        String jsonStr = CodeUtils.readIS(HttpUtils.httpRequest(path));
        return Result.fill(new JSONObject(jsonStr), new Result.Parser<Feed>() {
            @Override
            public Feed parse(JSONObject jsonObject) {
                return Feed.fill(jsonObject.optJSONObject("statuses"));
            }
        });
    }
}
