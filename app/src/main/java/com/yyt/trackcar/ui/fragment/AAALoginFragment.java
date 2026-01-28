package com.yyt.trackcar.ui.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.actionsheet.ActionSheet;
import com.google.gson.reflect.TypeToken;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.alpha.XUIAlphaTextView;
import com.xuexiang.xutil.app.ActivityUtils;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.MainApplication;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAABaseResponseBean;
import com.yyt.trackcar.bean.AAARequestBean;
import com.yyt.trackcar.bean.PostMessage;
import com.yyt.trackcar.dbflow.AAADeviceModel;
import com.yyt.trackcar.dbflow.AAAUserModel;
import com.yyt.trackcar.dbflow.AAAUserModel_Table;
import com.yyt.trackcar.ui.activity.BindDeviceActivity;
import com.yyt.trackcar.ui.activity.MainActivity;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.DataUtils;
import com.yyt.trackcar.utils.ErrorCode;
import com.yyt.trackcar.utils.RequestToastUtils;
import com.yyt.trackcar.utils.SettingSPUtils;
import com.yyt.trackcar.utils.TConstant;
import com.yyt.trackcar.utils.XToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
@Page(name = "AAALogin", anim = CoreAnim.none)
public class AAALoginFragment extends BaseFragment implements ActionSheet.ActionSheetListener {
    //    @BindView(R.id.clCountry)
//    View mCountryView; // 选中国家布局
//    @BindView(R.id.tvSelectCountry)
//    TextView mTvSelectCountry; // 选中国家
    @BindView(R.id.tvCountryCode)
    TextView mTvCountryCode; // 国家/区号
    @BindView(R.id.etUsername)
    EditText mEtUsername; // 手机号/邮箱文本编辑
    @BindView(R.id.etPwd)
    EditText mEtPwd; // 密码文本编辑
    @BindView(R.id.tvSwitchType)
    XUIAlphaTextView mTvSwitchType; // 切换登陆方式
    @BindView(R.id.tvSelectLanguage)
    TextView mTvSelectLanguage; // 语言
    @BindView(R.id.tvShownServer)
    TextView mTvShownServer; // 服务器
    @BindView(R.id.tvLogin)
    TextView mTvTitle;
    @BindView(R.id.tvPwd)
    TextView mTvPwd;
    @BindView(R.id.tvLanguage)
    TextView mTvLanguage;
    @BindView(R.id.tvServer)
    TextView mTvServer;
    @BindView(R.id.loginBtn)
    Button mLoginBtn;
    @BindView(R.id.tvForgotPwd)
    XUIAlphaTextView mTvForgotPwd;

    @BindView(R.id.tvMapType)
    TextView tvMapType;

    /**
     * 登陆类型 1帐号 2设备号
     */
    private int mLoginType = 1;
    private String mCountryCode; // 国家编码
    private boolean mIsLogin; // 是否已经登陆
    private boolean mIsLogining; // 是否正在已经登陆
    /**
     * 显示地图类型 0、高德  1、谷歌
     */
    private int mapType = 1;  //显示地图类型 0.高德 1.谷歌

    private int actionType = 0;  // 1.选择地图 2.注册方式 3.找回密码方式
    private String mUsername;
    private String mPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 注册订阅者
        EventBus.getDefault().register(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_login_new;
    }

    @Override
    protected TitleBar initTitle() {
        return null;
    }

    @Override
    protected void initViews() {
//        SettingSPUtils.getInstance().putInt(CWConstant.DEVICE_TYPE, 0);
        mLoginType = SettingSPUtils.getInstance().getInt(CWConstant.LOGIN_TYPE, 0);
        switchLoginType();
        mEtUsername.setText(SettingSPUtils.getInstance().getString(CWConstant.USERNAME_LOGIN,
                ""));
        mEtPwd.setText(SettingSPUtils.getInstance().getString(CWConstant.PASSWORD_LOGIN,
                ""));
        String language = SettingSPUtils.getInstance().getString(CWConstant.LANGUAGE, "");
        if (TextUtils.isEmpty(language)) {
            if ("zh".equals(Locale.getDefault().getLanguage())) {
                if ("CN".equals(Locale.getDefault().getCountry()))
                    language = "zh";
                else
                    language = "tw";
            } else if ("id".equals(Locale.getDefault().getLanguage()))
                language = "in";
            else
                language = Locale.getDefault().getLanguage();
        }
        if ("zh".equals(language))
            mTvSelectLanguage.setText(R.string.language_type_first);
        else if ("tw".equals(language))
            mTvSelectLanguage.setText(R.string.language_type_sixth);
        else if ("in".equals(language))
            mTvSelectLanguage.setText(R.string.language_type_third);
        else if ("pt".equals(language))
            mTvSelectLanguage.setText(R.string.language_type_fourth);
        else if ("es".equals(language))
            mTvSelectLanguage.setText(R.string.language_type_fifth);
        else if ("vi".equals(language))
            mTvSelectLanguage.setText(R.string.language_type_seventh);
        else if ("ar".equals(language))
            mTvSelectLanguage.setText(R.string.language_type_tenth);
        else if ("ru".equals(language))
            mTvSelectLanguage.setText(R.string.language_type_ninth);
        else if ("fr".equals(language))
            mTvSelectLanguage.setText(R.string.language_type_twelfth);
        else if ("ja".equals(language))
            mTvSelectLanguage.setText(R.string.language_type_eighth);
        else if ("de".equals(language))
            mTvSelectLanguage.setText(R.string.language_type_eleventh);
        else
            mTvSelectLanguage.setText(R.string.language_type_second);
//        if (CWConstant.APP_TEST) {
//            int serverType = SettingSPUtils.getInstance().getInt(CWConstant.SERVER_ADDR, 2);
//            if (serverType == 0)
//                mTvSelectServer.setText(R.string.country_cn);
//            else if (serverType == 1)
//                mTvSelectServer.setText(R.string.country_en);
//            else if (serverType == 3)
//                mTvSelectServer.setText(R.string.country_europe);
//            else if (serverType == 4)
//                mTvSelectServer.setText(R.string.country_asia_two);
//            else
//                mTvSelectServer.setText(R.string.country_asia);
//        } else {
//            int serverType = SettingSPUtils.getInstance().getInt(CWConstant.SERVER_ADDR, 0);
//            if (serverType == 1)
//                mTvSelectServer.setText(R.string.country_en);
//            else if (serverType == 3)
//                mTvSelectServer.setText(R.string.country_europe);
//            else if (serverType == 4)
//                mTvSelectServer.setText(R.string.country_asia_two);
//            else
//                mTvSelectServer.setText(R.string.country_cn);
//        }

//        int serverType = SettingSPUtils.getInstance().getInt(CWConstant.SERVER_ADDR, 0);
//        if (serverType == 5)
//            mEtInputServer.setText("abc");
//        else
//            mEtInputServer.setText("123");
        String serverIp = SettingSPUtils.getInstance().getString(TConstant.SERVER_ADDRESS,
                TConstant.DEFAULT_IP);
        if (TextUtils.isEmpty(serverIp) || TConstant.DEFAULT_IP.equals(serverIp)) {
            //如果服务器地址为"trackservice.gps866.com"则显示默认
            mTvShownServer.setText(R.string.word_default);
        } else {
            mTvShownServer.setText(serverIp);
        }
//        mCountryCode = SettingSPUtils.getInstance().getString(CWConstant.COUNTRY_CODE, "");
//        if (TextUtils.isEmpty(mCountryCode)) {  //中国大陆版本设置为中国大陆以及中国大陆国家编码，台湾及海外设置为台湾
//            if (language.equals("zh"))
//                mCountryCode = getString(R.string.china_mobile_code);
//            else
//                mCountryCode = getString(R.string.taiwan_mobile_code);
//        } else
//            mCountryCode = "+" + mCountryCode;

        //国家编码设置
//        String[] countryArray;
//        if ("CN".equals(Locale.getDefault().getCountry()))
//            countryArray = getResources().getStringArray(R.array.country_code_list_ch);
//        else
//            countryArray = getResources().getStringArray(R.array.country_code_list_tw);
//        for (String s : countryArray) {
//            String[] country = s.split("\\*");
//            String countryName = country[0];
//            String countryNumber = country[1];
//            if (countryNumber.equals(mCountryCode)) {
//                if (mLoginType == 2)
//                    mTvCountryCode.setText(mCountryCode);
////                mTvSelectCountry.setText(countryName);
//                break;
//            }
//        }

        //设置默认地图，中国大陆使用高德，台湾及海外使用谷歌
        mapType = SettingSPUtils.getInstance().getInt(TConstant.MAP_TYPE, -1);
        if (mapType == -1) {
            if (language.equals("zh")) {
                if ("CN".equals(Locale.getDefault().getCountry())) {
                    mapType = 0;
                    tvMapType.setText(R.string.gaode_map);
                } else {
                    mapType = 1;
                    tvMapType.setText(R.string.google_map);
                }
            } else {
                mapType = 1;
                tvMapType.setText(R.string.google_map);
            }
        } else if (mapType == 1) {
            tvMapType.setText(R.string.google_map);
        } else {
            tvMapType.setText(R.string.gaode_map);
        }

    }

    @SingleClick
    @OnClick({R.id.tvSwitchType, R.id.loginBtn, R.id.tvForgotPwd,
            R.id.clLanguage, R.id.clServer, R.id.ibPwdShow, R.id.tvRegister, R.id.clMapType,
            R.id.clAccountLogin, R.id.clDeviceLogin, R.id.tvBleMode})
    public void onClick(View v) {
        Bundle bundle;
        switch (v.getId()) {
            case R.id.tvRegister:  //选择注册方式 邮箱、手机注册
                actionType = 1;
                ActionSheet.createBuilder(getContext(),
                                Objects.requireNonNull(getActivity()).getSupportFragmentManager())
                        .setCancelButtonTitle(getString(R.string.cancel))
                        .setOtherButtonTitles(getString(R.string.register_mobile),
                                getString(R.string.register_email))
                        .setCancelableOnTouchOutside(true)
                        .setListener(this).show();
                break;
            case R.id.clMapType:  //选地地图  谷歌、高德地图
                actionType = 2;
                ActionSheet.createBuilder(getContext(),
                                Objects.requireNonNull(getActivity()).getSupportFragmentManager())
                        .setCancelButtonTitle(getString(R.string.cancel))
                        .setOtherButtonTitles(getString(R.string.google_map),
                                getString(R.string.gaode_map))
                        .setCancelableOnTouchOutside(true)
                        .setListener(this).show();
                break;
            case R.id.tvSwitchType: // 切换登陆方式
                if (mLoginType == 2) // 切换设备号登录
                    mLoginType = 1;
                else // 切换帐号登录
                    mLoginType = 2;
                switchLoginType();
                break;
            case R.id.loginBtn: // 登录
                String username =
                        checkTaiWanMobilePhoneNumber(mEtUsername.getText().toString().trim());
                String pwd = mEtPwd.getText().toString().trim();
                if (TextUtils.isEmpty(username)) {  //帐号不为空
                    XToastUtils.toast(getContext(), mEtUsername.getHint().toString());
                    mEtUsername.requestFocus();
                }
//                else if (TextUtils.isEmpty(pwd)) {  //密码不为空
//                    XToastUtils.toast(getContext(), mEtPwd.getHint().toString());
//                    mEtPwd.requestFocus();
//                }

//                else if (mLoginType == 1 && !RegexUtils.isEmail(username)) {
//                    XToastUtils.toast(getContext(), R.string.input_true_email_prompt);
//                    mEtUsername.requestFocus();
//                } else if (mLoginType == 2 && !RegexUtils.isMobileSimple(username)) {
//                    XToastUtils.toast(getContext(), R.string.input_true_mobile_prompt);
//                    mEtUsername.requestFocus();
//                } else if (!RegularUtils.isPwd(pwd)) {
//                    XToastUtils.toast(getContext(), R.string.pwd_error_prompt);
//                    mEtPwd.requestFocus();
//                }
                else {
                    if (!NetworkUtils.isNetworkAvailable()) {
                        RequestToastUtils.toastNetwork(getContext());
                        return;
                    }
                    if (!mIsLogining && !mIsLogin) {
                        mIsLogining = true;
                        showDialog();
                        mUsername = username;
                        mPassword = pwd;
                        String language;
                        if ("zh".equals(Locale.getDefault().getLanguage())) {
                            if ("CN".equals(Locale.getDefault().getCountry())) {
                                language = "zh";
                            } else {
                                language = "tw";
                            }
                        } else {
                            language = Locale.getDefault().getLanguage();
                        }
                        if (mLoginType == CWConstant.LOGIN_TYPE_DEVICE) {
                            CarGpsRequestUtils.imeiLogin(username, pwd, language, mHandler);
                        } else {
                            CarGpsRequestUtils.doLogin(username, pwd, language, mHandler);
                        }
                    }
                }
                break;
            case R.id.tvForgotPwd: // 忘记密码
//                openNewPage(FindPwdFragment.class);
                actionType = 3;
                ActionSheet.createBuilder(getContext(),
                                Objects.requireNonNull(getActivity()).getSupportFragmentManager())
                        .setCancelButtonTitle(getString(R.string.cancel))
                        .setOtherButtonTitles(getString(R.string.find_pwd_email),
                                getString(R.string.find_pwd_mobile))
                        .setCancelableOnTouchOutside(true)
                        .setListener(this).show();
                break;
            case R.id.clLanguage: // 语言
                String language = SettingSPUtils.getInstance().getString(CWConstant.LANGUAGE, "");
                if (TextUtils.isEmpty(language)) {
                    if ("zh".equals(Locale.getDefault().getLanguage())) {
                        if ("CN".equals(Locale.getDefault().getCountry()))
                            language = "zh";
                        else
                            language = "tw";
                    } else if ("id".equals(Locale.getDefault().getLanguage()))
                        language = "in";
                    else
                        language = Locale.getDefault().getLanguage();
                }
                bundle = new Bundle();
                bundle.putInt(CWConstant.TYPE, 4);
                bundle.putString(CWConstant.TITLE, getString(R.string.select_language));
                bundle.putString(CWConstant.CONTENT, language);

                openNewPageForResult(CustomSelectorFragment.class, bundle,
                        CWConstant.REQUEST_LANGUAGE);
                break;
            case R.id.clServer: // 服务器
                bundle = new Bundle();
                bundle.putInt(CWConstant.TYPE, 21);
                bundle.putString(CWConstant.TITLE,
                        getResources().getString(R.string.select_server_address));
                openNewPageForResult(CustomSelectorFragment.class, bundle,
                        TConstant.REQUEST_SERVER_ADDRESS);

                break;
//            case R.id.tvMobileUnuse: // 手机号码已经不再使用
//                break;
            case R.id.ibPwdShow:
                v.setSelected(!v.isSelected());
                if (v.isSelected()) {
                    //如果选中，显示密码
                    mEtPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    //否则隐藏密码
                    mEtPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                break;
            case R.id.clAccountLogin:
                clickButtonToSwitchLoginType(CWConstant.LOGIN_TYPE_PHONE);
                break;
            case R.id.clDeviceLogin:
                clickButtonToSwitchLoginType(CWConstant.LOGIN_TYPE_DEVICE);
                break;
            case R.id.tvBleMode: // 蓝牙模式
                openNewPage(BlueToothFragment.class);
                break;
            default:
                break;
        }
    }

    private String checkTaiWanMobilePhoneNumber(String cal) {
        if (mLoginType == 2) {
            String countryCode = mTvCountryCode.getText().toString().substring(1);
            if (countryCode.equals("886") && cal.length() == 9) {
                StringBuilder stringBuilder = new StringBuilder(cal);
                stringBuilder.insert(0, "0");
                cal = stringBuilder.toString();
            }
        }
        return cal;
    }

    /**
     * 切换登陆方式
     */
    private void switchLoginType() {
        if (mLoginType == CWConstant.LOGIN_TYPE_DEVICE) { // 设备号登录
            mEtUsername.setInputType(InputType.TYPE_CLASS_NUMBER);
            mEtUsername.setHint(R.string.imei_hint);
            mTvCountryCode.setText(R.string.imei);
            mEtUsername.setText("");
            mEtPwd.setText("");
//            mCountryView.setVisibility(View.VISIBLE);
//            mTvSwitchType.setText(R.string.login_account);
        } else { // 帐号登录
            mEtUsername.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            mEtUsername.setHint(R.string.account_hint);
            mTvCountryCode.setText(R.string.account);
            mEtUsername.setText("");
            mEtPwd.setText("");
//            mCountryView.setVisibility(View.INVISIBLE);
//            mTvSwitchType.setText(R.string.login_imei);
        }
    }

    private void clickButtonToSwitchLoginType(int loginType) {
        if (mLoginType == loginType)
            return;
        mLoginType = loginType;
        switchLoginType();
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Intent data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                switch (requestCode) {
                    case CWConstant.REQUEST_COUNTRY_CODE: // 选择国家/区号
                        if (mLoginType == 2) {
                            mCountryCode = bundle.getString(CWConstant.COUNTRY_CODE);
//                            mTvSelectCountry.setText(bundle.getString(CWConstant.COUNTRY_NAME));
                            mTvCountryCode.setText(R.string.imei);
                        }
                        break;
                    case TConstant.REQUEST_SERVER_ADDRESS:
                        String serverIp = bundle.getString(CWConstant.CONTENT);
                        mTvShownServer.setText(serverIp);
                        break;
                    default:
                        break;
                }
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
                switch (msg.what) {
                    case TConstant.REQUEST_ACCOUNT_LOGIN: //新的登录回调
                        if (msg.obj == null) {
                            dismisDialog();
                            Toast.makeText(mActivity, R.string.login_failure_tips,
                                    Toast.LENGTH_SHORT).show();
                            mIsLogining = false;
                        } else {
                            AAABaseResponseBean responseBean = (AAABaseResponseBean) msg.obj;
//                            KLog.d("doLogin: "+mGson.toJson(responseBean));
                            if (responseBean.getCode() == TConstant.RESPONSE_SUCCESS) {
//                                KLog.d("data: "+mGson.toJson(responseBean.getData()));
                                AAAUserModel subUserModel =
                                        mGson.fromJson(mGson.toJson(((ArrayList<?>) responseBean.getData()).get(0)),
                                                AAAUserModel.class);
//                                KLog.d("AAAUserModel-> "+mGson.toJson(userModel1));
                                AAAUserModel fUserModel = SQLite.select().from(AAAUserModel.class)
                                        .where(AAAUserModel_Table.userId.eq(subUserModel.getUserId()))
                                        .querySingle();
                                if (fUserModel != null) {
                                    subUserModel.setSelectDeviceId(fUserModel.getSelectDeviceId());
                                    if (mLoginType == 1)
                                        subUserModel.setEmail(mUsername);
                                    else
                                        subUserModel.setMobile(mUsername);
                                }
                                subUserModel.save();

                                MainApplication.getInstance().setTrackUserModel(subUserModel);
                                String token = subUserModel.getToken();
                                long userId = subUserModel.getUserId();

                                SettingSPUtils.getInstance().putString(CWConstant.USERNAME,
                                        mUsername);
                                SettingSPUtils.getInstance().putString(CWConstant.PASSWORD,
                                        mPassword);
                                SettingSPUtils.getInstance().putString(CWConstant.USERNAME_LOGIN,
                                        mUsername);
                                SettingSPUtils.getInstance().putString(CWConstant.PASSWORD_LOGIN,
                                        mPassword);
                                DataUtils.setCreateTime(subUserModel.getCreateDate());
                                DataUtils.setPwdType(responseBean.getPwdType());

                                SettingSPUtils.getInstance().putString(TConstant.TOKEN, token);
                                SettingSPUtils.getInstance().putLong(TConstant.USER_ID_NEW, userId);
                                SettingSPUtils.getInstance().putInt(TConstant.IS_AGENT,
                                        subUserModel.getIsAgent());

                                if (NetworkUtils.isNetworkAvailable()) {
                                    SettingSPUtils.getInstance().putInt(TConstant.MAP_TYPE,
                                            mapType);
                                    CarGpsRequestUtils.getDeviceList(subUserModel, null, mHandler);
                                    return false;
                                } else {
                                    dismisDialog();
                                    showMessage(ErrorCode.getResId(responseBean.getCode()));
                                    mIsLogining = false;
                                }
                            } else {
                                dismisDialog();
                                mIsLogining = false;
                                showMessage(ErrorCode.getResId(responseBean.getCode()));
                            }
                        }
                        break;
                    case TConstant.REQUEST_URL_GET_DEVICE_LIST:
                        dismisDialog();
                        if (msg.obj == null) {
                            mIsLogining = false;
                            Toast.makeText(mActivity, R.string.network_error_prompt,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            AAABaseResponseBean responseBean = (AAABaseResponseBean) msg.obj;
                            AAARequestBean aaaRequestBean =
                                    mGson.fromJson(responseBean.getRequestObject(),
                                            AAARequestBean.class);
                            if (responseBean.getCode() == TConstant.RESPONSE_SUCCESS) {
                                try {
                                    showMessage(R.string.login_success_prompt);
                                    if (responseBean.getData() == null || ((ArrayList) responseBean.getData()).size() == 0) {
                                        EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_FINISH));
                                        ActivityUtils.startActivity(BindDeviceActivity.class);
                                    } else {
                                        List<AAADeviceModel> deviceModels =
                                                mGson.fromJson(mGson.toJson(responseBean.getData()), new TypeToken<List<AAADeviceModel>>() {
                                                }.getType());
                                        SQLite.delete(AAADeviceModel.class).execute();

                                        String imei =
                                                SettingSPUtils.getInstance().getString(TConstant.SELECTED_IMEI, deviceModels.get(0).getDeviceImei());
                                        AAADeviceModel deviceModel = deviceModels.get(0);
                                        for (AAADeviceModel item : deviceModels) {
                                            if (item.getDeviceImei().equals(imei)) {
                                                deviceModel = item;
                                                break;
                                            }
                                        }

                                        for (AAADeviceModel device : deviceModels) {
                                            device.setUserId(aaaRequestBean.getUserId());
                                            device.save();
                                        }
                                        AAAUserModel aaaUserModel = getTrackUserModel();
                                        aaaUserModel.setSelectDeviceId(imei);
                                        aaaUserModel.save();
                                        MainApplication.getInstance().setTrackDeviceList(deviceModels);
                                        MainApplication.getInstance().setTrackDeviceModel(deviceModel);

                                        SettingSPUtils.getInstance().putInt(CWConstant.DEVICE_TYPE, deviceModel.getDeviceType());

                                        EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_FINISH));
                                        ActivityUtils.startActivity(MainActivity.class);
                                    }
                                } catch (Exception e) {
                                    mIsLogining = false;
                                    Toast.makeText(mActivity, R.string.network_error_prompt,
                                            Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                mIsLogining = false;
                                showMessage(ErrorCode.getResId(responseBean.getCode()));
                            }
                        }
                        break;
                    case TConstant.REQUEST_IMEI_LOGIN: // 设备号登录
                        dismisDialog();
                        if (msg.obj == null) {
                            mIsLogining = false;
                            Toast.makeText(mActivity, R.string.network_error_prompt,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            AAABaseResponseBean responseBean = (AAABaseResponseBean) msg.obj;
                            if (responseBean.getCode() == TConstant.RESPONSE_SUCCESS) {
                                if (responseBean.getData() != null) {
                                    List list = (List) responseBean.getData();
                                    if (list.size() == 0) {
                                        showMessage(R.string.login_failure_tips);
                                        mIsLogining = false;
                                        return false;
                                    }
                                    AAADeviceModel device =
                                            mGson.fromJson(mGson.toJson(list.get(0)),
                                                    AAADeviceModel.class);
                                    List<AAADeviceModel> devices = new ArrayList<>();
                                    devices.add(device);

                                    String token = responseBean.getToken();
                                    long userId = Long.parseLong(responseBean.getUid());

                                    AAAUserModel subUserModel = new AAAUserModel();
                                    subUserModel.setToken(token);
                                    subUserModel.setUserId(userId);
                                    subUserModel.setUserName(mUsername);
                                    DataUtils.setCreateTime(0);
                                    MainApplication.getInstance().setTrackUserModel(subUserModel);
                                    SettingSPUtils.getInstance().putString(CWConstant.USERNAME,
                                            mUsername);
                                    SettingSPUtils.getInstance().putString(CWConstant.PASSWORD,
                                            mPassword);
                                    SettingSPUtils.getInstance().putString(CWConstant.USERNAME_LOGIN, mUsername);
                                    SettingSPUtils.getInstance().putString(CWConstant.PASSWORD_LOGIN, mPassword);

                                    DataUtils.setLoginType(CWConstant.LOGIN_TYPE_DEVICE);
                                    DataUtils.setPwdType(responseBean.getPwdType());
                                    SettingSPUtils.getInstance().putString(TConstant.TOKEN, token);
                                    SettingSPUtils.getInstance().putLong(TConstant.USER_ID_NEW,
                                            Long.parseLong(device.getDeviceImei()));
                                    SettingSPUtils.getInstance().putInt(TConstant.IS_AGENT, 0);
                                    //非经销商
                                    SettingSPUtils.getInstance().putInt(TConstant.MAP_TYPE,
                                            mapType);

                                    MainApplication.getInstance().setTrackDeviceList(devices);
                                    MainApplication.getInstance().setTrackDeviceModel(device);

                                    SettingSPUtils.getInstance().putInt(CWConstant.DEVICE_TYPE,
                                            device.getDeviceType());

                                    ActivityUtils.startActivity(MainActivity.class);
                                } else {
                                    showMessage(R.string.login_failure_tips);
                                    mIsLogining = false;
                                }
                            } else {
                                showMessage(ErrorCode.getResId(responseBean.getCode()));
                                mIsLogining = false;
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultSuccess(String event) {
        if (CWConstant.URL_USER_LOGIN.equals(event) && !mIsLogin)
            mIsLogin = true;
        else if (CWConstant.URL_REGISTER.equals(event))
            initViews();
    }

    @Override
    public void onDestroy() {
        // 注销订阅者
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        switch (index) {
            case 0:
                if (actionType == 1) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(CWConstant.TYPE, 2);
                    openNewPage(RegisterFragment.class, bundle);
                } else if (actionType == 2) {
                    mapType = 1;
                    tvMapType.setText(R.string.google_map);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putInt(TConstant.TYPE, 1);
                    openNewPage(FindPwdFragment.class, bundle);
                }
                break;
            case 1:
                if (actionType == 1) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(CWConstant.TYPE, 1);
                    openNewPage(RegisterFragment.class, bundle);
                } else if (actionType == 2) {
                    mapType = 0;
                    tvMapType.setText(R.string.gaode_map);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putInt(TConstant.TYPE, 2);
                    openNewPage(FindPwdFragment.class, bundle);
                }
                break;
        }
    }

}
