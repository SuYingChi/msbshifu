package com.msht.master.FunctionView;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.msht.master.Base.BaseHandler;
import com.msht.master.Common.CommonMethod;
import com.msht.master.Constants.NetConstants;
import com.msht.master.Constants.SPConstants;
import com.msht.master.LoginActivity;
import com.msht.master.Model.BaseModel;
import com.msht.master.R;
import com.msht.master.UIView.CustomDialog;
import com.msht.master.UIView.PromptDialog;
import com.msht.master.Utils.BitmapUtil;
import com.msht.master.Utils.NetUtil;
import com.msht.master.Utils.PermissionDog;
import com.msht.master.Utils.SendRequestUtils;
import com.msht.master.Utils.SharedPreferencesUtils;
import com.msht.master.Utils.StreamTools;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import me.iwf.photopicker.PhotoPicker;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class AddCertificate extends AppCompatActivity {
    private ImageView Imgcertificate;
    private EditText et_certName;
    private EditText et_date;
    private CheckBox box_date;
    private Button btn_send;
    private RelativeLayout Rcert;
    private String always_effective = "0", token;
    private File certe_file;
    private CustomDialog customDialog;
    private final int REQUEST_PERMISSION_SETTING = 0x001;
    String[] permissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};

    Handler requestHandler = new AddCertHandler(this);
    private TextView tv_number;//证书编号

    private void initShow() {
        new PromptDialog.Builder(this)
                .setTitle(R.string.dialog_title)
                .setViewStyle(PromptDialog.VIEW_STYLE_TITLEBAR_SKYBLUE)
                .setMessage("您的相关证书已提交")
                .setButton1("确定", new PromptDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        setResult(1);
                        dialog.dismiss();
                        finish();

                    }
                }).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocational_certificate);
        token = (String) SharedPreferencesUtils.getData(getApplicationContext(), SPConstants.TOKEN, "");
        customDialog = new CustomDialog(this, "正在加载");
        initHeaderTitle();
        initView();
        initEvent();
    }

    private void initHeaderTitle() {
        findViewById(R.id.id_goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView) findViewById(R.id.tv_title)).setText("添加证书");
    }

    private void initView() {
        et_certName = (EditText) findViewById(R.id.id_cert_name);
        et_date = (EditText) findViewById(R.id.id_tv_year);
        box_date = (CheckBox) findViewById(R.id.id_year_radio);
        Rcert = (RelativeLayout) findViewById(R.id.id_rela_img);
        btn_send = (Button) findViewById(R.id.id_btn_send);
        Imgcertificate = (ImageView) findViewById(R.id.id_certificate);
        tv_number = (TextView) findViewById(R.id.tv_number);
        btn_send.setEnabled(false);
    }
    private void initEvent() {
        box_date.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    always_effective = "1";
                    et_date.setInputType(InputType.TYPE_NULL);
                    et_date.setText("");
                } else {
                    always_effective = "0";
                    et_date.setInputType(InputType.TYPE_CLASS_DATETIME);
                }
            }
        });
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(et_certName.getText().toString())) {
                    btn_send.setEnabled(false);
                } else {
                    btn_send.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        et_certName.addTextChangedListener(watcher);
        Rcert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PermissionDog.getInstance().SetAction(new PermissionDog.PermissionDogAction() {
                    @Override
                    public void allallow() {
                        super.allallow();
                        getphoto();
                    }

                    @Override
                    public void multi(String[] granteds, String[] denieds, String[] nevershows) {
                        if (nevershows.length != 0) {
                            Log.e("tag", "" + nevershows.length);
                            new AlertDialog.Builder(AddCertificate.this, R.style.DialogTheme).setTitle("民生师傅")
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
                                    Toast.makeText(AddCertificate.this, "未获取到必要的权限", Toast.LENGTH_SHORT);
                                    finish();
                                }
                            }).create().show();
                        } else {
                            if (denieds.length != 0) {
                                Log.e("tag", "" + denieds.length);
                                new AlertDialog.Builder(AddCertificate.this, R.style.DialogTheme).setTitle("民生师傅")
                                        .setMessage("民生师傅需要读写空间和拨打电话的权限")
                                        .setPositiveButton("获取", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                PermissionDog.getInstance().requestMultiPermissions(AddCertificate.this, permissionList);
                                            }
                                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(AddCertificate.this, "未获取到必要的权限", Toast.LENGTH_SHORT);
                                        finish();
                                    }
                                }).create().show();
                            } else {
                                getphoto();
                            }
                        }
                    }
                });

                PermissionDog.getInstance().requestMultiPermissions(AddCertificate.this, permissionList);
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = et_certName.getText().toString().trim();
                String number = tv_number.getText().toString().trim();
                String effective_time = et_date.getText().toString().trim();
                if (matchjudge(certe_file)) {
                    customDialog.show();
                    String validateURL = NetConstants.ADD_CERTIFICATE;
                    Map<String, String> textParams = new HashMap<String, String>();
                    Map<String, File> fileParams = new HashMap<String, File>();
                    textParams.put("token", token);
                    textParams.put("name", name);
                    textParams.put("number", number);
                    textParams.put("effective_time", effective_time);
                    textParams.put("always_effective", always_effective);
                    fileParams.put("img", certe_file);
                    SendRequestUtils.PostFileToServer(textParams, fileParams, validateURL, requestHandler);
                }
            }
        });
    }

    private boolean matchjudge(File certe_file) {
        if (certe_file == null) {
            new PromptDialog.Builder(this)
                    .setTitle(R.string.dialog_title)
                    .setViewStyle(PromptDialog.VIEW_STYLE_TITLEBAR_SKYBLUE)
                    .setMessage("请添加证书图片！")
                    .setButton1("确定", new PromptDialog.OnClickListener() {
                        @Override
                        public void onClick(Dialog dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
            return false;
        } else {
            return true;
        }
    }

    private void getphoto() {
        PhotoPicker.builder()
                .setPhotoCount(1)
                .setShowCamera(true)
                .setShowGif(true)
                .setPreviewEnabled(false)
                .start(AddCertificate.this, PhotoPicker.REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK &&
                (requestCode == PhotoPicker.REQUEST_CODE)) {
            List<String> photos = null;
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                String filepath = photos.get(0);
                certe_file = new File(filepath);
                if (certe_file.exists() && certe_file.canRead()) {
                    compressImg(certe_file, 0);
                }

            }
        }
    }

    private void compressImg(File file_two, final int i) {
        Luban.get(this)
                .load(file_two)
                .putGear(Luban.THIRD_GEAR)
                .asObservable()
                .subscribeOn(Schedulers.io())
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        Toast.makeText(AddCertificate.this, "图片压缩失败!",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                })
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends File>>() {
                    @Override
                    public Observable<? extends File> call(Throwable throwable) {
                        return Observable.empty();
                    }
                })
                .subscribe(new Action1<File>() {
                    @Override
                    public void call(File file) {
                        certe_file = file;
                        Imgcertificate.setImageBitmap(BitmapUtil.decodeSampledBitmapFromFile(file.getAbsolutePath(), 500, 500));
                    }
                });
    }


    /*动态权限*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionDog.getInstance().notifyPermissionsChanged(AddCertificate.this, requestCode, permissions, grantResults);
    }

    private static class AddCertHandler extends BaseHandler<AddCertificate> {

        public AddCertHandler(AddCertificate object) {
            super(object);
        }

        @Override
        public void onSuccess(AddCertificate object, Message msg) {
            object.addCertSuccess(msg);
        }

        @Override
        public void onFinilly(AddCertificate object) {
            object.customDialog.dismiss();
        }
    }

    private void addCertSuccess(Message msg) {
        try {
            Gson gson = new Gson();
            BaseModel model = gson.fromJson(msg.obj.toString(), BaseModel.class);
            if (2 == model.result_code) {
                CommonMethod.goLogin(new WeakReference<Activity>(this), 2);
            } else {
                if (model.result.equals("success")) {
                    initShow();
                } else {
                    CommonMethod.faifure(new WeakReference<Activity>(this), model.error);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
