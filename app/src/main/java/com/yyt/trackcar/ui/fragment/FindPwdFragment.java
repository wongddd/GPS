package com.yyt.trackcar.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.alpha.XUIAlphaTextView;
import com.xuexiang.xui.widget.dialog.bottomsheet.BottomSheet;
import com.xuexiang.xutil.common.RegexUtils;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAABaseResponseBean;
import com.yyt.trackcar.bean.AAARequestBean;
import com.yyt.trackcar.bean.RequestBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.bean.ResultBean;
import com.yyt.trackcar.dbflow.VerificationCodeModel;
import com.yyt.trackcar.dbflow.VerificationCodeModel_Table;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CWRequestUtils;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.DialogUtils;
import com.yyt.trackcar.utils.ErrorCode;
import com.yyt.trackcar.utils.RequestToastUtils;
import com.yyt.trackcar.utils.SettingSPUtils;
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
 * @ fileName:      FindPwdFragment
 * @ author:        QING
 * @ createTime:    2020-03-01 18:38
 * @ describe:      TODO 找回密码
 */
@Page(name = "FindPwd")
public class FindPwdFragment extends BaseFragment {
    @BindView(R.id.tvTitlePrompt)
    TextView mTvTitlePrompt; // 标题提示文本
    @BindView(R.id.clCountry)
    View mCountryView; // 选中国家布局
    @BindView(R.id.tvSelectCountry)
    TextView mTvSelectCountry; // 选中国家
    @BindView(R.id.tvInput)
    TextView mTvInput; // 输入文本
    @BindView(R.id.etInput)
    EditText mEtInput; // 输入文本编辑
    @BindView(R.id.confirmBtn)
    Button mConfirmBtn; // 确认按钮
    @BindView(R.id.tvSwitchType)
    XUIAlphaTextView mTvSwitchType; // 切换登陆方式
    /**
     *  找回类型标记 1.邮箱 2.手机
     */
    @AutoWired
    int type;
    private String mCountryCode; // 国家编码
    private BottomSheet mBottomSheet; // 选项弹窗
    private boolean isNext; // 是否下一步

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_find_pwd;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle("");
        return titleBar;
    }

    @Override
    protected void initViews() {
        XRouter.getInstance().inject(this); // 参数注入@AutoWried
        switchFindType();
        mCountryCode = SettingSPUtils.getInstance().getString(CWConstant.COUNTRY_CODE, "");
        if (TextUtils.isEmpty(mCountryCode))
            mCountryCode = getString(R.string.china_mobile_code);
        else
            mCountryCode = "+" + mCountryCode;
//        mEtInput.setText(SettingSPUtils.getInstance().getString(CWConstant.USERNAME,
//                ""));
        if (type == 2) { // 手机找回时填入国家编码
            String[] countryArray;
            if ("CN".equals(Locale.getDefault().getCountry()))
                countryArray = getResources().getStringArray(R.array.country_code_list_ch);
            else
                countryArray = getResources().getStringArray(R.array.country_code_list_tw);
            for (String s : countryArray) {
                String[] country = s.split("\\*");
                String countryName = country[0];
                String countryNumber = country[1];
                if (countryNumber.equals(mCountryCode)) {
                    if (type == 2)
                        mTvInput.setText(mCountryCode);
                    mTvSelectCountry.setText(countryName);
                    break;
                }
            }
            if ("zh".equals(Locale.getDefault().getLanguage())) {
                if ("CN".equals(Locale.getDefault().getCountry())) {
                    mTvSelectCountry.setText(R.string.china);
                    mTvInput.setText(R.string.china_mobile_code);
                } else {
                    mTvSelectCountry.setText(R.string.taiwan);
                    mTvInput.setText(R.string.taiwan_mobile_code);
                }
            } else {
                mTvSelectCountry.setText(R.string.taiwan);
                mTvInput.setText(R.string.taiwan_mobile_code);
            }
        }
    }

    /**
     * 切换找回方式
     */
    private void switchFindType() {
        if (type == 1) { // 邮箱找回
            mEtInput.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            mEtInput.setHint(R.string.email_hint);
            mTvInput.setText(R.string.email);
            mEtInput.setText("");
            mCountryView.setVisibility(View.INVISIBLE);
            mTvSwitchType.setText(R.string.find_pwd_mobile);
            mTvTitlePrompt.setText(R.string.find_pwd_email_prompt);
        } else { // 手机找回
            mEtInput.setInputType(InputType.TYPE_CLASS_NUMBER);
            mEtInput.setHint(R.string.mobile_hint);
            mTvInput.setText(mCountryCode);
            mEtInput.setText("");
            mCountryView.setVisibility(View.VISIBLE);
            mTvSwitchType.setText(R.string.find_pwd_email);
            mTvTitlePrompt.setText(R.string.find_pwd_mobile_prompt);
        }
    }

    /**
     * 发送验证码
     */
    private void sendVerificationCode(RequestBean requestBean) {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork();
            return;
        }
        if ("4".equals(requestBean.getType()))
            CWRequestUtils.getInstance().findPwdAuthCode(mActivity, requestBean.getCountry(),
                    requestBean.getUsername(), mHandler);
        else
            CWRequestUtils.getInstance().findPwdMailCode(mActivity, requestBean.getUsername(),
                    mHandler);
    }

    /**
     * 显示提示对话框
     */
    private void showConfirmDialog(int type, String countryCode, String username) {
        if (mMaterialDialog == null || !mMaterialDialog.isShowing()) {
            String title;
            String content;
            if (type == 2) {
                title = getString(R.string.find_pwd_prompt_mobile_title);
                content = getString(R.string.find_pwd_prompt_mobile_content, username);
            } else {
                title = getString(R.string.find_pwd_prompt_email_title);
                content = getString(R.string.find_pwd_prompt_email_content, username);
            }
            RequestBean requestBean = new RequestBean();
            requestBean.setUsername(username);
            requestBean.setType(String.valueOf(type));
            requestBean.setCountry(countryCode);
            mMaterialDialog = DialogUtils.customMaterialDialog(getContext(), mMaterialDialog,
                    title, content, getString(R.string.confirm), getString(R.string.cancel),
                    requestBean, CWConstant.DIALOG_SEND_CODE, mHandler);
        }
    }

    @SingleClick
    @OnClick({R.id.confirmBtn, R.id.tvSwitchType, R.id.clCountry})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clCountry: // 选择国家/地区
                if (type == 2)
                    openNewPageForResult(CountryCodeSelectFragment.class,
                            CWConstant.REQUEST_COUNTRY_CODE);
                break;
            case R.id.confirmBtn: // 下一步按钮
                String username = mEtInput.getText().toString().trim();
                if (TextUtils.isEmpty(username))
                    XToastUtils.toast(mEtInput.getHint().toString());
//                else if (type == 1 && !RegexUtils.isEmail(username))
//                    XToastUtils.toast(R.string.input_true_email_prompt);
//                else if (type == 2 && !RegexUtils.isMobileSimple(username))
//                    XToastUtils.toast(R.string.input_true_mobile_prompt);
                else if (mBottomSheet == null || !mBottomSheet.isShowing()) {
                    showConfirmDialog(type,
                            mTvInput.getText().toString().substring(1),
                            username);
                }
                break;
            case R.id.tvSwitchType: // 切换找回方式
                if (type == 2) // 切换邮箱找回
                    type = 1;
                else // 切换手机找回
                    type = 2;
                switchFindType();
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
            if (bundle != null && type == 2) {
                mCountryCode = data.getStringExtra(CWConstant.COUNTRY_CODE);
                mTvSelectCountry.setText(data.getStringExtra(CWConstant.COUNTRY_NAME));
                mTvInput.setText(mCountryCode);
            }
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
                    case CWConstant.HANDLE_CONFIRM_ACTION: // 确认
                        switch (msg.arg1) {
                            case CWConstant.DIALOG_SEND_CODE: // 发送验证码
//                                requestBean = (RequestBean) msg.obj;
//                                VerificationCodeModel verificationCodeModel =
//                                        SQLite.select().from(VerificationCodeModel.class)
//                                                .orderBy(VerificationCodeModel_Table.createtime,
//                                                        false)
//                                                .querySingle();
//                                if (verificationCodeModel != null && !requestBean.getUsername().equals(verificationCodeModel.getUsername()) && System.currentTimeMillis() - verificationCodeModel.getCreatetime() < TimeUtils.MINUTE * 1000) {
//                                    isNext = true;
//                                    bundle = new Bundle();
//                                    bundle.putString(CWConstant.USERNAME,
//                                            requestBean.getUsername());
//                                    bundle.putString(CWConstant.VERIFICATION_CODE, "");
//                                    if ("4".equals(requestBean.getType()))
//                                        bundle.putInt(CWConstant.TYPE, 4);
//                                    else
//                                        bundle.putInt(CWConstant.TYPE, 3);
//                                    bundle.putString(CWConstant.COUNTRY_CODE,
//                                            requestBean.getCountry());
//                                    bundle.putLong(CWConstant.TIME, 888);
//                                    openNewPage(VerificationCodeFragment.class, bundle);
//                                    return false;
//                                }
//                                if (verificationCodeModel != null && !requestBean.getUsername().equals(verificationCodeModel.getUsername()))
//                                    verificationCodeModel =
//                                            SQLite.select().from(VerificationCodeModel.class)
//                                            .where(VerificationCodeModel_Table.username.eq(requestBean.getUsername()))
//                                            .querySingle();
//                                if (verificationCodeModel == null)
//                                    sendVerificationCode(requestBean);
//                                else {
//                                    long time = System.currentTimeMillis();
//                                    long createtime = verificationCodeModel.getCreatetime();
//                                    String date = TimeUtils.formatUTC(time, "yyyy-MM-dd");
//                                    String createDate = TimeUtils.formatUTC(createtime, "yyyy-MM" +
//                                            "-dd");
//                                    if ((time - createtime < TimeUtils.HOUR * 1000 && verificationCodeModel.getCount() >= 5)
//                                            || (date.equals(createDate) && verificationCodeModel.getCount() >= 10)) {
//                                        isNext = true;
//                                        bundle = new Bundle();
//                                        bundle.putString(CWConstant.USERNAME,
//                                                requestBean.getUsername());
//                                        bundle.putString(CWConstant.VERIFICATION_CODE,
//                                                verificationCodeModel.getVerificationCode());
//                                        if ("4".equals(requestBean.getType()))
//                                            bundle.putInt(CWConstant.TYPE, 4);
//                                        else
//                                            bundle.putInt(CWConstant.TYPE, 3);
//                                        bundle.putString(CWConstant.COUNTRY_CODE,
//                                                requestBean.getCountry());
//                                        bundle.putLong(CWConstant.TIME, 888);
//                                        openNewPage(VerificationCodeFragment.class, bundle);
//                                    } else if (time - createtime < TimeUtils.MINUTE * 1000) {
//                                        isNext = true;
//                                        bundle = new Bundle();
//                                        bundle.putString(CWConstant.USERNAME,
//                                                requestBean.getUsername());
//                                        bundle.putString(CWConstant.VERIFICATION_CODE,
//                                                verificationCodeModel.getVerificationCode());
//                                        if ("4".equals(requestBean.getType()))
//                                            bundle.putInt(CWConstant.TYPE, 4);
//                                        else
//                                            bundle.putInt(CWConstant.TYPE, 3);
//                                        bundle.putString(CWConstant.COUNTRY_CODE,
//                                                requestBean.getCountry());
//                                        bundle.putLong(CWConstant.TIME,
//                                                Math.max(60 - (time - createtime) / 1000,
//                                                        1));
//                                        openNewPage(VerificationCodeFragment.class, bundle);
//                                    } else
//                                        sendVerificationCode(requestBean);
//                                }
                                showDialog();
                                if (type == 1)
                                    CarGpsRequestUtils.getVerifyCode(null, mEtInput.getText().toString().trim()
                                            , type, 1, null, mHandler);
                                else
                                    CarGpsRequestUtils.getVerifyCode(mCountryCode, mEtInput.getText().toString().trim()
                                            , type, 1, null, mHandler);
                                break;
                            default:
                                break;
                        }
                        break;
                    case TConstant.REQUEST_GET_VERIFICATION_CODE:
                        dismisDialog();
                        if (msg.obj != null) {
                            try {
                                AAABaseResponseBean response = (AAABaseResponseBean) msg.obj;
                                AAARequestBean request;
                                if (response.getCode() == TConstant.RESPONSE_SUCCESS) {
                                    request = mGson.fromJson(response.getRequestObject(),
                                            AAARequestBean.class);
                                    bundle = new Bundle();
                                    bundle.putString(CWConstant.COUNTRY_CODE, "");
                                    bundle.putLong(CWConstant.TIME, 60);
                                    bundle.putInt(CWConstant.TYPE, type + 2);
                                    bundle.putString(CWConstant.USERNAME, mEtInput.getText().toString().trim());
                                    openNewPage(VerificationCodeFragment.class, bundle);
                                } else {
                                    showMessage(ErrorCode.getResId(response.getCode()));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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
    public void onResume() {
        super.onResume();
        isNext = false;
    }

    @Override
    public void onDestroy() {
        DialogUtils.dismiss(mBottomSheet);
        super.onDestroy();
    }
}
