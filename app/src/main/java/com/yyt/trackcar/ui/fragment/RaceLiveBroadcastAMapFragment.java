package com.yyt.trackcar.ui.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
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
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAABaseResponseBean;
import com.yyt.trackcar.bean.AAARequestBean;
import com.yyt.trackcar.bean.AAATrackModel;
import com.yyt.trackcar.bean.AMapMovementTrack;
import com.yyt.trackcar.bean.ListResponseBean;
import com.yyt.trackcar.dbflow.AAADeviceModel;
import com.yyt.trackcar.dbflow.AAAUserModel;
import com.yyt.trackcar.ui.adapter.GameLiveAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.BitmapBlobUtils;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.ErrorCode;
import com.yyt.trackcar.utils.FinalMapUtils;
import com.yyt.trackcar.utils.NewMapUtils;
import com.yyt.trackcar.utils.SettingSPUtils;
import com.yyt.trackcar.utils.TConstant;
import com.yyt.trackcar.utils.TransformImageAppearance;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;


@SuppressLint("NonConstantResourceId")
@Page(name = "RaceLiveBroadcastFragment", anim = CoreAnim.none)
public class RaceLiveBroadcastAMapFragment extends BaseFragment implements GeocodeSearch
        .OnGeocodeSearchListener, View.OnClickListener {
    // 地图
    private AMap mAMap;
    @BindView(R.id.tracking_map)
    MapView mMapView;
    private Marker mSearchMarker; // 搜索位置图标

    // UI相关
    private UiSettings mUiSettings;

    @BindView(R.id.tracking_message_content)
    TextView mMessageContent; // 定位信息文本
    @BindView(R.id.tracking_count_down_content)
    TextView mCountDownText; // 倒计时文本
    @BindView(R.id.tracking_weather_content)
    TextView mWeatherContent;

    @BindView(R.id.tracking_locus_btn)
    ImageButton mLocusBtn;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private final List<AAADeviceModel> mItemList = new ArrayList<>(); // 轨迹列表
    private final List<LatLng> mLatLngItemList = new ArrayList<>(); // 轨迹列表
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    private Timer mTimer; // 计时器
    private long countDown = 0; // 计时数
    private Bundle mSavedInstanceState;

    private Context mContext;
    private final List<LatLng> latLngs = new ArrayList<>();
    private final List<AMapMovementTrack> tracks = new ArrayList<>();
    private GameLiveAdapter adapter;
    private AAAUserModel mUserModel;
    private final float cameraUpdate = 17f;
    private BitmapDescriptor bitmapDes = null;
    private BitmapDescriptor supplementBitmapDes = null; //补传点marker样式
    private final Map<String, List<AAATrackModel>> trackMap = new HashMap<>();
    private boolean isChanged = false;
    private int refreshInterval = 0;
    private final int MARKER_SIZE = FinalMapUtils.MARKER_SIZE;
    private final int POLYLINE_WIDTH = FinalMapUtils.POLYLINE_WIDTH;

    private Long pigeonRaceId;


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
        return R.layout.aaa_fragment_race_live_broadcast;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSavedInstanceState = savedInstanceState;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
//        titleBar.setTitle(R.string.live_broadcast);
        titleBar.setTitle(String.format("%s%s", getString(R.string.pet_real_time),
                getString(R.string.live_broadcast)));
        TitleBar.Action action = new TitleBar.ImageAction(R.mipmap.ic_list_white_normal) {
            @Override
            public void performAction(View view) {
                mDrawerLayout.openDrawer(Gravity.END);
            }
        };
        titleBar.addAction(action);
        return titleBar;
    }

    @Override
    protected void initViews() {
        mContext = getContext();
        mLocusBtn.setVisibility(View.GONE);
        initMap(mSavedInstanceState);
        initListeners();
        initDatas();
        initBitmapDes();
        initAdapter();
        initRecyclerView();
    }

    private void initDatas() {
        refreshInterval = SettingSPUtils.getInstance().getInt(TConstant.LOCATION_REFRESH_INTERVAL, 120);
        mUserModel = getTrackUserModel();
        Bundle bundle = getArguments();
        if (bundle == null) return;
        pigeonRaceId = bundle.getLong("pigeonRaceId");
    }


    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(adapter);
    }

    private int getRandomNumber() {
        return (int) Math.round(255 * Math.random());
    }

    private void initAdapter() {
        List<Integer> colors = new ArrayList<>();
        resetColors(colors);
        TagAdapter<Integer> tagAdapter = new TagAdapter<Integer>(colors) {
            @Override
            public View getView(FlowLayout parent, int position, Integer integer) {
                TextView tv = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.aaa_adapter_color_board, null);
                tv.setBackgroundColor(integer);
                return tv;
            }
        };
        adapter = new GameLiveAdapter(tracks);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                int index = position;
                String deviceImei = mItemList.get(position).getDeviceImei();
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                AlertDialog alertDialog = builder.create();
                final View v = LayoutInflater.from(mContext).inflate(R.layout.aaa_dialog_color_board, null);
                final TagFlowLayout tagFlowLayout = v.findViewById(R.id.flowlayout);
                final TextView change = (TextView) v.findViewById(R.id.tv_change_color);
                change.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        resetColors(colors);
                        tagAdapter.notifyDataChanged();
                    }
                });
                tagFlowLayout.setAdapter(tagAdapter);

                tagFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
                    @Override
                    public boolean onTagClick(View view, int position, FlowLayout parent) {
                        isChanged = true;
                        alertDialog.dismiss();
                        tracks.get(index).setColor(colors.get(position));
                        adapter.notifyDataSetChanged();
                        initTrack(Objects.requireNonNull(trackMap.get(deviceImei)), deviceImei, 0);
                        mDrawerLayout.closeDrawers();
                        return false;
                    }
                });

//                final TextView textView = new TextView(mContext);
//                textView.setHeight(80);
//                textView.setWidth(80);
//                textView.setBackgroundColor(color);
                alertDialog.setView(v);
                alertDialog.setTitle(R.string.choose_color_of_polyline);
                alertDialog.show();

            }
        });
    }

    //初始化轨迹中途点marker的bitmapDescriptor
    private void initBitmapDes() {
        bitmapDes = BitmapDescriptorFactory.fromBitmap(
                TransformImageAppearance.resizeBitmap(
                        BitmapFactory.decodeResource(getResources(), R.mipmap.sub_marker), MARKER_SIZE));
        supplementBitmapDes = BitmapDescriptorFactory.fromBitmap(
                TransformImageAppearance.resizeBitmap(
                        BitmapFactory.decodeResource(getResources(), R.mipmap.supplement_marker), MARKER_SIZE));
    }


    /**
     * 初始化监听器
     */
    public void initListeners() {
        mAMap.setInfoWindowAdapter(mInfoWindowAdapter);
        mAMap.setOnInfoWindowClickListener(mInfoWindowClickListener);
        mAMap.setOnMarkerClickListener(mMarkerClickListener);

        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View view, float v) {

            }

            @Override
            public void onDrawerOpened(@NonNull View view) {

            }

            @Override
            public void onDrawerClosed(@NonNull View view) {
//                if (isChanged) {
//                    isChanged = false;
//                    for (AMapMovementTrack aMapMovementTrack : tracks) {
//                        mAMap.addPolyline(new PolylineOptions().addAll(aMapMovementTrack.getLatLngs()).width(POLY_LINE_WIDTH).color(aMapMovementTrack.getColor()));
//                    }
//                }
            }

            @Override
            public void onDrawerStateChanged(int i) {

            }
        });

    }

    /**
     * 初始化列表信息
     */
    private void initItems() {
        mLatLngItemList.clear();
        for (AAADeviceModel deviceModel : mItemList) {
            if (deviceModel.getLastLatitude() != null && deviceModel.getLastLongitude() != null) {
                mLatLngItemList.add(convertLatLng(new LatLng(deviceModel.getLastLatitude(), deviceModel.getLastLongitude())));
            } else
                mLatLngItemList.add(new LatLng(0, 0));
        }
        for (AAADeviceModel item : mItemList) {
            getHistoryLocation(0, item.getDeviceImei());
        }
        initTrackMap();
        refreshOverlay();
        showLatLngBounds();
    }

    private void initTrackMap() {
        for (AAADeviceModel item : mItemList) {
            trackMap.put(item.getDeviceImei(), new ArrayList<>());
        }
    }

    /**
     * 转换坐标系
     *
     * @param latLng
     * @return
     */
    private LatLng convertLatLng(LatLng latLng) {
        CoordinateConverter converter = new CoordinateConverter(getContext());
        converter.from(CoordinateConverter.CoordType.GPS);
        converter.coord(new LatLng(latLng.latitude, latLng.longitude));
        return converter.convert();
    }

    ;


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
     * 刷新设备图层
     */
    private void refreshOverlay() {
        for (int i = 0; i < mLatLngItemList.size(); i++) {
            int index = i;
            MarkerOptions option = new MarkerOptions()
                    .position(mLatLngItemList.get(i))
                    .draggable(false)
                    .title("")
                    .snippet("")
                    .setInfoWindowOffset(0, -2)
                    .setFlat(false);//将Marker设置为贴地显示，可以双指下拉地图查看效果
//            option.icon(BitmapDescriptorFactory.fromView
//                    (AAAMapUtils.getTrackingMarkerIcon(getContext(), mItemList.get(i))));

            Marker marker = mAMap.addMarker(option);
            View view = LayoutInflater.from(mContext).inflate(R.layout.map_tracking_icon_layout, null);
            ImageView iconImg = view.findViewById(R.id.map_tracking_icon);
            int imgRes = R.mipmap.ic_default_pigeon_marker;
            iconImg.setImageResource(imgRes);
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapBlobUtils.convertViewToBitmap(view)));
            marker.setObject(mItemList.get(i));

            tracks.add(new AMapMovementTrack()
                    .marker(marker)
                    .deviceModel(mItemList.get(index))
                    .latLng(mLatLngItemList.get(index))
                    .initColor());
            // 默认隐藏marker,获取到历史轨迹后再显示
            marker.setVisible(false);

            customizeMarkerIcon(mItemList.get(index).getHeadPic(), new OnMarkerIconLoadListener() {
                @Override
                public void markerIconLoadingFinished(View view) {
                    Bitmap bitmap = BitmapBlobUtils.convertViewToBitmap(view);
                    BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
                    option.icon(bitmapDescriptor);
                    marker.setIcon(bitmapDescriptor);
                    marker.setObject(mItemList.get(index));
                    for (AMapMovementTrack item : tracks) {
                        if (mItemList.get(index).getDeviceImei().equals(item.getDeviceModel().getDeviceImei())) {
                            item.setBitmapDescriptor(bitmapDescriptor);
                            break;
                        }
                    }
                }
            });

//            BitmapDescriptor bitmapDescriptor = getBitmapDescriptor(mItemList.get(i).getHeadPic());
//            option.icon(bitmapDescriptor);
//            Marker marker = mAMap.addMarker(option);
//            AAADeviceModel deviceModel = getTrackDeviceModel();
//            if (deviceModel != null && TextUtils.equals(
//                    mItemList.get(i).getDeviceImei(), deviceModel.getDeviceImei())) {
//                mMarker = marker;
//                mLatLng = mLatLngItemList.get(i);
//            }
//            marker.setObject(mItemList.get(i));
//            movementTracks.add(new AMapMovementTrack(mItemList.get(i).getDeviceImei(),mLatLngItemList.get(i),marker,bitmapDescriptor,mItemList.get(i)));
        }
    }

    /**
     * Glide下载网络图片并返回
     *
     * @param url
     * @param listener
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
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        listener.markerIconLoadingFinished(view);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
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
        LatLngBounds.Builder latLngBoundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLng : mLatLngItemList)
            latLngBoundsBuilder.include(latLng);
        if (mAMap != null)
            mAMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBoundsBuilder.build(),
                    getResources().getDimensionPixelOffset(R.dimen.margin_24)));
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
     * 地图信息弹窗适配器
     */
    private final AMap.InfoWindowAdapter mInfoWindowAdapter = new AMap.InfoWindowAdapter() {

        @Override
        public View getInfoWindow(Marker marker) {
            // TODO Auto-generated method stub
            AAADeviceModel deviceModel = null;
            if (marker.getObject() != null)
                deviceModel = (AAADeviceModel) marker.getObject();
            return FinalMapUtils.createInfoContentView(mContext, deviceModel, 0);
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

    private final AMap.OnMarkerClickListener mMarkerClickListener =
            new AMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    try {
                        AAADeviceModel deviceModel = (AAADeviceModel) marker.getObject();
                        if (!TextUtils.isEmpty(deviceModel.getLastPositionDesc()))
                            mMessageContent.setText(deviceModel.getLastPositionDesc());
                        else
                            mMessageContent.setText(new StringBuilder(getString(R.string.longitude)).append(":").append(deviceModel.getLastLongitude()).append("  ")
                                    .append(getString(R.string.latitude)).append(":").append(deviceModel.getLastLatitude()));
                        mWeatherContent.setText(deviceModel.getWeather());
                        mSearchMarker = marker;
                    } catch (ClassCastException e) {
                        e.printStackTrace();
                    }
                    return false;
                }
            };

    @OnClick({R.id.tracking_map_type_btn, R.id.tracking_zoom_in_btn
            , R.id.tracking_zoom_out_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tracking_map_type_btn: // 地图类型
                if (v.isSelected()) {
                    v.setSelected(false);
                    mAMap.setMapType(AMap.MAP_TYPE_NORMAL);
                } else {
                    v.setSelected(true);
                    mAMap.setMapType(AMap.MAP_TYPE_SATELLITE);
                }
                break;
            case R.id.tracking_zoom_in_btn: // 地图放大
                mAMap.moveCamera(CameraUpdateFactory.zoomIn());
                break;
            case R.id.tracking_zoom_out_btn: // 地图缩小
                mAMap.moveCamera(CameraUpdateFactory.zoomOut());
                break;
            default:
                break;
        }
    }

    /**
     * 获取历史轨迹（循环
     *
     * @param nextId 分页查询索引号
     *               requestRows 分页大小
     */
    @SuppressLint("SimpleDateFormat")
    private void getHistoryLocation(long nextId, String deviceImei) {
        AAAUserModel userModel = getTrackUserModel();
        SimpleDateFormat simpleDateFormatEnd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat simpleDateFormatStart = new SimpleDateFormat("yyyy-MM-dd");
        long date = new Date().getTime();
        String endTime = simpleDateFormatEnd.format(date);
        String startTime = simpleDateFormatStart.format(date) + " 00:00:00";
        CarGpsRequestUtils.getLastDeviceConfigTrack(userModel, deviceImei
                , startTime, endTime, nextId, 20, mHandler);
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        LatLng latLng = new LatLng(regeocodeResult.getRegeocodeQuery().getPoint().getLatitude(),
                regeocodeResult.getRegeocodeQuery().getPoint().getLongitude());
        String address = regeocodeResult.getRegeocodeAddress().getFormatAddress();
        if (mSearchMarker.getPosition().longitude == latLng.longitude && mSearchMarker
                .getPosition().latitude == latLng.latitude) {
            if (TextUtils.isEmpty(address))
                mMessageContent.setText(getString(R.string.tracking_address_not_find));
            else
                mMessageContent.setText(address);
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    /**
     * 消息处理
     */
    private final Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            try {
                AAABaseResponseBean responseBean;
                AAARequestBean requestBean;
                switch (msg.what) {
                    case 1:  // 倒计时
                        if ((countDown++) % refreshInterval == 0)
                            CarGpsRequestUtils.getDeviceListOfPigeonRace(mUserModel, pigeonRaceId, mHandler);
                        mCountDownText.setText(getString(R.string.tracking_count_down, refreshInterval - countDown % refreshInterval));
                        break;
                    case TConstant.REQUEST_GET_DEVICE_LIST_OF_PIGEON_RACE_LIVE://get the device information contains last LatLng in Pigeon race live broadcast
                        List<AAADeviceModel> deviceModels = new ArrayList<>();
                        if (msg.obj != null) {
                            responseBean = (AAABaseResponseBean) msg.obj;
                            if (responseBean.getCode() == TConstant.RESPONSE_SUCCESS) {
                                ArrayList sub1 = (ArrayList) responseBean.getData();
                                if (sub1.size() > 0) {
                                    ArrayList sub2 = (ArrayList) sub1.get(0);
                                    for (int i = 0; i < sub2.size(); i++) {
                                        AAADeviceModel deviceModel = mGson.fromJson(mGson.toJson(sub2.get(i)), AAADeviceModel.class);
                                        deviceModels.add(deviceModel);
                                    }
                                    if (mItemList.size() == 0) {  //replace to initItemList
                                        mItemList.addAll(deviceModels);
                                        adapter.notifyDataSetChanged();
                                        initItems();
                                    } else {
                                        for (AAADeviceModel item : deviceModels) {
                                            for (AMapMovementTrack track : tracks) {
                                                if (item.getDeviceImei().equals(track.getDeviceModel().getDeviceImei())) {
                                                    LatLng lastLatLng = convertLatLng(new LatLng(item.getLastLatitude(), item.getLastLongitude()));
                                                    track.getMarker().setPosition(lastLatLng);
                                                    track.getMarker().setObject(item);
                                                    track.setDeviceModel(item);
                                                    if (track.getLatLng().latitude != lastLatLng.latitude &&
                                                            track.getLatLng().longitude != lastLatLng.longitude) {
                                                        List<AAATrackModel> trackModelList = trackMap.get(item.getDeviceImei());
                                                        if (trackModelList != null && trackModelList.size() != 0)
                                                            getHistoryLocation(trackModelList.get(trackModelList.size() - 1).getLogId(), item.getDeviceImei());
                                                        else
                                                            getHistoryLocation(0, item.getDeviceImei());
                                                        track.setLatLng(lastLatLng);
                                                    }
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                showMessage(ErrorCode.getResId(responseBean.getCode()));
                            }
                        } else {
                            showMessage(R.string.network_error_prompt);
                        }
                        break;
                    case TConstant.REQUEST_URL_GET_LAST_DEVICE_CONFIG_TRACK:
                        if (msg.obj == null) {
                            dismisDialog();
                            showMessage(R.string.request_unkonow_prompt);
                            return false;
                        }
                        responseBean = (AAABaseResponseBean) msg.obj;
                        if (responseBean.getCode() == TConstant.RESPONSE_SUCCESS) {
                            ListResponseBean listResponseBean = mGson.fromJson(mGson.toJson(responseBean.getData()),
                                    ListResponseBean.class);
                            requestBean = mGson.fromJson(responseBean.getRequestObject(),
                                    AAARequestBean.class);
                            String deviceImei = requestBean.getDeviceImei();
                            int deviceType = 0;
                            for (AAADeviceModel item :
                                    mItemList) {
                                if (item.getDeviceImei().equals(deviceImei)) {
                                    deviceType = item.getDeviceType();
                                    break;
                                }
                            }
                            List<AAATrackModel> trackModelList = Objects.requireNonNull(trackMap.get(deviceImei), "can't find trackModel list");
                            int startIndex = trackModelList.size();

                            List list;
                            if (listResponseBean == null) {
                                list = null;
                            } else {
                                list = listResponseBean.getList();
                            }

                            if (list != null) {
                                for (Object item : list) {
                                    AAATrackModel trackModel = mGson.fromJson(mGson.toJson(item), AAATrackModel.class);
                                    trackModelList.add(trackModel);
                                }
                            }

                            initTrack(trackModelList, deviceImei, startIndex);

                            if (trackModelList.size() % 20 == 0 && trackModelList.get(trackModelList.size() - 1).getLogId() != null)
                                getHistoryLocation(trackModelList.get(trackModelList.size() - 1).getLogId(), deviceImei);
                        } else {
                            showMessage(R.string.tip_request_error);
                            dismisDialog();
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

    private void resetColors(List<Integer> list) {
        if (list.size() != 0) list.clear();
        for (int i = 0; i < 10; i++) {
            int color = Color.rgb(getRandomNumber(), getRandomNumber(), getRandomNumber());
            list.add(color);
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        // 关闭定位图层
        if (mAMap != null) {
            mAMap.clear();
            mAMap.setMyLocationEnabled(false);
        }
        if (mMapView != null)
            mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

    private void initTrack(List<AAATrackModel> list, String deviceImei, int startIndex) {
        if (list.size() == 0) return;
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
                tracks.get(j).getMarker().setVisible(true);
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
            if (deviceModel != null) {
                LatLng latLng = convertLatLng(new LatLng(list.get(i).getLat(), list.get(i).getLng()));
                if (supplementFlag != list.get(i).getSupplement()) {
                    change = true;
                }
                latLngs.add(latLng);
                Marker marker;
                if (i == 0) {
                    marker = mAMap.addMarker(new MarkerOptions().position(latLng)
                            .icon(BitmapDescriptorFactory.fromBitmap(NewMapUtils.getDrivingMarkerIcon(getResources(), 0))));
                } else {
                    if (list.get(i).getSupplement() == 1)
                        marker = mAMap.addMarker(new MarkerOptions().position(latLng).icon(supplementBitmapDes).anchor(0.5f, 0.5f));
                    else
                        marker = mAMap.addMarker(new MarkerOptions().position(latLng).icon(bitmapDes).anchor(0.5f, 0.5f));
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
                        latLngs.add(convertLatLng(new LatLng(list.get(i - 1).getLat(), list.get(i - 1).getLng())));
                    latLngs.add(latLng1);
                }
            }
        }
    }
}