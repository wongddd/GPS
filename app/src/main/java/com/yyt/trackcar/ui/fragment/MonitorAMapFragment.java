package com.yyt.trackcar.ui.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.baoyz.actionsheet.ActionSheet;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.dialog.bottomsheet.BottomSheet;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAABaseResponseBean;
import com.yyt.trackcar.bean.AAARequestBean;
import com.yyt.trackcar.bean.AAATrackModel;
import com.yyt.trackcar.bean.AMapMovementTrack;
import com.yyt.trackcar.bean.BaseItemBean;
import com.yyt.trackcar.bean.ListResponseBean;
import com.yyt.trackcar.bean.PostMessage;
import com.yyt.trackcar.dbflow.AAADeviceModel;
import com.yyt.trackcar.dbflow.AAAUserModel;
import com.yyt.trackcar.ui.activity.HistoryActivity;
import com.yyt.trackcar.ui.activity.WebActivity;
import com.yyt.trackcar.ui.adapter.CardDeviceAdapter;
import com.yyt.trackcar.ui.adapter.DeviceSelectorAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.AAAStringUtils;
import com.yyt.trackcar.utils.BitmapBlobUtils;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.Constant;
import com.yyt.trackcar.utils.DataUtils;
import com.yyt.trackcar.utils.DeviceType;
import com.yyt.trackcar.utils.ErrorCode;
import com.yyt.trackcar.utils.FinalMapUtils;
import com.yyt.trackcar.utils.MapUtils;
import com.yyt.trackcar.utils.NewMapUtils;
import com.yyt.trackcar.utils.PermissionUtils;
import com.yyt.trackcar.utils.RequestToastUtils;
import com.yyt.trackcar.utils.SettingSPUtils;
import com.yyt.trackcar.utils.StringUtils;
import com.yyt.trackcar.utils.TConstant;
import com.yyt.trackcar.utils.TimeUtils;
import com.yyt.trackcar.utils.TransformImageAppearance;
import com.yyt.trackcar.utils.XToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 项目名：   传信鸽
 * 包名：     com.yyt.trackcar.ui.fragment
 * 文件名：   TrackingFragment
 * 创建者：   QING
 * 创建时间： 2018/4/21 19:31
 * 描述：     TODO 监控中心fragment
 */
@SuppressLint("NonConstantResourceId")
@Page(name = "NewMonitorFragment", anim = CoreAnim.none)
public class MonitorAMapFragment extends BaseFragment
        implements GeocodeSearch.OnGeocodeSearchListener, View.OnClickListener,
        ActionSheet.ActionSheetListener, BaseQuickAdapter.OnItemChildClickListener {
    // 地图
    private AMapLocationClient locationClient;
    private AMapLocationClientOption locationOption;
    private AMap mAMap;
    @BindView(R.id.tracking_map)
    MapView mMapView;
    private Marker mMarker; // 设备定位图标
    private Marker mMobileMarker; // 手机定位图标
    private Marker mSearchMarker; // 搜索位置图标

    // UI相关
    private UiSettings mUiSettings;

    @BindView(R.id.tracking_count_down_content)
    TextView mCountDownText; // 倒计时文本
    @BindView(R.id.tracking_car_btn)
    ImageButton mCarBtn; // 定位车辆按钮
    @BindView(R.id.tracking_mobile_btn)
    ImageButton mMobileBtn; // 定位手机按钮
    @BindView(R.id.tracking_distance_btn)
    ImageButton mDistanceBtn; // 查看距离按钮
    @BindView(R.id.tracking_map_type_btn)
    ImageButton mMapTypeBtn; //显示地图类型
    @BindView(R.id.tracking_zoom_in_btn)
    ImageButton mZoomInBtn;  //地图放大
    @BindView(R.id.tracking_zoom_out_btn)
    ImageButton mZoomOutBtn;  //地方缩小
    @BindView(R.id.tracking_weather_content)
    TextView mWeatherContent; //天气信息文本
    @BindView(R.id.tracking_message_content)
    TextView mMessageContent; // 定位信息文本
    @BindView(R.id.ivTrackMode)
    ImageView mIvTrackMode;
    @BindView(R.id.ivRefresh)
    ImageView mIvRefresh;
    @BindView(R.id.tvRefresh)
    TextView mTvRefresh;

    @BindView(R.id.tracking_distance_content) //手机与设备距离文本
    TextView tvDistantDisplay;
    @BindView(R.id.tracking_locus_btn)
    ImageButton mLocusBtn;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.selectAll)
    CheckBox selectAll;

    @BindView(R.id.tracking_show_marker)
    ImageButton mShowMarker;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.pagerWrap)
    ViewGroup mPagerWrap;
    private RecyclerView mPagerRecyclerView;
    private CardDeviceAdapter mPagerAdapter;
    private List<BaseItemBean> mPagerItemList = new ArrayList<>(); // 列表
    private SnapHelper mSnapHelper;
    private int mSelectIndex;

    private long stampTimeWhenClickSwitchShowMarker = 0;
    private long stampTimeWhenClickSwitchMapType = 0;

    private final List<AAADeviceModel> mItemList = new ArrayList<>(); // 设备列表
    private final List<LatLng> mLatLngItemList = new ArrayList<>(); // 轨迹列表
    private LatLng mLatLng; // 定位坐标
    private LatLng mMobileLatLng; // 手机定位坐标
    private Timer mTimer; // 计时器
    private long countDown = 0; // 计时数
    private AAADeviceModel mDeviceModel; // 车辆信息
    private Bundle mSavedInstanceState;
    private int mLocationRefreshInterval; // 地图定位点刷新间隔

    private Context mContext;
    private final List<AMapMovementTrack> tracks = new ArrayList<>();
    private final List<Boolean> statusList = new ArrayList<>();  //设备选中状态临时列表
    private final List<LatLng> shownLatLgnList = new ArrayList<>(); //选中设备的位置
    private final Map<String, List<AAATrackModel>> trackMap = new HashMap<>(); //轨迹Map<Imei,
    // List<LatLng>>
    private final List<String> mQueryDeviceList = new ArrayList<>(); //查找设备列表
    private final Map<String, String> mDeviceMap = new HashMap<>(); //选中设备
    private final List<AAATrackModel> mTrackList = new ArrayList<>(); // 轨迹点
    private final Map<String, List<AAATrackModel>> mTrackListMap = new HashMap<>();
    private final Map<String, List<Marker>> mTrackMap = new HashMap<>();
    private final Map<String, String> mColorMap = new HashMap<>();
    private final Map<String, List<Polyline>> mPolylineMap = new HashMap<>();
    //    private boolean isFindNewDevice = false;
    private boolean isNeedBounds = false;

    private DeviceSelectorAdapter adapter;
    private boolean isChanged = false; // 是否点击了右侧弹出窗的确认按钮
    private final int cameraUpdate = 17; //地图缩放级别 3-19　越高地图越精细
    private final int POLYLINE_WIDTH = FinalMapUtils.POLYLINE_WIDTH;
    private final int MARKER_SIZE = FinalMapUtils.MARKER_SIZE;
    private BitmapDescriptor bitmapDes = null; //中途定位点的marker样式
    private BitmapDescriptor supplementBitmapDes = null; //补传点marker样式
    private BitmapDescriptor markerBitmapDescriptor; //定位终点的marker样式
    private boolean fragmentIsDestroy = false;
    private boolean notYetSwitchedToThisFragment = true; //从未切换到该fragment
    private BottomSheet mBottomSheet; // 选项弹窗
    private int mLocationMode = 0;
    /**
     * 从何处打开(或进入)此界面标记  0、主页导航栏"地图中心"(默认为0)  1、首页的实时追踪
     */
    private int whereFrom = 0;

    private final int MOVING_MARKER = 0x148;
    private final int REQUEST_ROWS = 100;


    /**
     * 需要进行检测的权限数组
     */
    protected String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE
    };

    @Override
    protected int getLayoutId() {
        return R.layout.aaa_fragment_tracking;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mSavedInstanceState = savedInstanceState;
    }

    @Override
    protected TitleBar initTitle() {
        String title = null;
        Bundle bundle = getArguments();
        if (bundle != null) {
            title = bundle.getString(TConstant.TITLE);
            notYetSwitchedToThisFragment = false;
        }
        TitleBar titleBar = super.initTitle();
        if (!TextUtils.isEmpty(title)) {
//            titleBar.setTitle(title);
//            titleBar.setTitle(R.string.map_center);
            titleBar.setTitle(String.format("%s%s", getString(R.string.pet_real_time),
                    getString(R.string.map_title)));
            titleBar.addAction(new TitleBar.ImageAction(R.mipmap.ic_list_white_normal) {
                @Override
                public void performAction(View view) {
                    mDrawerLayout.openDrawer(Gravity.END);
                }
            });
        } else {
//            titleBar.setTitle(R.string.map_center);
            titleBar.setTitle(String.format("%s%s", getString(R.string.pet_real_time),
                    getString(R.string.map_title)));
            titleBar.setLeftImageResource(0);
            titleBar.setLeftClickListener(null);
            titleBar.addAction(new TitleBar.ImageAction(R.mipmap.ic_list_white_normal) {
                @Override
                public void performAction(View view) {
                    mDrawerLayout.openDrawer(Gravity.END);
                }
            });
        }
        return titleBar;
    }

    @Override
    protected void initViews() {
        mContext = getContext();
        mShowMarker.setSelected(true);
        mIvTrackMode.setSelected(false);
        initDatas();
        initAdapter();
        initRecyclerView();
        initDrawerLayout();
        initBitmapDes();
        initLocation();
        startLocation();
        initMap(mSavedInstanceState);
        refreshMobileOverlay(mMobileLatLng);
        initItems();
        initTrackMap();
        initListeners();
        initPagerItems();
        initPagerAdapters();
        initPagerRecyclerViews();
        initDevicePosition();
//        refreshOverlay();
        if (!notYetSwitchedToThisFragment) {
            isNeedBounds = true;
//            isFindNewDevice = true;
//            showDialog();
            startSearchTrackLog();
        }
    }

    private void initBitmapDes() {
        bitmapDes = BitmapDescriptorFactory.fromBitmap(
                TransformImageAppearance.resizeBitmap(
                        BitmapFactory.decodeResource(getResources(), R.mipmap.sub_marker),
                        MARKER_SIZE));
        supplementBitmapDes = BitmapDescriptorFactory.fromBitmap(
                TransformImageAppearance.resizeBitmap(
                        BitmapFactory.decodeResource(getResources(), R.mipmap.supplement_marker),
                        MARKER_SIZE));
    }

    private void initTrackMap() {
        for (AAADeviceModel item : mItemList) {
            trackMap.put(item.getDeviceImei(), new ArrayList<>());
        }
    }

//    /**
//     * 获取历史轨迹（单个设备
//     *
//     * @param nextId 分页查询索引号
//     *               requestRows 分页大小
//     */
//    @SuppressLint("SimpleDateFormat")
//    private void getHistoryLocation(long nextId, String imei, int deviceType) {
//        AAAUserModel userModel = getTrackUserModel();
//        SimpleDateFormat simpleDateFormatEnd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        SimpleDateFormat simpleDateFormatStart = new SimpleDateFormat("yyyy-MM-dd");
//        long date = new Date().getTime();
//        String endTime = simpleDateFormatEnd.format(date);
//        String startTime = simpleDateFormatStart.format(date) + " 00:00:00";
//        if (deviceType == 2)
//            CarGpsRequestUtils.getLastDeviceConfigTrack(userModel, imei
//                    , startTime, endTime, nextId, REQUEST_ROWS, mHandler);
//        else
//            CarGpsRequestUtils.getHistoryLocation(userModel, imei
//                    , startTime, endTime, nextId, REQUEST_ROWS, mHandler);
//    }

//    private void querySelectedDeviceLastLocation() {
//        AAAUserModel userModel = getTrackUserModel();
//        for (AAADeviceModel item : mItemList) {
//            if (item.isSelected())
//                CarGpsRequestUtils.getLastLocation(userModel, item.getDeviceImei(), mHandler);
//        }
//    }

    private void initDrawerLayout() {
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View view, float v) {

            }

            @Override
            public void onDrawerOpened(@NonNull View view) {
                isChanged = false;
                for (AAADeviceModel item : mItemList) {
                    statusList.add(item.isSelected());
                }
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDrawerClosed(@NonNull View view) {
                if (isChanged) {  //确认更改
                    statusList.clear();
                    onShownStatusChanged();
                } else {  //取消更改恢复选中状态
                    for (int i = 0; i < statusList.size(); i++) {
                        mItemList.get(i).setSelected(statusList.get(i));
                    }
                    statusList.clear();
                    checkAllDeviceSelectStatus();
                }
                initLatLngList();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onDrawerStateChanged(int i) {
            }
        });
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(adapter);
    }

    private void initAdapter() {
        adapter = new DeviceSelectorAdapter(mItemList);
        adapter.setOnItemClickListener((adapter, view, position) -> {
            if (position >= 0 && position < mItemList.size()) {
                AAADeviceModel model = mItemList.get(position);
                model.setSelected(!model.isSelected());
                adapter.notifyItemChanged(position);
                checkAllDeviceSelectStatus();
            }
        });
    }

    /**
     * 初始化列表信息
     */
    private void initPagerItems() {
        List<AAADeviceModel> deviceList = getTrackDeviceList();
        List<BaseItemBean> itemList = new ArrayList<>();
        if (deviceList != null) {
            for (AAADeviceModel deviceModel : deviceList) {
                BaseItemBean itemBean = new BaseItemBean();
                itemBean.setGroup(deviceModel.getDeviceImei());
                itemBean.setObject(deviceModel);
//                for (BaseItemBean item : mPagerItemList) {
//                    if (item.getGroup().equals(itemBean.getGroup())) {
//                        itemBean.setTitle(item.getTitle());
//                        break;
//                    }
//                }
                itemList.add(itemBean);
            }
        }
//        BaseItemBean itemBean = new BaseItemBean();
//        itemBean.setGroup("");
//        itemList.add(itemBean);
        mPagerItemList.clear();
        mPagerItemList.addAll(itemList);
    }

    /**
     * 初始化适配器
     */
    private void initPagerAdapters() {
        mPagerAdapter = new CardDeviceAdapter(mPagerItemList);
        mPagerAdapter.setOnItemChildClickListener(this);
    }

    /**
     * 初始化ViewPager
     */
    private void initPagerRecyclerViews() {
        mPagerRecyclerView = new RecyclerView(mActivity);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mPagerRecyclerView.setLayoutManager(layoutManager);
        mPagerRecyclerView.setAdapter(mPagerAdapter);
        mPagerRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NotNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && recyclerView.getChildCount() > 0) {
                    View snapView = mSnapHelper.findSnapView(layoutManager);
                    if (snapView != null) {
                        int currentPosition =
                                ((RecyclerView.LayoutParams) snapView.getLayoutParams()).getViewAdapterPosition();
                        if (currentPosition >= 0 && currentPosition < mPagerItemList.size()) {
                            if (mPagerItemList.get(currentPosition).getObject() != null) {
                                AAADeviceModel selectModel =
                                        (AAADeviceModel) mPagerItemList.get(currentPosition)
                                                .getObject();
                                String deviceImei = selectModel.getDeviceImei();
                                if (!TextUtils.isEmpty(deviceImei)) {
                                    List<Marker> markerList = mTrackMap.get(deviceImei);
                                    if (markerList != null && markerList.size() > 0) {
                                        Marker marker = markerList.get(markerList.size() - 1);
                                        if (marker.isVisible()) {
                                            mSearchMarker = marker;
                                            marker.showInfoWindow();
                                            mAMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        mPagerWrap.addView(mPagerRecyclerView);
        mSnapHelper = new PagerSnapHelper();
        mSnapHelper.attachToRecyclerView(mPagerRecyclerView);
    }

    /**
     * 初始化设备位置
     */
    private void initDevicePosition() {
        AAADeviceModel deviceModel = getTrackDeviceModel();
        if (deviceModel == null) {
            if (mPagerRecyclerView != null)
                mPagerRecyclerView.scrollToPosition(mPagerItemList.size() - 1);
        } else {
            for (int i = 0; i < mPagerItemList.size(); i++) {
                BaseItemBean itemBean = mPagerItemList.get(i);
                if (deviceModel.getDeviceImei().equals(itemBean.getGroup())) {
                    mSelectIndex = i;
                    if (mPagerRecyclerView != null)
                        mPagerRecyclerView.scrollToPosition(i);
                    break;
                }
            }
        }
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if (position >= 0 && position < mPagerItemList.size()) {
            BaseItemBean itemBean = mPagerItemList.get(position);
            if (itemBean.getObject() != null) {
                AAADeviceModel deviceModel = (AAADeviceModel) itemBean.getObject();
                Bundle bundle;
                switch (view.getId()) {
                    case R.id.trackBtn: // 轨迹
                        if (TextUtils.isEmpty(deviceModel.getDeviceImei())) {
                            showMessage(R.string.no_device_tips);
                        } else {
                            if (DeviceType.PIGEON.getValue() == deviceModel.getDeviceType()) {
                                bundle = new Bundle();
                                bundle.putInt(TConstant.TYPE, 1);
                                bundle.putString(TConstant.DEVICE_IMEI,
                                        deviceModel.getDeviceImei());
                                startActivity(bundle, WebActivity.class);
                            } else {
                                bundle = new Bundle();
                                bundle.putString(TConstant.DEVICE_IMEI,
                                        deviceModel.getDeviceImei());
                                startActivity(bundle, HistoryActivity.class);
                            }
                        }
                        break;
                    case R.id.locationBtn: // 定位
                        sendCommand("COMMAND_IMMEDIAL_LOCATION", deviceModel.getDeviceImei());
                        break;
                    case R.id.messageBtn: // 发送指令列表
                        bundle = new Bundle();
                        bundle.putString(TConstant.DEVICE_IMEI, deviceModel.getDeviceImei());
                        openNewPage(MessageCenterFragment.class, bundle);
                        break;
                    case R.id.detailsBtn: // 详情
                        if (TextUtils.isEmpty(deviceModel.getDeviceImei())) {
                            showMessage(R.string.no_device_tips);
                        } else {
                            bundle = new Bundle();
                            bundle.putString(TConstant.DEVICE_IMEI, deviceModel.getDeviceImei());
                            openNewPage(BabyInfoFragment.class, bundle);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * 当设备选中状态改变时检测是否全部设备皆为选中
     */
    private void checkAllDeviceSelectStatus() {
        boolean allSelect = true;
        for (AAADeviceModel item : mItemList) {
            if (!item.isSelected()) {
                allSelect = false;
                break;
            }
        }
        selectAll.setChecked(allSelect);
    }

    /**
     * 初始化信息
     */
    private void initDatas() {
        mLocationRefreshInterval =
                SettingSPUtils.getInstance().getInt(TConstant.LOCATION_REFRESH_INTERVAL,
                        CWConstant.DEFAULT_LOCAL_REFRESH_INTERVAL);
        mDeviceModel = getTrackDeviceModel();
        Bundle bundle = getArguments();
        if (bundle == null) {
            whereFrom = 0;
            return;
        }
        whereFrom = bundle.getInt(TConstant.WHERE_FROM);
        if (1 == whereFrom) {
            mLocusBtn.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 初始化监听器
     */
    public void initListeners() {
        mAMap.setInfoWindowAdapter(mInfoWindowAdapter);
        mAMap.setOnInfoWindowClickListener(mInfoWindowClickListener);
        mAMap.setOnMarkerClickListener(mMarkerClickListener);
    }

    /**
     * 初始化列表信息
     */
    private void initItems() {
        mItemList.clear();
        if (whereFrom == 0 || whereFrom == 2) {
//            mItemList.addAll(getTrackDeviceList());
            for (AAADeviceModel model : getTrackDeviceList()) {
                mItemList.add(mGson.fromJson(mGson.toJson(model), AAADeviceModel.class));
            }
            for (AAADeviceModel item : mItemList) {
                if (item.getDeviceImei().equals(mDeviceModel.getDeviceImei())) {
                    item.setSelected(true);
                    break;
                }
            }
        } else if (whereFrom == 1) {
            mItemList.add(getTrackDeviceModel());
            mItemList.get(0).setSelected(true);
        }
        AAADeviceModel deviceModel = getTrackDeviceModel();
        if (deviceModel != null && !TextUtils.isEmpty(deviceModel.getDeviceImei())) {
            String deviceImei = deviceModel.getDeviceImei();
            mQueryDeviceList.add(deviceImei);
            mDeviceMap.put(deviceImei, "1");
        }
        initLatLngList();
    }

    /**
     * 显示更多对话框
     */
    private void showMoreDialog() {
        if (mBottomSheet == null || !mBottomSheet.isShowing()) {
            Context context = getContext();
            if (context == null)
                context = mActivity;
            BottomSheet.BottomListSheetBuilder builder =
                    new BottomSheet.BottomListSheetBuilder(context);
            String trackMode;
            String locationMode;
            String infoMode;
            if (mIvTrackMode.isSelected()) {
                trackMode = getString(R.string.track_mode_show);
            } else {
                trackMode = getString(R.string.track_mode_hide);
            }
            if (mLocationMode == 1) {
                locationMode = getString(R.string.location_mode_gps);
            } else {
                locationMode = getString(R.string.location_mode_base_station);
            }
            if (mPagerWrap.getVisibility() == View.GONE) {
                infoMode = getString(R.string.location_card_show);
            } else {
                infoMode = getString(R.string.location_card_hide);
            }
            mBottomSheet = builder.addItem(trackMode, "0")
                    .addItem(locationMode, "1")
                    .addItem(infoMode, "2")
                    .setIsCenter(true)
                    .setOnSheetItemClickListener(new BottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                        @Override
                        public void onClick(BottomSheet dialog, View itemView, int position,
                                            String tag) {
                            dialog.dismiss();
                            if ("0".equals(tag)) {
                                mIvTrackMode.setSelected(!mIvTrackMode.isSelected());
                                if (mIvTrackMode.isSelected()) {
                                    hideAllDeviceMarker();
                                    hideAllTrackLine();
                                } else {
                                    showAllDeviceMarker();
                                    showAllTrackLine();
                                }
                            } else if ("1".equals(tag)) {
                                if (mLocationMode == 1) {
                                    mLocationMode = 0;
                                } else {
                                    mLocationMode = 1;
                                }
                                mAMap.clear();
                                mTrackMap.clear();
                                mPolylineMap.clear();
                                if (mMobileMarker != null) {
                                    refreshMobileOverlay(mMobileMarker.getPosition());
                                }
                                mSearchMarker = null;
                                boolean hasSelect = false;
                                for (AAADeviceModel model : mItemList) {
                                    String deviceImei = model.getDeviceImei();
                                    if (!TextUtils.isEmpty(deviceImei)) {
                                        String selectType = mDeviceMap.get(deviceImei);
                                        if (model.isSelected()) {
                                            hasSelect = true;
                                            mQueryDeviceList.add(deviceImei);
                                            if (!"1".equals(selectType)) {
                                                showDeviceTrack(deviceImei);
                                            }
                                            mDeviceMap.put(deviceImei, "1");
                                        } else {
                                            if ("1".equals(selectType)) {
                                                hideDeviceTrack(deviceImei);
                                            }
                                            mDeviceMap.put(deviceImei, "0");
                                        }
                                    }
                                }
                                if (hasSelect) {
                                    isNeedBounds = true;
                                    startSearchTrackLog();
                                }
                            } else if ("2".equals(tag)) {
                                if (mPagerWrap.getVisibility() == View.GONE) {
                                    mPagerWrap.setVisibility(View.VISIBLE);
                                } else {
                                    mPagerWrap.setVisibility(View.GONE);
                                }
                            }
                        }
                    }).build();
            mBottomSheet.show();
        }
    }

    /**
     * 初始化位置列表(mLatLngItemList)以及要显示的位置列表(shownLatLngList)信息
     */
    private void initLatLngList() {
        shownLatLgnList.clear();
        mLatLngItemList.clear();
        for (AAADeviceModel deviceModel : mItemList) {
            if (deviceModel.getLastLatitude() != null
                    && deviceModel.getLastLongitude() != null) {
                LatLng latLng = convertLatLng(
                        new LatLng(deviceModel.getLastLatitude()
                                , deviceModel.getLastLongitude()));
                mLatLngItemList.add(latLng);
                if (deviceModel.isSelected()) {
                    shownLatLgnList.add(latLng);
                }
            } else
                mLatLngItemList.add(new LatLng(0, 0));
        }
    }

    /**
     * 转换坐标系
     */
    private LatLng convertLatLng(LatLng latLng) {
        CoordinateConverter converter = new CoordinateConverter(getContext());
        converter.from(CoordinateConverter.CoordType.GPS);
        converter.coord(new LatLng(latLng.latitude, latLng.longitude));
        return converter.convert();
    }


    /**
     * 初始化AMap对象
     */
    private void initMap(@Nullable Bundle savedInstanceState) {
        mMapView.onCreate(savedInstanceState);// 此方法必须重写
        mAMap = mMapView.getMap();
        mUiSettings = mAMap.getUiSettings();
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
            locationClient = new AMapLocationClient(getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        locationOption = getDefaultOption();
        //设置定位参数
        locationClient.setLocationOption(locationOption);
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
    }

    private void addDeviceTrackMarker(String deviceImei) {
        if (mTrackList.size() > 0) {
            AAATrackModel lastTrackModel = mTrackList.get(mTrackList.size() - 1);
            AAADeviceModel deviceModel = null;
            for (AAADeviceModel model : getTrackDeviceList()) {
                if (deviceImei.equals(model.getDeviceImei())) {
                    deviceModel = model;
                    lastTrackModel.setHeadPic(deviceModel.getHeadPic());
                    lastTrackModel.setDeviceName(deviceModel.getDeviceName());
                    break;
                }
            }
            List<Marker> markerList = mTrackMap.get(deviceImei);
            List<Marker> lineList = new ArrayList<>();
            if (markerList != null && markerList.size() > 0) {
                Marker marker = markerList.get(markerList.size() - 1);
                AAATrackModel trackModel = (AAATrackModel) marker.getObject();
                if (trackModel.getLogId() == null) {
                    markerList.clear();
                    marker.remove();
                    marker = null;
                }
                if (marker != null) {
                    marker.remove();
                    markerList.remove(marker);
                    Marker addMarker;
                    if (markerList.size() == 1) {
                        addMarker = addDeviceMarker(2, trackModel);
                    } else {
                        addMarker = addDeviceMarker(0, trackModel);
                    }
                    markerList.add(addMarker);
                    addPolyline(trackModel, addMarker, lineList, deviceImei);
                }
            } else if (markerList == null) {
                markerList = new ArrayList<>();
                mTrackMap.put(deviceImei, markerList);
            }
            for (int i = 0; i < mTrackList.size() - 1; i++) {
                AAATrackModel model = mTrackList.get(i);
                if (deviceModel != null) {
                    model.setDeviceName(deviceModel.getDeviceName());
                    model.setHeadPic(deviceModel.getHeadPic());
                }
                Marker addMarker;
                if (markerList.size() + i == 0) {
                    addMarker = addDeviceMarker(2, model);
                } else {
                    addMarker = addDeviceMarker(0, model);
                }
                markerList.add(addMarker);
                addPolyline(model, addMarker, lineList, deviceImei);
            }
            Marker lastMarker = addDeviceMarker(1, lastTrackModel);
            markerList.add(lastMarker);
            addPolyline(lastTrackModel, lastMarker, lineList, deviceImei);
            addPolyline(null, null, lineList, deviceImei);
            mTrackList.clear();
        } else if (!DataUtils.isPigeonDevice(deviceImei)) {
            List<Marker> markerList = mTrackMap.get(deviceImei);
            Marker marker;
            if (markerList != null && markerList.size() > 0) {
                marker = markerList.get(markerList.size() - 1);
            } else if (markerList == null) {
                markerList = new ArrayList<>();
                mTrackMap.put(deviceImei, markerList);
                marker = null;
            } else {
                marker = null;
            }
            boolean needAdd = false;
            if (marker == null) {
                needAdd = true;
            } else {
                AAATrackModel trackModel = (AAATrackModel) marker.getObject();
                if (trackModel.getLogId() == null) {
                    markerList.clear();
                    marker.remove();
                    needAdd = true;
                }
            }
            if (needAdd) {
                for (AAADeviceModel model : getTrackDeviceList()) {
                    if (deviceImei.equals(model.getDeviceImei()) && model.getLastLatitude() != 0 && model.getLastLongitude() != 0) {
                        AAATrackModel trackModel = FinalMapUtils.transformDeviceToTrack(model);
                        markerList.add(addDeviceMarker(1, trackModel));
                        break;
                    }
                }
            }
        }
    }

    /**
     * 添加设备标记点
     *
     * @param deviceImei 设备号
     * @param trackList  轨迹列表
     */
    private void addDeviceTrackMarker(String deviceImei, List<AAATrackModel> trackList) {
        if (trackList != null && trackList.size() > 0) {
            AAATrackModel lastTrackModel = trackList.get(trackList.size() - 1);
            AAADeviceModel deviceModel = null;
            for (AAADeviceModel model : getTrackDeviceList()) {
                if (deviceImei.equals(model.getDeviceImei())) {
                    deviceModel = model;
                    lastTrackModel.setHeadPic(deviceModel.getHeadPic());
                    lastTrackModel.setDeviceName(deviceModel.getDeviceName());
                    break;
                }
            }
            List<Marker> markerList = mTrackMap.get(deviceImei);
            List<Marker> lineList = new ArrayList<>();
            if (markerList != null && markerList.size() > 0) {
                Marker marker = markerList.get(markerList.size() - 1);
                AAATrackModel trackModel = (AAATrackModel) marker.getObject();
                if (trackModel.getLogId() == null) {
                    markerList.clear();
                    marker.remove();
                    marker = null;
                }
                if (marker != null) {
                    marker.remove();
                    markerList.remove(marker);
                    Marker addMarker;
                    if (markerList.size() == 0) {
                        addMarker = addDeviceMarker(2, trackModel);
                    } else {
                        addMarker = addDeviceMarker(0, trackModel);
                    }
                    markerList.add(addMarker);
                    addPolyline(trackModel, addMarker, lineList, deviceImei);
                }
            } else if (markerList == null) {
                markerList = new ArrayList<>();
                mTrackMap.put(deviceImei, markerList);
            }
            for (int i = 0; i < trackList.size() - 1; i++) {
                AAATrackModel model = trackList.get(i);
                if (deviceModel != null) {
                    model.setDeviceName(deviceModel.getDeviceName());
                    model.setHeadPic(deviceModel.getHeadPic());
                }
                Marker addMarker;
                if (markerList.size() + i == 0) {
                    addMarker = addDeviceMarker(2, model);
                } else {
                    addMarker = addDeviceMarker(0, model);
                }
                markerList.add(addMarker);
                addPolyline(model, addMarker, lineList, deviceImei);
            }
            Marker lastMarker = addDeviceMarker(1, lastTrackModel);
            markerList.add(lastMarker);
            addPolyline(lastTrackModel, lastMarker, lineList, deviceImei);
            addPolyline(null, null, lineList, deviceImei);
            trackList.clear();
        } else if (!DataUtils.isPigeonDevice(deviceImei)) {
            List<Marker> markerList = mTrackMap.get(deviceImei);
            Marker marker;
            if (markerList != null && markerList.size() > 0) {
                marker = markerList.get(markerList.size() - 1);
            } else if (markerList == null) {
                markerList = new ArrayList<>();
                mTrackMap.put(deviceImei, markerList);
                marker = null;
            } else {
                marker = null;
            }
            boolean needAdd = false;
            if (marker == null) {
                needAdd = true;
            } else {
                AAATrackModel trackModel = (AAATrackModel) marker.getObject();
                if (trackModel.getLogId() == null) {
                    markerList.clear();
                    marker.remove();
                    needAdd = true;
                }
            }
            if (needAdd) {
                for (AAADeviceModel model : getTrackDeviceList()) {
                    if (deviceImei.equals(model.getDeviceImei()) && model.getLastLatitude() != 0 && model.getLastLongitude() != 0) {
                        AAATrackModel trackModel = FinalMapUtils.transformDeviceToTrack(model);
                        markerList.add(addDeviceMarker(1, trackModel));
                        break;
                    }
                }
            }
        }
    }

    private Marker addDeviceMarker(int type, AAATrackModel model) {
        LatLng latLng = convertLatLng(new LatLng(model.getLat(),
                model.getLng()));
        Marker marker;
        if (type == 2) {
            marker = mAMap.addMarker(new MarkerOptions().position(latLng)
                    .icon(BitmapDescriptorFactory.fromBitmap(NewMapUtils.getDrivingMarkerIcon(getResources(), 0))));
        } else if (type == 1) {
            MarkerOptions option = new MarkerOptions()
                    .position(latLng)
                    .draggable(false)
                    .title("")
                    .snippet("")
                    .setInfoWindowOffset(0, -2)
                    .icon(getDefaultMarkerBitmapDescriptor()) //为Marker设置默认样式
                    .setFlat(false);//将Marker设置为贴地显示，可以双指下拉地图查看效果
            marker = mAMap.addMarker(option);
            initDeviceHeadPicMarker(marker, model.getHeadPic());
            for (BaseItemBean itemBean : mPagerItemList) {
                if (!TextUtils.isEmpty(model.getDeviceImei())
                        && model.getDeviceImei().equals(itemBean.getGroup())) {
                    AAADeviceModel deviceModel = (AAADeviceModel) itemBean.getObject();
                    deviceModel.setLocationType(model.getLocationType());
                    deviceModel.setSatellite(model.getSatellite());
                    deviceModel.setLastLocationTime(model.getLogTime());
                    deviceModel.setLastGpsTime(model.getGpsTime());
                    deviceModel.setLastLatitude(model.getLat());
                    deviceModel.setLastLongitude(model.getLng());
                    break;
                }
            }
        } else {
            if (model.getSupplement() == 1)
                marker =
                        mAMap.addMarker(new MarkerOptions().position(latLng).icon(supplementBitmapDes).anchor(0.5f, 0.5f));
            else
                marker =
                        mAMap.addMarker(new MarkerOptions().position(latLng).icon(bitmapDes).anchor(0.5f, 0.5f));
        }
        marker.setObject(model);
        if ((mIvTrackMode.isSelected() && type != 1) || (!mShowMarker.isSelected() && type == 0)) {
            marker.setVisible(false);
        }
//        if (isFindNewDevice || (!mShowMarker.isSelected() && type == 0)) {
//            marker.setVisible(false);
//        }
        return marker;
    }

    private void addPolyline(AAATrackModel model, Marker marker, List<Marker> lineList,
                             String deviceImei) {
        if (model != null && lineList.size() > 0) {
            AAATrackModel trackModel = (AAATrackModel) lineList.get(0).getObject();
            int lineType = trackModel.getSupplement();
            if (lineType == model.getSupplement()) {
                lineList.add(marker);
            } else {
                String polylineColor = mColorMap.get(deviceImei);
                String polylineSecondName = String.format("%s_Second", deviceImei);
                String polylineSecondColor = mColorMap.get(polylineSecondName);
                int lineColor;
                int lineSecondColor;
                if (polylineColor == null || polylineSecondColor == null) {
                    lineColor = AMapMovementTrack.getRandomColor();
                    lineSecondColor = AMapMovementTrack.getRandomColor();
                    mColorMap.put(deviceImei, String.valueOf(lineColor));
                    mColorMap.put(polylineSecondName, String.valueOf(lineSecondColor));
                } else {
                    lineColor = Integer.parseInt(polylineColor);
                    lineSecondColor = Integer.parseInt(polylineSecondColor);
                }
                List<Polyline> polylineList = mPolylineMap.get(deviceImei);
                if (polylineList == null) {
                    polylineList = new ArrayList<>();
                    mPolylineMap.put(deviceImei, polylineList);
                }
                List<LatLng> latLngList = new ArrayList<>();
                for (Marker trackBean : lineList) {
                    latLngList.add(trackBean.getPosition());
                }
                Polyline polyline;
                if (lineType == 1) {
                    polyline = mAMap.addPolyline(new PolylineOptions()
                            .width(POLYLINE_WIDTH)
                            .color(lineSecondColor)
                            .addAll(latLngList));
                } else {
                    polyline = mAMap.addPolyline(new PolylineOptions()
                            .width(POLYLINE_WIDTH)
                            .color(lineColor)
                            .addAll(latLngList));
                }
                polylineList.add(polyline);
                if (mIvTrackMode.isSelected()) {
                    polyline.setVisible(false);
                }
//                if (isFindNewDevice || mIvTrackMode.isSelected()) {
//                    marker.setVisible(false);
//                }
                lineList.clear();
                lineList.add(marker);
            }
        } else if (model != null) {
            lineList.add(marker);
        } else if (lineList.size() > 0) {
            AAATrackModel trackModel = (AAATrackModel) lineList.get(0).getObject();
            int lineType = trackModel.getSupplement();
            String polylineColor = mColorMap.get(deviceImei);
            String polylineSecondName = String.format("%s_Second", deviceImei);
            String polylineSecondColor = mColorMap.get(polylineSecondName);
            int lineColor;
            int lineSecondColor;
            if (polylineColor == null || polylineSecondColor == null) {
                lineColor = AMapMovementTrack.getRandomColor();
                lineSecondColor = AMapMovementTrack.getRandomColor();
                mColorMap.put(deviceImei, String.valueOf(lineColor));
                mColorMap.put(polylineSecondName, String.valueOf(lineSecondColor));
            } else {
                lineColor = Integer.parseInt(polylineColor);
                lineSecondColor = Integer.parseInt(polylineSecondColor);
            }
            List<Polyline> polylineList = mPolylineMap.get(deviceImei);
            if (polylineList == null) {
                polylineList = new ArrayList<>();
                mPolylineMap.put(deviceImei, polylineList);
            }
            List<LatLng> latLngList = new ArrayList<>();
            for (Marker trackBean : lineList) {
                latLngList.add(trackBean.getPosition());
            }
            Polyline polyline;
            if (lineType == 1) {
                polyline = mAMap.addPolyline(new PolylineOptions()
                        .width(POLYLINE_WIDTH)
                        .color(lineSecondColor)
                        .addAll(latLngList));
            } else {
                polyline = mAMap.addPolyline(new PolylineOptions()
                        .width(POLYLINE_WIDTH)
                        .color(lineColor)
                        .addAll(latLngList));
            }
            polylineList.add(polyline);
            if (mIvTrackMode.isSelected()) {
                polyline.setVisible(false);
            }
//            if (isFindNewDevice || mIvTrackMode.isSelected()) {
//                polyline.setVisible(false);
//            }
        }
    }

    @SuppressLint("InflateParams")
    private void initDeviceHeadPicMarker(Marker marker, String imageUrl) {
        View view = getLayoutInflater().inflate(R.layout.map_tracking_icon_layout, null);
        ImageView iconImg = view.findViewById(R.id.map_tracking_icon);
        iconImg.setImageResource(R.mipmap.ic_default_pigeon_marker);
        Bitmap bitmap = BitmapBlobUtils.convertViewToBitmap(view);
        if (bitmap != null && marker != null) {
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
            if (!TextUtils.isEmpty(imageUrl)) {
                Glide.with(this)
                        .asBitmap()
                        .load(imageUrl)
                        .error(R.mipmap.ic_default_pigeon_marker)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource,
                                                        @Nullable Transition<?
                                                                super Bitmap> transition) {
                                iconImg.setImageBitmap(resource);
                                Bitmap resultBitMap = BitmapBlobUtils.convertViewToBitmap(view);
                                if (resultBitMap != null) {
                                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(resultBitMap));
                                }
                            }
                        });
            }
        }
    }

    private void showDeviceTrack(String deviceImei) {
        List<Marker> markerList = mTrackMap.get(deviceImei);
        if (markerList != null && markerList.size() > 0) {
            if (mIvTrackMode.isSelected()) {
                markerList.get(markerList.size() - 1).setVisible(true);
            } else {
                if (mShowMarker.isSelected()) {
                    for (Marker marker : markerList) {
                        marker.setVisible(true);
                    }
                } else {
                    markerList.get(0).setVisible(true);
                    markerList.get(markerList.size() - 1).setVisible(true);
                }
            }
        }
        if (!mIvTrackMode.isSelected()) {
            List<Polyline> polylineList = mPolylineMap.get(deviceImei);
            if (polylineList != null) {
                for (Polyline polyline : polylineList) {
                    polyline.setVisible(true);
                }
            }
        }
    }

    private void hideDeviceTrack(String deviceImei) {
        List<Marker> markerList = mTrackMap.get(deviceImei);
        if (markerList != null) {
            for (Marker marker : markerList) {
                marker.setVisible(false);
            }
        }
        List<Polyline> polylineList = mPolylineMap.get(deviceImei);
        if (polylineList != null) {
            for (Polyline polyline : polylineList) {
                polyline.setVisible(false);
            }
        }
    }

    private void showAllDeviceMarker() {
        for (AAADeviceModel model : mItemList) {
            String deviceImei = model.getDeviceImei();
            if (!TextUtils.isEmpty(deviceImei) && "1".equals(mDeviceMap.get(deviceImei))) {
                List<Marker> markerList = mTrackMap.get(deviceImei);
                if (markerList != null) {
                    if (mIvTrackMode.isSelected()) {
                        markerList.get(markerList.size() - 1).setVisible(true);
                    } else {
                        if (mShowMarker.isSelected()) {
                            for (Marker marker : markerList) {
                                marker.setVisible(true);
                            }
                        } else {
                            markerList.get(0).setVisible(true);
                            markerList.get(markerList.size() - 1).setVisible(true);
                        }
                    }
                }
            }
        }
    }

    private void hideAllDeviceMarker() {
        for (AAADeviceModel model : mItemList) {
            String deviceImei = model.getDeviceImei();
            if (!TextUtils.isEmpty(deviceImei)) {
                List<Marker> markerList = mTrackMap.get(deviceImei);
                if (markerList != null && markerList.size() > 0) {
                    if ("1".equals(mDeviceMap.get(deviceImei))) {
                        for (int i = 1; i < markerList.size() - 1; i++) {
                            Marker marker = markerList.get(i);
                            marker.setVisible(false);
                        }
                        if (mIvTrackMode.isSelected() && markerList.size() > 1) {
                            markerList.get(0).setVisible(false);
                        }
                    } else {
                        for (Marker marker : markerList) {
                            marker.setVisible(false);
                        }
                    }
                }
            }
        }
    }

    private void showAllTrackLine() {
        for (AAADeviceModel model : mItemList) {
            String deviceImei = model.getDeviceImei();
            if (!TextUtils.isEmpty(deviceImei) && "1".equals(mDeviceMap.get(deviceImei))) {
                List<Polyline> polylineList = mPolylineMap.get(deviceImei);
                if (polylineList != null && polylineList.size() > 0) {
                    for (Polyline polyline : polylineList) {
                        polyline.setVisible(true);
                    }
                }
            }
        }
    }

    private void hideAllTrackLine() {
        for (AAADeviceModel model : mItemList) {
            String deviceImei = model.getDeviceImei();
            if (!TextUtils.isEmpty(deviceImei)) {
                List<Polyline> polylineList = mPolylineMap.get(deviceImei);
                if (polylineList != null && polylineList.size() > 0) {
                    for (Polyline polyline : polylineList) {
                        polyline.setVisible(false);
                    }
                }
            }
        }
    }

    /**
     * 刷新手机图层
     */
    private void refreshMobileOverlay(LatLng point) {
        if (point != null) {
            MarkerOptions option = new MarkerOptions().position(point).draggable(false).title("")
                    .snippet("")
                    .setInfoWindowOffset(0, -2);
            if (mMobileMarker != null)
                mMobileMarker.remove();
            if (mAMap != null) {
                mMobileMarker = mAMap.addMarker(option);
                if (mMobileBtn.isSelected()) {
                    mAMap.moveCamera(CameraUpdateFactory.newLatLng(point));
                    mMobileMarker.showInfoWindow();
                }
            }
        }
    }


    /**
     * 刷新设备图层
     */
    private void refreshOverlay() {
        for (int i = 0; i < mLatLngItemList.size(); i++) {
            AAADeviceModel deviceModel = mItemList.get(i);
            MarkerOptions option = new MarkerOptions()
                    .position(mLatLngItemList.get(i))
                    .draggable(false)
                    .title("")
                    .snippet("")
                    .setInfoWindowOffset(0, -2)
                    .icon(getDefaultMarkerBitmapDescriptor()) //为Marker设置默认样式
                    .setFlat(false);//将Marker设置为贴地显示，可以双指下拉地图查看效果
//            option.icon(BitmapDescriptorFactory.fromView
//                    (AAAMapUtils.getTrackingMarkerIcon(getContext(), mItemList.get(i))));

            Marker marker = mAMap.addMarker(option);
            marker.setVisible(deviceModel.isSelected());

            int index = i;
            tracks.add(new AMapMovementTrack()
                    .marker(marker)
                    .deviceModel(deviceModel)
                    .latLng(mLatLngItemList.get(i))
                    .initLatLng(mLatLngItemList.get(i))
                    .initColor());

//            ImageView imageView = (ImageView) LayoutInflater.from(mContext).inflate(R.layout
//            .amap_custom_texture, null);
//            imageView.setImageResource(R.mipmap.arrow_up_new);
//            imageView.setColorFilter(tracks.get(i).getColor(), PorterDuff.Mode.SRC_ATOP);
//            tracks.get(i).setCustomTexture(BitmapDescriptorFactory.fromView(imageView));

            if (!deviceModel.isSelected() || deviceModel.getDeviceType() == 2)
                marker.setVisible(false);

            String[] strings = null;
            if (deviceModel.getHeadPic() != null) {
                strings = deviceModel.getHeadPic().split(".com/");
            }
            if (strings != null && strings.length > 1 && !strings[1].equals("null")) {
                customizeMarkerIcon(deviceModel.getHeadPic(), new OnMarkerIconLoadListener() {
                    @Override
                    public void markerIconLoadingFinished(View view) {
                        Bitmap bitmap = BitmapBlobUtils.convertViewToBitmap(view);
                        BitmapDescriptor bitmapDescriptor =
                                BitmapDescriptorFactory.fromBitmap(bitmap);
                        option.icon(bitmapDescriptor);
                        if (mDeviceModel != null && TextUtils.equals(deviceModel.getDeviceImei(),
                                mDeviceModel.getDeviceImei())) {
                            mMarker = marker;
                            mSearchMarker = mMarker;
                            mLatLng = mLatLngItemList.get(index);
                        }
                        marker.setIcon(bitmapDescriptor);
                        marker.setObject(FinalMapUtils.transformDeviceToTrack(deviceModel));
                    }
                });
            } else {
                if (mDeviceModel != null && TextUtils.equals(deviceModel.getDeviceImei(),
                        mDeviceModel.getDeviceImei())) {
                    mMarker = marker;
                    mSearchMarker = mMarker;
                    mLatLng = mLatLngItemList.get(index);
                }
                marker.setObject(FinalMapUtils.transformDeviceToTrack(deviceModel));
                if (!deviceModel.isSelected())
                    marker.setVisible(false);
            }
        }
    }

    private BitmapDescriptor getDefaultMarkerBitmapDescriptor() {
        if (markerBitmapDescriptor == null) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.map_tracking_icon_layout,
                    null);
            ImageView iconImg = view.findViewById(R.id.map_tracking_icon);
            int imgRes = R.mipmap.ic_default_pigeon_marker;
            iconImg.setImageResource(imgRes);
            Bitmap bitmap = BitmapBlobUtils.convertViewToBitmap(view);
            markerBitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
        }
        return markerBitmapDescriptor;
    }

    /**
     * Glide下载网络图片并返回
     *
     * @param url      设备头像Url
     * @param listener 监听回调
     */
    private void customizeMarkerIcon(String url, final OnMarkerIconLoadListener listener) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.map_tracking_icon_layout, null);
        ImageView iconImg = view.findViewById(R.id.map_tracking_icon);

        int imgRes = R.mipmap.ic_default_pigeon_marker;
        iconImg.setImageResource(imgRes);

        Glide.with(mContext)
                .asBitmap()
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Bitmap> target, boolean isFirstResource) {
                        listener.markerIconLoadingFinished(view);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model,
                                                   Target<Bitmap> target, DataSource dataSource,
                                                   boolean isFirstResource) {
                        iconImg.setImageBitmap(resource);
                        listener.markerIconLoadingFinished(view);
                        return false;
                    }
                }).preload();
    }

    /**
     * by moos on 2017/11/15
     * func:自定义监听接口,用来marker的icon加载完毕后回调添加marker属性
     */
    public interface OnMarkerIconLoadListener {
        void markerIconLoadingFinished(View view);
    }

    /**
     * 显示范围
     */
    private void showLatLngBounds() {
        if (isNeedBounds) {
            isNeedBounds = false;
            LatLngBounds.Builder latLngBoundsBuilder = new LatLngBounds.Builder();
            for (AAADeviceModel model : mItemList) {
                String deviceImei = model.getDeviceImei();
                if (!TextUtils.isEmpty(deviceImei) && "1".equals(mDeviceMap.get(deviceImei))) {
                    List<Marker> markerList = mTrackMap.get(deviceImei);
                    if (markerList != null) {
                        for (Marker marker : markerList) {
                            if (marker.isVisible()) {
                                latLngBoundsBuilder.include(marker.getPosition());
                            }
                        }
                    }
                }
            }
            if (mMobileLatLng != null)
                latLngBoundsBuilder.include(mMobileLatLng);
            if (mAMap != null)
                mAMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBoundsBuilder.build(),
                        getResources().getDimensionPixelOffset(R.dimen.margin_40)));
        }
    }

    /**
     * 改变要在地图上显示的设备
     */
    private void onShownStatusChanged() {
//        if (!mShowMarker.isSelected())
//            return;
//        mAMap.clear();
//        mMobileMarker = mAMap.addMarker(new MarkerOptions().position(mMobileLatLng));
//        for (int i = 0; i < mItemList.size(); i++) {
//            if (mItemList.get(i).isSelected()) {
//                String deviceImei = mItemList.get(i).getDeviceImei();
//                Marker marker = mAMap.addMarker(tracks.get(i).getMarker().getOptions());
//                marker.setObject(tracks.get(i).getDeviceModel());
//                if (mItemList.get(i).getDeviceType() != 2)
//                    marker.setVisible(true);
//                else if (Objects.requireNonNull(trackMap.get(mItemList.get(i).getDeviceImei()))
//                .size() != 0)
//                    marker.setVisible(true);
//                else
//                    marker.setVisible(false);
//                tracks.get(i).setMarker(marker);
//                if (deviceImei.equals(mDeviceModel.getDeviceImei()))
//                    mMarker = marker;
//                if (!notYetSwitchedToThisFragment) {
////                    if (trackMap.get(deviceImei) == null || Objects.requireNonNull(trackMap.get
////                    (deviceImei)).size() == 0) //如果轨迹为空重新获取
////                        getHistoryLocation(0, deviceImei, mItemList.get(i).getDeviceType());
////                    else
//                    initTrack(Objects.requireNonNull(trackMap.get(deviceImei)), deviceImei, 0);
//                }
//
//            }
//        }
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
        mOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
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
        if (getContext() != null)
            PermissionUtils.checkAndRequestMorePermissions(getContext(), needPermissions,
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
        if (null != locationClient)
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
     * 开始搜索轨迹
     */
    private void startSearchTrackLog() {
        if (mQueryDeviceList.size() > 0) {
            Date date = new Date();
            String startTime = TimeUtils.formatUTC(date.getTime(), "yyyy-MM-dd 00:00:00");
            String endTime = TimeUtils.formatUTC(date.getTime(), "yyyy-MM-dd HH:mm:ss");
            JsonArray jsonArray = new JsonArray();
            for (int i = mQueryDeviceList.size() - 1; i >= 0; i--) {
                String deviceImei = mQueryDeviceList.get(i);
                if (!TextUtils.isEmpty(deviceImei)) {
                    if ("1".equals(mDeviceMap.get(deviceImei))) {
                        List<AAATrackModel> trackList = mTrackListMap.get(deviceImei);
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("deviceImei", deviceImei);
                        jsonObject.addProperty("startTime", startTime);
                        jsonObject.addProperty("endTime", endTime);
                        if (mLocationMode == 1) {
                            jsonObject.addProperty("locationType", 2);
                        }
                        jsonObject.addProperty("requestRows", REQUEST_ROWS);
                        AAATrackModel model = null;
                        if (trackList != null && trackList.size() > 0) {
                            model = trackList.get(trackList.size() - 1);
                        } else {
                            List<Marker> markerList = mTrackMap.get(deviceImei);
                            if (markerList != null && markerList.size() > 0) {
                                if (markerList.get(markerList.size() - 1).getObject() != null) {
                                    model =
                                            (AAATrackModel) markerList.get(markerList.size() - 1).getObject();
                                }
                            }
                        }
                        long nextId;
                        if (model != null && model.getLogId() != null) {
                            nextId = model.getLogId();
                        } else {
                            nextId = 0;
                        }
                        jsonObject.addProperty("nextId", nextId);
                        jsonArray.add(jsonObject);
                    } else {
                        mQueryDeviceList.remove(i);
                    }
                }
            }
            if (jsonArray.size() > 0) {
                getMultipleDeviceTraceLog(jsonArray);
                return;
            }
        }
        for (String deviceImei : mTrackListMap.keySet()) {
            addDeviceTrackMarker(deviceImei, mTrackListMap.get(deviceImei));
        }
        showLatLngBounds();
        mTrackListMap.clear();
//        if (isFindNewDevice) {
//            for (AAADeviceModel model : mItemList) {
//                if (!TextUtils.isEmpty(model.getDeviceImei()) && "1".equals(mDeviceMap.get
//                (model.getDeviceImei()))) {
//                    showDeviceTrack(model.getDeviceImei());
//                }
//            }
//            showLatLngBounds();
//        }
//        isFindNewDevice = false;

//        if (mQueryDeviceList.size() > 0) {
//            String deviceImei = mQueryDeviceList.get(0);
//            if (!TextUtils.isEmpty(deviceImei)) {
//                if ("1".equals(mDeviceMap.get(deviceImei))) {
//                    if (mTrackList.size() > 0 && deviceImei.equals(mTrackList.get(0)
//                    .getDeviceImei())) {
//                        getTrackLog(mTrackList.get(mTrackList.size() - 1), deviceImei);
//                    } else {
//                        if (mTrackList.size() > 0 && !TextUtils.isEmpty(mTrackList.get(0)
//                        .getDeviceImei())) {
//                            mTrackList.clear();
//                        }
//                        List<Marker> markerList = mTrackMap.get(deviceImei);
//                        if (markerList != null && markerList.size() > 0) {
//                            AAATrackModel trackModel = null;
//                            if (markerList.get(0).getObject() != null) {
//                                trackModel = (AAATrackModel) markerList.get(0).getObject();
//                            }
//                            getTrackLog(trackModel, deviceImei);
//                        } else {
//                            getTrackLog(null, deviceImei);
//                        }
//                    }
//                } else {
//                    mQueryDeviceList.remove(0);
//                    startSearchTrackLog();
//                }
//            } else {
//                mQueryDeviceList.remove(0);
//                startSearchTrackLog();
//            }
//        } else {
//            if (isFindNewDevice) {
//                for (AAADeviceModel model : mItemList) {
//                    if (!TextUtils.isEmpty(model.getDeviceImei()) && "1".equals(mDeviceMap.get
//                    (model.getDeviceImei()))) {
//                        showDeviceTrack(model.getDeviceImei());
//                    }
//                }
//                showLatLngBounds();
//            }
//            isFindNewDevice = false;
////            dismisDialog();
//        }
    }

    /**
     * 获取设备定位点
     */
    private void getTrackLog(AAATrackModel model, String deviceImei) {
        if (!NetworkUtils.isNetworkAvailable()) {
            mQueryDeviceList.clear();
            startSearchTrackLog();
            XToastUtils.toast(getContext(), R.string.network_error_prompt);
            return;
        }
        AAAUserModel userModel = getTrackUserModel();
        if (userModel != null && !TextUtils.isEmpty(deviceImei)) {
            Date date = new Date();
            long nextId = 0;
            if (model != null && model.getLogId() != null) {
                nextId = model.getLogId();
            }
            CarGpsRequestUtils.getLastDeviceConfigTrack(userModel, deviceImei,
                    TimeUtils.formatUTC(date.getTime(), "yyyy-MM-dd 00:00:00"),
                    TimeUtils.formatUTC(date.getTime(), "yyyy-MM-dd HH:mm:ss"), nextId,
                    REQUEST_ROWS,
                    mHandler);
        } else {
            startSearchTrackLog();
        }
    }

    /**
     * 获取设备定位点
     *
     * @param imeis 多个设备的IMEI
     */
    private void getMultipleLastConfigDeviceTraceLog(String imeis, long nextId) {
        AAAUserModel userModel = getTrackUserModel();
        if (userModel != null && !TextUtils.isEmpty(imeis)) {
            Date date = new Date();
            CarGpsRequestUtils.getMultipleLastConfigDeviceTraceLog(userModel, imeis,
                    TimeUtils.formatUTC(date.getTime(), "yyyy-MM-dd 00:00:00"),
                    TimeUtils.formatUTC(date.getTime(), "yyyy-MM-dd HH:mm:ss"), nextId,
                    REQUEST_ROWS,
                    mHandler);
        }
    }

    /**
     * 获取多个设备轨迹数据
     *
     * @param jsonArray 多个设备的数据
     */
    private void getMultipleDeviceTraceLog(JsonArray jsonArray) {
        if (!NetworkUtils.isNetworkAvailable()) {
            mQueryDeviceList.clear();
            startSearchTrackLog();
            XToastUtils.toast(getContext(), R.string.network_error_prompt);
            return;
        }
        AAAUserModel userModel = getTrackUserModel();
        if (userModel != null) {
            CarGpsRequestUtils.getMultipleDeviceTraceLog(userModel, jsonArray, mHandler);
        } else {
            startSearchTrackLog();
        }
    }

    /**
     * 发送指令
     */
    private void sendCommand(String command, String imei) {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork();
            return;
        }
        AAAUserModel userModel = getTrackUserModel();
        if (userModel != null && !TextUtils.isEmpty(imei)) {
            CarGpsRequestUtils.sendCommand(userModel, imei, command, mHandler);
        }
    }

    /**
     * 地图信息弹窗适配器
     */
    private final AMap.InfoWindowAdapter mInfoWindowAdapter = new AMap.InfoWindowAdapter() {

        @Override
        public View getInfoWindow(Marker marker) {
            // TODO Auto-generated method stub
            AAATrackModel model = null;
            if (marker.getObject() != null)
                model = (AAATrackModel) marker.getObject();
            int type = 0;
            if (marker.equals(mMobileMarker))
                type = 1;
            return FinalMapUtils.createTrackInfoContentView(mContext, model, type);
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
    private final AMap.OnInfoWindowClickListener mInfoWindowClickListener = new AMap
            .OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(Marker marker) {
            // TODO Auto-generated method stub
            marker.hideInfoWindow();
        }
    };

    private final AMap.OnMarkerClickListener mMarkerClickListener =
            new AMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    if (!marker.equals(mMobileMarker)) {
                        searchLocation(marker);
                    }
                    return false;
                }
            };

    /**
     * 定位监听
     */
    private final AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (null != location) {
                if (Math.round(location.getLatitude()) == 0 && Math.round(location.getLongitude()) == 0) {
                    return;
                }
                mMobileLatLng = new LatLng(location.getLatitude(), location.getLongitude());
//                if (mAMap != null && mDistanceBtn.isSelected())
//                    showLatLngBounds();
                refreshMobileOverlay(mMobileLatLng);
            }
        }
    };

    @OnClick({R.id.tracking_car_btn, R.id.tracking_mobile_btn, R.id.tracking_distance_btn, R.id
            .tracking_map_type_btn, R.id.tracking_zoom_in_btn, R.id.tracking_zoom_out_btn,
            R.id.tracking_show_marker, R.id.confirm, R.id.cancel, R.id.iv_refresh, R.id.selectAll,
            R.id.tracking_locus_btn, R.id.clTrackMode, R.id.clMore, R.id.clRefresh})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tracking_car_btn: // 定位车辆
//                if (mLatLng == null || mMarker == null)
//                    showMessage(R.string.no_device_gps_tips);
                if (mSearchMarker == null || !mSearchMarker.isVisible())
                    showMessage(R.string.no_device_gps_tips);
                else {
                    mCarBtn.setSelected(true);
                    mMobileBtn.setSelected(false);
                    mDistanceBtn.setSelected(false);
                    mUiSettings.setScrollGesturesEnabled(true);
//                    mAMap.animateCamera(CameraUpdateFactory.newLatLng(mLatLng));
//                    mMarker.showInfoWindow();
//                    mAMap.moveCamera(CameraUpdateFactory.zoomTo(cameraUpdate));
//                    mSearchMarker = mMarker;
//                    searchLocation(mMarker);
                    mSearchMarker.showInfoWindow();
                    mAMap.animateCamera(CameraUpdateFactory.newLatLng(mSearchMarker.getPosition()));
//                    mAMap.moveCamera(CameraUpdateFactory.zoomTo(cameraUpdate));
                    searchLocation(mSearchMarker);
                }
                break;
            case R.id.tracking_mobile_btn: // 定位手机
                mCarBtn.setSelected(false);
                mMobileBtn.setSelected(true);
                mDistanceBtn.setSelected(false);
                mUiSettings.setScrollGesturesEnabled(true);
                if (mMobileLatLng != null) {
                    mAMap.animateCamera(CameraUpdateFactory.newLatLng(mMobileLatLng));
                    mMobileMarker.showInfoWindow();
                }
                mAMap.moveCamera(CameraUpdateFactory.zoomTo(cameraUpdate));
                startLocation();
                tvDistantDisplay.setVisibility(View.VISIBLE);
                //显示距离文本
                tvDistantDisplay.setText(getString(R.string.tracking_distance,
                        AAAStringUtils.getMapDistance(
                                (long) AMapUtils.calculateLineDistance(mLatLng, mMobileLatLng))));
                break;
            case R.id.tracking_distance_btn: // 距离
                mCarBtn.setSelected(false);
                mMobileBtn.setSelected(false);
                mDistanceBtn.setSelected(true);
                mUiSettings.setScrollGesturesEnabled(true);
                isNeedBounds = true;
                showLatLngBounds();
                break;
            case R.id.tracking_map_type_btn: // 地图类型
                if (preventButtonFastReClick(stampTimeWhenClickSwitchMapType)) {
                    stampTimeWhenClickSwitchMapType = System.currentTimeMillis();
                    if (v.isSelected()) {
                        v.setSelected(false);
                        mAMap.setMapType(AMap.MAP_TYPE_NORMAL);
                    } else {
                        v.setSelected(true);
                        mAMap.setMapType(AMap.MAP_TYPE_SATELLITE);
                    }
                }
                break;
            case R.id.tracking_zoom_in_btn: // 地图放大
                mAMap.moveCamera(CameraUpdateFactory.zoomIn());
                break;
            case R.id.tracking_zoom_out_btn: // 地图缩小
                mAMap.moveCamera(CameraUpdateFactory.zoomOut());
                break;
            case R.id.confirm:
                mQueryDeviceList.clear();
                boolean hasSelect = false;
                for (AAADeviceModel model : mItemList) {
                    String deviceImei = model.getDeviceImei();
                    if (!TextUtils.isEmpty(deviceImei)) {
                        String selectType = mDeviceMap.get(deviceImei);
                        if (model.isSelected()) {
                            hasSelect = true;
                            mQueryDeviceList.add(deviceImei);
                            if (!"1".equals(selectType)) {
                                showDeviceTrack(deviceImei);
                            }
                            mDeviceMap.put(deviceImei, "1");
                        } else {
                            if ("1".equals(selectType)) {
                                hideDeviceTrack(deviceImei);
                            }
                            mDeviceMap.put(deviceImei, "0");
                        }
                    }
                }
                if (hasSelect) {
                    isChanged = true;
                    mDrawerLayout.closeDrawers();
                    isNeedBounds = true;
//                    isFindNewDevice = true;
//                    showDialog();
                    startSearchTrackLog();
                } else {
                    XToastUtils.toast(getContext(), R.string.select_device_prompt);
                }
                break;
            case R.id.cancel:
                mDrawerLayout.closeDrawers();
                break;
            case R.id.iv_refresh:
                countDown = 0; //刷新倒计时归零，开始获取数据并重置刷新时间
                break;
            case R.id.clRefresh:
                if (mIvRefresh.getVisibility() == View.VISIBLE) {
//                    for (AAADeviceModel model : mItemList) {
//                        String deviceImei = model.getDeviceImei();
//                        if (!TextUtils.isEmpty(deviceImei) && DeviceType.PET.getValue() ==
//                        model.getDeviceType()) {
//                            String selectType = mDeviceMap.get(deviceImei);
//                            if ("1".equals(selectType)) {
//                                sendCommand("COMMAND_IMMEDIAL_LOCATION", deviceImei);
//                            }
//                        }
//                    }
                    countDown = 0;
                    mTvRefresh.setText("10");
                    mTvRefresh.setVisibility(View.VISIBLE);
                    mIvRefresh.setVisibility(View.GONE);
                }
                break;
            case R.id.selectAll:
                boolean isChecked = selectAll.isChecked();
                for (int i = 0; i < mItemList.size(); i++) {
                    if (mItemList.get(i).isSelected() ^ isChecked) {
                        mItemList.get(i).setSelected(isChecked);
                    }
                }
                adapter.notifyDataSetChanged();
                break;
            case R.id.tracking_locus_btn:
                ActionSheet.createBuilder(getContext(),
                                Objects.requireNonNull(getActivity()).getSupportFragmentManager())
                        .setCancelButtonTitle(getString(R.string.cancel))
                        .setOtherButtonTitles(getString(R.string.google_map),
                                getString(R.string.gaode_map), getString(R.string.baidu_map))
                        .setCancelableOnTouchOutside(true)
                        .setListener(this).show();
                break;
            case R.id.tracking_show_marker:
                if (preventButtonFastReClick(stampTimeWhenClickSwitchShowMarker)) {
                    stampTimeWhenClickSwitchShowMarker = System.currentTimeMillis();
                    mShowMarker.setSelected(!mShowMarker.isSelected());
                    if (!mIvTrackMode.isSelected()) {
                        if (mShowMarker.isSelected()) {
//                        onShownStatusChanged();
                            showAllDeviceMarker();
                        } else {
                            hideAllDeviceMarker();
//                        if (mAMap != null) {
//                            mAMap.clear();
//                            if (mMobileMarker != null) {
//                                mMobileMarker.setVisible(true);
//                            }
//                            for (AAADeviceModel item : mItemList) {
//                                List<AAATrackModel> list = trackMap.get(item.getDeviceImei());
//                                showTrackPolyline(list, item.getDeviceImei());
//                            }
//                        }
//                        mShowMarker.setBackgroundResource(R.mipmap.ic_not_show_marker);
                        }
                    }
                }
                break;
            case R.id.clTrackMode:
                if (preventButtonFastReClick(stampTimeWhenClickSwitchShowMarker)) {
                    stampTimeWhenClickSwitchShowMarker = System.currentTimeMillis();
                    mIvTrackMode.setSelected(!mIvTrackMode.isSelected());
                    if (mIvTrackMode.isSelected()) {
                        hideAllDeviceMarker();
                        hideAllTrackLine();
                    } else {
                        showAllDeviceMarker();
                        showAllTrackLine();
                    }
                }
                break;
            case R.id.clMore: // 更多
                showMoreDialog();
                break;
            default:
                break;
        }
    }

    private void searchLocation(Marker marker) {
        if (marker != null && marker.getObject() != null) {
            mSearchMarker = marker;
            AAATrackModel model = (AAATrackModel) marker.getObject();
            StringUtils.setText(mMessageContent, model.getPositionDesc());
            StringUtils.setText(mWeatherContent, model.getWeather());
//            for (BaseItemBean item : mPagerItemList) {
//                if (item.getGroup().equals(model.getDeviceImei())) {
//                    item.setTitle(model.getPositionDesc());
//                    item.setContent(model.getWeather());
//                    mPagerAdapter.notifyDataSetChanged();
//                    break;
//                }
//            }
        }
    }

    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        if (mSearchMarker == null) {
            XToastUtils.toast(mContext, R.string.no_position_info);
            return;
        }
        LatLng latLng = convertLatLng(mSearchMarker.getPosition());
//        if (mSearchMarker == null) {
//            latLng = convertLatLng(mMarker.getPosition());
//        } else {
//            latLng = convertLatLng(mSearchMarker.getPosition());
//        }
//        String packageName;
        switch (index) {
            case 0:
                MapUtils.naviMap(mActivity, latLng.latitude, latLng.longitude, 3);
//                packageName = "com.google.android.apps.maps";
//                if (isInstallPackage(packageName)) {
//                    MapUtils.naviMap(mActivity, latLng.latitude, latLng.longitude, 3);
//                } else {
//                    showMessage(R.string.no_google_map_tips);
//                }
//                // Create a Uri from an intent string. Use the result to create an Intent.
//                Uri gmmIntentUri = Uri.parse("google.streetview:cbll=46.414382,10.013988");
//                // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
//                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//                // Make the Intent explicit by setting the Google Maps package
//                mapIntent.setPackage("com.google.android.apps.maps");
//                // Attempt to start an activity that can handle the Intent
//                startActivity(mapIntent);
                break;
            case 1:
                MapUtils.naviMap(mActivity, latLng.latitude, latLng.longitude, 0);
//                packageName = "com.autonavi.minimap";
//                if (isInstallPackage(packageName)) {
//                    String uriStr = "androidamap://navi?sourceApplication="
//                            + getString(R.string.app_name)
//                            + "&lat=" + latLng.latitude + "&lon=" + latLng.longitude
//                            + "&dev=1&style=2";
//                    PackageManager manager = mActivity.getPackageManager();
//                    Intent intent = manager.getLaunchIntentForPackage(packageName);
//                    intent.setAction(Intent.ACTION_VIEW);
//                    intent.addCategory(Intent.CATEGORY_DEFAULT);
//                    intent.setData(Uri.parse(uriStr));
//                    startActivity(intent);
//                } else {
//                    showMessage(R.string.no_gaode_map_tips);
//                }
                break;
            case 2:
                MapUtils.naviMap(mActivity, latLng.latitude, latLng.longitude, 1);
//                packageName = "com.baidu.BaiduMap";
//                if (isInstallPackage(packageName)) {
//                    MapUtils.naviMap(mActivity, latLng.latitude, latLng.longitude, 1);
//                } else {
//                    showMessage(R.string.no_baidu_map_tips);
//                }
                break;
        }
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
//        LatLng latLng = new LatLng(regeocodeResult.getRegeocodeQuery().getPoint().getLatitude(),
//                regeocodeResult.getRegeocodeQuery().getPoint().getLongitude());
//        String address = regeocodeResult.getRegeocodeAddress().getFormatAddress();
//        if (mSearchMarker.getPosition().longitude == latLng.longitude &&
//                mSearchMarker.getPosition().latitude == latLng.latitude) {
//            mMessageContent.setText(address);
//        }
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
        @SuppressLint("DefaultLocale")
        @Override
        public boolean handleMessage(Message msg) {
            if (fragmentIsDestroy)
                return false;
            try {
                AAABaseResponseBean responseBean;
                AAARequestBean requestBean;
                switch (msg.what) {
                    case 1:  // 倒计时
                        if ((countDown++) % mLocationRefreshInterval == 0
                                && !notYetSwitchedToThisFragment) {
                            if (mQueryDeviceList.size() == 0) {
                                boolean hasSelect = false;
                                for (AAADeviceModel model : mItemList) {
                                    String deviceImei = model.getDeviceImei();
                                    if (!TextUtils.isEmpty(deviceImei)) {
                                        String selectType = mDeviceMap.get(deviceImei);
                                        if ("1".equals(selectType)) {
                                            hasSelect = true;
                                            mQueryDeviceList.add(deviceImei);
                                        }
                                    }
                                }
                                if (hasSelect) {
//                                    showDialog();
                                    startSearchTrackLog();
                                }
                            }
//                            querySelectedDeviceLastLocation();
                        }
                        mCountDownText.setText(getString(R.string.tracking_count_down,
                                mLocationRefreshInterval - countDown % mLocationRefreshInterval));
                        if (mIvRefresh.getVisibility() == View.GONE) {
                            if (countDown == 10) {
                                mTvRefresh.setVisibility(View.GONE);
                                mIvRefresh.setVisibility(View.VISIBLE);
                            } else {
                                mTvRefresh.setText(String.format("%d", 10 - countDown));
                            }
                        }
                        break;
                    case TConstant.REQUEST_URL_GET_LAST_LOCATION:  //获取设备最后所在位置
                        if (msg.obj != null) {
                            responseBean = (AAABaseResponseBean) msg.obj;
                            if (responseBean.getCode() == TConstant.RESPONSE_SUCCESS) {
                                requestBean = mGson.fromJson(responseBean.getRequestObject(),
                                        AAARequestBean.class);
                                AAADeviceModel deviceModel =
                                        mGson.fromJson(mGson.toJson(responseBean.getData()),
                                                AAADeviceModel.class);

                                if (deviceModel.getLastLatitude() == null || deviceModel.getLastLongitude() == null)
                                    return false;
                                LatLng lastLatLng =
                                        convertLatLng(new LatLng(deviceModel.getLastLatitude(),
                                                deviceModel.getLastLongitude()));

                                for (int i = 0; i < tracks.size(); i++) {
                                    if (deviceModel.getDeviceImei().equals(tracks.get(i).getDeviceModel().getDeviceImei())) {
                                        tracks.get(i).getMarker().setPosition(lastLatLng);
//                                        tracks.get(i).getMarker().setObject(deviceModel);
//                                        tracks.get(i).updateDeviceModel(deviceModel);
                                        if (requestBean.getDeviceImei().equals(mDeviceModel.getDeviceImei())) {
                                            mDeviceModel = deviceModel;
                                            mLatLng = lastLatLng;
                                            if (mCarBtn.isSelected()) {
                                                mMarker.showInfoWindow();
                                                mAMap.moveCamera(CameraUpdateFactory.newLatLng(lastLatLng));
                                                mSearchMarker = mMarker;
                                                searchLocation(mMarker);
                                            } else {
                                                mSearchMarker.showInfoWindow();
                                            }
                                        }
                                        // 当位置变动时获取继续获取轨迹
                                        if (tracks.get(i).getLatLng().latitude != lastLatLng.latitude &&
                                                tracks.get(i).getLatLng().longitude != lastLatLng.longitude) {
                                            int deviceType = deviceModel.getDeviceType();
                                            List<AAATrackModel> subTrackModelList =
                                                    trackMap.get(deviceModel.getDeviceImei());
//                                            if (subTrackModelList != null && subTrackModelList
//                                            .size() != 0)
//                                                getHistoryLocation(subTrackModelList.get
//                                                (subTrackModelList.size() - 1).getLogId(),
//                                                deviceModel.getDeviceImei(), deviceType);
//                                            else
//                                                getHistoryLocation(0, deviceModel.getDeviceImei()
//                                                        , deviceType);
                                            tracks.get(i).updateLatLngs(lastLatLng);
                                            tracks.get(i).setLatLng(lastLatLng);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    case TConstant.REQUEST_URL_GET_LAST_DEVICE_CONFIG_TRACK:
                        if (msg.obj == null) {
                            mQueryDeviceList.clear();
                            startSearchTrackLog();
                            XToastUtils.toast(getContext(), R.string.network_error_prompt);
                        } else {
                            responseBean = (AAABaseResponseBean) msg.obj;
                            requestBean = mGson.fromJson(responseBean.getRequestObject(),
                                    AAARequestBean.class);
                            String deviceImei = requestBean.getDeviceImei();
                            if (responseBean.getCode() == TConstant.RESPONSE_SUCCESS && !TextUtils.isEmpty(deviceImei)) {
                                ListResponseBean listResponseBean =
                                        mGson.fromJson(mGson.toJson(responseBean.getData()),
                                                ListResponseBean.class);
                                if (listResponseBean != null && listResponseBean.getList() != null && listResponseBean.getList().size() > 0) {
                                    List<AAATrackModel> trackList =
                                            mGson.fromJson(mGson.toJson(listResponseBean.getList()),
                                                    new TypeToken<List<AAATrackModel>>() {
                                                    }.getType());
                                    if (trackList.size() > 0 && mQueryDeviceList.size() > 0 && deviceImei.equals(mQueryDeviceList.get(0))) {
                                        for (AAATrackModel model : trackList) {
                                            model.setDeviceImei(deviceImei);
                                            if (model.getLat() != 0 && model.getLat() != 0) {
                                                mTrackList.add(model);
                                            }
                                        }
                                        if (trackList.size() != REQUEST_ROWS) {
                                            mQueryDeviceList.remove(0);
                                            addDeviceTrackMarker(deviceImei);
                                        }
                                        startSearchTrackLog();
                                        return false;
                                    }
                                }
                            }
                            if (!TextUtils.isEmpty(deviceImei) && mQueryDeviceList.size() > 0 && deviceImei.equals(mQueryDeviceList.get(0))) {
                                mQueryDeviceList.remove(0);
                            }
                            startSearchTrackLog();
                        }
                        break;
                    case TConstant.REQUEST_URL_GET_MULTIPLE_DEVICE_TRACE_LOG: // 多设备轨迹
                        if (msg.obj == null) {
                            return false;
                        }
                        break;
                    case TConstant.REQUEST_GET_MULTIPLE_TRACE_LOG: // 多设备轨迹
                        if (msg.obj == null) {
                            mQueryDeviceList.clear();
                            startSearchTrackLog();
                            XToastUtils.toast(getContext(), R.string.network_error_prompt);
                        } else {
                            responseBean = (AAABaseResponseBean) msg.obj;
                            if (responseBean.getCode() == TConstant.RESPONSE_SUCCESS) {
                                List<Object> list =
                                        mGson.fromJson(mGson.toJson(responseBean.getData()),
                                                new TypeToken<List<Object>>() {
                                                }.getType());
                                if (list != null && list.size() > 0) {
                                    Map<String, Integer> numMap = new HashMap<>();
                                    boolean isHasTrack = false;
                                    for (Object obj : list) {
                                        ListResponseBean listResponseBean =
                                                mGson.fromJson(mGson.toJson(obj),
                                                        ListResponseBean.class);
                                        if (listResponseBean != null && listResponseBean.getList() != null
                                                && listResponseBean.getList().size() > 0) {
                                            List<AAATrackModel> trackList =
                                                    mGson.fromJson(mGson.toJson(listResponseBean.getList()),
                                                            new TypeToken<List<AAATrackModel>>() {
                                                            }.getType());
                                            if (trackList.size() > 0) {
                                                isHasTrack = true;
                                                for (AAATrackModel model : trackList) {
                                                    String deviceImei = model.getDeviceImei();
                                                    List<AAATrackModel> trackSecondList =
                                                            mTrackListMap.get(deviceImei);
                                                    if (trackSecondList == null) {
                                                        trackSecondList = new ArrayList<>();
                                                        mTrackListMap.put(deviceImei,
                                                                trackSecondList);
                                                    }
                                                    Integer num = numMap.get(deviceImei);
                                                    if (num == null) {
                                                        num = 1;
                                                    } else {
                                                        num += 1;
                                                    }
                                                    numMap.put(deviceImei, num);
                                                    if (Math.round(model.getLat()) != 0
                                                            && Math.round(model.getLng()) != 0) {
                                                        trackSecondList.add(model);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    for (int i = mQueryDeviceList.size() - 1; i >= 0; i--) {
                                        String deviceImei = mQueryDeviceList.get(i);
                                        if (TextUtils.isEmpty(deviceImei)) {
                                            mQueryDeviceList.remove(i);
                                        } else {
                                            Integer num = numMap.get(deviceImei);
                                            if (num == null || num == 0 || num % REQUEST_ROWS != 0) {
                                                mQueryDeviceList.remove(i);
                                            }
                                        }
                                    }
                                    if (isHasTrack) {
                                        startSearchTrackLog();
                                        return false;
                                    }
                                }
                            }
                            mQueryDeviceList.clear();
                            startSearchTrackLog();
                        }
                        break;
                    case TConstant.REQUEST_SEND_COMMAND: // 发送指令
                        responseBean = (AAABaseResponseBean) msg.obj;
                        if (responseBean.getCode() == TConstant.RESPONSE_SUCCESS) {
                            XToastUtils.toast(getContext(),
                                    getString(R.string.send_success_prompt));
                        } else {
                            showMessage(ErrorCode.getResId(responseBean.getCode()));
                        }
                        break;
                    case MOVING_MARKER: {
                        Marker marker = (Marker) msg.obj;
                        LatLng latLng2 = new LatLng(marker.getPosition().latitude + 0.1d,
                                marker.getPosition().longitude + 0.1d);
                        marker.setPosition(latLng2);
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

    @SuppressLint("NotifyDataSetChanged")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveMessage(PostMessage event) {
        if (CWConstant.POST_MESSAGE_CHANGE_DEVICE == event.getType()) {
//            mDrawerLayout.closeDrawers();
//            AAADeviceModel deviceModel = getTrackDeviceModel();
//            for (int i = 0; i < mItemList.size(); i++) {
//                if (deviceModel.getDeviceImei().equals(mItemList.get(i).getDeviceImei())) {
//                    mDeviceModel = tracks.get(i).getDeviceModel();
//                    mMarker = tracks.get(i).getMarker();
//                    mLatLng = tracks.get(i).getLatLng();
//                    if (!mItemList.get(i).isSelected()) {
//                        mItemList.get(i).setSelected(true);
//                        shownLatLgnList.add(tracks.get(i).getLatLng());
//                        onShownStatusChanged();
//                    }
//                    break;
//                }
//            }
//            adapter.notifyDataSetChanged();
        } else if (CWConstant.POST_MESSAGE_CHANGE_REFRESH_INTERVAL == event.getType()) {
            mLocationRefreshInterval =
                    SettingSPUtils.getInstance().getInt(TConstant.LOCATION_REFRESH_INTERVAL,
                            CWConstant.DEFAULT_LOCAL_REFRESH_INTERVAL);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mMapView != null)
            mMapView.onResume();

        if (mTimer != null)
            mTimer.cancel();
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                mHandler.sendEmptyMessage(1);
            }
        }, 0, 1000);
//        if (!mMobileBtn.isSelected() && !mCarBtn.isSelected() && !mDistanceBtn.isSelected()) {
//            mDistanceBtn.setSelected(true);
//            showLatLngBounds();
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mTimer != null)
            mTimer.cancel();
        mTimer = null;
        if (mMapView != null)
            mMapView.onPause();
    }

    public void onSwitchPage(boolean isCurrentPage) {
        if (isCurrentPage) {
            if (notYetSwitchedToThisFragment) {
                notYetSwitchedToThisFragment = false;
                isNeedBounds = true;
//                isFindNewDevice = true;
//                showDialog();
                startSearchTrackLog();
            }
        } else {
//            MainApplication.getInstance().setTrackDeviceModel(mDeviceModel);
        }
    }

//    private void getSelectedDeviceHistoryTrack() {
////        String imei = "";
////        for (AAADeviceModel item : mItemList) {
////            if (item.isSelected() && !TextUtils.isEmpty(item.getDeviceImei())) {
////                imei = String.format("%s,%s", imei, item.getDeviceImei());
////            }
////        }
////        if (imei.length() > 0) {
////            getMultipleLastConfigDeviceTraceLog(imei.substring(1), 0);
////        }
//        for (AAADeviceModel item : mItemList) {
//            if (item.isSelected())
//                getHistoryLocation(0, item.getDeviceImei(), item.getDeviceType());
//        }
//    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        fragmentIsDestroy = true;
        stopLocation();
        destroyLocation();
        // 关闭定位图层
        if (mAMap != null) {
            mAMap.clear();
            mAMap.setMyLocationEnabled(false);
        }
        if (mMapView != null)
            mMapView.onDestroy();
        mMapView = null;
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * @param list       轨迹列表
     * @param deviceImei 国际移动设备唯一识别码
     * @param startIndex 开始的点
     */
    private void initTrack(List<AAATrackModel> list, String deviceImei, int startIndex) {
        if (list.size() == 0)
            return;
        List<LatLng> latLngs = new ArrayList<>();
        AAADeviceModel deviceModel = null;
        int color = 0;
        int supplementColor = 0;
        int supplementFlag = 0;
        boolean change = false;
        for (int j = 0; j < mItemList.size(); j++) {
            if (mItemList.get(j).getDeviceImei().equals(deviceImei)) {
                color = tracks.get(j).getColor();
                supplementColor = tracks.get(j).getSupplementColor();
                tracks.get(j).getMarker().setVisible(true); // 有轨迹显示marker
                break;
            }
        }
        List<AAATrackModel> trackModelList = Objects.requireNonNull(trackMap.get(deviceImei));
        if (startIndex != 0 && trackModelList.size() > 0) {
            latLngs.add(convertLatLng(new LatLng(
                    trackModelList.get(startIndex - 1).getLat()
                    , trackModelList.get(startIndex - 1).getLng())));
            if ((supplementFlag = trackModelList.get(startIndex - 1).getSupplement()) != 1) {
                supplementFlag = 0;
            }
        }
        for (int i = 0; i < mItemList.size(); i++) {
            if (mItemList.get(i).getDeviceImei().equals(deviceImei)) {
                deviceModel = mItemList.get(i);
                break;
            }
        }
        for (int i = startIndex; i < list.size(); i++) {
            AAADeviceModel subDeviceModel = FinalMapUtils.transformTrackToDevice(list.get(i));
//            if (i%20 >10)
//                list.get(i).setSupplement(1);
            if (deviceModel != null && deviceModel.isSelected()) {
                LatLng latLng = convertLatLng(new LatLng(list.get(i).getLat(),
                        list.get(i).getLng()));
                if (supplementFlag != list.get(i).getSupplement()) {
                    change = true;
                }
                latLngs.add(latLng);
                Marker marker;
                if (mShowMarker.isSelected()) {
                    if (i == 0) {
                        marker = mAMap.addMarker(new MarkerOptions().position(latLng)
                                .icon(BitmapDescriptorFactory.fromBitmap(NewMapUtils.getDrivingMarkerIcon(getResources(), 0))));
                    } else {
                        if (list.get(i).getSupplement() == 1)
                            marker =
                                    mAMap.addMarker(new MarkerOptions().position(latLng).icon(supplementBitmapDes).anchor(0.5f, 0.5f));
                        else
                            marker =
                                    mAMap.addMarker(new MarkerOptions().position(latLng).icon(bitmapDes).anchor(0.5f, 0.5f));
                        if (i == list.size() - 1) {
                            for (int j = 0; j < mItemList.size(); j++) {
                                if (mItemList.get(j).getDeviceImei().equals(deviceImei)) {
                                    tracks.get(j).getMarker().setObject(subDeviceModel);
                                    break;
                                }
                            }
                        }
                    }
                    marker.setObject(subDeviceModel);
                }
                if (change || i == list.size() - 1) {
                    change = false;
                    LatLng latLng1 = latLngs.get(latLngs.size() - 1);
                    if (supplementFlag == 0)
                        mAMap.addPolyline(new PolylineOptions()
                                .width(POLYLINE_WIDTH)
//                                .setCustomTexture(bitmapDescriptor)
                                .color(color)
                                .addAll(latLngs));
                    else
                        mAMap.addPolyline(new PolylineOptions()
                                .width(POLYLINE_WIDTH)
                                .color(supplementColor)
//                                .setCustomTexture(supplementBitmapDescriptor)
                                .addAll(latLngs));
                    supplementFlag = list.get(i).getSupplement();
                    latLngs.clear();
                    if (i >= 1)
                        latLngs.add(convertLatLng(new LatLng(list.get(i - 1).getLat(),
                                list.get(i - 1).getLng())));
                    latLngs.add(latLng1);
                }
            }
        }
    }

    private void showTrackPolyline(List<AAATrackModel> list, String deviceImei) {
        if (list == null || list.size() == 0)
            return;
        int color = 0;
        int supplementColor = 0;
        boolean isSupplementPoint = false;
        List<LatLng> latLngs = new ArrayList<>();
        for (int j = 0; j < mItemList.size(); j++) {
            if (mItemList.get(j).getDeviceImei().equals(deviceImei)) {
                color = tracks.get(j).getColor();
                supplementColor = tracks.get(j).getSupplementColor();
//                tracks.get(j).getMarker().setVisible(true); // 有轨迹显示marker
                break;
            }
        }
        for (int i = 0; i < list.size(); i++) {
            AAATrackModel track = list.get(i);
            LatLng latLng = new LatLng(track.getLat(), track.getLng());
            latLngs.add(latLng);
        }
        mAMap.addPolyline(new PolylineOptions()
                .width(POLYLINE_WIDTH)
                .color(color)
                .addAll(latLngs));
    }
}