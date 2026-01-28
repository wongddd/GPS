package com.yyt.trackcar.dbflow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.dbflow
 * @ fileName:      AAADeviceModel
 * @ author:        QING
 * @ createTime:    2020-02-26 18:21
 * @ describe:      TODO 设备对象
 */
@Table(database = AppDataBase.class)
public class DeviceModel extends BaseModel {
    @Column
    @PrimaryKey
    private String imei; // 设备imei

    @Column
    @PrimaryKey
    private long u_id; // 用户id

    @Column
    private String name; // 设备名

    @Column
    private long id; // 设备id

    @Column
    private int status; // 0不是管理员1是管理员

    @Column
    private long timestamp; // 时间戳

    @Column
    private long d_id; // 设备id

    @Column
    private String activeCode; // 激活码

    @Column
    private int device_type;

    @Column
    private String ip;

    private boolean aa;

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public long getU_id() {
        return u_id;
    }

    public void setU_id(long u_id) {
        this.u_id = u_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getD_id() {
        return d_id;
    }

    public void setD_id(long d_id) {
        this.d_id = d_id;
    }

    public String getActiveCode() {
        return activeCode;
    }

    public void setActiveCode(String activeCode) {
        this.activeCode = activeCode;
    }

    public int getDevice_type() {
        return device_type;
    }

    public void setDevice_type(int device_type) {
        this.device_type = device_type;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
