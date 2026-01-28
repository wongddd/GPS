package com.yyt.trackcar.dbflow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.dbflow
 * @ fileName:      BleLocationModel
 * @ author:        QING
 * @ createTime:    2023/8/2 13:51
 * @ describe:      TODO 蓝牙定位点对象
 */
@Table(database = AppDataBase.class)
public class BleLocationModel extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = true)
    private long id; // 读取的赛场ID
    @Column
    private String macAddress; // mac地址
    @Column
    private String matchId; // 读取的赛场ID
    @Column
    private long locationTime; // 定位时间
    @Column
    private int battery; // 电量
    @Column
    private float lat; // 纬度
    @Column
    private float lng; // 经度
    @Column
    private int altitude; // 海拔高度
    @Column
    private int satellitesNum; // 卫星数
    @Column
    private int heading; // 对地真航向

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public long getLocationTime() {
        return locationTime;
    }

    public void setLocationTime(long locationTime) {
        this.locationTime = locationTime;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }

    public int getAltitude() {
        return altitude;
    }

    public void setAltitude(int altitude) {
        this.altitude = altitude;
    }

    public int getSatellitesNum() {
        return satellitesNum;
    }

    public void setSatellitesNum(int satellitesNum) {
        this.satellitesNum = satellitesNum;
    }

    public int getHeading() {
        return heading;
    }

    public void setHeading(int heading) {
        this.heading = heading;
    }

}
