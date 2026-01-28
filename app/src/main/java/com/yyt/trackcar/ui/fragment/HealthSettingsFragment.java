package com.yyt.trackcar.ui.fragment;

import android.annotation.SuppressLint;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.BaseItemBean;
import com.yyt.trackcar.bean.SectionItem;
import com.yyt.trackcar.data.DataServer;
import com.yyt.trackcar.ui.adapter.CustomTextAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @ projectName:
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      HealthSettingsFragment
 * @ author:        QING
 * @ createTime:    7/1/21 14:33
 * @ describe:      TODO
 */
@SuppressLint("NonConstantResourceId")
@Page(name = "HealthSettings")
public class HealthSettingsFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView; // RecyclerView
    private CustomTextAdapter mAdapter; // 适配器
    private List<SectionItem> mItemList = new ArrayList<>(); // 列表

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycler_view;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(R.string.health_settings);
        return titleBar;
    }

    @Override
    protected void initViews() {
        initItems();
        initAdapters();
        initRecyclerViews();
    }

    /**
     * 初始化列表信息
     */
    private void initItems() {
        DataServer.getHealthSettingsData(mActivity, mItemList);
    }

    /**
     * 初始化适配器
     */
    private void initAdapters() {
        mAdapter = new CustomTextAdapter(mItemList);
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

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (position >= 0 && position < mItemList.size()) {
            BaseItemBean itemBean = mItemList.get(position).t;
            if (itemBean != null) {
                switch (itemBean.getType()) {
                    case 0:
                        openNewPage(SedentaryReminderFragment.class);
                        break;
                    case 1:
                        openNewPage(HeartRateTestFragment.class);
                        break;
                    default:
                        break;
                }
            }
        }
    }

}
