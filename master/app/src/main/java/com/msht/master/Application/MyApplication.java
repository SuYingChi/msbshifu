package com.msht.master.Application;

import android.app.Application;
import android.app.Notification;

import android.content.Context;
import android.graphics.BitmapFactory;

import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;


import com.msht.master.R;

import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;


/**
 * Created by hei123 on 2016/11/22.
 */

public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE,"b1039518e98591859dace4479bbf04ee");
        MobclickAgent.openActivityDurationTrack(false);
        PushAgent mPushAgent = PushAgent.getInstance(this);
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
               // Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
            }
        };
        UmengMessageHandler messageHandler = new UmengMessageHandler() {

            @Override
            public Notification getNotification(Context context, UMessage msg) {
                switch (msg.builder_id) {
                    case 1:
                        Notification.Builder builder = new Notification.Builder(context);
                        RemoteViews myNotificationView = new RemoteViews(context.getPackageName(),
                                R.layout.notification_view);
                        myNotificationView.setTextViewText(R.id.notification_title, msg.title);
                        myNotificationView.setTextViewText(R.id.notification_text, msg.text);
                        myNotificationView.setImageViewBitmap(R.id.notification_large_icon,
                                getLargeIcon(context, msg));
                        myNotificationView.setImageViewResource(R.id.notification_small_icon,
                                getSmallIconId(context, msg));
                        builder.setContent(myNotificationView)
                                .setSmallIcon(getSmallIconId(context, msg))
                                .setTicker(msg.ticker)
                                .setAutoCancel(true);

                        return builder.getNotification();
                    default:
                        //默认为0，若填写的builder_id并不存在，也使用默认。
                        return super.getNotification(context, msg);
                }
            }
        };

        mPushAgent.setMessageHandler(messageHandler);
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String s) { }
            @Override
            public void onFailure(String s, String s1) {}
        });
    }
}
