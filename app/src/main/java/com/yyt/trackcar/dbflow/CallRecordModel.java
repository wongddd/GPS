package com.yyt.trackcar.dbflow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.dbflow
 * @ fileName:      CallRecordModel
 * @ author:        QING
 * @ createTime:    2020/4/16 11:52
 * @ describe:      TODO 拨号记录对象
 */
@Table(database = AppDataBase.class)
public class CallRecordModel extends BaseModel {
    @Column
    @PrimaryKey
    private String imei; // 设备imei

    @Column
    @PrimaryKey
    private long createtime; //上报时间

    @Column
    @PrimaryKey
    private String phone; // 电话号码

    @Column
    private int phone_type; //类型: 呼入0 呼出1

    @Column
    private int phone_status; //是否接听：0未接听 1接听

    @Column
    private long call_duration; //通话时长 单位秒

    @Column
    private String nick_name; // 昵称

    @Column
    private String phone_time; // 通话时间

    @Column
    private int cmdType; // 1.正常2.sos3.监听4拦截陌生来电

    private int bgDrawable; //背景

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public int getPhone_type() {
        return phone_type;
    }

    public void setPhone_type(int phone_type) {
        this.phone_type = phone_type;
    }

    public int getPhone_status() {
        return phone_status;
    }

    public void setPhone_status(int phone_status) {
        this.phone_status = phone_status;
    }

    public long getCall_duration() {
        return call_duration;
    }

    public void setCall_duration(long call_duration) {
        this.call_duration = call_duration;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone_time() {
        return phone_time;
    }

    public void setPhone_time(String phone_time) {
        this.phone_time = phone_time;
    }

    public int getCmdType() {
        return cmdType;
    }

    public void setCmdType(int cmdType) {
        this.cmdType = cmdType;
    }

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }

    public int getBgDrawable() {
        return bgDrawable;
    }

    public void setBgDrawable(int bgDrawable) {
        this.bgDrawable = bgDrawable;
    }
}
