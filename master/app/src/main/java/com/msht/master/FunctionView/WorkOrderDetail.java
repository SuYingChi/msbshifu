package com.msht.master.FunctionView;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.msht.master.Adapter.PhotoAdapter;
import com.msht.master.Common.CommonMethod;
import com.msht.master.Constants.NetConstants;
import com.msht.master.Constants.SPConstants;
import com.msht.master.Controls.FullyGridLayoutManager;
import com.msht.master.Model.OrderViewModel;
import com.msht.master.R;
import com.msht.master.UIView.ConvertOrderDialog;
import com.msht.master.UIView.CustomDialog;
import com.msht.master.UIView.PromptDialog;
import com.msht.master.Utils.AppToast;
import com.msht.master.Utils.PermissionDog;
import com.msht.master.Utils.SendRequestUtils;
import com.msht.master.Utils.SharedPreferencesUtils;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hei on 2016/12/20.
 */

public class WorkOrderDetail extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_order_num;//订单编号
    private TextView tv_order_status;//订单状态
    private ImageView iv_order_type;//图片展示的订单类型
    private TextView tv_second_type;//维修小类
    private TextView tv_time;//订单生成时间
    private TextView tv_username;//用户的姓名
    private TextView tv_phone_num;//用户的电话号码
    private ImageView iv_callphone;//拨打电话的按钮
    private TextView tv_address;//用户的地址
    private TextView tv_appoint_time;//用户预约时间
    private TextView tv_problem_desc;//问题描述
    private CustomDialog customDialog;
    private String id;
    private String token;
    private View layout_workdetail_pic;
    private View layout_expanse_bill;
    private View layout_user_bill_pay;
    private Button btn_convert;//转单按钮
    private Button btn_receive;//接单按钮
    private Button btn_finish;//完工按钮
    private Button btn_replace_pay;//代付按钮
    private TextView id_et_detect_fee;//上门检测费
    private TextView id_et_material_fee;//配件材料费
    private TextView id_serve_fee;//维修服务费
    private Button id_btn_rewrite;//重新提交
    private TextView id_tv_total;//理论总额
    private View layout_control;//控制面板
    private TextView tv_coupon;//优惠券
    private TextView tv_fact_pay;//订单实付
    private int ConvertCode = 0x003;
    private int ReceiveCode = 0x002;
    private int SubmitCode = 0x004;
    private int ReplacePayCode = 0x005;

    private Handler getOrderDetailHandler = new GetOrderDetailHanlder(this);
    private Handler receiveOrderHandler = new ReceiveOrderHandler(this);
    private Handler convertOrderHandler = new ConvertOrderHandler(this);
    private OrderViewModel.OrderViewDetail data;
    private RecyclerView recycler_photo;//展示图片的
    private PhotoAdapter photoAdapter;
    private View layout_call_phone;//拨打电话所在的layout


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_workorder_detail);
        Intent data = getIntent();
        id = data.getStringExtra("id");
        token = (String) SharedPreferencesUtils.getData(getApplicationContext(), SPConstants.TOKEN, "");
        customDialog = new CustomDialog(this, "正在加载...");
        initHeaderTitle();
        initView();
        initEvent();
        GetWorkDetail();
    }

    private void initEvent() {
        btn_receive.setOnClickListener(this);
        btn_convert.setOnClickListener(this);
        btn_finish.setOnClickListener(this);
        btn_replace_pay.setOnClickListener(this);
        id_btn_rewrite.setOnClickListener(this);
        layout_call_phone.setOnClickListener(this);
    }

    private void GetWorkDetail() {
        customDialog.show();
        String validateURL = NetConstants.REPAIR_ORDER_VIEW;
        Map<String, String> textParams = new HashMap<String, String>();
        textParams.put("token", token);
        textParams.put("id", id);
        SendRequestUtils.PostDataFromService(textParams, validateURL, getOrderDetailHandler);
    }

    private void initView() {
        tv_order_num = (TextView) findViewById(R.id.tv_order_num);
        tv_order_status = (TextView) findViewById(R.id.tv_order_status);
        iv_order_type = (ImageView) findViewById(R.id.iv_order_type);
        tv_second_type = (TextView) findViewById(R.id.tv_second_type);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_username = (TextView) findViewById(R.id.tv_username);
        tv_phone_num = (TextView) findViewById(R.id.tv_phone_num);
        iv_callphone = (ImageView) findViewById(R.id.iv_callphone);
        layout_call_phone = findViewById(R.id.layout_call_phone);
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_appoint_time = (TextView) findViewById(R.id.tv_appoint_time);
        tv_problem_desc = (TextView) findViewById(R.id.tv_problem_desc);


        layout_workdetail_pic = findViewById(R.id.layout_workdetail_pic);

        layout_expanse_bill = findViewById(R.id.layout_expanse_bill);
        id_btn_rewrite = (Button) layout_expanse_bill.findViewById(R.id.id_btn_rewrite);
        id_et_detect_fee = (TextView) layout_expanse_bill.findViewById(R.id.id_et_detect_fee);
        id_et_material_fee = (TextView) layout_expanse_bill.findViewById(R.id.id_et_material_fee);
        id_serve_fee = (TextView) layout_expanse_bill.findViewById(R.id.id_serve_fee);
        id_tv_total = (TextView) layout_expanse_bill.findViewById(R.id.id_tv_total);

        layout_user_bill_pay = findViewById(R.id.layout_user_bill_pay);
        tv_coupon = (TextView) layout_user_bill_pay.findViewById(R.id.tv_coupon);
        tv_fact_pay = (TextView) layout_user_bill_pay.findViewById(R.id.tv_fact_pay);

        layout_control = findViewById(R.id.layout_control);

        btn_convert = (Button) layout_control.findViewById(R.id.btn_convert);
        btn_receive = (Button) layout_control.findViewById(R.id.btn_receive);
        btn_finish = (Button) layout_control.findViewById(R.id.btn_finish);
        btn_replace_pay = (Button) layout_control.findViewById(R.id.btn_replace_pay);

        recycler_photo = (RecyclerView) findViewById(R.id.recycler_photo);
        FullyGridLayoutManager manager = new FullyGridLayoutManager(this, 4);
        manager.setOrientation(GridLayoutManager.VERTICAL);
        manager.setSmoothScrollbarEnabled(true);
        recycler_photo.setLayoutManager(manager);
        photoAdapter = new PhotoAdapter(this);
        recycler_photo.setAdapter(photoAdapter);
    }

    private void initHeaderTitle() {
        findViewById(R.id.id_goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("订单详情");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_convert:
                convertOrder();
                //转单
                break;
            case R.id.btn_finish:
                //完工
                finishOrder();
                break;
            case R.id.btn_replace_pay:
                Intent pay = new Intent(this, ReplacePay.class);
                pay.putExtra("orderId", data.id);
                startActivityForResult(pay, 0);
                //代付
                break;
            case R.id.btn_receive:
                //接单
                receiveOrder();
                break;
            case R.id.id_btn_rewrite:
                finishOrder();
                break;
            case R.id.layout_call_phone:
                if (iv_callphone.getVisibility() == View.VISIBLE) {
                    hotline();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                //代付
                if (resultCode == 2) {
                    setResult(ReplacePayCode);
                    layout_control.setVisibility(View.GONE);
                    //代付成功
                    GetWorkDetail();
                }
                break;
            case 1:
                //提交账单
                if (resultCode == 0x001) {
                    setResult(SubmitCode);
                    layout_control.setVisibility(View.GONE);
                    GetWorkDetail();
                }
                break;
        }


    }

    private void finishOrder() {
        Intent intent = new Intent(WorkOrderDetail.this, SubmitBill.class);
        intent.putExtra("id", data.id);
        startActivityForResult(intent, 1);
    }

    private void convertOrder() {
        final ConvertOrderDialog convertdialog = new ConvertOrderDialog(WorkOrderDetail.this);
        final RadioButton radioSix = (RadioButton) convertdialog.getRadioButon();
        final EditText otherReason = (EditText) convertdialog.getReasonText();
        convertdialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                convertdialog.dismiss();
            }
        });
        convertdialog.setOnpositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String reason = "";
                if (radioSix.isChecked()) {
                    reason = otherReason.getText().toString().trim();
                } else {
                    reason = (String) convertdialog.getStringText();
                }
                if (TextUtils.isEmpty(reason)) {
                    Toast.makeText(WorkOrderDetail.this, "请输入原因，帮助我们改进", Toast.LENGTH_SHORT).show();
                } else {
                    //转单
                    String validateURL = NetConstants.REPAIR_ORDER_CANCAL;
                    Map<String, String> textParams = new HashMap<String, String>();
                    textParams.put("token", token);
                    textParams.put("id", id);
                    textParams.put("reason", reason);
                    SendRequestUtils.PostDataFromService(textParams, validateURL, convertOrderHandler);
                    convertdialog.dismiss();
                }

            }
        });
        convertdialog.show();
    }

    private void receiveOrder() {
        customDialog.show();
        //确认接单
        String validateURL = NetConstants.REPAIR_ORDER_ACCEPT;
        Map<String, String> textParams = new HashMap<String, String>();
        textParams.put("token", token);
        textParams.put("id", id);
        SendRequestUtils.PostDataFromService(textParams, validateURL, receiveOrderHandler);
    }

    private class GetOrderDetailHanlder extends Handler {
        private WeakReference<WorkOrderDetail> ref;

        GetOrderDetailHanlder(WorkOrderDetail activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final WorkOrderDetail activity = ref.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            switch (msg.what) {
                case SendRequestUtils.SUCCESS:
                    activity.getDetailSuccess(msg);
                    break;
                case SendRequestUtils.FAILURE:
                    AppToast.makeShortToast(activity, "网络连接失败");
                    break;
                case SendRequestUtils.ERRORCODE:
                    AppToast.makeShortToast(activity, "网络连接失败");
                    break;
            }
            customDialog.dismiss();
        }
    }

    private void getDetailSuccess(Message msg) {
        customDialog.dismiss();
        try {
            Gson gson = new Gson();
            OrderViewModel orderViewModel = gson.fromJson(msg.obj.toString(), OrderViewModel.class);
            if (2 == orderViewModel.result_code) {
                //重新登陆
                CommonMethod.goLogin(new WeakReference<Activity>(WorkOrderDetail.this), 2);
            } else {
                if (orderViewModel.result.equals("success")) {
                    analyseData(orderViewModel);
                } else {
                    //失败
                    CommonMethod.faifure(new WeakReference<Activity>(WorkOrderDetail.this), orderViewModel.error);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void analyseData(OrderViewModel orderViewModel) {
        //更新数据
        data = orderViewModel.data;
        tv_order_num.setText(data.id);
        tv_second_type.setText(data.category);
        tv_time.setText(data.time);
        tv_username.setText(data.username);
        tv_phone_num.setText(data.user_phone);
        tv_address.setText(data.address);
        tv_appoint_time.setText(data.appoint_time);
        tv_problem_desc.setText(data.memo);
        photoAdapter.SetImageUrlList(data.img);
        photoAdapter.notifyDataSetChanged();
        switch (data.status) {
            case "3": //已派单
                tv_order_status.setText("待接单");
                layout_workdetail_pic.setVisibility(View.VISIBLE);
                layout_expanse_bill.setVisibility(View.GONE);
                layout_user_bill_pay.setVisibility(View.GONE);
                layout_control.setVisibility(View.VISIBLE);

                btn_convert.setVisibility(View.VISIBLE);
                btn_receive.setVisibility(View.VISIBLE);
                btn_finish.setVisibility(View.INVISIBLE);
                btn_replace_pay.setVisibility(View.INVISIBLE);
                break;
            case "5":
                //服务中
                tv_order_status.setText("服务中");
                layout_workdetail_pic.setVisibility(View.VISIBLE);
                layout_expanse_bill.setVisibility(View.GONE);
                layout_user_bill_pay.setVisibility(View.GONE);
                layout_control.setVisibility(View.VISIBLE);

                btn_convert.setVisibility(View.VISIBLE);
                btn_receive.setVisibility(View.INVISIBLE);
                btn_finish.setVisibility(View.VISIBLE);
                btn_replace_pay.setVisibility(View.INVISIBLE);
                break;
            case "6": //待支付
                //用户未选择支付渠道时，师傅也能帮他代付
                tv_order_status.setText("待支付");
                layout_workdetail_pic.setVisibility(View.VISIBLE);
                layout_expanse_bill.setVisibility(View.VISIBLE);
                id_btn_rewrite.setVisibility(View.VISIBLE);
                layout_user_bill_pay.setVisibility(View.GONE);
                layout_control.setVisibility(View.VISIBLE);

                btn_replace_pay.setVisibility(View.VISIBLE);
                btn_convert.setVisibility(View.INVISIBLE);
                btn_receive.setVisibility(View.INVISIBLE);
                btn_finish.setVisibility(View.INVISIBLE);
                showBill(data);
                break;
            case "7":
                //待评价
                //用户端如果点击了支付了，包括选择师傅代付，状态变为待评价
                //
                tv_order_status.setText("待评价");
                layout_workdetail_pic.setVisibility(View.VISIBLE);
                layout_expanse_bill.setVisibility(View.VISIBLE);
                if (data.replace_pay_status == null) {
                    //走到了待评价，并且status为空说明 用户未选择支付方式，师傅直接支付了
                    //此时相当于订单完成 不展示控制面板
                    layout_control.setVisibility(View.GONE);
                    layout_user_bill_pay.setVisibility(View.GONE);
                } else {
                    if (data.replace_pay_status.equals("0")) {
                        layout_user_bill_pay.setVisibility(View.VISIBLE);
                        layout_control.setVisibility(View.VISIBLE);
                        btn_replace_pay.setVisibility(View.VISIBLE);
                    } else {
                        //用户选择了师傅代付，并且师傅已经代付完成
                        layout_user_bill_pay.setVisibility(View.VISIBLE);
                        layout_control.setVisibility(View.GONE);
                        btn_replace_pay.setVisibility(View.INVISIBLE);
                    }
                    showUserPay(data);
                }
                id_btn_rewrite.setVisibility(View.INVISIBLE);
                btn_convert.setVisibility(View.INVISIBLE);
                btn_receive.setVisibility(View.INVISIBLE);
                btn_finish.setVisibility(View.INVISIBLE);
                showBill(data);
                break;
            case "8":
                //已完成
                //订单状态已完成 也可能未完成代付
                tv_order_status.setText("已完成");
                layout_workdetail_pic.setVisibility(View.VISIBLE);
                layout_expanse_bill.setVisibility(View.VISIBLE);
                layout_user_bill_pay.setVisibility(View.VISIBLE);
                if (data.replace_pay_status == null) {
                    layout_control.setVisibility(View.GONE);
                    layout_user_bill_pay.setVisibility(View.GONE);
                } else {
                    if (data.replace_pay_status.equals("0")) {
                        //用户选择了师傅代付，但师傅还没有代付
                        layout_user_bill_pay.setVisibility(View.VISIBLE);
                        layout_control.setVisibility(View.VISIBLE);
                        btn_replace_pay.setVisibility(View.VISIBLE);
                    } else {
                        layout_user_bill_pay.setVisibility(View.VISIBLE);
                        layout_control.setVisibility(View.GONE);
                        btn_replace_pay.setVisibility(View.INVISIBLE);
                    }
                    showUserPay(data);
                }
                id_btn_rewrite.setVisibility(View.INVISIBLE);
                btn_convert.setVisibility(View.INVISIBLE);
                btn_receive.setVisibility(View.INVISIBLE);
                btn_finish.setVisibility(View.INVISIBLE);
                iv_callphone.setVisibility(View.INVISIBLE);
                showBill(data);
                break;
        }
    }

    private class ReceiveOrderHandler extends Handler {
        private WeakReference<WorkOrderDetail> ref;

        ReceiveOrderHandler(WorkOrderDetail activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final WorkOrderDetail activity = ref.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            switch (msg.what) {
                case SendRequestUtils.SUCCESS:
                    activity.receiveOrderSuccess(msg);
                    break;
                case SendRequestUtils.FAILURE:
                    AppToast.makeShortToast(activity, "网络连接失败");
                    break;
                case SendRequestUtils.ERRORCODE:
                    AppToast.makeShortToast(activity, "网络连接失败");
                    break;
            }
            customDialog.dismiss();
        }
    }

    private void receiveOrderSuccess(Message msg) {
        new PromptDialog.Builder(this)
                .setTitle(R.string.dialog_title)
                .setViewStyle(PromptDialog.VIEW_STYLE_TITLEBAR_SKYBLUE)
                .setMessage("接单成功")
                .setButton1("确定", new PromptDialog.OnClickListener() {

                    @Override
                    public void onClick(Dialog dialog, int which) {
                        setResult(ReceiveCode);
                        btn_finish.setVisibility(View.VISIBLE);
                        btn_receive.setVisibility(View.INVISIBLE);
                        dialog.dismiss();
                    }
                }).show();
        tv_order_status.setText("服务中");
    }

    private static class ConvertOrderHandler extends Handler {
        private WeakReference<WorkOrderDetail> ref;

        ConvertOrderHandler(WorkOrderDetail activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final WorkOrderDetail activity = ref.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            switch (msg.what) {
                case SendRequestUtils.SUCCESS:
                    activity.convertOrderSuccess(msg);
                    break;
                case SendRequestUtils.FAILURE:
                    AppToast.makeShortToast(activity, "网络连接失败");
                    break;
                case SendRequestUtils.ERRORCODE:
                    AppToast.makeShortToast(activity, "网络连接失败");
                    break;
            }
            activity.customDialog.dismiss();
        }
    }

    private void convertOrderSuccess(Message msg) {
        new PromptDialog.Builder(this)
                .setTitle(R.string.dialog_title)
                .setViewStyle(PromptDialog.VIEW_STYLE_TITLEBAR_SKYBLUE)
                .setMessage("转单成功")
                .setButton1("确定", new PromptDialog.OnClickListener() {

                    @Override
                    public void onClick(Dialog dialog, int which) {
                        setResult(ConvertCode);
                        finish();
                        dialog.dismiss();
                    }
                }).show();

    }

    private void hotline() {
        new PromptDialog.Builder(this)
                .setTitle("拨打电话")
                .setViewStyle(PromptDialog.VIEW_STYLE_TITLEBAR_SKYBLUE)
                .setMessage(data.user_phone)
                .setButton1("取消", new PromptDialog.OnClickListener() {

                    @Override
                    public void onClick(Dialog dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setButton2("呼叫", new PromptDialog.OnClickListener() {

                    @Override
                    public void onClick(final Dialog dialog, int which) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (WorkOrderDetail.this.checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel:" + data.user_phone));
                                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(callIntent);
                            } else {
                                //没有权限
                                PermissionDog.getInstance().SetAction(new PermissionDog.PermissionDogAction() {
                                    @Override
                                    public void never() {
                                        super.never();
                                        Toast.makeText(getApplicationContext(), "已经拒绝了拨打电话的权限，请前往设置开启", Toast.LENGTH_SHORT);
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void denied() {
                                        super.denied();
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void grant() {
                                        super.grant();
                                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                                        callIntent.setData(Uri.parse("tel:" + data.user_phone));
                                        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        try {
                                            startActivity(callIntent);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                PermissionDog.getInstance().requestSinglePermissions(WorkOrderDetail.this, Manifest.permission.CALL_PHONE);
                            }
                        } else {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + data.user_phone));
                            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(callIntent);
                        }
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionDog.getInstance().notifyPermissionsChanged(this, requestCode, permissions, grantResults);
    }

    private void showUserPay(OrderViewModel.OrderViewDetail data) {
        tv_coupon.setText(data.discount_fee + "元");
        tv_fact_pay.setText(data.replace_pay_amount + "元");
    }

    private void showBill(OrderViewModel.OrderViewDetail data) {
        id_et_detect_fee.setText(data.detect_fee + "元");
        id_et_material_fee.setText(data.material_fee + "元");
        id_serve_fee.setText(data.serve_fee + "元");
        double total = data.detect_fee + data.material_fee + data.serve_fee;
        double tals=BigDecinmals(total);
        id_tv_total.setText(tals + "元");
    }
    private double BigDecinmals(double totals) {
        long lon=Math.round(totals*100);
        double tot=lon/100.0;
        return tot;
    }
}

