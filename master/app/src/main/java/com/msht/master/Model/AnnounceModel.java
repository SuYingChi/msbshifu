package com.msht.master.Model;

import java.util.ArrayList;

/**
 * Created by hei on 2017/1/13.
 * 公告数据实体类
 */

public class AnnounceModel extends BaseModel {
    public ArrayList<AnnounceDetail> data;
    public static class AnnounceDetail{
        public String content;
        public String time;
    }
}
