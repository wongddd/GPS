package com.yyt.trackcar.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.yyt.trackcar.R;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      EditLocationFragment
 * @ author:        QING
 * @ createTime:    2020/3/12 10:49
 * @ describe:      TODO 编辑地点页面
 */
@Page(name = "EditLocation", params = {CWConstant.TYPE, CWConstant.NAME, CWConstant.ADDRESS})
public class EditLocationFragment extends BaseFragment {
    @BindView(R.id.tvNameContent)
    TextView mTvName; // 地点名称文本
    @BindView(R.id.tvPositionContent)
    TextView mTvPosition; // 地点名称文本
    @BindView(R.id.clWifi)
    View mWifiView; // wifi布局
    @BindView(R.id.tvWifi)
    TextView mTvWifi; // wifi文本
    @BindView(R.id.tvWifiContent)
    TextView mTvWifiContent; // wifi名称文本
    @BindView(R.id.ivArrow)
    ImageView mIvArrow; // arrow图标
    @AutoWired
    String name; // 名称
    @AutoWired
    int type; // 0家 1学校 2以上其它
    @AutoWired
    String address; // 地址

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_edit_location;
    }

    @Override
    protected void initArgs() {
        XRouter.getInstance().inject(this);
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(R.string.edit_location);
        if (type > 1) {
            titleBar.addAction(new TitleBar.TextAction(getString(R.string.del)) {
                @Override
                public void performAction(View view) {
                }
            });
        }
        return titleBar;
    }

    @Override
    protected void initViews() {
        if (type == 0)
            name = getString(R.string.family);
        else if (type == 1) {
            name = getString(R.string.school);
            mWifiView.setVisibility(View.GONE);
        } else {
            mTvWifi.setText(R.string.nearby_wifi);
            mIvArrow.setVisibility(View.VISIBLE);
        }
        if (name == null)
            name = "";
        if (address == null)
            address = "";
        mTvName.setText(name);
        mTvPosition.setText(address);
        mTvWifiContent.setText(getString(R.string.optional));
    }

    @SingleClick
    @OnClick({R.id.clName, R.id.clPosition, R.id.clWifi, R.id.saveBtn})
    public void onClick(View v) {
        Bundle bundle;
        switch (v.getId()) {
            case R.id.clName: // 地点名称
                if (type > 1) {
                    bundle = new Bundle();
                    bundle.putInt(CWConstant.TYPE, 2);
                    bundle.putString(CWConstant.TITLE, getString(R.string.location_name));
                    openNewPage(CustomInputSecondFragment.class, bundle);
                }
                break;
            case R.id.clPosition: // 地点位置
                break;
            case R.id.clWifi: // 家里Wifi
                openNewPage(WifiFragment.class);
                break;
            case R.id.saveBtn: // 保存
                break;
            default:
                break;
        }
    }
}
