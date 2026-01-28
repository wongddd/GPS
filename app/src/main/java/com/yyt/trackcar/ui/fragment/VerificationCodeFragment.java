package com.yyt.trackcar.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.socks.library.KLog;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.utils.CountDownButtonHelper;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAABaseResponseBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.bean.ResultBean;
import com.yyt.trackcar.dbflow.VerificationCodeModel;
import com.yyt.trackcar.dbflow.VerificationCodeModel_Table;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.ErrorCode;
import com.yyt.trackcar.utils.RequestToastUtils;
import com.yyt.trackcar.utils.TConstant;
import com.yyt.trackcar.utils.TimeUtils;
import com.yyt.trackcar.utils.XToastUtils;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      VerificationCodeFragment
 * @ author:        QING
 * @ createTime:    2020-02-26 14:58
 * @ describe:      TODO 验证码页面
 */
@Page(name = "VerificationCode", params = {CWConstant.TYPE, CWConstant.USERNAME,
        CWConstant.COUNTRY_CODE, CWConstant.VERIFICATION_CODE, CWConstant.TIME})
public class VerificationCodeFragment extends BaseFragment {
    @BindView(R.id.etVerificationCode)
    TextView mEtVerificationCode; // 验证码文本编辑
    @BindView(R.id.verificationCodeBtn)
    TextView mVerificationCodeBtn; // 获取验证码按钮
    @AutoWired
    String countryCode; // 国家编码
    @AutoWired
    String username; // 用户名
    /**
     * 类型  1、邮箱注册  2、手机注册  3、邮箱找回  4、手机找回
     */
    @AutoWired
    int type; // 类型 1邮箱注册 2手机注册 3邮箱找回 4手机找回
    @AutoWired
    String verificationCode; // 验证码
    @AutoWired
    long time; // 时间
    private CountDownButtonHelper mCountDownHelper;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_verification_code;
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
        if (time == 888) {
            XToastUtils.toast(R.string.verification_code_to_max_prompt);
            time = 60;
        }
        mCountDownHelper = new CountDownButtonHelper(mVerificationCodeBtn, (int) time);
        mCountDownHelper.start();
    }

    /**
     * 发送验证码
     */
    private void sendVerificationCode() {
        if (type == 1)
//            CWRequestUtils.getInstance().getMailCode(mActivity, username, mHandler);
            CarGpsRequestUtils.getVerifyCode(null, username, type, 1, null, mHandler);
        else if (type == 3)
            CarGpsRequestUtils.getVerifyCode(null, username, type - 2, 1, null, mHandler);
//            CWRequestUtils.getInstance().findPwdMailCode(mActivity, username, mHandler);
        else if (type == 4)
            CarGpsRequestUtils.getVerifyCode(countryCode, username, type - 2, 1, null, mHandler);
//            CWRequestUtils.getInstance().findPwdAuthCode(mActivity, countryCode, username, mHandler);
        else
            CarGpsRequestUtils.getVerifyCode(countryCode, username, type, 1, null, mHandler);
//            CWRequestUtils.getInstance().getAuthCode(mActivity, countryCode, username, mHandler);
    }

    @SuppressLint("SetTextI18n")
    @SingleClick
    @OnClick({R.id.nextStepBtn, R.id.tvNoReceive, R.id.verificationCodeBtn})
    public void onClick(View v) {
        Bundle bundle;
        switch (v.getId()) {
            case R.id.verificationCodeBtn: // 获取验证码
                if (NetworkUtils.isNetworkAvailable()) {
                    VerificationCodeModel verificationCodeModel =
                            SQLite.select().from(VerificationCodeModel.class)
                                    .where(VerificationCodeModel_Table.username.eq(username))
                                    .querySingle();
                    if (verificationCodeModel == null) {
                        if (time != 60)
                            mCountDownHelper = new CountDownButtonHelper(mVerificationCodeBtn, 60);
                        mCountDownHelper.start();
                        sendVerificationCode();
                    } else {
                        long time = System.currentTimeMillis();
                        long createtime = verificationCodeModel.getCreatetime();
                        String date = TimeUtils.formatUTC(time, "yyyy-MM-dd");
                        String createDate = TimeUtils.formatUTC(createtime, "yyyy-MM" +
                                "-dd");
                        if ((time - createtime < TimeUtils.HOUR * 1000 && verificationCodeModel.getCount() >= 5)
                                || (date.equals(createDate) && verificationCodeModel.getCount() >= 10))
                            XToastUtils.toast(R.string.verification_code_to_max_prompt);
                        else if (time - createtime < TimeUtils.MINUTE * 1000) {
                            if (time != 60)
                                mCountDownHelper = new CountDownButtonHelper(mVerificationCodeBtn
                                        , 60);
                            mCountDownHelper.start();
                        } else {
                            if (time != 60)
                                mCountDownHelper = new CountDownButtonHelper(mVerificationCodeBtn
                                        , 60);
                            mCountDownHelper.start();
                            sendVerificationCode();
//                            if (TextUtils.isEmpty(countryCode))
//                                CarGpsRequestUtils.getVerifyCode(null, username, type, 1, null, mHandler);
//                            else
//                                CarGpsRequestUtils.getVerifyCode(countryCode, username, type, 1, null, mHandler);
                        }
                    }
                } else
                    RequestToastUtils.toastNetwork();
                break;
            case R.id.nextStepBtn: // 下一步
                String inputCode = mEtVerificationCode.getText().toString().trim();
                if (TextUtils.isEmpty(inputCode)) {
                    XToastUtils.toast(mEtVerificationCode.getHint().toString());
                    mEtVerificationCode.requestFocus();
                } else
//                    if (inputCode.equals(verificationCode) || "2573".equals(inputCode))
                {
                    CarGpsRequestUtils.verifyVerificationCode(username, type, inputCode, null, null, mHandler);
//                    bundle = new Bundle();
//                    bundle.putInt(CWConstant.TYPE, type);
//                    bundle.putString(CWConstant.USERNAME, username);
//                    bundle.putString(CWConstant.COUNTRY_CODE, countryCode);
//                    bundle.putString(CWConstant.VERIFICATION_CODE, inputCode);
//                    if (type == 3 || type == 4)
//                        openNewPage(ResetPwdFragment.class, bundle);
//                    else
//                        openNewPage(SetPwdFragment.class, bundle);
                }
//                else {
//                    XToastUtils.toast(R.string.verification_code_input_error_prompt);
//                    mEtVerificationCode.requestFocus();
//                }

                break;
            case R.id.tvNoReceive: // 收不到验证码？
                bundle = new Bundle();
                bundle.putString(CWConstant.TITLE,
                        getString(R.string.no_receive_verification_code));
                if (type == 1 || type == 3)
                    bundle.putInt(CWConstant.TYPE, 1);
                else
                    bundle.putInt(CWConstant.TYPE, 0);
                openNewPage(TextFragment.class, bundle);
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
                switch (msg.what) {
                    case CWConstant.REQUEST_URL_GET_MAIL_CODE: // 获取邮箱验证码
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.U_ALREADY_REGED) {
                                RequestToastUtils.toast(resultBean.getCode());
                                popToBack();
                            } else if (resultBean.getCode() == CWConstant.SUCCESS) {
                                ResultBean resultModel = mGson.fromJson(mGson.toJson(resultBean.getResultBean()),
                                        ResultBean.class);
                                verificationCode = resultModel.getVerificationCode();
                                VerificationCodeModel verificationCodeModel =
                                        SQLite.select().from(VerificationCodeModel.class)
                                                .where(VerificationCodeModel_Table.username.eq(username))
                                                .querySingle();
                                if (verificationCodeModel == null) {
                                    verificationCodeModel = new VerificationCodeModel();
                                    verificationCodeModel.setUsername(username);
                                    verificationCodeModel.setCreatetime(System.currentTimeMillis());
                                    verificationCodeModel.setCount(1);
                                    verificationCodeModel.setVerificationCode(verificationCode);
                                } else {
                                    long time = System.currentTimeMillis();
                                    long createtime = verificationCodeModel.getCreatetime();
                                    String date = TimeUtils.formatUTC(time, "yyyy-MM-dd");
                                    String createDate = TimeUtils.formatUTC(createtime, "yyyy-MM" +
                                            "-dd");
                                    if (date.equals(createDate))
                                        verificationCodeModel.setCount(verificationCodeModel.getCount() + 1);
                                    else
                                        verificationCodeModel.setCount(1);
                                    verificationCodeModel.setCreatetime(time);
                                    verificationCodeModel.setVerificationCode(verificationCode);
                                }
                                verificationCodeModel.save();
                            } else if (resultBean.getCode() == CWConstant.ERROR) {
                                ResultBean resultModel = mGson.fromJson(mGson.toJson(resultBean.getResultBean()),
                                        ResultBean.class);
                                XToastUtils.toast(resultModel.getMsg());
                            } else
                                RequestToastUtils.toast(resultBean.getCode());
                        }
                        break;
                    case CWConstant.REQUEST_URL_FIND_PWD_MAIL_CODE: // 找回密码  邮箱验证码的发送
                    case CWConstant.REQUEST_URL_FIND_PWD_AUTH_CODE: // 找回密码  手机号验证码的发送
                    case CWConstant.REQUEST_URL_GET_AUTH_CODE: // 获取短信验证码
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.U_ALREADY_REGED) {
                                RequestToastUtils.toast(resultBean.getCode());
                                popToBack();
                            } else if (resultBean.getCode() == CWConstant.SUCCESS) {
                                ResultBean resultModel =
                                        mGson.fromJson(mGson.toJson(resultBean.getResultBean()),
                                                ResultBean.class);
                                verificationCode = resultModel.getVerificationCode();
                                VerificationCodeModel verificationCodeModel =
                                        SQLite.select().from(VerificationCodeModel.class)
                                                .where(VerificationCodeModel_Table.username.eq(username))
                                                .querySingle();
                                if (verificationCodeModel == null) {
                                    verificationCodeModel = new VerificationCodeModel();
                                    verificationCodeModel.setUsername(username);
                                    verificationCodeModel.setCreatetime(System.currentTimeMillis());
                                    verificationCodeModel.setCount(1);
                                    verificationCodeModel.setVerificationCode(verificationCode);
                                } else {
                                    long time = System.currentTimeMillis();
                                    long createtime = verificationCodeModel.getCreatetime();
                                    String date = TimeUtils.formatUTC(time, "yyyy-MM-dd");
                                    String createDate = TimeUtils.formatUTC(createtime, "yyyy-MM" +
                                            "-dd");
                                    if (date.equals(createDate))
                                        verificationCodeModel.setCount(verificationCodeModel.getCount() + 1);
                                    else
                                        verificationCodeModel.setCount(1);
                                    verificationCodeModel.setCreatetime(time);
                                    verificationCodeModel.setVerificationCode(verificationCode);
                                }
                                verificationCodeModel.save();
                            } else if (resultBean.getCode() == CWConstant.ERROR) {
                                ResultBean resultModel = mGson.fromJson(resultBean.getResultBean(),
                                        ResultBean.class);
                                XToastUtils.toast(resultModel.getMsg());
                            } else
                                RequestToastUtils.toast(resultBean.getCode());
                        }
                        break;
                    case TConstant.REQUEST_VERIFY_VERIFICATION_CODE:
                        try {
                            AAABaseResponseBean response = (AAABaseResponseBean) msg.obj;
                            KLog.d(mGson.toJson("verifyVerificationCode: " + response));
                            if (response.getCode() == TConstant.RESPONSE_SUCCESS) {
                                Bundle bundle = new Bundle();
                                bundle.putString("verificationCode", mEtVerificationCode.getText().toString().trim());
                                bundle.putString(TConstant.USERNAME, username);
                                bundle.putInt(TConstant.TYPE, type);
                                if (type == 1 || type == 2)
                                    openNewPage(SetPwdFragment.class, bundle);
                                else
                                    openPage(ResetPwdFragment.class, bundle);
                            } else {
                                showMessage(ErrorCode.getResId(response.getCode()));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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

    @Override
    public void onDestroy() {
        mCountDownHelper.cancel();
        super.onDestroy();
    }
}
