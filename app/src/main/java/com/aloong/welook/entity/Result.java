package com.aloong.welook.entity;

import org.json.JSONObject;

/**
 * Created by aloong on 2017/8/6.
 */

public class Result<Type> {
    private String errorCode;
    private String errorMsg;
    private Type mBody;

    public static<Programs> Result<Programs> fill(JSONObject jsonObject, Parser<Programs> parser){
        Result weiBoResult = new Result();
        if (jsonObject.has("error")){
            weiBoResult.setErrorMsg(jsonObject.optString("error"));
        }
        if (jsonObject.has("error_code")){
            weiBoResult.setErrorCode(jsonObject.optString("error_code"));
        }
        if (weiBoResult.isSuccess()){
            weiBoResult.setBody(parser.parse(jsonObject));
        }



        return weiBoResult;
    }
    public Type getBody(){
        return mBody;
    }
    public Result setBody(Type body){
         mBody = body;
        return this;
    }
    public String getErrorCode(){
        return errorCode;
    }
    public Result setErrorCode(String errorCode){
        this.errorCode = errorCode;
        return this;
    }

    public String getErrorMsg(){
        return errorMsg;
    }
    public Result setErrorMsg(String errorMsg){
        this.errorMsg = errorMsg;
        return this;
    }
    public boolean isSuccess(){
        return errorCode == null;
    }
    public interface Parser<Type>{
        Type parse(JSONObject jsonObject);
    }
}
