package com.yyt.trackcar.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.socks.library.KLog;
import com.xuexiang.xutil.app.ActivityUtils;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.MainApplication;
import com.yyt.trackcar.bean.PostMessage;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.dbflow.DeviceInfoModel;
import com.yyt.trackcar.ui.activity.LoginActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.Random;


/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.utils
 * @ fileName:      CWRequestUtils
 * @ author:        QING
 * @ createTime:    2020-02-14 16:20
 * @ describe:      TODO 服务器请求工具类
 */
public class CWRequestUtils {

    private static CWRequestUtils mInstance;

    /**
     * 单例模式
     */
    public static CWRequestUtils getInstance() {
        synchronized (CWRequestUtils.class) {
            if (mInstance == null) {
                mInstance = new CWRequestUtils();
            }
        }
        return mInstance;
    }

    /*
     * Request POST
     */

    /**
     * 登录
     *
     * @param context     上下文
     * @param countryCode 国家编码
     * @param username    用户名
     * @param pwd         密码
     * @param type        类型 1.邮箱 2手机
     * @param handler     回调
     */
    public void login(Context context, String countryCode, String username, String pwd, String type,
                      String languageType, Handler handler) {
        if (context == null)
            context = MainApplication.getContext();
        Gson gson = new Gson();
        JsonObject json = new JsonObject();
        json.addProperty("username", username);
        json.addProperty("pwd", pwd);
        json.addProperty("type", type);
        json.addProperty("languageType", languageType);
        if (countryCode != null)
            json.addProperty("country", countryCode);
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s,json:%s", ipUrl, CWConstant.URL_USER_LOGIN,
                    gson.toJson(json)));
        Ion.with(context)
                .load(String.format("%s%s", ipUrl, CWConstant.URL_USER_LOGIN))
//                .setStringBody(gson.toJson(json))
                .setStringBody(AesUtil.encrypt(gson.toJson(json)))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_USER_LOGIN, handler));
    }

    /**
     * 获取邮箱验证码
     *
     * @param context 上下文
     * @param email   邮箱
     * @param handler 消息处理
     */
    public void getMailCode(Context context, String email, Handler handler) {
        if (context == null)
            context = MainApplication.getContext();
        Gson gson = new Gson();
        JsonObject json = new JsonObject();
        json.addProperty("mail", email);
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s,json:%s", ipUrl, CWConstant.URL_GET_MAIL_CODE,
                    gson.toJson(json)));
        Ion.with(context)
                .load(String.format("%s%s", ipUrl, CWConstant.URL_GET_MAIL_CODE))
//                .setStringBody(gson.toJson(json))
                .setStringBody(AesUtil.encrypt(gson.toJson(json)))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_GET_MAIL_CODE,
                        handler));
    }

    /**
     * 注册
     *
     * @param context     上下文
     * @param countryCode 国家编码
     * @param username    用户名
     * @param pwd         密码
     * @param code        验证码
     * @param type        类型 1.邮箱 2手机
     * @param handler     消息处理
     */
    public void register(Context context, String countryCode, String username, String pwd,
                         String code, String type, Handler handler) {
        if (context == null)
            context = MainApplication.getContext();
        Gson gson = new Gson();
        JsonObject json = new JsonObject();
        json.addProperty("username", username);
        json.addProperty("pwd", pwd);
        json.addProperty("type", type);
        json.addProperty("code", code);
        if (countryCode != null)
            json.addProperty("country", countryCode);
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s,json:%s", ipUrl, CWConstant.URL_REGISTER,
                    gson.toJson(json)));
        Ion.with(context)
                .load(String.format("%s%s", ipUrl, CWConstant.URL_REGISTER))
//                .setStringBody(gson.toJson(json))
                .setStringBody(AesUtil.encrypt(gson.toJson(json)))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_REGISTER, handler));
    }

    /**
     * 绑定设备
     *
     * @param context    上下文
     * @param token      token
     * @param imei       imei
     * @param name       称呼
     * @param remark     备注
     * @param deviceType 设备类型
     * @param handler    消息处理
     */
    public void bindDevice(Context context, String token, String imei, String name, String remark
            , int deviceType, Handler handler) {
        if (context == null)
            context = MainApplication.getContext();
        Gson gson = new Gson();
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("activeCode", imei);
        json.addProperty("name", name);
        json.addProperty("remark", remark);
        json.addProperty("deviceType", deviceType);
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s,json:%s", ipUrl, CWConstant.URL_BIND_DEVICE,
                    gson.toJson(json)));
        Ion.with(context)
                .load(String.format("%s%s", ipUrl, CWConstant.URL_BIND_DEVICE))
//                .setStringBody(gson.toJson(json))
                .setStringBody(AesUtil.encrypt(gson.toJson(json)))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_BIND_DEVICE, handler));
    }

    /**
     * 绑定设备
     *
     * @param context  上下文
     * @param token    token
     * @param did      did
     * @param imei     imei
     * @param nickname 称呼
     * @param imageUrl 头像
     * @param handler  消息处理
     */
    public void setBabyNameAndHead(Context context, String token, String did, String imei,
                                   String nickname, String imageUrl, Handler handler) {
        if (context == null)
            context = MainApplication.getContext();
        Gson gson = new Gson();
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("did", did);
        json.addProperty("imei", imei);
        json.addProperty("nickname", nickname);
        json.addProperty("headurl", imageUrl);
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s,json:%s", ipUrl, CWConstant.URL_SET_BABY_NAME_AND_HEAD,
                    gson.toJson(json)));
        Ion.with(context)
                .load(String.format("%s%s", ipUrl, CWConstant.URL_SET_BABY_NAME_AND_HEAD))
//                .setStringBody(gson.toJson(json))
                .setStringBody(AesUtil.encrypt(gson.toJson(json)))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_SET_BABY_NAME_AND_HEAD,
                        handler));
    }

    /**
     * 找回密码  邮箱验证码的发送
     *
     * @param context 上下文
     * @param mail    邮箱
     * @param handler 消息处理
     */
    public void findPwdMailCode(Context context, String mail, Handler handler) {
        if (context == null)
            context = MainApplication.getContext();
        Gson gson = new Gson();
        JsonObject json = new JsonObject();
        json.addProperty("mail", mail);
        String ipUrl = ServerUtils.getCustomRequestUrl(ServerUtils.getCustomServerIp());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s,json:%s", ipUrl, CWConstant.URL_FIND_PWD_MAIL_CODE,
                    gson.toJson(json)));
        Ion.with(context)
                .load(String.format("%s%s", ipUrl, CWConstant.URL_FIND_PWD_MAIL_CODE))
//                .setStringBody(gson.toJson(json))
                .setStringBody(AesUtil.customEncrypt(gson.toJson(json)))
                .asString()
                .setCallback(getCustomCallback(json, CWConstant.REQUEST_URL_FIND_PWD_MAIL_CODE,
                        handler));
    }

    /**
     * 找回密码
     *
     * @param context     上下文
     * @param countryCode 国家编码
     * @param username    用户名
     * @param pwd         密码
     * @param code        验证码
     * @param type        类型 1.邮箱 2手机
     * @param handler     消息处理
     */
    public void findPwd(Context context, String countryCode, String username, String pwd,
                        String code, String type, Handler handler) {
        if (context == null)
            context = MainApplication.getContext();
        Gson gson = new Gson();
        JsonObject json = new JsonObject();
        json.addProperty("username", username);
        json.addProperty("pwd", pwd);
        json.addProperty("type", type);
        json.addProperty("code", code);
        if (countryCode != null)
            json.addProperty("country", countryCode);
        String ipUrl = ServerUtils.getCustomRequestUrl(ServerUtils.getCustomServerIp());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s,json:%s", ipUrl, CWConstant.URL_FIND_PWD,
                    gson.toJson(json)));
        Ion.with(context)
                .load(String.format("%s%s", ipUrl, CWConstant.URL_FIND_PWD))
//                .setStringBody(gson.toJson(json))
                .setStringBody(AesUtil.customEncrypt(gson.toJson(json)))
                .asString()
                .setCallback(getCustomCallback(json, CWConstant.REQUEST_URL_FIND_PWD, handler));
    }

    /**
     * APP用户修改头像
     *
     * @param context      上下文
     * @param token        token
     * @param rongyunToken 融云token
     * @param head         oss的url地址
     * @param id           信息id
     * @param type         类型 1.邮箱 2手机
     * @param handler      消息处理
     */
    public void updateUserPortrait(Context context, String token, String type, String username,
                                   String rongyunToken,
                                   String head, String id, Handler handler) {
        if (context == null)
            context = MainApplication.getContext();
        Gson gson = new Gson();
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("rongyun_token", rongyunToken);
        json.addProperty("type", type);
        json.addProperty("head", head);
        json.addProperty("id", id);
        json.addProperty("username", username);
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s,json:%s", ipUrl, CWConstant.URL_UPDATE_USER_PORTRAIT,
                    gson.toJson(json)));
        Ion.with(context)
                .load(String.format("%s%s", ipUrl, CWConstant.URL_UPDATE_USER_PORTRAIT))
//                .setStringBody(gson.toJson(json))
                .setStringBody(AesUtil.encrypt(gson.toJson(json)))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_UPDATE_USER_PORTRAIT,
                        handler));
    }

    /**
     * 用户修改密码
     *
     * @param context  上下文
     * @param token    token
     * @param username 用户名
     * @param pwd      密码
     * @param oldPwd   旧密码
     * @param type     类型 1.邮箱 2手机
     * @param handler  消息处理
     */
    public void updatePwd(Context context, String token, String type, String username, String pwd,
                          String oldPwd, Handler handler) {
        if (context == null)
            context = MainApplication.getContext();
        Gson gson = new Gson();
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("username", username);
        json.addProperty("type", type);
        json.addProperty("pwd", pwd);
        json.addProperty("oldpwd", oldPwd);
        String ipUrl = ServerUtils.getCustomRequestUrl(ServerUtils.getCustomServerIp());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s,json:%s", ipUrl, CWConstant.URL_UPDATE_PWD,
                    gson.toJson(json)));
        Ion.with(context)
                .load(String.format("%s%s", ipUrl, CWConstant.URL_UPDATE_PWD))
//                .setStringBody(gson.toJson(json))
                .setStringBody(AesUtil.customEncrypt(gson.toJson(json)))
                .asString()
                .setCallback(getCustomCallback(json, CWConstant.REQUEST_URL_UPDATE_PWD, handler));
    }

    /**
     * 增加电子围栏   学校位置也使用这个接口
     *
     * @param context   上下文
     * @param token     token
     * @param imei      imei
     * @param dId       设备id
     * @param fenceName 围栏名称
     * @param lat       纬度
     * @param lng       经度
     * @param radius    必须大于550米
     * @param entry     进电子围栏 1 开 0 关
     * @param exit      出电子围栏 1 开 0 关
     * @param enable    电子围栏开关 1 开 0 关
     * @param handler   消息处理
     */
    public void addWatchFence(Context context, String token, String imei, long dId,
                              String fenceName, String lat, String lng, int radius,
                              int entry, int exit, int enable, Handler handler) {
        if (context == null)
            context = MainApplication.getContext();
        Gson gson = new Gson();
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("imei", imei);
        json.addProperty("d_id", dId);
        json.addProperty("fenceName", fenceName);
        json.addProperty("lat", lat);
        json.addProperty("lng", lng);
        json.addProperty("radius", radius);
        json.addProperty("entry", entry);
        json.addProperty("exit", exit);
        json.addProperty("enable", enable);
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s,json:%s", ipUrl, CWConstant.URL_ADD_WATCH_FENCE,
                    gson.toJson(json)));
        Ion.with(context)
                .load(String.format("%s%s", ipUrl, CWConstant.URL_ADD_WATCH_FENCE))
//                .setStringBody(gson.toJson(json))
                .setStringBody(AesUtil.encrypt(gson.toJson(json)))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_ADD_WATCH_FENCE, handler));
    }

    /**
     * 修改电子围栏
     *
     * @param context   上下文
     * @param token     token
     * @param imei      imei
     * @param dId       设备id
     * @param fenceName 围栏名称
     * @param lat       纬度
     * @param lng       经度
     * @param radius    必须大于550米
     * @param entry     进电子围栏 1 开 0 关
     * @param exit      出电子围栏 1 开 0 关
     * @param enable    电子围栏开关 1 开 0 关
     * @param handler   消息处理
     */
    public void updateWatchFence(Context context, String token, String imei, long dId, long id,
                                 String fenceName, String lat, String lng, int radius,
                                 int entry, int exit, int enable, Handler handler) {
        if (context == null)
            context = MainApplication.getContext();
        Gson gson = new Gson();
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("imei", imei);
        json.addProperty("d_id", dId);
        json.addProperty("id", id);
        json.addProperty("fenceName", fenceName);
        json.addProperty("lat", lat);
        json.addProperty("lng", lng);
        json.addProperty("radius", radius);
        json.addProperty("entry", entry);
        json.addProperty("exit", exit);
        json.addProperty("enable", enable);
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s,json:%s", ipUrl, CWConstant.URL_UPDATE_WATCH_FENCE,
                    gson.toJson(json)));
        Ion.with(context)
                .load(String.format("%s%s", ipUrl, CWConstant.URL_UPDATE_WATCH_FENCE))
//                .setStringBody(gson.toJson(json))
                .setStringBody(AesUtil.encrypt(gson.toJson(json)))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_UPDATE_WATCH_FENCE, handler));
    }

    /**
     * 修改宝贝资料
     *
     * @param context 上下文
     * @param token   token
     * @param dId     设备id
     * @param model   设备信息对象
     * @param handler 消息处理
     */
    public void updateWatchInfo(Context context, String token, long dId, DeviceInfoModel model,
                                Handler handler) {
        if (context == null)
            context = MainApplication.getContext();
        Gson gson = new Gson();
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("imei", model.getImei());
        json.addProperty("d_id", dId);
        json.addProperty("phone", model.getPhone());
        json.addProperty("shortNumber", model.getShortNumber());
        json.addProperty("nickname", model.getNickname());
        json.addProperty("sex", model.getSex());
        json.addProperty("birday", model.getBirday());
        json.addProperty("school_age", model.getSchool_age());
        json.addProperty("weight", model.getWeight());
        json.addProperty("height", model.getHeight());
        json.addProperty("head", model.getHead());
        json.addProperty("familyNumber", model.getFamilyNumber());
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s,json:%s", ipUrl, CWConstant.URL_UPDATE_WATCH_INFO,
                    gson.toJson(json)));
        Ion.with(context)
                .load(String.format("%s%s", ipUrl, CWConstant.URL_UPDATE_WATCH_INFO))
//                .setStringBody(gson.toJson(json))
                .setStringBody(AesUtil.encrypt(gson.toJson(json)))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_UPDATE_WATCH_INFO, handler));
    }

    /**
     * 远程监拍
     *
     * @param context 上下文
     * @param ip      ip
     * @param token   token
     * @param imei    imei
     * @param come    come
     * @param handler 消息处理
     */
    public void captDevice(Context context, String ip, String token, String imei, String come,
                           Handler handler) {
        if (context == null)
            context = MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ip);
        Gson gson = new Gson();
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("imei", imei);
        json.addProperty("come", come);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s,json:%s", ipUrl, CWConstant.URL_CAPT_DEVICE,
                    gson.toJson(json)));
        Ion.with(context)
                .load(String.format("%s%s", ipUrl, CWConstant.URL_CAPT_DEVICE))
//                .setStringBody(gson.toJson(json))
                .setStringBody(AesUtil.encrypt(gson.toJson(json)))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_CAPT_DEVICE, handler));
    }

    /**
     * 设置家庭wifi
     *
     * @param context 上下文
     * @param ip      ip
     * @param token   token
     * @param imei    imei
     * @param dId     d_id
     * @param wifi    wifi 名称+密码
     * @param handler 消息处理
     */
    public void setFamilyWifi(Context context, String ip, String token, String imei, long dId,
                              String wifi, Handler handler) {
        if (context == null)
            context = MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ip);
        Gson gson = new Gson();
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("imei", imei);
        json.addProperty("d_id", dId);
        json.addProperty("wifi", wifi);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s,json:%s", ipUrl, CWConstant.URL_SET_FAMILY_WIFI,
                    gson.toJson(json)));
        Ion.with(context)
                .load(String.format("%s%s", ipUrl, CWConstant.URL_SET_FAMILY_WIFI))
//                .setStringBody(gson.toJson(json))
                .setStringBody(AesUtil.encrypt(gson.toJson(json)))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_SET_FAMILY_WIFI, handler));
    }

    /**
     * 设置闹钟
     *
     * @param context    上下文
     * @param ip         ip
     * @param token      token
     * @param imei       imei
     * @param dId        d_id
     * @param alarmClock 闹钟 格式 ：名称|时间|周期
     * @param handler    消息处理
     */
    public void setAlarmClock(Context context, String ip, String token, String imei, long dId,
                              String alarmClock, Handler handler) {
        if (context == null)
            context = MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ip);
        Gson gson = new Gson();
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("imei", imei);
        json.addProperty("d_id", dId);
        json.addProperty("alarm_clock", alarmClock);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s,json:%s", ipUrl, CWConstant.URL_SET_ALARM_CLOCK,
                    gson.toJson(json)));
        Ion.with(context)
                .load(String.format("%s%s", ipUrl, CWConstant.URL_SET_ALARM_CLOCK))
//                .setStringBody(gson.toJson(json))
                .setStringBody(AesUtil.encrypt(gson.toJson(json)))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_SET_ALARM_CLOCK, handler));
    }

    /**
     * 设置通讯录
     *
     * @param context   上下文
     * @param ip        ip
     * @param token     token
     * @param imei      imei
     * @param dId       d_id
     * @param phoneBook 通讯录 格式 ：角色名称|号码|短号|头像类型
     * @param handler   消息处理
     */
    public void setContacts(Context context, String ip, String token, String imei, long dId,
                            String phoneBook, Handler handler) {
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ip);
        Gson gson = new Gson();
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("imei", imei);
        json.addProperty("d_id", dId);
        json.addProperty("phonebook", phoneBook);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s,json:%s", ipUrl, CWConstant.URL_SET_CONTACTS,
                    gson.toJson(json)));
        Ion.with(context)
                .load(String.format("%s%s", ipUrl, CWConstant.URL_SET_CONTACTS))
//                .setStringBody(gson.toJson(json))
                .setStringBody(AesUtil.encrypt(gson.toJson(json)))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_SET_CONTACTS, handler));
    }

    /**
     * 设置上课禁用
     *
     * @param context         上下文
     * @param ip              ip
     * @param token           token
     * @param imei            imei
     * @param dId             d_id
     * @param disabledInClass 禁用时间 格式 ：名称|时间|状态|频率
     * @param handler         消息处理
     */
    public void setDisabledInclass(Context context, String ip, String token, String imei,
                                   long dId, String disabledInClass, Handler handler) {
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ip);
        Gson gson = new Gson();
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("imei", imei);
        json.addProperty("d_id", dId);
        json.addProperty("disabledInClass", disabledInClass);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s,json:%s", ipUrl, CWConstant.URL_SET_DISABLED_INCLASS,
                    gson.toJson(json)));
        Ion.with(context)
                .load(String.format("%s%s", ipUrl, CWConstant.URL_SET_DISABLED_INCLASS))
//                .setStringBody(gson.toJson(json))
                .setStringBody(AesUtil.encrypt(gson.toJson(json)))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_SET_DISABLED_INCLASS,
                        handler));
    }

    /**
     * 下发版本升级url
     *
     * @param context 上下文
     * @param ip      ip
     * @param token   token
     * @param imei    imei
     * @param dId     d_id
     * @param version 版本
     * @param url     升级的url地址
     * @param handler 消息处理
     */
    public void upgradeDevice(Context context, String ip, String token, String imei, long dId,
                              String version, String url, Handler handler) {
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ip);
        Gson gson = new Gson();
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("imei", imei);
        json.addProperty("d_id", dId);
        json.addProperty("version", version);
        json.addProperty("url", url);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s,json:%s", ipUrl, CWConstant.URL_UPGRADE_DEVICE,
                    gson.toJson(json)));
        Ion.with(context)
                .load(String.format("%s%s", ipUrl, CWConstant.URL_UPGRADE_DEVICE))
//                .setStringBody(gson.toJson(json))
                .setStringBody(AesUtil.encrypt(gson.toJson(json)))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_UPGRADE_DEVICE, handler));
    }

    /**
     * 设置其他参数
     *
     * @param context 上下文
     * @param ip      ip
     * @param token   token
     * @param imei    imei
     * @param dId     d_id
     * @param other   其他设置 5,1,1,6:00|23:00|1,20|0
     * @param handler 消息处理
     */
    public void setOther(Context context, String ip, String token, String imei,
                         long dId, String other, Handler handler) {
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ip);
        Gson gson = new Gson();
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("imei", imei);
        json.addProperty("d_id", dId);
        json.addProperty("other", other);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s,json:%s", ipUrl, CWConstant.URL_SET_OTHER,
                    gson.toJson(json)));
        Ion.with(context)
                .load(String.format("%s%s", ipUrl, CWConstant.URL_SET_OTHER))
//                .setStringBody(gson.toJson(json))
                .setStringBody(AesUtil.encrypt(gson.toJson(json)))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_SET_OTHER,
                        handler));
    }

    /**
     * 改为wifi流量升级控制
     *
     * @param context    上下文
     * @param ip         ip
     * @param token      token
     * @param imei       imei
     * @param dId        d_id
     * @param wifiStatus 有新版本自动安装  1打开 0关闭
     * @param webTraffic 是否打开流量下载  1打开  0关闭
     * @param handler    消息处理
     */
    public void setUpgrade(Context context, String ip, String token, String imei, long dId,
                           String wifiStatus, String webTraffic, Handler handler) {
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ip);
        Gson gson = new Gson();
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("imei", imei);
        json.addProperty("d_id", dId);
        json.addProperty("wifiStatus", wifiStatus);
        json.addProperty("webTraffic", webTraffic);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s,json:%s", ipUrl, CWConstant.URL_UPGRADE,
                    gson.toJson(json)));
        Ion.with(context)
                .load(String.format("%s%s", ipUrl, CWConstant.URL_UPGRADE))
//                .setStringBody(gson.toJson(json))
                .setStringBody(AesUtil.encrypt(gson.toJson(json)))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_UPGRADE,
                        handler));
    }

    /**
     * 用户打开关闭 通知消息
     *
     * @param context        上下文
     * @param token          token
     * @param arriveHome     到家
     * @param sos            SOS
     * @param location       定位
     * @param addFriend      添加朋友
     * @param step           计步
     * @param uploadPhoto    上传图片
     * @param phoneLog       通话日志
     * @param cost           短信
     * @param upgrade        升级
     * @param fence          电子围栏
     * @param phoneVoice     手机铃声
     * @param phoneVibration 手机震动
     * @param handler        消息处理
     */
    public void setNotifyStatus(Context context, String token, int arriveHome, int sos,
                                int location, int addFriend, int step, int uploadPhoto,
                                int phoneLog, int cost, int upgrade, int fence, int phoneVoice,
                                int phoneVibration, Handler handler) {
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        Gson gson = new Gson();
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("arriveHome", arriveHome);
        json.addProperty("sos", sos);
        json.addProperty("location", location);
        json.addProperty("addFriend", addFriend);
        json.addProperty("step", step);
        json.addProperty("uploadPhoto", uploadPhoto);
        json.addProperty("phoneLog", phoneLog);
        json.addProperty("cost", cost);
        json.addProperty("upgrade", upgrade);
        json.addProperty("fence", fence);
        json.addProperty("phoneVoice", phoneVoice);
        json.addProperty("phoneVibration", phoneVibration);
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s,json:%s", ipUrl, CWConstant.URL_SET_NOTIFY_STATUS,
                    gson.toJson(json)));
        Ion.with(context)
                .load(String.format("%s%s", ipUrl, CWConstant.URL_SET_NOTIFY_STATUS))
//                .setStringBody(gson.toJson(json))
                .setStringBody(AesUtil.encrypt(gson.toJson(json)))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_SET_NOTIFY_STATUS,
                        handler));
    }

    /**
     * 修改绑定设备昵称
     *
     * @param context 上下文
     * @param uId     用户ID
     * @param token   token
     * @param id      设备id
     * @param name    宝贝昵称
     * @param handler 消息处理
     */
    public void updateBindBabyName(Context context, long uId, String token, long id, String name,
                                   Handler handler) {
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        Gson gson = new Gson();
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("u_id", uId);
        json.addProperty("id", id);
        json.addProperty("name", name);
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s,json:%s", ipUrl,
                    CWConstant.URL_UPDATE_BIND_BABY_NAME, gson.toJson(json)));
        Ion.with(context)
                .load(String.format("%s%s", ipUrl, CWConstant.URL_UPDATE_BIND_BABY_NAME))
//                .setStringBody(gson.toJson(json))
                .setStringBody(AesUtil.encrypt(gson.toJson(json)))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_UPDATE_BIND_BABY_NAME,
                        handler));
    }

    /**
     * 设置吃药提醒
     *
     * @param context 上下文
     * @param imei    设备imei
     * @param token   token
     * @param dId     设备id
     * @param info    吃药提醒
     * @param handler 消息处理
     */
    public void setCureRemind(Context context, String ip, String token, String imei, long dId,
                              String info,
                              Handler handler) {
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ip);
        Gson gson = new Gson();
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("imei", imei);
        json.addProperty("d_id", dId);
        json.addProperty("info", info);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s,json:%s", ipUrl,
                    CWConstant.URL_SET_CURE_REMIND, gson.toJson(json)));
        Ion.with(context)
                .load(String.format("%s%s", ipUrl, CWConstant.URL_SET_CURE_REMIND))
//                .setStringBody(gson.toJson(json))
                .setStringBody(AesUtil.encrypt(gson.toJson(json)))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_SET_CURE_REMIND,
                        handler));
    }

    /**
     * 设置久坐提醒
     *
     * @param context 上下文
     * @param imei    设备imei
     * @param token   token
     * @param dId     设备id
     * @param info    吃药提醒
     * @param handler 消息处理
     */
    public void setSedentaryRemind(Context context, String ip, String token, String imei, long dId,
                                   String info, Handler handler) {
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ip);
        Gson gson = new Gson();
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("imei", imei);
        json.addProperty("d_id", dId);
        json.addProperty("info", info);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s,json:%s", ipUrl,
                    CWConstant.URL_SET_SEDENTARY_REMIND, gson.toJson(json)));
        Ion.with(context)
                .load(String.format("%s%s", ipUrl, CWConstant.URL_SET_SEDENTARY_REMIND))
//                .setStringBody(gson.toJson(json))
                .setStringBody(AesUtil.encrypt(gson.toJson(json)))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_SET_SEDENTARY_REMIND,
                        handler));
    }

    /**
     * 修改绑定成员里的昵称
     *
     * @param context 上下文
     * @param imei    设备imei
     * @param token   token
     * @param id      设备id
     * @param name    成员昵称
     * @param url     地址
     * @param handler 消息处理
     */
    public void updateBindUserName(Context context, String imei, String token, long id, String name,
                                   String url, Handler handler) {
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        Gson gson = new Gson();
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("imei", imei);
        json.addProperty("id", id);
        json.addProperty("name", name);
        json.addProperty("url", url);
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s,json:%s", ipUrl,
                    CWConstant.URL_UPDATE_BIND_USER_NAME, gson.toJson(json)));
        Ion.with(context)
                .load(String.format("%s%s", ipUrl, CWConstant.URL_UPDATE_BIND_USER_NAME))
//                .setStringBody(gson.toJson(json))
                .setStringBody(AesUtil.encrypt(gson.toJson(json)))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_UPDATE_BIND_USER_NAME,
                        handler));
    }

    /**
     * RTOS  APP 向设备发送聊天的接口通知设备（设备在线才发起
     *
     * @param context 上下文
     * @param imei    设备imei
     * @param token   token
     * @param type    TYPE 1视频  2语音
     * @param handler 消息处理
     */
    public void juphoonSend(Context context, String imei, String token, String type,
                            Handler handler) {
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        Gson gson = new Gson();
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("imei", imei);
        json.addProperty("type", type);
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s,json:%s", ipUrl,
                    CWConstant.URL_JUPHOON_SEND, gson.toJson(json)));
        Ion.with(context)
                .load(String.format("%s%s", ipUrl, CWConstant.URL_JUPHOON_SEND))
//                .setStringBody(gson.toJson(json))
                .setStringBody(AesUtil.encrypt(gson.toJson(json)))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_JUPHOON_SEND,
                        handler));
    }

    /*
     * Request GET
     */

    /**
     * 获取短信验证码
     *
     * @param context     上下文
     * @param countryCode 国家编码
     * @param mobile      手机号码
     * @param handler     消息处理
     */
    public void getAuthCode(Context context, String countryCode, String mobile, Handler handler) {
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s/%s/%s", ipUrl, CWConstant.URL_GET_AUTH_CODE,
                    countryCode, mobile));
        JsonObject json = new JsonObject();
        json.addProperty("country", countryCode);
        json.addProperty("username", mobile);
        Ion.with(context)
                .load(String.format("%s%s/%s/%s", ipUrl, CWConstant.URL_GET_AUTH_CODE,
                        countryCode, mobile))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_GET_AUTH_CODE, handler));
    }

    /**
     * 用户查询绑定设备列表
     *
     * @param context 上下文
     * @param uID     用户id
     * @param token   token
     * @param handler 消息处理
     */
    public void getBindDeviceList(Context context, long uID, String token, int deviceType,
                                  Handler handler) {
        if (!NetworkUtils.isNetworkAvailable())
            return;
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s/%s", ipUrl, CWConstant.URL_GET_BIND_DEVICE_LIST,
                    token));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("u_id", uID);
        json.addProperty("deviceType", deviceType);
        Ion.with(context)
                .load(String.format("%s%s/%s", ipUrl, CWConstant.URL_GET_BIND_DEVICE_LIST,
                        token))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_GET_BIND_DEVICE_LIST,
                        handler));
    }

    /**
     * 设备管理员查询  某个设备的绑定用户
     *
     * @param context 上下文
     * @param token   token
     * @param imei    imei
     * @param handler 消息处理
     */
    public void getImeiBindUsers(Context context, String token, String imei, Handler handler) {
        if (!NetworkUtils.isNetworkAvailable())
            return;
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s/%s/%s", ipUrl, CWConstant.URL_GET_IMEI_BIND_USERS,
                    token, imei));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("imei", imei);
        Ion.with(context)
                .load(String.format("%s%s/%s/%s", ipUrl, CWConstant.URL_GET_IMEI_BIND_USERS,
                        token, imei))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_GET_IMEI_BIND_USERS,
                        handler));
    }

    /**
     * 找回密码  手机号验证码的发送
     *
     * @param context     上下文
     * @param countryCode 国家编码
     * @param mobile      手机号码
     * @param handler     消息处理
     */
    public void findPwdAuthCode(Context context, String countryCode, String mobile,
                                Handler handler) {
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getCustomRequestUrl(ServerUtils.getCustomServerIp());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s/%s/%s", ipUrl, CWConstant.URL_FIND_PWD_AUTH_CODE,
                    countryCode, mobile));
        JsonObject json = new JsonObject();
        json.addProperty("country", countryCode);
        json.addProperty("username", mobile);
        Ion.with(context)
                .load(String.format("%s%s/%s/%s", ipUrl, CWConstant.URL_FIND_PWD_AUTH_CODE,
                        countryCode, mobile))
                .asString()
                .setCallback(getCustomCallback(json, CWConstant.REQUEST_URL_FIND_PWD_AUTH_CODE,
                        handler));
    }

    /**
     * 获取定位信息
     *
     * @param context 上下文
     * @param token   token
     * @param imei    imei
     * @param dId     d_id
     * @param handler 消息处理
     */
    public void getLastLocation(Context context, String token, String imei, long dId,
                                Handler handler) {
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s/%s/%s/%s", ipUrl, CWConstant.URL_GET_LAST_LOCATION,
                    token, imei, dId));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("imei", imei);
        json.addProperty("d_id", dId);
        Ion.with(context)
                .load(String.format("%s%s/%s/%s/%s", ipUrl,
                        CWConstant.URL_GET_LAST_LOCATION, token, imei, dId))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_GET_LAST_LOCATION, handler));
    }

    /**
     * 转让管理员
     *
     * @param context 上下文
     * @param token   token
     * @param imei    imei
     * @param userId  user_id
     * @param handler 消息处理
     */
    public void transferAdmin(Context context, String token, String imei, String userId,
                              Handler handler) {
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s/%s/%s/%s", ipUrl, CWConstant.URL_TRANSFER_ADMIN,
                    token, imei, userId));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("imei", imei);
        json.addProperty("user_id", userId);
        Ion.with(context)
                .load(String.format("%s%s/%s/%s/%s", ipUrl, CWConstant.URL_TRANSFER_ADMIN
                        , token, imei, userId))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_TRANSFER_ADMIN, handler));
    }

    /**
     * 解绑
     *
     * @param context 上下文
     * @param token   token
     * @param imei    imei
     * @param uId     u_id
     * @param handler 消息处理
     */
    @SuppressLint("DefaultLocale")
    public void deleteDevice(Context context, String token, String imei, long uId,
                             Handler handler) {
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s/%s/%s/%d", ipUrl, CWConstant.URL_DELETE_DEVICE,
                    token, imei, uId));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("imei", imei);
        json.addProperty("u_id", uId);
        Ion.with(context)
                .load(String.format("%s%s/%s/%s/%d", ipUrl, CWConstant.URL_DELETE_DEVICE,
                        token, imei, uId))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_DELETE_DEVICE, handler));
    }

    /**
     * 获取定位轨迹
     *
     * @param context   上下文
     * @param token     token
     * @param dId       d_id
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param handler   消息处理
     */
    @SuppressLint("DefaultLocale")
    public void watchTrack(Context context, String token, long dId, String startTime,
                           String endTime, Handler handler) {
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s/%s/%d/%s/%s", ipUrl, CWConstant.URL_WATCH_TRACK,
                    token, dId, startTime, endTime));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("d_id", dId);
        json.addProperty("startTime", startTime);
        json.addProperty("endTime", endTime);
        Ion.with(context)
                .load(String.format("%s%s/%s/%d/%s/%s", ipUrl, CWConstant.URL_WATCH_TRACK
                        , token, dId, startTime, endTime))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_WATCH_TRACK, handler));
    }

    /**
     * 查询电子围栏列表
     *
     * @param context 上下文
     * @param token   token
     * @param dId     d_id
     * @param handler 消息处理
     */
    @SuppressLint("DefaultLocale")
    public void getWatchFence(Context context, String token, long dId, Handler handler) {
        if (!NetworkUtils.isNetworkAvailable())
            return;
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s/%s/%d", ipUrl, CWConstant.URL_GET_WATCH_FENCE,
                    token, dId));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("d_id", dId);
        Ion.with(context)
                .load(String.format("%s%s/%s/%d", ipUrl, CWConstant.URL_GET_WATCH_FENCE
                        , token, dId))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_GET_WATCH_FENCE, handler));
    }

    /**
     * 删除电子围栏
     *
     * @param context 上下文
     * @param token   token
     * @param dId     d_id
     * @param id      围栏id
     * @param handler 消息处理
     */
    @SuppressLint("DefaultLocale")
    public void deleteWatchFence(Context context, String token, long dId, String id,
                                 Handler handler) {
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s/%s/%d/%s", ipUrl, CWConstant.URL_DELETE_WATCH_FENCE,
                    token, dId, id));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("d_id", dId);
        json.addProperty("id", id);
        Ion.with(context)
                .load(String.format("%s%s/%s/%d/%s", ipUrl,
                        CWConstant.URL_DELETE_WATCH_FENCE, token, dId, id))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_DELETE_WATCH_FENCE, handler));
    }

    /**
     * 查看宝贝资料
     *
     * @param context 上下文
     * @param token   token
     * @param dId     d_id
     * @param imei    imei
     * @param handler 消息处理
     */
    @SuppressLint("DefaultLocale")
    public void getWatchInfo(Context context, String token, long dId, String imei,
                             Handler handler) {
        if (!NetworkUtils.isNetworkAvailable())
            return;
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s/%s/%s/%d", ipUrl, CWConstant.URL_GET_WATCH_INFO,
                    token, imei, dId));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("d_id", dId);
        json.addProperty("imei", imei);
        Ion.with(context)
                .load(String.format("%s%s/%s/%s/%d", ipUrl,
                        CWConstant.URL_GET_WATCH_INFO, token, imei, dId))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_GET_WATCH_INFO, handler));
    }

    /**
     * 获取APP最新版本信息
     *
     * @param context 上下文
     * @param token   token
     * @param handler 消息处理
     */
    public void getAppVersion(Context context, String token, Handler handler) {
        if (!NetworkUtils.isNetworkAvailable())
            return;
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s/%s/1", ipUrl, CWConstant.URL_GET_APP_VERSION,
                    token));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        Ion.with(context)
                .load(String.format("%s%s/%s/1", ipUrl, CWConstant.URL_GET_APP_VERSION,
                        token))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_GET_APP_VERSION, handler));
    }

    /**
     * 获取手表最新版本信息(下发使用)
     *
     * @param context 上下文
     * @param token   token
     * @param dId     d_id
     * @param handler 消息处理
     */
    @SuppressLint("DefaultLocale")
    public void getWatchVersion(Context context, String token, long dId, Handler handler) {
        if (!NetworkUtils.isNetworkAvailable())
            return;
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s/%s/%d", ipUrl, CWConstant.URL_GET_WATCH_VERSION,
                    token, dId));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("d_id", dId);
        Ion.with(context)
                .load(String.format("%s%s/%s/%d", ipUrl, CWConstant.URL_GET_WATCH_VERSION,
                        token, dId))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_GET_WATCH_VERSION, handler));
    }

    /**
     * 获取紧急通知信息(例如服务器升级维护等)
     *
     * @param context 上下文
     * @param handler 消息处理
     */
    public void getInstrancy(Context context, Handler handler) {
        if (!NetworkUtils.isNetworkAvailable())
            return;
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        KLog.d(String.format("url:%s%s", ipUrl, CWConstant.URL_GET_INSTRANCY));
        JsonObject json = new JsonObject();
        Ion.with(context)
                .load(String.format("%s%s", ipUrl, CWConstant.URL_GET_INSTRANCY))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_GET_INSTRANCY, handler));
    }

    /**
     * 获取设备系统通知消息
     *
     * @param context 上下文
     * @param token   token
     * @param dId     d_id
     * @param handler 消息处理
     */
    @SuppressLint("DefaultLocale")
    public void getWatchMsg(Context context, String token, long dId, Handler handler) {
        if (!NetworkUtils.isNetworkAvailable())
            return;
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s/%s/%d", ipUrl, CWConstant.URL_GET_WATCH_MSG,
                    token, dId));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("d_id", dId);
        Ion.with(context)
                .load(String.format("%s%s/%s/%d", ipUrl, CWConstant.URL_GET_WATCH_MSG,
                        token, dId))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_GET_WATCH_MSG, handler));
    }

    /**
     * 获取APP系统通知消息
     *
     * @param context 上下文
     * @param token   token
     * @param handler 消息处理
     */
    public void getAppMsg(Context context, String token, Handler handler) {
        if (!NetworkUtils.isNetworkAvailable())
            return;
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s/%s", ipUrl, CWConstant.URL_GET_APP_MSG,
                    token));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        Ion.with(context)
                .load(String.format("%s%s/%s", ipUrl, CWConstant.URL_GET_APP_MSG,
                        token))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_GET_APP_MSG, handler));
    }

    /**
     * 管理员同意某个用户绑定(当获取APP系统通知消息type为0时 就需要请求此接口)
     *
     * @param context 上下文
     * @param token   token
     * @param sendId  发送id
     * @param imei    imei
     * @param handler 消息处理
     */
    public void adminAgreeBind(Context context, String token, String sendId, String imei,
                               Handler handler) {
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s/%s/%s/%s", ipUrl, CWConstant.URL_ADMIN_AGREE_BIND,
                    token, sendId, imei));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("sendId", sendId);
        json.addProperty("imei", imei);
        Ion.with(context)
                .load(String.format("%s%s/%s/%s/%s", ipUrl, CWConstant.URL_ADMIN_AGREE_BIND,
                        token, sendId, imei))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_ADMIN_AGREE_BIND, handler));
    }

    /**
     * 管理员拒绝某个用户绑定
     *
     * @param context 上下文
     * @param token   token
     * @param sendId  发送id
     * @param imei    imei
     * @param handler 消息处理
     */
    public void refuseBind(Context context, String token, String sendId, String imei,
                           Handler handler) {
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s/%s/%s/%s", ipUrl, CWConstant.URL_REFUSE_BIND,
                    token, sendId, imei));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("sendId", sendId);
        json.addProperty("imei", imei);
        Ion.with(context)
                .load(String.format("%s%s/%s/%s/%s", ipUrl, CWConstant.URL_REFUSE_BIND,
                        token, sendId, imei))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_REFUSE_BIND, handler));
    }

    /**
     * 用户获取设备拍照的图片
     *
     * @param context 上下文
     * @param token   token
     * @param dId     d_id
     * @param imei    imei
     * @param handler 消息处理
     */
    @SuppressLint("DefaultLocale")
    public void getDevicePhoto(Context context, String token, long dId, String imei,
                               Handler handler) {
        if (!NetworkUtils.isNetworkAvailable())
            return;
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s/%s/%d", ipUrl, CWConstant.URL_GET_DEVICE_PHOTO,
                    token, dId));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("d_id", dId);
        json.addProperty("imei", imei);
        Ion.with(context)
                .load(String.format("%s%s/%s/%d", ipUrl, CWConstant.URL_GET_DEVICE_PHOTO,
                        token, dId))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_GET_DEVICE_PHOTO, handler));
    }

    /**
     * 用户获取上报短信
     *
     * @param context 上下文
     * @param token   token
     * @param dId     d_id
     * @param handler 消息处理
     */
    @SuppressLint("DefaultLocale")
    public void getDeviceSms(Context context, String token, long dId, Handler handler) {
        if (!NetworkUtils.isNetworkAvailable())
            return;
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s/%s/%d", ipUrl, CWConstant.URL_GET_DEVICE_SMS,
                    token, dId));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("d_id", dId);
        Ion.with(context)
                .load(String.format("%s%s/%s/%d", ipUrl, CWConstant.URL_GET_DEVICE_SMS,
                        token, dId))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_GET_DEVICE_SMS, handler));
    }

    /**
     * 获取通话记录
     *
     * @param context 上下文
     * @param token   token
     * @param dId     d_id
     * @param handler 消息处理
     */
    @SuppressLint("DefaultLocale")
    public void getDevicePhoneLog(Context context, String token, long dId, Handler handler) {
        if (!NetworkUtils.isNetworkAvailable())
            return;
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s/%s/%d", ipUrl, CWConstant.URL_GET_DEVICE_PHONE_LOG,
                    token, dId));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("d_id", dId);
        Ion.with(context)
                .load(String.format("%s%s/%s/%d", ipUrl,
                        CWConstant.URL_GET_DEVICE_PHONE_LOG, token, dId))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_GET_DEVICE_PHONE_LOG,
                        handler));
    }

    /**
     * 获取单个设备好友接口
     *
     * @param context 上下文
     * @param token   token
     * @param dId     d_id
     * @param handler 消息处理
     */
    @SuppressLint("DefaultLocale")
    public void getDeviceFriend(Context context, String token, long dId, Handler handler) {
        if (!NetworkUtils.isNetworkAvailable())
            return;
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s/%s/%d", ipUrl, CWConstant.URL_GET_DEVICE_FRIEND,
                    token, dId));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("d_id", dId);
        Ion.with(context)
                .load(String.format("%s%s/%s/%d", ipUrl,
                        CWConstant.URL_GET_DEVICE_FRIEND, token, dId))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_GET_DEVICE_FRIEND, handler));
    }

    /**
     * 获取拨号盘状态开关
     *
     * @param context 上下文
     * @param ip      ip
     * @param token   token
     * @param dId     d_id
     * @param handler 消息处理
     */
    @SuppressLint("DefaultLocale")
    public void getDialPad(Context context, String ip, String token, long dId, Handler handler) {
        if (!NetworkUtils.isNetworkAvailable())
            return;
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ip);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s/%s/%d", ipUrl, CWConstant.URL_GET_DIAL_PAD, token, dId));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("d_id", dId);
        Ion.with(context)
                .load(String.format("%s%s/%s/%d", ipUrl, CWConstant.URL_GET_DIAL_PAD, token, dId))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_GET_DIAL_PAD, handler));
    }

    /**
     * 拨号盘的打开关闭+
     *
     * @param context 上下文
     * @param ip      ip
     * @param token   token
     * @param dId     d_id
     * @param imei    imei
     * @param type    1开0关
     * @param handler 消息处理
     */
    @SuppressLint("DefaultLocale")
    public void setDialPad(Context context, String ip, String token, long dId, String imei,
                           String type, Handler handler) {
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ip);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s/%s/%d/%s/%s", ipUrl, CWConstant.URL_SET_DIAL_PAD,
                    token, dId, imei, type));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("d_id", dId);
        json.addProperty("imei", imei);
        json.addProperty("type", type);
        Ion.with(context)
                .load(String.format("%s%s/%s/%d/%s/%s", ipUrl, CWConstant.URL_SET_DIAL_PAD, token,
                        dId, imei, type))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_SET_DIAL_PAD, handler));
    }

    /**
     * 自动接听的打开关闭
     *
     * @param context 上下文
     * @param ip      ip
     * @param token   token
     * @param dId     d_id
     * @param imei    imei
     * @param type    1开0关
     * @param handler 消息处理
     */
    @SuppressLint("DefaultLocale")
    public void automaticAnswer(Context context, String ip, String token, long dId, String imei,
                                String type, Handler handler) {
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ip);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s/%s/%d/%s/%s", ipUrl, CWConstant.URL_AUTOMATIC_ANSWER,
                    token, dId, imei, type));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("d_id", dId);
        json.addProperty("imei", imei);
        json.addProperty("type", type);
        Ion.with(context)
                .load(String.format("%s%s/%s/%d/%s/%s", ipUrl, CWConstant.URL_AUTOMATIC_ANSWER,
                        token, dId, imei, type))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_AUTOMATIC_ANSWER, handler));
    }

    /**
     * 获取自动接听的状态
     *
     * @param context 上下文
     * @param ip      ip
     * @param token   token
     * @param dId     d_id
     * @param handler 消息处理
     */
    @SuppressLint("DefaultLocale")
    public void getAutomaticAnswer(Context context, String ip, String token, long dId,
                                   Handler handler) {
        if (!NetworkUtils.isNetworkAvailable())
            return;
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ip);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s/%s/%d", ipUrl, CWConstant.URL_GET_AUTOMATIC_ANSWER,
                    token,
                    dId));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("d_id", dId);
        Ion.with(context)
                .load(String.format("%s%s/%s/%d", ipUrl, CWConstant.URL_GET_AUTOMATIC_ANSWER,
                        token, dId))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_GET_AUTOMATIC_ANSWER,
                        handler));
    }

    /**
     * 查找手表
     *
     * @param context 上下文
     * @param ip      ip
     * @param token   token
     * @param imei    imei
     * @param handler 消息处理
     */
    public void findDevice(Context context, String ip, String token, String imei, Handler handler) {
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ip);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s/%s/%s", ipUrl, CWConstant.URL_FIND_DEVICE, token, imei));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("imei", imei);
        Ion.with(context)
                .load(String.format("%s%s/%s/%s", ipUrl, CWConstant.URL_FIND_DEVICE, token, imei))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_FIND_DEVICE, handler));
    }

    /**
     * 获取家庭wifi
     *
     * @param context 上下文
     * @param ip      ip
     * @param token   token
     * @param dId     d_id
     * @param handler 消息处理
     */
    @SuppressLint("DefaultLocale")
    public void getFamilyWifi(Context context, String ip, String token, long dId,
                              Handler handler) {
        if (!NetworkUtils.isNetworkAvailable())
            return;
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ip);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s/%s/%d", ipUrl, CWConstant.URL_GET_FAMILY_WIFI, token,
                    dId));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("d_id", dId);
        Ion.with(context)
                .load(String.format("%s%s/%s/%d", ipUrl, CWConstant.URL_GET_FAMILY_WIFI, token,
                        dId))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_GET_FAMILY_WIFI, handler));
    }

    /**
     * 获取闹钟
     *
     * @param context 上下文
     * @param ip      ip
     * @param token   token
     * @param dId     d_id
     * @param handler 消息处理
     */
    @SuppressLint("DefaultLocale")
    public void getAlarmClock(Context context, String ip, String token, long dId,
                              Handler handler) {
        if (!NetworkUtils.isNetworkAvailable())
            return;
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ip);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s/%s/%d", ipUrl, CWConstant.URL_GET_ALARM_CLOCK, token,
                    dId));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("d_id", dId);
        Ion.with(context)
                .load(String.format("%s%s/%s/%d", ipUrl, CWConstant.URL_GET_ALARM_CLOCK, token,
                        dId))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_GET_ALARM_CLOCK, handler));
    }

    /**
     * 请求设备定位
     *
     * @param context 上下文
     * @param ip      ip
     * @param token   token
     * @param imei    imei
     * @param handler 消息处理
     */
    public void requestLocation(Context context, String ip, String token, String imei,
                                Handler handler) {
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ip);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s/%s/%s", ipUrl, CWConstant.URL_REQUEST_LOCATION, token,
                    imei));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("imei", imei);
        Ion.with(context)
                .load(String.format("%s%s/%s/%s", ipUrl, CWConstant.URL_REQUEST_LOCATION, token,
                        imei))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_REQUEST_LOCATION, handler));
    }

    /**
     * 关机
     *
     * @param context 上下文
     * @param ip      ip
     * @param token   token
     * @param imei    imei
     * @param handler 消息处理
     */
    public void powerOff(Context context, String ip, String token, String imei, Handler handler) {
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ip);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s/%s/%s", ipUrl, CWConstant.URL_POWER_OFF, token,
                    imei));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("imei", imei);
        Ion.with(context)
                .load(String.format("%s%s/%s/%s", ipUrl, CWConstant.URL_POWER_OFF, token,
                        imei))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_POWER_OFF, handler));
    }

    /**
     * 监听
     *
     * @param context 上下文
     * @param ip      ip
     * @param token   token
     * @param imei    imei
     * @param phone   phone
     * @param handler 消息处理
     */
    public void moniotrDevice(Context context, String ip, String token, String imei, String phone
            , Handler handler) {
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ip);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s/%s/%s/%s", ipUrl, CWConstant.URL_MONIOTR_DEVICE, token,
                    imei, phone));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("imei", imei);
        json.addProperty("phone", phone);
        Ion.with(context)
                .load(String.format("%s%s/%s/%s/%s", ipUrl, CWConstant.URL_MONIOTR_DEVICE, token,
                        imei, phone))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_MONIOTR_DEVICE, handler));
    }

    /**
     * 恢复出厂设置
     *
     * @param context 上下文
     * @param ip      ip
     * @param token   token
     * @param imei    imei
     * @param dId     d_id
     * @param handler 消息处理
     */
    @SuppressLint("DefaultLocale")
    public void factoryDevice(Context context, String ip, String token, String imei,
                              long dId, Handler handler) {
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ip);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s/%s/%s/%d", ipUrl, CWConstant.URL_FACTORY_DEVICE, token,
                    imei, dId));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("imei", imei);
        json.addProperty("d_id", dId);
        Ion.with(context)
                .load(String.format("%s%s/%s/%s/%d", ipUrl, CWConstant.URL_FACTORY_DEVICE, token,
                        imei, dId))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_FACTORY_DEVICE, handler));
    }

    /**
     * 重启
     *
     * @param context 上下文
     * @param ip      ip
     * @param token   token
     * @param imei    imei
     * @param handler 消息处理
     */
    public void restartDevice(Context context, String ip, String token, String imei,
                              Handler handler) {
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ip);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s/%s/%s", ipUrl, CWConstant.URL_RESET_DEVICE, token,
                    imei));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("imei", imei);
        Ion.with(context)
                .load(String.format("%s%s/%s/%s", ipUrl, CWConstant.URL_RESET_DEVICE, token,
                        imei))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_RESET_DEVICE, handler));
    }

    /**
     * 丢失 挂失
     *
     * @param context 上下文
     * @param ip      ip
     * @param token   token
     * @param dId     d_id
     * @param imei    imei
     * @param pwd     丢失密码
     * @param handler 消息处理
     */
    @SuppressLint("DefaultLocale")
    public void setLost(Context context, String ip, String token, long dId, String imei,
                        String pwd, Handler handler) {
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ip);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s/%s/%d/%s/%s%%231", ipUrl, CWConstant.URL_SET_LOST, token,
                    dId, imei, pwd));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("d_id", dId);
        json.addProperty("imei", imei);
        json.addProperty("pwd", pwd);
        Ion.with(context)
                .load(String.format("%s%s/%s/%d/%s/%s%%231", ipUrl, CWConstant.URL_SET_LOST, token,
                        dId, imei, pwd))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_SET_LOST, handler));
    }

    /**
     * 获取设置的丢失密码
     *
     * @param context 上下文
     * @param ip      ip
     * @param token   token
     * @param dId     d_id
     * @param handler 消息处理
     */
    @SuppressLint("DefaultLocale")
    public void getLostPwd(Context context, String ip, String token, long dId,
                           Handler handler) {
        if (!NetworkUtils.isNetworkAvailable())
            return;
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ip);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s/%s/%d", ipUrl, CWConstant.URL_GET_LOST_PASSWORD, token,
                    dId));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("d_id", dId);
        Ion.with(context)
                .load(String.format("%s%s/%s/%d", ipUrl, CWConstant.URL_GET_LOST_PASSWORD, token,
                        dId))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_GET_LOST_PASSWORD, handler));
    }

    /**
     * 解除挂失
     *
     * @param context 上下文
     * @param ip      ip
     * @param token   token
     * @param imei    imei
     * @param dId     d_id
     * @param handler 消息处理
     */
    @SuppressLint("DefaultLocale")
    public void alreadyFound(Context context, String ip, String token, String imei, long dId,
                             Handler handler) {
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ip);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s/%s/%s/%d", ipUrl, CWConstant.URL_ALREADY_FOUND, token,
                    imei, dId));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("imei", imei);
        json.addProperty("d_id", dId);
        Ion.with(context)
                .load(String.format("%s%s/%s/%s/%d", ipUrl, CWConstant.URL_ALREADY_FOUND, token,
                        imei, dId))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_ALREADY_FOUND, handler));
    }

    /**
     * 获取通讯录
     *
     * @param context 上下文
     * @param ip      ip
     * @param token   token
     * @param dId     d_id
     * @param handler 消息处理
     */
    @SuppressLint("DefaultLocale")
    public void getContacts(Context context, String ip, String token, long dId,
                            Handler handler) {
        if (!NetworkUtils.isNetworkAvailable())
            return;
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ip);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s/%s/%d", ipUrl, CWConstant.URL_GET_CONTACTS, token, dId));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("d_id", dId);
        Ion.with(context)
                .load(String.format("%s%s/%s/%d", ipUrl, CWConstant.URL_GET_CONTACTS, token, dId))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_GET_CONTACTS, handler));
    }

    /**
     * 获取上课禁用
     *
     * @param context 上下文
     * @param ip      ip
     * @param token   token
     * @param dId     d_id
     * @param handler 消息处理
     */
    @SuppressLint("DefaultLocale")
    public void getDisabledIncalss(Context context, String ip, String token, long dId,
                                   Handler handler) {
        if (!NetworkUtils.isNetworkAvailable())
            return;
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ip);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s/%s/%d", ipUrl, CWConstant.URL_GET_DISABLED_INCLASS,
                    token,
                    dId));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("d_id", dId);
        Ion.with(context)
                .load(String.format("%s%s/%s/%d", ipUrl, CWConstant.URL_GET_DISABLED_INCLASS,
                        token, dId))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_GET_DISABLED_INCLASS,
                        handler));
    }

    /**
     * 定位频率设置
     *
     * @param context 上下文
     * @param ip      ip
     * @param token   token
     * @param dId     d_id
     * @param imei    imei
     * @param time    时间
     * @param handler 消息处理
     */
    @SuppressLint("DefaultLocale")
    public void locationFrequency(Context context, String ip, String token, long dId, String imei,
                                  String time, Handler handler) {
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ip);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s/%s/%d/%s/%s", ipUrl, CWConstant.URL_LOCATION_FREQUENCY,
                    token, dId, imei, time));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("d_id", dId);
        json.addProperty("imei", imei);
        json.addProperty("time", time);
        Ion.with(context)
                .load(String.format("%s%s/%s/%d/%s/%s", ipUrl, CWConstant.URL_LOCATION_FREQUENCY,
                        token, dId, imei, time))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_LOCATION_FREQUENCY, handler));
    }

    /**
     * 获取定位频率设置
     *
     * @param context 上下文
     * @param ip      ip
     * @param token   token
     * @param dId     d_id
     * @param handler 消息处理
     */
    @SuppressLint("DefaultLocale")
    public void getLocationFrequency(Context context, String ip, String token, long dId,
                                     Handler handler) {
        if (!NetworkUtils.isNetworkAvailable())
            return;
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ip);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s/%s/%d", ipUrl, CWConstant.URL_GET_LOCATION_FREQUENCY,
                    token,
                    dId));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("d_id", dId);
        Ion.with(context)
                .load(String.format("%s%s/%s/%d", ipUrl, CWConstant.URL_GET_LOCATION_FREQUENCY,
                        token, dId))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_GET_LOCATION_FREQUENCY,
                        handler));
    }

    /**
     * 设置手表目标步数  计步
     *
     * @param context 上下文
     * @param ip      ip
     * @param token   token
     * @param dId     d_id
     * @param imei    imei
     * @param step    目标步数
     * @param handler 消息处理
     */
    @SuppressLint("DefaultLocale")
    public void setStepGoal(Context context, String ip, String token, long dId, String imei,
                            String step, Handler handler) {
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ip);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s/%s/%d/%s/%s", ipUrl, CWConstant.URL_SET_STEP_GOAL, token,
                    dId, imei, step));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("d_id", dId);
        json.addProperty("imei", imei);
        json.addProperty("steps", step);
        Ion.with(context)
                .load(String.format("%s%s/%s/%d/%s/%s", ipUrl, CWConstant.URL_SET_STEP_GOAL,
                        token, dId, imei, step))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_SET_STEP_GOAL, handler));
    }

    /**
     * 获取手表步数
     *
     * @param context 上下文
     * @param ip      ip
     * @param token   token
     * @param dId     d_id
     * @param handler 消息处理
     */
    @SuppressLint("DefaultLocale")
    public void getTodayStep(Context context, String ip, String token, long dId, Handler handler) {
        if (!NetworkUtils.isNetworkAvailable())
            return;
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ip);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s/%s/%d", ipUrl, CWConstant.URL_GET_TODAY_STEP, token,
                    dId));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("d_id", dId);
        Ion.with(context)
                .load(String.format("%s%s/%s/%d", ipUrl, CWConstant.URL_GET_TODAY_STEP,
                        token, dId))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_GET_TODAY_STEP,
                        handler));
    }

    /**
     * 获取手表设置目标步数
     *
     * @param context 上下文
     * @param ip      ip
     * @param token   token
     * @param dId     d_id
     * @param handler 消息处理
     */
    @SuppressLint("DefaultLocale")
    public void getStepGoal(Context context, String ip, String token, long dId, Handler handler) {
        if (!NetworkUtils.isNetworkAvailable())
            return;
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ip);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s/%s/%d", ipUrl, CWConstant.URL_GET_STEP_GOAL, token,
                    dId));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("d_id", dId);
        Ion.with(context)
                .load(String.format("%s%s/%s/%d", ipUrl, CWConstant.URL_GET_STEP_GOAL,
                        token, dId))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_GET_STEP_GOAL,
                        handler));
    }

    /**
     * 获取最近七天的步数
     *
     * @param context   上下文
     * @param token     token
     * @param dId       d_id
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param handler   消息处理
     */
    @SuppressLint("DefaultLocale")
    public void getStepList(Context context, String token, long dId, String imei, String startTime,
                            String endTime, Handler handler) {
        if (!NetworkUtils.isNetworkAvailable())
            return;
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s/%s/%d/%s/%s", ipUrl, CWConstant.URL_GET_STEP_LIST,
                    token, dId, startTime, endTime));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("d_id", dId);
        json.addProperty("startTime", startTime);
        json.addProperty("endTime", endTime);
        Ion.with(context)
                .load(String.format("%s%s/%s/%d/%s/%s", ipUrl, CWConstant.URL_GET_STEP_LIST,
                        token, dId, startTime, endTime))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_GET_STEP_LIST, handler));
        json.addProperty("imei", imei);
    }

    /**
     * 获取设置
     *
     * @param context 上下文
     * @param ip      ip
     * @param token   token
     * @param dId     d_id
     * @param handler 消息处理
     */
    @SuppressLint("DefaultLocale")
    public void getOther(Context context, String ip, String token, long dId, Handler handler) {
        if (!NetworkUtils.isNetworkAvailable())
            return;
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ip);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s/%s/%d", ipUrl, CWConstant.URL_GET_OTHER, token,
                    dId));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("d_id", dId);
        Ion.with(context)
                .load(String.format("%s%s/%s/%d", ipUrl, CWConstant.URL_GET_OTHER,
                        token, dId))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_GET_OTHER,
                        handler));
    }

    /**
     * 获取 wifi流量升级控制
     *
     * @param context 上下文
     * @param ip      ip
     * @param token   token
     * @param dId     d_id
     * @param handler 消息处理
     */
    @SuppressLint("DefaultLocale")
    public void getUpgrade(Context context, String ip, String token, long dId, Handler handler) {
        if (!NetworkUtils.isNetworkAvailable())
            return;
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ip);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s/%s/%d", ipUrl, CWConstant.URL_GET_UPGRADE, token,
                    dId));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("d_id", dId);
        Ion.with(context)
                .load(String.format("%s%s/%s/%d", ipUrl, CWConstant.URL_GET_UPGRADE,
                        token, dId))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_GET_UPGRADE,
                        handler));
    }

    /**
     * 获取用户打开关闭 通知消息
     *
     * @param context 上下文
     * @param token   token
     * @param handler 消息处理
     */
    public void getNotifyStatus(Context context, String token, Handler handler) {
        if (!NetworkUtils.isNetworkAvailable())
            return;
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s/%s", ipUrl, CWConstant.URL_GET_NOTIFY_STATUS,
                    token));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        Ion.with(context)
                .load(String.format("%s%s/%s", ipUrl, CWConstant.URL_GET_NOTIFY_STATUS,
                        token))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_GET_NOTIFY_STATUS,
                        handler));
    }

    /**
     * 管理员解绑这个设备的所有人
     *
     * @param context 上下文
     * @param token   token
     * @param imei    imei
     * @param handler 消息处理
     */
    public void unbindImei(Context context, String token, String imei, Handler handler) {
        if (!NetworkUtils.isNetworkAvailable())
            return;
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s%s/%s/%s", ipUrl, CWConstant.URL_UNBIND_IMEI,
                    token, imei));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("imei", imei);
        Ion.with(context)
                .load(String.format("%s%s/%s/%s", ipUrl, CWConstant.URL_UNBIND_IMEI,
                        token, imei))
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_UNBIND_IMEI,
                        handler));
    }

    /**
     * 唤醒手表
     *
     * @param context      上下文
     * @param ip           ip
     * @param token        token
     * @param serialNumber serialNumber
     * @param handler      消息处理
     */
    public void wakeDevice(Context context, String ip, String token, String imei,
                           String serialNumber, Handler handler) {
        if (!NetworkUtils.isNetworkAvailable())
            return;
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ip);
        String url = String.format("%s%s/%s/%s/%s", ipUrl, CWConstant.URL_WAKE_DEVICE,
                token, imei, serialNumber);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s", url));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("imei", imei);
        json.addProperty("other", serialNumber);
        Ion.with(context)
                .load(url)
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_WAKE_DEVICE,
                        handler));
    }

    /**
     * 获取吃药提醒设置
     *
     * @param context 上下文
     * @param ip      ip
     * @param token   token
     * @param dId     设备ID
     * @param handler 消息处理
     */
    @SuppressLint("DefaultLocale")
    public void getCureRemind(Context context, String ip, String token, long dId,
                              Handler handler) {
        if (!NetworkUtils.isNetworkAvailable())
            return;
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ip);
        String url = String.format("%s%s/%s/%d", ipUrl, CWConstant.URL_GET_CURE_REMIND, token, dId);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s", url));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("d_id", dId);
        Ion.with(context)
                .load(url)
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_GET_CURE_REMIND,
                        handler));
    }

    /**
     * 获取久坐提醒设置
     *
     * @param context 上下文
     * @param ip      ip
     * @param token   token
     * @param dId     设备ID
     * @param handler 消息处理
     */
    @SuppressLint("DefaultLocale")
    public void getSedentaryRemind(Context context, String ip, String token, long dId,
                                   Handler handler) {
        if (!NetworkUtils.isNetworkAvailable())
            return;
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ip);
        String url = String.format("%s%s/%s/%d", ipUrl, CWConstant.URL_GET_SEDENTARY_REMIND,
                token, dId);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s", url));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("d_id", dId);
        Ion.with(context)
                .load(url)
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_GET_SEDENTARY_REMIND,
                        handler));
    }


    /**
     * 健康管理状态指令下发 想主动获取
     *
     * @param context 上下文
     * @param ip      ip
     * @param token   token
     * @param imei    imei
     * @param type    6 心率 7 血氧 8 温度 9  血压
     * @param handler 消息处理
     */
    public void healthSet(Context context, String ip, String token, String imei,
                          int type, Handler handler) {
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ip);
        String url = String.format("%s%s/%s/%s/%s", ipUrl, CWConstant.URL_HEALTH_SET,
                token, imei, type);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s", url));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("imei", imei);
        json.addProperty("type", type);
        Ion.with(context)
                .load(url)
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_HEALTH_SET,
                        handler));
    }

    public void healthRateLast(Context context, String token, long dId, Handler handler) {
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        String url = String.format("%s%s/%s/%s", ipUrl, CWConstant.URL_HEART_RATE_LAST,
                token, dId);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s", url));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("d_id", dId);
        Ion.with(context)
                .load(url)
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_HEART_RATE_LAST,
                        handler));
    }

    public void healthRateList(Context context, String token, long dId, String starttime,
                               String endtime, Handler handler) {
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        String url = String.format("%s%s/%s/%s/%s/%s", ipUrl, CWConstant.URL_HEART_RATE_LIST,
                token, dId, starttime, endtime);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s", url));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("d_id", dId);
        json.addProperty("startTime", starttime);
        json.addProperty("endTime", endtime);
        Ion.with(context)
                .load(url)
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_HEART_RATE_LIST,
                        handler));
    }

    public void bloodOxygenLast(Context context, String token, long dId, Handler handler) {
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        String url = String.format("%s%s/%s/%s", ipUrl, CWConstant.URL_BLOOD_OXYGEN_LAST,
                token, dId);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s", url));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("d_id", dId);
        Ion.with(context)
                .load(url)
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_BLOOD_OXYGEN_LAST,
                        handler));
    }

    public void bloodOxygenList(Context context, String token, long dId, String starttime,
                                String endtime, Handler handler) {
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        String url = String.format("%s%s/%s/%s/%s/%s", ipUrl, CWConstant.URL_BLOOD_OXYGEN_LIST,
                token, dId, starttime, endtime);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s", url));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("d_id", dId);
        json.addProperty("startTime", starttime);
        json.addProperty("endTime", endtime);
        Ion.with(context)
                .load(url)
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_BLOOD_OXYGEN_LIST,
                        handler));
    }

    public void temperatureLast(Context context, String token, long dId, Handler handler) {
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        String url = String.format("%s%s/%s/%s", ipUrl, CWConstant.URL_TEMPERATURE_LAST,
                token, dId);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s", url));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("d_id", dId);
        Ion.with(context)
                .load(url)
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_TEMPERATURE_LAST,
                        handler));
    }

    public void temperatureList(Context context, String token, long dId, String starttime,
                                String endtime, Handler handler) {
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        String url = String.format("%s%s/%s/%s/%s/%s", ipUrl, CWConstant.URL_TEMPERATURE_LIST,
                token, dId, starttime, endtime);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s", url));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("d_id", dId);
        json.addProperty("startTime", starttime);
        json.addProperty("endTime", endtime);
        Ion.with(context)
                .load(url)
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_TEMPERATURE_LIST,
                        handler));
    }

    public void bloodPressureLast(Context context, String token, long dId, Handler handler) {
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        String url = String.format("%s%s/%s/%s", ipUrl, CWConstant.URL_BLOOD_PRESSURE_LAST,
                token, dId);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s", url));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("d_id", dId);
        Ion.with(context)
                .load(url)
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_BLOOD_PRESSURE_LAST,
                        handler));
    }

    public void bloodPressureList(Context context, String token, long dId, String starttime,
                                  String endtime, Handler handler) {
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        String url = String.format("%s%s/%s/%s/%s/%s", ipUrl, CWConstant.URL_BLOOD_PRESSURE_LIST,
                token, dId, starttime, endtime);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s", url));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("d_id", dId);
        json.addProperty("startTime", starttime);
        json.addProperty("endTime", endtime);
        Ion.with(context)
                .load(url)
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_BLOOD_PRESSURE_LIST,
                        handler));
    }

    public void getAllHealth(Context context, String token, long dId, String imei, String starttime,
                             String endtime, Handler handler) {
        if (!NetworkUtils.isNetworkAvailable())
            return;
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        String url = String.format("%s%s/%s/%s/%s/%s", ipUrl, CWConstant.URL_GET_ALL_HEALTH,
                token, dId, starttime, endtime);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s", url));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("d_id", dId);
        json.addProperty("startTime", starttime);
        json.addProperty("endTime", endtime);
        json.addProperty("imei", imei);
        Ion.with(context)
                .load(url)
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_GET_ALL_HEALTH,
                        handler));
    }

    public void getSevenHeartRate(Context context, String token, long dId, String imei,
                                  Handler handler) {
        if (!NetworkUtils.isNetworkAvailable())
            return;
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        String url = String.format("%s%s/%s/%s", ipUrl, CWConstant.URL_GET_SEVEN_HEART_RATE,
                token, dId);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s", url));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("d_id", dId);
        json.addProperty("imei", imei);
        Ion.with(context)
                .load(url)
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_GET_SEVEN_HEART_RATE,
                        handler));
    }

    public void getHeartRateDayList(Context context, String token, long dId, String imei,
                                    String starttime, String endtime, Handler handler) {
        if (!NetworkUtils.isNetworkAvailable())
            return;
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        String url = String.format("%s%s/%s/%s/%s/%s", ipUrl,
                CWConstant.URL_GET_HEART_RATE_DAY_LIST,
                token, dId, starttime, endtime);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s", url));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("d_id", dId);
        json.addProperty("startTime", starttime);
        json.addProperty("endTime", endtime);
        json.addProperty("imei", imei);
        Ion.with(context)
                .load(url)
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_GET_HEART_RATE_DAY_LIST,
                        handler));
    }

    public void getHeartRateWeekList(Context context, String token, long dId, String starttime,
                                     String endtime, Handler handler) {
        if (!NetworkUtils.isNetworkAvailable())
            return;
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        String url = String.format("%s%s/%s/%s/%s/%s", ipUrl,
                CWConstant.URL_GET_HEART_RATE_WEEK_LIST,
                token, dId, starttime, endtime);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s", url));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("d_id", dId);
        json.addProperty("startTime", starttime);
        json.addProperty("endTime", endtime);
        Ion.with(context)
                .load(url)
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_GET_HEART_RATE_WEEK_LIST,
                        handler));
    }

    public void getHeartRateMonthList(Context context, String token, long dId, String imei,
                                      String starttime, String endtime, Handler handler) {
        if (!NetworkUtils.isNetworkAvailable())
            return;
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        String url = String.format("%s%s/%s/%s/%s/%s", ipUrl,
                CWConstant.URL_GET_HEART_RATE_MONTH_LIST,
                token, dId, starttime, endtime);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s", url));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("d_id", dId);
        json.addProperty("startTime", starttime);
        json.addProperty("endTime", endtime);
        json.addProperty("imei", imei);
        Ion.with(context)
                .load(url)
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_GET_HEART_RATE_MONTH_LIST,
                        handler));
    }

    public void getSevenBloodOxygen(Context context, String token, long dId, String imei,
                                    Handler handler) {
        if (!NetworkUtils.isNetworkAvailable())
            return;
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        String url = String.format("%s%s/%s/%s", ipUrl, CWConstant.URL_GET_SEVEN_BLOOD_OXYGEN,
                token, dId);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s", url));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("d_id", dId);
        json.addProperty("imei", imei);
        Ion.with(context)
                .load(url)
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_GET_SEVEN_BLOOD_OXYGEN,
                        handler));
    }

    public void getHeartStatus(Context context, String token, long dId, String imei,
                               Handler handler) {
        if (!NetworkUtils.isNetworkAvailable())
            return;
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        String url = String.format("%s%s/%s/%s", ipUrl, CWConstant.URL_GET_HEART_STATUS,
                token, dId);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s", url));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("d_id", dId);
        json.addProperty("imei", imei);
        Ion.with(context)
                .load(url)
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_GET_HEART_STATUS,
                        handler));
    }

    public void setHeartStatus(Context context, String ip, String token, long dId, String imei,
                               String type, Handler handler) {
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ip);
        String url = String.format("%s%s/%s/%s/%s/%s", ipUrl, CWConstant.URL_SET_HEART_STATUS,
                token, dId, imei, type);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s", url));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("d_id", dId);
        json.addProperty("imei", imei);
        json.addProperty("type", type);
        Ion.with(context)
                .load(url)
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_SET_HEART_STATUS,
                        handler));
    }

    public void getStepDayList(Context context, String token, long dId, String imei,
                               String starttime, String endtime, Handler handler) {
        if (!NetworkUtils.isNetworkAvailable())
            return;
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        String url = String.format("%s%s/%s/%s/%s/%s", ipUrl, CWConstant.URL_GET_STEP_DAY_LIST,
                token, dId, starttime, endtime);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s", url));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("d_id", dId);
        json.addProperty("startTime", starttime);
        json.addProperty("endTime", endtime);
        json.addProperty("imei", imei);
        Ion.with(context)
                .load(url)
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_GET_STEP_DAY_LIST,
                        handler));
    }

    public void getFallOff(Context context, String token, long dId, String imei, Handler handler) {
        if (!NetworkUtils.isNetworkAvailable())
            return;
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ServerUtils.getServerIp());
        String url = String.format("%s%s/%s/%s", ipUrl, CWConstant.URL_GET_FALL_OFF,
                token, dId);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s", url));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("d_id", dId);
        json.addProperty("imei", imei);
        Ion.with(context)
                .load(url)
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_GET_FALL_OFF,
                        handler));
    }

    public void setFallOff(Context context, String ip, String token, long dId, String imei,
                           String type, Handler handler) {
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        String ipUrl = ServerUtils.getRequestUrl(ip);
        String url = String.format("%s%s/%s/%s/%s/%s", ipUrl, CWConstant.URL_SET_FALL_OFF,
                token, dId, imei, type);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s", url));
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.addProperty("d_id", dId);
        json.addProperty("imei", imei);
        json.addProperty("type", type);
        Ion.with(context)
                .load(url)
                .asString()
                .setCallback(getCallback(json, CWConstant.REQUEST_URL_SET_FALL_OFF,
                        handler));
    }

    /**
     * 获取融云Token
     *
     * @param context     上下文
     * @param userId      用户Id
     * @param name        用户名
     * @param portraitUri 用户头像
     * @param handler     消息处理
     */
    public void getUserToken(Context context, long userId, String name, String portraitUri,
                             Handler handler) {
        if (!NetworkUtils.isNetworkAvailable())
            return;
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        Gson gson = new Gson();
        JsonObject json = new JsonObject();
        json.addProperty("u_id", userId);
        json.addProperty("userId", String.valueOf(userId));
        json.addProperty("name", name);
        json.addProperty("portraitUri", portraitUri);
        String nonce = String.valueOf(new Random().nextLong());
        String timestamp = String.valueOf(System.currentTimeMillis());
        String signature = EncryptUtils.getSHA(String.format("%s%s%s",
                CWConstant.RONG_CLOUD_APP_SECRET, nonce, timestamp));
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s,json:%s", CWConstant.URL_GET_TOKEN, gson.toJson(json)));
        Ion.with(context)
                .load(CWConstant.URL_GET_TOKEN)
                .setHeader("App-Key", CWConstant.RONG_CLOUD_APP_KEY)
                .setHeader("Nonce", nonce)
                .setHeader("Timestamp", timestamp)
                .setHeader("Signature", signature)
                .setBodyParameter("userId", String.valueOf(userId))
                .setBodyParameter("name", "family group")
                .setBodyParameter("portraitUri", portraitUri)
                .asJsonObject()
                .setCallback(getJsonCallback(json, CWConstant.REQUEST_URL_GET_TOKEN, handler));
    }

    /**
     * 获取融云Token
     *
     * @param context 上下文
     * @param userId  用户Id
     * @param groupId 群组ID
     * @param handler 消息处理
     */
    public void joinGroup(Context context, String userId, String groupId, Handler handler) {
        if (!NetworkUtils.isNetworkAvailable())
            return;
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        Gson gson = new Gson();
        JsonObject json = new JsonObject();
        json.addProperty("userId", userId);
        json.addProperty("groupId", groupId);
        json.addProperty("groupName", "family group");
        String nonce = String.valueOf(new Random().nextLong());
        String timestamp = String.valueOf(System.currentTimeMillis());
        String signature = EncryptUtils.getSHA(String.format("%s%s%s",
                CWConstant.RONG_CLOUD_APP_SECRET, nonce, timestamp));
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s,json:%s", CWConstant.URL_JOIN_GROUP, gson.toJson(json)));
        Ion.with(context)
                .load(CWConstant.URL_JOIN_GROUP)
                .setHeader("App-Key", CWConstant.RONG_CLOUD_APP_KEY)
                .setHeader("Nonce", nonce)
                .setHeader("Timestamp", timestamp)
                .setHeader("Signature", signature)
                .setBodyParameter("userId", String.valueOf(userId))
                .setBodyParameter("groupId", groupId)
                .setBodyParameter("groupName", "family group")
                .asJsonObject()
                .setCallback(getJsonCallback(json, CWConstant.REQUEST_URL_JOIN_GROUP, handler));
    }

    /**
     * 获取地址
     *
     * @param context 上下文
     * @param lat     纬度
     * @param lng     经度
     * @param handler 消息处理
     */
    public void getAddress(Context context, String lat, String lng, Handler handler) {
        if (!NetworkUtils.isNetworkAvailable())
            return;
        if (context == null)
            context = com.yyt.trackcar.MainApplication.getContext();
        if (BuildConfig.DEBUG)
            KLog.d(String.format("url:%s", String.format(CWConstant.URL_GET_ADDRESS, lat, lng)));
        JsonObject json = new JsonObject();
        json.addProperty("lat", lat);
        json.addProperty("lng", lng);
        Ion.with(context)
                .load(String.format(CWConstant.URL_GET_ADDRESS, lat, lng))
                .asJsonObject()
                .setCallback(getJsonCallback(json, CWConstant.REQUEST_URL_GET_ADDRESS,
                        handler));
    }


    /**
     * 获取回调
     *
     * @param type    类型
     * @param handler 消息处理
     */
    private FutureCallback<String> getCallback(JsonObject jsonObject, int type,
                                               Handler handler) {
        return new FutureCallback<String>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onCompleted(Exception e, String result) {
                // do stuff with the result or error
                Gson gson = new Gson();
                if (e == null) {
                    if (BuildConfig.DEBUG) {
//                        KLog.d(String.format("type:%d,result:%s", type, result));
                        KLog.d(String.format("type:%d,result:%s", type,
                                AesUtil.decrypt(result)));
                    }
                    try {
//                        RequestResultBean resultBean = gson.fromJson(result,
//                                        RequestResultBean.class);
                        RequestResultBean resultBean =
                                gson.fromJson(AesUtil.decrypt(result),
                                        RequestResultBean.class);
                        if (resultBean.getCode() == CWConstant.U_TOKEN_ERR) {
                            RequestToastUtils.toast(null, resultBean.getCode());
                            SettingSPUtils.getInstance().putString(CWConstant.TOKEN, "");
                            SettingSPUtils.getInstance().putLong(CWConstant.U_ID, -1);
                            com.yyt.trackcar.MainApplication.getInstance().setDeviceModel(null);
                            com.yyt.trackcar.MainApplication.getInstance().setUserModel(null);
                            com.yyt.trackcar.MainApplication.getInstance().getDeviceList().clear();
                            EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_FINISH));
                            ActivityUtils.startActivity(LoginActivity.class);
                        } else {
//                            switch (type) {
//                                case CWConstant.REQUEST_URL_USER_LOGIN: // 登录
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_AUTH_CODE: // 获取短信验证码
//                                    break;
//                                case CWConstant.REQUEST_URL_REGISTER: // 注册
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_BIND_DEVICE_LIST: // 用户查询绑定设备列表
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_IMEI_BIND_USERS: // 设备管理员查询
//                                    // 某个设备的绑定用户
//                                    break;
//                                case CWConstant.REQUEST_URL_BIND_DEVICE: // 绑定设备
//                                    break;
//                                case CWConstant.REQUEST_URL_FIND_PWD_MAIL_CODE: // 找回密码  邮箱验证码的发送
//                                    break;
//                                case CWConstant.REQUEST_URL_FIND_PWD_AUTH_CODE: // 找回密码  手机号验证码的发送
//                                    break;
//                                case CWConstant.REQUEST_URL_FIND_PWD: // 找回密码
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_LAST_LOCATION: // 获取定位信息
//                                    break;
//                                case CWConstant.REQUEST_URL_UPDATE_USER_PORTRAIT: // APP用户修改头像
//                                    break;
//                                case CWConstant.REQUEST_URL_UPDATE_PWD: // 用户修改密码
//                                    break;
//                                case CWConstant.REQUEST_URL_TRANSFER_ADMIN: // 转让管理员
//                                    break;
//                                case CWConstant.REQUEST_URL_DELETE_DEVICE: // 解绑
//                                    break;
//                                case CWConstant.REQUEST_URL_WATCH_TRACK: // 获取定位轨迹
//                                    break;
//                                case CWConstant.REQUEST_URL_ADD_WATCH_FENCE: // 增加电子围栏
//                                    // 学校位置也使用这个接口
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_WATCH_FENCE: // 查询电子围栏列表
//                                    break;
//                                case CWConstant.REQUEST_URL_UPDATE_WATCH_FENCE: // 修改电子围栏
//                                    break;
//                                case CWConstant.REQUEST_URL_DELETE_WATCH_FENCE: // 删除电子围栏
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_WATCH_INFO: // 查看宝贝资料
//                                    break;
//                                case CWConstant.REQUEST_URL_UPDATE_WATCH_INFO: // 修改宝贝资料
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_APP_VERSION: // 获取APP最新版本信息
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_WATCH_VERSION: // 获取手表最新版本信息
//                                    // (下发使用)
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_INSTRANCY: // 获取紧急通知信息
//                                    // (例如服务器升级维护等)
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_WATCH_MSG: // 获取设备系统通知消息
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_APP_MSG: // 获取APP系统通知消息
//                                    break;
//                                case CWConstant.REQUEST_URL_ADMIN_AGREE_BIND: // 管理员同意某个用户绑定
//                                    // (当获取APP系统通知消息type为0时 就需要请求此接口)
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_DEVICE_PHOTO: // 用户获取设备拍照的图片
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_DEVICE_SMS: // 用户获取上报短信
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_DEVICE_PHONE_LOG: // 获取通话记录
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_DEVICE_FRIEND: // 获取单个设备好友接口
//                                    break;
//                                case CWConstant.REQUEST_URL_SET_DIAL_PAD: // 拨号盘的打开关闭+
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_DIAL_PAD: // 获取拨号盘状态开关
//                                    break;
//                                case CWConstant.REQUEST_URL_AUTOMATIC_ANSWER: // 自动接听的打开关闭
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_AUTOMATIC_ANSWER: // 获取自动接听的状态
//                                    break;
//                                case CWConstant.REQUEST_URL_FIND_DEVICE: // 查找手表
//                                    break;
//                                case CWConstant.REQUEST_URL_CAPT_DEVICE: // 远程监拍
//                                    break;
//                                case CWConstant.REQUEST_URL_SET_FAMILY_WIFI: // 设置家庭wifi
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_FAMILY_WIFI: // 获取家庭wifi
//                                    break;
//                                case CWConstant.REQUEST_URL_SET_ALARM_CLOCK: // 设置闹钟
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_ALARM_CLOCK: // 获取闹钟
//                                    break;
//                                case CWConstant.REQUEST_URL_REQUEST_LOCATION: // 请求设备定位
//                                    break;
//                                case CWConstant.REQUEST_URL_POWER_OFF: // 关机
//                                    break;
//                                case CWConstant.REQUEST_URL_MONIOTR_DEVICE: // 监听
//                                    break;
//                                case CWConstant.REQUEST_URL_FACTORY_DEVICE: // 恢复出厂设置
//                                    break;
//                                case CWConstant.REQUEST_URL_RESET_DEVICE: // 重启
//                                    break;
//                                case CWConstant.REQUEST_URL_SET_LOST: // 丢失 挂失
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_LOST_PASSWORD: // 获取设置的丢失密码
//                                    break;
//                                case CWConstant.REQUEST_URL_ALREADY_FOUND: // 解除挂失
//                                    break;
//                                case CWConstant.REQUEST_URL_SET_CONTACTS: // 设置通讯录
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_CONTACTS: // 获取通讯录
//                                    break;
//                                case CWConstant.REQUEST_URL_SET_DISABLED_INCLASS: // 设置上课禁用
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_DISABLED_INCLASS: // 获取上课禁用
//                                    break;
//                                case CWConstant.REQUEST_URL_UPGRADE_DEVICE: // 下发版本升级url
//                                    break;
//                                case CWConstant.REQUEST_URL_LOCATION_FREQUENCY: // 定位频率设置
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_LOCATION_FREQUENCY: // 获取定位频率设置
//                                    break;
//                                case CWConstant.REQUEST_URL_SET_STEP_GOAL: // 设置手表目标步数  计步
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_STEP_GOAL: // 获取手表设置目标步数
//                                    break;
//                                case CWConstant.REQUEST_URL_SET_OTHER: // 设置其他参数
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_OTHER: // 获取设置
//                                    break;
//                                case CWConstant.REQUEST_URL_UPGRADE: // 改为wifi流量升级控制
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_UPGRADE: // 获取 wifi流量升级控制
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_STEP_LIST: // 获取最近七天的步数
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_NOTIFY_STATUS: // 获取用户打开关闭 通知消息
//                                    break;
//                                case CWConstant.REQUEST_URL_SET_NOTIFY_STATUS: // 用户打开关闭 通知消息
//                                    break;
//                                case CWConstant.REQUEST_URL_UPDATE_BIND_BABY_NAME: // 修改绑定设备昵称
//                                    break;
//                                case CWConstant.REQUEST_URL_UPDATE_BIND_USER_NAME: // 修改绑定设备昵称
//                                    break;
//                                case CWConstant.REQUEST_URL_REFUSE_BIND: // 管理员拒绝某个用户绑定
//                                    break;
//                                case CWConstant.REQUEST_URL_UNBIND_IMEI: // 管理员解绑这个设备的所有人
//                                    break;
//                                case CWConstant.REQUEST_URL_WAKE_DEVICE: // 唤醒手表
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_MAIL_CODE: // 获取邮箱验证码
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_TODAY_STEP: // 获取手表步数
//                                    break;
//                                case CWConstant.REQUEST_URL_SET_BABY_NAME_AND_HEAD: // 获取手表步数
//                                    break;
//                                default:
//                                    break;
//                            }
                            if (handler != null) {
                                resultBean.setRequestObject(jsonObject);
//                                resultBean.setResultBean(JsonParser.parseString(result)
//                                .getAsJsonObject());
                                resultBean.setResultBean(JsonParser.parseString(AesUtil.decrypt(result)).getAsJsonObject());
                                handler.sendMessage(handler.obtainMessage(type, resultBean));
                            }
                        }
                    } catch (Exception ex) {
                        if (handler != null)
                            handler.sendMessage(handler.obtainMessage(type));
                        if (BuildConfig.DEBUG) {
                            ex.printStackTrace();
                            KLog.d(String.format("type:%d,Exception:%s", type,
                                    gson.toJson(ex.getMessage())));
                        }
                    }
                } else {
                    if (handler != null)
                        handler.sendMessage(handler.obtainMessage(type));
                    if (BuildConfig.DEBUG) {
                        e.printStackTrace();
                        KLog.d(String.format("type:%d,Exception:%s", type,
                                gson.toJson(e.getMessage())));
                    }
                }
            }
        };
    }

    /**
     * 获取回调
     *
     * @param type    类型
     * @param handler 消息处理
     */
    private FutureCallback<String> getCustomCallback(JsonObject jsonObject, int type,
                                                     Handler handler) {
        return new FutureCallback<String>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onCompleted(Exception e, String result) {
                // do stuff with the result or error
                Gson gson = new Gson();
                if (e == null) {
                    if (BuildConfig.DEBUG) {
                        KLog.d(String.format("type:%d,result:%s", type, result));
                        KLog.d(String.format("type:%d,result:%s", type,
                                AesUtil.customDecrypt(result)));
                    }
                    try {
//                        RequestResultBean resultBean = gson.fromJson(result,
//                                        RequestResultBean.class);
                        RequestResultBean resultBean =
                                gson.fromJson(AesUtil.customDecrypt(result),
                                        RequestResultBean.class);
                        if (resultBean.getCode() == CWConstant.U_TOKEN_ERR) {
                            RequestToastUtils.toast(null, resultBean.getCode());
                            SettingSPUtils.getInstance().putString(CWConstant.TOKEN, "");
                            SettingSPUtils.getInstance().putLong(CWConstant.U_ID, -1);
                            com.yyt.trackcar.MainApplication.getInstance().setDeviceModel(null);
                            com.yyt.trackcar.MainApplication.getInstance().setUserModel(null);
                            com.yyt.trackcar.MainApplication.getInstance().getDeviceList().clear();
                            EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_FINISH));
                            ActivityUtils.startActivity(LoginActivity.class);
                        } else {
//                            switch (type) {
//                                case CWConstant.REQUEST_URL_USER_LOGIN: // 登录
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_AUTH_CODE: // 获取短信验证码
//                                    break;
//                                case CWConstant.REQUEST_URL_REGISTER: // 注册
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_BIND_DEVICE_LIST: // 用户查询绑定设备列表
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_IMEI_BIND_USERS: // 设备管理员查询
//                                    // 某个设备的绑定用户
//                                    break;
//                                case CWConstant.REQUEST_URL_BIND_DEVICE: // 绑定设备
//                                    break;
//                                case CWConstant.REQUEST_URL_FIND_PWD_MAIL_CODE: // 找回密码  邮箱验证码的发送
//                                    break;
//                                case CWConstant.REQUEST_URL_FIND_PWD_AUTH_CODE: // 找回密码  手机号验证码的发送
//                                    break;
//                                case CWConstant.REQUEST_URL_FIND_PWD: // 找回密码
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_LAST_LOCATION: // 获取定位信息
//                                    break;
//                                case CWConstant.REQUEST_URL_UPDATE_USER_PORTRAIT: // APP用户修改头像
//                                    break;
//                                case CWConstant.REQUEST_URL_UPDATE_PWD: // 用户修改密码
//                                    break;
//                                case CWConstant.REQUEST_URL_TRANSFER_ADMIN: // 转让管理员
//                                    break;
//                                case CWConstant.REQUEST_URL_DELETE_DEVICE: // 解绑
//                                    break;
//                                case CWConstant.REQUEST_URL_WATCH_TRACK: // 获取定位轨迹
//                                    break;
//                                case CWConstant.REQUEST_URL_ADD_WATCH_FENCE: // 增加电子围栏
//                                    // 学校位置也使用这个接口
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_WATCH_FENCE: // 查询电子围栏列表
//                                    break;
//                                case CWConstant.REQUEST_URL_UPDATE_WATCH_FENCE: // 修改电子围栏
//                                    break;
//                                case CWConstant.REQUEST_URL_DELETE_WATCH_FENCE: // 删除电子围栏
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_WATCH_INFO: // 查看宝贝资料
//                                    break;
//                                case CWConstant.REQUEST_URL_UPDATE_WATCH_INFO: // 修改宝贝资料
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_APP_VERSION: // 获取APP最新版本信息
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_WATCH_VERSION: // 获取手表最新版本信息
//                                    // (下发使用)
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_INSTRANCY: // 获取紧急通知信息
//                                    // (例如服务器升级维护等)
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_WATCH_MSG: // 获取设备系统通知消息
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_APP_MSG: // 获取APP系统通知消息
//                                    break;
//                                case CWConstant.REQUEST_URL_ADMIN_AGREE_BIND: // 管理员同意某个用户绑定
//                                    // (当获取APP系统通知消息type为0时 就需要请求此接口)
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_DEVICE_PHOTO: // 用户获取设备拍照的图片
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_DEVICE_SMS: // 用户获取上报短信
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_DEVICE_PHONE_LOG: // 获取通话记录
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_DEVICE_FRIEND: // 获取单个设备好友接口
//                                    break;
//                                case CWConstant.REQUEST_URL_SET_DIAL_PAD: // 拨号盘的打开关闭+
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_DIAL_PAD: // 获取拨号盘状态开关
//                                    break;
//                                case CWConstant.REQUEST_URL_AUTOMATIC_ANSWER: // 自动接听的打开关闭
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_AUTOMATIC_ANSWER: // 获取自动接听的状态
//                                    break;
//                                case CWConstant.REQUEST_URL_FIND_DEVICE: // 查找手表
//                                    break;
//                                case CWConstant.REQUEST_URL_CAPT_DEVICE: // 远程监拍
//                                    break;
//                                case CWConstant.REQUEST_URL_SET_FAMILY_WIFI: // 设置家庭wifi
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_FAMILY_WIFI: // 获取家庭wifi
//                                    break;
//                                case CWConstant.REQUEST_URL_SET_ALARM_CLOCK: // 设置闹钟
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_ALARM_CLOCK: // 获取闹钟
//                                    break;
//                                case CWConstant.REQUEST_URL_REQUEST_LOCATION: // 请求设备定位
//                                    break;
//                                case CWConstant.REQUEST_URL_POWER_OFF: // 关机
//                                    break;
//                                case CWConstant.REQUEST_URL_MONIOTR_DEVICE: // 监听
//                                    break;
//                                case CWConstant.REQUEST_URL_FACTORY_DEVICE: // 恢复出厂设置
//                                    break;
//                                case CWConstant.REQUEST_URL_RESET_DEVICE: // 重启
//                                    break;
//                                case CWConstant.REQUEST_URL_SET_LOST: // 丢失 挂失
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_LOST_PASSWORD: // 获取设置的丢失密码
//                                    break;
//                                case CWConstant.REQUEST_URL_ALREADY_FOUND: // 解除挂失
//                                    break;
//                                case CWConstant.REQUEST_URL_SET_CONTACTS: // 设置通讯录
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_CONTACTS: // 获取通讯录
//                                    break;
//                                case CWConstant.REQUEST_URL_SET_DISABLED_INCLASS: // 设置上课禁用
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_DISABLED_INCLASS: // 获取上课禁用
//                                    break;
//                                case CWConstant.REQUEST_URL_UPGRADE_DEVICE: // 下发版本升级url
//                                    break;
//                                case CWConstant.REQUEST_URL_LOCATION_FREQUENCY: // 定位频率设置
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_LOCATION_FREQUENCY: // 获取定位频率设置
//                                    break;
//                                case CWConstant.REQUEST_URL_SET_STEP_GOAL: // 设置手表目标步数  计步
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_STEP_GOAL: // 获取手表设置目标步数
//                                    break;
//                                case CWConstant.REQUEST_URL_SET_OTHER: // 设置其他参数
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_OTHER: // 获取设置
//                                    break;
//                                case CWConstant.REQUEST_URL_UPGRADE: // 改为wifi流量升级控制
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_UPGRADE: // 获取 wifi流量升级控制
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_STEP_LIST: // 获取最近七天的步数
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_NOTIFY_STATUS: // 获取用户打开关闭 通知消息
//                                    break;
//                                case CWConstant.REQUEST_URL_SET_NOTIFY_STATUS: // 用户打开关闭 通知消息
//                                    break;
//                                case CWConstant.REQUEST_URL_UPDATE_BIND_BABY_NAME: // 修改绑定设备昵称
//                                    break;
//                                case CWConstant.REQUEST_URL_UPDATE_BIND_USER_NAME: // 修改绑定设备昵称
//                                    break;
//                                case CWConstant.REQUEST_URL_REFUSE_BIND: // 管理员拒绝某个用户绑定
//                                    break;
//                                case CWConstant.REQUEST_URL_UNBIND_IMEI: // 管理员解绑这个设备的所有人
//                                    break;
//                                case CWConstant.REQUEST_URL_WAKE_DEVICE: // 唤醒手表
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_MAIL_CODE: // 获取邮箱验证码
//                                    break;
//                                case CWConstant.REQUEST_URL_GET_TODAY_STEP: // 获取手表步数
//                                    break;
//                                case CWConstant.REQUEST_URL_SET_BABY_NAME_AND_HEAD: // 获取手表步数
//                                    break;
//                                default:
//                                    break;
//                            }
                            if (handler != null) {
                                resultBean.setRequestObject(jsonObject);
//                                resultBean.setResultBean(JsonParser.parseString(result)
//                                .getAsJsonObject());
                                resultBean.setResultBean(JsonParser.parseString(AesUtil.customDecrypt(result)).getAsJsonObject());
                                handler.sendMessage(handler.obtainMessage(type, resultBean));
                            }
                        }
                    } catch (Exception ex) {
                        if (handler != null)
                            handler.sendMessage(handler.obtainMessage(type));
                        if (BuildConfig.DEBUG) {
                            ex.printStackTrace();
                            KLog.d(String.format("type:%d,Exception:%s", type,
                                    gson.toJson(ex.getMessage())));
                        }
                    }
                } else {
                    if (handler != null)
                        handler.sendMessage(handler.obtainMessage(type));
                    if (BuildConfig.DEBUG) {
                        e.printStackTrace();
                        KLog.d(String.format("type:%d,Exception:%s", type,
                                gson.toJson(e.getMessage())));
                    }
                }
            }
        };
    }

    /**
     * 获取回调
     *
     * @param type    类型
     * @param handler 消息处理
     */
    private FutureCallback<JsonObject> getJsonCallback(JsonObject jsonObject, int type,
                                                       Handler handler) {
        return new FutureCallback<JsonObject>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                // do stuff with the result or error
                Gson gson = new Gson();
                if (e == null) {
                    if (BuildConfig.DEBUG)
                        KLog.d(String.format("type:%d,result:%s", type,
                                gson.toJson(result)));
                    try {
                        RequestResultBean resultBean =
                                gson.fromJson(gson.toJson(result),
                                        RequestResultBean.class);
//                        switch (type) {
//                            case CWConstant.REQUEST_URL_GET_TOKEN: // 获取融云Token
//                                break;
//                            case CWConstant.REQUEST_URL_GET_ADDRESS: // 获取地址
//                                break;
//                            case CWConstant.REQUEST_URL_JOIN_GROUP: // 加入群组
//                                break;
//                            default:
//                                break;
//                        }
                        if (handler != null) {
                            resultBean.setRequestObject(jsonObject);
                            resultBean.setResultBean(result);
                            handler.sendMessage(handler.obtainMessage(type, resultBean));
                        }
                    } catch (Exception ex) {
                        handler.sendMessage(handler.obtainMessage(type));
                        if (BuildConfig.DEBUG)
                            ex.printStackTrace();
                    }
                } else {
                    if (handler != null)
                        handler.sendMessage(handler.obtainMessage(type));
                    if (BuildConfig.DEBUG) {
                        e.printStackTrace();
                        KLog.d(String.format("type:%d,Exception:%s", type,
                                gson.toJson(e.getMessage())));
                    }
                }
            }
        };
    }

//    /**
//     * 获取回调
//     *
//     * @param type    类型
//     * @param handler 消息处理
//     */
//    private FutureCallback<String> getStringCallback(JsonObject jsonObject, int type,
//                                                     Handler handler) {
//        return new FutureCallback<String>() {
//            @SuppressLint("DefaultLocale")
//            @Override
//            public void onCompleted(Exception e, String result) {
//                // do stuff with the result or error
//                Gson gson = new Gson();
//                if (e == null) {
//                    if (BuildConfig.DEBUG)
//                        KLog.d(String.format("type:%d,result:%s", type, AesUtil.decrypt(result)));
//                    try {
//                        RequestResultBean resultBean =
//                                gson.fromJson(AesUtil.decrypt(result), RequestResultBean.class);
//                        if (resultBean.getCode() == CWConstant.U_TOKEN_ERR) {
//                            RequestToastUtils.toast(resultBean.getCode());
//                            SettingSPUtils.getInstance().putString(CWConstant.TOKEN, "");
//                            SettingSPUtils.getInstance().putString(CWConstant.U_ID, "");
//                            MainApplication.getInstance().setDeviceModel(null);
//                            MainApplication.getInstance().setUserModel(null);
//                            MainApplication.getInstance().getDeviceList().clear();
//                            EventBus.getDefault().post(new PostMessage(CWConstant
//                            .POST_MESSAGE_FINISH));
//                            ActivityUtils.startActivity(LoginActivity.class);
//                        } else {
//                            switch (type) {
//                                case CWConstant.REQUEST_URL_GET_MAIL_CODE: // 获取邮箱验证码
//                                    break;
//                                default:
//                                    break;
//                            }
//                            if (handler != null) {
//                                resultBean.setRequestObject(jsonObject);
//                                resultBean.setResultBean(gson.fromJson(result, JsonObject.class));
//                                KLog.d(gson.toJson(resultBean.getResultBean()));
//                                handler.sendMessage(handler.obtainMessage(type, resultBean));
//                            }
//                        }
//                    } catch (Exception ex) {
//                        handler.sendMessage(handler.obtainMessage(type));
//                        if (BuildConfig.DEBUG)
//                            ex.printStackTrace();
//                    }
//                } else {
//                    if (handler != null)
//                        handler.sendMessage(handler.obtainMessage(type));
//                    if (BuildConfig.DEBUG) {
//                        e.printStackTrace();
//                        KLog.d(String.format("type:%d,Exception:%s", type,
//                                gson.toJson(e.getMessage())));
//                    }
//                }
//            }
//        };
//    }


//    /**
//     * 初始化OSS
//     */
//    public OSS initOSS() {
//        //if null , default will be init
//        ClientConfiguration conf = new ClientConfiguration();
//        conf.setConnectionTimeout(15 * 1000); // connction time out default 15s
//        conf.setSocketTimeout(15 * 1000); // socket timeout，default 15s
//        conf.setMaxConcurrentRequest(5); // synchronous request number，default 5
//        conf.setMaxErrorRetry(2); // retry，default 2
//        //OSSLog.enableLog(); //write local log file ,path is SDCard_path\OSSLog\logs.csv
//
//        OSSCredentialProvider credentialProvider = new OSSCustomSignerCredentialProvider() {
//            @Override
//            public String signContent(String content) {
//                // 您需要在这里依照OSS规定的签名算法，实现加签一串字符内容，并把得到的签名传拼接上AccessKeyId后返回
//                // 一般实现是，将字符内容post到您的业务服务器，然后返回签名
//                // 如果因为某种原因加签失败，描述error信息后，返回nil
//
//                // 以下是用本地算法进行的演示
//                return OSSUtils.sign(CWConstant.OSS_ACCESS_KEY_ID, CWConstant.OSS_ACCESS_KEY_SECRET
//                        , content);
//            }
//        };
//        return new OSSClient(com.yyt.trackcar.MainApplication.getInstance().getApplicationContext(),
//                CWConstant.OSS_END_POINT, credentialProvider);
//    }

//    /**
//     * 上传文件
//     *
//     * @param filePath 文件路径
//     */
//    public OSSAsyncTask uploadFile(DeviceModel deviceModel, String filePath, Handler handler) {
//        if (!NetworkUtils.isNetworkAvailable()) {
//            RequestToastUtils.toastNetwork(null);
//            return null;
//        }
//        String upladFilePath;
//        if (deviceModel == null || TextUtils.isEmpty(deviceModel.getImei()))
//            upladFilePath = String.format("lagenio/portrait/device/%s/%s.png",
//                    TimeUtils.formatUTC(System.currentTimeMillis(), "yyyy-MM-dd"),
//                    StringUtils.getRandomString(16));
//        else
//            upladFilePath = String.format("lagenio/portrait/device/%s/%s/%s.png",
//                    TimeUtils.formatUTC(System.currentTimeMillis(), "yyyy-MM-dd"),
//                    deviceModel.getImei(), StringUtils.getRandomString(16));
//        // Construct an upload request
//        PutObjectRequest put = new PutObjectRequest(CWConstant.OSS_BUCKET_NAME, upladFilePath,
//                filePath);
//        // You can set progress callback during asynchronous upload
////        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
////            @Override
////            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
////                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
////            }
////        });
//        return initOSS().asyncPutObject(put,
//                new OSSCompletedCallback<PutObjectRequest,
//                        PutObjectResult>() {
//                    @Override
//                    public void onSuccess(PutObjectRequest request, PutObjectResult result) {
//                        if (handler != null)
//                            handler.sendMessage(handler.obtainMessage(CWConstant.REQUEST_UPLOAD_IMAGE,
//                                    String.format("%s%s", CWConstant.OSS_IMAGE_PATH,
//                                            request.getObjectKey())));
//                    }
//
//                    @Override
//                    public void onFailure(PutObjectRequest request, ClientException clientExcepion,
//                                          ServiceException serviceException) {
//                        if (handler != null)
//                            handler.sendMessage(handler.obtainMessage(CWConstant.REQUEST_UPLOAD_IMAGE, null));
//                        // Request exception
////                        if (clientExcepion != null) {
////                            // Local exception, such as a network exception
////                            clientExcepion.printStackTrace();
////                        }
////                        if (serviceException != null) {
////                            // Service exception
////                            Log.e("ErrorCode", serviceException.getErrorCode());
////                            Log.e("RequestId", serviceException.getRequestId());
////                            Log.e("HostId", serviceException.getHostId());
////                            Log.e("RawMessage", serviceException.getRawMessage());
////                        }
//                    }
//                });
//    }
//
//    /**
//     * 上传文件
//     *
//     * @param filePath 文件路径
//     */
//    public OSSAsyncTask uploadFile(UserModel userModel, String filePath, Handler handler) {
//        if (!NetworkUtils.isNetworkAvailable()) {
//            RequestToastUtils.toastNetwork(null);
//            return null;
//        }
//        String upladFilePath;
//
//        if (userModel == null)
//            upladFilePath = String.format("lagenio/portrait/user/%s/%s.png",
//                    TimeUtils.formatUTC(System.currentTimeMillis(), "yyyy-MM-dd"),
//                    StringUtils.getRandomString(16));
//        else
//            upladFilePath = String.format("lagenio/portrait/user/%s/%s/%s.png",
//                    TimeUtils.formatUTC(System.currentTimeMillis(), "yyyy-MM-dd"),
//                    String.valueOf(userModel.getU_id()), StringUtils.getRandomString(16));
//        // Construct an upload request
//        PutObjectRequest put = new PutObjectRequest(CWConstant.OSS_BUCKET_NAME, upladFilePath,
//                filePath);
//        // You can set progress callback during asynchronous upload
////        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
////            @Override
////            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
////                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
////            }
////        });
//        return initOSS().asyncPutObject(put,
//                new OSSCompletedCallback<PutObjectRequest,
//                        PutObjectResult>() {
//                    @Override
//                    public void onSuccess(PutObjectRequest request, PutObjectResult result) {
//                        if (handler != null)
//                            handler.sendMessage(handler.obtainMessage(CWConstant.REQUEST_UPLOAD_IMAGE,
//                                    String.format("%s%s", CWConstant.OSS_IMAGE_PATH,
//                                            request.getObjectKey())));
//                    }
//
//                    @Override
//                    public void onFailure(PutObjectRequest request, ClientException clientExcepion,
//                                          ServiceException serviceException) {
//                        if (handler != null)
//                            handler.sendMessage(handler.obtainMessage(CWConstant.REQUEST_UPLOAD_IMAGE, null));
//                        // Request exception
////                        if (clientExcepion != null) {
////                            // Local exception, such as a network exception
////                            clientExcepion.printStackTrace();
////                        }
////                        if (serviceException != null) {
////                            // Service exception
////                            Log.e("ErrorCode", serviceException.getErrorCode());
////                            Log.e("RequestId", serviceException.getRequestId());
////                            Log.e("HostId", serviceException.getHostId());
////                            Log.e("RawMessage", serviceException.getRawMessage());
////                        }
//                    }
//                });
//    }

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     */
    public void deleteFile(String filePath) {
//        String[] array = filePath.split(CWConstant.OSS_IMAGE_PATH);
//        if (NetworkUtils.isNetworkAvailable() && array.length == 2) {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        OSS ossClient = initOSS();
//                        DeleteObjectRequest objectRequest =
//                                new DeleteObjectRequest(CWConstant.OSS_BUCKET_NAME, array[1]);
//                        // 删除文件。如需删除文件夹，请将ObjectName设置为对应的文件夹名称。如果文件夹非空，则需要将文件夹下的所有object
//                        // 删除后才能删除该文件夹。
//                        ossClient.deleteObject(objectRequest);
//                    } catch (Exception e) {
//                        if (BuildConfig.DEBUG)
//                            e.printStackTrace();
//                    }
//                }
//            }).start();
//        }
    }

}
