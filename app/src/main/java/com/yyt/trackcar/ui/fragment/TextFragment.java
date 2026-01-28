package com.yyt.trackcar.ui.fragment;

import android.widget.TextView;

import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.yyt.trackcar.R;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;

import butterknife.BindView;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      TextFragment
 * @ author:        QING
 * @ createTime:    2020/4/21 17:13
 * @ describe:      TODO 内容页面
 */
@Page(name = "Text", params = {CWConstant.TYPE, CWConstant.TITLE})
public class TextFragment extends BaseFragment {
    @BindView(R.id.tvContent)
    TextView mTvContent; // 内容文本
    @AutoWired
    String title; // 标题
    @AutoWired
    int type; // 类型 0手机收不到验证码 1 邮箱收不到验证码 2未找到手表绑定号

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_text;
    }

    @Override
    protected void initArgs() {
        XRouter.getInstance().inject(this);
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(title);
        return titleBar;
    }

    @Override
    protected void initViews() {
        switch (type) {
            case 0: // 手机收不到验证码
                mTvContent.setText(R.string.phone_no_receive_content);
                break;
            case 1: // 邮箱收不到验证码
                mTvContent.setText(R.string.email_no_receive_content);
                break;
            case 2: // 未找到手表绑定号
                mTvContent.setText(R.string.not_found_device_imei_content);
                break;
            default:
                break;
        }
    }
}
