package com.msht.master.fragment;


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
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.msht.master.Base.BaseHandler;
import com.msht.master.Common.CommonMethod;
import com.msht.master.Constants.NetConstants;
import com.msht.master.Constants.SPConstants;
import com.msht.master.Controls.MyFragmentButton;
import com.msht.master.FunctionView.BankCard;
import com.msht.master.FunctionView.LearnRule;
import com.msht.master.FunctionView.MyEvaluteActivity;
import com.msht.master.FunctionView.MyWallet;
import com.msht.master.FunctionView.MyData;
import com.msht.master.FunctionView.NewPriceBook;
import com.msht.master.FunctionView.PriceTable;
import com.msht.master.HtmlWeb.BuyInsurance;
import com.msht.master.FunctionView.PriceBook;
import com.msht.master.Model.BasicInfoModel;
import com.msht.master.R;
import com.msht.master.UIView.CircleImageView;
import com.msht.master.UIView.CustomDialog;
import com.msht.master.UIView.PromptDialog;
import com.msht.master.Utils.PermissionDog;
import com.msht.master.Utils.SendRequestUtils;
import com.msht.master.Utils.SharedPreferencesUtils;
import com.msht.master.myview.AboutMine;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyFragment extends Fragment implements View.OnClickListener {

    private RelativeLayout Raboutmine, Rhotline;
    private RelativeLayout Rprice;
    private RelativeLayout Rmydata;
    private CircleImageView avatarimg;
    private TextView tv_name, tv_balance;
    private String token;
    private static final int WITH_CODE = 1;
    private CustomDialog customDialog;
    private Activity mActivity;
    private MyFragmentButton mb_my_bankcark;
    private MyFragmentButton mb_my_wallet;
    private MyFragmentButton mb_my_evalute;

    private Handler getBasicInfoHandler=new GetBasicInfoHandler(this);
    private Button btn_buy_insurance;
    private RelativeLayout learn_rule;
    private String number="";


    /**
     * 获取基本信息成功
     */
    private void getBasicInfoSuccess(Message msg) {
        try {
            Gson gson = new Gson();
            BasicInfoModel model = gson.fromJson(msg.obj.toString(), BasicInfoModel.class);
            if (2 == model.result_code) {
                //重新登陆
                CommonMethod.goLogin(new WeakReference<Activity>(mActivity),1);
            } else {
                if (model.result.equals("success")) {
                    //更新数据
                    BasicInfoModel.BasicInfoDetail data = model.data;
                    Glide
                            .with(this)
                            .load(data.avatar)
                            .error(R.drawable.default_portrait)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)//deactivate the disk cache for a request.
                            .skipMemoryCache(true)//glide will not put image in the memory cache
                            .into(avatarimg);
                    tv_name.setText(data.name);
                    tv_balance.setText("¥ " + data.balance);
                    number = data.number;
                } else {
                    //失败
                    CommonMethod.faifure(new WeakReference<Activity>(mActivity),model.error);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my, container, false);
        customDialog = new CustomDialog(getActivity(), "正在加载");
        token = (String) SharedPreferencesUtils.getData(getActivity(), SPConstants.TOKEN, "");
        mActivity=getActivity();
        initView(view);
        initData();
        initEvent();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case WITH_CODE:
                if (resultCode == 1) {
                    initData();
                }
                break;
            default:
                break;
        }
    }


    private void initData() {
        customDialog.show();
        String validateURL = NetConstants.BASCI_INFO;
        Map<String, String> textParams = new HashMap<String, String>();
        textParams.put("token", token);
        SendRequestUtils.PostDataFromService(textParams, validateURL, getBasicInfoHandler);
    }

    private void initView(View view) {
        Raboutmine = (RelativeLayout) view.findViewById(R.id.id_aboutmine_layout);
        Rhotline = (RelativeLayout) view.findViewById(R.id.id_sevice_layout);
        Rprice = (RelativeLayout) view.findViewById(R.id.id_price_layout);
        Rmydata = (RelativeLayout) view.findViewById(R.id.id_data_layout);
        avatarimg = (CircleImageView) view.findViewById(R.id.id_portrait);
        tv_name = (TextView) view.findViewById(R.id.id_mastername);
        tv_balance = (TextView) view.findViewById(R.id.id_balance);

        mb_my_bankcark = (MyFragmentButton) view.findViewById(R.id.mb_my_bankcark);
        mb_my_wallet = (MyFragmentButton) view.findViewById(R.id.mb_my_wallet);
        mb_my_evalute = (MyFragmentButton) view.findViewById(R.id.mb_my_evalute);

        learn_rule = (RelativeLayout)view.findViewById(R.id.learn_rule);

        btn_buy_insurance = (Button) view.findViewById(R.id.btn_buy_insurance);



    }

    private void initEvent() {

        Raboutmine.setOnClickListener(this);
        Rhotline.setOnClickListener(this);
        Rprice.setOnClickListener(this);
        Rmydata.setOnClickListener(this);

        mb_my_bankcark.setOnClickListener(this);
        mb_my_evalute.setOnClickListener(this);
        mb_my_wallet.setOnClickListener(this);
        btn_buy_insurance.setOnClickListener(this);
        learn_rule.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_price_layout:
                //价格手册
                Intent price = new Intent(getActivity(), PriceTable.class);
                startActivity(price);
                break;
            case R.id.id_aboutmine_layout:
                //关于我们
                Intent idea = new Intent(getActivity(), AboutMine.class);
                startActivity(idea);
                break;
            case R.id.id_sevice_layout:
                hotline();
                break;
            case R.id.id_data_layout:
                //我的资料
                Intent data = new Intent(getActivity(), MyData.class);
                startActivity(data);
                break;
            case R.id.mb_my_bankcark:
                Intent bankcard = new Intent(getActivity(), BankCard.class);
                startActivity(bankcard);
                break;
            case R.id.mb_my_wallet:
                Intent wallet = new Intent(getActivity(), MyWallet.class);
                startActivityForResult(wallet, 1);
                break;
            case R.id.mb_my_evalute:
                Intent evalute = new Intent(getActivity(), MyEvaluteActivity.class);
                startActivity(evalute);
                break;
            case R.id.btn_buy_insurance:
                //购买保险
                //有盟数据统计
                MobclickAgent.onEvent(getActivity(), "buyInsurance");
                Intent identy = new Intent(getActivity(), BuyInsurance.class);
                startActivity(identy);
                break;
            case R.id.learn_rule:
                Intent learn = new Intent(getActivity(), LearnRule.class);
                startActivity(learn);
                break;
            default:
                break;
        }
    }


    private void hotline() {
        final String phone = "963666";

        new PromptDialog.Builder(getActivity())
                .setTitle("客服热线")
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
                                        Toast.makeText(getActivity(), "已经拒绝了拨打电话的权限，请前往设置开启", Toast.LENGTH_SHORT);
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
                                PermissionDog.getInstance().requestSinglePermissions(MyFragment.this, Manifest.permission.CALL_PHONE);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionDog.getInstance().notifyPermissionsChanged(getActivity(), requestCode, permissions, grantResults);
    }


    private static class GetBasicInfoHandler extends BaseHandler<MyFragment>{

        public GetBasicInfoHandler(MyFragment object) {
            super(object);
        }

        @Override
        public void onSuccess(MyFragment object, Message msg) {
            object.getBasicInfoSuccess(msg);
        }

        @Override
        public void onFinilly(MyFragment object) {
            object.customDialog.dismiss();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("MyFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MyFragment");
    }
}
