package com.yyt.trackcar.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.BaseItemBean;
import com.yyt.trackcar.bean.DeviceSysMsgBean;
import com.yyt.trackcar.bean.RequestBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.DeviceSettingsModel;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.ui.adapter.CustomSubSelectorAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CWRequestUtils;
import com.yyt.trackcar.utils.RequestToastUtils;
import com.yyt.trackcar.utils.XToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      DeviceWifiFragment
 * @ author:        QING
 * @ createTime:    2020/3/13 16:24
 * @ describe:      TODO 手表WIFI页面
 */
@Page(name = "DeviceWifi")
public class DeviceWifiFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.switchBtn)
    Button mSwitchBtn; // 开启/关闭按钮
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView; // RecyclerView
    private CustomSubSelectorAdapter mAdapter; // 适配器
    private List<BaseItemBean> mItemList = new ArrayList<>(); // 列表
    private TitleBar mTitleBar; // 标题栏
    private boolean mIsEdit; // 是否编辑

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 注册订阅者
        EventBus.getDefault().register(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycler_view_btn;
    }

    @Override
    protected TitleBar initTitle() {
        mTitleBar = super.initTitle();
        mTitleBar.setTitle(R.string.device_wifi);
        mTitleBar.addAction(getAction());
        return mTitleBar;
    }

    @Override
    protected void initViews() {
        initAdapters();
        initRecyclerViews();
        initHeaderView();
        initFooterView();
        getFamilyWifi();
    }

    /**
     * 初始化列表信息
     */
    private void initItems() {
        DeviceSettingsModel settingsModel = getDeviceSettings();
        if (settingsModel != null && !TextUtils.isEmpty(settingsModel.getWifi()))
            addItem(settingsModel.getWifi(), settingsModel.getWifiType());
    }

    /**
     * 初始化适配器
     */
    private void initAdapters() {
        mAdapter = new CustomSubSelectorAdapter(mItemList);
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
     * 初始化头布局
     */
    private void initHeaderView() {
        View headerView = getLayoutInflater().inflate(R.layout.header_view_info,
                mRecyclerView, false);
        ImageView ivBg = headerView.findViewById(R.id.ivBg);
        TextView tvTitle = headerView.findViewById(R.id.tvTitle);
        TextView tvContent = headerView.findViewById(R.id.tvContent);
        ivBg.setImageResource(R.mipmap.bg_device_wifi);
        tvTitle.setText(R.string.add_wifi_title);
        tvContent.setText(R.string.add_wifi_content);
        mAdapter.addHeaderView(headerView);
    }

    /**
     * 初始化脚布局
     */
    private void initFooterView() {
        View footerView = getLayoutInflater().inflate(R.layout.item_space_section, mRecyclerView,
                false);
        mAdapter.addFooterView(footerView);
    }

    /**
     * 初始化按钮状态
     */
    private void initSwitchBtn() {
        if (mItemList.size() == 0)
            mSwitchBtn.setText(R.string.add_wifi);
        else
            mSwitchBtn.setText(R.string.edit_wifi);
    }

    /**
     * 添加选项
     */
    private void addItem(String wifiString, int status) {
        String[] array = wifiString.split(CWConstant.WIFI_SEPARATE);
        if (array.length >= 2 && !TextUtils.isEmpty(array[0])) {
            BaseItemBean itemBean = new BaseItemBean(array[0], null);
            itemBean.setGroup(wifiString);
            if (status == 1)
                itemBean.setContent(getString(R.string.verify_success));
            else if (status == 0)
                itemBean.setContent(getString(R.string.verify_wait));
            else
                itemBean.setContent(getString(R.string.verify_error));
            itemBean.setTitleColor(R.color.mediumpurple);
            itemBean.setBgDrawable(R.drawable.btn_custom_item_round_selector);
            mItemList.add(itemBean);
        }
    }

    /**
     * 获取家庭wifi
     */
    public void getFamilyWifi() {
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().getFamilyWifi(getContext(), getIp(),
                    userModel.getToken(), deviceModel.getD_id(), mHandler);
    }

    /**
     * 设置家庭wifi
     *
     * @param wifi wifi
     */
    public void setFamilyWifi(String wifi) {
        if(!NetworkUtils.isNetworkAvailable()){
            RequestToastUtils.toastNetwork();
            return;
        }
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().setFamilyWifi(getContext(), getIp(),
                    userModel.getToken(), deviceModel.getImei(), deviceModel.getD_id(), wifi,
                    mHandler);
    }

    /**
     * 初始化标题栏动作
     *
     * @return 动作
     */
    private TitleBar.Action getAction() {
        TitleBar.Action action;
        if (mIsEdit)
            action = new TitleBar.TextAction(getString(R.string.cancel)) {
                @Override
                public void performAction(View view) {
                    mIsEdit = false;
                    mAdapter.setEdit(mIsEdit);
                    mAdapter.notifyDataSetChanged();
                    mTitleBar.removeAllActions();
                    mTitleBar.addAction(getAction());
                    mSwitchBtn.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
                    mSwitchBtn.setBackgroundResource(R.drawable.btn_custom_selector);
                    initSwitchBtn();
                }
            };
        else
            action = new TitleBar.TextAction(getString(R.string.edit)) {
                @Override
                public void performAction(View view) {
                    mIsEdit = true;
                    for (BaseItemBean itemBean : mItemList) {
                        itemBean.setSelect(false);
                    }
                    mAdapter.setEdit(mIsEdit);
                    mAdapter.notifyDataSetChanged();
                    mTitleBar.removeAllActions();
                    mTitleBar.addAction(getAction());
                    mSwitchBtn.setTextColor(ContextCompat.getColor(mActivity, R.color.red));
                    mSwitchBtn.setBackgroundResource(R.drawable.btn_custom_item_round_selector);
                    mSwitchBtn.setText(R.string.del);
                }
            };
        return action;
    }

    @SingleClick
    @OnClick({R.id.switchBtn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switchBtn:
                if (mIsEdit) { // 删除
                    for (int i = mItemList.size() - 1; i >= 0; i--) {
                        BaseItemBean itemBean = mItemList.get(i);
                        if (itemBean != null && itemBean.isSelect()) {
                            setFamilyWifi(CWConstant.WIFI_SEPARATE);
                            mItemList.remove(i);
                        }
                    }
                    mIsEdit = false;
                    mAdapter.setEdit(mIsEdit);
                    mAdapter.notifyDataSetChanged();
                    mTitleBar.removeAllActions();
                    mTitleBar.addAction(getAction());
                    mSwitchBtn.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
                    mSwitchBtn.setBackgroundResource(R.drawable.btn_custom_selector);
                    initSwitchBtn();
                } else  // 添加
                    openNewPage(WifiFragment.class);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (position >= 0 && position < mItemList.size()) {
            BaseItemBean itemBean = mItemList.get(position);
            if (itemBean != null) {
                if (mIsEdit) {
                    itemBean.setSelect(!itemBean.isSelect());
                    mAdapter.notifyDataSetChanged();
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putInt(CWConstant.TYPE, 1);
                    bundle.putString(CWConstant.MODEL, itemBean.getGroup());
                    openNewPage(WifiAddFragment.class, bundle);
                }
            }
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
                    case CWConstant.REQUEST_URL_GET_FAMILY_WIFI: // 获取家庭wifi
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
                                    settingsModel.setWifi(bean.getWifi());
                                    settingsModel.setWifiType(bean.getStatus());
                                    settingsModel.save();
                                }
                                mItemList.clear();
                                addItem(bean.getWifi(), bean.getStatus());
                                mAdapter.notifyDataSetChanged();
                                initSwitchBtn();
                            }
                        }
                        break;
                    case CWConstant.REQUEST_URL_SET_FAMILY_WIFI: // 设置家庭wifi
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
                            resultBean = (RequestResultBean) msg.obj;
                            if (!TextUtils.isEmpty(resultBean.getService_ip()) && !resultBean.getService_ip().equals(resultBean.getLast_online_ip())) {
                                userModel = getUserModel();
                                DeviceModel deviceModel = getDevice();
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                if (userModel != null && deviceModel != null && deviceModel.getD_id() == requestBean.getD_id()) {
                                    DeviceSettingsModel settingsModel = getDeviceSettings();
                                    settingsModel.setIp(resultBean.getLast_online_ip());
                                    settingsModel.save();
                                    if(!NetworkUtils.isNetworkAvailable()){
                                        RequestToastUtils.toastNetwork();
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
                                DeviceModel deviceModel = getDevice();
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                if (userModel != null && deviceModel != null && deviceModel.getD_id() == requestBean.getD_id()) {
                                    DeviceSettingsModel settingsModel = getDeviceSettings();
                                    settingsModel.setWifi(requestBean.getWifi());
                                    settingsModel.setWifiType(0);
                                    settingsModel.save();
                                }
                            } else if (resultBean.getCode() == CWConstant.ERROR)
                                XToastUtils.toast(R.string.send_error_prompt);
                            else
                                RequestToastUtils.toast(resultBean.getCode());
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPostMsgBean(DeviceSysMsgBean event) {
        if (CWConstant.FAMILYWIFI == event.getType())
            getFamilyWifi();
    }

    @Override
    public void onResume() {
        super.onResume();
        mItemList.clear();
        initItems();
        mAdapter.notifyDataSetChanged();
        initSwitchBtn();
    }

    @Override
    public void onDestroy() {
        // 注销订阅者
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

}
