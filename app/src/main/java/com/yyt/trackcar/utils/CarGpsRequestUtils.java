package com.yyt.trackcar.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.socks.library.KLog;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.MainApplication;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAABaseResponseBean;
import com.yyt.trackcar.bean.DeviceRaceconfig;
import com.yyt.trackcar.bean.GeoFenceBean;
import com.yyt.trackcar.bean.GpsPigeonRaceBean;
import com.yyt.trackcar.dbflow.AAADeviceModel;
import com.yyt.trackcar.dbflow.AAAUserModel;
import com.yyt.trackcar.ui.activity.LoginActivity;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 项目名：   传信鸽
 * 包名：     com.yyt.trackcar.utils
 * 文件名：   CarGpsRequestUtils
 * 创建者：   QING
 * 创建时间： 2018/4/19 15:01
 * 描述：     TODO 服务器请求工具类
 */

public class CarGpsRequestUtils {

    private static final Gson mGson = new Gson();
    private static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; " +
            "charset=utf-8");

    /**
     * 帐号登录
     */
    public static void doLogin(String username, String password, String language, Handler handler) {
        JsonObject jsonObject = new JsonObject();
        JsonObject subJsonObject = new JsonObject();
        subJsonObject.addProperty("userName", username);
        subJsonObject.addProperty("password", password);
        jsonObject.addProperty("action", "userAction");
        jsonObject.addProperty("method", "login");
        jsonObject.addProperty("language", language);
        jsonObject.add("data", subJsonObject);
//        String url = String.format(TConstant.URL_USER_LOGIN, ip);
//        String url = "http://" + ServerUtils.getNewServiceIp() + "/MainServlet";
        String url = "http://" + ServerUtils.getNewServiceIp() + "/MainServlet";
        String jsonString = mGson.toJson(jsonObject);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, mGson.toJson(jsonObject)));
        Request request = new Request.Builder()
                .addHeader("clientFlag", "1")
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_ACCOUNT_LOGIN, handler));
    }

    /**
     * 用设备号登陆
     */
    public static void imeiLogin(String imei, String password, String language, Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "deviceAction");
        jsonObject.addProperty(TConstant.METHOD, "login");
        jsonObject.addProperty("language", language);
        JsonObject subJson = new JsonObject();
        subJson.addProperty("deviceImei", imei);
        subJson.addProperty("devicePassword", password);
        jsonObject.add("data", subJson);
        String url = "http://" + ServerUtils.getNewServiceIp() + "/MainServlet";
        String jsonString = mGson.toJson(jsonObject);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, mGson.toJson(jsonObject)));
        Request request = new Request.Builder()
                .addHeader("clientFlag", "1")
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_IMEI_LOGIN, handler));
    }

    /**
     * 获取验证码
     *
     * @param account  用户账号
     * @param codeType 账号类型  1、邮箱  2、手机
     */
    public static void getVerifyCode(String areaCode, String account, int codeType, int userId,
                                     String token, Handler handler) {
        JsonObject subJsonObject = new JsonObject();
        if (areaCode != null)
            subJsonObject.addProperty("areaCode", areaCode);
        subJsonObject.addProperty("num", account);
        subJsonObject.addProperty("codeType", codeType);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "authcodeAction");
        jsonObject.addProperty(TConstant.METHOD, "insert");
        jsonObject.addProperty(TConstant.USER_ID_NEW, userId);
        if (!TextUtils.isEmpty(token))
            jsonObject.addProperty(TConstant.TOKEN, token);
        else
            jsonObject.addProperty(TConstant.TOKEN, "null");
        jsonObject.add("data", subJsonObject);
        String jsonString = mGson.toJson(jsonObject);
        String url = "http://" + ServerUtils.getNewServiceIp() + "/MainServlet";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("clientFlag", "1")
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_GET_VERIFICATION_CODE, handler));
    }

    /**
     * 对验证码进行验证
     */
    public static void verifyVerificationCode(String username, int codeType,
                                              String verificationCode, Integer uid, String token,
                                              Handler handler) {
        JsonObject jsonObject = new JsonObject();
        JsonObject subJsonObject = new JsonObject();
        subJsonObject.addProperty("num", username);
        subJsonObject.addProperty("codeType", codeType);
        subJsonObject.addProperty("code", verificationCode);
        jsonObject.addProperty(TConstant.ACTION, "authcodeAction");
        jsonObject.addProperty(TConstant.METHOD, "verification");
        if (uid != null)
            jsonObject.addProperty(TConstant.UID, uid);
        if (!TextUtils.isEmpty(token))
            jsonObject.addProperty(TConstant.TOKEN, token);
        jsonObject.add(TConstant.DATA, subJsonObject);
//        String url = String.format(TConstant.URL_USER_LOGIN, ip);
        String url = "http://" + ServerUtils.getNewServiceIp() + "/MainServlet";
        String jsonString = mGson.toJson(jsonObject);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        KLog.d(String.format("%s  json:%s", url, mGson.toJson(subJsonObject)));
//        jsonObject.addProperty("ip", ip);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, mGson.toJson(jsonObject)));
        Request request = new Request.Builder()
                .addHeader("clientFlag", "1")
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_VERIFY_VERIFICATION_CODE, handler));
    }

    /**
     * 注册
     */
    public static void doRegister(String username, String password, String uid, String token,
                                  String parameter, Handler handler) {
        JsonObject subJsonObject = new JsonObject();
        subJsonObject.addProperty("userName", username);
        subJsonObject.addProperty(TConstant.PASSWORD, password);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "userAction");
        jsonObject.addProperty(TConstant.METHOD, "regist");
        if (uid != null && !uid.equals(""))
            jsonObject.addProperty(TConstant.USER_ID_NEW, uid);
        if (token != null && !token.equals(""))
            jsonObject.addProperty(TConstant.TOKEN, token);
        if (parameter != null) {
            jsonObject.addProperty(TConstant.PARAMETER, parameter);
        }
        jsonObject.add(TConstant.DATA, subJsonObject);

        String jsonString = mGson.toJson(jsonObject);
        String url = "http://" + ServerUtils.getNewServiceIp() + "/MainServlet";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("clientFlag", "1")
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_BIND_DEVICE, handler));
    }

    /**
     * 绑定设备
     */
    public static void bindDevice(String imei, AAAUserModel userModel, String deviceName,
                                  String Url, Handler handler) {
        JsonObject subJsonObject = new JsonObject();
        subJsonObject.addProperty("imei", imei);
        subJsonObject.addProperty("userId", String.valueOf(userModel.getUserId()));
        subJsonObject.addProperty("name", deviceName);
        subJsonObject.addProperty("url", Url);
        subJsonObject.addProperty("pwdType", DataUtils.getPwdType());
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "deviceAction");
        jsonObject.addProperty(TConstant.METHOD, "bind");
        jsonObject.add(TConstant.DATA, subJsonObject);

        String jsonString = mGson.toJson(jsonObject);
        String url = "http://" + ServerUtils.getNewServiceIp() + "/MainServlet";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("clientFlag", "1")
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_BIND_DEVICE, handler));
    }

    /**
     * 解绑设备
     */
    public static void deleteDevice(String imei, AAAUserModel userModel, Handler handler) {
        JsonObject subJsonObject = new JsonObject();
        subJsonObject.addProperty("imei", imei);
        subJsonObject.addProperty("userId", String.valueOf(userModel.getUserId()));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "deviceAction");
        jsonObject.addProperty(TConstant.METHOD, "delete");
        jsonObject.add(TConstant.DATA, subJsonObject);

        String jsonString = mGson.toJson(jsonObject);
        String url = "http://" + ServerUtils.getNewServiceIp() + "/MainServlet";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        jsonObject.addProperty("deviceImei", imei);
        Request request = new Request.Builder()
                .addHeader("clientFlag", "1")
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_UNBIND_DEVICE, handler));
    }

    /**
     * 更新设备状态  1、可用  2、飞丢
     *
     * @param deviceId     设备ID
     * @param userModel    用户登录对象，用于在请求头设置userId和Token
     * @param deviceStatus 设备状态  1、可用  2、飞丢
     */
    public static void updateDeviceStatus(long deviceId, AAAUserModel userModel,
                                          Integer deviceStatus, Handler handler) {
        JsonObject subJsonObject = new JsonObject();
        subJsonObject.addProperty("deviceId", deviceId);
        subJsonObject.addProperty("deviceStatus", deviceStatus);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "deviceAction");
        jsonObject.addProperty(TConstant.METHOD, "updateGpsDevicedeviceStatus");
        jsonObject.add(TConstant.DATA, subJsonObject);

        String jsonString = mGson.toJson(jsonObject);
        String url = "http://" + ServerUtils.getNewServiceIp() + "/MainServlet";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        jsonObject.addProperty("deviceId", deviceId);
        jsonObject.addProperty("type", deviceStatus);
        Request request = new Request.Builder()
                .addHeader("clientFlag", "1")
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_UPDATE_DEVICE_STATUS, handler));
    }

    /**
     * 找回密码
     *
     * @param verifyCode 验证码
     */
    public static void forgetPassword(String account, String password, String verifyCode,
                                      Handler handler) {
        JsonObject subJsonObject = new JsonObject();
        subJsonObject.addProperty("userName", account);
        subJsonObject.addProperty("password", password);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "userAction");
        jsonObject.addProperty(TConstant.METHOD, "changePassword");
        jsonObject.addProperty("parameter", verifyCode);

        jsonObject.add("data", subJsonObject);
        String jsonString = mGson.toJson(jsonObject);
        String url = "http://" + ServerUtils.getNewServiceIp() + "/MainServlet";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("clientFlag", "1")
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_RESET_PASSWORD, handler));
    }

    /**
     * 重置密码
     */
    public static void resetPassword(AAAUserModel userModel, String password, String oldPassword,
                                     Handler handler) {
        JsonObject subJsonObject = new JsonObject();
        subJsonObject.addProperty("userName", userModel.getUserName());
        subJsonObject.addProperty("password", password);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "userAction");
        jsonObject.addProperty(TConstant.METHOD, "reSetPassword");
        jsonObject.addProperty("parameter", oldPassword);

        jsonObject.add("data", subJsonObject);
        String jsonString = mGson.toJson(jsonObject);
        String url = "http://" + ServerUtils.getNewServiceIp() + "/MainServlet";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, mGson.toJson(jsonObject)));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_RESET_PASSWORD, handler));
    }

    /**
     * 注销当前登录的帐号
     */
    public static void logOff(AAAUserModel userModel, Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "userAction");
        jsonObject.addProperty(TConstant.METHOD, "release");
        String jsonString = mGson.toJson(jsonObject);
        String url = "http://" + ServerUtils.getNewServiceIp() + "/MainServlet";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, mGson.toJson(jsonObject)));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_LOG_OFF, handler));
    }

    /**
     * 修改用户设备头像
     */
    public static void uploadDeviceHeadPortrait(AAAUserModel userModel, String deviceImei,
                                                String base64Context, String parameter,
                                                Handler handler) {
        JsonObject subJsonObject = new JsonObject();
        subJsonObject.addProperty("deviceImei", deviceImei);
        subJsonObject.addProperty("base64Context", base64Context);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "deviceAction");
        jsonObject.addProperty(TConstant.METHOD, "uploadDeviceHeadPic");
        jsonObject.addProperty("parameter", parameter);

        jsonObject.add("data", subJsonObject);
        String jsonString = mGson.toJson(jsonObject);
        String url = "http://" + ServerUtils.getNewServiceIp() + "/MainServlet";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_UPLOAD_DEVICE_HEAD_PORTRAIT, handler));
    }

    /**
     * 获取设备配置参数
     */
    public static void getDeviceConfiguration(AAAUserModel userModel, String deviceImei,
                                              Handler handler) {
        JsonObject subJsonObject = new JsonObject();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "deviceRaceconfigAction");
        jsonObject.addProperty(TConstant.METHOD, "query");
        jsonObject.addProperty("parameter", deviceImei);

        jsonObject.add("data", subJsonObject);
        String jsonString = mGson.toJson(jsonObject);
        String url = "http://" + ServerUtils.getNewServiceIp() + "/MainServlet";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_GET_DEVICE_RACE_CONFIGURATION, handler));
    }

    public static void getDevicePresetConfiguration(AAAUserModel userModel, String deviceImei,
                                                    Handler handler) {
        JsonObject subJsonObject = new JsonObject();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "deviceRaceconfigtemplateAction");
        jsonObject.addProperty(TConstant.METHOD, "query");
        jsonObject.addProperty("parameter", deviceImei);

        jsonObject.add("data", subJsonObject);
        String jsonString = mGson.toJson(jsonObject);
        String url = "http://" + ServerUtils.getNewServiceIp() + "/MainServlet";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_GET_DEVICE_PRESET_CONFIGURATION, handler));
    }

    /**
     * 更新多个设备的配置(多个设备IMEI之间用逗号隔开)
     */
    public static void updateMultipleDevicesConfiguration(AAAUserModel userModel,
                                                          DeviceRaceconfig config,
                                                          String parameter, Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "deviceRaceconfigAction");
        jsonObject.addProperty(TConstant.METHOD, "updateByMultiImei");
        jsonObject.addProperty(TConstant.PARAMETER, parameter);
        jsonObject.addProperty("data", mGson.toJson(config));
        String jsonString = mGson.toJson(jsonObject);
        String url = "http://" + ServerUtils.getNewServiceIp() + "/MainServlet";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_UPDATE_DEVICE_RACE_CONFIGURATION, handler));
    }

    /**
     * 更新多个设备的记录模式
     */
    public static void updateMultipleDevicesPresetConfiguration(AAAUserModel userModel, long id,
                                                                long rsut, String parameter,
                                                                Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "deviceRaceconfigAction");
        jsonObject.addProperty(TConstant.METHOD, "updateByMultiImeiAndTemplate");
        jsonObject.addProperty(TConstant.PARAMETER, parameter);
        JsonObject subJson = new JsonObject();
        subJson.addProperty("id", id);
        subJson.addProperty("rsut", rsut);
        jsonObject.add("data", subJson);
        String jsonString = mGson.toJson(jsonObject);
        String url = "http://" + ServerUtils.getNewServiceIp() + "/MainServlet";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_UPDATE_DEVICE_PRESET_CONFIGURATION, handler));
    }

    /**
     * 获取直播下所有设备的信息
     */
    public static void getDeviceListOfPigeonRace(AAAUserModel userModel, Long pigeonRaceId,
                                                 Handler handler) {
        JsonObject subJsonObject = new JsonObject();
        subJsonObject.addProperty("pigeonRaceId", pigeonRaceId);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "gpsPigeonRaceAction");
        jsonObject.addProperty(TConstant.METHOD, "queryRaceDetailById");

        jsonObject.add("data", subJsonObject);
        String jsonString = mGson.toJson(jsonObject);
        String url = "http://" + ServerUtils.getNewServiceIp() + "/MainServlet";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_GET_DEVICE_LIST_OF_PIGEON_RACE_LIVE, handler));
    }

    /**
     * 获取直播列表
     */
    public static void getPigeonRaceList(AAAUserModel userModel, Handler handler) {
        JsonObject subJsonObject = new JsonObject();
//        subJsonObject.addProperty("pigeonRaceId", 1);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "gpsPigeonRaceAction");
        jsonObject.addProperty(TConstant.METHOD, "queryRaceList");

        jsonObject.add("data", subJsonObject);
        String jsonString = mGson.toJson(jsonObject);
        String url = "http://" + ServerUtils.getNewServiceIp() + "/MainServlet";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_GET_PIGEON_RACE_LIST, handler));
    }

    /**
     * 获取训飞历史列表
     */
    public static void getFlightTrainingPlanList(AAAUserModel userModel, String deviceImei,
                                                 int pageIndex, int pageSize, Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "deviceRaceConfigPlanAction");
        jsonObject.addProperty(TConstant.METHOD, "query");
        jsonObject.addProperty(TConstant.PARAMETER, deviceImei);
        jsonObject.addProperty(TConstant.PAGE_INDEX, pageIndex);
        jsonObject.addProperty(TConstant.PAGE_SIZE, pageSize);

        String jsonString = mGson.toJson(jsonObject);
        String url = "http://" + ServerUtils.getNewServiceIp() + "/MainServlet";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_GET_FLIGHT_TRAINING_PLAN, handler));
    }

    /**
     * 更新训飞有效结束时间
     */
    public static void deleteFlightTrainingPlan(AAAUserModel userModel, int id, Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "deviceRaceConfigPlanAction");
        jsonObject.addProperty(TConstant.METHOD, "del");
        jsonObject.addProperty(TConstant.PARAMETER, id);

        String jsonString = mGson.toJson(jsonObject);
        String url = "http://" + ServerUtils.getNewServiceIp() + "/MainServlet";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_DELETE_FLIGHT_TRAINING_PLAN, handler));
    }

    /**
     * 更新训飞有效结束时间
     */
    public static void updateFlightTrainingPlan(AAAUserModel userModel, int id, String date,
                                                Handler handler) {
        JsonObject subJsonObject = new JsonObject();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "deviceRaceConfigPlanAction");
        jsonObject.addProperty(TConstant.METHOD, "update");
        subJsonObject.addProperty("id", id);
        subJsonObject.addProperty("cstValidenddatetime", date);

        jsonObject.add("data", subJsonObject);
        String jsonString = mGson.toJson(jsonObject);
        String url = "http://" + ServerUtils.getNewServiceIp() + "/MainServlet";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_UPDATE_FLIGHT_TRAINING_VALID_END_TIME, handler));
    }


    /**
     * 设置设备昵称
     */
    public static void resetDeviceNickname(AAAUserModel userModel, AAADeviceModel deviceModel,
                                           String nickname, Handler handler) {
        JsonObject subJsonObject = new JsonObject();
        subJsonObject.addProperty("imei", deviceModel.getDeviceImei());
        subJsonObject.addProperty("name", nickname);
        subJsonObject.addProperty("userId", userModel.getUserId());
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "deviceAction");
        jsonObject.addProperty(TConstant.METHOD, "update");

        jsonObject.add("data", subJsonObject);
        String jsonString = mGson.toJson(jsonObject);
        String url = "http://" + ServerUtils.getNewServiceIp() + "/MainServlet";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_RESET_NICKNAME, handler));
    }

    /**
     * 设置设备的手机号
     */
    public static void bindMobileForDevice(AAAUserModel userModel, AAADeviceModel deviceModel,
                                           String mobilePhone, Handler handler) {
        JsonObject subJsonObject = new JsonObject();
        subJsonObject.addProperty("deviceImei", deviceModel.getDeviceImei());
        subJsonObject.addProperty("bindMobile", mobilePhone);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "deviceAction");
        jsonObject.addProperty(TConstant.METHOD, "uploadDeviceByBindMobile");

        jsonObject.add("data", subJsonObject);
        String jsonString = mGson.toJson(jsonObject);
        String url = "http://" + ServerUtils.getNewServiceIp() + "/MainServlet";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_BIND_MOBILE_FOR_DEVICE, handler));
    }

    /**
     * 获取轨迹圈列表
     */
    public static void getTrackCircleList(AAAUserModel userModel, int pageIndex, int pageSize,
                                          Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "deviceTrackcircleAction");
        jsonObject.addProperty(TConstant.METHOD, "query");
        jsonObject.addProperty(TConstant.PAGE_INDEX, pageIndex);
        jsonObject.addProperty(TConstant.PAGE_SIZE, pageSize);
        String jsonString = mGson.toJson(jsonObject);
        String url = "http://" + ServerUtils.getNewServiceIp() + "/MainServlet";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_GET_TRACK_CIRCLE_LIST, handler));
    }

    /**
     * 轨迹圈 点赞/取消点赞
     *
     * @param circleId       轨迹圈ID
     * @param thumbsupRemark 备注
     */
    public static void thumbsUpRequest(AAAUserModel userModel, int circleId,
                                       String thumbsupRemark, Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "gpsThumbsupsAction");
        jsonObject.addProperty(TConstant.METHOD, "insert");
        JsonObject subJson = new JsonObject();
        subJson.addProperty("circleId", circleId);
        subJson.addProperty("thumbsupRemark", thumbsupRemark);
        jsonObject.add(TConstant.DATA, subJson);
        String jsonString = mGson.toJson(jsonObject);
        String url = "http://" + ServerUtils.getNewServiceIp() + "/MainServlet";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_TRACK_CIRCLE_THUMBS_UP_AND_THUMB_DOWN, handler));
    }

    /**
     * 取消点赞
     */
    public static void thumbDownRequest(AAAUserModel userModel, int circleId, Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "gpsThumbsupsAction");
        jsonObject.addProperty(TConstant.METHOD, "del");
        jsonObject.addProperty(TConstant.PARAMETER, circleId);
        String jsonString = mGson.toJson(jsonObject);
        String url = "http://" + ServerUtils.getNewServiceIp() + "/MainServlet";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_TRACK_CIRCLE_THUMBS_UP_AND_THUMB_DOWN, handler));
    }

    /**
     * 浏览轨迹圈
     */
    public static void browserTrackCircle(AAAUserModel userModel, int circleId, Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "deviceTrackcircleAction");
        jsonObject.addProperty(TConstant.METHOD, "views");
        jsonObject.addProperty(TConstant.PARAMETER, String.valueOf(circleId));
        String jsonString = mGson.toJson(jsonObject);
        String url = "http://" + ServerUtils.getNewServiceIp() + "/MainServlet";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_VIEW_TRACK_CIRCLE, handler));
    }

    /**
     * 上传轨迹图片
     */
    public static void uploadTrackImage(AAAUserModel userModel, String deviceImei,
                                        String base64Context, Handler handler) {
        JsonObject jsonObject = new JsonObject();
        JsonObject subJson = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "deviceTrackcircleAction");
        jsonObject.addProperty(TConstant.METHOD, "uploadTrackcirclePic");
        subJson.addProperty("deviceImei", deviceImei);
        subJson.addProperty("base64Context", base64Context);
        jsonObject.add(TConstant.DATA, subJson);

        String jsonString = mGson.toJson(jsonObject);
        String url = "http://" + ServerUtils.getNewServiceIp() + "/MainServlet";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_UPLOAD_TRACK_IMAGE, handler));
    }

    /**
     * 分享至轨迹圈
     *
     * @param userModel
     * @param deviceImei
     * @param startTime     轨迹开始时间
     * @param endTime       轨迹结束时间
     * @param fileInfoId    轨迹圈图片Id
     * @param circleSubject 轨迹主题
     * @param handler
     */
    public static void shareToTrackCircle(AAAUserModel userModel, String deviceImei,
                                          List<String> fileInfoId, String startTime,
                                          String endTime, String circleSubject,
                                          int distance, Handler handler) {
        JsonObject jsonObject = new JsonObject();
        JsonObject subJson = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        for (String item : fileInfoId) {
            jsonArray.add(item);
        }
        jsonObject.addProperty(TConstant.ACTION, "deviceTrackcircleAction");
        jsonObject.addProperty(TConstant.METHOD, "insert");
        subJson.addProperty("deviceImei", deviceImei);
        subJson.add("fileInfoId", jsonArray);
        subJson.addProperty("starDatetime", startTime);
        subJson.addProperty("endDatetime", endTime);
        subJson.addProperty("circleSubject", circleSubject);
        subJson.addProperty("distance", distance);
        jsonObject.add(TConstant.DATA, subJson);

        String jsonString = mGson.toJson(jsonObject);
        String url = "http://" + ServerUtils.getNewServiceIp() + "/MainServlet";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_SHARE_TRACK_TOT_TRACK_CIRCLE, handler));
    }

    /**
     * 删除我分享的轨迹
     */
    public static void deleteMySharedTrack(AAAUserModel userModel, int circleId, Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "deviceTrackcircleAction");
        jsonObject.addProperty(TConstant.METHOD, "del");
        jsonObject.addProperty(TConstant.PARAMETER, circleId);
        String jsonString = mGson.toJson(jsonObject);
        String url = "http://" + ServerUtils.getNewServiceIp() + "/MainServlet";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_DELETE_MY_SHARED_TRACK, handler));
    }

    /**
     * 创建鸽子比赛
     */
    public static void createPigeonCompetition(AAAUserModel userModel, GpsPigeonRaceBean data,
                                               Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "gpsPigeonRaceAction");
        jsonObject.addProperty(TConstant.METHOD, "insert");
        jsonObject.addProperty(TConstant.DATA, mGson.toJson(data));
        String jsonString = mGson.toJson(jsonObject);
        String url = "http://" + ServerUtils.getNewServiceIp() + "/MainServlet";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_CREATE_PIGEON_COMPETITION, handler));
    }

    /**
     * 获取鸽子比赛列表
     *
     * @param userModel
     * @param handler
     * @param date      比赛日期 必须是日期格式，例如：2022-02-13；为空，表示查询所有
     */
    public static void queryPigeonCompetition(AAAUserModel userModel, String date,
                                              Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "gpsPigeonRaceAction");
        jsonObject.addProperty(TConstant.METHOD, "queryRaceByuidList");
        jsonObject.addProperty(TConstant.PARAMETER, date);
        String jsonString = mGson.toJson(jsonObject);
        String url = "http://" + ServerUtils.getNewServiceIp() + "/MainServlet";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_QUERY_PIGEON_COMPETITION, handler));
    }

    /**
     * 根据比赛id获取比赛的配置信息
     */
    public static void queryPigeonCompetitionConfiguration(AAAUserModel userModel, Long raceId,
                                                           Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "deviceRaceconfigAction");
        jsonObject.addProperty(TConstant.METHOD, "queryById");
        jsonObject.addProperty(TConstant.PARAMETER, raceId);
        String jsonString = mGson.toJson(jsonObject);
        String url = "http://" + ServerUtils.getNewServiceIp() + "/MainServlet";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_QUERY_PIGEON_COMPETITION_CONFIGURATION, handler));
    }

    /**
     * 更新比赛的配置信息
     */
    public static void updatePigeonCompetitionConfiguration(AAAUserModel userModel,
                                                            DeviceRaceconfig object,
                                                            Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "deviceRaceconfigAction");
        jsonObject.addProperty(TConstant.METHOD, "updateByRace");
        jsonObject.addProperty(TConstant.DATA, mGson.toJson(object));
        String jsonString = mGson.toJson(jsonObject);
        String url = "http://" + ServerUtils.getNewServiceIp() + "/MainServlet";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_UPDATE_PIGEON_COMPETITION_CONFIGURATION, handler));
    }

    /**
     * 更新赛事信息
     *
     * @param userModel
     * @param object
     * @param handler
     */
    public static void updateCompetitionInfo(AAAUserModel userModel, GpsPigeonRaceBean object,
                                             Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "gpsPigeonRaceAction");
        jsonObject.addProperty(TConstant.METHOD, "update");
        jsonObject.addProperty(TConstant.DATA, mGson.toJson(object));
        String jsonString = mGson.toJson(jsonObject);
        String url = "http://" + ServerUtils.getNewServiceIp() + "/MainServlet";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_UPDATE_COMPETITION_INFO, handler));
    }

    /**
     * 删除比赛
     */
    public static void deletePigeonCompetition(AAAUserModel userModel, Long competitionId,
                                               Handler handler) {
        JsonObject jsonObject = new JsonObject();
        JsonObject subJson = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "gpsPigeonRaceAction");
        jsonObject.addProperty(TConstant.METHOD, "del");
        subJson.addProperty("id", competitionId);
        jsonObject.add(TConstant.DATA, subJson);
        String jsonString = mGson.toJson(jsonObject);
        String url = "http://" + ServerUtils.getNewServiceIp() + "/MainServlet";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_DELETE_PIGEON_COMPETITION, handler));
    }

    /**
     * 搜索参加比赛的设备
     */
    public static void queryDeviceOfPigeonCompetition(AAAUserModel userModel, String deviceImei,
                                                      Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "deviceAction");
        jsonObject.addProperty(TConstant.METHOD, "queryByRace");
        jsonObject.addProperty(TConstant.PARAMETER, deviceImei);
        String jsonString = mGson.toJson(jsonObject);
        String url = "http://" + ServerUtils.getNewServiceIp() + "/MainServlet";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_QUERY_DEVICE_OF_PIGEON_COMPETITION, handler));
    }

    /**
     * 根据IMEI号查找设备
     */
    public static void searchDevice(AAAUserModel userModel, String deviceImei, int pageIndex,
                                    int pageSize, Handler handler) {
        JsonObject jsonObject = new JsonObject();
        JsonObject subJson = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "deviceAction");
        jsonObject.addProperty(TConstant.METHOD, "query");
        jsonObject.addProperty(TConstant.PAGE_INDEX, pageIndex);
        jsonObject.addProperty(TConstant.PAGE_SIZE, pageSize);
        subJson.addProperty("deviceImei", deviceImei);
        jsonObject.add("data", subJson);
        String jsonString = mGson.toJson(jsonObject);
        String url = "http://" + ServerUtils.getNewServiceIp() + "/MainServlet";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_SEARCH_DEVICE_BY_IMEI, handler));
    }

    /**
     * 根据用户名查询经销商
     */
    public static void searchAgencyUser(AAAUserModel userModel, String userName, Handler handler) {
        JsonObject jsonObject = new JsonObject();
        JsonObject subJson = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "userAction");
        jsonObject.addProperty(TConstant.METHOD, "query");
        subJson.addProperty("userName", userName);
        jsonObject.add("data", subJson);
        String jsonString = mGson.toJson(jsonObject);
        String url = "http://" + ServerUtils.getNewServiceIp() + "/MainServlet";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_QUERY_AGENT_USER, handler));
    }

    /**
     * 为经销商绑定设备
     */
    public static void bindDeviceForDealer(AAAUserModel userModel, String deviceImei,
                                           int parentId, Handler handler) {
        JsonObject jsonObject = new JsonObject();
        JsonObject subJson = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "deviceAction");
        jsonObject.addProperty(TConstant.METHOD, "bindDevice");
        subJson.addProperty(TConstant.DEVICE_IMEI, deviceImei);
        subJson.addProperty("parentId", parentId);
        jsonObject.add("data", subJson);
        String jsonString = mGson.toJson(jsonObject);
        String url = "http://" + ServerUtils.getNewServiceIp() + "/MainServlet";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_BIND_DEVICE_FOR_DEALER, handler));
    }

    /**
     * 添加下级经销商
     */
    public static void addSubordinateDealer(AAAUserModel userModel, String username,
                                            Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "userAction");
        jsonObject.addProperty(TConstant.METHOD, "addByAgent");
        jsonObject.addProperty(TConstant.PARAMETER, username);
        String jsonString = mGson.toJson(jsonObject);
        String url = "http://" + ServerUtils.getNewServiceIp() + "/MainServlet";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_ADD_SUBORDINATE_DEALER, handler));
    }

    /**
     * 创建经销商账号
     */
    public static void createSubordinateDealerAccount(AAAUserModel userModel, String username,
                                                      String password, String permissions,
                                                      Handler handler) {
        JsonObject jsonObject = new JsonObject();
        JsonObject subJson = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "userAction");
        jsonObject.addProperty(TConstant.METHOD, "registByagent");
        jsonObject.addProperty(TConstant.PARAMETER, password);
        subJson.addProperty("userName", username);
        subJson.addProperty(TConstant.PASSWORD, password);
        subJson.addProperty(TConstant.ROLES, permissions);
        jsonObject.add(TConstant.DATA, subJson);
        String jsonString = mGson.toJson(jsonObject);
        String url = "http://" + ServerUtils.getNewServiceIp() + "/MainServlet";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_CREATE_SUBORDINATE_DEALER_ACCOUNT, handler));
    }

    /**
     * 从经销商处解绑设备
     */
    public static void unbindDeviceFromDealer(AAAUserModel userModel, String deviceImei,
                                              Handler handler) {
        JsonObject jsonObject = new JsonObject();
        JsonObject subJson = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "deviceAction");
        jsonObject.addProperty(TConstant.METHOD, "unbindDevice");
        subJson.addProperty(TConstant.DEVICE_IMEI, deviceImei);
        jsonObject.add("data", subJson);
        String jsonString = mGson.toJson(jsonObject);
        String url = "http://" + ServerUtils.getNewServiceIp() + "/MainServlet";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_UNBIND_DEVICE_FROM_AGENCY, handler));
    }

    /**
     * 删除下级经销商
     */
    public static void deleteSubordinateDealer(AAAUserModel userModel, long userId,
                                               Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "userAction");
        jsonObject.addProperty(TConstant.METHOD, "delByAgent");
        jsonObject.addProperty(TConstant.PARAMETER, userId);
        String jsonString = mGson.toJson(jsonObject);
        String url = "http://" + ServerUtils.getNewServiceIp() + "/MainServlet";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_DELETE_SUBORDINATE_DEALER, handler));
    }

    /**
     * 搜索参加比赛的设备
     */
    public static void updateCompetitionDevice(AAAUserModel userModel, Long pigeonRaceId,
                                               String deviceImei, Handler handler) {
        JsonObject jsonObject = new JsonObject();
        JsonObject subJson = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "deviceAction");
        jsonObject.addProperty(TConstant.METHOD, "updateGpsDevicepigeonRaceId");
        subJson.addProperty(TConstant.DEVICE_IMEI, deviceImei);
        subJson.addProperty("pigeonRaceId", pigeonRaceId);
        jsonObject.add(TConstant.DATA, subJson);
        String jsonString = mGson.toJson(jsonObject);
        String url = "http://" + ServerUtils.getNewServiceIp() + "/MainServlet";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_UPDATE_DEVICE_OF_COMPETITION, handler));
    }

    /**
     * 获取此经销商下的所有绑定设备
     */
    public static void queryDealerBoundDevices(AAAUserModel userModel, int pageIndex,
                                               int pageSize, long parentId, Handler handler) {
        JsonObject jsonObject = new JsonObject();
        JsonObject subJson = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "deviceAction");
        jsonObject.addProperty(TConstant.METHOD, "queryByAgent");
        jsonObject.addProperty(TConstant.PAGE_INDEX, pageIndex);
        jsonObject.addProperty(TConstant.PAGE_SIZE, pageSize);
        subJson.addProperty("parentId", parentId);
        jsonObject.add(TConstant.DATA, subJson);
        String jsonString = mGson.toJson(jsonObject);
        String url = "http://" + ServerUtils.getNewServiceIp() + "/MainServlet";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_QUERY_DEALER_BOUND_DEVICES, handler));
    }

    /**
     * 获取此经销商以及此经销商的所有子级经销商的所有绑定的设备
     */
    public static void queryDealerAndSubDealersBoundDevices(AAAUserModel userModel, long parentId
            , Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "userAction");
        jsonObject.addProperty(TConstant.METHOD, "queryAllByAgent");
        jsonObject.addProperty(TConstant.PARAMETER, parentId);
        String jsonString = mGson.toJson(jsonObject);
        String url = "http://" + ServerUtils.getNewServiceIp() + "/MainServlet";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_QUERY_DEALER_AND_SUBORDINATE_DEALERS_BOUND_DEVICES, handler));
    }

    /**
     * 切换到子经销商账号
     */
    public static void loginToSubordinateDealer(AAAUserModel userModel, String username,
                                                Handler handler) {
        JsonObject jsonObject = new JsonObject();
        JsonObject subJson = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "userAction");
        jsonObject.addProperty(TConstant.METHOD, "loginByAgent");
        jsonObject.addProperty(TConstant.PARAMETER, userModel.getUserId());
        subJson.addProperty("userName", username);
        jsonObject.add("data", subJson);
        String jsonString = mGson.toJson(jsonObject);
        String url = "http://" + ServerUtils.getNewServiceIp() + "/MainServlet";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .addHeader("clientFlag", "1")
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_LOGIN_TO_SUBORDINATE_DEALER_ACCOUNT, handler));
    }

    /**
     * 获取当前用户绑定的设备列表
     */
    public static void getDeviceList(AAAUserModel userModel, Integer deviceStatus,
                                     Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", userModel.getUserId());
        jsonObject.addProperty("token", userModel.getToken());
        jsonObject.addProperty("type", deviceStatus);
        String url = String.format(TConstant.URL_GET_DEVICE_LIST, ServerUtils.getNewServiceIp());
        if (deviceStatus != null) {
            url += "?deviceStatus=" + deviceStatus;
        }
        String jsonString = mGson.toJson(jsonObject);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .get()
                .build();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(TConstant.CONNECT_TIMEOUT, TimeUnit.SECONDS) // 设置连接超时时间
                .readTimeout(TConstant.READ_TIMEOUT, TimeUnit.SECONDS) // 设置读取超时时间
                .build();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_URL_GET_DEVICE_LIST, handler));
    }

    /**
     * 获取设备最后一次位置信息
     */
    public static void getLastLocation(AAAUserModel userModel, String deviceImei, Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("deviceImei", deviceImei);
        String url = String.format(TConstant.URL_GET_LAST_LOCATION, ServerUtils.getNewServiceIp());
        String jsonString = mGson.toJson(jsonObject);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, mGson.toJson(jsonObject)));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_URL_GET_LAST_LOCATION, handler));
    }

    /**
     * 查询设备历史轨迹 old～
     */
    public static void getHistoryLocation(AAAUserModel userModel, String deviceImei,
                                          String startTime, String endTime, long nextId,
                                          int requestRows, Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("deviceImei", deviceImei);
        jsonObject.addProperty("startTime", startTime);
        jsonObject.addProperty("endTime", endTime);
        jsonObject.addProperty("nextId", nextId);
        jsonObject.addProperty("requestRows", requestRows);
        String url = String.format(TConstant.URL_GET_HISTORY_LOCATION,
                ServerUtils.getNewServiceIp());
        String jsonString = mGson.toJson(jsonObject);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, mGson.toJson(jsonObject)));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(TConstant.CONNECT_TIMEOUT, TimeUnit.SECONDS) // 设置连接超时时间
                .readTimeout(TConstant.READ_TIMEOUT, TimeUnit.SECONDS) // 设置读取超时时间
                .build();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_URL_GET_HISTORY_LOCATION, handler));
    }

    /**
     * 查询设备历史轨迹 new～
     */
    public static void getLastDeviceConfigTrack(AAAUserModel userModel, String deviceImei,
                                                String startTime, String endTime, long nextId,
                                                int requestRows, Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("deviceImei", deviceImei);
        jsonObject.addProperty("startTime", startTime);
        jsonObject.addProperty("endTime", endTime);
        jsonObject.addProperty("nextId", nextId);
        jsonObject.addProperty("requestRows", requestRows);
        String url = String.format(TConstant.URL_GET_LAST_DEVICE_CONFIG_TRACK,
                ServerUtils.getNewServiceIp());
        String jsonString = mGson.toJson(jsonObject);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, mGson.toJson(jsonObject)));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(TConstant.CONNECT_TIMEOUT, TimeUnit.SECONDS) // 设置连接超时时间
                .readTimeout(TConstant.READ_TIMEOUT, TimeUnit.SECONDS) // 设置读取超时时间
                .build();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_URL_GET_LAST_DEVICE_CONFIG_TRACK, handler));
    }

    //                                      分割线
    //  <=====================================================================================>


    public static void sendCommand(String imeiNo, int commandId, int type, Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ImeiNo", imeiNo);
        jsonObject.addProperty("commandid", commandId);
        jsonObject.addProperty("type", type);
        String url = String.format(TConstant.URL_SEND_COMMAND, ServerUtils.getNewServiceIp());
        String jsonString = mGson.toJson(jsonObject);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        RequestBody requestBody = new FormBody.Builder()
                .add("trackerid", imeiNo)
                .add("command", String.valueOf(commandId))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_URL_SEND_COMMAND, handler));
    }

    public static void sendOutputCommand(String imeiNo, int commandId, int type, Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ImeiNo", imeiNo);
        jsonObject.addProperty("commandid", commandId);
        jsonObject.addProperty("type", type);
        String url = String.format(TConstant.URL_SEND_COMMAND, ServerUtils.getNewServiceIp());
        String jsonString = mGson.toJson(jsonObject);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        int output1 = 2;
        int output2 = 2;
        int output3 = 2;
        int output4 = 2;
        int output5 = 2;
        switch (type) {
            case 101:
                output1 = 0;
                break;
            case 102:
                output5 = 1;
                break;
            case 103:
                output4 = 1;
                break;
            case 104:
                output3 = 1;
                break;
            case 105:
            case 1104:
                output2 = 1;
                break;
            default:
                output1 = 1;
                break;
        }
        RequestBody requestBody = new FormBody.Builder()
                .add("trackerid", imeiNo)
                .add("command", String.valueOf(commandId))
                .add("output1", String.valueOf(output1))
                .add("output2", String.valueOf(output2))
                .add("output3", String.valueOf(output3))
                .add("output4", String.valueOf(output4))
                .add("output5", String.valueOf(output5))
                .build();
//        RequestBody requestBody = RequestBody.create(MediaType.parse("application/x-www-form" +
//                "-urlencoded;"), String.format("trackerid=%s&command=%s&output1=%s&output2=%s" +
//                        "&output3=%s&output4=%s&output5=%s",
//                imeiNo, commandId, output1, output2, output3, output4, output4));
//        KLog.d(String.format("trackerid=%s&command=%s&output1=%s&output2=%s" +
//                        "&output3=%s&output4=%s&output5=%s",
//                imeiNo, commandId, output1, output2, output3, output4, output4));
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_URL_SEND_COMMAND, handler));
    }

    public static void sendCommandParam(String imeiNo, int commandId, String param, int type,
                                        Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ImeiNo", imeiNo);
        jsonObject.addProperty("commandid", commandId);
        jsonObject.addProperty("param", param);
        jsonObject.addProperty("type", type);
        String url = String.format(TConstant.URL_SEND_COMMAND, ServerUtils.getNewServiceIp());
        String jsonString = mGson.toJson(jsonObject);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        RequestBody requestBody = new FormBody.Builder()
                .add("trackerid", imeiNo)
                .add("command", String.valueOf(commandId))
                .add("param", param)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_URL_SEND_COMMAND, handler));
    }

    public static void getCommandResult(long commandid, int type, int count, Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("commandid", commandid);
        jsonObject.addProperty("type", type);
        jsonObject.addProperty("count", count);
        String url = String.format(TConstant.URL_GET_COMMAND_RESULT, ServerUtils.getNewServiceIp());
        String jsonString = mGson.toJson(jsonObject);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
//        RequestBody requestBody = new FormBody.Builder()
//                .add("commandid ", String.valueOf(commandid))
//                .build();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/x-www-form" +
                "-urlencoded;"), String.format("commandid=%s", commandid));
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_URL_GET_COMMAND_RESULT, handler));
    }

    public static void addGeoFence(AAAUserModel userModel, String deviceImei, GeoFenceBean bean,
                                   Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("deviceImei", deviceImei);
        jsonObject.addProperty("fenceName", bean.getFenceName());
        jsonObject.addProperty("latitude", bean.getLatitude());
        jsonObject.addProperty("longitude", bean.getLongitude());
        jsonObject.addProperty("radius", bean.getRadius());
        jsonObject.addProperty("inOut", 1);
        String url = String.format(TConstant.URL_ADD_GEO_FENCE, ServerUtils.getNewServiceIp());
        String jsonString = mGson.toJson(jsonObject);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        jsonObject.addProperty("userId", userModel.getUserId());
        jsonObject.addProperty("token", userModel.getToken());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, mGson.toJson(jsonObject)));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_URL_ADD_GEO_FENCE, handler));
    }

    public static void updateGeoFence(AAAUserModel userModel, GeoFenceBean bean, Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("fenceId", bean.getFenceId());
        jsonObject.addProperty("fenceName", bean.getFenceName());
        jsonObject.addProperty("latitude", bean.getLatitude());
        jsonObject.addProperty("longitude", bean.getLongitude());
        jsonObject.addProperty("radius", bean.getRadius());
        jsonObject.addProperty("inOut", 1);
        String url = String.format(TConstant.URL_UPDATE_GEO_FENCE, ServerUtils.getNewServiceIp());
        String jsonString = mGson.toJson(jsonObject);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        jsonObject.addProperty("userId", userModel.getUserId());
        jsonObject.addProperty("token", userModel.getToken());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, mGson.toJson(jsonObject)));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_URL_UPDATE_GEO_FENCE, handler));
    }

    public static void delGeoFence(AAAUserModel userModel, long fenceId, Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("fenceId", fenceId);
        String url = String.format(TConstant.URL_DEL_GEO_FENCE, ServerUtils.getNewServiceIp());
        String jsonString = mGson.toJson(jsonObject);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        jsonObject.addProperty("userId", userModel.getUserId());
        jsonObject.addProperty("token", userModel.getToken());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, mGson.toJson(jsonObject)));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_URL_DEL_GEO_FENCE, handler));
    }

    public static void getGeoFenceList(AAAUserModel userModel, String deviceImei, Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("deviceImei", deviceImei);
        String url = String.format(TConstant.URL_GET_GEO_FENCE_LIST, ServerUtils.getNewServiceIp());
        String jsonString = mGson.toJson(jsonObject);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        jsonObject.addProperty("userId", userModel.getUserId());
        jsonObject.addProperty("token", userModel.getToken());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, mGson.toJson(jsonObject)));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_URL_GET_GEO_FENCE_LIST, handler));
    }

    public static void changePassword(AAAUserModel userModel, String oldPwd, String newPwd,
                                      Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", userModel.getUserId());
        jsonObject.addProperty("oldPassword", oldPwd);
        jsonObject.addProperty("newPassword", newPwd);
        String url = String.format(TConstant.URL_CHANGE_PASSWORD, ServerUtils.getNewServiceIp());
        String jsonString = mGson.toJson(jsonObject);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        jsonObject.addProperty("userId", userModel.getUserId());
        jsonObject.addProperty("token", userModel.getToken());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, mGson.toJson(jsonObject)));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_URL_CHANGE_PASSWORD, handler));
    }

    public static void getAlarmList(long userId, String imeiNo, String starttime, String endtime,
                                    int page,
                                    int rows, Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", userId);
        jsonObject.addProperty("ImeiNo", imeiNo);
        jsonObject.addProperty("starttime", starttime);
        jsonObject.addProperty("endtime", endtime);
        jsonObject.addProperty("page", page);
        jsonObject.addProperty("rows", rows);
        String url = String.format(TConstant.URL_GET_ALARM_LIST, ServerUtils.getNewServiceIp());
        String jsonString = mGson.toJson(jsonObject);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        RequestBody requestBody = new FormBody.Builder()
                .add("userid", String.valueOf(userId))
                .add("trackerid", imeiNo)
                .add("starttime", starttime)
                .add("endtime", endtime)
                .add("page", String.valueOf(page))
                .add("rows", String.valueOf(rows))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(TConstant.CONNECT_TIMEOUT, TimeUnit.SECONDS) // 设置连接超时时间
                .readTimeout(TConstant.READ_TIMEOUT, TimeUnit.SECONDS) // 设置读取超时时间
                .build();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_URL_GET_ALARM_LIST, handler));
    }

    public static void alarmCheck(long userId, String imeiNo, int freq, Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", userId);
        jsonObject.addProperty("ImeiNo", imeiNo);
        jsonObject.addProperty("freq", freq);
        String url = String.format(TConstant.URL_GET_ALARM_LIST, ServerUtils.getNewServiceIp());
        String jsonString = mGson.toJson(jsonObject);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        RequestBody requestBody = new FormBody.Builder()
                .add("userid", String.valueOf(userId))
                .add("trackerid", imeiNo)
                .add("freq", String.valueOf(freq))
                .add("page", "1")
                .add("rows", "100")
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(TConstant.CONNECT_TIMEOUT, TimeUnit.SECONDS) // 设置连接超时时间
                .readTimeout(TConstant.READ_TIMEOUT, TimeUnit.SECONDS) // 设置读取超时时间
                .build();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_URL_GET_ALARM_LIST, handler));
    }

    public static void getTripList(String imeiNo, String startTime, String endTime, int page,
                                   int rows, Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ImeiNo", imeiNo);
        jsonObject.addProperty("starttime", startTime);
        jsonObject.addProperty("endtime", endTime);
        jsonObject.addProperty("page", page);
        jsonObject.addProperty("rows", rows);
        String url = String.format(TConstant.URL_GET_TRIP_LIST, ServerUtils.getNewServiceIp());
        String jsonString = mGson.toJson(jsonObject);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
//        RequestBody requestBody = RequestBody.create(MediaType.parse("application/x-www-form" +
//                "-urlencoded;"), String.format
//                ("trackerid=%s&starttime=%s&endtime=%s&page=%s&rows" +
//                        "=%s",
//                imeiNo, startTime, endTime, page, rows));
        RequestBody requestBody = new FormBody.Builder()
                .add("trackerid", imeiNo)
                .add("starttime", startTime)
                .add("endtime", endTime)
                .add("page", String.valueOf(page))
                .add("rows", String.valueOf(rows))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(TConstant.CONNECT_TIMEOUT, TimeUnit.SECONDS) // 设置连接超时时间
                .readTimeout(TConstant.READ_TIMEOUT, TimeUnit.SECONDS) // 设置读取超时时间
                .build();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_URL_GET_TRIP_LIST, handler));
    }

    public static void getPicList(String imeiNo, Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ImeiNo", imeiNo);
        String url = String.format(TConstant.URL_GET_PIC_LIST, ServerUtils.getNewServiceIp());
        String jsonString = mGson.toJson(jsonObject);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        RequestBody requestBody = new FormBody.Builder()
                .add("imeino", imeiNo)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_URL_GET_PIC_LIST, handler));
    }

    public static void getTrackSumList(String imeiNo, String startTime, String endTime, int page,
                                       int rows, Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ImeiNo", imeiNo);
        jsonObject.addProperty("starttime", startTime);
        jsonObject.addProperty("endtime", endTime);
        jsonObject.addProperty("page", page);
        jsonObject.addProperty("rows", rows);
        String url = String.format(TConstant.URL_GET_TRACK_SUM_LIST, ServerUtils.getNewServiceIp());
        String jsonString = mGson.toJson(jsonObject);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        RequestBody requestBody = new FormBody.Builder()
                .add("trackerid", imeiNo)
                .add("starttime", startTime)
                .add("endtime", endTime)
                .add("page", String.valueOf(page))
                .add("rows", String.valueOf(rows))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(TConstant.CONNECT_TIMEOUT, TimeUnit.SECONDS) // 设置连接超时时间
                .readTimeout(TConstant.READ_TIMEOUT, TimeUnit.SECONDS) // 设置读取超时时间
                .build();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_URL_GET_TRACK_SUM_LIST, handler));
    }

    public static void getCustomerList(AAAUserModel userModel, Handler handler) {
        JsonObject jsonObject = new JsonObject();
        String url = String.format(TConstant.URL_GET_CUSTOMER_LIST, ServerUtils.getNewServiceIp());
        jsonObject.addProperty("userId", userModel.getUserId());
        jsonObject.addProperty("token", userModel.getToken());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, mGson.toJson(jsonObject)));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .get()
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_URL_GET_CUSTOMER_LIST, handler));
    }

    public static void sendRemoteControl(AAAUserModel userModel, String deviceImei, int cmdValue,
                                         Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("deviceImei", deviceImei);
        jsonObject.addProperty("cmdValue", String.valueOf(cmdValue));
//        String url = String.format(TConstant.URL_SEND_REMOTE_CONTROL, ServerUtils
//        .getNewServiceIp());
        String url = String.format(TConstant.URL_SEND_REMOTE_CONTROL,
                ServerUtils.getNewServiceIp());
        String jsonString = mGson.toJson(jsonObject);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        jsonObject.addProperty("userId", userModel.getUserId());
        jsonObject.addProperty("token", userModel.getToken());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, mGson.toJson(jsonObject)));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_URL_SEND_REMOTE_CONTROL, handler));
    }

    public static void setTimeIntervalForTracking(AAAUserModel userModel, String deviceImei,
                                                  int interval, Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("deviceImei", deviceImei);
        jsonObject.addProperty("interval", String.valueOf(interval));
        String url = String.format(TConstant.URL_SET_TIME_INTERVAL_FOR_TRACKING,
                ServerUtils.getNewServiceIp());
        String jsonString = mGson.toJson(jsonObject);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        jsonObject.addProperty("userId", userModel.getUserId());
        jsonObject.addProperty("token", userModel.getToken());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, mGson.toJson(jsonObject)));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_URL_SET_TIME_INTERVAL_FOR_TRACKING, handler));
    }

    public static void setTimeZone(AAAUserModel userModel, String deviceImei,
                                   int timeZoneValue, Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("deviceImei", deviceImei);
        jsonObject.addProperty("timeZoneValue", String.valueOf(timeZoneValue));
        String url = String.format(TConstant.URL_SET_TIME_ZONE,
                ServerUtils.getNewServiceIp());
        String jsonString = mGson.toJson(jsonObject);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        jsonObject.addProperty("userId", userModel.getUserId());
        jsonObject.addProperty("token", userModel.getToken());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, mGson.toJson(jsonObject)));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_URL_SET_TIME_ZONE, handler));
    }

    public static void setAngleForTracking(AAAUserModel userModel, String deviceImei,
                                           int angle, Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("deviceImei", deviceImei);
        jsonObject.addProperty("angle", String.valueOf(angle));
        String url = String.format(TConstant.URL_SET_ANGLE_FOR_TRACKING,
                ServerUtils.getNewServiceIp());
        String jsonString = mGson.toJson(jsonObject);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        jsonObject.addProperty("userId", userModel.getUserId());
        jsonObject.addProperty("token", userModel.getToken());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, mGson.toJson(jsonObject)));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_URL_SET_ANGLE_FOR_TRACKING, handler));
    }

    public static void setOdometerInterval(AAAUserModel userModel, String deviceImei,
                                           int distance, Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("deviceImei", deviceImei);
        jsonObject.addProperty("distance", String.valueOf(distance));
        String url = String.format(TConstant.URL_SET_ODOMETER_FOR_INTERVAL,
                ServerUtils.getNewServiceIp());
        String jsonString = mGson.toJson(jsonObject);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        jsonObject.addProperty("userId", userModel.getUserId());
        jsonObject.addProperty("token", userModel.getToken());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, mGson.toJson(jsonObject)));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_URL_SET_ODOMETER_FOR_INTERVAL, handler));

    }

    public static void setTelePhoneForWiretapping(AAAUserModel userModel, String deviceImei,
                                                  String phoneNumber, Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("deviceImei", deviceImei);
        jsonObject.addProperty("phoneNumber", phoneNumber);
        String url = String.format(TConstant.URL_SET_TELEPHONE_FOR_WIRETAPPING,
                ServerUtils.getNewServiceIp());
        String jsonString = mGson.toJson(jsonObject);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        jsonObject.addProperty("userId", userModel.getUserId());
        jsonObject.addProperty("token", userModel.getToken());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, mGson.toJson(jsonObject)));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_URL_SET_TELEPHONE_FOR_WIRETAPPING, handler));
    }

    public static void setSpeedLimit(AAAUserModel userModel, String deviceImei,
                                     int speed, Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("deviceImei", deviceImei);
        jsonObject.addProperty("speed", String.valueOf(speed));
        String url = String.format(TConstant.URL_SET_SPEED_LIMIT,
                ServerUtils.getNewServiceIp());
        String jsonString = mGson.toJson(jsonObject);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        jsonObject.addProperty("userId", userModel.getUserId());
        jsonObject.addProperty("token", userModel.getToken());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, mGson.toJson(jsonObject)));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_URL_SET_SPEED_LIMIT, handler));
    }

    public static void setMovementAlert(AAAUserModel userModel, String deviceImei,
                                        int meters, Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("deviceImei", deviceImei);
        jsonObject.addProperty("meters", String.valueOf(meters));
        String url = String.format(TConstant.URL_SET_MOVEMENT_ALERT,
                ServerUtils.getNewServiceIp());
        String jsonString = mGson.toJson(jsonObject);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        jsonObject.addProperty("userId", userModel.getUserId());
        jsonObject.addProperty("token", userModel.getToken());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, mGson.toJson(jsonObject)));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_URL_SET_MOVEMENT_ALERT, handler));
    }

    public static void setAuthorizedPhoneNumber(AAAUserModel userModel, String deviceImei,
                                                int buttonNumber, String phoneNumberForReceiveSms,
                                                String phoneNumberForReceiveCall, Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("deviceImei", deviceImei);
        jsonObject.addProperty("buttonNumber", String.valueOf(buttonNumber));
        jsonObject.addProperty("phoneNumberForReceiveSms", phoneNumberForReceiveSms);
        jsonObject.addProperty("phoneNumberForReceiveCall", phoneNumberForReceiveCall);
        String url = String.format(TConstant.URL_SET_AUTHORIZED_PHONE_NUMBER,
                ServerUtils.getNewServiceIp());
        String jsonString = mGson.toJson(jsonObject);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        jsonObject.addProperty("userId", userModel.getUserId());
        jsonObject.addProperty("token", userModel.getToken());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, mGson.toJson(jsonObject)));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_URL_SET_AUTHORIZED_PHONE_NUMBER, handler));
    }

    public static void requestSingleLocation(AAAUserModel userModel, String deviceImei,
                                             Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("deviceImei", deviceImei);
        String url = String.format(TConstant.URL_REQUEST_SINGLE_LOCATION,
                ServerUtils.getNewServiceIp());
        String jsonString = mGson.toJson(jsonObject);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        jsonObject.addProperty("userId", userModel.getUserId());
        jsonObject.addProperty("token", userModel.getToken());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, mGson.toJson(jsonObject)));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_URL_REQUEST_SINGLE_LOCATION, handler));
    }

    public static void readDeviceVersion(AAAUserModel userModel, String deviceImei,
                                         Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("deviceImei", deviceImei);
        String url = String.format(TConstant.URL_READ_DEVICE_VERSION,
                ServerUtils.getNewServiceIp());
        String jsonString = mGson.toJson(jsonObject);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        jsonObject.addProperty("userId", userModel.getUserId());
        jsonObject.addProperty("token", userModel.getToken());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, mGson.toJson(jsonObject)));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_URL_READ_DEVICE_VERSION, handler));
    }

    public static void readIntervalForTracking(AAAUserModel userModel, String deviceImei,
                                               Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("deviceImei", deviceImei);
        String url = String.format(TConstant.URL_READ_INTERVAL_FOR_TRACKING,
                ServerUtils.getNewServiceIp());
        String jsonString = mGson.toJson(jsonObject);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        jsonObject.addProperty("userId", userModel.getUserId());
        jsonObject.addProperty("token", userModel.getToken());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, mGson.toJson(jsonObject)));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_URL_READ_INTERVAL_FOR_TRACKING, handler));
    }

    public static void readOdometerForTracking(AAAUserModel userModel, String deviceImei,
                                               Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("deviceImei", deviceImei);
        String url = String.format(TConstant.URL_READ_ODOMETER_FOR_TRACKING,
                ServerUtils.getNewServiceIp());
        String jsonString = mGson.toJson(jsonObject);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        jsonObject.addProperty("userId", userModel.getUserId());
        jsonObject.addProperty("token", userModel.getToken());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, mGson.toJson(jsonObject)));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_URL_READ_ODOMETER_FOR_TRACKING, handler));
    }

    public static void getOdometerReport(AAAUserModel userModel, String deviceImei, String startTime
            , String endTime, long nextId, int requestRows, Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("deviceImei", deviceImei);
        jsonObject.addProperty("startDate", startTime);
        jsonObject.addProperty("endDate", endTime);
        jsonObject.addProperty("nextId", nextId);
        jsonObject.addProperty("requestRows", requestRows);
        String url = String.format(TConstant.URL_GET_ODOMETER_REPORT,
                ServerUtils.getNewServiceIp());
        String jsonString = mGson.toJson(jsonObject);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        jsonObject.addProperty("userId", userModel.getUserId());
        jsonObject.addProperty("token", userModel.getToken());
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, mGson.toJson(jsonObject)));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_URL_GET_ODOMETER_REPORT, handler));
    }

    /**
     * 获取已解绑列表
     *
     * @param handler 消息处理
     */
    public static void queryUnboundList(AAAUserModel userModel, Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "binddevicehistoryAction");
        jsonObject.addProperty("method", "selectUnBindBinddevicehistoryList");
        jsonObject.addProperty("parameter", userModel.getUserId());
        String url = String.format(TConstant.URL_MAIN, ServerUtils.getNewServiceIp());
        String jsonString = mGson.toJson(jsonObject);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, mGson.toJson(jsonObject)));
        Request request = new Request.Builder()
                .addHeader("clientFlag", "1")
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_QUERY_UNUNBOUND_LIST, handler));
    }


    /**
     * 激活设备
     *
     * @param imei       设备IMEI
     * @param activated  激活 1.激活
     * @param expireDate 有效期
     * @param userModel  用户对象
     * @param handler    消息处理
     */
    public static void activatedDevice(String imei, int activated, long expireDate,
                                       AAAUserModel userModel, Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "deviceAction");
        jsonObject.addProperty(TConstant.METHOD, "activated");
        JsonObject dataJsonObject = new JsonObject();
        dataJsonObject.addProperty("device_imei", imei);
        dataJsonObject.addProperty("activated", activated);
        dataJsonObject.addProperty("expireDate", expireDate);
        jsonObject.add(TConstant.DATA, dataJsonObject);

        String jsonString = mGson.toJson(jsonObject);
        String url = String.format(TConstant.URL_MAIN, ServerUtils.getNewServiceIp());
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        jsonObject.addProperty("deviceImei", imei);
        jsonObject.addProperty("type", activated);
        jsonObject.addProperty("expireDate", expireDate);
        Request request = new Request.Builder()
                .addHeader("clientFlag", "1")
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_ACTIVATED_DEVICE, handler));
    }

    /**
     * 更新有效期
     *
     * @param imei       设备IMEI
     * @param expireDate 有效期
     * @param userModel  用户对象
     * @param handler    消息处理
     */
    public static void updateExpireDate(String imei, long expireDate,
                                        AAAUserModel userModel, Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "deviceAction");
        jsonObject.addProperty(TConstant.METHOD, "expireDate");
        JsonObject dataJsonObject = new JsonObject();
        dataJsonObject.addProperty("device_imei", imei);
        dataJsonObject.addProperty("expireDate", expireDate);
        jsonObject.add(TConstant.DATA, dataJsonObject);

        String jsonString = mGson.toJson(jsonObject);
        String url = String.format(TConstant.URL_MAIN, ServerUtils.getNewServiceIp());
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        jsonObject.addProperty("deviceImei", imei);
        jsonObject.addProperty("expireDate", expireDate);
        Request request = new Request.Builder()
                .addHeader("clientFlag", "1")
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_UPDATE_EXPIRE_DATE, handler));
    }

    /**
     * 更新保修期
     *
     * @param imei          设备IMEI
     * @param guaranteeDate 有效期
     * @param userModel     用户对象
     * @param handler       消息处理
     */
    public static void updateGuaranteeDate(String imei, long guaranteeDate,
                                           AAAUserModel userModel, Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "deviceAction");
        jsonObject.addProperty(TConstant.METHOD, "guaranteeDate");
        JsonObject dataJsonObject = new JsonObject();
        dataJsonObject.addProperty("deviceImei", imei);
        dataJsonObject.addProperty("guaranteeDate", guaranteeDate);
        jsonObject.add(TConstant.DATA, dataJsonObject);

        String jsonString = mGson.toJson(jsonObject);
        String url = String.format(TConstant.URL_MAIN, ServerUtils.getNewServiceIp());
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        jsonObject.addProperty("deviceImei", imei);
        jsonObject.addProperty("expireDate", guaranteeDate);
        Request request = new Request.Builder()
                .addHeader("clientFlag", "1")
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_UPDATE_GUARANTEE_DATE, handler));
    }

    /**
     * 获取设备定位点
     *
     * @param userModel   用户对象
     * @param deviceImei  多个设备的IMEI
     * @param startTime   开始时间
     * @param endTime     结束时间
     * @param nextId      id
     * @param requestRows 获取数据个数
     * @param handler     消息处理
     */
    public static void getMultipleLastConfigDeviceTraceLog(AAAUserModel userModel,
                                                           String deviceImei, String startTime,
                                                           String endTime, long nextId,
                                                           int requestRows,
                                                           Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("deviceImei", deviceImei);
        jsonObject.addProperty("startTime", startTime);
        jsonObject.addProperty("endTime", endTime);
        jsonObject.addProperty("nextId", nextId);
        jsonObject.addProperty("requestRows", requestRows);
        String url = String.format(TConstant.URL_GET_MULTIPLE_DEVICE_TRACE_LOG,
                ServerUtils.getNewServiceIp());
        String jsonString = mGson.toJson(jsonObject);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application" +
                "/json; charset=utf-8"), jsonString);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, mGson.toJson(jsonObject)));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(TConstant.CONNECT_TIMEOUT, TimeUnit.SECONDS) // 设置连接超时时间
                .readTimeout(TConstant.READ_TIMEOUT, TimeUnit.SECONDS) // 设置读取超时时间
                .build();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_URL_GET_MULTIPLE_DEVICE_TRACE_LOG, handler));
    }

    /**
     * 设置定位间隔
     *
     * @param userModel    用户对象
     * @param deviceImei   设备的IMEI
     * @param positionType 定位类型
     * @param interval     定位间隔
     * @param handler      消息处理
     */
    public static void setPositionInterval(AAAUserModel userModel, String deviceImei,
                                           int positionType, Integer interval, Handler handler) {
        JsonObject jsonObject = new JsonObject();
        JsonObject dataObject = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "gpsCommandAction");
        jsonObject.addProperty(TConstant.METHOD, "setPositionInterval");
        dataObject.addProperty(TConstant.DEVICE_IMEI, deviceImei);
        dataObject.addProperty("receiveCommand", "COMMAND_UPLOAD_INTERVAL");
        dataObject.addProperty("positionType", positionType);
        if (interval != null) {
            dataObject.addProperty("positionInterval", interval);
        }
        jsonObject.add(TConstant.DATA, dataObject);
        String jsonString = mGson.toJson(jsonObject);
        String url = String.format(TConstant.DEFAULT_URL, ServerUtils.getNewServiceIp());
        RequestBody requestBody = RequestBody.create(jsonString, JSON_MEDIA_TYPE);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_COMMAND_UPLOAD_INTERVAL, handler));
    }

    /**
     * 重启设备
     *
     * @param userModel  用户对象
     * @param deviceImei 设备的IMEI
     * @param handler    消息处理
     */
    public static void restartDevice(AAAUserModel userModel, String deviceImei,
                                     Handler handler) {
        JsonObject jsonObject = new JsonObject();
        JsonObject dataObject = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "gpsCommandAction");
        jsonObject.addProperty(TConstant.METHOD, "send");
        dataObject.addProperty(TConstant.DEVICE_IMEI, deviceImei);
        dataObject.addProperty("receiveCommand", "COMMAND_RESTART");
        jsonObject.add(TConstant.DATA, dataObject);
        String jsonString = mGson.toJson(jsonObject);
        String url = String.format(TConstant.DEFAULT_URL, ServerUtils.getNewServiceIp());
        RequestBody requestBody = RequestBody.create(jsonString, JSON_MEDIA_TYPE);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_COMMAND_RESTART, handler));
    }

    /**
     * 关机设备
     *
     * @param userModel  用户对象
     * @param deviceImei 设备的IMEI
     * @param handler    消息处理
     */
    public static void shutDownDevice(AAAUserModel userModel, String deviceImei,
                                      Handler handler) {
        JsonObject jsonObject = new JsonObject();
        JsonObject dataObject = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "gpsCommandAction");
        jsonObject.addProperty(TConstant.METHOD, "send");
        dataObject.addProperty(TConstant.DEVICE_IMEI, deviceImei);
        dataObject.addProperty("receiveCommand", "COMMAND_SHUTDOWN");
        jsonObject.add(TConstant.DATA, dataObject);
        String jsonString = mGson.toJson(jsonObject);
        String url = String.format(TConstant.DEFAULT_URL, ServerUtils.getNewServiceIp());
        RequestBody requestBody = RequestBody.create(jsonString, JSON_MEDIA_TYPE);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_COMMAND_SHUT_DOWN, handler));
    }

    /**
     * 立即定位设备
     *
     * @param userModel  用户对象
     * @param deviceImei 设备的IMEI
     * @param handler    消息处理
     */
    public static void immedialLocationDevice(AAAUserModel userModel, String deviceImei,
                                              Handler handler) {
        JsonObject jsonObject = new JsonObject();
        JsonObject dataObject = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "gpsCommandAction");
        jsonObject.addProperty(TConstant.METHOD, "send");
        dataObject.addProperty(TConstant.DEVICE_IMEI, deviceImei);
        dataObject.addProperty("receiveCommand", "COMMAND_IMMEDIAL_LOCATION");
        jsonObject.add(TConstant.DATA, dataObject);
        String jsonString = mGson.toJson(jsonObject);
        String url = String.format(TConstant.DEFAULT_URL, ServerUtils.getNewServiceIp());
        RequestBody requestBody = RequestBody.create(jsonString, JSON_MEDIA_TYPE);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_COMMAND_IMMEDIAL_LOCATION, handler));
    }

    /**
     * 查找设备
     *
     * @param userModel  用户对象
     * @param deviceImei 设备的IMEI
     * @param handler    消息处理
     */
    public static void findDevice(AAAUserModel userModel, String deviceImei,
                                  Handler handler) {
        JsonObject jsonObject = new JsonObject();
        JsonObject dataObject = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "gpsCommandAction");
        jsonObject.addProperty(TConstant.METHOD, "send");
        dataObject.addProperty(TConstant.DEVICE_IMEI, deviceImei);
        dataObject.addProperty("receiveCommand", "COMMAND_FIND");
        jsonObject.add(TConstant.DATA, dataObject);
        String jsonString = mGson.toJson(jsonObject);
        String url = String.format(TConstant.DEFAULT_URL, ServerUtils.getNewServiceIp());
        RequestBody requestBody = RequestBody.create(jsonString, JSON_MEDIA_TYPE);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_COMMAND_FIND, handler));
    }

    /**
     * 停止查找设备
     *
     * @param userModel  用户对象
     * @param deviceImei 设备的IMEI
     * @param handler    消息处理
     */
    public static void findDeviceEnd(AAAUserModel userModel, String deviceImei,
                                     Handler handler) {
        JsonObject jsonObject = new JsonObject();
        JsonObject dataObject = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "gpsCommandAction");
        jsonObject.addProperty(TConstant.METHOD, "send");
        dataObject.addProperty(TConstant.DEVICE_IMEI, deviceImei);
        dataObject.addProperty("receiveCommand", "COMMAND_FIND_END");
        jsonObject.add(TConstant.DATA, dataObject);
        String jsonString = mGson.toJson(jsonObject);
        String url = String.format(TConstant.DEFAULT_URL, ServerUtils.getNewServiceIp());
        RequestBody requestBody = RequestBody.create(jsonString, JSON_MEDIA_TYPE);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_COMMAND_FIND_END, handler));
    }

    /**
     * 设置设备 备注信息
     *
     * @param userModel  用户对象
     * @param deviceImei 设备的IMEI
     * @param remark     备注
     * @param handler    消息处理
     */
    public static void updateDeviceRemark(AAAUserModel userModel, String deviceImei,
                                          String remark, Handler handler) {
        JsonObject jsonObject = new JsonObject();
        JsonObject dataObject = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "deviceAction");
        jsonObject.addProperty(TConstant.METHOD, "updateDeviceRemark");
        dataObject.addProperty(TConstant.DEVICE_IMEI, deviceImei);
        dataObject.addProperty("deviceRemark", remark);
        jsonObject.add(TConstant.DATA, dataObject);
        String jsonString = mGson.toJson(jsonObject);
        String url = String.format(TConstant.DEFAULT_URL, ServerUtils.getNewServiceIp());
        RequestBody requestBody = RequestBody.create(jsonString, JSON_MEDIA_TYPE);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_UPDATE_DEVICE_REMARK, handler));
    }

    /**
     * 发送指令
     *
     * @param userModel  用户对象
     * @param deviceImei 设备的IMEI
     * @param command    指令
     * @param handler    消息处理
     */
    public static void sendCommand(AAAUserModel userModel, String deviceImei, String command,
                                   Handler handler) {
        JsonObject jsonObject = new JsonObject();
        JsonObject dataObject = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "gpsCommandAction");
        jsonObject.addProperty(TConstant.METHOD, "send");
        dataObject.addProperty(TConstant.DEVICE_IMEI, deviceImei);
        dataObject.addProperty("receiveCommand", command);
        jsonObject.add(TConstant.DATA, dataObject);
        String jsonString = mGson.toJson(jsonObject);
        String url = String.format(TConstant.DEFAULT_URL, ServerUtils.getNewServiceIp());
        RequestBody requestBody = RequestBody.create(jsonString, JSON_MEDIA_TYPE);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_SEND_COMMAND, handler));
    }

    /**
     * 获取多个设备轨迹数据
     *
     * @param userModel 用户对象
     * @param jsonArray 多个设备的数据
     * @param handler   消息处理
     */
    public static void getMultipleDeviceTraceLog(AAAUserModel userModel, JsonArray jsonArray,
                                                 Handler handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("requestList", jsonArray);
        String jsonString = mGson.toJson(jsonObject);
        String url = String.format(TConstant.URL_GET_MULTIPLE_TRACE_LOG,
                ServerUtils.getNewServiceIp());
        RequestBody requestBody = RequestBody.create(jsonString, JSON_MEDIA_TYPE);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(TConstant.CONNECT_TIMEOUT, TimeUnit.SECONDS) // 设置连接超时时间
                .readTimeout(TConstant.READ_TIMEOUT, TimeUnit.SECONDS) // 设置读取超时时间
                .build();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_GET_MULTIPLE_TRACE_LOG, handler));
    }

    /**
     * 查询发送指令列表
     *
     * @param userModel  用户对象
     * @param deviceImei 设备的IMEI
     * @param pageIndex  查询页数
     * @param pageSize   查询数量
     * @param handler    消息处理
     */
    public static void querySendCommandList(AAAUserModel userModel, String deviceImei,
                                            int pageIndex, int pageSize,
                                            Handler handler) {
        JsonObject jsonObject = new JsonObject();
        JsonObject dataObject = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "gpsCommandAction");
        jsonObject.addProperty(TConstant.METHOD, "query");
        jsonObject.addProperty("pageIndex", pageIndex);
        jsonObject.addProperty("pageSize", pageSize);
        if (TextUtils.isEmpty(deviceImei)) {
            dataObject.addProperty("userId", userModel.getUserId());
        } else {
            dataObject.addProperty(TConstant.DEVICE_IMEI, deviceImei);
        }
        dataObject.addProperty("commandMode", 1);
        jsonObject.add(TConstant.DATA, dataObject);
        String jsonString = mGson.toJson(jsonObject);
        String url = String.format(TConstant.DEFAULT_URL, ServerUtils.getNewServiceIp());
        RequestBody requestBody = RequestBody.create(jsonString, JSON_MEDIA_TYPE);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_QUERY_SEND_COMMAND_LIST, handler));
    }

    /**
     * 获取蓝牙设备配置
     *
     * @param userModel  用户对象
     * @param xmlJson    数据
     * @param macAddress mac地址
     * @param handler    消息处理
     */
    public static void getBleDeviceConfig(AAAUserModel userModel, String xmlJson,
                                          String macAddress, Handler handler) {
        JsonObject dataObject = new JsonObject();
        dataObject.addProperty("topicName", BLEDataUtils.BLE_REQUERT_CONFIG);
        dataObject.addProperty("parameter", xmlJson);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "requestCconfigAction");
        jsonObject.addProperty(TConstant.METHOD, "Execute");
        jsonObject.add(TConstant.DATA, dataObject);
        String jsonString = mGson.toJson(jsonObject);
        String url = String.format(TConstant.DEFAULT_URL, TConstant.GPS_IP);
        RequestBody requestBody = RequestBody.create(jsonString, JSON_MEDIA_TYPE);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        jsonObject.addProperty("macAddress", macAddress);
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_GET_BLE_DEVICE_CONFIG, handler));
    }

    /**
     * 发送蓝牙设备定位数据
     *
     * @param userModel  用户对象
     * @param xmlJson    数据
     * @param macAddress mac地址+比赛id
     * @param locationId 定位数据id
     * @param handler    消息处理
     */
    public static void sendBleDeviceLocation(AAAUserModel userModel, String xmlJson,
                                             String macAddress, String locationId,
                                             Handler handler) {
        JsonObject dataObject = new JsonObject();
        dataObject.addProperty("topicName", BLEDataUtils.BLE_UPLOAD_LOCATION);
        dataObject.addProperty("parameter", xmlJson);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "dataInstanceAction");
        jsonObject.addProperty(TConstant.METHOD, "Execute");
        jsonObject.add(TConstant.DATA, dataObject);
        String jsonString = mGson.toJson(jsonObject);
        String url = String.format(TConstant.DEFAULT_URL, TConstant.GPS_IP);
        RequestBody requestBody = RequestBody.create(jsonString, JSON_MEDIA_TYPE);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        jsonObject.addProperty("macAddress", macAddress);
        jsonObject.addProperty("locationId", locationId);
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_SEND_BLE_DEVICE_LOCATION, handler));
    }

    /**
     * 设置个人模式 raceStatus =0，比赛模式 raceStatus=1
     *
     * @param userModel  用户对象
     * @param deviceImei 设备IMEI
     * @param raceStatus 个人模式 raceStatus =0，比赛模式 raceStatus=1
     * @param handler    消息处理
     */
    public static void updateRaceStatus(AAAUserModel userModel, String deviceImei,
                                        String raceStatus,
                                        Handler handler) {
        JsonObject dataObject = new JsonObject();
        dataObject.addProperty("deviceImei", deviceImei);
        dataObject.addProperty("raceStatus", raceStatus);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TConstant.ACTION, "deviceAction");
        jsonObject.addProperty(TConstant.METHOD, "uploadDeviceByRaceStatus");
        jsonObject.addProperty("userId", String.valueOf(userModel.getUserId()));
        jsonObject.addProperty("token", userModel.getToken());
        jsonObject.add(TConstant.DATA, dataObject);
        String jsonString = mGson.toJson(jsonObject);
//        String url = String.format(TConstant.DEFAULT_URL, TConstant.GPS_IP);
        String url = String.format(TConstant.DEFAULT_URL, ServerUtils.getNewServiceIp());
        RequestBody requestBody = RequestBody.create(jsonString, JSON_MEDIA_TYPE);
        if (BuildConfig.DEBUG)
            KLog.d(String.format("%s  json:%s", url, jsonString));
        jsonObject.addProperty("deviceImei", deviceImei);
        jsonObject.addProperty("type", "1".equals(raceStatus) ? 1 : 0);
        Request request = new Request.Builder()
                .addHeader("userId", String.valueOf(userModel.getUserId()))
                .addHeader("token", userModel.getToken())
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(getCallback(jsonObject,
                TConstant.REQUEST_UPDATE_RACE_STATUS, handler));
    }

    private static Callback getCallback(JsonObject jsonObject, int type, Handler handler) {
        return new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (BuildConfig.DEBUG)
                    KLog.d("onFailure: " + e.getMessage());
                handleNetError(type, jsonObject, handler);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    ResponseBody body = response.body();
                    if (BuildConfig.DEBUG)
                        KLog.d(String.format("请求结果 code:%s", response.code()));
                    if (response.code() == 200 && body != null) {
                        String bodyString = body.string();
                        if (BuildConfig.DEBUG)
                            KLog.d(String.format("请求结果 string:%s", bodyString));
                        if (TextUtils.isEmpty(bodyString))
                            handleNetError(type, jsonObject, handler);
                        else {
                            AAABaseResponseBean responseBean = mGson.fromJson(bodyString,
                                    AAABaseResponseBean.class);
                            if ((responseBean.getCode() == TConstant.RESPONSE_TOKEN_IS_EXPIRED
                                    || responseBean.getCode() == TConstant.RESPONSE_TOKEN_IS_EMPTY
                                    || responseBean.getCode() == TConstant.RESPONSE_TOKEN_IS_ERROR
                                    || responseBean.getCode() == TConstant.RESPONSE_USER_NOT_LOGIN)
                                    && SettingSPUtils.getInstance().getLong(TConstant.USER_ID_NEW
                                    , -1) != -1) {
                                SettingSPUtils.getInstance().putString(CWConstant.TOKEN, "");
                                SettingSPUtils.getInstance().putLong(CWConstant.U_ID, -1);
                                MainApplication.getInstance().setTrackDeviceModel(null);
                                MainApplication.getInstance().getTrackDeviceList().clear();
                                MainApplication.getInstance().setTrackUserModel(null);
                                Activity activity =
                                        ActivityManagerUtils.getInstance().currentActivity();
                                activity.runOnUiThread(() -> XToastUtils.toast(R.string.token_error_prompt));
                                Intent intent = new Intent(activity, LoginActivity.class);
                                if (intent.resolveActivity(activity.getPackageManager()) != null)
                                    activity.startActivity(intent);
                                ActivityManagerUtils.getInstance().popAllActivity();
                            } else {
                                responseBean.setRequestObject(jsonObject);
                                handler.sendMessage(handler.obtainMessage(type, responseBean));
                            }
                        }
                    } else
                        handleNetError(type, jsonObject, handler);
                } catch (Exception e) {
                    if (BuildConfig.DEBUG)
                        e.printStackTrace();
                    handleNetError(type, jsonObject, handler);
                }
            }
        };
    }

    private static void handleNetError(int type, JsonObject jsonObject, Handler handler) {
        if (handler != null) {
            AAABaseResponseBean responseBean = new AAABaseResponseBean();
            responseBean.setCode(TConstant.RESPONSE_NET_ERROR);
            responseBean.setRequestObject(jsonObject);
            handler.sendMessage(handler.obtainMessage(type, responseBean));
        }
    }
}
