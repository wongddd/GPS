package com.yyt.trackcar.bean;

import java.io.Serializable;

/**
 * 项目名：   传信鸽
 * 包名：     com.yyt.trackcar.bean
 * 文件名：   AAACarGpsBean
 * 创建者：   QING
 * 创建时间： 2018/4/21 15:53
 * 描述：     TODO 车辆设备类
 */

public class AAACarGpsBean implements Serializable {
    private static final long serialVersionUID = 9103623319690241655L;
    private String id; // ID
    // 设备号 。默认情况与 IMEI 相同
    private String DeviceNO;
    // 设备装置，默认 空
    private String DeviceModel;
    // 设备手机号码
    private String TELNO;
    // 是否已有账户绑定
    private String IsBind;
    // 注释
    private String Remark;
    // 状态
    /// AUTOSTART GPRS upload status when ACC switch to ON
    //AUTOSTOP GPRS upload status when ACC switch to OFF
    //AUTO ACC ON
    //AUTOLOW ACC OFF and vehicle stop
    //TOWED ACC OFF and vehicle Move
    //CALL CALL alert
    //SOS SOS alarm
    //DEF Cut Power alarm
    //HT High Temperature alert
    //BLP Backup battery low voltage
    //CLP Car Battery low voltage
    //OS Out of the Geo-fence alarm
    //RS Enter the Geo-fence alarm
    //OVERSPEED Overs-peed alarm
    //SAFESPEED Safe-speed alarm
    private String State;
    // 国家
    private String Country;
    // 主控 用户 ID
    private String uid;
    // 设备类型
    private String DeviceType;
    // IMEI 设备号
    private String imei;
    // 纬度
    private String lat;
    // 经度
    private String lon;
    // 最后一次 经纬度 日期
    private String gps_date;
    // 最后一次 经纬度 时间
    private String gpstime;
    // 剩余电量
    private String vol;
    // 手机卡信号强度
    private String sms;
    // 设备密码
    private String DevicePassword;
    // 有效期
    private String ExpireDate;

    private String Name; // 名称
    private String alarm; // 报警提醒 0x01 是否启用，0x02 声音、0x04 震动
    private String online; // 在线状态
    private String ACC; // ACC开关
    private String SpeedOverGroud; // 速度
    private String CourseOverGround; // 角度
    private String oldDevicePassword; // driving
    private String lndicator_N_S; //
    private String Indicator_E_W; //
    private String last_upload_datetime; // 最后请求时间
    private String Contact; // 联系人
    private String Contact_telno; // 联系人手机号码
    private String gps_alarm; // 最后一条报警信息
    private Boolean isSelect; // 是否选中
    private String residence; // 停留时间

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeviceNO() {
        return DeviceNO;
    }

    public void setDeviceNO(String deviceNO) {
        DeviceNO = deviceNO;
    }

    public String getDeviceModel() {
        return DeviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        DeviceModel = deviceModel;
    }

    public String getTELNO() {
        return TELNO;
    }

    public void setTELNO(String TELNO) {
        this.TELNO = TELNO;
    }

    public String getIsBind() {
        return IsBind;
    }

    public void setIsBind(String isBind) {
        IsBind = isBind;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getDeviceType() {
        return DeviceType;
    }

    public void setDeviceType(String deviceType) {
        DeviceType = deviceType;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getGpstime() {
        return gpstime;
    }

    public void setGpstime(String gpstime) {
        this.gpstime = gpstime;
    }

    public String getVol() {
        return vol;
    }

    public void setVol(String vol) {
        this.vol = vol;
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    public String getDevicePassword() {
        return DevicePassword;
    }

    public void setDevicePassword(String devicePassword) {
        DevicePassword = devicePassword;
    }

    public String getExpireDate() {
        return ExpireDate;
    }

    public void setExpireDate(String expireDate) {
        ExpireDate = expireDate;
    }

    public String getCourseOverGround() {
        return CourseOverGround;
    }

    public void setCourseOverGround(String courseOverGround) {
        CourseOverGround = courseOverGround;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAlarm() {
        return alarm;
    }

    public void setAlarm(String alarm) {
        this.alarm = alarm;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getACC() {
        return ACC;
    }

    public void setACC(String ACC) {
        this.ACC = ACC;
    }

    public String getSpeedOverGroud() {
        return SpeedOverGroud;
    }

    public void setSpeedOverGroud(String speedOverGroud) {
        SpeedOverGroud = speedOverGroud;
    }

    public String getOldDevicePassword() {
        return oldDevicePassword;
    }

    public void setOldDevicePassword(String oldDevicePassword) {
        this.oldDevicePassword = oldDevicePassword;
    }

    public String getLndicator_N_S() {
        return lndicator_N_S;
    }

    public void setLndicator_N_S(String lndicator_N_S) {
        this.lndicator_N_S = lndicator_N_S;
    }

    public String getIndicator_E_W() {
        return Indicator_E_W;
    }

    public void setIndicator_E_W(String indicator_E_W) {
        Indicator_E_W = indicator_E_W;
    }

    public String getLast_upload_datetime() {
        return last_upload_datetime;
    }

    public void setLast_upload_datetime(String last_upload_datetime) {
        this.last_upload_datetime = last_upload_datetime;
    }

    public String getContact() {
        return Contact;
    }

    public void setContact(String contact) {
        Contact = contact;
    }

    public String getContact_telno() {
        return Contact_telno;
    }

    public void setContact_telno(String contact_telno) {
        Contact_telno = contact_telno;
    }

    public String getGps_alarm() {
        return gps_alarm;
    }

    public void setGps_alarm(String gps_alarm) {
        this.gps_alarm = gps_alarm;
    }

    public boolean getSelect() {
        return isSelect == null ? false : isSelect;
    }

    public void setSelect(Boolean select) {
        isSelect = select;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getGps_date() {
        return gps_date;
    }

    public void setGps_date(String gps_date) {
        this.gps_date = gps_date;
    }

    public String getResidence() {
        return residence;
    }

    public void setResidence(String residence) {
        this.residence = residence;
    }
}
