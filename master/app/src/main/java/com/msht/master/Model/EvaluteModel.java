package com.msht.master.Model;

import java.util.ArrayList;

/**
 * Created by hei123 on 2016/12/4.
 * 评价
 */

public class EvaluteModel extends BaseModel{

    public ArrayList<EvaluteDetailModel> data;
    public static class EvaluteDetailModel{
        public String user_avatar;
        public String username;
        public int eval_score;
        public String eval_info;
        public String time;
    }
}
