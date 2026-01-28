package com.yyt.trackcar.bean;

/**
 * @ projectName:
 * @ packageName:   com.yyt.trackcar.bean
 * @ fileName:      OdometerBean
 * @ author:        QING
 * @ createTime:    9/27/21 14:20
 * @ describe:      TODO
 */
public class OdometerBean {
    private Long reportId;

    private String reportDate;

    private Long deviceId;

    private String deviceImei;

    private String deviceName;

    private Float odometer;

    private Float oilConsumption;

    private Long customerId;

    private Long deptId;

    private Long userId;

    private Float avgSpeed;

    private Float maxSpeed;

    private String tripStartDate;

    private String tripEndDate;

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public String getReportDate() {
        return reportDate;
    }

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceImei() {
        return deviceImei;
    }

    public void setDeviceImei(String deviceImei) {
        this.deviceImei = deviceImei;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public Float getOdometer() {
        return odometer;
    }

    public void setOdometer(Float odometer) {
        this.odometer = odometer;
    }

    public Float getOilConsumption() {
        return oilConsumption;
    }

    public void setOilConsumption(Float oilConsumption) {
        this.oilConsumption = oilConsumption;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Float getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(Float avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public Float getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(Float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public String getTripStartDate() {
        return tripStartDate;
    }

    public void setTripStartDate(String tripStartDate) {
        this.tripStartDate = tripStartDate;
    }

    public String getTripEndDate() {
        return tripEndDate;
    }

    public void setTripEndDate(String tripEndDate) {
        this.tripEndDate = tripEndDate;
    }
}
