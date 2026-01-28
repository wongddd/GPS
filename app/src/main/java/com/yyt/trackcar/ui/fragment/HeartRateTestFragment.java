package com.yyt.trackcar.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.button.switchbutton.SwitchButton;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.RequestBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.HealthModel;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CWRequestUtils;
import com.yyt.trackcar.utils.DBUtils;
import com.yyt.trackcar.utils.RequestToastUtils;
import com.yyt.trackcar.utils.XToastUtils;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * @ projectName:
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      HeartRateTestFragment
 * @ author:        QING
 * @ createTime:    6/2/21 16:19
 * @ describe:      TODO
 */
@SuppressLint("NonConstantResourceId")
@Page(name = "HeartRateTest")
public class HeartRateTestFragment extends BaseFragment {
    @BindView(R.id.sbSwitch)
    SwitchButton mSbSwitch;
    @BindView(R.id.ivCheckTypeFirst)
    ImageView mIvCheckTypeFirst;
    @BindView(R.id.ivCheckTypeSecond)
    ImageView mIvCheckTypeSecond;
    private long mSwitchTime;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_heart_rate_test;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(R.string.heart_rate_continue_test);
        return titleBar;
    }

    @Override
    protected void initViews() {
        mIvCheckTypeFirst.setSelected(true);
        DeviceModel deviceModel = getDevice();
        if (deviceModel != null) {
            HealthModel healthModel = DBUtils.getDeviceHealth(deviceModel.getImei());
            mSbSwitch.setCheckedImmediatelyNoEvent(healthModel != null && "1".equals(healthModel.getHeartRateTest()));
        }
        getHeartStatus();
    }

    private void getHeartStatus() {
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null) {
            CWRequestUtils.getInstance().getHeartStatus(getContext(), userModel.getToken()
                    , deviceModel.getD_id(), deviceModel.getImei(), mHandler);
        }
    }

    private void setHeartStatus(boolean isCheck) {
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null) {
            mSwitchTime = System.currentTimeMillis();
            CWRequestUtils.getInstance().setHeartStatus(getContext(), getIp(), userModel.getToken()
                    , deviceModel.getD_id(), deviceModel.getImei(), isCheck ? "1" : "0", mHandler);
        }
    }

    @SingleClick
    @OnClick({R.id.clTypeFirst, R.id.clTypeSecond})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clTypeFirst:
                mIvCheckTypeFirst.setSelected(true);
                mIvCheckTypeSecond.setSelected(false);
                break;
            case R.id.clTypeSecond:
                mIvCheckTypeFirst.setSelected(false);
                mIvCheckTypeSecond.setSelected(true);
                break;
            default:
                break;
        }
    }

    @OnCheckedChanged({R.id.sbSwitch})
    public void OnCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.sbSwitch:
                mSbSwitch.setCheckedImmediatelyNoEvent(!isChecked);
                if (!NetworkUtils.isNetworkAvailable()) {
                    RequestToastUtils.toastNetwork();
                    return;
                }
                if (System.currentTimeMillis() - mSwitchTime > 3000)
                    setHeartStatus(isChecked);
                else
                    XToastUtils.toast(R.string.request_too_busy_prompt);
                break;
            default:
                break;
        }
    }

    /**
     * 消息处理
     */
    private final Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NotNull Message msg) {
            try {
                RequestResultBean resultBean;
                RequestBean requestBean;
                switch (msg.what) {
                    case CWConstant.REQUEST_URL_SET_HEART_STATUS: // 连续心率测量开关设置
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS || resultBean.getCode() == CWConstant.WAIT_ONLINE_UPDATE) {
                                if (resultBean.getCode() == CWConstant.WAIT_ONLINE_UPDATE)
                                    XToastUtils.toast(R.string.wait_online_update_prompt);
                                else
                                    XToastUtils.toast(R.string.send_success_prompt);
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                mSbSwitch.setCheckedImmediatelyNoEvent("1".equals(requestBean.getType()));
                                HealthModel healthModel =
                                        DBUtils.getDeviceHealth(requestBean.getImei());
                                if (healthModel != null) {
                                    healthModel.setHeartRateTest(requestBean.getType());
                                    healthModel.save();
                                }
                            } else if (resultBean.getCode() == CWConstant.ERROR)
                                XToastUtils.toast(R.string.send_error_prompt);
                            else
                                RequestToastUtils.toast(resultBean.getCode());
                        }
                        break;
                    case CWConstant.REQUEST_URL_GET_HEART_STATUS: // 获取连续心率测量开关设置
                        if (msg.obj != null) {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()),
                                                RequestBean.class);
                                RequestBean statusBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getResultBean()),
                                                RequestBean.class);
                                mSbSwitch.setCheckedImmediatelyNoEvent("1".equals(statusBean.getType()));
                                HealthModel healthModel =
                                        DBUtils.getDeviceHealth(requestBean.getImei());
                                if (healthModel != null) {
                                    healthModel.setHeartRateTest(statusBean.getType());
                                    healthModel.save();
                                }
                            }
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
