package com.msht.master.Model;

import java.util.ArrayList;

/**
 * Created by hong on 2017/4/19.
 */

public class PriceCategoryModel extends BaseModel{
    public ArrayList<PriceCategoryModel.PriceCategory> data;
    public class PriceCategory{
        public String id;
        public String name;
        public String s_index;
        public String code;
        public ArrayList<PriceCategoryModel.PriceCategory.ChildCategory> child;
        public class ChildCategory{
            public String id;
            public String price_web;
            public String name;
            public String s_index;
            public String code;
        }
    }
}
