package com.msht.master.FunctionView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.google.gson.Gson;
import com.msht.master.Adapter.SelectBankCardAdapter;
import com.msht.master.Base.BaseHandler;
import com.msht.master.Common.CommonMethod;
import com.msht.master.Constants.NetConstants;
import com.msht.master.Constants.SPConstants;
import com.msht.master.Model.BankCardModel;
import com.msht.master.R;
import com.msht.master.UIView.CustomDialog;
import com.msht.master.Utils.AppToast;
import com.msht.master.Utils.SendRequestUtils;
import com.msht.master.Utils.SharedPreferencesUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hei on 2016/12/26.
 */

public class SelectBankCard extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private String token;
    private CustomDialog customDialog;
    private static final int ADD_SUCCESS = 2;

    private Handler getBankCardHandler =new GetBankCardHandler(this);
    private SelectBankCardAdapter selectBankCardAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_bankcard);
        initHeaderTitle();
        token = (String) SharedPreferencesUtils.getData(getApplicationContext(), SPConstants.TOKEN, "");
        customDialog = new CustomDialog(this, "正在加载");
        initView();
        getBankCardData();
    }

    private void getBankCardData() {
        customDialog.show();
        //银行卡列表
        String validateURL = NetConstants.BANKCARD_LIST;
        Map<String, String> textParams = new HashMap<String, String>();
        textParams.put("token", token);
        SendRequestUtils.PostDataFromService(textParams, validateURL, getBankCardHandler);
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_selsect_bank_card);
        selectBankCardAdapter = new SelectBankCardAdapter(this);
        mRecyclerView.setAdapter(selectBankCardAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new SelectBankCardAdapter.SpaceItemDecoration(16));
        selectBankCardAdapter.SetOnItemClickListener(new SelectBankCardAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent();
                intent.putExtra("id",selectBankCardAdapter.getDataList().get(position).id);
                intent.putExtra("bank",selectBankCardAdapter.getDataList().get(position).bank);
                intent.putExtra("card",selectBankCardAdapter.getDataList().get(position).card);
                setResult(1,intent);
                finish();
            }
        });

    }

    private void initHeaderTitle() {
        findViewById(R.id.id_goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("选择银行卡");
        findViewById(R.id.tv_add_bankcard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent add = new Intent(SelectBankCard.this, AddBankCard.class);
                startActivityForResult(add, 2);
            }
        });
    }
    private static class GetBankCardHandler extends BaseHandler<SelectBankCard>{

        public GetBankCardHandler(SelectBankCard object) {
            super(object);
        }

        @Override
        public void onSuccess(SelectBankCard object, Message msg) {
            object.getBankCardDataSuccess(msg);
        }


        @Override
        public void onFinilly(SelectBankCard object) {
            super.onFinilly(object);
            object.customDialog.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ADD_SUCCESS:
                if (resultCode == 2) {
                    selectBankCardAdapter.clear();
                    getBankCardData();
                }
                break;
            default:
                break;
        }
    }

    private void getBankCardDataSuccess(Message msg) {
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
                    selectBankCardAdapter.clear();
                    selectBankCardAdapter.addAll(data);
                    selectBankCardAdapter.notifyDataSetChanged();
                } else {
                    //失败
                    CommonMethod.faifure(new WeakReference<Activity>(this), bankCardModel.error);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
