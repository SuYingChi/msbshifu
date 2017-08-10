package com.msht.master.FunctionView;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.recyclerview.LuRecyclerView;
import com.github.jdsjlzx.recyclerview.LuRecyclerViewAdapter;
import com.github.jdsjlzx.util.LuRecyclerViewStateUtils;
import com.github.jdsjlzx.view.LoadingFooter;
import com.google.gson.Gson;
import com.msht.master.Adapter.MyEvaluteAdapter;
import com.msht.master.Adapter.QualityAssuranceAdapter;
import com.msht.master.Common.CommonMethod;
import com.msht.master.Constants.NetConstants;
import com.msht.master.Constants.SPConstants;
import com.msht.master.Model.QualityAssuranceModel;
import com.msht.master.R;
import com.msht.master.Utils.AppToast;
import com.msht.master.Utils.DialogUtil;
import com.msht.master.Utils.SendRequestUtils;
import com.msht.master.Utils.SharedPreferencesUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QualityAssurance extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, OnLoadMoreListener {
    private int size=18;
    private int pageNo=1;
    private String token;
    private boolean isRefreshing;
    private LuRecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private QualityAssuranceAdapter qualityAssuranceAdapter;
    private LuRecyclerViewAdapter luRecyclerViewAdapter;
    private Handler mHandler=new PreviewHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quality_assurance);
        initHeaderTitle();
        token= (String) SharedPreferencesUtils.getData(getApplicationContext(), SPConstants.TOKEN,"");
        initView();

    }

    private void initView() {
        mRecyclerView = (LuRecyclerView) findViewById(R.id.recycler_qualite);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setProgressViewOffset(false, 0, DialogUtil.dip2px(this,48));
            mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
            mSwipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
            mSwipeRefreshLayout.setProgressViewEndTarget(true, 100);
            mSwipeRefreshLayout.setOnRefreshListener(this);
        }
        qualityAssuranceAdapter = new QualityAssuranceAdapter(this);
        luRecyclerViewAdapter = new LuRecyclerViewAdapter(qualityAssuranceAdapter);
        mRecyclerView.setAdapter(luRecyclerViewAdapter);
        mRecyclerView.addItemDecoration(new MyEvaluteAdapter.SpaceItemDecoration(16));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setOnLoadMoreListener(this);
        onRefresh();
    }

    private void initHeaderTitle() {
        findViewById(R.id.id_goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("质保金");
    }

    @Override
    public void onRefresh() {
        isRefreshing=true;
        mSwipeRefreshLayout.setRefreshing(true);
        qualityAssuranceAdapter.clear();
        LuRecyclerViewStateUtils.setFooterViewState(QualityAssurance.this,mRecyclerView,size, LoadingFooter.State.Loading,null);
        notifyDataSetChanged();
        pageNo=1;
        requestData();
    }
    @Override
    public void onLoadMore() {
        LoadingFooter.State state = LuRecyclerViewStateUtils.getFooterViewState(mRecyclerView);
        if(state == LoadingFooter.State.Loading) {
            return;
        }
        if(state== LoadingFooter.State.TheEnd){
            return;
        }
        pageNo++;
        LuRecyclerViewStateUtils.setFooterViewState(this,mRecyclerView,size, LoadingFooter.State.Loading,null);
        requestData();
    }
    private void requestData() {
        String page = String.valueOf(pageNo);
        String validateURL = NetConstants.QUALITY_ASSURANCE_LIST;
        Map<String, String> textParams = new HashMap<String, String>();
        textParams.put("token", token);
        textParams.put("page", page);
        textParams.put("size", size+"");
        SendRequestUtils.PostDataFromService(textParams,validateURL,mHandler);
    }
    private static class PreviewHandler extends Handler {
        private WeakReference<QualityAssurance> ref;

        PreviewHandler(QualityAssurance activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final QualityAssurance activity = ref.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            switch (msg.what){
                case SendRequestUtils.SUCCESS:
                    activity.getDataSuccess(msg);
                    break;
                case SendRequestUtils.FAILURE:
                    LuRecyclerViewStateUtils.setFooterViewState(activity, activity.mRecyclerView, activity.size, LoadingFooter.State.NetWorkError, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            activity.onRefresh();
                        }
                    });
                    activity.notifyDataSetChanged();

                    AppToast.makeShortToast(activity,"网络连接失败");
                    break;
                case SendRequestUtils.ERRORCODE:
                    AppToast.makeShortToast(activity,"网络连接失败");
                    break;
            }
            activity.isRefreshing=false;
            activity.mSwipeRefreshLayout.setRefreshing(false);
        }
    }
    private void getDataSuccess(Message msg) {
        //解析数据
        try{
            Gson gson = new Gson();
            QualityAssuranceModel qualityAssuranceModel = gson.fromJson(msg.obj.toString(), QualityAssuranceModel.class);
            if(2==qualityAssuranceModel.result_code){
                //重新登陆
                CommonMethod.goLogin(new WeakReference<Activity>(this),2);
            }else{
                if(qualityAssuranceModel.result.equals("success")){
                    //更新数据
                    ArrayList<QualityAssuranceModel.QualityAssuranceDetail> data = qualityAssuranceModel.data;
                    if(isRefreshing) {
                        //是刷新的数据
                        qualityAssuranceAdapter.clear();
                    }
                    addItems(data);
                    notifyDataSetChanged();
                    if(data.size()<size){
                        //数据不足18个
                        LuRecyclerViewStateUtils.setFooterViewState(this,mRecyclerView,data.size(), LoadingFooter.State.TheEnd,null);
                    }else{
                        LuRecyclerViewStateUtils.setFooterViewState(this,mRecyclerView,data.size(), LoadingFooter.State.Normal,null);
                    }
                }else{
                    //失败
                    CommonMethod.faifure(new WeakReference<Activity>(this),qualityAssuranceModel.error);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private void notifyDataSetChanged() {
        luRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void addItems(ArrayList<QualityAssuranceModel.QualityAssuranceDetail> list) {

        qualityAssuranceAdapter.addAll(list);

    }

}
