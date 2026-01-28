package com.yyt.trackcar.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAABaseResponseBean;
import com.yyt.trackcar.bean.AAARequestBean;
import com.yyt.trackcar.bean.GeoFenceBean;
import com.yyt.trackcar.dbflow.AAADeviceModel;
import com.yyt.trackcar.dbflow.AAAUserModel;
import com.yyt.trackcar.ui.adapter.GeoFenceAdapter;
import com.yyt.trackcar.ui.base.BaseActivity;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.SettingSPUtils;
import com.yyt.trackcar.utils.TConstant;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.baseadapter.BGAOnItemChildClickListener;

/**
 * 项目名：   传信鸽
 * 包名：     com.yyt.trackcar.ui.activity
 * 文件名：   ElectronicActivity
 * 创建者：   QING
 * 创建时间： 2018/4/23 18:22
 * 描述：     TODO 电子围栏页面
 */

public class ElectronicActivity extends BaseActivity implements BGAOnItemChildClickListener {
    private RefreshLayout mRefreshLayout; // 下拉加载控件
    private RecyclerView mRecyclerView; // RecyclerView
    private GeoFenceAdapter mAdapter; // 适配器
    private List<GeoFenceBean> mItemList = new ArrayList<>(); // 电子围栏列表

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        initToolBar(R.string.home_electronic, R.drawable.ic_back_white,
//        mNavigationOnClickListener);
        initToolBar(String.format("%s%s", getString(R.string.pet_real_time),
                getString(R.string.home_electronic)), R.drawable.ic_back_white,
                mNavigationOnClickListener);
        initToolBarMenu(R.menu.action_menu_add, mMenuItemClickListener);
        initViews();
        initAdapters();
        initRecyclerViews();
        initListeners();
        getGeoFenceList();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_refresh_layout;
    }

    protected void initViews() {
        mRefreshLayout = findViewById(R.id.refreshlayout);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setEnableRefresh(false);
    }


    protected void initDatas() {

    }


    protected void initListeners() {
        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
            }
        });
    }

    protected void initItems() {

    }

    protected void initAdapters() {
        mAdapter = new GeoFenceAdapter(mRecyclerView);
        mAdapter.setData(mItemList);
        mAdapter.setOnItemChildClickListener(this);
    }

    protected void initRecyclerViews() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter.getHeaderAndFooterAdapter());
    }

    private void getGeoFenceList() {
        if (!NetworkUtils.isNetworkAvailable()) {
            showMessage(R.string.network_error_prompt);
            return;
        }
        AAAUserModel userModel = getTrackUserModel();
        AAADeviceModel deviceModel = getTrackDevice();
        if (deviceModel != null)
            CarGpsRequestUtils.getGeoFenceList(userModel, deviceModel.getDeviceImei(),
                    mHandler);
    }

    private void delGeoFence(long fenceId) {
        if (!NetworkUtils.isNetworkAvailable()) {
            showMessage(R.string.network_error_prompt);
            return;
        }
        AAAUserModel userModel = getTrackUserModel();
        CarGpsRequestUtils.delGeoFence(userModel, fenceId, mHandler);
    }

    /**
     * 标题栏Navigation点击监听器
     */
    private final View.OnClickListener mNavigationOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            finish();
        }
    };

    /**
     * 标题栏菜单点击监听器
     */
    private final Toolbar.OnMenuItemClickListener mMenuItemClickListener = new Toolbar
            .OnMenuItemClickListener() {

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (item.getItemId() == R.id.item_add) {
                Intent intent;
                if (SettingSPUtils.getInstance().getInt(TConstant.MAP_TYPE, 0) == 1)
                    intent = new Intent(ElectronicActivity.this, ElectronicAddGoogleActivity.class);
                else
                    intent = new Intent(ElectronicActivity.this, ElectronicAddActivity.class);
                startActivityForResult(intent, TConstant.REQUEST_ADD_ELECTRONIC);
            }
            return false;
        }
    };

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onItemChildClick(ViewGroup parent, View childView, int position) {
        switch (childView.getId()) {
            case R.id.item_layout: // 点击
                mAdapter.closeAllItems();
                Intent intent;
                if (SettingSPUtils.getInstance().getInt(TConstant.MAP_TYPE, 0) == 1)
                    intent = new Intent(this, ElectronicAddGoogleActivity.class);
                else
                    intent = new Intent(this, ElectronicAddActivity.class);
                intent.putExtra(TConstant.BEAN, mItemList.get(position));
                startActivityForResult(intent, TConstant.REQUEST_EDIT_ELECTRONIC);
                break;
            case R.id.item_delete_btn: // 删除
                mAdapter.closeAllItems();
                if (mItemList.get(position).getFenceId() != null) {
                    showDialog();
                    mLoadingDialog.setMessage(getString(R.string.requesting_tips));
                    delGeoFence(mItemList.get(position).getFenceId());
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK) {
                switch (requestCode) {
                    case TConstant.REQUEST_ADD_ELECTRONIC: // 添加电子围栏
                        getGeoFenceList();
                        break;
                    case TConstant.REQUEST_EDIT_ELECTRONIC: // 编辑电子围栏
                        if (data != null) {
                            GeoFenceBean geoFenceBean = data.getParcelableExtra(TConstant.BEAN);
                            if (geoFenceBean != null) {
                                for (int i = 0; i < mItemList.size(); i++) {
                                    if (mItemList.get(i).getFenceId() != null &&
                                            mItemList.get(i).getFenceId().
                                                    equals(geoFenceBean.getFenceId())) {
                                        mItemList.set(i, geoFenceBean);
                                        mAdapter.notifyItemChangedWrapper(i);
                                        break;
                                    }
                                }
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 消息处理
     */
    private final Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            try {
                AAABaseResponseBean responseBean;
                AAARequestBean requestBean;
                switch (msg.what) {
                    case TConstant.REQUEST_URL_GET_GEO_FENCE_LIST:
                        if (msg.obj != null) {
                            responseBean = (AAABaseResponseBean) msg.obj;
                            if (responseBean.getCode() == TConstant.RESPONSE_SUCCESS) {
                                List list = mGson.fromJson(mGson.toJson(responseBean.getData()),
                                        List.class);
                                mItemList.clear();
                                if (list != null) {
                                    for (Object object : list) {
                                        GeoFenceBean geoFenceBean =
                                                mGson.fromJson(mGson.toJson(object),
                                                        GeoFenceBean.class);
                                        mItemList.add(geoFenceBean);
                                    }
                                }
                                mAdapter.notifyDataSetChangedWrapper();
                            }
                        }
                        break;
                    case TConstant.REQUEST_URL_DEL_GEO_FENCE:
                        dismisDialog();
                        if (msg.obj == null)
                            showMessage(R.string.request_unkonow_prompt);
                        else {
                            responseBean = (AAABaseResponseBean) msg.obj;
                            if (responseBean.getCode() == TConstant.RESPONSE_SUCCESS) {
                                requestBean = mGson.fromJson(responseBean.getRequestObject(),
                                        AAARequestBean.class);
                                if (requestBean.getFenceId() != null) {
                                    for (int i = 0; i < mItemList.size(); i++) {
                                        if (requestBean.getFenceId().equals(mItemList.get(i).getFenceId())) {
                                            mItemList.remove(i);
                                            mAdapter.notifyDataSetChangedWrapper();
                                            break;
                                        }
                                    }
                                }
                            } else if (responseBean.getCode() == TConstant.RESPONSE_NET_ERROR)
                                showMessage(R.string.request_unkonow_prompt);
                            else
                                showMessage(R.string.del_error_tips);
                        }
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    });
}
