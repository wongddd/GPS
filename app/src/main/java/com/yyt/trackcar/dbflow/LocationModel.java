package com.yyt.trackcar.dbflow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.dbflow
 * @ fileName:      LocationModel
 * @ author:        QING
 * @ createTime:    2020/3/5 18:19
 * @ describe:      TODO 定位信息
 */
@Table(database = AppDataBase.class)
public class LocationModel extends BaseModel {
    @Column
    @PrimaryKey
    private String imei; // 设备imei

    @Column
    @PrimaryKey
    private long u_id; // 用户id

    @Column
    private int Electricity; // 电量

    @Column
    private String lng; // 纬度

    @Column
    private String lat; // 经度

    @Column
    private int locationType; // 类型，0：gps， 1：基站 2:wifi

    @Column
    private long uploadtime; // 时间

    @Column
    private String step; // 步数

    @Column
    private int accuracy; // 精度

    @Column
    private String desc; // 地址

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

    public int getElectricity() {
        return Electricity;
    }

    public void setElectricity(int electricity) {
        Electricity = electricity;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public int getLocationType() {
        return locationType;
    }

    public void setLocationType(int locationType) {
        this.locationType = locationType;
    }

    public long getUploadtime() {
        return uploadtime;
    }

    public void setUploadtime(long uploadtime) {
        this.uploadtime = uploadtime;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
