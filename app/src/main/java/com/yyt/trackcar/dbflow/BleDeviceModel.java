package com.yyt.trackcar.dbflow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.dbflow
 * @ fileName:      BleDeviceModel
 * @ author:        QING
 * @ createTime:    2023/8/2 13:54
 * @ describe:      TODO 蓝牙设备对象
 */
@Table(database = AppDataBase.class)
public class BleDeviceModel extends BaseModel {

    @Column
    @PrimaryKey
    private String macAddress; // mac地址
    @Column
    @PrimaryKey
    private String matchId; // 读取的赛场ID

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

}
