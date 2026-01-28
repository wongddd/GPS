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
import com.yyt.trackcar.bean.AAAResponseBean;
import com.yyt.trackcar.bean.ListResultBean;
import com.yyt.trackcar.bean.TrackSumBean;
import com.yyt.trackcar.ui.adapter.SportRecordAdapter;
import com.yyt.trackcar.ui.base.BaseActivity;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.TConstant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @ projectName:
 * @ packageName:   com.yyt.trackcar.ui.activity
 * @ fileName:      SportRecordActivity
 * @ author:        QING
 * @ createTime:    6/28/21 20:14
 * @ describe:      TODO
 */
@SuppressLint("NonConstantResourceId")
public class SportRecordActivity extends BaseActivity {
    @BindView(R.id.refreshlayout)
    RefreshLayout mRefreshLayout; // 下拉加载控件
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView; // RecyclerView
    private SportRecordAdapter mAdapter; // 适配器
    private List<TrackSumBean> mItemList = new ArrayList<>(); // 用户信息列表
    private String mImeiNo;
    private String mStartTime;
    private String mEndTime;
    private int mPage = 1; // 查询页数

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initToolBar(R.string.home_sports_report);
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
            getTrackSumList(1);
        }
    }

    private void initListeners() {
        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                getTrackSumList(mPage + 1);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getTrackSumList(1);
            }
        });
    }

    private void initAdapters() {
        mAdapter = new SportRecordAdapter(mRecyclerView);
        mAdapter.setData(mItemList);
    }

    private void initRecyclerViews() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter.getHeaderAndFooterAdapter());
    }

    private void getTrackSumList(int page) {
        if (!NetworkUtils.isNetworkAvailable()) {
            if (mRefreshLayout.getState() == RefreshState.Refreshing)
                mRefreshLayout.finishRefresh();
            else if (mRefreshLayout.getState() == RefreshState.Loading)
                mRefreshLayout.finishLoadMore();
            return;
        }
        if (!TextUtils.isEmpty(mImeiNo) && !TextUtils.isEmpty(mStartTime) && !TextUtils.isEmpty(mEndTime))
            CarGpsRequestUtils.getTrackSumList(mImeiNo, mStartTime, mEndTime, page,
                    20, mHandler);
        else {
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
                AAAResponseBean responseBean;
                switch (msg.what) {
                    case TConstant.REQUEST_URL_GET_TRACK_SUM_LIST:
                        if (msg.obj != null) {
                            responseBean = (AAAResponseBean) msg.obj;
                            if (responseBean.getResult() != TConstant.RESPONSE_NET_ERROR) {
                                ListResultBean listResultBean =
                                        mGson.fromJson(responseBean.getResponseObject(),
                                                ListResultBean.class);
                                List list = listResultBean.getRows();
                                if (list != null) {
                                    if (listResultBean.getPage() == 1) {
                                        mPage = 1;
                                        mItemList.clear();
                                    } else if (mPage + 1 != listResultBean.getPage())
                                        return false;
                                    else
                                        mPage = listResultBean.getPage();
                                    for (Object object : list) {
                                        TrackSumBean trackSumBean =
                                                mGson.fromJson(mGson.toJson(object),
                                                        TrackSumBean.class);
                                        mItemList.add(trackSumBean);
                                    }
                                    mAdapter.notifyDataSetChangedWrapper();
                                    if (mItemList.size() == 0)
                                        mRefreshLayout.setEnableLoadMore(false);
                                    else
                                        mRefreshLayout.setEnableLoadMore(true);
                                    if (mRefreshLayout.getState() != RefreshState.Loading &&
                                            listResultBean.getTotal() == listResultBean.getPage()) {
                                        mRefreshLayout.finishRefresh();
                                        mRefreshLayout.setNoMoreData(true);
                                    } else if (mRefreshLayout.getState() == RefreshState.Refreshing)
                                        mRefreshLayout.finishRefresh();
                                    else if (mRefreshLayout.getState() == RefreshState.Loading &&
                                            listResultBean.getTotal() == listResultBean.getPage())
                                        mRefreshLayout.finishLoadMoreWithNoMoreData();
                                    else if (mRefreshLayout.getState() == RefreshState.Loading)
                                        mRefreshLayout.finishLoadMore();
                                    return false;
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
