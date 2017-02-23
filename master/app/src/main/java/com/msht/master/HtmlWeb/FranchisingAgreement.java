package com.msht.master.HtmlWeb;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.msht.master.Constants.NetConstants;
import com.msht.master.FunctionView.ApplyIdentify;
import com.msht.master.R;
import com.msht.master.Utils.NetWorkUtil;

/**
 * Created by hei123 on 2016/12/1.
 */

public class FranchisingAgreement extends Activity {

    private Button btn_next;
    private Button btn_refuse;
    private ImageView titlebar_goback;
    private WebView webview;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_franchising);
        initHeaderTitle();
        initView();
        initEvent();
        initWebView();
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

    private void initHeaderTitle() {
        titlebar_goback = (ImageView) findViewById(R.id.id_goback);
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("加盟协议");
    }

    private void initEvent() {
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FranchisingAgreement.this, ApplyIdentify.class);
                startActivityForResult(intent, 0);
            }
        });
        btn_refuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(0);
                //拒绝返回0
                finish();
            }
        });
        titlebar_goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(0);
                finish();
            }
        });
    }

    private void initView() {
        btn_next = (Button) findViewById(R.id.btn_next);
        btn_refuse = (Button)findViewById(R.id.btn_refuse);
        webview = (WebView) findViewById(R.id.webview);
        progress = (ProgressBar) findViewById(R.id.progress);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 2) {
            //当提交认证成功时
            //已经提交认证
            setResult(2);
            finish();
        } else {
            finish();
        }
    }
}
