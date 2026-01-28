package com.yyt.trackcar.dbflow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.dbflow
 * @ fileName:      SmsModel
 * @ author:        QING
 * @ createTime:    2020/4/23 10:56
 * @ describe:      TODO 短信记录对象
 */
@Table(database = AppDataBase.class)
public class SmsModel extends BaseModel {
    @Column
    @PrimaryKey
    private String imei; // 设备imei

    @Column
    @PrimaryKey
    private String phone; // 电话

    @Column
    @PrimaryKey
    private String get_time; //发送或收到时间

    @Column
    private String rmsg; //短信内容

    @Column
    private int type; //短信类型  0收到 1发送

    private int bgDrawable; // 背景

    private boolean isSelect; // 是否选中

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String get_time() {
        return get_time;
    }

    public void setGet_time(String get_time) {
        this.get_time = get_time;
    }

    public String getRmsg() {
        return rmsg;
    }

    public void setRmsg(String rmsg) {
        this.rmsg = rmsg;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getBgDrawable() {
        return bgDrawable;
    }

    public void setBgDrawable(int bgDrawable) {
        this.bgDrawable = bgDrawable;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
