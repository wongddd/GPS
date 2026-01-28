package com.yyt.trackcar.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAABaseItemBean;
import com.yyt.trackcar.bean.DeviceRaceconfigplan;
import com.yyt.trackcar.dbflow.AAADeviceModel;
import com.yyt.trackcar.dbflow.AAAUserModel;
import com.yyt.trackcar.ui.adapter.DeviceSettingItemAdapter;
import com.yyt.trackcar.ui.base.BaseActivity;
import com.yyt.trackcar.utils.TConstant;
import com.yyt.trackcar.utils.ViewDataUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


/**
 * @ projectName:
 * @ packageName:   com.yyt.trackcar.ui.activity
 * @ fileName:      DeviceSettingActivity
 * @ author:        QING
 * @ createTime:    6/22/21 21:42
 * @ describe:      TODO
 */
@SuppressLint("NonConstantResourceId")
public class ConfigurationParameterActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView; // RecyclerView
    private List<AAABaseItemBean> mItemList = new ArrayList<>(); // 用户信息列表
    private AAADeviceModel mDeviceModel; // 设备对象
    private AAAUserModel mUserModel;

    private DeviceRaceconfigplan mConfiguration;
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("HH:mm");
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat simpleDateFormat3 = new SimpleDateFormat("yyyy-MM-dd");
    @BindView(R.id.fl_back)
    FrameLayout back;
    private DeviceSettingItemAdapter adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDatas();
        initPigeonDeviceItem();
        initListener();
        initAdapters();
        initRecyclerViews();
        initHeaderView();
    }

    private void initListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_device_setting;
    }

    private void initDatas() {
        tvTitle.setText(String.format("%s%s", getString(R.string.pet_real_time), getString(R.string.home_device_setting)));
        mDeviceModel = getTrackDevice();
        mUserModel = getTrackUserModel();
//        KLog.d("aaaaaa: "+getIntent().getSerializableExtra(TConstant.SERIALIZABLE));
        mConfiguration = getIntent().getParcelableExtra(TConstant.PARCELABLE);
    }

    private void initAdapters() {
        adapter = new DeviceSettingItemAdapter(mItemList);
    }

    private void initRecyclerViews() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);
    }

    /**
     * 初始化头布局
     */
    private void initHeaderView() {
        View headerView = getLayoutInflater().inflate(R.layout.header_view_device_info,
                mRecyclerView, false);
        ViewDataUtils.initDeviceInfoView(this, headerView, mDeviceModel);
        adapter.addHeaderView(headerView);
    }

    private void initPigeonDeviceItem() {
        mItemList.clear();
        mItemList.add(new AAABaseItemBean(218,getString(R.string.standby_time),mConfiguration.getStandbyDatetime() == null ? "0" : simpleDateFormat.format(mConfiguration.getStandbyDatetime())));
        mItemList.add(new AAABaseItemBean(200, getString(R.string.configuration_data_modification_time), mConfiguration.getCst() == null ? "" : simpleDateFormat.format(mConfiguration.getCst())));
        mItemList.add(new AAABaseItemBean(201, getString(R.string.scheduled_start_time), mConfiguration.getRsut() == null ? "" : simpleDateFormat.format(mConfiguration.getRsut())));
        mItemList.add(new AAABaseItemBean(202, getString(R.string.positioning_frequency), String.format("%s%s", mConfiguration.getRgli() == null ? "0" : mConfiguration.getRgli(), getString(R.string.position_frequency_unit))));
        mItemList.add(new AAABaseItemBean(203, getString(R.string.upload_frequency), String.format("%s%s", mConfiguration.getRdui() == null ? "0" : mConfiguration.getRdui(), getString(R.string.position_frequency_unit))));
        mItemList.add(new AAABaseItemBean(205, getString(R.string.low_battery_threshold), String.format("%s%%", mConfiguration.getLpl() == null ? "0" : mConfiguration.getLpl())));
        mItemList.add(new AAABaseItemBean(212, getString(R.string.positioning_frequency_in_low_battery_mode), String.format("%s%s", mConfiguration.getLpgli() == null ? "0" : mConfiguration.getLpgli(), getString(R.string.position_frequency_unit))));
        mItemList.add(new AAABaseItemBean(213, getString(R.string.data_upload_frequency_in_low_battery_mode), String.format("%s%s", mConfiguration.getLpdui() == null ? "0" : mConfiguration.getLpdui(), getString(R.string.position_frequency_unit))));
        mItemList.add(new AAABaseItemBean(206, getString(R.string.second_stage_start_time), mConfiguration.getNmst() == null ? "" : simpleDateFormat2.format(mConfiguration.getNmst())));
        mItemList.add(new AAABaseItemBean(207, getString(R.string.second_stage_end_time), mConfiguration.getNmet() == null ? "" : simpleDateFormat2.format(mConfiguration.getNmet())));
        mItemList.add(new AAABaseItemBean(208, getString(R.string.positioning_frequency_in_second_stage), String.format("%s%s", mConfiguration.getNgli() == null ? "0" : mConfiguration.getNgli(), getString(R.string.position_frequency_unit))));
        mItemList.add(new AAABaseItemBean(209, getString(R.string.data_upload_frequency_in_second_stage), String.format("%s%s", mConfiguration.getNdui() == null ? "0" : mConfiguration.getNdui(), getString(R.string.position_frequency_unit))));
        mItemList.add(new AAABaseItemBean(210, getString(R.string.positioning_frequency_in_third_stage), String.format("%s%s", mConfiguration.getCgli() == null ? "0" : mConfiguration.getCgli(), getString(R.string.position_frequency_unit))));
        mItemList.add(new AAABaseItemBean(211, getString(R.string.data_upload_frequency_in_third_stage), String.format("%s%s", mConfiguration.getCdui() == null ? "0" : mConfiguration.getCdui(), getString(R.string.position_frequency_unit))));
        mItemList.add(new AAABaseItemBean(204, getString(R.string.data_upload_start_time), mConfiguration.getRdut() == null ? "" : simpleDateFormat3.format(mConfiguration.getRdut())));
        mItemList.add(new AAABaseItemBean(217, getString(R.string.start_downloading_ephemeris_time), mConfiguration.getGddt() == null ? "" : simpleDateFormat.format(mConfiguration.getGddt())));
    }
}

