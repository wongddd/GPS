/*
 * Copyright (C) 2019 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.yyt.trackcar.ui.base;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.xuexiang.xpage.base.XPageActivity;
import com.xuexiang.xpage.base.XPageFragment;
import com.xuexiang.xpage.core.CoreSwitchBean;
import com.xuexiang.xui.utils.StatusBarUtils;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.slideback.SlideBack;
import com.yyt.trackcar.MainApplication;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.PostMessage;
import com.yyt.trackcar.dbflow.AAADeviceModel;
import com.yyt.trackcar.dbflow.AAAUserModel;
import com.yyt.trackcar.dbflow.DeviceInfoModel;
import com.yyt.trackcar.dbflow.DeviceInfoModel_Table;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.DeviceSettingsModel;
import com.yyt.trackcar.dbflow.DeviceSettingsModel_Table;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.ui.dialog.LoadingDialog;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.DialogUtils;
import com.yyt.trackcar.utils.LanguageUtils;
import com.yyt.trackcar.utils.ServerUtils;
import com.yyt.trackcar.utils.SettingSPUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * 基础容器Activity
 *
 * @author XUE
 * @since 2019/3/22 11:21
 */
public class BaseActivity extends XPageActivity {
    protected Gson mGson = new Gson();
    protected MaterialDialog mMaterialDialog; // 对话框
    Unbinder mUnbinder;

    @Override
    protected void attachBaseContext(Context newBase) {
        String language = SettingSPUtils.getInstance().getString(CWConstant.LANGUAGE, "");
        //注入字体
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            super.attachBaseContext(ViewPumpContextWrapper.wrap(CalligraphyContextWrapper.wrap
                    (LanguageUtils.attachBaseContext(newBase, language))));
        else
            super.attachBaseContext(CalligraphyContextWrapper.wrap(LanguageUtils.attachBaseContext
                    (newBase, language)));
//        // 解决android api Q layout/abc_screen_simple出错
        //super.attachBaseContext(ViewPumpContextWrapper.wrap(CalligraphyContextWrapper.wrap
        // (LanguageUtils.attachBaseContext(newBase, language))));
    }

    /**
     * 是否支持侧滑返回
     */
    public static final String KEY_SUPPORT_SLIDE_BACK = "key_support_slide_back";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        XUI.initTheme(this);
        super.onCreate(savedInstanceState);
        // 注册订阅者
        EventBus.getDefault().register(this);
//        StatusBarUtils.setStatusBarLightMode(this);
        mUnbinder = ButterKnife.bind(this);

        // 侧滑回调
        if (isSupportSlideBack()) {
            SlideBack.with(this)
                    .haveScroll(true)
                    .callBack(this::popPage)
                    .register();
        }
//        ActivityManagerUtils.getInstance().pushActivity(this);
    }

    /**
     * @return 是否支持侧滑返回
     */
    protected boolean isSupportSlideBack() {
        CoreSwitchBean page = getIntent().getParcelableExtra(CoreSwitchBean.KEY_SWITCH_BEAN);
        return page == null || page.getBundle() == null || page.getBundle().getBoolean(KEY_SUPPORT_SLIDE_BACK, true);
    }

    /**
     * 打开fragment
     *
     * @param clazz          页面类
     * @param addToBackStack 是否添加到栈中
     * @return 打开的fragment对象
     */
    public <T extends XPageFragment> Fragment openPage(Class<T> clazz, boolean addToBackStack) {
        CoreSwitchBean page = new CoreSwitchBean(clazz)
                .setAddToBackStack(addToBackStack);
        return openPage(page);
    }

    /**
     * 打开fragment
     *
     * @return 打开的fragment对象
     */
    public <T extends XPageFragment> Fragment openNewPage(Class<T> clazz) {
        CoreSwitchBean page = new CoreSwitchBean(clazz)
                .setNewActivity(true);
        return openPage(page);
    }

    /**
     * 打开fragment
     *
     * @return 打开的fragment对象
     */
    public <T extends XPageFragment> Fragment openNewPage(Class<T> clazz, Bundle bundle) {
        CoreSwitchBean page = new CoreSwitchBean(clazz).setBundle(bundle)
                .setNewActivity(true);
        return openPage(page);
    }

    /**
     * 切换fragment
     *
     * @param clazz 页面类
     * @return 打开的fragment对象
     */
    public <T extends XPageFragment> Fragment switchPage(Class<T> clazz) {
        return openPage(clazz, false);
    }

//    /**
//     * 序列化对象
//     *
//     * @param object 对象
//     * @return 序列化
//     */
//    public String serializeObject(Object object) {
//        return XRouter.getInstance().navigation(SerializationService.class).object2Json(object);
//    }

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
     * 获取设备信息对象
     *
     * @return 设备信息对象
     */
    protected DeviceInfoModel getDeviceInfo() {
        DeviceInfoModel infoModel = null;
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null) {
            OperatorGroup operatorGroup = OperatorGroup.clause(OperatorGroup.clause()
                    .and(DeviceInfoModel_Table.u_id.eq(userModel.getU_id()))
                    .and(DeviceInfoModel_Table.imei.eq(deviceModel.getImei())));
            infoModel = SQLite.select().from(DeviceInfoModel.class)
                    .where(operatorGroup)
                    .querySingle();
        }
        if (infoModel == null) {
            infoModel = new DeviceInfoModel();
            if (userModel != null)
                infoModel.setU_id(userModel.getU_id());
            if (deviceModel != null)
                infoModel.setImei(deviceModel.getImei());
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

    /**
     * 处理传递的信息
     */
    protected void handlePostMessage(PostMessage event) {
        if (CWConstant.POST_MESSAGE_FINISH == event.getType() || CWConstant.POST_MESSAGE_BACK_TO_MAIN == event.getType())
            finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Fragment fragment = getActiveFragment();
        if (fragment != null)
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onRelease() {
        mUnbinder.unbind();
        if (isSupportSlideBack()) {
            SlideBack.unregister(this);
        }
        super.onRelease();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPostMessage(PostMessage event) {
        handlePostMessage(event);
    }

    @Override
    protected void onDestroy() {
        DialogUtils.dismiss(mMaterialDialog);
        // 注销订阅者
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    //========================================================================================
    protected Toolbar mToolbar; // 标题栏
    protected TextView mTitle; // 标题
    protected LoadingDialog mLoadingDialog; // 加载框
    boolean mStartedActivityFromFragment;


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
    protected AAADeviceModel getTrackDevice() {
        return MainApplication.getInstance().getTrackDeviceModel();
    }

    protected void showMessage(int resId){
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    protected void showMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 初始化标题栏
     */
    protected void initToolBar() {
        mToolbar = findViewById(R.id.toolbar);
        mTitle = findViewById(R.id.title);
    }

    /**
     * 初始化标题栏
     */
    protected void initToolBar(int title) {
        mToolbar = findViewById(R.id.toolbar);
        mTitle = findViewById(R.id.title);
        mTitle.setText(title);
    }

    /**
     * 初始化标题栏
     */
    protected void initToolBar(int title, int navIcon, View.OnClickListener mListener) {
        mToolbar = findViewById(R.id.toolbar);
        mTitle = findViewById(R.id.title);
        mTitle.setText(title);
        mToolbar.setNavigationIcon(navIcon);
        //设置导航按钮的点击事件
        mToolbar.setNavigationOnClickListener(mListener);
        //Slidr.attach(this);
    }

    /**
     * 初始化标题栏
     */
    protected void initToolBar(String title, int navIcon, View.OnClickListener mListener) {
        mToolbar = findViewById(R.id.toolbar);
        mTitle = findViewById(R.id.title);
        mTitle.setText(title);
        mToolbar.setNavigationIcon(navIcon);
        //设置导航按钮的点击事件
        mToolbar.setNavigationOnClickListener(mListener);
        //Slidr.attach(this);
    }


    /**
     * 初始化标题栏菜单
     */
    protected void initToolBarMenu(int menu, Toolbar.OnMenuItemClickListener
            menuItemClickListener) {
        mToolbar.getMenu().clear();
        //生成选项菜单
        mToolbar.inflateMenu(menu);
        //设置选项菜单的菜单项的点击事件
        mToolbar.setOnMenuItemClickListener(menuItemClickListener);
        //设置溢出菜单的icon，显示、隐藏溢出菜单弹出的窗口
        mToolbar.setOverflowIcon(ContextCompat.getDrawable(this, android.R.drawable.ic_menu_more));
    }


    /**
     * 显示对话框
     */
    protected void showDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(this, R.style.dialog_loading_style);
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
     * 页面跳转
     *
     * @param bundle 传递的数据
     * @param cls    跳转的页面
     */
    protected void startActivity(Bundle bundle, Class<?> cls) {
        Intent intent = new Intent(this, cls);
        intent.putExtras(bundle);
        if (intent.resolveActivity(this.getPackageManager()) != null)
            startActivity(intent);
    }

    /**
     * 获取返回值的页面跳转
     *
     * @param requestCode 请求编号
     * @param cls         跳转的页面
     */
    protected void startActivityForResult(int requestCode, Class<?> cls) {
        Intent intent = new Intent(this, cls);
        if (intent.resolveActivity(this.getPackageManager()) != null)
            startActivityForResult(intent, requestCode);
    }

    public void startActivityForResult(Intent intent, int requestCode) {
        if (!this.mStartedActivityFromFragment && requestCode != -1) {
            checkForValidRequestCode(requestCode);
        }
        super.startActivityForResult(intent, requestCode);
    }

    static void checkForValidRequestCode(int requestCode) {
        if ((requestCode & -65536) != 0) {
            throw new IllegalArgumentException("Can only use lower 16 bits for requestCode");
        }
    }

    /**
     * 页面跳转
     *
     * @param cls 跳转的页面
     */
    protected void startActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        if (intent.resolveActivity(this.getPackageManager()) != null)
            startActivity(intent);
    }

    /**
     * 防止按钮快速连续点击，默认间隔1000ms
     */
    protected boolean preventButtonFastReClick(Long stampTimeWhenClick) {
        long currentTimeMillis = System.currentTimeMillis();
        return (currentTimeMillis - stampTimeWhenClick) > 1000;
    }

}
