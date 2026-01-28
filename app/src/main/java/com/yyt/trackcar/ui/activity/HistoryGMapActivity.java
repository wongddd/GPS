package com.yyt.trackcar.ui.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CoordinateConverter;
import com.baoyz.actionsheet.ActionSheet;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAABaseResponseBean;
import com.yyt.trackcar.bean.AAATrackModel;
import com.yyt.trackcar.bean.ListResponseBean;
import com.yyt.trackcar.dbflow.AAADeviceModel;
import com.yyt.trackcar.dbflow.AAAUserModel;
import com.yyt.trackcar.ui.base.BaseActivity;
import com.yyt.trackcar.ui.fragment.ShareToTrackCircleFragment;
import com.yyt.trackcar.utils.AAAStringUtils;
import com.yyt.trackcar.utils.BitmapBlobUtils;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.FinalMapUtils;
import com.yyt.trackcar.utils.MapUtils;
import com.yyt.trackcar.utils.NewMapUtils;
import com.yyt.trackcar.utils.TConstant;
import com.yyt.trackcar.utils.TransformImageAppearance;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 项目名：   传信鸽
 * 包名：     com.yyt.trackcar.ui.activity
 * 文件名：   HistoryMapGoogleActivity
 * 创建者：   QING
 * 创建时间： 2018/6/1 17:33
 * 描述：     TODO 历史轨迹显示页面(Google Map)
 */
@SuppressLint("NonConstantResourceId")
public class HistoryGMapActivity extends BaseActivity implements
        OnMapReadyCallback, ActionSheet.ActionSheetListener, View.OnClickListener {
    private GoogleMap mMap;
    private Marker mMarker; // 设备定位图标
    private Marker mSearchMarker;
    //private SmoothMoveMarker mSmoothMarker; // 点平滑图标

    private TextView mMessageContent; // 定位信息文本
    private ImageButton mPlayBtn, mMapTypeBtn, mInformationBtn, mZoomInBtn, mZoomOutBtn,
            mLocusBtn, mShowMarker; // 播放,
    private TextView mWeatherContent; //天气信息文本
    private SeekBar mProgressSeekBar, mSpeedSeekBar; // 播放，速度进度条
    private List<AAATrackModel> mItemList = new ArrayList<>(); // 轨迹列表
    private List<LatLng> mLatLngItemList = new ArrayList<>(); // 轨迹列表
    private SparseArray<Marker> mMarkerMap = new SparseArray<>(); // 特殊点标记
    private List<Marker> mMarkerList = new ArrayList<>(); // 点标记列表
    private Timer mTimer; // 计时器
    private String mImeiNo;
    private String mStartTime;
    private String mEndTime;
    private BitmapDescriptor bitmapDes;
    private BitmapDescriptor supplementBitmapDes;
    private Polyline mPolyline;
    private final List<LatLng> latLngs = new ArrayList<>();
    private ImageView mLocus;
    private AAAUserModel mUserModel;

    private final SparseArray<PointRecode> sparseArray = new SparseArray<>();
    private final int POLYLINE_WIDTH = FinalMapUtils.POLYLINE_WIDTH;
    private final int MARKER_SIZE = FinalMapUtils.MARKER_SIZE;
    private int infoWindowStatus = 0;
    private final int REQUEST_ROWS = 100;
    private long stampTimeWhenClickSwitchShowMarker = 0;

    private static class PointRecode {
        private LatLng startLatLng;
        private LatLng endLatLng;
        private int supplementFlag;

        public LatLng getStartLatLng() {
            return startLatLng;
        }

        public LatLng getEndLatLng() {
            return endLatLng;
        }

        public int getSupplementFlag() {
            return supplementFlag;
        }

        public PointRecode startLatLng(LatLng latLng) {
            this.startLatLng = latLng;
            return this;
        }

        public PointRecode endLatLng(LatLng latLng) {
            this.endLatLng = latLng;
            return this;
        }

        public PointRecode supplementFlag(int supplementFlag) {
            this.supplementFlag = supplementFlag;
            return this;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        String title = bundle.getString(TConstant.TITLE);
        if (title == null)
            title = getString(R.string.track_playback);
        initToolBar(title, R.drawable.ic_back_white, mNavigationOnClickListener);
        initViews();
        initDatas();
        initMap();
        initListeners();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_history_map_google;
    }

    protected void initViews() {
        mPlayBtn = findViewById(R.id.history_google_play_btn);
        mProgressSeekBar = findViewById(R.id.history_google_progress_seekbar);
        mSpeedSeekBar = findViewById(R.id.history_google_speed_seekbar);
        mMapTypeBtn = findViewById(R.id.history_google_map_type_btn);
        mInformationBtn = findViewById(R.id.iv_information);
        mZoomInBtn = findViewById(R.id.history_google_zoom_in_btn);
        mZoomOutBtn = findViewById(R.id.history_google_zoom_out_btn);
        mLocus = findViewById(R.id.iv_locus);
        mLocusBtn = findViewById(R.id.tracking_google_locus_btn);
        mShowMarker = findViewById(R.id.tracking_show_marker);
        mMessageContent = findViewById(R.id.tracking_google_message_content);
        mWeatherContent = findViewById(R.id.tracking_google_weather_content);
    }

    protected void initDatas() {
        mShowMarker.setSelected(true);
        mUserModel = getTrackUserModel();
        mProgressSeekBar.setEnabled(false);
        mSpeedSeekBar.setEnabled(false);
        showDialog();
        mLoadingDialog.setMessage(getString(R.string.loading_tips));
        Bundle bundle = super.getIntent().getExtras();
        if (bundle != null) {
            mImeiNo = bundle.getString(TConstant.IMEI_NO);
            mStartTime = bundle.getString(TConstant.START_TIME);
            mEndTime = bundle.getString(TConstant.END_TIME);
            if (bundle.getInt("needScreenshot") != 1)
                mLocus.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(mImeiNo) && !TextUtils.isEmpty(mStartTime) && !TextUtils.isEmpty(mEndTime))
                getHistoryLocation(mImeiNo, mStartTime, mEndTime, 0);
        }
    }

    protected void initListeners() {
        mPlayBtn.setOnClickListener(this);
        mMapTypeBtn.setOnClickListener(this);
        mInformationBtn.setOnClickListener(this);
        mZoomInBtn.setOnClickListener(this);
        mLocus.setOnClickListener(this);
        mZoomOutBtn.setOnClickListener(this);
        mLocusBtn.setOnClickListener(this);
        mShowMarker.setOnClickListener(this);
        mProgressSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                refreshSmoothMarker();
                if (mPolyline != null && mProgressSeekBar.getProgress() != 0) {
                    mPolyline.setPoints(mLatLngItemList.subList(0,
                            mProgressSeekBar.getProgress() + 1));
                } else if (mPolyline != null && mProgressSeekBar.getProgress() == 0) {
                    mPolyline.setPoints(mLatLngItemList);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    private void initTrack() {
        LatLng lastLanLng = null;
        for (int i = 0; i < mItemList.size(); i++) {
            AAATrackModel trackModel = mItemList.get(i);
//            if (i % 20 > 10)
//                trackModel.setSupplement(1);
            LatLng desLatLng;
            if (trackModel.getLat() != null && trackModel.getLng() != null)
                desLatLng = convertLatLng(new LatLng(trackModel.getLat(), trackModel.getLng()));
            else
                desLatLng = new LatLng(0, 0);
            if (lastLanLng != null) {
                sparseArray.append(i,
                        new PointRecode().startLatLng(lastLanLng).endLatLng(desLatLng).supplementFlag(mItemList.get(i).getSupplement()));
            }
            lastLanLng = desLatLng;
            mLatLngItemList.add(desLatLng);
        }
        mProgressSeekBar.setMax(mLatLngItemList.size() - 1);
        mSpeedSeekBar.setMax(1800);
        dismisDialog();
        mProgressSeekBar.setEnabled(true);
        mSpeedSeekBar.setEnabled(true);
        mProgressSeekBar.setProgress(0);
        mSpeedSeekBar.setProgress(900);
        refreshOverlay(mLatLngItemList.size() - 1, mLatLngItemList.get
                (mLatLngItemList.size() - 1), 1);
        refreshOverlay(0, mLatLngItemList.get(0), 0);
        if (mMap != null) {
            for (int i = 0; i < mMarkerMap.size(); i++) {
                int key = mMarkerMap.keyAt(i); // get the object by the key.
                Marker marker = mMarkerMap.get(key);
                if (marker == null) {
                    if (key == 0)
                        refreshOverlay(0, mLatLngItemList.get(0), 0);
                    else if (key == mLatLngItemList.size() - 1)
                        refreshOverlay(key, mLatLngItemList.get(key), 1);
                    else {
                        refreshOverlay(key, mLatLngItemList.get(key), 2);
                    }
                }
            }
            refreshLine();
            refreshSmoothMarker();
        }
        if (mMap != null) {
            mMap.addPolyline(new PolylineOptions().add(mLatLngItemList.get(0))
                    .add(mLatLngItemList.get(mLatLngItemList.size() - 1)).width(POLYLINE_WIDTH).zIndex(1));
        }
    }

    private void getHistoryLocation(String deviceImei, String startTime, String endTime,
                                    long nextId) {
        CarGpsRequestUtils.getHistoryLocation(mUserModel, deviceImei, startTime, endTime, nextId,
                REQUEST_ROWS, mHandler);
    }

    /**
     * 初始化AMap对象
     */
    private void initMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.history_google_map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
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
     * 查找路线
     */
    public void onLocus(View v) {
        ActionSheet.createBuilder(this, getSupportFragmentManager())
                .setCancelButtonTitle(getString(R.string.cancel))
                .setOtherButtonTitles(getString(R.string.gaode_map), getString(R.string
                        .baidu_map), getString(R.string.tencent_map), getString(R.string
                        .google_map))
                .setCancelableOnTouchOutside(true)
                .setListener(this).show();
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
     * 播放
     */
    public void onPlay(View v) {
        if (mMap == null)
            return;
        if (mProgressSeekBar.isEnabled() && mItemList.size() > 1) {
            if (v.isSelected()) {
                mTimer.cancel();
                v.setSelected(false);
            } else {
                latLngs.clear();
                for (int i = 0; i < mProgressSeekBar.getProgress(); i++) {
                    latLngs.add(mLatLngItemList.get(i));
                }
                mPolyline.setPoints(latLngs);
                v.setSelected(true);
                refreshSmoothMarker();
                mTimer = new Timer();
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        mHandler.sendEmptyMessage(1);
                    }
                }, 2000 - mSpeedSeekBar.getProgress(), 2000 - mSpeedSeekBar.getProgress());
            }
        }
    }

    /**
     * 刷新设备图层
     *
     * @param index  位置
     * @param latLng 坐标
     * @param type   类型
     */
    private void refreshOverlay(int index, LatLng latLng, int type) {
        if (mMap == null)
            mMarkerMap.put(index, null);
        else {
            AAATrackModel trackModel = mItemList.get(index);
            MarkerOptions option = new MarkerOptions().position(latLng)
                    .draggable(false).title("").snippet("");
            option.icon(BitmapDescriptorFactory.fromBitmap(NewMapUtils.getDrivingMarkerIcon
                    (getResources(), type)));
            // 将Marker设置为贴地显示，可以双指下拉地图查看效果
            option.flat(true);//设置marker平贴地图效果
            Marker marker = mMap.addMarker(option);
            trackModel.settId(index + 1);
            marker.setTag(trackModel);
            mMarkerMap.put(index, marker);
            if (index == mItemList.size() - 1) {
                mSearchMarker = marker;
            }
        }
    }

    /**
     * 刷新设备图层
     */
    private void refreshSmoothMarker() {
        if (mMap != null) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                    R.mipmap.ic_default_pigeon_marker);
            bitmap = TransformImageAppearance.getDefaultMethodForBitmap(bitmap);
            if (mItemList.size() == 1) {
                LatLng latLng = mLatLngItemList.get(0);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//                if (mMarkerMap.get(0) != null)
//                    mMarkerMap.get(0).showInfoWindow();
            } else if (mProgressSeekBar
                    .getProgress() == mProgressSeekBar.getMax()) {
                if (mTimer != null)
                    mTimer.cancel();
                if (mMarker != null)
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(mLatLngItemList.get(mProgressSeekBar.getProgress())));
//                if (mMarkerMap.get(mProgressSeekBar
//                        .getProgress()) != null)
//                    mMarkerMap.get(mProgressSeekBar
//                            .getProgress()).showInfoWindow();
                if (mPlayBtn.isSelected()) {
                    onPlay(mPlayBtn);
                    mProgressSeekBar.setProgress(0);
                }
            } else {
                LatLng latLng = mLatLngItemList.get(mProgressSeekBar.getProgress());
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                if (mMarkerMap.get(mProgressSeekBar.getProgress()) == null) {
                    AAATrackModel trackModel = mItemList.get(mProgressSeekBar.getProgress());
                    if (mMarker == null) {
                        MarkerOptions option = new MarkerOptions().position(latLng)
                                .draggable(false).anchor(0.5f, 0.5f)
                                .title("").snippet("")
                                .icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                        // 将Marker设置为贴地显示，可以双指下拉地图查看效果
                        option.flat(true);//设置marker平贴地图效果
                        mMarker = mMap.addMarker(option);
                    } else {
//                        mMarker.setPosition(latLng);
                        FinalMapUtils.animateMarker(mMap, mMarker, latLng,
                                2000 - mSpeedSeekBar.getProgress());
                    }
                    mMarker.setTag(trackModel);
                    mMarker.setVisible(true);
//                    mMarker.showInfoWindow();
                } else {
                    if (mMarker != null)
                        mMarker.setVisible(false);
//                    mMarkerMap.get(mProgressSeekBar
//                            .getProgress()).showInfoWindow();
                }
            }
        }
    }

    /**
     * 初始化轨迹中途点marker的bitmapDescriptor
     */
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

    /**
     * 显示路线
     */
    private void refreshLine() {
        if (mLatLngItemList.size() > 1 && mMap != null) {
            List<LatLng> latLngs = new ArrayList<>();
            LatLngBounds.Builder latLngBoundsBuilder = new LatLngBounds.Builder();
            for (int i = 0; i < mLatLngItemList.size(); i++) {
                LatLng desLatLng = mLatLngItemList.get(i);
                latLngs.add(desLatLng);
                latLngBoundsBuilder.include(desLatLng);
                Marker marker;
                if (mItemList.get(i).getSupplement() != 1)
                    marker =
                            mMap.addMarker(new MarkerOptions().icon(bitmapDes).position(desLatLng).anchor(0.5f, 0.5f));
                else
                    marker =
                            mMap.addMarker(new MarkerOptions().icon(supplementBitmapDes).position(desLatLng).anchor(0.5f, 0.5f));
                mItemList.get(i).settId(i + 1);
                marker.setTag(mItemList.get(i));
                mMarkerList.add(marker);
                if (!mShowMarker.isSelected()) {
                    marker.setVisible(false);
                }
            }

            mPolyline = mMap.addPolyline(new PolylineOptions().addAll(latLngs)
                    .jointType(JointType.ROUND)
                    .width(POLYLINE_WIDTH)
                    .color(Color.argb(255, 0, 120, 0)));
            for (int i = 1; i <= sparseArray.size(); i++) {
                if (sparseArray.get(i).getSupplementFlag() == 1)
                    mMap.addPolyline(new PolylineOptions().add(sparseArray.get(i).getStartLatLng()
                            , sparseArray.get(i).getEndLatLng()).color(Color.rgb(200, 0, 0)));
                else
                    mMap.addPolyline(new PolylineOptions().add(sparseArray.get(i).getStartLatLng()
                            , sparseArray.get(i).getEndLatLng()).color(Color.rgb(0, 200, 0)));
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBoundsBuilder.build(),
                    getResources().getDimensionPixelOffset(R.dimen.margin_24)));
        }
    }

    private void showAllDeviceMarker() {
        for (Marker marker : mMarkerList) {
            marker.setVisible(true);
        }
    }

    private void hideAllDeviceMarker() {
        for (Marker marker : mMarkerList) {
            marker.setVisible(false);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setIndoorEnabled(false);
        UiSettings mUiSettings = mMap.getUiSettings();
        mUiSettings.setMyLocationButtonEnabled(false); // 是否显示默认的定位按钮
        mUiSettings.setTiltGesturesEnabled(true);// 设置地图是否可以倾斜
        mUiSettings.setZoomControlsEnabled(false);
        mUiSettings.setIndoorLevelPickerEnabled(false); // 禁用楼层选择器
        mUiSettings.setMapToolbarEnabled(false);
        mMap.setOnInfoWindowClickListener(mInfoWindowClickListener);
        mMap.setInfoWindowAdapter(mInfoWindowAdapter);
        mMap.setOnMarkerClickListener(mOnMarkerClickListener);
        initBitmapDes();
        if (mProgressSeekBar.isEnabled()) {
            for (int i = 0; i < mMarkerMap.size(); i++) {
                int key = mMarkerMap.keyAt(i); // get the object by the key.
                Marker marker = mMarkerMap.get(key);
                if (marker == null) {
                    if (key == 0)
                        refreshOverlay(0, mLatLngItemList.get(0), 0);
                    else if (key == mLatLngItemList.size() - 1)
                        refreshOverlay(key, mLatLngItemList.get(key), 1);
                    else {
                        refreshOverlay(key, mLatLngItemList.get(key), 2);
                    }
                }
            }
            refreshSmoothMarker();
            refreshLine();
        }
    }

    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        if (mSearchMarker == null)
            return;
        LatLng latLng = convertLatLng(mSearchMarker.getPosition());
        switch (index) {
            case 0:
                MapUtils.naviMap(this, latLng.latitude, latLng.longitude, 3);
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
                MapUtils.naviMap(this, latLng.latitude, latLng.longitude, 0);
                break;
            case 2:
                MapUtils.naviMap(this, latLng.latitude, latLng.longitude, 1);
                break;
        }
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.history_google_play_btn: // 播放、暂停
                if (mMap != null)
                    onPlay(v);
                break;
            case R.id.iv_information: {
                if (mItemList.size() == 0)
                    return;
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                String distance = getString(R.string.tracking_distance,
                        AAAStringUtils.getMapDistance(
                                (long) AMapUtils.calculateLineDistance(
                                        new com.amap.api.maps.model.LatLng(mLatLngItemList.get(0).latitude, mLatLngItemList.get(0).longitude)
                                        ,
                                        new com.amap.api.maps.model.LatLng(mLatLngItemList.get(mLatLngItemList.size() - 1).latitude, mLatLngItemList.get(mLatLngItemList.size() - 1).longitude))));
                String startTime = mItemList.get(0).getGpsTime();
                String endTime = mItemList.get(mItemList.size() - 1).getGpsTime();
                String locationPointCount = String.valueOf(mLatLngItemList.size());
                String period = null;
                try {
                    long sub =
                            Objects.requireNonNull(simpleDateFormat.parse(endTime)).getTime() - Objects.requireNonNull(simpleDateFormat.parse(startTime)).getTime();
                    long second = sub / (1000) % (60);
                    long minute = sub / (1000 * 60) % 60;
                    long hour = sub / (1000 * 60 * 60) % 24;
                    long day = sub / (1000 * 60 * 60 * 24);
                    period = String.format("%d%s%d%s%d%s%d%s", day, getString(R.string.day_new),
                            hour, getString(R.string.hour_new), minute,
                            getString(R.string.minute_new), second, getString(R.string.second_new));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                builder.setTitle(R.string.track_info_prompt)
                        .setMessage(
                                getString(R.string.track_information, startTime, endTime,
                                        distance, locationPointCount, period))
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).show();
            }
            break;
            case R.id.history_google_map_type_btn: // 地图类型
                onMapType(v);
                break;
            case R.id.history_google_zoom_in_btn: // 地图放大
                onZoomIn(v);
                break;
            case R.id.history_google_zoom_out_btn: // 地图缩小
                onZoomOut(v);
                break;
            case R.id.iv_locus: {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.prompt)
                        .setMessage(R.string.whether_capture_the_map_and_share_it_prompt)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                getScreenshotAndShare(v);
                            }
                        }).setNegativeButton(R.string.cancel,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                }).show();
            }
            break;
            case R.id.tracking_google_locus_btn:
                ActionSheet.createBuilder(this, getSupportFragmentManager())
                        .setCancelButtonTitle(getString(R.string.cancel))
                        .setOtherButtonTitles(getString(R.string.google_map))
                        .setCancelableOnTouchOutside(true)
                        .setListener(this).show();
                break;
            case R.id.tracking_show_marker:
                if (preventButtonFastReClick(stampTimeWhenClickSwitchShowMarker)) {
                    stampTimeWhenClickSwitchShowMarker = System.currentTimeMillis();
                    mShowMarker.setSelected(!mShowMarker.isSelected());
                    if (mShowMarker.isSelected()) {
                        showAllDeviceMarker();
                    } else {
                        hideAllDeviceMarker();
                    }
                }
                break;
            default:
                break;
        }
    }

    private void getScreenshotAndShare(View view) {
        long apartDistance = (long) AMapUtils.calculateLineDistance(
                convertGoogleMapLatLngToAMapLatLng(mLatLngItemList.get(0))
                ,
                convertGoogleMapLatLngToAMapLatLng(mLatLngItemList.get(mLatLngItemList.size() - 1)));
        mMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap bitmap) {
                bitmap = BitmapBlobUtils.getRoundedCornerBitmap(bitmap, 30f);
                Bundle bundle = new Bundle();
                bundle.putString(TConstant.START_TIME, mItemList.get(0).getGpsTime());
                bundle.putString(TConstant.END_TIME,
                        mItemList.get(mItemList.size() - 1).getGpsTime());
                bundle.putLong("distance", apartDistance);
                bundle.putByteArray("image", BitmapBlobUtils.Bitmap2Bytes(bitmap));
                openNewPage(ShareToTrackCircleFragment.class, bundle);
//                ImageView imageView = new ImageView(HistoryMapGoogleActivity.this);
//                imageView.setImageBitmap(bitmap);
//                AlertDialog.Builder builder = new AlertDialog.Builder(HistoryMapGoogleActivity
//                .this);
//                Bitmap finalBitmap = bitmap;
//                builder.setView(imageView)
//                        .setPositiveButton(R.string.share, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                Bundle bundle = new Bundle();
////                                bundle.putString(TConstant.START_TIME,mStartTime);
////                                bundle.putString(TConstant.END_TIME,mEndTime);
//                                bundle.putString(TConstant.START_TIME, mItemList.get(0)
//                                .getGpsTime());
//                                bundle.putString(TConstant.END_TIME, mItemList.get(mItemList
//                                .size() - 1).getGpsTime());
//                                bundle.putLong("distance", apartDistance);
//                                bundle.putByteArray("image", BitmapBlobUtils.Bitmap2Bytes
//                                (finalBitmap));
//                                openNewPage(ShareToTrackCircleFragment.class, bundle);
//                            }
//                        }).setNegativeButton(R.string.cancel, new DialogInterface
//                        .OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                    }
//                }).show();
            }
        });
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

    private final GoogleMap.OnMarkerClickListener mOnMarkerClickListener =
            new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    infoWindowStatus = 0;
                    mSearchMarker = marker;
                    marker.showInfoWindow();
                    AAATrackModel trackModel = (AAATrackModel) marker.getTag();
                    if (trackModel != null) {
                        mWeatherContent.setText(trackModel.getWeather());
                        if (!TextUtils.isEmpty(trackModel.getPositionDesc()))
                            mMessageContent.setText(trackModel.getPositionDesc());
                        else
                            mMessageContent.setText(new StringBuilder(getString(R.string.longitude)).append(":").append(trackModel.getLng()).append("  ")
                                    .append(getString(R.string.latitude)).append(":").append(trackModel.getLat()));
                    }
                    return false;
                }
            };

    /**
     * 地图信息弹窗适配器
     */
    private final GoogleMap.InfoWindowAdapter mInfoWindowAdapter =
            new GoogleMap.InfoWindowAdapter() {

                @Override
                public View getInfoWindow(Marker marker) {
                    // TODO Auto-generated method stub
                    @SuppressLint("InflateParams")
                    View view = LayoutInflater.from(HistoryGMapActivity.this).inflate(R.layout
                            .amap_info_window_layout, null);
                    TextView mAddressText = view.findViewById(R.id.amap_info_window_address);
                    if (marker.getTag() == null) {
                        mAddressText.setText("");
                        return view;
                    }
                    AAATrackModel trackModel = (AAATrackModel) marker.getTag();
                    AAADeviceModel deviceModel = FinalMapUtils.transformTrackToDevice(trackModel);
                    if (infoWindowStatus == 0) {
                        mAddressText.setText(FinalMapUtils.concatInfoWindowCollapsedContent(deviceModel, 0));
                    } else {
                        mAddressText.setText(FinalMapUtils.concatInfoWindowContent(deviceModel, 0));
                    }
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
            if (infoWindowStatus == 0) {
                infoWindowStatus += 1;
                mark.showInfoWindow();
            } else {
                infoWindowStatus = 0;
                mark.hideInfoWindow();
            }
        }
    };

    /**
     * 消息处理
     */
    private final Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            try {
                AAABaseResponseBean responseBean;
                switch (msg.what) {
                    case 1:  // 播放轨迹
                        if (mPlayBtn.isSelected() && mProgressSeekBar.getProgress() <
                                mProgressSeekBar.getMax())
                            mProgressSeekBar.setProgress(mProgressSeekBar.getProgress() + 1);
                        else if (mTimer != null)
                            mTimer.cancel();
                        break;
                    case TConstant.REQUEST_URL_GET_HISTORY_LOCATION:
                        if (msg.obj == null) {
                            showMessage(R.string.request_unkonow_prompt);
                            dismisDialog();
                        } else {
                            responseBean = (AAABaseResponseBean) msg.obj;
                            if (responseBean.getCode() == TConstant.RESPONSE_NET_ERROR) {
                                showMessage(R.string.request_unkonow_prompt);
                                dismisDialog();
                            } else if (responseBean.getCode() == TConstant.RESPONSE_SUCCESS) {
                                ListResponseBean listResponseBean =
                                        mGson.fromJson(mGson.toJson(responseBean.getData()),
                                                ListResponseBean.class);
                                List list;
                                if (listResponseBean == null)
                                    list = null;
                                else
                                    list = listResponseBean.getList();
                                if (list == null || list.size() == 0) {
                                    dismisDialog();
                                    initTrack();
                                } else {
                                    for (Object object : list) {
                                        AAATrackModel trackModel =
                                                mGson.fromJson(mGson.toJson(object),
                                                        AAATrackModel.class);
                                        mItemList.add(trackModel);
                                    }
                                    if (list.size() != REQUEST_ROWS ||
                                            mItemList.get(mItemList.size() - 1).getLogId() == null) {
                                        dismisDialog();
                                        initTrack();
                                    } else
                                        getHistoryLocation(mImeiNo, mStartTime, mEndTime,
                                                mItemList.get(mItemList.size() - 1).getLogId());
                                }
                            } else {
                                showMessage(R.string.tip_request_error);
                                dismisDialog();
                            }
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

    private LatLng convertLatLng(LatLng latLng) {
        CoordinateConverter converter = new CoordinateConverter(this);
        converter.from(CoordinateConverter.CoordType.GPS);
        converter.coord(new com.amap.api.maps.model.LatLng(latLng.latitude,
                latLng.longitude));
        com.amap.api.maps.model.LatLng aMapLatlng = converter.convert();
        return new LatLng(aMapLatlng.latitude, aMapLatlng.longitude);
    }

    @Override
    public void onDestroy() {
        if (mTimer != null)
            mTimer.cancel();
        mMap = null;
        super.onDestroy();
    }

    private com.amap.api.maps.model.LatLng convertGoogleMapLatLngToAMapLatLng(LatLng latLng) {
        return new com.amap.api.maps.model.LatLng(latLng.latitude, latLng.longitude);
    }
}
