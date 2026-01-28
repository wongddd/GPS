package com.yyt.trackcar.ui.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.reflect.TypeToken;
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
import com.yyt.trackcar.bean.DeviceRaceconfig;
import com.yyt.trackcar.dbflow.AAADeviceModel;
import com.yyt.trackcar.dbflow.AAAUserModel;
import com.yyt.trackcar.ui.adapter.DeviceSelectorAdapter;
import com.yyt.trackcar.ui.adapter.DeviceSettingItemAdapter;
import com.yyt.trackcar.ui.base.BaseActivity;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.DataUtils;
import com.yyt.trackcar.utils.ErrorCode;
import com.yyt.trackcar.utils.TConstant;
import com.yyt.trackcar.utils.TimeUtils;
import com.yyt.trackcar.utils.ViewDataUtils;
import com.yyt.trackcar.utils.XToastUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;

/**
 * @ projectName:
 * @ packageName:   com.yyt.trackcar.ui.activity
 * @ fileName:      DeviceModelSettingActivity
 * @ author:        QING
 * @ createTime:    6/22/21 21:42
 * @ describe:      TODO
 */
@SuppressLint("NonConstantResourceId")
public class DeviceModelSettingActivity extends BaseActivity implements View.OnClickListener {
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

    private final List<AAABaseItemBean> mItemList = new ArrayList<>(); // 用户信息列表
    private List<DeviceRaceconfig> recordingModeList = new ArrayList<>(); // 获取到的配置列表
    private AAADeviceModel mDeviceModel; // 设备对象
    private AAAUserModel mUserModel; //用户对象
    private final List<AAADeviceModel> deviceModels = new ArrayList<>();

    private TimePickerView mTimePickerView;
    private DeviceSelectorAdapter deviceSelectorAdapter; // 右侧弹出窗内容适配器

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss",
            Locale.US);
    private DeviceSettingItemAdapter adapter;
    private long rsut = 0; // 预约开机时间
    private long selectedModeId = 0; // 选中的记录模式ID

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTitle();
        initData();
        initAdapters();
        initRecyclerViews();
        initHeaderView();
        initListener();
        initInformationCard();
        getDevicePresetConfiguration();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_device_setting;
    }

    private void initTitle() {
//        tvTitle.setText(R.string.appointment_parameter);
        tvTitle.setText(String.format("%s%s", getString(R.string.pet_real_time),
                getString(R.string.appointment_parameter)));
    }

    private void initData() {
        mUserModel = getTrackUserModel();
        mDeviceModel = getTrackDevice();
        for (AAADeviceModel item : getTrackDeviceList()) {
            if(DataUtils.isPigeonDevice(item.getDeviceImei())) {
                item.setSelected(item.getDeviceImei().equals(mDeviceModel.getDeviceImei()));
                deviceModels.add(item);
            }
        }
    }

    private void initListener() {
        flBack.setOnClickListener(mNavigationOnClickListener);

        tvConfirmChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(Gravity.END);
            }
        });

        tvCancel.setOnClickListener(view -> {
            drawerLayout.closeDrawer(Gravity.END);
        });

        tvConfirm.setOnClickListener(view -> {
            if (rsut == 0) {
                showMessage(R.string.please_select_schedule_start_time);
                drawerLayout.closeDrawer(Gravity.END);
                return;
            }
            if (rsut <= System.currentTimeMillis()) {
                showMessage(R.string.schedule_start_time_cannot_be_earlier_than_the_current_time);
                drawerLayout.closeDrawer(Gravity.END);
                return;
            }
            if (selectedModeId == 0) {
                showMessage(R.string.please_select_a_recording_mode);
                drawerLayout.closeDrawer(Gravity.END);
                return;
            }

            drawerLayout.closeDrawer(Gravity.END);
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
                CarGpsRequestUtils.updateMultipleDevicesPresetConfiguration(mUserModel,
                        selectedModeId, rsut, parameter.toString(), mHandler);
            else
                showMessage(R.string.please_select_at_least_one_device_to_apply_this_configuration);
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

    private void initInformationCard() {
        tvConfirmChange.setVisibility(View.VISIBLE);
    }

    private void initAdapters() {
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
                if(position >= 0 && position < deviceModels.size()){
                    AAADeviceModel model = deviceModels.get(position);
                    model.setSelected(!model.isSelected());
                    adapter.notifyItemChanged(position);
                    checkDeviceSelectStatus();
                }
            }
        });
    }

    private void initRecyclerViews() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);
        rightMenu.setLayoutManager(new LinearLayoutManager(this));
        rightMenu.setAdapter(deviceSelectorAdapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initPigeonDeviceItem() {
        mItemList.clear();
        mItemList.add(new AAABaseItemBean(201, getString(R.string.scheduled_start_time), ""));
        mItemList.add(new AAABaseItemBean(202, getString(R.string.recording_mode), ""));
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
     * 初始化头布局
     */
    private void initHeaderView() {
        View headerView = getLayoutInflater().inflate(R.layout.header_view_device_info,
                mRecyclerView, false);
        headerView.findViewById(R.id.clBtn).setVisibility(View.VISIBLE);
        headerView.findViewById(R.id.ibRefresh).setVisibility(View.VISIBLE);
        headerView.findViewById(R.id.ibRefresh).setOnClickListener(this);
        ViewDataUtils.initDeviceInfoView(this, headerView, mDeviceModel);
        adapter.addHeaderView(headerView);
    }

    /**
     * 获取设备预设参数
     */
    private void getDevicePresetConfiguration() {
        if (!NetworkUtils.isNetworkAvailable()) {
            XToastUtils.toast(this, R.string.network_error_prompt);
            return;
        }
        AAAUserModel userModel = getTrackUserModel();
        if (userModel != null && mDeviceModel != null && !TextUtils.isEmpty(mDeviceModel.getDeviceImei())) {
            showDialog();
            CarGpsRequestUtils.getDevicePresetConfiguration(userModel,
                    mDeviceModel.getDeviceImei(), mHandler);
        }
    }

    /**
     * 标题栏Navigation点击监听器
     */
    private final View.OnClickListener mNavigationOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
//                AlertDialog.Builder builder = new AlertDialog.Builder
//                (DeviceModelSettingActivity.this);
//                builder.setTitle(R.string.prompt)
//                        .setMessage(R.string.discard_modification_prompt)
//                        .setPositiveButton(R.string.confirm, new DialogInterface
//                        .OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                finish();
//                            }
//                        }).setNegativeButton(R.string.cancel, new DialogInterface
//                        .OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                            }
//                        }).show();
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibRefresh: // 刷新
                rsut = 0;
                selectedModeId = 0;
                getDevicePresetConfiguration();
                break;
            default:
                break;
        }
    }

    private final BaseQuickAdapter.OnItemClickListener onItemClickListener =
            new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    if (position >= 0 && position < mItemList.size()) {
                        AAABaseItemBean itemBean = mItemList.get(position);
                        switch (itemBean.getType()) {
                            case 201:  //进入工作状态时间
                                showDateTimePickView(getString(R.string.recording_mode), rsut);
                                break;
                            case 202: // 选择记录模式
                                List<String> strs = new ArrayList<>();
                                for (DeviceRaceconfig item : recordingModeList) {
                                    strs.add(item.getConfigName());
                                }
                                strs.add(getString(R.string.preset_params_custom_mode));
                                OptionsPickerView<String> pickerView =
                                        new OptionsPickerBuilder(DeviceModelSettingActivity.this,
                                                new OnOptionsSelectListener() {
                                                    @Override
                                                    public void onOptionsSelect(int options1,
                                                                                int options2,
                                                                                int options3,
                                                                                View v) {
                                                        itemBean.setContent(strs.get(options1));
                                                        adapter.notifyDataSetChanged();
                                                        if (options1 == strs.size() - 1) {
                                                            Bundle bundle = new Bundle();
                                                            bundle.putInt(TConstant.TYPE, 1);
                                                            startActivity(bundle,
                                                                    DeviceSettingActivity.class);
                                                        } else {
                                                            selectedModeId =
                                                                    recordingModeList.get(options1).getId();
                                                        }
                                                    }
                                                }).setTitleText(getString(R.string.recording_mode)).build();
                                pickerView.setPicker(strs);
                                pickerView.setSelectOptions(0);
                                pickerView.show();
                                break;
                        }
                    }
                }
            };

    /**
     * 显示时间选择器
     *
     * @param title 标题
     */
    private void showDateTimePickView(String title, long dateValue) {
        if (mTimePickerView == null || !mTimePickerView.isShowing()) {
            Calendar selectedDate = Calendar.getInstance();
            Calendar startDate = Calendar.getInstance();
            Calendar endDate = Calendar.getInstance();
            Date contentDate;
            if (dateValue == 0) {
                contentDate = new Date();
            } else {
                contentDate = new Date(dateValue);
            }
            startDate.set(2022, 0, 1, 0, 0);
            endDate.set(new Date().getYear() + 1900, 11, 31, 23, 59);
            selectedDate.set(contentDate.getYear() + 1900, contentDate.getMonth(), contentDate.getDate()
                    , contentDate.getHours(), contentDate.getMinutes());
            mTimePickerView = new TimePickerBuilder(this, new OnTimeSelectListener() {
                @Override
                public void onTimeSelected(Date date, View v) {//选中事件回调
                    for (AAABaseItemBean model : mItemList) {
                        if(model.getType() == 201){
                            model.setContent(TimeUtils.formatUTC(date.getTime(),
                                    "yyyy/MM/dd HH:mm:ss"));
                            rsut = date.getTime();
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }).setType(true, true, true, true, true, false)// 默认全部显示
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
                    .isCenterLabel(true) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                    .isDialog(false)//是否显示为对话框样式
                    .build();
            mTimePickerView.show();
        }
    }

    /**
     * 消息处理
     */
    private final Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            AAABaseResponseBean response;
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
                    case TConstant.REQUEST_GET_DEVICE_PRESET_CONFIGURATION: // 获取配置模式列表
                        dismisDialog();
                        try {
                            List subList = (List) response.getData();
                            if (subList.size() > 0) {
                                recordingModeList = mGson.fromJson(mGson.toJson(subList.get(0)),
                                        new TypeToken<List<DeviceRaceconfig>>() {
                                        }.getType());
                                initPigeonDeviceItem();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case TConstant.REQUEST_UPDATE_DEVICE_PRESET_CONFIGURATION:  // 更新多个设备的记录模式
                        if (response.getCode() == TConstant.RESPONSE_SUCCESS) {
                            showMessage(R.string.update_succeed_prompt);
                            if (rsut >= System.currentTimeMillis()) {
                                AlertDialog.Builder builder =
                                        new AlertDialog.Builder(DeviceModelSettingActivity.this);
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
