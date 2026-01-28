package com.yyt.trackcar.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.reflect.TypeToken;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.dialog.bottomsheet.BottomSheet;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.picker.widget.TimePickerView;
import com.xuexiang.xui.widget.picker.widget.builder.TimePickerBuilder;
import com.xuexiang.xui.widget.picker.widget.listener.OnTimeSelectListener;
import com.xuexiang.xui.widget.tabbar.TabSegment;
import com.xuexiang.xutil.app.ActivityUtils;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.MainApplication;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAABaseResponseBean;
import com.yyt.trackcar.bean.AAARequestBean;
import com.yyt.trackcar.bean.PostMessage;
import com.yyt.trackcar.dbflow.AAADeviceModel;
import com.yyt.trackcar.dbflow.AAADeviceModel_Table;
import com.yyt.trackcar.dbflow.AAAUserModel;
import com.yyt.trackcar.ui.activity.BindDeviceActivity;
import com.yyt.trackcar.ui.adapter.DeviceListAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.DataUtils;
import com.yyt.trackcar.utils.DialogUtils;
import com.yyt.trackcar.utils.ErrorCode;
import com.yyt.trackcar.utils.SettingSPUtils;
import com.yyt.trackcar.utils.StringUtils;
import com.yyt.trackcar.utils.TConstant;
import com.yyt.trackcar.utils.TimeUtils;
import com.yyt.trackcar.utils.XToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      DeviceListFragment
 * @ author:        QING
 * @ createTime:    2023/3/27 15:36
 * @ describe:      TODO 设备列表
 */
@Page(name = "DeviceList", anim = CoreAnim.none)
public class DeviceListFragment extends BaseFragment implements TabSegment.OnTabClickListener,
        View.OnClickListener, BaseQuickAdapter.OnItemClickListener,
        BaseQuickAdapter.OnItemChildClickListener {
    @BindView(R.id.titleBar)
    TitleBar mTitleBar; // titleBar
    @BindView(R.id.tabSegment)
    TabSegment mTabSegment; // 选项卡
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout; // 下拉加载控件
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView; // RecyclerView
    private DeviceListAdapter mAdapter; // 适配器
    private final List<AAADeviceModel> mItemList = new ArrayList<>(); // 列表
    private int mSelectIndex = 0; // 选中选项
    private Button mDateBtn; // 时间按钮
    private TimePickerView mTimePickerView; // 时间选择器
    private AAADeviceModel mClickDeviceModel;
    private BottomSheet mBottomSheet; // 选项弹窗

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_device_list;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = mTitleBar;
//        titleBar.setTitle(R.string.device_list);
        titleBar.setTitle(String.format("%s%s", getString(R.string.pet_real_time),
                getString(R.string.device_list)));
        titleBar.setLeftImageResource(0);
        return titleBar;
    }

    @Override
    protected void initViews() {
        mRefreshLayout.setEnableLoadMore(false);
        initTabSegment();
        initItems();
        initAdapters();
        initRecyclerViews();
    }

    /**
     * 初始化选项卡
     */
    private void initTabSegment() {
        mTabSegment.addTab(new TabSegment.Tab(getString(R.string.device_list_selector_all)));
        mTabSegment.addTab(new TabSegment.Tab(getString(R.string.device_list_selector_usable)));
        mTabSegment.addTab(new TabSegment.Tab(getString(R.string.device_list_selector_lost)));
        mTabSegment.addTab(new TabSegment.Tab(getString(R.string.device_list_selector_unbound)));
        mTabSegment.setIndicatorWidthAdjustContent(false);
        //不使用ViewPager的话，必须notifyDataChanged，否则不能正常显示
        mTabSegment.notifyDataChanged();
        mTabSegment.selectTab(0);
    }

    @Override
    protected void initListeners() {
        mTabSegment.setOnTabClickListener(this);
        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (!NetworkUtils.isNetworkAvailable()) {
                    refreshLayout.finishRefresh();
                    return;
                }
                queryDevicesList();
            }
        });
    }

    /**
     * 初始化列表信息
     */
    private void initItems() {
    }

    /**
     * 初始化适配器
     */
    private void initAdapters() {
        mAdapter = new DeviceListAdapter(mItemList);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemChildClickListener(this);
    }

    /**
     * 初始化ViewPager
     */
    private void initRecyclerViews() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 激活/更新有效期
     */
    @SuppressLint("InflateParams")
    private void showActivatedDialog(int type, AAADeviceModel model, String content) {
        Context context = getContext();
        if ((mMaterialDialog == null || !mMaterialDialog.isShowing()) && context != null) {
            MaterialDialog.Builder materialDialogBuilder = new MaterialDialog.Builder(context);
            try {
                Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/arial" +
                        ".ttf");
                if (typeface != null)
                    materialDialogBuilder.typeface(typeface, typeface);
            } catch (Exception e) {
                if (BuildConfig.DEBUG)
                    e.printStackTrace();
            }
            materialDialogBuilder.title(R.string.prompt);
            String dateTimeString = null;
            Date dateTime = null;
            if (type == 1 && !TextUtils.isEmpty(model.getExpireDate())) {
                dateTime = TimeUtils.formatUTC(model.getExpireDate(), "yyyy-MM-dd");
            }
            if (type == 2 && !TextUtils.isEmpty(model.getGuaranteeDate())) {
                dateTime = TimeUtils.formatUTC(model.getGuaranteeDate(), "yyyy-MM-dd");
            }
            if (!(type == 1 || type == 2) && !TextUtils.isEmpty(model.getActivatedDatetime())) {
                dateTime = TimeUtils.formatUTC(model.getActivatedDatetime(), null);
            }
            if (dateTime != null) {
                dateTimeString = TimeUtils.formatUTC(dateTime.getTime(), "yyyy/MM/dd");
            }
            if (TextUtils.isEmpty(dateTimeString)) {
                dateTimeString = TimeUtils.formatUTC(System.currentTimeMillis(), "yyyy/MM/dd");
            }
            View customView = getLayoutInflater().inflate(R.layout.dialog_activated, null);
            TextView tvContent = customView.findViewById(R.id.tvContent);
            TextView tvData = customView.findViewById(R.id.tvDate);
            mDateBtn = customView.findViewById(R.id.dateBtn);
            tvContent.setText(content);
            if (type == 2) {
                tvData.setText(R.string.device_list_guarantee_date_select);
            }
            mDateBtn.setText(dateTimeString);
            mDateBtn.setOnClickListener(this);
            materialDialogBuilder.customView(customView, true);
            materialDialogBuilder.positiveText(R.string.confirm)
                    .onPositive((dialog, which) -> {
                        if (!NetworkUtils.isNetworkAvailable()) {
                            XToastUtils.toast(getContext(), R.string.network_error_prompt);
                            return;
                        }
                        dialog.dismiss();
                        Date expireDate = TimeUtils.formatUTC(mDateBtn.getText().toString(),
                                "yyyy/MM/dd");
                        if (expireDate != null) {
                            if (type == 1) {
                                updateExpireDate(model.getDeviceImei(), expireDate.getTime());
                            } else if (type == 2) {
                                updateGuaranteeDate(model.getDeviceImei(), expireDate.getTime());
                            } else {
                                activatedDevice(model.getDeviceImei(), 1, expireDate.getTime());
                            }
                        }
                    });
            materialDialogBuilder.negativeText(R.string.cancel)
                    .onNegative((dialog, which) -> {
                        dialog.dismiss();
                    });
            materialDialogBuilder.autoDismiss(false);
            mMaterialDialog = materialDialogBuilder.show();
        }
    }

    /**
     * 显示时间选择器
     */
    private void showTimePickView() {
        Context context = getContext();
        if (context != null && (mTimePickerView == null || !mTimePickerView.isShowing())) {
            Calendar selectedDate = Calendar.getInstance();
            Calendar startDate = Calendar.getInstance();
            Calendar endDate = Calendar.getInstance();
            String timeString = mDateBtn.getText().toString();
            Date dateTime = null;
            if (!TextUtils.isEmpty(timeString)) {
                dateTime = TimeUtils.formatUTC(timeString, "yyyy/MM/dd");
            }
            if (dateTime == null) {
                dateTime = new Date();
            }
            startDate.set(2022, 0, 1, 0, 0, 0);
            endDate.set(3000, 11, 31, 23, 59, 59);
            selectedDate.setTime(dateTime);
            mTimePickerView = new TimePickerBuilder(context, new OnTimeSelectListener() {
                @Override
                public void onTimeSelected(Date date, View v) {//选中事件回调
                    mDateBtn.setText(TimeUtils.formatUTC(date.getTime(), "yyyy/MM/dd"));
                }
            }).setType(true, true, true, false, false, false)// 默认全部显示
                    .setCancelText(getString(R.string.cancel))//取消按钮文字
                    .setSubmitText(getString(R.string.confirm))//确认按钮文字
                    .setContentTextSize(15) //滚轮文字大小
                    .setTitleSize(20)//标题文字大小
                    .setTitleText(getString(R.string.device_list_expire_date_select))//标题文字
                    .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                    .isCyclic(false)//是否循环滚动
                    .setTitleColor(Color.BLACK)//标题文字颜色
                    .setSubmitColor(Color.BLUE)//确定按钮文字颜色
                    .setCancelColor(Color.BLUE)//取消按钮文字颜色
                    // mode
                    .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/
                    .setRangDate(startDate, endDate)//起始终止年月日设定
                    .setLabel(getString(R.string.year), getString(R.string.mouth), getString(R
                                    .string.day), getString(R.string.hour_new), getString(R.string
                                    .minute_new),
                            getString(R.string.second_new))//默认设置为年月日时分秒
                    .isCenterLabel(true) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                    .isDialog(true)//是否显示为对话框样式
                    .build();
            mTimePickerView.show();
        }
    }

    private void showHandleDialog(AAADeviceModel model) {
        if (mSelectIndex == 3) {
            return;
        }
        if (mBottomSheet == null || !mBottomSheet.isShowing()) {
            Context context = getContext();
            if (context == null)
                context = mActivity;
            BottomSheet.BottomListSheetBuilder builder =
                    new BottomSheet.BottomListSheetBuilder(context);
            String imei = StringUtils.getNotNullText(model.getDeviceImei());
            boolean isActivated = model.getActivated() != null && model.getActivated() == 1;
//            builder.addItem(getString(R.string.details), "0");
//            if (DataUtils.isAgent() && !DataUtils.isDeviceLogin()) {
//                builder.addItem(getString(R.string.device_list_selector_unbind), "1");
//            }
            if (DataUtils.getPwdType() == 2 && !isActivated) {
                builder.addItem(getString(R.string.device_list_selector_activated), "2");
            }
            if (DataUtils.isPigeonDevice(imei)) {
                boolean isLost = model.getDeviceStatus() != null && model.getDeviceStatus() == 2;
                if (isLost) {
                    builder.addItem(getString(R.string.device_list_selector_find), "3");
                } else {
                    builder.addItem(getString(R.string.device_list_selector_lost), "4");
                }
            }
            if (DataUtils.getPwdType() == 2) {
//                builder.addItem(getString(R.string.device_info_remark), "5");
                builder.addItem(getString(R.string.expire_time_limit), "6");
                builder.addItem(getString(R.string.guarantee_time_limit), "7");
                if (model.getRaceStatus() != null && model.getRaceStatus() == 1) {
                    builder.addItem(getString(R.string.private_mode), "8");
                }
//                builder.addItem(getString(R.string.competition_mode), "9");
            }
            mBottomSheet = builder.setIsCenter(true)
                    .setOnSheetItemClickListener(new BottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                        @Override
                        public void onClick(BottomSheet dialog, View itemView, int position,
                                            String tag) {
                            dialog.dismiss();
                            Bundle bundle;
                            if ("0".equals(tag)) {  // 详情
                                if (TextUtils.isEmpty(model.getDeviceImei())) {
                                    showMessage(R.string.no_device_tips);
                                } else {
                                    bundle = new Bundle();
                                    bundle.putString(TConstant.DEVICE_IMEI,
                                            model.getDeviceImei());
                                    openNewPage(BabyInfoFragment.class, bundle);
                                }
                            } else if ("1".equals(tag)) {  // 解绑
                                mMaterialDialog = DialogUtils.customMaterialDialog(getContext()
                                        , mMaterialDialog, getString(R.string.prompt)
                                        , String.format("%s%s",
                                                model.getDeviceImei() + "(" + model.getDeviceName() + ")",
                                                getString(R.string.unbind_prompt))
                                        , getString(R.string.confirm)
                                        , getString(R.string.cancel)
                                        , model
                                        , CWConstant.DIALOG_UNBIND, mHandler);
                            } else if ("2".equals(tag)) {  // 激活
                                showActivatedDialog(0, model,
                                        String.format(getString(R.string.activated_device_prompt),
                                                model.getDeviceImei(), model.getDeviceName()));
                            } else if ("3".equals(tag)) {  // 找回
                                mMaterialDialog =
                                        DialogUtils.customMaterialDialog(getContext()
                                                , mMaterialDialog, getString(R.string.prompt)
                                                , String.format("%s%s",
                                                        model.getDeviceImei() + "(" + model.getDeviceName() + ")"
                                                        , getString(R.string.tip_mark_as_found))
                                                , getString(R.string.confirm)
                                                , getString(R.string.cancel)
                                                , model
                                                , CWConstant.DIALOG_DEVICE_FIND, mHandler);
                            } else if ("4".equals(tag)) {  // 飞丢
                                mMaterialDialog =
                                        DialogUtils.customMaterialDialog(getContext()
                                                , mMaterialDialog, getString(R.string.prompt)
                                                , String.format("%s%s",
                                                        model.getDeviceImei() + "(" + model.getDeviceName() + ")"
                                                        , getString(R.string.tip_mark_as_lost))
                                                , getString(R.string.confirm)
                                                , getString(R.string.cancel)
                                                , model
                                                , CWConstant.DIALOG_DEVICE_LOST, mHandler);
                            } else if ("5".equals(tag)) {  // 备注
                                if (TextUtils.isEmpty(model.getDeviceImei())) {
                                    showMessage(R.string.no_device_tips);
                                } else {
                                    mClickDeviceModel = model;
                                    bundle = new Bundle();
                                    bundle.putInt(CWConstant.TYPE, 8);
                                    bundle.putString(CWConstant.TITLE,
                                            getString(R.string.device_info_remark));
                                    bundle.putString(CWConstant.CONTENT,
                                            StringUtils.getNotNullText(model.getDeviceRemark()));
                                    bundle.putString(TConstant.DEVICE_IMEI,
                                            model.getDeviceImei());
                                    openNewPageForResult(CustomInputSecondFragment.class,
                                            bundle,
                                            CWConstant.REQUEST_OTHER);
                                }
                            } else if ("6".equals(tag)) {  // 更新有效期
                                showActivatedDialog(1, model,
                                        String.format(getString(R.string.update_expire_date_prompt),
                                                model.getDeviceImei(), model.getDeviceName()));
                            } else if ("7".equals(tag)) {  // 更新保修期
                                showActivatedDialog(2, model,
                                        String.format(getString(R.string.update_guarantee_date_prompt),
                                                model.getDeviceImei(), model.getDeviceName()));
                            } else if ("8".equals(tag)) { // 个人模式
                                mMaterialDialog =
                                        DialogUtils.customMaterialDialog(getContext()
                                                , mMaterialDialog, getString(R.string.prompt)
                                                , String.format("%s%s",
                                                        model.getDeviceImei() + "(" + model.getDeviceName() + ")"
                                                        ,
                                                        getString(R.string.update_race_status_first_prompt))
                                                , getString(R.string.confirm)
                                                , getString(R.string.cancel)
                                                , model
                                                , CWConstant.DIALOG_DEVICE_RACE_STATUS_FIRST,
                                                mHandler);
                            } else if ("9".equals(tag)) { // 比赛模式
                                mMaterialDialog =
                                        DialogUtils.customMaterialDialog(getContext()
                                                , mMaterialDialog, getString(R.string.prompt)
                                                , String.format("%s%s",
                                                        model.getDeviceImei() + "(" + model.getDeviceName() + ")"
                                                        ,
                                                        getString(R.string.update_race_status_second_prompt))
                                                , getString(R.string.confirm)
                                                , getString(R.string.cancel)
                                                , model
                                                , CWConstant.DIALOG_DEVICE_RACE_STATUS_SECOND,
                                                mHandler);
                            }
                        }
                    }).build();
            mBottomSheet.show();
        }
    }

    /**
     * 点击主页底部导航栏时调用
     */
    public void onSwitchToThisPage() {
        queryDevicesList();
    }

    /**
     * 根据状态请求获取设备列表
     * <p>
     * null:全部  1:可用  2:飞丢
     */
    private void queryDevicesList() {
        AAAUserModel userModel = getTrackUserModel();
        if (userModel != null) {
            Integer deviceStatus = null;
            if (mSelectIndex == 1) {
                deviceStatus = 1;
            } else if (mSelectIndex == 2) {
                deviceStatus = 2;
            } else if (mSelectIndex == 3) {
                CarGpsRequestUtils.queryUnboundList(userModel, mHandler);
                return;
            }
            CarGpsRequestUtils.getDeviceList(userModel, deviceStatus, mHandler);
        }
    }

    /**
     * 解绑设备
     *
     * @param imei 绑定号
     */
    private void unbindDevice(String imei) {
        if (!NetworkUtils.isNetworkAvailable()) {
            XToastUtils.toast(getContext(), R.string.network_error_prompt);
            return;
        }
        AAAUserModel userModel = getTrackUserModel();
        if (userModel != null && !TextUtils.isEmpty(imei)) {
            showDialog();
            CarGpsRequestUtils.deleteDevice(imei, userModel, mHandler);
        }
    }

    /**
     * 更新设备状态
     *
     * @param deviceId 设备ID
     * @param status   1.找回 2.丢失
     */
    private void updateDeviceStatus(long deviceId, int status) {
        if (!NetworkUtils.isNetworkAvailable()) {
            XToastUtils.toast(getContext(), R.string.network_error_prompt);
            return;
        }
        AAAUserModel userModel = getTrackUserModel();
        if (userModel != null) {
            showDialog();
            CarGpsRequestUtils.updateDeviceStatus(deviceId,
                    getTrackUserModel(), status, mHandler);
        }
    }

    /**
     * 激活设备
     *
     * @param imei       设备IMEI
     * @param status     1.激活
     * @param expireDate 有效期
     */
    private void activatedDevice(String imei, int status, long expireDate) {
        AAAUserModel userModel = getTrackUserModel();
        if (userModel != null && !TextUtils.isEmpty(imei)) {
            showDialog();
            CarGpsRequestUtils.activatedDevice(imei, status, expireDate,
                    userModel, mHandler);
        }
    }

    /**
     * 更新有效期
     *
     * @param imei       设备IMEI
     * @param expireDate 有效期
     */
    private void updateExpireDate(String imei, long expireDate) {
        AAAUserModel userModel = getTrackUserModel();
        if (userModel != null && !TextUtils.isEmpty(imei)) {
            showDialog();
            CarGpsRequestUtils.updateExpireDate(imei, expireDate,
                    userModel, mHandler);
        }
    }

    /**
     * 更新保修期
     *
     * @param imei          设备IMEI
     * @param guaranteeDate 保修期
     */
    private void updateGuaranteeDate(String imei, long guaranteeDate) {
        AAAUserModel userModel = getTrackUserModel();
        if (userModel != null && !TextUtils.isEmpty(imei)) {
            showDialog();
            CarGpsRequestUtils.updateGuaranteeDate(imei, guaranteeDate,
                    userModel, mHandler);
        }
    }

    /**
     * 设置个人模式 raceStatus =0，比赛模式 raceStatus=1
     *
     * @param deviceImei 设备IMEI
     * @param raceStatus 个人模式 raceStatus =0，比赛模式 raceStatus=1
     */
    private void updateRaceStatus(String deviceImei, String raceStatus) {
        AAAUserModel userModel = getTrackUserModel();
        if (userModel != null && !TextUtils.isEmpty(deviceImei)) {
            showDialog();
            CarGpsRequestUtils.updateRaceStatus(userModel, deviceImei, raceStatus, mHandler);
        }
    }

    @Override
    public void onTabClick(int index) {
        mSelectIndex = index;
        mRefreshLayout.finishRefresh();
        mItemList.clear();
        mAdapter.setType(index);
        mAdapter.notifyDataSetChanged();
        queryDevicesList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dateBtn: // 选择有效期
                showTimePickView();
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (position >= 0 && position < mItemList.size()) {
            AAADeviceModel model = mItemList.get(position);
            MainApplication.getInstance().setTrackDeviceModel(model);
            mAdapter.notifyDataSetChanged();
            SettingSPUtils.getInstance().putString(TConstant.SELECTED_IMEI, model.getDeviceImei());
            EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_CHANGE_DEVICE));
        }
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if (position >= 0 && position < mItemList.size()) {
            AAADeviceModel model = mItemList.get(position);
            Bundle bundle;
            switch (view.getId()) {
                case R.id.unbindBtn: // 解绑
                    mMaterialDialog = DialogUtils.customMaterialDialog(getContext()
                            , mMaterialDialog, getString(R.string.prompt)
                            , String.format("%s%s",
                                    model.getDeviceImei() + "(" + model.getDeviceName() + ")",
                                    getString(R.string.unbind_prompt))
                            , getString(R.string.confirm)
                            , getString(R.string.cancel)
                            , model
                            , CWConstant.DIALOG_UNBIND, mHandler);
                    break;
                case R.id.handleBtn: // 找回/飞丢
                    showHandleDialog(model);
//                    boolean isLost =
//                            model.getDeviceStatus() != null && model.getDeviceStatus() == 2;
//                    if (isLost) {
//                        mMaterialDialog = DialogUtils.customMaterialDialog(getContext()
//                                , mMaterialDialog, getString(R.string.prompt)
//                                , String.format("%s%s",
//                                        model.getDeviceImei() + "(" + model.getDeviceName() + ")"
//                                        , getString(R.string.tip_mark_as_found))
//                                , getString(R.string.confirm)
//                                , getString(R.string.cancel)
//                                , model
//                                , CWConstant.DIALOG_DEVICE_FIND, mHandler);
//                    } else {
//                        mMaterialDialog = DialogUtils.customMaterialDialog(getContext()
//                                , mMaterialDialog, getString(R.string.prompt)
//                                , String.format("%s%s",
//                                        model.getDeviceImei() + "(" + model.getDeviceName() + ")"
//                                        , getString(R.string.tip_mark_as_lost))
//                                , getString(R.string.confirm)
//                                , getString(R.string.cancel)
//                                , model
//                                , CWConstant.DIALOG_DEVICE_LOST, mHandler);
//                    }
                    break;
                case R.id.activatedBtn: // 激活
                    showActivatedDialog(0, model,
                            String.format(getString(R.string.activated_device_prompt),
                                    model.getDeviceImei(), model.getDeviceName()));
                    break;
                case R.id.expireBtn: // 更新有效期
                    showActivatedDialog(1, model,
                            String.format(getString(R.string.update_expire_date_prompt),
                                    model.getDeviceImei(), model.getDeviceName()));
                    break;
                case R.id.guaranteeBtn: // 更新保修期
                    showActivatedDialog(2, model,
                            String.format(getString(R.string.update_guarantee_date_prompt),
                                    model.getDeviceImei(), model.getDeviceName()));
                    break;
                case R.id.detailsBtn: // 详情
                    if (TextUtils.isEmpty(model.getDeviceImei())) {
                        showMessage(R.string.no_device_tips);
                    } else {
                        bundle = new Bundle();
                        bundle.putString(TConstant.DEVICE_IMEI, model.getDeviceImei());
                        openNewPage(BabyInfoFragment.class, bundle);
                    }
                    break;
                case R.id.remarkBtn: // 备注
                    if (TextUtils.isEmpty(model.getDeviceImei())) {
                        showMessage(R.string.no_device_tips);
                    } else {
                        mClickDeviceModel = model;
                        bundle = new Bundle();
                        bundle.putInt(CWConstant.TYPE, 8);
                        bundle.putString(CWConstant.TITLE, getString(R.string.device_info_remark));
                        bundle.putString(CWConstant.CONTENT,
                                StringUtils.getNotNullText(model.getDeviceRemark()));
                        bundle.putString(TConstant.DEVICE_IMEI, model.getDeviceImei());
                        openNewPageForResult(CustomInputSecondFragment.class, bundle,
                                CWConstant.REQUEST_OTHER);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Intent data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CWConstant.REQUEST_OTHER: // 其它
                    if (data != null) {
                        Bundle bundle = data.getExtras();
                        if (bundle != null) {
                            String remark = bundle.getString(CWConstant.NAME);
                            mClickDeviceModel.setDeviceRemark(remark);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private final Handler mHandler = new Handler(new Handler.Callback() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public boolean handleMessage(@NonNull Message message) {
            try {
                AAABaseResponseBean responseBean;
                AAARequestBean requestBean;
                switch (message.what) {
                    case TConstant.REQUEST_URL_GET_DEVICE_LIST: { // 获取设备列表
                        mRefreshLayout.finishRefresh();
                        responseBean = (AAABaseResponseBean) message.obj;
                        if (responseBean.getCode() == TConstant.RESPONSE_SUCCESS) {
                            requestBean = mGson.fromJson(responseBean.getRequestObject(),
                                    AAARequestBean.class);
                            if ((requestBean.getType() == null && mSelectIndex == 0)
                                    || (requestBean.getType() != null && mSelectIndex == requestBean.getType())) {
                                mItemList.clear();
                                if (responseBean.getData() != null) {
                                    mItemList.addAll(mGson.fromJson(mGson.toJson(responseBean.getData()),
                                            new TypeToken<List<AAADeviceModel>>() {
                                            }.getType()));
                                }
                                if (requestBean.getType() == null) {
                                    getTrackDeviceList().clear();
                                    getTrackDeviceList().addAll(mItemList);
                                    for (AAADeviceModel model : getTrackDeviceList()) {
                                        AAADeviceModel deviceModel = getTrackDeviceModel();
                                        if (deviceModel != null
                                                && !TextUtils.isEmpty(deviceModel.getDeviceImei())
                                                && deviceModel.getDeviceImei().equals(model.getDeviceImei())) {
                                            MainApplication.getInstance().setTrackDeviceModel(model);
                                        }
                                    }
                                    if (mItemList.size() == 0) {
                                        showMessage(R.string.user_no_bound_device_tips);
                                        MainApplication.getInstance().setTrackDeviceModel(null);
                                        EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_FINISH));
                                        ActivityUtils.startActivity(BindDeviceActivity.class);
                                        return false;
                                    }
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                    break;
                    case TConstant.REQUEST_QUERY_UNUNBOUND_LIST: { // 获取已解绑列表
                        mRefreshLayout.finishRefresh();
                        responseBean = (AAABaseResponseBean) message.obj;
                        if (responseBean.getCode() == TConstant.RESPONSE_SUCCESS) {
                            if (mSelectIndex == 3) {
                                mItemList.clear();
                                if (responseBean.getData() != null) {
                                    List list = (List) responseBean.getData();
                                    if (list.size() > 0) {
                                        mItemList.addAll(mGson.fromJson(mGson.toJson(list.get(0)),
                                                new TypeToken<List<AAADeviceModel>>() {
                                                }.getType()));
                                    }
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                    break;
                    case TConstant.REQUEST_UNBIND_DEVICE: { // 解除绑定
                        dismisDialog();
                        responseBean = (AAABaseResponseBean) message.obj;
                        if (responseBean.getCode() == TConstant.RESPONSE_SUCCESS) {
                            XToastUtils.toast(getContext(),
                                    getString(R.string.unbind_succeed_prompt));
                            requestBean = mGson.fromJson(responseBean.getRequestObject(),
                                    AAARequestBean.class);
                            if (!TextUtils.isEmpty(requestBean.getDeviceImei())) {
                                for (int i = 0; i < mItemList.size(); i++) {
                                    AAADeviceModel model = mItemList.get(i);
                                    if (requestBean.getDeviceImei().equals(model.getDeviceImei())) {
                                        mItemList.remove(i);
                                        mAdapter.notifyDataSetChanged();
                                        break;
                                    }
                                }
                                AAAUserModel userModel = getTrackUserModel();
                                if (userModel != null) {
                                    List<AAADeviceModel> deviceList = getTrackDeviceList();
                                    for (int i = 0; i < deviceList.size(); i++) {
                                        AAADeviceModel deviceModel = deviceList.get(i);
                                        if (requestBean.getDeviceImei().equals(deviceModel.getDeviceImei())) {
                                            OperatorGroup operatorGroup =
                                                    OperatorGroup.clause(OperatorGroup.clause()
                                                            .and(AAADeviceModel_Table.userId.eq(userModel.getUserId()))
                                                            .and(AAADeviceModel_Table.deviceId.eq(deviceModel.getDeviceId())));
                                            SQLite.delete(AAADeviceModel.class).where(operatorGroup).execute();
                                            deviceList.remove(i);
                                            break;
                                        }
                                    }
                                    if (deviceList.size() == 0) {
                                        MainApplication.getInstance().setDeviceModel(null);
                                        EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_FINISH));
                                        ActivityUtils.startActivity(BindDeviceActivity.class);
                                    } else {
                                        MainApplication.getInstance().setTrackDeviceModel(deviceList.get(0));
                                        SettingSPUtils.getInstance().putString(TConstant.SELECTED_IMEI, deviceList.get(0).getDeviceImei());
                                        EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_CHANGE_DEVICE));
                                    }
                                }
                            }
                        } else {
                            XToastUtils.toast(getContext(),
                                    ErrorCode.getResId(responseBean.getCode()));
                        }
                    }
                    break;
                    case TConstant.REQUEST_UPDATE_DEVICE_STATUS: { // 更新设备状态
                        dismisDialog();
                        responseBean = (AAABaseResponseBean) message.obj;
                        if (responseBean.getCode() == TConstant.RESPONSE_SUCCESS) {
                            XToastUtils.toast(getContext(),
                                    getString(R.string.update_succeed_prompt));
                            requestBean = mGson.fromJson(responseBean.getRequestObject(),
                                    AAARequestBean.class);
                            if (requestBean.getDeviceId() != null) {
                                for (AAADeviceModel model : mItemList) {
                                    if (requestBean.getDeviceId() == model.getDeviceId()) {
                                        model.setDeviceStatus(requestBean.getType());
                                        mAdapter.notifyDataSetChanged();
                                        break;
                                    }
                                }
                            }
                        } else {
                            XToastUtils.toast(getContext(),
                                    ErrorCode.getResId(responseBean.getCode()));
                        }
                    }
                    break;
                    case TConstant.REQUEST_ACTIVATED_DEVICE: { // 激活设备
                        dismisDialog();
                        responseBean = (AAABaseResponseBean) message.obj;
                        if (responseBean.getCode() == TConstant.RESPONSE_SUCCESS) {
                            XToastUtils.toast(getContext(),
                                    getString(R.string.activated_device_success_prompt));
                            requestBean = mGson.fromJson(responseBean.getRequestObject(),
                                    AAARequestBean.class);
                            if (!TextUtils.isEmpty(requestBean.getDeviceImei())) {
                                for (AAADeviceModel model : mItemList) {
                                    if (requestBean.getDeviceImei().equals(model.getDeviceImei())) {
                                        model.setActivated(1);
                                        model.setActivatedDatetime(TimeUtils.formatUTC(System.currentTimeMillis(), null));
                                        if (requestBean.getExpireDate() != null) {
                                            model.setExpireDate(TimeUtils.formatUTC(requestBean.getExpireDate(), "yyyy-MM-dd"));
                                        }
                                        mAdapter.notifyDataSetChanged();
                                        break;
                                    }
                                }
                            }
                        } else {
                            XToastUtils.toast(getContext(),
                                    ErrorCode.getResId(responseBean.getCode()));
                        }
                    }
                    break;
                    case TConstant.REQUEST_UPDATE_EXPIRE_DATE: { // 更新有效期
                        dismisDialog();
                        responseBean = (AAABaseResponseBean) message.obj;
                        if (responseBean.getCode() == TConstant.RESPONSE_SUCCESS) {
                            XToastUtils.toast(getContext(),
                                    getString(R.string.update_succeed_prompt));
                            requestBean = mGson.fromJson(responseBean.getRequestObject(),
                                    AAARequestBean.class);
                            if (!TextUtils.isEmpty(requestBean.getDeviceImei())) {
                                for (AAADeviceModel model : mItemList) {
                                    if (requestBean.getDeviceImei().equals(model.getDeviceImei())) {
                                        if (requestBean.getExpireDate() != null) {
                                            model.setExpireDate(TimeUtils.formatUTC(requestBean.getExpireDate(), "yyyy-MM-dd"));
                                        }
                                        mAdapter.notifyDataSetChanged();
                                        break;
                                    }
                                }
                            }
                        } else {
                            XToastUtils.toast(getContext(),
                                    ErrorCode.getResId(responseBean.getCode()));
                        }
                    }
                    break;
                    case TConstant.REQUEST_UPDATE_GUARANTEE_DATE: { // 更新保修期
                        dismisDialog();
                        responseBean = (AAABaseResponseBean) message.obj;
                        if (responseBean.getCode() == TConstant.RESPONSE_SUCCESS) {
                            XToastUtils.toast(getContext(),
                                    getString(R.string.update_succeed_prompt));
                            requestBean = mGson.fromJson(responseBean.getRequestObject(),
                                    AAARequestBean.class);
                            if (!TextUtils.isEmpty(requestBean.getDeviceImei())) {
                                for (AAADeviceModel model : mItemList) {
                                    if (requestBean.getDeviceImei().equals(model.getDeviceImei())) {
                                        if (requestBean.getExpireDate() != null) {
                                            model.setGuaranteeDate(TimeUtils.formatUTC(requestBean.getExpireDate(), "yyyy-MM-dd"));
                                        }
                                        mAdapter.notifyDataSetChanged();
                                        break;
                                    }
                                }
                            }
                        } else {
                            XToastUtils.toast(getContext(),
                                    ErrorCode.getResId(responseBean.getCode()));
                        }
                    }
                    break;
                    case TConstant.REQUEST_UPDATE_RACE_STATUS: { // 设置设备模式
                        dismisDialog();
                        responseBean = (AAABaseResponseBean) message.obj;
                        if (responseBean.getCode() == TConstant.RESPONSE_SUCCESS) {
                            XToastUtils.toast(getContext(),
                                    getString(R.string.update_succeed_prompt));
                            requestBean = mGson.fromJson(responseBean.getRequestObject(),
                                    AAARequestBean.class);
                            if (!TextUtils.isEmpty(requestBean.getDeviceImei())) {
                                for (AAADeviceModel model : mItemList) {
                                    if (requestBean.getDeviceImei().equals(model.getDeviceImei())) {
                                        model.setRaceStatus(requestBean.getType());
                                        mAdapter.notifyDataSetChanged();
                                        break;
                                    }
                                }
                            }
                        } else {
                            XToastUtils.toast(getContext(),
                                    ErrorCode.getResId(responseBean.getCode()));
                        }
                    }
                    break;
                    case CWConstant.HANDLE_CONFIRM_ACTION: // MaterialDialog弹窗点击确认按钮后的回调
                        switch (message.arg1) {
                            case CWConstant.DIALOG_UNBIND: {
                                if (message.obj != null) {
                                    AAADeviceModel model = (AAADeviceModel) message.obj;
                                    unbindDevice(model.getDeviceImei());
                                }
                            }
                            break;
                            case CWConstant.DIALOG_DEVICE_LOST: {
                                if (message.obj != null) {
                                    AAADeviceModel model = (AAADeviceModel) message.obj;
                                    updateDeviceStatus(model.getDeviceId(), 2);
                                }
                            }
                            break;
                            case CWConstant.DIALOG_DEVICE_FIND: {
                                if (message.obj != null) {
                                    AAADeviceModel model = (AAADeviceModel) message.obj;
                                    updateDeviceStatus(model.getDeviceId(), 1);
                                }
                            }
                            break;
                            case CWConstant.DIALOG_DEVICE_ACTIVE: {
//                                if (message.obj != null) {
//                                    AAADeviceModel model = (AAADeviceModel) message.obj;
//                                    activatedDevice(model.getDeviceImei(), 1);
//                                }
                            }
                            break;
                            case CWConstant.DIALOG_DEVICE_RACE_STATUS_FIRST: {
                                if (message.obj != null) {
                                    AAADeviceModel model = (AAADeviceModel) message.obj;
                                    updateRaceStatus(model.getDeviceImei(), "0");
                                }
                            }
                            break;
                            case CWConstant.DIALOG_DEVICE_RACE_STATUS_SECOND: {
                                if (message.obj != null) {
                                    AAADeviceModel model = (AAADeviceModel) message.obj;
                                    updateRaceStatus(model.getDeviceImei(), "1");
                                }
                            }
                            break;
                            default:
                                break;
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handlePostMessage(PostMessage event) {
        if (CWConstant.POST_MESSAGE_BACK_TO_MAIN == event.getType()) {
            queryDevicesList();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

}
