package com.msht.master.Model;

import java.util.ArrayList;

/**
 * Created by hei on 2016/12/16.
 * 提现记录
 */

public class WithDrawRecordModel extends BaseModel {
    public ArrayList<WithDrawRecodDetail> data;
    public class WithDrawRecodDetail{
        public double amount;
        public int status;
        public String bankcard;
        public String time;
    }
}
