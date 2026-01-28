package com.yyt.trackcar.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 信鸽比赛对象 gps_pigeon_race
 * 
 * @author ruoyi
 * @date 2022-01-13
 */
public class GpsPigeonRaceBean implements Parcelable {
    private static final long serialVersionUID = 1L;

    public GpsPigeonRaceBean() {
    }

    /** $column.columnComment */
    private Long id;

    /** 比赛名称 */
    private String pigeonRaceName;

    /**
     * 配置信息
     */
    private String configName;

    /**
     * 配置文件
     */
    private Long raceconfigId;

    /** 比赛日期 */
    private Long pigeonRaceDate;

    /** 创建日期 */
    private Long createDatetime;

    /** 创建人 */
    private Long creater;

    /** 状态 0 禁用 1 启用 */
    private Long raceStatus;

    /** 有效日期 */
    private Long raceExpiryDate;

    /** 默认参数 0 禁用 1 启用 */
    private Long raceDefault;

    /** 起点纬度 */
    private Double starLon;

    /** 起点经度 */
    private Double starLat;

    /** 终点纬度 */
    private Double endLon;

    /** 终点经度 */
    private Double endLat;


    protected GpsPigeonRaceBean(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        pigeonRaceName = in.readString();
        configName = in.readString();
        if (in.readByte() == 0) {
            raceconfigId = null;
        } else {
            raceconfigId = in.readLong();
        }
        if (in.readByte() == 0) {
            pigeonRaceDate = null;
        } else {
            pigeonRaceDate = in.readLong();
        }
        if (in.readByte() == 0) {
            createDatetime = null;
        } else {
            createDatetime = in.readLong();
        }
        if (in.readByte() == 0) {
            creater = null;
        } else {
            creater = in.readLong();
        }
        if (in.readByte() == 0) {
            raceStatus = null;
        } else {
            raceStatus = in.readLong();
        }
        if (in.readByte() == 0) {
            raceExpiryDate = null;
        } else {
            raceExpiryDate = in.readLong();
        }
        if (in.readByte() == 0) {
            raceDefault = null;
        } else {
            raceDefault = in.readLong();
        }
        if (in.readByte() == 0) {
            starLon = null;
        } else {
            starLon = in.readDouble();
        }
        if (in.readByte() == 0) {
            starLat = null;
        } else {
            starLat = in.readDouble();
        }
        if (in.readByte() == 0) {
            endLon = null;
        } else {
            endLon = in.readDouble();
        }
        if (in.readByte() == 0) {
            endLat = null;
        } else {
            endLat = in.readDouble();
        }
    }

    public static final Creator<GpsPigeonRaceBean> CREATOR = new Creator<GpsPigeonRaceBean>() {
        @Override
        public GpsPigeonRaceBean createFromParcel(Parcel in) {
            return new GpsPigeonRaceBean(in);
        }

        @Override
        public GpsPigeonRaceBean[] newArray(int size) {
            return new GpsPigeonRaceBean[size];
        }
    };

    public Double getStarLon() {
        return starLon;
    }

    public void setStarLon(Double starLon) {
        this.starLon = starLon;
    }

    public Double getStarLat() {
        return starLat;
    }

    public void setStarLat(Double starLat) {
        this.starLat = starLat;
    }

    public Double getEndLon() {
        return endLon;
    }

    public void setEndLon(Double endLon) {
        this.endLon = endLon;
    }

    public Double getEndLat() {
        return endLat;
    }

    public void setEndLat(Double endLat) {
        this.endLat = endLat;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPigeonRaceName() {
        return pigeonRaceName;
    }

    public void setPigeonRaceName(String pigeonRaceName) {
        this.pigeonRaceName = pigeonRaceName;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public Long getRaceconfigId() {
        return raceconfigId;
    }

    public void setRaceconfigId(Long raceconfigId) {
        this.raceconfigId = raceconfigId;
    }

    public Long getCreater() {
        return creater;
    }

    public void setCreater(Long creater) {
        this.creater = creater;
    }

    public Long getPigeonRaceDate() {
        return pigeonRaceDate;
    }

    public void setPigeonRaceDate(Long pigeonRaceDate) {
        this.pigeonRaceDate = pigeonRaceDate;
    }

    public Long getCreateDatetime() {
        return createDatetime;
    }

    public void setCreateDatetime(Long createDatetime) {
        this.createDatetime = createDatetime;
    }

    public Long getRaceStatus() {
        return raceStatus;
    }

    public void setRaceStatus(Long raceStatus) {
        this.raceStatus = raceStatus;
    }

    public Long getRaceExpiryDate() {
        return raceExpiryDate;
    }

    public void setRaceExpiryDate(Long raceExpiryDate) {
        this.raceExpiryDate = raceExpiryDate;
    }

    public Long getRaceDefault() {
        return raceDefault;
    }

    public void setRaceDefault(Long raceDefault) {
        this.raceDefault = raceDefault;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(pigeonRaceName);
        dest.writeString(configName);
        if (raceconfigId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(raceconfigId);
        }
        if (pigeonRaceDate == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(pigeonRaceDate);
        }
        if (createDatetime == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(createDatetime);
        }
        if (creater == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(creater);
        }
        if (raceStatus == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(raceStatus);
        }
        if (raceExpiryDate == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(raceExpiryDate);
        }
        if (raceDefault == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(raceDefault);
        }
        if (starLon == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(starLon);
        }
        if (starLat == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(starLat);
        }
        if (endLon == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(endLon);
        }
        if (endLat == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(endLat);
        }
    }
}
