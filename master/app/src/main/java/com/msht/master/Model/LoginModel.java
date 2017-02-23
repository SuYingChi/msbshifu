package com.msht.master.Model;

/**
 * Created by hei on 2017/1/6.
 * 登录
 */

public class LoginModel extends BaseModel {
    public LoginDetail data;
    public static class LoginDetail{
        public int id;
        public String name;
        public String avatar;
        public int valid;
        public String token;
    }


}
