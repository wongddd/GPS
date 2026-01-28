package com.yyt.trackcar.utils;

import android.content.Context;

import com.yyt.trackcar.R;

import java.util.List;

/**
 * @ projectName:
 * @ packageName:   com.yyt.trackcar.utils
 * @ fileName:      DataUtils
 * @ author:        QING
 * @ createTime:    6/21/21 14:11
 * @ describe:      TODO
 */
public class AAADataUtils {

    public static int getIntValue(Object object) {
        if (object == null)
            return 0;
        else if (object instanceof Integer)
            return (int) object;
        else if (object instanceof Double)
            return (int) ((double) object);
        else
            return 0;
    }

    public static float getFloatValue(Object object) {
        if (object == null)
            return 0;
        else if (object instanceof Integer)
            return (int) object;
        else if (object instanceof Float)
            return (float) object;
        else if (object instanceof Double)
            return (float) ((double) object);
        else
            return 0;
    }

    /**
     * @param context 上下文
     */
    public static void getAlarmSpeedList(Context context, List<String> list) {
        list.clear();
        list.add(context.getString(R.string.device_setting_close_over_speed));
        for (int i = 1; i <= 20; i++) {
            list.add(i * 10 + "KM/h");
        }
    }

    /**
     * @param context 上下文
     */
    public static void getMovementList(Context context, List<String> list) {
        list.clear();
        list.add(context.getString(R.string.device_setting_close_movement));
        list.add(String.format("30 %s",context.getString(R.string.unit_metre)));
        list.add(String.format("50 %s",context.getString(R.string.unit_metre)));
        list.add(String.format("100 %s",context.getString(R.string.unit_metre)));
        list.add(String.format("200 %s",context.getString(R.string.unit_metre)));
        list.add(String.format("300 %s",context.getString(R.string.unit_metre)));
        list.add(String.format("500 %s",context.getString(R.string.unit_metre)));
        list.add(String.format("1000 %s",context.getString(R.string.unit_metre)));
        list.add(String.format("2000 %s",context.getString(R.string.unit_metre)));
    }

    /**
     * @param context 上下文
     */
    public static void getAuthNoList(Context context, List<String> list) {
        list.clear();
        list.add(String.format("%s 1",context.getString(R.string.device_setting_key_no)));
        list.add(String.format("%s 2",context.getString(R.string.device_setting_key_no)));
        list.add(String.format("%s 3",context.getString(R.string.device_setting_key_no)));
    }

    /**
     * @param context 上下文
     */
    public static void getTakePhoneList(Context context, List<String> list) {
        list.clear();
        list.add(context.getString(R.string.device_setting_view));
        list.add(context.getString(R.string.chat_send));
    }

}
