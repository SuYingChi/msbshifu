package com.msht.master.HtmlWeb;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.msht.master.Constants.NetConstants;
import com.msht.master.R;
import com.msht.master.Utils.NetWorkUtil;

public class SeePrice extends AppCompatActivity {
    private WebView  Wseeprice;
    private String Id;
    private String priceUrl;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_price);
        initHeaderTitle();
        Intent data=getIntent();
        Id=data.getStringExtra("Id");
        priceUrl=data.getStringExtra("price_web");
        Wseeprice=(WebView)findViewById(R.id.id_web_price);
        progress = (ProgressBar) findViewById(R.id.progress);
        initWeb();

    }

    private void initHeaderTitle() {
        findViewById(R.id.id_goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView)findViewById(R.id.tv_title)).setText("价格手册");
    }
    private void initWeb() {
        Wseeprice.loadUrl(priceUrl);
        WebSettings settings= Wseeprice.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
//        if(NetWorkUtil.IsNetWorkEnable(this)) {
//            settings.setCacheMode(WebSettings.LOAD_DEFAULT);
//        }else {
//            settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//        }
        Wseeprice.requestFocusFromTouch();
        Wseeprice.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.toString());
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
