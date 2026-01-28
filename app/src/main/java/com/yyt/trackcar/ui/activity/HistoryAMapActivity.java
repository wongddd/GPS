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
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

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
import com.amap.api.maps.utils.overlay.SmoothMoveMarker;
import com.baoyz.actionsheet.ActionSheet;
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
import java.util.Locale;
import java.util.Objects;

/**
 * 项目名：   传信鸽
 * 包名：     com.yyt.trackcar.ui.activity
 * 文件名：   HistoryMapActivity
 * 创建者：   QING
 * 创建时间： 2018/4/26 18:00
 * 描述：     TODO 历史轨迹显示页面
 */

public class HistoryAMapActivity extends BaseActivity implements OnClickListener,
        ActionSheet.ActionSheetListener {
    private static final String TAG = "HistoryMapActivity";
    // 地图
    private AMap mAMap;
    private MapView mMapView;
    private SmoothMoveMarker mSmoothMarker; // 点平滑图标
    // UI相关
    private UiSettings mUiSettings;
    private Marker mSearchMarker;


    private TextView mMessageContent; // 定位信息文本
    private ImageButton mInformationBtn, mPlayBtn, mMapTypeBtn, mZoomInBtn, mZoomOutBtn,
            mLocusBtn, mShowMarker; // 播放,
    // 切换地图类型，导航，地图放大，地图缩小, 导航按钮
    private SeekBar mProgressSeekBar, mSpeedSeekBar; // 播放，速度进度条
    private TextView mWeatherContent; //天气信息文本

    private List<AAATrackModel> mItemList = new ArrayList<>(); // 轨迹列表
    private List<LatLng> mLatLngItemList = new ArrayList<>(); // 轨迹列表
    private SparseArray<Marker> mMarkerMap = new SparseArray<>(); // 特殊点标记
    private List<Marker> mMarkerList = new ArrayList<>(); // 点标记列表
    private String mImeiNo;
    private String mStartTime;
    private String mEndTime;
    //private Timer mTimer; // 计时器
    private ImageView mScreenshotBtn;

    private final static int GREEN = Color.argb(255, 0, 200, 0);
    private final static int RED = Color.argb(255, 200, 0, 0);
    private BitmapDescriptor bitmapDes; //中途标记点
    private BitmapDescriptor supplementBitmapDes; //补传点标记
    private BitmapDescriptor texture; //轨迹线样式
    private Polyline mPolyline;
    private final List<LatLng> latLngs = new ArrayList<>();
    private int mDeviceType; //设备类型  1、车载定位器  2、鸽子定位器 3、学生卡
    private final SparseArray<PointRecode> sparseArray = new SparseArray<>();
    private final int POLYLINE_WIDTH = FinalMapUtils.POLYLINE_WIDTH;
    private final int MARKER_SIZE = FinalMapUtils.MARKER_SIZE;
    private final int REQUEST_ROWS = 100;
    private long stampTimeWhenClickSwitchShowMarker = 0;

    public static class PointRecode {
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
        initTexture();
        initMap(savedInstanceState);
        initDatas();
        initListeners();
        initBitmapDes();
    }


    private void initTexture() {
        ImageView imageView =
                (ImageView) LayoutInflater.from(this).inflate(R.layout.amap_custom_texture, null);
        imageView.setImageResource(R.mipmap.arrow_up_new);
        imageView.setColorFilter(Color.rgb(0, 80, 0));
        texture = BitmapDescriptorFactory.fromView(imageView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_history_map;
    }

    protected void initViews() {
        mMapView = findViewById(R.id.history_map);
        mPlayBtn = findViewById(R.id.history_play_btn);
        mProgressSeekBar = findViewById(R.id.history_progress_seekbar);
        mSpeedSeekBar = findViewById(R.id.history_speed_seekbar);
        mMapTypeBtn = findViewById(R.id.history_map_type_btn);
        mZoomInBtn = findViewById(R.id.history_zoom_in_btn);
        mZoomOutBtn = findViewById(R.id.history_zoom_out_btn);
        mInformationBtn = findViewById(R.id.iv_information);
        mScreenshotBtn = findViewById(R.id.iv_locus);
        mLocusBtn = findViewById(R.id.tracking_locus_btn);
        mShowMarker = findViewById(R.id.tracking_show_marker);
        mMessageContent = findViewById(R.id.tracking_message_content);
        mWeatherContent = findViewById(R.id.tracking_weather_content);
    }


    protected void initDatas() {
        mShowMarker.setSelected(true);
        mProgressSeekBar.setEnabled(false);
        mSpeedSeekBar.setEnabled(false);
        showDialog();
        mLoadingDialog.setMessage(getString(R.string.loading_tips));
        mSmoothMarker = new SmoothMoveMarker(mAMap);
        Bundle bundle = super.getIntent().getExtras();
        int imgRes = R.mipmap.ic_default_pigeon_marker;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imgRes);
        bitmap = TransformImageAppearance.getDefaultMethodForBitmap(bitmap);
        mSmoothMarker.setDescriptor(BitmapDescriptorFactory.fromBitmap(bitmap));
        if (bundle != null) {
            mImeiNo = bundle.getString(TConstant.IMEI_NO);
            mStartTime = bundle.getString(TConstant.START_TIME);
            mEndTime = bundle.getString(TConstant.END_TIME);
            mDeviceType = bundle.getInt(TConstant.DEVICE_TYPE);
            if (bundle.getInt("needScreenshot") != 1)
                mScreenshotBtn.setVisibility(View.GONE);

            mSmoothMarker.setDescriptor(BitmapDescriptorFactory.fromBitmap(bitmap));
            if (!TextUtils.isEmpty(mImeiNo) && !TextUtils.isEmpty(mStartTime) && !TextUtils.isEmpty(mEndTime))
                getHistoryLocation(mImeiNo, mStartTime, mEndTime, 0);

        } else {
            // 设置滑动的图标
            mSmoothMarker.setDescriptor(BitmapDescriptorFactory.fromBitmap(TransformImageAppearance.getDefaultMethodForBitmap(bitmap)));//R.mipmap.ic_marker_car
        }
    }

    private interface OnSmoothMarkerImageLoad {
        void onSmoothMarkerImageLoadFinished(Bitmap bitmap);
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


    protected void initListeners() {
        //mAMap.setInfoWindowAdapter(mInfoWindowAdapter);
        mPlayBtn.setOnClickListener(this);
        mMapTypeBtn.setOnClickListener(this);
        mZoomInBtn.setOnClickListener(this);
        mZoomOutBtn.setOnClickListener(this);
        mInformationBtn.setOnClickListener(this);
        mScreenshotBtn.setOnClickListener(this);
        mLocusBtn.setOnClickListener(this);
        mShowMarker.setOnClickListener(this);
        mAMap.setOnMarkerClickListener(onMarkerClickListener);
        mAMap.setOnInfoWindowClickListener(mInfoWindowClickListener);
        mAMap.setInfoWindowAdapter(mInfoWindowAdapter);
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
        mSmoothMarker.setMoveListener(new SmoothMoveMarker.MoveListener() {
            @Override
            public void move(double v) {
                if (mSmoothMarker.getIndex() == 0 && mProgressSeekBar.getProgress() <
                        mProgressSeekBar.getMax()) {
                    mProgressSeekBar.setProgress(mProgressSeekBar.getProgress() + 1);
                }
            }
        });
    }


    private final AMap.OnMarkerClickListener onMarkerClickListener =
            new AMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    marker.showInfoWindow();
                    mSearchMarker = marker;
                    AAATrackModel trackModel = (AAATrackModel) marker.getObject();
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


    private void initTrack() {
        LatLng lastLanLng = null;
        for (int i = 0; i < mItemList.size(); i++) {
            AAATrackModel trackModel = mItemList.get(i);
            LatLng desLatLng;
            if (trackModel.getLat() != null && trackModel.getLng() != null) {
                desLatLng = convertLanLng(new LatLng(trackModel.getLat(), trackModel.getLng()));
            } else
                desLatLng = new LatLng(0, 0);
            if (lastLanLng != null) {
                sparseArray.append(i,
                        new PointRecode().startLatLng(lastLanLng).endLatLng(desLatLng).supplementFlag(mItemList.get(i).getSupplement()));
            }
            lastLanLng = desLatLng;
            Marker marker;
            if (trackModel.getSupplement() != 1)
                marker = mAMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                        .position(desLatLng)
                        .icon(bitmapDes));
            else
                marker = mAMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                        .position(desLatLng)
                        .icon(supplementBitmapDes));
            mMarkerList.add(marker);
            if(!mShowMarker.isSelected()){
                marker.setVisible(false);
            }
            mItemList.get(i).settId(i + 1);
            marker.setObject(mItemList.get(i));
            mLatLngItemList.add(desLatLng);
            if (i == mItemList.size() - 1) {
                mSearchMarker = marker;
            }
        }
        mProgressSeekBar.setMax(mLatLngItemList.size() - 1);
        mSpeedSeekBar.setMax(2);

        dismisDialog();
        mProgressSeekBar.setEnabled(true);
        mSpeedSeekBar.setEnabled(true);
        refreshOverlay(mLatLngItemList.size() - 1, mLatLngItemList.get
                (mLatLngItemList.size() - 1), 1);
        refreshOverlay(0, mLatLngItemList.get(0), 0);
        mProgressSeekBar.setProgress(0);
        mSpeedSeekBar.setProgress(1);
        mSmoothMarker.setVisible(true);
        //refreshSmoothMarker();
        refreshLine();
    }

    private void getHistoryLocation(String deviceImei, String startTime, String endTime,
                                    long nextId) {
        AAAUserModel userModel = getTrackUserModel();
        CarGpsRequestUtils.getHistoryLocation(userModel, deviceImei, startTime, endTime, nextId,
                REQUEST_ROWS, mHandler);
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
     * 播放
     */
    public void onPlay(View v) {
        if (mItemList.size() > 1) {
            if (mProgressSeekBar.isEnabled()) {
                if (v.isSelected()) {
                    mSmoothMarker.stopMove();
                    v.setSelected(false);
                } else {
                    latLngs.clear();
                    for (int i = 0; i <= mProgressSeekBar.getProgress(); i++) {
                        latLngs.add(mLatLngItemList.get(i));
                    }
                    mPolyline.setPoints(latLngs);
                    v.setSelected(true);
                    refreshSmoothMarker();
                }
            }
        }
    }


    private void getScreenshotAndShare(View view) {
//        showLatLngBounds();
        long apartDistance = (long) AMapUtils.calculateLineDistance(mLatLngItemList.get(0),
                mLatLngItemList.get(mLatLngItemList.size() - 1));
        mAMap.getMapScreenShot(new AMap.OnMapScreenShotListener() {
            @Override
            public void onMapScreenShot(Bitmap bitmap) {
                bitmap = BitmapBlobUtils.getRoundedCornerBitmap(bitmap, 30f);
                Bundle bundle = new Bundle();
                bundle.putString(TConstant.START_TIME, mItemList.get(0).getGpsTime());
                bundle.putString(TConstant.END_TIME,
                        mItemList.get(mItemList.size() - 1).getGpsTime());
                bundle.putLong("distance", apartDistance);
                bundle.putByteArray("image", BitmapBlobUtils.Bitmap2Bytes(bitmap));
                openNewPage(ShareToTrackCircleFragment.class, bundle);
//                ImageView imageView = new ImageView(HistoryMapActivity.this);
//                imageView.setImageBitmap(bitmap);
//                AlertDialog.Builder builder = new AlertDialog.Builder(HistoryMapActivity.this);
//                Bitmap finalBitmap = bitmap;
//                builder.setTitle(R.string.prompt)
//                        .setView(imageView)
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

            @Override
            public void onMapScreenShot(Bitmap bitmap, int i) {

            }
        });


//        DisplayMetrics metrics = new DisplayMetrics();
//        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
//        mScreenDensity = metrics.densityDpi;
//        windowHeight = metrics.heightPixels;
//        windowWidth = metrics.widthPixels;

//        View v = this.getWindow().getDecorView();
//        v.setDrawingCacheEnabled(true);
//        v.buildDrawingCache();
//        Bitmap bitmap = v.getDrawingCache();

    }


    //显示所有的途经点
    private void showLatLngBounds() {
        if (mLatLngItemList.size() == 0)
            return;
        LatLngBounds.Builder latLngBoundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLng : mLatLngItemList) {
            latLngBoundsBuilder.include(latLng);
        }
        mAMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBoundsBuilder.build(),
                getResources().getDimensionPixelOffset(R.dimen.margin_24)));
    }


    /**
     * 刷新设备图层
     *
     * @param index  位置
     * @param latLng 坐标
     * @param type   类型
     */
    private void refreshOverlay(int index, LatLng latLng, int type) {
        AAATrackModel trackModel = mItemList.get(index);
        MarkerOptions option = new MarkerOptions().position(latLng)
                .draggable(false).title("").snippet("").setInfoWindowOffset(0, -2);
        option.icon(BitmapDescriptorFactory.fromBitmap(NewMapUtils.getDrivingMarkerIcon
                (getResources(), type)));
        // 将Marker设置为贴地显示，可以双指下拉地图查看效果
        option.setFlat(true);//设置marker平贴地图效果
        Marker marker = mAMap.addMarker(option);
        trackModel.settId(index + 1);
        marker.setObject(trackModel);
        mMarkerMap.put(index, marker);
//        if (type == 0)
//            marker.showInfoWindow();
    }

    /**
     * 刷新设备图层
     */
    private void refreshSmoothMarker() {
        if (mItemList.size() == 1) {
            mAMap.moveCamera(CameraUpdateFactory.newLatLng(mLatLngItemList.get(0)));
//            if (mMarkerMap.get(0) != null)
//                mMarkerMap.get(0).showInfoWindow();
        } else if (mProgressSeekBar
                .getProgress() == mProgressSeekBar.getMax()) {
            mSmoothMarker.stopMove();
            mSmoothMarker.setVisible(false);
            mAMap.moveCamera(CameraUpdateFactory.newLatLng(mLatLngItemList.get
                    (mProgressSeekBar
                            .getProgress())));
//            if (mMarkerMap.get(mProgressSeekBar
//                    .getProgress()) != null)
//                mMarkerMap.get(mProgressSeekBar
//                        .getProgress()).showInfoWindow();
            if (mPlayBtn.isSelected()) {
                onPlay(mPlayBtn);
                mProgressSeekBar.setProgress(0);
            }
        } else {
            mSmoothMarker.stopMove();
            List<LatLng> subList = new ArrayList<>();
            subList.add(mLatLngItemList.get(mProgressSeekBar
                    .getProgress()));
            subList.add(mLatLngItemList.get(mProgressSeekBar
                    .getProgress() + 1));
            // 设置滑动的轨迹左边点
            mSmoothMarker.setPoints(subList);
            if (mProgressSeekBar
                    .getProgress() != 0 && mMarkerMap.get
                    (mProgressSeekBar
                            .getProgress()) == null) {
                mSmoothMarker.setVisible(true);
//                mSmoothMarker.getMarker().showInfoWindow();
                if (mPlayBtn.isSelected()) { // 开始滑动
                    // 设置滑动的总时间
                    mSmoothMarker.setTotalDuration(mSpeedSeekBar.getProgress());
                    mSmoothMarker.startSmoothMove();
                }
            } else {
                mSmoothMarker.setVisible(false);
                mAMap.moveCamera(CameraUpdateFactory.newLatLng(mLatLngItemList.get
                        (mProgressSeekBar
                                .getProgress())));
//                if (mMarkerMap.get(mProgressSeekBar
//                        .getProgress()) != null)
//                    mMarkerMap.get(mProgressSeekBar
//                            .getProgress()).showInfoWindow();
                if (mPlayBtn.isSelected()) { // 开始滑动
                    final int progress = mProgressSeekBar.getProgress();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            HistoryAMapActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (mProgressSeekBar.getProgress() == progress && mPlayBtn
                                            .isSelected()) {
                                        mSmoothMarker.setVisible(true);
//                                        mSmoothMarker.getMarker().showInfoWindow();
                                        // 设置滑动的总时间
                                        mSmoothMarker.setTotalDuration(mSpeedSeekBar.getProgress());
                                        mSmoothMarker.startSmoothMove();
                                    }
                                }
                            });
                        }
                    }, mSpeedSeekBar.getProgress() * 1000L);
                }
            }
        }
    }

    /**
     * 显示路线
     */
    private void refreshLine() {
        if (mLatLngItemList.size() > 1) {
            mPolyline = mAMap.addPolyline(new PolylineOptions()
                    .addAll(mLatLngItemList)
                    .width(POLYLINE_WIDTH)
                    .color(GREEN)
                    .setCustomTexture(texture));
            for (int i = 1; i <= sparseArray.size(); i++) {
                if (sparseArray.get(i).getSupplementFlag() == 1)
                    mAMap.addPolyline(new PolylineOptions().add(sparseArray.get(i).getStartLatLng()
                            , sparseArray.get(i).getEndLatLng()).color(RED));
                else
                    mAMap.addPolyline(new PolylineOptions().add(sparseArray.get(i).getStartLatLng()
                            , sparseArray.get(i).getEndLatLng()).color(GREEN));
            }
            LatLngBounds.Builder latLngBoundsBuilder = new LatLngBounds.Builder();
            for (LatLng latLng : mLatLngItemList) {
                latLngBoundsBuilder.include(latLng);
            }
            mAMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBoundsBuilder.build(),
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

    @SuppressLint({"NonConstantResourceId", "DefaultLocale"})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.history_play_btn: // 播放、暂停
                onPlay(v);
                break;
            case R.id.history_map_type_btn: // 地图类型
                onMapType(v);
                break;
            case R.id.history_zoom_in_btn: // 地图放大
                onZoomIn(v);
                break;
            case R.id.history_zoom_out_btn: // 地图缩小
                onZoomOut(v);
                break;
            case R.id.iv_information:
                if (mItemList.size() == 0)
                    break;
                showInformationCard();
                break;
            case R.id.iv_locus:
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
                break;
            case R.id.tracking_locus_btn:
                ActionSheet.createBuilder(this, getSupportFragmentManager())
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

    //显示基础信息弹窗
    private void showInformationCard() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.CHINA);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String distance = getString(R.string.tracking_distance, AAAStringUtils.getMapDistance(
                (long) AMapUtils.calculateLineDistance(mLatLngItemList.get(0),
                        mLatLngItemList.get(mLatLngItemList.size() - 1))));
        String startTime = mItemList.get(0).getGpsTime();
        String endTime = mItemList.get(mItemList.size() - 1).getGpsTime();
        String locationPointCount = String.valueOf(mLatLngItemList.size());
        String period = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            long sub =
                    Objects.requireNonNull(simpleDateFormat.parse(endTime)).getTime() - Objects.requireNonNull(simpleDateFormat.parse(startTime)).getTime();
            long second = sub / (1000) % (60);
            long minute = sub / (1000 * 60) % 60;
            long hour = sub / (1000 * 60 * 60) % 24;
            long day = sub / (1000 * 60 * 60 * 24);
            period = stringBuilder.append(day).append(getString(R.string.day_new))
                    .append(hour).append(getString(R.string.hour_new))
                    .append(minute).append(getString(R.string.minute_new))
                    .append(second).append(getString(R.string.second_new))
                    .toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        builder.setTitle(R.string.track_info_prompt)
                .setMessage(
                        getString(R.string.track_information, startTime, endTime, distance,
                                locationPointCount, period))
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
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
     * 地图信息弹窗适配器
     */
    private final AMap.InfoWindowAdapter mInfoWindowAdapter = new AMap.InfoWindowAdapter() {

        @Override
        public View getInfoWindow(Marker marker) {
            // TODO Auto-generated method stub
            AAATrackModel trackModel;
            if (marker.getObject() == null) {
                trackModel = mItemList.get(mProgressSeekBar.getProgress());
            } else {
                trackModel = (AAATrackModel) marker.getObject();
            }
            AAADeviceModel deviceModel = FinalMapUtils.transformTrackToDevice(trackModel);
            return FinalMapUtils.createInfoContentView(HistoryAMapActivity.this, deviceModel, 0);
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
        public void onInfoWindowClick(Marker mark) {
            // TODO Auto-generated method stub
            mark.hideInfoWindow();
        }
    };

    private LatLng convertLanLng(LatLng latLng) {
        CoordinateConverter converter = new CoordinateConverter
                (HistoryAMapActivity.this);
        converter.from(CoordinateConverter.CoordType.GPS);
        converter.coord(latLng);
        return converter.convert();
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
                    case 1:  // 播放轨迹
                        if (mProgressSeekBar.getProgress() < mProgressSeekBar.getMax())
                            mProgressSeekBar.setProgress(mProgressSeekBar.getProgress() + 1);
                        else {
                            if (mPlayBtn.isSelected())
                                onPlay(mPlayBtn);
                            mProgressSeekBar.setProgress(0);
                        }
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
//                                List<AAATrackModel> list = mGson.fromJson(mGson.toJson
//                                (listResponseBean.getList()), new
//                                TypeToken<List<AAATrackModel>>(){}.getType());
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
        //        if (mTimer != null)
//            mTimer.cancel();
        // 关闭定位图层
        mAMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }


    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        if (mSearchMarker == null)
            return;
        LatLng latLng = convertLanLng(mSearchMarker.getPosition());
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
}
