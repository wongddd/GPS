package com.yyt.trackcar.bean;

/**
 * @ projectName:
 * @ packageName:   com.yyt.trackcar.bean
 * @ fileName:      HealthReportBean
 * @ author:        QING
 * @ createTime:    7/5/21 16:10
 * @ describe:      TODO
 */
public class HealthReportBean {

    private int msg;
    private String time;
    private long uploadTime;

    public int getMsg() {
        return msg;
    }

    public void setMsg(int msg) {
        this.msg = msg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(long uploadTime) {
        this.uploadTime = uploadTime;
    }
}
