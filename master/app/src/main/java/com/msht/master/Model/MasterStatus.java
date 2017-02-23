package com.msht.master.Model;

/**
 * Created by hei123 on 2016/12/6.
 * 师傅状态
 */

public class MasterStatus extends BaseModel{

    public MasterDetailModel data;
    public class MasterDetailModel{
        public int valid;
        public String vaid_fail_reason;
        public int status;
        public String freeze_info;
        public int work_status;
    }
}
