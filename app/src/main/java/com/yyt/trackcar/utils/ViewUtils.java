package com.yyt.trackcar.utils;

import android.text.InputFilter;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

/**
 * 项目名：   传信鸽
 * 包名：     com.lllcommon.utils
 * 文件名：   ViewUtils
 * 创建者：   QING
 * 创建时间： 2018/5/3 19:24
 * 描述：     TODO EditText工具类
 */

public class ViewUtils {

    /**
     * 设置editText最大长度
     * @param editText 文本编辑器
     * @param length 最大长度
     */
    public static void setEtCoustomLength(EditText editText, int length) {
        if (length > 0)
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)});
    }

    /**
     * 判断是否在控件区域内
     * @param view 控件
     * @param event 触摸结果
     * @return 返回结果
     */
    public static boolean inRangeOfView(View view, MotionEvent event){
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        return !(event.getX() < x) && !(event.getX() > (x + view.getWidth())) && !(event.getY() <
                y) && !(event.getY() > (y + view.getHeight()));
    }
}
