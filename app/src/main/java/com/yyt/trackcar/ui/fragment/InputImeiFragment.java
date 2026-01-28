package com.yyt.trackcar.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.MainApplication;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAABaseResponseBean;
import com.yyt.trackcar.bean.RequestBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.dbflow.AAADeviceModel;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.BLEDataUtils;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.RequestToastUtils;
import com.yyt.trackcar.utils.TConstant;
import com.yyt.trackcar.utils.XToastUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      InputImeiFragment
 * @ author:        QING
 * @ createTime:    2020-02-28 15:24
 * @ describe:      TODO 输入绑定号页面
 */
@Page(name = "InputImei", params = {CWConstant.TYPE})
public class InputImeiFragment extends BaseFragment {
    @BindView(R.id.etImei)
    EditText mEtImei; // 绑定号文本编辑
    /**
     * 0.从未绑定界面 1.首页绑定新设备 2.首页绑定新设备 3.经销商经销设备
     */
    @AutoWired
    int type;
    private int userId = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_input_imei;
    }

    @Override
    protected void initArgs() {
        XRouter.getInstance().inject(this);
        Bundle bundle = getArguments();
        if (bundle != null)
            userId = bundle.getInt(TConstant.USER_ID, 0);
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle("");
        return titleBar;
    }

    @Override
    protected void initViews() {
//        KLog.d("type :"+type+" && userId: "+userId);
        mEtImei.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    mEtImei.setSelection(mEtImei.getText().length());
                }
            }
        });
    }

    @SingleClick
    @OnClick({R.id.bindBtn, R.id.tvNotFound, R.id.tv_qrcode_scanner})
    public void onClick(View v) {
        Bundle bundle;
        switch (v.getId()) {
            case R.id.bindBtn: // 绑定
                String imei = mEtImei.getText().toString().trim();
                if (TextUtils.isEmpty(imei)) {
                    mEtImei.requestFocus();
                    XToastUtils.toast(mEtImei.getHint().toString());
                } else if (imei.length() >= 6) {
                    if (type != 3) {
                        if (imei.length() == 17) {
                            String[] array = imei.split(":");
                            if (array.length == 6) {
                                String macAddress = BLEDataUtils.convertMacAddress(imei);
                                if(macAddress.length() == 18){
                                    imei = macAddress;
                                }
                            }
                        }
                        List<AAADeviceModel> deviceList =
                                MainApplication.getInstance().getTrackDeviceList();
                        for (AAADeviceModel model : deviceList) {
                            if (imei.equals(model.getDeviceImei())) {
                                XToastUtils.toast(R.string.user_already_bind_prompt);
                                return;
                            }
                        }
                        bundle = new Bundle();
                        bundle.putInt(CWConstant.TYPE, type);
                        bundle.putString(CWConstant.IMEI, imei);
                        openNewPage(BindApplyFragmentNew.class, bundle);
                    } else
                        CarGpsRequestUtils.bindDeviceForDealer(getTrackUserModel(), imei, userId,
                                mHandler);
//                    CWRequestUtils.getInstance().getImeiBindUsers(mActivity,
//                            getUserModel().getToken(),
//                            imei, mHandler);
                } else {
                    mEtImei.requestFocus();
                    XToastUtils.toast(R.string.input_imei_error_prompt);
                }
                break;
            case R.id.tvNotFound: // 找不到输入的绑定号？
                bundle = new Bundle();
                bundle.putString(CWConstant.TITLE,
                        getString(R.string.not_found_imei));
                bundle.putInt(CWConstant.TYPE, 2);
                openNewPage(TextFragment.class, bundle);
                break;
            case R.id.tv_qrcode_scanner: // 切换到扫描
                bundle = new Bundle();
                bundle.putInt(CWConstant.TYPE, type);
//                openNewPage(CameraCaptureFragment.class,bundle);
                openNewPage(ScanQuickResponseCodeFragment.class, bundle);
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
                    case CWConstant.REQUEST_URL_GET_IMEI_BIND_USERS: // 设备管理员查询  某个设备的绑定用户
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()),
                                                RequestBean.class);
                                List userList = resultBean.getUserList();
                                Bundle bundle = new Bundle();
                                if (userList != null && userList.size() > 0) {
                                    userModel = mGson.fromJson(mGson.toJson(userList.get(0)),
                                            UserModel.class);
                                    bundle.putInt(CWConstant.TYPE, 1);
                                    bundle.putString(CWConstant.NAME, userModel.getName());
                                } else
                                    bundle.putInt(CWConstant.TYPE, 0);
                                bundle.putString(CWConstant.IMEI, requestBean.getImei());
                                openNewPage(BindApplyFragmentNew.class, bundle);
                            } else
                                RequestToastUtils.toast(resultBean.getCode());
                        }
                        break;
                    case TConstant.REQUEST_BIND_DEVICE_FOR_DEALER:
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
                            AAABaseResponseBean response = (AAABaseResponseBean) msg.obj;
                            if (response.getCode() == TConstant.RESPONSE_SUCCESS) {
                                Toast.makeText(mActivity, R.string.bind_success,
                                        Toast.LENGTH_SHORT).show();
                                popToBack();
                            } else {
                                XToastUtils.toast(R.string.request_unkonow_prompt);
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
