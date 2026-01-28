package com.yyt.trackcar.utils;

import android.content.Context;
import android.text.TextUtils;

import com.yyt.trackcar.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.utils
 * @ fileName:      TimeUtils
 * @ author:        QING
 * @ createTime:    2020/3/24 17:57
 * @ describe:      TODO 时间工具类
 */
public class TimeUtils {
    public static final int YEAR = 365 * 24 * 60 * 60;// 年
    public static final int MONTH = 30 * 24 * 60 * 60;// 月
    public static final int DAY = 24 * 60 * 60;// 天
    public static final int HOUR = 60 * 60;// 小时
    public static final int MINUTE = 60;// 分钟

//    private static SimpleDateFormat sdf = null;

    /**
     * 转换时间
     *
     * @param l          // 时间戳
     * @param strPattern // 格式
     * @return 结果
     */
    public static String formatUTC(long l, String strPattern) {
        if (TextUtils.isEmpty(strPattern)) {
            strPattern = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = null;
        if (sdf == null) {
            try {
                sdf = new SimpleDateFormat(strPattern, Locale.CHINA);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else {
            sdf.applyPattern(strPattern);
        }
        return sdf == null ? "" : sdf.format(l);
    }

    /**
     * 转换时间
     *
     * @param dateStr    // 时间字符串
     * @param strPattern // 格式
     * @return 结果
     */
    public static Date formatUTC(String dateStr, String strPattern) {
        if (TextUtils.isEmpty(strPattern)) {
            strPattern = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = null;
        if (sdf == null) {
            try {
                sdf = new SimpleDateFormat(strPattern, Locale.CHINA);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else {
            sdf.applyPattern(strPattern);
        }
        Date date = null;
        if (sdf != null) {
            try {
                date = sdf.parse(dateStr);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return date;
    }

    /**
     * 转换时间
     *
     * @param l          // 时间戳
     * @param strPattern // 格式
     * @return 结果
     */
    public static String formatUTCC(long l, String strPattern) {
        if (TextUtils.isEmpty(strPattern)) {
            strPattern = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = null;
        if (sdf == null) {
            try {
                sdf = new SimpleDateFormat(strPattern, Locale.CHINA);
                sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else {
            sdf.applyPattern(strPattern);
        }
        return sdf == null ? "" : sdf.format(l);
    }

    /**
     * 转换时间
     *
     * @param dateStr    // 时间字符串
     * @param strPattern // 格式
     * @return 结果
     */
    public static Date formatUTCC(String dateStr, String strPattern) {
        if (TextUtils.isEmpty(strPattern)) {
            strPattern = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = null;
        if (sdf == null) {
            try {
                sdf = new SimpleDateFormat(strPattern, Locale.CHINA);
                sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else {
            sdf.applyPattern(strPattern);
        }
        Date date = null;
        if (sdf != null) {
            try {
                date = sdf.parse(dateStr);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return date;
    }

    /**
     * 根据时间戳获取模糊型的时间描述。
     *
     * @param timestamp 时间戳 单位为毫秒
     * @return 模糊型的与当前时间的差
     * <ul>
     * <li>如果在 1 分钟内或者时间是未来的时间，显示刚刚</li>
     * <li>如果在 1 小时内，显示 XXX分钟前</li>
     * <li>如果在 1 天内，显示XXX小时前</li>
     * <li>如果在 1 月内，显示XXX天前</li>
     * <li>如果在 1 年内，显示XXX月前</li>
     * <li>如果在 1 年外，显示XXX年前</li>
     * </ul>
     */
//    public static String getFuzzyTimeDescriptionByNow(long timestamp) {
//        long currentTime = System.currentTimeMillis();
//        long timeGap = (currentTime - timestamp) / 1000;// 与现在时间相差秒数
//        String timeStr;
//        long span;
//        if ((span = Math.round((float) timeGap / YEAR)) > 0) {
//            timeStr = span + "年前";
//        } else if ((span = Math.round((float) timeGap / MONTH)) > 0) {
//            timeStr = span + "个月前";
//        } else if ((span = Math.round((float) timeGap / DAY)) > 0) {// 1天以上
//            timeStr = span + "天前";
//        } else if ((span = Math.round((float) timeGap / HOUR)) > 0) {// 1小时-24小时
//            timeStr = span + "小时前";
//        } else if ((span = Math.round((float) timeGap / MINUTE)) > 0) {// 1分钟-59分钟
//            timeStr = span + "分钟前";
//        } else {// 1秒钟-59秒钟
//            timeStr = "刚刚";
//        }
//        return timeStr;
//    }

    /**
     * 获取日期描述
     *
     * @param context   上下文
     * @param timestamp 时间
     * @return 返回描述
     */
    public static String getDateDescriptionByNow(Context context, String timestamp) {
        if (!TextUtils.isEmpty(timestamp)) {
            Date date = formatUTC(timestamp, "yyyy-MM-dd");
            if (date != null) {
                Date nowDate = new Date();
                long wee = getWeeOfToday();
                long millis = date.getTime();
                if (millis >= wee) {
                    return context.getString(R.string.today);
                } else if (millis >= wee - DAY * 1000) {
                    return context.getString(R.string.yestoday);
                } else if (millis >= wee - DAY * 1000 * 2) {
                    return context.getString(R.string.before_yestoday);
                } else if (nowDate.getYear() == date.getYear()) {
                    return formatUTC(date.getTime(), "MM/dd");
                } else {
                    return formatUTC(date.getTime(), "yyyy/MM/dd");
                }
            }
        }
        return context.getString(R.string.unknow);
    }

    /**
     * 获取时间描述
     *
     * @param context   上下文
     * @param timestamp 时间
     * @return 返回描述
     */
    public static String getDateDescriptionByNow(Context context, long timestamp) {
        long currentTime = System.currentTimeMillis();
        Date date = formatUTC(formatUTCC(timestamp, null), null);
        long timeGap = (currentTime - date.getTime()) / 1000;// 与现在时间相差秒数
        String timeStr;
        long span;
        if ((span = timeGap / YEAR) > 0) {
            timeStr = context.getString(R.string.year_ago, span);
        } else if ((span = timeGap / MONTH) > 0) {
            timeStr = context.getString(R.string.month_ago, span);
        } else if ((span = timeGap / DAY) > 0) {// 1天以上
            timeStr = context.getString(R.string.day_ago, span);
        } else if ((span = timeGap / HOUR) > 0) {// 1小时-24小时
            timeStr = context.getString(R.string.hour_ago, span);
        } else if ((span = timeGap / MINUTE) > 0) {// 1分钟-59分钟
            timeStr = context.getString(R.string.minutes_ago, span);
        } else {// 1秒钟-59秒钟
            timeStr = context.getString(R.string.now);
        }
        return timeStr;
    }

    /**
     * 获取短信日期描述
     *
     * @param context   上下文
     * @param timestamp 时间
     * @return 返回描述
     */
    public static String getSmsDateDescriptionByNow(Context context, String timestamp) {
        if (!TextUtils.isEmpty(timestamp)) {
            Date date = formatUTC(timestamp, "yyyy/MM/dd HH:mm:ss");
            if (date != null) {
                Date nowDate = new Date();
                long wee = getWeeOfToday();
                long millis = date.getTime();
                if (millis >= wee) {
                    long timeGap = (nowDate.getTime() - millis) / 1000;// 与现在时间相差秒数
                    long span;
                    if ((span = timeGap / HOUR) > 0) {// 1小时-24小时
//                        return context.getString(R.string.hour_ago, span);
                        return formatUTC(date.getTime(), "HH:mm");
                    } else if ((span = timeGap / MINUTE) > 0) {// 1分钟-59分钟
                        return context.getString(R.string.minutes_ago, span);
                    } else {// 1秒钟-59秒钟
                        return context.getString(R.string.now);
                    }
                } else if (millis >= wee - DAY * 1000) {
                    return context.getString(R.string.yestoday);
                } else if (millis >= wee - DAY * 1000 * 2) {
                    return context.getString(R.string.before_yestoday);
                } else if (nowDate.getYear() == date.getYear()) {
                    return formatUTC(date.getTime(), "MM/dd HH:mm");
                } else {
                    return formatUTC(date.getTime(), "yyyy/MM/dd HH:mm");
                }
            }
        }
        return context.getString(R.string.unknow);
    }

    /**
     * 获取通话记录日期描述
     *
     * @param context   上下文
     * @param timestamp 时间
     * @return 返回描述
     */
    public static String getCallDateDescriptionByNow(Context context, String timestamp) {
        if (!TextUtils.isEmpty(timestamp)) {
            Date date = formatUTCC(timestamp, "yyyy/MM/dd HH:mm:ss");
            if (date != null) {
                Date nowDate = new Date();
                long wee = getWeeOfToday();
                long millis = date.getTime();
                if (millis >= wee) {
                    long timeGap = (nowDate.getTime() - millis) / 1000;// 与现在时间相差秒数
                    long span;
                    if (timeGap / HOUR > 0) {// 1小时-24小时
                        return formatUTCC(date.getTime(), "HH:mm");
                    } else if ((span = timeGap / MINUTE) > 0) {// 1分钟-59分钟
                        return context.getString(R.string.minutes_ago, span);
                    } else {// 1秒钟-59秒钟
                        return context.getString(R.string.now);
                    }
                } else if (millis >= wee - DAY * 1000) {
                    return context.getString(R.string.yestoday);
                } else if (millis >= wee - DAY * 1000 * 2) {
                    return context.getString(R.string.before_yestoday);
                } else if (nowDate.getYear() == date.getYear()) {
                    return formatUTCC(date.getTime(), "MM/dd\nHH:mm");
                } else {
                    return formatUTCC(date.getTime(), "yyyy/MM/dd\nHH:mm");
                }
            }
        }
        return context.getString(R.string.unknow);
    }

    /**
     * 获取聊天记录时间描述
     *
     * @param context   上下文
     * @param timestamp 时间
     * @return 返回描述
     */
    public static String getChatDateDescriptionByNow(Context context, String timestamp) {
        if (!TextUtils.isEmpty(timestamp)) {
            Date date = formatUTC(timestamp, "yyyy/MM/dd HH:mm:ss");
            if (date != null) {
                Date nowDate = new Date();
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.MILLISECOND, 0);
                long wee = cal.getTimeInMillis();
                long millis = date.getTime();
                int week = cal.get(Calendar.DAY_OF_WEEK) - 2;
                if (week == -1)
                    week = 6;
                if (millis >= wee) {
                    return formatUTC(date.getTime(), "HH:mm");
                } else if (millis >= wee - DAY * 1000) {
                    return context.getString(R.string.yestoday);
                } else if (millis >= wee - DAY * 1000 * 2) {
                    return context.getString(R.string.before_yestoday);
                } else if (week >= 3 && millis >= wee - DAY * 1000 * week) {
                    return getWeek(context, week - 3);
                } else if (nowDate.getYear() == date.getYear()) {
                    return formatUTC(date.getTime(), "MM/dd");
                } else {
                    return formatUTC(date.getTime(), "yyyy/MM/dd");
                }
            }
        }
        return context.getString(R.string.unknow);
    }

    /**
     * 获取星期描述
     *
     * @param context 上下文
     * @param week    星期
     * @return 返回描述
     */
    public static String getWeekDescription(Context context, String week) {
        if ("0000000".equals(week))
            return context.getString(R.string.not_set);
        else if ("1111111".equals(week))
            return context.getString(R.string.everyday);
        else if (week.length() == 7) {
            String weekString = String.format("%s%s", week.substring(1), week.substring(0, 1));
            String description = "";
            int start = -1;
            int end = -1;
            for (int i = 0; i <= 7; i++) {
                if (i != 7 && "1".equals(weekString.substring(i, i + 1))) {
                    if (start == -1) {
                        start = i;
                        end = i;
                    } else
                        end = i;
                } else if (start != -1) {
                    if (end - start == 0)
                        description = String.format("%s,%s", description, getWeek(context, start));
                    else if (end - start == 1)
                        description = String.format("%s,%s,%s", description, getWeek(context,
                                start), getWeek(context, end));
                    else
                        description = String.format("%s,%s", description,
                                context.getString(R.string.repeat_to,
                                        getWeek(context, start), getWeek(context, end)));
                    start = -1;
                    end = -1;
                }
            }
            if (TextUtils.isEmpty(description))
                return context.getString(R.string.not_set);
            else
                return description.substring(1);
        }
        return context.getString(R.string.everyday);
    }

    /**
     * 获取星期描述
     *
     * @param context 上下文
     * @param week    星期
     * @return 返回描述
     */
    public static String getWeek(Context context, int week) {
        switch (week) {
            case 0:
                return context.getString(R.string.monday);
            case 1:
                return context.getString(R.string.tuesday);
            case 2:
                return context.getString(R.string.wednesday);
            case 3:
                return context.getString(R.string.thursday);
            case 4:
                return context.getString(R.string.friday);
            case 5:
                return context.getString(R.string.saturday);
            case 6:
                return context.getString(R.string.sunday);
            default:
                return "";
        }
    }

    private static long getWeeOfToday() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    //判断闰年
    private static boolean isLeap(int year)
    {
        if (((year % 100 == 0) && year % 400 == 0) || ((year % 100 != 0) && year % 4 == 0))
            return true;
        else
            return false;
    }

    //返回当月天数
    public static int getDays(int year, int month) {
        int days;
        int FebDay = 28;
        if (isLeap(year))
            FebDay = 29;
        switch (month)
        {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                days = 31;
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                days = 30;
                break;
            case 2:
                days = FebDay;
                break;
            default:
                days = 0;
                break;
        }
        return days;
    }

    /**
     * 获取时长
     *
     * @param second 时间
     */
    public static String getSecond(Context context, String second) {
        String timeStr = "";
        long time;
        if (TextUtils.isEmpty(second))
            time = 0;
        else
            time = Long.parseLong(second);
        if (time % 60 > 0)
            timeStr = context.getString(R.string.second, time % 60);
        time = time / 60;
        if (time % 60 > 0)
            timeStr = String.format("%s%s", context.getString(R.string.minute, time % 60), timeStr);
        time = time / 60;
        if (time > 0)
            timeStr = String.format("%s%s", context.getString(R.string.hour, time), timeStr);
        if (timeStr.isEmpty())
            timeStr = context.getString(R.string.second, 0);
        return timeStr;
    }

}
