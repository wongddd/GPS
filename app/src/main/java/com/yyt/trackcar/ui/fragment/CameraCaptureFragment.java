package com.yyt.trackcar.ui.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xqrcode.XQRCode;
import com.xuexiang.xqrcode.camera.CameraManager;
import com.xuexiang.xqrcode.ui.CaptureFragment;
import com.xuexiang.xqrcode.util.QRCodeAnalyzeUtils;
import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xutil.app.IntentUtils;
import com.xuexiang.xutil.app.PathUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.MainApplication;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAABaseResponseBean;
import com.yyt.trackcar.bean.RequestBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.ui.widget.textview.QMUISpanTouchFixTextView;
import com.yyt.trackcar.ui.widget.textview.QMUITouchableSpan;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.DialogUtils;
import com.yyt.trackcar.utils.PermissionUtils;
import com.yyt.trackcar.utils.PictureSelectorUtils;
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
 * @ fileName:      CameraCaptureFragment
 * @ author:        QING
 * @ createTime:    2020-02-27 17:08
 * @ describe:      TODO 二维码扫描页面
 */
@Page(name = "CameraCapture", params = {CWConstant.TYPE})
public class CameraCaptureFragment extends BaseFragment {
    @BindView(R.id.ibFlashLight)
    ImageButton mIbFlashLight; // 闪光灯开关按钮
    @BindView(R.id.tvTryInputImei)
    QMUISpanTouchFixTextView mTvTryInputImei;
    private boolean mIsOpen; // 闪光灯是否打开
    @AutoWired
    int type; // 0.从未绑定界面 1.首页绑定新设备 2.首页绑定新设备 3.经销商经销设备
    private int userId = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_camera_capture;
    }

    @Override
    protected void initArgs() {
        XRouter.getInstance().inject(this);
        Bundle bundle = getArguments();
        if (bundle != null)
            userId = bundle.getInt(TConstant.USER_ID,0);
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(R.string.scan_qrcode_new);
        titleBar.addAction(new TitleBar.TextAction(getString(R.string.album)) {
            @Override
            public void performAction(View view) {
                PermissionUtils.checkAndRequestMorePermissions(mActivity, new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },
                        CWConstant.REQUEST_PERMISSION_STORAGE, new PermissionUtils
                                .PermissionRequestSuccessCallBack() {
                            @Override
                            public void onHasPermission() {
                                PictureSelectorUtils.selectQRImage(mActivity,
                                        CameraCaptureFragment.this);
//                                selectQRCode();
                            }
                        });
            }
        });
        return titleBar;
    }

    @Override
    protected void initViews() {
        mTvTryInputImei.setMovementMethodDefault();
        String text = getString(R.string.try_input_imei_prompt);
        String highlight = getString(R.string.try_input_imei);
        SpannableString sp = new SpannableString(text);
        int start = 0, end;
        int index;
        while ((index = text.indexOf(highlight, start)) > -1) {
            end = index + highlight.length();
            sp.setSpan(new QMUITouchableSpan(ContextCompat.getColor(mActivity,
                    R.color.colorTexNormal), ContextCompat.getColor(mActivity,
                    R.color.colorTextPressed),
                    ContextCompat.getColor(mActivity, R.color.transparent),
                    ContextCompat.getColor(mActivity, R.color.transparent)) {
                @Override
                public void onSpanClick(View widget) {
//                    Bundle bundle = new Bundle();
//                    bundle.putInt(CWConstant.TYPE, type);
//                    if (userId != 0)
//                        bundle.putInt(TConstant.USER_ID,userId);
//                    openNewPage(InputImeiFragment.class, bundle);
                    popToBack();
                }
            }, index, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            start = end;
        }
        mTvTryInputImei.setText(sp);
        PictureSelectorUtils.initRxPermissions(mActivity);
        PermissionUtils.checkAndRequestPermission(mActivity, Manifest.permission.CAMERA,
                CWConstant.REQUEST_PERMISSION_CAMERA, new PermissionUtils
                        .PermissionRequestSuccessCallBack() {
                    @Override
                    public void onHasPermission() {
                        initCaptureFragment();
                    }
                });
    }

    /**
     * 初始化扫描二维码布局
     */
    private void initCaptureFragment() {
        // 为二维码扫描界面设置定制化界面
        CaptureFragment captureFragment =
                XQRCode.getCaptureFragment(R.layout.layout_custom_camera, true, 3 * 1000);
        captureFragment.setAnalyzeCallback(analyzeCallback);
        captureFragment.setCameraInitCallBack(new CaptureFragment.CameraInitCallBack() {
            @Override
            public void callBack(Exception e) {
                if (e == null) {
                    mIsOpen = XQRCode.isFlashLightOpen();
                    mIbFlashLight.setSelected(mIsOpen);
                } else
                    mMaterialDialog = DialogUtils.customMaterialDialog(getContext(),
                            mMaterialDialog, getString(R.string.prompt),
                            getString(R.string.no_camera_permission_prompt),
                            getString(R.string.confirm));
            }
        });
        getChildFragmentManager().beginTransaction().replace(R.id.flContainer, captureFragment).commit();
    }

    /**
     * 选择二维码
     */
    private void selectQRCode() {
        startActivityForResult(IntentUtils.getPickIntentWithGallery(), CWConstant.REQUEST_IMAGE);
    }

    @SuppressLint("MissingPermission")
    private void getAnalyzeQRCodeResult(Uri uri) {
        XQRCode.analyzeQRCode(PathUtils.getFilePathByUri(getContext(), uri), analyzeCallback);
    }

    @SingleClick
    @OnClick({R.id.ibFlashLight, R.id.tvInputImei})
    public void onClick(View v) {
        Bundle bundle;
        switch (v.getId()) {
            case R.id.ibFlashLight: // 打开/关闭闪光灯
                try {
                    XQRCode.switchFlashLight(!mIsOpen);
                    mIsOpen = !mIsOpen;
                    mIbFlashLight.setSelected(mIsOpen);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    XToastUtils.toast(R.string.no_support_flash_light_prompt);
                }
                break;
            case R.id.tvInputImei: // 输入绑定号
//                bundle = new Bundle();
//                bundle.putInt(CWConstant.TYPE, type);
//                openNewPage(InputImeiFragment.class, bundle);
                popToBack();
                break;
            default:
                break;
        }
    }

    /**
     * 二维码解析回调函数
     */
    QRCodeAnalyzeUtils.AnalyzeCallback analyzeCallback = new QRCodeAnalyzeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            String activeCode;
            String url;
//            if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 1)
//                url = "https://www.lagenio.com/html/lagenioappdownload/?ElderlyWatch";
//            else if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 2)
//                url = "https://www.lagenio.com/html/lagenioappdownload/?RTOS";
//            else
//                url = "https://www.lagenio.com/html/lagenioappdownload/?";
            if (TextUtils.isEmpty(result))
                activeCode = result;
            else if (result.startsWith("https://www.lagenio" +
                    ".com/html/lagenioappdownload/?ElderlyWatch")) {
                url = "https://www.lagenio.com/html/lagenioappdownload/?ElderlyWatch";
                if (String.format("%s0000", url).equals(result)) {
                    XToastUtils.toast(R.string.scan_imei_none_prompt);
                    return;
                } else
                    activeCode = result.substring(url.length());
            } else if (result.startsWith("https://www.lagenio.com/html/lagenioappdownload/?RTOS")) {
                url = "https://www.lagenio.com/html/lagenioappdownload/?RTOS";
                if (String.format("%s0000", url).equals(result)) {
                    XToastUtils.toast(R.string.scan_imei_none_prompt);
                    return;
                } else
                    activeCode = result.substring(url.length());
            } else if (result.startsWith("https://www.lagenio.com/html/lagenioappdownload/?")) {
                url = "https://www.lagenio.com/html/lagenioappdownload/?";
                if (String.format("%s0000", url).equals(result)) {
                    XToastUtils.toast(R.string.scan_imei_none_prompt);
                    return;
                } else
                    activeCode = result.substring(url.length());
            } else
                activeCode = result;
            if (TextUtils.isEmpty(activeCode)
//                    || activeCode.length() != 10
//                    || !RegularUtils.isPwd(activeCode)
            )
                XToastUtils.toast(R.string.scan_imei_result_prompt);
            else {
                if (type == 3){
                    CarGpsRequestUtils.bindDeviceForDealer(getTrackUserModel(),activeCode,userId,mHandler);
                }else {
                    List<DeviceModel> deviceList = MainApplication.getInstance().getDeviceList();
                    for (DeviceModel model : deviceList) {
                        if (activeCode.equals(model.getActiveCode())) {
                            XToastUtils.toast(R.string.user_already_bind_prompt);
                            return;
                        }
                    }
                    Bundle bundle = new Bundle();
                    bundle.putInt(CWConstant.TYPE, type);
                    bundle.putString(CWConstant.IMEI, activeCode);
                    openNewPage(BindApplyFragmentNew.class, bundle);
//                CWRequestUtils.getInstance().getImeiBindUsers(mActivity, getUserModel()
//                .getToken(),
//                        result, mHandler);
                }
            }
        }

        @Override
        public void onAnalyzeFailed() {
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //选择系统图片并解析
        if (requestCode == CWConstant.REQUEST_IMAGE) {
            if (data != null) {
                Uri uri = data.getData();
                getAnalyzeQRCodeResult(uri);
            }
        } else if (requestCode == CWConstant.REQUEST_GALLREY) {
            if (resultCode == Activity.RESULT_OK) {
                List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                for (LocalMedia localMedia : selectList) {
                    String path = localMedia.getPath();
                    XQRCode.analyzeQRCode(path, analyzeCallback);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CWConstant.REQUEST_PERMISSION_CAMERA: // 摄像头权限
                initCaptureFragment();
                break;
            case CWConstant.REQUEST_PERMISSION_STORAGE: // 读写文件权限
                if (PermissionUtils.isPermissionRequestSuccess(grantResults))
                    selectQRCode();
                else
                    mMaterialDialog = DialogUtils.customMaterialDialog(getContext(),
                            mMaterialDialog, getString(R.string.prompt),
                            getString(R.string.no_gallery_permission_prompt),
                            getString(R.string.confirm));
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
                                }
                                bundle.putString(CWConstant.IMEI, requestBean.getImei());
                                openNewPage(BindApplyFragmentNew.class, bundle);
                            } else
                                RequestToastUtils.toast(resultBean.getCode());
                        }
                        break;
                    case TConstant.REQUEST_BIND_DEVICE_FOR_DEALER:
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.network_error_prompt);
                        else{
                            AAABaseResponseBean response = (AAABaseResponseBean) msg.obj;
                            if (response.getCode() == TConstant.RESPONSE_SUCCESS){
                                showMessage(R.string.bind_success);
                            }else{
                                XToastUtils.toast(R.string.network_error_prompt);
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
    public void onDestroyView() {
        //恢复设置
        CameraManager.FRAME_MARGIN_TOP = -1;
        super.onDestroyView();
    }
}
