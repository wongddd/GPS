package com.yyt.trackcar.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.picker.widget.TimePickerView;
import com.xuexiang.xui.widget.picker.widget.builder.TimePickerBuilder;
import com.xuexiang.xui.widget.picker.widget.listener.OnTimeSelectListener;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.BuildConfig;
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
 * @ fileName:      SetAlarmClockFragment
 * @ author:        QING
 * @ createTime:    2020/3/20 17:22
 * @ describe:      TODO 闹钟设置页面
 */
@Page(name = "SetAlarmClock", params = {CWConstant.MODEL, CWConstant.CONTENT})
public class SetAlarmClockFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.switchBtn)
    Button mSwitchBtn; // 开启/关闭按钮
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView; // RecyclerView
    private CustomTextAdapter mAdapter; // 适配器
    private List<SectionItem> mItemList = new ArrayList<>(); // 列表
    @AutoWired
    String model; // 闹钟设置
    @AutoWired
    String content; // 选择的闹钟
    private TimePickerView mTimePickerDialog; // 时间选择器

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycler_view_btn;
    }

    @Override
    protected void initArgs() {
        XRouter.getInstance().inject(this);
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 1)
            titleBar.setTitle(R.string.set_alarm_and_prompt);
        else
            titleBar.setTitle(R.string.set_alarm_clock);
        if (!TextUtils.isEmpty(content) && !TextUtils.isEmpty(model)) {
            String[] array = model.split("#");
//            if (array.length >= 2)
            titleBar.addAction(new TitleBar.TextAction(getString(R.string.del)) {
                @Override
                public void performAction(View view) {
                    StringBuilder alarmString = new StringBuilder();
                    for (String str : array) {
                        if (!"0".equals(str))
                            alarmString.append("#").append(str);
                    }
                    if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 1)
                        if (alarmString.length() == 0)
                            setCureRemind("");
                        else
                            setCureRemind(alarmString.toString().substring(1));
                    else {
                        if (alarmString.length() == 0)
                            setAlarmClock("");
                        else
                            setAlarmClock(alarmString.toString().substring(1));
                    }
                }
            });
        }
        return titleBar;
    }

    @Override
    protected void initViews() {
        mSwitchBtn.setText(R.string.save);
        initItems();
        initAdapters();
        initRecyclerViews();
    }

    /**
     * 初始化列表信息
     */
    private void initItems() {
        if (model == null)
            model = "";
        if (content == null)
            content = "";
        String[] array = content.split("\\|");
        if (array.length != 4) {
            content = String.format("%s|07:00|1111111|0",
                    getString(R.string.alarm_clock));
            array = content.split("\\|");
        }
        mItemList.add(new SectionItem(true, null));
        BaseItemBean itemBean;
        if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 1)
            itemBean = new BaseItemBean(0, getString(R.string.prompt_content),
                    TextUtils.isEmpty(array[0]) ? getString(R.string.alarm_clock) : array[0]);
        else
            itemBean = new BaseItemBean(0, getString(R.string.name),
                    TextUtils.isEmpty(array[0]) ? getString(R.string.alarm_clock) : array[0]);
        itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
        itemBean.setHasArrow(true);
        mItemList.add(new SectionItem(itemBean));
        itemBean = new BaseItemBean(1, getString(R.string.time), array[1]);
        itemBean.setHasArrow(true);
        mItemList.add(new SectionItem(itemBean));
        itemBean = new BaseItemBean(2, getString(R.string.repeat),
                TimeUtils.getWeekDescription(getContext(), array[2]));
        itemBean.setGroup(array[2]);
        itemBean.setBgDrawable(R.drawable.btn_custom_bottom_radius);
        itemBean.setHasArrow(true);
        mItemList.add(new SectionItem(itemBean));

        mItemList.add(new SectionItem(true, null));
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
                    itemBean.setContent(TimeUtils.formatUTC(date.getTime(), "HH:mm"));
                    mAdapter.notifyDataSetChanged();
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

    @SingleClick
    @OnClick({R.id.switchBtn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switchBtn: // 保存
                StringBuilder alarmString = new StringBuilder();
                for (SectionItem item : mItemList) {
                    BaseItemBean itemBean = item.t;
                    if (itemBean != null) {
                        switch (itemBean.getType()) {
                            case 0: // 名称
                                alarmString.append(itemBean.getContent());
                                break;
                            case 1: // 时间
                                alarmString.append("|").append(itemBean.getContent());
                                break;
                            case 2: // 重复
                                String week = itemBean.getGroup();
                                if ("0000000".equals(week)) {
                                    XToastUtils.toast(String.format("%s%s",
                                            getString(R.string.repeat),
                                            getString(R.string.select_at_least_one_prompt)));
                                    return;
                                } else if (week.length() != 7)
                                    week = "1111111";
                                else {
                                    for (int i = 0; i < 7; i++) {
                                        String str = week.substring(i, i + 1);
                                        if (!"1".equals(str) && !"0".equals(str)) {
                                            week = "1111111";
                                            break;
                                        }
                                    }
                                }
                                alarmString.append("|").append(week);
                                break;
                            default:
                                break;
                        }
                    }
                }
                String[] contentArray = content.split("\\|");
//                if (contentArray.length == 4)
//                    alarmString.append("|").append(contentArray[3]);
//                else
//                    alarmString.append("|0");
                alarmString.append("|1");
                if (alarmString.toString().equals(content))
                    popToBack();
                String[] array = model.split("#");
                for (int i = 0; i < array.length; i++) {
                    if ("0".equals(array[i]))
                        array[i] = alarmString.toString();
                    else {
                        String[] subArray = array[i].split("\\|");
//                        if(subArray.length == 4 && contentArray.length == 4){
//                            if(subArray[0].equals(contentArray[0])){
//                                XToastUtils.toast(R.string.name_equal_prompt);
//                                return;
//                            }else
//                                if(subArray[1].equals(contentArray[1])){
//                                XToastUtils.toast(R.string.time_equal_prompt);
//                                return;
//                            }
//                        }
                    }
                }
                String alarmClock = "";
                for (String str : array) {
                    alarmClock = String.format("%s#%s", alarmClock, str);
                }
                if (!TextUtils.isEmpty(alarmClock))
                    alarmClock = alarmClock.substring(1);
                if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 1)
                    setCureRemind(alarmClock);
                else
                    setAlarmClock(alarmClock);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (position >= 0 && position < mItemList.size()) {
            BaseItemBean itemBean = mItemList.get(position).t;
            if (itemBean != null) {
                Bundle bundle;
                switch (itemBean.getType()) {
                    case 0: // 名称
                        bundle = new Bundle();
                        bundle.putString(CWConstant.TITLE, itemBean.getTitle());
                        bundle.putInt(CWConstant.TYPE, 10);
                        bundle.putString(CWConstant.CONTENT, itemBean.getContent());
                        openNewPageForResult(CustomInputSecondFragment.class, bundle,
                                CWConstant.REQUEST_FORBIDDEN_TIME_NAME);
                        break;
                    case 1: // 时间
                        showTimePickerDialog(itemBean);
                        break;
                    case 2: // 重复
                        bundle = new Bundle();
                        bundle.putString(CWConstant.TITLE, itemBean.getTitle());
                        bundle.putString(CWConstant.CONTENT, itemBean.getGroup());
                        bundle.putInt(CWConstant.TYPE, 1);
                        openNewPageForResult(CustomSelectorFragment.class, bundle,
                                CWConstant.REQUEST_FORBIDDEN_TIME_WEEK);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Intent data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                BaseItemBean itemBean;
                switch (requestCode) {
                    case CWConstant.REQUEST_FORBIDDEN_TIME_NAME: // 名称
                        for (SectionItem item : mItemList) {
                            itemBean = item.t;
                            if (itemBean != null && itemBean.getType() == 0) {
                                itemBean.setContent(bundle.getString(CWConstant.NAME));
                                mAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                        break;
                    case CWConstant.REQUEST_FORBIDDEN_TIME_WEEK: // 重复
                        for (SectionItem item : mItemList) {
                            itemBean = item.t;
                            if (itemBean != null && itemBean.getType() == 2) {
                                itemBean.setGroup(bundle.getString(CWConstant.CONTENT));
                                itemBean.setContent(TimeUtils.getWeekDescription(getContext(),
                                        itemBean.getGroup()));
                                mAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                        break;
                    default:
                        break;
                }
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
                    case CWConstant.REQUEST_URL_SET_ALARM_CLOCK: // 设置闹钟
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
                                Intent intent = new Intent();
                                Bundle bundle = new Bundle();
                                bundle.putString(CWConstant.MODEL, requestBean.getAlarm_clock());
                                intent.putExtras(bundle);
                                setFragmentResult(Activity.RESULT_OK, intent);
                                popToBack();
                            } else if (resultBean.getCode() == CWConstant.ERROR)
                                XToastUtils.toast(R.string.send_error_prompt);
                            else
                                RequestToastUtils.toast(resultBean.getCode());
                        }
                        break;
                    case CWConstant.REQUEST_URL_SET_CURE_REMIND: // 设置吃药提醒
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
                                    CWRequestUtils.getInstance().setCureRemind(getContext(),
                                            resultBean.getLast_online_ip(),
                                            requestBean.getToken(), requestBean.getImei(),
                                            requestBean.getD_id(),
                                            requestBean.getAlarm_clock(), mHandler);
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
                                }
                                Intent intent = new Intent();
                                Bundle bundle = new Bundle();
                                bundle.putString(CWConstant.MODEL, requestBean.getInfo());
                                intent.putExtras(bundle);
                                setFragmentResult(Activity.RESULT_OK, intent);
                                popToBack();
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
        super.onDestroy();
    }

}
