package com.yyt.trackcar.ui.fragment;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xutil.app.ActivityUtils;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.MainApplication;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.PostMessage;
import com.yyt.trackcar.bean.RequestBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.DeviceModel_Table;
import com.yyt.trackcar.dbflow.DeviceSettingsModel;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.ui.activity.BindDeviceActivity;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CWRequestUtils;
import com.yyt.trackcar.utils.DialogUtils;
import com.yyt.trackcar.utils.RequestToastUtils;
import com.yyt.trackcar.utils.XToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.OnClick;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      UnbindFragment
 * @ author:        QING
 * @ createTime:    2020/5/8 16:29
 * @ describe:      TODO 解绑页面
 */
@Page(name = "Unbind")
public class  UnbindFragment extends BaseFragment {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_unbind;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(R.string.unbind);
        return titleBar;
    }

    @Override
    protected void initViews() {
    }

    /**
     * 管理员解绑这个设备的所有人
     */
    private void unbindImei() {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork();
            return;
        }
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().unbindImei(MainApplication.getInstance(),
                    userModel.getToken(), deviceModel.getImei(), mHandler);
    }

    /**
     * 恢复出厂设置
     */
    private void factoryDevice() {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork();
            return;
        }
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().factoryDevice(MainApplication.getInstance(),
                    getIp(), userModel.getToken(), deviceModel.getImei(),
                    deviceModel.getD_id(), mHandler);
    }

    @SingleClick
    @OnClick({R.id.unBindBtn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.unBindBtn: // 解除绑定
                mMaterialDialog = DialogUtils.customMaterialDialog(getContext(),
                        mMaterialDialog, getString(R.string.prompt),
                        getString(R.string.reset_device_prompt),
                        getString(R.string.confirm), getString(R.string.cancel), null,
                        0, mHandler);
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
                    case CWConstant.REQUEST_URL_UNBIND_IMEI: // 管理员解绑这个设备的所有人
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                userModel = getUserModel();
                                if (userModel != null) {
                                    requestBean =
                                            mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                    List<DeviceModel> devicelist = getDeviceList();
                                    for (int i = 0; i < devicelist.size(); i++) {
                                        deviceModel = devicelist.get(i);
                                        if (deviceModel.getImei().equals(requestBean.getImei())) {
                                            OperatorGroup operatorGroup =
                                                    OperatorGroup.clause(OperatorGroup.clause()
                                                            .and(DeviceModel_Table.u_id.eq(userModel.getU_id()))
                                                            .and(DeviceModel_Table.d_id.eq(deviceModel.getD_id())));
                                            SQLite.delete(DeviceModel.class).where(operatorGroup).execute();
                                            devicelist.remove(i);
                                            break;
                                        }
                                    }
                                    if (devicelist.size() == 0) {
                                        MainApplication.getInstance().setDeviceModel(null);
                                        EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_FINISH));
                                        ActivityUtils.startActivity(BindDeviceActivity.class);
                                    } else {
                                        MainApplication.getInstance().setDeviceModel(devicelist.get(0));
                                        userModel.setSelectImei(getDevice().getImei());
                                        userModel.save();
                                        EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_CHANGE_DEVICE));
                                        EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_BACK_TO_MAIN));
                                    }
                                }
                            } else
                                RequestToastUtils.toast(resultBean.getCode());
                        }
                        break;
                    case CWConstant.REQUEST_URL_FACTORY_DEVICE: // 恢复出厂设置
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
                                    CWRequestUtils.getInstance().factoryDevice(getContext(),
                                            resultBean.getLast_online_ip(),
                                            requestBean.getToken(), requestBean.getImei(),
                                            requestBean.getD_id(), mHandler);
                                }
                            }else if (resultBean.getCode() == CWConstant.SUCCESS)
                                unbindImei();
                            else if (resultBean.getCode() == CWConstant.ERROR)
                                XToastUtils.toast(R.string.send_error_prompt);
                            else
                                RequestToastUtils.toast(resultBean.getCode());
                        }
                        break;
                    case CWConstant.HANDLE_CONFIRM_ACTION: // 确认
                        unbindImei();
//                        factoryDevice();
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
