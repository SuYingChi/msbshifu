package com.msht.master.Model;

import java.util.ArrayList;

/**
 * Created by hong on 2018/2/8.
 */

public class EnterpriseModel extends BaseModel{
    public ArrayList<EnterpriseDetail> data;
    public class EnterpriseDetail{
        public long ep_id;
        public String company_name;
        public String company_code;
    }
}
