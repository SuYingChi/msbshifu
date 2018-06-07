package com.msht.master.Model;

import java.util.Calendar;

/**
 * 师傅基本信息
 * Created by hei123 on 11/14/2016.
 */
public class BasicInfoModel extends BaseModel {
    public BasicInfoDetail data;
    public class BasicInfoDetail{
        public int valid;
        public String name;
        public String username;
        public String phone;
        public String idCard;
        public String number;
        public String company_code;
        public double balance;
        public String avatar;
        public String sex;
        public String city_id;
        public String city_name;
    }
}
