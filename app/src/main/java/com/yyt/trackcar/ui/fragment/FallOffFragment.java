package com.yyt.trackcar.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

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
import com.yyt.trackcar.dbflow.HealthModel;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.ui.adapter.CommonLocationAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CWRequestUtils;
import com.yyt.trackcar.utils.DBUtils;
import com.yyt.trackcar.utils.RequestToastUtils;
import com.yyt.trackcar.utils.XToastUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @ projectName:
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      FallOffFragment
 * @ author:        QING
 * @ createTime:    7/8/21 17:14
 * @ describe:      TODO
 */
@SuppressLint("NonConstantResourceId")
@Page(name = "FallOff")
public class FallOffFragment extends BaseFragment {
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
        titleBar.setTitle(getString(R.string.fall_off));
        return titleBar;
    }

    @Override
    protected void initViews() {
        initItems();
        initAdapters();
        initRecyclerViews();
        initHeaderView();
        DeviceModel deviceModel = getDevice();
        if (deviceModel != null) {
            HealthModel healthModel =
                    DBUtils.getDeviceHealth(deviceModel.getImei());
            if (healthModel != null)
                mIsOpen = "1".equals(healthModel.getFallOff());
        }
        setSwitch();
        getFallOff();
    }

    /**
     * 初始化列表信息
     */
    private void initItems() {
        BaseItemBean itemBean = new BaseItemBean(getString(R.string.fall_off_title),
                getString(R.string.fall_off_content));
        itemBean.setBgDrawable(android.R.color.transparent);
        mItemList.add(new SectionItem(itemBean));
//        itemBean = new BaseItemBean(getString(R.string.intercept_record),
//                getString(R.string.intercept_record_content));
//        itemBean.setBgDrawable(android.R.color.transparent);
//        mItemList.add(new SectionItem(itemBean));
//        mItemList.add(new SectionItem(true, null));
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
//        TextView tvTitle = headerView.findViewById(R.id.tvTitle);
//        TextView tvContent = headerView.findViewById(R.id.tvContent);
        ivBg.setImageResource(R.mipmap.bg_fall_off);
//        tvTitle.setText(R.string.refuse_strangers_title);
//        tvTitle.setTextColor(ContextCompat.getColor(mActivity, R.color.black));
//        tvContent.setText(R.string.refuse_strangers_content);
//        tvContent.setTextColor(ContextCompat.getColor(mActivity, R.color.black));
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

    public void getFallOff() {
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().getFallOff(getContext(), userModel.getToken(),
                    deviceModel.getD_id(), deviceModel.getImei(), mHandler);
    }

    private void setFallOff() {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork();
            return;
        }
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null) {
            CWRequestUtils.getInstance().setFallOff(getContext(), getIp(),
                    userModel.getToken(), deviceModel.getD_id(), deviceModel.getImei(),
                    mIsOpen ? "0" : "1", mHandler);
        }
    }

    @SingleClick
    @OnClick({R.id.switchBtn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switchBtn: // 开启/关闭
                setFallOff();
                break;
            default:
                break;
        }
    }

    /**
     * 消息处理
     */
    private final Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NotNull Message msg) {
            try {
                RequestResultBean resultBean;
                RequestBean requestBean;
                switch (msg.what) {
                    case CWConstant.REQUEST_URL_GET_FALL_OFF: // 佩戴检测开关获取
                        if (msg.obj != null) {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()),
                                                RequestBean.class);
                                RequestBean statusBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getResultBean()),
                                                RequestBean.class);
                                mIsOpen = "1".equals(statusBean.getType());
                                setSwitch();
                                HealthModel healthModel =
                                        DBUtils.getDeviceHealth(requestBean.getImei());
                                if (healthModel != null) {
                                    healthModel.setFallOff(statusBean.getType());
                                    healthModel.save();
                                }
                            }
                        }
                        break;
                    case CWConstant.REQUEST_URL_SET_FALL_OFF: // 佩戴检测开关设置
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS || resultBean.getCode() == CWConstant.WAIT_ONLINE_UPDATE) {
                                if (resultBean.getCode() == CWConstant.WAIT_ONLINE_UPDATE)
                                    XToastUtils.toast(R.string.wait_online_update_prompt);
                                else
                                    XToastUtils.toast(R.string.send_success_prompt);
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                mIsOpen = "1".equals(requestBean.getType());
                                setSwitch();
                                HealthModel healthModel =
                                        DBUtils.getDeviceHealth(requestBean.getImei());
                                if (healthModel != null) {
                                    healthModel.setFallOff(requestBean.getType());
                                    healthModel.save();
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
