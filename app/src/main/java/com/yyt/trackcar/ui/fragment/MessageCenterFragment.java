package com.yyt.trackcar.ui.fragment;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAABaseResponseBean;
import com.yyt.trackcar.bean.AAARequestBean;
import com.yyt.trackcar.bean.CommandMessageBean;
import com.yyt.trackcar.dbflow.AAAUserModel;
import com.yyt.trackcar.ui.adapter.MessageCenterAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.TConstant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      MessageCenterFragment
 * @ author:        QING
 * @ createTime:    2023/7/11 16:05
 * @ describe:      TODO 消息中心页面
 */
@Page(name = "MessageCenter", params = {TConstant.DEVICE_IMEI})
public class MessageCenterFragment extends BaseFragment {

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout; // 下拉加载控件
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView; // RecyclerView
    private MessageCenterAdapter mAdapter; // 适配器
    private final List<CommandMessageBean> mItemList = new ArrayList<>(); // 列表
    @AutoWired
    String deviceImei; // 设备号
    private int mPage = 1; // 查询页数

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_refresh_layout;
    }

    @Override
    protected void initArgs() {
        XRouter.getInstance().inject(this);
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        if (TextUtils.isEmpty(deviceImei)) {
//            titleBar.setTitle(R.string.message_center);
            titleBar.setTitle(String.format("%s%s", getString(R.string.pet_real_time),
                    getString(R.string.message_center)));
        } else {
//            titleBar.setTitle(R.string.send_command_message);
            titleBar.setTitle(String.format("%s%s", getString(R.string.pet_real_time),
                    getString(R.string.send_command_message)));
        }
        return titleBar;
    }

    @Override
    protected void initViews() {
        initItems();
        initAdapters();
        initRecyclerViews();
        initEmptyView();
        querySendCommandList(1);
    }

    @Override
    protected void initListeners() {
        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                querySendCommandList(mPage + 1);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                querySendCommandList(1);
            }
        });
    }

    /**
     * 初始化列表信息
     */
    private void initItems() {
    }

    /**
     * 初始化适配器
     */
    private void initAdapters() {
        mAdapter = new MessageCenterAdapter(mItemList);
    }

    /**
     * 初始化ViewPager
     */
    private void initRecyclerViews() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 初始化空布局
     */
    private void initEmptyView() {
        View emptyView = getLayoutInflater().inflate(R.layout.layout_empty_view,
                mRecyclerView, false);
        TextView tvEmpty = emptyView.findViewById(R.id.tvEmpty);
        tvEmpty.setText(R.string.no_message_prompt);
        mAdapter.setEmptyView(emptyView);
    }

    /**
     * 查询发送指令列表
     */
    private void querySendCommandList(int pageIndex) {
        if (!NetworkUtils.isNetworkAvailable()) {
            if (mRefreshLayout.getState() == RefreshState.Refreshing) {
                mRefreshLayout.finishRefresh();
            } else if (mRefreshLayout.getState() == RefreshState.Loading) {
                mRefreshLayout.finishLoadMore();
            }
            return;
        }
        AAAUserModel userModel = getTrackUserModel();
        if (userModel != null) {
            CarGpsRequestUtils.querySendCommandList(userModel, deviceImei, pageIndex,
                    TConstant.REQUEST_LIST_NUM, mHandler);
        } else {
            if (mRefreshLayout.getState() == RefreshState.Refreshing) {
                mRefreshLayout.finishRefresh();
            } else if (mRefreshLayout.getState() == RefreshState.Loading) {
                mRefreshLayout.finishLoadMore();
            }
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
                    case TConstant.REQUEST_QUERY_SEND_COMMAND_LIST: // 查询发送指令列表
                        if (msg.obj != null) {
                            responseBean = (AAABaseResponseBean) msg.obj;
                            if (responseBean.getCode() != TConstant.RESPONSE_NET_ERROR) {
                                requestBean = mGson.fromJson(responseBean.getRequestObject(),
                                        AAARequestBean.class);
                                if (requestBean.getPageIndex() != null && requestBean.getPageIndex() == 1) {
                                    mPage = 1;
                                    mItemList.clear();
                                } else if (requestBean.getPageIndex() == null || mPage + 1 != requestBean.getPageIndex())
                                    return false;
                                else {
                                    mPage = requestBean.getPageIndex();
                                }
                                List<Object> list =
                                        mGson.fromJson(mGson.toJson(responseBean.getData()),
                                                new TypeToken<List<Object>>() {
                                                }.getType());
                                if (list != null && list.size() > 0) {
                                    for (Object object : list) {
                                        List<CommandMessageBean> subList =
                                                mGson.fromJson(mGson.toJson(object),
                                                        new TypeToken<List<CommandMessageBean>>() {
                                                        }.getType());
                                        if (subList != null && subList.size() > 0) {
                                            mItemList.addAll(subList);
                                        }
                                    }
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                        if (mItemList.size() == 0) {
                            mRefreshLayout.setEnableLoadMore(false);
                        } else {
                            mRefreshLayout.setEnableLoadMore(true);
                        }
                        if (mRefreshLayout.getState() != RefreshState.Loading && mItemList
                                .size() % TConstant.REQUEST_LIST_NUM != 0) {
                            mRefreshLayout.finishRefresh();
                            mRefreshLayout.finishLoadMoreWithNoMoreData();
                        } else if (mRefreshLayout.getState() == RefreshState.Refreshing) {
                            mRefreshLayout.finishRefresh();
                        } else if (mRefreshLayout.getState() == RefreshState.Loading && mItemList
                                .size() % TConstant.REQUEST_LIST_NUM != 0) {
                            mRefreshLayout.finishLoadMoreWithNoMoreData();
                        } else if (mRefreshLayout.getState() == RefreshState.Loading) {
                            mRefreshLayout.finishLoadMore();
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
