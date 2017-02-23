package com.msht.master.HtmlWeb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.msht.master.Constants.NetConstants;
import com.msht.master.R;
import com.msht.master.Utils.NetWorkUtil;

public class Agreement extends AppCompatActivity {
    private WebView Wagreement;
    private String  myurl= NetConstants.REGIST_AGREEMENT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);
        initHeaderTitle();
        Wagreement=(WebView)findViewById(R.id.id_web_agreement);
        initWeb();
    }

    private void initHeaderTitle() {
        findViewById(R.id.id_goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("服务协议");
    }

    private void initWeb() {
        Wagreement.loadUrl(myurl);
        WebSettings settings=Wagreement.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        Wagreement.requestFocusFromTouch();
        Wagreement.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

}
