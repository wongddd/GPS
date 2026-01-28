package com.yyt.trackcar.bean;

public class AAAPigeonRaceBean {
    private int id;
    private long pigeonRaceDate;
    private String pigeonRaceName;
    private int raceDefault;
    private long raceExpiryDate;
    private int raceStatus;
    private int raceconfigId;

    public int getRaceconfigId() {
        return raceconfigId;
    }

    public void setRaceconfigId(int raceconfigId) {
        this.raceconfigId = raceconfigId;
    }

    private String remark;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPigeonRaceName() {
        return pigeonRaceName;
    }

    public void setPigeonRaceName(String pigeonRaceName) {
        this.pigeonRaceName = pigeonRaceName;
    }

    public int getRaceDefault() {
        return raceDefault;
    }

    public void setRaceDefault(int raceDefault) {
        this.raceDefault = raceDefault;
    }

    public int getRaceStatus() {
        return raceStatus;
    }

    public void setRaceStatus(int raceStatus) {
        this.raceStatus = raceStatus;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public long getPigeonRaceDate() {
        return pigeonRaceDate;
    }

    public void setPigeonRaceDate(long pigeonRaceDate) {
        this.pigeonRaceDate = pigeonRaceDate;
    }

    public long getRaceExpiryDate() {
        return raceExpiryDate;
    }

    public void setRaceExpiryDate(long raceExpiryDate) {
        this.raceExpiryDate = raceExpiryDate;
    }
}
