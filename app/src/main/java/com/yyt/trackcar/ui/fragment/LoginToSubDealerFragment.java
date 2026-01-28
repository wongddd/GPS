package com.yyt.trackcar.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.reflect.TypeToken;
import com.socks.library.KLog;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xutil.app.ActivityUtils;
import com.yyt.trackcar.MainApplication;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAABaseResponseBean;
import com.yyt.trackcar.bean.PostMessage;
import com.yyt.trackcar.dbflow.AAADeviceModel;
import com.yyt.trackcar.dbflow.AAAUserModel;
import com.yyt.trackcar.ui.activity.BindDeviceActivity;
import com.yyt.trackcar.ui.activity.MainActivity;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.ErrorCode;
import com.yyt.trackcar.utils.SettingSPUtils;
import com.yyt.trackcar.utils.TConstant;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
@Page(name = "LoginToSubDealerFragment", anim = CoreAnim.none)
public class LoginToSubDealerFragment extends BaseFragment {

    @BindView(R.id.etUsername)
    EditText etUsername;

    private String mUsername;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_login_to_sub_dealer;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
//        titleBar.setLeftImageResource(0);
        titleBar.setTitle(R.string.login_to_subordinate_dealer_account);
        return titleBar;
    }

    @Override
    protected void initViews() {
        initData();
        etUsername.setText(mUsername);
        etUsername.setEnabled(false);
    }

    private void initData () {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mUsername = bundle.getString(TConstant.USERNAME);
            KLog.d("username: " + mUsername);
        }
    }

    @OnClick(R.id.loginBtn)
    public void onClick (View view) {
        switch (view.getId()) {
            case R.id.loginBtn:
                doLogin();
                break;
        }
    }

    private void doLogin () {
        if (mUsername != null) {
            showDialog();
            CarGpsRequestUtils.loginToSubordinateDealer(getTrackUserModel(), mUsername, mHandler);
        }
    }

    private final Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            try {
                if (message.obj == null) {
                    dismisDialog();
                    showMessage(R.string.request_error_prompt);
                    return false;
                }
                AAABaseResponseBean response;
                switch (message.what) {
                    case TConstant.REQUEST_LOGIN_TO_SUBORDINATE_DEALER_ACCOUNT:
                        response = (AAABaseResponseBean) message.obj;
                        if (response.getCode() == TConstant.RESPONSE_SUCCESS) {
                            List<AAAUserModel> userModelList = mGson.fromJson(mGson.toJson(response.getData()), new TypeToken<List<AAAUserModel>>(){}.getType());
                            AAAUserModel userModel = userModelList.get(0);

                            MainApplication.getInstance().setTrackUserModel(userModel);
                            SettingSPUtils.getInstance().putString(CWConstant.PASSWORD, "");
                            SettingSPUtils.getInstance().putString(CWConstant.USERNAME, mUsername);

                            SettingSPUtils.getInstance().putString(TConstant.TOKEN, userModel.getToken());
                            SettingSPUtils.getInstance().putLong(TConstant.USER_ID_NEW, userModel.getUserId());
                            SettingSPUtils.getInstance().putInt(TConstant.IS_AGENT, 1);

                            // 请求该用户的绑定的设备列表
                            CarGpsRequestUtils.getDeviceList(userModel, null, mHandler);
                        } else {
                            dismisDialog();
                            showMessage(ErrorCode.getResId(response.getCode()));
                        }
                        break;
                    case TConstant.REQUEST_URL_GET_DEVICE_LIST:
                        dismisDialog();
                        response = (AAABaseResponseBean) message.obj;
                        if (response.getCode() == TConstant.RESPONSE_SUCCESS) {
                            List<AAADeviceModel> deviceList = mGson.fromJson(mGson.toJson(response.getData()), new TypeToken<List<AAADeviceModel>>(){}.getType());
                            EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_FINISH));
                            if (deviceList.size() == 0) {
                                showMessage(R.string.user_no_bound_device_tips);
                                ActivityUtils.startActivity(BindDeviceActivity.class);
                            } else {
                                MainApplication.getInstance().setTrackDeviceList(deviceList);
                                showMessage(R.string.login_success_prompt);
                                startActivity(MainActivity.class);
                                mActivity.finish();
                            }
                        } else {
                            showMessage(ErrorCode.getResId(response.getCode()));
                        }
                        break;
                }
            }catch (ClassCastException e) {
                e.printStackTrace();
            }
            return false;
        }
    });
}
