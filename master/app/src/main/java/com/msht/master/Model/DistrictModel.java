package com.msht.master.Model;

import java.util.ArrayList;

/**
 * Created by hei123 on 2016/11/30.
 * 地区
 */

public class DistrictModel extends BaseModel{

    public ArrayList<DistrictDetail> data;

    public class DistrictDetail{
        public int id;
        public String name;
        public int isSelected;
    }
}
