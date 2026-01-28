package com.yyt.trackcar.ui.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CoordinateConverter;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.utils.DensityUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.dialog.bottomsheet.BottomSheet;
import com.xuexiang.xui.widget.dialog.bottomsheet.BottomSheetItemView;
import com.xuexiang.xui.widget.textview.badge.BadgeView;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.MainApplication;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.DeviceSysMsgBean;
import com.yyt.trackcar.bean.GpsBean;
import com.yyt.trackcar.bean.PostMessage;
import com.yyt.trackcar.bean.RequestBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.dbflow.DeviceInfoModel;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.DeviceSettingsModel;
import com.yyt.trackcar.dbflow.LocationModel;
import com.yyt.trackcar.dbflow.LocationModel_Table;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.ui.activity.MainActivity;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CWRequestUtils;
import com.yyt.trackcar.utils.DialogUtils;
import com.yyt.trackcar.utils.ImageLoadUtils;
import com.yyt.trackcar.utils.MapUtils;
import com.yyt.trackcar.utils.PermissionUtils;
import com.yyt.trackcar.utils.PositionUtils;
import com.yyt.trackcar.utils.RequestToastUtils;
import com.yyt.trackcar.utils.SettingSPUtils;
import com.yyt.trackcar.utils.TextColorSizeHelper;
import com.yyt.trackcar.utils.TimeUtils;
import com.yyt.trackcar.utils.XToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      LocationFragment
 * @ author:        QING
 * @ createTime:    2020-03-02 16:28
 * @ describe:      TODO 定位页面
 */
@Page(name = "Location", anim = CoreAnim.none)
public class LocationFragment extends BaseFragment implements OnMapReadyCallback,
        GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraIdleListener, GeocodeSearch
                .OnGeocodeSearchListener {
    @BindView(R.id.titleBar)
    TitleBar mTitleBar; // titleBar
    @BindView(R.id.ivElectric)
    ImageView mIvElectric; // 电量图标
    @BindView(R.id.tvInfoTitle)
    TextView mTvInfoTitle; // 定位信息标题
    @BindView(R.id.ivLocationType)
    ImageView mIvLocationType; // 定位类型图标
    @BindView(R.id.tvLocationInfo)
    TextView mTvLocationInfo; // 定位信息文本
    @BindView(R.id.ivPortrait)
    ImageView mIvPortrait; // 设备头像
    @BindView(R.id.clRefresh)
    View mClRefresh; // 更新位置布局
    @BindView(R.id.tvRefresh)
    TextView mTvRefresh; // 更新位置文字
    @BindView(R.id.scaleView)
    MapScaleView mScaleView; // 比例尺布局
    private GoogleMap mMap;
    private Marker mMarker; // 设备定位图标
    // UI相关
    private UiSettings mUiSettings;
    private BadgeView mBadgeView; // 系统消息提示红点
    private BottomSheet mBottomSheet; // 选项弹窗
    private DeviceModel mLocationDeviceModel;
    private LocationModel mLocationModel; //定位对象
    private DeviceInfoModel mInfoModel; //设备信息对象
    private LatLng mLatLng; // 定位坐标
    private LatLng mMobileLatLng; // 手机定位坐标
    private String mAddress = ""; // 定位地址
    private int mLocationType; // 定位类型
    private MainActivity mMainActivity;
    private long mLastLocationTime; // 最后一次定位时间
    private int mStartTime; // 刷新时间
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private String mDeviceImgUrl;
    private String mMarkerImgUrl;

    /**
     * 需要进行检测的权限数组
     */
    protected String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 注册订阅者
        EventBus.getDefault().register(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_location;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = mTitleBar;
        titleBar.setLeftText(R.string.location);
        titleBar.setLeftImageResource(0);
        titleBar.setLeftTextSize(titleBar.getCenterText().getTextSize());
        titleBar.getLeftText().getPaint().setFakeBoldText(true);
        TitleBar.Action nofityAction = new TitleBar.ImageAction(R.drawable.ic_notify) {
            @Override
            public void performAction(View view) {
                mBadgeView.setBadgeNumber(0);
                openNewPage(DeviceMessageFragment.class);
            }
        };
        titleBar.addAction(nofityAction);
        View nofityView = titleBar.getViewByAction(nofityAction);
        if (nofityView != null) {
            mBadgeView = new BadgeView(getContext());
            mBadgeView.bindTarget(nofityView);
            mBadgeView.setBadgeGravity(Gravity.END | Gravity.TOP);
        }
        return null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMainActivity = (MainActivity) getActivity();
    }

    @SuppressLint("DefaultLocale")
    @Override
    protected void initViews() {
        if (mStartTime > 0) {
            mClRefresh.setEnabled(false);
            mTvRefresh.setText(String.format("%ds", mStartTime));
        }
        initMap();
        initLocation();
        startLocation();
        initDeviceInfo();
        initPortrait();
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        if (mMap != null) {
            mMap.setOnCameraMoveListener(this);
            mMap.setOnCameraIdleListener(this);
        }
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
     * 初始化设备信息
     */
    private void initDeviceInfo() {
        if (mIvElectric != null)
            mIvElectric.setImageResource(R.mipmap.ic_electric_hundred);
        if (mIvLocationType != null)
            mIvLocationType.setImageResource(R.mipmap.ic_wifi);
        if (mTvLocationInfo != null)
            mTvLocationInfo.setText("");
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        mLocationDeviceModel = deviceModel;
        if (userModel != null && deviceModel != null) {
            OperatorGroup operatorGroup = OperatorGroup.clause(OperatorGroup.clause()
                    .and(LocationModel_Table.u_id.eq(userModel.getU_id()))
                    .and(LocationModel_Table.imei.eq(deviceModel.getImei())));
            mLocationModel = SQLite.select().from(LocationModel.class)
                    .where(operatorGroup)
                    .querySingle();
            if (mLocationModel != null) {
                try {
                    if (!TextUtils.isEmpty(mLocationModel.getLat()) &&
                            !TextUtils.isEmpty(mLocationModel.getLng()))
                        mLatLng = new LatLng(Double.parseDouble(mLocationModel.getLat()),
                                Double.parseDouble(mLocationModel.getLng()));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                refreshDeviceInfo();
            }
        }
        if (mTvInfoTitle != null) {
            String infoText;
            if (mLatLng == null)
                infoText = getString(R.string.no_position_info);
            else
                infoText = getString(R.string.position_info);
            DeviceInfoModel infoModel = getDeviceInfo();
            if (infoModel == null || TextUtils.isEmpty(infoModel.getNickname())) {
                if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 0)
                    mTvInfoTitle.setText(String.format("%s:%s", getString(R.string.baby),
                            infoText));
                else
                    mTvInfoTitle.setText(String.format("%s:%s", getString(R.string.device),
                            infoText));
            } else
                mTvInfoTitle.setText(String.format("%s:%s", infoModel.getNickname(), infoText));
        }
    }

    /**
     * 刷新设备图层
     */
    private void refreshOverlay(LatLng point) {
        int locationType;
        Context context = getContext();
        if (context == null)
            context = MainApplication.getInstance();
        if (mLocationModel == null) {
            locationType = 0;
            mAddress = context.getString(R.string.please_wait);
        } else {
            locationType = mLocationModel.getLocationType();
            mAddress = mLocationModel.getDesc();
            if (TextUtils.isEmpty(mAddress))
                mAddress = context.getString(R.string.please_wait);
        }
        if (point != null && mMap != null) {
            CoordinateConverter converter = new CoordinateConverter(getContext());
            LatLng latLng;
            if (locationType == 0) {
                converter.from(CoordinateConverter.CoordType.GPS);
                converter.coord(new com.amap.api.maps.model.LatLng(point.latitude,
                        point.longitude));
                com.amap.api.maps.model.LatLng convertLatLng = converter.convert();
                if (CoordinateConverter.isAMapDataAvailable(convertLatLng.latitude,
                        convertLatLng.longitude))
                    latLng = new LatLng(convertLatLng.latitude, convertLatLng.longitude);
                else
                    latLng = point;
            } else {
                if (CoordinateConverter.isAMapDataAvailable(point.latitude, point.longitude)) {
                    if (mMobileLatLng == null)
                        latLng = point;
                    else {
                        converter.from(CoordinateConverter.CoordType.GPS);
                        converter.coord(new com.amap.api.maps.model.LatLng(mMobileLatLng.latitude
                                , mMobileLatLng.longitude));
                        com.amap.api.maps.model.LatLng convertLatLng = converter.convert();
                        float distance = AMapUtils.calculateLineDistance(convertLatLng,
                                new com.amap.api.maps.model.LatLng(point.latitude,
                                        point.longitude));
                        if (distance <= 150 && locationType == 2) {
                            if (distance < 10)
                                latLng = point;
                            else {
                                LatLng moveLatLng = MapUtils.moveCoordinate(point.latitude,
                                        point.longitude, convertLatLng.latitude,
                                        convertLatLng.longitude, distance, 10);
                                LatLng mobileLatlng =
                                        new LatLng(convertLatLng.latitude + moveLatLng.latitude,
                                                convertLatLng.longitude + moveLatLng.longitude);
                                if (CoordinateConverter.isAMapDataAvailable(mobileLatlng.latitude
                                        , mobileLatlng.longitude))
                                    latLng = mobileLatlng;
                                else
                                    latLng =
                                            new LatLng(mMobileLatLng.latitude + moveLatLng.latitude,
                                                    mMobileLatLng.longitude + moveLatLng.longitude);
                            }
                        } else if (distance <= 250 && locationType == 1) {
                            if (distance < 10)
                                latLng = mMobileLatLng;
                            else {
                                LatLng moveLatLng = MapUtils.moveCoordinate(point.latitude,
                                        point.longitude, convertLatLng.latitude,
                                        convertLatLng.longitude, distance, 10);
                                LatLng mobileLatlng =
                                        new LatLng(convertLatLng.latitude + moveLatLng.latitude,
                                                convertLatLng.longitude + moveLatLng.longitude);
                                if (CoordinateConverter.isAMapDataAvailable(mobileLatlng.latitude
                                        , mobileLatlng.longitude))
                                    latLng = mobileLatlng;
                                else
                                    latLng =
                                            new LatLng(mMobileLatLng.latitude + moveLatLng.latitude,
                                                    mMobileLatLng.longitude + moveLatLng.longitude);
                            }
                        } else
                            latLng = point;
                    }
                } else {
                    if (mMobileLatLng == null)
                        latLng = point;
                    else {
                        float distance =
                                AMapUtils.calculateLineDistance(new com.amap.api.maps.model.LatLng(mMobileLatLng.latitude,
                                                mMobileLatLng.longitude),
                                        new com.amap.api.maps.model.LatLng(point.latitude,
                                                point.longitude));
                        if (distance <= 150 && locationType == 2) {
                            if (distance < 10)
                                latLng = point;
                            else {
                                LatLng moveLatLng = MapUtils.moveCoordinate(point.latitude,
                                        point.longitude, mMobileLatLng.latitude,
                                        mMobileLatLng.longitude, distance, 10);
                                LatLng mobileLatlng =
                                        new LatLng(mMobileLatLng.latitude + moveLatLng.latitude,
                                                mMobileLatLng.longitude + moveLatLng.longitude);
                                converter.from(CoordinateConverter.CoordType.GPS);
                                converter.coord(new com.amap.api.maps.model.LatLng(mobileLatlng.latitude
                                        , mobileLatlng.longitude));
                                com.amap.api.maps.model.LatLng convertLatLng = converter.convert();
                                if (CoordinateConverter.isAMapDataAvailable(convertLatLng.latitude
                                        , convertLatLng.longitude))
                                    latLng = new LatLng(convertLatLng.latitude,
                                            convertLatLng.longitude);
                                else
                                    latLng = mobileLatlng;
                            }
                        } else if (distance <= 250 && locationType == 1) {
                            if (distance < 10)
                                latLng = mMobileLatLng;
                            else {
                                LatLng moveLatLng = MapUtils.moveCoordinate(point.latitude,
                                        point.longitude, mMobileLatLng.latitude,
                                        mMobileLatLng.longitude, distance, 10);
                                LatLng mobileLatlng =
                                        new LatLng(mMobileLatLng.latitude + moveLatLng.latitude,
                                                mMobileLatLng.longitude + moveLatLng.longitude);
                                converter.from(CoordinateConverter.CoordType.GPS);
                                converter.coord(new com.amap.api.maps.model.LatLng(mobileLatlng.latitude
                                        , mobileLatlng.longitude));
                                com.amap.api.maps.model.LatLng convertLatLng = converter.convert();
                                if (CoordinateConverter.isAMapDataAvailable(convertLatLng.latitude
                                        , convertLatLng.longitude))
                                    latLng = new LatLng(convertLatLng.latitude,
                                            convertLatLng.longitude);
                                else
                                    latLng = mobileLatlng;
                            }
                        } else
                            latLng = point;
                    }
                }
            }
            boolean isNull = false;
            if (mMarker == null) {
                MarkerOptions option = new MarkerOptions().position(latLng)
                        .draggable(false)
                        .title("").snippet("");
                mMarker = mMap.addMarker(option);
                isNull = true;
            } else
                mMarker.setPosition(latLng);
            initLocationMarkInfos(mMarker, mDeviceImgUrl, isNull);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }
        refreshAddressInfo();
//        if (point != null && mMap != null) {
//            LatLng latLng;
//            boolean isInChina = false;
//            if (AAAMapUtils.isInsideChina(point.latitude, point.longitude)) {
//                GpsBean gpsBean = PositionUtils.gps84_To_Gcj02(point.latitude, point.longitude);
//                latLng = new LatLng(gpsBean.getWgLat(), gpsBean.getWgLon());
//                isInChina = true;
//            } else
//                latLng = point;
//            if (mMarker == null) {
//                MarkerOptions option = new MarkerOptions().position(latLng)
//                        .draggable(false)
//                        .title("").snippet("");
//                mMarker = mMap.addMarker(option);
//            } else
//                mMarker.setPosition(latLng);
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
////            getAddress(point);
//            if (mLocationType != 0) {
//                mAddress = getString(R.string.get_position_info);
//                refreshAddressInfo();
//                if (isInChina)
//                    searchLocation(latLng);
//                else {
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            String address = AAAMapUtils.getAddress(mActivity, point.latitude,
//                                    point.longitude);
//                            if (mLatLng != null && point.latitude == mLatLng.latitude && point
//                            .longitude == mLatLng.longitude) {
//                                if (mActivity != null) {
//                                    mActivity.runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            mAddress = address;
//                                            if (TextUtils.isEmpty(mAddress))
//                                                mAddress = getString(R.string.no_address_info);
//                                            refreshAddressInfo();
//                                        }
//                                    });
//                                }
//                            }
//                        }
//                    }).start();
//                }
//            }
////            new Thread(new Runnable() {
////                @Override
////                public void run() {
////                    mAddress = AAAMapUtils.getAddress(mActivity, point.latitude, point.longitude);
////                    if (TextUtils.isEmpty(mAddress))
////                        mAddress = getString(R.string.no_address_info);
////                    mActivity.runOnUiThread(new Runnable() {
////                        @Override
////                        public void run() {
////                            try {
////                                if (mMarker == null) {
////                                    MarkerOptions option = new MarkerOptions().position(point)
////                                            .draggable(false)
////                                            .title("").snippet("");
////                                    mMarker = mMap.addMarker(option);
////                                } else
////                                    mMarker.setPosition(point);
////                                mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
////                                refreshAddressInfo();
////                            } catch (Exception e) {
////                                if (BuildConfig.DEBUG)
////                                    e.printStackTrace();
////                            }
////                        }
////                    });
////                }
////            }).start();
//        }
    }

    /**
     * 刷新位置信息
     */
    private void refreshAddressInfo() {
        if (mTvLocationInfo != null) {
            if (mLatLng == null || mLocationModel == null || TextUtils.isEmpty(mLocationModel.getDesc()))
                mTvLocationInfo.setText(mAddress);
            else {
                String time;
                if (mLocationModel == null || mLocationModel.getUploadtime() == 0)
                    time = String.format(" %s ", getString(R.string.now));
                else
                    time = String.format(" %s ", TimeUtils.getDateDescriptionByNow(mActivity,
                            mLocationModel.getUploadtime()));
                String text;
                if (mLatLng == null || mLocationModel == null || TextUtils.isEmpty(mLocationModel.getDesc()))
                    text = String.format("%s %s", mAddress, time);
                else
                    text = String.format("%s %s", mAddress, time);
//                text = String.format("%s%s %s", mAddress, getString(R.string.accuracy_info,
//                        mLocationModel.getAccuracy()), time);
                List<TextColorSizeHelper.SpanInfo> list = new ArrayList<>();
                list.add(new TextColorSizeHelper.SpanInfo(time, DensityUtils.sp2px(12),
                        ContextCompat.getColor(mActivity, R.color.white),
                        ContextCompat.getColor(mActivity, R.color.colorTexNormal),
                        DensityUtils.dp2px(2), true));
                mTvLocationInfo.setText(TextColorSizeHelper.getTextSpan(mActivity, text, list));
            }
            if (mTvInfoTitle != null) {
                String infoText;
                if (mLatLng == null)
                    infoText = getString(R.string.no_position_info);
                else
                    infoText = getString(R.string.position_info);
                if (mTvInfoTitle != null) {
                    DeviceInfoModel infoModel = getDeviceInfo();
                    if (infoModel == null || TextUtils.isEmpty(infoModel.getNickname())) {
                        if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 0)
                            mTvInfoTitle.setText(String.format("%s:%s", getString(R.string.baby),
                                    infoText));
                        else
                            mTvInfoTitle.setText(String.format("%s:%s", getString(R.string.device),
                                    infoText));
                    } else
                        mTvInfoTitle.setText(String.format("%s:%s", infoModel.getNickname(),
                                infoText));
                }
            }
        }
    }

    /**
     * 刷新位置信息
     */
    private void refreshAddressInfo(String infoText, String refreshTime) {
        if (mTvLocationInfo != null) {
            if (mLatLng == null || mLocationModel == null || TextUtils.isEmpty(mLocationModel.getDesc()))
                mTvLocationInfo.setText(infoText);
            else {
                String time = String.format(" %s ", refreshTime);
                String text = String.format("%s %s", infoText, time);
                List<TextColorSizeHelper.SpanInfo> list = new ArrayList<>();
                list.add(new TextColorSizeHelper.SpanInfo(time, DensityUtils.sp2px(12),
                        ContextCompat.getColor(mActivity, R.color.white),
                        ContextCompat.getColor(mActivity, R.color.colorTexNormal),
                        DensityUtils.dp2px(2), true));
                mTvLocationInfo.setText(TextColorSizeHelper.getTextSpan(mActivity, text, list));
            }
        }
    }

    /**
     * 刷新设备信息
     */
    private void refreshDeviceInfo() {
        if (mLocationModel != null) {
            if (mIvElectric != null) {
                if (mLocationModel.getElectricity() == 255)
                    mIvElectric.setImageResource(R.mipmap.ic_electric_charge);
                else if (mLocationModel.getElectricity() <= 0)
                    mIvElectric.setImageResource(R.mipmap.ic_electric_zero);
                else if (mLocationModel.getElectricity() <= 20)
                    mIvElectric.setImageResource(R.mipmap.ic_electric_twenty);
                else if (mLocationModel.getElectricity() <= 40)
                    mIvElectric.setImageResource(R.mipmap.ic_electric_forty);
                else if (mLocationModel.getElectricity() <= 60)
                    mIvElectric.setImageResource(R.mipmap.ic_electric_sixty);
                else if (mLocationModel.getElectricity() <= 80)
                    mIvElectric.setImageResource(R.mipmap.ic_electric_eighty);
                else
                    mIvElectric.setImageResource(R.mipmap.ic_electric_hundred);
            }
            if (mIvLocationType != null) {
                if (mLocationModel.getLocationType() == 1)
                    mIvLocationType.setImageResource(R.mipmap.ic_lbs);
                else if (mLocationModel.getLocationType() == 2)
                    mIvLocationType.setImageResource(R.mipmap.ic_wifi);
                else
                    mIvLocationType.setImageResource(R.mipmap.ic_gps);
            }
        }
    }

    /**
     * 刷新设备头像
     */
    private void initPortrait() {
        if (mIvPortrait != null) {
            UserModel userModel = getUserModel();
            DeviceModel deviceModel = getDevice();
            int imgRes;
            if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 0)
                imgRes = R.mipmap.ic_device_portrait;
            else
                imgRes = R.mipmap.ic_default_pigeon_marker;
            if (userModel != null && deviceModel != null) {
                mInfoModel = getDeviceInfo();
                ImageLoadUtils.loadPortraitImage(getContext(), mInfoModel.getHead(),
                        imgRes, mIvPortrait);
            } else
                ImageLoadUtils.loadPortraitImage(getContext(), "", imgRes,
                        mIvPortrait);
        }
        if (mInfoModel == null)
            mDeviceImgUrl = "";
        else
            mDeviceImgUrl = mInfoModel.getHead();
    }

    /**
     * 切换地图类型
     */
    private void switchMapType() {
        if (mMap != null) {
            if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL)
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            else
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }

    /**
     * 刷新数据
     */
    public void refreshData() {
//        if (getContext() == null)
//            return;
        startLocation();
        DeviceModel deviceModel = getDevice();
        if (deviceModel != null && (mLocationDeviceModel == null || !deviceModel.getImei().equals(mLocationDeviceModel.getImei()))) {
            mLatLng = null;
            mInfoModel = null;
            mLocationModel = null;
            mAddress = null;
            mMarker = null;
            mMarkerImgUrl = null;
            if (mMap != null)
                mMap.clear();
            initDeviceInfo();
            initPortrait();
            refreshOverlay(mLatLng);
            getLastLocation();
//            requestLocation();
            if (getContext() == null)
                refreshAddressInfo(MainApplication.getInstance().getString(R.string.please_wait),
                        MainApplication.getInstance().getString(R.string.now));
            else
                refreshAddressInfo(getString(R.string.please_wait), getString(R.string.now));
            mLocationType = 1;
//            onPostMessage(new PostMessage(CWConstant.POST_MESSAGE_CHANGE_DEVICE));
        }
//        else if (System.currentTimeMillis() - mLastLocationTime > CWConstant.FIVE_MINUTES) {
        else {
            initPortrait();
//            getLastLocation();
            if (System.currentTimeMillis() - mLastLocationTime > 60 * 1000) {
                getLastLocation();
//                requestLocation();
                if (getContext() == null)
                    refreshAddressInfo(MainApplication.getInstance().getString(R.string.please_wait), MainApplication.getInstance().getString(R.string.now));
                else
                    refreshAddressInfo(getString(R.string.please_wait), getString(R.string.now));
                mLocationType = 1;
            }
        }
    }

    //初始化位置标注信息
    private void initLocationMarkInfos(Marker marker, String imageUrl, boolean isNull) {
        if (TextUtils.isEmpty(imageUrl) && !TextUtils.isEmpty(mMarkerImgUrl)) {
            @SuppressLint("InflateParams")
            View markerView = getLayoutInflater().inflate(R.layout.marker_avator_view, null);
            int imgRes;
            if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 0)
                imgRes = R.mipmap.ic_device_portrait;
            else
                imgRes = R.mipmap.ic_default_pigeon_marker;
            ImageView ivPortrait = markerView.findViewById(R.id.ivPortrait);
            ivPortrait.setImageResource(imgRes);
            Bitmap bitmap = convertViewToBitmap(markerView);
            if (bitmap != null && marker != null) {
                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
                marker.setIcon(bitmapDescriptor);
                mMarkerImgUrl = imageUrl;
            }
        } else if (!TextUtils.isEmpty(imageUrl) && !imageUrl.equals(mMarkerImgUrl)) {
            @SuppressLint("InflateParams")
            View markerView = getLayoutInflater().inflate(R.layout.marker_avator_view, null);
            int imgRes;
            if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 0)
                imgRes = R.mipmap.ic_device_portrait;
            else
                imgRes = R.mipmap.ic_default_pigeon_marker;
            ImageView ivPortrait = markerView.findViewById(R.id.ivPortrait);
            ivPortrait.setImageResource(imgRes);
            Bitmap bitmap = convertViewToBitmap(markerView);
            if (bitmap != null && marker != null) {
                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
                marker.setIcon(bitmapDescriptor);
                returnPictureView(imageUrl, new ResultListener() {
                    @Override
                    public void onReturnResult(View view) {
                        if (!imageUrl.equals(mMarkerImgUrl)) {
                            Bitmap resultBitMap = convertViewToBitmap(view);
                            if (resultBitMap != null) {
                                mMarkerImgUrl = imageUrl;
                                marker.setIcon(BitmapDescriptorFactory.fromBitmap(resultBitMap));
                            }
                        }
                    }
                });
            }
        } else if (isNull) {
            @SuppressLint("InflateParams")
            View markerView = getLayoutInflater().inflate(R.layout.marker_avator_view, null);
            int imgRes;
            if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 0)
                imgRes = R.mipmap.ic_device_portrait;
            else
                imgRes = R.mipmap.ic_default_pigeon_marker;
            ImageView ivPortrait = markerView.findViewById(R.id.ivPortrait);
            ivPortrait.setImageResource(imgRes);
            Bitmap bitmap = convertViewToBitmap(markerView);
            if (bitmap != null && marker != null) {
                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
                marker.setIcon(bitmapDescriptor);
                mMarkerImgUrl = imageUrl;
            }
        }
    }

    //将图片加载到布局中
    private void returnPictureView(String imagUrl, final ResultListener resultListener) {
        @SuppressLint("InflateParams")
        View markerView = getLayoutInflater().inflate(R.layout.marker_avator_view, null);
        int imgRes;
        if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 0)
            imgRes = R.mipmap.ic_device_portrait;
        else
            imgRes = R.mipmap.ic_default_pigeon_marker;
        ImageView ivPortrait = markerView.findViewById(R.id.ivPortrait);
        ivPortrait.setImageResource(imgRes);
        Glide.with(this)
                .asBitmap()
                .load(imagUrl)
                .error(imgRes)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<?
                            super Bitmap> transition) {
                        ivPortrait.setImageBitmap(resource);
                        resultListener.onReturnResult(markerView);
                    }
                });
    }

    //回调接口
    private interface ResultListener {
        void onReturnResult(View view);
    }

    /**
     * view转换bitmap
     *
     * @param view 转换的view
     * @return bitmap
     */
    public static Bitmap convertViewToBitmap(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View
                .MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        return view.getDrawingCache();
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
                .longitude), 0,
                GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);
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
     * 显示更多对话框
     */
    private void showMoreDialog() {
        if (mBottomSheet == null || !mBottomSheet.isShowing()) {
            Context context = getContext();
            if (context == null)
                context = mActivity;
            BottomSheet.BottomGridSheetBuilder builder =
                    new BottomSheet.BottomGridSheetBuilder(context);
            builder.setButtonText(getString(R.string.cancel));
            String findStr;
            int findImg;
            if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 0) {
                findStr = getString(R.string.find_child);
                findImg = R.mipmap.ic_find_child;
            } else {
                findStr = getString(R.string.find_device_navi);
                findImg = R.mipmap.ic_find_device;
            }
            mBottomSheet = builder.addItem(R.mipmap.ic_satellite_map,
                    getString(R.string.satellite_map), 0,
                    BottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                    .addItem(findImg, findStr, 1,
                            BottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                    .addItem(R.mipmap.ic_navi_track, getString(R.string.navi_track), 2,
                            BottomSheet.BottomGridSheetBuilder.FIRST_LINE)
//                    .addItem(R.mipmap.ic_position_rectification,
//                            getString(R.string.fence), 3,
//                            BottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                    .addItem(R.mipmap.ic_location_settings,
                            getString(R.string.locate_mode_explain), 4,
                            BottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                    .addItem(R.mipmap.ic_location_mode,
                            getString(R.string.location_mode), 5,
                            BottomSheet.BottomGridSheetBuilder.SECOND_LINE)
                    .setOnSheetItemClickListener(new BottomSheet.BottomGridSheetBuilder.OnSheetItemClickListener() {
                        @Override
                        public void onClick(BottomSheet dialog,
                                            BottomSheetItemView itemView) {
                            dialog.dismiss();
                            int tag = (int) itemView.getTag();
                            Bundle bundle;
                            switch (tag) {
                                case 0: // 卫星地图
                                    switchMapType();
                                    break;
                                case 1: // 找小孩
                                    showNaviDialog();
                                    break;
                                case 2: // 导航追踪
                                    openNewPage(NaviFragment.class);
                                    break;
                                case 3: // 电子围栏
                                    openNewPage(FenceFragment.class);
                                    break;
//                                case 3: // 位置纠偏
//                                    if (mLatLng == null)
//                                        XToastUtils.toast(R.string.no_device_gps_prompt);
//                                    else if (mMaterialDialog == null || !mMaterialDialog
//                                    .isShowing()) {
//                                        Context context = getContext();
//                                        if (context == null)
//                                            context = mActivity;
//                                        mMaterialDialog = new MaterialDialog.Builder(context)
//                                                .title(R.string.prompt)
//                                                .content(R.string
//                                                .position_rectification_overtime_content)
//                                                .positiveText(R.string.i_know)
//                                                .show();
//                                    }
//                                    break;
                                case 4: // 定位设置
                                    openNewPage(LocationSettingsFragment.class);
                                    break;
                                case 5: // 定位模式
                                    bundle = new Bundle();
                                    bundle.putString(CWConstant.TITLE,
                                            itemView.getTextView().getText().toString());
                                    bundle.putInt(CWConstant.TYPE, 2);
                                    openNewPage(CustomSelectorFragment.class, bundle);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }).build();
            mBottomSheet.show();
        }
    }

    /**
     * 显示导航对话框
     */
    private void showNaviDialog() {
        Context context = getContext();
        if (context == null)
            context = mActivity;
        BottomSheet.BottomListSheetBuilder builder =
                new BottomSheet.BottomListSheetBuilder(context)
                        .setIsCenter(true)
                        .setOnSheetItemClickListener(new BottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                            @Override
                            public void onClick(BottomSheet dialog, View itemView,
                                                int position, String tag) {
                                dialog.dismiss();
                                if (mMarker == null || mMarker.getPosition() == null)
                                    XToastUtils.toast(R.string.no_device_gps_prompt);
                                else if (!"4".equals(tag) && !TextUtils.isEmpty(tag)) {
                                    LatLng latLng = mMarker.getPosition();
//                                    if (AAAMapUtils.isInsideChina(mLatLng.latitude,
//                                            mLatLng.longitude)) {
//                                        GpsBean gpsBean =
//                                                PositionUtils.gps84_To_Gcj02(mLatLng.latitude,
//                                                        mLatLng.longitude);
//                                        latLng = new LatLng(gpsBean.getWgLat(), gpsBean
//                                        .getWgLon());
//                                    } else
//                                        latLng = mLatLng;
                                    MapUtils.naviMap(mActivity, latLng.latitude,
                                            latLng.longitude, Integer.parseInt(tag));
                                }
                            }
                        });
//        if (AAAAppUtils.isInstallApp("com.autonavi.minimap"))
//            builder.addItem(getString(R.string.gaode_map));
//        if (AAAAppUtils.isInstallApp("com.baidu.BaiduMap"))
//            builder.addItem(getString(R.string.baidu_map));
//        if (AAAAppUtils.isInstallApp("com.tencent.map"))
//            builder.addItem(getString(R.string.tencent_map));
        builder.addItem(getString(R.string.google_map), "3");
        builder.addItem(getString(R.string.cancel), "4");
        mBottomSheet = builder.build();
        mBottomSheet.show();
    }

    /**
     * 获取定位信息
     */
    public void getLastLocation() {
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null) {
            mLastLocationTime = System.currentTimeMillis();
            CWRequestUtils.getInstance().getLastLocation(getContext(), userModel.getToken(),
                    deviceModel.getImei(), deviceModel.getD_id(), mHandler);
        }
    }

    /**
     * 请求设备定位
     */
    private void requestLocation() {
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().requestLocation(getContext(), getIp(),
                    userModel.getToken(), deviceModel.getImei(), mHandler);
    }

    /**
     * 获取设备地址
     */
    private void getAddress(LatLng latLng) {
        CWRequestUtils.getInstance().getAddress(getContext(), String.valueOf(latLng.latitude),
                String.valueOf(latLng.longitude), mHandler);
    }

    @SingleClick
    @OnClick({R.id.clSwitch, R.id.clNavi, R.id.clMore, R.id.clZoomIn, R.id.clZoomOut,
            R.id.clRefresh,R.id.setting})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clSwitch: // 卫星地图切换
                switchMapType();
                break;
            case R.id.clNavi: // 轨迹
                openNewPage(NaviFragment.class);
                break;
            case R.id.clMore: // 更多
                showMoreDialog();
                break;
            case R.id.clZoomIn: // 放大地图
                if (mMap != null)
                    mMap.moveCamera(CameraUpdateFactory.zoomIn());
                break;
            case R.id.clZoomOut: // 缩小地图
                if (mMap != null)
                    mMap.moveCamera(CameraUpdateFactory.zoomOut());
                break;
            case R.id.clRefresh: // 更新位置
//                refreshOverlay(mLatLng);
                if (NetworkUtils.isNetworkAvailable()) {
                    if (mClRefresh.isEnabled()) {
                        mClRefresh.setEnabled(false);
                        mStartTime = 60;
                        mTimer.start();
                    }
                    requestLocation();
                    refreshAddressInfo(getString(R.string.get_position_now),
                            getString(R.string.now));
                    mLocationType = 0;
                } else
                    RequestToastUtils.toastNetwork();
                break;
            case R.id.setting:
                openNewPage(TrackSettingFragment.class);
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
        mScaleView.update(mMap.getCameraPosition().zoom, mMap.getCameraPosition().target.latitude);
        initListeners();
//        if (mLatLng == null)
//            mLatLng = new LatLng(114, 28);
        refreshOverlay(mLatLng);
    }

    @Override
    public void onCameraMove() {
        if (mScaleView != null && mMap != null)
            mScaleView.update(mMap.getCameraPosition().zoom,
                    mMap.getCameraPosition().target.latitude);
    }

    @Override
    public void onCameraIdle() {
        if (mScaleView != null && mMap != null)
            mScaleView.update(mMap.getCameraPosition().zoom,
                    mMap.getCameraPosition().target.latitude);
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        com.amap.api.maps.model.LatLng latLng =
                new com.amap.api.maps.model.LatLng(regeocodeResult.getRegeocodeQuery().getPoint().getLatitude(),
                        regeocodeResult.getRegeocodeQuery().getPoint().getLongitude());
        if (MapUtils.isInsideChina(mLatLng.latitude, mLatLng.longitude)) {
            GpsBean gpsBean = PositionUtils.gps84_To_Gcj02(mLatLng.latitude, mLatLng.longitude);
            if (latLng.latitude == gpsBean.getWgLat() && latLng.longitude == gpsBean.getWgLon()) {
                mAddress = regeocodeResult.getRegeocodeAddress().getFormatAddress();
                if (TextUtils.isEmpty(mAddress))
                    mAddress = getString(R.string.no_address_info);
                refreshAddressInfo();
            }
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
    }

    /**
     * 定位监听
     */
    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult != null) {
                if (locationResult.getLocations().size() > 0) {
                    mMobileLatLng = new LatLng(locationResult.getLocations().get(0).getLatitude()
                            , locationResult.getLocations().get(0).getLongitude());
                    if (mLatLng == null && mMap != null)
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(mMobileLatLng));
                }
            }
        }
    };

    /**
     * 倒计时
     */
    private CountDownTimer mTimer = new CountDownTimer(60 * 1000, 1000) {

        @SuppressLint("DefaultLocale")
        @Override
        public void onTick(long millisUntilFinished) {
            // TODO Auto-generated method stub
            mStartTime = Math.round(millisUntilFinished / 1000.0f);
            if (mTvRefresh != null)
                mTvRefresh.setText(String.format("%ds", mStartTime));
        }

        @Override
        public void onFinish() {
            // TODO Auto-generated method stub
            mStartTime = 0;
            if (mTvRefresh != null) {
                mTvRefresh.setText(R.string.location_refresh);
                mClRefresh.setEnabled(true);
                if (mLocationType == 0) {
                    mLocationType = 1;
                    getLastLocation();
                }
            }
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
                RequestBean requestBean;
                UserModel userModel;
                switch (msg.what) {
                    case CWConstant.REQUEST_URL_GET_LAST_LOCATION: // 获取定位信息
                        if (msg.obj == null) {
                        } else {
                            resultBean = (RequestResultBean) msg.obj;
//                            mLastLocationTime = System.currentTimeMillis();
                            userModel = getUserModel();
                            requestBean =
                                    mGson.fromJson(mGson.toJson(resultBean.getRequestObject()),
                                            RequestBean.class);
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                if (userModel != null) {
                                    LocationModel locationModel =
                                            mGson.fromJson(mGson.toJson(resultBean.getResultBean()),
                                                    LocationModel.class);
                                    locationModel.setU_id(userModel.getU_id());
                                    locationModel.setImei(requestBean.getImei());
                                    if (!TextUtils.isEmpty(locationModel.getLat()) && !TextUtils.isEmpty
                                            (locationModel.getLng())) {
                                        locationModel.save();
                                        mLocationModel = locationModel;
                                        mLatLng =
                                                new LatLng(Double.parseDouble(locationModel.getLat()),
                                                        Double.parseDouble(locationModel.getLng()));
                                    }
                                    refreshDeviceInfo();
                                }
                            } else {
                                if (userModel != null) {
                                    LocationModel locationModel =
                                            mGson.fromJson(mGson.toJson(resultBean.getResultBean()),
                                                    LocationModel.class);
                                    OperatorGroup operatorGroup =
                                            OperatorGroup.clause(OperatorGroup.clause()
                                                    .and(LocationModel_Table.u_id.eq(userModel.getU_id()))
                                                    .and(LocationModel_Table.imei.eq(requestBean.getImei())));
                                    mLocationModel = SQLite.select().from(LocationModel.class)
                                            .where(operatorGroup)
                                            .querySingle();
                                    if (mLocationModel == null) {
                                        mLocationModel = new LocationModel();
                                        mLocationModel.setU_id(userModel.getU_id());
                                        mLocationModel.setImei(requestBean.getImei());
                                    }
                                    mLocationModel.setElectricity(locationModel.getElectricity());
                                    mLocationModel.setAccuracy(locationModel.getAccuracy());
                                    mLocationModel.setStep(locationModel.getStep());
                                    mLocationModel.save();
                                    refreshDeviceInfo();
                                }
                            }
                        }
                        refreshOverlay(mLatLng);
                        break;
                    case CWConstant.REQUEST_URL_REQUEST_LOCATION: // 请求设备定位
                        if (msg.obj == null) {
                            mLocationType = 1;
                            getLastLocation();
                        } else {
                            mLastLocationTime = System.currentTimeMillis();
                            resultBean = (RequestResultBean) msg.obj;
                            if (!TextUtils.isEmpty(resultBean.getService_ip()) && !resultBean.getService_ip().equals(resultBean.getLast_online_ip())) {
                                userModel = getUserModel();
                                DeviceModel deviceModel = getDevice();
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                if (userModel != null && deviceModel != null && deviceModel.getImei().equals(requestBean.getImei())) {
                                    DeviceSettingsModel settingsModel = getDeviceSettings();
                                    settingsModel.setIp(resultBean.getLast_online_ip());
                                    settingsModel.save();
                                    CWRequestUtils.getInstance().requestLocation(getContext(),
                                            resultBean.getLast_online_ip(), userModel.getToken(),
                                            deviceModel.getImei(), mHandler);
                                }
                            } else {
                                if(resultBean.getCode() == CWConstant.SUCCESS)
                                    XToastUtils.toast(R.string.send_success_prompt);
                                else if (resultBean.getCode() == CWConstant.NOT_ONLINE
                                        || CWConstant.WAIT_ONLINE_UPDATE == resultBean.getCode())
                                    XToastUtils.toast(R.string.location_no_net_prompt);
                                if (!(CWConstant.SUCCESS == resultBean.getCode()
                                        || CWConstant.NOT_ONLINE == resultBean.getCode()
                                        || CWConstant.WAIT_ONLINE_UPDATE == resultBean.getCode())) {
                                    mLocationType = 1;
                                    getLastLocation();
                                }
                            }
                        }
                        break;
                    case CWConstant.REQUEST_URL_GET_ADDRESS: // 获取地址

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPostMessage(PostMessage event) {
        if (CWConstant.POST_MESSAGE_CHANGE_DEVICE == event.getType()) {
            if (mMainActivity != null) {
                if (mMainActivity.getBottomNavigation() != null && mMainActivity.getBottomNavigation().getSelectedItemId() == R.id.navLocation)
                    refreshData();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPostMsgBean(DeviceSysMsgBean event) {
        if (CWConstant.LOCATION == event.getType()) {
            String[] array = event.getMsg().split(",");
            if (array.length >= 4 && !TextUtils.isEmpty(array[0])) {
                UserModel userModel = getUserModel();
                if (userModel != null && !TextUtils.isEmpty(array[1]) && !TextUtils.isEmpty(array[2])) {
                    OperatorGroup operatorGroup =
                            OperatorGroup.clause(OperatorGroup.clause()
                                    .and(LocationModel_Table.u_id.eq(userModel.getU_id()))
                                    .and(LocationModel_Table.imei.eq(array[0])));
                    LocationModel locationModel = SQLite.select().from(LocationModel.class)
                            .where(operatorGroup)
                            .querySingle();
                    if (locationModel == null) {
                        locationModel = new LocationModel();
                        locationModel.setU_id(userModel.getU_id());
                        locationModel.setImei(array[0]);
                        locationModel.setElectricity(100);
                        locationModel.setAccuracy(10);
                    }
                    locationModel.setLat(array[1]);
                    locationModel.setLng(array[2]);
                    locationModel.setLocationType(Integer.parseInt(array[3]));
                    Date date = TimeUtils.formatUTCC(event.getTime(), null);
                    locationModel.setUploadtime(date.getTime());
//                    locationModel.setDesc(event.getIntroduction());
                    locationModel.save();
                    DeviceModel deviceModel = getDevice();
                    if (deviceModel != null && deviceModel.getImei().equals(array[0])) {
                        mLocationType = 1;
                        mLocationModel = locationModel;
                        mLatLng = new LatLng(Double.parseDouble(mLocationModel.getLat()),
                                Double.parseDouble(mLocationModel.getLng()));
                        refreshOverlay(mLatLng);
                        refreshDeviceInfo();
                        getLastLocation();
                    }
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMainActivity != null) {
            if (mMainActivity.getBottomNavigation() != null && mMainActivity.getBottomNavigation().getSelectedItemId() == R.id.navLocation)
                refreshData();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        // 注销订阅者
        EventBus.getDefault().unregister(this);
        mMap = null;
        DialogUtils.dismiss(mBottomSheet);
        stopLocation();
        mTimer.cancel();
        super.onDestroy();
    }
}
