package com.msht.master.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.msht.master.Adapter.OrderListViewpagerAdapter;
import com.msht.master.Adapter.RewordAndPunishAdapter;
import com.msht.master.Controls.ViewPagerIndicator;
import com.msht.master.R;

/**
 * Created by hei on 2016/12/26.
 */

public class OrderList extends Fragment {

    private ViewPager viewpager;
    private ViewPagerIndicator indicator;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_order_list, container, false);
        viewpager = (ViewPager) inflate.findViewById(R.id.viewpager);
        indicator = (ViewPagerIndicator) inflate.findViewById(R.id.indicator);
        viewpager.setAdapter(new OrderListViewpagerAdapter(getChildFragmentManager()));
        indicator.setViewPager(viewpager,0);
        return inflate;
    }
}
