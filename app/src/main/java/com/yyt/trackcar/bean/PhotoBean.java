package com.yyt.trackcar.bean;

import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.xuexiang.xui.widget.imageview.preview.enitity.IPreviewInfo;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.bean
 * @ fileName:      PhotoBean
 * @ author:        QING
 * @ createTime:    2020/4/16 18:27
 * @ describe:      TODO 图片对象
 */
public class PhotoBean implements IPreviewInfo {

    private long createtime; // 创建时间
    private String name; // 名称
    private String url; // 地址
    private Rect mBounds; // 记录坐标
    private String imei; // imei
    private boolean isSelect; // 是否选中

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setBounds(Rect mBounds) {
        this.mBounds = mBounds;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public Rect getBounds() {
        return mBounds;
    }

    @Nullable
    @Override
    public String getVideoUrl() {
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(name);
        dest.writeParcelable(mBounds, flags);
        dest.writeLong(createtime);
        dest.writeString(imei);
        dest.writeByte((byte) (isSelect ? 1 : 0));
    }

    protected PhotoBean(Parcel in) {
        url = in.readString();
        name = in.readString();
        mBounds = in.readParcelable(Rect.class.getClassLoader());
        createtime = in.readLong();
        imei = in.readString();
        isSelect = in.readByte() != 0;
    }

    public static final Parcelable.Creator<PhotoBean> CREATOR = new Parcelable.Creator<PhotoBean>() {
        @Override
        public PhotoBean createFromParcel(Parcel source) {
            return new PhotoBean(source);
        }

        @Override
        public PhotoBean[] newArray(int size) {
            return new PhotoBean[size];
        }
    };

    public PhotoBean() {
    }

    public PhotoBean(String url) {
        this.url = url;
    }
}
