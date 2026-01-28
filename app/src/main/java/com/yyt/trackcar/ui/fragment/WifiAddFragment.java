package com.yyt.trackcar.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.RequestBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.DeviceSettingsModel;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CWRequestUtils;
import com.yyt.trackcar.utils.EmojiFilter;
import com.yyt.trackcar.utils.RequestToastUtils;
import com.yyt.trackcar.utils.XToastUtils;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      WifiAddFragment
 * @ author:        QING
 * @ createTime:    2020/4/24 17:20
 * @ describe:      TODO 添加/编辑WiFi页面
 */
@Page(name = "WifiAdd", params = {CWConstant.TYPE, CWConstant.MODEL})
public class WifiAddFragment extends BaseFragment {
    @BindView(R.id.etName)
    EditText mEtName; // 名称文本编辑
    @BindView(R.id.etPwd)
    EditText mEtPwd; // 密码文本编辑
    @AutoWired
    String model; // 对象
    @AutoWired
    int type; // 类型 0添加 1修改

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_wifi_add;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        if (type == 1)
            titleBar.setTitle(R.string.edit_wifi);
        else
            titleBar.setTitle(R.string.add_wifi);
        return titleBar;
    }

    @Override
    protected void initArgs() {
        XRouter.getInstance().inject(this);
    }

    @Override
    protected void initViews() {
        if (model == null)
            model = "";
        String[] array = model.split(CWConstant.WIFI_SEPARATE);
        if (array.length > 0)
            mEtName.setText(array[0]);
        if (array.length > 1)
            mEtPwd.setText(array[1]);
    }

    /**
     * 设置家庭wifi
     *
     * @param wifi wifi
     */
    public void setFamilyWifi(String wifi) {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork(getContext());
            return;
        }
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().setFamilyWifi(getContext(), getIp(),
                    userModel.getToken(), deviceModel.getImei(), deviceModel.getD_id(), wifi,
                    mHandler);
    }

    @SingleClick
    @OnClick({R.id.confirmBtn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirmBtn: // 保存
                String name = mEtName.getText().toString().trim();
                String pwd = mEtPwd.getText().toString().trim();
                if (TextUtils.isEmpty(name) || EmojiFilter.containsEmoji(name)) {
                    XToastUtils.toast(mEtName.getHint().toString());
                    mEtName.requestFocus();
                } else {
                    UserModel userModel = getUserModel();
                    if (userModel != null) {
                        String wifiString = String.format("%s%s%s%s%s", name,
                                CWConstant.WIFI_SEPARATE,
                                pwd, CWConstant.WIFI_SEPARATE, userModel.getU_id());
                        if (wifiString.equals(model)) {
                            XToastUtils.toast(R.string.send_success_prompt);
                            popToBack();
                        }else
                            setFamilyWifi(wifiString);
                    }
                }
                break;
            default:
                break;
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
                    case CWConstant.REQUEST_URL_SET_FAMILY_WIFI: // 设置家庭wifi
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
                                    if (!NetworkUtils.isNetworkAvailable()) {
                                        RequestToastUtils.toastNetwork(getContext());
                                        return false;
                                    }
                                    CWRequestUtils.getInstance().setFamilyWifi(getContext(),
                                            resultBean.getLast_online_ip(),
                                            requestBean.getToken(), requestBean.getImei(),
                                            requestBean.getD_id(), requestBean.getWifi(),
                                            mHandler);
                                }
                            } else if (resultBean.getCode() == CWConstant.SUCCESS || resultBean.getCode() == CWConstant.WAIT_ONLINE_UPDATE) {
                                if (resultBean.getCode() == CWConstant.WAIT_ONLINE_UPDATE)
                                    XToastUtils.toast(R.string.wait_online_update_prompt);
                                else
                                    XToastUtils.toast(R.string.send_success_prompt);
                                userModel = getUserModel();
                                deviceModel = getDevice();
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                if (userModel != null && deviceModel != null && deviceModel.getD_id() == requestBean.getD_id()) {
                                    DeviceSettingsModel settingsModel = getDeviceSettings();
                                    settingsModel.setWifi(requestBean.getWifi());
                                    settingsModel.setWifiType(0);
                                    settingsModel.save();
                                }
                                Intent intent = new Intent();
                                Bundle bundle = new Bundle();
                                bundle.putString(CWConstant.MODEL, requestBean.getWifi());
                                intent.putExtras(bundle);
                                setFragmentResult(Activity.RESULT_OK, intent);
                                popToBack();
                            } else if (resultBean.getCode() == CWConstant.ERROR)
                                XToastUtils.toast(R.string.send_error_prompt);
                            else
                                RequestToastUtils.toast(getContext(), resultBean.getCode());
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
