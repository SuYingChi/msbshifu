package com.msht.master.FunctionView;

import android.app.Dialog;
import android.content.Context;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.msht.master.Constants.NetConstants;
import com.msht.master.Constants.SPConstants;
import com.msht.master.R;
import com.msht.master.UIView.CustomDialog;
import com.msht.master.UIView.PromptDialog;
import com.msht.master.Utils.AppToast;
import com.msht.master.Utils.SendRequestUtils;
import com.msht.master.Utils.SharedPreferencesUtils;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class ConvertOrderReson extends AppCompatActivity {
    private String      token;
    private Button      btn_cancel,btn_ensure;
    private RadioGroup  Group;
    private RadioButton radioOne,radioTwo,radiotThird;
    private RadioButton radioFour,radioFive,radioSix,radioSeven;
    private TextView    textNum;
    private EditText    et_Reason;
    private int num=140;
    private String  id;
    private String  reasonText;
    private Context mContext;
    private CustomDialog customDialog;
    private Handler convertOrderHandler = new ConvertOrderHandler(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert_order_reson);
        mContext=this;
        customDialog = new CustomDialog(this, "正在加载...");
        token = (String) SharedPreferencesUtils.getData(getApplicationContext(), SPConstants.TOKEN, "");
        id=getIntent().getStringExtra("id");
        initHeader();
        initView();
    }
    private void initHeader() {
        ((TextView)findViewById(R.id.tv_title)).setText("转单原因");
        findViewById(R.id.id_goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {finish();}
        });
    }
    private void initView() {
        textNum=(TextView)findViewById(R.id.id_textnum) ;
        Group=(RadioGroup)findViewById(R.id.id_Group);
        radioOne=(RadioButton)findViewById(R.id.id_radioOne);
        radioTwo=(RadioButton)findViewById(R.id.id_radioTwo);
        radiotThird=(RadioButton)findViewById(R.id.id_radiotThird);
        radioFour=(RadioButton)findViewById(R.id.id_radioFour);
        radioFive=(RadioButton)findViewById(R.id.id_radioFive);
        radioSix=(RadioButton)findViewById(R.id.id_radioSix);
        radioSeven=(RadioButton)findViewById(R.id.id_radioSeven);
        et_Reason=(EditText)findViewById(R.id.id_memo);
        btn_cancel=(Button)findViewById(R.id.id_cancel);
        btn_ensure=(Button)findViewById(R.id.id_btn_ok);
        Group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i==radioOne.getId()){
                    reasonText=radioOne.getText().toString().trim();
                }else if (i==radioTwo.getId()){
                    reasonText=radioTwo.getText().toString().trim();
                }else if (i==radiotThird.getId()){
                    reasonText=radiotThird.getText().toString().trim();
                }else if (i==radioFour.getId()){
                    reasonText=radioFour.getText().toString().trim();
                }else if (i==radioFive.getId()){
                    reasonText=radioFive.getText().toString().trim();
                }else if (i==radioSix.getId()){
                    reasonText=radioSix.getText().toString().trim();
                }else if (i==radioSeven.getId()){
                    reasonText=et_Reason.getText().toString().trim();
                }
            }
        });
        et_Reason.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                int number=num-s.length();
                String Wnum=String.valueOf(number);
                textNum.setText(Wnum);
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_ensure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestService();
            }
        });
    }
    private void RequestService() {
        String reason = "";
        if (radioSeven.isChecked()) {
            reason = et_Reason.getText().toString().trim();
        } else {
            reason = reasonText+","+et_Reason.getText().toString().trim();
        }
        if (TextUtils.isEmpty(reason)) {
            Toast.makeText(mContext, "请输入原因，帮助我们改进", Toast.LENGTH_SHORT).show();
        } else {
            //转单
            customDialog.show();
            String validateURL = NetConstants.REPAIR_ORDER_CANCAL;
            Map<String, String> textParams = new HashMap<String, String>();
            textParams.put("token", token);
            textParams.put("id", id);
            textParams.put("reason", reason);
            SendRequestUtils.PostDataFromService(textParams, validateURL, convertOrderHandler);
        }
    }
    private static class ConvertOrderHandler extends Handler {
        private WeakReference<ConvertOrderReson> ref;
        ConvertOrderHandler(ConvertOrderReson activity) {
            ref = new WeakReference<>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            final ConvertOrderReson activity = ref.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            switch (msg.what) {
                case SendRequestUtils.SUCCESS:
                    activity.convertOrderSuccess(msg);
                    break;
                case SendRequestUtils.FAILURE:
                    AppToast.makeShortToast(activity, "网络连接失败");
                    break;
                case SendRequestUtils.ERRORCODE:
                    AppToast.makeShortToast(activity, "网络连接失败");
                    break;
            }
            activity.customDialog.dismiss();
        }
    }
    private void convertOrderSuccess(Message msg) {
        new PromptDialog.Builder(this)
                .setTitle(R.string.dialog_title)
                .setViewStyle(PromptDialog.VIEW_STYLE_TITLEBAR_SKYBLUE)
                .setMessage("转单成功")
                .setButton1("确定", new PromptDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        setResult(0x003);
                        finish();
                        dialog.dismiss();
                    }
                }).show();
    }

}
