package com.yyt.trackcar.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.alpha.XUIAlphaTextView;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAABaseResponseBean;
import com.yyt.trackcar.bean.RequestBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.dbflow.AAADeviceModel;
import com.yyt.trackcar.dbflow.AAAUserModel;
import com.yyt.trackcar.dbflow.DeviceInfoModel;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.PortraitModel;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CWRequestUtils;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.EmojiFilter;
import com.yyt.trackcar.utils.ErrorCode;
import com.yyt.trackcar.utils.PortraitUtils;
import com.yyt.trackcar.utils.RequestToastUtils;
import com.yyt.trackcar.utils.SettingSPUtils;
import com.yyt.trackcar.utils.TConstant;
import com.yyt.trackcar.utils.XToastUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      CustomInputSecondFragment
 * @ author:        QING
 * @ createTime:    2020/3/12 08:32
 * @ describe:      TODO 输入设置页面
 */
@Page(name = "CustomInputSecond", params = {CWConstant.TYPE, CWConstant.TITLE, CWConstant.CONTENT
        , TConstant.DEVICE_IMEI})
public class CustomInputSecondFragment extends BaseFragment {
    @BindView(R.id.tvPrompt)
    TextView mTvPrompt; // 提示文本
    @BindView(R.id.etInput)
    EditText mEtInput; // 输入文本编辑
    @BindView(R.id.tvQuestion)
    XUIAlphaTextView mTvQuestion; // 其他问题文本
    @BindView(R.id.tvTextLen)
    TextView mTvTextLen; // 字数提示文本
    @AutoWired
    String title; // 标题
    @AutoWired
    String content; // 内容
    @AutoWired
    int type; // 1短号/亲情号 2地点名称 3手机号码  4宝贝名称 5上课禁用名称,6通讯录短号/亲情号 7通讯录手机号码 8备注
    @AutoWired
    String deviceImei; // 设备号
    private AAADeviceModel mDeviceModel; // 设备对象
    private int mMaxTextLen; // 最大字数长度

    private String deviceNickname;
    private String mobilePhone;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_custom_input_second;
    }

    @Override
    protected void initArgs() {
        XRouter.getInstance().inject(this);
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(title);
        String text;
        if (type == 5)
            text = getString(R.string.complete);
        else
            text = getString(R.string.save);
        titleBar.addAction(new TitleBar.TextAction(text) {
            @Override
            public void performAction(View view) {
                String inputText = mEtInput.getText().toString().trim();
                if ((TextUtils.isEmpty(inputText) && type != 6) || EmojiFilter.containsEmoji(inputText))
                    XToastUtils.toast(mEtInput.getHint().toString());
                else if (inputText.equals(content))
                    popToBack();
                else {
                    DeviceInfoModel infoModel;
                    Intent intent;
                    Bundle bundle;
                    switch (type) {
                        case 1: // 短号/亲情号
                            infoModel = getDeviceInfo();
                            infoModel.setShortNumber(inputText);
                            updateWatchInfo(infoModel);
                            break;
                        case 3: // 手机号码
//                            infoModel = getDeviceInfo();
//                            infoModel.setPhone(inputText);
//                            updateWatchInfo(infoModel);
                            if (inputText.length() >= 9) {
                                mobilePhone = inputText;
                                if (!NetworkUtils.isNetworkAvailable()) {
                                    RequestToastUtils.toastNetwork();
                                    break;
                                }
                                AAADeviceModel deviceModel = getTrackDeviceModel();
                                AAAUserModel userModel = getTrackUserModel();
                                if (userModel != null && deviceModel != null) {
                                    CarGpsRequestUtils.bindMobileForDevice(userModel, deviceModel
                                            , inputText, mHandler);
                                }
                            } else {
                                showMessage(R.string.mobile_hint);
                            }
                            break;
                        case 4: // 宝贝名称
                            deviceNickname = inputText;
                            setBabyNameAndHead(inputText);
                            break;
//                        case 5: // 上课禁用名称
//                        case 6: // 通讯录短号/亲情号
//                        case 7: // 通讯录手机号码
//                            intent = new Intent();
//                            bundle = new Bundle();
//                            bundle.putString(CWConstant.NAME, inputText);
//                            intent.putExtras(bundle);
//                            setFragmentResult(Activity.RESULT_OK, intent);
//                            popToBack();
//                            break;
                        case 8: // 备注
                            deviceNickname = inputText;
                            updateDeviceRemark(inputText);
                            break;
                        default:
                            intent = new Intent();
                            bundle = new Bundle();
                            bundle.putString(CWConstant.NAME, inputText);
                            intent.putExtras(bundle);
                            setFragmentResult(Activity.RESULT_OK, intent);
                            popToBack();
                            break;
                    }
                }
            }
        });
        return titleBar;
    }

    @SuppressLint("DefaultLocale")
    @Override
    protected void initViews() {
        if (!TextUtils.isEmpty(deviceImei)) {
            for (AAADeviceModel model : getTrackDeviceList()) {
                if (deviceImei.equals(model.getDeviceImei())) {
                    mDeviceModel = model;
                    break;
                }
            }
        }
        if (mDeviceModel == null) {
            mDeviceModel = getTrackDeviceModel();
        }
        mEtInput.setText(content);
        switch (type) {
            case 1: // 短号/亲情号
                mEtInput.setFilters(new InputFilter[]{new InputFilter
                        .LengthFilter(19)});
                mEtInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                mEtInput.setHint(R.string.short_number_hint);
                mTvPrompt.setText(R.string.change_short_number_input_prompt);
                mTvQuestion.setVisibility(View.GONE);
                mMaxTextLen = 19;
                break;
            case 2: // 地点名称
                mEtInput.setFilters(new InputFilter[]{new InputFilter
                        .LengthFilter(12)});
                mEtInput.setInputType(InputType.TYPE_CLASS_TEXT);
                mEtInput.setHint(R.string.location_name_hint);
                mTvPrompt.setVisibility(View.INVISIBLE);
                mTvQuestion.setVisibility(View.GONE);
                mMaxTextLen = 12;
                break;
            case 3: // 手机号码
                mEtInput.setFilters(new InputFilter[]{new InputFilter
                        .LengthFilter(19)});
                mEtInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                mEtInput.setHint(R.string.mobile_hint);
                mTvPrompt.setText(R.string.change_phone_number_input_prompt);
//            mTvQuestion.setText(R.string.change_phone_number_question);
                mTvQuestion.setVisibility(View.GONE);
                mMaxTextLen = 19;
                break;
            case 10: // 吃药
                mEtInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
                mEtInput.setInputType(InputType.TYPE_CLASS_TEXT);
                mEtInput.setHint(getString(R.string.input_hint, title));
                mTvPrompt.setVisibility(View.INVISIBLE);
                mTvQuestion.setVisibility(View.GONE);
                mMaxTextLen = 8;
                break;
            case 6: // 通讯录短号/亲情号
                mEtInput.setFilters(new InputFilter[]{new InputFilter
                        .LengthFilter(6)});
                mEtInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                mEtInput.setHint(R.string.short_number_hint);
                mTvPrompt.setText(" ");
                mTvQuestion.setVisibility(View.GONE);
                mMaxTextLen = 6;
                break;
            case 7: // 通讯录手机号码
                mEtInput.setFilters(new InputFilter[]{new InputFilter
                        .LengthFilter(19)});
                mEtInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                mEtInput.setHint(R.string.mobile_hint);
                mTvPrompt.setText(" ");
                mTvQuestion.setVisibility(View.GONE);
                mMaxTextLen = 19;
                break;
            case 8: // 备注
                mEtInput.setFilters(new InputFilter[]{new InputFilter
                        .LengthFilter(200)});
                mEtInput.setInputType(InputType.TYPE_CLASS_TEXT);
                mEtInput.setHint(getString(R.string.input_hint, title));
                mTvPrompt.setText(" ");
                mTvQuestion.setVisibility(View.GONE);
                mMaxTextLen = 200;
                break;
            default:
                mEtInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
                mEtInput.setInputType(InputType.TYPE_CLASS_TEXT);
                mEtInput.setHint(getString(R.string.input_hint, title));
                mTvPrompt.setVisibility(View.INVISIBLE);
                mTvQuestion.setVisibility(View.GONE);
                mMaxTextLen = 12;
                break;
        }
        mTvTextLen.setText(String.format("%d/%d", mEtInput.getText().toString().length(),
                mMaxTextLen));
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        mEtInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("DefaultLocale")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTvTextLen.setText(String.format("%d/%d", mEtInput.getText().toString().length(),
                        mMaxTextLen));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * 修改宝贝资料
     *
     * @param model 宝贝信息对向
     */
    private void updateWatchInfo(DeviceInfoModel model) {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork();
            return;
        }
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null) {
            if (TextUtils.isEmpty(model.getImei())) {
                model.setImei(deviceModel.getImei());
            }
            model.setU_id(userModel.getU_id());
            CWRequestUtils.getInstance().updateWatchInfo(getContext(), userModel.getToken(),
                    deviceModel.getD_id(), model, mHandler);
        }
    }

    /**
     * 修改绑定设备昵称
     *
     * @param name 设备昵称
     */
    private void updateBindBabyName(String name) {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork();
            return;
        }
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null) {
            CWRequestUtils.getInstance().updateBindBabyName(getContext(), userModel.getU_id(),
                    userModel.getToken(), deviceModel.getId(), name, mHandler);
        }
    }

    /**
     * 设置设备昵称和头像
     */
    private void setBabyNameAndHead(String babyName) {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork();
            return;
        }
//        UserModel userModel = getUserModel();
//        DeviceModel deviceModel = getDevice();
//        if (userModel != null && deviceModel != null) {
//            DeviceInfoModel infoModel = getDeviceInfo();
//            CWRequestUtils.getInstance().setBabyNameAndHead(MainApplication.getInstance(),
//                    getUserModel().getToken(),
//                    String.valueOf(deviceModel.getD_id()), deviceModel.getImei(), babyName,
//                    infoModel == null || infoModel.getHead() == null ? "" : infoModel.getHead(),
//                    mHandler);
//        }
        AAAUserModel userModel = getTrackUserModel();
        if (userModel != null && mDeviceModel != null && !TextUtils.isEmpty(mDeviceModel.getDeviceImei())) {
            CarGpsRequestUtils.resetDeviceNickname(userModel, mDeviceModel, babyName, mHandler);
        }
    }

    /**
     * 设置设备 备注信息
     */
    private void updateDeviceRemark(String remark) {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork();
            return;
        }
        AAAUserModel userModel = getTrackUserModel();
        if (userModel != null && mDeviceModel != null && !TextUtils.isEmpty(mDeviceModel.getDeviceImei())) {
            CarGpsRequestUtils.updateDeviceRemark(userModel, mDeviceModel.getDeviceImei(), remark,
                    mHandler);
        }
    }

    @SingleClick
    @OnClick({R.id.tvQuestion})
    public void onClick(View v) {
        Bundle bundle;
        switch (v.getId()) {
            case R.id.tvQuestion: // 其他问题
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
                AAABaseResponseBean responseBean;
                switch (msg.what) {
                    case CWConstant.REQUEST_URL_UPDATE_WATCH_INFO: // 修改宝贝资料
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                XToastUtils.toast(R.string.update_success_prompt);
                                DeviceInfoModel infoModel =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), DeviceInfoModel.class);
                                DeviceInfoModel model = getDeviceInfo();
                                if (!TextUtils.isEmpty(model.getImei()) && model.getU_id() != 0) {
                                    infoModel.setU_id(model.getU_id());
                                    infoModel.setImei(model.getImei());
                                    infoModel.setCreatetime(model.getCreatetime());
                                    infoModel.setHome_info(model.getHome_info());
                                    infoModel.setSchool_info(model.getSchool_info());
                                    infoModel.save();
                                }
                                popToBack();
                            } else
                                RequestToastUtils.toast(resultBean.getCode());
                        }
                        break;
                    case CWConstant.REQUEST_URL_UPDATE_BIND_BABY_NAME: // 修改绑定设备昵称
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                XToastUtils.toast(R.string.update_success_prompt);
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                userModel = getUserModel();
                                if (userModel != null && userModel.getU_id() == requestBean.getU_id()) {
                                    DeviceModel deviceModel = getDevice();
                                    if (deviceModel != null && deviceModel.getId() == requestBean.getId()) {
                                        deviceModel.setName(requestBean.getName());
                                    }
                                    for (DeviceModel model : getDeviceList()) {
                                        if (model.getId() == requestBean.getId()) {
                                            model.setName(requestBean.getName());
                                            model.save();
                                            break;
                                        }
                                    }
                                }
                                Intent intent = new Intent();
                                Bundle bundle = new Bundle();
                                bundle.putString(CWConstant.NAME, requestBean.getName());
                                intent.putExtras(bundle);
                                setFragmentResult(Activity.RESULT_OK, intent);
                                popToBack();
                            } else
                                RequestToastUtils.toast(resultBean.getCode());
                        }
                        break;
                    case CWConstant.REQUEST_URL_SET_BABY_NAME_AND_HEAD: // 设置设备头像和昵称
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS || resultBean.getCode() == CWConstant.WAIT_ONLINE_UPDATE) {
                                if (resultBean.getCode() == CWConstant.WAIT_ONLINE_UPDATE)
                                    XToastUtils.toast(R.string.wait_online_update_prompt);
                                else
                                    XToastUtils.toast(R.string.update_success_prompt);
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                DeviceModel deviceModel = getDevice();
                                if (deviceModel != null && deviceModel.getImei().equals(requestBean.getImei())) {
                                    DeviceInfoModel infoModel = getDeviceInfo();
                                    infoModel.setNickname(requestBean.getNickname());
                                    infoModel.setHead(requestBean.getHeadurl());
                                    infoModel.save();
                                    PortraitModel portraitModel = new PortraitModel();
                                    portraitModel.setImei(deviceModel.getImei());
                                    portraitModel.setUserId(deviceModel.getImei());
                                    if (TextUtils.isEmpty(infoModel.getNickname())) {
                                        if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 0)
                                            portraitModel.setName(getString(R.string.baby));
                                        else
                                            portraitModel.setName(getString(R.string.device));
                                    } else
                                        portraitModel.setName(infoModel.getNickname());
                                    portraitModel.setUrl(infoModel.getHead());
                                    portraitModel.save();
                                    PortraitUtils.getInstance().updatePortrait(portraitModel);
                                }
                                Intent intent = new Intent();
                                Bundle bundle = new Bundle();
                                bundle.putString(CWConstant.NAME, requestBean.getNickname());
                                intent.putExtras(bundle);
                                setFragmentResult(Activity.RESULT_OK, intent);
                                popToBack();
                            } else
                                RequestToastUtils.toast(resultBean.getCode());
                        }
                        break;
                    case TConstant.REQUEST_RESET_NICKNAME:  //设置昵称
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
                            responseBean = (AAABaseResponseBean) msg.obj;
                            if (responseBean.getCode() == TConstant.RESPONSE_SUCCESS_NEW
                                    || responseBean.getCode() == TConstant.RESPONSE_SUCCESS) {
                                XToastUtils.toast(R.string.update_success_prompt);
                                mDeviceModel.setDeviceName(deviceNickname);
                                List<AAADeviceModel> trackDeviceList = getTrackDeviceList();
                                for (AAADeviceModel model : trackDeviceList) {
                                    if (mDeviceModel.getDeviceImei().equals(model.getDeviceImei())) {
                                        model.setDeviceName(deviceNickname);
                                        break;
                                    }
                                }
                                Intent intent = new Intent();
                                Bundle bundle = new Bundle();
                                bundle.putString(CWConstant.NAME, deviceNickname);
                                intent.putExtras(bundle);
                                setFragmentResult(Activity.RESULT_OK, intent);
                                popToBack();
                            } else {
                                XToastUtils.toast(R.string.request_unkonow_prompt);
                            }
                        }
                        break;
                    case TConstant.REQUEST_BIND_MOBILE_FOR_DEVICE:  //设置绑定的手机号码
                        if (msg.obj == null) {
                            showMessage(R.string.request_unkonow_prompt);
                        } else {
                            responseBean = (AAABaseResponseBean) msg.obj;
                            if (responseBean.getCode() == TConstant.RESPONSE_SUCCESS) {
                                XToastUtils.toast(R.string.update_success_prompt);
                                getTrackDeviceModel().setBindMobile(mobilePhone);
                                setFragmentResult(Activity.RESULT_OK, null);
                                popToBack();
                            } else {
                                showMessage(ErrorCode.getResId(responseBean.getCode()));
                            }
                        }
                        break;
                    case TConstant.REQUEST_UPDATE_DEVICE_REMARK:  // 设置设备 备注信息
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
                            responseBean = (AAABaseResponseBean) msg.obj;
                            if (responseBean.getCode() == TConstant.RESPONSE_SUCCESS) {
                                XToastUtils.toast(R.string.update_success_prompt);
                                mDeviceModel.setDeviceRemark(deviceNickname);
                                List<AAADeviceModel> trackDeviceList = getTrackDeviceList();
                                for (AAADeviceModel model : trackDeviceList) {
                                    if (mDeviceModel.getDeviceImei().equals(model.getDeviceImei())) {
                                        model.setDeviceRemark(deviceNickname);
                                        break;
                                    }
                                }
                                Intent intent = new Intent();
                                Bundle bundle = new Bundle();
                                bundle.putString(CWConstant.NAME, deviceNickname);
                                intent.putExtras(bundle);
                                setFragmentResult(Activity.RESULT_OK, intent);
                                popToBack();
                            } else {
                                showMessage(ErrorCode.getResId(responseBean.getCode()));
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
