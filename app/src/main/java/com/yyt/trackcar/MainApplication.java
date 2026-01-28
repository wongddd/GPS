package com.yyt.trackcar;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.polidea.rxandroidble2.RxBleClient;
import com.polidea.rxandroidble2.exceptions.BleException;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.socks.library.KLog;
import com.tot.badges.IconBadgeNumManager;
import com.yyt.trackcar.bean.BLEConfigModel;
import com.yyt.trackcar.bean.BLEDataModel;
import com.yyt.trackcar.bean.PostMessage;
import com.yyt.trackcar.dbflow.AAADeviceModel;
import com.yyt.trackcar.dbflow.AAAUserModel;
import com.yyt.trackcar.dbflow.BleDeviceModel;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.utils.BLEDataUtils;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.LanguageUtils;
import com.yyt.trackcar.utils.SettingSPUtils;
import com.yyt.trackcar.utils.TConstant;
import com.yyt.trackcar.utils.TimeUtils;
import com.yyt.trackcar.utils.TypefaceUtil;
import com.yyt.trackcar.utils.sdkinit.XBasicLibInit;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;
import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.reactivex.exceptions.UndeliverableException;
import io.reactivex.plugins.RxJavaPlugins;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar
 * @ fileName:      MainApplication
 * @ author:        QING
 * @ createTime:    2020-02-25 15:12
 * @ describe:      TODO 主程序入口
 */
public class MainApplication extends MultiDexApplication {
    @SuppressLint("StaticFieldLeak")
    private static MainApplication mMainApplication; // 主程序
    private UserModel mUserModel; // 用户对象
    private List<DeviceModel> mDeviceList = new ArrayList<>(); // 设备列表
    private DeviceModel mDeviceModel; // 当前选中设备

    private AAAUserModel mAAAUserModel; // 用户信息
    private List<AAADeviceModel> mTrackDeviceList = new ArrayList<>(); // 设备列表
    private Map<String, String> mDeviceTypeMap = new HashMap<>(); // 设备类型Map
    private long messageId; // 消息编号
    private int selectIndex; // 选中设备
    private AAADeviceModel mAAADeviceModel;
    private RxBleClient mBleClient; // 蓝牙连接
//    private MqttAndroidClient mMqttClient; // mqtt连接
//    private boolean mFirstConnect; // 第一次连接成功
    //    private BLEDataModel mBLEDataModel; // 蓝牙数据对象
//    private final Map<String, BLEDataModel> mBLEDataMap = new HashMap<>(); // 蓝牙数据对象Map
//    private final Map<String, String> mMqttConnectMap = new HashMap<>(); // mqtt连接Map
//    private String mBleMacAddress; // mqtt连接的蓝牙设备

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        mMainApplication = this;
        initLibs();

        TypefaceUtil.replaceSystemDefaultFont(this, "fonts/arial.ttf");

        String language = SettingSPUtils.getInstance().getString(CWConstant.LANGUAGE, "");
        if (TextUtils.isEmpty(language)) {
            if ("zh".equals(Locale.getDefault().getLanguage())) {
                if ("CN".equals(Locale.getDefault().getCountry()))
                    language = "zh";
                else
                    language = "tw";
            } else
                language = Locale.getDefault().getLanguage();
        }
        LanguageUtils.changeAppLanguage(context, language);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //解决4.x运行崩溃的问题
        MultiDex.install(this);
    }

    /**
     * 单例模式
     */
    public static MainApplication getInstance() {
        synchronized (MainApplication.class) {
            if (mMainApplication == null) {
                mMainApplication = new MainApplication();
            }
        }
        return mMainApplication;
    }

    /**
     * 初始化基础库
     */
    private void initLibs() {
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            return;
//        }
//        LeakCanary.install(this);
        // This instantiates DBFlow
        FlowManager.init(this);
        XBasicLibInit.init(this);

//        XUpdateInit.init(this);

        //ANR监控å
//        ANRWatchDogInit.init();

        mBleClient = RxBleClient.create(this);
//        RxBleClient.updateLogOptions(new LogOptions.Builder()
//                .setLogLevel(LogConstants.INFO)
//                .setMacAddressLogSetting(LogConstants.MAC_ADDRESS_FULL)
//                .setUuidsLogSetting(LogConstants.UUIDS_FULL)
//                .setShouldLogAttributeValues(false)
//                .build()
//        );
        RxJavaPlugins.setErrorHandler(throwable -> {
            if (throwable instanceof UndeliverableException && throwable.getCause() instanceof BleException) {
                KLog.d("Suppressed UndeliverableException: " + throwable);
                return; // ignore BleExceptions as they were surely delivered at least once
            }
            // add other custom handlers if needed
            throw new RuntimeException("Unexpected Throwable in RxJavaPlugins error handler",
                    throwable);
        });

        // 解决android api Q layout/abc_screen_simple出错
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            ViewPump.init(ViewPump.builder()
                    .addInterceptor(new CalligraphyInterceptor(
                            new CalligraphyConfig.Builder()
                                    .setDefaultFontPath(null)
                                    .setFontAttrId(R.attr.fontPath)
                                    .build()))
                    .build());
    }

//    /**
//     * 初始化mqtt
//     *
//     * @return MqttAndroidClient
//     */
//    public void getMqttClient(String macAddress, String type) {
//        closeMqttClient();
//        mMqttConnectMap.put(macAddress, type);
//        mBleMacAddress = macAddress;
//        String imei = BLEDataUtils.convertMacAddress(macAddress);
//        mMqttClient = new MqttAndroidClient(this, BLEDataUtils.SERVER_URI, imei);
//        mMqttClient.setCallback(new MqttCallbackExtended() {
//            @Override
//            public void connectComplete(boolean reconnect, String serverURI) {
//                if (reconnect) {
//                    KLog.d("Reconnected to : " + serverURI);
//                    // Because Clean Session is true, we need to re-subscribe
////                    subscribeToTopic();
//                } else {
//                    KLog.d("Connected to: " + serverURI);
//                }
//            }
//
//            @Override
//            public void connectionLost(Throwable cause) {
//                KLog.d("The Connection was lost.");
//            }
//
//            @Override
//            public void messageArrived(String topic, MqttMessage message) throws Exception {
//                KLog.d("Incoming message: " + new String(message.getPayload()));
//            }
//
//            @Override
//            public void deliveryComplete(IMqttDeliveryToken token) {
//                KLog.d("IdeliveryComplete");
//            }
//        });
//        connectMqtt(macAddress, imei);
////        return mMqttClient;
//    }
//
//    /**
//     * 连接mqtt
//     */
//    private void connectMqtt(String macAddress, String imei) {
//        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
//        mqttConnectOptions.setAutomaticReconnect(true);
//        mqttConnectOptions.setCleanSession(true);
//        mqttConnectOptions.setUserName(BLEDataUtils.BLE_USER_NAME);
//        mqttConnectOptions.setPassword(BLEDataUtils.BLE_PASSWROD);
//        try {
//            mMqttClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
//                @Override
//                public void onSuccess(IMqttToken asyncActionToken) {
//                    DisconnectedBufferOptions disconnectedBufferOptions =
//                            new DisconnectedBufferOptions();
//                    disconnectedBufferOptions.setBufferEnabled(true);
//                    disconnectedBufferOptions.setBufferSize(100);
//                    disconnectedBufferOptions.setPersistBuffer(false);
//                    disconnectedBufferOptions.setDeleteOldestMessages(false);
//                    mMqttClient.setBufferOpts(disconnectedBufferOptions);
//                    String type = mMqttConnectMap.get(macAddress);
//                    if ("1".equals(type)) {
//                        mMqttConnectMap.remove(macAddress);
//                        initMqttSubscribe(macAddress, imei);
//                    } else if ("2".equals(type)) {
//                        mMqttConnectMap.remove(macAddress);
//                    } else if ("3".equals(type)) {
//                        mMqttConnectMap.remove(macAddress);
//                        BleDeviceModel model = new BleDeviceModel();
//                        model.setMacAddress(macAddress);
//                        EventBus.getDefault().post(model);
//                    }
//                }
//
//                @Override
//                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
//                }
//            });
//        } catch (MqttException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 初始化MQTT数据接收
//     */
//    private void initMqttSubscribe(String macAddress, String imei) {
//        if (!mBLEDataMap.containsKey(macAddress)) {
//            MqttMessage model = new MqttMessage();
//            model.setPayload(String.format("<request>\n" +
//                    "<device>%s</device>\n" +
//                    "<ver>33</ver>\n" +
//                    "</request>", imei).getBytes());
//            model.setQos(1);
//            try {
//                if (BuildConfig.DEBUG) {
//                    KLog.d(String.format("%s%s", BLEDataUtils.BLE_TOPIC_NAME,
//                            imei));
//                }
//                mMqttClient.subscribe(String.format("%s%s", BLEDataUtils.BLE_TOPIC_NAME,
//                        imei), 1, new IMqttMessageListener() {
//                    @Override
//                    public void messageArrived(String topic, MqttMessage message) throws Exception {
//                        // message Arrived!
//                        XmlToJson xmlToJson =
//                                new XmlToJson.Builder(new String(message.getPayload())).build();
//                        BLEDataModel dataModel =
//                                new Gson().fromJson(xmlToJson.toString(),
//                                        BLEDataModel.class);
//                        BLEDataModel bleDataModel = new BLEDataModel();
//                        bleDataModel.setMacAddress(macAddress);
//                        if (dataModel != null && dataModel.getConfig() != null) {
//                            BLEConfigModel configModel = dataModel.getConfig();
//                            bleDataModel.setMatchId(configModel.getRid());
//                            Date date;
//                            if (!TextUtils.isEmpty(configModel.getRsut())) {
//                                date = TimeUtils.formatUTC(configModel.getRsut(), null);
//                                if (date != null) {
//                                    bleDataModel.setStartTime(date.getTime() / 1000);
//                                }
//                            }
//                            date = TimeUtils.formatUTC(configModel.getNmst(), "HH:mm:ss");
//                            if (date != null) {
//                                bleDataModel.setNightTime(date.getTime() / 1000);
//                            }
//                            date = TimeUtils.formatUTC(configModel.getNmet(), "HH:mm:ss");
//                            if (date != null) {
//                                bleDataModel.setContinuedFlyTime(date.getTime() / 1000);
//                            }
//                            bleDataModel.setLowBattery(configModel.getLpl());
//                            bleDataModel.setDelayTime(configModel.getRsud());
//                            bleDataModel.setForcedStartup(1);
//                            bleDataModel.setStartLocationInterval(configModel.getRgli());
//                            bleDataModel.setNightLocationInterval(configModel.getNgli());
//                            bleDataModel.setContinuedFlyLocationInterval(configModel.getCdui());
//                            bleDataModel.setLowBatteryLocationInterval(configModel.getLpgli());
//                            if (BuildConfig.DEBUG) {
//                                KLog.d("bleDataModel: " + new Gson().toJson(bleDataModel));
//                            }
//                            mBLEDataMap.put(macAddress, bleDataModel);
//                        }
//                        EventBus.getDefault().post(bleDataModel);
//                        if (BuildConfig.DEBUG) {
//                            KLog.d("Message: " + topic + " : " + new String(message.getPayload
//                                    ()));
//                        }
//                    }
//                });
//                mMqttClient.publish(BLEDataUtils.BLE_REQUERT_CONFIG, model);
//            } catch (MqttException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    /**
//     * 关闭连接
//     */
//    public void closeMqttClient(){
//        if (mMqttClient != null) {
//            mMqttClient.close();
//        }
//    }

    /**
     * In practice you will use some kind of dependency injection pattern.
     */
    public static RxBleClient getBleClient(Context context) {
        if (context == null)
            return MainApplication.getInstance().mBleClient;
        MainApplication application = (MainApplication) context.getApplicationContext();
        return application.mBleClient;
    }

    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess :
                activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return "";
    }

    /**
     * 创建通知通道
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // 通知渠道的id
            String id = "1";
            // 用户可以看到的通知渠道的名字.
            CharSequence name = getString(R.string.app_name);
            // 用户可以看到的通知渠道的描述
            String description = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            // 配置通知渠道的属性
            mChannel.setDescription(description);
            // 设置通知出现时的闪灯（如果 android 设备支持的话）
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            // 设置通知出现时的震动（如果 android 设备支持的话）
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            //最后在notificationmanager中创建该通知渠道
            mNotificationManager.createNotificationChannel(mChannel);

            // 通知渠道的id
            id = CWConstant.CHANNEL_DEVICE_MESSAGE_ID;
            // 用户可以看到的通知渠道的名字.
            name = getString(R.string.notification_name);
            // 用户可以看到的通知渠道的描述
            description = getString(R.string.notification_description);
            importance = NotificationManager.IMPORTANCE_HIGH;
            mChannel = new NotificationChannel(id, name, importance);
            // 配置通知渠道的属性
            mChannel.setDescription(description);
            // 设置通知出现时的闪灯（如果 android 设备支持的话）
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            // 设置通知出现时的震动（如果 android 设备支持的话）
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            //最后在notificationmanager中创建该通知渠道
            mNotificationManager.createNotificationChannel(mChannel);
        }
//        BasicCustomPushNotification notification = new BasicCustomPushNotification();
//        notification.setRemindType(BasicCustomPushNotification
//                .REMIND_TYPE_VIBRATE_AND_SOUND);
//        notification.setBuildWhenAppInForeground(false);//设置当推送到达时如果应用处于前台不创建通知
//        boolean res =
//                CustomNotificationBuilder.getInstance().setCustomNotification(1, notification);
    }

    /**
     * 设置桌面角标
     *
     * @param count 数量
     */
    public void setIconBadgeNum(int count) {
        try {
            new IconBadgeNumManager().setIconBadgeNum(this, null, count);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取用户对象
     *
     * @return 返回用户对象
     */
    public UserModel getUserModel() {
        return mUserModel;
    }

    /**
     * 设置用户对象
     *
     * @param mUserModel 用户对象
     */
    public void setUserModel(UserModel mUserModel) {
        this.mUserModel = mUserModel;
    }

    /**
     * 获取设备列表
     *
     * @return 返回设备列表
     */
    public List<DeviceModel> getDeviceList() {
        return mDeviceList;
    }

    /**
     * 设置设备列表
     *
     * @param mDeviceList 设备列表
     */
    public void setDeviceList(List<DeviceModel> mDeviceList) {
        this.mDeviceList = mDeviceList;
    }

    /**
     * 获取设备
     *
     * @return 返回设备
     */
    public DeviceModel getDeviceModel() {
        return mDeviceModel;
    }

    /**
     * 设置设备
     *
     * @param mDeviceModel 设备
     */
    public void setDeviceModel(DeviceModel mDeviceModel) {
        if (mDeviceModel != null) {
            SettingSPUtils.getInstance().putInt(CWConstant.DEVICE_MODEL,
                    mDeviceModel.getDevice_type());
            int deviceType = SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0);
            switch (mDeviceModel.getDevice_type()) {
                case CWConstant.DEVICE_TYPE_S6:
                case CWConstant.DEVICE_TYPE_S9:
                    SettingSPUtils.getInstance().putInt(CWConstant.DEVICE_TYPE, 1);
                    if (deviceType != 1)
                        EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_CHANGE_DEVICE_TYPE));
                    break;
                case CWConstant.DEVICE_TYPE_L08:
                case CWConstant.DEVICE_TYPE_L09:
                case CWConstant.DEVICE_TYPE_L10:
                case CWConstant.DEVICE_TYPE_L11:
                case CWConstant.DEVICE_TYPE_L12S:
                default:
                    SettingSPUtils.getInstance().putInt(CWConstant.DEVICE_TYPE, 0);
                    if (deviceType != 0)
                        EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_CHANGE_DEVICE_TYPE));
                    break;
            }
        }
        this.mDeviceModel = mDeviceModel;
    }

    /**
     * @return 当前app是否是调试开发模式
     */
    public static boolean isDebug() {
        return BuildConfig.DEBUG;

    }


    public AAAUserModel getTrackUserModel() {
        return mAAAUserModel;
    }

    public void setTrackUserModel(AAAUserModel mAAAUserModel) {
        this.mAAAUserModel = mAAAUserModel;
    }

    public List<AAADeviceModel> getTrackDeviceList() {
        return mTrackDeviceList;
    }

    public void setTrackDeviceList(List<AAADeviceModel> mTrackDeviceList) {
        this.mTrackDeviceList.clear();
        this.mTrackDeviceList.addAll(mTrackDeviceList);
    }

    /**
     * 获取当前设备对象
     */
    public AAADeviceModel getTrackDeviceModel() {
        return mAAADeviceModel;
    }

    /**
     * 设置设备
     *
     * @param mAAADeviceModel 设备
     */
    public void setTrackDeviceModel(AAADeviceModel mAAADeviceModel) {
        if (mAAADeviceModel != null) {
            SettingSPUtils.getInstance().put(TConstant.TRACK_DEVICE_MODEL, mAAADeviceModel);
            SettingSPUtils.getInstance().putInt(CWConstant.DEVICE_MODEL,
                    mAAADeviceModel.getDeviceType());
            String imei = mAAADeviceModel.getDeviceImei();
            if (!TextUtils.isEmpty(imei)) {
                int type = mAAADeviceModel.getDeviceType();
                mDeviceTypeMap.put(imei, String.valueOf(type));
            }
//            List<AAADeviceModel> deviceModels = getTrackDeviceList();
//            for (AAADeviceModel item :
//                    deviceModels) {
//
//            }
        }
        this.mAAADeviceModel = mAAADeviceModel;
    }

    public long getMessageId() {
        return messageId++;
    }

    public Map<String, String> getDeviceTypeMap() {
        return mDeviceTypeMap;
    }

//    public Map<String, BLEDataModel> getBLEDataMap() {
//        return mBLEDataMap;
//    }
//
//    public MqttAndroidClient getMqttClient() {
//        return mMqttClient;
//    }
//
//    public String getBleMacAddress() {
//        return mBleMacAddress;
//    }

}
