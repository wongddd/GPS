package com.yyt.trackcar.bean;

import com.google.gson.JsonObject;

public class TemporaryBaseResponseBean {

    private int code;

    private String msg;

    private Object data;

    private JsonObject requestObject;

    private String strParameter;

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
}
