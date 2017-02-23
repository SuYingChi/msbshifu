package com.msht.master.Model;

/**
 * Created by hei123 on 2016/12/7.
 * 新版本
 */

public class NewVersionModel {
    public String result;
    public String error;
    public VersionDetailModel data;

    public class VersionDetailModel{
        public int version;
        public String title;
        public String desc;
        public String url;
    }
}
