package com.msht.master.FunctionView;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.recyclerview.LuRecyclerView;
import com.github.jdsjlzx.recyclerview.LuRecyclerViewAdapter;
import com.github.jdsjlzx.util.LuRecyclerViewStateUtils;
import com.github.jdsjlzx.view.LoadingFooter;
import com.google.gson.Gson;
import com.msht.master.Adapter.IncomeAdapter;
import com.msht.master.Common.CommonMethod;
import com.msht.master.Constants.NetConstants;
import com.msht.master.Constants.SPConstants;
import com.msht.master.Controls.WalletButton;
import com.msht.master.Model.IncomeModel;
import com.msht.master.Model.WalletOverView;
import com.msht.master.R;
import com.msht.master.UIView.DatePickerDialog;
import com.msht.master.Utils.AppToast;
import com.msht.master.Utils.DialogUtil;
import com.msht.master.Utils.SendRequestUtils;
import com.msht.master.Utils.SharedPreferencesUtils;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hei123 on 2016/12/1.
 */

public class MyWallet extends Activity implements SwipeRefreshLayout.OnRefreshListener, OnLoadMoreListener, View.OnClickListener {

    private LuRecyclerView mRecyclerView;
    private String token;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private IncomeAdapter incomeAdapter;
    private LuRecyclerViewAdapter mLRecyclerViewAdapter;
    private View header1;
    private View header2;//recyclerview中的标题栏
    private View layout_choose;//置顶用的标题栏
    private WalletButton wb_balance;
    private WalletButton wb_reword_amount;
    private WalletButton wb_assurance;
    private TextView tv_income;
    private String month;
    private boolean isRefreshing;
    private int pageNo = 1;//当前页数
    private int size = 18;//每页加载的大小
    private TextView header2_tv_month, layout_choose_month;//展示月份

    private Handler overViewHandler = new OverViewHandler(this);
    private Handler monthIcomeListHandler = new MonthIncomeListHandler(this);
    private TextView layout_choose_income;
    private TextView header2_tv_income;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newwallet);
        token = (String) SharedPreferencesUtils.getData(getApplicationContext(), SPConstants.TOKEN, "");
        initHeaderTitle();
        initView();

    }

    private void DisPlayMonth(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        month = format.format(date);
        layout_choose_month.setText(month);
        header2_tv_month.setText(month);
    }

    private void initHeaderTitle() {
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("我的钱包");
        findViewById(R.id.id_goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.id_tv_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent record = new Intent(MyWallet.this, WithDrawRecord.class);
                startActivity(record);
            }
        });
    }

    private void initView() {
        mRecyclerView = (LuRecyclerView) findViewById(R.id.recycler_income);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setProgressViewOffset(false, 0, DialogUtil.dip2px(this, 48));
            mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
            mSwipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
            mSwipeRefreshLayout.setProgressViewEndTarget(true, 100);
            mSwipeRefreshLayout.setOnRefreshListener(this);
        }

        layout_choose = findViewById(R.id.layout_choose);
        layout_choose_month = (TextView) layout_choose.findViewById(R.id.id_month);
        layout_choose_income = (TextView) layout_choose.findViewById(R.id.id_month_income);
        layout_choose.setOnClickListener(this);

        header1 = LayoutInflater.from(this).inflate(R.layout.layout_wallet_header_1, (ViewGroup) findViewById(android.R.id.content), false);
        tv_income = (TextView) header1.findViewById(R.id.tv_income);
        wb_balance = (WalletButton) header1.findViewById(R.id.wb_balance);
        wb_reword_amount = (WalletButton) header1.findViewById(R.id.wb_reword_amount);
        wb_assurance = (WalletButton) header1.findViewById(R.id.wb_assurance);

        header2 = LayoutInflater.from(this).inflate(R.layout.layout_wallet_header_2, (ViewGroup) findViewById(android.R.id.content), false);
        header2_tv_month = (TextView) header2.findViewById(R.id.id_month);
        header2_tv_income = (TextView) header2.findViewById(R.id.id_month_income);
        header2.setOnClickListener(this);

        incomeAdapter = new IncomeAdapter(this);
        mLRecyclerViewAdapter = new LuRecyclerViewAdapter(incomeAdapter);
        mRecyclerView.setAdapter(mLRecyclerViewAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mLRecyclerViewAdapter.addHeaderView(header1);
        mLRecyclerViewAdapter.addHeaderView(header2);

        mRecyclerView.setOnLoadMoreListener(this);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            //导航栏置顶
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
                //判断是当前layoutManager是否为LinearLayoutManager
                // 只有LinearLayoutManager才有查找第一个和最后一个可见view位置的方法
                if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                    //获取第一个可见view的位置
                    int firstItemPosition = linearManager.findFirstVisibleItemPosition();
                    if (firstItemPosition == 0) {
                        layout_choose.setVisibility(View.INVISIBLE);
                    } else {
                        layout_choose.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        findViewById(R.id.id_btn_withdraw).setOnClickListener(this);
        wb_assurance.setOnClickListener(this);
        wb_balance.setOnClickListener(this);
        wb_reword_amount.setOnClickListener(this);
        DisPlayMonth(new java.util.Date());
        onRefresh();
    }


    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        isRefreshing = true;
        incomeAdapter.clear();
        LuRecyclerViewStateUtils.setFooterViewState(this, mRecyclerView, size, LoadingFooter.State.Loading, null);
        notifyDataSetChanged();
        pageNo=1;
        getOverViewData();
        GetMonthIncomeList();
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
        LuRecyclerViewStateUtils.setFooterViewState(MyWallet.this, mRecyclerView, size, LoadingFooter.State.Loading, null);
        GetMonthIncomeList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_choose:
            case R.id.layout_choose:
                Calendar calendar = Calendar.getInstance();
                new DatePickerDialog(MyWallet.this, 0, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker startDatePicker, int startYear, int startMonthOfYear,
                                          int startDayOfMonth) {
                        String dateString = String.format("%d-%d", startYear, startMonthOfYear + 1);
                        try {
                            SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM");
                            Date d = sim.parse(dateString);
                            DisPlayMonth(d);
                            onRefresh();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE)).show();
                break;
            case R.id.id_btn_withdraw:
                Intent with = new Intent(this, WithDraw.class);
                startActivityForResult(with, 2);
                break;
            case R.id.wb_assurance:
                Intent quality = new Intent(this, QualityAssurance.class);
                startActivity(quality);
                break;
            case R.id.wb_balance:
                break;
            case R.id.wb_reword_amount:
                Intent reward = new Intent(this, RewordAndPunishHistory.class);
                startActivity(reward);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 2:
                if (resultCode == 2) {
                    setResult(1);
                    onRefresh();
                }
                break;
            default:
                break;
        }
    }

    private void notifyDataSetChanged() {
        mLRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void addItems(ArrayList<IncomeModel.IncomeDetail> list) {

        incomeAdapter.addAll(list);

    }


    private void GetMonthIncomeList() {
        String page = String.valueOf(pageNo);
        String validateURL = NetConstants.MONTH_INCOME;
        Map<String, String> textParams = new HashMap<String, String>();
        textParams.put("token", token);
        textParams.put("month", month);
        textParams.put("page", page);
        textParams.put("size", size + "");
        SendRequestUtils.PostDataFromService(textParams, validateURL, monthIcomeListHandler);
    }

    private void getMonthIncomeListSuccess(Message msg) {
        //解析数据
        try {
            Gson gson = new Gson();
            IncomeModel incomeModel = gson.fromJson(msg.obj.toString(), IncomeModel.class);
            if (2 == incomeModel.result_code) {
                //重新登陆
                CommonMethod.goLogin(new WeakReference<Activity>(this), 2);
                //gologin();
            } else {
                if (incomeModel.result.equals("success")) {
                    //更新数据
                    IncomeModel.MonthIncome data = incomeModel.data;
                    layout_choose_income.setText(data.amount+"元");
                    header2_tv_income.setText(data.amount+"元");
                    ArrayList<IncomeModel.IncomeDetail> list = data.list;
                    if (isRefreshing) {
                        //是刷新的数据
                        incomeAdapter.clear();

                    }
                    addItems(list);
                    notifyDataSetChanged();
                    if (list.size() < size) {
                        //数据不足18个
                        LuRecyclerViewStateUtils.setFooterViewState(MyWallet.this, mRecyclerView, size, LoadingFooter.State.TheEnd, null);
                    } else {
                        LuRecyclerViewStateUtils.setFooterViewState(MyWallet.this, mRecyclerView, size, LoadingFooter.State.Normal, null);
                    }
                } else {
                    //失败
                    CommonMethod.faifure(new WeakReference<Activity>(this), incomeModel.error);
                    //faifure(incomeModel.error);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static class MonthIncomeListHandler extends Handler {
        private WeakReference<MyWallet> ref;

        MonthIncomeListHandler(MyWallet activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final MyWallet activity = ref.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            switch (msg.what) {
                case SendRequestUtils.SUCCESS:
                    activity.getMonthIncomeListSuccess(msg);
                    break;
                case SendRequestUtils.FAILURE:
                    AppToast.makeShortToast(activity, "网络连接失败");
                    break;
                case SendRequestUtils.ERRORCODE:
                    AppToast.makeShortToast(activity, "网络连接失败");
                    break;
            }
            activity.isRefreshing = false;
            activity.mSwipeRefreshLayout.setRefreshing(false);
        }
    }


    /**
     * 获取总览数据
     */
    private void getOverViewData() {
        String validateURL = NetConstants.WALLET_OVERVIEW;
        Map<String, String> textParams = new HashMap<String, String>();
        textParams.put("token", token);
        SendRequestUtils.PostDataFromService(textParams, validateURL, overViewHandler);
    }

    private void GetOverViewSuccess(Message msg) {
        try {
            Gson gson = new Gson();
            WalletOverView walletOverView = gson.fromJson(msg.obj.toString(), WalletOverView.class);
            if (2 == walletOverView.result_code) {
                //重新登陆
                CommonMethod.goLogin(new WeakReference<Activity>(this), 2);
            } else {
                if (walletOverView.result.equals("success")) {
                    WalletOverView.WalletOverViewDetaile data = walletOverView.data;
                    wb_balance.SetDesc(data.amount + "元");
                    wb_reword_amount.SetDesc(data.reward_amount + "元");
                    wb_assurance.SetDesc(data.quality_assurance_amount + "元");
                    tv_income.setText(data.total_amount + "元");
                } else {
                    //失败
                    CommonMethod.faifure(new WeakReference<Activity>(this), walletOverView.error);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class OverViewHandler extends Handler {
        private WeakReference<MyWallet> ref;

        OverViewHandler(MyWallet activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final MyWallet activity = ref.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            switch (msg.what) {
                case SendRequestUtils.SUCCESS:
                    activity.GetOverViewSuccess(msg);
                    break;
                case SendRequestUtils.FAILURE:
                    AppToast.makeShortToast(activity, "网络连接失败");
                    break;
                case SendRequestUtils.ERRORCODE:
                    AppToast.makeShortToast(activity, "网络连接失败");
                    break;
            }
        }
    }
}
