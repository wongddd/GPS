package com.yyt.trackcar.bean;

import android.graphics.Color;

import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.yyt.trackcar.dbflow.AAADeviceModel;

import java.util.ArrayList;
import java.util.List;

public class AMapMovementTrack {

    /**
     * 最后定位的经纬度
     */
    private LatLng latLng;

    /**
     * 高德地图Marker
     */
    private Marker marker;

    /**
     * 高德地图BitmapDescriptor（marker图标
     */
    private BitmapDescriptor bitmapDescriptor;

    private AAADeviceModel previousDeviceModel;

    private AAADeviceModel deviceModel;

    /**
     * 历史定位点
     */
    private List<LatLng> latLngs = new ArrayList<>();

    /**
     * 初始颜色（用于轨迹线颜色绘制
     */
    private int color = Color.argb(218,180,0,180);

    private int supplementColor = Color.argb(255,200,100,0);

    /**
     * 轨迹线纹理
     */
    private BitmapDescriptor customTexture;

    /**
     * 补传点轨迹线纹理
     */
    private BitmapDescriptor supplementCustomTexture;

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

    public AAADeviceModel getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(AAADeviceModel deviceModel) {
        this.deviceModel = deviceModel;
    }

    public List<LatLng> getLatLngs() {
        return latLngs;
    }

    public void setLatLngs(List<LatLng> latLngs) {
        this.latLngs = latLngs;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getSupplementColor() {
        return supplementColor;
    }

    public void setSupplementColor(int supplementColor) {
        this.supplementColor = supplementColor;
    }

    public BitmapDescriptor getSupplementCustomTexture() {
        return supplementCustomTexture;
    }

    public void setSupplementCustomTexture(BitmapDescriptor supplementCustomTexture) {
        this.supplementCustomTexture = supplementCustomTexture;
    }

    public AAADeviceModel getPreviousDeviceModel() {
        return previousDeviceModel;
    }

    /**
     * 更新坐标列表
     * @param latLng
     */
    public void updateLatLngs(LatLng latLng){
        this.latLngs.add(latLng);
    }

    //链式构造
    public AMapMovementTrack latLng(LatLng latLng){
        this.latLng = latLng;
        return this;
    }

    public AMapMovementTrack marker(Marker marker){
        this.marker = marker;
        return this;
    }

    public AMapMovementTrack deviceModel(AAADeviceModel deviceModel){
        this.deviceModel = deviceModel;
        return this;
    }

    public AMapMovementTrack bitmapDescriptor(BitmapDescriptor bitmapDescriptor){
        this.bitmapDescriptor = bitmapDescriptor;
        return this;
    }

    public AMapMovementTrack initLatLng(LatLng latLng){
        this.latLngs.add(latLng);
        return this;
    }

    public AMapMovementTrack initColor(){
        this.setColor(getRandomColor());
        this.setSupplementColor(getRandomColor());
        return this;
    }

    public void updateDeviceModel(AAADeviceModel deviceModel){
        this.previousDeviceModel = this.deviceModel;
        this.deviceModel = deviceModel;
    }

    public BitmapDescriptor getCustomTexture() {
        return customTexture;
    }

    public void setCustomTexture(BitmapDescriptor customTexture) {
        this.customTexture = customTexture;
    }

    public static int getRandomColor(){
        return Color.rgb((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*200)+50);
    }

}
