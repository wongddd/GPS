package com.yyt.trackcar.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xutil.app.AppUtils;
import com.yyt.trackcar.R;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.SettingSPUtils;
import com.yyt.trackcar.utils.XToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      AppUpdateFragment
 * @ author:        QING
 * @ createTime:    2020/3/11 02:24
 * @ describe:      TODO App版本更新
 */
@Page(name = "AppUpdate")
public class AppUpdateFragment extends BaseFragment implements CompoundButton.OnCheckedChangeListener {
    //    @BindView(R.id.sbSwitch)
//    SwitchButton mSbSwitch; // 自动更新开关
    @BindView(R.id.tvTitle)
    TextView mTvTitle; // 更新版本标题文本
    @BindView(R.id.tvContent)
    TextView mTvContent; // 更新版本信息文本
    @BindView(R.id.updateBtn)
    Button mUpdateBen; // 立即更新按钮

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_app_update;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(R.string.app_version_update);
        return titleBar;
    }

    @Override
    protected void initViews() {
        int nowAppVersionCode = AppUtils.getAppVersionCode();
        int newAppVersionCode = SettingSPUtils.getInstance().getInt(CWConstant.APP_VERSION_CODE,
                -1);
        String nowAppVersionName = AppUtils.getAppVersionName();
        String newAppVersionName =
                SettingSPUtils.getInstance().getString(CWConstant.APP_VERSION_NAME, "");
        boolean isCanUpdate =
                nowAppVersionCode < newAppVersionCode || (nowAppVersionCode == newAppVersionCode
                        && !newAppVersionName.equals(nowAppVersionName));
        if (isCanUpdate) {
            mTvTitle.setText(getString(R.string.app_new_version, newAppVersionName));
            mTvContent.setText(getString(R.string.app_new_version_content,
                    SettingSPUtils.getInstance().getString(CWConstant.APP_VERSION_CONTENT,
                            "")));
        } else {
            mTvTitle.setText(R.string.app_is_newest_version);
            mUpdateBen.setVisibility(View.GONE);
        }
//        mSbSwitch.setChecked(true);
    }

//    @Override
//    protected void initListeners() {
//        mSbSwitch.setOnCheckedChangeListener(this);
//    }

    @SingleClick(1000)
    @OnClick({R.id.updateBtn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.updateBtn: // 立即更新
                Uri uri = Uri.parse("market://details?id=com.yyt.trackcar");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setPackage("com.android.vending");//应用市场包名
                if (intent.resolveActivity(mActivity.getPackageManager()) != null)
                    mActivity.startActivity(intent);
                else
                    XToastUtils.toast("Google Play Store not install");
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }
}
