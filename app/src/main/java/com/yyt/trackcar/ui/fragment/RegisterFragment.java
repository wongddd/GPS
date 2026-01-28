package com.yyt.trackcar.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xutil.common.RegexUtils;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAABaseResponseBean;
import com.yyt.trackcar.bean.RequestBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.bean.ResultBean;
import com.yyt.trackcar.dbflow.VerificationCodeModel;
import com.yyt.trackcar.dbflow.VerificationCodeModel_Table;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.ui.widget.textview.QMUISpanTouchFixTextView;
import com.yyt.trackcar.ui.widget.textview.QMUITouchableSpan;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.ErrorCode;
import com.yyt.trackcar.utils.RequestToastUtils;
import com.yyt.trackcar.utils.TConstant;
import com.yyt.trackcar.utils.TimeUtils;
import com.yyt.trackcar.utils.XToastUtils;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      RegisterFragment
 * @ author:        QING
 * @ createTime:    2020-02-26 11:45
 * @ describe:      TODO 注册页面
 */
@Page(name = "Register", params = {CWConstant.TYPE})
@SuppressLint("NonConstantResourceId")
public class RegisterFragment extends BaseFragment {
    @BindView(R.id.clCountry)
    View mCountryView; // 选中国家布局
    @BindView(R.id.tvRegister)
    TextView mTvRegister; // 标题
    @BindView(R.id.tvRegisterPrompt)
    TextView mTvRegisterPrompt; // 提示信息
    @BindView(R.id.tvSelectCountry)
    TextView mTvSelectCountry; // 选中国家
    @BindView(R.id.tvCountryCode)
    TextView mTvCountryCode; // 国家/区号
    @BindView(R.id.etUsername)
    EditText mEtUsername; // 手机号/邮箱文本编辑
    @BindView(R.id.cbProtocol)
    CheckBox mCbProtocol; // 同意相关政策选择
    @BindView(R.id.cbJoin)
    CheckBox mCbJoin; // 加入用户体验计划选择
    @BindView(R.id.tvRegisterProtocol)
    QMUISpanTouchFixTextView mTVRegisterProtocol;
    @BindView(R.id.registerBtn)
    Button mRegisterBtn; // 注册按钮
    @BindView(R.id.clExperience)
    View mExperienceView;
    @BindView(R.id.clExperienceSecond)
    View mExperienceSecondView;
    @BindView(R.id.tvExperienceSecond)
    QMUISpanTouchFixTextView mTVExperienceSecond;
    /**
     * 注册类型 1邮箱 2手机
     */
    @AutoWired
    int type;
    private boolean isNext; // 是否下一步

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_register;
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
        if (type == 1) { // 邮箱注册
            mEtUsername.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            mEtUsername.setHint(R.string.email_hint);
            mTvCountryCode.setText(R.string.email);
            mEtUsername.setText("");
            mCountryView.setVisibility(View.INVISIBLE);
            mTvRegister.setText(R.string.register_email);
            mTvRegisterPrompt.setVisibility(View.INVISIBLE);
        } else {  //如果是海外版本默认设置为台湾，中国大陆设置为中国
            if ("zh".equals(Locale.getDefault().getLanguage())){
                if ("CN".equals(Locale.getDefault().getCountry())) {
                    mTvSelectCountry.setText(R.string.china);
                    mTvCountryCode.setText(R.string.china_mobile_code);
                }else{
                    mTvSelectCountry.setText(R.string.taiwan);
                    mTvCountryCode.setText(R.string.taiwan_mobile_code);
                }
            }else{
                mTvSelectCountry.setText(R.string.taiwan);
                mTvCountryCode.setText(R.string.taiwan_mobile_code);
            }
        }



//        String language = SettingSPUtils.getInstance().getString(CWConstant.LANGUAGE, "");
//        if ("de".equals(language)) {
//            mExperienceView.setVisibility(View.GONE);
//            mExperienceSecondView.setVisibility(View.VISIBLE);
//            mTVExperienceSecond.setMovementMethodDefault();
//            String text = String.format("%s %s", getString(R.string.jion),
//                    getString(R.string.user_experience));
//            String highlight1 = getString(R.string.user_experience);
//            SpannableString sp = new SpannableString(text);
//            int start = 0, end;
//            int index;
//            while ((index = text.indexOf(highlight1, start)) > -1) {
//                end = index + highlight1.length();
//                sp.setSpan(new QMUITouchableSpan(ContextCompat.getColor(mActivity,
//                        R.color.colorTexNormal), ContextCompat.getColor(mActivity,
//                        R.color.colorTextPressed),
//                        ContextCompat.getColor(mActivity, R.color.transparent),
//                        ContextCompat.getColor(mActivity, R.color.transparent)) {
//                    @Override
//                    public void onSpanClick(View widget) {
//                        Bundle bundle = new Bundle();
//                        bundle.putString(CWConstant.TITLE, getString(R.string.about_user_experience));
//                        bundle.putString(CWConstant.URL, CWConstant.PROTOCOL_URL);
//                        openNewPage(WebFragment.class, bundle);
//                    }
//                }, index, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
//                start = end;
//            }
//            mTVExperienceSecond.setText(sp);
//        } else {
//            mExperienceView.setVisibility(View.VISIBLE);
//            mExperienceSecondView.setVisibility(View.GONE);
//        }
        mTVRegisterProtocol.setMovementMethodDefault();
        String text = getString(R.string.register_protocol);
        String highlight1 = getString(R.string.user_service_agreement);
        String highlight2 = getString(R.string.privacy_protocol);
        SpannableString sp = new SpannableString(text);
        int start = 0, end;
        int index;
        while ((index = text.indexOf(highlight1, start)) > -1) {
            end = index + highlight1.length();
            sp.setSpan(new QMUITouchableSpan(ContextCompat.getColor(mActivity,
                    R.color.colorTexNormal), ContextCompat.getColor(mActivity,
                    R.color.colorTextPressed),
                    ContextCompat.getColor(mActivity, R.color.transparent),
                    ContextCompat.getColor(mActivity, R.color.transparent)) {
                @Override
                public void onSpanClick(View widget) {
                    Bundle bundle = new Bundle();
                    bundle.putString(CWConstant.TITLE, getString(R.string.about_user_service));
                    bundle.putString(CWConstant.URL, CWConstant.USER_SERVICE_AGREEMENT_URL);
                    openNewPage(WebFragment.class, bundle);
                }
            }, index, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            start = end;
        }

        start = 0;
        while ((index = text.indexOf(highlight2, start)) > -1) {
            end = index + highlight2.length();
            sp.setSpan(new QMUITouchableSpan(ContextCompat.getColor(mActivity,
                    R.color.colorTexNormal), ContextCompat.getColor(mActivity,
                    R.color.colorTextPressed),
                    ContextCompat.getColor(mActivity, R.color.transparent),
                    ContextCompat.getColor(mActivity, R.color.transparent)) {
                @Override
                public void onSpanClick(View widget) {
                    Bundle bundle = new Bundle();
                    bundle.putString(CWConstant.TITLE, getString(R.string.about_privacy_protocol));
                    bundle.putString(CWConstant.URL, CWConstant.PROTOCOL_URL);
                    openNewPage(WebFragment.class, bundle);
                }
            }, index, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            start = end;
        }
        mTVRegisterProtocol.setText(sp);
    }

    /**
     * 发送验证码
     */
    private void sendVerificationCode() {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork();
            return;
        }
        String username = mEtUsername.getText().toString().trim();
//        if (type == 1)
//            CWRequestUtils.getInstance().getMailCode(mActivity, username, mHandler);
//        else
//            CWRequestUtils.getInstance().getAuthCode(mActivity,
//                    mTvCountryCode.getText().toString().substring(1), username, mHandler);
        if (type == 1)
            CarGpsRequestUtils.getVerifyCode(null, username, type, 0, null, mHandler);
        else {
            String countryCode = mTvCountryCode.getText().toString().substring(1);
            username = checkTaiWanMobilePhoneNumber(username,countryCode);
            CarGpsRequestUtils.getVerifyCode(mTvCountryCode.getText().toString().trim(), username, 0, 0, null, mHandler);
        }

        showDialog();
    }

    //台湾电话号码检测 号码九位数的时候前置位加0
    private String checkTaiWanMobilePhoneNumber(String cal, String countryCode) {
        if (countryCode.equals("886") && cal.length() == 9) {
            StringBuilder stringBuilder = new StringBuilder(cal);
            stringBuilder.insert(0, "0");
            cal = stringBuilder.toString();
        }
        return cal;
    }

    @SingleClick
    @OnClick({R.id.clCountry, R.id.registerBtn, R.id.tvAgreement, R.id.tvProtocol,
            R.id.tvExperience})
    public void onClick(View v) {
        Bundle bundle;
        switch (v.getId()) {
            case R.id.clCountry: // 选择国家/地区
                openNewPageForResult(CountryCodeSelectFragment.class,
                        CWConstant.REQUEST_COUNTRY_CODE);
                break;
            case R.id.registerBtn: // 注册
                String username = mEtUsername.getText().toString().trim();
                if (TextUtils.isEmpty(username)) {
                    XToastUtils.toast(mEtUsername.getHint().toString());
                    mEtUsername.requestFocus();
                } else if (type == 1 && !RegexUtils.isEmail(username)) {
                    XToastUtils.toast(R.string.input_true_email_prompt);
                    mEtUsername.requestFocus();
                } else if (type == 2 && username.length() < 9
//                        && !RegexUtils.isMobileSimple(username)
                ) {
                    XToastUtils.toast(R.string.input_true_mobile_prompt);
                    mEtUsername.requestFocus();
                } else if (mCbProtocol.isChecked()) {
                    if (!isNext) {
                        mRegisterBtn.setEnabled(false);
                        VerificationCodeModel verificationCodeModel =
                                SQLite.select().from(VerificationCodeModel.class)
                                        .orderBy(VerificationCodeModel_Table.createtime, false)
                                        .querySingle();
                        if (verificationCodeModel != null && !username.equals(verificationCodeModel.getUsername()) && System.currentTimeMillis() - verificationCodeModel.getCreatetime() < TimeUtils.MINUTE * 1000) {
                            isNext = true;
                            if (type == 2)
                                username = checkTaiWanMobilePhoneNumber(username,mTvCountryCode.getText().toString().trim().substring(1));
                            bundle = new Bundle();
                            bundle.putString(CWConstant.USERNAME, username);
                            bundle.putString(CWConstant.VERIFICATION_CODE, "");
                            bundle.putInt(CWConstant.TYPE, type);
                            if (type == 2)
                                bundle.putString(CWConstant.COUNTRY_CODE,
                                        mTvCountryCode.getText().toString().substring(1));
                            else
                                bundle.putString(CWConstant.COUNTRY_CODE, "");
                            bundle.putLong(CWConstant.TIME, 888);
                            openNewPage(VerificationCodeFragment.class, bundle);
                            mRegisterBtn.setEnabled(true);
                            return;
                        }
                        if (verificationCodeModel != null && !username.equals(verificationCodeModel.getUsername()))
                            verificationCodeModel =
                                    SQLite.select().from(VerificationCodeModel.class)
                                            .where(VerificationCodeModel_Table.username.eq(username))
                                            .querySingle();
                        if (verificationCodeModel == null)
                            sendVerificationCode();
                        else {
                            long time = System.currentTimeMillis();
                            long createtime = verificationCodeModel.getCreatetime();
                            String date = TimeUtils.formatUTC(time, "yyyy-MM-dd");
                            String createDate = TimeUtils.formatUTC(createtime, "yyyy-MM-dd");
                            if ((time - createtime < TimeUtils.HOUR * 1000 && verificationCodeModel.getCount() >= 5)
                                    || (date.equals(createDate) && verificationCodeModel.getCount() >= 10)) {
                                isNext = true;
                                bundle = new Bundle();
                                bundle.putString(CWConstant.USERNAME, username);
                                bundle.putString(CWConstant.VERIFICATION_CODE,
                                        verificationCodeModel.getVerificationCode());
                                bundle.putInt(CWConstant.TYPE, type + 2);
                                if (type == 2)
                                    bundle.putString(CWConstant.COUNTRY_CODE,
                                            mTvCountryCode.getText().toString().substring(1));
                                else
                                    bundle.putString(CWConstant.COUNTRY_CODE, "");
                                bundle.putLong(CWConstant.TIME, 888);
                                openNewPage(VerificationCodeFragment.class, bundle);
                                mRegisterBtn.setEnabled(true);
                            } else if (time - createtime < TimeUtils.MINUTE * 1000) {
                                isNext = true;
                                bundle = new Bundle();
                                bundle.putString(CWConstant.USERNAME, username);
                                bundle.putString(CWConstant.VERIFICATION_CODE,
                                        verificationCodeModel.getVerificationCode());
                                bundle.putInt(CWConstant.TYPE, type + 2);
                                if (type == 2)
                                    bundle.putString(CWConstant.COUNTRY_CODE,
                                            mTvCountryCode.getText().toString().substring(1));
                                else
                                    bundle.putString(CWConstant.COUNTRY_CODE, "");
                                bundle.putLong(CWConstant.TIME,
                                        Math.max(60 - (time - createtime) / 1000, 1));
                                openNewPage(VerificationCodeFragment.class, bundle);
                                mRegisterBtn.setEnabled(true);
                            } else
                                sendVerificationCode();
                        }
                    }
                } else
                    XToastUtils.toast(R.string.read_protocol_prompt);
                break;
            case R.id.tvAgreement: // 用户服务协议
                bundle = new Bundle();
                bundle.putString(CWConstant.TITLE, getString(R.string.about_user_service));
                bundle.putString(CWConstant.URL, CWConstant.USER_SERVICE_AGREEMENT_URL);
                openNewPage(WebFragment.class, bundle);
                break;
            case R.id.tvProtocol: // 隐私保护政策
                bundle = new Bundle();
                bundle.putString(CWConstant.TITLE, getString(R.string.about_privacy_protocol));
                bundle.putString(CWConstant.URL, CWConstant.PROTOCOL_URL);
                openNewPage(WebFragment.class, bundle);
                break;
            case R.id.tvExperience: // 用户体验改进计划
                bundle = new Bundle();
                bundle.putString(CWConstant.TITLE, getString(R.string.about_user_experience));
                bundle.putString(CWConstant.URL, CWConstant.PROTOCOL_URL);
                openNewPage(WebFragment.class, bundle);
                break;
            default:
                break;
        }
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Intent data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == CWConstant.REQUEST_COUNTRY_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                mTvSelectCountry.setText(data.getStringExtra(CWConstant.COUNTRY_NAME));
                mTvCountryCode.setText(data.getStringExtra(CWConstant.COUNTRY_CODE));
            }
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
                Bundle bundle;
                switch (msg.what) {
                    case CWConstant.REQUEST_URL_GET_MAIL_CODE: // 获取邮箱验证码
                        mRegisterBtn.setEnabled(true);
                        if (isNext)
                            return false;
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                isNext = true;
                                ResultBean resultModel =
                                        mGson.fromJson(mGson.toJson(resultBean.getResultBean()),
                                                ResultBean.class);
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                VerificationCodeModel verificationCodeModel =
                                        SQLite.select().from(VerificationCodeModel.class)
                                                .where(VerificationCodeModel_Table.username.eq(requestBean.getMail()))
                                                .querySingle();
                                if (verificationCodeModel == null) {
                                    verificationCodeModel = new VerificationCodeModel();
                                    verificationCodeModel.setUsername(requestBean.getMail());
                                    verificationCodeModel.setCreatetime(System.currentTimeMillis());
                                    verificationCodeModel.setCount(1);
                                    verificationCodeModel.setVerificationCode(resultModel.getVerificationCode());
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
                                    verificationCodeModel.setVerificationCode(resultModel.getVerificationCode());
                                }
                                verificationCodeModel.save();
                                bundle = new Bundle();
                                bundle.putInt(CWConstant.TYPE, 1);
                                bundle.putString(CWConstant.USERNAME, requestBean.getMail());
                                bundle.putString(CWConstant.VERIFICATION_CODE,
                                        resultModel.getVerificationCode());
                                bundle.putString(CWConstant.COUNTRY_CODE, requestBean.getCountry());
                                bundle.putLong(CWConstant.TIME, 60);
                                openNewPage(VerificationCodeFragment.class, bundle);
                            } else if (resultBean.getCode() == CWConstant.ERROR) {
                                ResultBean resultModel =
                                        mGson.fromJson(mGson.toJson(resultBean.getResultBean()),
                                                ResultBean.class);
                                XToastUtils.toast(resultModel.getMsg());
                            } else
                                RequestToastUtils.toast(resultBean.getCode());
                        }
                        break;
                    case CWConstant.REQUEST_URL_GET_AUTH_CODE: // 获取短信验证码
                        mRegisterBtn.setEnabled(true);
                        if (isNext)
                            return false;
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                isNext = true;
                                ResultBean resultModel =
                                        mGson.fromJson(mGson.toJson(resultBean.getResultBean()),
                                                ResultBean.class);
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                VerificationCodeModel verificationCodeModel =
                                        SQLite.select().from(VerificationCodeModel.class)
                                                .where(VerificationCodeModel_Table.username.eq(requestBean.getMail()))
                                                .querySingle();
                                if (verificationCodeModel == null) {
                                    verificationCodeModel = new VerificationCodeModel();
                                    verificationCodeModel.setUsername(requestBean.getUsername());
                                    verificationCodeModel.setCreatetime(System.currentTimeMillis());
                                    verificationCodeModel.setCount(1);
                                    verificationCodeModel.setVerificationCode(resultModel.getVerificationCode());
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
                                    verificationCodeModel.setVerificationCode(resultModel.getVerificationCode());
                                }
                                verificationCodeModel.save();
                                bundle = new Bundle();
                                bundle.putInt(CWConstant.TYPE, 2);
                                bundle.putString(CWConstant.USERNAME, requestBean.getUsername());
                                bundle.putString(CWConstant.VERIFICATION_CODE,
                                        resultModel.getVerificationCode());
                                bundle.putString(CWConstant.COUNTRY_CODE, "");
                                bundle.putLong(CWConstant.TIME, 60);
                                openNewPage(VerificationCodeFragment.class, bundle);
                            } else if (resultBean.getCode() == CWConstant.ERROR) {
                                ResultBean resultModel = mGson.fromJson(resultBean.getResultBean(),
                                        ResultBean.class);
                                XToastUtils.toast(resultModel.getMsg());
                            } else
                                RequestToastUtils.toast(resultBean.getCode());
                        }
                        break;
                    case TConstant.REQUEST_GET_VERIFICATION_CODE:
                        dismisDialog();
                        mRegisterBtn.setEnabled(true);
                        if (isNext)
                            return false;
                        if (msg.obj == null) {
                            showMessage(R.string.request_error_prompt);
                        } else {
                            try {
                                AAABaseResponseBean responseBean = (AAABaseResponseBean) msg.obj;
//                                KLog.d("sendRegisterVerifyCode: " + mGson.toJson(responseBean));
                                if (responseBean.getCode() == TConstant.RESPONSE_SUCCESS) {
                                    isNext = true;
                                    String username = null;
                                    if (type == 2){
                                        username = checkTaiWanMobilePhoneNumber(mEtUsername.getText().toString().trim(),mTvCountryCode.getText().toString().trim().substring(1));
                                    }
                                    bundle = new Bundle();
                                    bundle.putInt(CWConstant.TYPE, type);
                                    bundle.putString(CWConstant.USERNAME, username);
//                                bundle.putString(CWConstant.USERNAME, responseBean.getRequestObject().get);
//                                bundle.putString(CWConstant.VERIFICATION_CODE,
//                                        resultModel.getVerificationCode());
//                                bundle.putString(CWConstant.COUNTRY_CODE, requestBean.getCountry());
                                    bundle.putLong(CWConstant.TIME, 60);
                                    openNewPage(VerificationCodeFragment.class, bundle);
                                } else {
                                    showMessage(ErrorCode.getResId(responseBean.getCode()));
                                }
                            } catch (Exception e) {
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

    @Override
    public void onResume() {
        super.onResume();
        isNext = false;
    }

}
