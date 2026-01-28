package com.yyt.trackcar.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import java.util.Locale;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.utils
 * @ fileName:      LanguageUtils
 * @ author:        QING
 * @ createTime:    2020/6/10 18:24
 * @ describe:      TODO 语言工具类
 */
public class LanguageUtils {

//    public static void changeAppLanguage(Context context, int type) {
//        Locale mLocale = Locale.getDefault();
//        switch (type) {
//            case -1://跟随系统
//                mLocale = Locale.getDefault();
//                break;
//            case 0: // 简体中文
//                mLocale = Locale.SIMPLIFIED_CHINESE;
//                break;
////                case "zh_TW": //自定义语言，参数1为语种代码，参数2为地区代码
////                    myLocale = new Locale("zh", "TW");
////                    currentPosition = 2;
////                    break;
//            case 1: // 英语
//                mLocale = Locale.ENGLISH;
//                break;
////                case "zh_HK":
////                    myLocale = new Locale("zh", "HK");
////                    currentPosition = 4;
////                    break;
//            default:
//                break;
//        }
//        Resources res = context.getResources();
//        DisplayMetrics dm = res.getDisplayMetrics();
//        Configuration conf = res.getConfiguration();
//        conf.locale = mLocale;
//        //更新配置
//        res.updateConfiguration(conf, dm);
//    }
//
//    public static Configuration changeAppActivityLanguage(Context context, int type) {
//        Locale mLocale = Locale.getDefault();
//        switch (type) {
//            case -1://跟随系统
//                mLocale = Locale.getDefault();
//                break;
//            case 0: // 简体中文
//                mLocale = Locale.SIMPLIFIED_CHINESE;
//                break;
////                case "zh_TW": //自定义语言，参数1为语种代码，参数2为地区代码
////                    myLocale = new Locale("zh", "TW");
////                    currentPosition = 2;
////                    break;
//            case 1: // 英语
//                mLocale = Locale.ENGLISH;
//                break;
////                case "zh_HK":
////                    myLocale = new Locale("zh", "HK");
////                    currentPosition = 4;
////                    break;
//            default:
//                break;
//        }
//        Resources res = context.getResources();
//        DisplayMetrics dm = res.getDisplayMetrics();
//        Configuration conf = res.getConfiguration();
//        conf.locale = mLocale;
//        return conf;
//    }

    public static Context attachBaseContext(Context context, String language) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context, language);
        } else {
            return context;
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResources(Context context, String language) {
        if (TextUtils.isEmpty(language)){
            if("zh".equals(Locale.getDefault().getLanguage())){
                if ("CN".equals(Locale.getDefault().getCountry()))
                    language = "zh";
                else
                    language = "tw";
            }else
                language = Locale.getDefault().getLanguage();
        }
        Resources resources = context.getResources();
        Locale locale = getLocaleByLanguage(language);
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        configuration.setLocales(new LocaleList(locale));
        return context.createConfigurationContext(configuration);
    }

    /**
     * @param context
     * @param newLanguage 想要切换的语言类型 比如 "en" ,"zh"
     */
    @SuppressWarnings("deprecation")
    public static void changeAppLanguage(Context context, String newLanguage) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        //获取想要切换的语言类型
        Locale locale = getLocaleByLanguage(newLanguage);
        configuration.setLocale(locale);
        // updateConfiguration
        DisplayMetrics dm = resources.getDisplayMetrics();
        resources.updateConfiguration(configuration, dm);
    }

    private static Locale getLocaleByLanguage(String language) {
        if ("zh".equals(language))
            return Locale.SIMPLIFIED_CHINESE;
        else if ("tw".equals(language))
            return Locale.TRADITIONAL_CHINESE;
        else if ("en".equals(language))
            return Locale.ENGLISH;
        else
            return Locale.ENGLISH;
    }
}
