package com.yyt.trackcar.dbflow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.dbflow
 * @ fileName:      UserSettingsModel
 * @ author:        QING
 * @ createTime:    2020/4/13 16:55
 * @ describe:      TODO 用户设置对象
 */
@Table(database = AppDataBase.class)
public class UserSettingsModel extends BaseModel {

    @Column
    @PrimaryKey
    private long u_id; // 用户id

    @Column
    private int arriveHome; // 到家

    @Column
    private int sos; // SOS

    @Column
    private int location; // 定位

    @Column
    private int addFriend; // 添加朋友

    @Column
    private int step; // 计步

    @Column
    private int uploadPhoto; // 上传图片

    @Column
    private int phoneLog; // 通话日志

    @Column
    private int cost; // 短信

    @Column
    private int upgrade; // 升级

    @Column
    private int fence; // 电子围栏

    @Column
    private int phoneVoice; // 手机铃声

    @Column
    private int phoneVibration; // 手机震动

    public long getU_id() {
        return u_id;
    }

    public void setU_id(long u_id) {
        this.u_id = u_id;
    }

    public int getArriveHome() {
        return arriveHome;
    }

    public void setArriveHome(int arriveHome) {
        this.arriveHome = arriveHome;
    }

    public int getSos() {
        return sos;
    }

    public void setSos(int sos) {
        this.sos = sos;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public int getAddFriend() {
        return addFriend;
    }

    public void setAddFriend(int addFriend) {
        this.addFriend = addFriend;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getUploadPhoto() {
        return uploadPhoto;
    }

    public void setUploadPhoto(int uploadPhoto) {
        this.uploadPhoto = uploadPhoto;
    }

    public int getPhoneLog() {
        return phoneLog;
    }

    public void setPhoneLog(int phoneLog) {
        this.phoneLog = phoneLog;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getUpgrade() {
        return upgrade;
    }

    public void setUpgrade(int upgrade) {
        this.upgrade = upgrade;
    }

    public int getFence() {
        return fence;
    }

    public void setFence(int fence) {
        this.fence = fence;
    }

    public int getPhoneVoice() {
        return phoneVoice;
    }

    public void setPhoneVoice(int phoneVoice) {
        this.phoneVoice = phoneVoice;
    }

    public int getPhoneVibration() {
        return phoneVibration;
    }

    public void setPhoneVibration(int phoneVibration) {
        this.phoneVibration = phoneVibration;
    }
}
