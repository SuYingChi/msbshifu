package com.msht.master.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.recyclerview.LuRecyclerView;
import com.github.jdsjlzx.recyclerview.LuRecyclerViewAdapter;
import com.github.jdsjlzx.util.LuRecyclerViewStateUtils;
import com.github.jdsjlzx.view.LoadingFooter;
import com.google.gson.Gson;
import com.msht.master.Base.BaseHandler;
import com.msht.master.Base.ListBaseAdapter;
import com.msht.master.Constants.NetConstants;
import com.msht.master.Constants.SPConstants;
import com.msht.master.Controls.AlwaysMarqueeTextView;
import com.msht.master.HtmlWeb.FranchisingAgreement;
import com.msht.master.FunctionView.WorkOrderDetail;
import com.msht.master.HtmlWeb.BuyInsurance;
import com.msht.master.LoginActivity;
import com.msht.master.Model.AnnounceModel;
import com.msht.master.Model.ChangeWorkStatusModel;
import com.msht.master.Model.MasterStatus;
import com.msht.master.Model.OrderModel;
import com.msht.master.Model.TakeOrderModel;
import com.msht.master.R;
import com.msht.master.UIView.CustomDialog;
import com.msht.master.UIView.PromptDialog;
import com.msht.master.UIView.SwitchButton;
import com.msht.master.Utils.AppToast;
import com.msht.master.Utils.DialogUtil;
import com.msht.master.Utils.PermissionDog;
import com.msht.master.Utils.SendRequestUtils;
import com.msht.master.Utils.SharedPreferencesUtils;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by hei123 on 2016/12/6.
 */

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, OnLoadMoreListener {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ImageView iv_headimage;//头像
    private String mastername;//师傅名字
    private String avatar;//师傅头像的地址
    private TextView id_mastername;
    private String token;
    private RelativeLayout  Rannounce;
    private FragmentActivity mActivity;
    private SwitchButton sw_workstatus;//工作状态按钮
    private View layout_vertify;//指示是否需要重新认证
    private Button btn_identify;//申请认证的按钮
    private TextView tv_notice;//提示信息
    //private ImageView iv_mascot;//认证审核中
    private TextView tv_freeze;//指示冻结信息
    private LuRecyclerView mRecyclerView;//订单列表
    private String status = "1";//未处理订单
    private int size = 18;
    private int pageNo = 1;//指示当前页
    private boolean isRefreshing;//是不是刷新出来的数据
    private MyWorkOrderAdapter myWorkOrderAdapter;
    private LuRecyclerViewAdapter luRecyclerViewAdapter;
    private TextView tv_order_count;
    private CustomDialog customDialog;
    private Button btn_buy_insurance;

    private Handler getWorkStatusHandler = new GetWorkStatusHandler(this);
    private Handler getOrderListHandler = new GetOrderListHandler(this);
    private Handler changeWorkStatusHandler = new ChangeWorkStatusHandler(this);
    private Handler getAnnounceHandler = new GetAnnounceHandler(this);

    private View layout_work_status;//指示是否显示更改状态按钮
    private View layout_identify;
    private Button btn_i_want_you;//我要加盟
    private View layout_vertifying;
    private AlwaysMarqueeTextView layout_announce;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_newhome, container, false);
        mActivity = getActivity();
        mastername = (String) SharedPreferencesUtils.getData(getActivity(), "MASTER", "");
        avatar = (String) SharedPreferencesUtils.getData(getActivity(), "AVATARURL", "");
        token = (String) SharedPreferencesUtils.getData(getActivity(), SPConstants.TOKEN, "");
        customDialog = new CustomDialog(getActivity(), "正在加载");
        initView(inflate);
        initEvent();
        return inflate;
    }
    private void initView(View view) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setProgressViewOffset(false, 0, DialogUtil.dip2px(getContext(), 48));
            mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
            mSwipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
            mSwipeRefreshLayout.setProgressViewEndTarget(true, 100);
            mSwipeRefreshLayout.setOnRefreshListener(this);
        }
        iv_headimage = (ImageView) view.findViewById(R.id.iv_headimage);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(R.drawable.default_portrait);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        requestOptions.skipMemoryCache(true);
        Glide.with(this).load(avatar).apply(requestOptions).into(iv_headimage);
        id_mastername = (TextView) view.findViewById(R.id.id_mastername);
        id_mastername.setText(mastername);
        Rannounce=(RelativeLayout)view.findViewById(R.id.id_re_annount);
        sw_workstatus = (SwitchButton) view.findViewById(R.id.sw_workstatus);
        tv_order_count = (TextView) view.findViewById(R.id.id_ordernum);

        tv_freeze = (TextView) view.findViewById(R.id.tv_freeze);

        layout_work_status = view.findViewById(R.id.layout_work_status);

        //认证
        layout_vertify = view.findViewById(R.id.layout_vertify);
        btn_identify = (Button) layout_vertify.findViewById(R.id.btn_identify);
        tv_notice = (TextView) layout_vertify.findViewById(R.id.tv_notice);

        //未审核
        layout_identify = view.findViewById(R.id.layout_identify);
        btn_i_want_you = (Button) layout_identify.findViewById(R.id.btn_i_want_you);
        //审核中
        layout_vertifying = view.findViewById(R.id.layout_vertifying);

        layout_announce = (AlwaysMarqueeTextView) view.findViewById(R.id.layout_announce);


        mRecyclerView = (LuRecyclerView) view.findViewById(R.id.recycler_view);
        myWorkOrderAdapter = new MyWorkOrderAdapter(getContext());
        luRecyclerViewAdapter = new LuRecyclerViewAdapter(myWorkOrderAdapter);
        mRecyclerView.setAdapter(luRecyclerViewAdapter);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(16));
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
        btn_buy_insurance = (Button) view.findViewById(R.id.btn_buy_insurance);
        onRefresh();
    }

    private void initEvent() {
        btn_buy_insurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //购买保险
                //有盟数据统计
                MobclickAgent.onEvent(getActivity(), "buyInsurance");
                Intent identy = new Intent(getActivity(), BuyInsurance.class);
                startActivity(identy);

            }
        });
        btn_identify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //申请认证
                Intent identy = new Intent(getActivity(), FranchisingAgreement.class);
                //Intent identy = new Intent(getActivity(), ApplyIdentify.class);
                startActivityForResult(identy, 2);
            }
        });
        btn_i_want_you.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //申请认证
                Intent identy = new Intent(getActivity(), FranchisingAgreement.class);
                //Intent identy = new Intent(getActivity(), ApplyIdentify.class);
                startActivityForResult(identy, 2);
            }
        });
        sw_workstatus.setOnToggleChanged(new SwitchButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                //0休息中 1 工作中
                //String work_status = "0";
                MobclickAgent.onEvent(getActivity(), "changeworkstatus");
                String work_status = on ? "1" : "0";
                //switchs = false;
                customDialog.show();
                //切换工作状态
                String validateURL = NetConstants.CHANGE_WORK_STATUS;
                Map<String, String> textParams = new HashMap<String, String>();
                textParams.put("token", token);
                textParams.put("work_status", work_status);
                SendRequestUtils.PostDataFromService(textParams, validateURL, changeWorkStatusHandler);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        onRefresh();
    }

    @Override
    public void onRefresh() {
        isRefreshing = true;
        mSwipeRefreshLayout.setRefreshing(true);
        myWorkOrderAdapter.clear();
        LuRecyclerViewStateUtils.setFooterViewState(mActivity, mRecyclerView, size, LoadingFooter.State.Loading, null);
        notifyDataSetChanged();
        pageNo = 1;
        getMasterWorkStatus();
        getAnnounce();
    }

    private void getAnnounce() {
        String announceUrl = NetConstants.ANNOUNCE_LIST + "?&num=3";
        SendRequestUtils.GetDataFromService(announceUrl, getAnnounceHandler);
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
        getOrderList();
    }


    private void notifyDataSetChanged() {
        luRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void addItems(ArrayList<OrderModel.OrderDetailModel> list) {

        myWorkOrderAdapter.addAll(list);

    }

    private void hotline(final String phone) {
        new PromptDialog.Builder(getActivity())
                .setTitle("拨打电话")
                .setViewStyle(PromptDialog.VIEW_STYLE_TITLEBAR_SKYBLUE)
                .setMessage(phone)
                .setButton1("取消", new PromptDialog.OnClickListener() {
                    @Override
                    public void onClick(final Dialog dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setButton2("呼叫", new PromptDialog.OnClickListener() {
                    @Override
                    public void onClick(final Dialog dialog, int which) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (getActivity().checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel:" + phone));
                                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                getActivity().startActivity(callIntent);
                            } else {
                                PermissionDog.getInstance().SetAction(new PermissionDog.PermissionDogAction() {
                                    @Override
                                    public void never() {
                                        super.never();
                                        Toast.makeText(getActivity(), "已经拒绝了拨打电话的权限，请前往设置开启", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void denied() {
                                        super.denied();
                                    }

                                    @Override
                                    public void grant() {
                                        super.grant();
                                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                                        callIntent.setData(Uri.parse("tel:" + phone));
                                        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(callIntent);
                                    }
                                });
                                PermissionDog.getInstance().requestSinglePermissions(HomeFragment.this, Manifest.permission.CALL_PHONE);
                            }
                        } else {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + phone));
                            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getActivity().startActivity(callIntent);
                        }
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void gologin() {
        Intent mainintent = new Intent(mActivity, LoginActivity.class);
        mainintent.putExtra("state", 1);
        startActivity(mainintent);
        getActivity().finish();
    }

    private void faifure(String error) {
        new PromptDialog.Builder(mActivity)
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


    /**
     * 改变工作状态
     */
    private void changeWorkStatusSuccess(Message msg) {
        try {
            Gson gson = new Gson();
            ChangeWorkStatusModel changeWorkStatus = gson.fromJson(msg.obj.toString(), ChangeWorkStatusModel.class);
            if (2 == changeWorkStatus.result_code) {
                gologin();
            } else {
                if (changeWorkStatus.result.equals("success")) {
                    boolean currentState = sw_workstatus.getCurrentState();
                } else {
                    faifure(changeWorkStatus.error);
                    if (sw_workstatus.getCurrentState())
                        sw_workstatus.setToggleOff();
                    else
                        sw_workstatus.setToggleOn();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ChangeWorkStatusHandler extends Handler {
        private WeakReference<HomeFragment> ref;

        ChangeWorkStatusHandler(HomeFragment fragment) {
            ref = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            final HomeFragment fragment = ref.get();
            if (fragment == null || fragment.isDetached()) {
                return;
            }
            switch (msg.what) {
                case SendRequestUtils.SUCCESS:
                    fragment.changeWorkStatusSuccess(msg);
                    break;
                case SendRequestUtils.FAILURE:
                    AppToast.makeShortToast(fragment.mActivity, "网络连接失败");
                    break;
                case SendRequestUtils.ERRORCODE:
                    AppToast.makeShortToast(fragment.mActivity, "网络连接失败");
                    break;
            }
            customDialog.dismiss();
        }
    }


    /**
     * 获取订单列表
     */
    private void getOrderList() {
        String page = String.valueOf(pageNo);
        String validateURL = NetConstants.REPAIR_ORDER_LIST;
        Map<String, String> textParams = new HashMap<String, String>();
        textParams.put("token", token);
        textParams.put("size", size + "");
        textParams.put("status", status);
        textParams.put("page", page);
        SendRequestUtils.PostDataFromService(textParams, validateURL, getOrderListHandler);
    }

    private void getOrderListSuccess(Message msg) {
        try {
            Gson gson = new Gson();
            OrderModel orderModel = gson.fromJson(msg.obj.toString(), OrderModel.class);
            if (2 == orderModel.result_code) {
                //重新登陆
                gologin();
            } else {
                if (orderModel.result.equals("success")) {
                    //更新数据
                    OrderModel.OrderDataModel data = orderModel.data;
                    tv_order_count.setText(data.total + "");
                    ArrayList<OrderModel.OrderDetailModel> orderList=new ArrayList<OrderModel.OrderDetailModel>();
                    for (int i=0;i<data.orderList.size();i++){
                        if (!data.orderList.get(i).status.equals("6")){    //首页屏蔽status=6
                            orderList.add(data.orderList.get(i));
                        }
                    }
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
                    faifure(orderModel.error);
                }
            }
        } catch (Exception e) {
            AppToast.makeShortToast(mActivity, "遇到错误了，下载最新版本试试");
        }
    }

    public class GetOrderListHandler extends Handler {
        private WeakReference<HomeFragment> ref;

        GetOrderListHandler(HomeFragment fragment) {
            ref = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            final HomeFragment fragment = ref.get();
            if (fragment == null || fragment.isDetached()) {
                return;
            }
            switch (msg.what) {
                case SendRequestUtils.SUCCESS:
                    fragment.getOrderListSuccess(msg);
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

    private class MyWorkOrderAdapter extends ListBaseAdapter<OrderModel.OrderDetailModel> {
        private LayoutInflater mLayoutInflater;

        public MyWorkOrderAdapter(Context context) {
            mContext = context.getApplicationContext();
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final OrderModel.OrderDetailModel model = mDataList.get(position);
            MyWorkOrderViewHolder viewHolder = (MyWorkOrderViewHolder) holder;
            switch (model.status) {
                case "3":
                    viewHolder.iv_callphone.setVisibility(View.GONE);
                    viewHolder.tv_detail.setVisibility(View.VISIBLE);
                    break;
                default:
                    viewHolder.iv_callphone.setVisibility(View.VISIBLE);
                    viewHolder.tv_detail.setVisibility(View.GONE);
                    break;
            }
            viewHolder.tv_order_num.setText(model.id);
            viewHolder.tv_order_type.setText(model.category);
            viewHolder.tv_address.setText(model.address);
            viewHolder.tv_time.setText(model.time);
            viewHolder.iv_callphone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String phone = model.user_phone;
                    hotline(phone);
                }
            });

        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyWorkOrderViewHolder(mLayoutInflater.inflate(R.layout.item_order_work, parent, false));
        }
    }

    private class MyWorkOrderViewHolder extends RecyclerView.ViewHolder {
        private final TextView tv_order_num;
        private final TextView tv_time;
        private final TextView tv_order_type;
        private final TextView tv_address;
        private final ImageView iv_callphone;
        private final TextView tv_detail;

        public MyWorkOrderViewHolder(View itemView) {
            super(itemView);
            tv_order_num = (TextView) itemView.findViewById(R.id.tv_order_num);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_order_type = (TextView) itemView.findViewById(R.id.tv_order_type);
            tv_address = (TextView) itemView.findViewById(R.id.tv_address);
            iv_callphone = (ImageView) itemView.findViewById(R.id.iv_callphone);
            tv_detail = (TextView) itemView.findViewById(R.id.tv_detail);
        }
    }

    public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = space;
            }
            outRect.bottom = space;

        }
    }




    /**
     * 获取师傅的工作状态
     */
    private void getMasterWorkStatus() {
        String validateURL = NetConstants.REPAIRMAN_STATUS;
        Map<String, String> textParams = new HashMap<String, String>();
        textParams.put("token", token);
        SendRequestUtils.PostDataFromService(textParams, validateURL, getWorkStatusHandler);
    }

    private void getworkstatusSuccess(Message msg) {
        try {
            Gson gson = new Gson();
            MasterStatus masterStatus = gson.fromJson(msg.obj.toString(), MasterStatus.class);
            if (2 == masterStatus.result_code) {
                //重新登陆
                gologin();
            } else {
                if (masterStatus.result.equals("success")) {
                    //成功
                    MasterStatus.MasterDetailModel data = masterStatus.data;
                    if (1 == data.status) {
                        tv_freeze.setVisibility(View.INVISIBLE);
                    } else {
                        tv_freeze.setVisibility(View.VISIBLE);
                    }
                    switch (data.valid) {
                        //认证状态
                        case 0:
                            //未认证
                            mSwipeRefreshLayout.setRefreshing(false);
                            mRecyclerView.setVisibility(View.GONE);
                            layout_vertify.setVisibility(View.GONE);
                            layout_identify.setVisibility(View.VISIBLE);
                            layout_vertifying.setVisibility(View.GONE);
                            layout_work_status.setVisibility(View.GONE);
                            break;
                        case 1:
                            layout_vertify.setVisibility(View.GONE);
                            layout_identify.setVisibility(View.GONE);
                            layout_vertifying.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                            layout_work_status.setVisibility(View.VISIBLE);
                            getOrderList();
                            //已认证
                            break;
                        case 2:
                            mSwipeRefreshLayout.setRefreshing(false);
                            //认证审核中--没有按钮只有图像
                            mRecyclerView.setVisibility(View.GONE);
                            layout_vertify.setVisibility(View.GONE);
                            layout_identify.setVisibility(View.GONE);
                            layout_vertifying.setVisibility(View.VISIBLE);
                            layout_work_status.setVisibility(View.GONE);
                            break;
                        case 3:
                            mSwipeRefreshLayout.setRefreshing(false);
                            //审核失败
                            mRecyclerView.setVisibility(View.GONE);
                            layout_vertify.setVisibility(View.VISIBLE);
                            btn_identify.setVisibility(View.VISIBLE);
                            layout_identify.setVisibility(View.GONE);
                            layout_vertifying.setVisibility(View.GONE);
                            tv_notice.setText("认证失败:" + data.vaid_fail_reason);
                            btn_identify.setText("重新认证");
                            layout_work_status.setVisibility(View.GONE);
                            break;
                    }
                    if (data.work_status == 1) {
                        sw_workstatus.setToggleOn();
                    } else {
                        sw_workstatus.setToggleOff();
                    }
                } else {
                    //失败
                    faifure(masterStatus.error);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class GetWorkStatusHandler extends Handler {
        private WeakReference<HomeFragment> ref;

        GetWorkStatusHandler(HomeFragment fragment) {
            ref = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            final HomeFragment fragment = ref.get();
            if (fragment == null || fragment.isDetached()) {
                return;
            }
            switch (msg.what) {
                case SendRequestUtils.SUCCESS:
                    fragment.getworkstatusSuccess(msg);
                    break;
                case SendRequestUtils.FAILURE:

                case SendRequestUtils.ERRORCODE:

                    AppToast.makeShortToast(fragment.mActivity, "网络连接失败");
                    fragment.mRecyclerView.setVisibility(View.VISIBLE);
                    LuRecyclerViewStateUtils.setFooterViewState(fragment.mActivity, fragment.mRecyclerView, fragment.size, LoadingFooter.State.NetWorkError, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            fragment.onRefresh();
                        }
                    });
                    fragment.notifyDataSetChanged();
                    fragment.isRefreshing = false;
                    fragment.mSwipeRefreshLayout.setRefreshing(false);
                    break;
            }
        }
    }

    private static class GetAnnounceHandler extends BaseHandler<HomeFragment> {

        public GetAnnounceHandler(HomeFragment object) {
            super(object);
        }

        @Override
        public void onSuccess(HomeFragment object, Message msg) {
            object.GetAnnounceSuccess(msg);
        }
    }

    private void GetAnnounceSuccess(Message msg) {
        try {
            Gson gson = new Gson();
            AnnounceModel announceModel = gson.fromJson(msg.obj.toString(), AnnounceModel.class);
            if (2 == announceModel.result_code) {
                gologin();
            } else {
                if (announceModel.result.equals("success")) {
                    if(announceModel.data.size()!=0){
                        StringBuilder result = new StringBuilder();
                        boolean flag = false;
                      //  int i=announceModel.data.size();   //标题数
                        for (AnnounceModel.AnnounceDetail item:announceModel.data) {
                            if (flag) {
                                result.append("         ");
                            } else {
                                flag = true;
                            }
                           // result.append(i);
                           // result.append(":");
                            result.append(item.content);
                        }
                        layout_announce.setText(result.toString());
                    }else{
                        Rannounce.setVisibility(View.GONE);
                        layout_announce.setVisibility(View.GONE);
                    }


                } else {
                    faifure(announceModel.error);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("HomeFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("HomeFragment");
    }
}
