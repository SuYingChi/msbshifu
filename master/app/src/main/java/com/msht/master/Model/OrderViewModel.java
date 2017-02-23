package com.msht.master.Model;

import java.util.ArrayList;

/**
 * Created by hei on 2016/12/20.
 * 订单详情
 */

public class OrderViewModel  extends BaseModel{
    public OrderViewDetail data;
    public class OrderViewDetail{
        public String id;
        public String category;
        public String address;
        public String username;
        public String user_phone;
        public String time;
        public String status;
        public String memo;
        public String appoint_time;
        public ArrayList<String> img;
        public double detect_fee;
        public double material_fee;
        public double serve_fee;
        public double discount_fee;
        public String replace_pay_status;
        public double replace_pay_amount;
    }
}
