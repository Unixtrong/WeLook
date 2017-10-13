package com.aloong.welook.entity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aloong on 2017/8/18.
 */

public class Group {
    private int mId;
    private String mName;
    private int mCount;

    public static Group fill(JSONObject jsonObject){
        Group group = new Group();
        if (jsonObject.has("id")){
            group.setId(jsonObject.optInt("id"));
        }
        if (jsonObject.has("name")){
            group.setName(jsonObject.optString("name"));
        }
        if (jsonObject.has("count")){
            group.setCount(jsonObject.optInt("count"));
        }
        return group;
    }
    public static List<Group> fillList(JSONArray jsonArray){
        List<Group> groupList = new ArrayList<>();

        for (int i = 0; i<jsonArray.length();i++){
            groupList.add(fill(jsonArray.optJSONObject(i)));
        }

        return groupList;
    }

    public int getId() {
        return mId;
    }

    public Group setId(int mId) {
        this.mId = mId;
        return this;
    }

    public String getName() {
        return mName;
    }

    public Group setName(String mName) {
        this.mName = mName;
        return this;
    }

    public int getCount() {
        return mCount;
    }

    public Group setCount(int mCount) {
        this.mCount = mCount;
        return this;
    }
}
