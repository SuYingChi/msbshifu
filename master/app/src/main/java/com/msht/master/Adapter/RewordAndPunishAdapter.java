package com.msht.master.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.msht.master.fragment.RewordHistory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hei on 2016/12/20.
 * 奖惩界面的适配器
 */

public class RewordAndPunishAdapter extends FragmentPagerAdapter{
    private String[] Titles={"奖励"};
    public RewordAndPunishAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return RewordHistory.getInstance(position);
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
