package com.aloong.welook.handler;

import com.aloong.welook.entity.Comments;
import com.aloong.welook.entity.Result;
import com.aloong.welook.utils.CodeUtils;
import com.aloong.welook.utils.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * Created by aloong on 2017/8/29.
 */

public class RequestCommentsToMeAPI {
    private static final String BASE_URL = "https://api.weibo.com/2/";
    public static Result<List<Comments>> requestComments(String token, int count) throws IOException, JSONException {
        String path = BASE_URL + "comments/to_me.json?access_token="+ token + "&count="+count;
        String jsonStr = CodeUtils.readIS(HttpUtils.httpRequest(path));
        return Result.fill(new JSONObject(jsonStr), new Result.Parser<List<Comments>>() {
            @Override
            public List<Comments> parse(JSONObject jsonObject) {
                return Comments.fillList(jsonObject.optJSONArray("comments"));
            }
        });
    }
}
