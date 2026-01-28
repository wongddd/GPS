package com.yyt.trackcar.bean;

/**
 * @ projectName:
 * @ packageName:   com.yyt.trackcar.bean
 * @ fileName:      TrackSumBean
 * @ author:        QING
 * @ createTime:    6/28/21 21:09
 * @ describe:      TODO
 */
public class TrackSumBean {

    private String SquadDate;
    private String Ride;
    private String ERide;
    private String RideMileage;
    private String Calorie;

    public String getSquadDate() {
        return SquadDate;
    }

    public void setSquadDate(String squadDate) {
        SquadDate = squadDate;
    }

    public String getRide() {
        return Ride;
    }

    public void setRide(String ride) {
        Ride = ride;
    }

    public String getERide() {
        return ERide;
    }

    public void setERide(String ERide) {
        this.ERide = ERide;
    }

    public String getRideMileage() {
        return RideMileage;
    }

    public void setRideMileage(String rideMileage) {
        RideMileage = rideMileage;
    }

    public String getCalorie() {
        return Calorie;
    }

    public void setCalorie(String calorie) {
        Calorie = calorie;
    }
}
