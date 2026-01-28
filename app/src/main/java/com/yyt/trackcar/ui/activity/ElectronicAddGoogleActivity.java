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
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.amap.api.maps.CoordinateConverter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtilLight;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.MainApplication;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAABaseResponseBean;
import com.yyt.trackcar.bean.GeoFenceBean;
import com.yyt.trackcar.bean.GpsBean;
import com.yyt.trackcar.dbflow.AAADeviceModel;
import com.yyt.trackcar.dbflow.AAAUserModel;
import com.yyt.trackcar.ui.base.BaseActivity;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.NewMapUtils;
import com.yyt.trackcar.utils.AAAStringUtils;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.Constant;
import com.yyt.trackcar.utils.PermissionUtils;
import com.yyt.trackcar.utils.PositionUtils;
import com.yyt.trackcar.utils.SettingSPUtils;
import com.yyt.trackcar.utils.TConstant;
import com.yyt.trackcar.utils.ViewUtils;

//import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 项目名：   传信鸽
 * 包名：     com.yyt.trackcar.ui.activity
 * 文件名：   ElectronicAddGoogleActivity
 * 创建者：   QING
 * 创建时间： 2018/5/31 20:58
 * 描述：     TODO 添加电子围栏(Google Map)
 */
@SuppressLint("NonConstantResourceId")
public class ElectronicAddGoogleActivity extends BaseActivity implements
        OnMapReadyCallback, View.OnClickListener {
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private GoogleMap mMap;
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
    private int radius = 1000; // 半径
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
        initDatas();
        initMap();
        initLocation();
        if (mGeoFenceBean == null) {
//            initToolBar(R.string.electronic_add, R.drawable.ic_back_white, mNavigationOnClickListener);
            initToolBar(String.format("%s%s", getString(R.string.pet_real_time),
                    getString(R.string.electronic_add)), R.drawable.ic_back_white, mNavigationOnClickListener);
            initToolBarMenu(R.menu.action_menu_add, mMenuItemClickListener);
        } else {
//            initToolBar(R.string.electronic_edit, R.drawable.ic_back_white, mNavigationOnClickListener);
            initToolBar(String.format("%s%s", getString(R.string.pet_real_time),
                    getString(R.string.electronic_add)), R.drawable.ic_back_white, mNavigationOnClickListener);
            initToolBarMenu(R.menu.action_menu_edit, mMenuItemClickListener);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_electronic_add_google;
    }

    protected void initViews() {
        mCarBtn = findViewById(R.id.electronic_google_car_btn);
        mMobileBtn = findViewById(R.id.electronic_google_mobile_btn);
        mDistanceBtn = findViewById(R.id.electronic_google_distance_btn);
        mSeekBar = findViewById(R.id.electronic_google_seekbar);
        mMapTypeBtn = findViewById(R.id.electronic_google_map_type_btn);
        mZoomInBtn = findViewById(R.id.electronic_google_zoom_in_btn);
        mZoomOutBtn = findViewById(R.id.electronic_google_zoom_out_btn);

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
                if (mDeviceModel.getLastLatitude() != null &&
                        mDeviceModel.getLastLongitude() != null) {
                    mDeviceLatLng = new LatLng(mDeviceModel.getLastLatitude(),  mDeviceModel.getLastLongitude());
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
                    mLatLng = new LatLng(mGeoFenceBean.getLatitude(), mGeoFenceBean.getLongitude());
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
            mCarBtn.setSelected(true);
        } else
            mDistanceBtn.setSelected(true);
    }


    protected void initListeners() {
        mCarBtn.setOnClickListener(this);
        mDistanceBtn.setOnClickListener(this);
        mMobileBtn.setOnClickListener(this);
        mMapTypeBtn.setOnClickListener(this);
        mZoomInBtn.setOnClickListener(this);
        mZoomOutBtn.setOnClickListener(this);
        mMap.setInfoWindowAdapter(mInfoWindowAdapter);
        mMap.setOnInfoWindowClickListener(mInfoWindowClickListener);
        mMap.setOnMapClickListener(mMapClickListener);
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


    /**
     * 初始化AMap对象
     */
    private void initMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.electronic_google_map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    /**
     * 初始化定位
     */
    private void initLocation() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(Constant.FIVE_MINUTES);
        mLocationRequest.setMaxWaitTime(Constant.FIVE_MINUTES);
        mLocationRequest.setFastestInterval(Constant.FIVE_MINUTES);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        client.checkLocationSettings(builder.build());
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    /**
     * 切换地图类型
     */
    public void onMapType(View v) {
        if (v.isSelected() && mMap != null) {
            v.setSelected(false);
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        } else if (mMap != null) {
            v.setSelected(true);
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }
    }

    /**
     * 查找车辆位置
     */
    public void onCar(View v) {
        if (mDeviceLatLng == null)
            showMessage(R.string.no_device_gps_tips);
        else {
            mCarBtn.setSelected(true);
            mMobileBtn.setSelected(false);
            mDistanceBtn.setSelected(false);
            if (mUiSettings != null)
                mUiSettings.setScrollGesturesEnabled(true);
            refreshOverlay(mDeviceLatLng);
            if (mMarker != null)
                mMarker.showInfoWindow();
        }
    }

    /**
     * 查找手机位置
     */
    public void onMobile(View v) {
        if (mMap != null) {
            mCarBtn.setSelected(false);
            mMobileBtn.setSelected(true);
            mDistanceBtn.setSelected(false);
            if (mUiSettings != null)
                mUiSettings.setScrollGesturesEnabled(false);
            startLocation();
            if (mMarker != null)
                mMarker.showInfoWindow();
        }
    }

    /**
     * 计算距离
     */
    public void onDistance(View v) {
        mCarBtn.setSelected(false);
        mMobileBtn.setSelected(false);
        mDistanceBtn.setSelected(true);
        if (mUiSettings != null)
            mUiSettings.setScrollGesturesEnabled(true);
        if (mMarker != null)
            mMarker.showInfoWindow();
    }

    /**
     * 放大地图
     */
    public void onZoomIn(View v) {
        if (mMap != null)
            mMap.moveCamera(CameraUpdateFactory.zoomIn());
    }

    /**
     * 缩小地图
     */
    public void onZoomOut(View v) {
        if (mMap != null)
            mMap.moveCamera(CameraUpdateFactory.zoomOut());
    }

    /**
     * 刷新图层
     */
    private void refreshOverlay(final LatLng point) {
        if (point != null && mMap != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
//                    mAddress = MapUtils.getAddress(ElectronicAddGoogleActivity.this, point
//                            .latitude, point.longitude);
                    try {
                        mLatLng = point;
                        if (TextUtils.isEmpty(mAddress))
                            mAddress = getString(R.string.tracking_address_not_find);
                        if (mMap != null) {
                            //mMap.clear();
                            CoordinateConverter converter =
                                    new CoordinateConverter(ElectronicAddGoogleActivity.this);
                            converter.from(CoordinateConverter.CoordType.GPS);
                            converter.coord(new com.amap.api.maps.model.LatLng(point.latitude, point
                                    .longitude));
                            com.amap.api.maps.model.LatLng aMapLatLng = converter.convert();
                            final LatLng latLng = new LatLng(aMapLatLng.latitude,
                                    aMapLatLng.longitude);
                            ElectronicAddGoogleActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (mMarker == null) {
                                            MarkerOptions option =
                                                    new MarkerOptions().position(latLng)
                                                    .draggable(false)
                                                    .title("").snippet("");
                                            option.flat(true);
                                            mMarker = mMap.addMarker(option);
                                            mCirCle = mMap.addCircle(new CircleOptions().
                                                    center(latLng).
                                                    radius(radius).
                                                    fillColor(getResources().getColor(R.color
                                                            .translucent_gray)).
                                                    strokeColor(getResources().getColor(R.color.blue)).
                                                    strokeWidth(getResources().getDimension(R.dimen
                                                            .line_width)));
                                        } else {
                                            mMarker.setPosition(latLng);
                                            mCirCle.setCenter(latLng);
                                        }
                                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                        mMarker.showInfoWindow();
                                    } catch (Exception e) {
                                        if (BuildConfig.DEBUG)
                                            e.printStackTrace();
                                    }
                                }
                            });
                        }
                    } catch (Exception e) {
                        if (BuildConfig.DEBUG)
                            e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    /**
     * 开始定位
     */
    private void startLocation() {
        int errorCode = GooglePlayServicesUtilLight.isGooglePlayServicesAvailable(this);
        switch (errorCode) {
            case ConnectionResult.SUCCESS:
                PermissionUtils.checkAndRequestMorePermissions(this, needPermissions,
                        TConstant.REQUEST_PERMISSION_MOBILE_LOCATION, new PermissionUtils
                                .PermissionRequestSuccessCallBack() {
                            @SuppressLint("MissingPermission")
                            @Override
                            public void onHasPermission() {
                                mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                        mLocationCallback,
                                        null /* Looper */);
                            }
                        });
                break;
            default:
                break;
        }
    }

    /**
     * 停止定位
     */
    private void stopLocation() {
        // 停止定位
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    /**
     * 电子围栏对话框
     *
     * @param type 类型
     */
    private void showElecronicDialog(final int type) {
        final EditText editText = new EditText(ElectronicAddGoogleActivity.this);
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
                                GpsBean gps = PositionUtils.gcj_To_Gps84(mLatLng.latitude, mLatLng.longitude);
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

        Window window = alertDialog.getWindow();
        window.getDecorView().setPadding(50,30,50,30);

//        new SweetAlertDialog(ElectronicAddGoogleActivity.this, SweetAlertDialog
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
//                            if(MapUtils.isInsideChina(mLatLng.latitude, mLatLng
//                                    .longitude)) {
//                                GpsBean gps = PositionUtils.gcj_To_Gps84(mLatLng.latitude,mLatLng.longitude);
//                                latLng = new LatLng(gps.getWgLat(),gps.getWgLon());
//                            }else
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
            case R.id.electronic_google_car_btn: // 定位车辆
                onCar(v);
                break;
            case R.id.electronic_google_mobile_btn: // 定位手机
                onMobile(v);
                break;
            case R.id.electronic_google_distance_btn: // 距离
                onDistance(v);
                break;
            case R.id.electronic_google_map_type_btn: // 地图类型
                onMapType(v);
                break;
            case R.id.electronic_google_zoom_in_btn: // 地图放大
                onZoomIn(v);
                break;
            case R.id.electronic_google_zoom_out_btn: // 地图缩小
                onZoomOut(v);
                break;
            default:
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        mMap.setIndoorEnabled(false);
        mUiSettings = mMap.getUiSettings();
        mUiSettings.setMyLocationButtonEnabled(false); // 是否显示默认的定位按钮
        mUiSettings.setTiltGesturesEnabled(true);// 设置地图是否可以倾斜
        mUiSettings.setZoomControlsEnabled(false);
        mUiSettings.setIndoorLevelPickerEnabled(false); // 禁用楼层选择器
        mUiSettings.setMapToolbarEnabled(false);
        initListeners();
        if (mCarBtn.isSelected())
            onCar(mCarBtn);
        else if (mMobileBtn.isSelected())
            onMobile(mMobileBtn);
        else {
            refreshOverlay(mLatLng);
            onDistance(mDistanceBtn);
        }
    }

    /**
     * 标题栏Navigation点击监听器
     */
    private final View.OnClickListener mNavigationOnClickListener = new View.OnClickListener() {

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
    private final GoogleMap.OnMapClickListener mMapClickListener =
            new GoogleMap.OnMapClickListener() {

                @Override
                public void onMapClick(LatLng location) {
                    // TODO Auto-generated method stub
                    if (mDistanceBtn.isSelected()) {
                        refreshOverlay(location);
                    }
                }
            };

    /**
     * 地图信息弹窗适配器
     */
    private final GoogleMap.InfoWindowAdapter mInfoWindowAdapter = new GoogleMap.InfoWindowAdapter() {

        @Override
        public View getInfoWindow(Marker marker) {
            // TODO Auto-generated method stub
            @SuppressLint("InflateParams")
            View view = LayoutInflater.from(ElectronicAddGoogleActivity.this).inflate(R.layout
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
    private final GoogleMap.OnInfoWindowClickListener mInfoWindowClickListener = new GoogleMap
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
    private final LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult != null) {
                if (locationResult.getLocations().size() > 0 && mMobileBtn.isSelected()) {
                    mLatLng = new LatLng(locationResult.getLocations().get(0).getLatitude()
                            , locationResult.getLocations().get(0).getLongitude());
                    refreshOverlay(mLatLng);
                }
            }
        }
    };

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
    public void onDestroy() {
        stopLocation();
        mMap = null;
        super.onDestroy();
    }
}
