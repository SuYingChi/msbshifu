package com.msht.master.HtmlWeb;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.msht.master.Base.BaseHandler;
import com.msht.master.Common.CommonMethod;
import com.msht.master.Constants.NetConstants;
import com.msht.master.Constants.SPConstants;
import com.msht.master.Model.BasicInfoModel;
import com.msht.master.R;
import com.msht.master.Utils.AppToast;
import com.msht.master.Utils.NetWorkUtil;
import com.msht.master.Utils.SendRequestUtils;
import com.msht.master.Utils.SharedPreferencesUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BuyInsurance extends AppCompatActivity {
    private ImageView backimg;
    private WebView Winsurance;

    private Handler mhandler = new InfoHandler(this);
    private ProgressBar progress;

    private void initShow(Message msg) {
        try {
            Gson gson = new Gson();
            BasicInfoModel model = gson.fromJson(msg.obj.toString(), BasicInfoModel.class);
            if (2==model.result_code) {
                //未登陆
            } else {
                if (model.result.equals("success")) {
                    //获取成功
                    String username = model.data.username;
                    String phone = model.data.phone;
                    String idCard = model.data.idCard;
                   // String url = "http://wxmsb.cpic.com.cn/fmsb/xpxhtml/pagebeijin/b01.html?empNo=HANZ5643&productCode=GY0617_01&productType=qxb&productName=*%25EF%25BD%25BA%250F%2516$%25EF%25BD%25B3%25EF%25BE%259Di%2508%2511%251F%25E4%25BA%25A4&insuranceAmount=220000&money=1.00&countCode=HANZ5643&delayedDay=1&" + String.format("parentName=%s&parentIdNum=%s&parentTel=%s", username, idCard, phone);
                    String url ="http://wxmsb.cpic.com.cn/fmsb/xpxhtml/pagebeijin/b01.html?empNo=HAN30321&productCode=GY0617_01&productType=qxb&productName=*%EF%BD%BA%0F%16$%EF%BD%B3%EF%BE%9Di%08%11%1F%E4%BA%A4&insuranceAmount=220000&money=1.00&countCode=HAN30321&delayedDay=1&" + String.format("parentName=%s&parentIdNum=%s&parentTel=%s", username, idCard, phone);
                    /**
                     * http://wxmsb.cpic.com.cn/fmsb/xpxhtml/pagebeijin/b01.html?empNo=HANZ5643&productCode=GY0617_01&productType=qxb&productName=*%25EF%25BD%25BA%250F%2516$%25EF%25BD%25B3%25EF%25BE%259Di%2508%2511%251F%25E4%25BA%25A4&insuranceAmount=220000&money=1.00&countCode=HANZ5643&delayedDay=1&parentName=张三&parentIdNum=32118119921028281X&parentTel=15396961809
                     */
                    initWeb(url);
                } else {
                    //失败
                    AppToast.makeShortToast(this,"网络连接失败");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_insurance);
        initHeaderTitle();

        Winsurance = (WebView) findViewById(R.id.id_insurance);
        progress = (ProgressBar) findViewById(R.id.progress);
        initUrl();
    }

    private void initHeaderTitle() {
        backimg = (ImageView) findViewById(R.id.id_goback);
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("购买保险");
        backimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initUrl() {
        String token = (String) SharedPreferencesUtils.getData(this, SPConstants.TOKEN, "");
        Map<String, String> textParams = new HashMap<String, String>();
        textParams.put("token", token);
        SendRequestUtils.PostDataFromService(textParams, NetConstants.BASCI_INFO, mhandler);
    }


    private void initWeb(String myurl) {

        //TODO:获取用户信息 用于购买保险
        Winsurance.loadUrl(myurl);
        WebSettings settings = Winsurance.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setDomStorageEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        Winsurance.requestFocusFromTouch();
        Winsurance.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progress.setVisibility(View.INVISIBLE);
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (Winsurance.canGoBack()) {
                Winsurance.goBack();
                return true;
            } else {
                onBackPressed();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    private static class InfoHandler extends BaseHandler<BuyInsurance>{

        public InfoHandler(BuyInsurance object) {
            super(object);
        }

        @Override
        public void onSuccess(BuyInsurance object, Message msg) {
           object.initShow(msg);
        }
    }
}
