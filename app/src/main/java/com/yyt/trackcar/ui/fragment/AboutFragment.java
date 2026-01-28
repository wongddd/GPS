package com.yyt.trackcar.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xutil.app.AppUtils;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.BaseItemBean;
import com.yyt.trackcar.bean.SectionItem;
import com.yyt.trackcar.data.DataServer;
import com.yyt.trackcar.ui.adapter.CustomTextAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      AboutFragment
 * @ author:        QING
 * @ createTime:    2020/3/11 01:14
 * @ describe:      TODO 关于页面
 */
@Page(name = "About")
public class AboutFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener {
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
        titleBar.setTitle(R.string.about);
        return titleBar;
    }

    @Override
    protected void initViews() {
        initItems();
        initAdapters();
        initRecyclerViews();
        initHeaderView();
    }

    /**
     * 初始化列表信息
     */
    private void initItems() {
        DataServer.getAboutData(mActivity, mItemList);
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

    /**
     * 初始化脚布局
     */
    private void initHeaderView() {
        View headerView = getLayoutInflater().inflate(R.layout.header_view_about, mRecyclerView,
                false);
        TextView tvContent = headerView.findViewById(R.id.tvContent);
        tvContent.setText(String.format("%s %s", getString(R.string.app_name),
                AppUtils.getAppVersionName()));
        mAdapter.addHeaderView(headerView);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (position >= 0 && position < mItemList.size()) {
            BaseItemBean itemBean = mItemList.get(position).t;
            if (itemBean != null) {
                Bundle bundle;
                switch (itemBean.getType()) {
                    case 0: // 用户服务协议
                        bundle = new Bundle();
                        bundle.putString(CWConstant.TITLE, getString(R.string.about_user_service));
                        bundle.putString(CWConstant.URL, CWConstant.USER_SERVICE_AGREEMENT_URL);
                        openNewPage(WebFragment.class, bundle);
                        break;
                    case 1: // 隐私保护政策
                        bundle = new Bundle();
                        bundle.putString(CWConstant.TITLE,
                                getString(R.string.about_privacy_protocol));
                        bundle.putString(CWConstant.URL,CWConstant.PROTOCOL_URL);
                        openNewPage(WebFragment.class, bundle);
                        break;
                    case 2: // 用户体验改进计划
                        bundle = new Bundle();
                        bundle.putString(CWConstant.TITLE,
                                getString(R.string.about_user_experience));
                        bundle.putString(CWConstant.URL, CWConstant.PROTOCOL_URL);
                        openNewPage(WebFragment.class, bundle);
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
