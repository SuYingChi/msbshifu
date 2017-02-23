package com.msht.master.FunctionView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.msht.master.Adapter.BankCardAdapter;
import com.msht.master.Base.BaseHandler;
import com.msht.master.Common.CommonMethod;
import com.msht.master.Constants.NetConstants;
import com.msht.master.Constants.SPConstants;
import com.msht.master.Model.BankCardModel;
import com.msht.master.R;


import com.msht.master.UIView.CustomDialog;
import com.msht.master.UIView.PromptDialog;
import com.msht.master.Utils.SendRequestUtils;
import com.msht.master.Utils.SharedPreferencesUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BankCard extends AppCompatActivity {
    private String token;
    private static final int ADD_SUCCESS = 2;
    private CustomDialog customDialog;
    private TextView tv_add_bankcard;
    private RecyclerView mRecyclerView;
    private BankCardAdapter bankCardAdapter;
    int position = -1;

    private Handler getBankCardHanlder = new BankCardLisrHanlder(this);
    private Handler deleteBankCardHandler = new DeleteBankCardHandler(this);
    private View layout_empty;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_card);
        initHeaderTitle();
        token = (String) SharedPreferencesUtils.getData(getApplicationContext(), SPConstants.TOKEN, "");
        customDialog = new CustomDialog(this, "正在加载");
        initView();
        initData();
    }

    private void initHeaderTitle() {
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("我的银行卡");
        findViewById(R.id.id_goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tv_add_bankcard = (TextView) findViewById(R.id.tv_add_bankcard);
        tv_add_bankcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent add = new Intent(BankCard.this, AddBankCard.class);
                startActivityForResult(add, 2);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ADD_SUCCESS:
                if (resultCode == 2) {
                    bankCardAdapter.clear();
                    initData();
                }
                break;
            default:
                break;
        }
    }

    private void initData() {
        customDialog.show();
        //银行卡列表
        String validateURL = NetConstants.BANKCARD_LIST;
        Map<String, String> textParams = new HashMap<String, String>();
        textParams.put("token", token);
        SendRequestUtils.PostDataFromService(textParams, validateURL, getBankCardHanlder);
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_bank_card);
        layout_empty = findViewById(R.id.layout_empty);
        bankCardAdapter = new BankCardAdapter(this);
        mRecyclerView.setAdapter(bankCardAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new BankCardAdapter.SpaceItemDecoration(16));
        bankCardAdapter.SetOnItemClickListener(new BankCardAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onDeleteClick(View view, final int position) {
                BankCard.this.position = position;
                new PromptDialog.Builder(BankCard.this)
                        .setTitle(R.string.dialog_title)
                        .setViewStyle(PromptDialog.VIEW_STYLE_TITLEBAR_SKYBLUE)
                        .setMessage("确定要解绑尾号为" + bankCardAdapter.getDataList().get(position).card + "的银行卡吗")
                        .setButton1("取消", new PromptDialog.OnClickListener() {
                            @Override
                            public void onClick(Dialog dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setButton2("解绑", new PromptDialog.OnClickListener() {
                            @Override
                            public void onClick(Dialog dialog, int which) {
                                customDialog.show();
                                String validateURL = NetConstants.BANKCARD_DELETE;
                                Map<String, String> textParams = new HashMap<String, String>();
                                textParams.put("token", token);
                                textParams.put("id", bankCardAdapter.getDataList().get(position).id+"");
                                SendRequestUtils.PostDataFromService(textParams, validateURL, deleteBankCardHandler);
                                dialog.dismiss();
                            }
                        })
                        .show();

            }
        });
    }


    private void getBankCardListSuccess(Message msg) {
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
                    if(data.size()!=0){
                        bankCardAdapter.clear();
                        bankCardAdapter.addAll(data);
                        bankCardAdapter.notifyDataSetChanged();
                        layout_empty.setVisibility(View.INVISIBLE);
                    }else {
                        layout_empty.setVisibility(View.VISIBLE);
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

    private static class BankCardLisrHanlder extends BaseHandler<BankCard> {
        public BankCardLisrHanlder(BankCard object) {
            super(object);
        }

        @Override
        public void onSuccess(BankCard object, Message msg) {
            object.getBankCardListSuccess(msg);
        }

        @Override
        public void onFinilly(BankCard object) {
            object.customDialog.dismiss();
        }
    }

    private void deleteBankCardSuccess(Message msg) {
        new PromptDialog.Builder(this)
                .setTitle(R.string.dialog_title)
                .setViewStyle(PromptDialog.VIEW_STYLE_TITLEBAR_SKYBLUE)
                .setMessage("已解除您绑定的银行卡")
                .setButton1("确定", new PromptDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        if (position != -1) {
                            bankCardAdapter.remove(position);
                            bankCardAdapter.notifyDataSetChanged();
                            if(bankCardAdapter.getDataList().size()==0){
                                //没有银行卡了
                                layout_empty.setVisibility(View.VISIBLE);
                            }else {
                                layout_empty.setVisibility(View.INVISIBLE);
                            }
                            dialog.dismiss();
                        }
                    }
                }).show();
    }

    private static class DeleteBankCardHandler extends BaseHandler<BankCard> {

        public DeleteBankCardHandler(BankCard object) {
            super(object);
        }

        @Override
        public void onSuccess(BankCard object, Message msg) {
            object.deleteBankCardSuccess(msg);
        }

        @Override
        public void onFinilly(BankCard object) {
            object.customDialog.dismiss();
        }
    }


}
