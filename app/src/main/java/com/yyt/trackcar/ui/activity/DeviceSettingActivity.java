package com.yyt.trackcar.ui.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.baoyz.actionsheet.ActionSheet;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xuexiang.xui.utils.KeyboardUtils;
import com.xuexiang.xui.widget.picker.widget.OptionsPickerView;
import com.xuexiang.xui.widget.picker.widget.TimePickerView;
import com.xuexiang.xui.widget.picker.widget.builder.OptionsPickerBuilder;
import com.xuexiang.xui.widget.picker.widget.builder.TimePickerBuilder;
import com.xuexiang.xui.widget.picker.widget.listener.OnOptionsSelectListener;
import com.xuexiang.xui.widget.picker.widget.listener.OnTimeSelectListener;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAABaseItemBean;
import com.yyt.trackcar.bean.AAABaseResponseBean;
import com.yyt.trackcar.bean.AAARequestBean;
import com.yyt.trackcar.bean.DeviceRaceconfig;
import com.yyt.trackcar.dbflow.AAADeviceModel;
import com.yyt.trackcar.dbflow.AAAUserModel;
import com.yyt.trackcar.ui.adapter.ArrowValueAdapter;
import com.yyt.trackcar.ui.adapter.DeviceSelectorAdapter;
import com.yyt.trackcar.ui.adapter.DeviceSettingItemAdapter;
import com.yyt.trackcar.ui.base.BaseActivity;
import com.yyt.trackcar.utils.AAADataUtils;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.DataUtils;
import com.yyt.trackcar.utils.DialogUtils;
import com.yyt.trackcar.utils.ErrorCode;
import com.yyt.trackcar.utils.TConstant;
import com.yyt.trackcar.utils.TimeUtils;
import com.yyt.trackcar.utils.ViewDataUtils;
import com.yyt.trackcar.utils.XToastUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import cn.bingoogolapple.baseadapter.BGAOnRVItemClickListener;
//import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * @ projectName:
 * @ packageName:   com.yyt.trackcar.ui.activity
 * @ fileName:      DeviceSettingActivity
 * @ author:        QING
 * @ createTime:    6/22/21 21:42
 * @ describe:      TODO
 */
@SuppressLint("NonConstantResourceId")
public class DeviceSettingActivity extends BaseActivity implements View.OnClickListener,
        BGAOnRVItemClickListener {
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView; // RecyclerView
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.fl_back)
    FrameLayout flBack;
    @BindView(R.id.tv_confirm_change)
    TextView tvConfirmChange;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.right_side_menu) // 右侧设备列表
    RecyclerView rightMenu;
    @BindView(R.id.tvCancel) // 右侧列表取消按钮
    TextView tvCancel;
    @BindView(R.id.tvConfirm) // 右侧列表确定按钮
    TextView tvConfirm;
    @BindView(R.id.selectAll)
    CheckBox selectAll;

    private ArrowValueAdapter mAdapter; // 适配器
    private List<AAABaseItemBean> mItemList = new ArrayList<>(); // 用户信息列表
    private OptionsPickerView mOptionsPicker; // 选择器
    private AAADeviceModel mDeviceModel; // 设备对象
    private AAAUserModel mUserModel; //用户对象
    private List<String> mTimeIntervalList = new ArrayList<>();
    private List<String> mAlarmSpeedList = new ArrayList<>();
    private List<String> mMovementList = new ArrayList<>();
    private List<String> mTimeZoneList = new ArrayList<>();
    private List<String> mAngleList = new ArrayList<>();
    private List<String> mOdometerList = new ArrayList<>();
    private List<String> mAuthNoList = new ArrayList<>();
    private List<String> mTakePhotoList = new ArrayList<>();
    private final List<AAADeviceModel> deviceModels = new ArrayList<>();
    private View mHeaderView; // 头布局

    private DeviceRaceconfig mConfiguration;  //全局配置对象
    private TimePickerView mTimePickerView;
    private DeviceSelectorAdapter deviceSelectorAdapter;

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss",
            Locale.US);
    private SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("HH:mm", Locale.US);
    private SimpleDateFormat simpleDateFormat3 = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
    private boolean isChanged = false; //用户是否修改(提交)参数
    private DeviceSettingItemAdapter adapter;
    private int deviceType;
    /**
     * 从何处进入的标识符  1、首页  2、搜索设备  3、赛事管理
     */
    private int original;
    private String title;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initTitle();
        initAdapters();
        initRecyclerViews();
        switchVisual();
        initListener();
        initHeaderView();
    }

    private void initTitle() {
        if (title == null)
            return;
//        tvTitle.setText(title);
        tvTitle.setText(String.format("%s%s", getString(R.string.pet_real_time), title));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_device_setting;
    }

    private void initData() {
        mUserModel = getTrackUserModel();
//        simpleDateFormat2.setTimeZone(TimeZone.getTimeZone("UTC"));
//        simpleDateFormat4.setTimeZone(TimeZone.getTimeZone("UTC"));
        AAADeviceModel currentDevice = getTrackDevice();
        for (AAADeviceModel item : getTrackDeviceList()) {
            if (DataUtils.isPigeonDevice(item.getDeviceImei())) {
                item.setSelected(item.getDeviceImei().equals(currentDevice.getDeviceImei()));
                deviceModels.add(item);
            }
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            title = bundle.getString(TConstant.TITLE);
            original = bundle.getInt(TConstant.TYPE);
            if (original == 1) {
                mDeviceModel = getTrackDevice();
                deviceType = mDeviceModel.getDeviceType();
            } else if (original == 2) {
                mDeviceModel = mGson.fromJson(bundle.getString("deviceModel"),
                        AAADeviceModel.class);
                deviceType = mDeviceModel.getDeviceType();
            } else if (original == 3) {
                deviceType = 2;
                mConfiguration = bundle.getParcelable(TConstant.CONFIGURATION);
            }
        }
    }

    private void switchVisual() {
        if (original == 1 || original == 2) {  //首页进来的设备类型有多种，需要逐一判断显示内容
            if (deviceType == 1)
                initCarDeviceItems();
            else if (deviceType == 2) {
                getDeviceConfiguration();
                tvConfirmChange.setVisibility(View.VISIBLE);
            } else if (deviceType == 3) {
                finish();
            }
        } else if (original == 3) {  //从鸽子比赛管理进来的只有鸽环设备类型
            tvConfirmChange.setVisibility(View.VISIBLE);
            initPigeonDeviceItem();
        }
    }

    private void initListener() {
        flBack.setOnClickListener(mNavigationOnClickListener);

        tvConfirmChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mConfiguration == null)
                    return;
//                if (mConfiguration.getRaceStatus() == 1 && original != 3) {
//                    showMessage(R.string.configuration_cannot_be_modified_in_race_mode_prompt);
//                    return;
//                }
                if (original == 3) {
                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(DeviceSettingActivity.this);
                    builder.setTitle(R.string.prompt)
                            .setMessage(String.format(getString(R.string.send_order_tips),
                                    getString(R.string.update)))
                            .setPositiveButton(R.string.confirm,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface,
                                                            int i) {
                                            showDialog();
                                            CarGpsRequestUtils.updatePigeonCompetitionConfiguration(mUserModel, mConfiguration, mHandler);
                                        }
                                    }).setNegativeButton(R.string.cancel,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface,
                                                            int i) {

                                        }
                                    }).show();
                } else {
                    drawerLayout.openDrawer(Gravity.END);
                }
            }
        });

        if (deviceType == 2) {
            tvCancel.setOnClickListener(view -> {
                drawerLayout.closeDrawer(Gravity.END);
            });

            tvConfirm.setOnClickListener(view -> {
                drawerLayout.closeDrawer(Gravity.END);
                if (original == 1 || original == 2) {
                    StringBuilder parameter = null;
                    for (AAADeviceModel item : deviceModels) {
                        if (item.isSelected()) {
                            if (parameter == null) {
                                parameter = new StringBuilder(item.getDeviceImei());
                            } else {
                                parameter.append(",").append(item.getDeviceImei());
                            }
                        }
                    }
                    if (parameter != null)
                        CarGpsRequestUtils.updateMultipleDevicesConfiguration(mUserModel,
                                mConfiguration, parameter.toString(), mHandler);
                    else
                        showMessage(R.string.please_select_at_least_one_device_to_apply_this_configuration);
                }
            });

            selectAll.setOnClickListener(v -> {
                boolean isChecked = selectAll.isChecked();
                for (int i = 0; i < deviceModels.size(); i++) {
                    if (deviceModels.get(i).isSelected() ^ isChecked) {
                        deviceModels.get(i).setSelected(isChecked);
                    }
                }
                deviceSelectorAdapter.notifyDataSetChanged();
            });
        }

    }

    private void initAdapters() {
        if (deviceType != 2) {
            mAdapter = new ArrowValueAdapter(mRecyclerView);
            mAdapter.setData(mItemList);
            mAdapter.setOnRVItemClickListener(this);
        } else {
            adapter = new DeviceSettingItemAdapter(mItemList);
            adapter.setOnItemClickListener(onItemClickListener);

            deviceSelectorAdapter = new DeviceSelectorAdapter(deviceModels);
            TextView headerView = new TextView(this);
            headerView.setMinimumHeight(40);
            headerView.setMinimumWidth(200);
            headerView.setPadding(2, 2, 2, 2);
            headerView.setBackgroundColor(getResources().getColor(R.color.searchBarCursorColor));
            headerView.setText(R.string.select_which_device_update);
            deviceSelectorAdapter.addHeaderView(headerView); // 添加了headerView后数据的position往后偏移一位
            deviceSelectorAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    if (position >= 0 && position < mItemList.size()) {
                        AAADeviceModel model = deviceModels.get(position);
                        model.setSelected(!model.isSelected());
                        adapter.notifyItemChanged(position);
                        checkDeviceSelectStatus();
                    }
                }
            });
        }
    }

    private void initRecyclerViews() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (deviceType == 2) {
            mRecyclerView.setAdapter(adapter);
            rightMenu.setLayoutManager(new LinearLayoutManager(this));
            rightMenu.setAdapter(deviceSelectorAdapter);
        } else {
            mRecyclerView.setAdapter(mAdapter.getHeaderAndFooterAdapter());
        }
    }

    /**
     * 初始化头布局
     */
    private void initHeaderView() {
        if (adapter != null) {
            mHeaderView = getLayoutInflater().inflate(R.layout.header_view_device_info,
                    mRecyclerView, false);
            mHeaderView.findViewById(R.id.clBtn).setVisibility(View.VISIBLE);
            mHeaderView.findViewById(R.id.ibRefresh).setVisibility(View.VISIBLE);
            mHeaderView.findViewById(R.id.ibRefresh).setOnClickListener(this);
            ViewDataUtils.initDeviceInfoView(this, mHeaderView, mDeviceModel);
            adapter.addHeaderView(mHeaderView);
        }
    }

    private void initCarDeviceItems() {
        mItemList.add(new AAABaseItemBean(111, getString(R.string.device_setting_type_twelfth),
                false));
        mItemList.add(new AAABaseItemBean(100, getString(R.string.device_setting_type_first),
                false));
        mItemList.add(new AAABaseItemBean(101, getString(R.string.device_setting_type_second),
                false));
        mItemList.add(new AAABaseItemBean(102, getString(R.string.device_setting_type_third),
                false));
//        mItemList.add(new AAABaseItemBean(1103, getString(R.string.device_setting_type_fourth),
//                false));
//        mItemList.add(new AAABaseItemBean(1104, getString(R.string.device_setting_type_fifth),
//        false));
        mItemList.add(new AAABaseItemBean(105, getString(R.string.device_setting_type_sixth),
                false));
        mItemList.add(new AAABaseItemBean(106, getString(R.string.device_setting_type_seventh),
                false));
        mItemList.add(new AAABaseItemBean(107, getString(R.string.device_setting_type_eightth),
                false));
        mItemList.add(new AAABaseItemBean(108, getString(R.string.device_setting_type_ninth),
                false));
//        mItemList.add(new AAABaseItemBean(109, getString(R.string.device_setting_type_tenth),
//        false));
//        mItemList.add(new AAABaseItemBean(110, getString(R.string.device_setting_type_eleventh),
//                false));
//            mItemList.add(new AAABaseItemBean(214, getString(R.string
//            .Continue_the_last_race_data), false));
//            mItemList.add(new AAABaseItemBean(215, getString(R.string.clear_the_last_race_data)
//            , false));
//            mItemList.add(new AAABaseItemBean(216, getString(R.string.race_number), false));
//            mItemList.add(new AAABaseItemBean(217, getString(R.string
//            .distribution_time_of_configuration_data), false));
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initPigeonDeviceItem() {
        mItemList.clear();
        mItemList.add(new AAABaseItemBean(218, getString(R.string.standby_time),
                mConfiguration.getStandbyDatetime() == null ? "" :
                        simpleDateFormat.format(mConfiguration.getStandbyDatetime())));
        mItemList.add(new AAABaseItemBean(200,
                getString(R.string.configuration_data_modification_time),
                mConfiguration.getCst() == null ? "" :
                        simpleDateFormat.format(mConfiguration.getCst())));
        mItemList.add(new AAABaseItemBean(201, getString(R.string.scheduled_start_time),
                mConfiguration.getRsut() == null ? "" :
                        simpleDateFormat.format(mConfiguration.getRsut())));
        mItemList.add(new AAABaseItemBean(202, getString(R.string.positioning_frequency),
                String.format("%s%s", mConfiguration.getRgli() == null ? "0" :
                        mConfiguration.getRgli(), getString(R.string.position_frequency_unit))));
        mItemList.add(new AAABaseItemBean(203, getString(R.string.upload_frequency),
                String.format("%s%s", mConfiguration.getRdui() == null ? "0" :
                        mConfiguration.getRdui(), getString(R.string.position_frequency_unit))));
        mItemList.add(new AAABaseItemBean(205, getString(R.string.low_battery_threshold),
                String.format("%s%%", mConfiguration.getLpl() == null ? "0" :
                        mConfiguration.getLpl())));
        mItemList.add(new AAABaseItemBean(212,
                getString(R.string.positioning_frequency_in_low_battery_mode), String.format("%s" +
                        "%s", mConfiguration.getLpgli() == null ? "0" : mConfiguration.getLpgli(),
                getString(R.string.position_frequency_unit))));
        mItemList.add(new AAABaseItemBean(213,
                getString(R.string.data_upload_frequency_in_low_battery_mode), String.format("%s" +
                        "%s", mConfiguration.getLpdui() == null ? "0" : mConfiguration.getLpdui(),
                getString(R.string.position_frequency_unit))));
        mItemList.add(new AAABaseItemBean(206, getString(R.string.second_stage_start_time),
                mConfiguration.getNmst() == null ? "" :
                        simpleDateFormat2.format(mConfiguration.getNmst())));
        mItemList.add(new AAABaseItemBean(207, getString(R.string.second_stage_end_time),
                mConfiguration.getNmet() == null ? "" :
                        simpleDateFormat2.format(mConfiguration.getNmet())));
        mItemList.add(new AAABaseItemBean(208,
                getString(R.string.positioning_frequency_in_second_stage), String.format("%s%s",
                mConfiguration.getNgli() == null ? "0" : mConfiguration.getNgli(),
                getString(R.string.position_frequency_unit))));
        mItemList.add(new AAABaseItemBean(209,
                getString(R.string.data_upload_frequency_in_second_stage), String.format("%s%s",
                mConfiguration.getNdui() == null ? "0" : mConfiguration.getNdui(),
                getString(R.string.position_frequency_unit))));
        mItemList.add(new AAABaseItemBean(210,
                getString(R.string.positioning_frequency_in_third_stage), String.format("%s%s",
                mConfiguration.getCgli() == null ? "0" : mConfiguration.getCgli(),
                getString(R.string.position_frequency_unit))));
        mItemList.add(new AAABaseItemBean(211,
                getString(R.string.data_upload_frequency_in_third_stage), String.format("%s%s",
                mConfiguration.getCdui() == null ? "0" : mConfiguration.getCdui(),
                getString(R.string.position_frequency_unit))));
        mItemList.add(new AAABaseItemBean(204, getString(R.string.data_upload_start_time),
                mConfiguration.getRdut() == null ? "" :
                        simpleDateFormat3.format(mConfiguration.getRdut())));
        mItemList.add(new AAABaseItemBean(217,
                getString(R.string.start_downloading_ephemeris_time),
                mConfiguration.getGddt() == null ? "" :
                        simpleDateFormat.format(mConfiguration.getGddt())));
        mItemList.add(new AAABaseItemBean(219, getString(R.string.delay_start_time_of_game),
                String.format("%s%s", mConfiguration.getRsud() == null ? "0" :
                        mConfiguration.getRsud() / (60), getString(R.string.minute_new))));
        adapter.notifyDataSetChanged();
    }

    private void checkDeviceSelectStatus() {
        boolean allSelected = true;
        for (AAADeviceModel item : deviceModels) {
            if (!item.isSelected()) {
                allSelected = false;
                break;
            }
        }
        selectAll.setChecked(allSelected);
    }

    /**
     * 获取设备参数
     */
    private void getDeviceConfiguration() {
        if (!NetworkUtils.isNetworkAvailable()) {
            XToastUtils.toast(this, R.string.network_error_prompt);
            return;
        }
        AAAUserModel userModel = getTrackUserModel();
        if (userModel != null && mDeviceModel != null && !TextUtils.isEmpty(mDeviceModel.getDeviceImei())) {
            showDialog();
            CarGpsRequestUtils.getDeviceConfiguration(userModel, mDeviceModel.getDeviceImei(),
                    mHandler);
        }
    }

    private void setTimeIntervalForTracking(int interval) {
        if (!NetworkUtils.isNetworkAvailable()) {
            showMessage(R.string.network_error_prompt);
            return;
        }
        AAAUserModel userModel = getTrackUserModel();
        if (mDeviceModel != null) {
            showDialog();
            mLoadingDialog.setMessage(getString(R.string.requesting_tips));
            CarGpsRequestUtils.setTimeIntervalForTracking(userModel, mDeviceModel.getDeviceImei()
                    , interval, mHandler);
        }
    }

    private void setTimeZone(int timeZoneValue) {
        if (!NetworkUtils.isNetworkAvailable()) {
            showMessage(R.string.network_error_prompt);
            return;
        }
        AAAUserModel userModel = getTrackUserModel();
        if (mDeviceModel != null) {
            showDialog();
            mLoadingDialog.setMessage(getString(R.string.requesting_tips));
            CarGpsRequestUtils.setTimeZone(userModel, mDeviceModel.getDeviceImei()
                    , timeZoneValue, mHandler);
        }
    }

    private void setAngleForTracking(int angle) {
        if (!NetworkUtils.isNetworkAvailable()) {
            showMessage(R.string.network_error_prompt);
            return;
        }
        AAAUserModel userModel = getTrackUserModel();
        if (mDeviceModel != null) {
            showDialog();
            mLoadingDialog.setMessage(getString(R.string.requesting_tips));
            CarGpsRequestUtils.setAngleForTracking(userModel, mDeviceModel.getDeviceImei()
                    , angle, mHandler);
        }
    }

    private void setOdometerInterval(int distance) {
        if (!NetworkUtils.isNetworkAvailable()) {
            showMessage(R.string.network_error_prompt);
            return;
        }
        AAAUserModel userModel = getTrackUserModel();
        if (mDeviceModel != null) {
            showDialog();
            mLoadingDialog.setMessage(getString(R.string.requesting_tips));
            CarGpsRequestUtils.setOdometerInterval(userModel, mDeviceModel.getDeviceImei()
                    , distance, mHandler);
        }
    }

    private void setTelePhoneForWiretapping(String phoneNumber) {
        if (!NetworkUtils.isNetworkAvailable()) {
            showMessage(R.string.network_error_prompt);
            return;
        }
        AAAUserModel userModel = getTrackUserModel();
        if (mDeviceModel != null) {
            showDialog();
            mLoadingDialog.setMessage(getString(R.string.requesting_tips));
            CarGpsRequestUtils.setTelePhoneForWiretapping(userModel, mDeviceModel.getDeviceImei()
                    , phoneNumber, mHandler);
        }
    }

    private void setSpeedLimit(int speed) {
        if (!NetworkUtils.isNetworkAvailable()) {
            showMessage(R.string.network_error_prompt);
            return;
        }
        AAAUserModel userModel = getTrackUserModel();
        if (mDeviceModel != null) {
            showDialog();
            mLoadingDialog.setMessage(getString(R.string.requesting_tips));
            CarGpsRequestUtils.setSpeedLimit(userModel, mDeviceModel.getDeviceImei()
                    , speed, mHandler);
        }
    }

    private void setMovementAlert(int meters) {
        if (!NetworkUtils.isNetworkAvailable()) {
            showMessage(R.string.network_error_prompt);
            return;
        }
        AAAUserModel userModel = getTrackUserModel();
        if (mDeviceModel != null) {
            showDialog();
            mLoadingDialog.setMessage(getString(R.string.requesting_tips));
            CarGpsRequestUtils.setMovementAlert(userModel, mDeviceModel.getDeviceImei()
                    , meters, mHandler);
        }
    }

    private void setAuthorizedPhoneNumber(int buttonNumber, String phoneNumberForReceiveSms,
                                          String phoneNumberForReceiveCall) {
        if (!NetworkUtils.isNetworkAvailable()) {
            showMessage(R.string.network_error_prompt);
            return;
        }
        AAAUserModel userModel = getTrackUserModel();
        if (mDeviceModel != null) {
            showDialog();
            mLoadingDialog.setMessage(getString(R.string.requesting_tips));
            CarGpsRequestUtils.setAuthorizedPhoneNumber(userModel, mDeviceModel.getDeviceImei()
                    , buttonNumber, phoneNumberForReceiveSms, phoneNumberForReceiveCall, mHandler);
        }
    }

    private void showTimeIntervalDialog(AAABaseItemBean itemBean) {
        View rootView = getLayoutInflater().inflate(R.layout.layout_dialog_input, null);
        TextView tvUnit = rootView.findViewById(R.id.tvUnit);
        EditText etInput = rootView.findViewById(R.id.etInput);
        etInput.setText("6");
        tvUnit.setText(String.format("*10 %s", getString(R.string.second_new)));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog alertDialog = builder.create();

        builder.setView(rootView)
                .setTitle(itemBean.getTitle())
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String inputValue = etInput.getText().toString();
                        if (!TextUtils.isEmpty(inputValue)) {
                            long valueNum = 6;
                            try {
                                valueNum = Long.parseLong(inputValue);
                            } catch (Exception e) {
                                if (BuildConfig.DEBUG)
                                    e.printStackTrace();
                            }
                            if (valueNum > 65535)
                                valueNum = 65535;
                            else if (valueNum < 0)
                                valueNum = 0;
                            alertDialog.cancel();
                            setTimeIntervalForTracking((int) valueNum);
                        }
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.cancel();
                    }
                }).show();

//        new SweetAlertDialog(this, SweetAlertDialog
//                .NORMAL_TYPE).setTitleText(itemBean.getTitle())
//                .setCustomView(rootView).setCancelText(getString(R
//                .string.cancel)).showCancelButton(true)
//                .setConfirmButton(R.string.confirm, new SweetAlertDialog
//                        .OnSweetClickListener() {
//                    @Override
//                    public void onClick(SweetAlertDialog sweetAlertDialog) {
//                        String inputValue = etInput.getText().toString();
//                        if (!TextUtils.isEmpty(inputValue)) {
//                            long valueNum = 6;
//                            try {
//                                valueNum = Long.parseLong(inputValue);
//                            } catch (Exception e) {
//                                if (BuildConfig.DEBUG)
//                                    e.printStackTrace();
//                            }
//                            if (valueNum > 65535)
//                                valueNum = 65535;
//                            else if (valueNum < 0)
//                                valueNum = 0;
//                            sweetAlertDialog.dismissWithAnimation();
//                            setTimeIntervalForTracking((int) valueNum);
//                        }
//                    }
//                }).show();
    }

    private void showAlarmSpeedDialog(AAABaseItemBean itemBean) {
        if (mOptionsPicker == null || !mOptionsPicker.isShowing()) {
            if (mAlarmSpeedList.size() == 0)
                AAADataUtils.getAlarmSpeedList(this, mAlarmSpeedList);
            mOptionsPicker = new OptionsPickerBuilder(this,
                    new OnOptionsSelectListener() {
                        @Override
                        public void onOptionsSelect(int options1, int options2, int options3,
                                                    View v) {
                            setSpeedLimit(options1);
                        }
                    })
                    .setTitleText(itemBean.getTitle())
                    .setSubmitText(getString(R.string.confirm))
                    .setCancelText(getString(R.string.cancel))
                    .setSelectOptions(0)
                    .build();
            mOptionsPicker.setPicker(mAlarmSpeedList);
            mOptionsPicker.show();
        }
    }

    private void showMovementDialog(AAABaseItemBean itemBean) {
        if (mOptionsPicker == null || !mOptionsPicker.isShowing()) {
            if (mMovementList.size() == 0)
                AAADataUtils.getMovementList(this, mMovementList);
            mOptionsPicker = new OptionsPickerBuilder(this,
                    new OnOptionsSelectListener() {
                        @Override
                        public void onOptionsSelect(int options1, int options2, int options3,
                                                    View v) {
                            setMovementAlert(options1);
                        }
                    })
                    .setTitleText(itemBean.getTitle())
                    .setSubmitText(getString(R.string.confirm))
                    .setCancelText(getString(R.string.cancel))
                    .setSelectOptions(0)
                    .build();
            mOptionsPicker.setPicker(mMovementList);
            mOptionsPicker.show();
        }
    }

    private void showTimeZoneDialog(AAABaseItemBean itemBean) {
        View rootView = getLayoutInflater().inflate(R.layout.layout_dialog_input, null);
        TextView tvUnit = rootView.findViewById(R.id.tvUnit);
        EditText etInput = rootView.findViewById(R.id.etInput);
        etInput.setText("0");
        tvUnit.setText(R.string.minute_new);
        etInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog alertDialog = builder.create();

        builder.setView(rootView)
                .setTitle(itemBean.getTitle())
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String inputValue = etInput.getText().toString();
                        if (!TextUtils.isEmpty(inputValue)) {
                            long valueNum = 0;
                            try {
                                valueNum = Long.parseLong(inputValue);
                            } catch (Exception e) {
                                if (BuildConfig.DEBUG)
                                    e.printStackTrace();
                            }
                            if (valueNum > 720)
                                valueNum = 720;
                            else if (valueNum < -720)
                                valueNum = -720;
                            alertDialog.cancel();
                            setTimeZone((int) valueNum);
                        }
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.cancel();
                    }
                }).show();

//        new SweetAlertDialog(this, SweetAlertDialog
//                .NORMAL_TYPE).setTitleText(itemBean.getTitle())
//                .setCustomView(rootView).setCancelText(getString(R
//                .string.cancel)).showCancelButton(true)
//                .setConfirmButton(R.string.confirm, new SweetAlertDialog
//                        .OnSweetClickListener() {
//                    @Override
//                    public void onClick(SweetAlertDialog sweetAlertDialog) {
//                        String inputValue = etInput.getText().toString();
//                        if (!TextUtils.isEmpty(inputValue)) {
//                            long valueNum = 0;
//                            try {
//                                valueNum = Long.parseLong(inputValue);
//                            } catch (Exception e) {
//                                if (BuildConfig.DEBUG)
//                                    e.printStackTrace();
//                            }
//                            if (valueNum > 720)
//                                valueNum = 720;
//                            else if (valueNum < -720)
//                                valueNum = -720;
//                            sweetAlertDialog.dismissWithAnimation();
//                            setTimeZone((int) valueNum);
//                        }
//                    }
//                }).show();
    }

    /**
     * 标题栏Navigation点击监听器
     */
    private final View.OnClickListener mNavigationOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!isChanged)
                finish();
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(DeviceSettingActivity.this);
                builder.setTitle(R.string.prompt)
                        .setMessage(R.string.discard_modification_prompt)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        }).setNegativeButton(R.string.cancel,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                }).show();
            }
        }
    };

    private void showAngleDialog(AAABaseItemBean itemBean) {
        View rootView = getLayoutInflater().inflate(R.layout.layout_dialog_input, null);
        TextView tvUnit = rootView.findViewById(R.id.tvUnit);
        EditText etInput = rootView.findViewById(R.id.etInput);
        etInput.setText("0");
        tvUnit.setText("°");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog alertDialog = builder.create();

        builder.setView(rootView)
                .setTitle(itemBean.getTitle())
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String inputValue = etInput.getText().toString();
                        if (!TextUtils.isEmpty(inputValue)) {
                            long valueNum = 0;
                            try {
                                valueNum = Long.parseLong(inputValue);
                            } catch (Exception e) {
                                if (BuildConfig.DEBUG)
                                    e.printStackTrace();
                            }
                            if (valueNum > 359)
                                valueNum = 359;
                            else if (valueNum < 0)
                                valueNum = 0;
                            alertDialog.cancel();
                            setAngleForTracking((int) valueNum);
                        }
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.cancel();
                    }
                }).show();

//        new SweetAlertDialog(this, SweetAlertDialog
//                .NORMAL_TYPE).setTitleText(itemBean.getTitle())
//                .setCustomView(rootView).setCancelText(getString(R
//                .string.cancel)).showCancelButton(true)
//                .setConfirmButton(R.string.confirm, new SweetAlertDialog
//                        .OnSweetClickListener() {
//                    @Override
//                    public void onClick(SweetAlertDialog sweetAlertDialog) {
//                        String inputValue = etInput.getText().toString();
//                        if (!TextUtils.isEmpty(inputValue)) {
//                            long valueNum = 0;
//                            try {
//                                valueNum = Long.parseLong(inputValue);
//                            } catch (Exception e) {
//                                if (BuildConfig.DEBUG)
//                                    e.printStackTrace();
//                            }
//                            if (valueNum > 359)
//                                valueNum = 359;
//                            else if (valueNum < 0)
//                                valueNum = 0;
//                            sweetAlertDialog.dismissWithAnimation();
//                            setAngleForTracking((int) valueNum);
//                        }
//                    }
//                }).show();
    }

    private void showOdometerDialog(AAABaseItemBean itemBean) {
        View rootView = getLayoutInflater().inflate(R.layout.layout_dialog_input, null);
        TextView tvUnit = rootView.findViewById(R.id.tvUnit);
        EditText etInput = rootView.findViewById(R.id.etInput);
        etInput.setText("0");
        tvUnit.setText(R.string.unit_metre);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog alertDialog = builder.create();
        builder.setView(rootView)
                .setTitle(itemBean.getTitle())
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String inputValue = etInput.getText().toString();
                        if (!TextUtils.isEmpty(inputValue)) {
                            long valueNum = 0;
                            try {
                                valueNum = Long.parseLong(inputValue);
                            } catch (Exception e) {
                                if (BuildConfig.DEBUG)
                                    e.printStackTrace();
                            }
                            if (valueNum > 65535)
                                valueNum = 65535;
                            else if (valueNum < 0)
                                valueNum = 0;
                            alertDialog.cancel();
                            setOdometerInterval((int) valueNum);
                        }
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.cancel();
                    }
                }).show();

//        new SweetAlertDialog(this, SweetAlertDialog
//                .NORMAL_TYPE).setTitleText(itemBean.getTitle())
//                .setCustomView(rootView).setCancelText(getString(R
//                .string.cancel)).showCancelButton(true)
//                .setConfirmButton(R.string.confirm, new SweetAlertDialog
//                        .OnSweetClickListener() {
//                    @Override
//                    public void onClick(SweetAlertDialog sweetAlertDialog) {
//                        String inputValue = etInput.getText().toString();
//                        if (!TextUtils.isEmpty(inputValue)) {
//                            long valueNum = 0;
//                            try {
//                                valueNum = Long.parseLong(inputValue);
//                            } catch (Exception e) {
//                                if (BuildConfig.DEBUG)
//                                    e.printStackTrace();
//                            }
//                            if (valueNum > 65535)
//                                valueNum = 65535;
//                            else if (valueNum < 0)
//                                valueNum = 0;
//                            sweetAlertDialog.dismissWithAnimation();
//                            setOdometerInterval((int) valueNum);
//                        }
//                    }
//                }).show();
    }

    private void showAuthNoDialog(AAABaseItemBean itemBean) {
        if (mOptionsPicker == null || !mOptionsPicker.isShowing()) {
            if (mAuthNoList.size() == 0)
                AAADataUtils.getAuthNoList(this, mAuthNoList);
            mOptionsPicker = new OptionsPickerBuilder(this,
                    new OnOptionsSelectListener() {
                        @Override
                        public void onOptionsSelect(int options1, int options2, int options3,
                                                    View v) {
                            Intent intent = new Intent(DeviceSettingActivity.this,
                                    AuthPhoneNumberActivity.class);
                            intent.putExtra(TConstant.TYPE, options1 + 1);
                            startActivityForResult(intent, TConstant.REQUEST_INPUT_AUTH);
                        }
                    })
                    .setTitleText(itemBean.getTitle())
                    .setSubmitText(getString(R.string.confirm))
                    .setCancelText(getString(R.string.cancel))
                    .setSelectOptions(0)
                    .build();
            mOptionsPicker.setPicker(mAuthNoList);
            mOptionsPicker.show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibRefresh: // 刷新
                getDeviceConfiguration();
                break;
            default:
                break;
        }
    }

    @Override
    public void onRVItemClick(ViewGroup parent, View itemView, int position) {
        if (position >= 0 && position < mItemList.size()) {
            AAABaseItemBean itemBean = mItemList.get(position);
            if (!("1".equals(itemBean.getGroup()) || "2".equals(itemBean.getGroup()))) {
                Intent intent;
                switch (itemBean.getType()) {
                    case 111:
                        showTimeIntervalDialog(itemBean);
                        break;
                    case 100:
                        showAuthNoDialog(itemBean);
                        break;
                    case 101:
                        showAlarmSpeedDialog(itemBean);
                        break;
                    case 102:
                        showMovementDialog(itemBean);
                        break;
                    case 105:
                        intent = new Intent(this, InputActivity.class);
                        intent.putExtra(TConstant.TYPE, 2);
                        startActivityForResult(intent, TConstant.REQUEST_INPUT);
                        break;
                    case 106:
                        showTimeZoneDialog(itemBean);
                        break;
                    case 107:
                        showAngleDialog(itemBean);
                        break;
                    case 108:
                        showOdometerDialog(itemBean);
                        break;
//                    case 109:
//                        showTakePhotoDialog(itemBean);
//                        break;
                    case 110:
                        startActivityForResult(TConstant.REQUEST_INPUT_RFID,
                                RFIDNumberActivity.class);
                        break;
                    default:
                        mMaterialDialog = DialogUtils.customMaterialDialog(this, mMaterialDialog,
                                getString(R.string.tips), getString(R.string.send_order_tips,
                                        mItemList.get(position).getTitle()), getString(R.string
                                        .confirm), getString(R.string.cancel),
                                mItemList.get(position).getType(), mHandler);
                        break;
                }
            }
        }
    }

    private final BaseQuickAdapter.OnItemClickListener onItemClickListener =
            new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//            if (mConfiguration.getRaceStatus() == 1 && original != 3) {
//                showMessage(R.string.configuration_cannot_be_modified_in_race_mode_prompt);
//                return;
//            }
                    if (position >= 0 && position < mItemList.size()) {
                        AAABaseItemBean itemBean = mItemList.get(position);
                        switch (itemBean.getType()) {
                            case 200: //配置数据修改时间
                                break;
                            case 201:  //进入工作状态时间
                                ActionSheet.createBuilder(DeviceSettingActivity.this,
                                                getSupportFragmentManager())
                                        .setCancelButtonTitle(R.string.cancel)
                                        .setOtherButtonTitles(getString(R.string.set_value_to_empty),
                                                getString(R.string.make_modification))
                                        .setListener(new ActionSheet.ActionSheetListener() {
                                            @Override
                                            public void onDismiss(ActionSheet actionSheet,
                                                                  boolean isCancel) {

                                            }

                                            @Override
                                            public void onOtherButtonClick(ActionSheet actionSheet,
                                                                           int index) {
                                                int position;
                                                for (position = 0; position < mItemList.size(); position++) {
                                                    if (mItemList.get(position).getType() == 201) {
                                                        break;
                                                    }
                                                }
                                                if (index == 0) {
                                                    mItemList.get(position).setContent("");
                                                    mConfiguration.setRsut(null);
                                                    adapter.notifyDataSetChanged();
                                                    isChanged = true;
                                                } else {
                                                    showDateTimePickView(201,
                                                            getString(R.string.time), 1
                                                            , mConfiguration.getRsut() == null ? 0 :
                                                                    mConfiguration.getRsut(),
                                                            position);
                                                }
                                            }
                                        }).show();
                                break;
                            case 202: //定位频率
                                showPositionFrequencyAlertDialog(0,
                                        getString(R.string.positioning_frequency),
                                        mConfiguration.getRgli() == null ? 0 :
                                                mConfiguration.getRgli(),
                                        position);
                                break;
                            case 203: //上传频率
                                showPositionFrequencyAlertDialog(1,
                                        getString(R.string.upload_frequency),
                                        mConfiguration.getRdui() == null ? 0 :
                                                mConfiguration.getRdui(),
                                        position);
                                break;
                            case 204: //数据上传启动时间
                                showDateTimePickView(204,
                                        getString(R.string.data_upload_start_time), 3,
                                        mConfiguration.getRdut() == null ? 0 :
                                                mConfiguration.getRdut(), position);
                                break;
                            case 205: //低电量门限
                                showLowBatteryThresholdOptionsPickerDialog(getString(R.string.low_battery_threshold), mConfiguration.getLpl(), position);
//                        showLowBatteryThreshold(getString(R.string.low_battery_threshold),
//                        configuration.getLpl(),position);
                                break;
                            case 206: //夜间模式开启时间
                                showDateTimePickView(206,
                                        getString(R.string.second_stage_start_time), 2,
                                        mConfiguration.getNmst() == null ? 0 :
                                                mConfiguration.getNmst(), position);
                                break;
                            case 207: //夜间模式结束时间
                                showDateTimePickView(207,
                                        getString(R.string.second_stage_end_time), 2,
                                        mConfiguration.getNmet() == null ? 0 :
                                                mConfiguration.getNmet(), position);
                                break;
                            case 208: //夜间模式定位频率　
                                showPositionFrequencyAlertDialog(2,
                                        getString(R.string.positioning_frequency_in_second_stage),
                                        mConfiguration.getNgli() == null ? 0 :
                                                mConfiguration.getNgli(),
                                        position);
                                break;
                            case 209: //夜间模式数据上传频率
                                showPositionFrequencyAlertDialog(3,
                                        getString(R.string.data_upload_frequency_in_second_stage),
                                        mConfiguration.getNdui() == null ? 0 :
                                                mConfiguration.getNdui(),
                                        position);
                                break;
                            case 210: //续飞模式定位频率
                                showPositionFrequencyAlertDialog(4,
                                        getString(R.string.positioning_frequency_in_third_stage),
                                        mConfiguration.getCgli() == null ? 0 :
                                                mConfiguration.getCgli(),
                                        position);
                                break;
                            case 211: //续飞模式数据上传频率
                                showPositionFrequencyAlertDialog(5,
                                        getString(R.string.data_upload_frequency_in_third_stage),
                                        mConfiguration.getCdui() == null ? 0 :
                                                mConfiguration.getCdui(),
                                        position);
                                break;
                            case 212: //低电量模式定位频率
                                showPositionFrequencyAlertDialog(6,
                                        getString(R.string.positioning_frequency_in_low_battery_mode),
                                        mConfiguration.getLpgli() == null ? 0 :
                                                mConfiguration.getLpgli()
                                        , position);
                                break;
                            case 213: //低电量模式上传频率
                                showPositionFrequencyAlertDialog(7,
                                        getString(R.string.data_upload_frequency_in_low_battery_mode),
                                        mConfiguration.getLpdui() == null ? 0 :
                                                mConfiguration.getLpdui()
                                        , position);
                                break;
                            case 214: //续传上次比赛数据
                                break;
                            case 215: //清除上次比赛数据
                                break;
                            case 216://比赛编号
                                break;
                            case 217://开始下载星历时间
                                showDateTimePickView(217, getString(R.string.time), 1,
                                        (mConfiguration.getGddt() == null ? 0 :
                                                mConfiguration.getGddt()), position);
                                break;
                            case 218://开始待机时间
                                break;
                            case 219:
                                showTimeSettingDialog(1,
                                        getString(R.string.delay_start_time_of_game),
                                        mConfiguration.getRsud() == null ? 0 :
                                                (mConfiguration.getRsud() == null ? 0 :
                                                        mConfiguration.getRsud())
                                                        / (60), position);
                                break;
                        }
                    }
                }
            };

    /**
     * 显示时间选择器
     *
     * @param category 1:yyyy/MM/dd HH:mm 2:HH:mm
     * @param type     类型
     * @param title    标题z
     */
    private void showDateTimePickView(final int type, String title, int category, long dateValue,
                                      Integer position) {
        boolean[] standard1 = new boolean[]{true, true, true, true, true, false};
        boolean[] standard2 = new boolean[]{false, false, false, true, true, false};
        boolean[] standard3 = new boolean[]{true, true, true, false, false, false};
        List<boolean[]> list = new ArrayList<>();
        list.add(standard1);
        list.add(standard2);
        list.add(standard3);
        if (mTimePickerView == null || !mTimePickerView.isShowing()) {
            String str = null;
            Calendar selectedDate = Calendar.getInstance();
            Calendar startDate = Calendar.getInstance();
            Calendar endDate = Calendar.getInstance();
            Date nowDate = new Date();
            if (dateValue != 0) {
                str = simpleDateFormat.format(dateValue);
            } else {
                str = simpleDateFormat.format(nowDate);
            }
            Date date = TimeUtils.formatUTC(str, "yyyy/MM/dd HH:mm");
            startDate.set(nowDate.getYear() + 1900, 0, 1, 0, 0);
            endDate.set(nowDate.getYear() + 1901, 11, 31, 23, 59);
            selectedDate.set(date.getYear() + 1900, date.getMonth(), date.getDate()
                    , date.getHours(), date.getMinutes());
            if (category == 2) {
                selectedDate.set(nowDate.getYear() + 1900, nowDate.getMonth(), nowDate.getDate()
                        , date.getHours(), date.getMinutes());
            }
            mTimePickerView = new TimePickerBuilder(this, new OnTimeSelectListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onTimeSelected(Date date, View v) {//选中事件回调
                    isChanged = true;
                    try {
                        switch (type) {
                            case 217: //开始下载星历时间
                                mConfiguration.setGddt(date.getTime());
                                mItemList.get(position).setContent(simpleDateFormat.format(date.getTime()));
                                break;
                            case 201: //进入工作状态时间
                                mItemList.get(position).setContent(simpleDateFormat.format(date.getTime()));
                                mConfiguration.setRsut(date.getTime());
                                break;
                            case 204: //数据上传启动时间
                                mItemList.get(position).setContent(simpleDateFormat3.format(date.getTime()));
                                mConfiguration.setRdut(date.getTime());
                                break;
                            case 206: { //夜间模式开启时间
                                long time = date.getTime();
                                mItemList.get(position).setContent(simpleDateFormat2.format(time));
                                mConfiguration.setNmst(Math.abs(time));
                                break;
                            }
                            case 207: { //夜间模式结束时间
                                long time = date.getTime();
                                mItemList.get(position).setContent(simpleDateFormat2.format(time));
                                mConfiguration.setNmet(Math.abs(time));
                                break;
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).setType(list.get(category - 1))// 默认全部显示
                    .setCancelText(getString(R.string.cancel))//取消按钮文字
                    .setSubmitText(getString(R.string.confirm))//确认按钮文字
                    .setContentTextSize(15) //滚轮文字大小
                    .setTitleSize(20)//标题文字大小
                    .setTitleText(title)//标题文字
                    .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                    .isCyclic(false)//是否循环滚动
                    .setTitleColor(Color.BLACK)//标题文字颜色
//                    .setSubmitColor(Color.BLUE)//确定按钮文字颜色
//                    .setCancelColor(Color.BLUE)//取消按钮文字颜色
                    .setTitleBgColor(getResources().getColor(R.color.white))//标题背景颜色 Night mode
                    .setBgColor(getResources().getColor(R.color.layout_background))//滚轮背景颜色 Night
                    // mode
                    .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/
                    .setRangDate(startDate, endDate)//起始终止年月日设定
                    .setLabel(getString(R.string.year), getString(R.string.mouth), getString(R
                            .string.day), getString(R.string.hour_new), getString(R.string
                            .minute_new), getString(R.string.second_new))//默认设置为年月日时分秒
                    .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                    .isDialog(false)//是否显示为对话框样式
                    .build();
            mTimePickerView.show();
        }
    }

    /**
     * 设置定位频率
     *
     * @param title
     * @param type
     * @param defaultedValue
     */
    private void showPositionFrequencyAlertDialog(int type, String title, long defaultedValue,
                                                  int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View rootView = getLayoutInflater().inflate(R.layout.layout_dialog_input, null);
        TextView tvUnit = rootView.findViewById(R.id.tvUnit);
        EditText etInput = rootView.findViewById(R.id.etInput);
        etInput.setText(String.valueOf(defaultedValue));
        etInput.setSelection(String.valueOf(defaultedValue).length());
        KeyboardUtils.toggleSoftInput();
        tvUnit.setText(R.string.position_frequency_unit);
        builder.setTitle(title)
                .setView(rootView)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        KeyboardUtils.hideSoftInput(etInput);
                        long value = 0;
                        String str = etInput.getText().toString().trim();
                        if (str.equals("")) {
                            showMessage(String.format("%s%s", getString(R.string.frequency),
                                    getString(R.string.cannot_empty_prompt)));
                            return;
                        }
//                        else if (Integer.parseInt(str) == 1) {
//                            if (type == 0 || type == 2 || type == 4 || type == 6) { //定位频率过高限制
//                                showMessage(getString(R.string
//                                .position_frequency_too_high_prompt));
//                                return;
//                            }
//                        }
                        value = Long.parseLong(etInput.getText().toString().trim());
                        mItemList.get(position).setContent(String.format("%s%s", value,
                                getString(R.string.position_frequency_unit)));
                        adapter.notifyDataSetChanged();
                        isChanged = true;
                        switch (type) {
                            case 0://定位频率
                                mConfiguration.setRgli(value);
                                break;
                            case 1://上传频率
                                mConfiguration.setRdui(value);
                                break;
                            case 2://夜间模式定位频率　
                                mConfiguration.setNgli(value);
                                break;
                            case 3: //夜间模式数据上传频率
                                mConfiguration.setNdui(value);
                                break;
                            case 4: //续飞模式定位频率
                                mConfiguration.setCgli(value);
                                break;
                            case 5://续飞模式数据上传频率
                                mConfiguration.setCdui(value);
                                break;
                            case 6: //低电量模式定位频率
                                mConfiguration.setLpgli(value);
                                break;
                            case 7: //低电量模式数据上传频率
                                mConfiguration.setLpdui(value);
                                break;
                            case 8: //延迟开始比赛时间
                                mConfiguration.setRsud(value * 60);
                                break;
                        }
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        KeyboardUtils.hideSoftInput(etInput);
                    }
                }).show();
    }

    private void showTimeSettingDialog(int type, String title, long defaultedValue, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View rootView = getLayoutInflater().inflate(R.layout.layout_dialog_input, null);
        TextView tvUnit = rootView.findViewById(R.id.tvUnit);
        EditText etInput = rootView.findViewById(R.id.etInput);
        etInput.setText(String.valueOf(defaultedValue));
        etInput.setSelection(String.valueOf(defaultedValue).length());
        KeyboardUtils.toggleSoftInput();
        tvUnit.setText(R.string.minute_new);
        builder.setTitle(title)
                .setView(rootView)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        KeyboardUtils.hideSoftInput(etInput);
                        long value = Long.parseLong(etInput.getText().toString().trim());
                        mItemList.get(position).setContent(String.format("%s%s", value,
                                getString(R.string.minute_new)));
                        adapter.notifyDataSetChanged();
                        isChanged = true;
                        mConfiguration.setRsud(value * 60);
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        KeyboardUtils.hideSoftInput(etInput);
                    }
                }).show();
    }

    /**
     * 低电量门限设置
     *
     * @param title
     * @param defaultedValue
     * @param position
     */
    private void showLowBatteryThresholdOptionsPickerDialog(String title, long defaultedValue,
                                                            int position) {
        List<Integer> list = Arrays.asList(0, 25, 50, 75);
        int index = 0;
        for (Integer item :
                list) {
            if (defaultedValue == item) {
                break;
            }
            index++;
        }
        OptionsPickerView<Integer> optionsPickerView = new OptionsPickerBuilder(this,
                new OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int options2, int options3, View v) {
                        isChanged = true;
                        mItemList.get(position).setContent(list.get(options1) + "%");
                        mConfiguration.setLpl(list.get(options1).longValue());
                        adapter.notifyDataSetChanged();
                    }
                }).setTitleText(title)
                .setSelectOptions(index)
                .build();
        optionsPickerView.setPicker(list);
        optionsPickerView.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            String phoneNum;
            switch (requestCode) {
                case TConstant.REQUEST_INPUT:
                    phoneNum = data.getStringExtra(TConstant.BEAN);
                    if (!TextUtils.isEmpty(phoneNum))
                        setTelePhoneForWiretapping(phoneNum);
                    break;
                case TConstant.REQUEST_INPUT_AUTH:
                    phoneNum = data.getStringExtra(TConstant.BEAN);
                    if (!TextUtils.isEmpty(phoneNum)) {
                        try {
                            String[] array = phoneNum.split(",");
                            if (array.length == 3)
                                setAuthorizedPhoneNumber(Integer.parseInt(array[0]), array[1],
                                        array[2]);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
//                case TConstant.REQUEST_INPUT_RFID:
//                    phoneNum = data.getStringExtra(TConstant.BEAN);
//                    if (!TextUtils.isEmpty(phoneNum))
//                        sendCommand(4155, phoneNum, 110);
//                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 消息处理
     */
    private final Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            AAABaseResponseBean response;
            AAARequestBean requestBean;
            try {
                dismisDialog();
                if (msg.obj == null) {
                    showMessage(R.string.request_unkonow_prompt);
                    return false;
                }
                response = (AAABaseResponseBean) msg.obj;
                if (response.getCode() != TConstant.RESPONSE_SUCCESS) {
                    showMessage(ErrorCode.getResId(response.getCode()));
                    return false;
                }
                switch (msg.what) {
                    case TConstant.REQUEST_URL_SET_TIME_INTERVAL_FOR_TRACKING:
                    case TConstant.REQUEST_URL_SET_TIME_ZONE:
                    case TConstant.REQUEST_URL_SET_ANGLE_FOR_TRACKING:
                    case TConstant.REQUEST_URL_SET_ODOMETER_FOR_INTERVAL:
                    case TConstant.REQUEST_URL_SET_TELEPHONE_FOR_WIRETAPPING:
                    case TConstant.REQUEST_URL_SET_SPEED_LIMIT:
                    case TConstant.REQUEST_URL_SET_MOVEMENT_ALERT:
                    case TConstant.REQUEST_URL_SET_AUTHORIZED_PHONE_NUMBER:
                        showMessage(R.string.send_success_tips);
                        break;
//                    case Constant.HANDLE_CONFIRM_ACTION: // 确定
//                        switch (msg.arg1) {
//                            case 1103:
//                                sendOutputCommand(4114, msg.arg1);
//                                break;
//                            case 1104:
//                                sendOutputCommand(4114, msg.arg1);
//                                break;
//                            case 109:
//                                sendCommand(4151, msg.arg1);
//                                break;
//                            default:
//                                break;
//                        }
//                        break;
//                    case Constant.HANDLE_CANCEL_ACTION: // 取消
//
//                        break;
                    case TConstant.REQUEST_GET_DEVICE_RACE_CONFIGURATION:
                        List list1 = (ArrayList) response.getData();
                        if (list1.size() >= 1) {
                            List list2 = (ArrayList) list1.get(0);
                            if (list2.size() >= 1) {
                                mConfiguration = mGson.fromJson(mGson.toJson(list2.get(0)),
                                        DeviceRaceconfig.class);
                                if (mHeaderView != null) {
                                    TextView tvRaceMode = mHeaderView.findViewById(R.id.tvMode);
                                    tvRaceMode.setVisibility(View.VISIBLE);
                                    tvRaceMode.setText(mConfiguration.getRaceStatus() == 0 ?
                                            getString(R.string.private_mode) :
                                            getString(R.string.competition_mode));
                                }
                                isChanged = false;
                                initPigeonDeviceItem();
                            }
                        }
                        break;
                    case TConstant.REQUEST_UPDATE_DEVICE_RACE_CONFIGURATION:  //更新设备设置参数
                    case TConstant.REQUEST_UPDATE_PIGEON_COMPETITION_CONFIGURATION:  //更新比赛设置参数
                        if (response.getCode() == TConstant.RESPONSE_SUCCESS) {
                            showMessage(R.string.update_succeed_prompt);
                            isChanged = false;
                            if (mConfiguration.getRsut() != null && mConfiguration.getRsut() >= new Date().getTime()) {
                                AlertDialog.Builder builder =
                                        new AlertDialog.Builder(DeviceSettingActivity.this);
                                builder.setTitle(R.string.prompt)
                                        .setMessage(R.string.update_time_later_than_now)
                                        .setPositiveButton(R.string.confirm,
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface,
                                                                        int i) {

                                                    }
                                                }).show();
                            }
                        } else {
                            showMessage(R.string.update_failed_prompt);
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

}
