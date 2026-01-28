package com.yyt.trackcar.utils;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.utils
 * @ fileName:      NotificationUtils
 * @ author:        QING
 * @ createTime:    2020/4/9 05:09
 * @ describe:      TODO 系统通知工具类
 */
public class NotificationUtils {
    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
    private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static boolean isNotificationEnabled(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo appInfo = context.getApplicationInfo();
        String packageName = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;

        Class<?> appOpsClass = null;
        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method method = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
                    String.class);
            Field notificationFieldValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);
            int value = (int) notificationFieldValue.get(Integer.class);
            return ((int) method.invoke(appOps, value, uid, packageName) == AppOpsManager.MODE_ALLOWED);
        } catch (ClassNotFoundException | NoSuchMethodException | NoSuchFieldException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void toSetting(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        context.startActivity(localIntent);
    }

//    public static void showNotification(Context context, String title, String content, PendingIntent intent, int notificationId, int defaults){
//        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        manager.cancel(notificationId);
//        Notification notification = new NotificationCompat.Builder(context, CWConstant.CHANNEL_DEVICE_MESSAGE_ID)
//                .setTicker(content)
//                .setContentTitle(title)
//                .setContentText(content)
//                .setWhen(System.currentTimeMillis())
//                .setSmallIcon(R.drawable.notification_icon)
//                .setColor(context.getResources().getColor(R.color.colorTexNormal))
//                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
//                .setDefaults(Notification.DEFAULT_LIGHTS)
//                .setContentIntent(intent)
//                .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
//                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//                .setAutoCancel(true)
//                .setOngoing(false)
//                .build();
//        manager.notify(notificationId, notification);
//    }

}
