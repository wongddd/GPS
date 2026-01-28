package com.yyt.trackcar.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.picker.RulerView;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.R;
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
 * @ fileName:      StepTargetFragment
 * @ author:        QING
 * @ createTime:    2020/3/20 17:32
 * @ describe:      TODO 每日运动目标页面
 */
@Page(name = "StepTarget", params = {CWConstant.CONTENT})
public class StepTargetFragment extends BaseFragment {
    @BindView(R.id.ivPortrait)
    ImageView mIvPortrait; // 头像
    @BindView(R.id.tvTargetStep)
    TextView mTvTargetStep; // 目标步数
    @BindView(R.id.rvTargetStep)
    RulerView mRvTargetStep; // 目标步数设置
    @AutoWired
    String content; // 目标步数
    private String mStepTarget; // 目标步数

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_step_target;
    }

    @Override
    protected void initArgs() {
        XRouter.getInstance().inject(this);
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(getString(R.string.step_target));
        return titleBar;
    }

    @Override
    protected void initViews() {
        DeviceModel deviceModel = getDevice();
        UserModel userModel = getUserModel();
        int imgRes;
        if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 0)
            imgRes = R.mipmap.ic_device_portrait;
        else
            imgRes = R.mipmap.ic_default_pigeon_marker;
        if (userModel != null && deviceModel != null) {
            OperatorGroup operatorGroup = OperatorGroup.clause(OperatorGroup.clause()
                    .and(DeviceInfoModel_Table.u_id.eq(userModel.getU_id()))
                    .and(DeviceInfoModel_Table.imei.eq(deviceModel.getImei())));
            DeviceInfoModel infoModel = SQLite.select().from(DeviceInfoModel.class)
                    .where(operatorGroup)
                    .querySingle();
            if (infoModel == null)
                ImageLoadUtils.loadPortraitImage(getContext(), "", imgRes,
                        mIvPortrait);
            else
                ImageLoadUtils.loadPortraitImage(getContext(), infoModel.getHead(),
                       imgRes, mIvPortrait);
        } else
            ImageLoadUtils.loadPortraitImage(getContext(), "", imgRes,
                    mIvPortrait);
        long step = 8000;
        if (!TextUtils.isEmpty(content)) {
            try {
                step = Long.parseLong(content);
                if (step < 1000)
                    step = 8000;
            } catch (NumberFormatException e) {
                if (BuildConfig.DEBUG)
                    e.printStackTrace();
            }
        }
        mStepTarget = String.valueOf(step);
        mTvTargetStep.setText(mStepTarget);
        mRvTargetStep.setCurrentValue(step);
    }

    @Override
    protected void initListeners() {
        mRvTargetStep.setOnChooseResultListener(new RulerView.OnChooseResultListener() {
            @Override
            public void onEndResult(String result) {
                try {
                    float step = Float.parseFloat(result);
                    mStepTarget = String.valueOf(Math.round((step / 100)) * 100);
                    if (mTvTargetStep != null) {
                        mTvTargetStep.setText(mStepTarget);
                    }
                } catch (NumberFormatException e) {
                    if (BuildConfig.DEBUG)
                        e.printStackTrace();
                }
            }

            @Override
            public void onScrollResult(String result) {

            }
        });
    }

    /**
     * 设置手表目标步数
     */
    private void setStepGoal() {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork();
            return;
        }
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().setStepGoal(getContext(), getIp(),
                    userModel.getToken(), deviceModel.getD_id(), deviceModel.getImei(), mStepTarget,
                    mHandler);
    }

    @SingleClick
    @OnClick({R.id.saveBtn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveBtn: // 保存
                setStepGoal();
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
                switch (msg.what) {
                    case CWConstant.REQUEST_URL_SET_STEP_GOAL: // 设置手表目标步数
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
                                    CWRequestUtils.getInstance().setStepGoal(getContext(),
                                            resultBean.getLast_online_ip(),
                                            requestBean.getToken(), requestBean.getD_id(),
                                            requestBean.getImei(), requestBean.getSteps(),
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
                                    settingsModel.setStep(requestBean.getSteps());
                                    settingsModel.save();
                                }
                                Intent intent = new Intent();
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
}
