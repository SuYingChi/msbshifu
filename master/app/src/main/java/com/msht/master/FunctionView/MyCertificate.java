package com.msht.master.FunctionView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.recyclerview.LuRecyclerView;
import com.github.jdsjlzx.recyclerview.LuRecyclerViewAdapter;
import com.github.jdsjlzx.util.LuRecyclerViewStateUtils;
import com.github.jdsjlzx.view.LoadingFooter;
import com.google.gson.Gson;
import com.msht.master.Adapter.CertificateAdapter;
import com.msht.master.Base.BaseHandler;
import com.msht.master.Common.CommonMethod;
import com.msht.master.Constants.NetConstants;
import com.msht.master.Constants.SPConstants;
import com.msht.master.Model.CertificateModel;
import com.msht.master.Model.IncomeModel;
import com.msht.master.Model.MyDataModel;
import com.msht.master.R;
import com.msht.master.Utils.DialogUtil;
import com.msht.master.Utils.SendRequestUtils;
import com.msht.master.Utils.SharedPreferencesUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hei on 2016/12/26.
 */

public class MyCertificate extends AppCompatActivity implements OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    private LuRecyclerView mRecyclerView;
    private CertificateAdapter certificateAdapter;
    private View layout_empty;
    private int AddCertCode=0x001;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LuRecyclerViewAdapter luRecyclerViewAdapter;
    private boolean isRefreshing;
    private int pageNo = 1;//当前页数
    private int size = 18;//每页加载的大小
    private String token;

    private Handler getCertListHandler =new GetCertListHandler(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycertficate);
        token = (String) SharedPreferencesUtils.getData(getApplicationContext(), SPConstants.TOKEN, "");
        initHeaderTitle();
        initView();
    }

    private void initView() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setProgressViewOffset(false, 0, DialogUtil.dip2px(this, 48));
            mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
            mSwipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
            mSwipeRefreshLayout.setProgressViewEndTarget(true, 100);
            mSwipeRefreshLayout.setOnRefreshListener(this);
        }
        mRecyclerView = (LuRecyclerView) findViewById(R.id.recycler_cert);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        certificateAdapter = new CertificateAdapter(getApplicationContext());
        luRecyclerViewAdapter = new LuRecyclerViewAdapter(certificateAdapter);
        mRecyclerView.setAdapter(luRecyclerViewAdapter);
        mRecyclerView.addItemDecoration(new CertificateAdapter.SpaceItemDecoration(16));
        mRecyclerView.setOnLoadMoreListener(this);
        layout_empty = findViewById(R.id.layout_empty);
        onRefresh();
    }

    private void initHeaderTitle() {
        findViewById(R.id.id_goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView)findViewById(R.id.tv_title)).setText("我的证书");
        findViewById(R.id.tv_add_cert).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyCertificate.this, AddCertificate.class);
                startActivityForResult(intent,AddCertCode);
            }
        });
    }
    private void notifyDataSetChanged() {
        luRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void addItems(ArrayList<CertificateModel.CertificateDetail> list) {

        certificateAdapter.addAll(list);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==AddCertCode){
            if(resultCode==1){
                finish();
            }
        }
    }

    @Override
    public void onLoadMore() {
        LoadingFooter.State state = LuRecyclerViewStateUtils.getFooterViewState(mRecyclerView);
        if (state == LoadingFooter.State.Loading) {
            return;
        }
        if (state == LoadingFooter.State.TheEnd) {
            return;
        }
        pageNo++;
        LuRecyclerViewStateUtils.setFooterViewState(this, mRecyclerView, size, LoadingFooter.State.Loading, null);
        GetCertList();
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        isRefreshing = true;
        certificateAdapter.clear();
        LuRecyclerViewStateUtils.setFooterViewState(this, mRecyclerView, size, LoadingFooter.State.Loading, null);
        notifyDataSetChanged();
        pageNo=1;
        GetCertList();
    }

    private void GetCertList() {
        String validateURL = NetConstants.CERTIFICATELIST;
        Map<String, String> textParams = new HashMap<String, String>();
        textParams.put("token", token);
        textParams.put("page",pageNo+"");
        textParams.put("size",size+"");
        SendRequestUtils.PostDataFromService(textParams, validateURL, getCertListHandler);
    }

    private static class GetCertListHandler extends BaseHandler<MyCertificate>{

        public GetCertListHandler(MyCertificate object) {
            super(object);
        }

        @Override
        public void onSuccess(MyCertificate object, Message msg) {
            object.getCertListSuccess(msg);
        }

        @Override
        public void onFinilly(MyCertificate object) {
            object.isRefreshing=false;
            object.mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void getCertListSuccess(Message msg) {
        try{
            Gson gson = new Gson();
            CertificateModel certificateModel = gson.fromJson(msg.obj.toString(), CertificateModel.class);
            if (2 == certificateModel.result_code) {
                //重新登陆
                CommonMethod.goLogin(new WeakReference<Activity>(this), 2);
            } else {
                if (certificateModel.result.equals("success")) {
                    //更新数据
                    ArrayList<CertificateModel.CertificateDetail> data = certificateModel.data;
                    if(data.size()==0){
                        layout_empty.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.INVISIBLE);
                    }else{
                        layout_empty.setVisibility(View.INVISIBLE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                    }
                    if (isRefreshing) {
                        //是刷新的数据
                        certificateAdapter.clear();
                    }
                    addItems(data);
                    notifyDataSetChanged();
                    if (data.size() < size) {
                        //数据不足18个
                        LuRecyclerViewStateUtils.setFooterViewState(this, mRecyclerView, size, LoadingFooter.State.TheEnd, null);
                    } else {
                        LuRecyclerViewStateUtils.setFooterViewState(this, mRecyclerView, size, LoadingFooter.State.Normal, null);
                    }
                } else {
                    //失败
                    CommonMethod.faifure(new WeakReference<Activity>(this), certificateModel.error);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
