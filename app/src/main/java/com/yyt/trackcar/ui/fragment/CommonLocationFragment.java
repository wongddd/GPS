package com.yyt.trackcar.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.adapter.simple.AdapterItem;
import com.xuexiang.xui.adapter.simple.XUISimpleAdapter;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.popupwindow.popup.XUISimplePopup;
import com.xuexiang.xutil.display.DensityUtils;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.BaseItemBean;
import com.yyt.trackcar.bean.SectionItem;
import com.yyt.trackcar.data.DataServer;
import com.yyt.trackcar.ui.adapter.CommonLocationAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      CommonLocationFragment
 * @ author:        QING
 * @ createTime:    2020/3/12 09:58
 * @ describe:      TODO 常用地点页面
 */
@Page(name = "CommonLocation")
public class CommonLocationFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView; // RecyclerView
    private CommonLocationAdapter mAdapter; // 适配器
    private List<SectionItem> mItemList = new ArrayList<>(); // 列表
    private XUISimplePopup mMenuPopup; // 弹出菜单

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycler_view;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(R.string.common_location);
        titleBar.addAction(new TitleBar.ImageAction(R.drawable.ic_add) {
            @Override
            public void performAction(View view) {
                if (mMenuPopup == null)
                    initMenuPopup();
                mMenuPopup.showDown(view);
            }
        });
        return titleBar;
    }

    @Override
    protected void initViews() {
        initItems();
        initAdapters();
        initRecyclerViews();
        initFooterView();
    }

    /**
     * 初始化列表信息
     */
    private void initItems() {
        DataServer.getCommonLocationData(mActivity, mItemList);
        mItemList.get(1).t.setContent("XXX地址");
        mItemList.get(2).t.setContent("XXX小学");
        mItemList.add(new SectionItem(true, null));
        BaseItemBean itemBean = new BaseItemBean(2, getString(R.string.common_location_second));
        itemBean.setContent("XXXXXX位置");
        itemBean.setBgDrawable(R.drawable.btn_custom_item_round_selector);
        itemBean.setHasArrow(true);
        mItemList.add(new SectionItem(itemBean));
    }

    /**
     * 初始化适配器
     */
    private void initAdapters() {
        mAdapter = new CommonLocationAdapter(mItemList);
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
    private void initFooterView() {
        View footerView = getLayoutInflater().inflate(R.layout.footer_view_text, mRecyclerView,
                false);
        TextView tvContent = footerView.findViewById(R.id.tvContent);
        tvContent.setText(R.string.common_location_prompt);
        mAdapter.addFooterView(footerView);
    }

    /**
     * 初始化弹出菜单
     */
    private void initMenuPopup() {
        mMenuPopup = new XUISimplePopup(mActivity, DataServer.getCommonLocationMenuData(mActivity))
                .create(DensityUtils.dip2px(120), DensityUtils.dip2px(240),
                        new XUISimplePopup.OnPopupItemClickListener() {
                            @Override
                            public void onItemClick(XUISimpleAdapter adapter, AdapterItem item,
                                                    int position) {
                                Bundle bundle = new Bundle();
                                bundle.putInt(CWConstant.TYPE, position + 2);
                                bundle.putString(CWConstant.NAME, item.getTitle().toString());
                                openNewPage(EditLocationFragment.class, bundle);
                            }
                        });
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (position >= 0 && position < mItemList.size()) {
            BaseItemBean itemBean = mItemList.get(position).t;
            if (itemBean != null) {
                Bundle bundle;
                switch (itemBean.getType()) {
                    case 0: // 家
                    case 1: // 学校
                        bundle = new Bundle();
                        bundle.putInt(CWConstant.TYPE, itemBean.getType());
                        bundle.putString(CWConstant.ADDRESS, itemBean.getContent());
                        openNewPage(EditLocationFragment.class, bundle);
                        break;
                    default:
                        bundle = new Bundle();
                        bundle.putInt(CWConstant.TYPE, itemBean.getType());
                        bundle.putString(CWConstant.NAME, itemBean.getTitle());
                        bundle.putString(CWConstant.ADDRESS, itemBean.getContent());
                        openNewPage(EditLocationFragment.class, bundle);
                        break;
                }
            }
        }
    }
}
