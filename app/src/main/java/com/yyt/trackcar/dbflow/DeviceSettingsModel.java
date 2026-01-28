package com.yyt.trackcar.dbflow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.dbflow
 * @ fileName:      DeviceSettingsModel
 * @ author:        QING
 * @ createTime:    2020/3/25 11:01
 * @ describe:      TODO 设备设置信息
 */
@Table(database = AppDataBase.class)
public class DeviceSettingsModel extends BaseModel {
    @Column
    @PrimaryKey
    private String imei; // 设备imei

    @Column
    @PrimaryKey
    private long u_id; // 用户id

    @Column
    private String ip; // 设备IP地址

    @Column
    private String locationMode; // 定位模式

    @Column
    private String wifi; // 家庭wifi

    @Column
    private int wifiType; // 家庭wifi验证类型

    @Column
    private String disabledInClass; // 上课禁用

    @Column
    private String automaticAnswer; // 自动接听

    @Column
    private String other; // 其他设置

    @Column
    private String loss; // 挂失

    @Column
    private String dial_pad; // 拨号键盘开关

    @Column
    private String alarm_clock; // 闹钟设置

    @Column
    private String step; // APP设置的目标步数

    @Column
    private String devicestep; // 设备今日步数

    @Column
    private String wifiStatus; // 有新版本自动安装  1打开 0关闭

    @Column
    private String webTraffic; // 是否打开流量下载  1打开  0关闭

    @Column
    private String phonebook; // 通讯录

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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getLocationMode() {
        return locationMode;
    }

    public void setLocationMode(String locationMode) {
        this.locationMode = locationMode;
    }

    public String getWifi() {
        return wifi;
    }

    public void setWifi(String wifi) {
        this.wifi = wifi;
    }

    public int getWifiType() {
        return wifiType;
    }

    public void setWifiType(int wifiType) {
        this.wifiType = wifiType;
    }

    public String getDisabledInClass() {
        return disabledInClass;
    }

    public void setDisabledInClass(String disabledInClass) {
        this.disabledInClass = disabledInClass;
    }

    public String getAutomaticAnswer() {
        return automaticAnswer;
    }

    public void setAutomaticAnswer(String automaticAnswer) {
        this.automaticAnswer = automaticAnswer;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public String getLoss() {
        return loss;
    }

    public void setLoss(String loss) {
        this.loss = loss;
    }

    public String getDial_pad() {
        return dial_pad;
    }

    public void setDial_pad(String dial_pad) {
        this.dial_pad = dial_pad;
    }

    public String getAlarm_clock() {
        return alarm_clock;
    }

    public void setAlarm_clock(String alarm_clock) {
        this.alarm_clock = alarm_clock;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public String getDevicestep() {
        return devicestep;
    }

    public void setDevicestep(String devicestep) {
        this.devicestep = devicestep;
    }

    public String getWifiStatus() {
        return wifiStatus;
    }

    public void setWifiStatus(String wifiStatus) {
        this.wifiStatus = wifiStatus;
    }

    public String getWebTraffic() {
        return webTraffic;
    }

    public void setWebTraffic(String webTraffic) {
        this.webTraffic = webTraffic;
    }

    public String getPhonebook() {
        return phonebook;
    }

    public void setPhonebook(String phonebook) {
        this.phonebook = phonebook;
    }

}
