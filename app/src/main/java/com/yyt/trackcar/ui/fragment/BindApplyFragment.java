package com.yyt.trackcar.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xutil.XUtil;
import com.xuexiang.xutil.app.ActivityUtils;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.PostMessage;
import com.yyt.trackcar.bean.RequestBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.ui.activity.ChangeDeviceNameActivity;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CWRequestUtils;
import com.yyt.trackcar.utils.EmojiFilter;
import com.yyt.trackcar.utils.RequestToastUtils;
import com.yyt.trackcar.utils.SettingSPUtils;
import com.yyt.trackcar.utils.XToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      BindApplyFragment
 * @ author:        QING
 * @ createTime:    2020-02-28 15:57
 * @ describe:      TODO 绑定申请页面
 */
@Page(name = "BindApply", params = {CWConstant.TYPE, CWConstant.NAME, CWConstant.IMEI})
public class BindApplyFragment extends BaseFragment {
    @BindView(R.id.tvTitle)
    TextView mTvTitle; // 标题文本
    @BindView(R.id.tvTitlePrompt)
    TextView mTvTitlePrompt; // 标题提示文本
    @BindView(R.id.tvInput)
    TextView mTvInput; // 输入文本
    @BindView(R.id.etInput)
    EditText mEtInput; // 输入文本编辑
    //    @BindView(R.id.tvSecondInput)
//    TextView mTvSecondInput; // 第二输入文本
//    @BindView(R.id.etSecondInput)
//    EditText mEtSecondInput; // 第二输入文本编辑
    @BindView(R.id.confirmBtn)
    Button mConfirmBtn; // 确认按钮
    @AutoWired
    String name; // 管理员名称
    @AutoWired
    String imei; // 设备imei
    @AutoWired
    int type; // 0.从未绑定界面 1.首页绑定新设备 2.首页绑定新设备
    private boolean mIsBind; // 是否已绑定设备
    private boolean mIsBinding;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_custom_input;
    }

    @Override
    protected void initArgs() {
        XRouter.getInstance().inject(this);
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle("");
        return titleBar;
    }

    @Override
    protected void initViews() {
        mTvTitle.setText(R.string.bind_apply);
        if (name == null)
            name = "";
//        if (type == 1)
//            mTvTitlePrompt.setText(getString(R.string.bind_apply_has_manager_prompt, name));
//        else
        mTvTitlePrompt.setText(R.string.bind_apply_prompt);
        mTvInput.setText(R.string.contact_name);
//        if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 0)
//            mEtInput.setHint(R.string.contact_name_hint);
//        else
        mEtInput.setHint(R.string.contact_device_name_hint);
//        mTvSecondInput.setText(R.string.baby_name);
//        mEtSecondInput.setHint(R.string.baby_name_hint);
        mConfirmBtn.setText(R.string.send_apply);
        mEtInput.setFilters(new InputFilter[]{new InputFilter
                .LengthFilter(12)});
        mEtInput.setInputType(InputType.TYPE_CLASS_TEXT);
//        mEtSecondInput.setFilters(new InputFilter[]{new InputFilter
//                .LengthFilter(12)});
//        mEtSecondInput.setInputType(InputType.TYPE_CLASS_TEXT);
    }

    @SingleClick
    @OnClick({R.id.confirmBtn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirmBtn: // 确认按钮
                String inputText = mEtInput.getText().toString().trim();
//                String inputSecondText = mEtSecondInput.getText().toString().trim();
                if (TextUtils.isEmpty(inputText) || EmojiFilter.containsEmoji(inputText)) {
                    XToastUtils.toast(mEtInput.getHint().toString());
                    mEtInput.requestFocus();
                }
//                else if (TextUtils.isEmpty(inputSecondText)) {
//                    XToastUtils.toast(mEtSecondInput.getHint().toString());
//                    mEtSecondInput.requestFocus();
//                }
                else
                    bindDevice(inputText, "");
                break;
            default:
                break;
        }
    }

    /**
     * 绑定设备
     */
    private void bindDevice(String inputText, String inputSecondText) {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork();
            return;
        }
        UserModel userModel = getUserModel();
        if (userModel != null && !TextUtils.isEmpty(imei) && !mIsBinding && !mIsBind) {
            mIsBinding = true;
            int deviceType = SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_MODEL, 1);
            CWRequestUtils.getInstance().bindDevice(getContext(), getUserModel().getToken(),
                    imei, inputSecondText, inputText, deviceType, mHandler);
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
                Bundle bundle;
                switch (msg.what) {
                    case CWConstant.REQUEST_URL_BIND_DEVICE: // 绑定设备
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                if (!mIsBind) {
//                                    mIsBind = true;
                                    Intent intent = new Intent(XUtil.getContext(),
                                            ChangeDeviceNameActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    bundle = new Bundle();
                                    bundle.putInt(CWConstant.TYPE, type);
                                    bundle.putString(CWConstant.IMEI, imei);
                                    intent.putExtras(bundle);
                                    if (type == 0)
                                        EventBus.getDefault().post(new PostMessage(CWConstant
                                                .POST_MESSAGE_FINISH));
                                    else
                                        EventBus.getDefault().post(new PostMessage(CWConstant
                                                .POST_MESSAGE_BACK_TO_MAIN));
                                    ActivityUtils.startActivity(intent);
//                                    bundle = new Bundle();
//                                    bundle.putString(CWConstant.USERNAME,
//                                            mEtInput.getText().toString().trim());
////                                    bundle.putString(CWConstant.NAME, mEtSecondInput.getText()
////                                    .toString().trim());
//                                    bundle.putString(CWConstant.NAME, "0");
//                                    bundle.putInt(CWConstant.TYPE, type);
//                                    bundle.putString(CWConstant.IMEI, imei);
//                                    if (type == 0) {
//                                        openNewPage(ChangeDeviceNameFragment.class, bundle);
//                                        popToBack("BindDevice", null);
//                                    } else {
//                                        EventBus.getDefault().post(new PostMessage(CWConstant
//                                        .POST_MESSAGE_BACK_TO_MAIN));
//                                        openNewPage(ChangeDeviceNameFragment.class, bundle);
//                                    }
//                                    popToBack();
//                                    if (type == 0) {
//                                        EventBus.getDefault().post(new PostMessage(CWConstant
//                                        .POST_MESSAGE_FINISH));
//                                        Intent intent = new Intent(XUtil.getContext(),
//                                                BindSuccessActivity.class);
//                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                        bundle = new Bundle();
//                                        bundle.putInt(CWConstant.TYPE, 0);
//                                        bundle.putString(CWConstant.IMEI, imei);
//                                        intent.putExtras(bundle);
//                                        ActivityUtils.startActivity(intent);
//                                    } else {
//                                        EventBus.getDefault().post(new PostMessage(CWConstant
//                                        .POST_MESSAGE_BACK_TO_MAIN));
//                                        bundle = new Bundle();
//                                        bundle.putInt(CWConstant.TYPE, 1);
//                                        bundle.putString(CWConstant.IMEI, imei);
//                                        openNewPage(BindSuccessFragment.class, bundle);
//                                    }
                                }
                            } else if (resultBean.getCode() == CWConstant.BIND_ADMIN_IS_SURE) {
                                if (!mIsBind) {
                                    mIsBind = true;
                                    if (type == 0) {
                                        bundle = new Bundle();
                                        bundle.putString(CWConstant.NAME, name);
                                        openNewPage(WaitManagerConfirmFragment.class, bundle);
                                        popToBack("BindDevice", null);
                                    } else {
                                        EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_BACK_TO_MAIN));
                                        bundle = new Bundle();
                                        bundle.putString(CWConstant.NAME, name);
                                        openNewPage(WaitManagerConfirmFragment.class, bundle);
                                    }
                                }
                            } else {
                                RequestToastUtils.toast(resultBean.getCode());
                                if (resultBean.getCode() == CWConstant.DEVICE_NOT_ACTIVE)
                                    popToBack();
                            }
                        }
                        mIsBinding = false;
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
