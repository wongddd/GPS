package com.yyt.trackcar.ui.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CoordinateConverter;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.github.pengrad.mapscaleview.MapScaleView;
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
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xutil.net.NetworkUtils;
import com.xuexiang.xutil.system.KeyboardUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.FenceBean;
import com.yyt.trackcar.bean.GpsBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.LocationModel;
import com.yyt.trackcar.dbflow.LocationModel_Table;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CWRequestUtils;
import com.yyt.trackcar.utils.EmojiFilter;
import com.yyt.trackcar.utils.MapUtils;
import com.yyt.trackcar.utils.PermissionUtils;
import com.yyt.trackcar.utils.PositionUtils;
import com.yyt.trackcar.utils.RequestToastUtils;
import com.yyt.trackcar.utils.XToastUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      FenceMapFragment
 * @ author:        QING
 * @ createTime:    2020/4/9 07:50
 * @ describe:      TODO
 */
@Page(name = "FenceMap", params = {CWConstant.TITLE, CWConstant.LIST, CWConstant.MODEL})
public class FenceMapFragment extends BaseFragment implements OnMapReadyCallback,
        GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraIdleListener, GeocodeSearch
                .OnGeocodeSearchListener {
    @BindView(R.id.sbRadius)
    SeekBar mSbRadius; // 半径
    @BindView(R.id.tvRadius)
    TextView mTvRadius; // 半径文本
    @BindView(R.id.scaleView)
    MapScaleView mScaleView; // 比例尺布局
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private GoogleMap mMap;
    private Marker mMarker; // 设备定位图标
    private Circle mCirCle; // 围栏
    // UI相关
    private UiSettings mUiSettings;
    @AutoWired
    String title; // 标题
    @AutoWired
    String list; // 电子围栏列表
    @AutoWired
    FenceBean model; // 电子围栏对象
    private LatLng mLatLng; // 定位坐标
    private LatLng mDeviceLatLng; // 设备定位坐标
    private String mAddress; // 定位地址
    private int mRadius = 500; // 半径
    private String mFenceName; // 围栏名称
    private boolean mIsLocation; // 是否定位
    private LatLng mSearchLatLng; // 搜索坐标
    private ArrayList mItemList; // 电子围栏列表

    /**
     * 需要进行检测的权限数组
     */
    protected String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_fence_map;
    }

    @Override
    protected void initArgs() {
        XRouter.getInstance().inject(this);
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(title);
        titleBar.addAction(new TitleBar.TextAction(getString(R.string.save)) {
            @Override
            public void performAction(View view) {
                if (mMaterialDialog == null) {
                    @SuppressLint("InflateParams")
                    View fenceMapView = getLayoutInflater().inflate(R.layout.dialog_fence_map, null,
                            false);
                    MaterialDialog.Builder builder = new MaterialDialog.Builder(mActivity)
                            .title(title)
                            .customView(fenceMapView, true);
                    EditText etName = fenceMapView.findViewById(R.id.etName);
//                    SwitchButton sbEntry = fenceMapView.findViewById(R.id.sbEntry);
//                    SwitchButton sbExit = fenceMapView.findViewById(R.id.sbExit);
                    if (model != null) {
                        etName.setText(model.getFenceName());
//                        sbEntry.setChecked(model.getEntry() == 1);
//                        sbExit.setChecked(model.getExit() == 1);
                    }
                    builder.positiveText(getString(R.string.confirm))
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog,
                                                    @NonNull DialogAction which) {
                                    String name = etName.getText().toString().trim();
                                    KeyboardUtils.hideSoftInput(etName);
                                    if (mLatLng == null)
                                        XToastUtils.toast(R.string.no_position_info);
                                    else if (TextUtils.isEmpty(name) || EmojiFilter.containsEmoji(name))
                                        XToastUtils.toast(etName.getHint().toString());
                                    else {
                                        KeyboardUtils.hideSoftInput(etName);
                                        FenceBean fenceBean = new FenceBean();
                                        fenceBean.setFenceName(name);
                                        fenceBean.setRadius(mRadius);
                                        if (CoordinateConverter.isAMapDataAvailable(mLatLng.latitude,
                                                mLatLng.longitude)) {
                                            GpsBean gpsBean =
                                                    PositionUtils.gcj_To_Gps84(mLatLng.latitude,
                                                            mLatLng.longitude);
                                            fenceBean.setLat(String.valueOf(gpsBean.getWgLat()));
                                            fenceBean.setLng(String.valueOf(gpsBean.getWgLon()));
                                        } else {
                                            fenceBean.setLat(String.valueOf(mLatLng.latitude));
                                            fenceBean.setLng(String.valueOf(mLatLng.longitude));
                                        }
                                        if (mItemList != null) {
                                            for (Object obj : mItemList) {
                                                FenceBean fBean =
                                                        mGson.fromJson(mGson.toJson(obj),
                                                                FenceBean.class);
                                                if (model == null || model.getId() != fBean.getId()) {
                                                    if (fenceBean.getFenceName().equals(fBean
                                                    .getFenceName())) {
                                                        XToastUtils.toast(R.string
                                                        .name_equal_prompt);
                                                        return;
                                                    } else {
                                                        float raduis =
                                                                fBean.getRadius() + fenceBean.getRadius();
                                                        float distance =
                                                                AMapUtils.calculateLineDistance(new com.amap.api.maps.model.LatLng(Double.parseDouble(fenceBean.getLat()),
                                                                                Double.parseDouble(fenceBean.getLng())),
                                                                        new com.amap.api.maps.model.LatLng(Double.parseDouble(fBean.getLat()),
                                                                                Double.parseDouble(fBean.getLng())));
                                                        if (raduis > distance) {
                                                            XToastUtils.toast(R.string.fence_equal_prompt);
                                                            return;
                                                        }
                                                    }
//                                                }
                                                }
                                            }
                                        }
//                                        fenceBean.setEntry(sbEntry.isChecked() ? 1 : 0);
//                                        fenceBean.setExit(sbExit.isChecked() ? 1 : 0);
                                        fenceBean.setEntry(1);
                                        fenceBean.setExit(1);
                                        dialog.dismiss();
                                        if (model == null) {
                                            fenceBean.setEnable(1);
                                            addWatchFence(fenceBean);
                                        } else {
                                            fenceBean.setEnable(model.getEnable());
                                            fenceBean.setId(model.getId());
                                            updateWatchFence(fenceBean);
                                        }
                                    }
                                }
                            });
                    builder.negativeText(getString(R.string.cancel))
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog,
                                                    @NonNull DialogAction which) {
                                    KeyboardUtils.hideSoftInput(etName);
                                    dialog.dismiss();
                                }
                            });
                    builder.autoDismiss(false);
                    mMaterialDialog = builder.show();
                }
                if (!mMaterialDialog.isShowing())
                    mMaterialDialog.show();
            }
        });
        return titleBar;
    }

    @Override
    protected void initViews() {
        if (!TextUtils.isEmpty(list))
            mItemList = mGson.fromJson(list, ArrayList.class);
        initMap();
        initLocation();
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null) {
            OperatorGroup operatorGroup = OperatorGroup.clause(OperatorGroup.clause()
                    .and(LocationModel_Table.u_id.eq(userModel.getU_id()))
                    .and(LocationModel_Table.imei.eq(deviceModel.getImei())));
            LocationModel locationModel = SQLite.select().from(LocationModel.class)
                    .where(operatorGroup)
                    .querySingle();
            if (locationModel != null) {
                try {
                    if (!TextUtils.isEmpty(locationModel.getLat()) &&
                            !TextUtils.isEmpty(locationModel.getLng())) {
                        mDeviceLatLng = new LatLng(Double.parseDouble(locationModel.getLat()),
                                Double.parseDouble(locationModel.getLng()));
                        if (locationModel.getLocationType() == 0) {
                            CoordinateConverter converter = new CoordinateConverter(getContext());
                            converter.from(CoordinateConverter.CoordType.GPS);
                            converter.coord(new com.amap.api.maps.model.LatLng(mDeviceLatLng.latitude,
                                    mDeviceLatLng.longitude));
                            com.amap.api.maps.model.LatLng convertLatLng = converter.convert();
                            if (CoordinateConverter.isAMapDataAvailable(convertLatLng.latitude,
                                    convertLatLng.longitude))
                                mDeviceLatLng = new LatLng(convertLatLng.latitude,
                                        convertLatLng.longitude);
                        }
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        if (model != null) {
            mFenceName = model.getFenceName();
            if (!TextUtils.isEmpty(model.getLat()) && !TextUtils.isEmpty
                    (model.getLng())) {
                try {
                    LatLng latLng = new LatLng(Double.parseDouble(model.getLat()),
                            Double.parseDouble(model.getLng()));
                    CoordinateConverter converter = new CoordinateConverter(getContext());
                    converter.from(CoordinateConverter.CoordType.GPS);
                    converter.coord(new com.amap.api.maps.model.LatLng(latLng.latitude,
                            latLng.longitude));
                    com.amap.api.maps.model.LatLng convertLatLng = converter.convert();
                    if (CoordinateConverter.isAMapDataAvailable(convertLatLng.latitude,
                            convertLatLng.longitude))
                        mLatLng = new LatLng(convertLatLng.latitude, convertLatLng.longitude);
                    else
                        mLatLng = latLng;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                mRadius = model.getRadius();
            }
        }
        mSbRadius.setProgress(Math.max(mRadius - 500, 0));
        mTvRadius.setText(MapUtils.getMapDistance(mRadius));
        if (TextUtils.isEmpty(mAddress))
            mAddress = getString(R.string.no_position_info);
    }

    @Override
    protected void initListeners() {
        mSbRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mRadius = progress + 500;
                mTvRadius.setText(MapUtils.getMapDistance(mRadius));
                if (mCirCle != null)
                    mCirCle.setRadius(mRadius);
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
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.googleMapContainer);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);
    }

    /**
     * 初始化定位
     */
    private void initLocation() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(CWConstant.FIVE_MINUTES);
        mLocationRequest.setMaxWaitTime(CWConstant.FIVE_MINUTES);
        mLocationRequest.setFastestInterval(CWConstant.FIVE_MINUTES);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setNumUpdates(1);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(mActivity);
        client.checkLocationSettings(builder.build());
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mActivity);
    }

    /**
     * 刷新图层
     */
    private void refreshOverlay(LatLng point) {
        if (point != null && mMap != null) {
            if (mMarker == null) {
                MarkerOptions option =
                        new MarkerOptions().position(point)
                                .draggable(false)
                                .title("").snippet("");
                option.flat(true);
                mMarker = mMap.addMarker(option);
                mCirCle = mMap.addCircle(new CircleOptions().
                        center(point).
                        radius(mRadius).
                        fillColor(ContextCompat.getColor(mActivity
                                , R.color.translucentPrimary)).
                        strokeColor(ContextCompat.getColor(mActivity,
                                R.color.colorTexNormal)).
                        strokeWidth(getResources().getDimension(R.dimen.line_size)));
            } else {
                mMarker.setPosition(point);
                mCirCle.setCenter(point);
            }
            mLatLng = point;
            mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
            mMarker.showInfoWindow();
//            GpsBean gpsBean =
//                    PositionUtils.gcj_To_Gps84(point.latitude,
//                            point.longitude);
//            LatLng latLng = new LatLng(gpsBean.getWgLat(), gpsBean.getWgLon());
//            if (AAAMapUtils.isInsideChina(latLng.latitude, latLng.longitude)) {
//                mSearchLatLng = point;
//                searchLocation(point);
//            } else {
//                mSearchLatLng = null;
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mAddress = AAAMapUtils.getAddress(mActivity, point
//                                .latitude, point.longitude);
//                        mLatLng = point;
//                        if (mActivity != null && TextUtils.isEmpty(mAddress))
//                            mAddress = mActivity.getString(R.string.address_not_find_prompt);
//                        try {
//                            if (mMap != null && mActivity != null) {
//                                mActivity.runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        try {
//                                            if (mMarker == null) {
//                                                MarkerOptions option =
//                                                        new MarkerOptions().position(point)
//                                                                .draggable(false)
//                                                                .title("").snippet("");
//                                                option.flat(true);
//                                                mMarker = mMap.addMarker(option);
//                                                mCirCle = mMap.addCircle(new CircleOptions().
//                                                        center(point).
//                                                        radius(mRadius).
//                                                        fillColor(ContextCompat.getColor(mActivity
//                                                                , R.color.translucentPrimary)).
//                                                        strokeColor(ContextCompat.getColor
//                                                        (mActivity,
//                                                                R.color.colorTexNormal)).
//                                                        strokeWidth(getResources().getDimension
//                                                        (R.dimen.line_size)));
//                                            } else {
//                                                mMarker.setPosition(point);
//                                                mCirCle.setCenter(point);
//                                            }
//                                            mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
//                                            mMarker.showInfoWindow();
//                                        } catch (Exception e) {
//                                            if (BuildConfig.DEBUG)
//                                                e.printStackTrace();
//                                        }
//                                    }
//                                });
//                            }
//                        } catch (Exception e) {
//                            if (BuildConfig.DEBUG)
//                                e.printStackTrace();
//                        }
//                    }
//                }).start();
//            }
        }
    }

    /**
     * 开始定位
     */
    private void startLocation() {
        if (mActivity == null)
            return;
        int errorCode = GooglePlayServicesUtilLight.isGooglePlayServicesAvailable(mActivity);
        switch (errorCode) {
            case ConnectionResult.SUCCESS:
                PermissionUtils.checkAndRequestMorePermissions(mActivity, needPermissions,
                        CWConstant.REQUEST_PERMISSION_MOBILE_LOCATION, new PermissionUtils
                                .PermissionRequestSuccessCallBack() {
                            @Override
                            public void onHasPermission() {
                                mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                        mLocationCallback, null /* Looper */);
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
     * 查找位置
     *
     * @param latLng 坐标
     */
    private void searchLocation(LatLng latLng) {
        GeocodeSearch geocoderSearch = new GeocodeSearch(getContext());
        geocoderSearch.setOnGeocodeSearchListener(this);
        RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(latLng.latitude, latLng
                .longitude), 0, GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);
    }

    /**
     * 增加电子围栏
     */
    private void addWatchFence(FenceBean fenceBean) {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork();
            return;
        }
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().addWatchFence(getContext(), userModel.getToken(),
                    deviceModel.getImei(), deviceModel.getD_id(), fenceBean.getFenceName(),
                    fenceBean.getLat(), fenceBean.getLng(), fenceBean.getRadius(),
                    fenceBean.getEntry(), fenceBean.getExit(), fenceBean.getEnable(), mHandler);
    }

    /**
     * 修改电子围栏
     */
    private void updateWatchFence(FenceBean fenceBean) {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork();
            return;
        }
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().updateWatchFence(getContext(), userModel.getToken(),
                    deviceModel.getImei(), deviceModel.getD_id(), fenceBean.getId(),
                    fenceBean.getFenceName(), fenceBean.getLat(), fenceBean.getLng(),
                    fenceBean.getRadius(), fenceBean.getEntry(), fenceBean.getExit(),
                    fenceBean.getEnable(), mHandler);
    }

    @SingleClick
    @OnClick({R.id.clZoomIn, R.id.clZoomOut, R.id.ibMobile, R.id.ibDevice})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clZoomIn: // 放大地图
                if (mMap != null)
                    mMap.moveCamera(CameraUpdateFactory.zoomIn());
                break;
            case R.id.clZoomOut: // 缩小地图
                if (mMap != null)
                    mMap.moveCamera(CameraUpdateFactory.zoomOut());
                break;
            case R.id.ibMobile: // 手机定位
                if (mMap != null) {
                    mIsLocation = true;
                    startLocation();
                }
                break;
            case R.id.ibDevice: // 设备定位
                if (mDeviceLatLng == null)
                    XToastUtils.toast(R.string.no_device_gps_prompt);
                else
                    refreshOverlay(mDeviceLatLng);
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
//        mMap.setInfoWindowAdapter(mInfoWindowAdapter);
//        mMap.setOnInfoWindowClickListener(mInfoWindowClickListener);
        mMap.setOnMapClickListener(mMapClickListener);
        mMap.setOnCameraMoveListener(this);
        mMap.setOnCameraIdleListener(this);
        mScaleView.update(mMap.getCameraPosition().zoom, mMap.getCameraPosition().target.latitude);
        if (mLatLng == null && mDeviceLatLng == null) {
            mIsLocation = true;
            startLocation();
        } else if (mLatLng == null)
            refreshOverlay(mDeviceLatLng);
        else
            refreshOverlay(mLatLng);
    }

    @Override
    public void onCameraMove() {
        mScaleView.update(mMap.getCameraPosition().zoom, mMap.getCameraPosition().target.latitude);
    }

    @Override
    public void onCameraIdle() {
        mScaleView.update(mMap.getCameraPosition().zoom, mMap.getCameraPosition().target.latitude);
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        com.amap.api.maps.model.LatLng latLng =
                new com.amap.api.maps.model.LatLng(regeocodeResult.getRegeocodeQuery().getPoint().getLatitude(),
                        regeocodeResult.getRegeocodeQuery().getPoint().getLongitude());
        if (latLng.latitude == mSearchLatLng.latitude && latLng.longitude == mSearchLatLng.longitude) {
            mAddress = regeocodeResult.getRegeocodeAddress().getFormatAddress();
            mLatLng = mSearchLatLng;
            if (mActivity != null && TextUtils.isEmpty(mAddress))
                mAddress = mActivity.getString(R.string.address_not_find_prompt);
            try {
                if (mMarker == null) {
                    MarkerOptions option = new MarkerOptions().position(mSearchLatLng)
                            .draggable(false)
                            .title("").snippet("");
                    option.flat(true);
                    mMarker = mMap.addMarker(option);
                    mCirCle = mMap.addCircle(new CircleOptions().
                            center(mSearchLatLng).
                            radius(mRadius).
                            fillColor(ContextCompat.getColor(mActivity
                                    , R.color.translucentPrimary)).
                            strokeColor(ContextCompat.getColor(mActivity,
                                    R.color.colorTexNormal)).
                            strokeWidth(getResources().getDimension(R.dimen.line_size)));
                } else {
                    mMarker.setPosition(mSearchLatLng);
                    mCirCle.setCenter(mSearchLatLng);
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLng(mSearchLatLng));
                mMarker.showInfoWindow();
            } catch (Exception e) {
                if (BuildConfig.DEBUG)
                    e.printStackTrace();
            }
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
    }

    /**
     * 地图点击监听器
     */
    private GoogleMap.OnMapClickListener mMapClickListener =
            new GoogleMap.OnMapClickListener() {

                @Override
                public void onMapClick(LatLng location) {
                    // TODO Auto-generated method stub
                    refreshOverlay(location);
                }
            };

    /**
     * 地图信息弹窗适配器
     */
    private GoogleMap.InfoWindowAdapter mInfoWindowAdapter = new GoogleMap.InfoWindowAdapter() {

        @Override
        public View getInfoWindow(Marker marker) {
            // TODO Auto-generated method stub
            @SuppressLint("InflateParams")
            View view = getLayoutInflater().inflate(R.layout.info_window_fence, null,
                    false);
            TextView mAddressText = view.findViewById(R.id.tvAddress);
            mAddressText.setText(getString(R.string.fence_info_description, mAddress));
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
    private GoogleMap.OnInfoWindowClickListener mInfoWindowClickListener = new GoogleMap
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
    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult != null && mIsLocation) {
                if (locationResult.getLocations().size() > 0) {
                    LatLng latLng = new LatLng(locationResult.getLocations().get(0).getLatitude()
                            , locationResult.getLocations().get(0).getLongitude());
                    CoordinateConverter converter = new CoordinateConverter(getContext());
                    converter.from(CoordinateConverter.CoordType.GPS);
                    converter.coord(new com.amap.api.maps.model.LatLng(latLng.latitude,
                            latLng.longitude));
                    com.amap.api.maps.model.LatLng convertLatLng = converter.convert();
                    if (CoordinateConverter.isAMapDataAvailable(convertLatLng.latitude,
                            convertLatLng.longitude))
                        mLatLng = new LatLng(convertLatLng.latitude, convertLatLng.longitude);
                    else
                        mLatLng = latLng;
                    refreshOverlay(mLatLng);
                }
            }
            mIsLocation = false;
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CWConstant.REQUEST_PERMISSION_MOBILE_LOCATION: // 手机定位权限
                if (PermissionUtils.isPermissionRequestSuccess(grantResults))
                    startLocation();
                else
                    XToastUtils.toast(R.string.no_location_permission_prompt);
                break;
            default:
                break;
        }
    }

    /**
     * 消息处理
     */
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NotNull Message msg) {
            try {
                RequestResultBean resultBean;
                switch (msg.what) {
                    case CWConstant.REQUEST_URL_ADD_WATCH_FENCE: // 增加电子围栏
                    case CWConstant.REQUEST_URL_UPDATE_WATCH_FENCE: // 修改电子围栏
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                XToastUtils.toast(R.string.send_success_prompt);
                                Intent intent = new Intent();
                                Bundle bundle = new Bundle();
                                intent.putExtras(bundle);
                                setFragmentResult(Activity.RESULT_OK, intent);
                                popToBack();
                            } else if (resultBean.getCode() == CWConstant.WAIT_ONLINE_UPDATE) {
                                XToastUtils.toast(R.string.wait_online_update_prompt);
                                Intent intent = new Intent();
                                Bundle bundle = new Bundle();
                                intent.putExtras(bundle);
                                setFragmentResult(Activity.RESULT_OK, intent);
                                popToBack();
                            } else if (resultBean.getCode() == CWConstant.ERROR)
                                XToastUtils.toast(R.string.send_error_prompt);
                            else
                                RequestToastUtils.toast(resultBean.getCode());
                        }
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                if (BuildConfig.DEBUG)
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
