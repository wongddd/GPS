package com.yyt.trackcar.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.button.switchbutton.SwitchButton;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.DeviceVersionBean;
import com.yyt.trackcar.bean.RequestBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.dbflow.DeviceInfoModel;
import com.yyt.trackcar.dbflow.DeviceInfoModel_Table;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.DeviceSettingsModel;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CWRequestUtils;
import com.yyt.trackcar.utils.ImageLoadUtils;
import com.yyt.trackcar.utils.RequestToastUtils;
import com.yyt.trackcar.utils.SettingSPUtils;
import com.yyt.trackcar.utils.XToastUtils;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      UpdateFirmwareFragment
 * @ author:        QING
 * @ createTime:    2020/3/12 17:18
 * @ describe:      TODO 手表固件升级
 */
@Page(name = "UpdateFirmware")
public class UpdateFirmwareFragment extends BaseFragment implements CompoundButton.OnCheckedChangeListener {
    @BindView(R.id.ivPortrait)
    ImageView mIvPortrait; // 头像
    @BindView(R.id.tvTitle)
    TextView mTvTitle; // 更新版本标题文本
    @BindView(R.id.tvContent)
    TextView mTvContent; // 更新版本信息文本
    @BindView(R.id.sbSwitch)
    SwitchButton mSbSwitch; // 自动更新开关
    @BindView(R.id.ivCheck)
    ImageView mIvCheck; // 流量下载安装包开关
    @BindView(R.id.clVersionNewest)
    View mVersionNewestView; // 当前已是最新版本布局
    private DeviceVersionBean mVersionBean; // 设备版本对象

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_update_firmware;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(R.string.update_device_firmware);
        return titleBar;
    }

    @Override
    protected void initViews() {
        refreshDeviceInfo();
        getWatchVersion();
        getUpgrade();
    }

    @Override
    protected void initListeners() {
        mSbSwitch.setOnCheckedChangeListener(this);
    }

    /**
     * 刷新设备信息
     */
    private void refreshDeviceInfo() {
        DeviceModel deviceModel = getDevice();
        DeviceInfoModel infoModel = null;
        UserModel userModel = getUserModel();
        if (userModel != null && deviceModel != null) {
            OperatorGroup operatorGroup = OperatorGroup.clause(OperatorGroup.clause()
                    .and(DeviceInfoModel_Table.u_id.eq(userModel.getU_id()))
                    .and(DeviceInfoModel_Table.imei.eq(deviceModel.getImei())));
            infoModel = SQLite.select().from(DeviceInfoModel.class)
                    .where(operatorGroup)
                    .querySingle();
        }
        int imgRes;
        if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 0)
            imgRes = R.mipmap.ic_device_portrait;
        else
            imgRes = R.mipmap.ic_default_pigeon_marker;
        if (infoModel == null)
            ImageLoadUtils.loadPortraitImage(getContext(), "", imgRes,
                    mIvPortrait);
        else
            ImageLoadUtils.loadPortraitImage(getContext(), infoModel.getHead(),
                    imgRes, mIvPortrait);
        if (infoModel == null || TextUtils.isEmpty(infoModel.getNickname())) {
            if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 0)
                mTvTitle.setText(getString(R.string.device_watch, getString(R.string.baby)));
            else
                mTvTitle.setText(getString(R.string.device_watch, getString(R.string.device)));
        } else
            mTvTitle.setText(getString(R.string.device_watch, infoModel.getNickname()));
        DeviceSettingsModel settingsModel = getDeviceSettings();
        if (settingsModel != null) {
            mIvCheck.setSelected("1".equals(settingsModel.getWebTraffic()));
            mSbSwitch.setChecked("1".equals(settingsModel.getWifiStatus()));
        }
        mTvContent.setText(getString(R.string.current_version, ""));
    }

    /**
     * 获取手表最新版本信息(下发使用)
     */
    private void getWatchVersion() {
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().getWatchVersion(getContext(), userModel.getToken(),
                    deviceModel.getD_id(), mHandler);
    }

    /**
     * 获取 wifi流量升级控制
     */
    private void getUpgrade() {
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().getUpgrade(getContext(), getIp(), userModel.getToken(),
                    deviceModel.getD_id(), mHandler);
    }

    /**
     * 改为wifi流量升级控制
     */
    private void setUpgrade() {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork();
            return;
        }
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().setUpgrade(getContext(), getIp(), userModel.getToken(),
                    deviceModel.getImei(), deviceModel.getD_id(), mSbSwitch.isChecked() ? "1" : "0",
                    mIvCheck.isSelected() ? "1" : "0", mHandler);
    }

    @SingleClick
    @OnClick({R.id.clInfo, R.id.clTraffic})
    public void onClick(View v) {
        Bundle bundle;
        switch (v.getId()) {
            case R.id.clInfo: //  手表固件信息
                bundle = new Bundle();
                bundle.putParcelable(CWConstant.MODEL, mVersionBean);
                openNewPage(FirmwareInfoFragment.class, bundle);
                break;
            case R.id.clTraffic: //  流量下载安装包
                mIvCheck.setSelected(!mIvCheck.isSelected());
                setUpgrade();
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        setUpgrade();
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
                    case CWConstant.REQUEST_URL_GET_WATCH_VERSION: // 获取手表最新版本信息(下发使用)
                        if (msg.obj != null) {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                mVersionBean = mGson.fromJson(resultBean.getResultBean(),
                                        DeviceVersionBean.class);
                                String version = getString(R.string.current_version,
                                        mVersionBean.getDv());
                                mTvContent.setText(version);
                            }
                        }
                        break;
                    case CWConstant.REQUEST_URL_GET_UPGRADE: // 获取 wifi流量升级控制
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
                                    settingsModel.setWebTraffic(bean.getWebTraffic());
                                    settingsModel.setWifiStatus(bean.getWifiStatus());
                                    settingsModel.save();
                                    mIvCheck.setSelected("1".equals(bean.getWebTraffic()));
                                    mSbSwitch.setOnCheckedChangeListener(null);
                                    mSbSwitch.setCheckedImmediately("1".equals(bean.getWifiStatus()));
                                    mSbSwitch.setOnCheckedChangeListener(UpdateFirmwareFragment.this);
                                }
                            }
                        }
                        break;
                    case CWConstant.REQUEST_URL_UPGRADE: // 改为wifi流量升级控制
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
                                    CWRequestUtils.getInstance().setUpgrade(getContext(),
                                            resultBean.getLast_online_ip(),
                                            requestBean.getToken(), requestBean.getImei(),
                                            requestBean.getD_id(), requestBean.getWifiStatus(),
                                            requestBean.getWebTraffic(), mHandler);
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
                                    settingsModel.setWebTraffic(requestBean.getWebTraffic());
                                    settingsModel.setWifiStatus(requestBean.getWifiStatus());
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
}
