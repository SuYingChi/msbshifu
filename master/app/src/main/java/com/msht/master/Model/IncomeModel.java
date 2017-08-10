package com.msht.master.Model;

import java.util.ArrayList;

/**
 * Created by hei123 on 2016/12/12.
 * 收入
 */

public class IncomeModel extends BaseModel{
    public MonthIncome data;
    public class MonthIncome{
        public double amount;
        public int total;//本月订单总数
        public ArrayList<IncomeDetail> list;
    }
    public static class IncomeDetail{
        public String order_no;
        public String time;
        public int order_type;//订单大类Id
        public String parent_type_name;
        public String type_name;
        public double real_amount;
        public double total_amount;
        public double plateform_fee;
        public double quality_assurance_fee;

    }

}
