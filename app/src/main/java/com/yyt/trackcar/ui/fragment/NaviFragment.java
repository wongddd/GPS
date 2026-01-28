package com.yyt.trackcar.ui.fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.amap.api.maps.CoordinateConverter;
import com.github.pengrad.mapscaleview.MapScaleView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.picker.widget.TimePickerView;
import com.xuexiang.xui.widget.picker.widget.builder.TimePickerBuilder;
import com.xuexiang.xui.widget.picker.widget.listener.OnTimeSelectListener;
import com.xuexiang.xui.widget.tabbar.TabControlView;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.DeviceSettingsBean;
import com.yyt.trackcar.bean.GpsBean;
import com.yyt.trackcar.bean.RequestBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.DeviceSettingsModel;
import com.yyt.trackcar.dbflow.LocationModel;
import com.yyt.trackcar.dbflow.LocationModel_Table;
import com.yyt.trackcar.dbflow.TrackModel;
import com.yyt.trackcar.dbflow.TrackModel_Table;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CWRequestUtils;
import com.yyt.trackcar.utils.DialogUtils;
import com.yyt.trackcar.utils.MapUtils;
import com.yyt.trackcar.utils.PositionUtils;
import com.yyt.trackcar.utils.TimeUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      NaviFragment
 * @ author:        QING
 * @ createTime:    2020/3/6 12:08
 * @ describe:      TODO 导航追踪页面
 */
@Page(name = "Navi")
public class NaviFragment extends BaseFragment implements OnMapReadyCallback,
        GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraIdleListener {
    @BindView(R.id.tvInfoTitle)
    TextView mTvInfoTitle; // 定位信息标题
    @BindView(R.id.tabView)
    TabControlView mTabView; // 选项卡布局
    @BindView(R.id.scaleView)
    MapScaleView mScaleView; // 比例尺布局
    @BindView(R.id.xllPlay)
    View mPlayView; // 播放布局
    @BindView(R.id.xllInfo)
    View mInfoView; // 信息布局
    @BindView(R.id.playBtn)
    ImageButton mPlayBtn; // 开始/暂停按钮
    @BindView(R.id.sbProgress)
    SeekBar mSbProgress; // 播放进度条
    @BindView(R.id.sbSpeed)
    SeekBar mSbSpeed; // 速度条进度条
    //    @BindView(R.id.tvInfoContent)
//    TextView mTvInfoContent; // 定位信息标题
    private GoogleMap mMap;
    private Marker mMarker; // 设备定位图标
    // UI相关
    private UiSettings mUiSettings;
    private TimePickerView mDatePicker; // 日期选择器
    //    private Date mDate; // 当前选中时间
    private LatLng mLatLng; // 定位坐标
    private boolean isLoad; // 是否已加载
    private boolean isGetToday; // 是否获取今天轨迹
    private String mSelectDate = "0"; // 选中时间
    private Timer mTimer; // 计时器
    private List<TrackModel> mItemList = new ArrayList<>(); // 轨迹列表
    private List<LatLng> mLatLngItemList = new ArrayList<>(); // 轨迹列表
    private SparseArray<Marker> mMarkerMap = new SparseArray<>(); // 特殊点标记

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_navi;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(R.string.navi_track);
//        titleBar.addAction(new TitleBar.ImageAction(R.drawable.ic_calendar) {
//            @Override
//            public void performAction(View view) {
//                showDatePicker();
//            }
//        });
        return titleBar;
    }

    @Override
    protected void initViews() {
        try {
            mTabView.setItems(new String[]{getString(R.string.before_yestoday),
                    getString(R.string.yestoday), getString(R.string.today)}, new String[]{"2",
                    "1", "0"});
            mTabView.setDefaultSelection(2);
        } catch (Exception e) {
            if (BuildConfig.DEBUG)
                e.printStackTrace();
        }
        mSbSpeed.setMax(1900);
        mTvInfoTitle.setText(String.format("%s  %s",
                getString(R.string.navi_track_no_data), getString(R.string.today)));
//        mDate = new Date();
//        mTvInfoTitle.setText(String.format("%s  %s", getString(R.string.navi_track_no_data),
//                TimeUtils.getDateDescriptionByNow(mActivity,
//                        TimeUtils.formatUTC(System.currentTimeMillis(), ""))));
//        mTvInfoContent.setText("孩子使用手表导航时，客在此查看其行走情况");
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
                            !TextUtils.isEmpty(locationModel.getLng()))
                        mLatLng = new LatLng(Double.parseDouble(locationModel.getLat()),
                                Double.parseDouble(locationModel.getLng()));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                if (mLatLng != null && MapUtils.isInsideChina(mLatLng.latitude,
                        mLatLng.longitude)) {
                    GpsBean gpsBean = PositionUtils.gps84_To_Gcj02(mLatLng.latitude,
                            mLatLng.longitude);
                    mLatLng = new LatLng(gpsBean.getWgLat(), gpsBean.getWgLon());
                }
            }
        }
        initMap();
        watchTrack(TimeUtils.formatUTC(System.currentTimeMillis(), "yyyy-MM-dd"));
        getLocationFrequency();
    }

    @Override
    protected void initListeners() {
        mTabView.setOnTabSelectionChangedListener(new TabControlView.OnTabSelectionChangedListener() {
            @Override
            public void newSelection(String title, String value) {
                if (mTimer != null)
                    mTimer.cancel();
                mPlayBtn.setSelected(false);
                if (mMap != null)
                    mMap.clear();
                mMarker = null;
                mLatLngItemList.clear();
                mItemList.clear();
                mMarkerMap.clear();
                mPlayView.setVisibility(View.GONE);
                mSelectDate = value;
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                if ("2".equals(mSelectDate))
                    calendar.add(Calendar.DATE, -2);
                else if ("1".equals(mSelectDate))
                    calendar.add(Calendar.DATE, -1);
                String dateTime = TimeUtils.formatUTC(calendar.getTime().getTime(), "yyyy-MM-dd");
//                if (!"0".equals(mSelectDate)) {
//                    AAADeviceModel deviceModel = getDevice();
//                    if (deviceModel != null) {
//                        OperatorGroup operatorGroup = OperatorGroup.clause(OperatorGroup.clause()
//                                .and(TrackModel_Table.imei.eq(deviceModel.getImei()))
//                                .and(TrackModel_Table.date.eq(dateTime)));
//                        TrackModel trackModel = SQLite.select().from(TrackModel.class)
//                                .where(operatorGroup)
//                                .querySingle();
//                        if (trackModel != null) {
//                            mInfoView.setVisibility(View.GONE);
//                            initMapTrack(dateTime);
//                            return;
//                        }
//                    }
//                }
                mInfoView.setVisibility(View.VISIBLE);
                mTvInfoTitle.setText(String.format("%s  %s", getString(R.string.navi_track_query)
                        , title));
                watchTrack(dateTime);
            }
        });
        mSbProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                refreshSmoothMarker();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    /**
     * 初始化历史轨迹
     */
    private void initMapTrack(String date) {
        DeviceModel deviceModel = getDevice();
        if (deviceModel != null && mMap != null) {
            mMap.clear();
            List<LatLng> mLatLngList = new ArrayList<>();
            LatLngBounds.Builder latLngBoundsBuilder =
                    new LatLngBounds.Builder();
            OperatorGroup operatorGroup = OperatorGroup.clause(OperatorGroup.clause()
                    .and(TrackModel_Table.imei.eq(deviceModel.getImei()))
                    .and(TrackModel_Table.date.eq(date)));
            List<TrackModel> mTrackList = SQLite.select().from(TrackModel.class)
                    .where(operatorGroup)
                    .orderBy(TrackModel_Table.uploadtime, true)
                    .queryList();
            if (mTimer != null)
                mTimer.cancel();
            mMarkerMap.clear();
            mItemList = mTrackList;
            for (int i = 0; i < mTrackList.size(); i++) {
                TrackModel model = mTrackList.get(i);
                LatLng latLng =
                        new LatLng(Double.parseDouble(model.getLat()),
                                Double.parseDouble(model.getLng()));
                CoordinateConverter converter = new CoordinateConverter(getContext());
                if (model.getLocationType() == 0) {
                    converter.from(CoordinateConverter.CoordType.GPS);
                    converter.coord(new com.amap.api.maps.model.LatLng(latLng.latitude,
                            latLng.longitude));
                    com.amap.api.maps.model.LatLng convertLatLng = converter.convert();
                    if (CoordinateConverter.isAMapDataAvailable(convertLatLng.latitude,
                            convertLatLng.longitude))
                        latLng = new LatLng(convertLatLng.latitude, convertLatLng.longitude);
                }
                mLatLngList.add(latLng);
                latLngBoundsBuilder.include(latLng);
                refreshOverlay(i, latLng);
            }
            mLatLngItemList = mLatLngList;
            if (mLatLngList.size() > 1) {
                mPlayView.setVisibility(View.VISIBLE);
                mInfoView.setVisibility(View.GONE);
                mSbProgress.setMax(mLatLngItemList.size() - 1);
                mSbProgress.setProgress(0);
                refreshSmoothMarker();
                mMap.addPolyline(new PolylineOptions().addAll(mLatLngList).width
                        (getResources().getDimension(R.dimen.navi_line_size)).color(ContextCompat.getColor(mActivity, R.color.colorNaviLine)));
//                mMap.addPolyline(new PolylineOptions().addAll(mLatLngList).width
//                        (getResources().getDimension(R.dimen.margin_2)).color(ContextCompat.getColor(mActivity, R.color.white)).pattern(Arrays.asList(new Dot(), new Gap(20))));
                if (isLoad)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBoundsBuilder.build(),
                            getResources().getDimensionPixelOffset(R.dimen.margin_24)));
                else
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(mLatLngList.get(0)));
                if (mMap.getCameraPosition().zoom > 15)
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
            } else if (mLatLngList.size() == 1) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(mLatLngList.get(0)));
                if (mMap.getCameraPosition().zoom > 15)
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                mPlayView.setVisibility(View.GONE);
                mInfoView.setVisibility(View.VISIBLE);
                String day;
                if ("2".equals(mTabView.getChecked()))
                    day = getString(R.string.before_yestoday);
                else if ("1".equals(mTabView.getChecked()))
                    day = getString(R.string.yestoday);
                else
                    day = getString(R.string.today);
                mTvInfoTitle.setText(String.format("%s  %s",
                        getString(R.string.navi_track_no_data_play), day));
            } else {
                mPlayView.setVisibility(View.GONE);
                mInfoView.setVisibility(View.VISIBLE);
                String day;
                if ("2".equals(mTabView.getChecked()))
                    day = getString(R.string.before_yestoday);
                else if ("1".equals(mTabView.getChecked()))
                    day = getString(R.string.yestoday);
                else
                    day = getString(R.string.today);
                mTvInfoTitle.setText(String.format("%s  %s",
                        getString(R.string.navi_track_no_data), day));
            }
//            if (mLatLngList.size() == 0)
//                mTvInfoTitle.setText(String.format("%s  %s",
//                        getString(R.string.navi_track_no_data),
//                        TimeUtils.getDateDescriptionByNow(mActivity,
//                                TimeUtils.formatUTC(mDate.getTime(), ""))));
//            else
//                mTvInfoTitle.setText(String.format("%s  %s",
//                        getString(R.string.navi_track_content),
//                        TimeUtils.getDateDescriptionByNow(mActivity,
//                                TimeUtils.formatUTC(mDate.getTime(), ""))));
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
     * 日期选择器
     */
    private void showDatePicker() {
        if (mDatePicker == null) {
            mDatePicker = new TimePickerBuilder(mActivity, new OnTimeSelectListener() {
                @Override
                public void onTimeSelected(Date date, View v) {
//                    mDate = date;
                    String dateTime = TimeUtils.formatUTC(date.getTime(), "yyyy-MM-dd");
                    initMapTrack(dateTime);
                    watchTrack(dateTime);
                }
            })
                    .setTitleText("")
                    .setSubmitText(getString(R.string.confirm))
                    .setCancelText(getString(R.string.cancel))
                    .build();
        }
        mDatePicker.show();
    }

    /**
     * 刷新设备图层
     *
     * @param index  位置
     * @param latLng 坐标
     */
    private void refreshOverlay(int index, LatLng latLng) {
        if (mMap == null)
            mMarkerMap.put(index, null);
        else {
            TrackModel trackModel = mItemList.get(index);
            MarkerOptions option;

            if (index == 0)
                option = new MarkerOptions().position(latLng)
                        .draggable(false).title("").snippet("").icon(BitmapDescriptorFactory.fromBitmap(zoomImg(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_maker_start), getResources().getDimensionPixelSize(R.dimen.navi_marker_width), getResources().getDimensionPixelSize(R.dimen.navi_marker_height))));
            else if (index == mItemList.size() - 1)
                option = new MarkerOptions().position(latLng)
                        .draggable(false).title("").snippet("").icon(BitmapDescriptorFactory.fromBitmap(zoomImg(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_maker_end), getResources().getDimensionPixelSize(R.dimen.navi_marker_width), getResources().getDimensionPixelSize(R.dimen.navi_marker_height))));
            else
                option = new MarkerOptions().position(latLng)
                        .draggable(false).title("").snippet("").icon(BitmapDescriptorFactory.fromBitmap(zoomImg(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_maker_dot), getResources().getDimensionPixelSize(R.dimen.navi_dot_size), getResources().getDimensionPixelSize(R.dimen.navi_dot_size)))).anchor(0.5F, 0.5F);
            // 将Marker设置为贴地显示，可以双指下拉地图查看效果
            option.flat(true);//设置marker平贴地图效果
            Marker marker = mMap.addMarker(option);
            marker.setTag(trackModel);
            mMarkerMap.put(index, marker);
        }
    }

    public Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    /**
     * 刷新设备图层
     */
    private void refreshSmoothMarker() {
        if (mMap != null) {
            if (mItemList.size() == 1) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(mLatLngItemList.get(0)));
                if (mMarkerMap.get(0) != null)
                    mMarkerMap.get(0).showInfoWindow();
            } else if (mSbProgress.getProgress() == mSbProgress.getMax()) {
                if (mTimer != null)
                    mTimer.cancel();
                if (mMarker != null)
                    mMarker.setVisible(false);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(mLatLngItemList.get(mSbProgress.getProgress())));
                if (mMarkerMap.get(mSbProgress.getProgress()) != null)
                    mMarkerMap.get(mSbProgress.getProgress()).showInfoWindow();
                if (mPlayBtn.isSelected()) {
                    onClick(mPlayBtn);
//                    mSbProgress.setProgress(0);
                }
            } else {
                LatLng latLng = mLatLngItemList.get(mSbProgress.getProgress());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                if (mMarkerMap.get(mSbProgress.getProgress()) == null) {
                    TrackModel trackModel = mItemList.get(mSbProgress.getProgress());
                    if (mMarker == null) {
                        MarkerOptions option = new MarkerOptions().position(latLng).draggable
                                (false).title("").snippet("");
                        // 将Marker设置为贴地显示，可以双指下拉地图查看效果
                        option.flat(true);//设置marker平贴地图效果
                        mMarker = mMap.addMarker(option);
                    } else
                        mMarker.setPosition(latLng);
                    mMarker.setTag(trackModel);
                    mMarker.setVisible(true);
                    mMarker.showInfoWindow();
                } else {
                    if (mMarker != null)
                        mMarker.setVisible(false);
                    mMarkerMap.get(mSbProgress.getProgress()).showInfoWindow();
                }
            }
        }
    }

    /**
     * 获取定位轨迹
     *
     * @param time 日期时间
     */
    private void watchTrack(String time) {
        if (!NetworkUtils.isNetworkAvailable()) {
            String day;
            if ("2".equals(mTabView.getChecked()))
                day = getString(R.string.before_yestoday);
            else if ("1".equals(mTabView.getChecked()))
                day = getString(R.string.yestoday);
            else
                day = getString(R.string.today);
            mTvInfoTitle.setText(String.format("%s  %s",
                    getString(R.string.network_error_prompt), day));
            return;
        }
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null) {
            CWRequestUtils.getInstance().watchTrack(getContext(), userModel.getToken(),
                    deviceModel.getD_id(), String.format("%s%%2000:00:00", time), String.format(
                            "%s%%2023:59:59", time), mHandler);
        }
    }

    /**
     * 获取定位频率设置
     */
    private void getLocationFrequency() {
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().getLocationFrequency(getContext(), getIp(),
                    userModel.getToken(), deviceModel.getD_id(), mHandler);
    }

    @SingleClick
    @OnClick({R.id.clZoomIn, R.id.clZoomOut, R.id.playBtn})
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
            case R.id.playBtn: // 播放钮
                if (v.isSelected()) {
                    if (mTimer != null)
                        mTimer.cancel();
                    v.setSelected(false);
                } else {
                    v.setSelected(true);
                    if (mSbProgress.getProgress() == mSbProgress.getMax())
                        mSbProgress.setProgress(0);
                    else
                        refreshSmoothMarker();
                    mTimer = new Timer();
                    mTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            mHandler.sendEmptyMessage(1);
                        }
                    }, 2000 - mSbSpeed.getProgress(), 2000 - mSbSpeed.getProgress());
                }
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
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                isLoad = true;
            }
        });
        mMap.setOnCameraMoveListener(this);
        mMap.setOnCameraIdleListener(this);
        mMap.setOnInfoWindowClickListener(mInfoWindowClickListener);
        mMap.setInfoWindowAdapter(mInfoWindowAdapter);
        if (mLatLng != null)
            mMap.moveCamera(CameraUpdateFactory.newLatLng(mLatLng));
        if (isGetToday)
            initMapTrack(TimeUtils.formatUTC(System.currentTimeMillis(), "yyyy-MM-dd"));
    }

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
            TrackModel trackModel = (TrackModel) marker.getTag();
            if (trackModel == null)
                return null;
            else
                mAddressText.setText(TimeUtils.formatUTC(trackModel.getUploadtime(), "HH:mm"));
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
                    case 1:  // 播放轨迹
                        if (mPlayBtn.isSelected() && mSbProgress.getProgress() < mSbProgress.getMax())
                            mSbProgress.setProgress(mSbProgress.getProgress() + 1);
                        else if (mTimer != null)
                            mTimer.cancel();
                        break;
                    case CWConstant.REQUEST_URL_WATCH_TRACK: // 获取定位轨迹
                        if (msg.obj == null) {
                            String day;
                            if ("2".equals(mTabView.getChecked()))
                                day = getString(R.string.before_yestoday);
                            else if ("1".equals(mTabView.getChecked()))
                                day = getString(R.string.yestoday);
                            else
                                day = getString(R.string.today);
                            mTvInfoTitle.setText(String.format("%s  %s",
                                    getString(R.string.request_unkonow_prompt), day));
                        } else {
                            resultBean = (RequestResultBean) msg.obj;
                            requestBean =
                                    mGson.fromJson(mGson.toJson(resultBean.getRequestObject()),
                                            RequestBean.class);
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(new Date());
                            if ("2".equals(mTabView.getChecked()))
                                calendar.add(Calendar.DATE, -2);
                            else if ("1".equals(mTabView.getChecked()))
                                calendar.add(Calendar.DATE, -1);
                            String dateTime = TimeUtils.formatUTC(calendar.getTime().getTime(),
                                    "yyyy-MM-dd");
                            if (requestBean.getStartTime().substring(0, 10).equals(dateTime)) {
                                if (resultBean.getCode() == CWConstant.SUCCESS || resultBean.getCode() == CWConstant.NOT_RESULT) {
                                    DeviceModel deviceModel = getDevice();
                                    if (deviceModel != null && deviceModel.getD_id() == requestBean.getD_id()) {
                                        if (resultBean.getList() != null) {
                                            for (Object obj : resultBean.getList()) {
                                                TrackModel model = mGson.fromJson(mGson.toJson(obj),
                                                        TrackModel.class);
                                                if (!TextUtils.isEmpty(model.getLat()) && !TextUtils.isEmpty(model.getLng())) {
                                                    model.setImei(deviceModel.getImei());
                                                    model.setDate(TimeUtils.formatUTCC(model.getUploadtime(),
                                                            "yyyy-MM-dd"));
                                                    model.save();
                                                }
                                            }
                                        }
                                    }
                                    initMapTrack(requestBean.getStartTime().substring(0, 10));
                                    isGetToday = true;
                                } else {
                                    String day;
                                    if ("2".equals(mTabView.getChecked()))
                                        day = getString(R.string.before_yestoday);
                                    else if ("1".equals(mTabView.getChecked()))
                                        day = getString(R.string.yestoday);
                                    else
                                        day = getString(R.string.today);
                                    mTvInfoTitle.setText(String.format("%s  %s",
                                            getString(R.string.request_error_prompt), day));
                                }
                            }
                        }
                        break;
                    case CWConstant.REQUEST_URL_GET_LOCATION_FREQUENCY: // 获取定位频率设置
                        if (msg.obj != null) {
                            resultBean = (RequestResultBean) msg.obj;
                            if (!TextUtils.isEmpty(resultBean.getService_ip()) &&
                                    !resultBean.getService_ip().equals(resultBean.getLast_online_ip())) {
                                userModel = getUserModel();
                                DeviceModel deviceModel = getDevice();
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                if (userModel != null && deviceModel != null && deviceModel.getD_id() == requestBean.getD_id()) {
                                    DeviceSettingsModel settingsModel = getDeviceSettings();
                                    settingsModel.setIp(resultBean.getLast_online_ip());
                                    settingsModel.save();
                                    CWRequestUtils.getInstance().getLocationFrequency(getContext(),
                                            resultBean.getLast_online_ip(), userModel.getToken(),
                                            deviceModel.getD_id(), mHandler);
                                }
                            } else if (resultBean.getCode() == CWConstant.SUCCESS) {
                                DeviceModel deviceModel = getDevice();
                                userModel = getUserModel();
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                if (userModel != null && deviceModel.getD_id() == requestBean.getD_id()) {
                                    DeviceSettingsBean settingsBean =
                                            mGson.fromJson(mGson.toJson(resultBean.getResultBean()),
                                                    DeviceSettingsBean.class);
                                    DeviceSettingsModel settingsModel = getDeviceSettings();
                                    settingsModel.setLocationMode(settingsBean.getTime());
                                    settingsModel.save();
                                    if (TextUtils.isEmpty(settingsBean.getTime()) || "0".equals(settingsBean.getTime()))
                                        mMaterialDialog =
                                                DialogUtils.customMaterialDialog(mActivity,
                                                        mMaterialDialog, getString(R.string.prompt),
                                                        getString(R.string.location_type_prompt),
                                                        getString(R.string.i_know));
                                }
                            }
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
        if (mTimer != null)
            mTimer.cancel();
        mMap = null;
        if (mDatePicker != null && mDatePicker.isShowing())
            mDatePicker.dismiss();
        super.onDestroy();
    }

}
