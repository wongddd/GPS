package com.yyt.trackcar.ui.activity;

/**
 * @ projectName:
 * @ packageName:   com.yyt.trackcar.ui.activity
 * @ fileName:      AlarmListActivity
 * @ author:        QING
 * @ createTime:    6/23/21 18:51
 * @ describe:      TODO
 */

import android.annotation.SuppressLint;
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
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAAResponseBean;
import com.yyt.trackcar.bean.BaseItemBean;
import com.yyt.trackcar.bean.ListResultBean;
import com.yyt.trackcar.dbflow.AAADeviceModel;
import com.yyt.trackcar.dbflow.AAAUserModel;
import com.yyt.trackcar.ui.adapter.AlarmListAdapter;
import com.yyt.trackcar.ui.base.BaseActivity;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.TConstant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @ projectName:
 * @ packageName:   com.yyt.trackcar.ui.activity
 * @ fileName:      AlarmListActivity
 * @ author:        QING
 * @ createTime:    6/28/21 18:57
 * @ describe:      TODO
 */
@SuppressLint("NonConstantResourceId")
public class AlarmListActivity extends BaseActivity {
    @BindView(R.id.refreshlayout)
    RefreshLayout mRefreshLayout; // 下拉加载控件
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView; // RecyclerView
    private AlarmListAdapter mAdapter; // 适配器
    private List<BaseItemBean> mItemList = new ArrayList<>(); // 用户信息列表
    private String mImeiNo;
    private String mStartTime;
    private String mEndTime;
    private int mPage = 1; // 查询页数

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initToolBar(R.string.home_alarm_record, R.drawable.ic_back_white, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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
            getAlarmList(1);
        }
    }

    private void initListeners() {
        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                getAlarmList(mPage + 1);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getAlarmList(1);
            }
        });
    }

    private void initAdapters() {
        mAdapter = new AlarmListAdapter(mRecyclerView);
        mAdapter.setData(mItemList);

    }

    private void initRecyclerViews() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter.getHeaderAndFooterAdapter());
    }

    private void getAlarmList(int page) {
        if (!NetworkUtils.isNetworkAvailable()) {
            if (mRefreshLayout.getState() == RefreshState.Refreshing)
                mRefreshLayout.finishRefresh();
            else if (mRefreshLayout.getState() == RefreshState.Loading)
                mRefreshLayout.finishLoadMore();
            return;
        }
        AAAUserModel userModel = getTrackUserModel();
        if (userModel != null && !TextUtils.isEmpty(mImeiNo) && !TextUtils.isEmpty(mStartTime) && !TextUtils.isEmpty(mEndTime))
            CarGpsRequestUtils.getAlarmList(userModel.getUserId(),mImeiNo, mStartTime, mEndTime, page,
                    20, mHandler);
        else{
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
                    case TConstant.REQUEST_URL_GET_ALARM_LIST:
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
                                        AAADeviceModel deviceModel =
                                                mGson.fromJson(mGson.toJson(object),
                                                        AAADeviceModel.class);
                                        BaseItemBean itemBean = new BaseItemBean();
                                        itemBean.setTitle(deviceModel.getCreateTime());
                                        itemBean.setContent(deviceModel.getAlarmText());
                                        mItemList.add(itemBean);
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
