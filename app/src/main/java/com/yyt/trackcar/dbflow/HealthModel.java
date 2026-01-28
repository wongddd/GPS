package com.yyt.trackcar.dbflow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * @ projectName:
 * @ packageName:   com.yyt.trackcar.dbflow
 * @ fileName:      HealthModel
 * @ author:        QING
 * @ createTime:    6/29/21 21:20
 * @ describe:      TODO
 */
@Table(database = AppDataBase.class)
public class HealthModel extends BaseModel {

    @Column
    @PrimaryKey
    private String imei; // 设备imei

    @Column
    private String body_temperature;

    @Column
    private int blood_oxygen;

    @Column
    private int heart_rate;

    @Column
    private int ersi_heart_rate;

    @Column
    private String blood_pressure;

    @Column
    private String heart_rate_upload_time;

    @Column
    private long heart_rate_system_time;

    @Column
    private String blood_oxygen_upload_time;

    @Column
    private long blood_oxygen_system_time;

    @Column
    private int calories;

    @Column
    private String km;

    @Column
    private long step_time;

    @Column
    private String heartRateTest;

    @Column
    private String fallOff;

    private String devicestep;

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getBody_temperature() {
        return body_temperature;
    }

    public void setBody_temperature(String body_temperature) {
        this.body_temperature = body_temperature;
    }

    public int getBlood_oxygen() {
        return blood_oxygen;
    }

    public void setBlood_oxygen(int blood_oxygen) {
        this.blood_oxygen = blood_oxygen;
    }

    public int getHeart_rate() {
        return heart_rate;
    }

    public void setHeart_rate(int heart_rate) {
        this.heart_rate = heart_rate;
    }

    public int getErsi_heart_rate() {
        return ersi_heart_rate;
    }

    public void setErsi_heart_rate(int ersi_heart_rate) {
        this.ersi_heart_rate = ersi_heart_rate;
    }

    public String getBlood_pressure() {
        return blood_pressure;
    }

    public void setBlood_pressure(String blood_pressure) {
        this.blood_pressure = blood_pressure;
    }

    public String getDevicestep() {
        return devicestep;
    }

    public void setDevicestep(String devicestep) {
        this.devicestep = devicestep;
    }

    public String getHeart_rate_upload_time() {
        return heart_rate_upload_time;
    }

    public void setHeart_rate_upload_time(String heart_rate_upload_time) {
        this.heart_rate_upload_time = heart_rate_upload_time;
    }

    public long getHeart_rate_system_time() {
        return heart_rate_system_time;
    }

    public void setHeart_rate_system_time(long heart_rate_system_time) {
        this.heart_rate_system_time = heart_rate_system_time;
    }

    public String getBlood_oxygen_upload_time() {
        return blood_oxygen_upload_time;
    }

    public void setBlood_oxygen_upload_time(String blood_oxygen_upload_time) {
        this.blood_oxygen_upload_time = blood_oxygen_upload_time;
    }

    public long getBlood_oxygen_system_time() {
        return blood_oxygen_system_time;
    }

    public void setBlood_oxygen_system_time(long blood_oxygen_system_time) {
        this.blood_oxygen_system_time = blood_oxygen_system_time;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public String getKm() {
        return km;
    }

    public void setKm(String km) {
        this.km = km;
    }

    public long getStep_time() {
        return step_time;
    }

    public void setStep_time(long step_time) {
        this.step_time = step_time;
    }

    public String getHeartRateTest() {
        return heartRateTest;
    }

    public void setHeartRateTest(String heartRateTest) {
        this.heartRateTest = heartRateTest;
    }

    public String getFallOff() {
        return fallOff;
    }

    public void setFallOff(String fallOff) {
        this.fallOff = fallOff;
    }
}
