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
import android.widget.CompoundButton;

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
import com.yyt.trackcar.bean.SectionMultiItem;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.DeviceSettingsModel;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.ui.adapter.BanClassesAdapter;
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
 * @ fileName:      ForbiddenTimeFragment
 * @ author:        QING
 * @ createTime:    2020/3/20 16:11
 * @ describe:      TODO 禁用时间页面
 */
@Page(name = "ForbiddenTime", params = {CWConstant.MODEL, CWConstant.CONTENT})
public class ForbiddenTimeFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener, CompoundButton.OnCheckedChangeListener {
    @BindView(R.id.switchBtn)
    Button mSwitchBtn; // 开启/关闭按钮
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView; // RecyclerView
    private BanClassesAdapter mAdapter; // 适配器
    private List<SectionMultiItem> mItemList = new ArrayList<>(); // 列表
    @AutoWired
    String model; // 禁用时间段
    @AutoWired
    String content; // 禁用内容
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
        titleBar.setTitle(getString(R.string.ban_classes_time_slot));
        if (!TextUtils.isEmpty(content) && !TextUtils.isEmpty(model)) {
            String[] array = model.split("#");
            if (array.length >= 2)
                titleBar.addAction(new TitleBar.TextAction(getString(R.string.del)) {
                    @Override
                    public void performAction(View view) {
                        String disabledIncalss = "";
                        for (String str : array) {
                            if (!"0".equals(str))
                                disabledIncalss = String.format("%s#%s", disabledIncalss, str);
                        }
                        if (!TextUtils.isEmpty(disabledIncalss))
                            disabledIncalss = disabledIncalss.substring(1);
                        setDisabledIncalss(disabledIncalss);
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
            content = String.format("%s|08:00-11:30|0|1111111",
                    getString(R.string.ban_classes_time_slot));
            array = content.split("\\|");
        }
        mItemList.add(new SectionMultiItem(true, null));
        BaseItemBean itemBean = new BaseItemBean(0, getString(R.string.name), array[0]);
        itemBean.setBgDrawable(R.drawable.btn_custom_item_round_selector);
        itemBean.setHasArrow(true);
        mItemList.add(new SectionMultiItem(0, itemBean));

        mItemList.add(new SectionMultiItem(true, null));
//        itemBean = new BaseItemBean(1, "上午：");
//        itemBean.setSelect(true);
//        itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
//        mItemList.add(new SectionMultiItem(1, itemBean));
        itemBean = new BaseItemBean(2, getString(R.string.start_time), array[1].split("-")[0]);
        itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
        itemBean.setHasArrow(true);
        mItemList.add(new SectionMultiItem(0, itemBean));
        itemBean = new BaseItemBean(3, getString(R.string.end_time), array[1].split("-")[1]);
        itemBean.setBgDrawable(R.drawable.btn_custom_bottom_radius);
        itemBean.setHasArrow(true);
        mItemList.add(new SectionMultiItem(0, itemBean));

//        mItemList.add(new SectionMultiItem(true, null));
//        itemBean = new BaseItemBean(4, "下午：");
//        itemBean.setSelect(true);
//        itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
//        mItemList.add(new SectionMultiItem(1, itemBean));
//        itemBean = new BaseItemBean(5, "开始时间", "14:00");
//        itemBean.setHasArrow(true);
//        mItemList.add(new SectionMultiItem(0, itemBean));
//        itemBean = new BaseItemBean(6, "结束时间", "16:30");
//        itemBean.setBgDrawable(R.drawable.btn_custom_bottom_radius);
//        itemBean.setHasArrow(true);
//        mItemList.add(new SectionMultiItem(0, itemBean));
//
//        mItemList.add(new SectionMultiItem(true, null));
//        itemBean = new BaseItemBean(7, "晚上：");
//        itemBean.setSelect(true);
//        itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
//        mItemList.add(new SectionMultiItem(1, itemBean));
//        itemBean = new BaseItemBean(8, "开始时间", "");
//        itemBean.setHasArrow(true);
//        mItemList.add(new SectionMultiItem(0, itemBean));
//        itemBean = new BaseItemBean(9, "结束时间", "");
//        itemBean.setBgDrawable(R.drawable.btn_custom_bottom_radius);
//        itemBean.setHasArrow(true);
//        mItemList.add(new SectionMultiItem(0, itemBean));

        mItemList.add(new SectionMultiItem(true, null));
        itemBean = new BaseItemBean(10, getString(R.string.repeat),
                TimeUtils.getWeekDescription(getContext(), array[3]));
        itemBean.setGroup(array[3]);
        itemBean.setBgDrawable(R.drawable.btn_custom_item_round_selector);
        itemBean.setHasArrow(true);
        mItemList.add(new SectionMultiItem(0, itemBean));
        mItemList.add(new SectionMultiItem(true, null));

    }

    /**
     * 初始化适配器
     */
    private void initAdapters() {
        mAdapter = new BanClassesAdapter(mItemList, this);
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
                    String content = "";
                    for (SectionMultiItem item : mItemList) {
                        BaseItemBean bean = item.t;
                        if (bean != null && ((bean.getType() == 2 && itemBean.getType() == 3) ||
                                (bean.getType() == 3 && itemBean.getType() == 2))) {
                            content = bean.getContent();
                            break;
                        }
                    }
                    Date otherDate = TimeUtils.formatUTC(content, "HH:mm");
                    if (itemBean.getType() == 2 && otherDate != null && date.getTime() > otherDate.getTime())
                        XToastUtils.toast(R.string.start_more_than_end_time_prompt);
                    else if (itemBean.getType() == 3 && otherDate != null && otherDate.getTime() > date.getTime())
                        XToastUtils.toast(R.string.end_less_than_start_time_prompt);
                    else {
                        itemBean.setContent(TimeUtils.formatUTC(date.getTime(), "HH:mm"));
                        mAdapter.notifyDataSetChanged();
                    }
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
     * 设置上课禁用
     *
     * @param disabledIncalss 上课禁用
     */
    private void setDisabledIncalss(String disabledIncalss) {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork();
            return;
        }
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().setDisabledInclass(getContext(), getIp(),
                    userModel.getToken(), deviceModel.getImei(), deviceModel.getD_id(),
                    disabledIncalss, mHandler);
    }

    @SingleClick
    @OnClick({R.id.switchBtn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switchBtn: // 保存
                StringBuilder banString = new StringBuilder();
                for (SectionMultiItem item : mItemList) {
                    BaseItemBean itemBean = item.t;
                    if (itemBean != null) {
                        switch (itemBean.getType()) {
                            case 0: // 名称
                                banString.append(itemBean.getContent());
                                break;
                            case 2: // 开始时间
                                banString.append("|").append(itemBean.getContent());
                                break;
                            case 3: // 结束时间
                                banString.append("-").append(itemBean.getContent()).append("|");
//                                if (TextUtils.isEmpty(content))
//                                    banString.append("0");
//                                else {
//                                    String[] array = content.split("\\|");
//                                    if (array.length == 4)
//                                        banString.append(array[2]);
//                                    else
//                                        banString.append("0");
//                                }
                                banString.append("1");
                                banString.append("|");
                                break;
                            case 10: // 重复
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
                                banString.append(week);
                                break;
                            default:
                                break;
                        }
                    }
                }
                String[] contentArray = banString.toString().split("\\|");
                String[] array = model.split("#");
                for (int i = 0; i < array.length; i++) {
                    if ("0".equals(array[i]))
                        array[i] = banString.toString();
                    else {
                        String[] subArray = array[i].split("\\|");
//                        if (subArray.length == 4 && contentArray.length == 4) {
//                            if (subArray[0].equals(contentArray[0])) {
//                                XToastUtils.toast(R.string.name_equal_prompt);
//                                return;
//                            } else {
//                                String[] firstTimeArray = subArray[1].split("-");
//                                String[] secondTimeArray = contentArray[1].split("-");
//                                if (firstTimeArray.length == 2 && secondTimeArray.length == 2) {
//                                    if (!(secondTimeArray[0].compareTo(firstTimeArray[1]) > 0 ||
//                                            secondTimeArray[1].compareTo(firstTimeArray[0]) < 0)) {
//                                        XToastUtils.toast(R.string.time_equal_prompt);
//                                        return;
//                                    }
//                                }
//                            }
//                        }
                    }
                }
                String disabledIncalss = "";
                for (String str : array) {
                    disabledIncalss = String.format("%s#%s", disabledIncalss, str);
                }
                if (!TextUtils.isEmpty(disabledIncalss))
                    disabledIncalss = disabledIncalss.substring(1);
                setDisabledIncalss(disabledIncalss);
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
                        bundle.putInt(CWConstant.TYPE, 5);
                        bundle.putString(CWConstant.CONTENT, itemBean.getContent());
                        openNewPageForResult(CustomInputSecondFragment.class, bundle,
                                CWConstant.REQUEST_FORBIDDEN_TIME_NAME);
                        break;
                    case 2: // 开始时间
                    case 3: // 结束时间
                        showTimePickerDialog(itemBean);
                        break;
                    case 10: // 重复
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
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

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
                        for (SectionMultiItem item : mItemList) {
                            itemBean = item.t;
                            if (itemBean != null && itemBean.getType() == 0) {
                                itemBean.setContent(bundle.getString(CWConstant.NAME));
                                mAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                        break;
                    case CWConstant.REQUEST_FORBIDDEN_TIME_WEEK: // 重复
                        for (SectionMultiItem item : mItemList) {
                            itemBean = item.t;
                            if (itemBean != null && itemBean.getType() == 10) {
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
                    case CWConstant.REQUEST_URL_SET_DISABLED_INCLASS: // 设置上课禁用
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
                                    CWRequestUtils.getInstance().setDisabledInclass(getContext(),
                                            resultBean.getLast_online_ip(),
                                            requestBean.getToken(), requestBean.getImei(),
                                            requestBean.getD_id(), requestBean.getDisabledInClass(),
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
                                    settingsModel.setDisabledInClass(requestBean.getDisabledInClass());
                                    settingsModel.save();
                                }
                                Intent intent = new Intent();
                                Bundle bundle = new Bundle();
                                bundle.putString(CWConstant.MODEL,
                                        requestBean.getDisabledInClass());
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
