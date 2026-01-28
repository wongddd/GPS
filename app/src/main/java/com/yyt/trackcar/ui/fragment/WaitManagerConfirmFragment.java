package com.yyt.trackcar.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.DeviceSysMsgBean;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      WaitManagerConfirmFragment
 * @ author:        QING
 * @ createTime:    2020-02-28 17:05
 * @ describe:      TODO 等待管理员确认页面
 */
@Page(name = "WaitManagerConfirm", params = {CWConstant.TYPE})
public class WaitManagerConfirmFragment extends BaseFragment {
    @BindView(R.id.ivIcon)
    ImageView mIvIcon; // 图标
    @BindView(R.id.tvTitle)
    TextView mTvTitle; // 标题文本
    @BindView(R.id.tvTitlePrompt)
    TextView mTvTitlePrompt; // 标题提示文本
    @BindView(R.id.confirmBtn)
    Button mConfirmBtn; // 确认按钮
    @AutoWired
    String name; // 管理员名称

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 注册订阅者
        EventBus.getDefault().register(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_custom_result;
    }

    @Override
    protected void initArgs() {
        XRouter.getInstance().inject(this);
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle("");
        return titleBar;
    }

    @Override
    protected void initViews() {
        mIvIcon.setImageResource(R.mipmap.ic_wait_manager_confirm);
        mTvTitle.setText(R.string.wait_manager_confirm);
        mTvTitlePrompt.setText(getString(R.string.wait_manager_confirm_prompt));
        mConfirmBtn.setVisibility(View.GONE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPostMsgBean(DeviceSysMsgBean event) {
        if(CWConstant.AGREE_BIND == event.getType() || CWConstant.REFUSE_BIND == event.getType())
            popToBack();
    }

    @Override
    public void onDestroy() {
        // 注销订阅者
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

}
