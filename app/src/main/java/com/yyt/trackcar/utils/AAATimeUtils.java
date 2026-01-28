package com.yyt.trackcar.utils;

import android.content.Context;
import android.text.TextUtils;

import com.yyt.trackcar.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AAATimeUtils {
    private static final long ONE_MINUTE = 60000L;
    private static final long ONE_HOUR = 3600000L;
    private static final long ONE_DAY = 86400000L;
    private static final long ONE_MOUTH = 2592000000L;
    private static SimpleDateFormat sdf = null;

    /**
     * 获取昨天的日期
     *
     * @param strPattern 格式
     * @return 结果
     */
    public static String getYesterday(String strPattern) {
        if (TextUtils.isEmpty(strPattern))
            strPattern = "yyyy-MM-dd HH:mm:ss";
        long aimTime = System.currentTimeMillis() - 24 * 3600 * 1000;
        return formatUTC(aimTime, strPattern);
    }

    /**
     * 把日期类型转换成字符串类型
     *
     * @param str 日期
     * @return 结果
     */
    public static String isTodayTime(String str) {
        Date date = formatUTC(str, "yyyy-MM-dd HH:mm:ss");
        Date toDate = new Date();
        if (null == date)
            return "";
        else {
            if (inSameDay(date, toDate))
                return str.substring(11, 16);
            else
                return str.substring(0, 10);
        }
    }

    /**
     * 判断是否同一天
     *
     * @param date1 日期1
     * @param date2 日期2
     * @return 结果
     */
    public static boolean inSameDay(Date date1, Date date2) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        int year1 = calendar.get(Calendar.YEAR);
        int month1 = calendar.get(Calendar.MONTH);
        int day1 = calendar.get(Calendar.DAY_OF_MONTH);

        calendar.setTime(date2);
        int year2 = calendar.get(Calendar.YEAR);
        int month2 = calendar.get(Calendar.MONTH);
        int day2 = calendar.get(Calendar.DAY_OF_MONTH);
        return year1 == year2 && month1 == month2 && day1 == day2;
    }

    /**
     * 判断是否同一时间（精确到分）
     *
     * @param date1 日期1
     * @param date2 日期2
     * @return 结果
     */
    public static boolean inSameTime(Date date1, Date date2) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        int hour1 = calendar.get(Calendar.HOUR_OF_DAY);
        int minute1 = calendar.get(Calendar.MINUTE);

        calendar.setTime(date2);
        int hour2 = calendar.get(Calendar.HOUR_OF_DAY);
        int minute2 = calendar.get(Calendar.MINUTE);
        return hour2 == hour1 && minute1 == minute2;
    }

    /**
     * 获取某年中某月的天数
     *
     * @param mouth 月份
     * @param year  年份
     * @return 结果
     */
    public static int getMouthDay(String mouth, String year) {
        switch (Integer.parseInt(mouth)) {
            case 2:
                if (366 == getYearDay(year))
                    return 29;
                else
                    return 28;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            default:
                return 31;
        }
    }

    /**
     * 获取某年的天数
     *
     * @param year 年份
     * @return 结果
     */
    private static int getYearDay(String year) {
        if ((Integer.parseInt(year) % 4 == 0
                && Integer.parseInt(year) % 100 != 0)
                || Integer.parseInt(year) % 400 == 0)
            return 366;
        else
            return 365;
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
            timeStr = context.getString(R.string.seconds, time % 60);
        time = time / 60;
        if (time % 60 > 0)
            timeStr = context.getString(R.string.track_minutes, time % 60)
                    + timeStr;
        time = time / 60;
        if (time > 0)
            timeStr = context.getString(R.string.hours, time)
                    + timeStr;
        if (timeStr.isEmpty())
            timeStr = context.getString(R.string.seconds, 0);
        return timeStr;
    }

    /**
     * 获取时长
     *
     * @param context 上下文
     * @param minutes 时间
     * @return 结果
     */
    public static String getMinutes(Context context, long minutes) {
        String timeStr = "";
        long time = minutes;
        if (time % 60 > 0)
            timeStr = context.getString(R.string.track_minutes, time % 60);
        time = time / 60;
        if (time > 0)
            timeStr = context.getString(R.string.hours, time % 60) + timeStr;
        if (timeStr.isEmpty())
            timeStr = context.getString(R.string.track_minutes, 0);
        return timeStr;
    }

    private static long toSeconds(long date) {
        return date / 1000L;
    }

    private static long toMinutes(long date) {
        return toSeconds(date) / 60L;
    }

    private static long toHours(long date) {
        return toMinutes(date) / 60L;
    }

    private static long toDays(long date) {
        return toHours(date) / 24L;
    }

    private static long toMonths(long date) {
        return toDays(date) / 30L;
    }

    private static long toYears(long date) {
        return toDays(date) / 365L;
    }

    /**
     * 显示时长
     *
     * @param times 时间
     */
    public static String showTimeTextForFloat(float times) {
        if (times < 60) {
            DecimalFormat decimalFormat = new DecimalFormat("00.00");// 构造方法的字符格式这里如果小数不足2位,会以0补足.
            String seconds = decimalFormat.format(times);// format
            return "00:" + seconds;
        } else {
            DecimalFormat decimalFormat = new DecimalFormat("00.00");// 构造方法的字符格式这里如果小数不足2位,会以0补足.
            String seconds = decimalFormat.format(times % 60);// format
            DecimalFormat decimalFormat2 = new DecimalFormat("00");
            String minutes = decimalFormat2.format(times / 60);// format
            return minutes + ":" + seconds;
        }
    }

    /**
     * 显示时长 时分秒
     *
     * @param times 时间
     */
    public static String showTimeTextForInt(int times) {
        if (times < 60) {
            DecimalFormat decimalFormat = new DecimalFormat("00");// 构造方法的字符格式这里如果小数不足2位,会以0补足.
            String seconds = decimalFormat.format(times);// format
            return "00:" + seconds;
        } else if (times < 60 * 60) {
            DecimalFormat decimalFormat = new DecimalFormat("00");
            String seconds = decimalFormat.format(times % 60);// format
            String minutes = decimalFormat.format(times / 60);// format
            return minutes + ":" + seconds;
        } else {
            DecimalFormat decimalFormat = new DecimalFormat("00");
            String seconds = decimalFormat.format(times % 60);// format
            String minutes = decimalFormat.format(times / 60 % 60);// format
            return times / 3600 + ":" + minutes + ":" + seconds;
        }

    }

    /**
     * 显示时长 时分秒
     *
     * @param str 时间
     */
    public static String showTimeTextForString(String str) {
        int times = 0;
        if (!TextUtils.isEmpty(str))
            times = Integer.parseInt(str);
        if (times < 60) {
            DecimalFormat decimalFormat = new DecimalFormat("00");// 构造方法的字符格式这里如果小数不足2位,会以0补足.
            String seconds = decimalFormat.format(times);// format
            return "00:" + seconds;
        } else if (times < 60 * 60) {
            DecimalFormat decimalFormat = new DecimalFormat("00");
            String seconds = decimalFormat.format(times % 60);// format
            String minutes = decimalFormat.format(times / 60);// format
            return minutes + ":" + seconds;
        } else {
            DecimalFormat decimalFormat = new DecimalFormat("00");
            String seconds = decimalFormat.format(times % 60);// format
            String minutes = decimalFormat.format(times / 60 % 60);// format
            return times / 3600 + ":" + minutes + ":" + seconds;
        }
    }

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
        if(TextUtils.isEmpty(dateStr))
            return null;
        if (TextUtils.isEmpty(strPattern)) {
            strPattern = "yyyy-MM-dd HH:mm:ss";
        }
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
     * 发布时间
     *
     * @param context 上下文
     * @param str     时间
     * @return 发布时间
     */
    public static String getPublishTime(Context context, String str) {
        if (!TextUtils.isEmpty(str)) {
            Date date = TimeUtils.formatUTC(str, null);
            if (null != date) {
                Date nowDate = new Date();
                if (date.getYear() == nowDate.getYear()) {
                    long time = date.getTime();
                    long nowTime = nowDate.getTime();
                    if (nowTime > time) {
                        if ((nowTime - time) / (24 * 60 * 60 * 1000) > 0)
                            return TimeUtils.formatUTC(time, "MM-dd HH:mm");
                        else if ((nowTime - time) / (60 * 60 * 1000) > 0)
                            return context.getString(R.string.hour_tips, (nowTime - time) / (60 *
                                    60 * 1000));
                        else if ((nowTime - time) / (60 * 1000) > 3)
                            return context.getString(R.string.minutes_tips, (nowTime - time) /
                                    (60 * 1000));
                        else
                            return context.getString(R.string.now_time);
                    }
                } else
                    return TimeUtils.formatUTC(date.getTime(), "yyyy-MM-dd HH:mm");
            }
        }
        return context.getString(R.string.now_time);
    }

    /**
     * 消息时间
     *
     * @param str 时间
     * @return 消息时间
     */
    public static String getMessageTime(String str) {
        if (!TextUtils.isEmpty(str)) {
            Date date = TimeUtils.formatUTC(str, null);
            if (null != date) {
                Date nowDate = new Date();
                if (date.getYear() == nowDate.getYear()) {
                    if (date.getMonth() == nowDate.getMonth() && date.getDate() == nowDate
                            .getDate())
                        TimeUtils.formatUTC(date.getTime(), "HH:mm");
                    else
                        return TimeUtils.formatUTC(date.getTime(), "MM/dd");
                } else
                    return TimeUtils.formatUTC(date.getTime(), "yyyy/MM/dd");
            }
        }
        return TimeUtils.formatUTC(System.currentTimeMillis(), "HH:mm");
    }

    /**
     * 报警时间
     *
     * @param str 时间
     * @return 报警时间
     */
    public static String getAlarmTime(String str) {
        if (!TextUtils.isEmpty(str)) {
            Date date = TimeUtils.formatUTC(str, null);
            if (null != date) {
                Date nowDate = new Date();
                if (date.getYear() == nowDate.getYear()) {
                    if (date.getMonth() == nowDate.getMonth() && date.getDate() == nowDate
                            .getDate())
                        TimeUtils.formatUTC(date.getTime(), "HH:mm");
                    else
                        return TimeUtils.formatUTC(date.getTime(), "MM/dd HH:mm");
                } else
                    return TimeUtils.formatUTC(date.getTime(), "yyyy/MM/dd HH:mm");
            }
        }
        return TimeUtils.formatUTC(System.currentTimeMillis(), "HH:mm");
    }

    /**
     * 显示语音时长
     *
     * @param str 时间
     */
    public static String getAudioTime(String str) {
        int second = 0;
        if (!TextUtils.isEmpty(str)) {
            try {
                second = Integer.parseInt(str);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        if (second < 60)
            return second + "\"";
            // else if (second % 60 == 0)
            // return second / 60 + "'";
        else if (second % 60 == 0)
            return second / 60 + "'";
        else
            return second / 60 + "'" + (second % 60) + "\"";
    }
}
