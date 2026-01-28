package com.yyt.trackcar.bean;

/**
 * @ projectName:
 * @ packageName:   com.yyt.trackcar.bean
 * @ fileName:      TrackModel
 * @ author:        QING
 * @ createTime:    9/24/21 16:37
 * @ describe:      TODO
 */
public class AAATrackModel {

    private Long logId;

    /** 日志时间 */
    private String logTime;

    /**
     * imei
     */
    private String deviceImei;

    private String deviceName;

    private Integer deviceType;

    /** 经度 */
    private Double lng;

    /** 维度 */
    private Double lat;

    /**
     * 定位时间
     */
    private String gpsTime;

    /**
     * 朝向
     */
    private Float heading;

    /**  速度  */
    private Float speed;

    /**  设备状态 0 静止 1 运动  */
    private Long motionStatus;

    /** 电量 */
    private String deviceVol;

    /** 信号强度 */
    private String deviceSms;

    /** 定位类型 0 GPS 1 WIFI 2 基站 */
    private Integer locationType;

    /** 位置信息 */
    private String positionDesc;

    /**
     * 里程
     */
    private Float odometer;

    /**
     * ad1 油量
     */
    private Float ad1;

    /**
     * ad2油量
     */
    private Float ad2;

    /**
     * acc 状态
     */
    private Integer accStatus;

    /**
     * 温度
     */
    private Float temperature;

    /**
     * 警报
     */
    private String alarm;

    /**
     * 天气
     */
    private String weather;

    /**
     * 设备在线状态
     */
    private boolean isOnlineStatus;

    /**
     * 补传标记  supplement = 1 表示补传
     */
    private int supplement;

    /** 当前比赛 顺序点 （第几个点）*/
    private Integer pointIndex;

    /** 海拔 */
    private Double altitude;

    /** 时长（秒）  */
    private Long duration;

    /** 累积距离  */
    private Long accumulateOdometer;

    /** 累积时长  */
    private Long accumulateDuration;

    /** 设备版本 */
    private String version;
    private String headPic;
    private String rignNo;
    private String lastCommunicationDateTime;
    private Integer satellite;
    private Integer isCharge;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Long getAccumulateOdometer() {
        return accumulateOdometer;
    }

    public void setAccumulateOdometer(Long accumulateOdometer) {
        this.accumulateOdometer = accumulateOdometer;
    }

    public Long getAccumulateDuration() {
        return accumulateDuration;
    }

    public void setAccumulateDuration(Long accumulateDuration) {
        this.accumulateDuration = accumulateDuration;
    }

    public Integer getPointIndex() {
        return pointIndex;
    }

    public void setPointIndex(Integer pointIndex) {
        this.pointIndex = pointIndex;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    public boolean isOnlineStatus() {
        return isOnlineStatus;
    }

    public void setOnlineStatus(boolean onlineStatus) {
        isOnlineStatus = onlineStatus;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public String getLogTime() {
        return logTime;
    }

    public void setLogTime(String logTime) {
        this.logTime = logTime;
    }

    public String getDeviceImei() {
        return deviceImei;
    }

    public void setDeviceImei(String deviceImei) {
        this.deviceImei = deviceImei;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public String getGpsTime() {
        return gpsTime;
    }

    public void setGpsTime(String gpsTime) {
        this.gpsTime = gpsTime;
    }

    public Float getHeading() {
        return heading;
    }

    public void setHeading(Float heading) {
        this.heading = heading;
    }

    public Float getSpeed() {
        return speed;
    }

    public void setSpeed(Float speed) {
        this.speed = speed;
    }

    public Long getMotionStatus() {
        return motionStatus;
    }

    public void setMotionStatus(Long motionStatus) {
        this.motionStatus = motionStatus;
    }

    public String getDeviceVol() {
        return deviceVol;
    }

    public void setDeviceVol(String deviceVol) {
        this.deviceVol = deviceVol;
    }

    public String getDeviceSms() {
        return deviceSms;
    }

    public void setDeviceSms(String deviceSms) {
        this.deviceSms = deviceSms;
    }

    public Integer getLocationType() {
        return locationType;
    }

    public void setLocationType(Integer locationType) {
        this.locationType = locationType;
    }

    public String getPositionDesc() {
        return positionDesc;
    }

    public void setPositionDesc(String positionDesc) {
        this.positionDesc = positionDesc;
    }

    public Float getOdometer() {
        return odometer;
    }

    public void setOdometer(Float odometer) {
        this.odometer = odometer;
    }

    public Float getAd1() {
        return ad1;
    }

    public void setAd1(Float ad1) {
        this.ad1 = ad1;
    }

    public Float getAd2() {
        return ad2;
    }

    public void setAd2(Float ad2) {
        this.ad2 = ad2;
    }

    public Integer getAccStatus() {
        return accStatus;
    }

    public void setAccStatus(Integer accStatus) {
        this.accStatus = accStatus;
    }

    public Float getTemperature() {
        return temperature;
    }

    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }

    public String getAlarm() {
        return alarm;
    }

    public void setAlarm(String alarm) {
        this.alarm = alarm;
    }

    public int getSupplement() {
        return supplement;
    }

    public void setSupplement(int supplement) {
        this.supplement = supplement;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public Integer getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(Integer deviceType) {
        this.deviceType = deviceType;
    }

    private long tId;

    public long gettId() {
        return tId;
    }

    public void settId(long tId) {
        this.tId = tId;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public String getRignNo() {
        return rignNo;
    }

    public void setRignNo(String rignNo) {
        this.rignNo = rignNo;
    }

    public String getLastCommunicationDateTime() {
        return lastCommunicationDateTime;
    }

    public void setLastCommunicationDateTime(String lastCommunicationDateTime) {
        this.lastCommunicationDateTime = lastCommunicationDateTime;
    }

    public Integer getSatellite() {
        return satellite;
    }

    public void setSatellite(Integer satellite) {
        this.satellite = satellite;
    }

    public Integer getIsCharge() {
        return isCharge;
    }

    public void setIsCharge(Integer isCharge) {
        this.isCharge = isCharge;
    }
}
