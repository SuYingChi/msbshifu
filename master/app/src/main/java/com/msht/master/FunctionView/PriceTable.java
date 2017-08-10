package com.msht.master.FunctionView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.msht.master.Adapter.MyExpandableAdapter;
import com.msht.master.Base.BaseHandler;
import com.msht.master.Common.CommonMethod;
import com.msht.master.Constants.NetConstants;
import com.msht.master.Constants.SPConstants;
import com.msht.master.HtmlWeb.SeePrice;
import com.msht.master.Model.PriceCategoryModel;
import com.msht.master.R;
import com.msht.master.UIView.CustomDialog;
import com.msht.master.Utils.SendRequestUtils;
import com.msht.master.Utils.SharedPreferencesUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class PriceTable extends AppCompatActivity {
    private ArrayList<PriceCategoryModel.PriceCategory> datas=null;
    private ExpandableListView expandableListView;
    private MyExpandableAdapter myExpandableAdapter;
    private String urls;
    private String token;
    private CustomDialog customDialog;
    private Context mContext;
    Handler pricebookshandler = new PriceBooksHanlder(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_table);
        mContext=this;
        token = (String) SharedPreferencesUtils.getData(getApplicationContext(), SPConstants.TOKEN, "");
        customDialog = new CustomDialog(this, "正在加载");
        initHeaderTitle();
        initView();
        initData();
    }

    private void initHeaderTitle() {
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("价格手册");
        findViewById(R.id.id_goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void initView() {
        expandableListView=(ExpandableListView)findViewById(R.id.id_price_expandableView);

    }
    private void initData() {
        customDialog.show();
        String pricebookurl = NetConstants.PriceBook_URL;
        SendRequestUtils.GetDataFromService(pricebookurl, pricebookshandler);

    }
    private static class  PriceBooksHanlder extends BaseHandler<PriceTable> {
        public  PriceBooksHanlder(PriceTable object) {
            super(object);
        }

        @Override
        public void onSuccess(PriceTable object, Message msg) {
            object.getPriceListSuccess(msg);
        }
        @Override
        public void onFinilly(PriceTable object) {
            object.customDialog.dismiss();
        }
    }
    /**
     * 获取价格列表成功
     */
    private void getPriceListSuccess(Message msg) {
        try {
            Gson gson = new Gson();
            PriceCategoryModel model = gson.fromJson(msg.obj.toString(), PriceCategoryModel.class);
            if (2 == model.result_code) {
                //重新登陆
                CommonMethod.goLogin(new WeakReference<Activity>(this), 2);
            } else {
                if (model.result.equals("success")) {
                    //更新数据
                    datas = model.data;
                    if (datas!=null){
                        myExpandableAdapter=new MyExpandableAdapter(this,datas);
                        expandableListView.setAdapter(myExpandableAdapter);
                        initEvent();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initEvent() {
        myExpandableAdapter.SetOnItemClickListener(new MyExpandableAdapter.OnItemClickTypeListener() {
            @Override
            public void setOnItemClick(View view, int childPosition, PriceCategoryModel.PriceCategory.ChildCategory model) {
                String price_web=model.price_web;
                String id=model.id;
                Intent see=new Intent(mContext,SeePrice.class);
                see.putExtra("Id",id);
                see.putExtra("price_web",price_web);
                startActivity(see);
            }
        });
    }
}
