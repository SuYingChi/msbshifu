package com.msht.master.FunctionView;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.recyclerview.LuRecyclerView;
import com.github.jdsjlzx.recyclerview.LuRecyclerViewAdapter;
import com.github.jdsjlzx.util.LuRecyclerViewStateUtils;
import com.github.jdsjlzx.view.LoadingFooter;
import com.google.gson.Gson;
import com.msht.master.Adapter.WithDrawRecordAdapter;
import com.msht.master.Common.CommonMethod;
import com.msht.master.Constants.NetConstants;
import com.msht.master.Constants.SPConstants;
import com.msht.master.Model.WithDrawRecordModel;
import com.msht.master.R;
import com.msht.master.Utils.AppToast;
import com.msht.master.Utils.DialogUtil;
import com.msht.master.Utils.SendRequestUtils;
import com.msht.master.Utils.SharedPreferencesUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hei on 2016/12/14.
 */

public class WithDrawRecord extends Activity implements SwipeRefreshLayout.OnRefreshListener, OnLoadMoreListener {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LuRecyclerView mRecyclerView;
    private WithDrawRecordAdapter withDrawRecordAdapter;
    private LuRecyclerViewAdapter luRecyclerViewAdapter;
    private String token;;
    private boolean isRefreshing;
    private int size=18;
    private int pageNo=1;


    private Handler getWithDrawRecordHandler =new GetWithDrawRecordHandler(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_record);
        token= (String) SharedPreferencesUtils.getData(getApplicationContext(), SPConstants.TOKEN,"");
        initHeader();
        initView();
    }

    private void initView() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setProgressViewOffset(false, 0, DialogUtil.dip2px(this,48));
            mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
            mSwipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
            mSwipeRefreshLayout.setProgressViewEndTarget(true, 100);
            mSwipeRefreshLayout.setOnRefreshListener(this);
        }
        mRecyclerView = (LuRecyclerView) findViewById(R.id.recycler_withdraw_record);
        withDrawRecordAdapter = new WithDrawRecordAdapter(this);
        luRecyclerViewAdapter = new LuRecyclerViewAdapter(withDrawRecordAdapter);
        mRecyclerView.setAdapter(luRecyclerViewAdapter);
        mRecyclerView.addItemDecoration(new WithDrawRecordAdapter.SpaceItemDecoration(16));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setOnLoadMoreListener(this);
        onRefresh();
    }

    private void initHeader() {
        findViewById(R.id.id_goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("提现记录");
    }

    @Override
    public void onRefresh() {
        isRefreshing=true;
        mSwipeRefreshLayout.setRefreshing(true);
        withDrawRecordAdapter.clear();
        LuRecyclerViewStateUtils.setFooterViewState(this,mRecyclerView,size, LoadingFooter.State.Loading,null);
        notifyDataSetChanged();
        pageNo=1;
        RequestWithDrawRecord();
    }

    private void RequestWithDrawRecord() {
        String page = String.valueOf(pageNo);
        String validateURL = NetConstants.WITHDRAWALS_LIST;
        Map<String, String> textParams = new HashMap<String, String>();
        textParams.put("token", token);
        textParams.put("page", page);
        textParams.put("size", size+"");
        SendRequestUtils.PostDataFromService(textParams,validateURL,getWithDrawRecordHandler);
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
        RequestWithDrawRecord();
    }



    private void getWithDrawRecordSuccess(Message msg) {
        try{
            Gson gson = new Gson();
            WithDrawRecordModel withDrawRecordModel = gson.fromJson(msg.obj.toString(), WithDrawRecordModel.class);
            if(2==withDrawRecordModel.result_code){
                //重新登陆
                CommonMethod.goLogin(new WeakReference<Activity>(this),2);
            }else{
                if(withDrawRecordModel.result.equals("success")){
                    //更新数据
                    ArrayList<WithDrawRecordModel.WithDrawRecodDetail> data = withDrawRecordModel.data;
                    if(isRefreshing) {
                        //是刷新的数据
                        withDrawRecordAdapter.clear();
                    }
                    addItems(data);
                    notifyDataSetChanged();
                    if(data.size()<size){
                        //数据不足18个
                        LuRecyclerViewStateUtils.setFooterViewState(this,mRecyclerView,size, LoadingFooter.State.TheEnd,null);
                    }else{
                        LuRecyclerViewStateUtils.setFooterViewState(this,mRecyclerView, size, LoadingFooter.State.Normal,null);
                    }
                }else{
                    //失败
                    CommonMethod.faifure(new WeakReference<Activity>(this),withDrawRecordModel.error);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static class GetWithDrawRecordHandler extends Handler{
        private WeakReference<WithDrawRecord> ref;

        GetWithDrawRecordHandler(WithDrawRecord activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final WithDrawRecord activity = ref.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            switch (msg.what){
                case SendRequestUtils.SUCCESS:
                    activity.getWithDrawRecordSuccess(msg);
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


    private void notifyDataSetChanged() {
        luRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void addItems(ArrayList<WithDrawRecordModel.WithDrawRecodDetail> list) {
        withDrawRecordAdapter.addAll(list);
    }
}
