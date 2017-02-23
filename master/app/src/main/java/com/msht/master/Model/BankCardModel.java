package com.msht.master.Model;

import java.util.ArrayList;

/**
 * Created by hei on 2016/12/26.
 * 银行卡
 */

public class BankCardModel extends BaseModel {
    public ArrayList<BankCardDetail> data;

    public static class BankCardDetail{
        public int id;
        public String bank;
        public String card;
    }
}
