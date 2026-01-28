package com.yyt.trackcar.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import com.socks.library.KLog;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAABaseResponseBean;
import com.yyt.trackcar.dbflow.AAADeviceModel;
import com.yyt.trackcar.dbflow.AAAUserModel;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.Constant;
import com.yyt.trackcar.utils.DialogUtils;
import com.yyt.trackcar.utils.ErrorCode;
import com.yyt.trackcar.utils.RequestToastUtils;
import com.yyt.trackcar.utils.TConstant;
import com.yyt.trackcar.utils.ViewDataUtils;
import com.yyt.trackcar.utils.XToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      RemoteControlFragment
 * @ author:        QING
 * @ createTime:    2023/6/13 15:59
 * @ describe:      TODO 远程控制页面
 */
@Page(name = "RemoteControl")
public class RemoteControlFragment extends BaseFragment {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.headViewDeviceInfo)
    View mHeaderView;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_remote_control;
    }

    @Override
    protected void initArgs() {
        XRouter.getInstance().inject(this);
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
//        titleBar.setTitle(R.string.settings);
        titleBar.setTitle(String.format("%s%s", getString(R.string.pet_real_time),
                getString(R.string.settings)));
        return titleBar;
    }

    @Override
    protected void initViews() {
//        initHeaderView();
    }

    /**
     * 初始化头布局
     */
    private void initHeaderView() {
        ViewDataUtils.initDeviceInfoView(getContext(), mHeaderView, getTrackDeviceModel());
    }

    /**
     * 设置定位间隔
     *
     * @param interval 定位间隔
     */
    private void setPositionInterval(long interval) {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork();
            return;
        }
        AAAUserModel userModel = getTrackUserModel();
        AAADeviceModel deviceModel = getTrackDeviceModel();
//        if (userModel != null && deviceModel != null && !TextUtils.isEmpty(deviceModel
//        .getDeviceImei())) {
//            CarGpsRequestUtils.setPositionInterval(userModel, deviceModel.getDeviceImei(),
//                    interval, mHandler);
//        }
    }

    /**
     * 发送指令
     */
    private void sendCommand(String command) {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork();
            return;
        }
        AAAUserModel userModel = getTrackUserModel();
        AAADeviceModel deviceModel = getTrackDeviceModel();
        if (userModel != null && deviceModel != null && !TextUtils.isEmpty(deviceModel.getDeviceImei())) {
            showDialog();
            CarGpsRequestUtils.sendCommand(userModel, deviceModel.getDeviceImei(), command,
                    mHandler);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @SingleClick
    @OnClick({R.id.clFirst, R.id.clSecond, R.id.clThird, R.id.clFourth, R.id.clFifth, R.id.clSixth})
    public void onClick(View v) {
        Bundle bundle;
        switch (v.getId()) {
            case R.id.clFirst: // 设置定位间隔
                bundle = new Bundle();
                bundle.putString(CWConstant.TITLE, getString(R.string.location_mode));
                bundle.putInt(CWConstant.TYPE, 6);
                openNewPage(CustomSelectorFragment.class, bundle);
//                mMaterialDialog = DialogUtils.customInputMaterialDialog(getContext(),
//                        mMaterialDialog, String.format("%s%s",
//                                getString(R.string.set_position_interval),
//                                getString(R.string.unit_second)), null,
//                        getString(R.string.hint_set_position_interval), null,
//                        InputType.TYPE_CLASS_NUMBER
//                        , 10, 2, getString(R.string.confirm), getString(R.string.cancel),
//                        100, mHandler);
                break;
            case R.id.clSecond: // 重启设备
                mMaterialDialog = DialogUtils.customMaterialDialog(
                        getContext(),
                        mMaterialDialog,
                        getString(R.string.tips),
                        getString(R.string.send_order_tips,
                                getString(R.string.restart_device)),
                        getString(R.string.confirm),
                        getString(R.string.cancel),
                        101,
                        mHandler);
                break;
            case R.id.clThird: // 关机设备
                mMaterialDialog = DialogUtils.customMaterialDialog(
                        getContext(),
                        mMaterialDialog,
                        getString(R.string.tips),
                        getString(R.string.send_order_tips,
                                getString(R.string.shut_down_device)),
                        getString(R.string.confirm),
                        getString(R.string.cancel),
                        102,
                        mHandler);
                break;
            case R.id.clFourth: // 立即定位设备
                mMaterialDialog = DialogUtils.customMaterialDialog(
                        getContext(),
                        mMaterialDialog,
                        getString(R.string.tips),
                        getString(R.string.send_order_tips,
                                getString(R.string.immedial_location_device)),
                        getString(R.string.confirm),
                        getString(R.string.cancel),
                        103,
                        mHandler);
                break;
            case R.id.clFifth: // 查找设备
                mMaterialDialog = DialogUtils.customMaterialDialog(
                        getContext(),
                        mMaterialDialog,
                        getString(R.string.tips),
                        getString(R.string.send_order_tips,
                                getString(R.string.find_device)),
                        getString(R.string.confirm),
                        getString(R.string.cancel),
                        104,
                        mHandler);
                break;
            case R.id.clSixth: // 停止查找设备
                mMaterialDialog = DialogUtils.customMaterialDialog(
                        getContext(),
                        mMaterialDialog,
                        getString(R.string.tips),
                        getString(R.string.send_order_tips,
                                getString(R.string.find_device_end)),
                        getString(R.string.confirm),
                        getString(R.string.cancel),
                        105,
                        mHandler);
                break;
            default:
                break;
        }
    }

    /**
     * 消息处理
     */
    private final Handler mHandler = new Handler(msg -> {
        try {
            AAABaseResponseBean responseBean;
            switch (msg.what) {
                case TConstant.REQUEST_SEND_COMMAND: // 发送指令
                    dismisDialog();
                    responseBean = (AAABaseResponseBean) msg.obj;
                    if (responseBean.getCode() == TConstant.RESPONSE_SUCCESS) {
                        XToastUtils.toast(getContext(), getString(R.string.send_success_prompt));
                    } else {
                        showMessage(ErrorCode.getResId(responseBean.getCode()));
                    }
                    break;
                case Constant.HANDLE_INPUT_ACTION: // 输入回调
                    long interval = Long.parseLong((String) msg.obj);
                    if (interval < 10) {
                        XToastUtils.toast(getContext(),
                                getString(R.string.hint_set_position_interval));
                    } else {
                        setPositionInterval(interval);
                    }
                    break;
                case Constant.HANDLE_CONFIRM_ACTION: // 确定
                    switch (msg.arg1) {
                        case 101:
                            sendCommand("COMMAND_RESTART");
                            break;
                        case 102:
                            sendCommand("COMMAND_SHUTDOWN");
                            break;
                        case 103:
                            sendCommand("COMMAND_IMMEDIAL_LOCATION");
                            break;
                        case 104:
                            sendCommand("COMMAND_FIND");
                            break;
                        case 105:
                            sendCommand("COMMAND_FIND_END");
                            break;
                        default:
                            break;
                    }
                    break;
                case Constant.HANDLE_CANCEL_ACTION: // 取消

                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG)
                e.printStackTrace();
        }
        return false;
    });

    @Override
    public void onResume() {
        super.onResume();
        KLog.d("onResume");
        initHeaderView();
    }
}
