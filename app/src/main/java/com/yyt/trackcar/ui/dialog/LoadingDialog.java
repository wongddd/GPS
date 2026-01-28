package com.yyt.trackcar.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.yyt.trackcar.R;


/**
 * projectName：   FAXWIN
 * packageName：   com.fax.win.ui.dialog
 * fileName：      LoadingDialog
 * author：        QING
 * createTime：    2018/11/14 14:44
 * describe：      TODO 加载对话框
 */
public class LoadingDialog extends Dialog {
    private TextView mTVContent; // 提示内容文本
    private String message; // 提示内容

    public LoadingDialog(Context context) {
        super(context);
    }

    public LoadingDialog(Context context, int theme) {
        super(context, theme);
    }

    public void setMessage(String message) {
        this.message = message;
        if (mTVContent != null) {
            if (TextUtils.isEmpty(message))
                mTVContent.setVisibility(View.GONE);
            else {
                mTVContent.setText(message);
                mTVContent.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
        mTVContent = findViewById(R.id.tvContent);// 提示文字
        if (TextUtils.isEmpty(message))
            mTVContent.setVisibility(View.GONE);
        else {
            mTVContent.setText(message);
            mTVContent.setVisibility(View.VISIBLE);
        }
    }
}
