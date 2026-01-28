package com.yyt.trackcar.dbflow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.dbflow
 * @ fileName:      DeviceInfoModel
 * @ author:        QING
 * @ createTime:    2020/3/25 16:48
 * @ describe:      TODO 设备信息
 */
@Table(database = AppDataBase.class)
public class DeviceInfoModel extends BaseModel {
    @Column
    @PrimaryKey
    private String imei; // 设备imei

    @Column
    @PrimaryKey
    private long u_id; // 用户id

    @Column
    private long d_id; // 设备id

    @Column
    private long createtime; // 创建时间

    @Column
    private String birday; // 生日

    @Column
    private int sex; // 性别0女1男

    @Column
    private String weight; // 体重

    @Column
    private String height; // 身高

    @Column
    private String head; // 头像

    @Column
    private String nickname; // 昵称

    @Column
    private String school_age; // 年级

    @Column
    private String phone; // 电话

    @Column
    private String shortNumber; // 短号

    @Column
    private String familyNumber; // 家庭号

    @Column
    private String school_info; // 学校信息

    @Column
    private String home_info; // 家庭信息

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public long getU_id() {
        return u_id;
    }

    public void setU_id(long u_id) {
        this.u_id = u_id;
    }

    public long getD_id() {
        return d_id;
    }

    public void setD_id(long d_id) {
        this.d_id = d_id;
    }

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }

    public String getBirday() {
        return birday;
    }

    public void setBirday(String birday) {
        this.birday = birday;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSchool_age() {
        return school_age;
    }

    public void setSchool_age(String school_age) {
        this.school_age = school_age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getShortNumber() {
        return shortNumber;
    }

    public void setShortNumber(String shortNumber) {
        this.shortNumber = shortNumber;
    }

    public String getFamilyNumber() {
        return familyNumber;
    }

    public void setFamilyNumber(String familyNumber) {
        this.familyNumber = familyNumber;
    }

    public String getSchool_info() {
        return school_info;
    }

    public void setSchool_info(String school_info) {
        this.school_info = school_info;
    }

    public String getHome_info() {
        return home_info;
    }

    public void setHome_info(String home_info) {
        this.home_info = home_info;
    }

}
