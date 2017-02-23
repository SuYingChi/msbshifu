package com.msht.master.Model;

/**
 * Created by hei on 2016/12/27.
 * 提现
 */

public class ApplyWithDrawModel extends BaseModel {
    public ApplyWithDrawDetail data;
    public static class ApplyWithDrawDetail{
        public String apply_time;
        public String expect_time;
    }
}
