package com.msht.master.FunctionView;

import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jaredrummler.materialspinner.MaterialSpinner;
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
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddBankCard extends AppCompatActivity {
    private ImageView backimg;
    private EditText et_cardNo;
    private MaterialSpinner spinner;
    private Button btn_send;
    private CustomDialog customDialog;
    private String token, bankType = "招商银行";
    private static final String[] BANK = {"招商银行"};

    private Handler addBankHanlder =new GetDataHandler(this);


    private void initSuccess() {
        new PromptDialog.Builder(this)
                .setTitle(R.string.dialog_title)
                .setViewStyle(PromptDialog.VIEW_STYLE_TITLEBAR_SKYBLUE)
                .setMessage("已完成银行卡添加")
                .setButton1("确定", new PromptDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        setResult(2);
                        dialog.dismiss();
                        finish();

                    }
                }).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank_card);
        initHeaderTitle();
        customDialog = new CustomDialog(this, "正在加载");
        et_cardNo = (EditText) findViewById(R.id.id_et_cardNo);
        spinner = (MaterialSpinner) findViewById(R.id.spinner);
        spinner.setItems(BANK);
        btn_send = (Button) findViewById(R.id.id_btn_ok);
        btn_send.setEnabled(false);

        et_cardNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                btn_send.setEnabled(!TextUtils.isEmpty(et_cardNo.getText().toString()));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                bankType = item;
                Snackbar.make(view, "您的选择要绑定的银行类型为：" + item, Snackbar.LENGTH_LONG).show();
            }
        });
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cardNo = et_cardNo.getText().toString().trim();
                String regEx = "^(\\d{16}|\\d{19})$";
                Pattern pattern = Pattern.compile(regEx);
                Matcher matcher = pattern.matcher(et_cardNo.getText().toString());
                boolean b = matcher.matches();
                if(b){
                    //新增银行卡
                    token= (String) SharedPreferencesUtils.getData(getApplicationContext(), SPConstants.TOKEN,"");
                    String validateURL = NetConstants.BANKCARD_ADD;
                    Map<String, String> textParams = new HashMap<String, String>();
                    textParams.put("token", token);
                    textParams.put("bank", bankType);
                    textParams.put("card", cardNo);
                    SendRequestUtils.PostDataFromService(textParams,validateURL,addBankHanlder);
                }else {
                    AppToast.makeShortToast(getApplicationContext(),"请输入16位或19位银行卡号");
                }

            }
        });
    }

    private void initHeaderTitle() {
        backimg = (ImageView) findViewById(R.id.id_goback);
        backimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("添加银行卡");
    }

    private static class GetDataHandler extends BaseHandler<AddBankCard>{

        public GetDataHandler(AddBankCard object) {
            super(object);
        }

        @Override
        public void onSuccess(AddBankCard object, Message msg) {
            object.getDataSuccess(msg);

        }

        @Override
        public void onFinilly(AddBankCard object) {
            super.onFinilly(object);
            object.customDialog.dismiss();
        }
    }

    private void getDataSuccess(Message msg) {
        try{
            Gson gson = new Gson();
            BaseModel model = gson.fromJson(msg.obj.toString(), BaseModel.class);
            if (2 == model.result_code) {
                //重新登陆
                CommonMethod.goLogin(new WeakReference<Activity>(this), 2);
            } else {
                if (model.result.equals("success")) {
                    initSuccess();
                } else {
                    //失败
                    CommonMethod.faifure(new WeakReference<Activity>(this), model.error);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
