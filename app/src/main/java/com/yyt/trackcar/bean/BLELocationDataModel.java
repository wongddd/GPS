package com.yyt.trackcar.bean;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.bean
 * @ fileName:      BLELocationDataModel
 * @ author:        QING
 * @ createTime:    2023/6/5 14:40
 * @ describe:      TODO 定位数据对象
 */
public class BLELocationDataModel {

    private BLELocationSubDataModel data;

    public BLELocationSubDataModel getData() {
        return data;
    }

    public void setData(BLELocationSubDataModel data) {
        this.data = data;
    }
}
