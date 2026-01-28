package com.yyt.trackcar.ui.fragment;

import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.picker.widget.OptionsPickerView;
import com.xuexiang.xui.widget.picker.widget.TimePickerView;
import com.xuexiang.xui.widget.picker.widget.builder.OptionsPickerBuilder;
import com.xuexiang.xui.widget.picker.widget.builder.TimePickerBuilder;
import com.xuexiang.xui.widget.picker.widget.listener.OnOptionsSelectListener;
import com.xuexiang.xui.widget.picker.widget.listener.OnTimeSelectListener;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.BaseItemBean;
import com.yyt.trackcar.bean.RequestBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.bean.ResultBean;
import com.yyt.trackcar.bean.SectionMultiItem;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.DeviceSettingsModel;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.ui.adapter.SedentaryReminderAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CWRequestUtils;
import com.yyt.trackcar.utils.RequestToastUtils;
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
 * @ fileName:      SedentaryReminderFragment
 * @ author:        QING
 * @ createTime:    2020/10/22 13:16
 * @ describe:      TODO 久坐提醒页面
 */
@Page(name = "SedentaryReminder")
public class SedentaryReminderFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener, CompoundButton.OnCheckedChangeListener {
    @BindView(R.id.switchBtn)
    Button mSwitchBtn; // 开启/关闭按钮
    @BindView(R.id.cancelBtn)
    Button mCancelBtn; // 取消按钮
    @BindView(R.id.saveBtn)
    Button mSaveBtn; // 保存按钮
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView; // RecyclerView
    private SedentaryReminderAdapter mAdapter; // 适配器
    private List<SectionMultiItem> mItemList = new ArrayList<>(); // 列表
    private boolean mIsOpen; // 是否开启
    private TimePickerView mTimePickerDialog; // 时间选择器
    private String[] mTimeOption;
    private OptionsPickerView mOptionsPickerView; // 选择器

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycler_time_switch;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(R.string.sedentary_reminder);
        return titleBar;
    }

    @Override
    protected void initViews() {
        initItems();
        initAdapters();
        initRecyclerViews();
        initHeaderView();
        setSwitch();
        getSedentaryRemind();
    }

    /**
     * 初始化列表信息
     */
    private void initItems() {
        String startTime = "09:00";
        String endTime = "21:00";
        BaseItemBean itemBean = new BaseItemBean(0, getString(R.string.start_time),
                startTime);
        itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
        itemBean.setHasArrow(true);
        mItemList.add(new SectionMultiItem(0, itemBean));
        itemBean = new BaseItemBean(1, getString(R.string.end_time), endTime);
        itemBean.setBgDrawable(R.drawable.btn_custom_bottom_radius);
        itemBean.setHasArrow(true);
        mItemList.add(new SectionMultiItem(0, itemBean));
        itemBean = new BaseItemBean(2, getString(R.string.sedentary_reminder_no_disturb),
                getString(R.string.sedentary_reminder_no_disturb_content, "12:00", "14:00"));
        itemBean.setGroup("12:00-14:00");
        itemBean.setBgDrawable(R.drawable.btn_custom_bottom_radius);
        itemBean.setHasArrow(true);
        mItemList.add(new SectionMultiItem(1, itemBean));
    }

    /**
     * 初始化适配器
     */
    private void initAdapters() {
        mAdapter = new SedentaryReminderAdapter(mItemList, this);
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
        View headerView = getLayoutInflater().inflate(R.layout.header_view_info_third,
                mRecyclerView, false);
        ImageView ivBg = headerView.findViewById(R.id.ivBg);
//        TextView tvTitle = headerView.findViewById(R.id.tvTitle);
//        TextView tvContent = headerView.findViewById(R.id.tvContent);
        ivBg.setImageResource(R.mipmap.bg_sedentary_reminder);
//        tvTitle.setText(R.string.refuse_strangers_title);
//        tvTitle.setTextColor(ContextCompat.getColor(mActivity,R.color.black));
//        tvContent.setText(R.string.sedentary_reminder_content);
//        tvContent.setTextColor(ContextCompat.getColor(mActivity,R.color.black));
        mAdapter.addHeaderView(headerView);
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
    public void showTTimePickerDialog(final BaseItemBean itemBean) {
        if (mOptionsPickerView == null || !mOptionsPickerView.isShowing()) {
            if (mTimeOption == null) {
                String[] time = new String[24 * 60 / 15];
                int point, hour, min;
                for (int i = 0; i < time.length; i++) {
                    point = i * 15;
                    hour = point / 60;
                    min = point - hour * 60;
                    time[i] = (hour <= 9 ? "0" + hour : "" + hour) + ":" + (min <= 9 ? "0" + min
                            : "" + min);
                }
                mTimeOption = time;
            }
            int firstOption = 0;
            int secondOption = 0;
            String startTime = "12:00";
            String endTime = "14:00";
            if (!TextUtils.isEmpty(itemBean.getGroup())) {
                String[] array = itemBean.getGroup().split("-");
                if (array.length == 2) {
                    startTime = array[0];
                    endTime = array[1];
                }
            }
            for (int i = 0; i < mTimeOption.length; i++) {
                if (startTime.equals(mTimeOption[i]))
                    firstOption = i;
                else if (endTime.equals(mTimeOption[i]))
                    secondOption = i;
            }
            mOptionsPickerView = new OptionsPickerBuilder(mActivity, new OnOptionsSelectListener() {

                @Override
                public void onOptionsSelect(int options1, int options2, int options3, View v) {
                    if (options1 > options2)
                        XToastUtils.toast(R.string.start_more_than_end_time_prompt);
                    else {
                        itemBean.setContent(getString(R.string.sedentary_reminder_no_disturb_content, mTimeOption[options1], mTimeOption[options2]));
                        itemBean.setGroup(mTimeOption[options1] + "-" + mTimeOption[options2]);
                        mAdapter.notifyDataSetChanged();
                        mCancelBtn.setVisibility(View.VISIBLE);
                        mSaveBtn.setVisibility(View.VISIBLE);
                        mSwitchBtn.setVisibility(View.GONE);
                    }
                }
            })
                    .setTitleText(itemBean.getTitle())
                    .setSelectOptions(firstOption, secondOption)
                    .build();
            mOptionsPickerView.setNPicker(mTimeOption, mTimeOption);
            mOptionsPickerView.show();
        }
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
                    String startTime = "09:00";
                    String endTime = "21:00";
                    for (SectionMultiItem item : mItemList) {
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
                    long startDate = TimeUtils.formatUTC(startTime, "HH:mm").getTime();
                    long endDate = TimeUtils.formatUTC(endTime, "HH:mm").getTime();
                    if (startDate > endDate) {
                        XToastUtils.toast(R.string.start_more_than_end_time_prompt);
                        itemBean.setContent(time);
                        return;
                    } else if (endDate - startDate < TimeUtils.HOUR * 1000) {
                        XToastUtils.toast(R.string.settings_less_than_hour);
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
     * 获取久坐提醒设置
     */
    public void getSedentaryRemind() {
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().getSedentaryRemind(getContext(), getIp(),
                    userModel.getToken(), deviceModel.getD_id(), mHandler);
    }

    /**
     * 设置久坐提醒
     */
    public void setSedentaryRemind() {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork();
            return;
        }
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null) {
            String info = "";
            String startTime = "09:00";
            String endTime = "21:00";
            String otherTime = "12:00-14:00";
            boolean isOpen = false;
            for (SectionMultiItem item : mItemList) {
                BaseItemBean itemBean = item.t;
                if (itemBean != null) {
                    switch (itemBean.getType()) {
                        case 0: // 开始时间
                            startTime = itemBean.getContent();
                            break;
                        case 1: // 结束时间
                            endTime = itemBean.getContent();
                            break;
                        case 2: // 免打扰
                            otherTime = itemBean.getGroup();
                            isOpen = itemBean.isSelect();
                            break;
                        default:
                            break;
                    }
                }
            }
            info = String.format("%s|%s-%s|1111111|%s|%s|%s",
                    getString(R.string.sedentary_reminder), startTime, endTime,
                    mIsOpen ? "1" : "0", otherTime, isOpen ? "1" : "0");
            CWRequestUtils.getInstance().setSedentaryRemind(getContext(), getIp(),
                    userModel.getToken(), deviceModel.getImei(), deviceModel.getD_id(), info,
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
                setSedentaryRemind();
                break;
            case R.id.cancelBtn: // 取消
                mSwitchBtn.setVisibility(View.VISIBLE);
                mCancelBtn.setVisibility(View.GONE);
                mSaveBtn.setVisibility(View.GONE);
                break;
            case R.id.saveBtn: // 保存
                setSedentaryRemind();
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
            if (itemBean != null && itemBean.getType() < 2)
                showTimePickerDialog(itemBean);
            else if (itemBean != null && itemBean.getType() == 2)
                showTTimePickerDialog(itemBean);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int type = (int) buttonView.getTag();
        for (int i = 0; i < mItemList.size(); i++) {
            BaseItemBean itemBean = mItemList.get(i).t;
            if (itemBean != null && itemBean.getType() == type) {
                itemBean.setSelect(isChecked);
                mCancelBtn.setVisibility(View.VISIBLE);
                mSaveBtn.setVisibility(View.VISIBLE);
                mSwitchBtn.setVisibility(View.GONE);
                break;
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
                    case CWConstant.REQUEST_URL_GET_SEDENTARY_REMIND: // 获取久坐提醒设置
                        if (msg.obj != null) {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                userModel = getUserModel();
                                DeviceModel deviceModel = getDevice();
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                ResultBean bean =
                                        mGson.fromJson(mGson.toJson(resultBean.getResultBean()),
                                                ResultBean.class);
                                if (userModel != null && deviceModel != null && deviceModel.getD_id() == requestBean.getD_id()) {
                                    String info = bean.getInfo();
                                    String startTime = "09:00";
                                    String endTime = "21:00";
                                    String otherTime = "12:00-14:00";
                                    boolean isOpen = false;
                                    if (!TextUtils.isEmpty(info)) {
                                        String[] array = info.split("\\|");
                                        if (array.length >= 5) {
                                            mIsOpen = "1".equals(array[3]);
                                            String[] subArray = array[1].split("-");
                                            if (subArray.length >= 2) {
                                                if (TimeUtils.formatUTC(subArray[0], "HH:mm") != null)
                                                    startTime = subArray[0];
                                                if (TimeUtils.formatUTC(subArray[1], "HH:mm") != null)
                                                    endTime = subArray[1];
                                            }
                                            subArray = array[4].split("-");
                                            if (subArray.length == 2) {
                                                if (TimeUtils.formatUTC(subArray[0], "HH:mm") != null && TimeUtils.formatUTC(subArray[1], "HH:mm") != null)
                                                    otherTime = array[4];
                                            }
                                            if (array.length >= 6 && "1".equals(array[5]))
                                                isOpen = true;
                                        } else
                                            mIsOpen = false;
                                    } else
                                        mIsOpen = false;
                                    for (SectionMultiItem item : mItemList) {
                                        BaseItemBean itemBean = item.t;
                                        if (itemBean != null) {
                                            switch (itemBean.getType()) {
                                                case 0: // 开机时间
                                                    itemBean.setContent(startTime);
                                                    break;
                                                case 1: // 关机时间
                                                    itemBean.setContent(endTime);
                                                    break;
                                                case 2: // 关机时间
                                                    String[] subArray = otherTime.split("-");
                                                    itemBean.setContent(getString(R.string.sedentary_reminder_no_disturb_content, subArray[0], subArray[1]));
                                                    itemBean.setGroup(otherTime);
                                                    itemBean.setSelect(isOpen);
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
                    case CWConstant.REQUEST_URL_SET_SEDENTARY_REMIND: // 设置久坐提醒
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
                                    CWRequestUtils.getInstance().setSedentaryRemind(getContext(),
                                            resultBean.getLast_online_ip(),
                                            requestBean.getToken(), requestBean.getImei(),
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

    @Override
    public void onDestroy() {
        if (mTimePickerDialog != null && mTimePickerDialog.isShowing())
            mTimePickerDialog.dismiss();
        super.onDestroy();
    }

}
