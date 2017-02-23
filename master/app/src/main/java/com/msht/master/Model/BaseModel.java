package com.msht.master.Model;

import java.io.Serializable;

/**
 * Created by hei123 on 2016/12/12.
 * 所有实体类的基类
 */

public class BaseModel implements Serializable {
    public int result_code;
    public String result;
    public String error;
}
