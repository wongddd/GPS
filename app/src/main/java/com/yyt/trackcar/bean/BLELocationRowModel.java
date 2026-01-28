package com.yyt.trackcar.bean;

import java.util.List;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.bean
 * @ fileName:      BLELocationRowModel
 * @ author:        QING
 * @ createTime:    2023/6/5 14:48
 * @ describe:      TODO 定位数据列表对象
 */
public class BLELocationRowModel {

    private List<BLELocationModel> row;

    public List<BLELocationModel> getRow() {
        return row;
    }

    public void setRow(List<BLELocationModel> row) {
        this.row = row;
    }
}
