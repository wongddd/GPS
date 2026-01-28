package com.yyt.trackcar.ui.fragment;

import android.annotation.SuppressLint;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.socks.library.KLog;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.PostMessage;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;

@Page(name = "aaaWebFragment", params = {CWConstant.URL, CWConstant.TITLE})
public class AAAWebFragment extends BaseFragment {
    @AutoWired
    String title; // 标题
    @AutoWired
    String url; // 网络地址

    @BindView(R.id.webView)
    WebView webView;

    private String mShareContent;

    @Override
    protected int getLayoutId() {
        return R.layout.aaa_web_fragment;
    }

    @Override
    protected void initArgs() {
        XRouter.getInstance().inject(this);
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(R.string.dealer_manage);
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.finish();
            }
        });
        return titleBar;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void initViews() {
        // 让android支持javaScript
        webView.getSettings().setJavaScriptEnabled(true);
        // webView访问url
        webView.loadUrl(url);
        webView.addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");
        // 地址栏看不到地址
        webView.setWebViewClient(new MyWebView());
    }

    private final class InJavaScriptLocalObj {
        @JavascriptInterface
        public void getShareContent (String content) {
            mShareContent = content;
            KLog.d(content);
        }
    }

    private static final class MyWebView extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            return super.shouldOverrideUrlLoading(view, url);
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            view.loadUrl("javascript:" +
                    "window.local_obj.getStareContent(document.querySelector('meta[name=\"description\"]').getAttribute('content')");

            super.onPageFinished(view, url);
        }
    }

    @Override
    public void onDestroy() {
        webView = null;
        super.onDestroy();
    }
}
