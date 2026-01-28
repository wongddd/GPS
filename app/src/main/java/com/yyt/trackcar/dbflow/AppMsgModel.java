package com.yyt.trackcar.dbflow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.dbflow
 * @ fileName:      AppMsgModel
 * @ author:        QING
 * @ createTime:    2020/4/29 18:25
 * @ describe:      TODO 用户系统消息对象
 */
@Table(database = AppDataBase.class)
public class AppMsgModel extends BaseModel {

    @Column
    @PrimaryKey
    private long u_id; // 用户id

    @Column
    @PrimaryKey
    private int id; // 消息id

    @Column
    private String imei; // 设备imei

    @Column
    private long createtime; // 时间

    @Column
    private int type; // 类型

    @Column
    private long send_id; // 发送用户id

    @Column
    private String remark; // 备注

    public long getU_id() {
        return u_id;
    }

    public void setU_id(long u_id) {
        this.u_id = u_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getSend_id() {
        return send_id;
    }

    public void setSend_id(long send_id) {
        this.send_id = send_id;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
