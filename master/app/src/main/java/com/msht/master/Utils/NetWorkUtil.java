package com.msht.master.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by hong on 2016/8/11.
 */
public class NetWorkUtil {
    public static boolean isWifiAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected() && networkInfo
                .getType() == ConnectivityManager.TYPE_WIFI);
    }
    /**
     * 获取MAC地址
     *
     * @param context
     * @return
     */
    public static String getMacAddress(Context context) {
        if (context == null) {
            return "";
        }

        String localMac = null;
        if (isWifiAvailable(context)) {
            localMac = getWifiMacAddress(context);
        }

        if (localMac != null && localMac.length() > 0) {
            localMac = localMac.replace(":", "-").toLowerCase();
            return localMac;
        }

        localMac = getMacFromCallCmd();
        if (localMac != null) {
            localMac = localMac.replace(":", "-").toLowerCase();
        }

        return localMac;
    }
    private static String getWifiMacAddress(Context context) {
        String localMac = null;
        try {
            WifiManager wifi = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            if (wifi.isWifiEnabled()) {
                localMac = info.getMacAddress();
                if (localMac != null) {
                    localMac = localMac.replace(":", "-").toLowerCase();
                    return localMac;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    private static String getMacFromCallCmd() {
        String result = "";
        result = callCmd("busybox ifconfig", "HWaddr");

        if (result == null || result.length() <= 0) {
            return null;
        }

      //  DebugLog.v("tag", "cmd result : " + result);

        // 对该行数据进行解析
        // 例如：eth0 Link encap:Ethernet HWaddr 00:16:E8:3E:DF:67
        if (result.length() > 0 && result.contains("HWaddr") == true) {
            String Mac = result.substring(result.indexOf("HWaddr") + 6,
                    result.length() - 1);
            if (Mac.length() > 1) {
                result = Mac.replaceAll(" ", "");
            }
        }

        return result;
    }
    public static String callCmd(String cmd, String filter) {
        String result = "";
        String line = "";
        try {
            Process proc = Runtime.getRuntime().exec(cmd);
            InputStreamReader is = new InputStreamReader(proc.getInputStream());
            BufferedReader br = new BufferedReader(is);

            // 执行命令cmd，只取结果中含有filter的这一行
            while ((line = br.readLine()) != null
                    && line.contains(filter) == false) {
            }

            result = line;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean IsNetWorkEnable(Context context) {
        boolean netState=false;
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo=connectivity.getActiveNetworkInfo();
            if (networkInfo != null) {
               if (networkInfo.isAvailable()&&networkInfo.isConnected()){
                   netState=true;
               }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return netState;
    }

}
