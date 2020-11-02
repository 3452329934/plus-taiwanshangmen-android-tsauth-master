package com.zhiyicx.thinksnsplus.base;

import android.os.Message;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.zhiyicx.common.utils.log.LogUtils;
import com.zhiyicx.thinksnsplus.modules.settings.aboutus.CustomWEBActivity;

/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/10/25
 * @Contact master.jungle68@gmail.com
 */
public class BaseWebLoad {

    protected OnWebLoadListener mWebLoadListener;
    protected boolean isAredyLoad;

    public void setWebLoadListener(OnWebLoadListener webLoadListener) {
        mWebLoadListener = webLoadListener;
    }

    protected WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (mWebLoadListener != null && !isAredyLoad) {
                isAredyLoad = true;
                mWebLoadListener.onLoadFinish();
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            CustomWEBActivity.startToWEBActivity(view.getContext(), url);
            return true;
        }
    };

    protected WebChromeClient mWebChromeClient = new WebChromeClient() {

        /**
         * 多窗口的问题
         *
         * @param view
         * @param isDialog
         * @param isUserGesture
         * @param resultMsg
         * @return
         */
        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(view);
            resultMsg.sendToTarget();
            return true;
        }

        /**
         * 进度条
         *
         * @param view
         * @param newProgress
         */
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            LogUtils.d("onProgressChanged::" + newProgress);
            if (newProgress == 100 && mWebLoadListener != null && !isAredyLoad) {
                isAredyLoad = true;
                mWebLoadListener.onLoadFinish();
            }
        }

    };

    protected void destryWeb(WebView webView) {
        if (webView != null) {
            webView.clearHistory();
            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.loadUrl("about:blank");
            webView.stopLoading();
            webView.setWebChromeClient(null);
            webView.setWebViewClient(null);
            webView.destroy();
        }
    }

    public interface OnWebLoadListener {
        void onLoadFinish();

    }
}
