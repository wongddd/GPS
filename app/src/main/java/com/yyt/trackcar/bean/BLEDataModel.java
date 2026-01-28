package com.yyt.trackcar.bean;

import java.util.List;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.bean
 * @ fileName:      BLEDataModel
 * @ author:        QING
 * @ createTime:    2023/4/24 14:35
 * @ describe:      TODO 蓝牙数据对象
 */
public class BLEDataModel {

    private BLEConfigModel config; // 蓝牙配置对象

    private String macAddress; // mac地址
    private String matchId; // 赛场ID
    private Long startTime; // 开始时间
    private Long nightTime; // 夜间时间
    private Long continuedFlyTime; // 续飞时间
    private Integer lowBattery; // 低电量
    private Long delayTime; // 延时时间
    private Integer forcedStartup; // 强制开机
    private Integer startLocationInterval; // 开始定位间隔
    private Integer nightLocationInterval; // 夜间定位间隔
    private Integer continuedFlyLocationInterval; // 续飞定位间隔
    private Integer lowBatteryLocationInterval; // 低电量定位间隔
    private Long locationTime; // 定位时间
    private Integer battery; // 电量
    private Float lat; // 纬度
    private Float lng; // 经度
    private Integer altitude; // 海拔高度
    private Integer satellitesNum; // 卫星数
    private Integer heading; // 对地真航向

    public BLEConfigModel getConfig() {
        return config;
    }

    public void setConfig(BLEConfigModel config) {
        this.config = config;
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

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getNightTime() {
        return nightTime;
    }

    public void setNightTime(Long nightTime) {
        this.nightTime = nightTime;
    }

    public Long getContinuedFlyTime() {
        return continuedFlyTime;
    }

    public void setContinuedFlyTime(Long continuedFlyTime) {
        this.continuedFlyTime = continuedFlyTime;
    }

    public Integer getLowBattery() {
        return lowBattery;
    }

    public void setLowBattery(Integer lowBattery) {
        this.lowBattery = lowBattery;
    }

    public Long getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(Long delayTime) {
        this.delayTime = delayTime;
    }

    public Integer getForcedStartup() {
        return forcedStartup;
    }

    public void setForcedStartup(Integer forcedStartup) {
        this.forcedStartup = forcedStartup;
    }

    public Integer getStartLocationInterval() {
        return startLocationInterval;
    }

    public void setStartLocationInterval(Integer startLocationInterval) {
        this.startLocationInterval = startLocationInterval;
    }

    public Integer getNightLocationInterval() {
        return nightLocationInterval;
    }

    public void setNightLocationInterval(Integer nightLocationInterval) {
        this.nightLocationInterval = nightLocationInterval;
    }

    public Integer getContinuedFlyLocationInterval() {
        return continuedFlyLocationInterval;
    }

    public void setContinuedFlyLocationInterval(Integer continuedFlyLocationInterval) {
        this.continuedFlyLocationInterval = continuedFlyLocationInterval;
    }

    public Integer getLowBatteryLocationInterval() {
        return lowBatteryLocationInterval;
    }

    public void setLowBatteryLocationInterval(Integer lowBatteryLocationInterval) {
        this.lowBatteryLocationInterval = lowBatteryLocationInterval;
    }

    public Long getLocationTime() {
        return locationTime;
    }

    public void setLocationTime(Long locationTime) {
        this.locationTime = locationTime;
    }

    public Integer getBattery() {
        return battery;
    }

    public void setBattery(Integer battery) {
        this.battery = battery;
    }

    public Float getLat() {
        return lat;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public Float getLng() {
        return lng;
    }

    public void setLng(Float lng) {
        this.lng = lng;
    }

    public Integer getAltitude() {
        return altitude;
    }

    public void setAltitude(Integer altitude) {
        this.altitude = altitude;
    }

    public Integer getSatellitesNum() {
        return satellitesNum;
    }

    public void setSatellitesNum(Integer satellitesNum) {
        this.satellitesNum = satellitesNum;
    }

    public Integer getHeading() {
        return heading;
    }

    public void setHeading(Integer heading) {
        this.heading = heading;
    }
}
