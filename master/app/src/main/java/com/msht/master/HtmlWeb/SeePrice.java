package com.msht.master.HtmlWeb;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
        Wseeprice=(WebView)findViewById(R.id.id_web_price);
        progress = (ProgressBar) findViewById(R.id.progress);
        if (Id.equals("5")){
            priceUrl= NetConstants.SHUI_GUAN;
        }else if (Id.equals("6")){
            priceUrl=NetConstants.SHUI_LONG_TOU;
        }else if (Id.equals("7")){
            priceUrl=NetConstants.HUASA;
        }else if (Id.equals("8")){
            priceUrl=NetConstants.MATONG;
        }else if (Id.equals("9")){
            priceUrl=NetConstants.YUSHIGUI;
        }else if (Id.equals("10")){
            priceUrl=NetConstants.RANQIZAO;
        }else if (Id.equals("11")){
            priceUrl=NetConstants.RESHUIQI;
        }else if (Id.equals("12")){
            priceUrl=NetConstants.YOUYANJI;
        }else if (Id.equals("13")){
            priceUrl=NetConstants.XIAODUGUI;
        }else if (Id.equals("14")){
            priceUrl=NetConstants.DIANNAO;
        }else if (Id.equals("15")){
            priceUrl=NetConstants.KONGTIAO;
        }else if (Id.equals("16")){
            priceUrl=NetConstants.XIYIJI;
        }else if (Id.equals("17")){
            priceUrl=NetConstants.BINGXIANG;
        }else if (Id.equals("18")){
            priceUrl=NetConstants.DENGJU;
        }else if (Id.equals("19")){
            priceUrl=NetConstants.KAIGUANCHAZUO;
        }else if (Id.equals("20")){
            priceUrl=NetConstants.DIANLU;
        }else if (Id.equals("21")){
            priceUrl=NetConstants.KAISUOHUANSUO;
        }else if (Id.equals("22")){
            priceUrl=NetConstants.GUANDAOSHUTONG;
        }else if (Id.equals("24")){
            priceUrl=NetConstants.QIANGMIANDAKONG;
        }else if (Id.equals("25")){
            priceUrl=NetConstants.JIAJU;
        }else if (Id.equals("26")){
            priceUrl=NetConstants.MEN;
        }else if (Id.equals("27")){
            priceUrl=NetConstants.CHUANG;
        }else if (Id.equals("28")){
            priceUrl=NetConstants.YIJIAWUJIN;
        }else if (Id.equals("29")){
            priceUrl=NetConstants.FANGDAOWANG;
        }else if (Id.equals("30")){
            priceUrl=NetConstants.QINGXI_RANQIZAO;
        }else if (Id.equals("31")){
            priceUrl=NetConstants.QINGXI_RESHUIQI;
        }else if (Id.equals("32")){
            priceUrl=NetConstants.QINGXI_YOUYANJI;
        }else if (Id.equals("33")){
            priceUrl=NetConstants.QINGXI_KONGTIAO;
        }else if (Id.equals("34")){
            priceUrl=NetConstants.QINGXI_BINGXIANG;
        }else if (Id.equals("35")){
            priceUrl=NetConstants.QINGXI_XIYIJI;
        }
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
