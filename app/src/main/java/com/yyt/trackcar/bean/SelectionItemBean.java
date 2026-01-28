package com.yyt.trackcar.bean;

import com.yyt.trackcar.dbflow.AAADeviceModel;

/**
 * adapter使用带选择状态的列表
 */
public class SelectionItemBean {
    private AAADeviceModel deviceModel;
    private boolean selected;

    public SelectionItemBean (AAADeviceModel deviceModel, boolean selected) {
        this.deviceModel = deviceModel;
        this.selected = selected;
    }

    public AAADeviceModel getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(AAADeviceModel deviceModel) {
        this.deviceModel = deviceModel;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
