package com.msht.master.FunctionView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.msht.master.Base.BaseHandler;
import com.msht.master.Common.CommonMethod;
import com.msht.master.Constants.NetConstants;
import com.msht.master.Constants.SPConstants;
import com.msht.master.Controls.ImageTextButton;
import com.msht.master.Model.BasicInfoModel;
import com.msht.master.Model.MyDataModel;
import com.msht.master.R;
import com.msht.master.UIView.CustomDialog;
import com.msht.master.Utils.AppToast;
import com.msht.master.Utils.SendRequestUtils;
import com.msht.master.Utils.SharedPreferencesUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hei on 2016/12/23.
 */

public class MyData extends AppCompatActivity implements View.OnClickListener {

    private ImageTextButton btn_my_skill;
    private ImageTextButton btn_my_certificate;
    private TextView tv_phone_num;
    private TextView tv_name;
    private TextView tv_idcard;
    private String token;
    private CustomDialog customDialog;

    private Handler myDataHandler=new MyDataHandler(this);
    private TextView tv_number;
    private TextView tv_sex;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_mydata);
        token = (String) SharedPreferencesUtils.getData(getApplicationContext(), SPConstants.TOKEN, "");
        customDialog = new CustomDialog(this, "正在加载");
        initHeaderTitle();
        initView();
        initEvent();
        GetData();
    }

    private void GetData() {
        customDialog.show();
        //我的资料
        String validateURL = NetConstants.BASCI_INFO;
        Map<String, String> textParams = new HashMap<String, String>();
        textParams.put("token", token);
        SendRequestUtils.PostDataFromService(textParams,validateURL,myDataHandler);
    }

    private void initEvent() {
        btn_my_certificate.setOnClickListener(this);
        btn_my_skill.setOnClickListener(this);
    }

    private void initView() {
        btn_my_skill = (ImageTextButton) findViewById(R.id.btn_my_skill);
        btn_my_certificate = (ImageTextButton) findViewById(R.id.btn_my_certificate);
        tv_phone_num = (TextView) findViewById(R.id.tv_phone_num);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_idcard = (TextView) findViewById(R.id.tv_idcard);
        tv_number = (TextView) findViewById(R.id.tv_number);
        tv_sex = (TextView) findViewById(R.id.tv_sex);

    }

    private void initHeaderTitle() {
        findViewById(R.id.id_goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("我的资料");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_my_certificate:
                Intent cert=new Intent(MyData.this,MyCertificate.class);
                startActivity(cert);
                break;
            case R.id.btn_my_skill:
                Intent skill = new Intent(MyData.this, MySkill.class);
                startActivity(skill);
                break;
        }

    }
    private static class MyDataHandler extends BaseHandler<MyData> {

        MyDataHandler(MyData object) {
            super(object);
        }

        @Override
        public void onSuccess(MyData object, Message msg) {
            object.getDataSuccess(msg);
        }

        @Override
        public void onFinilly(MyData object) {
            super.onFinilly(object);
            object.customDialog.dismiss();
        }
    }

    private void getDataSuccess(Message msg) {
        //解析数据
        try{
            Gson gson = new Gson();
            BasicInfoModel model = gson.fromJson(msg.obj.toString(), BasicInfoModel.class);
            if(2==model.result_code){
                //重新登陆
                CommonMethod.goLogin(new WeakReference<Activity>(this),2);
            }else{
                if(model.result.equals("success")){
                    BasicInfoModel.BasicInfoDetail data = model.data;
                    tv_name.setText(data.name);
                    tv_idcard.setText(data.idCard);
                    tv_phone_num.setText(data.phone);
                    tv_number.setText(data.number);
                    tv_sex.setText(data.sex);
                }else{
                    //失败
                    CommonMethod.faifure(new WeakReference<Activity>(this),model.error);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
