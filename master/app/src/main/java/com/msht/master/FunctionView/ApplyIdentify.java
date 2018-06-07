package com.msht.master.FunctionView;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.msht.master.Adapter.DistrictAdapter;
import com.msht.master.Adapter.SkilllMainTypeAdapter;
import com.msht.master.Base.BaseHandler;
import com.msht.master.Common.CommonMethod;
import com.msht.master.Constants.NetConstants;
import com.msht.master.Constants.SPConstants;
import com.msht.master.Constants.VariableUtil;
import com.msht.master.Controls.FullyGridLayoutManager;
import com.msht.master.Controls.FullyLinearLayoutManager;
import com.msht.master.Controls.MyRecyclerView;
import com.msht.master.Model.BaseModel;
import com.msht.master.Model.DistrictModel;
import com.msht.master.Model.RepairCategoryModel;
import com.msht.master.R;
import com.msht.master.UIView.CustomDialog;
import com.msht.master.UIView.PromptDialog;
import com.msht.master.Utils.AppToast;
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
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class ApplyIdentify extends AppCompatActivity implements View.OnClickListener {

    private MyRecyclerView skillRecycler;
    private MaterialSpinner spinner;
    private RelativeLayout Rfront, Reverse, Rcertificate;
    private RelativeLayout Rdistrict,Rcity;
    private View layout_enterprise;
    private Button btn_send;
    private ImageView goback, Imgfront, Imgreverse, Imgcertificate;
    private RadioGroup Group;
    private RadioButton radiomale, radiofemale;
    private TextView tv_enterprise;
    private TextView tv_city,tv_district;
    private EditText et_mastername, et_address, et_idcard;
    private EditText et_certName, et_date;
    private CheckBox box_date;
    private String mCity="海口";
    private String ep_id="-1";
    private String city_id;
    private String regionId="";
    private String token, sex = "1";
    private String experience = "1 年";
    private String always_effective = "0";
    private final int REQUEST_PERMISSION_SETTING = 0x001;
    String[] permissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};

    private static final String[] Year = {"1 年", "2~3 年", "3~5 年", "5年以上"};
    private File front_file;
    private File reverse_file;
    private File cert_file;
    private final int REVERSE = 4;
    private final int FRONT = 3;
    private final int CERTE = 5;
    private int CODE = 3;
    private final int AREA_CODE=7;
    private final int CITY_CODE=6;
    private final int EP_CODE=8;
    private Context mContext;
    private CustomDialog customDialog;
    private ArrayList<Integer> selectedSkill = new ArrayList<>();
    private ArrayList<Integer> selectedDistrict = new ArrayList<>();
    private int currentPic = 0;

    private SkilllMainTypeAdapter skilllMainTypeAdapter;
  //  private DistrictAdapter districtAdapter;
    private boolean finishApplyIdentify = false;
    private boolean finishApplyCert = true;
    Handler skillhandler = new SkillHandler(this);
    Handler applyHandler = new ApplyIdentifyHandler(this);
    Handler certHandler = new CertHandler(this);
    /**
     * 获取技能列表成功
     */
    private void getSkillListSuccess(Message msg) {
        try {
            Gson gson = new Gson();
            RepairCategoryModel model = gson.fromJson(msg.obj.toString(), RepairCategoryModel.class);
            if (2 == model.result_code) {
                //重新登陆
                CommonMethod.goLogin(new WeakReference<Activity>(this), 2);
            } else {
                if (model.result.equals("success")) {
                    //更新数据
                    ArrayList<RepairCategoryModel.MainCategory> data = model.data;
                    skilllMainTypeAdapter.addAll(data);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void Success() {
        finishApplyCert = true;
        FinishApply();
    }
    private void FinishApply() {
        if (finishApplyCert && finishApplyIdentify) {
            customDialog.dismiss();
            new PromptDialog.Builder(this)
                    .setTitle("提交成功")
                    .setTitleBarGravity(100)
                    .setViewStyle(PromptDialog.VIEW_STYLE_TITLEBAR_SKYBLUE)
                    .setMessage("客服将于3个工作日内完成审核工作，稍后请关注消息提醒")
                    .setMessageSize(16)
                    .setButton1("确定", new PromptDialog.OnClickListener() {
                        @Override
                        public void onClick(Dialog dialog, int which) {
                            setResult(2);    //回调结果码
                            dialog.dismiss();
                            finish();
                        }
                    }).show();
        }
    }

    private void ApplySuccess() {
        finishApplyIdentify = true;
        FinishApply();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_identify);
        mContext=this;
        initHeaderTitle();
        token = (String) SharedPreferencesUtils.getData(getApplicationContext(), SPConstants.TOKEN, "");
        customDialog = new CustomDialog(this, "正在加载");
        initView();
        skillRecycler.setLayoutManager(new FullyLinearLayoutManager(getApplicationContext()));
        skilllMainTypeAdapter = new SkilllMainTypeAdapter(this);
        skillRecycler.setAdapter(skilllMainTypeAdapter);
        initData();
        intEvent();
    }

    private void initHeaderTitle() {
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("申请认证");
        goback = (ImageView) findViewById(R.id.id_goback);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void initData() {
        //获取技能信息
        String skillURL = NetConstants.REPAIR_CATEGORY_ALL;
        SendRequestUtils.GetDataFromService(skillURL, skillhandler);
    }
    private void initView() {
        btn_send = (Button) findViewById(R.id.id_btn_send);
        skillRecycler = (MyRecyclerView) findViewById(R.id.id_skill_view);
        Imgfront = (ImageView) findViewById(R.id.id_frontal);
        Imgreverse = (ImageView) findViewById(R.id.id_reverse_side);
        et_mastername = (EditText) findViewById(R.id.id_et_name);
        et_idcard = (EditText) findViewById(R.id.id_et_idcard);
        et_address = (EditText) findViewById(R.id.id_et_address);
        et_certName = (EditText) findViewById(R.id.id_et_certName);
        et_date = (EditText) findViewById(R.id.id_year);
        box_date = (CheckBox) findViewById(R.id.id_year_radio);
        tv_enterprise=(TextView)findViewById(R.id.id_tv_enterprise);
        tv_city=(TextView)findViewById(R.id.id_tv_city) ;
        tv_district=(TextView)findViewById(R.id.id_tv_district);
        layout_enterprise=findViewById(R.id.id_re_enterprise);
        Rcity=(RelativeLayout)findViewById(R.id.id_re_city);
        Rdistrict=(RelativeLayout)findViewById(R.id.id_re_district);
        Rfront = (RelativeLayout) findViewById(R.id.id_rela_img1);
        Reverse = (RelativeLayout) findViewById(R.id.id_rela_img2);
        Rcertificate = (RelativeLayout) findViewById(R.id.id_rela_img3);
        Imgcertificate = (ImageView) findViewById(R.id.id_certificate);

        Group = (RadioGroup) findViewById(R.id.radioGroup);
        radiomale = (RadioButton) findViewById(R.id.id_male);
        radiofemale = (RadioButton) findViewById(R.id.id_female);
        radiomale.setChecked(true);    //默认男人
        spinner = (MaterialSpinner) findViewById(R.id.spinner);
        spinner.setItems(Year);
        btn_send.setEnabled(false);
    }
    private void intEvent() {

        Group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == radiomale.getId()) {
                    sex = "1";
                } else if (i == radiofemale.getId()) {
                    sex = "2";
                }
            }
        });
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                experience = item;
                Snackbar.make(view, "您的工作经验" + item, Snackbar.LENGTH_LONG).show();
            }
        });
        Rcity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext,SelectCity.class);
                startActivityForResult(intent,CITY_CODE);
            }
        });
        Rdistrict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(tv_city.getText().toString())){
                    Intent intent=new Intent(mContext,SelectDistrict.class);
                    intent.putExtra("cityid",city_id);
                    startActivityForResult(intent,AREA_CODE);
                }
            }
        });
        layout_enterprise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext,SelectEnterprise.class);
                startActivityForResult(intent,EP_CODE);
            }
        });
        Rfront.setOnClickListener(this);
        Reverse.setOnClickListener(this);
        Rcertificate.setOnClickListener(this);
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(et_mastername.getText().toString()) || TextUtils.isEmpty(et_address.getText().toString())
                        || TextUtils.isEmpty(et_idcard.getText().toString())) {
                    btn_send.setEnabled(false);
                } else {
                    btn_send.setEnabled(true);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        };
        et_mastername.addTextChangedListener(watcher);
        et_address.addTextChangedListener(watcher);
        et_idcard.addTextChangedListener(watcher);
        box_date.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {     //证书期限checkbox
                if (b) {
                    always_effective = "1";
                } else {
                    always_effective = "0";
                }
            }
        });
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishApplyCert = true;
                finishApplyIdentify = false;
                String districtId = regionId;
                String skillId = getSkillid();
                if (matchjudge(skillId, districtId, front_file, reverse_file)) {
                    if (!TextUtils.isEmpty(et_certName.getText().toString()) && cert_file == null) {
                        finishApplyCert = false;
                        String name = et_certName.getText().toString().trim();
                        String effective_time = et_date.getText().toString().trim();
                        String validateURL = NetConstants.ADD_CERTIFICATE;
                        Map<String, String> textParams = new HashMap<String, String>();
                        Map<String, File> fileParams = new HashMap<String, File>();
                        textParams.put("token", token);
                        textParams.put("name", name);
                        textParams.put("effective_time", effective_time);
                        textParams.put("always_effective", always_effective);
                        fileParams.put("img", cert_file);
                        SendRequestUtils.PostFileToServer(textParams, fileParams, validateURL, certHandler);
                    }
                    customDialog.show();

                    String name = et_mastername.getText().toString().trim();
                    String address = et_address.getText().toString().trim();
                    String idCard = et_idcard.getText().toString().trim();
                    String validateURL = NetConstants.REPAIRMAN_APPLY_VALID;
                    Map<String, String> textParams = new HashMap<String, String>();
                    Map<String, File> fileParams = new HashMap<String, File>();
                    textParams.put("token", token);
                    textParams.put("sex", sex);
                    textParams.put("name", name);
                    textParams.put("city_id",city_id);
                    textParams.put("address", address);
                    textParams.put("idCard", idCard);
                    textParams.put("ep_id",ep_id);
                    textParams.put("experience_year", experience);
                    textParams.put("service_district", districtId);
                    textParams.put("categories", skillId);
                    fileParams.put("idCardFrontPic", front_file);
                    fileParams.put("idCardBackPic", reverse_file);
                    SendRequestUtils.PostFileToServer(textParams,fileParams,validateURL,applyHandler);
                }
            }
        });
    }
    private String getSkillid() {
        selectedSkill.clear();
        List<RepairCategoryModel.MainCategory> dataList = skilllMainTypeAdapter.getDataList();
        for (RepairCategoryModel.MainCategory data : dataList) {
            ArrayList<RepairCategoryModel.MainCategory.ChildCategory> childList = data.child;
            for (RepairCategoryModel.MainCategory.ChildCategory child : childList) {
                if (child.selected == 1) {
                    selectedSkill.add(child.id);
                }
            }
        }
        StringBuilder result = new StringBuilder();
        boolean flag = false;
        for (int item : selectedSkill) {
            if (flag) {
                result.append(",");
            } else {
                flag = true;
            }
            result.append(item);
        }
        return result.toString();
    }
    private boolean matchjudge(String skillids, String regionids, File front_file, File reverse_file) {
        if (skillids.equals("") || regionids.equals("") || front_file == null || reverse_file == null) {
            new PromptDialog.Builder(this)
                    .setTitle(R.string.dialog_title)
                    .setViewStyle(PromptDialog.VIEW_STYLE_TITLEBAR_SKYBLUE)
                    .setMessage("请添加身份证图片,接单区域和选择技能")
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK &&
                (requestCode == FRONT || requestCode == PhotoPreview.REQUEST_CODE)) {
            List<String> photos = null;
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                String filepath = photos.get(0);
                File file_one = new File(filepath);
                if (file_one.exists() && file_one.canRead()) {
                    currentPic = 1;
                    compressImg(file_one);
                } else {
                    AppToast.makeShortToast(this, "选择了不能使用的图片");
                }
            }
        }
        if (resultCode == RESULT_OK &&
                (requestCode == REVERSE || requestCode == PhotoPreview.REQUEST_CODE)) {
            List<String> photos = null;
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                String filepath = photos.get(0);
                File file_two = new File(filepath);
                if (file_two.exists() && file_two.canRead()) {
                    currentPic = 2;
                    compressImg(file_two);
                } else {
                    AppToast.makeShortToast(this, "选择了不能使用的图片");
                }
            }
        }
        if (resultCode == RESULT_OK &&
                (requestCode == CERTE || requestCode == PhotoPreview.REQUEST_CODE)) {
            List<String> photos = null;
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                String filepath = photos.get(0);
                File file_three = new File(filepath);
                if (file_three.exists() && file_three.canRead()) {
                    currentPic = 3;
                    compressImg(file_three);
                } else {
                    AppToast.makeShortToast(this, "选择了不能使用的图片");
                }
            }
        }
        if (requestCode==CITY_CODE&&resultCode==3){
            regionId="";   //清空重新选择
            tv_district.setText("");   //重新选择城市成功后，清空之前的区域数据
            VariableUtil.selectedPos.clear();
            mCity=data.getStringExtra("city");
            city_id=data.getStringExtra("id");
            tv_city.setText(mCity);
        }
        if (requestCode==AREA_CODE&&resultCode==4){
            regionId=data.getStringExtra("districtId");
            String districtName=data.getStringExtra("districtName");
            tv_district.setText(districtName);
        }
        if (requestCode==EP_CODE&&resultCode==5){
            ep_id=data.getStringExtra("ep_id");
            String company_code=data.getStringExtra("company_code");
            tv_enterprise.setText(company_code);
        }
    }
    private void compressImg(File file_two) {
        Luban.get(this)
                .load(file_two)
                .putGear(Luban.THIRD_GEAR)
                .asObservable()
                .subscribeOn(Schedulers.io())
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        Toast.makeText(ApplyIdentify.this, "图片压缩失败!",
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
                        if (currentPic == 1) {
                            front_file = file;
                            Imgfront.setImageBitmap(BitmapUtil.decodeSampledBitmapFromFile(file.getAbsolutePath(), 500, 500));
                        } else if (currentPic == 2) {
                            reverse_file = file;
                            Imgreverse.setImageBitmap(BitmapUtil.decodeSampledBitmapFromFile(file.getAbsolutePath(), 500, 500));
                        } else if (currentPic == 3) {
                            cert_file = file;
                            Imgcertificate.setImageBitmap(BitmapUtil.decodeSampledBitmapFromFile(file.getAbsolutePath(), 500, 500));
                        }
                    }
                });

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_rela_img2:
                CODE = 4;
                break;
            case R.id.id_rela_img1:
                CODE = 3;
                break;
            case R.id.id_rela_img3:
                CODE = 5;
                break;
            default:
                break;
        }
        PermissionDog.getInstance().SetAction(new PermissionDog.PermissionDogAction() {
            @Override
            public void allallow() {
                getphoto();
            }
            @Override
            public void multi(String[] granteds, String[] denieds, String[] nevershows) {
                if (nevershows.length != 0) {
                    new AlertDialog.Builder(ApplyIdentify.this, R.style.DialogTheme).setTitle("民生师傅")
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
                            Toast.makeText(ApplyIdentify.this, "未获取到必要的权限", Toast.LENGTH_SHORT);
                            finish();
                        }
                    }).create().show();
                } else {
                    if (denieds.length != 0) {
                        Log.e("tag", "" + denieds.length);
                        new AlertDialog.Builder(ApplyIdentify.this, R.style.DialogTheme).setTitle("民生师傅")
                                .setMessage("民生师傅需要读写空间和拨打电话的权限")
                                .setPositiveButton("获取", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        PermissionDog.getInstance().requestMultiPermissions(ApplyIdentify.this, permissionList);
                                    }
                                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(ApplyIdentify.this, "未获取到必要的权限", Toast.LENGTH_SHORT);
                                finish();
                            }
                        }).create().show();
                    } else {
                        getphoto();
                    }
                }
            }
        });

        PermissionDog.getInstance().requestMultiPermissions(ApplyIdentify.this, permissionList);

    }

    private void getphoto() {
        PhotoPicker.builder()
                .setPhotoCount(1)
                .setShowCamera(true)
                .setShowGif(true)
                .setPreviewEnabled(true)
                .start(ApplyIdentify.this, CODE);
    }

    /*动态权限*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionDog.getInstance().notifyPermissionsChanged(ApplyIdentify.this, requestCode, permissions, grantResults);
    }



    private static class SkillHandler extends BaseHandler<ApplyIdentify> {

        public SkillHandler(ApplyIdentify object) {
            super(object);
        }

        @Override
        public void onSuccess(ApplyIdentify object, Message msg) {
            object.getSkillListSuccess(msg);
        }

        @Override
        public void onFinilly(ApplyIdentify object) {
            object.customDialog.dismiss();
        }
    }
    private static class ApplyIdentifyHandler extends BaseHandler<ApplyIdentify> {

        public ApplyIdentifyHandler(ApplyIdentify object) {
            super(object);
        }

        @Override
        public void onSuccess(ApplyIdentify object, Message msg) {
            object.applyIdentifySuccess(msg);
        }
    }

    private void applyIdentifySuccess(Message msg) {
        try {
            Gson gson = new Gson();
            BaseModel model = gson.fromJson(msg.obj.toString(), BaseModel.class);
            if (2 == model.result_code) {
                //重新登陆
                CommonMethod.goLogin(new WeakReference<Activity>(this), 2);
            } else {
                if (model.result.equals("success")) {
                    //申请认证成功
                    ApplySuccess();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class CertHandler extends BaseHandler<ApplyIdentify> {

        public CertHandler(ApplyIdentify object) {
            super(object);
        }

        @Override
        public void onSuccess(ApplyIdentify object, Message msg) {
            object.applyCertSuccess(msg);
        }
    }

    private void applyCertSuccess(Message msg) {
        try {
            Gson gson = new Gson();
            BaseModel model = gson.fromJson(msg.obj.toString(), BaseModel.class);
            if (2 == model.result_code) {
                //重新登陆
                CommonMethod.goLogin(new WeakReference<Activity>(this), 2);
            } else {
                if (model.result.equals("success")) {
                    //申请认证成功
                    Success();
                } else {
                    CommonMethod.faifure(new WeakReference<Activity>(this), model.error);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
