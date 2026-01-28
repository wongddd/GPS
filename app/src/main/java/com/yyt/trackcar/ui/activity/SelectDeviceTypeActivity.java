package com.yyt.trackcar.ui.activity;

import android.os.Bundle;

import com.yyt.trackcar.bean.PostMessage;
import com.yyt.trackcar.ui.base.BaseActivity;
import com.yyt.trackcar.ui.fragment.SelectDeviceTypeFragment;
import com.yyt.trackcar.utils.CWConstant;

/**
 * 切换设备页面
 */
public class SelectDeviceTypeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        openPage(SelectDeviceTypeFragment.class, getIntent().getExtras());
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
