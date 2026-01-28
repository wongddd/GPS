package com.yyt.trackcar.ui.fragment;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.raizlabs.android.dbflow.sql.language.SQLite;
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
import com.yyt.trackcar.bean.RequestBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.DeviceModel_Table;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.dbflow.UserModel_Table;
import com.yyt.trackcar.ui.activity.BindDeviceActivity;
import com.yyt.trackcar.ui.activity.MainActivity;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CWRequestUtils;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.DataUtils;
import com.yyt.trackcar.utils.ErrorCode;
import com.yyt.trackcar.utils.RegularUtils;
import com.yyt.trackcar.utils.RequestToastUtils;
import com.yyt.trackcar.utils.SettingSPUtils;
import com.yyt.trackcar.utils.TConstant;
import com.yyt.trackcar.utils.XToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      SetPwdFragment
 * @ author:        QING
 * @ createTime:    2020-02-26 15:06
 * @ describe:      TODO 设置密码页面
 */
@Page(name = "SetPwd", params = {CWConstant.TYPE, CWConstant.USERNAME, CWConstant.COUNTRY_CODE,
        CWConstant.VERIFICATION_CODE})
public class SetPwdFragment extends BaseFragment {
    @BindView(R.id.etPwd)
    TextView mEtPwd; // 密码文本编辑
    @AutoWired
    String countryCode; // 国家编码
    @AutoWired
    String username; // 用户名
    @AutoWired
    String verificationCode; // 验证码
    @AutoWired
    int type; // 注册类型 1邮箱 2手机

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_pwd;
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
    }

    @SingleClick
    @OnClick({R.id.completeBtn, R.id.ibPwdShow})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.completeBtn: // 完成
                String pwd = mEtPwd.getText().toString().trim();
                if (TextUtils.isEmpty(pwd)) {
                    XToastUtils.toast(mEtPwd.getHint().toString());
                    mEtPwd.requestFocus();
                }
                else if (!RegularUtils.isPwd(pwd)) {  //设置密码规则
                    XToastUtils.toast(R.string.set_pwd_prompt);
                    mEtPwd.requestFocus();
                }
                else if (NetworkUtils.isNetworkAvailable()) {
                    String parameter =  verificationCode;
                    CarGpsRequestUtils.doRegister(username,pwd,null,null,parameter,mHandler);
                } else
                    RequestToastUtils.toastNetwork();
                break;
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
                    case CWConstant.REQUEST_URL_REGISTER: // 注册
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                XToastUtils.toast(R.string.register_success_prompt);
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                String username = requestBean.getUsername();
                                String pwd = requestBean.getPwd();
                                int type = Integer.parseInt(requestBean.getType());
                                String countryCode = requestBean.getCountry();
                                SettingSPUtils.getInstance().putString(CWConstant.USERNAME,
                                        username);
                                SettingSPUtils.getInstance().putString(CWConstant.PASSWORD, pwd);
                                SettingSPUtils.getInstance().putString(CWConstant.USERNAME_LOGIN, username);
                                SettingSPUtils.getInstance().putString(CWConstant.PASSWORD_LOGIN, pwd);
                                DataUtils.setLoginType(CWConstant.LOGIN_TYPE_PHONE);
                                if (type == 2)
                                    SettingSPUtils.getInstance().putString(CWConstant.COUNTRY_CODE,
                                            countryCode);
                                EventBus.getDefault().post(CWConstant.URL_REGISTER);
                                if (NetworkUtils.isNetworkAvailable()) {
                                    String languageType;
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
                                        languageType = "1";
                                    else if ("tw".equals(language))
                                        languageType = "2";
                                    else if ("in".equals(language))
                                        languageType = "4";
                                    else if ("pt".equals(language))
                                        languageType = "5";
                                    else if ("es".equals(language))
                                        languageType = "6";
                                    else if ("vi".equals(language))
                                        languageType = "7";
                                    else if ("ar".equals(language))
                                        languageType = "8";
                                    else if ("ru".equals(language))
                                        languageType = "9";
                                    else if ("fr".equals(language))
                                        languageType = "10";
                                    else if ("ja".equals(language))
                                        languageType = "11";
                                    else if ("de".equals(language))
                                        languageType = "12";
                                    else
                                        languageType = "3";
                                    CWRequestUtils.getInstance().login(mActivity,
                                            countryCode, username, pwd, String.valueOf(type), languageType, mHandler);
                                } else {
                                    RequestToastUtils.toastNetwork();
                                    popToBack("Login", null);
                                }
                            } else
                                RequestToastUtils.toast(resultBean.getCode());
                        }
                        break;
                    case CWConstant.REQUEST_URL_USER_LOGIN: // 登录
                        if (msg.obj == null) {
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                            popToBack("Login", null);
                        } else {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                SettingSPUtils.getInstance().putString(CWConstant.USERNAME,
                                        requestBean.getUsername());
                                SettingSPUtils.getInstance().putString(CWConstant.PASSWORD,
                                        requestBean.getPwd());
                                SettingSPUtils.getInstance().putString(CWConstant.USERNAME_LOGIN, requestBean.getUsername());
                                SettingSPUtils.getInstance().putString(CWConstant.PASSWORD_LOGIN, requestBean.getPwd());
                                DataUtils.setLoginType(CWConstant.LOGIN_TYPE_PHONE);
                                SettingSPUtils.getInstance().putString(CWConstant.COUNTRY_CODE,
                                        requestBean.getCountry());
                                userModel = mGson.fromJson(mGson.toJson(resultBean.getResultBean()),
                                        UserModel.class);
                                UserModel mLastUserModel =
                                        SQLite.select().from(UserModel.class)
                                                .where(UserModel_Table.u_id.eq(userModel.getU_id()))
                                                .querySingle();
                                if (mLastUserModel != null)
                                    userModel.setSelectImei(mLastUserModel.getSelectImei());
                                userModel.save();

//                                SettingSPUtils.getInstance().putLong(CWConstant.U_ID,
//                                        userModel.getU_id());
//                                SettingSPUtils.getInstance().putString(CWConstant.TOKEN,
//                                        userModel.getToken());
//                                MainApplication.getInstance().setUserModel(userModel);
//                                SQLite.delete(AAADeviceModel.class).where(DeviceModel_Table.u_id.eq(userModel.getU_id())).execute();
//                                Intent intent = new Intent(XUtil.getContext(),
//                                        SelectDeviceTypeActivity.class);
//                                Bundle bundle = new Bundle();
//                                bundle.putInt(CWConstant.TYPE, 3);
//                                intent.putExtras(bundle);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                EventBus.getDefault().post(CWConstant.URL_USER_LOGIN);
//                                EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_FINISH));
//                                ActivityUtils.startActivity(intent);

                                if (NetworkUtils.isNetworkAvailable())
                                    CWRequestUtils.getInstance().getBindDeviceList(mActivity,
                                            userModel.getU_id(), userModel.getToken(), 1, mHandler);
                                else {
                                    RequestToastUtils.toastNetwork();
                                    popToBack("Login", null);
                                }
                            } else {
                                RequestToastUtils.toast(resultBean.getCode());
                                popToBack("Login", null);
                            }
                        }
                        break;
                    case CWConstant.REQUEST_URL_GET_BIND_DEVICE_LIST: // 用户查询绑定设备列表
                        if (msg.obj == null) {
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                            popToBack("Login", null);
                        } else {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                XToastUtils.toast(R.string.login_success_prompt);
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()),
                                                RequestBean.class);
                                userModel =
                                        SQLite.select().from(UserModel.class)
                                                .where(UserModel_Table.u_id.eq(requestBean.getU_id()))
                                                .querySingle();
                                if (userModel != null && MainApplication.getInstance().getUserModel() == null) {
                                    SettingSPUtils.getInstance().putLong(CWConstant.U_ID,
                                            requestBean.getU_id());
                                    SettingSPUtils.getInstance().putString(CWConstant.TOKEN,
                                            requestBean.getToken());
                                    SQLite.delete(DeviceModel.class).where(DeviceModel_Table.u_id.eq(userModel.getU_id())).execute();
                                    String selectImei = userModel.getSelectImei();
                                    if (selectImei == null)
                                        selectImei = "";
                                    List<DeviceModel> deviceList = new ArrayList<>();
                                    if (resultBean.getDeviceList() != null) {
                                        for (Object obj : resultBean.getDeviceList()) {
                                            DeviceModel deviceModel =
                                                    mGson.fromJson(mGson.toJson(obj),
                                                            DeviceModel.class);
                                            saveDeviceIp(requestBean.getU_id(), deviceModel.getImei(),
                                                    deviceModel.getIp());
                                            deviceModel.setU_id(requestBean.getU_id());
                                            deviceModel.save();
                                            deviceList.add(deviceModel);
                                            if (selectImei.equals(deviceModel.getImei()))
                                                MainApplication.getInstance().setDeviceModel(deviceModel);
                                        }
                                        if (getDevice() == null && deviceList.size() > 0) {
                                            MainApplication.getInstance().setDeviceModel(deviceList.get(0));
                                            userModel.setSelectImei(getDevice().getImei());
                                            userModel.save();
                                        }
                                    }
                                    MainApplication.getInstance().setUserModel(userModel);
                                    getDeviceList().clear();
                                    getDeviceList().addAll(deviceList);
                                    EventBus.getDefault().post(CWConstant.URL_USER_LOGIN);
                                    EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_FINISH));
                                    if (getDeviceList().size() == 0)
                                        ActivityUtils.startActivity(BindDeviceActivity.class);
                                    else
                                        ActivityUtils.startActivity(MainActivity.class);
                                }
                            } else {
                                RequestToastUtils.toast(resultBean.getCode());
                                popToBack("Login", null);
                            }
                        }
                        break;
                    case TConstant.REQUEST_BIND_DEVICE:
                        try {
                            AAABaseResponseBean response = (AAABaseResponseBean) msg.obj;
                            if (response.getCode() == TConstant.RESPONSE_SUCCESS
                            || response.getCode() == TConstant.RESPONSE_SUCCESS_NEW){
//                                ActivityUtils.startActivity(LoginActivity.class);
                                Toast.makeText(mActivity, R.string.register_success_prompt, Toast.LENGTH_SHORT).show();
                                popToBack("AAALogin", null);
                            }else{
                                showMessage(ErrorCode.getResId(response.getCode()));
                            }
                        }catch(Exception e){
                            showMessage(R.string.register);
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
