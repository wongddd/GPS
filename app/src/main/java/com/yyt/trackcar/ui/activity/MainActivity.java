package com.yyt.trackcar.ui.activity;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.baoyz.actionsheet.ActionSheet;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.socks.library.KLog;
import com.xuexiang.xui.utils.DensityUtils;
import com.xuexiang.xui.widget.textview.badge.BadgeView;
import com.xuexiang.xutil.app.ActivityUtils;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.MainApplication;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.DeviceSysMsgBean;
import com.yyt.trackcar.bean.PostMessage;
import com.yyt.trackcar.bean.RequestBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.bean.UserBean;
import com.yyt.trackcar.dbflow.AAADeviceModel;
import com.yyt.trackcar.dbflow.AAADeviceModel_Table;
import com.yyt.trackcar.dbflow.AAAUserModel;
import com.yyt.trackcar.dbflow.AAAUserModel_Table;
import com.yyt.trackcar.dbflow.AppMsgModel;
import com.yyt.trackcar.dbflow.AppMsgModel_Table;
import com.yyt.trackcar.dbflow.DeviceInfoModel;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.DeviceModel_Table;
import com.yyt.trackcar.dbflow.DeviceSettingsModel;
import com.yyt.trackcar.dbflow.PortraitModel;
import com.yyt.trackcar.dbflow.PortraitModel_Table;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.dbflow.UserModel_Table;
import com.yyt.trackcar.ui.adapter.MainFragmentAdapter;
import com.yyt.trackcar.ui.base.BaseActivity;
import com.yyt.trackcar.ui.fragment.AAAMoreFragment;
import com.yyt.trackcar.ui.fragment.AAAPigeonFragment;
import com.yyt.trackcar.ui.fragment.BindMemberFragment;
import com.yyt.trackcar.ui.fragment.ContactsAddFragment;
import com.yyt.trackcar.ui.fragment.DeviceListFragment;
import com.yyt.trackcar.ui.fragment.DeviceListOldFragment;
import com.yyt.trackcar.ui.fragment.DeviceMessageFragment;
import com.yyt.trackcar.ui.fragment.HomeFragment;
import com.yyt.trackcar.ui.fragment.LocationFragment;
import com.yyt.trackcar.ui.fragment.ManagePigeonCompetitionFragment;
import com.yyt.trackcar.ui.fragment.MonitorAMapFragment;
import com.yyt.trackcar.ui.fragment.MonitorGMapFragment;
import com.yyt.trackcar.ui.fragment.MoreFragment;
import com.yyt.trackcar.ui.fragment.PigeonRaceBroadcastListFragment;
import com.yyt.trackcar.ui.fragment.TrackCircleFragment;
import com.yyt.trackcar.utils.ActivityManagerUtils;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CWRequestUtils;
import com.yyt.trackcar.utils.DataUtils;
import com.yyt.trackcar.utils.DialogUtils;
import com.yyt.trackcar.utils.PortraitUtils;
import com.yyt.trackcar.utils.RequestToastUtils;
import com.yyt.trackcar.utils.SettingSPUtils;
import com.yyt.trackcar.utils.TConstant;
import com.yyt.trackcar.utils.XToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.activity
 * @ fileName:      MainActivity
 * @ author:        QING
 * @ createTime:    2020-02-25 15:14
 * @ describe:      TODO 主页页面
 */
public class MainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener
        , ActionSheet.ActionSheetListener {
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.bottomNavigation)
    BottomNavigationViewEx mBottomNavigation; // 底部导航栏

    private MainFragmentAdapter mAdapter;
    private List<Fragment> mItemList = new ArrayList<>(); // 列表
    private BadgeView mChatBadge; // 语聊-未读
    private BadgeView mMoreBadge; // 更多-未读
    private boolean isFinish; // 是否结束
    private int deviceType = -1;
    private HashMap<Integer, Fragment> mBaseFragments = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFinish = false;
        initActivityManager();
        initUserModel();
        if (isFinish)
            return;
        initViews();
        initBottomNavigation();
        initListeners();
        isAllowed();
    }

    private void downloadImageAndSaveToLocation(String url) {
        Glide.with(this)
                .asBitmap()
                .load(url)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        saveImageToLocation(resource, url);
                    }
                });
    }

    private void saveImageToLocation(Bitmap bitmap, String url) {
        String saveImagePath = null;
//        Random random = new Random();
//        String imageFileName = "JPEG_" + "down" + random.nextInt(10) + ".jpg";
        String[] fileName = url.split(".com/");
        if (fileName.length == 1)
            return;
        String imageFileName = fileName[1];
//        File storageDir = new File(
//                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "ssss");
        // /storage/emulated/0/Android/data/com.yyt.trackcar/files/deviceHeadPic
        File storageDir = getExternalFilesDir(TConstant.DEVICE_HEAD_PORTRAIT);
//        File storageDir = getExternalCacheDir();
        boolean success = true;
        if (!storageDir.exists()) {
            success = storageDir.mkdir();
        }
        if (success) {
            File imageFile = new File(storageDir, imageFileName);
            saveImagePath = imageFile.getAbsolutePath();
            try {
                OutputStream fout = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout);
                fout.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Add the image to the system gallery
            galleryAddPic(saveImagePath);
            showMessage("Saved Image");
        }
    }

    private void galleryAddPic(String imagePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(imagePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }

    private void checkFileExist(String filename, String url) {
        File filePath = getExternalFilesDir(TConstant.DEVICE_HEAD_PORTRAIT);
        File files = new File(filePath.getAbsolutePath());
        File[] fileList = files.listFiles();
        if (fileList != null && fileList.length != 0) {
            boolean theSpecifyFileIsExist = false;
            for (File item :
                    fileList) {
                if (filename.equals(item.getName())) {
                    theSpecifyFileIsExist = true;
                    break;
                }
            }
            if (!theSpecifyFileIsExist)
                downloadImageAndSaveToLocation(url);
        } else {
            downloadImageAndSaveToLocation(url);
        }
    }

    private void initActivityManager() {
        ActivityManagerUtils.getInstance().popAllActivity();
        ActivityManagerUtils.getInstance().pushActivity(this);
//        CarGpsRequestUtils.getAllLastLocation(getTrackUserModel(),mHandler);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected boolean isSupportSlideBack() {
        return false;
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void initViews() {
        mAdapter = new MainFragmentAdapter(getSupportFragmentManager(), mItemList);
        updateDeviceType();
        mChatBadge.setBadgeGravity(Gravity.END | Gravity.TOP);
        mChatBadge.setGravityOffset(16, 4, true);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(mItemList.size() - 1);
        mMoreBadge = new BadgeView(this);
        mMoreBadge.bindTarget(mBottomNavigation.getBottomNavigationItemView(4));
        mMoreBadge.setBadgeGravity(Gravity.END | Gravity.TOP);
        mMoreBadge.setGravityOffset(16, 4, true);
//        mBottomNavigation.setItemIconTintList(null);
        SettingSPUtils.getInstance().putBoolean(CWConstant.DEVICE_SETTINGS, false);
        updateMoreUnread();
        getImeiBindUsers();
        getContacts();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getAppMsg();
            }
        }, 1500);
        Bundle bundle = super.getIntent().getExtras();
        if (bundle != null && "1".equals(bundle.getString(CWConstant.TYPE, ""))) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getBindDeviceList();
                }
            }, 4500);
        }
        //JCManager.getInstance().client.login(String.valueOf(getUserModel().getU_id()), "123",
        // null, null);
    }

    /**
     * 初始化监听器
     */
    private void initListeners() {
        mBottomNavigation.setOnNavigationItemSelectedListener(this);
    }

    /**
     * 初始化底部导航
     */
    private void initBottomNavigation() {
        mBottomNavigation.enableAnimation(false);
        mBottomNavigation.enableShiftingMode(false);
        mBottomNavigation.enableItemShiftingMode(false);
        mBottomNavigation.setSmallTextSize(10);
        mBottomNavigation.setLargeTextSize(10);
        int centerPosition = 2;
        // attention: you must ensure the center menu item title is empty
        // make icon bigger at centerPosition
        mBottomNavigation.setIconSizeAt(centerPosition, 40, 40);
        mBottomNavigation.setIconMarginTop(centerPosition, DensityUtils.dp2px(8));
    }

    /**
     * 初始化用户信息
     */
    private void initUserModel() {
        if (getTrackUserModel() == null) {
            long uID = SettingSPUtils.getInstance().getLong(CWConstant.U_ID, -1);
            if (uID >= 0) {
                AAAUserModel userModel =
                        SQLite.select().from(AAAUserModel.class)
                                .where(AAAUserModel_Table.userId.eq(uID))
                                .querySingle();
                if (userModel == null)
                    ActivityUtils.startActivity(LoginActivity.class);
                else {
                    List<AAADeviceModel> deviceList = SQLite.select().from(AAADeviceModel.class)
                            .where(AAADeviceModel_Table.userId.eq(uID))
                            .queryList();
                    String selectImei = userModel.getSelectDeviceId();
                    if (selectImei == null)
                        selectImei = "";
                    for (AAADeviceModel deviceModel : deviceList) {
                        if (selectImei.equals(deviceModel.getDeviceImei()))
                            MainApplication.getInstance().setTrackDeviceModel(deviceModel);
                    }
                    if (MainApplication.getInstance().getTrackDeviceModel() == null && deviceList.size() > 0) {
                        MainApplication.getInstance().setTrackDeviceModel(deviceList.get(0));
                        userModel.setSelectDeviceId(MainApplication.getInstance().getTrackDeviceModel().getDeviceImei());
                        userModel.save();
                    }
                    MainApplication.getInstance().setTrackUserModel(userModel);
                    MainApplication.getInstance().setTrackDeviceList(deviceList);
                    if (deviceList.size() == 0) {
                        isFinish = true;
                        EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_FINISH));
                        ActivityUtils.startActivity(BindDeviceActivity.class);
                        finish();
                    }
                }
            } else {
                isFinish = true;
                EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_FINISH));
                ActivityUtils.startActivity(LoginActivity.class);
                finish();
            }
        }
    }


    /**
     * 刷新设备类型
     */
    @SuppressLint("RestrictedApi")
    private void updateDeviceType() {
        int type = SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0);
        if (deviceType == type)
            return;
        deviceType = type;

        if (mChatBadge == null)
            mChatBadge = new BadgeView(this);
        else {
            ViewGroup viewGroup = (ViewGroup) mChatBadge.getParent();
            if (viewGroup != null)
                viewGroup.removeView(mChatBadge);
//            ((ViewGroup) mChatBadge.getParent()).removeView(mChatBadge);
        }
        mItemList.clear();

        if (deviceType == 1) {
            mItemList.add(createFragment(5));
            if (SettingSPUtils.getInstance().getInt(TConstant.MAP_TYPE, 0) == 0) {
//                mItemList.add(createFragment(6));
                mItemList.add(createFragment(8));
            } else {
//                mItemList.add(createFragment(7));
                mItemList.add(createFragment(9));
            }
            mItemList.add(createFragment(11));
            mItemList.add(createFragment(10));
            mChatBadge.bindTarget(mBottomNavigation.getBottomNavigationItemView(0));
        } else if (deviceType == 2) {
            mItemList.add(createFragment(5));
            if (SettingSPUtils.getInstance().getInt(TConstant.MAP_TYPE, 0) == 0) {
                mItemList.add(createFragment(8));
            } else {
                mItemList.add(createFragment(9));
            }
            mItemList.add(createFragment(11));
            mItemList.add(createFragment(10));
            mChatBadge.bindTarget(mBottomNavigation.getBottomNavigationItemView(0));
        } else {
            mItemList.add(createFragment(5));
            if (SettingSPUtils.getInstance().getInt(TConstant.MAP_TYPE, 0) == 0) {
                mItemList.add(createFragment(8));
            } else {
                mItemList.add(createFragment(9));
            }
            mItemList.add(createFragment(11));
            mItemList.add(createFragment(10));
//            menuItem = mBottomNavigation.getMenu().getItem(1);
//            menuItem.setTitle(R.string.monitor);
//            menuItem.setIcon(R.drawable.ic_menu_monitor);
//            menuItem = mBottomNavigation.getMenu().getItem(3);
//            menuItem.setTitle(R.string.pigeon_ring);
//            menuItem.setIcon(R.drawable.ic_menu_device);
            mChatBadge.bindTarget(mBottomNavigation.getBottomNavigationItemView(0));
        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 更新更多未读
     */
    private void updateMoreUnread() {
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        AppMsgModel appMsgModel = null;
        if (userModel != null && deviceModel != null) {
            OperatorGroup operatorGroup =
                    OperatorGroup.clause(OperatorGroup.clause()
                            .and(AppMsgModel_Table.u_id.eq(userModel.getU_id()))
                            .and(AppMsgModel_Table.imei.eq(deviceModel.getImei()))
                            .and(AppMsgModel_Table.type.eq(27)));
            appMsgModel =
                    SQLite.select().from(AppMsgModel.class).where(operatorGroup).querySingle();
        }
        if (appMsgModel == null)
            mMoreBadge.setBadgeNumber(0);
        else
            mMoreBadge.setBadgeText("");
    }

    /**
     * 显示添加联系人对话框
     */
    private void showAddContactDialog() {
        DeviceModel deviceModel = getDevice();
        DeviceSettingsModel settingsModel = getDeviceSettings();
        if ((settingsModel == null || TextUtils.isEmpty(settingsModel.getPhonebook()))) {
            mMaterialDialog = DialogUtils.customMaterialDialog(this,
                    mMaterialDialog, getString(R.string.prompt),
                    getString(R.string.bind_success_contacts_prompt),
                    getString(R.string.confirm), getString(R.string.cancel), null,
                    CWConstant.DIALOG_ADD_CONTACTS, mHandler);
        }
    }

    /**
     * 用户查询绑定设备列表
     */
    private void getBindDeviceList() {
        UserModel userModel = getUserModel();
        if (userModel != null) {
            int deviceType = SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_MODEL, 1);
            CWRequestUtils.getInstance().getBindDeviceList(this, userModel.getU_id(),
                    userModel.getToken(), deviceType, mHandler);
        }
    }

    /**
     * 获取APP系统通知消息
     */
    private void getAppMsg() {
        UserModel userModel = getUserModel();
        if (userModel != null)
            CWRequestUtils.getInstance().getAppMsg(this, userModel.getToken(), mHandler);
    }

    /**
     * 管理员同意某个用户绑定
     */
    private void adminAgreeBind(AppMsgModel appMsgBean) {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork(this);
            return;
        }
        UserModel userModel = getUserModel();
        if (userModel != null) {
            OperatorGroup operatorGroup =
                    OperatorGroup.clause(OperatorGroup.clause()
                            .and(AppMsgModel_Table.u_id.eq(userModel.getU_id()))
                            .and(AppMsgModel_Table.imei.eq(appMsgBean.getImei()))
                            .and(AppMsgModel_Table.send_id.eq(appMsgBean.getSend_id()))
                            .and(AppMsgModel_Table.type.eq(27)));
            SQLite.delete(AppMsgModel.class).where(operatorGroup).execute();
            DeviceModel deviceModel = getDevice();
            if (deviceModel != null && deviceModel.getImei().equals(appMsgBean.getImei()) &&
                    mBottomNavigation.getSelectedItemId() == R.id.navMore) {
                for (Fragment fragment : mItemList) {
                    if (fragment instanceof MoreFragment) {
                        ((MoreFragment) fragment).refreshBindMsg();
                        break;
                    }
                }
            } else
                updateMoreUnread();
            CWRequestUtils.getInstance().adminAgreeBind(this, userModel.getToken(),
                    String.valueOf(appMsgBean.getSend_id()), appMsgBean.getImei(), mHandler);
        }
    }

    /**
     * 管理员拒绝某个用户绑定
     */
    private void refuseBind(AppMsgModel appMsgBean) {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork(this);
            return;
        }
        UserModel userModel = getUserModel();
        if (userModel != null) {
            OperatorGroup operatorGroup =
                    OperatorGroup.clause(OperatorGroup.clause()
                            .and(AppMsgModel_Table.u_id.eq(userModel.getU_id()))
                            .and(AppMsgModel_Table.imei.eq(appMsgBean.getImei()))
                            .and(AppMsgModel_Table.send_id.eq(appMsgBean.getSend_id()))
                            .and(AppMsgModel_Table.type.eq(27)));
            SQLite.delete(AppMsgModel.class).where(operatorGroup).execute();
            DeviceModel deviceModel = getDevice();
            if (deviceModel != null && deviceModel.getImei().equals(appMsgBean.getImei()) &&
                    mBottomNavigation.getSelectedItemId() == R.id.navMore) {
                for (Fragment fragment : mItemList) {
                    if (fragment instanceof MoreFragment) {
                        ((MoreFragment) fragment).refreshBindMsg();
                        break;
                    }
                }
            } else
                updateMoreUnread();
            CWRequestUtils.getInstance().refuseBind(this, userModel.getToken(),
                    String.valueOf(appMsgBean.getSend_id()), appMsgBean.getImei(), mHandler);
        }
    }

    /**
     * 设备管理员查询  某个设备的绑定用户
     */
    private void getImeiBindUsers() {
        if (!NetworkUtils.isNetworkAvailable()) {
            return;
        }
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().getImeiBindUsers(this, userModel.getToken(),
                    deviceModel.getImei(), mHandler);
    }

    /**
     * 获取通讯录
     */
    private void getContacts() {
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().getContacts(this, getIp(),
                    userModel.getToken(), deviceModel.getD_id(), mHandler);
    }

    //================Navigation================//

    /**
     * 底部导航栏点击事件
     *
     * @param menuItem 菜单选项
     * @return 返回是否选中
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        final int navHome = R.id.navHome;
        final int navLocation = R.id.navLocation;
        final int navMenu = R.id.navMenu;
        final int navList = R.id.navList;
        final int navMore = R.id.navMore;
        for (Fragment fragment : mItemList) {
            if (fragment instanceof MonitorAMapFragment) {
                ((MonitorAMapFragment) fragment).onSwitchPage(false);
                break;
            } else if (fragment instanceof MonitorGMapFragment) {
                ((MonitorGMapFragment) fragment).onSwitchPage(false);
                break;
            }
        }
        switch (menuItem.getItemId()) {
            case navHome: {
                mViewPager.setCurrentItem(0, false);
                for (Fragment fragment : mItemList) {
                    if (fragment instanceof HomeFragment) {
                        ((HomeFragment) fragment).onSwitchToThisPage();
                        break;
                    }
                }
            }
            break;
            case navLocation: {
                mViewPager.setCurrentItem(1, false);
                for (Fragment fragment : mItemList) {
                    if (fragment instanceof MonitorAMapFragment) {
                        ((MonitorAMapFragment) fragment).onSwitchPage(true);
                        break;
                    } else if (fragment instanceof MonitorGMapFragment) {
                        ((MonitorGMapFragment) fragment).onSwitchPage(true);
                        break;
                    }
                }
            }
            break;
            case navMenu: {
                if (DataUtils.isAgent()) { // 经销商的弹窗显示内容
                    ActionSheet.createBuilder(this, this.getSupportFragmentManager())
                            .setCancelButtonTitle(getString(R.string.cancel))
                            .setOtherButtonTitles(getString(R.string.live_broadcast)
                                    , getString(R.string.track_playback)
                                    , getString(R.string.track_sharing_circle)
                                    , getString(R.string.manage_competition))
                            .setCancelableOnTouchOutside(true)
                            .setListener(this).show();
                } else if (deviceType == 2) { // 鸽子定位器的弹窗显示内容
                    ActionSheet.createBuilder(this, this.getSupportFragmentManager())
                            .setCancelButtonTitle(getString(R.string.cancel))
                            .setOtherButtonTitles(getString(R.string.live_broadcast)
                                    , getString(R.string.track_playback)
                                    , getString(R.string.track_sharing_circle))
                            .setCancelableOnTouchOutside(true)
                            .setListener(this).show();
                } else { // 即非经销商又非鸽子定位器的弹窗显示内容
                    ActionSheet.createBuilder(this, this.getSupportFragmentManager())
                            .setCancelButtonTitle(getString(R.string.cancel))
                            .setOtherButtonTitles(getString(R.string.track_playback)
                                    , getString(R.string.track_sharing_circle))
                            .setCancelableOnTouchOutside(true)
                            .setListener(this).show();
                }
            }
            return false;
            case navList:
                mViewPager.setCurrentItem(2, false);
                for (Fragment fragment : mItemList) {
                    if (fragment instanceof DeviceListFragment) {
                        ((DeviceListFragment) fragment).onSwitchToThisPage();
                        break;
                    }
                }
                break;
            case navMore:
                mViewPager.setCurrentItem(3, false);
                for (Fragment fragment : mItemList) {
                    if (fragment instanceof MoreFragment) {
                        ((MoreFragment) fragment).refreshData();
                        break;
                    }
                }
                break;
        }
        return true;
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
                    case CWConstant.REQUEST_URL_GET_APP_MSG: // 获取APP系统通知消息
                        if (msg.obj != null) {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                userModel = getUserModel();
                                if (resultBean.getMsgList() != null && userModel != null) {
                                    SQLite.delete(AppMsgModel.class).where(AppMsgModel_Table.u_id.eq(userModel.getU_id())).execute();
                                    DeviceModel deviceModel = getDevice();
                                    boolean isUpdate = false;
                                    for (int i = 0; i < resultBean.getMsgList().size(); i++) {
                                        AppMsgModel msgBean =
                                                mGson.fromJson(mGson.toJson(resultBean.getMsgList().get(i)), AppMsgModel.class);
                                        msgBean.setId(i);
                                        msgBean.setU_id(userModel.getU_id());
                                        msgBean.save();
                                        if (deviceModel != null && deviceModel.getImei().equals(msgBean.getImei()) && msgBean.getType() == 27)
                                            isUpdate = true;
                                        if (msgBean.getType() == 27)
                                            mMaterialDialog =
                                                    DialogUtils.customMaterialDialog(getTopActivity(),
                                                            mMaterialDialog,
                                                            getString(R.string.user_bind_device_prompt),
                                                            getString(R.string.manager_bind_content,
                                                                    msgBean.getRemark()),
                                                            getString(R.string.confirm),
                                                            getString(R.string.refuse),
                                                            getString(R.string.next_handle),
                                                            msgBean, CWConstant.DIALOG_MANAGER_BIND,
                                                            mHandler);
                                    }
                                    if (mBottomNavigation.getSelectedItemId() == R.id.navMore) {
                                        for (Fragment fragment : mItemList) {
                                            if (fragment instanceof MoreFragment) {
                                                ((MoreFragment) fragment).refreshBindMsg();
                                                break;
                                            }
                                        }
                                    } else if (isUpdate)
                                        mMoreBadge.setBadgeText("");
                                    else
                                        mMoreBadge.setBadgeNumber(0);
                                }
                            }
                        }
                        break;
                    case CWConstant.REQUEST_URL_ADMIN_AGREE_BIND: // 管理员同意某个用户绑定
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                XToastUtils.toast(R.string.manager_agree_success_prompt);
//                                requestBean =
//                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject
//                                        ()), AAATrackRequestBeanOldEdition.class);
//                                userModel = getUserModel();
//                                if (userModel != null) {
//                                    OperatorGroup operatorGroup =
//                                            OperatorGroup.clause(OperatorGroup.clause()
//                                                    .and(AppMsgModel_Table.u_id.eq(userModel
//                                                    .getU_id()))
//                                                    .and(AppMsgModel_Table.imei.eq(requestBean
//                                                    .getImei()))
//                                                    .and(AppMsgModel_Table.send_id.eq(Long
//                                                    .parseLong(requestBean.getSendId()))));
//                                    SQLite.delete(AppMsgModel.class).where(operatorGroup)
//                                    .execute();
//                                    AAADeviceModel deviceModel = getDevice();
//                                    if (deviceModel != null && deviceModel.getImei().equals
//                                    (requestBean.getImei()) &&
//                                            mViewPager.getCurrentItem() == 3) {
//                                        MoreFragment moreFragment = (MoreFragment) mFragments[3];
//                                        moreFragment.refreshBindMsg();
//                                    } else
//                                        updateMoreUnread();
//                                }
                            } else
                                RequestToastUtils.toast(MainActivity.this, resultBean.getCode());
                        }
                        break;
                    case CWConstant.REQUEST_URL_REFUSE_BIND: // 管理员拒绝某个用户绑定
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                XToastUtils.toast(R.string.manager_refuse_success_prompt);
//                                requestBean =
//                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject
//                                        ()), AAATrackRequestBeanOldEdition.class);
//                                userModel = getUserModel();
//                                if (userModel != null) {
//                                    OperatorGroup operatorGroup =
//                                            OperatorGroup.clause(OperatorGroup.clause()
//                                                    .and(AppMsgModel_Table.u_id.eq(userModel
//                                                    .getU_id()))
//                                                    .and(AppMsgModel_Table.imei.eq(requestBean
//                                                    .getImei()))
//                                                    .and(AppMsgModel_Table.send_id.eq(Long
//                                                    .parseLong(requestBean.getSendId()))));
//                                    SQLite.delete(AppMsgModel.class).where(operatorGroup)
//                                    .execute();
//                                    AAADeviceModel deviceModel = getDevice();
//                                    if (deviceModel != null && deviceModel.getImei().equals
//                                    (requestBean.getImei()) &&
//                                            mViewPager.getCurrentItem() == 3) {
//                                        MoreFragment moreFragment = (MoreFragment) mFragments[3];
//                                        moreFragment.refreshBindMsg();
//                                    } else
//                                        updateMoreUnread();
//                                }
                            } else
                                RequestToastUtils.toast(MainActivity.this, resultBean.getCode());
                        }
                        break;
                    case CWConstant.REQUEST_URL_GET_BIND_DEVICE_LIST: // 用户查询绑定设备列表
                        if (msg.obj != null) {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()),
                                                RequestBean.class);
                                userModel =
                                        SQLite.select().from(UserModel.class)
                                                .where(UserModel_Table.u_id.eq(requestBean.getU_id()))
                                                .querySingle();
                                if (userModel != null) {
                                    SQLite.delete(DeviceModel.class).where(DeviceModel_Table.u_id.eq(userModel.getU_id())).execute();
                                    String selectImei = userModel.getSelectImei();
                                    if (selectImei == null)
                                        selectImei = "";
                                    List<DeviceModel> deviceList = new ArrayList<>();
                                    boolean isFind = false;
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
                                            if (selectImei.equals(deviceModel.getImei())) {
                                                isFind = true;
                                                MainApplication.getInstance().setDeviceModel(deviceModel);
                                            }
                                        }
                                        if (!isFind && deviceList.size() > 0) {
                                            MainApplication.getInstance().setDeviceModel(deviceList.get(0));
                                            userModel.setSelectImei(getDevice().getImei());
                                            userModel.save();
                                            EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_CHANGE_DEVICE));
                                        }
                                    }
                                    MainApplication.getInstance().setUserModel(userModel);
                                    getDeviceList().clear();
                                    getDeviceList().addAll(deviceList);
                                    if (deviceList.size() == 0) {
                                        MainApplication.getInstance().setDeviceModel(null);
                                        EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_FINISH));
                                        ActivityUtils.startActivity(BindDeviceActivity.class);
                                    }
                                }
                            }
                        }
                        break;
                    case CWConstant.REQUEST_URL_GET_IMEI_BIND_USERS: // 设备管理员查询  某个设备的绑定用户
                        if (msg.obj != null) {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                List userList = resultBean.getUserList();
                                userModel = getUserModel();
                                DeviceModel deviceModel = getDevice();
                                if (deviceModel != null) {
                                    OperatorGroup oGroup =
                                            OperatorGroup.clause(OperatorGroup.clause()
                                                    .and(PortraitModel_Table.userId.notEq(deviceModel.getImei()))
                                                    .and(PortraitModel_Table.imei.eq(deviceModel.getImei())));
                                    SQLite.delete(PortraitModel.class).where(oGroup).execute();
                                }
                                if (userList == null || userList.size() == 0) {
                                    if (userModel != null) {
                                        requestBean =
                                                mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                        List<DeviceModel> deviceList = getDeviceList();
                                        for (int i = 0; i < deviceList.size(); i++) {
                                            DeviceModel model = deviceList.get(i);
                                            if (model.getImei().equals(requestBean.getImei())) {
                                                OperatorGroup operatorGroup =
                                                        OperatorGroup.clause(OperatorGroup.clause()
                                                                .and(DeviceModel_Table.u_id.eq(userModel.getU_id()))
                                                                .and(DeviceModel_Table.d_id.eq(model.getD_id())));
                                                SQLite.delete(DeviceModel.class).where(operatorGroup).execute();
                                                deviceList.remove(i);
                                                break;
                                            }
                                        }
                                        if (deviceList.size() == 0) {
                                            MainApplication.getInstance().setDeviceModel(null);
                                            EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_FINISH));
                                            ActivityUtils.startActivity(BindDeviceActivity.class);
                                        } else {
                                            if (deviceModel != null && deviceModel.getImei().equals(requestBean.getImei()))
                                                EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_BACK_TO_MAIN));
                                            MainApplication.getInstance().setDeviceModel(deviceList.get(0));
                                            userModel.setSelectImei(getDevice().getImei());
                                            userModel.save();
                                            EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_CHANGE_DEVICE));
                                        }
                                    }
                                } else {
                                    for (Object obj : userList) {
                                        UserBean userBean = mGson.fromJson(mGson.toJson(obj),
                                                UserBean.class);
                                        if (userBean.getStatus() != 0) {
                                            PortraitModel portraitModel = new PortraitModel();
                                            portraitModel.setImei(userBean.getImei());
                                            portraitModel.setUserId(String.valueOf(userBean.getUser_id()));
                                            portraitModel.setName(userBean.getName());
                                            portraitModel.setUrl(userBean.getUrl());
                                            portraitModel.save();
                                            PortraitUtils.getInstance().updatePortrait(portraitModel);
                                        }
                                        if (userModel != null &&
                                                userBean.getUser_id() == getUserModel().getU_id()) {
                                            int status = userBean.getStatus();
                                            if (userBean.getStatus() != 1)
                                                status = 0;
                                            boolean isSave = false;
                                            if (deviceModel != null && deviceModel.getImei().equals(userBean.getImei())) {
                                                deviceModel.setStatus(status);
                                                isSave = true;
                                                deviceModel.save();
                                            }
                                            for (DeviceModel model : getDeviceList()) {
                                                if (model.getImei().equals(userBean.getImei())) {
                                                    model.setStatus(status);
                                                    if (!isSave)
                                                        model.save();
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            } else if (resultBean.getCode() == CWConstant.NOT_RESULT) {
                                userModel = getUserModel();
                                DeviceModel deviceModel = getDevice();
                                if (deviceModel != null) {
                                    OperatorGroup oGroup =
                                            OperatorGroup.clause(OperatorGroup.clause()
                                                    .and(PortraitModel_Table.userId.notEq(deviceModel.getImei()))
                                                    .and(PortraitModel_Table.imei.eq(deviceModel.getImei())));
                                    SQLite.delete(PortraitModel.class).where(oGroup).execute();
                                }
                                if (userModel != null) {
                                    requestBean =
                                            mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                    List<DeviceModel> deviceList = getDeviceList();
                                    for (int i = 0; i < deviceList.size(); i++) {
                                        DeviceModel model = deviceList.get(i);
                                        if (model.getImei().equals(requestBean.getImei())) {
                                            OperatorGroup operatorGroup =
                                                    OperatorGroup.clause(OperatorGroup.clause()
                                                            .and(DeviceModel_Table.u_id.eq(userModel.getU_id()))
                                                            .and(DeviceModel_Table.d_id.eq(model.getD_id())));
                                            SQLite.delete(DeviceModel.class).where(operatorGroup).execute();
                                            deviceList.remove(i);
                                            break;
                                        }
                                    }
                                    if (deviceList.size() == 0) {
                                        MainApplication.getInstance().setDeviceModel(null);
                                        EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_FINISH));
                                        ActivityUtils.startActivity(BindDeviceActivity.class);
                                    } else {
                                        if (deviceModel != null && deviceModel.getImei().equals(requestBean.getImei()))
                                            EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_BACK_TO_MAIN));
                                        MainApplication.getInstance().setDeviceModel(deviceList.get(0));
                                        userModel.setSelectImei(getDevice().getImei());
                                        userModel.save();
                                        EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_CHANGE_DEVICE));
                                    }
                                }
                            }
                        }
                        break;
                    case CWConstant.REQUEST_URL_GET_CONTACTS: // 获取通讯录
                        if (msg.obj != null) {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                userModel = getUserModel();
                                DeviceModel deviceModel = getDevice();
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                RequestBean bean =
                                        mGson.fromJson(mGson.toJson(resultBean.getResultBean()),
                                                RequestBean.class);
                                if (userModel != null && deviceModel != null && deviceModel.getD_id() == requestBean.getD_id()) {
                                    DeviceSettingsModel settingsModel = getDeviceSettings();
                                    settingsModel.setPhonebook(bean.getPhonebook());
                                    settingsModel.save();
                                    showAddContactDialog();
                                }
                            }
                        }
                        break;
                    case CWConstant.REQUEST_URL_GET_WATCH_INFO: // 查看宝贝资料
                        if (msg.obj != null) {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                userModel = getUserModel();
                                if (userModel != null) {
                                    requestBean =
                                            mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                    DeviceInfoModel infoModel =
                                            mGson.fromJson(mGson.toJson(resultBean.getResultBean()), DeviceInfoModel.class);
                                    infoModel.setImei(requestBean.getImei());
                                    infoModel.setU_id(userModel.getU_id());
                                    infoModel.save();
                                    PortraitModel portraitModel = new PortraitModel();
                                    portraitModel.setImei(infoModel.getImei());
                                    portraitModel.setUserId(infoModel.getImei());
                                    if (TextUtils.isEmpty(infoModel.getNickname())) {
                                        if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 0)
                                            portraitModel.setName(getString(R.string.baby));
                                        else
                                            portraitModel.setName(getString(R.string.device));
                                    } else
                                        portraitModel.setName(infoModel.getNickname());
                                    portraitModel.setUrl(infoModel.getHead());
                                    portraitModel.save();
                                    PortraitUtils.getInstance().updatePortrait(portraitModel);
                                }
                            }
                        }
                        break;
                    case CWConstant.HANDLE_CONFIRM_ACTION: // 确认
                        switch (msg.arg1) {
                            case CWConstant.DIALOG_ADD_CONTACTS: // 添加联系人
                                openNewPage(ContactsAddFragment.class);
                                break;
                            case CWConstant.DIALOG_MANAGER_BIND: // 请求绑定
                                AppMsgModel msgBean = (AppMsgModel) msg.obj;
                                adminAgreeBind(msgBean);
                                break;
                            default:
                                break;
                        }
                        break;
                    case CWConstant.HANDLE_NEUTRAL_ACTION: // 中间
                        switch (msg.arg1) {
                            case CWConstant.DIALOG_MANAGER_BIND: // 拒绝绑定
                                AppMsgModel msgBean = (AppMsgModel) msg.obj;
                                refuseBind(msgBean);
                                break;
                            default:
                                break;
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
    protected void handlePostMessage(PostMessage event) {
        if (CWConstant.POST_MESSAGE_FINISH == event.getType())
            finish();
        else if (CWConstant.POST_MESSAGE_TO_DEVICE_MESSAGE == event.getType()) {
            List<DeviceModel> deviceList = getDeviceList();
            UserModel userModel = getUserModel();
            DeviceModel deviceModel = getDevice();
            for (DeviceModel model : deviceList) {
                if (model.getImei().equals(event.getMessage()) && (deviceModel == null || !deviceModel.getImei().equals(model.getImei()))) {
                    EventBus.getDefault().post(CWConstant.POST_MESSAGE_BACK_TO_MAIN);
                    MainApplication.getInstance().setDeviceModel(model);
                    if (userModel != null) {
                        userModel.setSelectImei(model.getImei());
                        userModel.save();
                    }
                    EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_CHANGE_DEVICE));
                    break;
                }
            }
            openNewPage(DeviceMessageFragment.class);
        } else if (CWConstant.POST_MESSAGE_TO_LOCATION == event.getType()) {
            List<DeviceModel> deviceList = getDeviceList();
            UserModel userModel = getUserModel();
            DeviceModel deviceModel = getDevice();
            EventBus.getDefault().post(CWConstant.POST_MESSAGE_BACK_TO_MAIN);
            for (DeviceModel model : deviceList) {
                if (model.getImei().equals(event.getMessage()) && (deviceModel == null || !deviceModel.getImei().equals(model.getImei()))) {
                    MainApplication.getInstance().setDeviceModel(model);
                    if (userModel != null) {
                        userModel.setSelectImei(model.getImei());
                        userModel.save();
                    }
                    EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_CHANGE_DEVICE));
                    break;
                }
            }
            mBottomNavigation.setSelectedItemId(R.id.navLocation);
        } else if (CWConstant.POST_MESSAGE_TO_BIND_MEMBER == event.getType()) {
            List<DeviceModel> deviceList = getDeviceList();
            UserModel userModel = getUserModel();
            DeviceModel deviceModel = getDevice();
            for (DeviceModel model : deviceList) {
                if (model.getImei().equals(event.getMessage()) && (deviceModel == null || !deviceModel.getImei().equals(model.getImei()))) {
                    MainApplication.getInstance().setDeviceModel(model);
                    if (userModel != null) {
                        userModel.setSelectImei(model.getImei());
                        userModel.save();
                    }
                    EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_CHANGE_DEVICE));
                    break;
                }
            }
            openNewPage(BindMemberFragment.class);
        } else if (CWConstant.POST_MESSAGE_CHANGE_DEVICE == event.getType()) {
            getImeiBindUsers();
            updateMoreUnread();
        } else if (CWConstant.POST_MESSAGE_BIND_MEMBER_HANDLE == event.getType())
            DialogUtils.dismiss(mMaterialDialog);
        else if (CWConstant.POST_MESSAGE_TO_CHAT == event.getType()) {
            String imeiInfo = event.getMessage();
            if (TextUtils.isEmpty(imeiInfo))
                return;
            String[] array = imeiInfo.split(",");
            if (array.length < 2)
                return;
            String imei = imeiInfo.substring(2);
            List<DeviceModel> deviceList = getDeviceList();
            UserModel userModel = getUserModel();
            DeviceModel deviceModel = getDevice();
            for (DeviceModel model : deviceList) {
                if (model.getImei().equals(imei) && (deviceModel == null || !deviceModel.getImei().equals(model.getImei()))) {
                    EventBus.getDefault().post(CWConstant.POST_MESSAGE_BACK_TO_MAIN);
                    MainApplication.getInstance().setDeviceModel(model);
                    if (userModel != null) {
                        userModel.setSelectImei(model.getImei());
                        userModel.save();
                    }
                    deviceModel = model;
                    EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_CHANGE_DEVICE));
                    break;
                }
            }
            if (deviceType == 1)
                mBottomNavigation.setSelectedItemId(R.id.navList);
            else
                mBottomNavigation.setSelectedItemId(R.id.navHome);
        } else if (CWConstant.POST_MESSAGE_ADD_CONTACT == event.getType())
            showAddContactDialog();
        else if (CWConstant.POST_MESSAGE_CHANGE_DEVICE_TYPE == event.getType()) {
            updateDeviceType();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPostMsgBean(DeviceSysMsgBean event) {
        if (CWConstant.DEVICE_CHANGE_NAME == event.getType()) {
            UserModel userModel = getUserModel();
            DeviceModel deviceModel = null;
            for (DeviceModel model : getDeviceList()) {
                if (model.getImei().equals(event.getImei())) {
                    deviceModel = model;
                    break;
                }
            }
            if (userModel != null && deviceModel != null)
                CWRequestUtils.getInstance().getWatchInfo(this, userModel.getToken(),
                        deviceModel.getD_id(), deviceModel.getImei(), mHandler);
        } else if (CWConstant.AGREE_BIND == event.getType()) {
            UserModel userModel = getUserModel();
            if (userModel != null && String.valueOf(userModel.getU_id()).equals(event.getMsg())) {
                XToastUtils.toast(getString(R.string.manager_agree_user_bind_prompt));
                getBindDeviceList();
            }
        } else if (CWConstant.MANAGER_BIND == event.getType())
            getAppMsg();
        else if (CWConstant.REFUSE_BIND == event.getType()) {
            UserModel userModel = getUserModel();
            if (userModel != null && String.valueOf(userModel.getU_id()).equals(event.getMsg()))
                XToastUtils.toast(getString(R.string.manager_refuse_user_bind_prompt));
        } else if (CWConstant.MANAGER_UNBIND == event.getType()) {
            UserModel userModel = getUserModel();
            if (userModel != null && String.valueOf(userModel.getU_id()).equals(event.getMsg())) {
                DeviceModel deviceModel = getDevice();
                List<DeviceModel> deviceList = getDeviceList();
                boolean isFind = false;
                for (int i = 0; i < deviceList.size(); i++) {
                    DeviceModel model = deviceList.get(i);
                    if (model.getImei().equals(event.getImei())) {
                        OperatorGroup operatorGroup =
                                OperatorGroup.clause(OperatorGroup.clause()
                                        .and(DeviceModel_Table.u_id.eq(userModel.getU_id()))
                                        .and(DeviceModel_Table.d_id.eq(model.getD_id())));
                        SQLite.delete(DeviceModel.class).where(operatorGroup).execute();
                        deviceList.remove(i);
                        isFind = true;
                        break;
                    }
                }
                if (!isFind) {
                    return;
                }
                if (deviceList.size() == 0) {
                    MainApplication.getInstance().setDeviceModel(null);
                    EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_FINISH));
                    ActivityUtils.startActivity(BindDeviceActivity.class);
                } else {
                    if (deviceModel != null && deviceModel.getImei().equals(event.getImei()))
                        EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_BACK_TO_MAIN));
                    MainApplication.getInstance().setDeviceModel(deviceList.get(0));
                    userModel.setSelectImei(getDevice().getImei());
                    userModel.save();
                    EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_CHANGE_DEVICE));
                }
            }
        } else if (CWConstant.DELETE_ALL_USER_BIND == event.getType()) {
            UserModel userModel = getUserModel();
            if (userModel != null) {
                DeviceModel deviceModel = getDevice();
                List<DeviceModel> deviceList = getDeviceList();
                boolean isFind = false;
                for (int i = 0; i < deviceList.size(); i++) {
                    DeviceModel model = deviceList.get(i);
                    if (model.getImei().equals(event.getImei())) {
                        OperatorGroup operatorGroup =
                                OperatorGroup.clause(OperatorGroup.clause()
                                        .and(DeviceModel_Table.u_id.eq(userModel.getU_id()))
                                        .and(DeviceModel_Table.d_id.eq(model.getD_id())));
                        SQLite.delete(DeviceModel.class).where(operatorGroup).execute();
                        deviceList.remove(i);
                        isFind = true;
                        break;
                    }
                }
                if (!isFind) {
                    return;
                }
                if (deviceList.size() == 0) {
                    MainApplication.getInstance().setDeviceModel(null);
                    EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_FINISH));
                    ActivityUtils.startActivity(BindDeviceActivity.class);
                } else {
                    if (deviceModel != null && deviceModel.getImei().equals(event.getImei()))
                        EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_BACK_TO_MAIN));
                    MainApplication.getInstance().setDeviceModel(deviceList.get(0));
                    userModel.setSelectImei(getDevice().getImei());
                    userModel.save();
                    EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_CHANGE_DEVICE));
                }
            }
        } else if (CWConstant.TRANSFER_MANAGER == event.getType()) {
            UserModel userModel = getUserModel();
            if (userModel != null) {
                List<DeviceModel> deviceList = getDeviceList();
                boolean isSave = false;
                for (int i = 0; i < deviceList.size(); i++) {
                    DeviceModel model = deviceList.get(i);
                    if (model.getImei().equals(event.getImei())) {
                        if (String.valueOf(userModel.getU_id()).equals(event.getMsg()))
                            model.setStatus(1);
                        else
                            model.setStatus(0);
                        isSave = true;
                        model.save();
                        break;
                    }
                }
                DeviceModel deviceModel = getDevice();
                if (deviceModel != null && deviceModel.getImei().equals(event.getImei())) {
                    if (String.valueOf(userModel.getU_id()).equals(event.getMsg()))
                        deviceModel.setStatus(1);
                    else
                        deviceModel.setStatus(0);
                    if (!isSave)
                        deviceModel.save();
                }
            }
        } else if (CWConstant.OTHER_PLACE_LOGIN == event.getType()) {
            UserModel userModel = getUserModel();
            if (userModel != null && !userModel.getToken().equals(event.getMsg())
                    && String.valueOf(userModel.getU_id()).equals(event.getImei())) {
                SettingSPUtils.getInstance().putString(CWConstant.TOKEN, "");
                SettingSPUtils.getInstance().putLong(CWConstant.U_ID, -1);
                MainApplication.getInstance().setDeviceModel(null);
                MainApplication.getInstance().setUserModel(null);
                MainApplication.getInstance().getDeviceList().clear();
                EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_FINISH));
                ActivityUtils.startActivity(LoginActivity.class);
                RequestToastUtils.toast(this, CWConstant.U_TOKEN_ERR);
            }
        }
    }

    private Fragment createFragment(int pos) {
        Fragment baseFragment = mBaseFragments.get(pos);
        if (baseFragment == null) {
            switch (pos) {
                case 1:
                    baseFragment = new LocationFragment();
                    break;
                case 3:
                    baseFragment = new MoreFragment();
                    break;
                case 5:
                    baseFragment = new HomeFragment();  //HomePage
                    break;
                case 8:
                    baseFragment = new MonitorAMapFragment();  //MonitorFragment
                    break;
                case 9:
                    baseFragment = new MonitorGMapFragment();  //GoogleMapMonitorFragment
                    break;
                case 10:
                    baseFragment = new AAAMoreFragment();  //MoreStation
                    break;
                case 11:
                    baseFragment = new DeviceListFragment();
                    break;
                case 13:
                    baseFragment = new DeviceListOldFragment();
                    break;
                case 14:
                    baseFragment = new AAAPigeonFragment();
                    break;
                default:
                    baseFragment = new HomeFragment(); // Default Page is Chat
                    break;
            }
            mBaseFragments.put(pos, baseFragment);
        }
        return baseFragment;
    }

    private boolean isAllowed() {
        AppOpsManager ops = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        try {
            int op = 10021;
            Method method = ops.getClass().getMethod("checkOpNoThrow", new Class[]{int.class,
                    int.class, String.class});
            Integer result = (Integer) method.invoke(ops, op, Process.myUid(), getPackageName());
            return result == AppOpsManager.MODE_ALLOWED;

        } catch (Exception e) {
            if (BuildConfig.DEBUG)
                KLog.e("not support");
        }
        return false;
    }

    public BottomNavigationViewEx getBottomNavigation() {
        return mBottomNavigation;
    }

    public BadgeView getChatBadge() {
        return mChatBadge;
    }

    public BadgeView getMoreBadge() {
        return mMoreBadge;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        Bundle bundle;
        if (!DataUtils.isAgent() && deviceType != 2) {
            switch (index) {
                case 0:
//                    ActivityUtils.startActivity(HistoryActivity.class);
                    startActivity(WebActivity.class);
                    break;
                case 1:
                    openNewPage(TrackCircleFragment.class);
                    break;
            }
        } else {
            switch (index) {
                case 0:
                    bundle = new Bundle();
                    bundle.putInt(CWConstant.TYPE, 1);
                    openNewPage(PigeonRaceBroadcastListFragment.class);
                    break;
                case 1:
//                    ActivityUtils.startActivity(HistoryActivity.class);
                    startActivity(WebActivity.class);
                    break;
                case 2:
                    openNewPage(TrackCircleFragment.class);
                    break;
                case 3:
                    openNewPage(ManagePigeonCompetitionFragment.class);
                    break;
            }
        }
    }
}
