package com.yyt.trackcar.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.bean
 * @ fileName:      UserBean
 * @ author:        QING
 * @ createTime:    2020/3/26 17:25
 * @ describe:      TODO 用户对象
 */
public class UserBean implements Parcelable {
    private long user_id; // 绑定的用户id ，   转让管理员需要用到这个
    private String name; // 名称
    private String imei; // 绑定imei
    private String url; // 头像地址
    private int status; // 0不是管理员1是管理员
    private long id; // id
    private long timestamp; //
    private boolean isMe; // 是否自己
    private int imgDrawable; // 头像

    protected UserBean(Parcel in) {
        user_id = in.readLong();
        name = in.readString();
        imei = in.readString();
        url = in.readString();
        status = in.readInt();
        id = in.readLong();
        timestamp = in.readLong();
        isMe = in.readByte() != 0;
        imgDrawable = in.readInt();
    }

    public static final Creator<UserBean> CREATOR = new Creator<UserBean>() {
        @Override
        public UserBean createFromParcel(Parcel in) {
            return new UserBean(in);
        }

        @Override
        public UserBean[] newArray(int size) {
            return new UserBean[size];
        }
    };

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isMe() {
        return isMe;
    }

    public void setMe(boolean me) {
        isMe = me;
    }

    public int getImgDrawable() {
        return imgDrawable;
    }

    public void setImgDrawable(int imgDrawable) {
        this.imgDrawable = imgDrawable;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(user_id);
        dest.writeString(name);
        dest.writeString(imei);
        dest.writeString(url);
        dest.writeInt(status);
        dest.writeLong(id);
        dest.writeLong(timestamp);
        dest.writeByte((byte) (isMe ? 1 : 0));
        dest.writeInt(imgDrawable);
    }

    public UserBean() {
    }
}
