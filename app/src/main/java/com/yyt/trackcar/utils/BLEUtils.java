package com.yyt.trackcar.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import com.socks.library.KLog;

import java.lang.reflect.Method;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.utils
 * @ fileName:      BLEUtils
 * @ author:        QING
 * @ createTime:    2023/4/22 14:06
 * @ describe:      TODO 蓝牙工具类
 */
public class BLEUtils {

    /**
     * 蓝牙是否开启
     *
     * @return 结果
     */
    public static boolean isEnableBLE() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        return adapter.isEnabled();
    }

    // 开启蓝牙
    @SuppressLint("MissingPermission")
    public static void enableBLE(Activity activity) {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter != null && activity != null && !adapter.isEnabled()) {
            adapter.enable();
        }
    }

    /**
     * 关闭蓝牙
     */
    public static void disableBLE(Activity activity) {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter != null && activity != null && adapter.isEnabled()) {
            if (ActivityCompat.checkSelfPermission(activity,
                    Manifest.permission.BLUETOOTH_CONNECT) ==
                    PackageManager.PERMISSION_GRANTED) {
                adapter.disable();
            }
        }
    }

    //反射来调用BluetoothDevice.removeBond取消设备的配对
    public static void unpairDevice(BluetoothDevice device) {
        try {
            Method removeBondMethod = device.getClass().getMethod("removeBond");
            Boolean returnValue = (Boolean) removeBondMethod.invoke(device);
            KLog.d("returnValue:" + returnValue);
//            Method m = device.getClass()
//                    .getMethod("removeBond", (Class[]) null);
//            m.invoke(device, (Object[]) null);
        } catch (Exception e) {
        }
    }

}
