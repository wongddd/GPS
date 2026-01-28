package com.yyt.trackcar.bean;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.bean
 * @ fileName:      BLELocationSubDataModel
 * @ author:        QING
 * @ createTime:    2023/6/5 14:40
 * @ describe:      TODO 定位数据子对象
 */
public class BLELocationSubDataModel {

    private String device; // 设备名
    private String rid; // 赛场ID
    private BLELocationRowModel rows;

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public BLELocationRowModel getRows() {
        return rows;
    }

    public void setRows(BLELocationRowModel rows) {
        this.rows = rows;
    }

}
