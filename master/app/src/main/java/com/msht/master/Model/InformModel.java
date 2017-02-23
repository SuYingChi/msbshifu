package com.msht.master.Model;

import java.util.ArrayList;

/**
 * Created by hei123 on 2016/12/8.
 * 通知
 */

public class InformModel extends BaseModel{

    public ArrayList<InformDetailModel> data;
    public class InformDetailModel{
        public int id;
        public String info;
        public String time;
        public int type;
        public String type_info;
    }
}
