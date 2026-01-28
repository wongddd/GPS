package com.yyt.trackcar.utils;

import android.content.Context;
import android.text.TextUtils;

import com.yyt.trackcar.R;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AAAStringUtils {

    /**
     * 验证手机号码
     *
     * @param str 手机号
     * @return 结果
     */
    public static boolean checkMobile(String str) {
        Pattern p = Pattern.compile(
                "^13[0-9]{9}|15[012356789][0-9]{8}|18[0-9]{9}|14[579][0-9]{8}|17[0-9]{9}$");
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 验证台湾手机号码
     */
    public static boolean checkMobiletaiwan(String str) {
        Pattern p = Pattern.compile("^09[1-9]{8}|9[0-9]{8}$");
        // Pattern p = Pattern
        // .compile("^([-_－—\\s\\(]?)([\\(]?)((((0?)|((00)?))(((\\s){0,2})|([-_－—\\s]?)))|(([\\)
        // ]?)[+]?))(886)?([\\)
        // ]?)([-_－—\\s]?)([\\(]?)[0]?[1-9]{1}([-_－—\\s\\)]?)
        // [1-9]{2}[-_－—]?[0-9]{3}[-_－—]?[0-9]{3}$");
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 验证密码
     *
     * @param str 密码
     * @return 结果
     */
    public static boolean checkPassword(String str) {
        Pattern p = Pattern.compile("^\\S{3,15}$");
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 半角转全角
     *
     * @param input String.
     * @return 全角字符串.
     */
    public static String ToSBC(String input) {
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == ' ') {
                c[i] = '\u3000';
            } else if (c[i] < '\177') {
                c[i] = (char) (c[i] + 65248);

            }
        }
        return new String(c);
    }

    // 十六进制转二进制
    public static String HToB(String a) {
        return Integer.toBinaryString(Integer.valueOf(toD(a, 16)));
    }

    // 二进制转十六进制
    public static String BToH(String a) {
        // 将二进制转为十进制再从十进制转为十六进制
        return Integer.toHexString(Integer.valueOf(toD(a, 2)));
    }

    // 任意进制数转为十进制数
    public static String toD(String a, int b) {
        int r = 0;
        for (int i = 0; i < a.length(); i++) {
            r = (int) (r + formatting(a.substring(i, i + 1))
                    * Math.pow(b, a.length() - i - 1));
        }
        return String.valueOf(r);
    }

    // 将十六进制中的字母转为对应的数字
    public static int formatting(String a) {
        int i = 0;
        for (int u = 0; u < 10; u++) {
            if (a.equals(String.valueOf(u))) {
                i = u;
            }
        }
        if (a.equals("a")) {
            i = 10;
        }
        if (a.equals("b")) {
            i = 11;
        }
        if (a.equals("c")) {
            i = 12;
        }
        if (a.equals("d")) {
            i = 13;
        }
        if (a.equals("e")) {
            i = 14;
        }
        if (a.equals("f")) {
            i = 15;
        }
        return i;
    }

    // 将十进制中的数字转为十六进制对应的字母
    public static String formattingH(int a) {
        String i = String.valueOf(a);
        switch (a) {
            case 10:
                i = "a";
                break;
            case 11:
                i = "b";
                break;
            case 12:
                i = "c";
                break;
            case 13:
                i = "d";
                break;
            case 14:
                i = "e";
                break;
            case 15:
                i = "f";
                break;
            default:
                break;
        }
        return i;
    }

    /**
     * Convert byte[] to hex
     * string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
     * <p>
     * byte[] data
     *
     * @return hex string
     */
    public static String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (byte b : bArray) {
            sTemp = Integer.toHexString(0xFF & b);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();

    }

    /**
     * 计算数量
     *
     * @param num 数量
     * @return 结果
     */
    public static String sumCount(long num) {
        return num + "";
//        if(num / 10000 > 0) {
//            DecimalFormat decimalFormat = new DecimalFormat("#.#w");
//            return decimalFormat.format(num / 10000.0f);
//        }else{
//            return num + "";
//        }
    }

    /**
     * 计算数量
     *
     * @param numStr 数量字符串
     * @return 结果
     */
    public static String sumCount(String numStr) {
        if (TextUtils.isEmpty(numStr))
            return "0";
        else
            return numStr;
//        try {
//            int num = Integer.parseInt(numStr);
//            if (num / 10000 > 0) {
//                DecimalFormat decimalFormat = new DecimalFormat("#.#w");
//                return decimalFormat.format(num / 10000.0f);
//            } else {
//                return numStr;
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return "0";
    }

    /**
     * 获取距离
     *
     * @param distance 距离
     * @return 结果
     */
    public static String getDistance(long distance) {
        if (distance / 100 > 31) {
            DecimalFormat decimalFormat = new DecimalFormat("#.#km");
            return decimalFormat.format(distance / 1000.0f);
        } else
            return "< 3km";
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

    /**
     * 获取速度
     *
     * @param speed 速度
     * @return 结果
     */
    public static String getSpeed(Float speed) {
        if(speed == null)
            return "0.00km/h";
        DecimalFormat decimalFormat = new DecimalFormat("0.00km/h");
        return decimalFormat.format(speed);
    }

    /**
     * 判断是否有足够点数
     *
     * @param point 点数
     * @param count 额度
     * @return 结果
     */
    public static boolean isHasEnoughPoint(String point, int count) {
        long num = 0;
        if (!TextUtils.isEmpty(point))
            num = Long.parseLong(point);
        return num >= count;
    }

    /**
     * 获取用户VIP等级
     *
     * @param lever 等级
     * @return 结果
     */
    public static int getUserVipLever(String lever) {
        if (TextUtils.isEmpty(lever))
            return 0;
        else
            return Integer.parseInt(lever);
    }

    /**
     * 获取匹配条件距离
     *
     * @param distance 距离
     * @return 距离
     */
    public static int getCallRandomConditionDistance(int distance) {
        if (distance <= 10)
            return distance;
        else if (distance < 20)
            return distance % 10 * 10;
        else if (distance == 20)
            return 100;
        else if (distance < 30)
            return distance % 10 * 100;
        else if (distance == 30)
            return 1000;
        else if (distance < 40)
            return distance % 10 * 1000;
        else
            return 10000;
    }

    /**
     * 将每三个数字加上逗号处理（通常使用金额方面的编辑）
     *
     * @param str 需要处理的字符串
     * @return 处理完之后的字符串
     */
    public static String addComma(String str) {
        DecimalFormat decimalFormat = new DecimalFormat(",###");
        return decimalFormat.format(Double.parseDouble(str));
    }

    /**
     * 方向描述
     *
     * @param str 角度
     * @return 方向
     */
    public static String directionDescription(Context context, Float direction) {
        if(direction == null)
            return context.getString(R.string.north);
        else if(direction >= 22.5 && direction < 67.5)
            return context.getString(R.string.northeast);
        else if(direction >= 67.5 && direction < 112.5)
            return context.getString(R.string.east);
        else if(direction >= 112.5 && direction < 157.5)
            return context.getString(R.string.southeast);
        else if(direction >= 157.5 && direction < 202.5)
            return context.getString(R.string.south);
        else if(direction >= 202.5 && direction < 247.5)
            return context.getString(R.string.southwest);
        else if(direction >= 247.5 && direction < 292.5)
            return context.getString(R.string.west);
        else if(direction >= 292.5 && direction < 337.5)
            return context.getString(R.string.northwest);
        else
            return context.getString(R.string.north);
    }
}
