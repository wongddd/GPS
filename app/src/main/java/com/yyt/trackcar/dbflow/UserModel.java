package com.yyt.trackcar.dbflow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.dbflow
 * @ fileName:      AAAUserModel
 * @ author:        QING
 * @ createTime:    2020-02-24 17:05
 * @ describe:      TODO 用户
 */
@Table(database = AppDataBase.class)
public class UserModel extends BaseModel {

    @Column
    @PrimaryKey
    private long u_id; // 用户id

    @Column
    private String token; // 用户token
    @Column
    private String rongyun_token; // 用户唯一在融云  token并且永久有效 head，rongyun_token为空或者为0或者为null
    // 都说明APP用户没有在融云注册激活过
    @Column
    private String head; // 头像
    @Column
    private String id; //
    @Column
    private String selectImei; // 当前选中imei

    private String name; // 名称

    public long getU_id() {
        return u_id;
    }

    public void setU_id(long u_id) {
        this.u_id = u_id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRongyun_token() {
        return rongyun_token;
    }

    public void setRongyun_token(String rongyun_token) {
        this.rongyun_token = rongyun_token;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSelectImei() {
        return selectImei;
    }

    public void setSelectImei(String selectImei) {
        this.selectImei = selectImei;
    }
}
