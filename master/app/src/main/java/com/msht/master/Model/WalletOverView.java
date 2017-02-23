package com.msht.master.Model;

/**
 * Created by hei123 on 2016/12/13.
 * 钱包收入总览
 */

public class WalletOverView extends BaseModel {
    public WalletOverViewDetaile data;
    public class WalletOverViewDetaile{
        public double total_amount;//总收入
        public double amount;//账户余额
        public double reward_amount;//奖励金额
        public double quality_assurance_amount;//质保金
    }
}
