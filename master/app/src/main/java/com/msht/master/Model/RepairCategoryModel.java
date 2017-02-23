package com.msht.master.Model;

import android.icu.lang.UCharacter;

import java.nio.channels.ClosedSelectorException;
import java.util.ArrayList;

/**
 * Created by hei123 on 2016/11/30.
 * 技能列表
 */

public class RepairCategoryModel extends BaseModel{

    public ArrayList<MainCategory> data;

    public class MainCategory{
        public int id;
        public String name;
        public ArrayList<ChildCategory> child;

        public class ChildCategory{
            public int id;
            public int selected;
            public String name;
            public int valid;
        }
    }
}
