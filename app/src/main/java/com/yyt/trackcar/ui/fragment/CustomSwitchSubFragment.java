package com.yyt.trackcar.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.CompoundButton;

import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.widget.actionbar.TitleBar;
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
import com.yyt.trackcar.ui.adapter.CustomSwitchSubAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CWRequestUtils;
import com.yyt.trackcar.utils.RequestToastUtils;
import com.yyt.trackcar.utils.XToastUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      CustomSwitchSubFragment
 * @ author:        QING
 * @ createTime:    2020/3/13 16:53
 * @ describe:      TODO 子标题开关页面
 */
@Page(name = "CustomSwitchSub", params = {CWConstant.TYPE, CWConstant.TITLE, CWConstant.CONTENT})
public class CustomSwitchSubFragment extends BaseFragment implements CompoundButton.OnCheckedChangeListener {
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView; // RecyclerView
    private CustomSwitchSubAdapter mAdapter; // 适配器
    private List<SectionItem> mItemList = new ArrayList<>(); // 列表
    @AutoWired
    String title; // 标题
    @AutoWired
    String content; // 内容
    @AutoWired
    int type; // 类型 0拨号键盘 1禁止关机

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycler_view;
    }

    @Override
    protected void initArgs() {
        XRouter.getInstance().inject(this);
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(title);
        return titleBar;
    }

    @Override
    protected void initViews() {
        initItems();
        initAdapters();
        initRecyclerViews();
    }

    /**
     * 初始化列表信息
     */
    private void initItems() {
        BaseItemBean itemBean;
        switch (type) {
            case 0: // 拨号键盘
                mItemList.add(new SectionItem(true, null));
                itemBean = new BaseItemBean(0, getString(R.string.allow_watch_use),
                        getString(R.string.allow_watch_use_content));
                itemBean.setSelect("1".equals(content));
                itemBean.setBgDrawable(R.drawable.bg_white_round);
                mItemList.add(new SectionItem(itemBean));
                mItemList.add(new SectionItem(true, null));
                break;
            case 1: // 禁止关机
                mItemList.add(new SectionItem(true, null));
                itemBean = new BaseItemBean(0, getString(R.string.disable_watch_shutdown),
                        getString(R.string.disable_watch_shutdown_content));
                itemBean.setSelect("1".equals(content));
                itemBean.setBgDrawable(R.drawable.bg_white_round);
                mItemList.add(new SectionItem(itemBean));
                mItemList.add(new SectionItem(true, null));
                break;
            default:
                break;
        }
    }

    /**
     * 初始化适配器
     */
    private void initAdapters() {
        mAdapter = new CustomSwitchSubAdapter(mItemList, this);
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
     * 拨号盘的打开关闭+
     */
    public void setDialPad() {
        if(!NetworkUtils.isNetworkAvailable()){
            RequestToastUtils.toastNetwork();
            return;
        }
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().setDialPad(getContext(), getIp(),
                    userModel.getToken(), deviceModel.getD_id(), deviceModel.getImei(),
                    content, mHandler);
    }

    /**
     * 设置禁止关机
     */
    private void setOther() {
        if(!NetworkUtils.isNetworkAvailable()){
            RequestToastUtils.toastNetwork();
            return;
        }
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null) {
            DeviceSettingsModel settingsModel = getDeviceSettings();
            String other = "";
            if (!TextUtils.isEmpty(settingsModel.getOther())) {
                String[] array = settingsModel.getOther().split(",");
                if (array.length >= 2)
                    array[1] = content;
                for (String str : array) {
                    other = String.format("%s,%s", other, str);
                }
            }
            if (TextUtils.isEmpty(other))
                other = String.format("5,%s,0,06:00|22:00|1,20|0", content);
            else
                other = other.substring(1);
            CWRequestUtils.getInstance().setOther(getContext(), getIp(),
                    userModel.getToken(), deviceModel.getImei(), deviceModel.getD_id(), other,
                    mHandler);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int itemType = (int) buttonView.getTag();
        for (int i = 0; i < mItemList.size(); i++) {
            BaseItemBean itemBean = mItemList.get(i).t;
            if (itemBean != null && itemBean.getType() == itemType) {
                itemBean.setSelect(isChecked);
                switch (type) {
                    case 0: // 拨号键盘
                        content = isChecked ? "1" : "0";
                        setDialPad();
                        break;
                    case 1: // 禁止关机
                        content = isChecked ? "1" : "0";
                        setOther();
                        break;
                    default:
                        break;
                }
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
                    case CWConstant.REQUEST_URL_SET_DIAL_PAD: // 拨号盘的打开关闭+
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
                                    if(!NetworkUtils.isNetworkAvailable()){
                                        RequestToastUtils.toastNetwork();
                                        return false;
                                    }
                                    CWRequestUtils.getInstance().setDialPad(getContext(),
                                            resultBean.getLast_online_ip(),
                                            requestBean.getToken(), requestBean.getD_id(),
                                            requestBean.getImei(), requestBean.getType(),
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
                                    settingsModel.setDial_pad(requestBean.getType());
                                    settingsModel.save();
                                }
                            } else if (resultBean.getCode() == CWConstant.ERROR)
                                XToastUtils.toast(R.string.send_error_prompt);
                            else
                                RequestToastUtils.toast(resultBean.getCode());
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
                                    if(!NetworkUtils.isNetworkAvailable()){
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
    public void popToBack() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        switch (type) {
            case 0: // 拨号键盘
            case 1: // 禁止关机
                for (SectionItem item : mItemList) {
                    BaseItemBean itemBean = item.t;
                    if (itemBean != null && itemBean.getType() == 0) {
                        content = itemBean.isSelect() ? "1" : "0";
                        break;
                    }
                }
                bundle.putString(CWConstant.CONTENT, content);
                break;
            default:
                break;
        }
        intent.putExtras(bundle);
        setFragmentResult(Activity.RESULT_OK, intent);
        super.popToBack();
    }

}
