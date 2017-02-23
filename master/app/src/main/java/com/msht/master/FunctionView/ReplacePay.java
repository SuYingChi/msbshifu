package com.msht.master.FunctionView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jdsjlzx.util.LuRecyclerViewStateUtils;
import com.github.jdsjlzx.view.LoadingFooter;
import com.google.gson.Gson;
import com.msht.master.Base.BaseHandler;
import com.msht.master.Constants.NetConstants;
import com.msht.master.Constants.SPConstants;
import com.msht.master.Controls.MyRadioButton;
import com.msht.master.LoginActivity;
import com.msht.master.Model.CreateOrder;
import com.msht.master.Model.EvaluteModel;
import com.msht.master.Model.OrderViewModel;
import com.msht.master.R;
import com.msht.master.UIView.CustomDialog;
import com.msht.master.UIView.PromptDialog;
import com.msht.master.Utils.AppToast;
import com.msht.master.Utils.SendRequestUtils;
import com.msht.master.Utils.SharedPreferencesUtils;
import com.pingplusplus.android.Pingpp;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReplacePay extends AppCompatActivity implements View.OnClickListener {
    private Button btn_send;
    private RadioButton Raalipay, Rawechat, Rayinlian;

    private String token;
    private String channel, type, orderNo;

    private String id;      //订单号
    private String charge;   //

    private CustomDialog customDialog;

    Handler requestHandler =new RequestHandler(this);

    private Handler getOrderDetailHandler=new GetOrderDetailHanlder(this);
    private double replace_pay_amount;
    private TextView tv_order_num;
    private TextView tv_fact_pay;


    private void initShow(Message msg) {
        try {
            Gson gson = new Gson();
            CreateOrder model = gson.fromJson(msg.obj.toString(), CreateOrder.class);
            id = model.data.id;
            charge = model.data.charge;
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(this.toString(),
                "数据=" + charge);
        Pingpp.createPayment(ReplacePay.this, charge);

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

    private void gologin() {
        Intent mainintent = new Intent(this, LoginActivity.class);
        mainintent.putExtra("state", 2);
        startActivity(mainintent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replace_pay);
        token = (String) SharedPreferencesUtils.getData(getApplicationContext(), SPConstants.TOKEN, "");
        customDialog = new CustomDialog(this, "正在加载");
        initHeaderTitle();
        Intent data = getIntent();
        orderNo = data.getStringExtra("orderId");
        initView();
        getOrderDetail();
        initEvent();
    }

    private void initHeaderTitle() {
        findViewById(R.id.id_goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView)findViewById(R.id.tv_title)).setText("付款");
    }

    private void getOrderDetail() {
        customDialog.show();
        String validateURL = NetConstants.REPAIR_ORDER_VIEW;
        Map<String, String> textParams = new HashMap<String, String>();
        textParams.put("token", token);
        textParams.put("id", orderNo);
        SendRequestUtils.PostDataFromService(textParams,validateURL,getOrderDetailHandler);
    }

    private void initEvent() {
        Raalipay.setOnClickListener(this);
        Rawechat.setOnClickListener(this);
        Rayinlian.setOnClickListener(this);
        btn_send.setOnClickListener(this);
    }

    private void initView() {

        tv_order_num = (TextView) findViewById(R.id.tv_order_num);
        tv_fact_pay = (TextView) findViewById(R.id.tv_fact_pay);
        Raalipay = (MyRadioButton) findViewById(R.id.id_radio_alipay);
        Rawechat = (MyRadioButton) findViewById(R.id.id_radio_wechat);
        Rayinlian = (MyRadioButton) findViewById(R.id.id_radio_yinlian);

        btn_send = (Button) findViewById(R.id.id_btn_pay);
        btn_send.setEnabled(false);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_radio_alipay:
                channel = "1";
                btn_send.setEnabled(true);
                break;
            case R.id.id_radio_wechat:
                channel = "5";
                btn_send.setEnabled(true);
                break;
            case R.id.id_radio_yinlian:
                channel = "3";
                btn_send.setEnabled(true);
                break;
            case R.id.id_btn_pay:
                payment();
                break;
            default:
                break;
        }
    }

    private void payment() {
        new PromptDialog.Builder(this)
                .setTitle("提示")
                .setViewStyle(PromptDialog.VIEW_STYLE_TITLEBAR_SKYBLUE)
                .setMessage("请确认是否要进行支付")
                .setButton1("取消", new PromptDialog.OnClickListener() {

                    @Override
                    public void onClick(Dialog dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setButton2("确定", new PromptDialog.OnClickListener() {

                    @Override
                    public void onClick(Dialog dialog, int which) {
                        customDialog.show();
                        //创建订单
                        String userId = "8";
                        type = "3";
                        String validateURL = NetConstants.CREATE_ORDER;
                        Map<String, String> textParams = new HashMap<String, String>();
                        textParams.put("userId", userId);
                        textParams.put("password", token);
                        textParams.put("type", type);
                        textParams.put("amount", replace_pay_amount+"");
                        textParams.put("channel", channel);
                        textParams.put("orderId", orderNo);
                        SendRequestUtils.PostDataFromService(textParams,validateURL,requestHandler);
                        dialog.dismiss();
                    }
                })
                .show();

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Pingpp.REQUEST_CODE_PAYMENT) {
            //if (requestCode == REQUEST_CODE_PAYMENT){
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getExtras().getString("pay_result");
                /* 处理返回值
                 * "success" - payment succeed
                 * "fail"    - payment failed
                 * "cancel"  - user canceld
                 * "invalid" - payment plugin not installed
                 */
                String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
                String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息
                showMsg(result, errorMsg, extraMsg);
            }
        }
    }

    private void showMsg(String result, String errorMsg, String extraMsg) {

        String str = result;
        if (str.equals("success")) {
            str = "缴费成功";
        } else if (str.equals("fail")) {
            str = "缴费失败";
        } else if (str.equals("cancel")) {
            str = "已取消缴费";
        }
        if (null != errorMsg && errorMsg.length() != 0) {
            str += "\n" + errorMsg;
        }
        if (null != extraMsg && extraMsg.length() != 0) {
            str += "\n" + extraMsg;
        }
        if (str.equals("success")) {
            finish();
        } else {
            showdialogs(str);
        }
    }

    private void showdialogs(final String str) {
        new PromptDialog.Builder(this)
                .setTitle("缴费提示")
                .setViewStyle(PromptDialog.VIEW_STYLE_TITLEBAR_SKYBLUE)
                .setMessage(str)
                .setButton1("确定", new PromptDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        if (str.equals("缴费成功")) {
                            setResult(2);
                            finish();
                        }
                        dialog.dismiss();
                    }
                }).show();
    }

    private static class GetOrderDetailHanlder extends BaseHandler<ReplacePay>{


        public GetOrderDetailHanlder(ReplacePay object) {
            super(object);
        }
        @Override
        public void onSuccess(ReplacePay object, Message msg) {
            object.getDataSuccess(msg);
        }

    }

    private void getDataSuccess(Message msg) {
        customDialog.dismiss();
        try{
            Gson gson = new Gson();
            OrderViewModel orderViewModel = gson.fromJson(msg.obj.toString(), OrderViewModel.class);
            if(2==orderViewModel.result_code){
                //重新登陆
                gologin();
            }else{
                if(orderViewModel.result.equals("success")){
                    //更新数据
                    OrderViewModel.OrderViewDetail data = orderViewModel.data;
                    replace_pay_amount = data.replace_pay_amount;
                    tv_fact_pay.setText("￥"+replace_pay_amount +"");
                    tv_order_num.setText(orderNo);
                }else{
                    //失败
                    faifure(orderViewModel.error);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private static class RequestHandler extends BaseHandler<ReplacePay>{

        public RequestHandler(ReplacePay object) {
            super(object);
        }

        @Override
        public void onSuccess(ReplacePay object, Message msg) {
            object.initShow(msg);
        }

        @Override
        public void onFinilly(ReplacePay object) {
            object.customDialog.dismiss();
        }
    }

}
