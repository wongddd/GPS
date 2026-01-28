package com.yyt.trackcar.bean;

import com.google.gson.JsonObject;

import java.util.List;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.bean
 * @ fileName:      RequestResultBean
 * @ author:        QING
 * @ createTime:    2020-02-21 17:47
 * @ describe:      TODO 请求结果对象
 */
public class RequestResultBean {
    private JsonObject requestObject; // 请求对象
    private JsonObject resultBean; // 请求结果
    private int code; // 返回结果
    private String service_ip; // 当前访问到的IP
    private String last_online_ip; // 设备最后连接的IP
    private List deviceList; // 设备列表
    private List userList; // 用户列表
    private List msgList; // 消息列表
    private List List; // 列表
    private List GeoFenceList; // 电子围栏列表
    private String result; // 请求结果

    public JsonObject getRequestObject() {
        return requestObject;
    }

    public void setRequestObject(JsonObject requestObject) {
        this.requestObject = requestObject;
    }

    public JsonObject getResultBean() {
        return resultBean;
    }

    public void setResultBean(JsonObject resultBean) {
        this.resultBean = resultBean;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List deviceList) {
        this.deviceList = deviceList;
    }

    public List getUserList() {
        return userList;
    }

    public void setUserList(List userList) {
        this.userList = userList;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List getMsgList() {
        return msgList;
    }

    public void setMsgList(List msgList) {
        this.msgList = msgList;
    }

    public java.util.List getList() {
        return List;
    }

    public void setList(java.util.List list) {
        List = list;
    }

    public String getService_ip() {
        return service_ip;
    }

    public void setService_ip(String service_ip) {
        this.service_ip = service_ip;
    }

    public String getLast_online_ip() {
        return last_online_ip;
    }

    public void setLast_online_ip(String last_online_ip) {
        this.last_online_ip = last_online_ip;
    }

    public java.util.List getGeoFenceList() {
        return GeoFenceList;
    }

    public void setGeoFenceList(java.util.List geoFenceList) {
        GeoFenceList = geoFenceList;
    }
}
