package com.msht.master.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.msht.master.HtmlWeb.CommissionRule;
import com.msht.master.fragment.RewordHistory;

/**
 * Created by hei on 2016/12/29.
 * 规则学习的viewpager适配器
 */

public class LearnRuleAdapter extends FragmentPagerAdapter {
    private String[] Titles={"奖励规则","服务流程"};
    public LearnRuleAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return CommissionRule.getInstance(position);
    }

    @Override
    public int getCount() {
        return Titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }
}
