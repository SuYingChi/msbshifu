package com.msht.master.FunctionView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.msht.master.Base.BaseHandler;
import com.msht.master.Common.CommonMethod;
import com.msht.master.Constants.NetConstants;
import com.msht.master.Constants.SPConstants;
import com.msht.master.Model.ApplyWithDrawModel;
import com.msht.master.Model.BankCardModel;
import com.msht.master.Model.WithDrawBiggestAmountModel;
import com.msht.master.R;
import com.msht.master.UIView.CustomDialog;
import com.msht.master.UIView.PromptDialog;
import com.msht.master.Utils.SendRequestUtils;
import com.msht.master.Utils.SharedPreferencesUtils;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WithDraw extends AppCompatActivity implements View.OnClickListener {
    private RelativeLayout Rselect;
    private ImageView bankimg;
    private TextView tv_bank, tv_cardNo;
    private EditText et_amount;
    private Button btn_all, btn_send;
    private String token;
    private String Id, Bank, CardNo;
    private boolean bankListFinsh=false;
    private boolean biggestFinish=false;


    private final int WITH_CODE = 2;

    private final int SELECT_BANK_CARD = 1;
    private TextView tv_next_tuesday;

    private CustomDialog customDialog;

    private Handler getBankCardListHandler = new GetBankCardListHandler(this);
    private Handler getBiggestAmountHandler=new GetBiggestAmountHanlder(this);
    private Handler applyWithDrawHandler =new ApplyWithDrawHandler(this);
    private String can_withdrawals;


    private void showDialogs() {
        new PromptDialog.Builder(this)
                .setTitle("温馨提示")
                .setViewStyle(PromptDialog.VIEW_STYLE_TITLEBAR_SKYBLUE)
                .setMessage("请添加银行卡")
                .setButton1("确定", new PromptDialog.OnClickListener() {

                    @Override
                    public void onClick(Dialog dialog, int which) {

                        Intent addbank = new Intent(WithDraw.this, AddBankCard.class);
                        startActivity(addbank);
                        dialog.dismiss();
                        finish();

                    }
                }).show();
    }

    private void AppySuccess(String apply_time, String expect_time) {
        Intent apply = new Intent(this, DrawSuccess.class);
        apply.putExtra("apply_time", apply_time);
        apply.putExtra("expect_time", expect_time);
        startActivityForResult(apply, 2);
    }

    private void initShow(ArrayList<BankCardModel.BankCardDetail> bankList) {
        Id = bankList.get(0).id + "";
        Bank = bankList.get(0).bank;
        CardNo = bankList.get(0).card;
        if (Bank.equals("招商银行")) {
            bankimg.setImageResource(R.drawable.cmbc_h);
        }
        tv_bank.setText(Bank);
        tv_cardNo.setText("(" + CardNo + ")");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_draw);
        initView();
        token = (String) SharedPreferencesUtils.getData(getApplicationContext(), SPConstants.TOKEN, "");
        customDialog = new CustomDialog(this, "正在加载");
        initHeaderTitle();
        getbankData();
        getAmountData();
        initEvent();
        getNextTuesday();
    }

    private void getNextTuesday() {
        int i = 0;
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.WEEK_OF_YEAR, 1);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
        Date time = cal.getTime();
        try {
            i = daysBetween(date, time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        SimpleDateFormat gettime = new SimpleDateFormat("MM-dd");
        String nexttuesday = gettime.format(time);
        String text = "每周二批量到账，本次提现预计" + nexttuesday + "(" + i + "天后)" + "到账23：59分前到账，感谢您的支持和理解";
        int bstart = text.indexOf(nexttuesday);
        int bend = bstart + nexttuesday.length() + 5;
        SpannableStringBuilder style = new SpannableStringBuilder(text);
        style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)), bstart, bend, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_next_tuesday.setText(style);
    }

    /**
     * 计算两个日期之间相差的天数
     *
     * @param smdate 较小的时间
     * @param bdate  较大的时间
     * @return 相差天数
     * @throws Exception
     */
    public static int daysBetween(Date smdate, Date bdate) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        smdate = sdf.parse(sdf.format(smdate));
        bdate = sdf.parse(sdf.format(bdate));
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);
        return Integer.parseInt(String.valueOf(between_days));
    }

    private void DismissDialog(){
        if(bankListFinsh&&biggestFinish){
            customDialog.dismiss();
        }
    }

    private void initHeaderTitle() {
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("提现");
        findViewById(R.id.id_goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case WITH_CODE:
                if (resultCode == 2) {
                    setResult(2);
                    finish();
                }
                break;
            case SELECT_BANK_CARD:
                if (resultCode == 1) {
                    Id = data.getIntExtra("id",0)+"";
                    Bank = data.getStringExtra("bank");
                    CardNo = data.getStringExtra("card");
                    tv_bank.setText(Bank);
                    tv_cardNo.setText("(" + CardNo + ")");

                }
            default:
                break;
        }
    }

    private void initView() {
        Rselect = (RelativeLayout) findViewById(R.id.id_bank_layout);
        bankimg = (ImageView) findViewById(R.id.id_bank_img);
        tv_bank = (TextView) findViewById(R.id.id_tv_bank);
        tv_cardNo = (TextView) findViewById(R.id.id_tv_card);
        et_amount = (EditText) findViewById(R.id.id_et_amonut);
        btn_all = (Button) findViewById(R.id.id_btn_all);
        btn_send = (Button) findViewById(R.id.id_btn_send);
        tv_next_tuesday = (TextView) findViewById(R.id.tv_next_tuesday);
        btn_send.setEnabled(false);
    }

    private void getbankData() {
        bankListFinsh=false;
        customDialog.show();
        //银行卡列表
        String validateURL = NetConstants.BANKCARD_LIST;
        Map<String, String> textParams = new HashMap<String, String>();
        textParams.put("token", token);
        SendRequestUtils.PostDataFromService(textParams, validateURL, getBankCardListHandler);
    }

    private void getAmountData() {
        biggestFinish=false;
        //可提现金额
        String validateURL = NetConstants.CAN_WITHDRAWALS;
        Map<String, String> textParams = new HashMap<String, String>();
        textParams.put("token", token);
        SendRequestUtils.PostDataFromService(textParams, validateURL, getBiggestAmountHandler);

    }

    private void initEvent() {
        Rselect.setOnClickListener(this);
        btn_all.setOnClickListener(this);
        et_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(et_amount.getText().toString())) {
                    btn_send.setEnabled(false);
                } else {
                    btn_send.setEnabled(true);
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
                //提交提现申请
                String draw_amount = et_amount.getText().toString().trim();
                String validateURL = NetConstants.WITHDRAWALS;
                Map<String, String> textParams = new HashMap<String, String>();
                textParams.put("token", token);
                textParams.put("bank", Id);
                textParams.put("amount", draw_amount);
                SendRequestUtils.PostDataFromService(textParams, validateURL, applyWithDrawHandler);

            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_bank_layout:
                selectBank();
                break;
            case R.id.id_btn_all:
                et_amount.setText(can_withdrawals);
                break;
            default:
                break;
        }
    }

    private void GetBankCardListSuccess(Message msg) {
        bankListFinsh=true;
        DismissDialog();
        try {
            Gson gson = new Gson();
            BankCardModel bankCardModel = gson.fromJson(msg.obj.toString(), BankCardModel.class);
            if (2 == bankCardModel.result_code) {
                //重新登陆
                CommonMethod.goLogin(new WeakReference<Activity>(this), 2);
            } else {
                if (bankCardModel.result.equals("success")) {
                    //更新数据
                    ArrayList<BankCardModel.BankCardDetail> data = bankCardModel.data;
                    if (data.size() == 0) {
                        showDialogs();
                    } else {
                        initShow(data);
                    }
                } else {
                    //失败
                    CommonMethod.faifure(new WeakReference<Activity>(this), bankCardModel.error);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectBank() {
        Intent selectbankcard = new Intent(WithDraw.this, SelectBankCard.class);
        startActivityForResult(selectbankcard, 1);
    }

    private static class GetBankCardListHandler extends BaseHandler<WithDraw> {

        public GetBankCardListHandler(WithDraw object) {
            super(object);
        }

        @Override
        public void onSuccess(WithDraw object, Message msg) {
            object.GetBankCardListSuccess(msg);
        }

        @Override
        public void onFinilly(WithDraw object) {
            super.onFinilly(object);
        }
    }

    private static class GetBiggestAmountHanlder extends BaseHandler<WithDraw> {

        public GetBiggestAmountHanlder(WithDraw object) {
            super(object);
        }

        @Override
        public void onSuccess(WithDraw object, Message msg) {
            object.getBiggestAmountSuccess(msg);
        }
    }

    private void getBiggestAmountSuccess(Message msg) {
        biggestFinish=true;
        DismissDialog();
        try {
            Gson gson = new Gson();
            WithDrawBiggestAmountModel model = gson.fromJson(msg.obj.toString(), WithDrawBiggestAmountModel.class);
            if (2 == model.result_code) {
                //重新登陆
                CommonMethod.goLogin(new WeakReference<Activity>(this), 2);
            } else {
                if (model.result.equals("success")) {
                    //更新数据
                    WithDrawBiggestAmountModel.BiggestAmount data = model.data;
                    can_withdrawals = data.can_withdrawals;
                    et_amount.setHint("本次最多可提现" +  can_withdrawals + "元");
                } else {
                    //失败
                    CommonMethod.faifure(new WeakReference<Activity>(this), model.error);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class ApplyWithDrawHandler extends BaseHandler<WithDraw>{

        public ApplyWithDrawHandler(WithDraw object) {
            super(object);
        }

        @Override
        public void onSuccess(WithDraw object, Message msg) {
            object.ApplyWithDrawSuccess(msg);
        }
    }

    private void ApplyWithDrawSuccess(Message msg) {
        customDialog.dismiss();
        try {
            Gson gson = new Gson();
            ApplyWithDrawModel model = gson.fromJson(msg.obj.toString(), ApplyWithDrawModel.class);
            if (2 == model.result_code) {
                //重新登陆
                CommonMethod.goLogin(new WeakReference<Activity>(this), 2);
            } else {
                if (model.result.equals("success")) {
                    //更新数据
                    ApplyWithDrawModel.ApplyWithDrawDetail data = model.data;
                    String apply_time =data.apply_time;
                    String expect_time =data.expect_time;
                    AppySuccess(apply_time, expect_time);
                } else {
                    //失败
                    CommonMethod.faifure(new WeakReference<Activity>(this), model.error);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
