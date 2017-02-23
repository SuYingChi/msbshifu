package com.msht.master.Model;

/**
 * Created by hei on 2017/1/9.
 * 创建订单
 */

public class CreateOrder extends BaseModel {
    public CreateOrderDetail data;
    public static class CreateOrderDetail{
        public String id;
        public String charge;
    }
}
