package com.yyt.trackcar.bean;

import com.google.gson.JsonObject;

public class AAABaseResponseBean {

    private int code;

    private String msg;

    private Object data;

    private JsonObject requestObject;

    private String strParameter;

    private String token;

    private String uid;

    private Integer pwdType; // 密码类型 0.普通密码 1.通用密码 2.超级密码

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public JsonObject getRequestObject() {
        return requestObject;
    }

    public String getStrParameter() {
        return strParameter;
    }

    public void setStrParameter(String strParameter) {
        this.strParameter = strParameter;
    }

    public void setRequestObject(JsonObject requestObject) {
        this.requestObject = requestObject;
    }

    public Integer getPwdType() {
        return pwdType;
    }

    public void setPwdType(Integer pwdType) {
        this.pwdType = pwdType;
    }
}
