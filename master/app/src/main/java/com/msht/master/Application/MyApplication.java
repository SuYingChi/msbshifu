package com.msht.master.Application;

import android.app.Application;
import android.app.Notification;

import android.content.Context;
import android.graphics.BitmapFactory;

import android.support.v7.app.NotificationCompat;
import android.util.Log;


import com.msht.master.R;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.entity.UMessage;


/**
 * Created by hei123 on 2016/11/22.
 */

public class MyApplication extends Application {

    private RefWatcher refWatcher;

    public static RefWatcher getRefWatcher(Context context) {
        MyApplication application = (MyApplication) context.getApplicationContext();
        return application.refWatcher;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //内存泄漏检测
        refWatcher = LeakCanary.install(this);

        //设置自定义统计
        MobclickAgent.openActivityDurationTrack(false);
        PushAgent mPushAgent = PushAgent.getInstance(this);

        UmengMessageHandler messageHandler = new UmengMessageHandler() {
            /**
             * 自定义消息的回调方法
             * */
//            @Override
//            public void dealWithCustomMessage(final Context context, final UMessage msg) {
//                new Handler().post(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        // TODO Auto-generated method stub
//                        // 对自定义消息的处理方式，点击或者忽略
//                        boolean isClickOrDismissed = true;
//                        if (isClickOrDismissed) {
//                            //自定义消息的点击统计
//                            UTrack.getInstance(getApplicationContext()).trackMsgClick(msg);
//                        } else {
//                            //自定义消息的忽略统计
//                            UTrack.getInstance(getApplicationContext()).trackMsgDismissed(msg);
//                        }
//                        Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
//                    }
//                });
//            }

            /**
             * 自定义通知栏样式的回调方法
             * */
            @Override
            public Notification getNotification(Context context, UMessage msg) {
                //实例化NotificationCompat.Builde并设置相关属性
                //自定义的通知展示
                NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(getApplicationContext())
                        //设置小图标
                        .setSmallIcon(R.drawable.notification_small)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                        //设置通知标题
                        .setContentTitle(msg.title)
                        .setColor(getResources().getColor(R.color.colorPrimary))
                        //设置通知内容
                        .setContentText(msg.text)
                        .setAutoCancel(true);
                return builder.build();
            }
        };
        mPushAgent.setMessageHandler(messageHandler);


        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回device token
                //Log.e("device", deviceToken);
            }

            @Override
            public void onFailure(String s, String s1) {
                //Log.e("device", "失败");
            }
        });
    }
}
