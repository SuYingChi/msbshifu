package com.msht.master.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by hong on 2016/4/14.
 */
public class StreamTools {
    public static String readInputStream(InputStream is) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            // 3.开始读文件
            int len = -1;
            while ((len = is.read(buffer)) != -1) {
                // 将Buffer中的数据写到outputStream对象中
                outputStream.write(buffer, 0, len);


            }
            is.close();
            outputStream.close();
            byte[] result=outputStream.toByteArray();
            String temp=new String(result);
            return temp;
        } catch (IOException e) {
            e.printStackTrace();
            return "获取失败";
        }
    }
}
