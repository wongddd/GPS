package com.yyt.trackcar.ui.fragment;

import android.Manifest;
import android.app.Service;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageButton;

import com.socks.library.KLog;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.yyt.trackcar.MainApplication;
import com.yyt.trackcar.R;
import com.yyt.trackcar.dbflow.AAADeviceModel;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.BLEDataUtils;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.XToastUtils;

import java.util.List;

import butterknife.OnClick;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

@Page(name = "ScanQRCodeFragment", anim = CoreAnim.none)
public class ScanQuickResponseCodeFragment extends BaseFragment implements QRCodeView.Delegate {

    private static final int REQUEST_CODE_CAMERA = 999;
    private QRCodeView mQRCodeView;
    private ImageButton mFlashSwitch;

    /**
     * 0.从未绑定界面 1.首页绑定新设备 2.首页绑定新设备 3.经销商绑定设备
     */
    @AutoWired
    int type;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_scan_quick_response_code;
    }

    @Override
    protected void initArgs() {
        XRouter.getInstance().inject(this);
    }

    @Override
    protected void initViews() {
        mFlashSwitch = (ImageButton) findViewById(R.id.ibFlashLight);
        mQRCodeView = (ZXingView) findViewById(R.id.zxingview);
        mQRCodeView.setDelegate(this);
        requestPermissions(new String[]{android.Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(R.string.scan_qrcode_new);
        return titleBar;
    }

    @Override
    public void onResume() {
        super.onResume();
        mFlashSwitch.setSelected(false); // 默认关闭照相机灯光
        mQRCodeView.startCamera();
        mQRCodeView.startSpotAndShowRect();
        // mQRCodeView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT); // 使用前置摄像头
    }

    @Override
    public void onPause() {
        mQRCodeView.stopCamera();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        mQRCodeView.onDestroy();
        super.onDestroyView();
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        result = result.trim();
//        KLog.i("result:" + result);
//        String regex = "\\d{15,17}";
//        Pattern pattern = Pattern.compile(regex);
        vibrate();//震动
        if (result.length() >= 6) {
            if (result.length() == 17) {
                String[] array = result.split(":");
                if (array.length == 6) {
                    String macAddress = BLEDataUtils.convertMacAddress(result);
                    if (macAddress.length() == 18) {
                        result = macAddress;
                    }
                }
            }
            List<AAADeviceModel> deviceList =
                    MainApplication.getInstance().getTrackDeviceList();
            for (AAADeviceModel model : deviceList) {
                if (result.equals(model.getDeviceImei())) {
                    XToastUtils.toast(R.string.user_already_bind_prompt);
                    mQRCodeView.startSpotAndShowRect();
                    return;
                }
            }
            Bundle bundle = new Bundle();
            bundle.putInt(CWConstant.TYPE, type);
            bundle.putString(CWConstant.IMEI, result);
            openNewPage(BindApplyFragmentNew.class, bundle);
        } else {
            mQRCodeView.startSpotAndShowRect();
        }
//        if (!TextUtils.isEmpty(result)) {
//            Matcher matcher = pattern.matcher(result);
//            if (matcher.matches()) { // 扫描结果匹配IMEI号规则
//
//                Bundle bundle = new Bundle();
//                bundle.putInt(CWConstant.TYPE, type);
//                bundle.putString(CWConstant.IMEI, result);
//                openNewPage(BindApplyFragmentNew.class, bundle);
//            } else {
//                showMessage(getString(R.string.invalid_imei_number));
//            }
//        } else {
//            showMessage(getString(R.string.invalid_imei_number));
//        }
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        KLog.e(getString(R.string.camera_permission_is_denied));
//        ActivityCompat.requestPermissions(mActivity,
//                new String[]{android.Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
    }

    @OnClick({R.id.start_spot, R.id.stop_spot, R.id.open_flashlight, R.id.close_flashlight,
            R.id.ibFlashLight})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_spot:
                mQRCodeView.startSpotAndShowRect();
                break;
            case R.id.stop_spot:
                mQRCodeView.stopSpotAndHiddenRect();
                break;
            case R.id.open_flashlight:
                mQRCodeView.openFlashlight();
                break;
            case R.id.close_flashlight:
                mQRCodeView.closeFlashlight();
                break;
            case R.id.ibFlashLight:
                if (mFlashSwitch.isSelected()) {
                    mQRCodeView.closeFlashlight();
                } else {
                    mQRCodeView.openFlashlight();
                }
                mFlashSwitch.setSelected(!mFlashSwitch.isSelected());
                break;
        }
    }

    /**
     * 权限请求回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == REQUEST_CODE_CAMERA) {
            int PermissionState =
                    ContextCompat.checkSelfPermission(mActivity.getApplicationContext(),
                            Manifest.permission.CAMERA);
            if (PermissionState == PackageManager.PERMISSION_GRANTED) {
                mQRCodeView.startCamera();
                mQRCodeView.startSpotAndShowRect();
            } else if (PermissionState == PackageManager.PERMISSION_DENIED) {
                showMessage(R.string.camera_permission_is_denied);
            }
        }
    }

    /**
     * 手机震动
     */
    private void vibrate() {
        Vibrator vibrator = (Vibrator) mActivity.getSystemService(Service.VIBRATOR_SERVICE);
        vibrator.vibrate(100);

        KLog.e("vibrate");
    }
}
