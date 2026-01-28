package com.yyt.trackcar.bean;

/**
 * projectName：
 * packageName：   com.lllcommon.bean
 * fileName：      Rectangle
 * author：        QING
 * createTime：    2019/5/6 14:16
 * describe：      TODO 地图范围类
 */
public class Rectangle {
    private double west; // 西
    private double north; // 北
    private double east; // 东
    private double south; // 南

    public double getWest() {
        return west;
    }

    public void setWest(double west) {
        this.west = west;
    }

    public double getNorth() {
        return north;
    }

    public void setNorth(double north) {
        this.north = north;
    }

    public double getEast() {
        return east;
    }

    public void setEast(double east) {
        this.east = east;
    }

    public double getSouth() {
        return south;
    }

    public void setSouth(double south) {
        this.south = south;
    }

    public Rectangle(double latitude1, double longitude1, double latitude2, double longitude2) {
        this.west = Math.min(longitude1, longitude2);
        this.north = Math.max(latitude1, latitude2);
        this.east = Math.max(longitude1, longitude2);
        this.south = Math.min(latitude1, latitude2);
    }
}
