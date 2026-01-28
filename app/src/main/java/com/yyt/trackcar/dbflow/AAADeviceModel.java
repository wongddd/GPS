package com.yyt.trackcar.dbflow;

/**
 * @ projectName:
 * @ packageName:   com.yyt.trackcar.dbflow
 * @ fileName:      AAADeviceModel
 * @ author:        QING
 * @ createTime:    6/19/21 17:57
 * @ describe:      TODO
 */

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = AppDataBase.class)
public class AAADeviceModel extends BaseModel {

    @Column
    @PrimaryKey
    private long userId;

    @Column
    @PrimaryKey
    private long deviceId;

    /**
     * imei 国际移动设备识别码
     */
    @Column
    private String deviceImei;

    /**
     * 所属组织（车队，或分部）
     */
    @Column
    private String organization;

    /**
     * 设备名称
     */
    @Column
    private String deviceName;

    /** 经度 */
    @Column
    private Double lastLongitude;

    /** 纬度 */
    @Column
    private Double lastLatitude;

    /** 最后一次定位时间 yyyy-MM-dd HH:mm:ss*/
    @Column
    private String lastGpsTime;

    /** 速度 */
    @Column
    private Float lastSpeed;

    /** 设备状态 0 静止 1 运动 */
    @Column
    private Long lastMotionStatus;

    /** 电量 */
    @Column
    private String lastDeviceVol;

    /**
     * 朝向
     */
    @Column
    private Float heading;

    /**
     * 里程
     */
    @Column
    private Float odometer;

    /**
     * 在线状态 1 在线 0 离线
     */
    @Column
    private boolean onlineStatus;

    /**
     * 引擎 acc状态 0 关  1 开
     */
    @Column
    private Integer engineStatus;

    /**
     * 空调状态 0 关  1 开
     */
    @Column
    private Integer airConditionStatus;

    /**
     * 门状态 0 关  1 开
     */
    @Column
    private Integer doorStatus;

    /**
     * 头灯/大灯 状态  0 关  1 开
     */
    @Column
    private Integer headlightStatus;

    /**
     * 客户id
     */
    @Column
    private Long customerId;

    /**
     * 设备类型  1、车载定位器, 2、鸽子定位器, 3、学生卡
     */
    @Column
    private int deviceType;

    /**
     * 设备状态 default: null  1:可用  2:飞丢
     */
    private Integer deviceStatus;

    private long uId; // 用户id

    private long companyid;

    private long tId;

    private long carid;

    private String Latitude;

    private String Longitude;

    private Object Battery;

    private float batterys;

    private Object Rpm;

    private int rpms;

    private Object Pedal;

    private int pedals;

    private Object Brake;

    private int brakes;

    private Object Throttle;

    private float throttles;

    private String AlarmText;

    private String StatusText;

    private String Engine;

    private int Online;

    private long ID;

    private String ImeiNo;

    private String Plate;

    private long ProtocolID;

    private long TeamID;

    private String SimNo;

    private String BaseMileage;

    private String Brand;

    private String Type;

    private String Color;

    private long TimeZone;

    private String MapInfo;

    private String Model;

    private String ChasisNo;

    private String EngineNo;

    private String ManufactureDate;

    private String License;

    private String Icon;

    private String CreateTime;

    private long CreatorID;

    private String ExpireTime;

    private int State;

    private String OldID;

    private String Spare1;

    private String LastMoveTime;

    private String vehicle_register_type;

    private String province_code;

    private String remark1;

    private String remark2;

    private String remark3;

    private String ClientID;

    private String GPSTime;

    private String Direct;

    private String SquadDate;

    private String Fuel;

    private String Fuel2;

    private String Mileage;

    private String Temperature;

    private String ServerTime;
    private String activatedDatetime; // 激活时间
    private String expireDate; // 有效期
    private String guaranteeDate; // 保修期
    private String bindDatetime; // 绑定时间
    private String unbindDatetime; // 解绑时间
    private String loseCreatetime; // 飞丢时间
    private String retrieveCreatetime; // 找回时间
//    private String lastGpsTime; // 最后定位时间
    private Integer locationType;
    private String lastCommunicationDateTime;
    private Integer satellite;
    private Integer isCharge;
    private Integer positionType; // 定位模式 1 精确定位（持续定位）、2 省电模式、3 睡眠模式
    private Integer positionInterval;
    private String deviceRemark; // 备注信息
    private Integer raceStatus; // 比赛模式

    private Object Input1;
    private Object Input2;
    private Object Input3;
    private Object Input4;
    private Object Input5;
    private Object Output1;
    private Object Output2;
    private Object Output3;
    private Object Output4;
    private Object Output5;

    //=========================================================

    /**
     * 设备是否显示
     */
    private boolean isSelected;

    /**
     * 设备头像
     */
    @Column
    private String headPic;

    /**
     * 设备绑定的手机号（学生卡类型
     */
    private String bindMobile;

    /**
     * 设备最后位置的文字描述
     */
    private String lastPositionDesc;

    /**
     * 设备所在位置的天气情况
     */
    private String weather;

    /**
     * 定位模式 1，2
     */
    private Integer lastLocationType;

    /**
     * 日志时间
     */
    private String lastLocationTime;

    /** 当前比赛 顺序点 （第几个点）*/
    private Integer pointIndex;

    /** 海拔 */
    private Double altitude;

    /** 信号强度 */
    private String deviceSms;

    /**  速度  */
    private Float speed;

    /** 时长（秒）  */
    private Long duration;

    /** 累积距离  */
    private Long accumulateOdometer;

    /** 累积时长  */
    private Long accumulateDuration;

    /** 设备版本 */
    private String version;

    /** 鸽环编号 */
    private String ringNo;

    private Integer activated;

    public String getRingNo() {
        return ringNo;
    }

    public void setRingNo(String ringNo) {
        this.ringNo = ringNo;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Float getLastSpeed() {
        return lastSpeed;
    }

    public void setLastSpeed(Float lastSpeed) {
        this.lastSpeed = lastSpeed;
    }

    public Float getSpeed() {
        return speed;
    }

    public void setSpeed(Float speed) {
        this.speed = speed;
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

    public String getDeviceSms() {
        return deviceSms;
    }

    public void setDeviceSms(String deviceSms) {
        this.deviceSms = deviceSms;
    }

    public String getLastLocationTime() {
        return lastLocationTime;
    }

    public void setLastLocationTime(String lastLocationTime) {
        this.lastLocationTime = lastLocationTime;
    }

    public Integer getLastLocationType() {
        return lastLocationType;
    }

    public void setLastLocationType(Integer lastLocationType) {
        this.lastLocationType = lastLocationType;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getLastPositionDesc() {
        return lastPositionDesc;
    }

    public void setLastPositionDesc(String lastPositionDesc) {
        this.lastPositionDesc = lastPositionDesc;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public long getUId() {
        return uId;
    }

    public void setUId(long uId) {
        this.uId = uId;
    }

    public long getCompanyid() {
        return companyid;
    }

    public void setCompanyid(long companyid) {
        this.companyid = companyid;
    }

    public long getTId() {
        return tId;
    }

    public void setTId(long tId) {
        this.tId = tId;
    }

    public long getCarid() {
        return carid;
    }

    public void setCarid(long carid) {
        this.carid = carid;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public Object getBattery() {
        return Battery;
    }

    public void setBattery(Object battery) {
        Battery = battery;
    }

    public Object getRpm() {
        return Rpm;
    }

    public void setRpm(Object rpm) {
        Rpm = rpm;
    }

    public Object getPedal() {
        return Pedal;
    }

    public void setPedal(Object pedal) {
        Pedal = pedal;
    }

    public Object getBrake() {
        return Brake;
    }

    public void setBrake(Object brake) {
        Brake = brake;
    }

    public Object getThrottle() {
        return Throttle;
    }

    public void setThrottle(Object throttle) {
        Throttle = throttle;
    }

    public String getAlarmText() {
        return AlarmText;
    }

    public void setAlarmText(String alarmText) {
        AlarmText = alarmText;
    }

    public String getStatusText() {
        return StatusText;
    }

    public void setStatusText(String statusText) {
        StatusText = statusText;
    }

    public String getEngine() {
        return Engine;
    }

    public void setEngine(String engine) {
        Engine = engine;
    }

    public int getOnline() {
        return Online;
    }

    public void setOnline(int online) {
        Online = online;
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getImeiNo() {
        return ImeiNo;
    }

    public void setImeiNo(String imeiNo) {
        ImeiNo = imeiNo;
    }

    public String getPlate() {
        return Plate;
    }

    public void setPlate(String plate) {
        Plate = plate;
    }

    public long getProtocolID() {
        return ProtocolID;
    }

    public void setProtocolID(long protocolID) {
        ProtocolID = protocolID;
    }

    public long getTeamID() {
        return TeamID;
    }

    public void setTeamID(long teamID) {
        TeamID = teamID;
    }

    public String getSimNo() {
        return SimNo;
    }

    public void setSimNo(String simNo) {
        SimNo = simNo;
    }

    public String getBaseMileage() {
        return BaseMileage;
    }

    public void setBaseMileage(String baseMileage) {
        BaseMileage = baseMileage;
    }

    public String getBrand() {
        return Brand;
    }

    public void setBrand(String brand) {
        Brand = brand;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getColor() {
        return Color;
    }

    public void setColor(String color) {
        Color = color;
    }

    public long getTimeZone() {
        return TimeZone;
    }

    public void setTimeZone(long timeZone) {
        TimeZone = timeZone;
    }

    public String getMapInfo() {
        return MapInfo;
    }

    public void setMapInfo(String mapInfo) {
        MapInfo = mapInfo;
    }

    public String getModel() {
        return Model;
    }

    public void setModel(String model) {
        Model = model;
    }

    public String getChasisNo() {
        return ChasisNo;
    }

    public void setChasisNo(String chasisNo) {
        ChasisNo = chasisNo;
    }

    public String getEngineNo() {
        return EngineNo;
    }

    public void setEngineNo(String engineNo) {
        EngineNo = engineNo;
    }

    public String getManufactureDate() {
        return ManufactureDate;
    }

    public void setManufactureDate(String manufactureDate) {
        ManufactureDate = manufactureDate;
    }

    public String getLicense() {
        return License;
    }

    public void setLicense(String license) {
        License = license;
    }

    public String getIcon() {
        return Icon;
    }

    public void setIcon(String icon) {
        Icon = icon;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public long getCreatorID() {
        return CreatorID;
    }

    public void setCreatorID(long creatorID) {
        CreatorID = creatorID;
    }

    public String getExpireTime() {
        return ExpireTime;
    }

    public void setExpireTime(String expireTime) {
        ExpireTime = expireTime;
    }

    public int getState() {
        return State;
    }

    public void setState(int state) {
        State = state;
    }

    public String getOldID() {
        return OldID;
    }

    public void setOldID(String oldID) {
        OldID = oldID;
    }

    public String getSpare1() {
        return Spare1;
    }

    public void setSpare1(String spare1) {
        Spare1 = spare1;
    }

    public String getLastMoveTime() {
        return LastMoveTime;
    }

    public void setLastMoveTime(String lastMoveTime) {
        LastMoveTime = lastMoveTime;
    }

    public String getVehicle_register_type() {
        return vehicle_register_type;
    }

    public void setVehicle_register_type(String vehicle_register_type) {
        this.vehicle_register_type = vehicle_register_type;
    }

    public String getProvince_code() {
        return province_code;
    }

    public void setProvince_code(String province_code) {
        this.province_code = province_code;
    }

    public String getRemark1() {
        return remark1;
    }

    public void setRemark1(String remark1) {
        this.remark1 = remark1;
    }

    public String getRemark2() {
        return remark2;
    }

    public void setRemark2(String remark2) {
        this.remark2 = remark2;
    }

    public String getRemark3() {
        return remark3;
    }

    public void setRemark3(String remark3) {
        this.remark3 = remark3;
    }

    public String getClientID() {
        return ClientID;
    }

    public void setClientID(String clientID) {
        ClientID = clientID;
    }

    public float getBatterys() {
        return batterys;
    }

    public void setBatterys(float batterys) {
        this.batterys = batterys;
    }

    public int getRpms() {
        return rpms;
    }

    public void setRpms(int rpms) {
        this.rpms = rpms;
    }

    public int getPedals() {
        return pedals;
    }

    public void setPedals(int pedals) {
        this.pedals = pedals;
    }

    public int getBrakes() {
        return brakes;
    }

    public void setBrakes(int brakes) {
        this.brakes = brakes;
    }

    public float getThrottles() {
        return throttles;
    }

    public void setThrottles(float throttles) {
        this.throttles = throttles;
    }

    public String getGPSTime() {
        return GPSTime;
    }

    public void setGPSTime(String GPSTime) {
        this.GPSTime = GPSTime;
    }

    public String getDirect() {
        return Direct;
    }

    public void setDirect(String direct) {
        Direct = direct;
    }

    public String getSquadDate() {
        return SquadDate;
    }

    public void setSquadDate(String squadDate) {
        SquadDate = squadDate;
    }

    public String getFuel() {
        return Fuel;
    }

    public void setFuel(String fuel) {
        Fuel = fuel;
    }

    public String getMileage() {
        return Mileage;
    }

    public void setMileage(String mileage) {
        Mileage = mileage;
    }

    public String getTemperature() {
        return Temperature;
    }

    public void setTemperature(String temperature) {
        Temperature = temperature;
    }

    public String getFuel2() {
        return Fuel2;
    }

    public void setFuel2(String fuel2) {
        Fuel2 = fuel2;
    }

    public Object getInput1() {
        return Input1;
    }

    public void setInput1(Object input1) {
        Input1 = input1;
    }

    public Object getInput2() {
        return Input2;
    }

    public void setInput2(Object input2) {
        Input2 = input2;
    }

    public Object getInput3() {
        return Input3;
    }

    public void setInput3(Object input3) {
        Input3 = input3;
    }

    public Object getInput4() {
        return Input4;
    }

    public void setInput4(Object input4) {
        Input4 = input4;
    }

    public Object getInput5() {
        return Input5;
    }

    public void setInput5(Object input5) {
        Input5 = input5;
    }

    public Object getOutput1() {
        return Output1;
    }

    public void setOutput1(Object output1) {
        Output1 = output1;
    }

    public Object getOutput2() {
        return Output2;
    }

    public void setOutput2(Object output2) {
        Output2 = output2;
    }

    public Object getOutput3() {
        return Output3;
    }

    public void setOutput3(Object output3) {
        Output3 = output3;
    }

    public Object getOutput4() {
        return Output4;
    }

    public void setOutput4(Object output4) {
        Output4 = output4;
    }

    public Object getOutput5() {
        return Output5;
    }

    public void setOutput5(Object output5) {
        Output5 = output5;
    }

    public String getServerTime() {
        return ServerTime;
    }

    public void setServerTime(String serverTime) {
        ServerTime = serverTime;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceImei() {
        return deviceImei;
    }

    public void setDeviceImei(String deviceImei) {
        this.deviceImei = deviceImei;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getLastGpsTime() {
        return lastGpsTime;
    }

    public void setLastGpsTime(String lastGpsTime) {
        this.lastGpsTime = lastGpsTime;
    }

    public Long getLastMotionStatus() {
        return lastMotionStatus;
    }

    public void setLastMotionStatus(Long lastMotionStatus) {
        this.lastMotionStatus = lastMotionStatus;
    }

    public String getLastDeviceVol() {
        return lastDeviceVol;
    }

    public void setLastDeviceVol(String lastDeviceVol) {
        this.lastDeviceVol = lastDeviceVol;
    }

    public Float getHeading() {
        return heading;
    }

    public void setHeading(Float heading) {
        this.heading = heading;
    }

    public Float getOdometer() {
        return odometer;
    }

    public void setOdometer(Float odometer) {
        this.odometer = odometer;
    }

    public boolean isOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(boolean onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public Integer getEngineStatus() {
        return engineStatus;
    }

    public void setEngineStatus(Integer engineStatus) {
        this.engineStatus = engineStatus;
    }

    public Integer getAirConditionStatus() {
        return airConditionStatus;
    }

    public void setAirConditionStatus(Integer airConditionStatus) {
        this.airConditionStatus = airConditionStatus;
    }

    public Integer getDoorStatus() {
        return doorStatus;
    }

    public void setDoorStatus(Integer doorStatus) {
        this.doorStatus = doorStatus;
    }

    public Integer getHeadlightStatus() {
        return headlightStatus;
    }

    public void setHeadlightStatus(Integer headlightStatus) {
        this.headlightStatus = headlightStatus;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getBindMobile() {
        return bindMobile;
    }

    public void setBindMobile(String bindMobile) {
        this.bindMobile = bindMobile;
    }

    public long getuId() {
        return uId;
    }

    public void setuId(long uId) {
        this.uId = uId;
    }

    public long gettId() {
        return tId;
    }

    public void settId(long tId) {
        this.tId = tId;
    }

    public Double getLastLongitude() {
        return lastLongitude;
    }

    public void setLastLongitude(Double lastLongitude) {
        this.lastLongitude = lastLongitude;
    }

    public Double getLastLatitude() {
        return lastLatitude;
    }

    public void setLastLatitude(Double lastLatitude) {
        this.lastLatitude = lastLatitude;
    }

    public Integer getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(Integer deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public Integer getActivated() {
        return activated;
    }

    public void setActivated(Integer activated) {
        this.activated = activated;
    }

    public String getActivatedDatetime() {
        return activatedDatetime;
    }

    public void setActivatedDatetime(String activatedDatetime) {
        this.activatedDatetime = activatedDatetime;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getGuaranteeDate() {
        return guaranteeDate;
    }

    public void setGuaranteeDate(String guaranteeDate) {
        this.guaranteeDate = guaranteeDate;
    }

    public String getBindDatetime() {
        return bindDatetime;
    }

    public void setBindDatetime(String bindDatetime) {
        this.bindDatetime = bindDatetime;
    }

    public String getUnbindDatetime() {
        return unbindDatetime;
    }

    public void setUnbindDatetime(String unbindDatetime) {
        this.unbindDatetime = unbindDatetime;
    }

    public String getLoseCreatetime() {
        return loseCreatetime;
    }

    public void setLoseCreatetime(String loseCreatetime) {
        this.loseCreatetime = loseCreatetime;
    }

    public String getRetrieveCreatetime() {
        return retrieveCreatetime;
    }

    public void setRetrieveCreatetime(String retrieveCreatetime) {
        this.retrieveCreatetime = retrieveCreatetime;
    }

    public Integer getLocationType() {
        return locationType;
    }

    public void setLocationType(Integer locationType) {
        this.locationType = locationType;
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

    public Integer getPositionType() {
        return positionType;
    }

    public void setPositionType(Integer positionType) {
        this.positionType = positionType;
    }

    public Integer getPositionInterval() {
        return positionInterval;
    }

    public void setPositionInterval(Integer positionInterval) {
        this.positionInterval = positionInterval;
    }

    public String getDeviceRemark() {
        return deviceRemark;
    }

    public void setDeviceRemark(String deviceRemark) {
        this.deviceRemark = deviceRemark;
    }

    public Integer getRaceStatus() {
        return raceStatus;
    }

    public void setRaceStatus(Integer raceStatus) {
        this.raceStatus = raceStatus;
    }
}
