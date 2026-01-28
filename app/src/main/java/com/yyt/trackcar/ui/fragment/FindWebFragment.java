package com.yyt.trackcar.ui.fragment;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.Gravity;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.just.agentweb.action.PermissionInterceptor;
import com.just.agentweb.core.AgentWeb;
import com.just.agentweb.core.client.DefaultWebClient;
import com.just.agentweb.core.web.AbsAgentWebSettings;
import com.just.agentweb.core.web.AgentWebConfig;
import com.just.agentweb.core.web.IAgentWebSettings;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xutil.common.logger.Logger;
import com.xuexiang.xutil.net.JsonUtil;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.R;
import com.yyt.trackcar.ui.base.BaseWebViewFragment;
import com.yyt.trackcar.ui.widget.webview.WebLayout;

import java.util.HashMap;

import butterknife.BindView;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      FindFragment
 * @ author:        QING
 * @ createTime:    2020-03-02 17:30
 * @ describe:      TODO 发现页面
 */
@Page(name = " FindWeb", anim = CoreAnim.none)
public class FindWebFragment extends BaseWebViewFragment {
    @BindView(R.id.titleBar)
    TitleBar mTitleBar; // titleBar
    @BindView(R.id.llContainer)
    LinearLayout mContainer; // 布局容器

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_find_web;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = mTitleBar;
        titleBar.setCenterGravity(Gravity.START | Gravity.CENTER);
        titleBar.disableLeftView();
        titleBar.setTitle(R.string.find);
        titleBar.getCenterText().getPaint().setFakeBoldText(true);
        return titleBar;
    }

    @Override
    protected void initViews() {
        mAgentWeb = AgentWeb.with(this)
                //传入AgentWeb的父控件。
                .setAgentWebParent(mContainer, -1,
                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT))
                //设置进度条颜色与高度，-1为默认值，高度为2，单位为dp。
                .useDefaultIndicator(-1, 3)
                //设置 IAgentWebSettings。
                .setAgentWebWebSettings(getSettings())
                //WebViewClient ， 与 WebView 使用一致 ，但是请勿获取WebView调用setWebViewClient(xx)方法了,
                // 会覆盖AgentWeb DefaultWebClient,同时相应的中间件也会失效。
                .setWebViewClient(mWebViewClient)
                //WebChromeClient
                .setWebChromeClient(mWebChromeClient)
//                //设置WebChromeClient中间件，支持多个WebChromeClient，AgentWeb 3.0.0 加入。
//                .useMiddlewareWebChrome(getMiddlewareWebChrome())
//                //设置WebViewClient中间件，支持多个WebViewClient， AgentWeb 3.0.0 加入。
//                .useMiddlewareWebClient(getMiddlewareWebClient())
//                //权限拦截 2.0.0 加入。
                .setPermissionInterceptor(mPermissionInterceptor)
                //严格模式 Android 4.2.2 以下会放弃注入对象 ，使用AgentWebView没影响。
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
//                //自定义UI  AgentWeb3.0.0 加入。
//                .setAgentWebUIController(new UIController(getActivity()))
                //参数1是错误显示的布局，参数2点击刷新控件ID -1表示点击整个布局都刷新， AgentWeb 3.0.0 加入。
//                .setMainFrameErrorView(R.layout.agentweb_error_page, -1)
                .setWebLayout(new WebLayout(getActivity()))
                //打开其他页面时，弹窗质询用户前往其他应用 AgentWeb 3.0.0 加入。
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.DISALLOW)
                //拦截找不到相关页面的Url AgentWeb 3.0.0 加入。
                .interceptUnkownUrl()
                //创建AgentWeb。
                .createAgentWeb()
                .ready()//设置 WebSettings。
                //WebView载入该url地址的页面并显示。
//                .go("https://www.baidu.com");
                .go("");
        if (BuildConfig.DEBUG)
            AgentWebConfig.debug();
        // AgentWeb 没有把WebView的功能全面覆盖 ，所以某些设置 AgentWeb 没有提供，请从WebView方面入手设置。
        mAgentWeb.getWebCreator().getWebView().setOverScrollMode(WebView.OVER_SCROLL_NEVER);
    }

    /**
     * 下载服务设置
     *
     * @return IAgentWebSettings
     */
    public IAgentWebSettings getSettings() {
        return new AbsAgentWebSettings() {
            @Override
            protected void bindAgentWebSupport(AgentWeb agentWeb) {
                this.mAgentWeb = agentWeb;
            }
        };
    }

    //===================WebChromeClient 和 WebViewClient===========================//

    /**
     * 和浏览器相关，包括和JS的交互
     */
    protected WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            //网页加载进度
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
        }
    };

    /**
     * 和网页url加载相关，统计加载时间
     */
    protected WebViewClient mWebViewClient = new WebViewClient() {
        private HashMap<String, Long> mTimer = new HashMap<>();

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request,
                                    WebResourceError error) {
            super.onReceivedError(view, request, error);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return shouldOverrideUrlLoading(view, request.getUrl() + "");
        }

        @Nullable
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view,
                                                          WebResourceRequest request) {
            return super.shouldInterceptRequest(view, request);
        }

        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, String url) {
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            mTimer.put(url, System.currentTimeMillis());
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (BuildConfig.DEBUG && mTimer.get(url) != null) {
                long overTime = System.currentTimeMillis();
                Long startTime = mTimer.get(url);
                //统计页面的使用时长
                if (startTime != null)
                    Logger.i(" page mUrl:" + url + "  used time:" + (overTime - startTime));
            }
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request,
                                        WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description,
                                    String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
    };

    /**
     * 权限申请拦截器
     */
    protected PermissionInterceptor mPermissionInterceptor = new PermissionInterceptor() {
        /**
         * PermissionInterceptor 能达到 url1 允许授权， url2 拒绝授权的效果。
         * @param url
         * @param permissions
         * @param action
         * @return true 该Url对应页面请求权限进行拦截 ，false 表示不拦截。
         */
        @Override
        public boolean intercept(String url, String[] permissions, String action) {
            if (BuildConfig.DEBUG)
                Logger.i("mUrl:" + url + "  permission:" + JsonUtil.toJson(permissions) + " " +
                        "action:" + action);
            return false;
        }
    };

}
