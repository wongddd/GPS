package com.yyt.trackcar.ui.activity;

import android.os.Bundle;

import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.PostMessage;
import com.yyt.trackcar.ui.base.BaseActivity;
import com.yyt.trackcar.ui.fragment.WebFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.TConstant;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ProtocolAndAgreementActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle;
        int type = getIntent().getIntExtra(CWConstant.TYPE, 0);
        if (type == 0) {
            bundle = new Bundle();
            bundle.putString(CWConstant.TITLE, getString(R.string.about_privacy_protocol));
            bundle.putString(CWConstant.URL, CWConstant.PROTOCOL_URL);
            openNewPage(WebFragment.class, bundle);
        } else if (type == 1) {
            bundle = new Bundle();
            bundle.putString(CWConstant.TITLE, getString(R.string.about_user_service));
            bundle.putString(CWConstant.URL, CWConstant.USER_SERVICE_AGREEMENT_URL);
            openNewPage(WebFragment.class, bundle);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleMessage(PostMessage event){
        if (event.getType() == TConstant.NOTIFY_PARENT_VIEW_TO_FINISH){
            finish();
        }
    }
}
