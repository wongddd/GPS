package com.yyt.trackcar.ui.fragment;

import android.widget.TextView;

import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.DeviceVersionBean;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;

import butterknife.BindView;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      FirmwareInfoFragment
 * @ author:        QING
 * @ createTime:    2020/3/12 17:28
 * @ describe:      TODO 手表固件信息页面
 */
@Page(name = "FirmwareInfo", params = {CWConstant.MODEL})
public class FirmwareInfoFragment extends BaseFragment {
    @BindView(R.id.tvTitle)
    TextView mTvTitle; // 更新版本标题文本
    @BindView(R.id.tvContent)
    TextView mTvContent; // 更新版本信息文本
    @AutoWired
    DeviceVersionBean model; // 设备版本对象

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_firmware_info;
    }

    @Override
    protected void initArgs() {
        XRouter.getInstance().inject(this);
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(R.string.update_device_firmware);
        return titleBar;
    }

    @Override
    protected void initViews() {
        if (model == null) {
            mTvTitle.setText(getString(R.string.current_version, ""));
            mTvContent.setText("");
        } else {
            mTvTitle.setText(getString(R.string.current_version, model.getDv()));
            mTvContent.setText(model.getDescription());
        }
    }
}
