package com.msht.master.HtmlWeb;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.msht.master.Base.BaseFragment;
import com.msht.master.Constants.NetConstants;
import com.msht.master.Constants.SPConstants;
import com.msht.master.R;
import com.msht.master.Utils.NetWorkUtil;
import com.msht.master.Utils.SharedPreferencesUtils;

public class CommissionRule extends BaseFragment {
    private WebView Wcommission;
    private String myurl = NetConstants.COMMISSION_RULE;
    private String token;
    private Activity mActivity;
    private View progress;


    public static CommissionRule getInstance(int position){
        CommissionRule commissionRule = new CommissionRule();
        switch (position){
            case 0:
                commissionRule.myurl=NetConstants.COMMISSION_RULE;
                break;
            case 1:
                commissionRule.myurl=NetConstants.PUNISH_RULE;
                break;
            default:
                commissionRule.myurl=NetConstants.COMMISSION_RULE;
                break;
        }
        return commissionRule;

    }

    @Override
    public View initView() {
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.activity_commission_rule, null, false);
        token = (String) SharedPreferencesUtils.getData(getActivity(), SPConstants.TOKEN, "");
        mActivity = getActivity();
        initMyView(mRootView);
        return mRootView;
    }

    private void initMyView(View mRootView) {
        Wcommission = (WebView) mRootView.findViewById(R.id.id_web_commission);
        progress = mRootView.findViewById(R.id.progress);
    }

    @Override
    public void initData() {
        initWeb();
    }


    private void initWeb() {
        Wcommission.loadUrl(myurl);
        WebSettings settings = Wcommission.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        if (NetWorkUtil.IsNetWorkEnable(mActivity)) {
            settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        Wcommission.requestFocusFromTouch();
        Wcommission.setWebViewClient(new WebViewClient() {
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
