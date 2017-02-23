package com.msht.master.FunctionView;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.msht.master.Adapter.RewordAndPunishAdapter;
import com.msht.master.R;
import com.msht.master.fragment.RewordHistory;

import java.util.ArrayList;

/**
 * Created by hei on 2016/12/19.
 */

public class RewordAndPunishHistory extends AppCompatActivity {

    private ViewPager viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewordandpunishhistory);
        initHeaderTitle();
        initView();
    }

    private void initHeaderTitle() {
        findViewById(R.id.id_goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView)findViewById(R.id.tv_title)).setText("奖励");
    }

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new RewordAndPunishAdapter(getSupportFragmentManager()));
    }
}
