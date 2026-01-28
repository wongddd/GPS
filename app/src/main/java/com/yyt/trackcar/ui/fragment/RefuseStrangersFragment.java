package com.yyt.trackcar.ui.fragment;

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

import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
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
import com.yyt.trackcar.ui.adapter.CommonLocationAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CWRequestUtils;
import com.yyt.trackcar.utils.RequestToastUtils;
import com.yyt.trackcar.utils.SettingSPUtils;
import com.yyt.trackcar.utils.XToastUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      RefuseStrangersFragment
 * @ author:        QING
 * @ createTime:    2020/3/13 14:56
 * @ describe:      TODO 拒绝陌生人来电页面
 */
@Page(name = "RefuseStrangers")
public class RefuseStrangersFragment extends BaseFragment {
    @BindView(R.id.switchBtn)
    Button mSwitchBtn; // 开启/关闭按钮
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView; // RecyclerView
    private CommonLocationAdapter mAdapter; // 适配器
    private List<SectionItem> mItemList = new ArrayList<>(); // 列表
    private boolean mIsOpen; // 是否开启

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycler_view_btn;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(getString(R.string.refuse_strangers_call));
        return titleBar;
    }

    @Override
    protected void initViews() {
        initItems();
        initAdapters();
        initRecyclerViews();
        initHeaderView();
        DeviceSettingsModel settingsModel = getDeviceSettings();
        if (settingsModel != null && !TextUtils.isEmpty(settingsModel.getOther())) {
            String[] array = settingsModel.getOther().split(",");
            mIsOpen = array.length >= 3 && "1".equals(array[2]);
        }
        setSwitch();
        getOther();
    }

    /**
     * 初始化列表信息
     */
    private void initItems() {
        BaseItemBean itemBean = new BaseItemBean(getString(R.string.call_permission),
                getString(R.string.call_permission_content));
        itemBean.setBgDrawable(android.R.color.transparent);
        mItemList.add(new SectionItem(itemBean));
        itemBean = new BaseItemBean(getString(R.string.intercept_record),
                getString(R.string.intercept_record_content));
        itemBean.setBgDrawable(android.R.color.transparent);
        mItemList.add(new SectionItem(itemBean));
        mItemList.add(new SectionItem(true, null));
    }

    /**
     * 初始化适配器
     */
    private void initAdapters() {
        mAdapter = new CommonLocationAdapter(mItemList);
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
        View headerView = getLayoutInflater().inflate(R.layout.header_view_info_third,
                mRecyclerView, false);
        ImageView ivBg = headerView.findViewById(R.id.ivBg);
        TextView tvTitle = headerView.findViewById(R.id.tvTitle);
        TextView tvContent = headerView.findViewById(R.id.tvContent);
        if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 0)
            ivBg.setImageResource(R.mipmap.bg_refuse_strangers);
        else
            ivBg.setImageResource(R.mipmap.bg_refuse_strangers_second);
        tvTitle.setText(R.string.refuse_strangers_title);
        tvTitle.setTextColor(ContextCompat.getColor(mActivity, R.color.black));
        tvContent.setText(R.string.refuse_strangers_content);
        tvContent.setTextColor(ContextCompat.getColor(mActivity, R.color.black));
        mAdapter.addHeaderView(headerView);
    }

    /**
     * 设置开关
     */
    public void setSwitch() {
        if (mIsOpen)
            mSwitchBtn.setText(getString(R.string.close_status));
        else
            mSwitchBtn.setText(getString(R.string.open_status));
    }

    /**
     * 获取设置
     */
    public void getOther() {
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().getOther(getContext(), getIp(),
                    userModel.getToken(), deviceModel.getD_id(), mHandler);
    }

    /**
     * 设置拒接陌生人
     */
    private void setOther() {
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null) {
            DeviceSettingsModel settingsModel = getDeviceSettings();
            String other = "";
            if (!TextUtils.isEmpty(settingsModel.getOther())) {
                String[] array = settingsModel.getOther().split(",");
                if (array.length >= 3)
                    array[2] = mIsOpen ? "1" : "0";
                for (String str : array) {
                    other = String.format("%s,%s", other, str);
                }
            }
            if (TextUtils.isEmpty(other))
                other = String.format("5,0,%s,06:00|22:00|1,20|0", mIsOpen ? "1" : "0");
            else
                other = other.substring(1);
            CWRequestUtils.getInstance().setOther(getContext(), getIp(),
                    userModel.getToken(), deviceModel.getImei(), deviceModel.getD_id(), other,
                    mHandler);
        }
    }

    @SingleClick
    @OnClick({R.id.switchBtn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switchBtn: // 开启/关闭
                if (!NetworkUtils.isNetworkAvailable()) {
                    RequestToastUtils.toastNetwork();
                    return;
                }
                mIsOpen = !mIsOpen;
                setSwitch();
                setOther();
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
                    case CWConstant.REQUEST_URL_GET_OTHER: // 获取设置
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
                                    settingsModel.setOther(bean.getOther());
                                    settingsModel.save();
                                    if (!TextUtils.isEmpty(bean.getOther())) {
                                        String[] array = bean.getOther().split(",");
                                        mIsOpen = array.length >= 3 && "1".equals(array[2]);
                                    } else
                                        mIsOpen = false;
                                    setSwitch();
                                }
                            }
                        }
                        break;
                    case CWConstant.REQUEST_URL_SET_OTHER: // 设置其他参数
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
                                    if (!NetworkUtils.isNetworkAvailable()) {
                                        RequestToastUtils.toastNetwork();
                                        return false;
                                    }
                                    CWRequestUtils.getInstance().setOther(getContext(),
                                            resultBean.getLast_online_ip(),
                                            requestBean.getToken(), requestBean.getImei(),
                                            requestBean.getD_id(), requestBean.getOther(),
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
                                    settingsModel.setOther(requestBean.getOther());
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

}
