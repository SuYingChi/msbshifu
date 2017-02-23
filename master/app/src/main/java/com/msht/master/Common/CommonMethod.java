package com.msht.master.Common;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;

import com.msht.master.LoginActivity;
import com.msht.master.R;
import com.msht.master.UIView.PromptDialog;

import java.lang.ref.WeakReference;

/**
 * Created by hei123 on 11/14/2016.
 * 应用中用到的一些公有方法
 */
public class CommonMethod {
    /**
     * 转到登陆界面
     * @param reference 当前activity的弱引用
     */
    public static void goLogin(WeakReference<Activity> reference,int state){
        final Activity activity = reference.get();
        if (activity == null || activity.isFinishing()) {
            return;
        }
        Intent mainintent = new Intent(activity, LoginActivity.class);
        mainintent.putExtra("state",state);
        activity.startActivity(mainintent);
        activity.finish();
    }
    /**
     * 提示错误信息
     * @param reference
     * @param error
     */
    public static void faifure(WeakReference<Activity> reference,String error) {
        final Activity activity = reference.get();
        if (activity == null || activity.isFinishing()) {
            return;
        }
        new PromptDialog.Builder(activity)
                .setTitle(R.string.dialog_title)
                .setViewStyle(PromptDialog.VIEW_STYLE_TITLEBAR_SKYBLUE)
                .setMessage(error)
                .setButton1("确定", new PromptDialog.OnClickListener() {

                    @Override
                    public void onClick(Dialog dialog, int which) {
                        dialog.dismiss();

                    }
                }).show();
    }
}
