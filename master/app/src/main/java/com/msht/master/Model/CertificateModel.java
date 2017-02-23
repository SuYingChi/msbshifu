package com.msht.master.Model;

import java.util.ArrayList;

/**
 * Created by hei on 2017/1/16.
 * 证书
 */

public class CertificateModel extends BaseModel {
    public ArrayList<CertificateDetail> data;
    public static class CertificateDetail{
        public int id;
        public String name;
        public String number;
        public String img;
        public String effective_time;
    }
}
