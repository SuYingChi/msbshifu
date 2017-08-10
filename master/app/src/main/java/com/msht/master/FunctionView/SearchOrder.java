package com.msht.master.FunctionView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.recyclerview.LuRecyclerView;
import com.github.jdsjlzx.recyclerview.LuRecyclerViewAdapter;
import com.github.jdsjlzx.util.LuRecyclerViewStateUtils;
import com.github.jdsjlzx.view.LoadingFooter;
import com.google.gson.Gson;
import com.msht.master.Adapter.MyWorkOrderAdapter;
import com.msht.master.Common.CommonMethod;
import com.msht.master.Constants.NetConstants;
import com.msht.master.Constants.SPConstants;
import com.msht.master.Model.OrderModel;
import com.msht.master.R;
import com.msht.master.Utils.AppToast;
import com.msht.master.Utils.SendRequestUtils;
import com.msht.master.Utils.SharedPreferencesUtils;
import com.msht.master.fragment.OrderListFragment;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchOrder extends AppCompatActivity {
    private ImageView cleanimg;
    private TextView  tv_cancel;
    private EditText  et_input;
    private String token;
    private LuRecyclerView mRecyclerView;
    private MyWorkOrderAdapter myWorkOrderAdapter;
    private LuRecyclerViewAdapter luRecyclerViewAdapter;
    private Context mContext;
    private Handler orderHandler = new PreviewHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_order);
        mContext=this;
        token = (String) SharedPreferencesUtils.getData(mContext, SPConstants.TOKEN, "");
        initView();
        myWorkOrderAdapter = new MyWorkOrderAdapter(mContext);
        luRecyclerViewAdapter = new LuRecyclerViewAdapter(myWorkOrderAdapter);
        mRecyclerView.setAdapter(luRecyclerViewAdapter);
        mRecyclerView.addItemDecoration(new MyWorkOrderAdapter.SpaceItemDecoration(16));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        luRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                OrderModel.OrderDetailModel model = myWorkOrderAdapter.getDataList().get(position);
                Intent detail = new Intent(mContext, WorkOrderDetail.class);
                detail.putExtra("id", model.id);
                startActivityForResult(detail, 1);
            }
        });
    }
    private void initView() {
        cleanimg=(ImageView)findViewById(R.id.id_clean);
        tv_cancel=(TextView)findViewById(R.id.id_cancel);
        et_input=(EditText)findViewById(R.id.id_input_orderNo);
        mRecyclerView = (LuRecyclerView)findViewById(R.id.recycler_view);
        et_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                myWorkOrderAdapter.clear();
                notifyDataSetChanged();
                if (!TextUtils.isEmpty(et_input.getText().toString())) {
                    requestData();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {finish();}
        });
        cleanimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {et_input.setText("");}
        });
    }
    private void notifyDataSetChanged() {
        luRecyclerViewAdapter.notifyDataSetChanged();
    }
    private void requestData() {
        String keyword =et_input.getText().toString().trim();
        String validateURL = NetConstants.SEARCH_ORDER_LIST;
        Map<String, String> textParams = new HashMap<String, String>();
        textParams.put("token", token);
        textParams.put("kw", keyword);
        SendRequestUtils.PostDataFromService(textParams, validateURL, orderHandler);
    }
    private static class PreviewHandler extends Handler {
        private WeakReference<SearchOrder> ref;

        PreviewHandler(SearchOrder activity) {
            ref = new WeakReference<>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            final SearchOrder activity = ref.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            switch (msg.what) {
                case SendRequestUtils.SUCCESS:
                    activity.getDataSuccess(msg);
                    break;
                case SendRequestUtils.FAILURE:
                    activity.notifyDataSetChanged();
                    AppToast.makeShortToast(activity, "网络连接失败");
                    break;
                case SendRequestUtils.ERRORCODE:
                    AppToast.makeShortToast(activity, "系统连接失败");
                    break;
            }
        }
    }

    private void getDataSuccess(Message msg) {
        //解析数据
        try {
            Gson gson = new Gson();
            OrderModel orderModel = gson.fromJson(msg.obj.toString(), OrderModel.class);
            if (2 == orderModel.result_code) {
                //重新登陆
                CommonMethod.goLogin(new WeakReference<Activity>(this),1);
            } else {
                if (orderModel.result.equals("success")) {
                    //更新数据
                    OrderModel.OrderDataModel data = orderModel.data;
                    ArrayList<OrderModel.OrderDetailModel> orderList = data.orderList;
                    if (data.orderList.size()!=0){
                        findViewById(R.id.id_nodata).setVisibility(View.GONE);
                    }else {
                        findViewById(R.id.id_nodata).setVisibility(View.VISIBLE);
                    }
                    myWorkOrderAdapter.clear();
                    addItems(orderList);
                    notifyDataSetChanged();
                } else {
                    //失败
                    CommonMethod.faifure(new WeakReference<Activity>(this),orderModel.error);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addItems(ArrayList<OrderModel.OrderDetailModel> list) {
        myWorkOrderAdapter.addAll(list);
    }
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SearchOrder");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SearchOrder");
    }
}
