package com.yyt.trackcar.ui.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;

import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.BaseItemBean;
import com.yyt.trackcar.data.DataServer;
import com.yyt.trackcar.ui.adapter.FindAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      FindFragment
 * @ author:        QING
 * @ createTime:    2020/3/20 14:09
 * @ describe:      TODO 发现页面
 */
@Page(name = " Find", anim = CoreAnim.none)
public class FindFragment extends BaseFragment {

    @BindView(R.id.titleBar)
    TitleBar mTitleBar; // titleBar
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView; // RecyclerView
    private FindAdapter mAdapter; // 适配器
    private List<BaseItemBean> mItemList = new ArrayList<>(); // 列表

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home_recycler_view;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = mTitleBar;
        titleBar.setCenterGravity(Gravity.START | Gravity.CENTER);
        titleBar.disableLeftView();
        titleBar.setTitle(R.string.find);
        titleBar.getCenterText().getPaint().setFakeBoldText(true);
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
        DataServer.getFindData(mItemList);
    }

    /**
     * 初始化适配器
     */
    private void initAdapters() {
        mAdapter = new FindAdapter(mItemList);
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

//    @SingleClick
//    @OnClick({R.id.clFirst, R.id.clSecond, R.id.clThird})
//    public void onClick(View v) {
//        String title;
//        switch (v.getId()) {
//            case R.id.clFirst:
//                title = getString(R.string.new_product);
//                break;
//            case R.id.clSecond:
//                title = getString(R.string.charge_base);
//                break;
//            case R.id.clThird:
//                title = getString(R.string.watch_strap);
//                break;
//            default:
//                title = "";
//                break;
//        }
//        Bundle bundle;
//        bundle = new Bundle();
//        bundle.putString(CWConstant.TITLE, title);
//        bundle.putString(CWConstant.URL, "http://hktest.lagenio.xyz/protocol.htm");
//        openNewPage(WebFragment.class, bundle);
//    }
}
