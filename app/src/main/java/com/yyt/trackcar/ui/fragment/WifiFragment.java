package com.yyt.trackcar.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.BaseItemBean;
import com.yyt.trackcar.bean.RequestBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.bean.SectionItem;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.DeviceSettingsModel;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.ui.adapter.WifiAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CWRequestUtils;
import com.yyt.trackcar.utils.PermissionUtils;
import com.yyt.trackcar.utils.RequestToastUtils;
import com.yyt.trackcar.utils.XToastUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      WifiFragment
 * @ author:        QING
 * @ createTime:    2020/3/12 11:34
 * @ describe:      TODO 选择wifi页面
 */
@Page(name = "Wifi", params = {CWConstant.TYPE, CWConstant.MODEL, CWConstant.CONTENT})
public class WifiFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout; // RefreshLayout
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView; // RecyclerView
    private WifiAdapter mAdapter; // 适配器
    private List<SectionItem> mItemList = new ArrayList<>(); // 列表
    @AutoWired
    String model; // wifi
    @AutoWired
    String content; // 选中WiFi
    @AutoWired
    int type; // 1.添加手机wifi
    private WifiManager mWifiManager;

    /**
     * 需要进行检测的权限数组
     */
    protected String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_refresh_layout;
    }

    @Override
    protected void initArgs() {
//        XRouter.getInstance().inject(this);
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(R.string.select_wifi);
        titleBar.addAction(new TitleBar.TextAction(getString(R.string.more)) {
            @Override
            public void performAction(View view) {
                Bundle bundle = new Bundle();
                if (TextUtils.isEmpty(content))
                    bundle.putInt(CWConstant.TYPE, 0);
                else
                    bundle.putInt(CWConstant.TYPE, 1);
                bundle.putString(CWConstant.MODEL, "");
                openNewPageForResult(WifiAddFragment.class, bundle, CWConstant.REQUEST_DEVICE_WIFI);
            }
        });
        return titleBar;
    }

    @Override
    protected void initViews() {
        mRefreshLayout.setBackgroundResource(R.color.colorPrimary);
        mRefreshLayout.setEnableLoadMore(false);
        initPermissions();
        mWifiManager =
                (WifiManager) mActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        DeviceSettingsModel settingsModel = getDeviceSettings();
        if (settingsModel != null && !TextUtils.isEmpty(settingsModel.getWifi()))
            content = settingsModel.getWifi();
        else
            content = "";
        openWifi();
        initItems();
        initAdapters();
        initRecyclerViews();
    }

    @Override
    protected void initListeners() {
        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(1000);
                mItemList.clear();
                initItems();
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 初始化列表信息
     */
    private void initItems() {
        String[] array = content.split(CWConstant.WIFI_SEPARATE);
        String wifiSelectWifiName;
        if (array.length == 0)
            wifiSelectWifiName = "";
        else
            wifiSelectWifiName = array[0];
        mItemList.add(new SectionItem(true, getString(R.string.mobile_search_wifi)));
        List<ScanResult> list = getWifiList();
        for (ScanResult result : list) {
            BaseItemBean itemBean;
            int security = getSecurity(result);
            if (security == 0)
                itemBean = new BaseItemBean(result.SSID, R.mipmap.ic_wifi);
            else
                itemBean = new BaseItemBean(result.SSID, R.mipmap.ic_wifi_lock);
            itemBean.setType(security);
            itemBean.setSelect(wifiSelectWifiName.equals(result.SSID));
            mItemList.add(new SectionItem(itemBean));
        }
        if (mItemList.size() == 2)
            mItemList.get(1).t.setBgDrawable(R.drawable.btn_custom_item_round_selector);
        else if (mItemList.size() > 2) {
            mItemList.get(1).t.setBgDrawable(R.drawable.btn_custom_top_radius);
            mItemList.get(mItemList.size() - 1).t.setBgDrawable(R.drawable.btn_custom_bottom_radius);
        }
        mItemList.add(new SectionItem(true, null));
    }

    /**
     * 初始化适配器
     */
    private void initAdapters() {
        mAdapter = new WifiAdapter(mItemList);
        mAdapter.setOnItemClickListener(this);
    }

    /**
     * 初始化ViewPager
     */
    private void initRecyclerViews() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 初始化权限
     */
    private void initPermissions() {
        PermissionUtils.checkAndRequestMorePermissions(mActivity, needPermissions,
                CWConstant.REQUEST_PERMISSION_MOBILE_LOCATION, new PermissionUtils
                        .PermissionRequestSuccessCallBack() {

                    @Override
                    public void onHasPermission() {

                    }
                });
    }

    /**
     * 打开wifi
     */
    private void openWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    /**
     * 获取wifi列表
     *
     * @return 列表
     */
    private List<ScanResult> getWifiList() {
        List<ScanResult> wifiList = new ArrayList<>();
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.startScan();
            List<ScanResult> scanWifiList = mWifiManager.getScanResults();
            if (scanWifiList != null && scanWifiList.size() > 0) {
                HashMap<String, Integer> signalStrength = new HashMap<String, Integer>();
                for (int i = 0; i < scanWifiList.size(); i++) {
                    ScanResult scanResult = scanWifiList.get(i);
                    if (!TextUtils.isEmpty(scanResult.SSID)) {
                        String key = scanResult.SSID + " " + scanResult.capabilities;
                        if (!signalStrength.containsKey(key)) {
                            signalStrength.put(key, i);
                            wifiList.add(scanResult);
                        }
                    }
                }
            }
        }
        return wifiList;
    }

    /**
     * 获取wifi连接方式
     *
     * @param result wifi
     * @return 类型
     */
    public int getSecurity(ScanResult result) {
        if (result.capabilities.contains("WEP")) {
            return 1;
        } else if (result.capabilities.contains("PSK")) {
            return 2;
        } else if (result.capabilities.contains("EAP")) {
            return 3;
        }
        return 0;
    }

    /**
     * 设置家庭wifi
     *
     * @param wifi wifi
     */
    public void setFamilyWifi(String wifi) {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork(getContext());
            return;
        }
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().setFamilyWifi(getContext(), getIp(),
                    userModel.getToken(), deviceModel.getImei(), deviceModel.getD_id(), wifi,
                    mHandler);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (position >= 0 && position < mItemList.size()) {
            BaseItemBean itemBean = mItemList.get(position).t;
            if (itemBean != null) {
                Bundle bundle = new Bundle();
                if (TextUtils.isEmpty(content))
                    bundle.putInt(CWConstant.TYPE, 0);
                else
                    bundle.putInt(CWConstant.TYPE, 1);
                bundle.putString(CWConstant.MODEL, itemBean.getTitle());
                openNewPageForResult(WifiAddFragment.class, bundle, CWConstant.REQUEST_DEVICE_WIFI);
//                switch (type) {
//                    case 1: // 添加手机wifi
//                        mSelectName = itemBean.getTitle();
//                        mMaterialDialog = TrackDialogUtils.customInputMaterialDialog(getContext(),
//                                mMaterialDialog, getString(R.string.device_wifi_title,
//                                        itemBean.getTitle()), null,
//                                getString(R.string.wifi_pwd_hint), null,
//                                InputType.TYPE_CLASS_TEXT
//                                , 20, 1, getString(R.string.confirm), getString(R.string.cancel),
//                                CWConstant.DIALOG_WIFI, mHandler);
//                        break;
//                    default:
//                        break;
//                }
            }
        }
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Intent data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == CWConstant.REQUEST_DEVICE_WIFI && data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                content = bundle.getString(CWConstant.MODEL);
                if (content == null)
                    content = "";
                String[] array = content.split(CWConstant.WIFI_SEPARATE);
                String wifiSelectWifiName;
                if (array.length == 0)
                    wifiSelectWifiName = "";
                else
                    wifiSelectWifiName = array[0];
                for (SectionItem item : mItemList) {
                    BaseItemBean itemBean = item.t;
                    if (itemBean != null)
                        itemBean.setSelect(wifiSelectWifiName.equals(itemBean.getTitle()));
                }
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CWConstant.REQUEST_PERMISSION_MOBILE_LOCATION: // 定位权限
                mItemList.clear();
                initItems();
                mAdapter.notifyDataSetChanged();
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
                DeviceModel deviceModel;
                switch (msg.what) {
                    case CWConstant.REQUEST_URL_SET_FAMILY_WIFI: // 设置家庭wifi
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
                            resultBean = (RequestResultBean) msg.obj;
                            if (!TextUtils.isEmpty(resultBean.getService_ip()) && !resultBean.getService_ip().equals(resultBean.getLast_online_ip())) {
                                userModel = getUserModel();
                                deviceModel = getDevice();
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                if (userModel != null && deviceModel != null && deviceModel.getD_id() == requestBean.getD_id()) {
                                    DeviceSettingsModel settingsModel = getDeviceSettings();
                                    settingsModel.setIp(resultBean.getLast_online_ip());
                                    settingsModel.save();
                                    if (!NetworkUtils.isNetworkAvailable()) {
                                        RequestToastUtils.toastNetwork(getContext());
                                        return false;
                                    }
                                    CWRequestUtils.getInstance().setFamilyWifi(getContext(),
                                            resultBean.getLast_online_ip(),
                                            requestBean.getToken(), requestBean.getImei(),
                                            requestBean.getD_id(), requestBean.getWifi(),
                                            mHandler);
                                }
                            } else if (resultBean.getCode() == CWConstant.SUCCESS || resultBean.getCode() == CWConstant.WAIT_ONLINE_UPDATE) {
                                if (resultBean.getCode() == CWConstant.WAIT_ONLINE_UPDATE)
                                    XToastUtils.toast(R.string.wait_online_update_prompt);
                                else
                                    XToastUtils.toast(R.string.send_success_prompt);
                                userModel = getUserModel();
                                deviceModel = getDevice();
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                if (userModel != null && deviceModel != null && deviceModel.getD_id() == requestBean.getD_id()) {
                                    DeviceSettingsModel settingsModel = getDeviceSettings();
                                    settingsModel.setWifi(requestBean.getWifi());
                                    settingsModel.setWifiType(0);
                                    settingsModel.save();
                                }
                                popToBack();
                            } else if (resultBean.getCode() == CWConstant.ERROR)
                                XToastUtils.toast(R.string.send_error_prompt);
                            else
                                RequestToastUtils.toast(getContext(), resultBean.getCode());
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
