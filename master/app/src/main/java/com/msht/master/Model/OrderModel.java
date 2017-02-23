package com.msht.master.Model;

import java.util.ArrayList;

/**
 * Created by hei123 on 2016/12/6.
 * 订单
 */

public class OrderModel extends BaseModel{

    public OrderDataModel data;

    public static class OrderDataModel{
        public int total;
        public ArrayList<OrderDetailModel> orderList;

    }
    public static class OrderDetailModel{
        public String id;
        public String category;
        public String address;
        public String username;
        public String user_phone;
        public String time;
        public String status;
    }
}
