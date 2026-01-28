package com.yyt.trackcar.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 项目名：   传信鸽
 * 包名：     com.lllcommon.utils
 * 文件名：   AAAAppUtils
 * 创建者：   QING
 * 创建时间： 2018/4/21 16:15
 * 描述：     TODO 工具类
 */

public class AAAAppUtils {

    /**
     * 获取进程名
     */
    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context
                .ACTIVITY_SERVICE);
        if (mActivityManager != null) {
            for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                    .getRunningAppProcesses()) {
                if (appProcess.pid == pid)
                    return appProcess.processName;
            }
        }
        return "";
    }

//    /**
//     * 根据传入控件显示键盘
//     *
//     * @param view 控件view
//     */
//    public static void showKeyboard(View view) {
//        try {
//            if (view != null && view instanceof EditText) {
//                view.requestFocus();
//                InputMethodManager inputMethodManager = (InputMethodManager)
//                        view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                if (inputMethodManager != null)
//                    inputMethodManager.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 根据传入控件的坐标和用户的焦点坐标，判断是否隐藏键盘，如果点击的位置在控件内，则不隐藏键盘
//     *
//     * @param view  控件view
//     * @param event 焦点位置
//     */
//    public static void hideKeyboard(MotionEvent event, View view,
//                                    Activity activity) {
//        try {
//            if (view != null && view instanceof EditText) {
//                int[] location = {0, 0};
//                view.getLocationInWindow(location);
//                int left = location[0], top = location[1], right = left
//                        + view.getWidth(), bootom = top + view.getHeight();
//                // 判断焦点位置坐标是否在控件内，如果位置在控件外，则隐藏键盘
//                if (event == null || event.getRawX() < left || event.getRawX() > right
//                        || event.getY() < top || event.getRawY() > bootom) {
//                    // 隐藏键盘
//                    IBinder token = view.getWindowToken();
//                    InputMethodManager inputMethodManager = (InputMethodManager) activity
//                            .getSystemService(Context.INPUT_METHOD_SERVICE);
//                    if (inputMethodManager != null)
//                        inputMethodManager.hideSoftInputFromWindow(token,
//                                InputMethodManager.HIDE_NOT_ALWAYS);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    // 是否安装了该应用
    public static boolean isAvilible(Activity mContext,
                                     String packageName) {
        // Get PackageManager
        final PackageManager packageManager = mContext.getPackageManager();
        // Get The All Install App Package Name
        List<PackageInfo> pInfo = packageManager.getInstalledPackages(0);
        // Create Name List
        List<String> pName = new ArrayList<>();
        // Add Package Name into Name List
        if (pInfo != null) {
            for (int i = 0; i < pInfo.size(); i++) {
                String pn = pInfo.get(i).packageName;
                pName.add(pn);
            }
        }
        return pName.contains(packageName);
    }

    /**
     * 安装指定路径下的Apk
     * <p>根据路径名是否符合和文件是否存在判断是否安装成功
     * <p>更好的做法应该是startActivityForResult回调判断是否安装成功比较妥当
     * <p>这里做不了回调，后续自己做处理
     */
    public static boolean installApp(Context context, String filePath) {
        if (filePath != null && filePath.length() > 4
                && filePath.toLowerCase().substring(filePath.length() - 4).equals(".apk")) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            File file = new File(filePath);
            if (file.exists() && file.isFile() && file.length() > 0) {
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android" +
                        ".package-archive");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return true;
            }
        }
        return false;
    }

    /**
     * 卸载指定包名的App
     * <p>这里卸载成不成功只判断了packageName是否为空
     * <p>如果要根据是否卸载成功应该用startActivityForResult回调判断是否还存在比较妥当
     * <p>这里做不了回调，后续自己做处理
     */
    public boolean uninstallApp(Context context, String packageName) {
        if (!TextUtils.isEmpty(packageName)) {
            Intent intent = new Intent(Intent.ACTION_DELETE);
            intent.setData(Uri.parse("package:" + packageName));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        }
        return false;
    }

    /**
     * 获取当前App信息
     * <p>AppInfo（名称，图标，包名，版本号，版本Code，是否安装在SD卡，是否是用户程序）
     */
    public static AppInfo getAppInfo(Context context) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(context.getApplicationContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pi != null ? getBean(pm, pi) : null;
    }

    /**
     * 得到AppInfo的Bean
     */
    private static AppInfo getBean(PackageManager pm, PackageInfo pi) {
        ApplicationInfo ai = pi.applicationInfo;
        String name = ai.loadLabel(pm).toString();
        Drawable icon = ai.loadIcon(pm);
        String packageName = pi.packageName;
        String versionName = pi.versionName;
        int versionCode = pi.versionCode;
        boolean isSD = (ApplicationInfo.FLAG_SYSTEM & ai.flags) != ApplicationInfo.FLAG_SYSTEM;
        boolean isUser = (ApplicationInfo.FLAG_SYSTEM & ai.flags) != ApplicationInfo.FLAG_SYSTEM;
        return new AppInfo(name, icon, packageName, versionName, versionCode, isSD, isUser);
    }

    /**
     * 获取所有已安装App信息
     * <p>AppInfo（名称，图标，包名，版本号，版本Code，是否安装在SD卡，是否是用户程序）
     * <p>依赖上面的getBean方法
     */
    public static List<AppInfo> getAllAppsInfo(Context context) {
        List<AppInfo> list = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        // 获取系统中安装的所有软件信息
        List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
        for (PackageInfo pi : installedPackages) {
            if (pi != null) {
                list.add(getBean(pm, pi));
            }
        }
        return list;
    }

    /**
     * 打开指定包名的App
     */
    public static boolean openAppByPackageName(Context context, String packageName) {
        if (!TextUtils.isEmpty(packageName)) {
            PackageManager pm = context.getPackageManager();
            Intent launchIntentForPackage = pm.getLaunchIntentForPackage(packageName);
            if (launchIntentForPackage != null) {
                context.startActivity(launchIntentForPackage);
                return true;
            }
        }
        return false;
    }

    /**
     * 判断某个service是否正在运行
     *
     * @param context
     * @param runService
     *            要验证的service组件的类名
     * @return
     */
    public static boolean isServiceRunning(Context context,
                                           Class<? extends Service> runService) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) am
                .getRunningServices(1024);
        for (int i = 0; i < runningService.size(); ++i) {
            if (runService.getName().equals(
                    runningService.get(i).service.getClassName().toString())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 返回app运行状态
     *
     * @param context
     *            一个context
     * @param packageName
     *            要判断应用的包名
     * @return int 1:前台 2:后台 0:不存在
     */
    public static int isAppAlive(Context context, String packageName) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> listInfos = activityManager
                .getRunningTasks(20);
        // 判断程序是否在栈顶
        if (listInfos.get(0).topActivity.getPackageName().equals(packageName)) {
            return 1;
        } else {
            // 判断程序是否在栈里
            for (ActivityManager.RunningTaskInfo info : listInfos) {
                if (info.topActivity.getPackageName().equals(packageName)) {
                    return 2;
                }
            }
            return 0;// 栈里找不到，返回3
        }
    }

    /**
     * 判断进程是否运行
     *
     * @param context
     * @param proessName
     * @return
     */
    public static boolean isProessRunning(Context context, String proessName) {
        boolean isRunning = false;
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> lists = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : lists) {
            if (info.processName.equals(proessName)) {
                isRunning = true;
            }
        }
        return isRunning;
    }

    /**
     * 判断某一个类是否存在任务栈里面
     *
     * @return
     */
    public static boolean isExsitMianActivity(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        ComponentName cmpName = intent.resolveActivity(context
                .getPackageManager());
        boolean flag = false;
        if (cmpName != null) { // 说明系统中存在这个activity
            ActivityManager am = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> taskInfoList = am.getRunningTasks(10);
            for (ActivityManager.RunningTaskInfo taskInfo : taskInfoList) {
                if (taskInfo.baseActivity.equals(cmpName)) { // 说明它已经启动了
                    flag = true;
                    break; // 跳出循环，优化效率
                }
            }
        }
        return flag;
    }

    /**
     * 打开指定包名的App应用信息界面
     */
    public static boolean openAppInfo(Context context, String packageName) {
        if (!TextUtils.isEmpty(packageName)) {
            Intent intent = new Intent();
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.parse("package:" + packageName));
            context.startActivity(intent);
            return true;
        }
        return false;
    }

    /**
     * 可用来做App信息分享
     */
    public static void shareAppInfo(Context context, String info) {
        if (!TextUtils.isEmpty(info)) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, info);
            context.startActivity(intent);
        }
    }

    /**
     * 判断当前App处于前台还是后台
     * <p>需添加<uses-permission android:name="android.permission.GET_TASKS"/>
     * <p>并且必须是系统应用该方法才有效
     */
    public static boolean isAppBackground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        @SuppressWarnings("deprecation")
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 封装App信息的Bean类
     */
    public static class AppInfo {
        private String name;
        private Drawable icon;
        private String packagName;
        private String versionName;
        private int versionCode;
        private boolean isSD;
        private boolean isUser;

        public Drawable getIcon() {
            return icon;
        }

        public void setIcon(Drawable icon) {
            this.icon = icon;
        }

        public boolean isSD() {
            return isSD;
        }

        public void setSD(boolean SD) {
            isSD = SD;
        }

        public boolean isUser() {
            return isUser;
        }

        public void setUser(boolean user) {
            isUser = user;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPackagName() {
            return packagName;
        }

        public void setPackagName(String packagName) {
            this.packagName = packagName;
        }

        public int getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(int versionCode) {
            this.versionCode = versionCode;
        }

        public String getVersionName() {
            return versionName;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }

        /**
         * @param name        名称
         * @param icon        图标
         * @param packagName  包名
         * @param versionName 版本号
         * @param versionCode 版本Code
         * @param isSD        是否安装在SD卡
         * @param isUser      是否是用户程序
         */
        public AppInfo(String name, Drawable icon, String packagName,
                       String versionName, int versionCode, boolean isSD, boolean isUser) {
            this.setName(name);
            this.setIcon(icon);
            this.setPackagName(packagName);
            this.setVersionName(versionName);
            this.setVersionCode(versionCode);
            this.setSD(isSD);
            this.setUser(isUser);
        }

    /*@Override
    public String toString() {
        return getName() + "\n"
                + getIcon() + "\n"
                + getPackagName() + "\n"
                + getVersionName() + "\n"
                + getVersionCode() + "\n"
                + isSD() + "\n"
                + isUser() + "\n";
    }*/
    }
}


