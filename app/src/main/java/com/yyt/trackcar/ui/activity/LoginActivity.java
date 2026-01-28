package com.yyt.trackcar.ui.activity;

import android.os.Bundle;

import com.yyt.trackcar.bean.PostMessage;
import com.yyt.trackcar.ui.base.BaseActivity;
import com.yyt.trackcar.ui.fragment.AAALoginFragment;
import com.yyt.trackcar.utils.CWConstant;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.activity
 * @ fileName:      LoginActivity
 * @ author:        QING
 * @ createTime:    2020-02-25 17:46
 * @ describe:      TODO 登录页面
 */
public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        openPage(AAALoginFragment.class,getIntent().getExtras());
    }

    @Override
    protected boolean isSupportSlideBack() {
        return false;
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void handlePostMessage(PostMessage event) {
        if (CWConstant.POST_MESSAGE_FINISH == event.getType())
            finish();
    }
}
