package com.msht.master.Model;

/**
 * Created by hong on 2018/2/11.
 */

public class WalletEnterpriseView extends BaseModel{
    public WalletEnterpriseDetaile data;
    public class WalletEnterpriseDetaile{
        public int order_num;//总订单量
        public double order_amount;//
        public double reward_amount;//好评奖励
    }
}
