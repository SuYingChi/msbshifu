package com.msht.master.myview;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.msht.master.Controls.ImageTextButton;
import com.msht.master.FunctionView.WorkOrderDetail;
import com.msht.master.HtmlWeb.CommissionRule;
import com.msht.master.HtmlWeb.Joinus;
import com.msht.master.LoginActivity;
import com.msht.master.R;
import com.msht.master.Utils.SharedPreferencesUtils;


public class AboutMine extends AppCompatActivity implements View.OnClickListener {
    private Button btn_exit;
    private RelativeLayout Rfeedback;

    public static final String MY_ACTION = "ui";   //广播跳转意图
    private TextView tv_version;
    private ImageTextButton itb_join_us;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_mine);
        initHeaderTitle();
        initView();
        initEvent();
        tv_version.setText("民生师傅v"+getCurrentVersionName());

    }

    private void initHeaderTitle() {
        ((TextView)findViewById(R.id.tv_title)).setText("关于我们");
        findViewById(R.id.id_goback).setOnClickListener(this);
    }
    private String getCurrentVersionName() {
        try {
            PackageManager manager = getPackageManager();
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void initEvent() {

        btn_exit.setOnClickListener(this);
        Rfeedback.setOnClickListener(this);
        itb_join_us.setOnClickListener(this);
    }

    private void initView() {

        btn_exit=(Button)findViewById(R.id.id_exit);
        Rfeedback=(RelativeLayout)findViewById(R.id.id_idea_layout);
        tv_version = (TextView) findViewById(R.id.tv_version);
        itb_join_us = (ImageTextButton) findViewById(R.id.itb_join_us);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.id_goback:
                finish();
                break;
            case R.id.id_exit:
                Exitlogin();
                break;
            case R.id.id_idea_layout:
                feedback();
                break;
            case R.id.itb_join_us:
                Intent intent = new Intent(AboutMine.this, Joinus.class);
                startActivity(intent);
                break;
            default:
                break;

        }
    }
    private void Exitlogin() {
        SharedPreferencesUtils.clearData(getApplicationContext());
        Intent broadcast=new Intent();
        broadcast.setAction(MY_ACTION);
        broadcast.putExtra("broadcast", "1");
        sendBroadcast(broadcast);
        finish();
        Intent mainintent = new Intent(this, LoginActivity.class);
        startActivity(mainintent);

    }

    private void feedback() {
        Intent idea=new Intent(this, FeedBack.class);
        startActivity(idea);
    }

}
