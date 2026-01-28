package com.yyt.trackcar.ui.fragment;

import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.dbflow.CallRecordModel;
import com.yyt.trackcar.dbflow.CallRecordModel_Table;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.ui.adapter.CallRecordAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CWRequestUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      CallRecordFragment
 * @ author:        QING
 * @ createTime:    2020/4/13 15:50
 * @ describe:      TODO 通话记录页面
 */
@Page(name = "CallRecord")
public class CallRecordFragment extends BaseFragment {
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView; // RecyclerView
    private CallRecordAdapter mAdapter; // 适配器
    private List<CallRecordModel> mItemList = new ArrayList<>(); // 列表

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycler_view;
    }

    @Override
    protected void initArgs() {
        XRouter.getInstance().inject(this);
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(R.string.call_record);
        return titleBar;
    }

    @Override
    protected void initViews() {
        initItems();
        initAdapters();
        initRecyclerViews();
        initHeaderAndFooterView();
        initEmptyView();
        getDevicePhoneLog();
    }

    /**
     * 初始化列表信息
     */
    private void initItems() {
        DeviceModel deviceModel = getDevice();
        if (deviceModel != null) {
            mItemList.addAll(SQLite.select().from(CallRecordModel.class)
                    .where(CallRecordModel_Table.imei.eq(deviceModel.getImei()))
                    .orderBy(CallRecordModel_Table.phone_time, false)
                    .queryList());
            if (mItemList.size() == 1)
                mItemList.get(0).setBgDrawable(R.drawable.bg_white_round);
            else if (mItemList.size() > 1) {
                mItemList.get(0).setBgDrawable(R.drawable.bg_custom_top_radius);
                mItemList.get(mItemList.size() - 1).setBgDrawable(R.drawable.bg_custom_bottom_radius);
            }
        }
    }

    /**
     * 初始化适配器
     */
    private void initAdapters() {
        mAdapter = new CallRecordAdapter(mItemList);
    }

    /**
     * 获取通话记录
     */
    private void getDevicePhoneLog() {
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().getDevicePhoneLog(getContext(), userModel.getToken(),
                    deviceModel.getD_id(), mHandler);
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
     * 初始化头脚布局
     */
    private void initHeaderAndFooterView() {
        View headerView = getLayoutInflater().inflate(R.layout.item_space_section,
                mRecyclerView,
                false);
        View footerView = getLayoutInflater().inflate(R.layout.item_space_section,
                mRecyclerView,
                false);
        mAdapter.addHeaderView(headerView);
        mAdapter.addFooterView(footerView);
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
                    case CWConstant.REQUEST_URL_GET_DEVICE_PHONE_LOG: // 获取通话记录
                        if (msg.obj != null) {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                DeviceModel deviceModel = getDevice();
                                if (deviceModel != null) {
                                    mItemList.clear();
                                    if (resultBean.getList() != null) {
                                        for (Object obj : resultBean.getList()) {
                                            CallRecordModel callRecordBean =
                                                    mGson.fromJson(mGson.toJson(obj),
                                                            CallRecordModel.class);
                                            callRecordBean.setImei(deviceModel.getImei());
                                            callRecordBean.save();
                                        }
                                    }
                                    initItems();
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
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
