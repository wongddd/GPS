package com.yyt.trackcar.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.BaseItemBean;
import com.yyt.trackcar.bean.RequestBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.DeviceSettingsModel;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.ui.adapter.AlarmClockAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CWRequestUtils;
import com.yyt.trackcar.utils.RequestToastUtils;
import com.yyt.trackcar.utils.SettingSPUtils;
import com.yyt.trackcar.utils.TimeUtils;
import com.yyt.trackcar.utils.XToastUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      AlarmClockFragment
 * @ author:        QING
 * @ createTime:    2020/3/13 17:11
 * @ describe:      TODO 闹钟设置界面
 */
@Page(name = "AlarmClock")
public class AlarmClockFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener,
        CompoundButton.OnCheckedChangeListener {
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView; // RecyclerView
    private AlarmClockAdapter mAdapter; // 适配器
    private List<BaseItemBean> mItemList = new ArrayList<>(); // 列表

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycler_view_plus;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 1)
            titleBar.setTitle(R.string.alarm_and_prompt);
        else
            titleBar.setTitle(R.string.alarm_clock_settings);
        return titleBar;
    }

    @Override
    protected void initViews() {
        initItems();
        initAdapters();
        initRecyclerViews();
        initEmptyView();
        initHeaderAndFooterView();
        if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 1)
            getCureRemind();
        else
            getAlarmClock();
    }

    /**
     * 初始化列表信息
     */
    private void initItems() {
        if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 0) {
            DeviceSettingsModel settingsModel = getDeviceSettings();
            if (settingsModel != null && !TextUtils.isEmpty(settingsModel.getAlarm_clock())) {
                String[] array = settingsModel.getAlarm_clock().split("#");
                for (String str : array) {
                    addItem(str);
                }
            }
        }
//        if (mItemList.size() == 1)
//            addItem(String.format("%s|07:00|1111111|0", getString(R.string.alarm_clock)));
        if (mItemList.size() == 1)
            mItemList.get(0).setBgDrawable(R.drawable.btn_custom_item_round_selector);
        else if (mItemList.size() > 1) {
            mItemList.get(0).setBgDrawable(R.drawable.btn_custom_top_radius);
            mItemList.get(mItemList.size() - 1).setBgDrawable(R.drawable.btn_custom_bottom_radius);
        }
    }

    /**
     * 初始化适配器
     */
    private void initAdapters() {
        mAdapter = new AlarmClockAdapter(mItemList, this);
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
     * 初始化空布局
     */
    private void initEmptyView() {
        View emptyView = getLayoutInflater().inflate(R.layout.layout_empty_view,
                mRecyclerView, false);
        emptyView.setBackgroundColor(ContextCompat.getColor(mActivity, R.color
                .white));
        ImageView ivEmpty = emptyView.findViewById(R.id.ivEmpty);
        TextView tvEmpty = emptyView.findViewById(R.id.tvEmpty);
        ivEmpty.setImageResource(R.mipmap.ic_no_query_data);
        if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 1)
            tvEmpty.setText(getString(R.string.no_add_data_prompt,
                    getString(R.string.alarm_and_prompt)));
        else
            tvEmpty.setText(R.string.no_alarm_clock_prompt);
        mAdapter.setEmptyView(emptyView);
    }

    /**
     * 初始化头脚布局
     */
    private void initHeaderAndFooterView() {
        View headerView = getLayoutInflater().inflate(R.layout.item_space_section,
                mRecyclerView, false);
        View footerView = getLayoutInflater().inflate(R.layout.item_space_section,
                mRecyclerView, false);
        mAdapter.addHeaderView(headerView);
        mAdapter.addFooterView(footerView);
    }

    /**
     * 添加选项
     */
    private void addItem(String alarmString) {
        String[] array = alarmString.split("\\|");
        if (array.length == 4 && !TextUtils.isEmpty(array[0])) {
            BaseItemBean itemBean = new BaseItemBean(array[0], String.format("%s\n%s：%s", array[1],
                    getString(R.string.repeat), TimeUtils.getWeekDescription(getContext(),
                            array[2])));
            itemBean.setType(mItemList.size());
            itemBean.setGroup(alarmString);
            itemBean.setSelect("1".equals(array[3]));
            mItemList.add(itemBean);
        }
    }

    /**
     * 获取闹钟
     */
    private void getAlarmClock() {
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().getAlarmClock(getContext(), getIp(),
                    userModel.getToken(), deviceModel.getD_id(), mHandler);
    }

    /**
     * 设置闹钟
     *
     * @param alarmClock 闹钟
     */
    private void setAlarmClock(String alarmClock) {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork();
            return;
        }
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().setAlarmClock(getContext(), getIp(),
                    userModel.getToken(), deviceModel.getImei(), deviceModel.getD_id(),
                    alarmClock, mHandler);
    }

    /**
     * 获取吃药提醒设置
     */
    private void getCureRemind() {
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().getCureRemind(getContext(), getIp(),
                    userModel.getToken(), deviceModel.getD_id(), mHandler);
    }

    /**
     * 设置吃药提醒
     *
     * @param info 内容
     */
    private void setCureRemind(String info) {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork();
            return;
        }
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().setCureRemind(getContext(), getIp(),
                    userModel.getToken(), deviceModel.getImei(), deviceModel.getD_id(), info,
                    mHandler);
    }

    @SingleClick(1000)
    @OnClick({R.id.fabAdd})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabAdd: // 添加
                String alarmString = "";
                int num = 0;
                for (BaseItemBean bean : mItemList) {
                    num++;
                    if (num >= 4) {
                        if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 1)
                            XToastUtils.toast(getString(R.string.settings_to_max_prompt, 4,
                                    getString(R.string.alarm_and_prompt)));
                        else
                            XToastUtils.toast(R.string.alarm_clock_to_max_prompt);
                        return;
                    }
                    alarmString = String.format("%s#%s", alarmString, bean.getGroup());
                }
                if (TextUtils.isEmpty(alarmString))
                    alarmString = "0";
                else {
                    alarmString = String.format("%s#0", alarmString);
                    alarmString = alarmString.substring(1);
                }
                Bundle bundle = new Bundle();
                bundle.putString(CWConstant.MODEL, alarmString);
                bundle.putString(CWConstant.CONTENT, "");
                openNewPageForResult(SetAlarmClockFragment.class, bundle,
                        CWConstant.REQUEST_ALARM_CLOCK);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (position >= 0 && position < mItemList.size()) {
            BaseItemBean itemBean = mItemList.get(position);
            if (itemBean != null) {
                String alarmString = "";
                for (int i = 0; i < mItemList.size(); i++) {
                    BaseItemBean bean = mItemList.get(i);
                    if (bean != null && position == i)
                        alarmString = String.format("%s#0", alarmString);
                    else if (bean != null)
                        alarmString = String.format("%s#%s", alarmString, bean.getGroup());
                }
                if (!TextUtils.isEmpty(alarmString))
                    alarmString = alarmString.substring(1);
                Bundle bundle = new Bundle();
                bundle.putString(CWConstant.MODEL, alarmString);
                bundle.putString(CWConstant.CONTENT, itemBean.getGroup());
                openNewPageForResult(SetAlarmClockFragment.class, bundle,
                        CWConstant.REQUEST_ALARM_CLOCK);
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int type = (int) buttonView.getTag();
        for (int i = 0; i < mItemList.size(); i++) {
            BaseItemBean itemBean = mItemList.get(i);
            if (itemBean != null && itemBean.getType() == type) {
                itemBean.setSelect(isChecked);
                String[] array = itemBean.getGroup().split("\\|");
                String alarmString = String.format("%s|%s|%s|%s", array[0], array[1], array[2],
                        isChecked ? "1" : "0");
                itemBean.setGroup(alarmString);
                StringBuilder alarm = new StringBuilder();
                for (BaseItemBean bean : mItemList) {
                    alarm.append("#").append(bean.getGroup());
                }
                if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 1) {
                    if (alarm.length() == 0)
                        setCureRemind("");
                    else
                        setCureRemind(alarm.toString().substring(1));
                } else {
                    if (alarm.length() == 0)
                        setAlarmClock("");
                    else
                        setAlarmClock(alarm.toString().substring(1));
                }
                break;
            }
        }
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Intent data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == CWConstant.REQUEST_ALARM_CLOCK && resultCode == Activity.RESULT_OK && data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                String alarmString = bundle.getString(CWConstant.MODEL);
                mItemList.clear();
                if (alarmString != null) {
                    String[] array = alarmString.split("#");
                    for (String str : array) {
                        addItem(str);
                    }
                }
                if (mItemList.size() == 1)
                    mItemList.get(0).setBgDrawable(R.drawable.btn_custom_item_round_selector);
                else if (mItemList.size() > 1) {
                    mItemList.get(0).setBgDrawable(R.drawable.btn_custom_top_radius);
                    mItemList.get(mItemList.size() - 1).setBgDrawable(R.drawable.btn_custom_bottom_radius);
                }
                mAdapter.notifyDataSetChanged();
            }
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
                    case CWConstant.REQUEST_URL_GET_ALARM_CLOCK: // 获取闹钟
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
                                    settingsModel.setAlarm_clock(bean.getAlarm_clock());
                                    settingsModel.save();
                                }
                                mItemList.clear();
                                if (!TextUtils.isEmpty(bean.getAlarm_clock())) {
                                    String[] array = bean.getAlarm_clock().split("#");
                                    for (String str : array) {
                                        addItem(str);
                                    }
                                }
//                                if (mItemList.size() == 1)
//                                    addItem(String.format("%s|07:00|1111111|0", getString(R
//                                    .string.alarm_clock)));
                                if (mItemList.size() == 1)
                                    mItemList.get(0).setBgDrawable(R.drawable.btn_custom_item_round_selector);
                                else if (mItemList.size() > 1) {
                                    mItemList.get(0).setBgDrawable(R.drawable.btn_custom_top_radius);
                                    mItemList.get(mItemList.size() - 1).setBgDrawable(R.drawable.btn_custom_bottom_radius);
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                        break;
                    case CWConstant.REQUEST_URL_SET_ALARM_CLOCK: // 设置闹钟
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
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
                                    if (!NetworkUtils.isNetworkAvailable()) {
                                        RequestToastUtils.toastNetwork();
                                        return false;
                                    }
                                    CWRequestUtils.getInstance().setAlarmClock(getContext(),
                                            resultBean.getLast_online_ip(),
                                            requestBean.getToken(), requestBean.getImei(),
                                            requestBean.getD_id(), requestBean.getAlarm_clock(),
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
                                    settingsModel.setAlarm_clock(requestBean.getAlarm_clock());
                                    settingsModel.save();
                                }
                            } else if (resultBean.getCode() == CWConstant.ERROR)
                                XToastUtils.toast(R.string.send_error_prompt);
                            else
                                RequestToastUtils.toast(resultBean.getCode());
                        }
                        break;
                    case CWConstant.REQUEST_URL_GET_CURE_REMIND: // 获取吃药提醒设置
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

                                }
                                mItemList.clear();
                                if (!TextUtils.isEmpty(bean.getInfo())) {
                                    String[] array = bean.getInfo().split("#");
                                    for (String str : array) {
                                        addItem(str);
                                    }
                                }
                                if (mItemList.size() == 1)
                                    mItemList.get(0).setBgDrawable(R.drawable.btn_custom_item_round_selector);
                                else if (mItemList.size() > 1) {
                                    mItemList.get(0).setBgDrawable(R.drawable.btn_custom_top_radius);
                                    mItemList.get(mItemList.size() - 1).setBgDrawable(R.drawable.btn_custom_bottom_radius);
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                        break;
                    case CWConstant.REQUEST_URL_SET_CURE_REMIND: // 设置吃药提醒
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
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
                                    if (!NetworkUtils.isNetworkAvailable()) {
                                        RequestToastUtils.toastNetwork();
                                        return false;
                                    }
                                    CWRequestUtils.getInstance().setCureRemind(getContext(),
                                            resultBean.getLast_online_ip(),
                                            requestBean.getToken(), deviceModel.getImei(),
                                            requestBean.getD_id(),
                                            requestBean.getInfo(), mHandler);
                                }
                            } else if (resultBean.getCode() == CWConstant.SUCCESS || resultBean.getCode() == CWConstant.WAIT_ONLINE_UPDATE) {
                                if (resultBean.getCode() == CWConstant.WAIT_ONLINE_UPDATE)
                                    XToastUtils.toast(R.string.wait_online_update_prompt);
                                else
                                    XToastUtils.toast(R.string.send_success_prompt);
//                                userModel = getUserModel();
//                                AAADeviceModel deviceModel = getDevice();
//                                requestBean =
//                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), AAATrackRequestBeanOldEdition.class);
//                                if (userModel != null && deviceModel != null && deviceModel.getD_id() == requestBean.getD_id()) {
//
//                                }
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

}
