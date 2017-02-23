package com.msht.master.Model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by hei on 2016/12/26.
 * 我的资料
 */

public class MyDataModel extends BaseModel {
    public DataDetail data;
    public static class DataDetail{
        public String name;
        public String phone;
        public String idCard;
        public int valid;
        public ArrayList<String> categories;
        public ArrayList<Certificate> certificates;

    }
    public static class Certificate implements Serializable{
        public String name;
        public String effective_time;
    }
}
