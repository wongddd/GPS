package com.yyt.trackcar.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAABaseResponseBean;
import com.yyt.trackcar.bean.AAARequestBean;
import com.yyt.trackcar.bean.AAATrackModel;
import com.yyt.trackcar.bean.ListResponseBean;
import com.yyt.trackcar.dbflow.AAAUserModel;
import com.yyt.trackcar.ui.adapter.HistoryRecordAdapter;
import com.yyt.trackcar.ui.base.BaseActivity;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.TConstant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @ projectName:
 * @ packageName:   com.yyt.trackcar.ui.activity
 * @ fileName:      HistoryRecordActivity
 * @ author:        QING
 * @ createTime:    6/28/21 19:01
 * @ describe:      TODO
 */
@SuppressLint("NonConstantResourceId")
public class HistoryRecordActivity extends BaseActivity {
    @BindView(R.id.refreshlayout)
    RefreshLayout mRefreshLayout; // 下拉加载控件
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView; // RecyclerView
    private HistoryRecordAdapter mAdapter; // 适配器
    private List<AAATrackModel> mItemList = new ArrayList<>(); // 用户信息列表
    private String mImeiNo;
    private String mStartTime;
    private String mEndTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initToolBar(R.string.home_summary_record);
        initDatas();
        initListeners();
        initAdapters();
        initRecyclerViews();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_refresh_layout;
    }

    private void initDatas() {
        mRefreshLayout.setEnableLoadMore(false);
        Bundle bundle = super.getIntent().getExtras();
        if (bundle != null) {
            mImeiNo = bundle.getString(TConstant.IMEI_NO);
            mStartTime = bundle.getString(TConstant.START_TIME);
            mEndTime = bundle.getString(TConstant.END_TIME);
            getHistoryLocation((long) 0);
        }
    }

    private void initListeners() {
        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (mItemList.size() == 0)
                    getHistoryLocation((long) 0);
                else
                    getHistoryLocation(mItemList.get(mItemList.size() - 1).getLogId());
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getHistoryLocation((long) 0);
            }
        });
    }

    private void initAdapters() {
        mAdapter = new HistoryRecordAdapter(mRecyclerView);
        mAdapter.setData(mItemList);

    }

    private void initRecyclerViews() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter.getHeaderAndFooterAdapter());
    }

    private void getHistoryLocation(Long nextId) {
        if (!NetworkUtils.isNetworkAvailable()) {
            if (mRefreshLayout.getState() == RefreshState.Refreshing)
                mRefreshLayout.finishRefresh();
            else if (mRefreshLayout.getState() == RefreshState.Loading)
                mRefreshLayout.finishLoadMore();
            return;
        }
        if (!TextUtils.isEmpty(mImeiNo) && !TextUtils.isEmpty(mStartTime)
                && !TextUtils.isEmpty(mEndTime) && nextId != null) {
            AAAUserModel userModel = getTrackUserModel();
            CarGpsRequestUtils.getHistoryLocation(userModel, mImeiNo, mStartTime, mEndTime, nextId,
                    20, mHandler);
        } else {
            if (mRefreshLayout.getState() == RefreshState.Refreshing)
                mRefreshLayout.finishRefresh();
            else if (mRefreshLayout.getState() == RefreshState.Loading)
                mRefreshLayout.finishLoadMore();
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
                    case TConstant.REQUEST_URL_GET_HISTORY_LOCATION:
                        if (msg.obj != null) {
                            responseBean = (AAABaseResponseBean) msg.obj;
                            if (responseBean.getCode() == TConstant.RESPONSE_SUCCESS) {
                                ListResponseBean listResponseBean =
                                        mGson.fromJson(mGson.toJson(responseBean.getData()),
                                                ListResponseBean.class);
                                requestBean = mGson.fromJson(responseBean.getRequestObject(),
                                        AAARequestBean.class);
                                List list;
                                if (listResponseBean == null)
                                    list = null;
                                else
                                    list = listResponseBean.getList();
                                if (list != null) {
                                    if (!(mItemList.size() > 0 && requestBean.getNextId() != null
                                            && requestBean.getNextId() != 0 &&
                                            requestBean.getNextId().equals(mItemList.get(mItemList.size() - 1).getLogId()))) {
                                        if (requestBean.getNextId() == 0)
                                            mItemList.clear();
                                        for (Object object : list) {
                                            AAATrackModel trackModel =
                                                    mGson.fromJson(mGson.toJson(object),
                                                            AAATrackModel.class);
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
                if (BuildConfig.DEBUG)
                    e.printStackTrace();
            }
            return false;
        }
    });

}
