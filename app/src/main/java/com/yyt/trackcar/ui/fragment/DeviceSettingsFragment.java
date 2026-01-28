package com.yyt.trackcar.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.BaseItemBean;
import com.yyt.trackcar.bean.RequestBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.bean.ResultBean;
import com.yyt.trackcar.bean.SectionItem;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.DeviceSettingsModel;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.ui.adapter.CustomTextAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CWRequestUtils;
import com.yyt.trackcar.utils.DialogUtils;
import com.yyt.trackcar.utils.RequestToastUtils;
import com.yyt.trackcar.utils.XToastUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      DeviceSettingsFragment
 * @ author:        QING
 * @ createTime:    2020/3/13 16:38
 * @ describe:      TODO 手表设置页面
 */
@Page(name = "DeviceSettings")
public class DeviceSettingsFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView; // RecyclerView
    private CustomTextAdapter mAdapter; // 适配器
    private List<SectionItem> mItemList = new ArrayList<>(); // 列表

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycler_view;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(R.string.device_settings);
        return titleBar;
    }

    @Override
    protected void initViews() {
        initItems();
        initAdapters();
        initRecyclerViews();
        getDialPad();
        getOther();
    }

    /**
     * 初始化列表信息
     */
    private void initItems() {
        DeviceSettingsModel settingsModel = getDeviceSettings();
        boolean isOpen;
        boolean isShutdownOpen;
        if (settingsModel != null) {
            isOpen = "1".equals(settingsModel.getDial_pad());
            if(TextUtils.isEmpty(settingsModel.getOther()))
                isShutdownOpen = false;
            else{
                String[] array = settingsModel.getOther().split(",");
                isShutdownOpen = array.length >= 2 && "1".equals(array[1]);
            }
        } else {
            isOpen = false;
            isShutdownOpen = false;
        }
        mItemList.add(new SectionItem(true, null));
//        BaseItemBean itemBean = new BaseItemBean(0, getString(R.string.time_display_format),
//                getString(R.string.hour_system, "24"));
//        itemBean.setGroup("24");
//        itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
//        itemBean.setHasArrow(true);
//        mItemList.add(new SectionItem(itemBean));
        BaseItemBean itemBean = new BaseItemBean(1, getString(R.string.dial_pad), isOpen ?
                getString(R.string.is_open) : getString(R.string.is_close));
        itemBean.setGroup(isOpen ? "1" : "0");
        itemBean.setBgDrawable(R.drawable.btn_custom_item_round_selector);
        itemBean.setHasArrow(true);
        mItemList.add(new SectionItem(itemBean));
//        itemBean = new BaseItemBean(2, getString(R.string.alarm_clock_settings));
//        itemBean.setBgDrawable(R.drawable.btn_custom_bottom_radius);
//        itemBean.setHasArrow(true);
//        mItemList.add(new SectionItem(itemBean));

        mItemList.add(new SectionItem(true, null));
//        itemBean = new BaseItemBean(9, getString(R.string.find_device));
//        itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
//        itemBean.setHasArrow(true);
//        mItemList.add(new SectionItem(itemBean));
//        itemBean = new BaseItemBean(3, getString(R.string.voice_monitor));
//        itemBean.setHasArrow(true);
//        mItemList.add(new SectionItem(itemBean));
//        itemBean = new BaseItemBean(4, getString(R.string.remote_photo_take));
//        itemBean.setHasArrow(true);
//        mItemList.add(new SectionItem(itemBean));
        itemBean = new BaseItemBean(5, getString(R.string.remote_shutdown));
        itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
        itemBean.setHasArrow(true);
        mItemList.add(new SectionItem(itemBean));
        itemBean = new BaseItemBean(6, getString(R.string.remote_reboot));
        itemBean.setHasArrow(true);
        mItemList.add(new SectionItem(itemBean));
//        itemBean = new BaseItemBean(7, getString(R.string.reset_device));
//        itemBean.setHasArrow(true);
//        mItemList.add(new SectionItem(itemBean));
        itemBean = new BaseItemBean(8, getString(R.string.no_shutdown), isShutdownOpen ?
                getString(R.string.is_open) : getString(R.string.is_close));
        itemBean.setGroup(isShutdownOpen ? "1" : "0");
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
     * 获取拨号盘状态开关
     */
    public void getDialPad() {
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().getDialPad(getContext(), getIp(),
                    userModel.getToken(), deviceModel.getD_id(), mHandler);
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
     * 获取设置的丢失密码
     */
    public void getLostPwd() {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork();
            return;
        }
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().getLostPwd(getContext(), getIp(),
                    userModel.getToken(), deviceModel.getD_id(), mHandler);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (position >= 0 && position < mItemList.size()) {
            BaseItemBean itemBean = mItemList.get(position).t;
            if (itemBean != null) {
                Bundle bundle;
                switch (itemBean.getType()) {
                    case 0: // 时间显示格式
                        bundle = new Bundle();
                        bundle.putString(CWConstant.TITLE, itemBean.getTitle());
                        bundle.putString(CWConstant.CONTENT, itemBean.getGroup());
                        openNewPageForResult(CustomSelectorFragment.class, bundle,
                                CWConstant.REQUEST_TIME_DISPLAY_FORMAT);
                        break;
                    case 1: // 拨号键盘开关
                        bundle = new Bundle();
                        bundle.putInt(CWConstant.TYPE, 0);
                        bundle.putString(CWConstant.TITLE, itemBean.getTitle());
                        bundle.putString(CWConstant.CONTENT, itemBean.getGroup());
                        openNewPageForResult(CustomSwitchSubFragment.class, bundle,
                                CWConstant.REQUEST_DIAL_PAD);
                        break;
                    case 2: // 闹钟设置
                        openNewPage(AlarmClockFragment.class);
                        break;
                    case 3: // 语音监护
                        mMaterialDialog = DialogUtils.customInputMaterialDialog(getContext(),
                                mMaterialDialog, getString(R.string.prompt), null,
                                getString(R.string.voice_monitor_hint), null,
                                InputType.TYPE_CLASS_NUMBER
                                , 20, 1, getString(R.string.confirm), getString(R.string.cancel),
                                CWConstant.DIALOG_VOICE_MONITOR, mHandler);
                        break;
                    case 4: // 远程拍照
                    case 5: // 远程关机
                    case 6: // 远程重启
                    case 7: // 重置设备
                    case 9: // 查找手表
                        mMaterialDialog = DialogUtils.customMaterialDialog(getContext(),
                                mMaterialDialog, getString(R.string.prompt),
                                getString(R.string.instruct_send_prompt, itemBean.getTitle()),
                                getString(R.string.confirm), getString(R.string.cancel), null,
                                itemBean.getType(), mHandler);
                        break;
                    case 8: // 禁止关机
                        bundle = new Bundle();
                        bundle.putInt(CWConstant.TYPE, 1);
                        bundle.putString(CWConstant.TITLE, itemBean.getTitle());
                        bundle.putString(CWConstant.CONTENT, itemBean.getGroup());
                        openNewPageForResult(CustomSwitchSubFragment.class, bundle,
                                CWConstant.REQUEST_NO_SHUTDOWN);
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
                switch (requestCode) {
                    case CWConstant.REQUEST_TIME_DISPLAY_FORMAT: // 时间显示格式
                        String content = bundle.getString(CWConstant.CONTENT);
                        for (SectionItem item : mItemList) {
                            BaseItemBean itemBean = item.t;
                            if (itemBean != null && itemBean.getType() == 0) {
                                itemBean.setContent(getString(R.string.hour_system,
                                        "12".equals(content) ? "12" : "24"));
                                itemBean.setGroup(content);
                                mAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                        break;
                    case CWConstant.REQUEST_DIAL_PAD: // 拨号键盘开关
                    case CWConstant.REQUEST_NO_SHUTDOWN: // 禁止关机
                        boolean isOpen = "1".equals(bundle.getString(CWConstant.CONTENT));
                        for (SectionItem item : mItemList) {
                            BaseItemBean itemBean = item.t;
                            if (itemBean != null && ((itemBean.getType() == 1 &&
                                    CWConstant.REQUEST_DIAL_PAD == requestCode) ||
                                    (itemBean.getType() == 8 && CWConstant.REQUEST_NO_SHUTDOWN == requestCode))) {
                                itemBean.setContent(isOpen ?
                                        getString(R.string.is_open) :
                                        getString(R.string.is_close));
                                itemBean.setGroup(bundle.getString(CWConstant.CONTENT));
                                mAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        }else{
            DeviceSettingsModel settingsModel;
            switch (requestCode) {
                case CWConstant.REQUEST_DIAL_PAD: // 拨号键盘开关
                    settingsModel = getDeviceSettings();
                    boolean isOpen;
                    if (settingsModel != null)
                        isOpen = "1".equals(settingsModel.getDial_pad());
                    else
                        isOpen = false;
                    for (SectionItem item : mItemList) {
                        BaseItemBean itemBean = item.t;
                        if (itemBean != null && itemBean.getType() == 1) {
                            itemBean.setContent(isOpen ?
                                    getString(R.string.is_open) :
                                    getString(R.string.is_close));
                            itemBean.setGroup(isOpen ? "1" : "0");
                            mAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                    break;
                case CWConstant.REQUEST_NO_SHUTDOWN: // 禁止关机
                    settingsModel = getDeviceSettings();
                    boolean isShutdownOpen;
                    if (settingsModel != null) {
                        String[] array = settingsModel.getOther().split(",");
                        isShutdownOpen = array.length >= 2 && "1".equals(array[1]);
                    } else
                        isShutdownOpen = false;
                    for (SectionItem item : mItemList) {
                        BaseItemBean itemBean = item.t;
                        if (itemBean != null && itemBean.getType() == 8) {
                            itemBean.setContent(isShutdownOpen ?
                                    getString(R.string.is_open) :
                                    getString(R.string.is_close));
                            itemBean.setGroup(isShutdownOpen ? "1" : "0");
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
                DeviceModel deviceModel;
                switch (msg.what) {
                    case CWConstant.REQUEST_URL_GET_OTHER: // 获取设置
                        if (msg.obj != null) {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                userModel = getUserModel();
                                deviceModel = getDevice();
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                RequestBean bean =
                                        mGson.fromJson(mGson.toJson(resultBean.getResultBean()),
                                                RequestBean.class);
                                if (userModel != null && deviceModel != null && deviceModel.getD_id() == requestBean.getD_id()) {
                                    DeviceSettingsModel settingsModel = getDeviceSettings();
                                    settingsModel.setOther(bean.getOther());
                                    settingsModel.save();
                                    boolean isOpen;
                                    if (!TextUtils.isEmpty(bean.getOther())) {
                                        String[] array = bean.getOther().split(",");
                                        isOpen = array.length >= 2 && "1".equals(array[1]);
                                    } else
                                        isOpen = false;
                                    for (SectionItem item : mItemList) {
                                        BaseItemBean itemBean = item.t;
                                        if (itemBean != null && itemBean.getType() == 8) {
                                            itemBean.setContent(isOpen ?
                                                    getString(R.string.is_open) :
                                                    getString(R.string.is_close));
                                            itemBean.setGroup(isOpen ? "1" : "0");
                                            mAdapter.notifyDataSetChanged();
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    case CWConstant.REQUEST_URL_GET_DIAL_PAD: // 获取拨号盘状态开关
                        if (msg.obj != null) {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                userModel = getUserModel();
                                deviceModel = getDevice();
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                RequestBean bean =
                                        mGson.fromJson(mGson.toJson(resultBean.getResultBean()),
                                                RequestBean.class);
                                if (userModel != null && deviceModel != null && deviceModel.getD_id() == requestBean.getD_id()) {
                                    DeviceSettingsModel settingsModel = getDeviceSettings();
                                    settingsModel.setDial_pad(bean.getType());
                                    settingsModel.save();
                                    boolean isOpen = "1".equals(bean.getType());
                                    for (SectionItem item : mItemList) {
                                        BaseItemBean itemBean = item.t;
                                        if (itemBean != null && itemBean.getType() == 1) {
                                            itemBean.setContent(isOpen ?
                                                    getString(R.string.is_open) :
                                                    getString(R.string.is_close));
                                            itemBean.setGroup(bean.getType());
                                            mAdapter.notifyDataSetChanged();
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    case CWConstant.REQUEST_URL_MONIOTR_DEVICE: // 语音监护
                    case CWConstant.REQUEST_URL_CAPT_DEVICE: // 远程监拍
                    case CWConstant.REQUEST_URL_POWER_OFF: // 关机
                    case CWConstant.REQUEST_URL_RESET_DEVICE: // 重启
                    case CWConstant.REQUEST_URL_FACTORY_DEVICE: // 恢复出厂设置
                    case CWConstant.REQUEST_URL_FIND_DEVICE: // 查找手表
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
                            resultBean = (RequestResultBean) msg.obj;
                            if (!TextUtils.isEmpty(resultBean.getService_ip()) && !resultBean.getService_ip().equals(resultBean.getLast_online_ip())) {
                                userModel = getUserModel();
                                deviceModel = getDevice();
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                if (userModel != null && deviceModel != null && deviceModel.getImei().equals(requestBean.getImei())) {
                                    DeviceSettingsModel settingsModel = getDeviceSettings();
                                    settingsModel.setIp(resultBean.getLast_online_ip());
                                    settingsModel.save();
                                    if(!NetworkUtils.isNetworkAvailable()){
                                        RequestToastUtils.toastNetwork();
                                        return false;
                                    }
                                    switch (msg.what) {
                                        case CWConstant.REQUEST_URL_MONIOTR_DEVICE: // 语音监护
                                            CWRequestUtils.getInstance().moniotrDevice(getContext(),
                                                    resultBean.getLast_online_ip(),
                                                    requestBean.getToken(), requestBean.getImei(),
                                                    requestBean.getPhone(), mHandler);
                                            break;
                                        case CWConstant.REQUEST_URL_CAPT_DEVICE: // 远程监拍
                                            CWRequestUtils.getInstance().captDevice(getContext(),
                                                    resultBean.getLast_online_ip(),
                                                    requestBean.getToken(), requestBean.getImei(),
                                                    requestBean.getCome(), mHandler);
                                            break;
                                        case CWConstant.REQUEST_URL_POWER_OFF: // 关机
                                            CWRequestUtils.getInstance().powerOff(getContext(),
                                                    resultBean.getLast_online_ip(),
                                                    requestBean.getToken(), requestBean.getImei(),
                                                    mHandler);
                                            break;
                                        case CWConstant.REQUEST_URL_RESET_DEVICE: // 重启
                                            CWRequestUtils.getInstance().restartDevice(getContext(),
                                                    resultBean.getLast_online_ip(),
                                                    requestBean.getToken(), requestBean.getImei(),
                                                    mHandler);
                                            break;
                                        case CWConstant.REQUEST_URL_FACTORY_DEVICE: // 恢复出厂设置
                                            CWRequestUtils.getInstance().factoryDevice(getContext(),
                                                    resultBean.getLast_online_ip(),
                                                    requestBean.getToken(), requestBean.getImei(),
                                                    requestBean.getD_id(), mHandler);
                                            break;
                                        case CWConstant.REQUEST_URL_FIND_DEVICE: // 查找手表
                                            CWRequestUtils.getInstance().findDevice(getContext(),
                                                    resultBean.getLast_online_ip(),
                                                    requestBean.getToken(), requestBean.getImei(),
                                                    mHandler);
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            }else if (resultBean.getCode() == CWConstant.SUCCESS)
                                XToastUtils.toast(R.string.send_success_prompt);
                            else if (resultBean.getCode() == CWConstant.ERROR)
                                XToastUtils.toast(R.string.send_error_prompt);
                            else
                                RequestToastUtils.toast(resultBean.getCode());
                        }
                        break;
                    case CWConstant.REQUEST_URL_GET_LOST_PASSWORD: // 获取设置的丢失密码
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
                            resultBean = (RequestResultBean) msg.obj;
                            if (!TextUtils.isEmpty(resultBean.getService_ip()) && !resultBean.getService_ip().equals(resultBean.getLast_online_ip())) {
                                userModel = getUserModel();
                                deviceModel = getDevice();
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                if (userModel != null && deviceModel != null && deviceModel.getD_id() == requestBean.getD_id()) {
                                    DeviceSettingsModel settingsModel = getDeviceSettings();
                                    settingsModel.setIp(resultBean.getLast_online_ip());
                                    settingsModel.save();
                                    CWRequestUtils.getInstance().getLostPwd(getContext(),
                                            resultBean.getLast_online_ip(),
                                            requestBean.getToken(), requestBean.getD_id(),
                                            mHandler);
                                }
                            } else if (resultBean.getCode() == CWConstant.SUCCESS) {
                                userModel = getUserModel();
                                deviceModel = getDevice();
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                if (userModel != null && deviceModel != null && deviceModel.getD_id() == requestBean.getD_id()) {
                                    ResultBean resultModel =
                                            mGson.fromJson(mGson.toJson(resultBean.getResultBean()),
                                                    ResultBean.class);
                                    DeviceSettingsModel settingsModel = getDeviceSettings();
                                    settingsModel.setLoss(resultModel.getPassword());
                                    settingsModel.save();
                                    boolean isOpen;
                                    if (TextUtils.isEmpty(resultModel.getPassword()))
                                        isOpen = false;
                                    else {
                                        String[] array = resultModel.getPassword().split("#");
                                        isOpen = array.length >= 2 && "1".equals(array[1]);
                                    }
                                    if(isOpen)
                                        XToastUtils.toast(R.string.watch_loss_prompt);
                                    else
                                        CWRequestUtils.getInstance().factoryDevice(getContext(),
                                                getIp(), userModel.getToken(), deviceModel.getImei(),
                                                deviceModel.getD_id(), mHandler);
                                }
                            }else if (resultBean.getCode() == CWConstant.ERROR)
                                XToastUtils.toast(R.string.send_error_prompt);
                            else
                                RequestToastUtils.toast(resultBean.getCode());
                        }
                        break;
                    case CWConstant.HANDLE_CONFIRM_ACTION: // 确认
                        if(!NetworkUtils.isNetworkAvailable()){
                            RequestToastUtils.toastNetwork();
                            return false;
                        }
                        userModel = getUserModel();
                        deviceModel = getDevice();
                        if (userModel != null && deviceModel != null) {
                            switch (msg.arg1) {
                                case 4: // 远程拍照
                                    CWRequestUtils.getInstance().captDevice(getContext(), getIp(),
                                            userModel.getToken(), deviceModel.getImei(), "",
                                            mHandler);
                                    break;
                                case 5: // 远程关机
                                    CWRequestUtils.getInstance().powerOff(getContext(), getIp(),
                                            userModel.getToken(), deviceModel.getImei(),
                                            mHandler);
                                    break;
                                case 6: // 远程重启
                                    CWRequestUtils.getInstance().restartDevice(getContext(),
                                            getIp(), userModel.getToken(), deviceModel.getImei(),
                                            mHandler);
                                    break;
                                case 7: // 重置设备
                                    getLostPwd();
                                    break;
                                case 9: // 查找手表
                                    CWRequestUtils.getInstance().findDevice(getContext(),
                                            getIp(), userModel.getToken(), deviceModel.getImei(),
                                            mHandler);
                                    break;
                                default:
                                    break;
                            }
                        }
                        break;
                    case CWConstant.HANDLE_INPUT_ACTION: // 输入回调
                        switch (msg.arg1) {
                            case CWConstant.DIALOG_VOICE_MONITOR: // 语音监护
                                if(!NetworkUtils.isNetworkAvailable()){
                                    RequestToastUtils.toastNetwork();
                                    return false;
                                }
                                String inputText = (String) msg.obj;
                                userModel = getUserModel();
                                deviceModel = getDevice();
                                if (userModel != null && deviceModel != null)
                                    CWRequestUtils.getInstance().moniotrDevice(getContext(),
                                            getIp(), userModel.getToken(), deviceModel.getImei(),
                                            inputText, mHandler);
                                break;
                            default:
                                break;
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
