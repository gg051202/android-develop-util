package com.a26c.android.frame.base;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.a26c.android.frame.R;

/**
 * Created by guilinlin on 16/7/20 11:20.
 * email 973635949@qq.com
 *
 * @desc 1.建议使用HtmlActivity. languch(Activity activity, String mUrl)打开
 * 2.使用时注意在Manifest文件中注册Activity
 */

public class HtmlActivity extends CommonActivity {

    protected WebView webView;
    protected ProgressBar progressBar;
    protected SwipeRefreshLayout swipeRefreshLayout;

    protected String mUrl;
    protected String mTitle;

    /**
     * @param title 如果为空表示不需要标题，根据网页的标题来设置
     */
    public static void languch(Activity activity, @Nullable String title, @NonNull String url) {
        Intent intent = new Intent(activity, HtmlActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        activity.startActivity(intent);
    }

    @Override
    public int getContainLayout() {
        return R.layout.frame_activity_html5;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mUrl = getIntent().getStringExtra("url");
        mTitle = getIntent().getStringExtra("title");
        setTitle("正在加载...");
        progressBar = (ProgressBar) findViewById(R.id.frame_progressBar);
        webView = (WebView) findViewById(R.id.frame_webView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.frame_swipeRefreshLayout);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.loadUrl(mUrl);
    }

    @Override
    protected void setEvent() {


        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

        });
        // 增加顶部进度条
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (!TextUtils.isEmpty(mTitle)) {
                    setTitle(mTitle);
                } else {
                    setTitle(title);
                }
            }

            @SuppressWarnings("static-access")
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    progressBar.setVisibility(view.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    if (View.GONE == progressBar.getVisibility()) {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                    progressBar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.reload();
            }
        });

    }

    @Override
    public void leftOnClickListener(View view) {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            finish();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_close) {
            finish();
        } else if (item.getItemId() == R.id.action_refresh) {
            webView.reload();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.frame_menu_html, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                webView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);

    }
}
