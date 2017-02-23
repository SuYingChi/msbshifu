package com.msht.master.myview;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.msht.master.Constants.NetConstants;
import com.msht.master.Constants.SPConstants;
import com.msht.master.LoginActivity;
import com.msht.master.R;
import com.msht.master.UIView.CustomDialog;
import com.msht.master.UIView.PromptDialog;
import com.msht.master.Utils.SendRequestUtils;
import com.msht.master.Utils.SharedPreferencesUtils;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FeedBack extends AppCompatActivity {
    private ImageView backimg;
    private EditText et_feed;
    private Button btn_send;

    private final int SUCCESS = 1;
    private final int FAILURE = 0;
    private final int ERRORCODE = 2;
    private CustomDialog customDialog;

    Handler requestHandler = new Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case SUCCESS:
                    customDialog.dismiss();
                    try {
                        JSONObject object = new JSONObject(msg.obj.toString());
                        String result_code = object.optString("result_code");
                        String Results = object.optString("result");
                        String Error = object.optString("error");
                        if (result_code.equals("2")) {
                            gologin();
                        } else {
                            if (Results.equals("success")) {
                                initSuccess();
                            } else {
                                faifure(Error);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case FAILURE:
                    customDialog.dismiss();
                    Toast.makeText(FeedBack.this, "网络请求出错!",
                            Toast.LENGTH_SHORT)
                            .show();
                    break;
                case ERRORCODE:
                    customDialog.dismiss();
                    Toast.makeText(FeedBack.this, "网络请求出错!",
                            Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    private void initSuccess() {
        new PromptDialog.Builder(this)
                .setTitle(R.string.dialog_title)
                .setViewStyle(PromptDialog.VIEW_STYLE_TITLEBAR_SKYBLUE)
                .setMessage("您反馈的意见已提交")
                .setButton1("确定", new PromptDialog.OnClickListener() {

                    @Override
                    public void onClick(Dialog dialog, int which) {
                        finish();
                        dialog.dismiss();

                    }
                }).show();
    }

    private void gologin() {
        Intent mainintent = new Intent(this, LoginActivity.class);
        mainintent.putExtra("state", 2);
        startActivity(mainintent);
        finish();
    }

    private void faifure(String error) {
        new PromptDialog.Builder(this)
                .setTitle(R.string.dialog_title)
                .setViewStyle(PromptDialog.VIEW_STYLE_TITLEBAR_SKYBLUE)
                .setMessage(error)
                .setButton1("确定", new PromptDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        dialog.dismiss();

                    }
                }).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        initHeaderView();
        customDialog = new CustomDialog(this, "正在加载");
        et_feed = (EditText) findViewById(R.id.id_et_feedidea);
        btn_send = (Button) findViewById(R.id.id_btn_feed);
        initEvent();
    }

    private void initHeaderView() {
        backimg = (ImageView) findViewById(R.id.id_goback);
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("提交反馈意见");
        backimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initEvent() {
        btn_send.setEnabled(false);
        btn_send.setBackgroundResource(R.drawable.shape_white_corder_button);
        et_feed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(et_feed.getText().toString())) {
                    btn_send.setEnabled(false);
                    btn_send.setBackgroundResource(R.drawable.shape_white_corder_button);
                } else {
                    btn_send.setEnabled(true);
                    btn_send.setBackgroundResource(R.drawable.shape_orange_corner_button);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.show();
                //意见反馈
                String token = (String) SharedPreferencesUtils.getData(getApplicationContext(), SPConstants.TOKEN,"");
                String info = et_feed.getText().toString().trim();
                String validateURL = NetConstants.FEEDBACK;
                Map<String, String> textParams = new HashMap<String, String>();
                textParams.put("token", token);
                textParams.put("info", info);
                SendRequestUtils.PostDataFromService(textParams,validateURL,requestHandler);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("FeedBack"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("FeedBack"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(this);
    }
}
