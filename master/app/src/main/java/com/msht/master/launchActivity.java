package com.msht.master;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.msht.master.Common.CommonMethod;

import com.msht.master.Constants.SPConstants;
import com.msht.master.Utils.PermissionDog;
import com.msht.master.Utils.SharedPreferencesUtils;
import com.umeng.message.PushAgent;

import java.lang.ref.WeakReference;


public class launchActivity extends Activity {
    private final int SPLASH_DISPLAY_LENGHT = 1000;
    private final int REQUEST_PERMISSION_SETTING=0x001;

    private String token = null;
    String[] permissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CALL_PHONE};


    /**
     * 加载主界面
     */
    private void launch() {
        if (!TextUtils.isEmpty(launchActivity.this.token)) {
            Intent mainintent = new Intent(launchActivity.this, HomePage.class);
            launchActivity.this.startActivity(mainintent);
            launchActivity.this.finish();
        } else {
            CommonMethod.goLogin(new WeakReference<Activity>(this), 1);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        PushAgent.getInstance(getApplicationContext()).onAppStart();

        this.token = (String) SharedPreferencesUtils.getData(this, SPConstants.TOKEN, "");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPerssion();
                } else {
                    launch();
                }

            }
        }, SPLASH_DISPLAY_LENGHT);
    }

    /**
     * 自定义的请求权限
     */
    private void requestPerssion() {
        PermissionDog.getInstance().SetAction( new PermissionDog.PermissionDogAction() {
            @Override
            public void allallow() {
                super.allallow();
                launch();
            }

            @Override
            public void multi(String[] granteds, String[] denieds, String[] nevershows) {
                if (nevershows.length != 0) {
                    Log.e("tag", "" + nevershows.length);
                    new AlertDialog.Builder(launchActivity.this, R.style.DialogTheme).setTitle("民生师傅")
                            .setMessage("民生师傅需要读写空间和拨打电话的权限")
                            .setPositiveButton("前往设置", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                                }
                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(launchActivity.this,"未获取到必要的权限",Toast.LENGTH_SHORT);
                            finish();
                        }
                    }).create().show();
                } else {
                    if (denieds.length != 0) {
                        Log.e("tag", "" + denieds.length);
                        new AlertDialog.Builder(launchActivity.this, R.style.DialogTheme).setTitle("民生师傅")
                                .setMessage("民生师傅需要读写空间和拨打电话的权限")
                                .setPositiveButton("获取", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        PermissionDog.getInstance().requestMultiPermissions(launchActivity.this, permissionList);
                                    }
                                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(launchActivity.this,"未获取到必要的权限",Toast.LENGTH_SHORT);
                                finish();
                            }
                        }).create().show();
                    } else {
                        launch();
                    }
                }
            }
        });

        PermissionDog.getInstance().requestMultiPermissions(launchActivity.this,permissionList);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_PERMISSION_SETTING){
            requestPerssion();
        }
    }

    //重写权限申请的回调
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
            PermissionDog.getInstance().notifyPermissionsChanged(launchActivity.this, requestCode, permissions, grantResults);
    }
}
