package com.msht.master.FunctionView;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jdsjlzx.recyclerview.LuRecyclerView;
import com.google.gson.Gson;
import com.msht.master.Adapter.PriceBookAdapter;
import com.msht.master.Adapter.SkilllMainTypeAdapter;
import com.msht.master.Base.BaseHandler;
import com.msht.master.Common.CommonMethod;
import com.msht.master.Constants.NetConstants;
import com.msht.master.Constants.SPConstants;
import com.msht.master.Controls.FullyLinearLayoutManager;
import com.msht.master.Controls.MyRecyclerView;
import com.msht.master.Model.PriceCategoryModel;
import com.msht.master.Model.RepairCategoryModel;
import com.msht.master.R;
import com.msht.master.UIView.CustomDialog;
import com.msht.master.Utils.SendRequestUtils;
import com.msht.master.Utils.SharedPreferencesUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class NewPriceBook extends AppCompatActivity {
    private PriceBookAdapter mAdapter;
    private MyRecyclerView   myRecyclerView;
    private String urls;
    private String token;
    private CustomDialog customDialog;
    private Context  mContext;
    Handler pricebookshandler = new PriceBooksHanlder(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_price_book);
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
        myRecyclerView=(MyRecyclerView)findViewById(R.id.id_recycler_price);
        myRecyclerView.setLayoutManager(new FullyLinearLayoutManager(getApplicationContext()));
        mAdapter = new PriceBookAdapter(this);
        myRecyclerView.setAdapter(mAdapter);
        mAdapter.SetOnItemSelectListener(new PriceBookAdapter.OnItemSelectListener() {
            @Override
            public void ItemSelectClick(View view, int secondPosition, PriceCategoryModel.PriceCategory.ChildCategory model) {
                Toast.makeText(mContext,model.name,Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void initData() {
        customDialog.show();
        String pricebookurl = NetConstants.PriceBook_URL;
        SendRequestUtils.GetDataFromService(pricebookurl, pricebookshandler);
    }
    private static class  PriceBooksHanlder extends BaseHandler<NewPriceBook> {
        public  PriceBooksHanlder(NewPriceBook object) {
            super(object);
        }

        @Override
        public void onSuccess(NewPriceBook object, Message msg) {
            object.getPriceListSuccess(msg);
        }
        @Override
        public void onFinilly(NewPriceBook object) {
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
                    ArrayList<PriceCategoryModel.PriceCategory> data = model.data;
                    mAdapter.addAll(data);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
