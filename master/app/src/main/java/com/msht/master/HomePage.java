package com.msht.master;


import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;


import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.msht.master.Common.DownloadService;
import com.msht.master.Constants.NetConstants;
import com.msht.master.Constants.SPConstants;
import com.msht.master.FunctionView.MyInform;
import com.msht.master.FunctionView.SearchOrder;
import com.msht.master.Model.NewVersionModel;
import com.msht.master.UIView.PromptDialog;
import com.msht.master.Utils.AppToast;
import com.msht.master.Utils.SendRequestUtils;
import com.msht.master.Utils.SharedPreferencesUtils;
import com.msht.master.fragment.MyFragment;
import com.msht.master.fragment.HomeFragment;
import com.msht.master.fragment.OrderList;
import com.msht.master.fragment.OrderListFragment;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import java.io.File;
import java.lang.ref.WeakReference;

public class HomePage extends FragmentActivity implements View.OnClickListener {
    private Fragment HomeFrag, OrderFrag, myFrag,currentFragment;
    private ImageView mSearch;
    private ImageView mMessage;
    private TextView  navigation_tv;
    public static final String FOLDER_NAME = "Download";

    public static final String MY_ACTION = "ui";   //广播跳转意图
    private RadioGroup radiogroup_main;
    /**
     * 下载新版本的处理
     */
    private Handler downloadHandler =new NewVersionHandler(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        PushAgent.getInstance(getApplicationContext()).onAppStart();
        initView();
        if (savedInstanceState != null) {
            currentFragment = getSupportFragmentManager().getFragment(savedInstanceState, "MyFragment");
        }else{
            initHome();
            checkVersion();
        }
        initBroadcast();
    }


    private void initView() {

        radiogroup_main = (RadioGroup) findViewById(R.id.radiogroup_main);


        mMessage=(ImageView)findViewById(R.id.id_message);
        mSearch=(ImageView)findViewById(R.id.id_search);
        navigation_tv=(TextView)findViewById(R.id.tv_logo);
        mSearch.setOnClickListener(this);
        mMessage.setOnClickListener(this);


        radiogroup_main.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radio_home:
                        clickTab1Layout();
                        break;
                    case R.id.radio_order:
                        clickTab2Layout();
                        break;
                    case R.id.radio_me:
                        clickTab3Layout();
                        break;
                }
            }
        });


    }

    /**
     * 检查新版本
     */
    private void checkVersion() {
        SendRequestUtils.GetDataFromService(NetConstants.NEW_VERSION, "?&device=2", downloadHandler);
    }
    /**
     * 获取当前版本
     *
     * @return
     */
    private int getCurrentVersionCode() {
        try {
            PackageManager manager = getPackageManager();
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            return info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    /**
     * 下载新版本
     */
    private void downLoadNewVersion(String url) {
        Intent intent = new Intent(this,DownloadService.class);
        intent.putExtra("url", url);
        startService(intent);
/*
        File folder = new File(FOLDER_NAME);
        if (!folder.exists() || !folder.isDirectory()) {
            folder.mkdirs();
        }
        DownloadManager mDownloadManaget = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDestinationInExternalPublicDir(FOLDER_NAME, "mshtMaster.apk");
        request.setTitle("更新民生师傅");
        request.setDescription("正在为您更新民生师傅");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setMimeType("application/vnd.android.package-archive");
        long enqueue = mDownloadManaget.enqueue(request);
        //保存到sp 以便于在我的界面查看
        SharedPreferencesUtils.saveData(HomePage.this, SPConstants.DOWNLOAD_NEW_VERSION_ID, enqueue);*/
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_message:
                clickMessage();
                break;
            case R.id.id_search:
                clickSearch();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode== KeyEvent.KEYCODE_BACK&& event.getRepeatCount()==0){
            showTips();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void showTips() {
        new PromptDialog.Builder(this)
                .setTitle(R.string.dialog_title)
                .setViewStyle(PromptDialog.VIEW_STYLE_TITLEBAR_SKYBLUE)
                .setMessage("是否要退出民生师傅")
                .setButton1("取消", new PromptDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setButton2("确定", new PromptDialog.OnClickListener() {

                    @Override
                    public void onClick(Dialog dialog, int which) {
                        System.exit(0);
                    }
                })
                .show();
    }

    private void initHome() {

        if (HomeFrag == null) {
            HomeFrag = new HomeFragment();
        }

        if (!HomeFrag.isAdded()) {
            // 提交事务
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content_layout, HomeFrag).commit();
            // 记录当前Fragment
            currentFragment = HomeFrag;

        }
    }

    private void clickTab1Layout() {
        if (HomeFrag == null) {
            HomeFrag = new HomeFragment();
        }
        addOrShowFragment(getSupportFragmentManager().beginTransaction(), HomeFrag);
        navigation_tv.setText(R.string.homepage);//导航栏——民生宝
        mSearch.setVisibility(View.GONE);
        mMessage.setVisibility(View.VISIBLE);
    }
    private void clickTab2Layout() {
        if (OrderFrag == null) {
            OrderFrag = new OrderList();
        }
        addOrShowFragment(getSupportFragmentManager().beginTransaction(), OrderFrag);
        navigation_tv.setText(R.string.orderworke);//导航栏——民生宝
        mSearch.setVisibility(View.VISIBLE);
        mMessage.setVisibility(View.GONE);
    }

    private void clickTab3Layout() {
        if (myFrag == null) {
                myFrag = new MyFragment();
        }
        addOrShowFragment(getSupportFragmentManager().beginTransaction(), myFrag);
        navigation_tv.setText(R.string.my_personal);//导航栏——民生宝
        mSearch.setVisibility(View.GONE);
        mMessage.setVisibility(View.VISIBLE);
    }
    private void addOrShowFragment(FragmentTransaction fragmentTransaction, Fragment fragment) {
        if (currentFragment == fragment)
            return;

        if (!fragment.isAdded()) { // 如果当前fragment未被添加，则添加到Fragment管理器中
            fragmentTransaction.hide(currentFragment)
                    .add(R.id.content_layout, fragment).commit();
        } else {
            fragmentTransaction.hide(currentFragment).show(fragment).commit();
        }
        currentFragment = fragment;
    }
    private void clickMessage() {
        Intent Message=new Intent(this, MyInform.class);
        startActivity(Message);
    }
    private void clickSearch() {

        Intent intent=new Intent(this, SearchOrder.class);
        startActivity(intent);
    }
    /*接受广播*/
    private BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String info=intent.getExtras().getString("broadcast");
            if(info.equals("1")){
                finish();  //接受到广播后把上一级的Mainpage 消除
            }
        }
    };
    private void initBroadcast() {      //注册广播
        IntentFilter filter=new IntentFilter();
        filter.addAction(MY_ACTION);
        registerReceiver(broadcastReceiver, filter);
    }
    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (currentFragment != null) {
            getSupportFragmentManager().putFragment(outState,"MyFragment",currentFragment);
        }
        super.onSaveInstanceState(outState);
    }

    private static class NewVersionHandler extends Handler{
        private WeakReference<HomePage> ref;

        NewVersionHandler(HomePage activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final HomePage activity = ref.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            switch (msg.what){
                case SendRequestUtils.SUCCESS:
                    activity.getNewVersionSuccess(msg);
                    break;
                case SendRequestUtils.FAILURE:
                    AppToast.makeShortToast(activity,"网络连接失败");
                    break;
                case SendRequestUtils.ERRORCODE:
                    AppToast.makeShortToast(activity,"网络连接失败");
                    break;
            }
        }
    }

    private void getNewVersionSuccess(Message msg) {
        try {
            Gson gson = new Gson();
            NewVersionModel newVersionModel = gson.fromJson(msg.obj.toString(), NewVersionModel.class);
            if (newVersionModel.result.equals("success")) {
                NewVersionModel.VersionDetailModel data = newVersionModel.data;
                int versionCode = data.version;
                String title = data.title;
                String desc = data.desc;
                final String url = data.url;
                String desc2 = desc.replace("\\n", "\n");
                if (versionCode > getCurrentVersionCode()) {
                    SharedPreferencesUtils.saveData(HomePage.this, SPConstants.HAVE_NEW_VERSION, true);
                    //版本不一致 更新版本
                    new PromptDialog.Builder(HomePage.this)
                            .setTitle(title)
                            .setViewStyle(PromptDialog.VIEW_STYLE_TITLEBAR_SKYBLUE)
                            .setMessage(desc2)
                            .setButton1("取消", new PromptDialog.OnClickListener() {

                                @Override
                                public void onClick(Dialog dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setButton2("更新", new PromptDialog.OnClickListener() {

                                @Override
                                public void onClick(Dialog dialog, int which) {
                                    downLoadNewVersion(url);
                                    dialog.dismiss();
                                }
                            })
                            .show();

                } else {
                    //code相等
                    //保存为没有新版本
                    SharedPreferencesUtils.saveData(HomePage.this, SPConstants.HAVE_NEW_VERSION, false);
                }
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
