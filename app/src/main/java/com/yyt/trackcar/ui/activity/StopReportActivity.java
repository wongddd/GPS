package com.yyt.trackcar.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAABaseResponseBean;
import com.yyt.trackcar.bean.AAARequestBean;
import com.yyt.trackcar.bean.OdometerBean;
import com.yyt.trackcar.dbflow.AAAUserModel;
import com.yyt.trackcar.ui.adapter.StopReportAdapter;
import com.yyt.trackcar.ui.base.BaseActivity;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.TConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名：   传信鸽
 * 包名：     com.yyt.trackcar.ui.activity
 * 文件名：   StopReportActivity
 * 创建者：   QING
 * 创建时间： 2018/4/27 21:17
 * 描述：     TODO
 */

public class StopReportActivity extends BaseActivity {
    private RefreshLayout mRefreshLayout; // 下拉加载控件
    private RecyclerView mRecyclerView; // RecyclerView
    private StopReportAdapter mAdapter; // 适配器
    private List<OdometerBean> mItemList = new ArrayList<>(); // 里程信息列表
    private String mImeiNo;
    private String mStartTime;
    private String mEndTime;
    private int mPage = 1; // 查询页数

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initToolBar(R.string.home_trip_report, R.drawable.ic_back_white, mNavigationOnClickListener);
        initViews();
        initDatas();
        initAdapters();
        initRecyclerViews();
        initListeners();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_refresh_layout;
    }


    protected void initViews() {
        mRefreshLayout = findViewById(R.id.refreshlayout);
        mRecyclerView = findViewById(R.id.recyclerView);
    }


    protected void initDatas() {
        mRefreshLayout.setEnableLoadMore(false);
        Bundle bundle = super.getIntent().getExtras();
        if (bundle != null) {
            mImeiNo = bundle.getString(TConstant.IMEI_NO);
            mStartTime = bundle.getString(TConstant.START_TIME);
            mEndTime = bundle.getString(TConstant.END_TIME);
            getOdometerReport((long) 0);
        }
    }


    protected void initListeners() {
        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (mItemList.size() == 0)
                    getOdometerReport((long) 0);
                else
                    getOdometerReport(mItemList.get(mItemList.size() - 1).getReportId());
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getOdometerReport((long) 0);
            }
        });
    }




    protected void initAdapters() {
        mAdapter = new StopReportAdapter(mRecyclerView);
        mAdapter.setData(mItemList);

    }


    protected void initRecyclerViews() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter.getHeaderAndFooterAdapter());
    }

    private void getOdometerReport(Long nextId) {
        if (!NetworkUtils.isNetworkAvailable()) {
            if (mRefreshLayout.getState() == RefreshState.Refreshing)
                mRefreshLayout.finishRefresh();
            else if (mRefreshLayout.getState() == RefreshState.Loading)
                mRefreshLayout.finishLoadMore();
            return;
        }
        AAAUserModel userModel = getTrackUserModel();
        if (!TextUtils.isEmpty(mImeiNo) && !TextUtils.isEmpty(mStartTime) && !TextUtils.isEmpty(mEndTime)
                && nextId != null)
            CarGpsRequestUtils.getOdometerReport(userModel, mImeiNo, mStartTime, mEndTime, nextId,
                    20, mHandler);
        else {
            if (mRefreshLayout.getState() == RefreshState.Refreshing)
                mRefreshLayout.finishRefresh();
            else if (mRefreshLayout.getState() == RefreshState.Loading)
                mRefreshLayout.finishLoadMore();
        }
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
     * 消息处理
     */
    private final Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            try {
                AAABaseResponseBean responseBean;
                AAARequestBean requestBean;
                switch (msg.what) {
                    case TConstant.REQUEST_URL_GET_ODOMETER_REPORT:
                        if (msg.obj != null) {
                            responseBean = (AAABaseResponseBean) msg.obj;
                            if (responseBean.getCode() == TConstant.RESPONSE_SUCCESS) {
                                List list = mGson.fromJson(mGson.toJson(responseBean.getData()),
                                                List.class);
                                requestBean = mGson.fromJson(responseBean.getRequestObject(),
                                        AAARequestBean.class);
                                if (list != null) {
                                    if (!(mItemList.size() > 0 && requestBean.getNextId() != null
                                            && requestBean.getNextId() != 0 &&
                                            requestBean.getNextId().equals(mItemList.get(mItemList.size() - 1).getReportId()))) {
                                        if (requestBean.getNextId() == 0)
                                            mItemList.clear();
                                        for (Object object : list) {
                                            OdometerBean trackModel =
                                                    mGson.fromJson(mGson.toJson(object),
                                                            OdometerBean.class);
                                            mItemList.add(trackModel);
                                        }
                                        mAdapter.notifyDataSetChangedWrapper();
                                        if (mItemList.size() == 0)
                                            mRefreshLayout.setEnableLoadMore(false);
                                        else
                                            mRefreshLayout.setEnableLoadMore(true);
                                        if (mRefreshLayout.getState() != RefreshState.Loading &&
                                                list.size() < 20) {
                                            mRefreshLayout.finishRefresh();
                                            mRefreshLayout.setNoMoreData(true);
                                        } else if (mRefreshLayout.getState() == RefreshState.Refreshing)
                                            mRefreshLayout.finishRefresh();
                                        else if (mRefreshLayout.getState() == RefreshState.Loading &&
                                                list.size() < 20)
                                            mRefreshLayout.finishLoadMoreWithNoMoreData();
                                        else if (mRefreshLayout.getState() == RefreshState.Loading)
                                            mRefreshLayout.finishLoadMore();
                                        return false;
                                    }
                                }
                            }
                        }
                        if (mItemList.size() == 0)
                            mRefreshLayout.setEnableLoadMore(false);
                        else
                            mRefreshLayout.setEnableLoadMore(true);
                        if (mRefreshLayout.getState() != RefreshState.Loading && mItemList
                                .size() % 20 != 0) {
                            mRefreshLayout.finishRefresh();
                            mRefreshLayout.setNoMoreData(true);
                        } else if (mRefreshLayout.getState() == RefreshState.Refreshing)
                            mRefreshLayout.finishRefresh();
                        else if (mRefreshLayout.getState() == RefreshState.Loading && mItemList
                                .size() % 20 != 0)
                            mRefreshLayout.finishLoadMoreWithNoMoreData();
                        else if (mRefreshLayout.getState() == RefreshState.Loading)
                            mRefreshLayout.finishLoadMore();
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
