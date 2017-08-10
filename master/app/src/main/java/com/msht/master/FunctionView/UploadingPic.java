package com.msht.master.FunctionView;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.msht.master.Adapter.PhotoAdapter;
import com.msht.master.Adapter.PhotoPickerAdapter;
import android.widget.AdapterView.OnItemClickListener;

import com.msht.master.Base.BaseHandler;
import com.msht.master.Common.CommonMethod;
import com.msht.master.Constants.NetConstants;
import com.msht.master.Constants.SPConstants;
import com.msht.master.Controls.FullyGridLayoutManager;
import com.msht.master.Model.BaseModel;
import com.msht.master.Model.OrderViewModel;
import com.msht.master.R;
import com.msht.master.UIView.CustomDialog;
import com.msht.master.UIView.PromptDialog;
import com.msht.master.Utils.AppToast;
import com.msht.master.Utils.PermissionDog;
import com.msht.master.Utils.SendRequestUtils;
import com.msht.master.Utils.SharedPreferencesUtils;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.iwf.photopicker.PhotoPicker;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class UploadingPic extends AppCompatActivity {
    private TextView tv_order_num;//订单编号
    private TextView tv_order_status;//订单状态
    private TextView tv_second_type;//维修小类
    private TextView tv_username;//用户的姓名
    private TextView tv_phone_num;//用户的电话号码
    //  private ImageView iv_callphone;//拨打电话的按钮
    private TextView tv_address;//用户的地址
    private TextView tv_appoint_time;//用户预约时间
    private TextView tv_problem_desc;//问题描述
    private TextView tv_order_memo;   //备注信息
    private Button   btn_picture;
    private GridView photogridview;
    private CustomDialog customDialog;
    private String id;
    private String token;
    private String type="2";
    private int thisposition=-1;
    private int k=0;
    private boolean operation=false;
    private static  final int MY_PERMISSIONS_REQUEST=1;
    private ArrayList<String> imgPaths = new ArrayList<>();
    private OrderViewModel.OrderViewDetail data;
    private RecyclerView recycler_photo;//展示图片的
    private PhotoAdapter photoAdapter;
    private PhotoPickerAdapter mAdapter;
    private View layout_call_phone;//拨打电话所在的layout
    private View layout_repair_info;
    private View layout_order_memo;
    private View layout_button;
    private View layout_all;
    private Context mContext;

    private Handler getOrderDetailHandler = new GetOrderDetailHanlder(this);
    private Handler startseverHandler = new StartServerHandler(this);
    private Handler uploadHandler = new UploadImageHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploading_pic);
        mContext=this;
        Intent data = getIntent();
        id = data.getStringExtra("id");
        token = (String) SharedPreferencesUtils.getData(getApplicationContext(), SPConstants.TOKEN, "");
        customDialog = new CustomDialog(this, "正在加载...");
        initHeaderTitle();
        initView();
        GetWorkDetail();
        initExecute();
        initEvent();
    }
    private void initHeaderTitle() {
        findViewById(R.id.id_goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("拍照上传");
        TextView tv_rightText=(TextView)findViewById(R.id.id_right_text);
        tv_rightText.setVisibility(View.VISIBLE);
        tv_rightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operation=true;
                type="4";
                SendPicture();
            }
        });
    }
    private void initView() {
        layout_all=findViewById(R.id.id_layout_all);
        layout_all.setVisibility(View.GONE);   //用于数据获取正常情况显示
        layout_repair_info=findViewById(R.id.id_layout_repair);
        layout_order_memo=layout_repair_info.findViewById(R.id.id_layout_order_mome);

        tv_order_num = (TextView) findViewById(R.id.id_orderNo);
        tv_order_status = (TextView) findViewById(R.id.id_tv_status);
        tv_second_type = (TextView) findViewById(R.id.tv_order_type);
        tv_username = (TextView) findViewById(R.id.id_userman);
        tv_phone_num = (TextView) findViewById(R.id.id_phoneNo);
        layout_call_phone = findViewById(R.id.layout_call_phone);
        tv_address = (TextView) findViewById(R.id.id_address);
        tv_appoint_time = (TextView) findViewById(R.id.id_appoint_time);
        tv_problem_desc = (TextView)layout_repair_info. findViewById(R.id.id_tv_problem);
        tv_order_memo=(TextView)findViewById(R.id.id_info);
        layout_button=findViewById(R.id.id_layout_button);
        btn_picture=(Button)layout_button.findViewById(R.id.btn_picture);
        btn_picture.setVisibility(View.VISIBLE);

        photogridview=(GridView)findViewById(R.id.noScrollgridview);
        recycler_photo = (RecyclerView) findViewById(R.id.id_recycler_photo);
        FullyGridLayoutManager manager = new FullyGridLayoutManager(this, 4);
        manager.setOrientation(GridLayoutManager.VERTICAL);
        manager.setSmoothScrollbarEnabled(true);
        recycler_photo.setLayoutManager(manager);
        photoAdapter = new PhotoAdapter(this);
        recycler_photo.setAdapter(photoAdapter);
    }
    private void GetWorkDetail() {
        customDialog.show();
        String validateURL = NetConstants.REPAIR_ORDER_VIEW;
        Map<String, String> textParams = new HashMap<String, String>();
        textParams.put("token", token);
        textParams.put("id", id);
        SendRequestUtils.PostDataFromService(textParams, validateURL, getOrderDetailHandler);
    }

    private class GetOrderDetailHanlder extends Handler {
        private WeakReference<UploadingPic> ref;

        GetOrderDetailHanlder(UploadingPic activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final UploadingPic activity = ref.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            switch (msg.what) {
                case SendRequestUtils.SUCCESS:
                    layout_all.setVisibility(View.VISIBLE);
                    activity.getDetailSuccess(msg);
                    break;
                case SendRequestUtils.FAILURE:
                    AppToast.makeShortToast(activity, "网络连接失败");
                    break;
                case SendRequestUtils.ERRORCODE:
                    AppToast.makeShortToast(activity, "网络连接失败");
                    break;
            }
            customDialog.dismiss();
        }
    }
    private void getDetailSuccess(Message msg) {
        customDialog.dismiss();
        try {
            Gson gson = new Gson();
            OrderViewModel orderViewModel = gson.fromJson(msg.obj.toString(), OrderViewModel.class);
            if (2 == orderViewModel.result_code) {
                //重新登陆
                CommonMethod.goLogin(new WeakReference<Activity>(UploadingPic.this), 2);
            } else {
                if (orderViewModel.result.equals("success")) {
                    analyseData(orderViewModel);
                } else {
                    //失败
                    CommonMethod.faifure(new WeakReference<Activity>(UploadingPic.this), orderViewModel.error);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void analyseData(OrderViewModel orderViewModel) {
        data = orderViewModel.data;
        tv_order_num.setText(data.id);
        tv_second_type.setText(data.category);
        tv_username.setText(data.username);
        tv_phone_num.setText(data.user_phone);
        tv_address.setText(data.address);
        if (data.appoint_time.equals("")||data.appoint_time==null){
            tv_appoint_time.setText("尽快上门服务");
        }else {
            tv_appoint_time.setText(data.appoint_time);
        }
        if (data.memo.equals("")||data.memo==null){
            tv_problem_desc.setText("无");
        }else {
            tv_problem_desc.setText(data.memo);
        }
        if (data.order_memo.equals("")||data.order_memo==null){
            layout_order_memo.setVisibility(View.GONE);
        }else {
            layout_order_memo.setVisibility(View.VISIBLE);
            tv_order_memo.setText(data.order_memo);
        }
        tv_order_status.setText(data.statusDesc);
        photoAdapter.SetImageUrlList(data.img);
        photoAdapter.notifyDataSetChanged();
    }
    private void initExecute() {
        mAdapter = new PhotoPickerAdapter(imgPaths);
        photogridview.setAdapter(mAdapter);
        photogridview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (Build.VERSION.SDK_INT >= 23) {
                    thisposition=position;
                    initphoto(position);
                } else {
                    if (position == imgPaths.size()) {
                        PhotoPicker.builder()
                                .setPhotoCount(4)
                                .setShowCamera(true)
                                .setSelected(imgPaths)
                                .setShowGif(true)
                                .setPreviewEnabled(true)
                                .start(UploadingPic.this, PhotoPicker.REQUEST_CODE);
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putStringArrayList("imgPaths", imgPaths);
                        bundle.putInt("position", position);
                        Intent intent = new Intent(UploadingPic.this, EnlargePicActivity.class);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, position);
                    }
                }
            }
        });
    }
    private void initphoto(int position) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED||ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED) {
            AndPermission.with(this)
                    .requestCode(MY_PERMISSIONS_REQUEST)
                    .permission(Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .send();
        }else {
            if (position == imgPaths.size()) {
                PhotoPicker.builder()
                        .setPhotoCount(4)
                        .setShowCamera(true)
                        .setSelected(imgPaths)
                        .setShowGif(true)
                        .setPreviewEnabled(true)
                        .start(UploadingPic.this, PhotoPicker.REQUEST_CODE);
            } else {
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("imgPaths",imgPaths);
                bundle.putInt("position",position);
                Intent intent=new Intent(UploadingPic.this, EnlargePicActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent,position);
            }
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        AndPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults, listener);
    }
    private PermissionListener listener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode) {
            if(requestCode==MY_PERMISSIONS_REQUEST) {
                showphoto();
            }
        }
        @Override
        public void onFailed(int requestCode) {
            if(requestCode==MY_PERMISSIONS_REQUEST) {
                Toast.makeText(UploadingPic.this,"授权失败",Toast.LENGTH_SHORT).show();
            }
        }
    };
    private void showphoto() {
        if (thisposition== imgPaths.size()) {
            PhotoPicker.builder()
                    .setPhotoCount(4)
                    .setShowCamera(true)
                    .setSelected(imgPaths)
                    .setShowGif(true)
                    .setPreviewEnabled(true)
                    .start(UploadingPic.this, PhotoPicker.REQUEST_CODE);
        } else {
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("imgPaths",imgPaths);
            bundle.putInt("position",thisposition);
            Intent intent=new Intent(UploadingPic.this, EnlargePicActivity.class);
            intent.putExtras(bundle);
            startActivityForResult(intent,thisposition);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                imgPaths.clear();
                ArrayList<String> photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                imgPaths.addAll(photos);
                mAdapter.notifyDataSetChanged();
            }
        }
        if (resultCode == RESULT_OK && requestCode >= 0 && requestCode <= 8) {
            imgPaths.remove(requestCode);
            mAdapter.notifyDataSetChanged();
        }
        if (requestCode==0x003&&resultCode==0x003){
            setResult(2);    //回调结果码
            finish();
        }
    }
    private void initEvent() {
        btn_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operation=false;
                type="2";
                SendPicture();
            }
        });
        layout_call_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hotline();
            }
        });
    }

    private void SendPicture() {
        if (imgPaths.size()!=0){
            customDialog.show();
            btn_picture.setEnabled(false);
            for(int i=0;i<imgPaths.size();i++){
                File files=new File(imgPaths.get(i));
                compressImg(files);
            }
        }else {
            new PromptDialog.Builder(mContext)
                    .setTitle(R.string.dialog_title)
                    .setViewStyle(PromptDialog.VIEW_STYLE_TITLEBAR_SKYBLUE)
                    .setMessage("请选择图片")
                    .setButton1("确定", new PromptDialog.OnClickListener() {

                        @Override
                        public void onClick(Dialog dialog, int which) {
                            dialog.dismiss();

                        }
                    }).show();
        }
    }
    private void compressImg(final File files) {
        Luban.get(this)
                .load(files)                     //传人要压缩的图片
                .putGear(Luban.THIRD_GEAR)      //设定压缩档次，默认三挡
                .setCompressListener(new OnCompressListener() { //设置回调
                    @Override
                    public void onStart() {}
                    @Override
                    public void onSuccess(File file) {
                        uploadImage(file);
                    }
                    @Override
                    public void onError(Throwable e) {
                        // TODO 当压缩过去出现问题时调用
                        uploadImage(files);
                        Toast.makeText(UploadingPic.this,"图片压缩失败!",
                                Toast.LENGTH_SHORT).show();
                    }
                }).launch();
    }
    private void uploadImage(File file) {
        String validateURL = NetConstants.Upload_Image;
        Map<String, String> textParams = new HashMap<String, String>();
        Map<String, File> fileparams = new HashMap<String, File>();
        textParams.put("token",token);
        textParams.put("id", id);
        textParams.put("type",type);
        fileparams.put("image",file);
        SendRequestUtils.PostFileToServer(textParams,fileparams,validateURL,uploadHandler);
    }
    private static class UploadImageHandler extends BaseHandler<UploadingPic> {

        public UploadImageHandler(UploadingPic object) {
            super(object);
        }

        @Override
        public void onSuccess(UploadingPic object, Message msg) {
            object.uploadingSuccess(msg);
        }
        @Override
        public void onFinilly(UploadingPic object) {
            object.btn_picture.setEnabled(true);    //设置可点
            super.onFinilly(object);
        }
    }
    private void uploadingSuccess(Message msg) {
        try {
            Gson gson = new Gson();
            BaseModel model = gson.fromJson(msg.obj.toString(), BaseModel.class);
            if (2 == model.result_code) {
                //重新登陆
                CommonMethod.goLogin(new WeakReference<Activity>(this), 2);
            } else {
                if (model.result.equals("success")) {
                    k++;
                    if (k==imgPaths.size()){
                        customDialog.dismiss();
                        k=0; //图片成功
                        if (operation){
                            CometoConvert();
                        }else {
                            UploadingSuccess();
                        }
                    }
                }else {
                    customDialog.dismiss();
                    CommonMethod.faifure(new WeakReference<Activity>(this), model.error);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void CometoConvert() {
        Intent intent=new Intent(mContext,ConvertOrderReson.class);
        intent.putExtra("id",id);
        startActivityForResult(intent,0X003);
    }
    private void UploadingSuccess() {
        new PromptDialog.Builder(this)
                .setTitle(R.string.dialog_title)
                .setTitleBarGravity(100)
                .setViewStyle(PromptDialog.VIEW_STYLE_TITLEBAR_SKYBLUE)
                .setMessage("图片上传成功")
                .setMessageSize(16)
                .setButton1("确定", new PromptDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        CometoSever();
                        dialog.dismiss();
                    }
                }).show();
    }
    private void CometoSever() {
        customDialog.show();
        //确认接单
        String validateURL = NetConstants.Start_Server;
        Map<String, String> textParams = new HashMap<String, String>();
        textParams.put("token", token);
        textParams.put("id", id);
        SendRequestUtils.PostDataFromService(textParams, validateURL, startseverHandler);
    }
    private static class StartServerHandler extends BaseHandler<UploadingPic> {

        public StartServerHandler(UploadingPic object) {
            super(object);
        }

        @Override
        public void onSuccess(UploadingPic object, Message msg) {
            object.ComeToSuccess(msg);
        }

        @Override
        public void onFinilly(UploadingPic object) {
            object.customDialog.dismiss();
            super.onFinilly(object);
        }
    }

    private void ComeToSuccess(Message msg) {
        try {
            Gson gson = new Gson();
            BaseModel model = gson.fromJson(msg.obj.toString(), BaseModel.class);
            if (2 == model.result_code) {
                //重新登陆
                CommonMethod.goLogin(new WeakReference<Activity>(this), 2);
            } else {
                if (model.result.equals("success")) {
                    setResult(2);    //回调结果码
                    finish();
                }else {
                    CommonMethod.faifure(new WeakReference<Activity>(this), model.error);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void hotline() {
        new PromptDialog.Builder(this)
                .setTitle("拨打电话")
                .setViewStyle(PromptDialog.VIEW_STYLE_TITLEBAR_SKYBLUE)
                .setMessage(data.user_phone)
                .setButton1("取消", new PromptDialog.OnClickListener() {

                    @Override
                    public void onClick(Dialog dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setButton2("呼叫", new PromptDialog.OnClickListener() {

                    @Override
                    public void onClick(final Dialog dialog, int which) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (UploadingPic.this.checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel:" + data.user_phone));
                                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(callIntent);
                            } else {
                                //没有权限
                                PermissionDog.getInstance().SetAction(new PermissionDog.PermissionDogAction() {
                                    @Override
                                    public void never() {
                                        super.never();
                                        Toast.makeText(getApplicationContext(), "已经拒绝了拨打电话的权限，请前往设置开启", Toast.LENGTH_SHORT);
                                        dialog.dismiss();
                                    }
                                    @Override
                                    public void denied() {
                                        super.denied();
                                        dialog.dismiss();
                                    }
                                    @Override
                                    public void grant() {
                                        super.grant();
                                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                                        callIntent.setData(Uri.parse("tel:" + data.user_phone));
                                        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        try {
                                            startActivity(callIntent);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                PermissionDog.getInstance().requestSinglePermissions(UploadingPic.this, Manifest.permission.CALL_PHONE);
                            }
                        } else {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + data.user_phone));
                            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(callIntent);
                        }
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
