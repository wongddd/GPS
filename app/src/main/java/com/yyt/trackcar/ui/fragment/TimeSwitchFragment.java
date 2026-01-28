package com.yyt.trackcar.ui.fragment;

import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.picker.widget.TimePickerView;
import com.xuexiang.xui.widget.picker.widget.builder.TimePickerBuilder;
import com.xuexiang.xui.widget.picker.widget.listener.OnTimeSelectListener;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.MainApplication;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.BaseItemBean;
import com.yyt.trackcar.bean.RequestBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.bean.SectionItem;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.DeviceSettingsModel;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.ui.adapter.CustomTextAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CWRequestUtils;
import com.yyt.trackcar.utils.RequestToastUtils;
import com.yyt.trackcar.utils.SettingSPUtils;
import com.yyt.trackcar.utils.TimeUtils;
import com.yyt.trackcar.utils.XToastUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      TimeSwitchFragment
 * @ author:        QING
 * @ createTime:    2020/3/13 15:41
 * @ describe:      TODO 定时开关机页面
 */
@Page(name = "TimeSwitch")
public class TimeSwitchFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.switchBtn)
    Button mSwitchBtn; // 开启/关闭按钮
    @BindView(R.id.cancelBtn)
    Button mCancelBtn; // 取消按钮
    @BindView(R.id.saveBtn)
    Button mSaveBtn; // 保存按钮
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView; // RecyclerView
    private CustomTextAdapter mAdapter; // 适配器
    private List<SectionItem> mItemList = new ArrayList<>(); // 列表
    private boolean mIsOpen; // 是否开启
    private TimePickerView mTimePickerDialog; // 时间选择器

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycler_time_switch;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(R.string.time_switch);
        return titleBar;
    }

    @Override
    protected void initViews() {
        initItems();
        initAdapters();
        initRecyclerViews();
        initHeaderView();
        initFooterView();
        setSwitch();
        getOther();
    }

    /**
     * 初始化列表信息
     */
    private void initItems() {
        DeviceSettingsModel settingsModel = getDeviceSettings();
        String startTime = "06:00";
        String endTime = "22:00";
        if (settingsModel != null && !TextUtils.isEmpty(settingsModel.getOther())) {
            String[] array = settingsModel.getOther().split(",");
            if (array.length >= 4) {
                String[] subArray = array[3].split("\\|");
                if (subArray.length >= 3) {
                    if (TimeUtils.formatUTC(subArray[0], "HH:mm") != null)
                        startTime = subArray[0];
                    if (TimeUtils.formatUTC(subArray[1], "HH:mm") != null)
                        endTime = subArray[1];
                    mIsOpen = "1".equals(subArray[2]);
                }
            }
        }
        BaseItemBean itemBean = new BaseItemBean(0, getString(R.string.time_switch_start),
                startTime);
        itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
        itemBean.setHasArrow(true);
        mItemList.add(new SectionItem(itemBean));
        itemBean = new BaseItemBean(1, getString(R.string.time_switch_end), endTime);
        itemBean.setBgDrawable(R.drawable.btn_custom_bottom_radius);
        itemBean.setHasArrow(true);
        mItemList.add(new SectionItem(itemBean));
    }

    /**
     * 初始化适配器
     */
    private void initAdapters() {
        mAdapter = new CustomTextAdapter(mItemList);
        mAdapter.setOnItemClickListener(this);
    }

    /**
     * 初始化ViewPager
     */
    private void initRecyclerViews() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 初始化头布局
     */
    private void initHeaderView() {
        if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 0) {
            View headerView = getLayoutInflater().inflate(R.layout.header_view_info_second,
                    mRecyclerView, false);
            ImageView ivBg = headerView.findViewById(R.id.ivBg);
            TextView tvTitle = headerView.findViewById(R.id.tvTitle);
            TextView tvContent = headerView.findViewById(R.id.tvContent);
            ivBg.setImageResource(R.mipmap.bg_time_switch);
            tvTitle.setText(R.string.time_switch_title);
            tvTitle.setTextColor(ContextCompat.getColor(mActivity, R.color.black));
            tvContent.setText(R.string.time_switch_content);
            tvContent.setTextColor(ContextCompat.getColor(mActivity, R.color.black));
            mAdapter.addHeaderView(headerView);
        } else {
            View headerView = getLayoutInflater().inflate(R.layout.header_view_info,
                    mRecyclerView, false);
            ImageView ivBg = headerView.findViewById(R.id.ivBg);
            TextView tvTitle = headerView.findViewById(R.id.tvTitle);
            TextView tvContent = headerView.findViewById(R.id.tvContent);
            ivBg.setImageResource(R.mipmap.bg_time_switch_second);
            tvTitle.setText(R.string.time_switch_title);
            tvTitle.setTextColor(ContextCompat.getColor(mActivity, R.color.black));
            tvContent.setText(R.string.time_switch_content);
            tvContent.setTextColor(ContextCompat.getColor(mActivity, R.color.black));
            mAdapter.addHeaderView(headerView);
        }
    }

    /**
     * 初始化脚布局
     */
    private void initFooterView() {
        View footerView = getLayoutInflater().inflate(R.layout.footer_view_text, mRecyclerView,
                false);
        TextView tvContent = footerView.findViewById(R.id.tvContent);
        tvContent.setText(R.string.time_switch_prompt);
        mAdapter.addFooterView(footerView);
    }

    /**
     * 设置开关
     */
    public void setSwitch() {
        if (mIsOpen)
            mSwitchBtn.setText(getString(R.string.close_status));
        else
            mSwitchBtn.setText(getString(R.string.open_status));
    }

    /**
     * 显示时间选择器
     *
     * @param itemBean 时间
     */
    public void showTimePickerDialog(final BaseItemBean itemBean) {
        if (mTimePickerDialog == null || !mTimePickerDialog.isShowing()) {
            Date date = TimeUtils.formatUTC(itemBean.getContent(), "HH:mm");
            if (date == null)
                date = TimeUtils.formatUTC("00:00", "HH:mm");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            mTimePickerDialog = new TimePickerBuilder(mActivity, new OnTimeSelectListener() {
                @Override
                public void onTimeSelected(Date date, View v) {
                    String time = itemBean.getContent();
                    itemBean.setContent(TimeUtils.formatUTC(date.getTime(), "HH:mm"));
                    if (itemBean.getContent().equals(time))
                        return;
                    String startTime = "06:00";
                    String endTime = "22:00";
                    for (SectionItem item : mItemList) {
                        BaseItemBean itemBean = item.t;
                        if (itemBean != null) {
                            switch (itemBean.getType()) {
                                case 0: // 开机时间
                                    startTime = itemBean.getContent();
                                    break;
                                case 1: // 关机时间
                                    endTime = itemBean.getContent();
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                    if (startTime.equals(endTime)) {
                        XToastUtils.toast(R.string.time_switch_time_error_prompt);
                        itemBean.setContent(time);
                        return;
                    } else {
                        mAdapter.notifyDataSetChanged();
//                        setOther();
                    }
                    mCancelBtn.setVisibility(View.VISIBLE);
                    mSaveBtn.setVisibility(View.VISIBLE);
                    mSwitchBtn.setVisibility(View.GONE);
                }
            })
                    .setType(false, false, false, true, true, false)
                    .setTitleText(itemBean.getTitle())
                    .setCancelText(getString(R.string.cancel))
                    .setSubmitText(getString(R.string.confirm))
                    .setLabel(getString(R.string.picker_year), getString(R.string.picker_month),
                            getString(R.string.picker_day), getString(R.string.picker_hour),
                            getString(R.string.picker_minute), getString(R.string.picker_second))
                    .setDate(calendar)
                    .build();
            mTimePickerDialog.show();
        }
    }

    /**
     * 获取设置
     */
    public void getOther() {
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().getOther(getContext(), getIp(),
                    userModel.getToken(), deviceModel.getD_id(), mHandler);
    }

    /**
     * 设置定时开关机
     */
    private void setOther() {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork();
            return;
        }
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null) {
            DeviceSettingsModel settingsModel = getDeviceSettings();
            String other = "";
            String startTime = "06:00";
            String endTime = "22:00";
            for (SectionItem item : mItemList) {
                BaseItemBean itemBean = item.t;
                if (itemBean != null) {
                    switch (itemBean.getType()) {
                        case 0: // 开机时间
                            startTime = itemBean.getContent();
                            break;
                        case 1: // 关机时间
                            endTime = itemBean.getContent();
                            break;
                        default:
                            break;
                    }
                }
            }
            if (!TextUtils.isEmpty(settingsModel.getOther())) {
                String[] array = settingsModel.getOther().split(",");
                if (array.length >= 4)
                    array[3] = String.format("%s|%s|%s", startTime, endTime, mIsOpen ? "1" : "0");
                for (String str : array) {
                    other = String.format("%s,%s", other, str);
                }
            }
            if (TextUtils.isEmpty(other))
                other = String.format("5,0,0,%s|%s|%s,20|0", startTime, endTime, mIsOpen ? "1" :
                        "0");
            else
                other = other.substring(1);
            if (!other.equals(settingsModel.getOther()))
                CWRequestUtils.getInstance().setOther(MainApplication.getInstance(), getIp(),
                        userModel.getToken(), deviceModel.getImei(), deviceModel.getD_id(), other,
                        mHandler);
        }
    }

    @SingleClick
    @OnClick({R.id.switchBtn, R.id.cancelBtn, R.id.saveBtn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switchBtn: // 开启/关闭
                mIsOpen = !mIsOpen;
                setSwitch();
                setOther();
                break;
            case R.id.cancelBtn: // 取消
                mSwitchBtn.setVisibility(View.VISIBLE);
                mCancelBtn.setVisibility(View.GONE);
                mSaveBtn.setVisibility(View.GONE);
                break;
            case R.id.saveBtn: // 保存
                setOther();
                mSwitchBtn.setVisibility(View.VISIBLE);
                mCancelBtn.setVisibility(View.GONE);
                mSaveBtn.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (position >= 0 && position < mItemList.size()) {
            BaseItemBean itemBean = mItemList.get(position).t;
            if (itemBean != null)
                showTimePickerDialog(itemBean);
        }
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
                    case CWConstant.REQUEST_URL_GET_OTHER: // 获取设置
                        if (msg.obj != null) {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                userModel = getUserModel();
                                DeviceModel deviceModel = getDevice();
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                RequestBean bean =
                                        mGson.fromJson(mGson.toJson(resultBean.getResultBean()),
                                                RequestBean.class);
                                if (userModel != null && deviceModel != null && deviceModel.getD_id() == requestBean.getD_id()) {
                                    DeviceSettingsModel settingsModel = getDeviceSettings();
                                    settingsModel.setOther(bean.getOther());
                                    settingsModel.save();
                                    String startTime = "06:00";
                                    String endTime = "22:00";
                                    if (!TextUtils.isEmpty(bean.getOther())) {
                                        String[] array = bean.getOther().split(",");
                                        if (array.length >= 4) {
                                            String[] subArray = array[3].split("\\|");
                                            if (subArray.length >= 3) {
                                                if (TimeUtils.formatUTC(subArray[0], "HH:mm") != null)
                                                    startTime = subArray[0];
                                                if (TimeUtils.formatUTC(subArray[1], "HH:mm") != null)
                                                    endTime = subArray[1];
                                                mIsOpen = "1".equals(subArray[2]);
                                            } else
                                                mIsOpen = false;
                                        } else
                                            mIsOpen = false;
                                    } else
                                        mIsOpen = false;
                                    for (SectionItem item : mItemList) {
                                        BaseItemBean itemBean = item.t;
                                        if (itemBean != null) {
                                            switch (itemBean.getType()) {
                                                case 0: // 开机时间
                                                    itemBean.setContent(startTime);
                                                    break;
                                                case 1: // 关机时间
                                                    itemBean.setContent(endTime);
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                    }
                                    mAdapter.notifyDataSetChanged();
                                    setSwitch();
                                }
                            }
                        }
                        break;
                    case CWConstant.REQUEST_URL_SET_OTHER: // 设置其他参数
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
                            resultBean = (RequestResultBean) msg.obj;
                            if (!TextUtils.isEmpty(resultBean.getService_ip()) && !resultBean.getService_ip().equals(resultBean.getLast_online_ip())) {
                                userModel = getUserModel();
                                DeviceModel deviceModel = getDevice();
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                if (userModel != null && deviceModel != null && deviceModel.getD_id() == requestBean.getD_id()) {
                                    DeviceSettingsModel settingsModel = getDeviceSettings();
                                    settingsModel.setIp(resultBean.getLast_online_ip());
                                    settingsModel.save();
                                    if (!NetworkUtils.isNetworkAvailable()) {
                                        RequestToastUtils.toastNetwork();
                                        return false;
                                    }
                                    CWRequestUtils.getInstance().setOther(getContext(),
                                            resultBean.getLast_online_ip(),
                                            requestBean.getToken(), requestBean.getImei(),
                                            requestBean.getD_id(), requestBean.getOther(),
                                            mHandler);
                                }
                            } else if (resultBean.getCode() == CWConstant.SUCCESS || resultBean.getCode() == CWConstant.WAIT_ONLINE_UPDATE) {
                                if (resultBean.getCode() == CWConstant.WAIT_ONLINE_UPDATE)
                                    XToastUtils.toast(R.string.wait_online_update_prompt);
                                else
                                    XToastUtils.toast(R.string.send_success_prompt);
                                userModel = getUserModel();
                                DeviceModel deviceModel = getDevice();
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                if (userModel != null && deviceModel != null && deviceModel.getD_id() == requestBean.getD_id()) {
                                    DeviceSettingsModel settingsModel = getDeviceSettings();
                                    settingsModel.setOther(requestBean.getOther());
                                    settingsModel.save();
                                }
                            } else if (resultBean.getCode() == CWConstant.ERROR)
                                XToastUtils.toast(R.string.send_error_prompt);
                            else
                                RequestToastUtils.toast(resultBean.getCode());
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
        if (mTimePickerDialog != null && mTimePickerDialog.isShowing())
            mTimePickerDialog.dismiss();
//        setOther();
        super.onDestroy();
    }
}
