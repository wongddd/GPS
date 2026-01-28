package com.yyt.trackcar.dbflow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.dbflow
 * @ fileName:      TrackModel
 * @ author:        QING
 * @ createTime:    2020/4/23 16:17
 * @ describe:      TODO 轨迹对象
 */
@Table(database = AppDataBase.class)
public class TrackModel extends BaseModel {
    @Column
    @PrimaryKey
    private String imei; // 设备imei

    @Column
    @PrimaryKey
    private String date; // 日期

    @Column
    @PrimaryKey
    private long uploadtime; // 上传时间

    @Column
    private String lng; // 经度

    @Column
    private String lat; // 纬度

    @Column
    private int locationType; // 定位类型

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getUploadtime() {
        return uploadtime;
    }

    public void setUploadtime(long uploadtime) {
        this.uploadtime = uploadtime;
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
}
