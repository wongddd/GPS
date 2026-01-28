package com.yyt.trackcar.bean;

import java.util.ArrayList;

public class AAAResponseDataBean {
    private String token;
    private float userId;
    private String userName;
    private float status;
    private ArrayList mainModuleList;
    private float deptId;


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public float getUserId() {
        return userId;
    }

    public void setUserId(float userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public float getStatus() {
        return status;
    }

    public void setStatus(float status) {
        this.status = status;
    }

    public ArrayList getMainModuleList() {
        return mainModuleList;
    }

    public void setMainModuleList(ArrayList mainModuleList) {
        this.mainModuleList = mainModuleList;
    }

    public float getDeptId() {
        return deptId;
    }

    public void setDeptId(float deptId) {
        this.deptId = deptId;
    }

}
