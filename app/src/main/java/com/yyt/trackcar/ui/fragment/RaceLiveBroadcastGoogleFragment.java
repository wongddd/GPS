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
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.CoordinateConverter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAABaseResponseBean;
import com.yyt.trackcar.bean.AAARequestBean;
import com.yyt.trackcar.bean.AAATrackModel;
import com.yyt.trackcar.bean.GoogleMapMovementTrack;
import com.yyt.trackcar.bean.GpsBean;
import com.yyt.trackcar.bean.ListResponseBean;
import com.yyt.trackcar.dbflow.AAADeviceModel;
import com.yyt.trackcar.dbflow.AAAUserModel;
import com.yyt.trackcar.ui.adapter.GameLiveGoogleAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.BitmapBlobUtils;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.ErrorCode;
import com.yyt.trackcar.utils.FinalMapUtils;
import com.yyt.trackcar.utils.NewMapUtils;
import com.yyt.trackcar.utils.PositionUtils;
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
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
@Page(name = "RaceLiveBroadcastGoogleFragment", anim = CoreAnim.none)
public class RaceLiveBroadcastGoogleFragment extends BaseFragment implements View.OnClickListener,
        OnMapReadyCallback {
    private GoogleMap mMap;
    private Marker mSearchMarker; // 搜索定位图标
    // UI相关
    private UiSettings mUiSettings;

    @BindView(R.id.tracking_google_message_content)
    TextView mMessageContent; // 定位信息文本
    @BindView(R.id.tracking_google_count_down_content)
    TextView mCountDownText; // 倒计时文本
    @BindView(R.id.tracking_google_weather_content)
    TextView mWeatherContent;

    private final List<AAADeviceModel> mItemList = new ArrayList<>(); // 轨迹列表
    private final List<LatLng> mLatLngItemList = new ArrayList<>(); // 轨迹列表
    private Timer mTimer; // 计时器
    private long countDown = 0; // 计时数
    private AAAUserModel mUserModel;
    private final int GET_POSITION_DESCRIPTION = 0x233;

    private Context mContext;
    private final List<GoogleMapMovementTrack> tracks = new ArrayList<>();
    private GameLiveGoogleAdapter adapter;
    private final List<LatLng> latLngs = new ArrayList<>();
    private BitmapDescriptor bitmapDes = null;
    private BitmapDescriptor supplementBitmapDes = null; //补传点marker样式
    private final Map<String, List<AAATrackModel>> trackMap = new HashMap<>(); //轨迹Map<Imei,List<LatLng>>
    private Long pigeonRaceId;
    private int refreshInterval = 0;
    private final int MARKER_SIZE = FinalMapUtils.MARKER_SIZE;
    private final int POLYLINE_WIDTH = FinalMapUtils.POLYLINE_WIDTH;
    private int infoWindowStatus = 0;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

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
        return R.layout.aaa_fragment_race_live_broadcast_google;
    }

    @Override
    protected void initViews() {
        mContext = getContext();
        initDatas();
        initAdapter();
        initRecyclerView();
        initListeners();
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

    /**
     * 初始化信息
     */
    protected void initDatas() {
        refreshInterval = SettingSPUtils.getInstance().getInt(TConstant.LOCATION_REFRESH_INTERVAL, 120);
        mUserModel = getTrackUserModel();
        Bundle bundle = getArguments();
        if (bundle == null) return;
        pigeonRaceId = bundle.getLong("pigeonRaceId");
    }

    /**
     * 初始化列表信息
     */
    protected void initItems() {
        mLatLngItemList.clear();
        for (AAADeviceModel deviceModel : mItemList) {
            if (deviceModel.getLastLatitude() != null && deviceModel.getLastLongitude() != null)
                mLatLngItemList.add(convertLatLng(new LatLng(deviceModel.getLastLatitude(), deviceModel.getLastLongitude())));
            else
                mLatLngItemList.add(new LatLng(0, 0));
        }
        initTrackMap();
        initMap();
    }

    /**
     * 初始化AMap对象
     */
    private void initMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.tracking_google_map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void initListeners() {
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View view, float v) {

            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDrawerOpened(@NonNull View view) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onDrawerClosed(@NonNull View view) {
//                for (GoogleMapMovementTrack item : tracks) {
//                    mMap.addPolyline(new PolylineOptions().addAll(item.getLatLngs()).width(POLY_LINE_WIDTH).color(item.getColor()));
//                }
            }

            @Override
            public void onDrawerStateChanged(int i) {

            }
        });
    }

    /**
     * 刷新设备图层
     */
    private void refreshOverlay() {
        if (mMap != null) {
            for (int i = 0; i < mLatLngItemList.size(); i++) {
                LatLng latLng = mLatLngItemList.get(i);
                MarkerOptions option = new MarkerOptions()
                        .position(latLng)
                        .draggable(false)
                        .title("")
                        .snippet("");
//                option.icon(BitmapDescriptorFactory.fromBitmap(BitmapBlobUtils
//                        .convertViewToBitmap(AAAMapUtils.getTrackingMarkerIcon(mContext,
//                                mItemList.get(i)))));
                // 将Marker设置为贴地显示，可以双指下拉地图查看效果
                option.flat(false);//设置marker平贴地图效果

                //设置默认marker
                Marker marker = mMap.addMarker(option);
                View markerView = getLayoutInflater().inflate(R.layout.marker_avator_view, null);
                int imgRes = R.mipmap.ic_default_pigeon_marker;
                ImageView ivPortrait = markerView.findViewById(R.id.ivPortrait);
                ivPortrait.setImageResource(imgRes);
                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(BitmapBlobUtils.convertViewToBitmap(markerView));
                marker.setIcon(bitmapDescriptor);
                marker.setTag(mItemList.get(i));
                int index = i;

                tracks.add(new GoogleMapMovementTrack()
                        .deviceModel(mItemList.get(index))
                        .lastLatLgn(latLng)
                        .marker(marker)
                        .initLatLngs(latLng)
                        .initColor());
                tracks.get(i).setBitmapDescriptor(bitmapDescriptor);

                // 默认隐藏marker,获取到历史轨迹后再显示
                marker.setVisible(false);

                int finalI = i;
                customMarker(mItemList.get(i).getHeadPic(), new OnMarkerImageLoadedListener() {
                    @Override
                    public void onMarkerImageLoadedListener(View view) {
                        Bitmap bitmap = BitmapBlobUtils.convertViewToBitmap(view);
                        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
                        marker.setIcon(bitmapDescriptor);
                        marker.setTag(mItemList.get(index));
                        tracks.get(finalI).setBitmapDescriptor(bitmapDescriptor);
                    }
                });
            }
        }
    }

    private void customMarker(String imageUrl, final OnMarkerImageLoadedListener listener) {
        View markerView = getLayoutInflater().inflate(R.layout.marker_avator_view, null);
        int imgRes = R.mipmap.ic_default_pigeon_marker;
        ImageView ivPortrait = markerView.findViewById(R.id.ivPortrait);
        ivPortrait.setImageResource(imgRes);

        Glide.with(mContext)
                .asBitmap()
                .load(imageUrl)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        ivPortrait.setImageBitmap(resource);
                        listener.onMarkerImageLoadedListener(markerView);
                    }
                });
    }


    private interface OnMarkerImageLoadedListener {
        void onMarkerImageLoadedListener(View view);
    }

    /**
     * 初始化轨迹中途点marker的bitmapDescriptor
     */
    private void initBitmapDes() {
        bitmapDes = BitmapDescriptorFactory.fromBitmap(
                TransformImageAppearance.resizeBitmap(
                        BitmapFactory.decodeResource(getResources(), R.mipmap.sub_marker), MARKER_SIZE));
        supplementBitmapDes = BitmapDescriptorFactory.fromBitmap(
                TransformImageAppearance.resizeBitmap(
                        BitmapFactory.decodeResource(getResources(), R.mipmap.supplement_marker), MARKER_SIZE));
    }

    /**
     * 初始化轨迹trackMap
     */
    private void initTrackMap() {
        for (AAADeviceModel item : mItemList) {
            trackMap.put(item.getDeviceImei(), new ArrayList<>());
        }
    }

    /**
     * 显示范围
     */
    private void showLatLngBounds() {
        if (mMap != null) {
            LatLngBounds.Builder latLngBoundsBuilder = new LatLngBounds.Builder();
            if (mLatLngItemList.size() != 0) {
                for (LatLng latLng : mLatLngItemList) {
                    latLngBoundsBuilder.include(latLng);
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBoundsBuilder.build(),
                        getResources().getDimensionPixelOffset(R.dimen.margin_24)));
            }
        }
    }

    /**
     * 查找位置
     *
     * @param marker 坐标
     */
    private void searchLocation(final Marker marker) {
        try {
            final AAADeviceModel deviceModel = (AAADeviceModel) marker.getTag();
            if (deviceModel != null) {
                mSearchMarker = marker;
                if (!TextUtils.isEmpty(deviceModel.getLastPositionDesc()))
                    mMessageContent.setText(deviceModel.getLastPositionDesc());
                else
//                    mHandler.sendEmptyMessage(GET_POSITION_DESCRIPTION);
                    mMessageContent.setText(new StringBuilder(getString(R.string.longitude)).append(":").append(deviceModel.getLastLongitude()).append("  ")
                            .append(getString(R.string.latitude)).append(":").append(deviceModel.getLastLatitude()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setIndoorEnabled(false);
        initBitmapDes();
        mUiSettings = mMap.getUiSettings();
        mUiSettings.setMyLocationButtonEnabled(false); // 是否显示默认的定位按钮
        mUiSettings.setTiltGesturesEnabled(true);// 设置地图是否可以倾斜
        mUiSettings.setZoomControlsEnabled(false);
        mUiSettings.setIndoorLevelPickerEnabled(false); // 禁用楼层选择器
        mUiSettings.setMapToolbarEnabled(false);
        mMap.setInfoWindowAdapter(mInfoWindowAdapter);
        mMap.setOnInfoWindowClickListener(mInfoWindowClickListener);
        mMap.setOnMarkerClickListener(mMarkerClickListener);
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                for (AAADeviceModel item : mItemList) {
                    getHistoryLocation(0, item.getDeviceImei());
                }
                showLatLngBounds();
            }
        });
        refreshOverlay();
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
        adapter = new GameLiveGoogleAdapter(tracks);
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
                        alertDialog.dismiss();
                        tracks.get(index).setColor(colors.get(position));
//                        adapter.notifyDataSetChanged();
                        initTrack(Objects.requireNonNull(trackMap.get(deviceImei)),deviceImei,0);
                        mDrawerLayout.closeDrawers();
                        return false;
                    }
                });
                alertDialog.setView(v);
                alertDialog.setTitle(R.string.choose_color_of_polyline);
                alertDialog.show();
            }
        });
    }

    /**
     * 获取历史轨迹
     *
     * @param nextId 分页查询索引号
     *               requestRows 分页大小
     */
    @SuppressLint("SimpleDateFormat")
    private void getHistoryLocation(long nextId, String imei) {
        AAAUserModel userModel = getTrackUserModel();
        SimpleDateFormat simpleDateFormatEnd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat simpleDateFormatStart = new SimpleDateFormat("yyyy-MM-dd");
        long date = new Date().getTime();
        String endTime = simpleDateFormatEnd.format(date);
        String startTime = simpleDateFormatStart.format(date) + " 00:00:00";
        CarGpsRequestUtils.getLastDeviceConfigTrack(userModel, imei
                , startTime, endTime, nextId, 20, mHandler);
    }

    /**
     * 地图信息弹窗适配器
     */
    private GoogleMap.InfoWindowAdapter mInfoWindowAdapter = new GoogleMap.InfoWindowAdapter() {

        @Override
        public View getInfoWindow(Marker marker) {
            // TODO Auto-generated method stub
            @SuppressLint("InflateParams")
            View view = LayoutInflater.from(mContext).inflate(R.layout
                    .amap_info_window_layout, null);
            TextView mAddressText = view.findViewById(R.id.amap_info_window_address);
            if (marker.getTag() == null)
                mAddressText.setText("");
            else {
                AAADeviceModel deviceModel = (AAADeviceModel) marker.getTag();
                if (infoWindowStatus == 0) {
                    mAddressText.setText(FinalMapUtils.concatInfoWindowCollapsedContent(deviceModel, 0));
                } else {
                    mAddressText.setText(FinalMapUtils.concatInfoWindowContent(deviceModel, 0));
                }
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
                mark.hideInfoWindow();
            }
        }
    };

    private final GoogleMap.OnMarkerClickListener mMarkerClickListener = new GoogleMap
            .OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            infoWindowStatus = 0;
            searchLocation(marker);
            return false;
        }
    };

    @OnClick({R.id.tracking_google_map_type_btn, R.id.tracking_google_zoom_in_btn, R.id.tracking_google_zoom_out_btn
    })
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tracking_google_map_type_btn: // 地图类型
                if (v.isSelected()) {
                    v.setSelected(false);
                    if (mMap != null)
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                } else {
                    v.setSelected(true);
                    if (mMap != null)
                        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                }
                break;
            case R.id.tracking_google_zoom_in_btn: // 地图放大
                if (mMap != null)
                    mMap.moveCamera(CameraUpdateFactory.zoomIn());
                break;
            case R.id.tracking_google_zoom_out_btn: // 地图缩小
                if (mMap != null)
                    mMap.moveCamera(CameraUpdateFactory.zoomOut());
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
                AAARequestBean requestBean;
                switch (msg.what) {
                    case 1:  // 倒计时
                        if ((countDown++) % refreshInterval == 0)
                            CarGpsRequestUtils.getDeviceListOfPigeonRace(mUserModel, pigeonRaceId, mHandler);
                        mCountDownText.setText(getString(R.string.tracking_count_down, refreshInterval - countDown % refreshInterval));
                        break;
                    case TConstant.REQUEST_GET_DEVICE_LIST_OF_PIGEON_RACE_LIVE: //get the device information contains last LatLng in Pigeon race live broadcast
                        List<AAADeviceModel> deviceModels = new ArrayList<>();
                        if (msg.obj != null) {
                            responseBean = (AAABaseResponseBean) msg.obj;
                            if (responseBean.getCode() == TConstant.RESPONSE_SUCCESS) {
                                ArrayList sub1 = (ArrayList) responseBean.getData();
                                if (sub1.size() > 0) {
                                    ArrayList sub2 = (ArrayList) sub1.get(0);
                                    for (int i = 0; i < sub2.size(); i++) {
                                        deviceModels.add(mGson.fromJson(mGson.toJson(sub2.get(i)), AAADeviceModel.class));
                                    }
                                    if (mItemList.size() == 0) {  //replace to initItemList
                                        mItemList.addAll(deviceModels);
                                        initItems();
                                    } else {
                                        for (AAADeviceModel item : deviceModels) {
                                            for (GoogleMapMovementTrack track : tracks) {
                                                if (item.getDeviceImei().equals(track.getDeviceModel().getDeviceImei())) {
                                                    LatLng lastLatLng = convertLatLng(new LatLng(item.getLastLatitude(), item.getLastLongitude()));
                                                    track.getMarker().setPosition(lastLatLng);
                                                    track.getMarker().setTag(item);
                                                    track.setDeviceModel(item);
                                                    // 当位置变动时获取继续获取轨迹
                                                    if (track.getLatLng().latitude != lastLatLng.latitude &&
                                                            track.getLatLng().longitude != lastLatLng.longitude) {
                                                        List<AAATrackModel> subTrackModelList = trackMap.get(item.getDeviceImei());
                                                        if (subTrackModelList != null && subTrackModelList.size() != 0)
                                                            getHistoryLocation(subTrackModelList.get(subTrackModelList.size() - 1).getLogId(), item.getDeviceImei());
                                                        else
                                                            getHistoryLocation(0, item.getDeviceImei());
                                                        track.setLatLng(lastLatLng);
                                                        break;
                                                    }
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }else {
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

                            initTrack(trackModelList,deviceImei,startIndex);

                            if (trackModelList.size() % 20 == 0 && trackModelList.get(trackModelList.size() - 1).getLogId() != null)
                                getHistoryLocation(trackModelList.get(trackModelList.size() - 1).getLogId(), deviceImei);
                        } else {
                            showMessage(R.string.tip_request_error);
                            dismisDialog();
                        }
                        break;
                    case GET_POSITION_DESCRIPTION:
                        GpsBean gps = PositionUtils.gcj_To_Gps84(mSearchMarker.getPosition().latitude
                                , mSearchMarker.getPosition().longitude);
                        LatLng latLng = new LatLng(gps.getWgLat(), gps.getWgLon());
                        String address = NewMapUtils.getAddress(mContext, latLng.latitude,
                                latLng.longitude);
                        try {
                            if (mSearchMarker != null) {
                                mMessageContent.setText(address);
                            }
                        } catch (Exception e) {
                            if (BuildConfig.DEBUG)
                                e.printStackTrace();
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
    public void onResume() {
        super.onResume();
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
        showLatLngBounds();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mTimer != null)
            mTimer.cancel();
        mTimer = null;
    }

    @Override
    public void onDestroy() {
        mMap = null;
        super.onDestroy();
    }

    //    坐标系转换 火星(谷歌)坐标系、高德坐标系
    private LatLng convertLatLng(LatLng latLng) {
        CoordinateConverter converter = new CoordinateConverter(mContext);
        converter.from(CoordinateConverter.CoordType.GPS);
        converter.coord(new com.amap.api.maps.model.LatLng(latLng.latitude,
                latLng.longitude));
        com.amap.api.maps.model.LatLng aMapLatlng = converter.convert();
        return new LatLng(aMapLatlng.latitude,
                aMapLatlng.longitude);
    }

    private void resetColors(List<Integer> list) {
        if (list.size() != 0) list.clear();
        for (int i = 0; i < 10; i++) {
            int color = Color.rgb(getRandomNumber(), getRandomNumber(), getRandomNumber());
            list.add(color);
        }
    }

    /**
     * @param list         轨迹列表
     * @param deviceImei   国际移动设备唯一识别码
     */
    private void initTrack(List<AAATrackModel> list, String deviceImei, int startIndex) {
        if (list.size() == 0) return;
        List<LatLng> latLngs = new ArrayList<>();
        AAADeviceModel deviceModel = null;
        int color = 0;
        int supplementFlag = 0;
        int supplementPointColor = 0;
        boolean change = false;
        for (int j = 0; j < mItemList.size(); j++) {
            if (mItemList.get(j).getDeviceImei().equals(deviceImei)) {
                color = tracks.get(j).getColor();
                supplementPointColor = tracks.get(j).getSupplementColor();
                tracks.get(j).getMarker().setVisible(true);
                break;
            }
        }
        List<AAATrackModel> trackModelList = Objects.requireNonNull(trackMap.get(deviceImei));
        if (startIndex != 0 && trackModelList.size() > 0) {
            latLngs.add(convertLatLng(new LatLng(
                    trackModelList.get(startIndex - 1).getLat()
                    , trackModelList.get(startIndex - 1).getLng())));
            if ((supplementFlag = trackModelList.get(startIndex-1).getSupplement()) != 1){
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
            if (deviceModel != null ) {
                LatLng latLng = convertLatLng(new LatLng(list.get(i).getLat(), list.get(i).getLng()));
                if (supplementFlag != list.get(i).getSupplement()){
                    change = true;
                }
                latLngs.add(latLng);
                Marker marker;
                if (i == 0) {
                    marker = mMap.addMarker(new MarkerOptions().position(latLng)
                            .icon(BitmapDescriptorFactory.fromBitmap(NewMapUtils.getDrivingMarkerIcon(getResources(), 0))));
                }else {
                    if (list.get(i).getSupplement() == 1)
                        marker = mMap.addMarker(new MarkerOptions().position(latLng).icon(supplementBitmapDes).anchor(0.5f, 0.5f));
                    else
                        marker = mMap.addMarker(new MarkerOptions().position(latLng).icon(bitmapDes).anchor(0.5f, 0.5f));
                    if (i == list.size() - 1){
                        for (int j = 0; j < mItemList.size(); j++) {
                            if (mItemList.get(j).getDeviceImei().equals(deviceImei)) {
                                tracks.get(j).getMarker().setTag(subDeviceModel);
                                break;
                            }
                        }
                    }
                }
                marker.setTag(subDeviceModel);
                if (change || i == list.size()-1){
                    change = false;
                    LatLng latLng1 = latLngs.get(latLngs.size()-1);
                    if (supplementFlag == 0)
                        mMap.addPolyline(new PolylineOptions()
                                .width(POLYLINE_WIDTH)
                                .color(color)
                                .addAll(latLngs));
                    else
                        mMap.addPolyline(new PolylineOptions()
                                .width(POLYLINE_WIDTH)
                                .color(supplementPointColor)
                                .addAll(latLngs));
                    supplementFlag = list.get(i).getSupplement();
                    latLngs.clear();
                    if (i >= 1)
                        latLngs.add(convertLatLng(new LatLng(list.get(i-1).getLat(), list.get(i-1).getLng())));
                    latLngs.add(latLng1);
                }
            }
        }
    }
}
