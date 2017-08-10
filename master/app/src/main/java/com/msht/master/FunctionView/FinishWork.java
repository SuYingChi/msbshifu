package com.msht.master.FunctionView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.msht.master.Adapter.PhotoPickerAdapter;
import com.msht.master.Base.BaseHandler;
import com.msht.master.Common.CommonMethod;
import com.msht.master.Constants.NetConstants;
import com.msht.master.Constants.SPConstants;
import com.msht.master.Model.BaseModel;
import com.msht.master.R;
import com.msht.master.UIView.CustomDialog;
import com.msht.master.UIView.PromptDialog;
import com.msht.master.Utils.AppToast;
import com.msht.master.Utils.SendRequestUtils;
import com.msht.master.Utils.SharedPreferencesUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.iwf.photopicker.PhotoPicker;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class FinishWork extends AppCompatActivity {
    private TextView tv_order_num;//订单编号
    private TextView tv_order_status;//订单状态
    private TextView tv_second_type;//维修小类
    private TextView tv_total;
    private TextView tv_num;

    private EditText et_detect;//上门检测费
    private EditText et_material;//维修材料费
    private EditText et_serve;//服务费
    private EditText et_memo;//备注
    private EditText et_day; //保修卡时间

    private Button   btn_submit;
    private View     layout_button;
    private GridView photogridview;
    private String id;
    private String token;
    private String category;
    private String statusDesc;
    private String detect_fee;
    private String material_fee;
    private String serve_fee;
    private String memo ;
    private String guarantee_day;
    private int k=0;
    private int num=140;
    private int MIN_MARK = 0;
    private int MAX_MARK = 90;
    private ArrayList<String> imgPaths = new ArrayList<>();
    private Context mContext;
    private CustomDialog customDialog;
    private PhotoPickerAdapter mAdapter;
    private int SubmitCode=0x001;

    private Handler submitHandler =new SubmitHandler(this);
    private Handler uploadHandler = new UploadImageHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_work);
        mContext=this;
        Intent data = getIntent();
        id = data.getStringExtra("id");
        category=data.getStringExtra("category");
        statusDesc=data.getStringExtra("statusDesc");
        token = (String) SharedPreferencesUtils.getData(getApplicationContext(), SPConstants.TOKEN, "");
        customDialog = new CustomDialog(this, "正在加载...");
        initHeaderTitle();
        initView();
        initEvent();
    }
    private void initView() {
        tv_order_num = (TextView) findViewById(R.id.id_orderNo);
        tv_num=(TextView)findViewById(R.id.id_tv_num) ;
        tv_order_status = (TextView) findViewById(R.id.id_tv_status);
        tv_second_type = (TextView) findViewById(R.id.tv_order_type);
        et_detect = (EditText) findViewById(R.id.et_detect);
        et_material = (EditText) findViewById(R.id.et_material);
        et_serve = (EditText) findViewById(R.id.et_serve);
        tv_total = (TextView) findViewById(R.id.tv_total);
        et_memo = (EditText) findViewById(R.id.id_et_memo);
        et_day=(EditText)  findViewById(R.id.id_et_day);
        layout_button=findViewById(R.id.id_layout_button);
        btn_submit=(Button) layout_button.findViewById(R.id.btn_send);
        btn_submit.setVisibility(View.VISIBLE);
        photogridview=(GridView)findViewById(R.id.noScrollgridview);

        tv_order_num.setText(id);
        tv_second_type.setText(category);
        tv_order_status.setText(statusDesc);

    }
    private void initHeaderTitle() {
        findViewById(R.id.id_goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("完工填写");
    }
    private void initEvent() {
        MyTextWatcher myTextWatcher = new MyTextWatcher();
        et_detect.addTextChangedListener(myTextWatcher);
        et_material.addTextChangedListener(myTextWatcher);
        et_serve.addTextChangedListener(myTextWatcher);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detect_fee = TextUtils.isEmpty(et_detect.getText().toString().trim()) ? "0" : et_detect.getText().toString().trim();
                material_fee = TextUtils.isEmpty(et_material.getText().toString().trim()) ? "0" : et_material.getText().toString().trim();
                serve_fee = TextUtils.isEmpty(et_serve.getText().toString().trim()) ? "0" : et_serve.getText().toString().trim();
                memo = et_memo.getText().toString().trim();
                guarantee_day=et_day.getText().toString().trim();
                if (imgPaths.size()!=0){
                    customDialog.show();
                    btn_submit.setEnabled(false);
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
        });
        mAdapter = new PhotoPickerAdapter(imgPaths);
        photogridview.setAdapter(mAdapter);
        photogridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == imgPaths.size()) {
                    PhotoPicker.builder()
                            .setPhotoCount(4)
                            .setShowCamera(true)
                            .setSelected(imgPaths)
                            .setShowGif(true)
                            .setPreviewEnabled(true)
                            .start(FinishWork.this, PhotoPicker.REQUEST_CODE);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("imgPaths", imgPaths);
                    bundle.putInt("position", position);
                    Intent intent = new Intent(FinishWork.this, EnlargePicActivity.class);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, position);
                }
            }
        });
        et_memo.addTextChangedListener(new TextWatcher() {    //备注数字显示
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                int number=num-s.length();
                String Wnum=String.valueOf(number);
                tv_num.setText("还剩"+Wnum+"个字可以填写");
            }
        });
        et_day.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (start > 1)
                {
                    if (MIN_MARK != -1 && MAX_MARK != -1)
                    {
                        int num = Integer.parseInt(s.toString());
                        if (num > MAX_MARK)
                        {
                            s = String.valueOf(MAX_MARK);
                            et_day.setText(s);
                            }
                        else if(num < MIN_MARK)
                        s = String.valueOf(MIN_MARK);
                        return;
                        }
                    }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && !s.equals(""))
                {
                    if (MIN_MARK != -1 && MAX_MARK != -1)
                    {
                        int markVal = 0;
                        try
                        {
                            markVal = Integer.parseInt(s.toString());
                        }
                        catch (NumberFormatException e)
                        {
                            markVal = 0;
                        }
                        if (markVal > MAX_MARK)
                        {
                            Toast.makeText(getBaseContext(), "天数不能超过90", Toast.LENGTH_SHORT).show();
                            et_day.setText(String.valueOf(MAX_MARK));
                        }
                        return;
                    }
                }
            }
        });
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
                        Toast.makeText(mContext,"图片压缩失败!",
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
        textParams.put("type","3");
        fileparams.put("image",file);
        SendRequestUtils.PostFileToServer(textParams,fileparams,validateURL,uploadHandler);
    }

    private static class UploadImageHandler extends BaseHandler<FinishWork> {

        public UploadImageHandler(FinishWork object) {
            super(object);
        }

        @Override
        public void onFailture(FinishWork object) {
            object.btn_submit.setEnabled(true);
            super.onFailture(object);
        }

        @Override
        public void onSuccess(FinishWork object, Message msg) {
            object.uploadingSuccess(msg);
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
                        //图片成功
                        submitBill();   //开始提交账单
                    }
                }else {
                    btn_submit.setEnabled(true);
                    customDialog.dismiss();
                    CommonMethod.faifure(new WeakReference<Activity>(this), model.error);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void submitBill() {
        String validateURL = NetConstants.REPAIR_ORDER_BILL;
        Map<String, String> textParams = new HashMap<String, String>();
        textParams.put("token", token);
        textParams.put("id", id);
        textParams.put("detect_fee", detect_fee);
        textParams.put("material_fee", material_fee);
        textParams.put("serve_fee", serve_fee);
        textParams.put("guarantee_day",guarantee_day);
        textParams.put("memo", memo);
        SendRequestUtils.PostDataFromService(textParams, validateURL, submitHandler);
    }
    private class SubmitHandler extends Handler{
        private WeakReference<FinishWork> ref;
        SubmitHandler(FinishWork activity) {
            ref = new WeakReference<>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            final FinishWork activity = ref.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            switch (msg.what) {
                case SendRequestUtils.SUCCESS:
                    btn_submit.setEnabled(true);
                    activity.submitBillSuccess(msg);
                    break;
                case SendRequestUtils.FAILURE:
                    btn_submit.setEnabled(true);
                    AppToast.makeShortToast(activity, "网络连接失败");
                    break;
                case SendRequestUtils.ERRORCODE:
                    btn_submit.setEnabled(true);
                    AppToast.makeShortToast(activity, "网络连接失败");
                    break;
            }
            customDialog.dismiss();
        }
    }
    private void submitBillSuccess(Message msg) {
        setResult(SubmitCode);
        new PromptDialog.Builder(this)
                .setTitle(R.string.dialog_title)
                .setViewStyle(PromptDialog.VIEW_STYLE_TITLEBAR_SKYBLUE)
                .setMessage("提交账单成功")
                .setButton1("确定", new PromptDialog.OnClickListener() {

                    @Override
                    public void onClick(Dialog dialog, int which) {
                        finish();
                        dialog.dismiss();
                    }
                }).show();
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
    }
    public class MyTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            double totals = 0.0;
            //上门检测费
            String detect = et_detect.getText().toString().trim();
            //材料费
            String material = et_material.getText().toString().trim();
            //维修服务费
            String serve_fee = et_serve.getText().toString().trim();
            if (!detect.equals("") && !detect.equals(".")) {
                double det = Double.valueOf(detect);
                totals = det;
            }
            if (!serve_fee.equals("") && !serve_fee.equals(".")) {
                double ser = Double.valueOf(serve_fee);
                totals = totals + ser;
            }
            if (!material.equals("") && !material.equals(".")) {
                double mat = Double.valueOf(material);
                totals = totals + mat;
            }
            double tals = BigDecinmals(totals);
            String tal = String.valueOf(tals);
            tv_total.setText(tal);
            if (!TextUtils.isEmpty(detect) || !TextUtils.isEmpty(material) || !TextUtils.isEmpty(serve_fee)) {
                btn_submit.setEnabled(true);
            } else {
                btn_submit.setEnabled(false);
            }
        }
        @Override
        public void afterTextChanged(Editable s) {}
    }
    private double BigDecinmals(double totals) {
        long lon = Math.round(totals * 100);
        double tot = lon / 100.0;
        return tot;
    }
}
