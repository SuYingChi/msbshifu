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
        public String third_order_no;
        public String category;
        public String address;
        public String username;
        public String user_phone;
        public String time;
        public String status;
        public String statusDesc;
        public String memo;
        public String order_memo;
        public String appoint_time;
        public String repairman_cancel_reason;
        public ArrayList<String> img;
        public ArrayList<String> repairman_cancel_img;
        public double detect_fee;
        public double material_fee;
        public double serve_fee;
        public double discount_fee;
        public String replace_pay_status;
        public double replace_pay_amount;
        public String guarantee_day;
    }
}
