package com.yyt.trackcar.dbflow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.dbflow
 * @ fileName:      DeviceSysMsgModel
 * @ author:        QING
 * @ createTime:    2020/4/28 16:19
 * @ describe:      TODO 设备系统消息对象
 */
@Table(database = AppDataBase.class)
public class DeviceSysMsgModel extends BaseModel {
    @Column
    @PrimaryKey
    private String imei; // imei

    @Column
    @PrimaryKey
    private long id; // id

    @Column
    private int type; // 类型

    @Column
    private long createtime; // 时间

    @Column
    private int msg_type;

    @Column
    private String msg;

    @Column
    private String time;

    @Column
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
