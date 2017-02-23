package com.msht.master.Model;

/**
 * Created by hei on 2016/12/27.
 * 最大可提现金额
 */

public class WithDrawBiggestAmountModel extends BaseModel {
    public BiggestAmount data;
    public static class BiggestAmount{
        public String can_withdrawals;
    }
}
