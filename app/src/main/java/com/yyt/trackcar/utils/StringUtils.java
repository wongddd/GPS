package com.yyt.trackcar.utils;

import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Random;


/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.utils
 * @ fileName:      AAAStringUtils
 * @ author:        QING
 * @ createTime:    2020/4/14 17:20
 * @ describe:      TODO 字符串工具类
 */
public class StringUtils {

    /*获取一条随机字符串*/
    public static String getRandomString(int length) { //length表示生成字符串的长度
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 设置文本
     *
     * @param textView 文本控件
     * @param text     文本
     */
    public static void setText(TextView textView, String text) {
        if (TextUtils.isEmpty(text)) {
            textView.setText(" ");
        } else {
            textView.setText(text);
        }
    }

    /**
     * 设置文本
     *
     * @param textView      文本控件
     * @param text          文本
     * @param defaultString 默认文本
     */
    public static void setText(TextView textView, String text, String defaultString) {
        if (TextUtils.isEmpty(text)) {
            textView.setText(defaultString);
        } else {
            textView.setText(text);
        }
    }

    /**
     * 设置文本
     *
     * @param textView      文本控件
     * @param text          文本
     * @param defaultString 默认文本
     */
    public static void setText(TextView textView, String text, int defaultString) {
        if (TextUtils.isEmpty(text)) {
            textView.setText(defaultString);
        } else {
            textView.setText(text);
        }
    }

    /**
     * 获取文本
     *
     * @param editText 文本输入控件
     * @return 控件内容
     */
    public static String getText(EditText editText) {
        String text;
        if (editText.getText() == null) {
            text = "";
        } else {
            text = editText.getText().toString();
        }
        return text;
    }

    /**
     * 获取非空文本
     *
     * @param  text 文本
     * @return 文本内容
     */
    public static String getNotNullText(String text) {
        return text == null ? "" : text;
    }

}
