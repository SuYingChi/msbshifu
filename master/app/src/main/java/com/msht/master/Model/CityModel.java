package com.msht.master.Model;

import java.util.ArrayList;

/**
 * Created by hong on 2017/4/6.
 */

public class CityModel extends BaseModel{
    public ArrayList<CityDetail> data;

    public class CityDetail{
        public String id;
        public String name;
        public int flag;
    }
}
