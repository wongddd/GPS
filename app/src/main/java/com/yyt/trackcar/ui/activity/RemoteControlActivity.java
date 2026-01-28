package com.yyt.trackcar.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAABaseResponseBean;
import com.yyt.trackcar.bean.AAARequestBean;
import com.yyt.trackcar.dbflow.AAADeviceModel;
import com.yyt.trackcar.dbflow.AAAUserModel;
import com.yyt.trackcar.ui.base.BaseActivity;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.Constant;
import com.yyt.trackcar.utils.DialogUtils;
import com.yyt.trackcar.utils.TConstant;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @ projectName:
 * @ packageName:   com.yyt.trackcar.ui.activity
 * @ fileName:      RemoteControlActivity
 * @ author:        QING
 * @ createTime:    7/27/21 20:44ß
 * @ describe:      TODO
 */
@SuppressLint("NonConstantResourceId")
public class RemoteControlActivity extends BaseActivity {
    @BindView(R.id.ivFirst)
    ImageView mIvFirst;
    @BindView(R.id.ivSecond)
    ImageView mIvSecond;
    @BindView(R.id.ivThird)
    ImageView mIvThird;
    @BindView(R.id.ivFourth)
    ImageView mIvFourth;
    @BindView(R.id.ivFifth)
    ImageView mIvFifth;
    @BindView(R.id.ivSixth)
    ImageView mIvSixth;
    @BindView(R.id.ivSeventh)
    ImageView mIvSeventh;
    private AAADeviceModel mDeviceModel; // 设备对象

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        initToolBar(R.string.home_send_order,R.drawable.ic_back_white, mNavigationOnClickListener);
        initToolBar(String.format("%s%s", getString(R.string.pet_real_time),
                        getString(R.string.home_send_order)), R.drawable.ic_back_white,
                mNavigationOnClickListener);
        initDatas();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.aaa_activity_remote_control;
    }

    /**
     * 初始化信息
     */
    private void initDatas() {
        mDeviceModel = getTrackDevice();
    }

    private void initStatus(int type, int status) {
        ImageView imageView = null;
        switch (type) {
            case 100:
                imageView = mIvFirst;
                break;
            case 101:
                imageView = mIvSecond;
                break;
            case 102:
                imageView = mIvThird;
                break;
            case 103:
                imageView = mIvFourth;
                break;
            case 104:
                imageView = mIvFifth;
                break;
            case 105:
                imageView = mIvSixth;
                break;
            case 106:
                imageView = mIvSeventh;
                break;
            default:
                break;
        }
        if (imageView != null) {
            if (status == 1) {
                imageView.setSelected(true);
                imageView.setEnabled(true);
            } else if (status == 2) {
                imageView.setSelected(false);
                imageView.setEnabled(false);
            } else {
                imageView.setSelected(false);
                imageView.setEnabled(true);
            }
        }
    }

    private void resetStatus(int type) {
        switch (type) {
            case 100:
                initStatus(100, 0);
                break;
            case 101:
                initStatus(101, 0);
                break;
            case 102:
                initStatus(102, 0);
                break;
            case 103:
                initStatus(103, 0);
                break;
            case 104:
                initStatus(104, 0);
                break;
            case 105:
                initStatus(105, 0);
                break;
            case 106:
                initStatus(106, 0);
                break;
            default:
                break;
        }
    }

    private void sendRemoteControl(int cmdValue) {
        if (!NetworkUtils.isNetworkAvailable()) {
            showMessage(R.string.network_error_prompt);
            return;
        }
        AAAUserModel userModel = getTrackUserModel();
        if (mDeviceModel != null) {
            initStatus(cmdValue + 99, 2);
//            showDialog();
//            mLoadingDialog.setMessage(getString(R.string.requesting_tips));
            CarGpsRequestUtils.sendRemoteControl(userModel, mDeviceModel.getDeviceImei(),
                    cmdValue, mHandler);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick({R.id.clFirst, R.id.clSecond, R.id.clThird, R.id.clFourth,
            R.id.clFifth, R.id.clSixth, R.id.clSeventh})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clFirst:
                if (mIvFirst.isEnabled()) {
                    mMaterialDialog = DialogUtils.customMaterialDialog(
                            this,
                            mMaterialDialog,
                            getString(R.string.tips),
                            getString(R.string.send_order_tips,
                            getString(R.string.common_type_first)),
                            getString(R.string.confirm),
                            getString(R.string.cancel),
                            100,
                            mHandler);
                }
                break;
            case R.id.clSecond:
                if (mIvSecond.isEnabled()) {
                    mMaterialDialog = DialogUtils.customMaterialDialog(this, mMaterialDialog,
                            getString(R.string.tips), getString(R.string.send_order_tips,
                                    getString(R.string.common_type_second)), getString(R.string
                                    .confirm), getString(R.string.cancel),
                            101, mHandler);
                }
                break;
            case R.id.clThird:
                if (mIvThird.isEnabled()) {
                    mMaterialDialog = DialogUtils.customMaterialDialog(this, mMaterialDialog,
                            getString(R.string.tips), getString(R.string.send_order_tips,
                                    getString(R.string.common_type_third)), getString(R.string
                                    .confirm), getString(R.string.cancel),
                            102, mHandler);
                }
                break;
            case R.id.clFourth:
                if (mIvFourth.isEnabled()) {
                    mMaterialDialog = DialogUtils.customMaterialDialog(this, mMaterialDialog,
                            getString(R.string.tips), getString(R.string.send_order_tips,
                                    getString(R.string.common_type_forth)), getString(R.string
                                    .confirm), getString(R.string.cancel),
                            103, mHandler);
                }
                break;
            case R.id.clFifth:
                if (mIvFifth.isEnabled()) {
                    mMaterialDialog = DialogUtils.customMaterialDialog(this, mMaterialDialog,
                            getString(R.string.tips), getString(R.string.send_order_tips,
                                    getString(R.string.common_type_fifth)), getString(R.string
                                    .confirm), getString(R.string.cancel),
                            104, mHandler);
                }
                break;
            case R.id.clSixth:
                if (mIvSixth.isEnabled()) {
                    mMaterialDialog = DialogUtils.customMaterialDialog(this, mMaterialDialog,
                            getString(R.string.tips), getString(R.string.send_order_tips,
                                    getString(R.string.device_setting_type_fourth)),
                            getString(R.string
                                    .confirm), getString(R.string.cancel),
                            105, mHandler);
                }
                break;
            case R.id.clSeventh:
                if (mIvSeventh.isEnabled()) {
                    mMaterialDialog = DialogUtils.customMaterialDialog(this, mMaterialDialog,
                            getString(R.string.tips), getString(R.string.send_order_tips,
                                    getString(R.string.device_setting_type_tenth)),
                            getString(R.string
                                    .confirm), getString(R.string.cancel),
                            106, mHandler);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 标题栏Navigation点击监听器
     */
    private final View.OnClickListener mNavigationOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            finish();
        }
    };

    /**
     * 消息处理
     */
    private final Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            try {
                AAABaseResponseBean responseBean;
                AAARequestBean requestBean;
                switch (msg.what) {
                    case TConstant.REQUEST_URL_SEND_REMOTE_CONTROL:
//                        dismisDialog();
                        if (msg.obj == null)
                            showMessage(R.string.request_unkonow_prompt);
                        else {
                            responseBean = (AAABaseResponseBean) msg.obj;
                            requestBean = mGson.fromJson(responseBean.getRequestObject(),
                                    AAARequestBean.class);
                            if (responseBean.getCode() == TConstant.RESPONSE_SUCCESS)
                                showMessage(R.string.send_success_tips);
                            else
                                showMessage(R.string.request_unkonow_prompt);
                            if (requestBean.getCmdValue() != null)
                                resetStatus(requestBean.getCmdValue() + 99);
                        }
                        break;
                    case Constant.HANDLE_CONFIRM_ACTION: // 确定
                        switch (msg.arg1) {
                            case 100:
                            case 106:
                            case 105:
                            case 104:
                            case 103:
                            case 102:
                            case 101:
                            case 107:
                                sendRemoteControl(msg.arg1 % 100 + 1);
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
        }
    });
}
