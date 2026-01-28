package com.yyt.trackcar.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.BaseItemBean;
import com.yyt.trackcar.bean.SectionMultiItem;
import com.yyt.trackcar.ui.adapter.BanClassesAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      GuardianTimeFragment
 * @ author:        QING
 * @ createTime:    2020/3/20 16:56
 * @ describe:      TODO 守护时间段页面
 */
@Page(name = "GuardianTime")
public class GuardianTimeFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener, CompoundButton.OnCheckedChangeListener {
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView; // RecyclerView
    private BanClassesAdapter mAdapter; // 适配器
    private List<SectionMultiItem> mItemList = new ArrayList<>(); // 列表

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycler_view;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(R.string.school_guardian);
        titleBar.addAction(new TitleBar.TextAction(getString(R.string.save)) {
            @Override
            public void performAction(View view) {
            }
        });
        return titleBar;
    }

    @Override
    protected void initViews() {
        BaseItemBean itemBean = new BaseItemBean(0, "上午");
        mItemList.add(new SectionMultiItem(4, itemBean));
        itemBean = new BaseItemBean(1, "到校时间", "08:00");
        itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
        itemBean.setHasArrow(true);
        mItemList.add(new SectionMultiItem(0, itemBean));
        itemBean = new BaseItemBean(2, "离校时间", "11:30");
        itemBean.setHasArrow(true);
        mItemList.add(new SectionMultiItem(0, itemBean));
        itemBean = new BaseItemBean(3, null, "注：上学守护时间段：07：40-08：10");
        mItemList.add(new SectionMultiItem(3, itemBean));

        itemBean = new BaseItemBean(4, "下午");
        mItemList.add(new SectionMultiItem(4, itemBean));
        itemBean = new BaseItemBean(5, "到校时间", "14:00");
        itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
        itemBean.setHasArrow(true);
        mItemList.add(new SectionMultiItem(0, itemBean));
        itemBean = new BaseItemBean(6, "离校时间", "16:30");
        itemBean.setHasArrow(true);
        mItemList.add(new SectionMultiItem(0, itemBean));
        itemBean = new BaseItemBean(7, null, "注：放学守护时间段：16：30-18：00");
        mItemList.add(new SectionMultiItem(3, itemBean));

        mItemList.add(new SectionMultiItem(true, null));
        itemBean = new BaseItemBean(8, "最晚到家时间", "18:00");
        itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
        itemBean.setHasArrow(true);
        mItemList.add(new SectionMultiItem(0, itemBean));
        itemBean = new BaseItemBean(9, "重复", "周一至周五");
        itemBean.setHasArrow(true);
        mItemList.add(new SectionMultiItem(0, itemBean));
        itemBean = new BaseItemBean(10, "法定节假日不守护");
        itemBean.setSelect(true);
        mItemList.add(new SectionMultiItem(1, itemBean));
        itemBean = new BaseItemBean(11, null, "注：暂时不支持中午放学守护");
        mItemList.add(new SectionMultiItem(3, itemBean));

        mItemList.add(new SectionMultiItem(true, null));

        initAdapters();
        initRecyclerViews();
    }

    /**
     * 初始化适配器
     */
    private void initAdapters() {
        mAdapter = new BanClassesAdapter(mItemList, this);
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
                Bundle bundle;
                switch (itemBean.getType()) {
                    case 9: // 重复
                        bundle = new Bundle();
                        bundle.putString(CWConstant.TITLE, itemBean.getTitle());
                        bundle.putInt(CWConstant.TYPE, 1);
                        openNewPage(CustomSelectorFragment.class, bundle);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }
}
