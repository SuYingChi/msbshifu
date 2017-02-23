package com.msht.master.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.recyclerview.LuRecyclerView;
import com.github.jdsjlzx.recyclerview.LuRecyclerViewAdapter;
import com.github.jdsjlzx.util.LuRecyclerViewStateUtils;
import com.github.jdsjlzx.view.LoadingFooter;
import com.google.gson.Gson;
import com.msht.master.Adapter.RewordHistoryAdapter;
import com.msht.master.Common.CommonMethod;
import com.msht.master.Constants.NetConstants;
import com.msht.master.Constants.SPConstants;
import com.msht.master.Model.OrderModel;
import com.msht.master.Model.RewardModel;
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
 * Created by hei on 2016/12/19.
 */

public class RewordHistory extends Fragment implements SwipeRefreshLayout.OnRefreshListener, OnLoadMoreListener {
    private int position;
    private LuRecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RewordHistoryAdapter rewordHistoryAdapter;
    private LuRecyclerViewAdapter luRecyclerViewAdapter;
    private boolean isRefreshing = false;
    private int size = 18;
    private int pageNo = 1;
    private String token;
    private Activity mActivity;

    private Handler rewordHistoryHanlder = new RewordHistoryHandler(this);


    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_rewordhistory, container, false);
        token = (String) SharedPreferencesUtils.getData(getActivity(), SPConstants.TOKEN, "");
        mActivity = getActivity();
        initView(inflate);

        return inflate;
    }

    private void initView(View view) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mRecyclerView = (LuRecyclerView) view.findViewById(R.id.recycler_view);
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setProgressViewOffset(false, 0, DialogUtil.dip2px(getContext(), 48));
            mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
            mSwipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
            mSwipeRefreshLayout.setProgressViewEndTarget(true, 100);
            mSwipeRefreshLayout.setOnRefreshListener(this);
        }
        rewordHistoryAdapter = new RewordHistoryAdapter(getContext());
        luRecyclerViewAdapter = new LuRecyclerViewAdapter(rewordHistoryAdapter);
        mRecyclerView.setAdapter(luRecyclerViewAdapter);
        mRecyclerView.addItemDecoration(new RewordHistoryAdapter.SpaceItemDecoration(16));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setOnLoadMoreListener(this);
        onRefresh();
    }

    public static RewordHistory getInstance(int position) {
        RewordHistory rewordHistory = new RewordHistory();
        rewordHistory.position = position;
        return rewordHistory;
    }

    @Override
    public void onRefresh() {
        isRefreshing = true;
        mSwipeRefreshLayout.setRefreshing(true);
        rewordHistoryAdapter.clear();
        LuRecyclerViewStateUtils.setFooterViewState(mActivity, mRecyclerView, size, LoadingFooter.State.Loading, null);
        notifyDataSetChanged();
        pageNo = 1;
        requestData();
    }

    private void requestData() {
        String validateURL = NetConstants.REWARD_HISTORY;
        switch (position) {
            case 0:
                break;
            case 1:
                break;
        }
        String page = String.valueOf(pageNo);
        Map<String, String> textParams = new HashMap<String, String>();
        textParams.put("token", token);
        textParams.put("page", page);
        textParams.put("size", size + "");
        SendRequestUtils.PostDataFromService(textParams, validateURL, rewordHistoryHanlder);
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
        LuRecyclerViewStateUtils.setFooterViewState(mActivity, mRecyclerView, size, LoadingFooter.State.Loading, null);
        requestData();
    }

    private void notifyDataSetChanged() {
        luRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void addItems(ArrayList<RewardModel.RewardDetail> list) {

        rewordHistoryAdapter.addAll(list);

    }

    private static class RewordHistoryHandler extends Handler {
        private WeakReference<RewordHistory> ref;

        RewordHistoryHandler(RewordHistory fragment) {
            ref = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            final RewordHistory fragment = ref.get();
            if (fragment == null || fragment.isDetached()) {
                return;
            }
            switch (msg.what) {
                case SendRequestUtils.SUCCESS:
                    fragment.getDataSuccess(msg);
                    break;
                case SendRequestUtils.FAILURE:
                    LuRecyclerViewStateUtils.setFooterViewState(fragment.mActivity, fragment.mRecyclerView, fragment.size, LoadingFooter.State.NetWorkError, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            fragment.onRefresh();
                        }
                    });
                    fragment.notifyDataSetChanged();

                    AppToast.makeShortToast(fragment.mActivity, "网络连接失败");
                    break;
                case SendRequestUtils.ERRORCODE:
                    AppToast.makeShortToast(fragment.mActivity, "网络连接失败");
                    break;
            }
            fragment.isRefreshing = false;
            fragment.mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void getDataSuccess(Message msg) {
        try {
            Gson gson = new Gson();
            RewardModel rewardModel = gson.fromJson(msg.obj.toString(), RewardModel.class);
            if (2 == rewardModel.result_code) {
                //重新登陆
                CommonMethod.goLogin(new WeakReference<Activity>(mActivity), 1);
            } else {
                if (rewardModel.result.equals("success")) {
                    //更新数据
                    ArrayList<RewardModel.RewardDetail> data = rewardModel.data;
                    if (isRefreshing) {
                        //是刷新的数据
                        rewordHistoryAdapter.clear();

                    }
                    addItems(data);
                    notifyDataSetChanged();
                    if (data.size() < size) {
                        //数据不足18个
                        LuRecyclerViewStateUtils.setFooterViewState(mActivity, mRecyclerView, data.size(), LoadingFooter.State.TheEnd, null);
                    } else {
                        LuRecyclerViewStateUtils.setFooterViewState(mActivity, mRecyclerView, data.size(), LoadingFooter.State.Normal, null);
                    }
                } else {
                    //失败
                    CommonMethod.faifure(new WeakReference<Activity>(mActivity), rewardModel.error);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
