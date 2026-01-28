package com.yyt.trackcar.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAACarGpsBean;
import com.yyt.trackcar.bean.Rectangle;
import com.yyt.trackcar.dbflow.AAADeviceModel;

import java.util.List;
import java.util.Locale;

/**
 * 项目名：   传信鸽
 * 包名：     com.yyt.trackcar.utils
 * 文件名：   AAAMapUtils
 * 创建者：   QING
 * 创建时间： 2018/4/25 19:14
 * 描述：     TODO 地图标记工具类
 */

public class NewMapUtils {

    /**
     * 设置地图标记图标
     *
     * @param resources  上下文
     * @param AAACarGpsBean 设备对象
     * @return 地图标记图标
     */
    public static Bitmap getMarkerIcon(Resources resources, AAACarGpsBean AAACarGpsBean) {
        int angle = 0;
        if (!TextUtils.isEmpty(AAACarGpsBean.getCourseOverGround()))
            angle = (int) Double.parseDouble(AAACarGpsBean.getCourseOverGround());
        Bitmap bitmap;
        if ("1".equals(AAACarGpsBean.getOnline())) {
            if (angle >= 45 && angle < 90)
                bitmap = BitmapFactory
                        .decodeResource(resources, R.mipmap.online_0);
            else if (angle >= 90 && angle < 135)
                bitmap = BitmapFactory
                        .decodeResource(resources, R.mipmap.online_90);
            else if (angle >= 135 && angle < 180)
                bitmap = BitmapFactory
                        .decodeResource(resources, R.mipmap.online_135);
            else if (angle >= 180 && angle < 225)
                bitmap = BitmapFactory
                        .decodeResource(resources, R.mipmap.online_180);
            else if (angle >= 225 && angle < 270)
                bitmap = BitmapFactory
                        .decodeResource(resources, R.mipmap.online_225);
            else if (angle >= 270 && angle < 315)
                bitmap = BitmapFactory
                        .decodeResource(resources, R.mipmap.online_270);
            else if (angle >= 315 && angle < 360)
                bitmap = BitmapFactory
                        .decodeResource(resources, R.mipmap.online_315);
            else
                bitmap = BitmapFactory
                        .decodeResource(resources, R.mipmap.online_0);
        } else {
            if (angle >= 45 && angle < 90)
                bitmap = BitmapFactory
                        .decodeResource(resources, R.mipmap.offline_45);
            else if (angle >= 90 && angle < 135)
                bitmap = BitmapFactory
                        .decodeResource(resources, R.mipmap.offline_90);
            else if (angle >= 135 && angle < 180)
                bitmap = BitmapFactory
                        .decodeResource(resources, R.mipmap.offline_135);
            else if (angle >= 180 && angle < 225)
                bitmap = BitmapFactory
                        .decodeResource(resources, R.mipmap.offline_180);
            else if (angle >= 225 && angle < 270)
                bitmap = BitmapFactory
                        .decodeResource(resources, R.mipmap.offline_225);
            else if (angle >= 270 && angle < 315)
                bitmap = BitmapFactory
                        .decodeResource(resources, R.mipmap.offline_270);
            else if (angle >= 315 && angle < 360)
                bitmap = BitmapFactory
                        .decodeResource(resources, R.mipmap.offline_315);
            else
                bitmap = BitmapFactory
                        .decodeResource(resources, R.mipmap.offline_0);
        }
        return bitmap;
    }

    /**
     * 设置地图标记图标
     *
     * @param resources   上下文
     * @param deviceModel 设备对象
     * @return 地图标记图标
     */
    public static Bitmap getMarkerIcon(Resources resources, AAADeviceModel deviceModel) {
        Bitmap bitmap;
//        if (deviceModel != null && deviceModel.getOnline() == 1) {
//            if ("Northwest".equals(deviceModel.getDirect()))
//                bitmap = BitmapFactory
//                        .decodeResource(resources, R.mipmap.online_315);
//            else if ("Northeast".equals(deviceModel.getDirect()))
//                bitmap = BitmapFactory
//                        .decodeResource(resources, R.mipmap.online_45);
//            else if ("West".equals(deviceModel.getDirect()))
//                bitmap = BitmapFactory
//                        .decodeResource(resources, R.mipmap.online_270);
//            else if ("East".equals(deviceModel.getDirect()))
//                bitmap = BitmapFactory
//                        .decodeResource(resources, R.mipmap.online_90);
//            else if ("South".equals(deviceModel.getDirect()))
//                bitmap = BitmapFactory
//                        .decodeResource(resources, R.mipmap.online_180);
//            else if ("Southwest".equals(deviceModel.getDirect()))
//                bitmap = BitmapFactory
//                        .decodeResource(resources, R.mipmap.online_225);
//            else if ("Southeast".equals(deviceModel.getDirect()))
//                bitmap = BitmapFactory
//                        .decodeResource(resources, R.mipmap.online_135);
//            else
//                bitmap = BitmapFactory
//                        .decodeResource(resources, R.mipmap.online_0);
//        } else {
//            if ("Northwest".equals(deviceModel.getDirect()))
//                bitmap = BitmapFactory
//                        .decodeResource(resources, R.mipmap.offline_315);
//            else if ("Northeast".equals(deviceModel.getDirect()))
//                bitmap = BitmapFactory
//                        .decodeResource(resources, R.mipmap.offline_45);
//            else if ("West".equals(deviceModel.getDirect()))
//                bitmap = BitmapFactory
//                        .decodeResource(resources, R.mipmap.offline_270);
//            else if ("East".equals(deviceModel.getDirect()))
//                bitmap = BitmapFactory
//                        .decodeResource(resources, R.mipmap.offline_90);
//            else if ("South".equals(deviceModel.getDirect()))
//                bitmap = BitmapFactory
//                        .decodeResource(resources, R.mipmap.offline_180);
//            else if ("Southwest".equals(deviceModel.getDirect()))
//                bitmap = BitmapFactory
//                        .decodeResource(resources, R.mipmap.offline_225);
//            else if ("Southeast".equals(deviceModel.getDirect()))
//                bitmap = BitmapFactory
//                        .decodeResource(resources, R.mipmap.offline_135);
//            else
//                bitmap = BitmapFactory
//                        .decodeResource(resources, R.mipmap.offline_0);
//        }
//        if (deviceModel == null || TextUtils.isEmpty(deviceModel.getImeiNo()))
//            bitmap = BitmapFactory
//                    .decodeResource(resources, R.mipmap.ic_marker_car);
//        else {
//            String deviceType = MainApplication.getInstance().getDeviceTypeMap()
//                    .get(deviceModel.getImeiNo());
//            if (TextUtils.isEmpty(deviceType))
//                bitmap = BitmapFactory
//                        .decodeResource(resources, R.mipmap.ic_marker_car);
//            else {
//                deviceType = deviceType.toLowerCase();
//                if (deviceType.contains("truck"))
//                    bitmap = BitmapFactory
//                            .decodeResource(resources, R.mipmap.ic_marker_truck);
//                else if (deviceType.contains("bicycle"))
//                    bitmap = BitmapFactory
//                            .decodeResource(resources, R.mipmap.ic_marker_bicycle);
//                else if (deviceType.contains("bike"))
//                    bitmap = BitmapFactory
//                            .decodeResource(resources, R.mipmap.ic_marker_electric_bike);
//                else
//                    bitmap = BitmapFactory
//                            .decodeResource(resources, R.mipmap.ic_marker_car);
//            }
//        }
        bitmap = BitmapFactory
                .decodeResource(resources, getMarkerRes(deviceModel));
        return bitmap;
    }


    /**
     * 获取行驶地图标记图标
     *
     * @param resources  上下文
     * @param AAACarGpsBean 设备对象
     * @return 地图标记图标
     */
    public static Bitmap getDrivingMarkerIcon(Resources resources, AAACarGpsBean AAACarGpsBean) {
        int angle = 0;
        if (!TextUtils.isEmpty(AAACarGpsBean.getCourseOverGround()))
            angle = (int) Double.parseDouble(AAACarGpsBean.getCourseOverGround());
        Bitmap bitmap;
        if (angle >= 45 && angle < 90)
            bitmap = BitmapFactory
                    .decodeResource(resources, R.mipmap.driving_45);
        else if (angle >= 90 && angle < 135)
            bitmap = BitmapFactory
                    .decodeResource(resources, R.mipmap.driving_90);
        else if (angle >= 135 && angle < 180)
            bitmap = BitmapFactory
                    .decodeResource(resources, R.mipmap.driving_135);
        else if (angle >= 180 && angle < 225)
            bitmap = BitmapFactory
                    .decodeResource(resources, R.mipmap.driving_180);
        else if (angle >= 225 && angle < 270)
            bitmap = BitmapFactory
                    .decodeResource(resources, R.mipmap.driving_225);
        else if (angle >= 270 && angle < 315)
            bitmap = BitmapFactory
                    .decodeResource(resources, R.mipmap.driving_270);
        else if (angle >= 315 && angle < 360)
            bitmap = BitmapFactory
                    .decodeResource(resources, R.mipmap.driving_315);
        else
            bitmap = BitmapFactory
                    .decodeResource(resources, R.mipmap.driving_0);
        return bitmap;
    }

    /**
     * 获取行驶地图标记图标
     *
     * @param resources   上下文
     * @param deviceModel 设备对象
     * @return 地图标记图标
     */
    public static Bitmap getDrivingMarkerIcon(Resources resources, AAADeviceModel deviceModel) {
        Bitmap bitmap;
//        if ("Northwest".equals(deviceModel.getDirect()))
//            bitmap = BitmapFactory
//                    .decodeResource(resources, R.mipmap.driving_315);
//        else if ("Northeast".equals(deviceModel.getDirect()))
//            bitmap = BitmapFactory
//                    .decodeResource(resources, R.mipmap.driving_45);
//        else if ("West".equals(deviceModel.getDirect()))
//            bitmap = BitmapFactory
//                    .decodeResource(resources, R.mipmap.driving_270);
//        else if ("East".equals(deviceModel.getDirect()))
//            bitmap = BitmapFactory
//                    .decodeResource(resources, R.mipmap.driving_90);
//        else if ("South".equals(deviceModel.getDirect()))
//            bitmap = BitmapFactory
//                    .decodeResource(resources, R.mipmap.driving_180);
//        else if ("Southwest".equals(deviceModel.getDirect()))
//            bitmap = BitmapFactory
//                    .decodeResource(resources, R.mipmap.driving_225);
//        else if ("Southeast".equals(deviceModel.getDirect()))
//            bitmap = BitmapFactory
//                    .decodeResource(resources, R.mipmap.driving_135);
//        else
//            bitmap = BitmapFactory
//                    .decodeResource(resources, R.mipmap.driving_0);
//        if (deviceModel == null || TextUtils.isEmpty(deviceModel.getImeiNo()))
//            bitmap = BitmapFactory
//                    .decodeResource(resources, R.mipmap.ic_marker_car);
//        else {
//            String deviceType = MainApplication.getInstance().getDeviceTypeMap()
//                    .get(deviceModel.getImeiNo());
//            if (TextUtils.isEmpty(deviceType))
//                bitmap = BitmapFactory
//                        .decodeResource(resources, R.mipmap.ic_marker_car);
//            else {
//                deviceType = deviceType.toLowerCase();
//                if (deviceType.contains("truck"))
//                    bitmap = BitmapFactory
//                            .decodeResource(resources, R.mipmap.ic_marker_truck);
//                else if (deviceType.contains("bicycle"))
//                    bitmap = BitmapFactory
//                            .decodeResource(resources, R.mipmap.ic_marker_bicycle);
//                else if (deviceType.contains("bike"))
//                    bitmap = BitmapFactory
//                            .decodeResource(resources, R.mipmap.ic_marker_electric_bike);
//                else
//                    bitmap = BitmapFactory
//                            .decodeResource(resources, R.mipmap.ic_marker_car);
//            }
//        }
        bitmap = BitmapFactory
                .decodeResource(resources, getMarkerRes(deviceModel));
        return bitmap;
    }

    /**
     * 获取行驶地图特殊点标记图标
     *
     * @param resources 上下文
     * @param type      类型
     * @return 地图标记图标
     */
    public static Bitmap getDrivingMarkerIcon(Resources resources, int type) {
        Bitmap bitmap;
        switch (type) {
            case 0: // 开始
                bitmap = BitmapFactory
                        .decodeResource(resources, R.mipmap.start_point);
                break;
            case 1: // 结束
                bitmap = BitmapFactory
                        .decodeResource(resources, R.mipmap.end_point);
                break;
            default: // 停靠
                bitmap = BitmapFactory
                        .decodeResource(resources, R.mipmap.stop_point);
                break;
        }
        return bitmap;
    }

    /**
     * 设置监控中心地图标记图标
     *
     * @param context    上下文
     * @param AAACarGpsBean 设备对象
     * @return 地图标记图标
     */
    public static View getTrackingMarkerIcon(Context context, AAACarGpsBean AAACarGpsBean) {
        View view = LayoutInflater.from(context).inflate(R.layout
                .map_tracking_icon_layout, null);
        ImageView iconImg = view.findViewById(R.id.map_tracking_icon);
        TextView mHide = view.findViewById(R.id.map_tracking_icon_hide_text);
        TextView mImei = view.findViewById(R.id.map_tracking_icon_imei);
        String name;
        if (TextUtils.isEmpty(AAACarGpsBean.getName()))
            name = AAACarGpsBean.getImei();
        else
            name = AAACarGpsBean.getName();
        mImei.setText(name);
        mHide.setText(name);
        int angle = 0;
        if (!TextUtils.isEmpty(AAACarGpsBean.getCourseOverGround()))
            angle = (int) Double.parseDouble(AAACarGpsBean.getCourseOverGround());
        if ("1".equals(AAACarGpsBean.getOnline())) {
            if (angle >= 45 && angle < 90)
                iconImg.setImageResource(R.mipmap.online_0);
            else if (angle >= 90 && angle < 135)
                iconImg.setImageResource(R.mipmap.online_90);
            else if (angle >= 135 && angle < 180)
                iconImg.setImageResource(R.mipmap.online_135);
            else if (angle >= 180 && angle < 225)
                iconImg.setImageResource(R.mipmap.online_180);
            else if (angle >= 225 && angle < 270)
                iconImg.setImageResource(R.mipmap.online_225);
            else if (angle >= 270 && angle < 315)
                iconImg.setImageResource(R.mipmap.online_270);
            else if (angle >= 315 && angle < 360)
                iconImg.setImageResource(R.mipmap.online_315);
            else
                iconImg.setImageResource(R.mipmap.online_0);
        } else {
            if (angle >= 45 && angle < 90)
                iconImg.setImageResource(R.mipmap.offline_45);
            else if (angle >= 90 && angle < 135)
                iconImg.setImageResource(R.mipmap.offline_90);
            else if (angle >= 135 && angle < 180)
                iconImg.setImageResource(R.mipmap.offline_135);
            else if (angle >= 180 && angle < 225)
                iconImg.setImageResource(R.mipmap.offline_180);
            else if (angle >= 225 && angle < 270)
                iconImg.setImageResource(R.mipmap.offline_225);
            else if (angle >= 270 && angle < 315)
                iconImg.setImageResource(R.mipmap.offline_270);
            else if (angle >= 315 && angle < 360)
                iconImg.setImageResource(R.mipmap.offline_315);
            else
                iconImg.setImageResource(R.mipmap.offline_0);
        }
        return view;
    }

    /**
     * 设置监控中心地图标记图标
     *
     * @param context     上下文
     * @param deviceModel 设备对象
     * @return 地图标记图标
     */
    public static View getTrackingMarkerIcon(Context context, AAADeviceModel deviceModel) {
        View view = LayoutInflater.from(context).inflate(R.layout
                .map_tracking_icon_layout, null);
        ImageView iconImg = view.findViewById(R.id.map_tracking_icon);
        TextView mHide = view.findViewById(R.id.map_tracking_icon_hide_text);
        TextView mImei = view.findViewById(R.id.map_tracking_icon_imei);
        String name;
        if (TextUtils.isEmpty(deviceModel.getDeviceName()))
            name = deviceModel.getDeviceImei();
        else
            name = deviceModel.getDeviceName();
        mImei.setText(name);
        mHide.setText(name);
        if (deviceModel.getOnline() == 1) {
            if ("Northeast".equals(deviceModel.getDirect()))
                iconImg.setImageResource(R.mipmap.online_45);
            else if ("East".equals(deviceModel.getDirect()))
                iconImg.setImageResource(R.mipmap.online_90);
            else if ("Southeast".equals(deviceModel.getDirect()))
                iconImg.setImageResource(R.mipmap.online_135);
            else if ("South".equals(deviceModel.getDirect()))
                iconImg.setImageResource(R.mipmap.online_180);
            else if ("Southwest".equals(deviceModel.getDirect()))
                iconImg.setImageResource(R.mipmap.online_225);
            else if ("West".equals(deviceModel.getDirect()))
                iconImg.setImageResource(R.mipmap.online_270);
            else if ("Northwest".equals(deviceModel.getDirect()))
                iconImg.setImageResource(R.mipmap.online_315);
            else
                iconImg.setImageResource(R.mipmap.online_0);
        } else {
            if ("Northeast".equals(deviceModel.getDirect()))
                iconImg.setImageResource(R.mipmap.offline_45);
            else if ("East".equals(deviceModel.getDirect()))
                iconImg.setImageResource(R.mipmap.offline_90);
            else if ("Southeast".equals(deviceModel.getDirect()))
                iconImg.setImageResource(R.mipmap.offline_135);
            else if ("South".equals(deviceModel.getDirect()))
                iconImg.setImageResource(R.mipmap.offline_180);
            else if ("Southwest".equals(deviceModel.getDirect()))
                iconImg.setImageResource(R.mipmap.offline_225);
            else if ("West".equals(deviceModel.getDirect()))
                iconImg.setImageResource(R.mipmap.offline_270);
            else if ("Northwest".equals(deviceModel.getDirect()))
                iconImg.setImageResource(R.mipmap.offline_315);
            else
                iconImg.setImageResource(R.mipmap.offline_0);
        }

        iconImg.setImageResource(getMarkerRes(deviceModel));
        return view;
    }

    private static int getMarkerRes(AAADeviceModel deviceModel) {
        int imgRes;
        if (deviceModel == null || TextUtils.isEmpty(deviceModel.getImeiNo()))
            imgRes = R.mipmap.ic_marker_car;
        else {
//            String deviceType =
//                    MainApplication.getInstance().getDeviceTypeMap().get(deviceModel.getImeiNo());
            String deviceType = null;
            int deviceStatus;
            if (deviceModel.isOnlineStatus() && deviceModel.getLastMotionStatus() != null
                    && deviceModel.getLastMotionStatus() == 1)
                deviceStatus = 1;
            else if (deviceModel.isOnlineStatus())
                deviceStatus = 2;
            else
                deviceStatus = 0;
            if (deviceType == null) {
                if (deviceStatus == 2)
                    imgRes = R.mipmap.ic_marker_car_green;
                else if (deviceStatus == 1)
                    imgRes = R.mipmap.ic_marker_car_red;
                else
                    imgRes = R.mipmap.ic_marker_car;
            } else {
                deviceType = deviceType.toLowerCase();
                if (deviceType.contains("truck")) {
                    if (deviceStatus == 2)
                        imgRes = R.mipmap.ic_marker_truck_green;
                    else if (deviceStatus == 1)
                        imgRes = R.mipmap.ic_marker_truck_red;
                    else
                        imgRes = R.mipmap.ic_marker_truck;
                } else if (deviceType.contains("bicycle")) {
                    if (deviceStatus == 2)
                        imgRes = R.mipmap.ic_marker_bicycle_green;
                    else if (deviceStatus == 1)
                        imgRes = R.mipmap.ic_marker_bicycle_red;
                    else
                        imgRes = R.mipmap.ic_marker_bicycle;
                } else if (deviceType.contains("bike")) {
                    if (deviceStatus == 2)
                        imgRes = R.mipmap.ic_marker_electric_bike_green;
                    else if (deviceStatus == 1)
                        imgRes = R.mipmap.ic_marker_electric_bike_red;
                    else
                        imgRes = R.mipmap.ic_marker_electric_bike;
                } else {
                    if (deviceStatus == 2)
                        imgRes = R.mipmap.ic_marker_car_green;
                    else if (deviceStatus == 1)
                        imgRes = R.mipmap.ic_marker_car_red;
                    else
                        imgRes = R.mipmap.ic_marker_car;
                }
            }
        }
        return imgRes;
    }

    /**
     * 计算两点之间距离
     *
     * @param start 开始距离
     * @param end   结束距离
     * @return 米
     */
    public static double getDistance(LatLng start, LatLng end) {
        double lat1 = (Math.PI / 180) * start.latitude;
        double lat2 = (Math.PI / 180) * end.latitude;

        double lon1 = (Math.PI / 180) * start.longitude;
        double lon2 = (Math.PI / 180) * end.longitude;

//      double Lat1r = (Math.PI/180)*(gp1.getLatitudeE6()/1E6);
//      double Lat2r = (Math.PI/180)*(gp2.getLatitudeE6()/1E6);
//      double Lon1r = (Math.PI/180)*(gp1.getLongitudeE6()/1E6);
//      double Lon2r = (Math.PI/180)*(gp2.getLongitudeE6()/1E6);

        //地球半径
        double R = 6371;

        //两点间距离 km，如果想要米的话，结果*1000就可以了
        double d = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) *
                Math.cos(lon2 - lon1)) * R;

        return d * 1000;
    }

    /**
     * 获取地址
     *
     * @param context   上下文
     * @param latitude  纬度
     * @param longitude 经度
     * @return 返回地址
     */
    public static String getAddress(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> findAddress = geocoder.getFromLocation(latitude, longitude, 1);
            if (findAddress.size() > 0) {
                StringBuilder address = new StringBuilder();
                if (!TextUtils.isEmpty(findAddress.get(0).getCountryName()))
                    address.append(findAddress.get(0).getCountryName());
                if (!TextUtils.isEmpty(findAddress.get(0).getAdminArea()))
                    address.append(findAddress.get(0).getAdminArea());
                if (!TextUtils.isEmpty(findAddress.get(0).getLocality()))
                    address.append(findAddress.get(0).getLocality());
                if (!TextUtils.isEmpty(findAddress.get(0).getFeatureName()))
                    address.append(findAddress.get(0).getFeatureName());
                return address.toString();
            } else
                return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 地图导航
     *
     * @param activity  上下文
     * @param latitude  纬度
     * @param longitude 经度
     * @param type      类型
     */
    public static void naviMap(Activity activity, double latitude, double longitude, int type) {
        Intent intent;
        Uri uri;
        switch (type) {
            case 0: // 高德地图
//                    try {
//                        NaviPara para = new NaviPara();
//                        para.setTargetPoint(mLatLng);
//                        AMapUtils.openAMapNavi(para, this);
//                    } catch (Exception e) {
//                        showMessage(e.getMessage());
//                    }
                if (AAAAppUtils.isAvilible(activity, "com.autonavi.minimap")) {
                    uri = Uri.parse("androidamap://route?dlat=" + latitude + "&dlon=" + longitude
                            + "&dname=" + activity.getString(R.string.destination) +
                            "&dev=0&t=0&m=0");
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setPackage("com.autonavi.minimap");
                    if (intent.resolveActivity(activity.getPackageManager()) != null)
                        activity.startActivity(intent);
                } else {
                    Toast.makeText(activity, R.string.no_gaode_map_tips, Toast.LENGTH_SHORT).show();
                    uri = Uri.parse("market://details?id=com.autonavi.minimap");
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                    if (intent.resolveActivity(activity.getPackageManager()) != null)
                        activity.startActivity(intent);
                }
                break;
            case 1: // 百度地图
                if (AAAAppUtils.isAvilible(activity, "com.baidu.BaiduMap")) {//传入指定应用包名
//                        uri = Uri.parse("baidumap://map/navi?location=" + gps.getWgLat() + "," +
//                                gps.getWgLon());
                    uri = Uri.parse("baidumap://map/direction?destination=" + latitude + "," +
                            longitude + "&mode=driving");
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setPackage("com.baidu.BaiduMap");
                    if (intent.resolveActivity(activity.getPackageManager()) != null)
                        activity.startActivity(intent);
                } else {
                    Toast.makeText(activity, R.string.no_baidu_map_tips, Toast.LENGTH_SHORT).show();
                    uri = Uri.parse("market://details?id=com.baidu.BaiduMap");
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                    if (intent.resolveActivity(activity.getPackageManager()) != null)
                        activity.startActivity(intent);
                }
                break;
            case 2: // 腾讯地图
                if (AAAAppUtils.isAvilible(activity, "com.tencent.map")) {//传入指定应用包名
                    //将功能Scheme以URI的方式传入data
                    uri = Uri.parse("qqmap://map/routeplan?type=drive&to=" + activity.getString(R
                            .string.destination) + "&tocoord=" + latitude + "," + longitude);
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setPackage("com.tencent.map");
                    if (intent.resolveActivity(activity.getPackageManager()) != null)
                        activity.startActivity(intent);
                    return;
                } else {
                    Toast.makeText(activity, R.string.no_tencent_map_tips, Toast.LENGTH_SHORT)
                            .show();

                    uri = Uri.parse("market://details?id=com.tencent.map");
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                    if (intent.resolveActivity(activity.getPackageManager()) != null)
                        activity.startActivity(intent);
                }
                break;
            case 3: // google map
                if (AAAAppUtils.isAvilible(activity, "com.google.android.apps.maps")) {
//                    uri = Uri.parse("directions:" + latitude + "," + longitude +
//                            "&mode=d");
                    uri = Uri.parse("http://ditu.google.cn/maps?f=d&source=s_d&&daddr=" +
                            latitude + "," +
                            longitude + "&hl=zh");
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setPackage("com.google.android.apps.maps");
                    if (intent.resolveActivity(activity.getPackageManager()) != null)
                        activity.startActivity(intent);
                } else {
                    Toast.makeText(activity, R.string.no_google_map_tips, Toast.LENGTH_SHORT)
                            .show();
                    uri = Uri.parse("market://details?id=com.google.android.apps.maps");
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                    if (intent.resolveActivity(activity.getPackageManager()) != null)
                        activity.startActivity(intent);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 是否实时定位手机
     *
     * @param AAACarGpsBean 设备信息
     * @return 是否实时定位手机
     */
    public static boolean isOnceLocation(AAACarGpsBean AAACarGpsBean) {
        //return true;
        if (AAACarGpsBean == null || TextUtils.isEmpty(AAACarGpsBean.getAlarm()))
            return true;
        else {
            int switchValue = 0;
            try {
                switchValue = Integer.parseInt(AAACarGpsBean.getAlarm());
            } catch (Exception e) {
                if (BuildConfig.DEBUG)
                    e.printStackTrace();
            }
            return (switchValue & 0x08) != 0x08;
        }
    }


    /**
     * 是否在中国内
     *
     * @param latitude  纬度
     * @param longitude 经度
     * @return 返回结果
     */
    public static boolean isInsideChina(double latitude, double longitude) {
        Rectangle[] region = new Rectangle[]{
                new Rectangle(49.220400, 079.446200, 42.889900, 096.330000),
                new Rectangle(54.141500, 109.687200, 39.374200, 135.000200),
                new Rectangle(42.889900, 073.124600, 29.529700, 124.143255),
                new Rectangle(29.529700, 082.968400, 26.718600, 097.035200),
                new Rectangle(29.529700, 097.025300, 20.414096, 124.367395),
                new Rectangle(20.414096, 107.975793, 17.871542, 111.744104),};
        Rectangle[] exclude = new Rectangle[]{
                new Rectangle(25.398623, 119.921265, 21.785006, 122.497559),
                new Rectangle(22.284000, 101.865200, 20.098800, 106.665000),
                new Rectangle(21.542200, 106.452500, 20.487800, 108.051000),
                new Rectangle(55.817500, 109.032300, 50.325700, 119.127000),
                new Rectangle(55.817500, 127.456800, 49.557400, 137.022700),
                new Rectangle(44.892200, 131.266200, 42.569200, 137.022700),};
        for (Rectangle Rectangle : region) {
            if (inRectangle(Rectangle, latitude, longitude)) {
                for (Rectangle AAARectangle1 : exclude) {
                    if (inRectangle(AAARectangle1, latitude, longitude)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return true;
    }

    /**
     * 是否在范围内
     *
     * @param rect      范围
     * @param latitude  纬度
     * @param longitude 经度
     * @return 返回结果
     */
    private static boolean inRectangle(Rectangle rect, double latitude, double longitude) {
        return rect.getWest() <= longitude && rect.getEast() >= longitude && rect.getNorth() >= latitude && rect.getSouth() <= latitude;
    }

}
