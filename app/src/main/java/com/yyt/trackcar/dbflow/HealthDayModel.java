package com.yyt.trackcar.dbflow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * @ projectName:
 * @ packageName:   com.yyt.trackcar.dbflow
 * @ fileName:      HealthDayModel
 * @ author:        QING
 * @ createTime:    7/5/21 11:40
 * @ describe:      TODO
 */
@Table(database = AppDataBase.class)
public class HealthDayModel extends BaseModel {

    @Column
    @PrimaryKey
    private String imei; // 设备imei

    @Column
    @PrimaryKey
    private int type;

    @Column
    @PrimaryKey
    private String time;

    @Column
    private String msg;


    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
