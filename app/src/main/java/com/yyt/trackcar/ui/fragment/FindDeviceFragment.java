package com.yyt.trackcar.ui.fragment;

import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.yyt.trackcar.R;
import com.yyt.trackcar.ui.base.BaseFragment;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      FindDeviceFragment
 * @ author:        QING
 * @ createTime:    2020/4/16 17:37
 * @ describe:      TODO 查找手表页面
 */
@Page(name = "FindDevice")
public class FindDeviceFragment extends BaseFragment {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_find_device;
    }

    @Override
    protected void initArgs() {
        XRouter.getInstance().inject(this);
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(R.string.find_device);
        return titleBar;
    }

    @Override
    protected void initViews() {
    }
}
