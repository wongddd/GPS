package com.yyt.trackcar.bean;

import java.util.List;

/**
 * @ projectName:
 * @ packageName:   com.yyt.trackcar.bean
 * @ fileName:      ResponseBean
 * @ author:        QING
 * @ createTime:    6/19/21 14:21
 * @ describe:      TODO
 */
public class AAAResponseBean {
    private String requestObject; // 请求对象
    private String responseObject; // 请求结果
    private int Result; // 返回结果
    private List list; // 返回列表

    public String getRequestObject() {
        return requestObject;
    }

    public void setRequestObject(String requestObject) {
        this.requestObject = requestObject;
    }

    public String getResponseObject() {
        return responseObject;
    }

    public void setResponseObject(String responseObject) {
        this.responseObject = responseObject;
    }

    public int getResult() {
        return Result;
    }

    public void setResult(int result) {
        Result = result;
    }

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }
}
