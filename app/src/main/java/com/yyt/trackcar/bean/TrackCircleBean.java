package com.yyt.trackcar.bean;

import java.util.List;

public class TrackCircleBean {

    private int id;
    /**
     * 轨迹圈创建时间
     */
    private Long circleCreatedatetime;
    /**
     * 轨迹圈主题
     */
    private String circleSubject;
    private String deviceImei;
    /**
     * 轨迹起点的时间
     */
    private Long starDatetime;
    /**
     * 轨迹终点的时间
     */
    private Long endDatetime;
    /**
     * 轨迹圈图片
     */
    private List<String> fileinfoId;
    /**
     * 设备类型
     */
    private int deviceType;
    /** 距离 */
    private Long distance;
    /**
     * 是否点赞 1、点赞 0、未点赞
     * */
    private Long isthumbsup;
    /** 点赞次数 */
    private Long thumbsup;
    /** 浏览次数 */
    private Long views;

    /**
     * 发布者Id
     */
    private int circleUid;

    public int getCircleUid() {
        return circleUid;
    }

    public void setCircleUid(int circleUid) {
        this.circleUid = circleUid;
    }

    public Long getDistance() {
        return distance;
    }

    public void setDistance(Long distance) {
        this.distance = distance;
    }

    public Long getIsthumbsup() {
        return isthumbsup;
    }

    public void setIsthumbsup(Long isthumbsup) {
        this.isthumbsup = isthumbsup;
    }

    public Long getThumbsup() {
        return thumbsup;
    }

    public void setThumbsup(Long thumbsup) {
        this.thumbsup = thumbsup;
    }

    public Long getViews() {
        return views;
    }

    public void setViews(Long views) {
        this.views = views;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public List<String> getFileinfoId() {
        return fileinfoId;
    }

    public void setFileinfoId(List<String> fileinfoId) {
        this.fileinfoId = fileinfoId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Long getCircleCreatedatetime() {
        return circleCreatedatetime;
    }

    public void setCircleCreatedatetime(Long circleCreatedatetime) {
        this.circleCreatedatetime = circleCreatedatetime;
    }

    public String getCircleSubject() {
        return circleSubject;
    }

    public void setCircleSubject(String circleSubject) {
        this.circleSubject = circleSubject;
    }

    public String getDeviceImei() {
        return deviceImei;
    }

    public void setDeviceImei(String deviceImei) {
        this.deviceImei = deviceImei;
    }

    public Long getStarDatetime() {
        return starDatetime;
    }

    public void setStarDatetime(Long starDatetime) {
        this.starDatetime = starDatetime;
    }

    public Long getEndDatetime() {
        return endDatetime;
    }

    public void setEndDatetime(Long endDatetime) {
        this.endDatetime = endDatetime;
    }
}
