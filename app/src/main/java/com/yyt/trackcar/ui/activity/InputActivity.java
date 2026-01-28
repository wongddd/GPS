package com.yyt.trackcar.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.R;
import com.yyt.trackcar.ui.base.BaseActivity;
import com.yyt.trackcar.utils.TConstant;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * projectName：   CarGps
 * packageName：   com.yyt.babywhere.ui.activity
 * fileName：      InputActivity
 * author：        QING
 * createTime：    2019/4/19 17:34
 * describe：      TODO 输入页面
 */
@SuppressLint("NonConstantResourceId")
public class InputActivity extends BaseActivity {

    @BindView(R.id.etInput)
    EditText mETInput; // 输入编辑文本
    private int mType; // 类型

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDatas();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_input;
    }

    /**
     * 初始化信息
     */
    private void initDatas() {
        mType = super.getIntent().getIntExtra(TConstant.TYPE, 0);
        switch (mType) {
            case 1:
                initToolBar(R.string.passthrough);
                mETInput.setInputType(InputType.TYPE_CLASS_TEXT);
                mETInput.setHint(R.string.passthrough);
                break;
            case 2:
                initToolBar(R.string.device_setting_type_sixth);
                mETInput.setInputType(InputType.TYPE_CLASS_PHONE);
                mETInput.setFilters(new InputFilter[]{new InputFilter
                        .LengthFilter(20)});
                mETInput.setHint(R.string.hint_wiretapping_num);
                break;
            default:
                initToolBar(R.string.main_control_num);
                mETInput.setHint(R.string.main_control_num);
                break;
        }
    }

    @OnClick({R.id.confirmBtn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirmBtn: // 确定
                String inputString = mETInput.getText().toString();
                if (TextUtils.isEmpty(inputString))
                    showMessage(mETInput.getHint().toString());
                else {
                    Intent intent;
                    switch (mType) {
                        case 1:
                            mHandler.sendEmptyMessageDelayed(1, 500);
                            break;
                        case 2:
                            intent = new Intent();
                            intent.putExtra(TConstant.BEAN, inputString);
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                            break;
                        default:
                            break;
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 消息处理
     */
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case 1:
                        dismisDialog();
                        finish();
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                if (BuildConfig.DEBUG)
                    e.printStackTrace();
            }
            return false;
        }
    });
}

