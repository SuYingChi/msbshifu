package com.msht.master.FunctionView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

/**
 * Created by hei on 2016/12/21.
 */

public class SubmitBill extends AppCompatActivity{

    private EditText et_detect;//上门检测费
    private EditText et_material;//维修材料费
    private EditText et_serve;//服务费
    private EditText et_memo;//备注
    private Button btn_submit;//提交按钮
    private TextView tv_total;
    private CustomDialog customDialog;
    private String id;
    private String token;
    private String detect_fee;
    private String material_fee;
    private String serve_fee;
    private int SubmitCode=0x001;

    private Handler submitHandler =new SubmitHandler(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);
        token = (String) SharedPreferencesUtils.getData(getApplicationContext(), SPConstants.TOKEN, "");
        Intent data=getIntent();
        id = data.getStringExtra("id");
        detect_fee=data.getStringExtra("detect_fee");
        material_fee=data.getStringExtra("material_fee");
        serve_fee=data.getStringExtra("serve_fee");
        customDialog = new CustomDialog(this, "正在加载...");
        initHeaderTitle();
        initView();
        initEvent();
    }

    private void initEvent() {
        MyTextWatcher myTextWatcher = new MyTextWatcher();
        et_detect.addTextChangedListener(myTextWatcher);
        et_material.addTextChangedListener(myTextWatcher);
        et_serve.addTextChangedListener(myTextWatcher);

        et_detect.setText(detect_fee);
        et_material.setText(material_fee);
        et_serve.setText(serve_fee);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String detect_fee = TextUtils.isEmpty(et_detect.getText().toString().trim()) ? "0" : et_detect.getText().toString().trim();
                String material_fee = TextUtils.isEmpty(et_material.getText().toString().trim()) ? "0" : et_material.getText().toString().trim();
                String serve_fee = TextUtils.isEmpty(et_serve.getText().toString().trim()) ? "0" : et_serve.getText().toString().trim();
                String memo = et_memo.getText().toString().trim();
                submitBill(detect_fee, material_fee, serve_fee, memo);
            }
        });
    }

    private void initView() {
        et_detect = (EditText) findViewById(R.id.et_detect);
        et_material = (EditText) findViewById(R.id.et_material);
        et_serve = (EditText) findViewById(R.id.et_serve);
        tv_total = (TextView) findViewById(R.id.tv_total);
        et_memo = (EditText) findViewById(R.id.et_memo);
        btn_submit = (Button) findViewById(R.id.btn_submit);

    }
    private void initHeaderTitle() {
        findViewById(R.id.id_goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("账单");
    }
    public class MyTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            double totals = 0.0;
            //上门检测费
            String detect = et_detect.getText().toString().trim();
            //材料费
            String material = et_material.getText().toString().trim();
            //维修服务费
            String serve_fee = et_serve.getText().toString().trim();
            if (!detect.equals("") && !detect.equals(".")) {
                double det = Double.valueOf(detect);
                totals = det;
            }
            if (!serve_fee.equals("") && !serve_fee.equals(".")) {
                double ser = Double.valueOf(serve_fee);
                totals = totals + ser;
            }
            if (!material.equals("") && !material.equals(".")) {
                double mat = Double.valueOf(material);
                totals = totals + mat;
            }
            double tals = BigDecinmals(totals);
            String tal = String.valueOf(tals);
            tv_total.setText(tal);
            if (!TextUtils.isEmpty(detect) || !TextUtils.isEmpty(material) || !TextUtils.isEmpty(serve_fee)) {
                btn_submit.setEnabled(true);
            } else {
                btn_submit.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
    private double BigDecinmals(double totals) {
        long lon = Math.round(totals * 100);
        double tot = lon / 100.0;
        return tot;
    }
    private void submitBill(String detect_fee, String material_fee, String serve_fee, String memo_fee) {
        customDialog.show();
        String detect = TextUtils.isEmpty(detect_fee) ? "0" : detect_fee;
        String material = TextUtils.isEmpty(material_fee) ? "0" : material_fee;
        String serve = TextUtils.isEmpty(serve_fee) ? "0" : serve_fee;
        String memo = memo_fee;
        String validateURL = NetConstants.REPAIR_ORDER_BILL;
        Map<String, String> textParams = new HashMap<String, String>();
        textParams.put("token", token);
        textParams.put("id", id);
        textParams.put("detect_fee", detect);
        textParams.put("material_fee", material);
        textParams.put("serve_fee", serve);
        textParams.put("memo", memo);
        SendRequestUtils.PostDataFromService(textParams, validateURL, submitHandler);
    }
    private class SubmitHandler extends Handler{
        private WeakReference<SubmitBill> ref;

        SubmitHandler(SubmitBill activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final SubmitBill activity = ref.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            switch (msg.what) {
                case SendRequestUtils.SUCCESS:
                    activity.submitBillSuccess(msg);
                    break;
                case SendRequestUtils.FAILURE:
                    AppToast.makeShortToast(activity, "网络连接失败");
                    break;
                case SendRequestUtils.ERRORCODE:
                    AppToast.makeShortToast(activity, "网络连接失败");
                    break;
            }
            customDialog.dismiss();
        }
    }

    private void submitBillSuccess(Message msg) {
        setResult(SubmitCode);
        new PromptDialog.Builder(this)
                .setTitle(R.string.dialog_title)
                .setViewStyle(PromptDialog.VIEW_STYLE_TITLEBAR_SKYBLUE)
                .setMessage("提交账单成功")
                .setButton1("确定", new PromptDialog.OnClickListener() {

                    @Override
                    public void onClick(Dialog dialog, int which) {
                        finish();
                        dialog.dismiss();
                    }
                }).show();
    }
}
