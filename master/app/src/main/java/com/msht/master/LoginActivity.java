package com.msht.master;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.msht.master.Base.BaseHandler;
import com.msht.master.Common.CommonMethod;
import com.msht.master.Constants.NetConstants;
import com.msht.master.Constants.SPConstants;
import com.msht.master.Model.LoginModel;
import com.msht.master.UIView.CustomDialog;
import com.msht.master.UIView.PromptDialog;
import com.msht.master.Utils.SendRequestUtils;
import com.msht.master.Utils.SharedPreferencesUtils;


import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private Button   btn_login,btn_register;
    private ImageView showimg;
    private TextView tv_forget;
    private EditText et_phnoe,et_password;
    private CheckBox Remember;

    private String userphone,password;
    private String LOGIN_USERNAME = "MAP_LOGIN_USERNAME";
    /** 如果登录成功后,用于保存PASSWORD到SharedPreferences,以便下次不再输入 */
    private String LOGIN_PASSWORD = "MAP_LOGIN_PASSWORD";
    private String LOGIN_STATE ="MAP_LOGIN_STATE";//登录状态
    private final String SHARE_LOGIN_TAG = "MAP_SHARE_LOGIN_TAG";
    private SharedPreferences share;
    private int state;


    private CustomDialog customDialog;

    Handler loginHandler = new LoginHandler(this);

    private void initShow(LoginModel.LoginDetail data) {
        String id=data.id+"";
        String masterName=data.name;
        String avatar=data.avatar;
        String valid=data.valid+"";
        String token=data.token;
        SharedPreferencesUtils.saveData(getApplicationContext(),"ID",id);
        SharedPreferencesUtils.saveData(getApplicationContext(),"MASTER",masterName);
        SharedPreferencesUtils.saveData(getApplicationContext(),"AVATARURL",avatar);
        SharedPreferencesUtils.saveData(getApplicationContext(),"VALID",valid);
        /**
         * 登陆成功
         */
        SharedPreferencesUtils.saveData(getApplicationContext(), SPConstants.TOKEN,token);
        if (Remember.isChecked()) {
            SharedPreferences.Editor editor = share.edit();
            editor.putBoolean(LOGIN_STATE, true);
            editor.putString(LOGIN_USERNAME, userphone);
            editor.putString(LOGIN_PASSWORD, password);
            editor.commit();
        }
        if (state==1) {
            Intent login = new Intent(LoginActivity.this, HomePage.class);
            startActivity(login);
            finish();
        }else if (state==2){
            //当传过来的页面不是主页面中的一个的话，直接退出
            finish();
        }else {
            Intent login = new Intent(LoginActivity.this, HomePage.class);
            startActivity(login);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        customDialog=new CustomDialog(this, "正在加载");
        Intent data=getIntent();
        state=data.getIntExtra("state",0);
        initView();
        initEvent();
    }
    private void initView() {
        tv_forget=(TextView)findViewById(R.id.id_forget_pwd) ;
        et_phnoe=(EditText)findViewById(R.id.id_userphone);
        et_password=(EditText)findViewById(R.id.id_password);
        Remember=(CheckBox)findViewById(R.id.id_remember_Box);
        btn_login=(Button)findViewById(R.id.id_btn_login);
        btn_register=(Button)findViewById(R.id.id_btn_register);
        showimg=(ImageView)findViewById(R.id.id_image_show); //显示密码
        showimg.setTag(0);
        btn_login.setEnabled(false);
        /*记住密码*/
        Remember.setChecked(false);
        share=this.getSharedPreferences(SHARE_LOGIN_TAG, Context.MODE_PRIVATE);
        if (share.getBoolean(LOGIN_STATE,false)){
            et_phnoe.setText(share.getString(LOGIN_USERNAME,""));
            et_password.setText(share.getString(LOGIN_PASSWORD,""));

            btn_login.setEnabled(true);
        }

    }
    private void initEvent() {

        et_phnoe.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(et_phnoe.getText().toString()) || TextUtils.isEmpty(et_password.getText().toString())) {
                    btn_login.setEnabled(false);
                } else {
                    btn_login.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(et_phnoe.getText().toString()) || TextUtils.isEmpty(et_password.getText().toString())) {
                    btn_login.setEnabled(false);
                } else {
                    btn_login.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Remember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean check) {
                if (!check){    //清除记住密码
                    SharedPreferences.Editor editor=share.edit();
                    editor.clear();
                    editor.commit();
                }
            }
        });
        tv_forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent forget=new Intent(LoginActivity.this,Findpassword.class);
                startActivity(forget);
            }
        });
        showimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tag=(Integer)v.getTag();
                switch (tag){
                    case 0:
                        showimg.setImageResource(R.drawable.show_password_primary);
                        et_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        v.setTag(1);
                        break;
                    case 1:
                        showimg.setImageResource(R.drawable.show_password_gray);
                        et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        v.setTag(0);
                        break;
                }
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.show();
                //登陆
                userphone = et_phnoe.getText().toString();
                password = et_password.getText().toString();
                String validateURL= NetConstants.REPAIRMAN_LOGIN;
                Map<String, String> textParams = new HashMap<String, String>();
                textParams.put("username",userphone);
                textParams.put("password", password);
                SendRequestUtils.PostDataFromService(textParams,validateURL,loginHandler);


            }
        });
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent register=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(register);
            }
        });
    }
    private static class LoginHandler extends BaseHandler<LoginActivity>{

        public LoginHandler(LoginActivity object) {
            super(object);
        }

        @Override
        public void onSuccess(LoginActivity object, Message msg) {
            object.loginSuccess(msg);
        }

        @Override
        public void onFinilly(LoginActivity object) {
            object.customDialog.dismiss();
        }
    }

    private void loginSuccess(Message msg) {
        try {
            Gson gson = new Gson();
            LoginModel loginModel = gson.fromJson(msg.obj.toString(), LoginModel.class);
            if (2 == loginModel.result_code) {
                //重新登陆
                CommonMethod.goLogin(new WeakReference<Activity>(this), 2);
            } else {
                if (loginModel.result.equals("success")) {

                    initShow(loginModel.data);

                } else {
                    //失败
                    CommonMethod.faifure(new WeakReference<Activity>(this), loginModel.error);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
