package com.msht.master.Model;

import android.widget.BaseAdapter;
import android.widget.CalendarView;

import java.util.ArrayList;

/**
 * Created by hei on 2016/12/23.
 * 质保金
 */

public class QualityAssuranceModel extends BaseModel {
    public ArrayList<QualityAssuranceDetail> data;
    public class QualityAssuranceDetail{
        public int type;
        public String info;
        public double amount;
        public String time;
    }
}
