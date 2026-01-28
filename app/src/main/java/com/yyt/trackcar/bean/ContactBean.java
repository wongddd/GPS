package com.yyt.trackcar.bean;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.bean
 * @ fileName:      ContactBean
 * @ author:        QING
 * @ createTime:    2020/4/10 10:36
 * @ describe:      TODO 联系人对象
 */
public class ContactBean {
    private String name; // 名称
    private String phone; // 手机号
    private String shortNumber; // 短号
    private String portrait; // 头像
    private String sos; // SOS号码
    private int portraitId; //  头像ID
    private int bgDrawable; //  背景
    private String contactString; // 联系人

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getSos() {
        return sos;
    }

    public void setSos(String sos) {
        this.sos = sos;
    }

    public int getPortraitId() {
        return portraitId;
    }

    public void setPortraitId(int portraitId) {
        this.portraitId = portraitId;
    }

    public int getBgDrawable() {
        return bgDrawable;
    }

    public void setBgDrawable(int bgDrawable) {
        this.bgDrawable = bgDrawable;
    }

    public String getContactString() {
        return contactString;
    }

    public void setContactString(String contactString) {
        this.contactString = contactString;
    }

    public ContactBean() {
    }
}
