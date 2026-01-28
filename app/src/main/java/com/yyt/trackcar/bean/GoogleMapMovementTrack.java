package com.yyt.trackcar.bean;

import android.graphics.Color;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.yyt.trackcar.dbflow.AAADeviceModel;

import java.util.ArrayList;
import java.util.List;

public class GoogleMapMovementTrack {

    private AAADeviceModel deviceModel;
    private LatLng latLng;
    private Marker marker;
    private BitmapDescriptor bitmapDescriptor;
    private int color = Color.argb(255,0,0,180);
    private List<LatLng> latLngs = new ArrayList<>();
    private AAADeviceModel previousDeviceModel;
    private Polyline polyline;
    private int supplementFlag;
    private int supplementColor = Color.argb(255,0,200,0);

    public Polyline getPolyline() {
        return polyline;
    }

    public void setPolyline(Polyline polyline) {
        this.polyline = polyline;
    }

    public AAADeviceModel getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(AAADeviceModel deviceModel) {
        this.deviceModel = deviceModel;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public BitmapDescriptor getBitmapDescriptor() {
        return bitmapDescriptor;
    }

    public void setBitmapDescriptor(BitmapDescriptor bitmapDescriptor) {
        this.bitmapDescriptor = bitmapDescriptor;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public AAADeviceModel getPreviousDeviceModel() {
        return previousDeviceModel;
    }

    public void setPreviousDeviceModel(AAADeviceModel previousDeviceModel) {
        this.previousDeviceModel = previousDeviceModel;
    }

    public List<LatLng> getLatLngs() {
        return latLngs;
    }

    public void setLatLngs(List<LatLng> latLngs) {
        this.latLngs = latLngs;
    }

    public int getSupplementFlag() {
        return supplementFlag;
    }

    public void setSupplementFlag(int supplementFlag) {
        this.supplementFlag = supplementFlag;
    }

    public int getSupplementColor() {
        return supplementColor;
    }

    public void setSupplementColor(int supplementColor) {
        this.supplementColor = supplementColor;
    }

    public void updateLatLngs(LatLng latLng){
        this.latLngs.add(latLng);
    }

    public void updateDeviceModel(AAADeviceModel deviceModel){
        this.previousDeviceModel = this.deviceModel;
        this.deviceModel = deviceModel;
    }

    // 链式构造
    public GoogleMapMovementTrack deviceModel(AAADeviceModel deviceModel){
        this.deviceModel = deviceModel;
        return this;
    }

    /**
     * 设定最后一个经纬度点
     */
    public GoogleMapMovementTrack lastLatLgn(LatLng lastLatLng){
        this.latLng = lastLatLng;
        return this;
    }

    /**
     * 设定marker
     */
    public GoogleMapMovementTrack marker(Marker marker){
        this.marker = marker;
        return this;
    }

    /**
     * 设定marker的bitmapDescriptor样式
     */
    public GoogleMapMovementTrack bitmapDescriptor(BitmapDescriptor bitmapDescriptor){
        this.bitmapDescriptor = bitmapDescriptor;
        return this;
    }

    public GoogleMapMovementTrack initLatLngs(LatLng latLng){
        this.latLngs.add(latLng);
        return this;
    }

    /**
     * 初始化轨迹线颜色
     */
    public GoogleMapMovementTrack initColor(){
        this.setColor(getRandomColor());
        this.setSupplementColor(getRandomColor());
        return this;
    }

    public int getRandomColor(){
        return Color.rgb((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255));
    }

}
