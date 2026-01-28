package com.yyt.trackcar.utils;

import android.text.TextUtils;

import com.yyt.trackcar.MainApplication;
import com.yyt.trackcar.dbflow.AAADeviceModel;

import java.util.Map;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.utils
 * @ fileName:      DataUtils
 * @ author:        QING
 * @ createTime:    2023/3/28 15:09
 * @ describe:      TODO 数据工具类
 */
public class DataUtils {

    /**
     * 是否是鸽子定位器
     *
     * @param imei 设备号
     * @return 结果
     */
    public static boolean isPigeonDevice(String imei) {
        if (!TextUtils.isEmpty(imei)) {
            MainApplication application = MainApplication.getInstance();
            Map<String, String> deviceTypeMap = application.getDeviceTypeMap();
            String type = deviceTypeMap.get(imei);
            if (TextUtils.isEmpty(type)) {
                for (AAADeviceModel model : application.getTrackDeviceList()) {
                    if (imei.equals(model.getDeviceImei())) {
                        deviceTypeMap.put(imei, String.valueOf(model.getDeviceType()));
                        return model.getDeviceType() == DeviceType.PIGEON.getValue();
                    }
                }
            } else {
                return String.valueOf(DeviceType.PIGEON.getValue()).equals(type);
            }
        }
        return false;
    }

    /**
     * 获取设备类型
     *
     * @param imei 设备号
     * @return 结果
     */
    public static String getDeviceType(String imei) {
        if (!TextUtils.isEmpty(imei)) {
            MainApplication application = MainApplication.getInstance();
            Map<String, String> deviceTypeMap = application.getDeviceTypeMap();
            String type = deviceTypeMap.get(imei);
            if (TextUtils.isEmpty(type)) {
                for (AAADeviceModel model : application.getTrackDeviceList()) {
                    if (imei.equals(model.getDeviceImei())) {
                        String deviceType = String.valueOf(model.getDeviceType());
                        deviceTypeMap.put(imei, deviceType);
                        return deviceType;
                    }
                }
            } else {
                return type;
            }
        }
        return String.valueOf(DeviceType.VEHICLE.getValue());
    }

    /**
     * 设置登录方式
     *
     * @param type 登录方式 2.普通登录 888.设备号登录
     */
    public static void setLoginType(int type) {
        SettingSPUtils.getInstance().putInt(CWConstant.LOGIN_TYPE, type);
    }

    /**
     * 获取登录方式
     *
     * @return 登录方式
     */
    public static int getLoginType() {
        return SettingSPUtils.getInstance().getInt(CWConstant.LOGIN_TYPE, 0);
    }

    /**
     * 设置密码类型
     *
     * @param type 密码类型 0.普通密码 1.通用密码 2.超级密码
     */
    public static void setPwdType(Integer type) {
        SettingSPUtils.getInstance().putInt(CWConstant.PWD_TYPE, type == null ? 0 : type);
    }

    /**
     * 获取密码类型
     *
     * @return 密码类型
     */
    public static int getPwdType() {
        return SettingSPUtils.getInstance().getInt(CWConstant.PWD_TYPE, 0);
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public static void setCreateTime(long createTime) {
        SettingSPUtils.getInstance().putLong(CWConstant.CREATE_TIME, createTime);
    }

    /**
     * 获取创建时间
     *
     * @return 创建时间
     */
    public static long getCreateTime() {
        return SettingSPUtils.getInstance().getLong(CWConstant.CREATE_TIME, 0);
    }

    /**
     * 是否设备登录
     *
     * @return 结果
     */
    public static boolean isDeviceLogin() {
        return DataUtils.getLoginType() == CWConstant.LOGIN_TYPE_DEVICE;
    }

    /**
     * 是否经销商
     *
     * @return 结果
     */
    public static boolean isAgent() {
        return getPwdType() == 2;
    }
}
