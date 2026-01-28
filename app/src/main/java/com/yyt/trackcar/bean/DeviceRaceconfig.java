package com.yyt.trackcar.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 配置文件对象 device_raceconfig
 * 
 * @author ruoyi
 * @date 2022-01-13
 */
public class DeviceRaceconfig implements Parcelable {
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    private String isCopy;
    /**
     * 配置名称
     */
    private String configName;

    /** 开始下载星历时间 */
    private Long gddt;

    /** 进入工作状态时间 */
    private Long rsut;

    /** 定位频率  */
    private Long rgli;

    /** 上传频率  */
    private Long rdui;

    /** 数据上传启动时间(pattern = "yyyy-MM-dd")*/
    private Long rdut;

    /** 低电量门限 */
    private Long lpl;

    /** 夜间模式开始时间(pattern = "HH:mm:ss")  */
    private Long nmst;

    /** 夜间模式定位频率  */
    private Long ngli;

    /** 夜间模式数据上传 */
    private Long ndui;

    /** 夜间模式结束时间 (pattern = "HH:mm:ss")*/
    private Long nmet;

    /** 续飞模式定位频率  */
    private Long cgli;

    /** 续飞模式数据上传 */
    private Long cdui;

    /** 低电量模式定位频 */
    private Long lpgli;

    /** 低电量模式上传频 */
    private Long lpdui;

    /** 续传上次比赛数据 */
    private String clrd;

    /** 清除上次比赛数据 */
    private String crl;

    /** 比赛编号  */
    private String rid;

    /** 配置数据下发时间 (pattern = "yyyy-MM-dd HH:mm:ss")*/
    private Long cst;

    /** 比赛模式 0、个人模式  1、比赛模式*/
    private Long raceStatus;

    /**开始待机时间*/
    private Long standbyDatetime;

    /**比赛延迟开始时间*/
    private Long rsud;

    protected DeviceRaceconfig(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        isCopy = in.readString();
        configName = in.readString();
        if (in.readByte() == 0) {
            gddt = null;
        } else {
            gddt = in.readLong();
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
        if (in.readByte() == 0) {
            raceStatus = null;
        } else {
            raceStatus = in.readLong();
        }
        if (in.readByte() == 0) {
            standbyDatetime = null;
        } else {
            standbyDatetime = in.readLong();
        }
        if (in.readByte() == 0) {
            rsud = null;
        } else {
            rsud = in.readLong();
        }
    }

    public static final Creator<DeviceRaceconfig> CREATOR = new Creator<DeviceRaceconfig>() {
        @Override
        public DeviceRaceconfig createFromParcel(Parcel in) {
            return new DeviceRaceconfig(in);
        }

        @Override
        public DeviceRaceconfig[] newArray(int size) {
            return new DeviceRaceconfig[size];
        }
    };

    public Long getRaceStatus() {
        return raceStatus;
    }

    public void setRaceStatus(Long raceStatus) {
        this.raceStatus = raceStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIsCopy() {
        return isCopy;
    }

    public void setIsCopy(String isCopy) {
        this.isCopy = isCopy;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public Long getGddt() {
        return gddt;
    }

    public void setGddt(Long gddt) {
        this.gddt = gddt;
    }

    public Long getRsut() {
        return rsut;
    }

    public void setRsut(Long rsut) {
        this.rsut = rsut;
    }

    public Long getRgli() {
        return rgli;
    }

    public void setRgli(Long rgli) {
        this.rgli = rgli;
    }

    public Long getRdui() {
        return rdui;
    }

    public void setRdui(Long rdui) {
        this.rdui = rdui;
    }

    public Long getRdut() {
        return rdut;
    }

    public void setRdut(Long rdut) {
        this.rdut = rdut;
    }

    public Long getLpl() {
        return lpl;
    }

    public void setLpl(Long lpl) {
        this.lpl = lpl;
    }

    public Long getNmst() {
        return nmst;
    }

    public void setNmst(Long nmst) {
        this.nmst = nmst;
    }

    public Long getNgli() {
        return ngli;
    }

    public void setNgli(Long ngli) {
        this.ngli = ngli;
    }

    public Long getNdui() {
        return ndui;
    }

    public void setNdui(Long ndui) {
        this.ndui = ndui;
    }

    public Long getNmet() {
        return nmet;
    }

    public void setNmet(Long nmet) {
        this.nmet = nmet;
    }

    public Long getCgli() {
        return cgli;
    }

    public void setCgli(Long cgli) {
        this.cgli = cgli;
    }

    public Long getCdui() {
        return cdui;
    }

    public void setCdui(Long cdui) {
        this.cdui = cdui;
    }

    public Long getLpgli() {
        return lpgli;
    }

    public void setLpgli(Long lpgli) {
        this.lpgli = lpgli;
    }

    public Long getLpdui() {
        return lpdui;
    }

    public void setLpdui(Long lpdui) {
        this.lpdui = lpdui;
    }

    public String getClrd() {
        return clrd;
    }

    public void setClrd(String clrd) {
        this.clrd = clrd;
    }

    public String getCrl() {
        return crl;
    }

    public void setCrl(String crl) {
        this.crl = crl;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public Long getCst() {
        return cst;
    }

    public void setCst(Long cst) {
        this.cst = cst;
    }

    public Long getStandbyDatetime() {
        return standbyDatetime;
    }

    public void setStandbyDatetime(Long standbyDatetime) {
        this.standbyDatetime = standbyDatetime;
    }

    public Long getRsud() {
        return rsud;
    }

    public void setRsud(Long rsud) {
        this.rsud = rsud;
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
            parcel.writeLong(id);
        }
        parcel.writeString(isCopy);
        parcel.writeString(configName);
        if (gddt == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(gddt);
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
        if (raceStatus == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(raceStatus);
        }
        if (standbyDatetime == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(standbyDatetime);
        }
        if (rsud == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(rsud);
        }
    }
}
