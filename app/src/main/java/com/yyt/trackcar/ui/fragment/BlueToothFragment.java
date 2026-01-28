package com.yyt.trackcar.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.polidea.rxandroidble2.RxBleClient;
import com.polidea.rxandroidble2.RxBleConnection;
import com.polidea.rxandroidble2.RxBleDevice;
import com.polidea.rxandroidble2.scan.IsConnectable;
import com.polidea.rxandroidble2.scan.ScanFilter;
import com.polidea.rxandroidble2.scan.ScanResult;
import com.polidea.rxandroidble2.scan.ScanSettings;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.socks.library.KLog;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xutil.data.ConvertTools;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.MainApplication;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAABaseResponseBean;
import com.yyt.trackcar.bean.AAARequestBean;
import com.yyt.trackcar.bean.BLEConfigModel;
import com.yyt.trackcar.bean.BLEDataModel;
import com.yyt.trackcar.bean.BLEItemModel;
import com.yyt.trackcar.bean.BLELocationDataModel;
import com.yyt.trackcar.bean.BLELocationModel;
import com.yyt.trackcar.bean.BLELocationRowModel;
import com.yyt.trackcar.bean.BLELocationSubDataModel;
import com.yyt.trackcar.bean.BLEProcessModel;
import com.yyt.trackcar.dbflow.AAAUserModel;
import com.yyt.trackcar.dbflow.BleDeviceModel;
import com.yyt.trackcar.dbflow.BleLocationModel;
import com.yyt.trackcar.dbflow.BleLocationModel_Table;
import com.yyt.trackcar.ui.adapter.BlueToothItemAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.BLEDataUtils;
import com.yyt.trackcar.utils.BLEUtils;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.DialogUtils;
import com.yyt.trackcar.utils.PermissionUtils;
import com.yyt.trackcar.utils.TConstant;
import com.yyt.trackcar.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import fr.arnaudguyon.xmltojsonlib.JsonToXml;
import fr.arnaudguyon.xmltojsonlib.XmlToJson;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      BlueToothFragment
 * @ author:        QING
 * @ createTime:    2023/4/18 11:21
 * @ describe:      TODO 蓝牙页面
 */
@Page(name = "BlueTooth")
public class BlueToothFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener {
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView; // RecyclerView
    private TitleBar mTitleBar;
    private BlueToothItemAdapter mAdapter; // 适配器
    private final List<BLEItemModel> mItemList = new ArrayList<>(); // 列表
    private final Map<String, String> mMap = new HashMap<>();
    private final Map<String, Disposable> mStateMap = new HashMap<>();
    private final Map<String, Disposable> mConnectionMap = new HashMap<>();
    private final Map<String, Disposable> mNotifyMap = new HashMap<>();
    private final Map<String, Disposable> mReadMap = new HashMap<>();
    private final Map<String, RxBleConnection> mBLEConnectionMap = new HashMap<>();
    private final Map<String, BLEProcessModel> mProcessMap = new HashMap<>();
    private final Map<String, BLEItemModel> mBLEItemMap = new HashMap<>();
    private final Map<String, String> mUploadMap = new HashMap<>();

    private RxBleClient mBleClient;
    private Disposable mScanDisposable;
    private Disposable mFlowDisposable;
    private Timer mTimer; // 计时器
//    private boolean isUploading; // 正在上传定位数据
//    private boolean isConnectioning; // 正在连接蓝牙

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 注册订阅者
//        EventBus.getDefault().register(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_blue_tooth;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
//        titleBar.setTitle(R.string.blue_tooth);
        titleBar.setTitle(String.format("%s%s", getString(R.string.pet_real_time),
                getString(R.string.blue_tooth)));
        titleBar.addAction(new TitleBar.TextAction(getString(R.string
                .blue_tooth_data_check)) {

            @Override
            public void performAction(View view) {
                List<BleLocationModel> list = SQLite.select().from(BleLocationModel.class)
                        .queryList();
                DialogUtils.customMaterialDialog(getContext(), mMaterialDialog,
                        getString(R.string.prompt), getString(R.string.blue_tooth_data_prompt,
                                list.size()),
                        getString(R.string.confirm));
            }

        });
//        titleBar.addAction(new TitleBar.ImageAction(R.drawable.ic_note) {
//            @Override
//            public void performAction(View view) {
//                KLog.d("mqtt connect state:" + mMqttClient.isConnected());
//                try {
//                    MqttMessage message = new MqttMessage();
////                    message.setPayload(("<data>\n" +
////                            "<device>867585332200486</device>\n" +
////                            "<rows>\n" +
////                            "<row>\n" +
////                            "<time>2023-03-14 09:43:31</time>\n" +
////                            "<ldt>1</ldt>\n" +
////                            "<ld>121.484928|25.285599</ld>\n" +
////                            "<ed>37.400000</ed>\n" +
////                            "<pl>100</pl>\n" +
////                            "<st>0</st>\n" +
////                            "</row>\n" +
////                            "</rows>\n" +
////                            "<rid>8873</rid>\n" +
////                            "<tid>8873</tid>\n" +
////                            "</data>").getBytes());
//                    message.setPayload(("<request>\n" +
//                            "<device>BLEDevice</device>\n<ver>33</ver>\n" +
//                            "</request>").getBytes());
//                    message.setQos(1);
////                    mMqttClient.publish("/data-instance", message);
//                    mMqttClient.publish("/request-config", message);
//                } catch (MqttException e) {
//                    e.printStackTrace();
//                }

//                if (!BLEUtils.isEnableBLE()) {
//                    if (mBleClient.isScanRuntimePermissionGranted() && mBleClient
//                    .isConnectRuntimePermissionGranted()) {
//                        BLEUtils.enableBLE(mActivity);
//                    } else {
//                        PermissionUtils.requestBlePermission(mActivity, mBleClient);
//                    }
//                } else if (mScanDisposable != null) {
//                    mScanDisposable.dispose();
//                } else {
//                    mMap.clear();
//                    mItemList.clear();
//                    mAdapter.notifyDataSetChanged();
//                    if (mBleClient.isScanRuntimePermissionGranted() && mBleClient
//                    .isConnectRuntimePermissionGranted()) {
//                        scanBleDevices();
//                    } else {
//                        PermissionUtils.requestBlePermission(mActivity, mBleClient);
//                    }
//                }
//            }
//        });
        mTitleBar = titleBar;
        return titleBar;
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void initViews() {
        initAdapters();
        initRecyclerViews();
        initHeaderAndFooterView();
        initBLE();
        initTimer();
        uploadLoationData();
//        BLEDataModel mBLEDataModel = new BLEDataModel();
//        mBLEDataModel.setMatchId("20230213001");
//        mBLEDataModel.setStartTime(1676260988L);
//        mBLEDataModel.setNightTime(1676260988L);
//        mBLEDataModel.setContinuedFlyTime(1676260988L);
//        mBLEDataModel.setLowBattery(25);
//        mBLEDataModel.setDelayTime(60L);
//        mBLEDataModel.setForcedStartup(0);
//        mBLEDataModel.setStartLocationInterval(5);
//        mBLEDataModel.setNightLocationInterval(15);
//        mBLEDataModel.setContinuedFlyLocationInterval(10);
//        mBLEDataModel.setLowBatteryLocationInterval(30);
////        XStream xStream = new XStream();
////        KLog.d(mGson.toJson(xStream.fromXML("<request>" +
////                "<device>BLEDevice</device>" +
////                "<ver>33</ver>" +
////                "</request>")));
////        KLog.d(xStream.toXML(mBLEDataModel));
//        XmlToJson xmlToJson = new XmlToJson.Builder("<request>\n" +
//                "<device>BLEDevice</device>\n" +
//                "<ver>33</ver>\n" +
//                "</request>")
//                .build();
//        KLog.d(xmlToJson.toString());
//        JsonToXml jsonToXml = new JsonToXml.Builder(mGson.toJson(mBLEDataModel)).build();
//        KLog.d(jsonToXml.toString());

//        BLELocationDataModel locationDataModel = new BLELocationDataModel();
//        BLELocationSubDataModel locationSubDataModel = new BLELocationSubDataModel();
//        locationDataModel.setData(locationSubDataModel);
//        List<BLELocationModel> list = new ArrayList<>();
//        locationSubDataModel.setRows(list);
//        BLELocationModel model = new BLELocationModel();
//        model.setRid("BLE1");
//        model.setDevice("BLEName1");
//        model.setTime(1676260988L);
//        model.setLd("123|55");
//        model.setPl(10);
//        model.setEd(100);
//        list.add(model);
//        model = new BLELocationModel();
//        model.setRid("BLE2");
//        model.setDevice("BLEName2");
//        model.setTime(1676260988L);
//        model.setLd("123|55");
//        model.setPl(10);
//        model.setEd(100);
//        list.add(model);
//        model = new BLELocationModel();
//        model.setRid("BLE3");
//        model.setDevice("BLEName3");
//        model.setTime(1676260988L);
//        model.setLd("123|55");
//        model.setPl(10);
//        model.setEd(100);
//        list.add(model);
//        try {
//            locationSubDataModel.setDevice("FF:00:00:00:00:E3");
//            locationSubDataModel.setRid("41214");
//            JsonToXml jsonToXml = new JsonToXml.Builder(mGson.toJson(locationDataModel)).build();
//            String xmlString = jsonToXml.toString();
//            KLog.d("uploadLoationData:" + xmlString);
//        } catch (Exception e) {
//
//        }
    }

    /**
     * 初始化适配器
     */
    private void initAdapters() {
        mAdapter = new BlueToothItemAdapter(mItemList);
        mAdapter.setOnItemClickListener(this);
    }

    /**
     * 初始化ViewPager
     */
    private void initRecyclerViews() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 初始化头布局
     */
    private void initHeaderAndFooterView() {
        View headerView = getLayoutInflater().inflate(R.layout.item_space_section,
                mRecyclerView, false);
        View footerView = getLayoutInflater().inflate(R.layout.item_space_section,
                mRecyclerView, false);
        mAdapter.addHeaderView(headerView);
        mAdapter.addFooterView(footerView);
    }

    /**
     * 初始化蓝牙
     */
    private void initBLE() {
        mBleClient = MainApplication.getBleClient(mActivity);
        mFlowDisposable = mBleClient.observeStateChanges()
                .switchMap(state -> { // switchMap makes sure that if the state will change the
                    // rxBleClient.scanBleDevices() will dispose and thus end the scan
                    switch (state) {
                        case READY:
                            // everything should work
                            if (mScanDisposable != null)
                                mScanDisposable.dispose();
                            scanBleDevices();
                            if (BuildConfig.DEBUG) {
                                KLog.d("everything should work");
                            }
                            return Observable.empty();
                        case BLUETOOTH_NOT_AVAILABLE:
                            // basically no functionality will work here
                            if (BuildConfig.DEBUG) {
                                KLog.d(" basically no functionality will work here");
                            }
                            break;
                        case LOCATION_PERMISSION_NOT_GRANTED:
                            // scanning and connecting will not work
                            if (BuildConfig.DEBUG) {
                                KLog.d("scanning and connecting will not permission");
                            }
                            break;
                        case BLUETOOTH_NOT_ENABLED:
                            // scanning and connecting will not work
                            if (BuildConfig.DEBUG) {
                                KLog.d("scanning and connecting will not work");
                            }
                            break;
                        case LOCATION_SERVICES_NOT_ENABLED:
                            // scanning will not work
                            if (BuildConfig.DEBUG) {
                                KLog.d("scanning will not work");
                            }
                            break;
                        default:
                            if (BuildConfig.DEBUG) {
                                KLog.d("observeStateChanges other");
                            }
                            break;
                    }
                    mMap.clear();
                    mItemList.clear();
                    refreshRecyclerView();
                    return Observable.empty();
                })
                .subscribe(
                        rxBleScanResult -> {
                            // Process scan result here.
                        },
                        throwable -> {
                            // Handle an error here.
                        }
                );
        if (BLEUtils.isEnableBLE()) {
            if (mBleClient.isScanRuntimePermissionGranted() && mBleClient.isConnectRuntimePermissionGranted()) {
                scanBleDevices();
            } else {
                PermissionUtils.requestBlePermission(mActivity, mBleClient);
            }
        } else {
            if (mBleClient.isScanRuntimePermissionGranted() && mBleClient.isConnectRuntimePermissionGranted()) {
                BLEUtils.enableBLE(mActivity);
            } else {
                PermissionUtils.requestBlePermission(mActivity, mBleClient);
            }
        }
    }

    /**
     * 初始化计时器
     */
    private void initTimer() {
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                startConnectionBLE();
                uploadLoationData();
            }
        }, TimeUtils.MINUTE * 1000, TimeUtils.MINUTE * 1000);
    }

    /**
     * 刷新UI
     */
    @SuppressLint("NotifyDataSetChanged")
    private void refreshRecyclerView() {
        Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(() -> mAdapter.notifyDataSetChanged());
        }
    }

//    private void refreshMacAddressStatus(String macAddress, String status) {
//        for (BaseItemBean itemBean : mItemList) {
//            RxBleDevice bleDevice = (RxBleDevice) itemBean.getObject();
//            if (!TextUtils.isEmpty(macAddress) && macAddress.equals(bleDevice.getMacAddress())) {
//                itemBean.setGroup(status);
//                refreshRecyclerView();
//                break;
//            }
//        }
//    }

//    /**
//     * 刷新UI
//     */
//    private void refreshMacAddressStatus(String macAddress) {
//        for (SectionItem item : mItemList) {
//            BaseItemBean itemBean = item.t;
//            if (itemBean != null) {
//                RxBleDevice bleDevice = (RxBleDevice) itemBean.getObject();
//                if (!TextUtils.isEmpty(macAddress) && macAddress.equals(bleDevice.getMacAddress
//                ())) {
//                    initDeviceStateChanges(bleDevice, itemBean);
//                    break;
//                }
//            }
//        }
//    }

    /**
     * 扫描设备
     */
    private void scanBleDevices() {
        mTitleBar.setTitle(String.format("%s(%s)", getString(R.string.blue_tooth),
                getString(R.string.blue_tooth_searching)));
        List<RxBleDevice> connectedList = new ArrayList<>(mBleClient.getConnectedPeripherals());
        for (RxBleDevice bleDevice : connectedList) {
            if (BLEDataUtils.BLE_DEVICE_NAME.equals(bleDevice.getName())) {
                BLEUtils.unpairDevice(bleDevice.getBluetoothDevice());
            }
        }
        mScanDisposable = mBleClient.scanBleDevices(
                        new ScanSettings.Builder()
                                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                                .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                                .build(),
                        new ScanFilter.Builder()
                                .setDeviceName(BLEDataUtils.BLE_DEVICE_NAME)
//                            .setDeviceAddress("B4:99:4C:34:DC:8B")
                                // add custom filters if needed
                                .build()
                )
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {
                    mTitleBar.setTitle(R.string.blue_tooth);
                    mScanDisposable = null;
                })
                .subscribe(this::onScanSuccess, this::onScanFailure);
    }

    /**
     * 扫描成功
     *
     * @param bleScanResult 扫描的设备结果
     */
    private void onScanSuccess(ScanResult bleScanResult) {
        // Not the best way to ensure distinct devices, just for sake on the demo.
        RxBleDevice bleDevice = bleScanResult.getBleDevice();
        if (bleScanResult.isConnectable() != IsConnectable.NOT_CONNECTABLE && bleDevice != null) {
            String macAddress = bleDevice.getMacAddress();
            String status = mMap.get(macAddress);
            if (status == null || "0".equals(status)) {
                if ("0".equals(status)) {
                    for (int i = mItemList.size() - 1; i >= 0; i--) {
                        if (macAddress.equals(mItemList.get(i).getMacAddress())) {
                            mItemList.remove(i);
                            break;
                        }
                    }
                }
                mMap.put(macAddress, "1");
                String imei = BLEDataUtils.convertMacAddress(bleDevice.getMacAddress());
                BLEItemModel bleModel = new BLEItemModel();
                bleModel.setImei(imei);
                bleModel.setMacAddress(macAddress);
                bleModel.setBleDevice(bleDevice);
                bleModel.setStatus(getString(R.string.blue_tooth_status_first));

                BLEItemModel firstModel = null;
                if (mItemList.size() > 0) {
                    firstModel = mItemList.get(0);
                }
                if (firstModel == null || !mConnectionMap.containsKey(firstModel.getMacAddress())) {
                    mItemList.add(0, bleModel);
                } else {
                    mItemList.add(1, bleModel);
                }
                refreshRecyclerView();
                startConnectionBLE();
            }
//            KLog.d(bleScanResult.getBleDevice().getMacAddress() + "   " +
//                    bleScanResult.getScanRecord().getDeviceName() + "   " +
//                    bleScanResult.getBleDevice().getName() + "       " + bleScanResult.getRssi());
        }
//        KLog.d(bleScanResult.getBleDevice().getMacAddress() + "   " +
//                bleScanResult.getScanRecord().getDeviceName() + "   " +
//                bleScanResult.getBleDevice().getName() + "       " + bleScanResult.getRssi());
    }

    /**
     * 扫描失败
     *
     * @param throwable 错误原因
     */
    private void onScanFailure(Throwable throwable) {
//        if (throwable instanceof BleScanException) {
//            ScanExceptionHandler.handleException(mActivity, (BleScanException) throwable);
//        } else {
//            KLog.d("Scan failed", throwable);
//        }
    }

//    /**
//     * 初始化蓝牙设备监听状态
//     *
//     * @param bleDevice 蓝牙设备
//     * @param itemBean  列表对象
//     */
//    private void initDeviceStateChanges(RxBleDevice bleDevice, BaseItemBean itemBean) {
//        Disposable disposable = mStateMap.get(bleDevice.getMacAddress());
//        if (disposable == null) {
//            disposable = bleDevice.observeConnectionStateChanges()
//                    .doFinally(() -> {
//                        itemBean.setContent(bleDevice.getMacAddress());
//                        refreshRecyclerView();
//                        mStateMap.remove(bleDevice.getMacAddress());
//                    })
//                    .subscribe(connectionState -> {
//                                // Process your way.
//                                switch (connectionState) {
//                                    case CONNECTING: // 连接中
//                                        itemBean.setContent(String.format("%s(%s)",
//                                                bleDevice.getMacAddress(),
//                                                getString(R.string.blue_tooth_connecting)));
//                                        refreshRecyclerView();
//                                        break;
//                                    case CONNECTED: // 连接成功
//                                        itemBean.setContent(String.format("%s(%s)",
//                                                bleDevice.getMacAddress(),
//                                                getString(R.string.blue_tooth_connect_success)));
//                                        refreshRecyclerView();
//                                        break;
//                                    case DISCONNECTED: // 断开连接
//                                        itemBean.setContent(bleDevice.getMacAddress());
//                                        refreshRecyclerView();
//                                        break;
//                                    default:
//                                        break;
//                                }
////                                KLog.d("observeConnectionStateChanges:" + connectionState
////                                .toString());
//                            },
//                            throwable -> {
//                                // Handle an error here.
////                                KLog.d("observeConnectionStateChanges error");
//                            }
//                    );
//            mStateMap.put(bleDevice.getMacAddress(), disposable);
//        }
//    }

    /**
     * 开始连接蓝牙
     */
    private synchronized void startConnectionBLE() {
        try {
            for (int i = 0; i < mItemList.size(); i++) {
                BLEItemModel model = mItemList.get(i);
                RxBleDevice bleDevice = model.getBleDevice();
                String macAddress = bleDevice.getMacAddress();
                if (!TextUtils.isEmpty(macAddress)
                        && !"0".equals(mMap.get(macAddress))
                        && bleDevice.getConnectionState() == RxBleConnection.RxBleConnectionState.DISCONNECTED) {
                    int connectCount = mConnectionMap.values().size();
                    if (connectCount >= 5) {
                        return;
                    }
                    mItemList.remove(i);
                    mItemList.add(0, model);
                    mBLEItemMap.put(macAddress, model);
//                    mBleModel = model;
                    startConnectionBLEDevice(bleDevice, model);
                    if (BuildConfig.DEBUG) {
                        KLog.d("startConnectionBLEDevice:" + macAddress);
                    }
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始连接蓝牙设备
     *
     * @param bleDevice 蓝牙设备
     * @param itemModel 蓝牙子对象
     */
    @SuppressLint("NotifyDataSetChanged")
    private void startConnectionBLEDevice(RxBleDevice bleDevice, BLEItemModel itemModel) {
        String macAddress = itemModel.getMacAddress();
        Disposable connectionDisposable;
        if (mConnectionMap.containsKey(macAddress)) {
            connectionDisposable = mConnectionMap.get(macAddress);
            if (connectionDisposable != null)
                connectionDisposable.dispose();
        }
        connectionDisposable = bleDevice
                .establishConnection(false)
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {
//                        KLog.d("Connection doFinally");
                    Disposable disposable = mReadMap.get(macAddress);
//                        KLog.d(macAddress + " mReadMap:" + disposable.isDisposed());
                    if (disposable != null)
                        disposable.dispose();
                    disposable = mNotifyMap.get(macAddress);
//                        KLog.d(macAddress + " mNotifyMap:" + disposable.isDisposed());
                    if (disposable != null)
                        disposable.dispose();
                    disposable = mStateMap.get(macAddress);
                    if (disposable != null)
                        disposable.dispose();
                    mProcessMap.remove(macAddress);
                    mBLEConnectionMap.remove(macAddress);
                    mConnectionMap.remove(macAddress);
//                    isConnectioning = false;
                    for (int i = 0; i < mItemList.size(); i++) {
                        BLEItemModel model = mItemList.get(i);
                        if (macAddress.equals(model.getMacAddress())) {
                            mItemList.remove(i);
                            mMap.put(macAddress, "0");
                            model.setDisconnectTime(TimeUtils.formatUTC(System.currentTimeMillis(), null));
//                            if (model.getStatusType() <= 5) {
//                                model.setStatus(String.format("%s,%s", model.getStatus(),
//                                        getString(R.string.blue_tooth_status_fifth)));
//                            }
                            mItemList.add(model);
                            refreshRecyclerView();
                            break;
                        }
                    }
                    startConnectionBLE();
                })
                .subscribe(bleConnection -> {
//                            isConnectioning = true;
                            mBLEConnectionMap.put(macAddress, bleConnection);
                            BLEDataModel bleDataModel = itemModel.getConfigModel();
                            itemModel.setConnectTime(TimeUtils.formatUTC(System.currentTimeMillis(), null));
                            if (bleDataModel == null) {
                                itemModel.setStatus(String.format("%s(%s)",
                                        getString(R.string.blue_tooth_status_second),
                                        getString(R.string.blue_tooth_status_third)));
                                refreshRecyclerView();
//                                MainApplication.getInstance().getMqttClient(macAddress,
//                                        "1");
                                getBleDeviceConfig(macAddress);
                            } else {
                                initBLENotify(bleConnection, macAddress);
                            }
                        },
                        throwable -> {
//                                KLog.d(macAddress + " Connection error: " + throwable);
                        });
        mConnectionMap.put(macAddress, connectionDisposable);
    }

    /**
     * 初始化蓝牙设备通知
     *
     * @param bleConnection 蓝牙连接
     * @param macAddress    mac地址
     */
    private void initBLENotify(RxBleConnection bleConnection, String macAddress) {
        BLEItemModel itemModel = mBLEItemMap.get(macAddress);
        if (itemModel != null) {
            itemModel.setStatus(getString(R.string.blue_tooth_status_second));
            refreshRecyclerView();
        }
        Disposable notifyDisposable =
                bleConnection.setupNotification(BLEDataUtils.READ_CHAR_UUID)
                        .doFinally(() -> {
//                    KLog.d("setupNotification doFinally");
                        })
                        .subscribe(notificationObservable -> {
                            // Characteristic value confirmed.
                            initBLERead(bleConnection, notificationObservable, macAddress);
                        }, throwable -> {
                            // Handle an error here.
//                    KLog.d("setupNotification failed", throwable);
                        });
        mNotifyMap.put(macAddress, notifyDisposable);
    }

    /**
     * 初始化蓝牙接收
     *
     * @param bleConnection 蓝牙连接
     * @param observable    接收监听
     * @param macAddress    mac地址
     */
    private void initBLERead(RxBleConnection bleConnection, Observable<byte[]> observable,
                             String macAddress) {
        BLEProcessModel bleProcessModel = new BLEProcessModel();
        bleProcessModel.setMacAddress(macAddress);
        mProcessMap.put(macAddress, bleProcessModel);
        Disposable readDisposable = observable
                .doFinally(() -> {
//                    KLog.d("readDisposable doFinally");
                })
                .subscribe(bytes -> {
                            // Characteristic value // confirmed.
                            if (BuildConfig.DEBUG) {
                                KLog.d("readDisposable:" + ConvertTools.byteArrayToHexString(bytes));
                            }
                            if (bytes.length > 0) {
                                BLEProcessModel processModel;
                                BLEItemModel itemModel;
                                BLEDataModel bleDataModel = null;
                                switch (bytes[0]) {
                                    case 0x01: // 设备时间
                                        writeBLEData(bleConnection, macAddress, 0x02);
                                        break;
                                    case 0x02: // 比赛ID
                                        processModel = mProcessMap.get(macAddress);
                                        if (processModel != null && TextUtils.isEmpty(processModel.getMatchId())) {
                                            StringBuilder matchID = new StringBuilder();
                                            for (int i = 1; i < bytes.length; i++) {
                                                matchID.append((char) bytes[i]);
                                            }
                                            processModel.setMatchId(matchID.toString());
                                            if (BuildConfig.DEBUG) {
                                                KLog.d("获取的赛场ID：" + processModel.getMatchId());
                                            }
                                            writeBLEData(bleConnection, macAddress, 0x05);
                                        } else if (processModel != null) {
                                            writeBLEData(bleConnection, macAddress, 0x03);
                                        }
                                        break;
                                    case 0x03: // 设置比赛时间
                                        writeBLEData(bleConnection, macAddress, 0x04);
                                        break;
                                    case 0x04: // 设置工作模式
                                        writeBLEData(bleConnection, macAddress, 0x06);
                                        break;
                                    case 0x05: // 获取定位数据
                                        processModel = mProcessMap.get(macAddress);
//                                        BLEDataModel bleDataModel =
//                                                MainApplication.getInstance().getBLEDataMap()
//                                                .get(macAddress);
                                        itemModel = mBLEItemMap.get(macAddress);
                                        if (itemModel != null) {
                                            bleDataModel = itemModel.getConfigModel();
                                        }
                                        if (processModel != null && bleDataModel != null) {
                                            if (bytes.length == 1 && processModel.getMatchId().equals(bleDataModel.getMatchId())) {
                                                itemModel.setStatusType(5);
                                                itemModel.setLocationCount(processModel.getLocationCount());
                                                refreshRecyclerView();
                                                writeBLEData(bleConnection, macAddress, 0x06);
                                                if (processModel.getLocationCount() != 0) {
                                                    uploadLoationData();
                                                }
//                                                writeBLEData(bleConnection, macAddress, 0x06);
                                            } else if (bytes.length == 1) {
                                                writeBLEData(bleConnection, macAddress,
                                                        0x01);
                                                if (processModel.getLocationCount() != 0) {
                                                    uploadLoationData();
                                                }
//                                                writeBLEData(bleConnection, macAddress, 0x01);
                                            } else if (bytes.length == 19) { // 有定位点
                                                try {
                                                    BleDeviceModel bleDeviceModel =
                                                            new BleDeviceModel();
                                                    bleDeviceModel.setMacAddress(macAddress);
                                                    bleDeviceModel.setMatchId(processModel.getMatchId());
                                                    bleDeviceModel.save();

                                                    BleLocationModel locationModel =
                                                            new BleLocationModel();
                                                    String dataString =
                                                            ConvertTools.byteArrayToHexString(bytes);
                                                    if ("05FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF".equals(dataString)) {
                                                        writeBLEData(bleConnection, macAddress,
                                                                0xFF);
                                                        return;
                                                    }
                                                    locationModel.setMacAddress(macAddress);
                                                    locationModel.setMatchId(processModel.getMatchId());
                                                    locationModel.setLocationTime(Long.parseLong(dataString.substring(2, 10), 16) * 1000);
                                                    locationModel.setBattery(Integer.parseInt(dataString.substring(10, 12), 16));
                                                    locationModel.setLng(Float.intBitsToFloat(Integer.parseInt(dataString.substring(12, 20), 16)));
                                                    locationModel.setLat(Float.intBitsToFloat(Integer.parseInt(dataString.substring(20, 28), 16)));
                                                    locationModel.setAltitude(Integer.parseInt(dataString.substring(28, 32), 16));
                                                    locationModel.setSatellitesNum(Integer.parseInt(dataString.substring(32, 34), 16));
                                                    locationModel.setHeading(Integer.parseInt(dataString.substring(34, 38), 16));
                                                    locationModel.save();
                                                    if (BuildConfig.DEBUG) {
                                                        KLog.d("定位数据 " + macAddress + "   :" + mGson.toJson(locationModel));
                                                    }
                                                    processModel.setLocationCount(processModel.getLocationCount() + 1);
                                                    itemModel.setStatusType(5);
                                                    itemModel.setLocationCount(processModel.getLocationCount());
                                                    if (itemModel.getLocationCount() % 20 == 0) {
                                                        uploadLoationData();
                                                    }
                                                    refreshRecyclerView();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                writeBLEData(bleConnection, macAddress, 0x05);
                                            }
                                        }
                                        break;
                                    case 0x06: // 完成配置
                                        if (BuildConfig.DEBUG) {
                                            KLog.d("完成配置:" + macAddress);
                                        }
                                        mProcessMap.remove(macAddress);
                                        Disposable connectionDisposable =
                                                mConnectionMap.get(macAddress);
                                        if (connectionDisposable != null)
                                            connectionDisposable.dispose();
                                        break;
                                    case (byte) 0xFF: // 恢复出厂设置
//                                        if (macAddress.equals(MainApplication.getInstance()
//                                        .getBleMacAddress())) {
//                                            MainApplication.getInstance().closeMqttClient();
//                                        }
//                                        MainApplication.getInstance().getBLEDataMap().remove
//                                        (macAddress);
                                        itemModel = mBLEItemMap.get(macAddress);
                                        if (itemModel != null) {
                                            itemModel.setConfigModel(null);
                                            itemModel.setStatus(getString(R.string.blue_tooth_reset));
                                            itemModel.setStatusType(7);
                                        }
//                                        mBLEDataMap.remove(macAddress)
//                                        mBleModel.setStatus(getString(R.string.blue_tooth_reset));
//                                        mBleModel.setStatusType(7);
                                        refreshRecyclerView();
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }, throwable -> {
                            // Handle an error here.
//                            KLog.d("readDisposable failed" + throwable);
                        }
                );
        mReadMap.put(macAddress, readDisposable);
        writeBLEData(bleConnection, macAddress, 0x00);
    }

    /**
     * 发送蓝牙数据
     *
     * @param bleConnection 蓝牙连接
     * @param macAddress    mac地址
     * @param type          发送类型
     */
    private void writeBLEData(RxBleConnection bleConnection, String macAddress, int type) {
        byte[] byteArray;
        BLEItemModel itemModel;
        BLEDataModel bleDataModel = null;
        switch (type) {
            case 0x01: // 设备时间
                byteArray = BLEDataUtils.setDeviceTime(System.currentTimeMillis() / 1000);
                break;
            case 0x02: // 比赛ID
//                bleDataModel = MainApplication.getInstance().getBLEDataMap().get(macAddress);
                itemModel = mBLEItemMap.get(macAddress);
                if (itemModel != null) {
                    bleDataModel = itemModel.getConfigModel();
                }
                byteArray = BLEDataUtils.setMatchID((bleDataModel == null
                        || bleDataModel.getMatchId() == null) ? null :
                        bleDataModel.getMatchId());
                break;
            case 0x03: // 设置比赛时间
//                bleDataModel = MainApplication.getInstance().getBLEDataMap().get(macAddress);
                itemModel = mBLEItemMap.get(macAddress);
                if (itemModel != null) {
                    bleDataModel = itemModel.getConfigModel();
                }
                byteArray = BLEDataUtils.setMatchTime(bleDataModel);
                break;
            case 0x04: // 设置工作模式
//                bleDataModel = MainApplication.getInstance().getBLEDataMap().get(macAddress);
                itemModel = mBLEItemMap.get(macAddress);
                if (itemModel != null) {
                    bleDataModel = itemModel.getConfigModel();
                }
                byteArray = BLEDataUtils.setWorkMode(bleDataModel);
                break;
            case 0x05: // 获取定位数据
                BLEProcessModel processModel = mProcessMap.get(macAddress);
                if (processModel != null && processModel.getLocationCount() > 0) {
                    byteArray = BLEDataUtils.setLocationData((byte) 0x01);
                } else {
                    byteArray = BLEDataUtils.setLocationData((byte) 0x00);
                }
                break;
            case 0x06: // 完成配置
//                if (macAddress.equals(MainApplication.getInstance().getBleMacAddress())) {
//                    MainApplication.getInstance().closeMqttClient();
//                }
//                MainApplication.getInstance().getBLEDataMap().remove(macAddress);
                itemModel = mBLEItemMap.get(macAddress);
                if (itemModel != null) {
                    itemModel.setConfigModel(null);
                    itemModel.setStatus(getString(R.string.blue_tooth_status_fourth));
                    itemModel.setStatusType(6);
                    refreshRecyclerView();
                }
                byteArray = BLEDataUtils.completeConfig();
                break;
            case 0xFF: // 完成配置
                byteArray = BLEDataUtils.resetDevice();
                break;
            default: // 获取赛场ID
                byteArray = BLEDataUtils.setMatchID(null);
                break;
        }
        if (BuildConfig.DEBUG) {
            KLog.d("writeBLEData:" + ConvertTools.byteArrayToHexString(byteArray));
        }
        Disposable disposable = bleConnection.writeCharacteristic(BLEDataUtils.WRITE_CHAR_UUID,
                        byteArray)
                .subscribe(characteristicValue -> {
                    // Characteristic value confirmed.
//                    KLog.d("characteristicValue:" + ConvertTools.byteArrayToHexString
//                            (characteristicValue));
                }, throwable -> {
                    // Handle an error here.
//                    KLog.d("write failed" + throwable);
                });
    }


//    /**
//     * 上传定位数据
//     */
//    @SuppressLint("DefaultLocale")
//    private synchronized void uploadLoationData() {
////        KLog.d("mqtt connect state:" + mMqttClient.isConnected());
//        if (!isUploading) {
//            MqttAndroidClient mqttClient = null;
//            List<BleLocationModel> dataList = null;
//            String macAddress = null;
//            String matchId = null;
//            if (isConnectioning) {
//                if (mBleModel != null && !TextUtils.isEmpty(mBleModel.getMacAddress())) {
//                    macAddress = mBleModel.getMacAddress();
//                    BLEProcessModel processModel = mProcessMap.get(macAddress);
//                    if (processModel != null && processModel.isLocationComplete()
//                            && !TextUtils.isEmpty(processModel.getMatchId())) {
//                        mqttClient = MainApplication.getInstance().getMqttClient();
//                        if (mqttClient != null && mqttClient.isConnected()
//                                && macAddress.equals(MainApplication.getInstance()
//                                .getBleMacAddress())) {
//                            matchId = processModel.getMatchId();
//                            OperatorGroup operatorGroup =
//                                    OperatorGroup.clause(OperatorGroup.clause()
//                                            .and(BleLocationModel_Table.macAddress.eq(macAddress))
//                                            .and(BleLocationModel_Table.matchId.eq(matchId)));
//                            dataList = SQLite.select().from(BleLocationModel.class)
//                                    .where(operatorGroup)
//                                    .orderBy(BleLocationModel_Table.locationTime, false)
//                                    .limit(10)
//                                    .queryList();
//                            if (dataList.size() == 0) {
//                                SQLite.delete().from(BleDeviceModel.class)
//                                        .where(operatorGroup)
//                                        .execute();
//                                processModel.setLocationComplete(false);
//                                nextBleStep(macAddress, processModel.getMatchId());
//                            }
//                        } else {
//                            MainApplication.getInstance().getMqttClient(macAddress, "3");
//                        }
//                    }
//                }
//            } else {
//                mqttClient = MainApplication.getInstance().getMqttClient();
//                if (mqttClient != null && mqttClient.isConnected()) {
//                    macAddress = MainApplication.getInstance().getBleMacAddress();
//                    if (!TextUtils.isEmpty(macAddress)) {
//                        BleDeviceModel bleModel = SQLite.select().from(BleDeviceModel.class)
//                                .where(BleDeviceModel_Table.macAddress.eq(macAddress))
//                                .querySingle();
//                        if (bleModel != null) {
//                            matchId = bleModel.getMatchId();
//                            OperatorGroup operatorGroup =
//                                    OperatorGroup.clause(OperatorGroup.clause()
//                                            .and(BleLocationModel_Table.macAddress.eq(macAddress))
//                                            .and(BleLocationModel_Table.matchId.eq(matchId)));
//                            dataList = SQLite.select().from(BleLocationModel.class)
//                                    .where(operatorGroup)
//                                    .orderBy(BleLocationModel_Table.locationTime, false)
//                                    .limit(10)
//                                    .queryList();
//                            if (dataList.size() == 0) {
//                                bleModel.delete();
//                                uploadLoationData();
//                            }
//                        }
//                    }
//                } else {
//                    BleDeviceModel bleModel = SQLite.select().from(BleDeviceModel.class)
//                            .querySingle();
//                    if (bleModel != null) {
//                        MainApplication.getInstance().getMqttClient(bleModel.getMacAddress(),
//                        "3");
//                    }
//                }
//            }
//            if (dataList == null || dataList.size() == 0) {
//                return;
//            }
//            isUploading = true;
//            try {
//                String imei = BLEDataUtils.convertMacAddress(macAddress);
//                BLELocationDataModel locationDataModel = new BLELocationDataModel();
//                locationDataModel.setData(new BLELocationSubDataModel());
//                locationDataModel.getData().setDevice(imei);
//                locationDataModel.getData().setRid(matchId);
//                locationDataModel.getData().setRows(new BLELocationRowModel());
//                List<BLELocationModel> locationList = new ArrayList<>();
//                locationDataModel.getData().getRows().setRow(locationList);
//                for (int i = 0; i < dataList.size(); i++) {
//                    BleLocationModel dataModel = dataList.get(i);
//                    BLELocationModel locationModel = new BLELocationModel();
//                    locationModel.setTime(TimeUtils.formatUTC(dataModel.getLocationTime(),
//                            null));
//                    if (dataModel.getLng() == 0 && dataModel.getLat() == 0) {
//                        locationModel.setLdt(0);
//                    } else {
//                        locationModel.setLdt(1);
//                    }
//                    locationModel.setLd(String.format("%.6f|%.6f", dataModel.getLng(),
//                            dataModel.getLat()));
//                    locationModel.setPl(dataModel.getBattery());
//                    locationModel.setEd(dataModel.getAltitude());
//                    locationList.add(locationModel);
//                }
//
//                JsonToXml jsonToXml =
//                        new JsonToXml.Builder(mGson.toJson(locationDataModel)).build();
//                String xmlString = jsonToXml.toString();
//                if (BuildConfig.DEBUG) {
//                    KLog.d("uploadLoationData:" + xmlString);
//                }
//                final List<BleLocationModel> locationDataList = dataList;
//                final String bleMacAddress = macAddress;
//                mqttClient.publish(BLEDataUtils.BLE_UPLOAD_LOCATION, xmlString.getBytes(),
//                        1, false, null, new IMqttActionListener() {
//                            @Override
//                            public void onSuccess(IMqttToken asyncActionToken) {
//                                if (BuildConfig.DEBUG) {
//                                    KLog.d("send onSuccess");
//                                }
//                                if (mBleModel != null && bleMacAddress.equals(mBleModel
//                                .getMacAddress())) {
//                                    mBleModel.setUploadCount(mBleModel.getUploadCount() +
//                                    locationDataList.size());
//                                    refreshRecyclerView();
//                                }
//                                isUploading = false;
//                                for (BleLocationModel locationModel : locationDataList) {
//                                    locationModel.delete();
//                                }
//                                uploadLoationData();
//                            }
//
//                            @Override
//                            public void onFailure(IMqttToken asyncActionToken,
//                                                  Throwable exception) {
//                                if (BuildConfig.DEBUG) {
//                                    KLog.d("Failed to send to: " + exception);
//                                }
//                                isUploading = false;
//                            }
//                        });
//            } catch (MqttException e) {
//                e.printStackTrace();
//                isUploading = false;
//            }
//        }
//    }

    /**
     * 上传定位数据
     */
    @SuppressLint("DefaultLocale")
    private synchronized void uploadLoationData() {
        try {
            if (mUploadMap.size() < 5) {
                List<BleDeviceModel> bleModelList = SQLite.select().from(BleDeviceModel.class)
                        .queryList();
                for (BleDeviceModel bleModel : bleModelList) {
                    String macAddress = bleModel.getMacAddress();
                    String matchId = bleModel.getMatchId();
                    String keyString = String.format("%s,%s", macAddress, matchId);
                    if (mUploadMap.size() >= 5) {
                        break;
                    } else if (!mUploadMap.containsKey(keyString)) {
                        OperatorGroup operatorGroup =
                                OperatorGroup.clause(OperatorGroup.clause()
                                        .and(BleLocationModel_Table.macAddress.eq(macAddress))
                                        .and(BleLocationModel_Table.matchId.eq(matchId)));
                        List<BleLocationModel> dataList =
                                SQLite.select().from(BleLocationModel.class)
                                        .where(operatorGroup)
                                        .orderBy(BleLocationModel_Table.locationTime, false)
                                        .limit(20)
                                        .queryList();
                        if (dataList.size() == 0) {
                            bleModel.delete();
                        } else {
                            mUploadMap.put(keyString, "1");
                            String imei = BLEDataUtils.convertMacAddress(macAddress);
                            StringBuilder locationId = new StringBuilder();
                            BLELocationDataModel locationDataModel = new BLELocationDataModel();
                            locationDataModel.setData(new BLELocationSubDataModel());
                            locationDataModel.getData().setDevice(imei);
                            locationDataModel.getData().setRid(matchId);
                            locationDataModel.getData().setRows(new BLELocationRowModel());
                            List<BLELocationModel> locationList = new ArrayList<>();
                            locationDataModel.getData().getRows().setRow(locationList);
                            for (int i = 0; i < dataList.size(); i++) {
                                BleLocationModel dataModel = dataList.get(i);
                                BLELocationModel locationModel = new BLELocationModel();
                                locationModel.setTime(TimeUtils.formatUTC(dataModel.getLocationTime(),
                                        null));
                                if (dataModel.getLng() == 0 && dataModel.getLat() == 0) {
                                    locationModel.setLdt(0);
                                } else {
                                    locationModel.setLdt(1);
                                }
                                locationModel.setLd(String.format("%.6f|%.6f", dataModel.getLng(),
                                        dataModel.getLat()));
                                locationModel.setPl(dataModel.getBattery());
                                locationModel.setEd(dataModel.getAltitude());
                                locationList.add(locationModel);
                                locationId.append(",").append(dataModel.getId());
                            }

                            JsonToXml jsonToXml =
                                    new JsonToXml.Builder(mGson.toJson(locationDataModel)).build();
                            String xmlString = jsonToXml.toString();
                            sendBleDeviceLocation(xmlString, keyString, locationId.substring(1));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    /**
//     * 蓝牙传输下一步
//     *
//     * @param macAddress mac地址
//     * @param matchId    比赛ID
//     */
//    private void nextBleStep(String macAddress, String matchId) {
//        RxBleConnection bleConnection = mBLEConnectionMap.get(macAddress);
////        BLEDataModel bleDataModel =
////                MainApplication.getInstance().getBLEDataMap().get(macAddress);
//        BLEItemModel itemModel = mBLEItemMap.get(macAddress);
//        BLEDataModel bleDataModel = null;
//        if (itemModel != null) {
//            bleDataModel = itemModel.getConfigModel();
//        }
//        if (bleConnection != null && bleDataModel != null) {
//            if (matchId.equals(bleDataModel.getMatchId())) {
//                writeBLEData(bleConnection, macAddress, 0x06);
//            } else {
//                writeBLEData(bleConnection, macAddress, 0x01);
//            }
//        }
//    }

    /**
     * 获取蓝牙设备配置
     *
     * @param macAddress mac地址
     */
    private void getBleDeviceConfig(String macAddress) {
        AAAUserModel userModel = getTrackUserModel();
        if (userModel != null && !TextUtils.isEmpty(macAddress)) {
            String imei = BLEDataUtils.convertMacAddress(macAddress);
            CarGpsRequestUtils.getBleDeviceConfig(userModel, String.format("<request>\n" +
                    "<device>%s</device>\n" +
                    "<ver>33</ver>\n" +
                    "</request>", imei), macAddress, mHandler);
        }
    }

    /**
     * 发送蓝牙设备定位数据
     *
     * @param xmlJson    数据
     * @param macAddress mac地址+比赛id
     * @param locationId 定位数据id
     */
    private void sendBleDeviceLocation(String xmlJson, String macAddress, String locationId) {
        AAAUserModel userModel = getTrackUserModel();
        if (userModel != null && !TextUtils.isEmpty(xmlJson)) {
            CarGpsRequestUtils.sendBleDeviceLocation(userModel, xmlJson, macAddress, locationId,
                    mHandler);
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//        List<RxBleDevice> connectedList = new ArrayList<>(mBleClient.getConnectedPeripherals());
//        for (RxBleDevice bleDevice : connectedList){
//            if(BLEDataUtils.BLE_DEVICE_NAME.equals(bleDevice.getName())){
//                BLEUtils.unpairDevice(bleDevice.getBluetoothDevice());
//            }
//        }
//        if (position >= 0 && position < mItemList.size()) {
//            BaseItemBean itemBean = mItemList.get(position).t;
//            if (itemBean != null) {
//                String macAddress = itemBean.getContent();
//                if (TextUtils.isEmpty(macAddress)) {
//                    return;
//                }
//                Disposable connectionDisposable;
//                RxBleDevice bleDevice = (RxBleDevice) itemBean.getObject();
//                if (itemBean.isSelect() || mConnectionMap.containsKey(macAddress)) {
//                    connectionDisposable = mConnectionMap.get(macAddress);
//                    if (connectionDisposable != null) {
//                        connectionDisposable.dispose();
//                    }
//                } else {
//                    connectionDisposable =
//                            mBleClient.getBleDevice(itemBean.getContent())
//                                    .establishConnection(false)
//                                    .observeOn(AndroidSchedulers.mainThread())
//                                    .doFinally(() -> {
//                                        KLog.d("Connection doFinally");
//                                        Disposable disposable = null;
//                                        if (mReadMap.containsKey(macAddress)) {
//                                            disposable = mReadMap.get(macAddress);
//                                            KLog.d(macAddress + " mReadMap:" + disposable
//                                            .isDisposed());
//                                            if (disposable != null) {
//                                                disposable.dispose();
//                                            }
//                                        }
//                                        if (mNotifyMap.containsKey(macAddress)) {
//                                            disposable = mNotifyMap.get(macAddress);
//                                            KLog.d(macAddress + " mNotifyMap:" + disposable
//                                            .isDisposed());
//                                            if (disposable != null) {
//                                                disposable.dispose();
//                                            }
//                                        }
//                                        mConnectionMap.remove(macAddress);
//                                        itemBean.setTitle(bleDevice.getName());
//                                        itemBean.setSelect(false);
//                                        mAdapter.notifyDataSetChanged();
//                                    })
//                                    .subscribe(bleConnection -> {
//                                                itemBean.setTitle(String.format("%s(%s)",
//                                                        bleDevice.getName(),
//                                                        getString(R.string
//                                                        .blue_tooth_connect_success)));
//                                                itemBean.setSelect(true);
//                                                mAdapter.notifyDataSetChanged();
//                                                startReadAndWriteSettings(bleConnection,
//                                                        macAddress);
//                                            },
//                                            throwable -> {
//                                                XToastUtils.toast(macAddress + " Connection " +
//                                                        "error: " + throwable);
//                                            });
//                    mConnectionMap.put(macAddress, connectionDisposable);
//                }
//            }
//        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           @NonNull final String[] permissions,
                                           @NonNull final int[] grantResults) {
        if (PermissionUtils.isRequestBlePermissionGranted(requestCode, permissions,
                grantResults, mBleClient)) {
            BLEUtils.enableBLE(mActivity);
        }
    }

    /**
     * 消息处理
     */
    private final Handler mHandler = new Handler(Looper.getMainLooper(), msg -> {
        try {
            AAABaseResponseBean responseBean;
            AAARequestBean requestBean;
            String macAddress;
            switch (msg.what) {
                case TConstant.REQUEST_GET_BLE_DEVICE_CONFIG: // 获取蓝牙设备配置
                    responseBean = (AAABaseResponseBean) msg.obj;
                    requestBean = mGson.fromJson(mGson.toJson(responseBean.getRequestObject()),
                            AAARequestBean.class);
                    macAddress = requestBean.getMacAddress();
                    if (responseBean.getCode() == TConstant.RESPONSE_SUCCESS) {
                        XmlToJson xmlToJson =
                                new XmlToJson.Builder(responseBean.getStrParameter()).build();
                        BLEDataModel dataModel =
                                new Gson().fromJson(xmlToJson.toString(),
                                        BLEDataModel.class);
                        BLEDataModel bleDataModel = new BLEDataModel();
                        bleDataModel.setMacAddress(macAddress);
                        if (dataModel != null && dataModel.getConfig() != null) {
                            BLEConfigModel configModel = dataModel.getConfig();
                            bleDataModel.setMatchId(configModel.getRid());
                            Date date;
                            if (!TextUtils.isEmpty(configModel.getRsut())) {
                                date = TimeUtils.formatUTC(configModel.getRsut(), null);
                                if (date != null) {
                                    bleDataModel.setStartTime(date.getTime() / 1000);
                                }
                            }
                            date = TimeUtils.formatUTC(configModel.getNmst(), "HH:mm:ss");
                            if (date != null) {
                                bleDataModel.setNightTime(date.getTime() / 1000);
                            }
                            date = TimeUtils.formatUTC(configModel.getNmet(), "HH:mm:ss");
                            if (date != null) {
                                bleDataModel.setContinuedFlyTime(date.getTime() / 1000);
                            }
                            bleDataModel.setLowBattery(configModel.getLpl());
                            bleDataModel.setDelayTime(configModel.getRsud());
                            bleDataModel.setForcedStartup(1);
                            bleDataModel.setStartLocationInterval(configModel.getRgli());
                            bleDataModel.setNightLocationInterval(configModel.getNgli());
                            bleDataModel.setContinuedFlyLocationInterval(configModel.getCdui());
                            bleDataModel.setLowBatteryLocationInterval(configModel.getLpgli());
                            if (BuildConfig.DEBUG) {
                                KLog.d("bleDataModel: " + new Gson().toJson(bleDataModel));
                            }
                            BLEItemModel itemModel = mBLEItemMap.get(macAddress);
                            if (itemModel != null) {
                                itemModel.setConfigModel(bleDataModel);
                            }
                            RxBleConnection bleConnection = mBLEConnectionMap.get(macAddress);
                            if (bleConnection != null) {
                                String matchId = bleDataModel.getMatchId();
                                if (TextUtils.isEmpty(matchId)) {
                                    Disposable disposable = mConnectionMap.get(macAddress);
                                    if (disposable != null) {
                                        disposable.dispose();
                                    }
                                } else {
                                    initBLENotify(bleConnection, macAddress);
                                }
                            }
                        }
                    } else {
                        if (mConnectionMap.containsKey(macAddress)) {
                            Disposable disposable = mConnectionMap.get(macAddress);
                            if (disposable != null) {
                                disposable.dispose();
                            }
                        }
                    }
                    break;
                case TConstant.REQUEST_SEND_BLE_DEVICE_LOCATION: // 发送蓝牙设备定位数据
                    responseBean = (AAABaseResponseBean) msg.obj;
                    requestBean = mGson.fromJson(mGson.toJson(responseBean.getRequestObject()),
                            AAARequestBean.class);
                    macAddress = requestBean.getMacAddress();
                    mUploadMap.remove(macAddress);
                    String[] locationIdArray = requestBean.getLocationId().split(",");
                    if (responseBean.getCode() == TConstant.RESPONSE_SUCCESS) {
                        for (String locationId : locationIdArray) {
                            SQLite.delete(BleLocationModel.class)
                                    .where(BleLocationModel_Table.id.eq(Long.parseLong(locationId)))
                                    .execute();
                        }
                        macAddress = macAddress.split(",")[0];
                        BLEItemModel itemModel = mBLEItemMap.get(macAddress);
                        if (itemModel == null) {
                            uploadLoationData();
                            for (BLEItemModel model : mItemList) {
                                if (macAddress.equals(model.getMacAddress())) {
                                    model.setUploadCount(model.getUploadCount() + locationIdArray.length);
                                    refreshRecyclerView();
                                    break;
                                }
                            }
                        } else {
                            itemModel.setUploadCount(itemModel.getUploadCount() + locationIdArray.length);
                            refreshRecyclerView();
                        }
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }
        return false;
    });

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onPostMessage(BLEDataModel event) {
//        String macAddress = event.getMacAddress();
//        if (!TextUtils.isEmpty(macAddress)) {
//            RxBleConnection bleConnection = mBLEConnectionMap.get(macAddress);
//            if (bleConnection != null) {
//                String matchId = event.getMatchId();
//                if (TextUtils.isEmpty(matchId)) {
//                    Disposable disposable = mConnectionMap.get(macAddress);
//                    if (disposable != null) {
//                        disposable.dispose();
//                    }
//                } else {
//                    initBLENotify(bleConnection, macAddress);
//                }
//            }
//        }
//    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onPostMessage(BleDeviceModel event) {
//        String macAddress = event.getMacAddress();
//        if (!TextUtils.isEmpty(macAddress)) {
//            uploadLoationData();
//        }
//    }

    @Override
    public void onDestroyView() {
        if (mTimer != null)
            mTimer.cancel();
        mTimer = null;
        if (mScanDisposable != null)
            mScanDisposable.dispose();
        mScanDisposable = null;
        if (mFlowDisposable != null)
            mFlowDisposable.dispose();
        mFlowDisposable = null;
        List<Disposable> list = new ArrayList<>(mStateMap.values());
        mStateMap.clear();
        for (Disposable disposable : list) {
            if (disposable != null)
                disposable.dispose();
        }
        list = new ArrayList<>(mConnectionMap.values());
        mConnectionMap.clear();
        for (Disposable disposable : list) {
            if (disposable != null)
                disposable.dispose();
        }
        list = new ArrayList<>(mNotifyMap.values());
        mNotifyMap.clear();
        for (Disposable disposable : list) {
            if (disposable != null)
                disposable.dispose();
        }
        list = new ArrayList<>(mReadMap.values());
        mReadMap.clear();
        for (Disposable disposable : list) {
            if (disposable != null)
                disposable.dispose();
        }
//        MainApplication.getInstance().closeMqttClient();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        // 注销订阅者
//        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

}
