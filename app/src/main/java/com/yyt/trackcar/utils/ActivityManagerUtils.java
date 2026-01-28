package com.yyt.trackcar.utils;

import android.app.Activity;

import java.util.Stack;

public class ActivityManagerUtils {

    private static Stack<Activity> activityStack;
    private static ActivityManagerUtils mActivityManagerUtils = null;

    private ActivityManagerUtils() {
    }

    public static ActivityManagerUtils getInstance() {
        synchronized (ActivityManagerUtils.class) {
            if (mActivityManagerUtils == null) {
                mActivityManagerUtils = new ActivityManagerUtils();
                activityStack = new Stack<>();
            }
        }
        return mActivityManagerUtils;
    }

    // 退出栈顶Activity
    public void popActivity(Activity activity) {
        if (activity != null) {
            activity.finish();
            activityStack.remove(activity);
        }
    }

    // 获得当前栈顶Activity

    public Activity currentActivity() {
        Activity activity = null;
        try{
            activity = activityStack.lastElement();
        }catch (Exception e){
            e.printStackTrace();
        }
        return activity;
    }

    // 将当前Activity推入栈中

    public void pushActivity(Activity activity) {
        activityStack.add(activity);
    }

    public int getActivityStackSize() {
        return activityStack.size();
    }

    // 退出栈中所有Activity
    public void popAllActivity() {
        while (activityStack.size() > 0) {
            Activity activity = currentActivity();
            popActivity(activity);
        }
    }

    // 退出栈中所有Activity

    public void popActivityExceptOne() {
        while (activityStack.size() > 1) {
            Activity activity = currentActivity();
            popActivity(activity);
        }
    }
}
