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
 * @ fileName:      RFIDNumberActivity
 * @ author:        QING
 * @ createTime:    6/27/21 21:00
 * @ describe:      TODO
 */
@SuppressLint("NonConstantResourceId")
public class RFIDNumberActivity extends BaseActivity {

    @BindView(R.id.etFirst)
    EditText mEtFirst;
    @BindView(R.id.etSecond)
    EditText mEtSecond;
    @BindView(R.id.etThird)
    EditText mEtThird;
    @BindView(R.id.etFourth)
    EditText mEtFourth;
    @BindView(R.id.etFifth)
    EditText mEtFifth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initToolBar(R.string.device_setting_type_eleventh);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_rfid_number;
    }

    @OnClick({R.id.confirmBtn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirmBtn: // 确定
                String inputString = mEtFirst.getText().toString();
                String inputSecondString = mEtSecond.getText().toString();
                String inputThirdString = mEtThird.getText().toString();
                String inputFourthString = mEtFourth.getText().toString();
                String inputFifthString = mEtFifth.getText().toString();
                if (TextUtils.isEmpty(inputString))
                    showMessage(mEtFirst.getHint().toString());
                else if (TextUtils.isEmpty(inputSecondString))
                    showMessage(mEtSecond.getHint().toString());
                else if (TextUtils.isEmpty(inputThirdString))
                    showMessage(mEtThird.getHint().toString());
                else if (TextUtils.isEmpty(inputFourthString))
                    showMessage(mEtFourth.getHint().toString());
                else if (TextUtils.isEmpty(inputFifthString))
                    showMessage(mEtFifth.getHint().toString());
                else {
                    Intent intent = new Intent();
                    intent.putExtra(TConstant.BEAN, String.format("%s,%s,%s,%s,%s", inputString,
                            inputSecondString, inputThirdString, inputFourthString,
                            inputFifthString));
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
                break;
            default:
                break;
        }
    }
}
