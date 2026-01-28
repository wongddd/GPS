package com.yyt.trackcar.bean;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.bean
 * @ fileName:      DeviceSysMsgBean
 * @ author:        QING
 * @ createTime:    2020/3/24 17:33
 * @ describe:      TODO 设备系统消息对象
 */
public class DeviceSysMsgBean {
    private String imei; // imei
    private long id; // id
    private int type; // 类型
    private long createtime; // 时间
    private int msg_type;
    private String msg;
    private String time;
    private String introduction; // 描述

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }

    public int getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(int msg_type) {
        this.msg_type = msg_type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }
}
