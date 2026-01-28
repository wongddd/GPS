package com.yyt.trackcar.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.yyt.trackcar.MainApplication;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAATrackModel;
import com.yyt.trackcar.dbflow.AAADeviceModel;

import java.text.DecimalFormat;

/**
 * 地图工具类
 * author: xiao
 */
public class FinalMapUtils {

    /**
     * double统一格式(保留小数点后两位)
     */
    private static final DecimalFormat decimalFormat = new DecimalFormat("#.00");
    /**
     * 高德地图和谷歌地图的轨迹线宽度
     */
    public static final int POLYLINE_WIDTH = 12;
    /**
     * 高德地图和谷歌地图上的marker的大小
     */
    public static final int MARKER_SIZE = 24;

    /**
     * 将Track对象(AAATrackModel)转换为Device(AAADeviceModel)对象
     *
     * @param track 轨迹点对象
     * @return device 设备对象
     */
    public static AAADeviceModel transformTrackToDevice(AAATrackModel track) {
        AAADeviceModel deviceModel = new AAADeviceModel();
        deviceModel.setDeviceImei(track.getDeviceImei()); //国际移动设备识别码
        deviceModel.setDeviceType(track.getDeviceType()); //设备类型 1、车载定位器  2、鸽子定位器 3、学生卡
        deviceModel.setDeviceName(track.getDeviceName()); //设备昵称
        deviceModel.setLastDeviceVol(track.getDeviceVol()); //设备剩余电量
        deviceModel.setLastLongitude(track.getLng()); // 经度
        deviceModel.setLastLatitude(track.getLat()); // 经度
        deviceModel.setHeading(track.getHeading()); //设备朝向 degree
        deviceModel.setLastGpsTime(track.getGpsTime()); //定位时间
        deviceModel.setLastLocationTime(track.getLogTime()); //定位上传时间
        deviceModel.setOnlineStatus(track.isOnlineStatus()); //在线状态
        deviceModel.setLastLocationType(track.getLocationType()); //定位类型
        deviceModel.setLocationType(track.getLocationType());
        deviceModel.setWeather(track.getWeather()); //天气状况
        deviceModel.setLastPositionDesc(track.getPositionDesc()); // 地址描述
        deviceModel.setEngineStatus(track.getAccStatus());  //车载 ACC开关
        deviceModel.setLastMotionStatus(track.getMotionStatus()); //车载 设备状态
        deviceModel.setPointIndex(track.getPointIndex()); // 当前比赛 顺序点(第几个点)
        deviceModel.setDeviceSms(track.getDeviceSms()); // 信号强度
        deviceModel.setSpeed(track.getSpeed()); // 速度
        deviceModel.setAltitude(track.getAltitude()); // 海拔高度
        deviceModel.setAccumulateDuration(track.getAccumulateDuration()); // 累积时长
        deviceModel.setAccumulateOdometer(track.getAccumulateOdometer()); // 累积里程
        deviceModel.setDuration(track.getDuration()); // 距上一点的时长
        deviceModel.setOdometer(track.getOdometer()); // 距上一点的距离
        deviceModel.setVersion(track.getVersion());
        deviceModel.setLocationType(track.getLocationType());
        deviceModel.setLastCommunicationDateTime(track.getLastCommunicationDateTime());
        deviceModel.setSatellite(track.getSatellite());
        deviceModel.setIsCharge(track.getIsCharge());
        return deviceModel;
    }

    public static AAATrackModel transformDeviceToTrack(AAADeviceModel model) {
        AAATrackModel trackModel = new AAATrackModel();
        trackModel.setDeviceImei(model.getDeviceImei());
        trackModel.setHeadPic(model.getHeadPic());
        trackModel.setOnlineStatus(model.getOnline() == 1);
        trackModel.setLat(model.getLastLatitude());
        trackModel.setLng(model.getLastLongitude());
        trackModel.setDeviceVol(model.getLastDeviceVol());
        trackModel.setGpsTime(model.getLastLocationTime());
        trackModel.setLogTime(model.getLastLocationTime());
        trackModel.setDeviceName(model.getDeviceName());
        trackModel.setHeading(model.getHeading());
        trackModel.setOdometer(model.getOdometer());
        trackModel.setSpeed(model.getSpeed());
        trackModel.setDeviceSms(model.getDeviceSms());
        trackModel.setPointIndex(model.getPointIndex());
        trackModel.setAltitude(model.getAltitude());
        trackModel.setDuration(model.getDuration());
        trackModel.setAccumulateDuration(model.getAccumulateDuration());
        trackModel.setAccumulateOdometer(model.getAccumulateOdometer());
        trackModel.setWeather(model.getWeather());
        trackModel.setRignNo(model.getRingNo());
        trackModel.setVersion(model.getVersion());
        trackModel.setLocationType(model.getLocationType());
        trackModel.setLastCommunicationDateTime(model.getLastCommunicationDateTime());
        trackModel.setSatellite(model.getSatellite());
        trackModel.setIsCharge(model.getIsCharge());
        return trackModel;
    }

    /**
     * 生成地图Marker的信息弹窗(InfoWindow)的内容(折叠)
     *
     * @param device 设备信息
     * @return String
     */
    public static String concatInfoWindowCollapsedContent(AAADeviceModel device, int mapType) {
        final Context context = MainApplication.getContext();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < device.getDeviceImei().length() - 3; i++) {
            stringBuilder.append("*");
        }
        String deviceImei =
                stringBuilder.append(device.getDeviceImei().substring(device.getDeviceImei().length() - 3)).toString();
        String deviceName = device.getDeviceName();
        if (device.getDeviceName().equals(device.getDeviceImei())) {
            deviceName = deviceImei;
        }
        if (DataUtils.isPigeonDevice(device.getDeviceImei())) { // 鸽子定位器信息弹窗内容
            String str = context.getString(R.string.imei) + ":" + deviceImei;
            str += "\n" + context.getString(R.string.device_power) + ":" + device.getLastDeviceVol() + "%";
            str += "\n" + context.getString(R.string.location_time) + ":" + (isEmpty(device.getLastGpsTime()) ? "" : device.getLastGpsTime());
            str += "\n" + context.getString(R.string.log_time) + ":" + (isEmpty(device.getLastLocationTime()) ? "" : device.getLastLocationTime());
            str += "\n" + context.getString(R.string.nickname) + ":" + deviceName;
            str += "\n" + context.getString(R.string.positioning_points) + ":" + (isEmpty(device.getPointIndex()) ? 0 : device.getPointIndex());
            if (mapType == 0) {
                str += "\n" + context.getString(R.string.expand);
            }
            return str;
        } else if (String.valueOf(DeviceType.PET.getValue()).equals(DataUtils.getDeviceType(device.getDeviceImei()))) {
            String isCharge;
            String locationMode;
            if (device.getIsCharge() != null && device.getIsCharge() == 1) {
                isCharge = String.format("(%s)", context.getString(R.string.charge_status));
            } else {
                isCharge = "";
            }
            if (device.getLocationType() == null) {
                locationMode = context.getString(R.string.invalid_position);
            } else if (device.getLocationType() == 1) {
                locationMode = "GPS";
                locationMode += "  " + context.getString(R.string.satellite_num) + ":" + (device.getSatellite() == null ? 0 : device.getSatellite());
            } else if (device.getLocationType() == 2) {
                locationMode = context.getString(R.string.base_station);
            } else if (device.getLocationType() == 3) {
                locationMode = context.getString(R.string.base_station_nb);
            } else if (device.getLocationType() == 4) {
                locationMode = "WIFI";
            } else {
                locationMode = context.getString(R.string.invalid_position);
            }
            String str = context.getString(R.string.imei) + ":" + deviceImei;
            str += "\n" + context.getString(R.string.device_power) + ":" + device.getLastDeviceVol() + "%" + isCharge;
            str += "\n" + context.getString(R.string.location_time) + ":" + (isEmpty(device.getLastGpsTime()) ? "" : device.getLastGpsTime());
            str += "\n" + context.getString(R.string.log_time) + ":" + StringUtils.getNotNullText(device.getLastLocationTime());
            str += "\n" + context.getString(R.string.nickname) + ":" + deviceName;
            str += "\n" + context.getString(R.string.locate_mode) + ":" + locationMode;
            str += "\n" + context.getString(R.string.positioning_points) + ":" + (isEmpty(device.getPointIndex()) ? 0 : device.getPointIndex());
            if (mapType == 0) {
                str += "\n" + context.getString(R.string.expand);
            }
            return str;
        } else { // 非鸽子定位器信息弹窗内容
            String online;
            if (device.isOnlineStatus() && device.getLastMotionStatus() != null &&
                    device.getLastMotionStatus() == 1)
                online = context.getString(R.string.device_sport);
            else if (device.isOnlineStatus())
                online = context.getString(R.string.device_motionless);
            else
                online = context.getString(R.string.offline);
            String onOff;
            if (device.isOnlineStatus() && device.getEngineStatus() != null
                    && device.getEngineStatus() == 1)
                onOff = context.getString(R.string.on);
            else
                onOff = context.getString(R.string.off);
            float vol = 0;
            if (!TextUtils.isEmpty(device.getLastDeviceVol()) && !device.getLastDeviceVol().equals("null")) {
                try {
                    vol = Float.parseFloat(device.getLastDeviceVol());
                    if (vol < 0)
                        vol = 0;
                    else if (vol > 100)
                        vol = 100;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return context.getString(R.string.tracking_device_info_track_new
                    , online, onOff, String.valueOf(vol)
                    , device.getLastGpsTime() == null ? "" : device.getLastGpsTime()
                    , AAAStringUtils.getSpeed(device.getLastSpeed())
                    , AAAStringUtils.directionDescription(context, device.getHeading())
                    , device.getDeviceName()
                    , device.getDeviceImei());
        }
    }

    /**
     * 生成地图Marker的信息弹出(InfoWindow)的内容
     *
     * @param device 设备信息
     * @return String
     */
    public static String concatInfoWindowContent(AAADeviceModel device, int mapType) {
        final Context context = MainApplication.getContext();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < device.getDeviceImei().length() - 3; i++) {
            stringBuilder.append("*");
        }
        String deviceImei =
                stringBuilder.append(device.getDeviceImei().substring(device.getDeviceImei().length() - 3)).toString();
        String deviceName = device.getDeviceName();
        if (device.getDeviceName().equals(device.getDeviceImei())) {
            deviceName = deviceImei;
        }
        if (DataUtils.isPigeonDevice(device.getDeviceImei())) { // 鸽子定位器信息弹窗内容
            double averageSpeed = 0.00d; // 平均速度 m/s
            if (device.getAccumulateOdometer() != null
                    && device.getAccumulateDuration() != null
                    && device.getAccumulateDuration() != 0) {
                averageSpeed =
                        (device.getAccumulateOdometer() * 1d) / device.getAccumulateDuration();
            }
            double speed = 0.00d; // 距上一点速度 m/s
            if (device.getOdometer() != null
                    && device.getDuration() != null
                    && device.getDuration() != 0) {
                speed = device.getOdometer() / device.getDuration();
            }
            String str = context.getString(R.string.imei) + ":" + deviceImei;
            str += "\n" + context.getString(R.string.device_power) + ":" + device.getLastDeviceVol() + "%";
            str += "\n" + context.getString(R.string.location_time) + ":" + (isEmpty(device.getLastGpsTime()) ? "" : device.getLastGpsTime());
            str += "\n" + context.getString(R.string.log_time) + ":" + (isEmpty(device.getLastLocationTime()) ? "" : device.getLastLocationTime());
            str += "\n" + context.getString(R.string.nickname) + ":" + deviceName;
            str += "\n" + context.getString(R.string.longitude) + ": " + device.getLastLongitude();
            str += "\n" + context.getString(R.string.latitude) + ":" + device.getLastLatitude();
            str += "\n" + context.getString(R.string.weather) + ":" + (isEmpty(device.getWeather()) ? "" : device.getWeather());
            str += "\n" + context.getString(R.string.position_info) + ":" + (isEmpty(device.getLastPositionDesc()) ? "" : device.getLastPositionDesc());
            str += "\n" + context.getString(R.string.distance_from_previous_point) + ":" + (isEmpty(device.getOdometer()) ? "" : device.getOdometer() + context.getString(R.string.meter));
            str += "\n" + context.getString(R.string.time_from_previous_point) + ":" + (isEmpty(device.getDuration()) ? "" : transformSecondToString(device.getDuration()));
            str += "\n" + context.getString(R.string.speed_per_minute) + ":" + (isEmpty(device.getSpeed()) ? "" : transformDecimal(speed * 60d) + context.getString(R.string.meter_per_minute));
            str += "\n" + context.getString(R.string.speed_per_hour) + ":" + (isEmpty(device.getSpeed()) ? "" : transformDecimal(speed * 3.6d) + context.getString(R.string.kilometerPerHour));
            str += "\n" + context.getString(R.string.cumulative_distance) + ":" + (isEmpty(device.getAccumulateOdometer()) ? "" : transformDecimal(device.getAccumulateOdometer() / 1000d) + context.getString(R.string.kilometer));
            str += "\n" + context.getString(R.string.cumulative_time) + ":" + (isEmpty(device.getAccumulateDuration()) ? "" : transformSecondToString(device.getAccumulateDuration()));
            str += "\n" + context.getString(R.string.average_speed_per_minute) + ":" + transformDecimal(averageSpeed * 60d) + context.getString(R.string.meter_per_minute);
            str += "\n" + context.getString(R.string.average_speed_per_hour) + ":" + transformDecimal(averageSpeed * 3.6d) + context.getString(R.string.kilometerPerHour);
//            str += "\n" + context.getString(R.string.direction) + ":" + (isEmpty(device
//            .getHeading()) ? "" : device.getHeading());
//            str += "\n" + context.getString(R.string.signal_intensity) + ":" + (isEmpty(device
//            .getDeviceSms()) ? "" : device.getDeviceSms());
            str += "\n" + context.getString(R.string.altitude) + ":" + (isEmpty(device.getAltitude()) ? "" : device.getAltitude() + context.getString(R.string.meter));
            str += "\n" + context.getString(R.string.device_version) + ":" + (isEmpty(device.getVersion()) ? "" : device.getVersion());
            str += "\n" + context.getString(R.string.positioning_points) + ":" + (isEmpty(device.getPointIndex()) ? "" : device.getPointIndex());
            if (mapType == 0) {
                str += "\n" + context.getString(R.string.close);
            }
            return str;
        } else if (String.valueOf(DeviceType.PET.getValue()).equals(DataUtils.getDeviceType(device.getDeviceImei()))) {
            String isCharge;
            String locationMode;
            String online;
            if (device.getIsCharge() != null && device.getIsCharge() == 1) {
                isCharge = String.format("(%s)", context.getString(R.string.charge_status));
            } else {
                isCharge = "";
            }
            if (device.getLocationType() == null) {
                locationMode = context.getString(R.string.invalid_position);
            } else if (device.getLocationType() == 1) {
                locationMode = "GPS";
                locationMode += "  " + context.getString(R.string.satellite_num) + ":" + (device.getSatellite() == null ? 0 : device.getSatellite());
            } else if (device.getLocationType() == 2) {
                locationMode = context.getString(R.string.base_station);
            } else if (device.getLocationType() == 3) {
                locationMode = context.getString(R.string.base_station_nb);
            } else if (device.getLocationType() == 4) {
                locationMode = "WIFI";
            } else {
                locationMode = context.getString(R.string.invalid_position);
            }
            if (device.isOnlineStatus()) {
                online = context.getString(R.string.online);
            } else {
                online = context.getString(R.string.offline);
            }
            String str = context.getString(R.string.imei) + ":" + deviceImei;
            str += "\n" + context.getString(R.string.device_power) + ":" + device.getLastDeviceVol() + "%" + isCharge;
            str += "\n" + context.getString(R.string.location_time) + ":" + (isEmpty(device.getLastGpsTime()) ? "" : device.getLastGpsTime());
            str += "\n" + context.getString(R.string.log_time) + ":" + StringUtils.getNotNullText(device.getLastLocationTime());
            str += "\n" + context.getString(R.string.nickname) + ":" + deviceName;
            str += "\n" + context.getString(R.string.locate_mode) + ":" + locationMode;
            str += "\n" + context.getString(R.string.longitude) + ": " + device.getLastLongitude();
            str += "\n" + context.getString(R.string.latitude) + ":" + device.getLastLatitude();
            str += "\n" + context.getString(R.string.history_speed) + ":" + AAAStringUtils.getSpeed(device.getSpeed());
            str += "\n" + context.getString(R.string.direction) + ":" + AAAStringUtils.directionDescription(context, device.getHeading());
            str += "\n" + context.getString(R.string.altitude) + ":" + (device.getAltitude() == null ? "0" : device.getAltitude()) + context.getString(R.string.meter);
            str += "\n" + online + ":" + AAATimeUtils.getSecond(context,
                    device.getDuration() == null ? "0" : String.valueOf(device.getDuration()));
            str += "\n" + context.getString(R.string.communication_time) + ":" + StringUtils.getNotNullText(device.getLastCommunicationDateTime());
            str += "\n" + context.getString(R.string.position_info) + ":" + StringUtils.getNotNullText(device.getLastPositionDesc());
            str += "\n" + context.getString(R.string.positioning_points) + ":" + (isEmpty(device.getPointIndex()) ? "" : device.getPointIndex());
            if (mapType == 0) {
                str += "\n" + context.getString(R.string.close);
            }
            return str;
        } else { // 非鸽子定位器信息弹窗内容
            String online;
            if (device.isOnlineStatus() && device.getLastMotionStatus() != null &&
                    device.getLastMotionStatus() == 1)
                online = context.getString(R.string.device_sport);
            else if (device.isOnlineStatus())
                online = context.getString(R.string.device_motionless);
            else
                online = context.getString(R.string.offline);
            String onOff;
            if (device.isOnlineStatus() && device.getEngineStatus() != null
                    && device.getEngineStatus() == 1)
                onOff = context.getString(R.string.on);
            else
                onOff = context.getString(R.string.off);
            float vol = 0;
            if (!TextUtils.isEmpty(device.getLastDeviceVol()) && !device.getLastDeviceVol().equals("null")) {
                try {
                    vol = Float.parseFloat(device.getLastDeviceVol());
                    if (vol < 0)
                        vol = 0;
                    else if (vol > 100)
                        vol = 100;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return context.getString(R.string.tracking_device_info_track_new
                    , online, onOff, String.valueOf(vol)
                    , device.getLastGpsTime() == null ? "" : device.getLastGpsTime()
                    , AAAStringUtils.getSpeed(device.getLastSpeed())
                    , AAAStringUtils.directionDescription(context, device.getHeading())
                    , device.getDeviceName()
                    , device.getDeviceImei());
        }
    }

    /**
     * 生成地图Marker的信息弹窗(InfoWindow)的内容(折叠)
     *
     * @param model 定位信息
     * @return String
     */
    public static String concatTrackInfoWindowCollapsedContent(Context context,
                                                               AAATrackModel model, int mapType,
                                                               boolean isExpand) {
        StringBuilder stringBuilder = new StringBuilder();
        String deviceImei;
        if (!TextUtils.isEmpty(model.getDeviceImei()) && model.getDeviceImei().length() > 3) {
            for (int i = 0; i < model.getDeviceImei().length() - 3; i++) {
                stringBuilder.append("*");
            }
            deviceImei =
                    stringBuilder.append(model.getDeviceImei().substring(model.getDeviceImei().length() - 3)).toString();
        } else {
            deviceImei = StringUtils.getNotNullText(model.getDeviceImei());
        }
        String deviceName = StringUtils.getNotNullText(model.getDeviceName());
        if (deviceName.equals(model.getDeviceImei())) {
            deviceName = deviceImei;
        }
        float vol = 0;
        if (!TextUtils.isEmpty(model.getDeviceVol()) && !"null".equals(model.getDeviceVol())) {
            try {
                vol = Float.parseFloat(model.getDeviceVol());
                if (vol < 0)
                    vol = 0;
                else if (vol > 100)
                    vol = 100;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (DataUtils.isPigeonDevice(model.getDeviceImei())) { // 鸽子定位器信息弹窗内容
            long pointIndex = model.getPointIndex() == null ? 1 : model.getPointIndex().longValue();
            if (pointIndex <= 0) {
                pointIndex = 1;
            }
            String str = context.getString(R.string.imei) + ":" + deviceImei;
            str += "\n" + context.getString(R.string.device_power) + ":" + vol + "%";
            str += "\n" + context.getString(R.string.location_time) + ":" + StringUtils.getNotNullText(model.getGpsTime());
            str += "\n" + context.getString(R.string.log_time) + ":" + StringUtils.getNotNullText(model.getLogTime());
            str += "\n" + context.getString(R.string.nickname) + ":" + deviceName;
            if (isExpand) {
                double averageSpeed = 0.00d; // 平均速度 m/s
                if (model.getAccumulateOdometer() != null
                        && model.getAccumulateDuration() != null
                        && model.getAccumulateDuration() != 0) {
                    averageSpeed =
                            (model.getAccumulateOdometer() * 1d) / model.getAccumulateDuration();
                }
                double speed = 0.00d; // 距上一点速度 m/s
                if (model.getOdometer() != null
                        && model.getDuration() != null
                        && model.getDuration() != 0) {
                    speed = model.getOdometer() / model.getDuration();
                }
                str += "\n" + context.getString(R.string.longitude) + ": " + model.getLng();
                str += "\n" + context.getString(R.string.latitude) + ":" + model.getLat();
                str += "\n" + context.getString(R.string.weather) + ":" + StringUtils.getNotNullText(model.getWeather());
                str += "\n" + context.getString(R.string.position_info) + ":" + StringUtils.getNotNullText(model.getPositionDesc());
                str += "\n" + context.getString(R.string.distance_from_previous_point) + ":" + (model.getOdometer() == null ? "0" : model.getOdometer()) + context.getString(R.string.meter);
                str += "\n" + context.getString(R.string.time_from_previous_point) + ":" + transformSecondToString(model.getDuration() == null ? 0 : model.getDuration());
                str += "\n" + context.getString(R.string.speed_per_minute) + ":" + transformDecimal(speed * 60d) + context.getString(R.string.meter_per_minute);
                str += "\n" + context.getString(R.string.speed_per_hour) + ":" + transformDecimal(speed * 3.6d) + context.getString(R.string.kilometerPerHour);
                str += "\n" + context.getString(R.string.cumulative_distance) + ":" + transformDecimal((model.getAccumulateOdometer() == null ? 0 : model.getAccumulateOdometer()) / 1000d) + context.getString(R.string.kilometer);
                str += "\n" + context.getString(R.string.cumulative_time) + ":" + transformSecondToString(model.getAccumulateDuration() == null ? 0 : model.getAccumulateDuration());
                str += "\n" + context.getString(R.string.average_speed_per_minute) + ":" + transformDecimal(averageSpeed * 60d) + context.getString(R.string.meter_per_minute);
                str += "\n" + context.getString(R.string.average_speed_per_hour) + ":" + transformDecimal(averageSpeed * 3.6d) + context.getString(R.string.kilometerPerHour);
//            str += "\n" + context.getString(R.string.direction) + ":" + (isEmpty(device
//            .getHeading()) ? "" : device.getHeading());
//            str += "\n" + context.getString(R.string.signal_intensity) + ":" + (isEmpty(device
//            .getDeviceSms()) ? "" : device.getDeviceSms());
                str += "\n" + context.getString(R.string.altitude) + ":" + (model.getAltitude() == null ? "" : model.getAltitude()) + context.getString(R.string.meter);
                str += "\n" + context.getString(R.string.device_version) + ":" + StringUtils.getNotNullText(model.getVersion());
            }
            str += "\n" + context.getString(R.string.positioning_points) + ":" + pointIndex;
            if (isExpand && mapType == 0) {
                str += "\n" + context.getString(R.string.collapse);
            } else if (mapType == 0) {
                str += "\n" + context.getString(R.string.expand);
            }
            return str;
        } else if (String.valueOf(DeviceType.PET.getValue()).equals(DataUtils.getDeviceType(model.getDeviceImei()))) {
            long pointIndex = model.getPointIndex() == null ? 1 : model.getPointIndex().longValue();
            String isCharge;
            String locationMode;
            if (pointIndex <= 0) {
                pointIndex = 1;
            }
            if (model.getIsCharge() != null && model.getIsCharge() == 1) {
                isCharge = String.format("(%s)", context.getString(R.string.charge_status));
            } else {
                isCharge = "";
            }
            if (model.getLocationType() == null) {
                locationMode = context.getString(R.string.invalid_position);
            } else if (model.getLocationType() == 1) {
                locationMode = "GPS";
                locationMode += "  " + context.getString(R.string.satellite_num) + ":" + (model.getSatellite() == null ? 0 : model.getSatellite());
            } else if (model.getLocationType() == 2) {
                locationMode = context.getString(R.string.base_station);
            } else if (model.getLocationType() == 3) {
                locationMode = context.getString(R.string.base_station_nb);
            } else if (model.getLocationType() == 4) {
                locationMode = "WIFI";
            } else {
                locationMode = context.getString(R.string.invalid_position);
            }
            String str = context.getString(R.string.imei) + ":" + deviceImei;
            str += "\n" + context.getString(R.string.device_power) + ":" + vol + "%" + isCharge;
            str += "\n" + context.getString(R.string.location_time) + ":" + StringUtils.getNotNullText(model.getGpsTime());
            str += "\n" + context.getString(R.string.log_time) + ":" + StringUtils.getNotNullText(model.getLogTime());
            str += "\n" + context.getString(R.string.nickname) + ":" + deviceName;
            str += "\n" + context.getString(R.string.locate_mode) + ":" + locationMode;
            if (isExpand) {
                String online;
                if (model.isOnlineStatus()) {
                    online = context.getString(R.string.online);
                } else {
                    online = context.getString(R.string.offline);
                }
                str += "\n" + context.getString(R.string.longitude) + ": " + model.getLng();
                str += "\n" + context.getString(R.string.latitude) + ":" + model.getLat();
                str += "\n" + context.getString(R.string.history_speed) + ":" + AAAStringUtils.getSpeed(model.getSpeed());
                str += "\n" + context.getString(R.string.direction) + ":" + AAAStringUtils.directionDescription(context, model.getHeading());
                str += "\n" + context.getString(R.string.altitude) + ":" + (model.getAltitude() == null ? "0" : model.getAltitude()) + context.getString(R.string.meter);
                str += "\n" + online + ":" + AAATimeUtils.getSecond(context,
                        model.getDuration() == null ? "0" : String.valueOf(model.getDuration()));
                str += "\n" + context.getString(R.string.communication_time) + ":" + StringUtils.getNotNullText(model.getLastCommunicationDateTime());
                str += "\n" + context.getString(R.string.position_info) + ":" + StringUtils.getNotNullText(model.getPositionDesc());
            }
            str += "\n" + context.getString(R.string.positioning_points) + ":" + pointIndex;
            if (isExpand && mapType == 0) {
                str += "\n" + context.getString(R.string.collapse);
            } else if (mapType == 0) {
                str += "\n" + context.getString(R.string.expand);
            }
            return str;
        } else { // 非鸽子定位器信息弹窗内容
            String online;
            if (model.isOnlineStatus() && model.getMotionStatus() != null &&
                    model.getMotionStatus() == 1)
                online = context.getString(R.string.device_sport);
            else if (model.isOnlineStatus())
                online = context.getString(R.string.device_motionless);
            else
                online = context.getString(R.string.offline);
            String onOff;
            if (model.isOnlineStatus() && model.getAccStatus() != null
                    && model.getAccStatus() == 1)
                onOff = context.getString(R.string.on);
            else
                onOff = context.getString(R.string.off);
            return context.getString(R.string.tracking_device_info_track_new
                    , online, onOff, String.valueOf(vol)
                    , StringUtils.getNotNullText(model.getGpsTime())
                    , AAAStringUtils.getSpeed(model.getSpeed())
                    , AAAStringUtils.directionDescription(context, model.getHeading())
                    , StringUtils.getNotNullText(model.getDeviceName())
                    , StringUtils.getNotNullText(model.getDeviceImei()));
        }
    }

    /**
     * 生成InfoWindow的ContentView
     *
     * @param context 上下文环境
     * @param device  设备对象
     * @param type    生成不同Marker的InfoWindow的标记 0:设备Marker的InfoWindow  1:手机marker的infoWindow
     * @return View
     */
    public static View createInfoContentView(Context context, AAADeviceModel device, int type) {
        @SuppressLint("InflateParams") final View view =
                LayoutInflater.from(context).inflate(R.layout.amap_info_window_layout, null);
        final TextView contentView = view.findViewById(R.id.amap_info_window_address);
        final TextView textBtn = view.findViewById(R.id.switchBtn);
        if (type == 1) { // 手机marker的infoWindow内容
            contentView.setText(R.string.tracking_my_address);
        } else if (device == null) {
            contentView.setText("");
        } else {
            contentView.setText(concatInfoWindowCollapsedContent(device, 1));
            textBtn.setVisibility(View.VISIBLE); // 显示(折叠/展开)按钮
            textBtn.setOnClickListener(view1 -> {
                if (view1.isSelected()) { // view默认未选中
                    textBtn.setText(R.string.expand);
                    contentView.setText(concatInfoWindowCollapsedContent(device, 1));
                } else {
                    textBtn.setText(R.string.collapse);
                    contentView.setText(concatInfoWindowContent(device, 1));
                }
                view1.setSelected(!view1.isSelected()); // 切换view的选中状态
            });
        }
        return view;
    }

    /**
     * 生成InfoWindow的ContentView
     *
     * @param context 上下文环境
     * @param model   定位对象
     * @param type    生成不同Marker的InfoWindow的标记 0:设备Marker的InfoWindow  1:手机marker的infoWindow
     * @return View
     */
    public static View createTrackInfoContentView(Context context, AAATrackModel model, int type) {
        @SuppressLint("InflateParams") final View view =
                LayoutInflater.from(context).inflate(R.layout.amap_info_window_layout, null);
        final TextView contentView = view.findViewById(R.id.amap_info_window_address);
        final TextView textBtn = view.findViewById(R.id.switchBtn);
        if (type == 1) { // 手机marker的infoWindow内容
            contentView.setText(R.string.tracking_my_address);
        } else if (model == null) {
            contentView.setText("");
        } else {
            contentView.setText(concatTrackInfoWindowCollapsedContent(context, model, 1, false));
            textBtn.setVisibility(View.VISIBLE); // 显示(折叠/展开)按钮
            textBtn.setOnClickListener(view1 -> {
                if (view1.isSelected()) { // view默认未选中
                    textBtn.setText(R.string.expand);
                    contentView.setText(concatTrackInfoWindowCollapsedContent(context, model, 1,
                            false));
                } else {
                    textBtn.setText(R.string.collapse);
                    contentView.setText(concatTrackInfoWindowCollapsedContent(context, model, 1,
                            true));
                }
                view1.setSelected(!view1.isSelected()); // 切换view的选中状态
            });
        }
        return view;
    }

    /**
     * 判断数据是否为空
     *
     * @param value 值
     * @param <T>   (泛型)传入值的类型
     * @return boolean是否为空值
     */
    private static <T> boolean isEmpty(T value) {
        return value == null || value == "" || value == "null";
    }

    /**
     * 将double值转换为统一格式
     *
     * @param value double值
     * @return decimalFormat(" # .00 ")
     */
    private static double transformDecimal(double value) {
        return Double.parseDouble(decimalFormat.format(value));
    }

    /**
     * 将秒数转换为时间描述
     *
     * @param value 秒数
     * @return TimeString
     */
    private static String transformSecondToString(Long value) {
        Context context = MainApplication.getContext();
        String str = "";
        boolean startRecorded = false;
        int hours = (int) Math.floor((double) (value / 60 / 60 % 24));
        int minutes = (int) (double) (value / 60 % 60);
        int seconds = (int) (double) (value % 60);
        if (hours > 0) {
            str += hours + context.getString(R.string.hour_new);
            startRecorded = true;
        }
        if (minutes > 0 || startRecorded) {
            str += minutes + context.getString(R.string.minute_new);
            startRecorded = true;
        }
        if (seconds > 0 || startRecorded) {
            str += seconds + context.getString(R.string.second_new);
        }
        return str;
    }

    /**
     * 为GoogleMap中的marker移动时添加动画效果(高频率移动)
     *
     * @param mMap       GoogleMap instance
     * @param marker     GoogleMarker instance
     * @param toPosition next destination point of marker
     * @param duration   animation total duration time
     */
    public static void animateMarker(final GoogleMap mMap, final Marker marker,
                                     final LatLng toPosition,
                                     final long duration) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
//        Projection proj = mMap.getProjection();
//        android.graphics.Point startPoint = proj.toScreenLocation(marker.getPosition());
//        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final LatLng startLatLng = marker.getPosition();
//        final Interpolator interpolator = new LinearInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                double t = (double) elapsed / duration;
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                if (t < 0.95d) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 10);
                } else {
                    marker.setPosition(toPosition);
                }
            }
        });
    }
}
