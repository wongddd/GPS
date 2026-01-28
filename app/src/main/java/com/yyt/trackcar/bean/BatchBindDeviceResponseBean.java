package com.yyt.trackcar.bean;

import com.google.android.gms.common.api.Batch;

public class BatchBindDeviceResponseBean {
    private String deviceImei;
    private boolean isBound;

    public String getDeviceImei() {
        return deviceImei;
    }

    public void setDeviceImei(String deviceImei) {
        this.deviceImei = deviceImei;
    }

    public boolean isBound() {
        return isBound;
    }

    public void setBound(boolean bound) {
        isBound = bound;
    }

    public BatchBindDeviceResponseBean deviceImei (String deviceImei) {
        this.deviceImei = deviceImei;
        return this;
    }

    public BatchBindDeviceResponseBean isBound (boolean isBound) {
        this.isBound = isBound;
        return this;
    }
}
