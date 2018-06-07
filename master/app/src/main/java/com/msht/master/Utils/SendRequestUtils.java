package com.msht.master.Utils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by hei123 on 11/10/2016.
 */
public class SendRequestUtils {

    public final static int SUCCESS = 1;
    public final static int FAILURE = 0;
    public final static int ERRORCODE = 2;

    /**
     * 通过Get方式获取数据
     */
    public static void GetDataFromService(final String validateURL, final Handler mHandler) {
        new Thread() {
            @Override
            public void run() {

                HttpURLConnection connection = null;
                try {
                    URL url = new URL(validateURL);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    connection.setRequestProperty("accept", "*/*");
                    connection.setRequestProperty("connection", "Keep-Alive");
                    if (connection.getResponseCode() == 200) {
                        Log.d(this.toString(),//!=HttpURLConnection.HTTP_OK
                                "getResponseCode() not HttpURLConnection.HTTP_OK!我们");
                        InputStream is = connection.getInputStream();
                        String resultStr = NetUtil.readString(is);
                        Message message = new Message();
                        message.what = SUCCESS;
                        message.obj = resultStr;
                        mHandler.sendMessage(message);
                    } else {
                        Message msg = new Message();
                        msg.what = ERRORCODE;
                        mHandler.sendMessage(msg);
                    }
                } catch (Exception e)//内部捕获异常并做处理
                {
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.what = FAILURE;
                    mHandler.sendMessage(msg);
                    Log.d(this.toString(), e.getMessage() + "  127 line");
                } finally {
                    //最后，释放连接
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }.start();
    }

    /**
     * 通过Get方式获取数据  带参数
     */
    public static void GetDataFromService(final String validateURL, final String param, final Handler mHandler) {
        new Thread() {
            @Override
            public void run() {
                String fullurl = validateURL + param;
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(fullurl);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    connection.setRequestProperty("accept", "*/*");
                    connection.setRequestProperty("connection", "Keep-Alive");
                    if (connection.getResponseCode() == 200) {
                        Log.d(this.toString(),//!=HttpURLConnection.HTTP_OK
                                "getResponseCode() not HttpURLConnection.HTTP_OK!我们");
                        InputStream is = connection.getInputStream();
                        String resultStr = NetUtil.readString(is);
                        Message message = new Message();
                        message.what = SUCCESS;
                        message.obj = resultStr;
                        mHandler.sendMessage(message);
                    } else {
                        Message msg = new Message();
                        msg.what = ERRORCODE;
                        mHandler.sendMessage(msg);
                    }
                } catch (Exception e)//内部捕获异常并做处理
                {
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.what = FAILURE;
                    mHandler.sendMessage(msg);
                    Log.d(this.toString(), e.getMessage() + "  127 line");
                } finally {
                    //最后，释放连接
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }.start();
    }

    /**
     * 通过Post方式获取数据
     */
    public static void PostDataFromService(final Map<String, String> params, final String validateURL, final Handler mhandler) {

        new Thread() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                DataInputStream dis = null;
                Map<String, String> textParams = params;
                try {
                    URL url = new URL(validateURL);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setDoInput(true); // 发送POST请求必须设置允许输入
                    conn.setDoOutput(true); // 发送POST请求必须设置允许输出
                    conn.setUseCaches(false);//新加
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Charset", "UTF-8");//设置编码
                    conn.setRequestProperty("User-Agent", "Fiddler");
                    conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + NetUtil.BOUNDARY);
                    OutputStream os = conn.getOutputStream();
                    DataOutputStream ds = new DataOutputStream(os);
                    NetUtil.writeStringParams(textParams, ds);
                    NetUtil.paramsEnd(ds);
                    os.flush();
                    os.close();
                    conn.connect();
                    int code = conn.getResponseCode(); // 从Internet获取网页,发送请求,将网页以流的形式读回来
                    if (code == 200) {
                        Log.d(this.toString(),//!=HttpURLConnection.HTTP_OK
                                "getResponseCode() not HttpURLConnection.HTTP_OK");
                        InputStream is = conn.getInputStream();
                        String result = StreamTools.readInputStream(is);
                        Message msg = new Message();
                        msg.obj = result;
                        msg.what = SUCCESS;
                        mhandler.sendMessage(msg);

                    } else {
                        Message msg = new Message();
                        msg.what = ERRORCODE;
                        mhandler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.what = FAILURE;
                    mhandler.sendMessage(msg);
                    Log.d(this.toString(), e.getMessage() + "  127 line");
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            }
        }.start();
    }
    public static void PostFileToServer(final Map<String, String> textparams, final Map<String, File> fileparams, final String validateURL, final Handler mhandler) {
        new Thread(){
            @Override
            public void run() {
                HttpURLConnection conn = null;
                DataInputStream dis = null;
                try {
                    URL url = new URL(validateURL);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(10000);
                    conn.setDoInput(true); // 发送POST请求必须设置允许输入
                    conn.setDoOutput(true); // 发送POST请求必须设置允许输出
                    conn.setUseCaches(false);//新加
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Charset", "UTF-8");//设置编码
                    conn.setRequestProperty("ser-Agent", "Fiddler");
                    conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + NetUtil.BOUNDARY);
                    OutputStream os = conn.getOutputStream();
                    DataOutputStream ds = new DataOutputStream(os);
                    NetUtil.writeStringParams(textparams, ds);
                    NetUtil.writeFileParams(fileparams, ds);
                    NetUtil.paramsEnd(ds);
                    os.flush();
                    os.close();
                    conn.connect();
                    int code = conn.getResponseCode(); // 从Internet获取网页,发送请求,将网页以流的形式读回来
                    if (code == 200) {
                        InputStream is = conn.getInputStream();
                        String result = StreamTools.readInputStream(is);
                        Message msg = new Message();
                        msg.obj = result;
                        msg.what = SUCCESS;
                        mhandler.sendMessage(msg);

                    } else {
                        Message msg = new Message();
                        msg.what = ERRORCODE;
                        mhandler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.what = FAILURE;
                    mhandler.sendMessage(msg);
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            }
        }.start();

    }
}
