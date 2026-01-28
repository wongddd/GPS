package com.yyt.trackcar.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.MainApplication;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAABaseResponseBean;
import com.yyt.trackcar.bean.GeoFenceBean;
import com.yyt.trackcar.bean.GpsBean;
import com.yyt.trackcar.dbflow.AAADeviceModel;
import com.yyt.trackcar.dbflow.AAAUserModel;
import com.yyt.trackcar.ui.base.BaseActivity;
import com.yyt.trackcar.utils.AAAStringUtils;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.Constant;
import com.yyt.trackcar.utils.NewMapUtils;
import com.yyt.trackcar.utils.PermissionUtils;
import com.yyt.trackcar.utils.PositionUtils;
import com.yyt.trackcar.utils.SettingSPUtils;
import com.yyt.trackcar.utils.TConstant;
import com.yyt.trackcar.utils.ViewUtils;

/**
 * 项目名：   传信鸽
 * 包名：     com.yyt.trackcar.ui.activity
 * 文件名：   ElectronicAddActivity
 * 创建者：   QING
 * 创建时间： 2018/4/26 20:58
 * 描述：     TODO 添加电子围栏
 */
@SuppressLint("NonConstantResourceId")
public class ElectronicAddActivity extends BaseActivity implements GeocodeSearch
        .OnGeocodeSearchListener, OnClickListener {
    // 地图
    private AMapLocationClient locationClient;
    private AMapLocationClientOption locationOption;
    private AMap mAMap;
    private MapView mMapView;
    private Marker mMarker; // 设备定位图标
    private Circle mCirCle; // 围栏

    // UI相关
    private UiSettings mUiSettings;

    private ImageButton mCarBtn, mMobileBtn, mDistanceBtn, mMapTypeBtn, mZoomInBtn, mZoomOutBtn;
    // 定位车辆，定位手机，查看距离,切换地图类型，地图放大，地图缩小按钮
    private SeekBar mSeekBar; // 进度条

    private AAADeviceModel mDeviceModel; // 车辆信息
    private LatLng mLatLng; // 定位坐标
    private LatLng mDeviceLatLng; // 设备定位坐标
    private String mAddress; // 定位地址
    private GeoFenceBean mGeoFenceBean; // 电子围栏对象
    private int radius = 500; // 半径
    private String mElectronicName; // 围栏名称

    /**
     * 需要进行检测的权限数组
     */
    protected String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        initMap(savedInstanceState);
        initLocation();
        initListeners();
        initDatas();
        if (mGeoFenceBean == null) {
//            initToolBar(R.string.electronic_add, R.drawable.ic_back_white, mNavigationOnClickListener);
            initToolBar(String.format("%s%s", getString(R.string.pet_real_time),
                    getString(R.string.electronic_add)), R.drawable.ic_back_white, mNavigationOnClickListener);
            initToolBarMenu(R.menu.action_menu_add, mMenuItemClickListener);
        } else {
//            initToolBar(R.string.electronic_edit, R.drawable.ic_back_white, mNavigationOnClickListener);
            initToolBar(String.format("%s%s", getString(R.string.pet_real_time),
                    getString(R.string.electronic_edit)), R.drawable.ic_back_white, mNavigationOnClickListener);
            initToolBarMenu(R.menu.action_menu_edit, mMenuItemClickListener);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_electronic_add;
    }

    protected void initViews() {
        mMapView = findViewById(R.id.electronic_map);
        mCarBtn = findViewById(R.id.electronic_car_btn);
        mMobileBtn = findViewById(R.id.electronic_mobile_btn);
        mDistanceBtn = findViewById(R.id.electronic_distance_btn);
        mSeekBar = findViewById(R.id.electronic_seekbar);
        mMapTypeBtn = findViewById(R.id.electronic_map_type_btn);
        mZoomInBtn = findViewById(R.id.electronic_zoom_in_btn);
        mZoomOutBtn = findViewById(R.id.electronic_zoom_out_btn);

        if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE,1) == 2) {
            mSeekBar.setMax(49900);
            radius = 2000;
        }else
            mSeekBar.setMax(1900);

    }


    protected void initDatas() {
        mDeviceModel = MainApplication.getInstance().getTrackDeviceModel();
        mGeoFenceBean = getIntent().getParcelableExtra(TConstant.BEAN);
        if (mDeviceModel != null) {
            try {
                if (mDeviceModel.getLastLatitude() != null && mDeviceModel.getLastLongitude() != null) {
                    CoordinateConverter converter = new CoordinateConverter(this);
                    converter.from(CoordinateConverter.CoordType.GPS);
                    converter.coord(new LatLng(mDeviceModel.getLastLatitude(),
                            mDeviceModel.getLastLongitude()));
                    mDeviceLatLng = converter.convert();
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        if (mGeoFenceBean != null) {
            mElectronicName = mGeoFenceBean.getFenceName() == null ? "" :
                    mGeoFenceBean.getFenceName();
            if (mGeoFenceBean.getLatitude() != null && mGeoFenceBean.getLongitude() != null) {
                try {
                    CoordinateConverter converter = new CoordinateConverter(this);
                    converter.from(CoordinateConverter.CoordType.GPS);
                    converter.coord(new LatLng(
                            mGeoFenceBean.getLatitude(), mGeoFenceBean.getLongitude()));
                    mLatLng = converter.convert();
                    radius = mGeoFenceBean.getRadius() == null ? 0 :
                            Integer.parseInt(String.valueOf(mGeoFenceBean.getRadius()));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                mAddress = "";
            }
        }
        mSeekBar.setProgress(radius - 100);
        if (TextUtils.isEmpty(mAddress))
            mAddress = getString(R.string.tracking_address_not_find);
        if (mLatLng == null && mDeviceLatLng == null)
            onMobile(null);
        else if (mLatLng == null) {
            mLatLng = mDeviceLatLng;
            onCar(null);
        } else {
            mDistanceBtn.setSelected(true);
            refreshOverlay(mLatLng);
            searchLocation(mLatLng);
        }
    }


    protected void initListeners() {
        mCarBtn.setOnClickListener(this);
        mDistanceBtn.setOnClickListener(this);
        mMobileBtn.setOnClickListener(this);
        mMapTypeBtn.setOnClickListener(this);
        mZoomInBtn.setOnClickListener(this);
        mZoomOutBtn.setOnClickListener(this);
        mAMap.setInfoWindowAdapter(mInfoWindowAdapter);
        mAMap.setOnInfoWindowClickListener(mInfoWindowClickListener);
        mAMap.setOnMapClickListener(mMapClickListener);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                radius = progress + 100;
                if (mCirCle != null)
                    mCirCle.setRadius(radius);
                if (mMarker != null)
                    mMarker.showInfoWindow();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mMarker != null)
                    mMarker.hideInfoWindow();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    protected void initItems() {

    }


    protected void initAdapters() {

    }


    protected void initRecyclerViews() {

    }

    /**
     * 初始化AMap对象
     */
    private void initMap(@Nullable Bundle savedInstanceState) {
        mMapView.onCreate(savedInstanceState);// 此方法必须重写
        if (mAMap == null) {
            mAMap = mMapView.getMap();
            mUiSettings = mAMap.getUiSettings();
        }
        mAMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        mAMap.setMapType(AMap.MAP_TYPE_NORMAL);

        mUiSettings.setMyLocationButtonEnabled(false); // 是否显示默认的定位按钮
        mUiSettings.setTiltGesturesEnabled(true);// 设置地图是否可以倾斜
        mUiSettings.setScaleControlsEnabled(true);// 设置地图默认的比例尺是否显示
        mUiSettings.setZoomControlsEnabled(false);
        mUiSettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);
    }

    /**
     * 初始化定位
     */
    private void initLocation() {
        //初始化client
        try {
            locationClient = new AMapLocationClient(this.getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        locationOption = getDefaultOption();
        //设置定位参数
        locationClient.setLocationOption(locationOption);
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
    }

    /**
     * 切换地图类型
     */
    public void onMapType(View v) {
        if (v.isSelected()) {
            v.setSelected(false);
            mAMap.setMapType(AMap.MAP_TYPE_NORMAL);
        } else {
            v.setSelected(true);
            mAMap.setMapType(AMap.MAP_TYPE_SATELLITE);
        }
    }

    /**
     * 查找车辆位置
     */
    public void onCar(View v) {
        if (mDeviceLatLng == null)
            showMessage(R.string.no_device_gps_tips);
        else {
            if (mMarker != null)
                mMarker.showInfoWindow();
            mCarBtn.setSelected(true);
            mMobileBtn.setSelected(false);
            mDistanceBtn.setSelected(false);
            mUiSettings.setScrollGesturesEnabled(true);
            searchLocation(mDeviceLatLng);
        }
    }

    /**
     * 查找手机位置
     */
    public void onMobile(View v) {
        mCarBtn.setSelected(false);
        mMobileBtn.setSelected(true);
        mDistanceBtn.setSelected(false);
        mUiSettings.setScrollGesturesEnabled(false);
        startLocation();
        if (mMarker != null)
            mMarker.showInfoWindow();
    }

    /**
     * 计算距离
     */
    public void onDistance(View v) {
        mCarBtn.setSelected(false);
        mMobileBtn.setSelected(false);
        mDistanceBtn.setSelected(true);
        mUiSettings.setScrollGesturesEnabled(true);
        if (mMarker != null)
            mMarker.showInfoWindow();
    }

    /**
     * 放大地图
     */
    public void onZoomIn(View v) {
        mAMap.moveCamera(CameraUpdateFactory.zoomIn());
    }

    /**
     * 缩小地图
     */
    public void onZoomOut(View v) {
        mAMap.moveCamera(CameraUpdateFactory.zoomOut());
    }

    /**
     * 刷新图层
     */
    private void refreshOverlay(LatLng point) {
        if (point != null) {
            //mAMap.clear();
            if (mMarker == null) {
                MarkerOptions option = new MarkerOptions().position(point).draggable(false).title
                        ("")
                        .snippet("")
                        .setInfoWindowOffset(0, -2);
                // 将Marker设置为贴地显示，可以双指下拉地图查看效果
                option.setFlat(true);//设置marker平贴地图效果
                mMarker = mAMap.addMarker(option);
                mCirCle = mAMap.addCircle(new CircleOptions().
                        center(point).
                        radius(radius).
                        fillColor(getResources().getColor(R.color.translucent_gray)).
                        strokeColor(getResources().getColor(R.color.blue)).
                        strokeWidth(getResources().getDimension(R.dimen.line_width)));
            } else {
                mMarker.setPosition(point);
                mCirCle.setCenter(point);
            }
            mAMap.moveCamera(CameraUpdateFactory.newLatLng(point));
            mMarker.showInfoWindow();
        }
    }

    /**
     * 查找位置
     *
     * @param latLng 坐标
     */
    private void searchLocation(LatLng latLng) {
        refreshOverlay(latLng);
//        GeocodeSearch geocoderSearch = new GeocodeSearch(this);
//        geocoderSearch.setOnGeocodeSearchListener(this);
//        RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(latLng.latitude, latLng
//                .longitude), 0,
//                GeocodeSearch.AMAP);
//        geocoderSearch.getFromLocationAsyn(query);
    }

    /**
     * 默认的定位参数
     */
    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(Constant.FIVE_MINUTES);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息是true
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,
        // 会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption
                .AMapLocationProtocol.HTTP);//可选，
        // 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true);
        //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }

    /**
     * 开始定位
     */
    private void startLocation() {
        PermissionUtils.checkAndRequestMorePermissions(this, needPermissions,
                TConstant.REQUEST_PERMISSION_MOBILE_LOCATION, new PermissionUtils
                        .PermissionRequestSuccessCallBack() {
                    @Override
                    public void onHasPermission() {
                        //根据控件的选择，重新设置定位参数
                        // 设置定位参数
                        locationClient.setLocationOption(locationOption);
                        // 启动定位
                        locationClient.startLocation();
                    }
                });
    }

    /**
     * 停止定位
     */
    private void stopLocation() {
        // 停止定位
        locationClient.stopLocation();
    }

    /**
     * 销毁定位
     */
    private void destroyLocation() {
        if (null != locationClient) {
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }

    /**
     * 电子围栏对话框
     *
     * @param type 类型
     */
    private void showElecronicDialog(final int type) {
        final EditText editText = new EditText(ElectronicAddActivity.this);
        editText.setBackgroundResource(R.drawable.edittext_bg);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setMaxLines(1);
        editText.setLabelFor(editText.getId());
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources()
                .getDimension(R.dimen
                        .edit_font));
        editText.setHintTextColor(getResources().getColor(R.color.lightgray));
        editText.setTextColor(getResources().getColor(R.color.darkslategrey));
        editText.setHint(R.string.electronic_hint);
        ViewUtils.setEtCoustomLength(editText, 20);
        int padding = getResources().getDimensionPixelOffset(R.dimen.margin_8);
        editText.setPadding(padding, padding, padding, padding);
        if (!TextUtils.isEmpty(mElectronicName)) {
            editText.setText(mElectronicName);
            editText.requestFocus();
        }

        AlertDialog.Builder builder =new AlertDialog.Builder(this);
        AlertDialog alertDialog = builder.setView(editText)
                .setTitle(mTitle.getText().toString())
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (TextUtils.isEmpty(editText.getText().toString()))
                            showMessage(R.string.electronic_name_empty_tips);
                        else {
                            if (!NetworkUtils.isNetworkAvailable()) {
                                showMessage(R.string.network_error_prompt);
                                return;
                            }
                            AAAUserModel userModel = getTrackUserModel();
                            if (userModel == null || mDeviceModel == null)
                                return;
                            showDialog();
                            mLoadingDialog.setMessage(getString(R.string.requesting_tips));
                            GeoFenceBean geoFenceBean = new GeoFenceBean();
                            LatLng latLng;
                            if (NewMapUtils.isInsideChina(mLatLng.latitude, mLatLng
                                    .longitude)) {
                                GpsBean gps = PositionUtils.gcj_To_Gps84(mLatLng.latitude,
                                        mLatLng.longitude);
                                latLng = new LatLng(gps.getWgLat(), gps.getWgLon());
                            } else
                                latLng = mLatLng;
                            geoFenceBean.setFenceName(editText.getText().toString());
                            geoFenceBean.setLatitude((float) latLng.latitude);
                            geoFenceBean.setLongitude((float) latLng.longitude);
                            geoFenceBean.setRadius((long) radius);
                            if (type == 0) { // 添加电子围栏
                                CarGpsRequestUtils.addGeoFence(userModel,
                                        mDeviceModel.getDeviceImei(), geoFenceBean, mHandler);
                            } else { // 编辑电子围栏
                                if (mGeoFenceBean != null) {
                                    geoFenceBean.setFenceId(mGeoFenceBean.getFenceId());
                                    CarGpsRequestUtils.updateGeoFence(userModel, geoFenceBean,
                                            mHandler);
                                }
                            }
                        }
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).show();
//
        Window window = alertDialog.getWindow();
        window.getDecorView().setPadding(50,30,50,30);

//        new SweetAlertDialog(ElectronicAddActivity.this, SweetAlertDialog
//                .NORMAL_TYPE).setTitleText(mTitle.getText().toString())
//                .setCustomView(editText).setCancelText(getString(R
//                .string.cancel)).showCancelButton(true)
//                .setConfirmButton(R.string.confirm, new SweetAlertDialog
//                        .OnSweetClickListener() {
//                    @Override
//                    public void onClick(SweetAlertDialog sweetAlertDialog) {
//                        if (TextUtils.isEmpty(editText.getText().toString()))
//                            showMessage(R.string.electronic_name_empty_tips);
//                        else {
//                            sweetAlertDialog.dismissWithAnimation();
//                            if (!NetworkUtils.isNetworkAvailable()) {
//                                showMessage(R.string.network_error_prompt);
//                                return;
//                            }
//                            AAAUserModel userModel = getTrackUserModel();
//                            if (userModel == null || mDeviceModel == null)
//                                return;
//                            showDialog();
//                            mLoadingDialog.setMessage(getString(R.string.requesting_tips));
//                            GeoFenceBean geoFenceBean = new GeoFenceBean();
//                            LatLng latLng;
//                            if (NewMapUtils.isInsideChina(mLatLng.latitude, mLatLng
//                                    .longitude)) {
//                                GpsBean gps = PositionUtils.gcj_To_Gps84(mLatLng.latitude,
//                                        mLatLng.longitude);
//                                latLng = new LatLng(gps.getWgLat(), gps.getWgLon());
//                            } else
//                                latLng = mLatLng;
//                            geoFenceBean.setFenceName(editText.getText().toString());
//                            geoFenceBean.setLatitude((float) latLng.latitude);
//                            geoFenceBean.setLongitude((float) latLng.longitude);
//                            geoFenceBean.setRadius((long) radius);
//                            if (type == 0) { // 添加电子围栏
//                                CarGpsRequestUtils.addGeoFence(userModel,
//                                        mDeviceModel.getDeviceImei(), geoFenceBean, mHandler);
//                            } else { // 编辑电子围栏
//                                if (mGeoFenceBean != null) {
//                                    geoFenceBean.setFenceId(mGeoFenceBean.getFenceId());
//                                    CarGpsRequestUtils.updateGeoFence(userModel, geoFenceBean,
//                                            mHandler);
//                                }
//                            }
//                        }
//                    }
//                }).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.electronic_car_btn: // 定位车辆
                onCar(v);
                break;
            case R.id.electronic_mobile_btn: // 定位手机
                onMobile(v);
                break;
            case R.id.electronic_distance_btn: // 距离
                onDistance(v);
                break;
            case R.id.electronic_map_type_btn: // 地图类型
                onMapType(v);
                break;
            case R.id.electronic_zoom_in_btn: // 地图放大
                onZoomIn(v);
                break;
            case R.id.electronic_zoom_out_btn: // 地图缩小
                onZoomOut(v);
                break;
            default:
                break;
        }
    }

    /**
     * 标题栏Navigation点击监听器
     */
    private final OnClickListener mNavigationOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            finish();
        }
    };

    /**
     * 标题栏菜单点击监听器
     */
    private final Toolbar.OnMenuItemClickListener mMenuItemClickListener = new Toolbar
            .OnMenuItemClickListener() {

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (mLatLng == null)
                showMessage(R.string.no_gps_tips);
            else {
                switch (item.getItemId()) {
                    case R.id.item_add: // 添加
                        showElecronicDialog(0);
                        break;
                    case R.id.item_edit: // 编辑
                        showElecronicDialog(1);
                        break;
                    default:
                        break;
                }
            }
            return false;
        }
    };

    /**
     * 地图点击监听器
     */
    private final AMap.OnMapClickListener mMapClickListener = new AMap.OnMapClickListener() {

        @Override
        public void onMapClick(LatLng location) {
            // TODO Auto-generated method stub
            if (mDistanceBtn.isSelected() || mCarBtn.isSelected())
                searchLocation(location);
        }
    };

    /**
     * 地图信息弹窗适配器
     */
    private final AMap.InfoWindowAdapter mInfoWindowAdapter = new AMap.InfoWindowAdapter() {

        @Override
        public View getInfoWindow(Marker marker) {
            // TODO Auto-generated method stub
            @SuppressLint("InflateParams")
            View view = LayoutInflater.from(ElectronicAddActivity.this).inflate(R.layout
                            .amap_info_window_layout,
                    null);
            TextView mAddressText = view.findViewById(R.id.amap_info_window_address);
            mAddressText.setText(getString(R.string.electronic_description,
                    String.valueOf(marker.getPosition().longitude),
                    String.valueOf(marker.getPosition().latitude),
                    AAAStringUtils.getMapDistance(radius)));
            return view;
        }

        @Override
        public View getInfoContents(Marker marker) {
            // TODO Auto-generated method stub
            return null;
        }
    };

    /**
     * 地图信息弹窗点击监听器
     */
    private AMap.OnInfoWindowClickListener mInfoWindowClickListener = new AMap
            .OnInfoWindowClickListener() {

        @Override
        public void onInfoWindowClick(Marker mark) {
            // TODO Auto-generated method stub
            mark.hideInfoWindow();
        }
    };

    /**
     * 定位监听
     */
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (null != location && mMobileBtn.isSelected()) {
                mAddress = location.getAddress();
                if (TextUtils.isEmpty(mAddress))
                    mAddress = getString(R.string.tracking_address_not_find);
                mLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                refreshOverlay(mLatLng);
            }
        }
    };

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        if (!mMobileBtn.isSelected()) {
            mLatLng = new LatLng(regeocodeResult.getRegeocodeQuery().getPoint().getLatitude(),
                    regeocodeResult.getRegeocodeQuery().getPoint().getLongitude());
            mAddress = regeocodeResult.getRegeocodeAddress().getFormatAddress();
            if (TextUtils.isEmpty(mAddress))
                mAddress = getString(R.string.tracking_address_not_find);
            refreshOverlay(mLatLng);
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case TConstant.REQUEST_PERMISSION_MOBILE_LOCATION: // 手机定位权限
                if (PermissionUtils.isPermissionRequestSuccess(grantResults))
                    startLocation();
                else
                    showMessage(R.string.no_amap_permission);
                break;
            default:
                break;
        }
    }

    /**
     * 消息处理
     */
    private final Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            try {
                AAABaseResponseBean responseBean;
                switch (msg.what) {
                    case TConstant.REQUEST_URL_ADD_GEO_FENCE:
                        dismisDialog();
                        if (msg.obj == null)
                            showMessage(R.string.request_unkonow_prompt);
                        else {
                            responseBean = (AAABaseResponseBean) msg.obj;
                            if (responseBean.getCode() == TConstant.RESPONSE_SUCCESS) {
                                showMessage(R.string.edit_success_tips);
                                Intent intent = new Intent();
                                setResult(RESULT_OK, intent);
                                finish();
                            } else if (responseBean.getCode() == TConstant.RESPONSE_NET_ERROR)
                                showMessage(R.string.request_unkonow_prompt);
                            else
                                showMessage(R.string.add_error_tips);
                        }
                        break;
                    case TConstant.REQUEST_URL_UPDATE_GEO_FENCE:
                        dismisDialog();
                        if (msg.obj == null)
                            showMessage(R.string.request_unkonow_prompt);
                        else {
                            responseBean = (AAABaseResponseBean) msg.obj;
                            if (responseBean.getCode() == TConstant.RESPONSE_SUCCESS) {
                                showMessage(R.string.edit_success_tips);
                                GeoFenceBean geoFenceBean =
                                        mGson.fromJson(responseBean.getRequestObject(),
                                                GeoFenceBean.class);
                                Intent intent = new Intent();
                                intent.putExtra(TConstant.BEAN, geoFenceBean);
                                setResult(RESULT_OK, intent);
                                finish();
                            } else if (responseBean.getCode() == TConstant.RESPONSE_NET_ERROR)
                                showMessage(R.string.request_unkonow_prompt);
                            else
                                showMessage(R.string.edit_error_tips);
                        }
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    });

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        stopLocation();
        destroyLocation();
        // 关闭定位图层
        mAMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }
}
