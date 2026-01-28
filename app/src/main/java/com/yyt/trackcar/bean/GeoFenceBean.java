package com.yyt.trackcar.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @ projectName:
 * @ packageName:   com.yyt.trackcar.bean
 * @ fileName:      GeoFenceBean
 * @ author:        QING
 * @ createTime:    6/20/21 14:18
 * @ describe:      TODO
 */
public class GeoFenceBean implements Parcelable {

    private String fenceName;

    /** 围栏id */
    private Long fenceId;

    /** 用户id */
    private Long userId;

    /** 设备id */
    private Long deviceId;

    /** 经度 */
    private Float longitude;

    /** 纬度 */
    private Float latitude;

    /** 半径 */
    private Long radius;

    /** 状态 1 启用 0 未启用 */
    private Integer status;

    private String createTime;

    private String updateTime;

    protected GeoFenceBean(Parcel in) {
        fenceName = in.readString();
        if (in.readByte() == 0) {
            fenceId = null;
        } else {
            fenceId = in.readLong();
        }
        if (in.readByte() == 0) {
            userId = null;
        } else {
            userId = in.readLong();
        }
        if (in.readByte() == 0) {
            deviceId = null;
        } else {
            deviceId = in.readLong();
        }
        if (in.readByte() == 0) {
            longitude = null;
        } else {
            longitude = in.readFloat();
        }
        if (in.readByte() == 0) {
            latitude = null;
        } else {
            latitude = in.readFloat();
        }
        if (in.readByte() == 0) {
            radius = null;
        } else {
            radius = in.readLong();
        }
        if (in.readByte() == 0) {
            status = null;
        } else {
            status = in.readInt();
        }
        createTime = in.readString();
        updateTime = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fenceName);
        if (fenceId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(fenceId);
        }
        if (userId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(userId);
        }
        if (deviceId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(deviceId);
        }
        if (longitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeFloat(longitude);
        }
        if (latitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeFloat(latitude);
        }
        if (radius == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(radius);
        }
        if (status == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(status);
        }
        dest.writeString(createTime);
        dest.writeString(updateTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GeoFenceBean> CREATOR = new Creator<GeoFenceBean>() {
        @Override
        public GeoFenceBean createFromParcel(Parcel in) {
            return new GeoFenceBean(in);
        }

        @Override
        public GeoFenceBean[] newArray(int size) {
            return new GeoFenceBean[size];
        }
    };

    public String getFenceName() {
        return fenceName;
    }

    public void setFenceName(String fenceName) {
        this.fenceName = fenceName;
    }

    public Long getFenceId() {
        return fenceId;
    }

    public void setFenceId(Long fenceId) {
        this.fenceId = fenceId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Long getRadius() {
        return radius;
    }

    public void setRadius(Long radius) {
        this.radius = radius;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public static Creator<GeoFenceBean> getCREATOR() {
        return CREATOR;
    }

    public GeoFenceBean() {
    }
}
