package com.yyt.trackcar.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.yyt.trackcar.R;
import com.yyt.trackcar.ui.base.BaseActivity;
import com.yyt.trackcar.utils.TConstant;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @ projectName:
 * @ packageName:   com.yyt.trackcar.ui.activity
 * @ fileName:      AuthPhoneNumberActivity
 * @ author:        QING
 * @ createTime:    6/27/21 20:00
 * @ describe:      TODO
 */
@SuppressLint("NonConstantResourceId")
public class AuthPhoneNumberActivity extends BaseActivity {
    @BindView(R.id.etFirst)
    EditText mEtFirst;
    @BindView(R.id.etSecond)
    EditText mEtSecond;
    private int mType; // 类型

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initToolBar(R.string.device_setting_type_first);
        initDatas();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_auth_phone_number;
    }

    /**
     * 初始化信息
     */
    private void initDatas() {
        mType = super.getIntent().getIntExtra(TConstant.TYPE, 0);
    }

    @OnClick({R.id.confirmBtn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirmBtn: // 确定
                String inputString = mEtFirst.getText().toString();
                String inputSecondString = mEtSecond.getText().toString();
                if (TextUtils.isEmpty(inputString))
                    showMessage(mEtFirst.getHint().toString());
                else if (TextUtils.isEmpty(inputSecondString))
                    showMessage(mEtSecond.getHint().toString());
                else {
                    Intent intent = new Intent();
                    intent.putExtra(TConstant.BEAN, String.format("%s,%s,%s", mType, inputString,
                            inputSecondString));
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
                break;
            default:
                break;
        }
    }
}
