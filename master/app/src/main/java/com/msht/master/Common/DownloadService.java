package com.msht.master.Common;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;


import com.msht.master.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadService extends Service {
    public static final String DOWNLOAD_PATH =
            Environment.getExternalStorageDirectory().getAbsolutePath()+
                    "/Msdownloads/";
    public static final String TAG = "download";
    private String url;//下载链接
    private int length;//文件长度
    private String fileName=null;//文件名
    private Notification notification;
    private RemoteViews contentView;
    private NotificationManager notificationManager;
    private static final int SC_OK=200;
    private static final int MSG_INIT = 0;
    private static final int URL_ERROR = 1;
    private static final int NET_ERROR = 2;
    private static final int DOWNLOAD_SUCCESS = 3;
    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_INIT:
                    length = (int) msg.obj;
                    new DownloadThread(url,length).start();
                    createNotification();
                    break;
                case DOWNLOAD_SUCCESS:
                    //下载完成
                    notifyNotification(100, 100);
                    installApk(DownloadService.this,new File(DOWNLOAD_PATH,fileName));
                    Toast.makeText(DownloadService.this, "下载完成", Toast.LENGTH_SHORT).show();
                    break;
                case URL_ERROR:
                    Toast.makeText(DownloadService.this, "下载地址错误",Toast.LENGTH_SHORT).show();
                    break;
                case NET_ERROR:
                    Toast.makeText(DownloadService.this, "连接失败，请检查网络设置",Toast.LENGTH_SHORT).show();
            }
        };
    };
    public DownloadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null){
            url = intent.getStringExtra("url");
            if(url != null && !TextUtils.isEmpty(url)){
                new InitThread(url).start();
            }else{
                mHandler.sendEmptyMessage(URL_ERROR);
            }

        }
        return super.onStartCommand(intent, flags, startId);
    }

    private class InitThread extends Thread{
        String url = "";
        public InitThread(String url) {
            this.url = url;
        }
        public void run() {
            HttpURLConnection conn= null;
            RandomAccessFile raf = null;
            try {
                //连接网络文件
                URL url = new URL(this.url);
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(6000);
                conn.setRequestMethod("GET");
                int length = -1;
                if(conn.getResponseCode() ==SC_OK){  //200
                    //获得文件长度
                    length = conn.getContentLength();
                }
                if(length <= 0){
                    return;
                }
                File dir = new File(DOWNLOAD_PATH);
                if(!dir.exists()){
                    dir.mkdir();
                }
                fileName = this.url.substring(this.url.lastIndexOf("/")+1, this.url.length());
                if(fileName==null && TextUtils.isEmpty(fileName) && !fileName.contains(".apk")){
                    fileName = getPackageName()+".apk";
                }
                File file = new File(dir, fileName);
                raf = new RandomAccessFile(file, "rwd");
                //设置文件长度
                raf.setLength(length);
                mHandler.obtainMessage(MSG_INIT,length).sendToTarget();
            } catch (Exception e) {
                mHandler.sendEmptyMessage(URL_ERROR);
                e.printStackTrace();
            } finally{
                try {
                    conn.disconnect();
                    raf.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    class DownloadThread extends Thread{
        String url;
        int length;
        public DownloadThread(String url, int length) {
            this.url = url;
            this.length = length;
        }
        @Override
        public void run() {
            HttpURLConnection conn = null;
            RandomAccessFile raf = null;
            InputStream input = null;
            try {

                URL url = new URL(this.url);
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(6000);
                conn.setRequestMethod("GET");
                //设置下载位置
                int start =0;
               // conn.setRequestProperty("Range", "bytes="+0+"-"+length);
                //设置文件写入位置
                File file = new File(DownloadService.DOWNLOAD_PATH,fileName);
                raf = new RandomAccessFile(file, "rwd");
                raf.seek(start);
                long mFinished = 0;
                //开始下载
                if(conn.getResponseCode() ==SC_OK||conn.getResponseCode()==206){ //这里判断  SC_OK=200,实为206
                    input = conn.getInputStream();
                    byte[] buffer = new byte[1024*4];
                    int len = -1;
                    long speed = 0;
                    long time = System.currentTimeMillis();
                    while((len = input.read(buffer)) != -1){
                        //写入文件
                        raf.write(buffer,0,len);
                        //把下载进度发送广播给Activity
                        mFinished += len;
                        speed += len;
                        if(System.currentTimeMillis() - time > 1000){
                            time = System.currentTimeMillis();
                            notifyNotification(mFinished,length);
                            Log.i(TAG, "mFinished=="+mFinished);
                            Log.i(TAG, "length=="+length);
                            Log.i(TAG, "speed=="+speed);
                            speed = 0;
                        }
                    }
                    mHandler.sendEmptyMessage(DOWNLOAD_SUCCESS);
                }else{   //conn.getResponseCode()!=200,
                    mHandler.sendEmptyMessage(NET_ERROR);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally{
                try {
                    if(conn != null){
                        conn.disconnect();
                    }
                    if(raf != null){
                        raf.close();
                    }
                    if(input != null ){
                        input.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @SuppressWarnings("deprecation")
    public void createNotification() {
        notification = new Notification(
                R.mipmap.ic_launcher,//应用的图标
                "安装包正在下载...",
                System.currentTimeMillis());
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        //notification.flags = Notification.FLAG_AUTO_CANCEL;

        /*** 自定义  Notification 的显示****/
        contentView = new RemoteViews(getPackageName(), R.layout.item_notification);
        contentView.setProgressBar(R.id.progress, 100, 0, false);
        contentView.setTextViewText(R.id.tv_progress, "0%");
        contentView.setTextViewText(R.id.id_tv_download,"安装包正在下载...");
        notification.contentView = contentView;
        /*updateIntent = new Intent(this, AboutActivity.class);
      	updateIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
     	updateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
     	pendingIntent = PendingIntent.getActivity(this, 0, updateIntent, 0);
      	notification.contentIntent = pendingIntent;*/
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //设置notification的PendingIntent
		/*Intent intt = new Intent(this, MainActivity.class);
		PendingIntent pi = PendingIntent.getActivity(this,100, intt,Intent.FLAG_ACTIVITY_NEW_TASK	| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		notification.contentIntent = pi;*/

        notificationManager.notify(R.layout.item_notification, notification);
    }
    private void notifyNotification(long percent,long length){

        contentView.setTextViewText(R.id.tv_progress, (percent*100/length)+"%");
        contentView.setProgressBar(R.id.progress, (int)length,(int)percent, false);
        contentView.setTextViewText(R.id.id_tv_download,"安装包下载完成");
        notification.contentView = contentView;
        notificationManager.notify(R.layout.item_notification, notification);
    }
    /**
     * 安装apk
     *
     * @param context 上下文
     * @param file    APK文件
     */
    public static void installApk(Context context, File file) {
       // SharedPreferencesUtil.Clear(context,"open_app");//清除原有数据
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            Uri apkUri= FileProvider.getUriForFile(context,"com.msht.master.fileProvider",file);
            Intent install=new Intent();
            install.setAction(Intent.ACTION_VIEW);
            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            install.setDataAndType(apkUri,"application/vnd.android.package-archive");
            context.startActivity(install);
        }else {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file),
                    "application/vnd.android.package-archive");
            context.startActivity(intent);
        }
    }
}
