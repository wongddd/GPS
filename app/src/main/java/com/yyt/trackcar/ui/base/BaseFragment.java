package com.yyt.trackcar.ui.base;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.xuexiang.xpage.base.XPageActivity;
import com.xuexiang.xpage.base.XPageFragment;
import com.xuexiang.xpage.core.PageOption;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.utils.WidgetUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.actionbar.TitleUtils;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.progress.loading.IMessageLoader;
import com.yyt.trackcar.MainApplication;
import com.yyt.trackcar.R;
import com.yyt.trackcar.dbflow.AAADeviceModel;
import com.yyt.trackcar.dbflow.AAAUserModel;
import com.yyt.trackcar.dbflow.DeviceInfoModel;
import com.yyt.trackcar.dbflow.DeviceInfoModel_Table;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.DeviceSettingsModel;
import com.yyt.trackcar.dbflow.DeviceSettingsModel_Table;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.dbflow.UserSettingsModel;
import com.yyt.trackcar.dbflow.UserSettingsModel_Table;
import com.yyt.trackcar.ui.dialog.LoadingDialog;
import com.yyt.trackcar.utils.DialogUtils;
import com.yyt.trackcar.utils.ServerUtils;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;

/**
 * @author xuexiang
 * @since 2018/5/25 下午3:44
 */
public abstract class BaseFragment extends XPageFragment {
    @Nullable
    @BindView(R.id.toolbar)
    View mToolBarView; // toolbar容器
    protected Gson mGson = new Gson();
    protected MaterialDialog mMaterialDialog; // 对话框

    private IMessageLoader mIMessageLoader;

    private LoadingDialog mLoadingDialog;

    @Override
    protected void initPage() {
        initTitle();
        initViews();
        initListeners();
    }

    protected TitleBar initTitle() {
        View toolbarContainer;
        if (mToolBarView == null)
            toolbarContainer = getRootView();
        else
            toolbarContainer = mToolBarView;
        return TitleUtils.addTitleBarDynamic((ViewGroup) toolbarContainer, getPageTitle(),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popToBack();
                    }
                });
    }

    public IMessageLoader getMessageLoader() {
        if (mIMessageLoader == null) {
            mIMessageLoader = WidgetUtils.getMiniLoadingDialog(getContext());
        }
        return mIMessageLoader;
    }

    public IMessageLoader getMessageLoader(String message) {
        if (mIMessageLoader == null) {
            mIMessageLoader = WidgetUtils.getMiniLoadingDialog(getContext(), message);
        } else {
            mIMessageLoader.updateMessage(message);
        }
        return mIMessageLoader;
    }

    @Override
    protected void initListeners() {

    }

    @Override
    public void onDestroyView() {
//        KeyboardUtils.fixSoftInputLeaks(getContext());
        super.onDestroyView();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        //屏幕旋转时刷新一下title
        super.onConfigurationChanged(newConfig);
        ViewGroup root = (ViewGroup) getRootView();
        if (root.getChildAt(0) instanceof TitleBar) {
            root.removeViewAt(0);
            initTitle();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * 打开一个新的页面
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T extends XPageFragment> Fragment openNewPage(Class<T> clazz) {
        return new PageOption(clazz)
                .setNewActivity(true)
                .open(this);
    }

    /**
     * 打开一个新的页面
     *
     * @param clazzName
     * @param <T>
     * @return
     */
    public <T extends XPageFragment> Fragment openNewPage(String clazzName) {
        return new PageOption(clazzName)
                .setAnim(CoreAnim.slide)
                .setNewActivity(true)
                .open(this);
    }

    /**
     * 打开一个新的页面
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T extends XPageFragment> Fragment openNewPage(Class<T> clazz, @NonNull Class<?
            extends XPageActivity> containActivityClazz) {
        return new PageOption(clazz)
                .setNewActivity(true)
                .setContainActivityClazz(containActivityClazz)
                .open(this);
    }

    /**
     * 打开一个新的页面
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T extends XPageFragment> Fragment openNewPage(Class<T> clazz, String key,
                                                          Object value) {
        PageOption option = new PageOption(clazz).setNewActivity(true);
        return openPage(option, key, value);
    }

    /**
     * 打开一个新的页面
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T extends XPageFragment> Fragment openNewPage(Class<T> clazz, Bundle bundle) {
        return new PageOption(clazz)
                .setNewActivity(true)
                .setBundle(bundle)
                .open(this);
    }

    public Fragment openPage(PageOption option, String key, Object value) {
        if (value instanceof Integer) {
            option.putInt(key, (Integer) value);
        } else if (value instanceof String) {
            option.putString(key, (String) value);
        } else if (value instanceof Float) {
            option.putFloat(key, (Float) value);
        } else if (value instanceof Parcelable) {
            option.putParcelable(key, (Parcelable) value);
        } else if (value instanceof Serializable) {
            option.putSerializable(key, (Serializable) value);
        }
//        else {
//            option.putString(key,
//                    XRouter.getInstance().navigation(SerializationService.class).object2Json
//                    (value));
//        }
        return option.open(this);
    }

    /**
     * 打开一个新的页面
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T extends XPageFragment> Fragment openPage(Class<T> clazz, boolean addToBackStack,
                                                       String key, String value) {
        return new PageOption(clazz)
                .setAddToBackStack(addToBackStack)
                .putString(key, value)
                .open(this);
    }

    /**
     * 打开一个新的页面
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T extends XPageFragment> Fragment openPage(Class<T> clazz, String key, Object value) {
        return openPage(clazz, true, key, value);
    }

    /**
     * 打开一个新的页面
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T extends XPageFragment> Fragment openPage(Class<T> clazz, boolean addToBackStack,
                                                       String key, Object value) {
        PageOption option = new PageOption(clazz).setAddToBackStack(addToBackStack);
        return openPage(option, key, value);
    }

    /**
     * 打开一个新的页面
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T extends XPageFragment> Fragment openPage(Class<T> clazz, String key, String value) {
        return new PageOption(clazz)
                .putString(key, value)
                .open(this);
    }

    /**
     * 打开页面,需要结果返回
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T extends XPageFragment> Fragment openPageForResult(Class<T> clazz, String key,
                                                                Object value, int requestCode) {
        PageOption option = new PageOption(clazz).setRequestCode(requestCode);
        return openPage(option, key, value);
    }

    /**
     * 打开页面,需要结果返回
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T extends XPageFragment> Fragment openPageForResult(Class<T> clazz, String key,
                                                                String value, int requestCode) {
        return new PageOption(clazz)
                .setRequestCode(requestCode)
                .putString(key, value)
                .open(this);
    }

    /**
     * 打开页面,需要结果返回
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T extends XPageFragment> Fragment openNewPageForResult(Class<T> clazz, String key,
                                                                   String value, int requestCode) {
        return new PageOption(clazz)
                .setNewActivity(true)
                .setRequestCode(requestCode)
                .putString(key, value)
                .open(this);
    }

    /**
     * 打开页面,需要结果返回
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T extends XPageFragment> Fragment openNewPageForResult(Class<T> clazz,
                                                                   int requestCode) {
        return new PageOption(clazz)
                .setNewActivity(true)
                .setRequestCode(requestCode)
                .open(this);
    }

    /**
     * 打开一个新的页面
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T extends XPageFragment> Fragment openNewPageForResult(Class<T> clazz, Bundle bundle,
                                                                   int requestCode) {
        return new PageOption(clazz)
                .setNewActivity(true)
                .setBundle(bundle)
                .setRequestCode(requestCode)
                .open(this);
    }

    /**
     * 打开页面,需要结果返回
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T extends XPageFragment> Fragment openPageForResult(Class<T> clazz, int requestCode) {
        return new PageOption(clazz)
                .setRequestCode(requestCode)
                .open(this);
    }

    /**
     * 获取用户对象
     */
    protected UserModel getUserModel() {
        return MainApplication.getInstance().getUserModel();
    }

    /**
     * 获取设备列表
     */
    protected List<DeviceModel> getDeviceList() {
        return MainApplication.getInstance().getDeviceList();
    }

    /**
     * 获取设备
     */
    protected DeviceModel getDevice() {
        return MainApplication.getInstance().getDeviceModel();
    }

    /**
     * 获取用户信息对象
     *
     * @return 用户信息对象
     */
    protected UserSettingsModel getUserSettingsModel() {
        UserSettingsModel settingsModel = null;
        UserModel userModel = getUserModel();
        if (userModel != null) {
            settingsModel =
                    SQLite.select().from(UserSettingsModel.class)
                            .where(UserSettingsModel_Table.u_id.eq(userModel.getU_id()))
                            .querySingle();
            if (settingsModel == null) {
                settingsModel = new UserSettingsModel();
                settingsModel.setU_id(userModel.getU_id());
                settingsModel.setArriveHome(1);
                settingsModel.setSos(1);
                settingsModel.setLocation(1);
                settingsModel.setAddFriend(1);
                settingsModel.setStep(1);
                settingsModel.setUploadPhoto(1);
                settingsModel.setPhoneLog(1);
                settingsModel.setCost(1);
                settingsModel.setUpgrade(1);
                settingsModel.setFence(1);
                settingsModel.setPhoneVoice(1);
                settingsModel.setPhoneVibration(1);
            }
        }
        return settingsModel;
    }

    /**
     * 获取设备信息对象
     *
     * @return 设备信息对象
     */
    protected DeviceInfoModel getDeviceInfo() {
        DeviceInfoModel infoModel = null;
//        UserModel userModel = getUserModel();
//        DeviceModel deviceModel = getDevice();
//        if (userModel != null && deviceModel != null) {
//            OperatorGroup operatorGroup = OperatorGroup.clause(OperatorGroup.clause()
//                    .and(DeviceInfoModel_Table.u_id.eq(userModel.getU_id()))
//                    .and(DeviceInfoModel_Table.imei.eq(deviceModel.getImei())));
//            infoModel = SQLite.select().from(DeviceInfoModel.class)
//                    .where(operatorGroup)
//                    .querySingle();
//        }
//        if (infoModel == null) {
//            infoModel = new DeviceInfoModel();
//            if (userModel != null)
//                infoModel.setU_id(userModel.getU_id());
//            if (deviceModel != null)
//                infoModel.setImei(deviceModel.getImei());
//            infoModel.setBirday("");
//            infoModel.setFamilyNumber("");
//            infoModel.setWeight("60");
//            infoModel.setHeight("175");
//            infoModel.setHead("");
//            infoModel.setNickname("");
//            infoModel.setSchool_age("");
//            infoModel.setPhone("");
//            infoModel.setFamilyNumber("");
//            infoModel.setSchool_info("");
//            infoModel.setHome_info("");
//        }

        AAAUserModel userModel = getTrackUserModel();
        AAADeviceModel deviceModel = getTrackDeviceModel();
        if (userModel != null && deviceModel != null) {
            OperatorGroup operatorGroup = OperatorGroup.clause(OperatorGroup.clause()
                    .and(DeviceInfoModel_Table.u_id.eq(userModel.getUserId()))
                    .and(DeviceInfoModel_Table.imei.eq(deviceModel.getDeviceImei())));
            infoModel = SQLite.select().from(DeviceInfoModel.class)
                    .where(operatorGroup)
                    .querySingle();
        }
        if (infoModel == null) {
            infoModel = new DeviceInfoModel();
            if (userModel != null)
                infoModel.setU_id(userModel.getUserId());
            if (deviceModel != null)
                infoModel.setImei(deviceModel.getDeviceImei());
            infoModel.setBirday("");
            infoModel.setFamilyNumber("");
            infoModel.setWeight("60");
            infoModel.setHeight("175");
            infoModel.setHead("");
            infoModel.setNickname("");
            infoModel.setSchool_age("");
            infoModel.setPhone("");
            infoModel.setFamilyNumber("");
            infoModel.setSchool_info("");
            infoModel.setHome_info("");
        }

        return infoModel;
    }

    /**
     * 获取设备设置
     */
    protected DeviceSettingsModel getDeviceSettings() {
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        DeviceSettingsModel settingsModel = null;
        if (userModel != null && deviceModel != null) {
            OperatorGroup operatorGroup = OperatorGroup.clause(OperatorGroup.clause()
                    .and(DeviceSettingsModel_Table.u_id.eq(userModel.getU_id()))
                    .and(DeviceSettingsModel_Table.imei.eq(deviceModel.getImei())));
            settingsModel = SQLite.select().from(DeviceSettingsModel.class)
                    .where(operatorGroup)
                    .querySingle();
            if (settingsModel == null) {
                settingsModel = new DeviceSettingsModel();
                settingsModel.setU_id(userModel.getU_id());
                settingsModel.setImei(deviceModel.getImei());
                settingsModel.setLocationMode("0");
                settingsModel.setWifi("");
                settingsModel.setWifiType(0);
                settingsModel.setDisabledInClass("");
                settingsModel.setOther("5,0,0,06:00|22:00|1,20|0");
                settingsModel.setAutomaticAnswer("0");
                settingsModel.setLoss("#0");
                settingsModel.setDial_pad("0");
                settingsModel.setAlarm_clock("");
                settingsModel.setStep("8000");
                settingsModel.setDevicestep("0");
                settingsModel.setWebTraffic("0");
                settingsModel.setWifiStatus("0");
                settingsModel.setPhonebook("");
//                settingsModel.setWakeTime(0);
                settingsModel.save();
            }
        }
        return settingsModel;
    }

    /**
     * 设置IP
     */
    protected void saveDeviceIp(long userId, String imei, String ip) {
        if (!TextUtils.isEmpty(imei)) {
            OperatorGroup operatorGroup = OperatorGroup.clause(OperatorGroup.clause()
                    .and(DeviceSettingsModel_Table.u_id.eq(userId))
                    .and(DeviceSettingsModel_Table.imei.eq(imei)));
            DeviceSettingsModel settingsModel = SQLite.select().from(DeviceSettingsModel.class)
                    .where(operatorGroup)
                    .querySingle();
            if (settingsModel == null) {
                settingsModel = new DeviceSettingsModel();
                settingsModel.setU_id(userId);
                settingsModel.setImei(imei);
                settingsModel.setLocationMode("0");
                settingsModel.setWifi("");
                settingsModel.setWifiType(0);
                settingsModel.setDisabledInClass("");
                settingsModel.setOther("5,0,0,06:00|22:00|1,20|0");
                settingsModel.setAutomaticAnswer("0");
                settingsModel.setLoss("#0");
                settingsModel.setDial_pad("0");
                settingsModel.setAlarm_clock("");
                settingsModel.setStep("8000");
                settingsModel.setDevicestep("0");
                settingsModel.setWebTraffic("0");
                settingsModel.setWifiStatus("0");
                settingsModel.setPhonebook("");
            }
            settingsModel.setIp(ip);
            settingsModel.save();
        }
    }

    /**
     * 获取IP
     */
    protected String getIp() {
        DeviceSettingsModel settingsModel = getDeviceSettings();
        String ip;
        if (settingsModel == null || TextUtils.isEmpty(settingsModel.getIp()))
            ip = ServerUtils.getServerIp();
        else
            ip = settingsModel.getIp();
        return ip;
    }

    @Override
    public Context getContext() {
        Context context = super.getContext();
        if (context == null)
            context = mActivity;
        return context;
    }

    @Override
    public void onDestroy() {
        DialogUtils.dismiss(mMaterialDialog);
        super.onDestroy();
    }


    //=============================================================================================


    /**
     * 获取用户对象
     */
    protected AAAUserModel getTrackUserModel() {
        return MainApplication.getInstance().getTrackUserModel();
    }

    /**
     * 获取设备列表
     */
    protected List<AAADeviceModel> getTrackDeviceList() {
        return MainApplication.getInstance().getTrackDeviceList();
    }

    /**
     * 获取设备
     */
    protected AAADeviceModel getTrackDeviceModel() {
        return MainApplication.getInstance().getTrackDeviceModel();
    }

    /**
     * 显示信息
     *
     * @param strId 显示内容
     */
    protected void showMessage(int strId) {
        if (mActivity != null)
            Toast.makeText(mActivity, strId, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示信息
     *
     * @param str 显示内容
     */
    protected void showMessage(String str) {
        if (mActivity != null)
            Toast.makeText(mActivity, str, Toast.LENGTH_SHORT).show();
    }

    /**
     * 页面跳转
     *
     * @param bundle 传递的数据
     * @param cls    跳转的页面
     */
    protected void startActivity(Bundle bundle, Class<?> cls) {
        Intent intent = new Intent(getActivity(), cls);
        intent.putExtras(bundle);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null)
            startActivity(intent);
    }

    /**
     * 页面跳转
     *
     * @param cls 跳转的页面
     */
    protected void startActivity(Class<?> cls) {
        Intent intent = new Intent(getActivity(), cls);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null)
            startActivity(intent);
    }

    /**
     * 显示对话框
     */
    protected void showDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(getContext(), R.style.dialog_loading_style);
            mLoadingDialog.setCanceledOnTouchOutside(false);
        }
        if (mLoadingDialog != null && !mLoadingDialog.isShowing())
            mLoadingDialog.show();
    }

    /**
     * 关闭对话框
     */
    protected void dismisDialog() {
        DialogUtils.dialogDismiss(mLoadingDialog);
    }

    /**
     * 防止按钮快速连续点击，默认间隔1000ms
     */
    protected boolean preventButtonFastReClick(Long stampTimeWhenClick) {
        long currentTimeMillis = System.currentTimeMillis();
        return (currentTimeMillis - stampTimeWhenClick) > 1000;
    }

    /**
     * 防止按钮快速连续点击
     *
     * @param period 连续点击间隔
     */
    protected boolean preventButtonFastReClick(Long stampTimeWhenClick, long period) {
        long currentTimeMillis = System.currentTimeMillis();
        if ((currentTimeMillis - stampTimeWhenClick) > period) {
            stampTimeWhenClick = currentTimeMillis;
            return true;
        }
        return false;
    }
}
