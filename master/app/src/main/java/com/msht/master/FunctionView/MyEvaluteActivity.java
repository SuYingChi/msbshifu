package com.msht.master.FunctionView;

import android.app.Activity;
import android.app.Dialog;

import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;


import android.view.View;

import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;



import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.recyclerview.LuRecyclerView;
import com.github.jdsjlzx.recyclerview.LuRecyclerViewAdapter;
import com.github.jdsjlzx.util.LuRecyclerViewStateUtils;
import com.github.jdsjlzx.view.LoadingFooter;
import com.google.gson.Gson;
import com.msht.master.Adapter.MyEvaluteAdapter;
import com.msht.master.Application.MyApplication;
import com.msht.master.Constants.NetConstants;
import com.msht.master.Constants.SPConstants;
import com.msht.master.LoginActivity;
import com.msht.master.Model.EvaluteModel;
import com.msht.master.R;

import com.msht.master.UIView.PromptDialog;
import com.msht.master.Utils.AppToast;
import com.msht.master.Utils.DialogUtil;
import com.msht.master.Utils.SendRequestUtils;
import com.msht.master.Utils.SharedPreferencesUtils;
import com.squareup.leakcanary.RefWatcher;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hei123 on 2016/12/5.
 */

public class MyEvaluteActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener, OnLoadMoreListener {

    private String  token;
    private RadioGroup radio_evaluate;
    private LuRecyclerView mRecyclerView;
    private String status = "0";//默认为0全部评价
    private int pageNo=1;//当前页数
    private int size = 18;//每页加载的大小
    private MyEvaluteAdapter myEvaluteAdapter;
    private LuRecyclerViewAdapter luRecyclerViewAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean isRefreshing;


    private Handler mHandler=new PreviewHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myevaluate);
        token= (String) SharedPreferencesUtils.getData(getApplicationContext(), SPConstants.TOKEN,"");
        initHeaderTitle();
        initView();
        initEvent();
    }

    private void initEvent() {
        radio_evaluate.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_button_all:
                        status = "0";
                        break;
                    case R.id.radio_button_good:
                        status = "1";
                        break;
                    case R.id.radio_button_middle:
                        status = "2";
                        break;
                    case R.id.radio_button_bad:
                        status = "3";
                        break;
                }

                onRefresh();
            }
        });



    }

    private void initView() {
        radio_evaluate = (RadioGroup) findViewById(R.id.radio_evaluate);
        mRecyclerView = (LuRecyclerView) findViewById(R.id.recycler_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setProgressViewOffset(false, 0, DialogUtil.dip2px(this,48));
            mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
            mSwipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
            mSwipeRefreshLayout.setProgressViewEndTarget(true, 100);
            mSwipeRefreshLayout.setOnRefreshListener(this);
        }
        myEvaluteAdapter =new MyEvaluteAdapter(this);
        luRecyclerViewAdapter=new LuRecyclerViewAdapter(myEvaluteAdapter);
        mRecyclerView.setAdapter(luRecyclerViewAdapter);
        mRecyclerView.addItemDecoration(new MyEvaluteAdapter.SpaceItemDecoration(16));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setOnLoadMoreListener(this);
        onRefresh();
    }

    private void initHeaderTitle() {
        ImageView id_goback = (ImageView) findViewById(R.id.id_goback);
        id_goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("我的评价");
    }

    private void notifyDataSetChanged() {
        luRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void addItems(ArrayList<EvaluteModel.EvaluteDetailModel> list) {

        myEvaluteAdapter.addAll(list);

    }

    @Override
    public void onRefresh() {
        isRefreshing=true;
        mSwipeRefreshLayout.setRefreshing(true);
        myEvaluteAdapter.clear();
        LuRecyclerViewStateUtils.setFooterViewState(MyEvaluteActivity.this,mRecyclerView,size, LoadingFooter.State.Loading,null);
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
    /**
     * 请求数据
     */
    private void requestData() {
        String page = String.valueOf(pageNo);
        String validateURL = NetConstants.ORDER_EVALUATE_LIST;
        Map<String, String> textParams = new HashMap<String, String>();
        textParams.put("token", token);
        textParams.put("status", status);
        textParams.put("page", page);
        textParams.put("size", size+"");
        SendRequestUtils.PostDataFromService(textParams,validateURL,mHandler);
    }


    private static class PreviewHandler extends Handler {
        private WeakReference<MyEvaluteActivity> ref;

        PreviewHandler(MyEvaluteActivity activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final MyEvaluteActivity activity = ref.get();
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

    private void gologin() {
        Intent mainintent = new Intent(this, LoginActivity.class);
        mainintent.putExtra("state",2);
        startActivity(mainintent);
        this.finish();
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
    private void getDataSuccess(Message msg) {
        //解析数据
        try{
            Gson gson = new Gson();
            EvaluteModel evaluteModel = gson.fromJson(msg.obj.toString(), EvaluteModel.class);
            if(2==evaluteModel.result_code){
                //重新登陆
                gologin();
            }else{
                if(evaluteModel.result.equals("success")){
                    //更新数据
                    ArrayList<EvaluteModel.EvaluteDetailModel> data = evaluteModel.data;
                    if(isRefreshing) {
                        //是刷新的数据
                        myEvaluteAdapter.clear();
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
                    faifure(evaluteModel.error);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = MyApplication.getRefWatcher(getApplicationContext());
        refWatcher.watch(this);
    }
}
