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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.MainApplication;
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
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      BanClassesFragment
 * @ author:        QING
 * @ createTime:    2020/3/13 12:14
 * @ describe:      TODO 上课禁用
 */
@Page(name = "BanClasses")
public class BanClassesFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener, CompoundButton.OnCheckedChangeListener {
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView; // RecyclerView
    private BanClassesAdapter mAdapter; // 适配器
    private List<SectionMultiItem> mItemList = new ArrayList<>(); // 列表

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycler_view_plus;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(R.string.ban_classes);
        return titleBar;
    }

    @Override
    protected void initViews() {
//        BaseItemBean itemBean = new BaseItemBean(0, "禁用期间联系家长", "允许");
//        itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
//        itemBean.setHasArrow(true);
//        mItemList.add(new SectionMultiItem(0, itemBean));
//        itemBean = new BaseItemBean(1, "法定节假日不禁用", "是");
//        itemBean.setBgDrawable(R.drawable.btn_custom_bottom_radius);
//        itemBean.setHasArrow(true);
//        mItemList.add(new SectionMultiItem(0, itemBean));
//
//        mItemList.add(new SectionMultiItem(true, null));
        initItems();
        initAdapters();
        initRecyclerViews();
        initHeaderView();
        initFooterView();
        getDisabledIncalss();
    }

    /**
     * 初始化列表信息
     */
    private void initItems() {
        DeviceSettingsModel settingsModel = getDeviceSettings();
        if (settingsModel != null && !TextUtils.isEmpty(settingsModel.getDisabledInClass())) {
            String[] array = settingsModel.getDisabledInClass().split("#");
            for (String str : array) {
                addItem(str);
            }
        }
        if (mItemList.size() == 0)
            addItem(String.format("%s|08:00-11:30|0|1111111",
                    getString(R.string.ban_classes_time_slot)));
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
     * 初始化头布局
     */
    private void initHeaderView() {
        View headerView = getLayoutInflater().inflate(R.layout.header_view_info_second,
                mRecyclerView, false);
        ImageView ivBg = headerView.findViewById(R.id.ivBg);
        TextView tvTitle = headerView.findViewById(R.id.tvTitle);
        TextView tvContent = headerView.findViewById(R.id.tvContent);
        ivBg.setImageResource(R.mipmap.bg_ban_classes);
        tvTitle.setText(R.string.ban_classes_title);
        tvContent.setText(R.string.ban_classes_content);
        mAdapter.addHeaderView(headerView);
    }

    /**
     * 初始化脚布局
     */
    private void initFooterView() {
        View footerView = getLayoutInflater().inflate(R.layout.footer_view_text, mRecyclerView,
                false);
        TextView tvContent = footerView.findViewById(R.id.tvContent);
        tvContent.setText(R.string.ban_classes_prompt);
        mAdapter.addFooterView(footerView);
    }

    /**
     * 添加选项
     *
     * @param banString 禁用选项
     */
    private void addItem(String banString) {
        String[] array = banString.split("\\|");
        if (array.length == 4) {
            BaseItemBean itemBean = new BaseItemBean(mItemList.size(), array[0]);
            if (TextUtils.isEmpty(itemBean.getTitle()))
                itemBean.setTitle(getString(R.string.ban_classes_time_slot));
            itemBean.setSelect("1".equals(array[2]));
            itemBean.setGroup(banString);
            itemBean.setBgDrawable(R.drawable.bg_custom_top_radius);
            mItemList.add(new SectionMultiItem(1, itemBean));
            itemBean = new BaseItemBean(String.format("%s\n%s：%s", array[1],
                    getString(R.string.repeat),
                    TimeUtils.getWeekDescription(getContext(), array[3])));
            itemBean.setType(mItemList.size());
            itemBean.setGroup(banString);
            itemBean.setBgDrawable(R.drawable.btn_custom_bottom_radius);
            itemBean.setHasArrow(true);
            mItemList.add(new SectionMultiItem(2, itemBean));
            mItemList.add(new SectionMultiItem(true, null));
        }
    }

    /**
     * 获取上课禁用
     */
    private void getDisabledIncalss() {
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().getDisabledIncalss(getContext(), getIp(),
                    userModel.getToken(), deviceModel.getD_id(), mHandler);
    }

    /**
     * 设置上课禁用
     */
    private void setDisabledIncalss() {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork();
            return;
        }
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null) {
            String banString = "";
            for (SectionMultiItem item : mItemList) {
                BaseItemBean itemBean = item.t;
                if (itemBean != null && itemBean.getType() % 3 == 1)
                    banString = String.format("%s#%s", banString, itemBean.getGroup());
            }
            if (!TextUtils.isEmpty(banString))
                banString = banString.substring(1);
            String settingString = "";
            DeviceSettingsModel settingsModel = getDeviceSettings();
            if (settingsModel != null && !TextUtils.isEmpty(settingsModel.getDisabledInClass()))
                settingString = settingsModel.getDisabledInClass();
            if (!settingString.equals(banString))
                CWRequestUtils.getInstance().setDisabledInclass(MainApplication.getContext(),
                        getIp(), userModel.getToken(), deviceModel.getImei(), deviceModel.getD_id(),
                        banString, mHandler);
        }
    }

    @SingleClick
    @OnClick({R.id.fabAdd})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabAdd: // 添加
                String banString = "";
                int num = 0;
                for (SectionMultiItem item : mItemList) {
                    BaseItemBean bean = item.t;
                    if (bean != null && bean.getType() % 3 == 1) {
                        num++;
                        if (num >= 4) {
                            XToastUtils.toast(R.string.ban_classes_to_max_prompt);
                            return;
                        }
                        banString = String.format("%s#%s", banString, bean.getGroup());
                    }
                }
                if (TextUtils.isEmpty(banString))
                    banString = "0";
                else {
                    banString = String.format("%s#0", banString);
                    banString = banString.substring(1);
                }
                Bundle bundle = new Bundle();
                bundle.putString(CWConstant.MODEL, banString);
                bundle.putString(CWConstant.CONTENT, "");
                openNewPageForResult(ForbiddenTimeFragment.class, bundle,
                        CWConstant.REQUEST_FORBIDDEN_TIME);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (position >= 0 && position < mItemList.size()) {
            BaseItemBean itemBean = mItemList.get(position).t;
            if (itemBean != null && mItemList.get(position).getItemType() == 2) {
                String banString = "";
                for (SectionMultiItem item : mItemList) {
                    BaseItemBean bean = item.t;
                    if (bean != null && bean.getType() == itemBean.getType())
                        banString = String.format("%s#0", banString);
                    else if (bean != null && bean.getType() % 3 == 1)
                        banString = String.format("%s#%s", banString, bean.getGroup());
                }
                if (!TextUtils.isEmpty(banString))
                    banString = banString.substring(1);
                Bundle bundle = new Bundle();
                bundle.putString(CWConstant.MODEL, banString);
                bundle.putString(CWConstant.CONTENT, itemBean.getGroup());
                openNewPageForResult(ForbiddenTimeFragment.class, bundle,
                        CWConstant.REQUEST_FORBIDDEN_TIME);
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int type = (int) buttonView.getTag();
        for (int i = 0; i < mItemList.size(); i++) {
            BaseItemBean itemBean = mItemList.get(i).t;
            if (itemBean != null && itemBean.getType() == type) {
                itemBean.setSelect(isChecked);
                String[] array = itemBean.getGroup().split("\\|");
                String banString = String.format("%s|%s|%s|%s", array[0], array[1], isChecked ?
                        "1" : "0", array[3]);
                if ((i + 1) < mItemList.size()) {
                    BaseItemBean bean = mItemList.get(i + 1).t;
                    if (bean != null && bean.getType() == type + 1 && itemBean.getGroup().equals(bean.getGroup()))
                        bean.setGroup(banString);
                }
                itemBean.setGroup(banString);
                setDisabledIncalss();
                break;
            }
        }
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Intent data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == CWConstant.REQUEST_FORBIDDEN_TIME && resultCode == Activity.RESULT_OK && data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                String banString = bundle.getString(CWConstant.MODEL);
                mItemList.clear();
                if (banString != null) {
                    String[] array = banString.split("#");
                    for (String str : array) {
                        addItem(str);
                    }
                }
                if (mItemList.size() == 0)
                    addItem(String.format("%s|08:00-11:30|0|1111111",
                            getString(R.string.ban_classes_time_slot)));
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
                    case CWConstant.REQUEST_URL_GET_DISABLED_INCLASS: // 获取上课禁用
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
                                    settingsModel.setDisabledInClass(bean.getDisabledInClass());
                                    settingsModel.save();
                                }
                                mItemList.clear();
                                if (!TextUtils.isEmpty(bean.getDisabledInClass())) {
                                    String[] array = bean.getDisabledInClass().split("#");
                                    for (String str : array) {
                                        addItem(str);
                                    }
                                }
                                if (mItemList.size() == 0)
                                    addItem(String.format("%s|08:00-11:30|0|1111111",
                                            getString(R.string.ban_classes_time_slot)));
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                        break;
                    case CWConstant.REQUEST_URL_SET_DISABLED_INCLASS: // 设置上课禁用
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
    public void onDestroyView() {
//        setDisabledIncalss();
        super.onDestroyView();
    }

}
