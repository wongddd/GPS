package com.yyt.trackcar.ui.fragment;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.xuexiang.xaop.annotation.IOThread;
import com.xuexiang.xaop.annotation.MainThread;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xaop.enums.ThreadType;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xqrcode.XQRCode;
import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.dialog.bottomsheet.BottomSheet;
import com.xuexiang.xutil.app.ActivityUtils;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.MainApplication;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAABaseResponseBean;
import com.yyt.trackcar.bean.PostMessage;
import com.yyt.trackcar.bean.RequestBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.dbflow.AAADeviceModel;
import com.yyt.trackcar.dbflow.AAAUserModel;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.DeviceModel_Table;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.ui.activity.BindDeviceActivity;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.DialogUtils;
import com.yyt.trackcar.utils.ErrorCode;
import com.yyt.trackcar.utils.ImageLoadUtils;
import com.yyt.trackcar.utils.RequestToastUtils;
import com.yyt.trackcar.utils.TConstant;
import com.yyt.trackcar.utils.XToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      QRCodeFragment
 * @ author:        QING
 * @ createTime:    2020/3/10 16:47
 * @ describe:      TODO 二维码页面
 */
@Page(name = "QRCode", params = {CWConstant.TITLE, CWConstant.TYPE})
public class QRCodeFragment extends BaseFragment {
    @BindView(R.id.ivPortrait)
    ImageView mIvPortrait; // 头像
    @BindView(R.id.tvTitle)
    TextView mTvTitle; // 二维码文本
    @BindView(R.id.tvContent)
    TextView mTvContent; // 绑定号文本
    @BindView(R.id.ivQrcode)
    ImageView mIvQrcode; // 二维码图片
    @BindView(R.id.unBindBtn)
    Button mUnBindBtn; // 解绑按钮
    @AutoWired
    String title; // 标题
    @AutoWired
    int type; // 类型 0.二维码 1.解绑
    private BottomSheet mBottomSheet; // 选项弹窗

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_qrcode;
    }

    @Override
    protected void initArgs() {
        XRouter.getInstance().inject(this);
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(title);
        return titleBar;
    }

    @Override
    protected void initViews() {
        if (type != 1) {
            mUnBindBtn.setEnabled(false);
            mUnBindBtn.setVisibility(View.INVISIBLE);
        }
        AAADeviceModel deviceModel = getTrackDeviceModel();
        AAAUserModel userModel = getTrackUserModel();
        int imgRes = R.mipmap.ic_default_pigeon_marker;
        if (deviceModel == null) {
            mTvTitle.setText(getString(R.string.devices_qrcode, getString(R.string.device)));
            ImageLoadUtils.loadPortraitImage(getContext(), "", imgRes,
                    mIvPortrait);
        } else {
            ImageLoadUtils.loadPortraitImage(getContext(), deviceModel.getHeadPic(),
                    imgRes, mIvPortrait);
            mTvTitle.setText(getString(R.string.devices_qrcode, deviceModel.getDeviceName()));
            mTvContent.setText(getString(R.string.bind_id, deviceModel.getDeviceImei()));
            createQRCodeWithLogo(deviceModel.getDeviceImei(), null);
        }
//        DeviceModel deviceModel = getDevice();
//        DeviceInfoModel infoModel = null;
//        UserModel userModel = getUserModel();
//        if (userModel != null && deviceModel != null) {
//            OperatorGroup operatorGroup = OperatorGroup.clause(OperatorGroup.clause()
//                    .and(LocationModel_Table.u_id.eq(userModel.getU_id()))
//                    .and(LocationModel_Table.imei.eq(deviceModel.getImei())));
//            infoModel = SQLite.select().from(DeviceInfoModel.class)
//                    .where(operatorGroup)
//                    .querySingle();
//        }
//        int imgRes;
//        if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 0)
//            imgRes = R.mipmap.ic_device_portrait;
//        else
//            imgRes = R.mipmap.ic_name_type_tenth;
//        if (infoModel == null)
//            ImageLoadUtils.loadPortraitImage(getContext(), "", imgRes,
//                    mIvPortrait);
//        else
//            ImageLoadUtils.loadPortraitImage(getContext(), infoModel.getHead(),
//                    imgRes, mIvPortrait);
//        if (deviceModel == null || TextUtils.isEmpty(deviceModel.getActiveCode())) {
//            if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 0)
//                mTvTitle.setText(getString(R.string.devices_qrcode, getString(R.string.baby)));
//            else
//                mTvTitle.setText(getString(R.string.devices_qrcode, getString(R.string.device)));
//            mTvContent.setText(getString(R.string.bind_id, ""));
//            XToastUtils.toast(R.string.no_active_code_prompt);
//            popToBack();
//        } else {
//            if (infoModel == null || TextUtils.isEmpty(infoModel.getNickname())) {
//                if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 0)
//                    mTvTitle.setText(getString(R.string.devices_qrcode, getString(R.string.baby)));
//                else
//                    mTvTitle.setText(getString(R.string.devices_qrcode,
//                            getString(R.string.device)));
//            } else
//                mTvTitle.setText(getString(R.string.devices_qrcode, infoModel.getNickname()));
//            mTvContent.setText(getString(R.string.bind_id, deviceModel.getActiveCode()));
//            createQRCodeWithLogo(deviceModel.getDeviceImei(), null);
//        }
    }

    /**
     * 生成简单的带logo的二维码
     *
     * @param imei imei
     * @param logo logo
     */
    @IOThread(ThreadType.Single)
    private void createQRCodeWithLogo(String imei, Bitmap logo) {
        showQRCode(XQRCode.createQRCodeWithLogo(imei, 480, 480, logo));
    }

    @MainThread
    private void showQRCode(Bitmap QRCode) {
        mIvQrcode.setImageBitmap(QRCode);
    }

    /**
     * 解绑
     */
    private void deleteDevice() {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork();
            return;
        }
//        UserModel userModel = getUserModel();
//        DeviceModel deviceModel = getDevice();
//        if (userModel != null && deviceModel != null)
//            CWRequestUtils.getInstance().deleteDevice(MainApplication.getInstance(),
//                    userModel.getToken(),
//                    deviceModel.getImei(), userModel.getU_id(), mHandler);
        AAAUserModel userModel = getTrackUserModel();
        AAADeviceModel deviceModel = getTrackDeviceModel();
        if (userModel != null && deviceModel != null) {
            showDialog();
            CarGpsRequestUtils.deleteDevice(deviceModel.getDeviceImei(), userModel, mHandler);
        }
    }

    @SingleClick
    @OnClick({R.id.unBindBtn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.unBindBtn: // 解绑
                if (type == 1) {
                    AAADeviceModel deviceModel = getTrackDeviceModel();

                    mMaterialDialog =
                            DialogUtils.customMaterialDialog(getContext(),
                                    mMaterialDialog,
                                    getString(R.string.unbind),
                                    String.format("%s%s", deviceModel.getDeviceName() + "(" + deviceModel.getDeviceImei() + ")", getString(R.string.unbind_prompt)),
                                    getString(R.string.confirm),
                                    getString(R.string.cancel), null,
                                    CWConstant.DIALOG_UNBIND, mHandler);
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
                    case CWConstant.REQUEST_URL_DELETE_DEVICE: // 解绑
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                XToastUtils.toast(getString(R.string.msg_unbind, ""));
                                userModel = getUserModel();
                                if (userModel != null) {
                                    requestBean =
                                            mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                    List<DeviceModel> devicelist = getDeviceList();
                                    for (int i = 0; i < devicelist.size(); i++) {
                                        DeviceModel deviceModel = devicelist.get(i);
                                        if (deviceModel.getImei().equals(requestBean.getImei())) {
                                            OperatorGroup operatorGroup =
                                                    OperatorGroup.clause(OperatorGroup.clause()
                                                            .and(DeviceModel_Table.u_id.eq(userModel.getU_id()))
                                                            .and(DeviceModel_Table.d_id.eq(deviceModel.getD_id())));
                                            SQLite.delete(DeviceModel.class).where(operatorGroup).execute();
                                            devicelist.remove(i);
                                            break;
                                        }
                                    }
                                    if (devicelist.size() == 0) {
                                        MainApplication.getInstance().setDeviceModel(null);
                                        EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_FINISH));
                                        ActivityUtils.startActivity(BindDeviceActivity.class);
                                    } else {
                                        MainApplication.getInstance().setDeviceModel(devicelist.get(0));
                                        userModel.setSelectImei(getDevice().getImei());
                                        userModel.save();
                                        EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_CHANGE_DEVICE));
                                        EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_BACK_TO_MAIN));
                                    }
                                }
                            } else
                                RequestToastUtils.toast(resultBean.getCode());
                        }
                        break;
                    case CWConstant.HANDLE_CONFIRM_ACTION: // 确认
                        switch (msg.arg1) {
                            case CWConstant.DIALOG_UNBIND: // 解除绑定
                                deleteDevice();
                                break;
                            default:
                                break;
                        }
                        break;
                    case TConstant.REQUEST_UNBIND_DEVICE:
                        if (msg.obj != null) {
                            try {
                                AAABaseResponseBean response = (AAABaseResponseBean) msg.obj;
                                if (response.getCode() == TConstant.RESPONSE_SUCCESS) {
                                    CarGpsRequestUtils.getDeviceList(getTrackUserModel(), null, mHandler);
                                } else {
                                    dismisDialog();
                                    showMessage(ErrorCode.getResId(response.getCode()));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                dismisDialog();
                            }
                        } else {
                            dismisDialog();
                            showMessage(R.string.request_unkonow_prompt);
                        }
                        break;
                    case TConstant.REQUEST_URL_GET_DEVICE_LIST:
                        dismisDialog();
                        if (msg.obj != null) {
                            try {
                                AAABaseResponseBean response = (AAABaseResponseBean) msg.obj;
                                if (response.getCode() == TConstant.RESPONSE_SUCCESS) {
                                    List<AAADeviceModel> deviceList = new ArrayList<>();
                                    List list = (ArrayList) response.getData();
                                    for (int i = 0; i < list.size(); i++) {
                                        deviceList.add(mGson.fromJson(mGson.toJson(list.get(i)), AAADeviceModel.class));
                                    }
                                    showMessage(R.string.unbind_device_success_tips);
                                    if (deviceList.size() == 0) {
                                        ActivityUtils.startActivity(BindDeviceActivity.class);
                                        mActivity.finish();
                                    } else {
                                        MainApplication.getInstance().setTrackDeviceList(deviceList);
                                        MainApplication.getInstance().setTrackDeviceModel(deviceList.get(0));
//                                        EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_CHANGE_DEVICE));
                                        EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_BACK_TO_MAIN));
                                    }
                                } else
                                    showMessage(ErrorCode.getResId(response.getCode()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            showMessage(R.string.request_unkonow_prompt);
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
    public void onDestroy() {
        DialogUtils.dismiss(mBottomSheet);
        super.onDestroy();
    }
}
