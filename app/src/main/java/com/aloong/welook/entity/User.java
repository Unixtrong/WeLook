package com.aloong.welook.entity;

import org.json.JSONObject;

/**
 * Created by aloong on 2017/8/6.
 */

public class User {
    private long userId;
    private String userName;
    private String userHeadUrl;
    private String screenName;

    public static User fill(JSONObject json){
        User user = new User();
        if (json.has("id")){
            user.setUserId(json.optInt("id"));
        }
        if (json.has("name")){
            user.setUserName(json.optString("name"));
        }
        if (json.has("profile_image_url")){
            user.setUserHeadUrl(json.optString("profile_image_url"));
        }
        return user;
    }

    public long getUserId(){
        return userId;
    }
    public User setUserId(long id){
        userId = id;
        return this;
    }

    public String getUserName(){
        return userName;
    }
    public User setUserName(String name){
        userName = name;
        return this;
    }

    public String getUserHeadUrl(){
        return userHeadUrl;
    }
    public User setUserHeadUrl(String head){
        userHeadUrl = head;
        return this;
    }

    public String getScreenName(){
        return screenName;
    }
    public User setScreenName(String name){
        screenName = name;
        return this;
    }
}
