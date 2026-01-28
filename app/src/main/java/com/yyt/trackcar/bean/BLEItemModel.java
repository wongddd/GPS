package com.yyt.trackcar.bean;

import com.polidea.rxandroidble2.RxBleDevice;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.bean
 * @ fileName:      BLEItemModel
 * @ author:        QING
 * @ createTime:    2023/8/1 17:32
 * @ describe:      TODO 蓝牙选项对象
 */
public class BLEItemModel {

    private String imei; // 设备号
    private String macAddress; // mac地址
    private RxBleDevice bleDevice; // 蓝牙对象
    private String connectTime; // 连接时间
    private String disconnectTime; // 断开时间
    private int locationCount; // 定位点数
    private int uploadCount; // 上传点数
    private String status; // 传输状态
    private int statusType; // 传输类型
    private BLEDataModel configModel; // 配置对象

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public RxBleDevice getBleDevice() {
        return bleDevice;
    }

    public void setBleDevice(RxBleDevice bleDevice) {
        this.bleDevice = bleDevice;
    }

    public String getConnectTime() {
        return connectTime;
    }

    public void setConnectTime(String connectTime) {
        this.connectTime = connectTime;
    }

    public String getDisconnectTime() {
        return disconnectTime;
    }

    public void setDisconnectTime(String disconnectTime) {
        this.disconnectTime = disconnectTime;
    }

    public int getLocationCount() {
        return locationCount;
    }

    public void setLocationCount(int locationCount) {
        this.locationCount = locationCount;
    }

    public int getUploadCount() {
        return uploadCount;
    }

    public void setUploadCount(int uploadCount) {
        this.uploadCount = uploadCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getStatusType() {
        return statusType;
    }

    public void setStatusType(int statusType) {
        this.statusType = statusType;
    }

    public BLEDataModel getConfigModel() {
        return configModel;
    }

    public void setConfigModel(BLEDataModel configModel) {
        this.configModel = configModel;
    }

}
