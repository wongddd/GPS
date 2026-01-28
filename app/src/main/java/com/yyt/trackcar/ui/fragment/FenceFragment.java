package com.yyt.trackcar.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.MainApplication;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.FenceBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.ui.adapter.FenceAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CWRequestUtils;
import com.yyt.trackcar.utils.RequestToastUtils;
import com.yyt.trackcar.utils.XToastUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      FenceFragment
 * @ author:        QING
 * @ createTime:    2020/4/9 05:25
 * @ describe:      TODO 电子围栏页面
 */
@Page(name = "Fence")
public class FenceFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener,
        CompoundButton.OnCheckedChangeListener {
    @BindView(R.id.switchBtn)
    Button mSwitchBtn; // 开启/关闭按钮
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView; // RecyclerView
    private FenceAdapter mAdapter; // 适配器
    private ArrayList<FenceBean> mItemList = new ArrayList<>(); // 列表
    private TitleBar mTitleBar; // 标题栏
    private boolean mIsEdit; // 是否编辑
    private Map<String, String> mFenceMap = new HashMap<>();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycler_view_btn;
    }

    @Override
    protected TitleBar initTitle() {
        mTitleBar = super.initTitle();
        mTitleBar.setTitle(R.string.fence);
        mTitleBar.addAction(getAction());
        return mTitleBar;
    }

    @Override
    protected void initViews() {
        initAdapters();
        initRecyclerViews();
        initHeaderAndFooterView();
        initEmptyView();
        getWatchFence();
        mSwitchBtn.setText(R.string.fence_add);
    }

    /**
     * 初始化适配器
     */
    private void initAdapters() {
        mAdapter = new FenceAdapter(mItemList, this);
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
     * 初始化头和脚布局
     */
    private void initHeaderAndFooterView() {
        View headerView = getLayoutInflater().inflate(R.layout.item_space_section, mRecyclerView,
                false);
        View footerView = getLayoutInflater().inflate(R.layout.item_space_section, mRecyclerView,
                false);
        mAdapter.addHeaderView(headerView);
        mAdapter.addFooterView(footerView);
    }

    /**
     * 初始化空布局
     */
    private void initEmptyView() {
        View emptyView = getLayoutInflater().inflate(R.layout.layout_empty_view,
                mRecyclerView, false);
        emptyView.setBackgroundColor(ContextCompat.getColor(mActivity, R.color
                .white));
        ImageView ivEmpty = emptyView.findViewById(R.id.ivEmpty);
        TextView tvEmpty = emptyView.findViewById(R.id.tvEmpty);
        ivEmpty.setImageResource(R.mipmap.ic_no_query_data);
        tvEmpty.setText(R.string.no_data_prompt);
        mAdapter.setEmptyView(emptyView);
    }

    /**
     * 查询电子围栏列表
     */
    private void getWatchFence() {
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().getWatchFence(getContext(), userModel.getToken(),
                    deviceModel.getD_id(), mHandler);
    }

    /**
     * 修改电子围栏
     */
    private void updateWatchFence(FenceBean fenceBean) {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork();
            return;
        }
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().updateWatchFence(MainApplication.getContext(),
                    userModel.getToken(),
                    deviceModel.getImei(), deviceModel.getD_id(), fenceBean.getId(),
                    fenceBean.getFenceName(), fenceBean.getLat(), fenceBean.getLng(),
                    fenceBean.getRadius(), fenceBean.getEntry(), fenceBean.getExit(),
                    fenceBean.getEnable(), mHandler);
    }

    /**
     * 删除电子围栏
     */
    private void deleteWatchFence(long id) {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork();
            return;
        }
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().deleteWatchFence(getContext(), userModel.getToken(),
                    deviceModel.getD_id(), String.valueOf(id), mHandler);
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
                    mSwitchBtn.setText(R.string.fence_add);
                }
            };
        else
            action = new TitleBar.TextAction(getString(R.string.edit)) {
                @Override
                public void performAction(View view) {
                    mIsEdit = true;
                    for (FenceBean item : mItemList) {
                        item.setSelect(false);
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
                        FenceBean fenceBean = mItemList.get(i);
                        if (fenceBean.isSelect()) {
                            deleteWatchFence(fenceBean.getId());
                            mItemList.remove(i);
                            mFenceMap.remove(String.valueOf(fenceBean.getId()));
                        } else
                            fenceBean.setBgDrawable(0);
                    }
                    if (mItemList.size() == 1)
                        mItemList.get(0).setBgDrawable(R.drawable.btn_custom_item_round_selector);
                    else if (mItemList.size() > 2) {
                        mItemList.get(0).setBgDrawable(R.drawable.btn_custom_top_radius);
                        mItemList.get(mItemList.size() - 1).setBgDrawable(R.drawable.btn_custom_bottom_radius);
                    }
                    mIsEdit = false;
                    mAdapter.setEdit(mIsEdit);
                    mTitleBar.removeAllActions();
                    mTitleBar.addAction(getAction());
                    mSwitchBtn.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
                    mSwitchBtn.setBackgroundResource(R.drawable.btn_custom_selector);
                    mSwitchBtn.setText(R.string.fence_add);
                    mAdapter.notifyDataSetChanged();
                } else { // 添加
                    if (mItemList.size() >= 2)
                        XToastUtils.toast(R.string.fence_to_max_prompt);
                    else {
                        Bundle bundle = new Bundle();
                        bundle.putString(CWConstant.TITLE, getString(R.string.fence_add));
                        bundle.putParcelable(CWConstant.MODEL, null);
                        bundle.putString(CWConstant.LIST, mGson.toJson(mItemList));
                        openNewPageForResult(FenceMapFragment.class, bundle,
                                CWConstant.REQUEST_FENCE_MAP);
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (mIsEdit && position >= 0 && position < mItemList.size()) {
            FenceBean fenceBean = mItemList.get(position);
            fenceBean.setSelect(!fenceBean.isSelect());
            mAdapter.notifyDataSetChanged();
        } else if (position >= 0 && position < mItemList.size()) {
            FenceBean fenceBean = mItemList.get(position);
            Bundle bundle = new Bundle();
            bundle.putString(CWConstant.TITLE, getString(R.string.fence_edit));
            bundle.putParcelable(CWConstant.MODEL, fenceBean);
            bundle.putString(CWConstant.LIST, mGson.toJson(mItemList));
            openNewPageForResult(FenceMapFragment.class, bundle,
                    CWConstant.REQUEST_FENCE_MAP);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        long id = (long) buttonView.getTag();
        for (FenceBean fenceBean : mItemList) {
            if (id == fenceBean.getId()) {
                fenceBean.setEnable(isChecked ? 1 : 0);
                updateWatchFence(fenceBean);
                break;
            }
        }
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Intent data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == CWConstant.REQUEST_FENCE_MAP && resultCode == Activity.RESULT_OK && data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                getWatchFence();
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
                switch (msg.what) {
                    case CWConstant.REQUEST_URL_GET_WATCH_FENCE: // 查询电子围栏列表
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                mItemList.clear();
                                mFenceMap.clear();
                                if (resultBean.getGeoFenceList() != null) {
                                    for (Object obj : resultBean.getGeoFenceList()) {
                                        FenceBean fenceBean = mGson.fromJson(mGson.toJson(obj),
                                                FenceBean.class);
                                        mFenceMap.put(String.valueOf(fenceBean.getId()),
                                                String.valueOf(fenceBean.getEnable()));
                                        mItemList.add(fenceBean);
                                    }
                                }
                                if (mItemList.size() == 1)
                                    mItemList.get(0).setBgDrawable(R.drawable.btn_custom_item_round_selector);
                                else if (mItemList.size() > 2) {
                                    mItemList.get(0).setBgDrawable(R.drawable.btn_custom_top_radius);
                                    mItemList.get(mItemList.size() - 1).setBgDrawable(R.drawable.btn_custom_bottom_radius);
                                }
                                mAdapter.notifyDataSetChanged();
                            } else
                                RequestToastUtils.toast(resultBean.getCode());
                        }
                        break;
                    case CWConstant.REQUEST_URL_UPDATE_WATCH_FENCE: // 修改电子围栏
                    case CWConstant.REQUEST_URL_DELETE_WATCH_FENCE: // 删除电子围栏
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS)
                                XToastUtils.toast(R.string.send_success_prompt);
                            else if (resultBean.getCode() == CWConstant.WAIT_ONLINE_UPDATE)
                                XToastUtils.toast(R.string.wait_online_update_prompt);
                            else if (resultBean.getCode() == CWConstant.ERROR)
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

    @Override
    public void onDestroyView() {
//        for (FenceBean item : mItemList) {
//            if (!String.valueOf(item.getEnable()).equals(mFenceMap.get(String.valueOf(item.getId()))))
//                updateWatchFence(item);
//        }
        super.onDestroyView();
    }

}
