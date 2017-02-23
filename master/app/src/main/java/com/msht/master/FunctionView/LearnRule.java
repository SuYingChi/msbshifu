package com.msht.master.FunctionView;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.msht.master.Adapter.LearnRuleAdapter;
import com.msht.master.Controls.ViewPagerIndicator;
import com.msht.master.R;

/**
 * Created by hei on 2016/12/29.
 */

public class LearnRule extends AppCompatActivity {

    private ViewPager viewPager;
    private ViewPagerIndicator indicator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_rule);
        initView();
        initHeaderTitle();
    }

    private void initHeaderTitle() {
        findViewById(R.id.id_goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView)findViewById(R.id.tv_title)).setText("规则学习");
    }

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        indicator = (ViewPagerIndicator) findViewById(R.id.indicator);
        viewPager.setAdapter(new LearnRuleAdapter(getSupportFragmentManager()));
        indicator.setViewPager(viewPager,0);
    }
}
