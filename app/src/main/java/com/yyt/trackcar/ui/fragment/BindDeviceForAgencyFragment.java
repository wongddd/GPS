package com.yyt.trackcar.ui.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.yyt.trackcar.R;
import com.yyt.trackcar.dbflow.AAADeviceModel;
import com.yyt.trackcar.ui.adapter.ManageCompetitionDeviceAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

@Page(name = "bindDeviceForAgencyFragment",anim = CoreAnim.none)
public class BindDeviceForAgencyFragment extends BaseFragment {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    private ManageCompetitionDeviceAdapter adapter;
    private final List<AAADeviceModel> mItemList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_refresh_layout;
    }

    @Override
    protected void initViews() {
        initData();
        initRecyclerView();
    }

    private void initData() {
        for (int i = 0; i < 10; i++) {
            mItemList.add(getTrackDeviceModel());
        }
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ManageCompetitionDeviceAdapter(mItemList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(R.string.device_management);
        return  titleBar;
    }
}
