package com.msht.master.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;

import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.recyclerview.LuRecyclerView;
import com.github.jdsjlzx.recyclerview.LuRecyclerViewAdapter;
import com.github.jdsjlzx.util.LuRecyclerViewStateUtils;
import com.github.jdsjlzx.view.LoadingFooter;
import com.google.gson.Gson;
import com.msht.master.Adapter.MyWorkOrderAdapter;
import com.msht.master.Base.BaseFragment;
import com.msht.master.Common.CommonMethod;
import com.msht.master.Constants.NetConstants;
import com.msht.master.Constants.SPConstants;
import com.msht.master.FunctionView.WorkOrderDetail;
import com.msht.master.Model.OrderModel;
import com.msht.master.R;
import com.msht.master.Utils.AppToast;
import com.msht.master.Utils.DialogUtil;
import com.msht.master.Utils.SendRequestUtils;
import com.msht.master.Utils.SharedPreferencesUtils;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by hei123 on 2016/12/6.
 */

public class OrderListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, OnLoadMoreListener {
    private String token;
    private LuRecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int status = 0;//默认全部评价
    private int pageNo = 1;//当前页数
    private int size = 18;//每页加载的大小
    private int pos;
    private MyWorkOrderAdapter myWorkOrderAdapter;
    private LuRecyclerViewAdapter luRecyclerViewAdapter;
    private boolean isRefreshing;//是不是刷新出来的数据
    private Activity mActivity;
    private Handler orderHandler = new PreviewHandler(this);

    @Override
    public View initView() {
        if(mRootView==null){
            mRootView= LayoutInflater.from(mContext).inflate(R.layout.fragment_orderlist,null,false);
            //view = inflater.inflate(R.layout.fragment_orderlist, container, false);
        }
        token = (String) SharedPreferencesUtils.getData(getActivity(), SPConstants.TOKEN, "");
        mActivity = getActivity();
        initMyView(mRootView);
        return mRootView;
    }

    @Override
    public void initData() {
        onRefresh();
    }

    public static OrderListFragment getInstanse(int position){
        OrderListFragment orderListFragment = new OrderListFragment();
        orderListFragment.status=position;
        return orderListFragment;
    }

    private void initMyView(View view) {
        mRecyclerView = (LuRecyclerView) view.findViewById(R.id.recycler_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setProgressViewOffset(false, 0, DialogUtil.dip2px(getContext(), 48));
            mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
            mSwipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
            mSwipeRefreshLayout.setProgressViewEndTarget(true, 100);
            mSwipeRefreshLayout.setOnRefreshListener(this);
        }
        myWorkOrderAdapter = new MyWorkOrderAdapter(getContext());
        luRecyclerViewAdapter = new LuRecyclerViewAdapter(myWorkOrderAdapter);
        mRecyclerView.setAdapter(luRecyclerViewAdapter);
        mRecyclerView.addItemDecoration(new MyWorkOrderAdapter.SpaceItemDecoration(16));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        luRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                OrderModel.OrderDetailModel model = myWorkOrderAdapter.getDataList().get(position);
                Intent detail = new Intent(getActivity(), WorkOrderDetail.class);
                detail.putExtra("id", model.id);
                startActivityForResult(detail, 1);
            }

        });
        mRecyclerView.setOnLoadMoreListener(this);
    }

    @Override
    public void onRefresh() {
        isRefreshing = true;
        mSwipeRefreshLayout.setRefreshing(true);
        myWorkOrderAdapter.clear();
        LuRecyclerViewStateUtils.setFooterViewState(mActivity, mRecyclerView, size, LoadingFooter.State.Loading, null);
        notifyDataSetChanged();
        pageNo = 1;
        requestData();
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

    /**
     * 请求数据
     */
    private void requestData() {
        String page = String.valueOf(pageNo);
        String validateURL = NetConstants.REPAIR_ORDER_LIST;
        Map<String, String> textParams = new HashMap<String, String>();
        textParams.put("token", token);
        textParams.put("status", status+"");
        textParams.put("page", page);
        textParams.put("size", size + "");
        SendRequestUtils.PostDataFromService(textParams, validateURL, orderHandler);
    }

    private void notifyDataSetChanged() {
        luRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void addItems(ArrayList<OrderModel.OrderDetailModel> list) {
        myWorkOrderAdapter.addAll(list);
    }


    private static class PreviewHandler extends Handler {
        private WeakReference<OrderListFragment> ref;

        PreviewHandler(OrderListFragment fragment) {
            ref = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            final OrderListFragment fragment = ref.get();
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




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0x002 || resultCode == 0x003||resultCode==0x004||resultCode==0x005||resultCode==0x006) {
            onRefresh();
        }
    }
    private void getDataSuccess(Message msg) {
        //解析数据
        try {
            Gson gson = new Gson();
            OrderModel orderModel = gson.fromJson(msg.obj.toString(), OrderModel.class);
            if (2 == orderModel.result_code) {
                //重新登陆
                CommonMethod.goLogin(new WeakReference<Activity>(mActivity),1);
            } else {
                if (orderModel.result.equals("success")) {
                    //更新数据
                    OrderModel.OrderDataModel data = orderModel.data;
                    ArrayList<OrderModel.OrderDetailModel> orderList = data.orderList;
                    if (isRefreshing) {
                        //是刷新的数据
                        myWorkOrderAdapter.clear();

                    }
                    addItems(orderList);
                    notifyDataSetChanged();
                    if (orderList.size() < size) {
                        //数据不足18个
                        LuRecyclerViewStateUtils.setFooterViewState(mActivity, mRecyclerView, orderList.size(), LoadingFooter.State.TheEnd, null);
                    } else {
                        LuRecyclerViewStateUtils.setFooterViewState(mActivity, mRecyclerView, orderList.size(), LoadingFooter.State.Normal, null);
                    }
                } else {
                    //失败
                    CommonMethod.faifure(new WeakReference<Activity>(mActivity),orderModel.error);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("OrderFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("OrderFragment");
    }

}
