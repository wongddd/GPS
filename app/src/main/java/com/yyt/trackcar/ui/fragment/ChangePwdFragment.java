package com.yyt.trackcar.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;

import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
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
import com.yyt.trackcar.dbflow.AAAUserModel;
import com.yyt.trackcar.ui.activity.LoginActivity;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.ErrorCode;
import com.yyt.trackcar.utils.RegularUtils;
import com.yyt.trackcar.utils.RequestToastUtils;
import com.yyt.trackcar.utils.SettingSPUtils;
import com.yyt.trackcar.utils.TConstant;
import com.yyt.trackcar.utils.XToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      ChangePwdFragment
 * @ author:        QING
 * @ createTime:    2020/3/11 02:31
 * @ describe:      TODO 修改密码页面
 */
@Page(name = "ChangePwd")
public class ChangePwdFragment extends BaseFragment {
    @BindView(R.id.etOldPwd)
    EditText mEtOldPwd; // 旧密码文本编辑
    @BindView(R.id.etNewPwd)
    EditText mEtNewPwd; // 新密码文本编辑
    @BindView(R.id.etConfirmPwd)
    EditText mEtConfirmPwd; // 确认密码文本编辑

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_change_pwd;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(R.string.change_pwd);
        return titleBar;
    }

    @Override
    protected void initViews() {
    }

    @SingleClick
    @OnClick({R.id.completeBtn, R.id.tvQuestion, R.id.ibOldPwdShow, R.id.ibNewPwdShow,
            R.id.ibConfirmPwdShow})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.completeBtn: // 完成
                String oldPwd = mEtOldPwd.getText().toString().trim();
                String newPwd = mEtNewPwd.getText().toString().trim();
                String confirmPwd = mEtConfirmPwd.getText().toString().trim();
                if (TextUtils.isEmpty(oldPwd)) {
                    XToastUtils.toast(mEtOldPwd.getHint().toString());
                    mEtOldPwd.requestFocus();
                } else if (TextUtils.isEmpty(newPwd)) {
                    XToastUtils.toast(mEtNewPwd.getHint().toString());
                    mEtNewPwd.requestFocus();
                } else if (!RegularUtils.isPwd(oldPwd)) {
                    XToastUtils.toast(R.string.old_pwd_input_prompt);
                    mEtOldPwd.requestFocus();
                } else if (!RegularUtils.isPwd(newPwd)) {
                    XToastUtils.toast(R.string.set_pwd_prompt);
                    mEtNewPwd.requestFocus();
                } else if (newPwd.equals(oldPwd)) {
                    XToastUtils.toast(R.string.old_same_new_pwd_prompt);
                    mEtNewPwd.requestFocus();
                } else if (newPwd.equals(confirmPwd)) {
                    if (!NetworkUtils.isNetworkAvailable()) {
                        RequestToastUtils.toastNetwork();
                        return;
                    }
//                    UserModel userModel = getUserModel();
//                    if (userModel != null)
//                        CWRequestUtils.getInstance().updatePwd(mActivity, userModel.getToken(),
//                                SettingSPUtils.getInstance().getString(CWConstant.LOGIN_TYPE, "1"),
//                                SettingSPUtils.getInstance().getString(CWConstant.USERNAME,
//                                        ""), newPwd, oldPwd, mHandler);
                    AAAUserModel userModel = getTrackUserModel();
                    if (userModel!=null)
                        CarGpsRequestUtils.resetPassword(userModel,oldPwd,newPwd,mHandler);
                } else {
                    XToastUtils.toast(R.string.reset_pwd_input_different_prompt);
                    mEtConfirmPwd.requestFocus();
                }
                break;
            case R.id.tvQuestion: // 忘记密码
                AAAUserModel userModel = getTrackUserModel();
                Bundle bundle = new Bundle();
                Pattern pattern = Pattern.compile("^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}$");
                Matcher matcher = pattern.matcher(userModel.getUserName());
                if (matcher.matches()) { // 帐号 匹配邮箱命名规则
                    bundle.putInt(TConstant.TYPE, 1);
                } else { // 帐号不匹配邮箱规则 则位手机
                    bundle.putInt(TConstant.TYPE, 2);
                }
                openNewPage(FindPwdFragment.class, bundle);
                break;
            case R.id.ibOldPwdShow:
                v.setSelected(!v.isSelected());
                if (v.isSelected()) {
                    //如果选中，显示密码
                    mEtOldPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    //否则隐藏密码
                    mEtOldPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                break;
            case R.id.ibNewPwdShow:
                v.setSelected(!v.isSelected());
                if (v.isSelected()) {
                    //如果选中，显示密码
                    mEtNewPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    //否则隐藏密码
                    mEtNewPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                break;
            case R.id.ibConfirmPwdShow:
                v.setSelected(!v.isSelected());
                if (v.isSelected()) {
                    //如果选中，显示密码
                    mEtConfirmPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    //否则隐藏密码
                    mEtConfirmPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
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
                switch (msg.what) {
                    case CWConstant.REQUEST_URL_UPDATE_PWD: // 用户修改密码
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                XToastUtils.toast(R.string.change_pwd_success_prompt);
//                                requestBean =
//                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), AAATrackRequestBeanOldEdition.class);
                                SettingSPUtils.getInstance().putString(CWConstant.PASSWORD, "");
                                SettingSPUtils.getInstance().putString(CWConstant.PASSWORD_LOGIN, "");
                                SettingSPUtils.getInstance().putString(CWConstant.TOKEN, "");
                                SettingSPUtils.getInstance().putLong(CWConstant.U_ID, -1);
                                MainApplication.getInstance().setDeviceModel(null);
                                MainApplication.getInstance().setUserModel(null);
                                MainApplication.getInstance().getDeviceList().clear();
                                EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_FINISH));
                                ActivityUtils.startActivity(LoginActivity.class);
                            } else
                                RequestToastUtils.toast(resultBean.getCode());
                        }
                        break;
                    case TConstant.REQUEST_RESET_PASSWORD:
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
                            AAABaseResponseBean response = (AAABaseResponseBean) msg.obj;
                            if (response.getCode() == TConstant.RESPONSE_SUCCESS) {
                                XToastUtils.toast(R.string.change_pwd_success_prompt);
//                                requestBean =
//                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), AAATrackRequestBeanOldEdition.class);
                                SettingSPUtils.getInstance().putString(CWConstant.PASSWORD, "");
                                SettingSPUtils.getInstance().putString(CWConstant.PASSWORD_LOGIN, "");
                                SettingSPUtils.getInstance().putString(CWConstant.TOKEN, "");
                                SettingSPUtils.getInstance().putLong(CWConstant.U_ID, -1);
                                MainApplication.getInstance().setDeviceModel(null);
                                MainApplication.getInstance().setUserModel(null);
                                MainApplication.getInstance().getDeviceList().clear();
                                EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_FINISH));
                                ActivityUtils.startActivity(LoginActivity.class);
                            } else
                                showMessage(ErrorCode.getResId(response.getCode()));
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
