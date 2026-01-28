package com.yyt.trackcar.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.yyt.trackcar.R;

/**
 * 项目名：   传信鸽
 * 包名：     com.llt.cargps.utils
 * 文件名：   ImageLoadUtils
 * 创建者：   QING
 * 创建时间： 2018/7/25 15:03
 * 描述：     TODO 图片加载工具类
 */
public class ImageLoadUtils {

    /**
     * 加载头像
     *
     * @param context   上下文
     * @param url       头像地址
     * @param imageView 加载控件
     */
    public static void loadPortraitImage(Context context, String url, ImageView imageView) {
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.mipmap.ic_default_pigeon_marker)
                .error(R.mipmap.ic_default_pigeon_marker)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .encodeQuality(90);
        Glide.with(context).load(url)
                .apply(requestOptions)
                .into(imageView);
    }

    /**
     * 加载图片
     *
     * @param context   上下文
     * @param url       图片地址
     * @param imageView 加载控件
     */
    public static void loadImage(Context context, String url, ImageView imageView) {
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.mipmap.xui_ic_default_img)
                .error(R.mipmap.xui_ic_default_img)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .encodeQuality(90);
        Glide.with(context).load(url)
                .apply(requestOptions)
                .into(imageView);
    }

    /**
     * @param context     上下文
     * @param url         图片地址
     * @param placeholder 缩略图
     * @param imageView   加载控件
     */
    public static void loadPortraitImage(Context context, String url, int placeholder,
                                         ImageView imageView) {
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(placeholder)
                .error(placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .encodeQuality(90);
        Glide.with(context).load(url)
                .apply(requestOptions)
                .into(imageView);
    }

}
