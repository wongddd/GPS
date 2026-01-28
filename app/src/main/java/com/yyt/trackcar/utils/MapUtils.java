package com.yyt.trackcar.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;
import com.xuexiang.xutil.app.AppUtils;
import com.yyt.trackcar.bean.Rectangle;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

/**
 * 项目名：   传信鸽
 * 包名：     com.llt.cargps.utils
 * 文件名：   AAAMapUtils
 * 创建者：   QING
 * 创建时间： 2018/4/25 19:14
 * 描述：     TODO 地图标记工具类
 */

public class MapUtils {

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
                StringBuilder addressSecond = new StringBuilder();
                if (!TextUtils.isEmpty(findAddress.get(0).getCountryName()))
                    address.append(findAddress.get(0).getCountryName());
                if (!TextUtils.isEmpty(findAddress.get(0).getAdminArea()))
                    address.append(findAddress.get(0).getAdminArea());
                if (!TextUtils.isEmpty(findAddress.get(0).getLocality()))
                    address.append(findAddress.get(0).getLocality());
                if (!TextUtils.isEmpty(findAddress.get(0).getFeatureName()))
                    address.append(findAddress.get(0).getFeatureName());
                if (findAddress.get(0).getMaxAddressLineIndex() >= 1 && !TextUtils.isEmpty(findAddress.get(0).getAddressLine(0)))
                    addressSecond.append(findAddress.get(0).getAddressLine(0));
                if (findAddress.get(0).getMaxAddressLineIndex() >= 1 && !TextUtils.isEmpty(findAddress.get(0).getAddressLine(1)))
                    addressSecond.append(findAddress.get(0).getAddressLine(1));
                if (address.length() > addressSecond.length())
                    return address.toString();
                else
                    return addressSecond.toString();
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
     * @param type      类型  0:高德地图  1:百度地图  2:腾讯地图  3:谷歌地图
     */
    public static void naviMap(Activity activity, double latitude, double longitude, int type) {
        Intent intent;
        Uri uri;
        switch (type) {
            case 0: // 高德地图
                if (AppUtils.isInstallApp("com.autonavi.minimap")) {
                    uri = Uri.parse("androidamap://route?dlat=" + latitude + "&dlon=" + longitude
                            + "&dname=&dev=0&t=0&m=0");
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setPackage("com.autonavi.minimap");
                    try {
                        activity.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    if (intent.resolveActivity(activity.getPackageManager()) != null)
                } else {
                    uri = Uri.parse("market://details?id=com.autonavi.minimap");
                    intent = new Intent(Intent.ACTION_VIEW, uri);
//                    if (intent.resolveActivity(activity.getPackageManager()) != null)
                    try {
                        activity.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 1: // 百度地图
                if (AppUtils.isInstallApp("com.baidu.BaiduMap")) {//传入指定应用包名
//                        uri = Uri.parse("baidumap://map/navi?location=" + gps.getWgLat() + "," +
//                                gps.getWgLon());
                    uri = Uri.parse("baidumap://map/direction?destination=" + latitude + "," +
                            longitude + "&mode=driving");
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setPackage("com.baidu.BaiduMap");
//                    if (intent.resolveActivity(activity.getPackageManager()) != null)
                    try {
                        activity.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    uri = Uri.parse("market://details?id=com.baidu.BaiduMap");
                    intent = new Intent(Intent.ACTION_VIEW, uri);
//                    if (intent.resolveActivity(activity.getPackageManager()) != null)
                    try {
                        activity.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 2: // 腾讯地图
                if (AppUtils.isInstallApp("com.tencent.map")) {//传入指定应用包名
                    //将功能Scheme以URI的方式传入data
                    uri = Uri.parse("qqmap://map/routeplan?type=drive&to="
                            + "&tocoord=" + latitude + "," + longitude);
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setPackage("com.tencent.map");
//                    if (intent.resolveActivity(activity.getPackageManager()) != null)
                    try {
                        activity.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                } else {
                    uri = Uri.parse("market://details?id=com.tencent.map");
                    intent = new Intent(Intent.ACTION_VIEW, uri);
//                    if (intent.resolveActivity(activity.getPackageManager()) != null)
                    try {
                        activity.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 3: // google map
                if (AppUtils.isInstallApp("com.google.android.apps.maps")) {
//                    uri = Uri.parse("directions:" + latitude + "," + longitude +
//                            "&mode=d");
//                    uri = Uri.parse("http://ditu.google.cn/maps?f=d&source=s_d&&daddr="
//                            + latitude + "," + longitude + "&hl=zh");
                    uri = Uri.parse("google.navigation:q=" + latitude + "," + longitude
//                            +", + TaiWan + Chinese"
//                            + ", + Chinese + Asia"
                    );
                    intent = new Intent(Intent.ACTION_VIEW, uri);
//                    // Create a Uri from an intent string. Use the result to create an Intent.
//                    Uri gmmIntentUri = Uri.parse("google.streetview:cbll=46.414382,10.013988");
//                    // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
//                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//                    // Make the Intent explicit by setting the Google Maps package
//                    mapIntent.setPackage("com.google.android.apps.maps");
//                    // Attempt to start an activity that can handle the Intent
//                    startActivity(mapIntent);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setPackage("com.google.android.apps.maps");
//                    if (intent.resolveActivity(activity.getPackageManager()) != null)
                    try {
                        activity.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    uri = Uri.parse("market://details?id=com.google.android.apps.maps");
                    intent = new Intent(Intent.ACTION_VIEW, uri);
//                    if (intent.resolveActivity(activity.getPackageManager()) != null)
                    try {
                        activity.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
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
        for (Rectangle rectangle : region) {
            if (inRectangle(rectangle, latitude, longitude)) {
                for (Rectangle rectangle1 : exclude) {
                    if (inRectangle(rectangle1, latitude, longitude)) {
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

    /**
     * 获取距离
     *
     * @param distance 距离
     * @return 结果
     */
    public static String getMapDistance(long distance) {
        if (distance / 1000 > 0) {
            DecimalFormat decimalFormat = new DecimalFormat("#.##km");
            return decimalFormat.format(distance / 1000.0f);
        } else {
            DecimalFormat decimalFormat = new DecimalFormat("0m");
            return decimalFormat.format(distance);
        }
    }

    public static LatLng moveCoordinate(double lat1, double lng1, double lat2, double lng2,
                                        float distance, int size) {
        double lat = (lat1 - lat2) / distance * size;
        double lng = (lng1 - lng2) / distance * size;
        return new LatLng(lat, lng);
    }

}
