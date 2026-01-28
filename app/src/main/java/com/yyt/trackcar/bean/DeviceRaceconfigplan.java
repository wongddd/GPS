package com.yyt.trackcar.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 配置文件计划对象 device_raceconfigplan
 *
 * @author ruoyi
 * @Long 2022-03-25
 */
public class DeviceRaceconfigplan implements Parcelable {

    /**
     * $column.columnComment
     */
    private Integer id;

    /**
     * 开始下载星历时间
     * "yyyy-MM-dd HH:mm:ss"
     * name= "开始下载星历时间"+ width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss"
     */
    private Long gddt;

    /**
     * 设备IMEI
     * name= "设备IMEI"
     */
    private String deviceImei;

    /**
     * 结束时间
     * "yyyy-MM-dd HH:mm:ss"
     * name= "结束时间"+ width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss"
     */
    private String cstEnddatetime;

    /**
     * 有效结束时间
     * "yyyy-MM-dd HH:mm:ss"
     * name= "结束时间"+ width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss"
     */
    private Long cstValidenddatetime;

    /**
     * 进入工作状态时间
     * "yyyy-MM-dd HH:mm:ss"
     * name= "进入工作状态时间"+ width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss"
     */
    private Long rsut;

    /**
     * 定位频率
     * name= "定位频率 "
     */
    private Long rgli;

    /**
     * 上传频率
     * name= "上传频率 "
     */
    private Long rdui;

    /**
     * 数据上传启动时间
     * "yyyy-MM-dd HH:mm:ss"
     * name= "数据上传启动时间"+ width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss"
     */
    private Long rdut;

    /**
     * 低电量门限
     * name= "低电量门限"
     */
    private Long lpl;

    /**
     * 夜间模式开始时间
     * "HH:mm:ss"
     * name= "夜间模式开始时间 "+ width = 30, dateFormat = "HH:mm:ss"
     */
    private Long nmst;

    /**
     * 夜间模式定位频率
     * name= "夜间模式定位频率 "
     */
    private Long ngli;

    /**
     * 夜间模式数据上传
     * name= "夜间模式数据上传"
     */
    private Long ndui;

    /**
     * 夜间模式结束时间
     * "HH:mm:ss"
     * name= "夜间模式结束时间"+ width = 30, dateFormat = "HH:mm:ss"
     */
    private Long nmet;

    /**
     * 续飞模式定位频率
     * name= "续飞模式定位频率 "
     */
    private Long cgli;

    /**
     * 续飞模式数据上传
     * name= "续飞模式数据上传"
     */
    private Long cdui;

    /**
     * 低电量模式定位频
     * name= "低电量模式定位频"
     */
    private Long lpgli;

    /**
     * 低电量模式上传频
     * name= "低电量模式上传频"
     */
    private Long lpdui;

    /**
     * 续传上次比赛数据
     * name= "续传上次比赛数据"
     */
    private String clrd;

    /**
     * 清除上次比赛数据
     * name= "清除上次比赛数据"
     */
    private String crl;

    /**
     * 比赛编号
     * name= "比赛编号 "
     */
    private String rid;

    /**
     * 配置数据下发时间
     * "yyyy-MM-dd HH:mm:ss"
     * name= "配置数据下发时间"+ width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss"
     */
    private Long cst;

    /**
     * 配置名称
     * name= "配置名称"
     */
    private String configName;

    /**
     * 比赛模式
     * name= "比赛模式"
     */
    private Long raceStatus;

    /**
     * 位置点数
     * name= "位置点数"
     */
    private Long positionCount;

    /**
     * 有效位置点数
     * name= "位置点数"
     */
    private Long positionValidcount;

    /**
     * 总时长
     */
    private String totalTime;

    /**
     * 第一个点定位时间
     */
    private Long firstGpstime;

    private String deviceName;

    /**
     * 待机总时间
     */
    private String totalStandbyTime;
    /**
     * 定位总时间
     */
    private String totalpositioningTime;

    /**
     * 最后日志时间
     */
    private Long logTime;

    /**
     * 开始待机时间
     */
    private Long standbyDatetime;

    protected DeviceRaceconfigplan(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        if (in.readByte() == 0) {
            gddt = null;
        } else {
            gddt = in.readLong();
        }
        deviceImei = in.readString();
        cstEnddatetime = in.readString();
        if (in.readByte() == 0) {
            cstValidenddatetime = null;
        } else {
            cstValidenddatetime = in.readLong();
        }
        if (in.readByte() == 0) {
            rsut = null;
        } else {
            rsut = in.readLong();
        }
        if (in.readByte() == 0) {
            rgli = null;
        } else {
            rgli = in.readLong();
        }
        if (in.readByte() == 0) {
            rdui = null;
        } else {
            rdui = in.readLong();
        }
        if (in.readByte() == 0) {
            rdut = null;
        } else {
            rdut = in.readLong();
        }
        if (in.readByte() == 0) {
            lpl = null;
        } else {
            lpl = in.readLong();
        }
        if (in.readByte() == 0) {
            nmst = null;
        } else {
            nmst = in.readLong();
        }
        if (in.readByte() == 0) {
            ngli = null;
        } else {
            ngli = in.readLong();
        }
        if (in.readByte() == 0) {
            ndui = null;
        } else {
            ndui = in.readLong();
        }
        if (in.readByte() == 0) {
            nmet = null;
        } else {
            nmet = in.readLong();
        }
        if (in.readByte() == 0) {
            cgli = null;
        } else {
            cgli = in.readLong();
        }
        if (in.readByte() == 0) {
            cdui = null;
        } else {
            cdui = in.readLong();
        }
        if (in.readByte() == 0) {
            lpgli = null;
        } else {
            lpgli = in.readLong();
        }
        if (in.readByte() == 0) {
            lpdui = null;
        } else {
            lpdui = in.readLong();
        }
        clrd = in.readString();
        crl = in.readString();
        rid = in.readString();
        if (in.readByte() == 0) {
            cst = null;
        } else {
            cst = in.readLong();
        }
        configName = in.readString();
        if (in.readByte() == 0) {
            raceStatus = null;
        } else {
            raceStatus = in.readLong();
        }
        if (in.readByte() == 0) {
            positionCount = null;
        } else {
            positionCount = in.readLong();
        }
        if (in.readByte() == 0) {
            positionValidcount = null;
        } else {
            positionValidcount = in.readLong();
        }
        totalTime = in.readString();
        if (in.readByte() == 0) {
            firstGpstime = null;
        } else {
            firstGpstime = in.readLong();
        }
        deviceName = in.readString();
        totalStandbyTime = in.readString();
        totalpositioningTime = in.readString();
        if (in.readByte() == 0) {
            logTime = null;
        } else {
            logTime = in.readLong();
        }
        if (in.readByte() == 0) {
            standbyDatetime = null;
        } else {
            standbyDatetime = in.readLong();
        }
    }

    public static final Creator<DeviceRaceconfigplan> CREATOR = new Creator<DeviceRaceconfigplan>() {
        @Override
        public DeviceRaceconfigplan createFromParcel(Parcel in) {
            return new DeviceRaceconfigplan(in);
        }

        @Override
        public DeviceRaceconfigplan[] newArray(int size) {
            return new DeviceRaceconfigplan[size];
        }
    };

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public Long getFirstGpstime() {
        return firstGpstime;
    }

    public void setFirstGpstime(Long firstGpstime) {
        this.firstGpstime = firstGpstime;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setGddt(Long gddt) {
        this.gddt = gddt;
    }

    public Long getGddt() {
        return gddt;
    }

    public void setDeviceImei(String deviceImei) {
        this.deviceImei = deviceImei;
    }

    public String getDeviceImei() {
        return deviceImei;
    }

    public void setCstEnddatetime(String cstEnddatetime) {
        this.cstEnddatetime = cstEnddatetime;
    }

    public String getCstEnddatetime() {
        return cstEnddatetime;
    }

    public void setRsut(Long rsut) {
        this.rsut = rsut;
    }

    public Long getRsut() {
        return rsut;
    }

    public void setRgli(Long rgli) {
        this.rgli = rgli;
    }

    public Long getRgli() {
        return rgli;
    }

    public void setRdui(Long rdui) {
        this.rdui = rdui;
    }

    public Long getRdui() {
        return rdui;
    }

    public void setRdut(Long rdut) {
        this.rdut = rdut;
    }

    public Long getRdut() {
        return rdut;
    }

    public void setLpl(Long lpl) {
        this.lpl = lpl;
    }

    public Long getLpl() {
        return lpl;
    }

    public void setNmst(Long nmst) {
        this.nmst = nmst;
    }

    public Long getNmst() {
        return nmst;
    }

    public void setNgli(Long ngli) {
        this.ngli = ngli;
    }

    public Long getNgli() {
        return ngli;
    }

    public void setNdui(Long ndui) {
        this.ndui = ndui;
    }

    public Long getNdui() {
        return ndui;
    }

    public void setNmet(Long nmet) {
        this.nmet = nmet;
    }

    public Long getNmet() {
        return nmet;
    }

    public void setCgli(Long cgli) {
        this.cgli = cgli;
    }

    public Long getCgli() {
        return cgli;
    }

    public void setCdui(Long cdui) {
        this.cdui = cdui;
    }

    public Long getCdui() {
        return cdui;
    }

    public void setLpgli(Long lpgli) {
        this.lpgli = lpgli;
    }

    public Long getLpgli() {
        return lpgli;
    }

    public void setLpdui(Long lpdui) {
        this.lpdui = lpdui;
    }

    public Long getLpdui() {
        return lpdui;
    }

    public void setClrd(String clrd) {
        this.clrd = clrd;
    }

    public String getClrd() {
        return clrd;
    }

    public void setCrl(String crl) {
        this.crl = crl;
    }

    public String getCrl() {
        return crl;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getRid() {
        return rid;
    }

    public void setCst(Long cst) {
        this.cst = cst;
    }

    public Long getCst() {
        return cst;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getConfigName() {
        return configName;
    }

    public void setRaceStatus(Long raceStatus) {
        this.raceStatus = raceStatus;
    }

    public Long getRaceStatus() {
        return raceStatus;
    }

    public void setPositionCount(Long positionCount) {
        this.positionCount = positionCount;
    }

    public Long getPositionCount() {
        return positionCount;
    }

    public Long getCstValidenddatetime() {
        return cstValidenddatetime;
    }

    public void setCstValidenddatetime(Long cstValidenddatetime) {
        this.cstValidenddatetime = cstValidenddatetime;
    }

    public Long getPositionValidcount() {
        return positionValidcount;
    }

    public void setPositionValidcount(Long positionValidcount) {
        this.positionValidcount = positionValidcount;
    }

    public String getTotalStandbyTime() {
        return totalStandbyTime;
    }

    public void setTotalStandbyTime(String totalStandbyTime) {
        this.totalStandbyTime = totalStandbyTime;
    }

    public String getTotalpositioningTime() {
        return totalpositioningTime;
    }

    public void setTotalpositioningTime(String totalpositioningTime) {
        this.totalpositioningTime = totalpositioningTime;
    }

    public Long getLogTime() {
        return logTime;
    }

    public void setLogTime(Long logTime) {
        this.logTime = logTime;
    }


    public Long getStandbyDatetime() {
        return standbyDatetime;
    }

    public void setStandbyDatetime(Long standbyDatetime) {
        this.standbyDatetime = standbyDatetime;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (id == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(id);
        }
        if (gddt == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(gddt);
        }
        parcel.writeString(deviceImei);
        parcel.writeString(cstEnddatetime);
        if (cstValidenddatetime == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(cstValidenddatetime);
        }
        if (rsut == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(rsut);
        }
        if (rgli == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(rgli);
        }
        if (rdui == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(rdui);
        }
        if (rdut == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(rdut);
        }
        if (lpl == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(lpl);
        }
        if (nmst == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(nmst);
        }
        if (ngli == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(ngli);
        }
        if (ndui == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(ndui);
        }
        if (nmet == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(nmet);
        }
        if (cgli == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(cgli);
        }
        if (cdui == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(cdui);
        }
        if (lpgli == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(lpgli);
        }
        if (lpdui == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(lpdui);
        }
        parcel.writeString(clrd);
        parcel.writeString(crl);
        parcel.writeString(rid);
        if (cst == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(cst);
        }
        parcel.writeString(configName);
        if (raceStatus == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(raceStatus);
        }
        if (positionCount == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(positionCount);
        }
        if (positionValidcount == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(positionValidcount);
        }
        parcel.writeString(totalTime);
        if (firstGpstime == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(firstGpstime);
        }
        parcel.writeString(deviceName);
        parcel.writeString(totalStandbyTime);
        parcel.writeString(totalpositioningTime);
        if (logTime == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(logTime);
        }
        if (standbyDatetime == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(standbyDatetime);
        }
    }
}
