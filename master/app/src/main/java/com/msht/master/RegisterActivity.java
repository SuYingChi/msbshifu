package com.msht.master;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.msht.master.Base.BaseHandler;
import com.msht.master.Constants.NetConstants;
import com.msht.master.HtmlWeb.Agreement;
import com.msht.master.UIView.PromptDialog;
import com.msht.master.Utils.NetUtil;
import com.msht.master.Utils.SendRequestUtils;
import com.msht.master.Utils.StreamTools;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText Etphonenumber,Etcode;
    private EditText Etnpassword;
    private ImageView gobackimg,clearimg,showimg;
    private Button Btngetcode,Btnregister;
    private TextView tv_treaty;

    private TimeCount time;     //倒计时时间

    private String PhoneNo;
    private String verifycode;
    private String password;
    private String reco_code;

    Handler registerHandler = new RegistHandler(this);
    Handler sendHandler = new SendHandler(this);



    private void failure() {
       new PromptDialog.Builder(this)
                .setTitle(R.string.dialog_title)
                .setViewStyle(PromptDialog.VIEW_STYLE_TITLEBAR_SKYBLUE)
                .setMessage("请检查你的网络是否连接")
                .setButton1("确定", new PromptDialog.OnClickListener() {

                    @Override
                    public void onClick(Dialog dialog, int which) {
                        dialog.dismiss();

                    }
                }).show();
    }


    private void Registerrequest() {
        new PromptDialog.Builder(this)
                .setTitle(R.string.dialog_title)
                .setViewStyle(PromptDialog.VIEW_STYLE_TITLEBAR_SKYBLUE)
                .setMessage("注册成功，确定返回登录")
                .setButton1("确定", new PromptDialog.OnClickListener() {

                    @Override
                    public void onClick(Dialog dialog, int which) {
                        dialog.dismiss();
                        finish();

                    }
                }).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initHeaderTtile();
        initView();
        initEvent();
        time=new TimeCount(120000,1000);
    }

    private void initHeaderTtile() {
        gobackimg=(ImageView)findViewById(R.id.id_goback);
        ((TextView)findViewById(R.id.tv_title)).setText("注册");
        gobackimg.setOnClickListener(this);
    }

    private void initView() {
        clearimg=(ImageView)findViewById(R.id.id_clearimg);//清除密码
        showimg=(ImageView)findViewById(R.id.id_image_show); //显示密码
        showimg.setTag(0);
        Btngetcode=(Button)findViewById(R.id.id_btn_getcode);
        Btnregister=(Button)findViewById(R.id.id_tijiao_regiser);
        Etphonenumber=(EditText)findViewById(R.id.id_et_phonenumber);
        Etcode=(EditText)findViewById(R.id.id_et_code);
        Etnpassword=(EditText)findViewById(R.id.id_et_password);
        tv_treaty=(TextView)findViewById(R.id.id_treaty);
        Btnregister.setEnabled(false);

    }

    private void initEvent() {
        clearimg.setOnClickListener(this);
        tv_treaty.setOnClickListener(this);
        Etphonenumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(Etphonenumber.getText().toString())) {
                    Btngetcode.setEnabled(false);            //设置无效点击、背景
                    Btngetcode.setTextColor(Color.parseColor("#FF545454"));
                    Btngetcode.setBackgroundResource(R.drawable.shape_white_border_button);

                } else {
                    Btngetcode.setEnabled(true);            //有效点击，
                    Btngetcode.setTextColor(Color.parseColor("#ffffffff"));
                    Btngetcode.setBackgroundResource(R.drawable.shape_red_corners_button);

                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Btngetcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneNo = Etphonenumber.getText().toString().trim();
                if (isPhone(PhoneNo)) {
                    Btngetcode.setText("正在发送...");
                    time.start();
//                    Thread getcodeThread = new Thread(new getcodeHandler());
//                    getcodeThread.start();
                    Map<String, String> textParams = new HashMap<String, String>();
                    String validateURL=NetConstants.REPAIRMAN_CAPTCHA;
                    textParams.put("username",PhoneNo);
                    SendRequestUtils.PostDataFromService(textParams,validateURL,registerHandler);
                }
            }
        });

        /*检测验证码，是否有输入
        * 检测密码是否有输入，如果两者都有输入，则触发注册按钮为可点击
        * 并改变按钮背景色*/
        MyTextWatcher myTextWatcher = new MyTextWatcher();
        Etcode.addTextChangedListener(myTextWatcher);
        Etnpassword.addTextChangedListener(myTextWatcher);
        Btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Thread registerThread = new Thread(new sendregisterHandler());
//                registerThread.start();
                /**
                 * 发送注册请求
                 */
                password=Etnpassword.getText().toString();
                verifycode=Etcode.getText().toString();
                Map<String, String> textParams = new HashMap<String, String>();
                textParams.put("phone",PhoneNo);
                textParams.put("password",password);
                textParams.put("code",verifycode);
                String validateURL= NetConstants.REPAIRMAIN_REGIST;
                SendRequestUtils.PostDataFromService(textParams,validateURL,sendHandler);

            }
        });
        showimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tag=(Integer)v.getTag();
                switch (tag){
                    case 0:
                        showimg.setImageResource(R.drawable.show_password_primary);
                        Etnpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        v.setTag(1);
                        break;
                    case 1:
                        showimg.setImageResource(R.drawable.show_password_gray);
                        Etnpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        v.setTag(0);
                        break;
                }
            }
        });

    }

    class MyTextWatcher implements TextWatcher{
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (TextUtils.isEmpty(Etcode.getText().toString()) || TextUtils.isEmpty(Etnpassword.getText().toString())) {
                Btnregister.setEnabled(false);
            } else {
                Btnregister.setEnabled(true);

            }
        }
        @Override
        public void afterTextChanged(Editable s) {

        }
    }


    private boolean isPhone(String phoneNo) {     //判断电话号码个格式
        Pattern pattern=Pattern.compile("1[0-9]{10}");
        Matcher matcher=pattern.matcher(phoneNo);
        if (matcher.matches()){
            return true;
        }else {
            new PromptDialog.Builder(this)
                    .setTitle("民生宝")
                    .setViewStyle(PromptDialog.VIEW_STYLE_TITLEBAR_SKYBLUE)
                    .setMessage("您输入电话号码不正确")
                    .setButton1("确定", new PromptDialog.OnClickListener() {

                        @Override
                        public void onClick(Dialog dialog, int which) {
                            dialog.dismiss();

                        }
                    }).show();
            return false;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.id_goback:
                finish();
                break;
            case R.id.id_clearimg:
                ClearForm();
                break;
            case R.id.id_treaty:
                Treaty();
                break;
            default:
                break;

        }


    }
    private void Treaty() {
        Intent treaty=new Intent(RegisterActivity.this,Agreement.class);
        startActivity(treaty);
    }
    private void ClearForm() {   //清除
        //saveLoggedInUId(0,"","");
        Etphonenumber.setText("");

    }

    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {  //计时过程
            Btngetcode.setClickable(false);
            Btngetcode.setText(millisUntilFinished/1000+"秒");
        }
        @Override
        public void onFinish() {
            Btngetcode.setText("获取验证码");
            Btngetcode.setClickable(true);
        }
    }
    private static class RegistHandler extends BaseHandler<RegisterActivity>{

        public RegistHandler(RegisterActivity object) {
            super(object);
        }

        @Override
        public void onSuccess(RegisterActivity object, Message msg) {
            object.Btngetcode.setText("获取验证码");
            object.Btngetcode.setClickable(true);
        }
    }
    private static class SendHandler extends BaseHandler<RegisterActivity>{

        public SendHandler(RegisterActivity object) {
            super(object);
        }

        @Override
        public void onSuccess(RegisterActivity object, Message msg) {
            object.Registerrequest();
        }

        @Override
        public void onFailture(RegisterActivity object) {
            super.onFailture(object);
            object.failure();

        }
    }

}
