package com.msht.master.FunctionView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.recyclerview.LuRecyclerView;
import com.github.jdsjlzx.recyclerview.LuRecyclerViewAdapter;
import com.google.gson.Gson;
import com.msht.master.Adapter.CityAdapter;
import com.msht.master.Adapter.MyWorkOrderAdapter;
import com.msht.master.Base.BaseHandler;
import com.msht.master.Common.CommonMethod;
import com.msht.master.Constants.NetConstants;
import com.msht.master.Model.CityModel;
import com.msht.master.Model.DistrictModel;
import com.msht.master.Model.OrderModel;
import com.msht.master.R;
import com.msht.master.UIView.CustomDialog;
import com.msht.master.Utils.SendRequestUtils;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;

public class SelectCity extends AppCompatActivity {
    private LuRecyclerView mRecyclerView;
    private LuRecyclerViewAdapter luRecyclerViewAdapter;
    private CityAdapter mAdapter;
    private Context mContext;
    private CustomDialog customDialog;
    Handler cityhandler = new SelectCity.CityHanlder(this);

    private void getCitySuccess(Message msg) {
        try {
            Gson gson = new Gson();
            CityModel model = gson.fromJson(msg.obj.toString(), CityModel.class);
            int result_code = model.result_code;
            String Results = model.result;
            String Error = model.error;
            if (result_code == 2) {
                CommonMethod.goLogin(new WeakReference<Activity>(this), 2);
            } else {
                if (Results.equals("success")) {
                    //获取信息成功
                    mAdapter.addAll(model.data);
                } else {
                    Toast.makeText(SelectCity.this, Error, Toast.LENGTH_SHORT)
                            .show();
                }
            }
        } catch (Exception e) {
            //数据解析失败
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);
        mContext=this;
        customDialog = new CustomDialog(this, "正在加载");
        initHeaderTile();
        initView();
        initData();
    }
    private void initData() {
        customDialog.show();
        String cityurl = NetConstants.SELECT_CITY;
        SendRequestUtils.GetDataFromService(cityurl, cityhandler);

    }
    private void initHeaderTile() {
        findViewById(R.id.id_goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("选择城市");
    }
    private void initView() {
        mRecyclerView = (LuRecyclerView)findViewById(R.id.recycler_view);

        mAdapter = new CityAdapter(this);
        luRecyclerViewAdapter = new LuRecyclerViewAdapter(mAdapter);
        mRecyclerView.setAdapter(luRecyclerViewAdapter);
        mRecyclerView.addItemDecoration(new CityAdapter.SpaceItemDecoration(16));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        luRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                CityModel.CityDetail model = mAdapter.getDataList().get(position);
                Intent detail = new Intent();
                detail.putExtra("id", model.id);
                detail.putExtra("city",model.name);
                setResult(3, detail);
                finish();
            }

        });
    }

    private static class CityHanlder extends BaseHandler<SelectCity> {

        public CityHanlder(SelectCity object) {
            super(object);
        }

        @Override
        public void onSuccess(SelectCity object, Message msg) {
            object.getCitySuccess(msg);
        }

        @Override
        public void onFinilly(SelectCity object) {
            object.customDialog.dismiss();
        }
    }
}
