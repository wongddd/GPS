package com.yyt.trackcar.utils;

import android.text.TextUtils;

import com.xuexiang.xutil.data.ConvertTools;
import com.yyt.trackcar.bean.BLEDataModel;

import java.util.UUID;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.utils
 * @ fileName:      BLEDataUtils
 * @ author:        QING
 * @ createTime:    2023/4/22 17:25
 * @ describe:      TODO 蓝牙数据传输工具类
 */
public class BLEDataUtils {

    public final static String SERVER_URI = "tcp://mqtt.gps866.com:1883";
    public final static String BLE_CLIENT_ID = "BLEClient";
    public final static String BLE_USER_NAME = "BLE";
    public final static char[] BLE_PASSWROD = "BLE123456".toCharArray();
    public final static String BLE_TOPIC_NAME = "/request-config/p2p/";
    public final static String BLE_REQUERT_CONFIG = "/request-config";
    public final static String BLE_UPLOAD_LOCATION = "/data-instance";
    public final static String BLE_DEVICE_NAME = "LX-G16";
    public final static UUID SERVICE_UUID = UUID.fromString("0000fff0-0000-1000-8000" +
            "-00805f9b34fb");
    public final static UUID WRITE_CHAR_UUID = UUID.fromString("0000fff2-0000-1000-8000" +
            "-00805f9b34fb");
    public final static UUID READ_CHAR_UUID = UUID.fromString("0000fff1-0000-1000-8000" +
            "-00805f9b34fb");

    /**
     * 设置设备时间
     *
     * @param timeMillis 时间
     * @return 数据
     */
    public static byte[] setDeviceTime(long timeMillis) {
        StringBuilder dataString = new StringBuilder();
        dataString.append("01");
        completeHexString(dataString, Long.toHexString(timeMillis), 4);
        if (dataString.length() % 2 != 0) {
            return new byte[]{0x01};
        } else {
            return ConvertTools.hexStringToByteArray(dataString.toString());
        }
    }

    /**
     * 设置设备时间
     *
     * @param timeMillis 时间
     * @return 数据
     */
    public static byte[] setDeviceTime() {
        return new byte[]{0x01};
    }

    /**
     * 设置赛场ID
     *
     * @param matchID 比赛ID
     * @return 数据
     */
    public static byte[] setMatchID(String matchID) {
        if (TextUtils.isEmpty(matchID) || matchID.length() > 19) {
            return new byte[]{0x02};
        }
        byte[] byteArray = new byte[matchID.length() + 1];
        byteArray[0] = 0x02;
        for (int i = 0; i < matchID.length(); i++) {
            byteArray[i + 1] = (byte) matchID.charAt(i);
        }
        return byteArray;
    }

    /**
     * 设置比赛时间
     *
     * @param model 对象
     * @return 数据
     */
    public static byte[] setMatchTime(BLEDataModel model) {
        if (model == null
                || model.getNightTime() == null || model.getContinuedFlyTime() == null
                || model.getLowBattery() == null || model.getDelayTime() == null
                || model.getForcedStartup() == null) {
            return new byte[]{0x03};
        }
        StringBuilder dataString = new StringBuilder();
        dataString.append("03");
        if (model.getStartTime() == null) {
            completeHexString(dataString, "", 4);
        } else {
            completeHexString(dataString, Long.toHexString(model.getStartTime()), 4);
        }
        completeHexString(dataString, Long.toHexString(model.getNightTime()), 4);
        completeHexString(dataString, Long.toHexString(model.getContinuedFlyTime()), 4);
        completeHexString(dataString, Integer.toHexString(model.getLowBattery()), 1);
        completeHexString(dataString, Long.toHexString(model.getDelayTime()), 4);
        completeHexString(dataString, Integer.toHexString(model.getForcedStartup()), 1);
        if (dataString.length() % 2 != 0) {
            return new byte[]{0x03};
        } else {
            return ConvertTools.hexStringToByteArray(dataString.toString());
        }
    }

    /**
     * 设置工作模式
     *
     * @param model 对象
     * @return 数据
     */
    public static byte[] setWorkMode(BLEDataModel model) {
        if (model == null || model.getStartLocationInterval() == null
                || model.getNightLocationInterval() == null
                || model.getContinuedFlyLocationInterval() == null
                || model.getLowBatteryLocationInterval() == null) {
            return new byte[]{0x04};
        }
        StringBuilder dataString = new StringBuilder();
        dataString.append("04");
        completeHexString(dataString, Integer.toHexString(model.getStartLocationInterval()), 2);
        completeHexString(dataString, Integer.toHexString(model.getNightLocationInterval()), 2);
        completeHexString(dataString,
                Integer.toHexString(model.getContinuedFlyLocationInterval()), 2);
        completeHexString(dataString, Integer.toHexString(model.getLowBatteryLocationInterval()),
                2);
        if (dataString.length() % 2 != 0) {
            return new byte[]{0x04};
        } else {
            return ConvertTools.hexStringToByteArray(dataString.toString());
        }
    }

    /**
     * 恢复出厂设置
     *
     * @return 数据
     */
    public static byte[] resetDevice() {
        return ConvertTools.hexStringToByteArray("FFFFFF");
    }

    /**
     * 获取定位数据
     *
     * @param type 类型
     * @return 数据
     */
    public static byte[] setLocationData(byte type) {
        return new byte[]{0x05, type};
    }

    /**
     * 完成配置
     *
     * @return 数据
     */
    public static byte[] completeConfig() {
        return new byte[]{0x06};
    }

    /**
     * 补全数据
     *
     * @param dataString 数据字符串
     * @param hexString  待补全16进制字符串
     * @param length     字节大小
     */
    private static void completeHexString(StringBuilder dataString, String hexString, int length) {
        if (dataString == null || hexString == null || length <= 0) {
            return;
        }
        if (hexString.length() > length * 2) {
            dataString.append(hexString.substring(hexString.length() - length * 2));
        } else if (hexString.length() < length * 2) {
            for (int i = 0; i < length * 2 - hexString.length(); i++) {
                dataString.append("0");
            }
            dataString.append(hexString);
        } else {
            dataString.append(hexString);
        }
    }

    /**
     * 转换mac地址
     *
     * @param macAddress 蓝牙mac地址
     * @return imei
     */
    public static String convertMacAddress(String macAddress) {
        StringBuilder dataString = new StringBuilder();
        if (!TextUtils.isEmpty(macAddress)) {
            String[] array = macAddress.split(":");
            for (String hexString : array) {
                try {
                    String num = String.valueOf(Integer.parseInt(hexString, 16));
                    for (int i = 0; i < 3 - num.length(); i++) {
                        dataString.append("0");
                    }
                    dataString.append(num);
                } catch (NumberFormatException ignored) {

                }
            }
        }
        if (dataString.length() > 0) {
            return dataString.toString();
        } else {
            return macAddress;
        }
    }

}
