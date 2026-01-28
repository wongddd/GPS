package com.yyt.trackcar.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.bean
 * @ fileName:      DeviceVersionBean
 * @ author:        QING
 * @ createTime:    2020/3/26 11:38
 * @ describe:      TODO 设备版本对象
 */
public class DeviceVersionBean implements Parcelable {
    private long version; // 版本号
    private String url; // 地址
    private String description; // 描述
    private long createtime; // 创建时间
    private String dv; // 描述

    protected DeviceVersionBean(Parcel in) {
        version = in.readLong();
        url = in.readString();
        description = in.readString();
        createtime = in.readLong();
        dv = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(version);
        dest.writeString(url);
        dest.writeString(description);
        dest.writeLong(createtime);
        dest.writeString(dv);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DeviceVersionBean> CREATOR = new Creator<DeviceVersionBean>() {
        @Override
        public DeviceVersionBean createFromParcel(Parcel in) {
            return new DeviceVersionBean(in);
        }

        @Override
        public DeviceVersionBean[] newArray(int size) {
            return new DeviceVersionBean[size];
        }
    };

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }

    public String getDv() {
        return dv;
    }

    public void setDv(String dv) {
        this.dv = dv;
    }
}
