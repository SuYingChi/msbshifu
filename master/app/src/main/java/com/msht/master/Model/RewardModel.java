package com.msht.master.Model;


import java.util.ArrayList;

/**
 * Created by hei on 2016/12/20.
 * 奖励
 */

public class RewardModel extends BaseModel {
    public ArrayList<RewardDetail> data;
    public class RewardDetail {
        public String info;
        public double amount;
        public String time;
    }
}
