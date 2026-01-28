package com.yyt.trackcar.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.yyt.trackcar.R;
import com.yyt.trackcar.dbflow.AAADeviceModel;
import com.yyt.trackcar.dbflow.AAAUserModel;
import com.yyt.trackcar.ui.base.BaseActivity;
import com.yyt.trackcar.utils.SettingSPUtils;
import com.yyt.trackcar.utils.TConstant;

import butterknife.BindView;

public class WebActivity extends BaseActivity {

    private String title; // 标题

    private String url; // 网络地址

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.fl_back)
    FrameLayout flBack;

    @Override
    protected int getLayoutId() {
        return R.layout.aaa_web_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AAAUserModel userModel = getTrackUserModel();
        if (userModel != null && !TextUtils.isEmpty(userModel.getToken())) {
            Intent intent = getIntent();
            Bundle bundle = null;
            if (intent != null) {
                bundle = intent.getExtras();
            }
            if (bundle != null && bundle.getInt(TConstant.TYPE) == 1) {
//                tvTitle.setText(R.string.trajectory_analysis);
                tvTitle.setText(String.format("%s%s", getString(R.string.pet_real_time),
                        getString(R.string.trajectory_analysis)));
                String deviceImei = bundle.getString(TConstant.DEVICE_IMEI);
                if(TextUtils.isEmpty(deviceImei)) {
                    AAADeviceModel deviceModel = getTrackDevice();
                    if (deviceModel != null && !TextUtils.isEmpty(deviceModel.getDeviceImei())) {
                        deviceImei = deviceModel.getDeviceImei();
                    }
                }
                if (!TextUtils.isEmpty(deviceImei)) {
                    int mapType = SettingSPUtils.getInstance().getInt(TConstant.MAP_TYPE, 0);
                    if (mapType == 0) {
                        url = String.format(TConstant.URL_HISTORY_AMAP, userModel.getToken(),
                                userModel.getUserId(), deviceImei);
                    } else {
                        url = String.format(TConstant.URL_HISTORY_GOOGLE_MAP,
                                userModel.getToken(), userModel.getUserId(),
                                deviceImei);
                    }
                }
            } else if (bundle != null && bundle.getInt(TConstant.TYPE) == 2) {
//                tvTitle.setText(R.string.home_system_configure);
                tvTitle.setText(String.format("%s%s", getString(R.string.pet_real_time),
                        getString(R.string.home_system_configure)));
                url = String.format(TConstant.URL_SYSTEM_CONFIGURE,
                        userModel.getUserId(), userModel.getToken());
            } else {
                int mapType = SettingSPUtils.getInstance().getInt(TConstant.MAP_TYPE, 0);
                if (mapType == 0)
                    url = String.format(TConstant.URL_MULTIPLE_HISTORY_AMAP,
                            userModel.getUserId(), userModel.getToken());
                else {
                    url = String.format(TConstant.URL_MULTIPLE_HISTORY_GOOGLE_MAP,
                            userModel.getUserId(), userModel.getToken());
                }
//                tvTitle.setText(R.string.home_history);
                tvTitle.setText(String.format("%s%s", getString(R.string.pet_real_time),
                        getString(R.string.home_history)));
            }
        }
        initViews();

//        AAAUserModel userModel = getTrackUserModel();
//        Bundle bundle = new Bundle();
//        bundle.putString(CWConstant.TITLE, getString(R.string.dealer_manage));
////        bundle.putString(CWConstant.URL, "http://gps88888.com/#/subordinate?id=" + userModel
// .getUserId() + "&token=" + userModel.getToken());
//        bundle.putString(CWConstant.URL, "http://gps88888.com/#/appMultipleHistory?id=" +
//        userModel.getUserId() + "&token=" + userModel.getToken());
//        KLog.e(bundle.getString(CWConstant.URL));
//        openNewPage(AAAWebFragment.class, bundle);
//        openNewPage(AAAWebFragment.class, bundle);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initViews() {
        flBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webView.stopLoading();
                ((ViewGroup) webView.getParent()).removeView(webView);
                webView.removeAllViews();
                webView.clearCache(true);
                webView.clearHistory();
                webView.destroy();
                finish();
            }
        });
        // 让android支持javaScript
        webView.getSettings().setJavaScriptEnabled(true);
        // setTextZoom设置的是webView内部字体的缩放比例,
        // android系统会根据系统字体大小修改h5的body的font-size，
        // 强制设置textZoom为100(%)使缩放比例始终为不缩放，
        // 从而影响不到webView内部字体的大小
        webView.getSettings().setTextZoom(100);
        webView.setWebViewClient(new MyWebView());
        // webView访问url
        webView.loadUrl(url);
    }

    private static final class MyWebView extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
//            if (!url.startsWith("http://gps88888.com/#/appMultipleHistory")) {
//                return false;
//            } else {
//                view.loadUrl(url);
//            }
//            return true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
