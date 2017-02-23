package com.msht.master.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.msht.master.fragment.OrderList;
import com.msht.master.fragment.OrderListFragment;

/**
 * Created by hei on 2016/12/26.
 * 我的订单界面的viewpager适配器
 */

public class OrderListViewpagerAdapter extends FragmentPagerAdapter {
    public String[] Titles={"全部","待处理","已完成"};

    public OrderListViewpagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return OrderListFragment.getInstanse(position);
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
