package com.msht.master.HtmlWeb;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.msht.master.Constants.NetConstants;
import com.msht.master.R;
import com.msht.master.Utils.NetWorkUtil;

/**
 * Created by hei on 2017/1/13.
 */

public class Joinus extends AppCompatActivity {
    private WebView webview;
    private ProgressBar progress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joinus);
        initHeaderTitle();
        initView();
        initWebView();
    }

    private void initView() {
        webview = (WebView) findViewById(R.id.webview);
        progress = (ProgressBar) findViewById(R.id.progress);
    }

    private void initHeaderTitle() {
        findViewById(R.id.id_goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView)findViewById(R.id.tv_title)).setText("加盟协议");
    }
    private void initWebView() {
        String url= "http://msbapp.cn/repairman_h5/xieyi.html";
        webview.loadUrl(url);
        WebSettings settings=webview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        if(NetWorkUtil.IsNetWorkEnable(this)) {
            settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        }else {
            settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        webview.requestFocusFromTouch();
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progress.setVisibility(View.VISIBLE);

            }
        });
    }
}
