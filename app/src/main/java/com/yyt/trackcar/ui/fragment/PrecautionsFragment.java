package com.yyt.trackcar.ui.fragment;

import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.yyt.trackcar.R;
import com.yyt.trackcar.ui.base.BaseFragment;

@Page(name = "PrecautionsFragment")
public class PrecautionsFragment extends BaseFragment {
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_precautions;
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(R.string.precautions);
        return titleBar;
    }
}
