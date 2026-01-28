package com.yyt.trackcar.dbflow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = AppDataBase.class)
public class PigeonModel extends BaseModel {

    /**
     * Imei编号
     */
    @Column
    @PrimaryKey
    private String number;

    /**
     * 用户ID
     */
    @Column
    private String userId;

    /**
     * 昵称
     */
    @Column
    private String nickname;

    /**
     * 羽色
     */
    @Column
    private String color;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
