package com.yyt.trackcar.ui.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.BaseItemBean;
import com.yyt.trackcar.data.DataServer;
import com.yyt.trackcar.ui.adapter.LocateModeExplainAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      LocateModeExplainFragment
 * @ author:        QING
 * @ createTime:    2020/3/9 18:09
 * @ describe:      TODO 定位方式说明页面
 */
@Page(name = "LocateModeExplain")
public class LocateModeExplainFragment extends BaseFragment {
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView; // RecyclerView
    private LocateModeExplainAdapter mAdapter; // 适配器
    private List<BaseItemBean> mItemList = new ArrayList<>(); // 列表

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycler_view;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(R.string.locate_mode);
        return titleBar;
    }

    @Override
    protected void initViews() {
        mRecyclerView.setBackgroundResource(R.color.white);
        initItems();
        initAdapters();
        initRecyclerViews();
        initFooterView();
    }

    /**
     * 初始化列表信息
     */
    private void initItems() {
        DataServer.getLocateModeExplainData(mActivity, mItemList);
    }

    /**
     * 初始化适配器
     */
    private void initAdapters() {
        mAdapter = new LocateModeExplainAdapter(mItemList);
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
     * 初始化脚布局
     */
    private void initFooterView() {
        View footerView = getLayoutInflater().inflate(R.layout.footer_view_text, mRecyclerView,
                false);
        TextView tvContent = footerView.findViewById(R.id.tvContent);
        tvContent.setText(R.string.locate_mode_prompt);
        mAdapter.addFooterView(footerView);
    }

}
