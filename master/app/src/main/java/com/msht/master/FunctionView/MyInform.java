package com.msht.master.FunctionView;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;

import android.view.View;

import android.widget.TextView;


import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.util.RecyclerViewStateUtils;
import com.github.jdsjlzx.view.LoadingFooter;
import com.google.gson.Gson;
import com.msht.master.Adapter.InformAdapter;
import com.msht.master.Common.CommonMethod;
import com.msht.master.Constants.NetConstants;
import com.msht.master.Constants.SPConstants;
import com.msht.master.Model.InformModel;
import com.msht.master.R;
import com.msht.master.Utils.AppToast;
import com.msht.master.Utils.SendRequestUtils;
import com.msht.master.Utils.SharedPreferencesUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hei123 on 2016/12/8.
 */

public class MyInform extends Activity {

    private LRecyclerView mRecyclerView;
    private InformAdapter informAdapter;
    private LRecyclerViewAdapter lRecyclerViewAdapter;
    private int size = 18;
    private int pageNo = 1;
    private String token;
    private boolean isRefreshing;

    private Handler informHandler = new InformHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_inform);
        initHeaderTitlle();
        token = (String) SharedPreferencesUtils.getData(getApplicationContext(), SPConstants.TOKEN, "");
        initView();
    }

    private void initView() {
        mRecyclerView = (LRecyclerView) findViewById(R.id.list);
        informAdapter = new InformAdapter(this);
        lRecyclerViewAdapter = new LRecyclerViewAdapter(informAdapter);
        mRecyclerView.setAdapter(lRecyclerViewAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new InformAdapter.SpaceItemDecoration(16));
        mRecyclerView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {

                isRefreshing = true;
                informAdapter.clear();
                lRecyclerViewAdapter.notifyDataSetChanged();
                RecyclerViewStateUtils.setFooterViewState(MyInform.this, mRecyclerView, size, LoadingFooter.State.Loading, null);
                pageNo = 1;
                requestData();
            }
        });

        mRecyclerView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                LoadingFooter.State state = RecyclerViewStateUtils.getFooterViewState(mRecyclerView);
                if (state == LoadingFooter.State.Loading) {
                    return;
                }
                if (state == LoadingFooter.State.TheEnd) {
                    return;
                }
                pageNo++;
                RecyclerViewStateUtils.setFooterViewState(MyInform.this, mRecyclerView, size, LoadingFooter.State.Loading, null);
                requestData();
            }
        });
        mRecyclerView.setRefreshing(true);
    }

    private void requestData() {
        String page = String.valueOf(pageNo);
        String size = "16";
        String validateURL = NetConstants.MESSAGE_LIST;
        Map<String, String> textParams = new HashMap<String, String>();
        textParams.put("token", token);
        textParams.put("page", page);
        textParams.put("size", size);
        SendRequestUtils.PostDataFromService(textParams, validateURL, informHandler);
    }

    private void initHeaderTitlle() {
        findViewById(R.id.id_goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("系统消息");
    }

    private void notifyDataSetChanged() {
        lRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void addItems(ArrayList<InformModel.InformDetailModel> list) {

        informAdapter.addAll(list);

    }


    private static class InformHandler extends Handler {
        private WeakReference<MyInform> ref;

        InformHandler(MyInform activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final MyInform activity = ref.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            switch (msg.what) {
                case SendRequestUtils.SUCCESS:
                    activity.getDataSuccess(msg);
                    break;
                case SendRequestUtils.FAILURE:
                    activity.isRefreshing = false;
                    activity.mRecyclerView.refreshComplete();
                    AppToast.makeShortToast(activity, "网络连接失败");
                    break;
                case SendRequestUtils.ERRORCODE:
                    AppToast.makeShortToast(activity, "网络连接失败");
                    activity.isRefreshing = false;
                    activity.mRecyclerView.refreshComplete();
                    break;
            }
        }
    }

    private void getDataSuccess(Message msg) {
        try {
            Gson gson = new Gson();
            InformModel informModel = gson.fromJson(msg.obj.toString(), InformModel.class);
            if (2 == informModel.result_code) {
                //重新登陆
                CommonMethod.goLogin(new WeakReference<Activity>(MyInform.this), 2);
            } else {
                if (informModel.result.equals("success")) {
                    //更新数据
                    ArrayList<InformModel.InformDetailModel> data = informModel.data;
                    if (isRefreshing) {
                        //是刷新的数据
                        informAdapter.clear();
                        isRefreshing = false;
                    }
                    mRecyclerView.refreshComplete();
                    addItems(data);
                    notifyDataSetChanged();
                    if (data.size() < size) {
                        //数据不足18个
                        RecyclerViewStateUtils.setFooterViewState(this, mRecyclerView, data.size(), LoadingFooter.State.TheEnd, null);
                    } else {
                        RecyclerViewStateUtils.setFooterViewState(this, mRecyclerView, data.size(), LoadingFooter.State.Normal, null);
                    }
                } else {
                    //失败
                    CommonMethod.faifure(new WeakReference<Activity>(MyInform.this), informModel.error);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
