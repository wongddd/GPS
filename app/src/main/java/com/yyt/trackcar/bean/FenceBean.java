package com.yyt.trackcar.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.bean
 * @ fileName:      FenceBean
 * @ author:        QING
 * @ createTime:    2020/4/9 06:57
 * @ describe:      TODO 电子围栏对象
 */
public class FenceBean implements Parcelable {

    private long id; // id
    private String fenceName; // 围栏名称
    private String lat; // 纬度
    private String lng; // 经度
    private int Radius; // 半径
    private int entry; // 进电子围栏 1开 0关
    private int exit; // 出电子围栏 1开 0关
    private int enable; // 电子围栏开关 1开 0关
    private int bgDrawable; // 背景
    private boolean select; // 是否选中

    protected FenceBean(Parcel in) {
        id = in.readLong();
        fenceName = in.readString();
        lat = in.readString();
        lng = in.readString();
        Radius = in.readInt();
        entry = in.readInt();
        exit = in.readInt();
        enable = in.readInt();
        bgDrawable = in.readInt();
        select = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(fenceName);
        dest.writeString(lat);
        dest.writeString(lng);
        dest.writeInt(Radius);
        dest.writeInt(entry);
        dest.writeInt(exit);
        dest.writeInt(enable);
        dest.writeInt(bgDrawable);
        dest.writeByte((byte) (select ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FenceBean> CREATOR = new Creator<FenceBean>() {
        @Override
        public FenceBean createFromParcel(Parcel in) {
            return new FenceBean(in);
        }

        @Override
        public FenceBean[] newArray(int size) {
            return new FenceBean[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFenceName() {
        return fenceName;
    }

    public void setFenceName(String fenceName) {
        this.fenceName = fenceName;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public int getRadius() {
        return Radius;
    }

    public void setRadius(int radius) {
        Radius = radius;
    }

    public int getEntry() {
        return entry;
    }

    public void setEntry(int entry) {
        this.entry = entry;
    }

    public int getExit() {
        return exit;
    }

    public void setExit(int exit) {
        this.exit = exit;
    }

    public int getEnable() {
        return enable;
    }

    public void setEnable(int enable) {
        this.enable = enable;
    }

    public int getBgDrawable() {
        return bgDrawable;
    }

    public void setBgDrawable(int bgDrawable) {
        this.bgDrawable = bgDrawable;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public FenceBean() {
    }
}
