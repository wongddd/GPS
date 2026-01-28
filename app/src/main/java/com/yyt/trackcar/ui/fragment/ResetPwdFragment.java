package com.yyt.trackcar.ui.fragment;

import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xutil.app.ActivityUtils;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.MainApplication;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAABaseResponseBean;
import com.yyt.trackcar.bean.PostMessage;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.ui.activity.LoginActivity;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.RegularUtils;
import com.yyt.trackcar.utils.RequestToastUtils;
import com.yyt.trackcar.utils.SettingSPUtils;
import com.yyt.trackcar.utils.TConstant;
import com.yyt.trackcar.utils.XToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      ResetPwdFragment
 * @ author:        QING
 * @ createTime:    2020-03-02 15:57
 * @ describe:      TODO 设置密码页面
 */
@Page(name = "ResetPwd", params = {CWConstant.TYPE, CWConstant.USERNAME, CWConstant.COUNTRY_CODE,
        CWConstant.VERIFICATION_CODE})
public class ResetPwdFragment extends BaseFragment {
    @BindView(R.id.tvTitle)
    TextView mTvTitle; // 标题文本
    @BindView(R.id.tvTitlePrompt)
    TextView mTvTitlePrompt; // 标题提示文本
    @BindView(R.id.tvInput)
    TextView mTvInput; // 输入文本
    @BindView(R.id.etInput)
    EditText mEtInput; // 输入文本编辑
    @BindView(R.id.tvSecondInput)
    TextView mTvSecondInput; // 第二输入文本
    @BindView(R.id.etSecondInput)
    EditText mEtSecondInput; // 第二输入文本编辑
    @BindView(R.id.confirmBtn)
    Button mConfirmBtn; // 确认按钮
    @AutoWired
    String countryCode; // 国家编码
    @AutoWired
    String username; // 用户名
    @AutoWired
    String verificationCode; // 验证码
    @AutoWired
    int type; // 找回类型 1邮箱 2手机

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_reset_pwd;
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
        if (type == 4)
            type = 2;
        else
            type = 1;
        mTvTitle.setText(R.string.reset_pwd);
        mTvTitlePrompt.setText(R.string.set_pwd_prompt);
        mTvInput.setText(R.string.reset_pwd_new);
        mEtInput.setHint(R.string.new_pwd_hint);
        mTvSecondInput.setText(R.string.reset_pwd_confirm);
        mEtSecondInput.setHint(R.string.confirm_pwd_hint);
        mConfirmBtn.setText(R.string.complete);
        mEtInput.setFilters(new InputFilter[]{new InputFilter
                .LengthFilter(16)});
        mEtInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mEtSecondInput.setFilters(new InputFilter[]{new InputFilter
                .LengthFilter(16)});
        mEtSecondInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }

    @SingleClick
    @OnClick({R.id.confirmBtn, R.id.ibPwdShow, R.id.ibSecondPwdShow})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirmBtn: // 完成
                String pwd = mEtInput.getText().toString().trim();
                String confirmPwd = mEtSecondInput.getText().toString().trim();
                if (TextUtils.isEmpty(pwd)) {
                    XToastUtils.toast(mEtInput.getHint().toString());
                    mEtInput.requestFocus();
                } else if (!RegularUtils.isPwd(pwd)) {
                    XToastUtils.toast(R.string.set_pwd_prompt);
                    mEtInput.requestFocus();
                } else if (pwd.equals(confirmPwd)) {
                    if (!NetworkUtils.isNetworkAvailable()) {
                        RequestToastUtils.toastNetwork();
                        return;
                    }
//                    CWRequestUtils.getInstance().findPwd(mActivity, countryCode, username, pwd,
//                            verificationCode, String.valueOf(type), mHandler);
                    showDialog();
                    CarGpsRequestUtils.forgetPassword(username,pwd,verificationCode,mHandler);
                } else {
                    XToastUtils.toast(R.string.reset_pwd_input_different_prompt);
                    mEtSecondInput.requestFocus();
                }
                break;
            case R.id.ibPwdShow:
                v.setSelected(!v.isSelected());
                if (v.isSelected()) {
                    //如果选中，显示密码
                    mEtInput.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    //否则隐藏密码
                    mEtInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                break;
            case R.id.ibSecondPwdShow:
                v.setSelected(!v.isSelected());
                if (v.isSelected()) {
                    //如果选中，显示密码
                    mEtSecondInput.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    //否则隐藏密码
                    mEtSecondInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
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
            dismisDialog();
            try {
                RequestResultBean resultBean;
                switch (msg.what) {
                    case CWConstant.REQUEST_URL_FIND_PWD: // 找回密码
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                XToastUtils.toast(R.string.reset_pwd_success_prompt);
                                if (getUserModel() == null)
                                    popToBack("Login", null);
                                else {
                                    SettingSPUtils.getInstance().putString(CWConstant.PASSWORD, "");
                                    SettingSPUtils.getInstance().putString(CWConstant.PASSWORD_LOGIN, "");
                                    SettingSPUtils.getInstance().putString(CWConstant.TOKEN, "");
                                    SettingSPUtils.getInstance().putLong(CWConstant.U_ID, -1);
                                    MainApplication.getInstance().setDeviceModel(null);
                                    MainApplication.getInstance().setUserModel(null);
                                    MainApplication.getInstance().getDeviceList().clear();
                                    EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_FINISH));
                                    ActivityUtils.startActivity(LoginActivity.class);
                                }
//                                popToBack("Login", null);
                            } else
                                RequestToastUtils.toast(resultBean.getCode());
                        }
                        break;
                    case TConstant.REQUEST_RESET_PASSWORD:  //邮箱重置密码
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
                            try {
                                AAABaseResponseBean response = (AAABaseResponseBean) msg.obj;
                                if (response.getCode() == TConstant.RESPONSE_SUCCESS_NEW
                                ||response.getCode() == TConstant.RESPONSE_SUCCESS) {
                                    showMessage(R.string.change_pwd_success_prompt);
//                                    if (getUserModel() == null)
//                                        popToBack("Login", null);
//                                    else {
                                        SettingSPUtils.getInstance().putString(CWConstant.PASSWORD, "");
                                        SettingSPUtils.getInstance().putString(CWConstant.PASSWORD_LOGIN, "");
                                        SettingSPUtils.getInstance().putString(CWConstant.TOKEN, "");
                                        SettingSPUtils.getInstance().putLong(CWConstant.U_ID, -1);
                                        MainApplication.getInstance().setDeviceModel(null);
                                        MainApplication.getInstance().setUserModel(null);
                                        MainApplication.getInstance().getDeviceList().clear();
                                        EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_FINISH));
                                        ActivityUtils.startActivity(LoginActivity.class);
//                                    }
                                } else
                                    RequestToastUtils.toast(response.getCode());
                            }catch (Exception e){
                                e.printStackTrace();
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
