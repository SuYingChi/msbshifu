package com.msht.master;

import android.app.Dialog;
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

import com.google.gson.Gson;
import com.msht.master.Base.BaseHandler;
import com.msht.master.Constants.NetConstants;
import com.msht.master.Model.BaseModel;
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

public class Findpassword extends AppCompatActivity implements View.OnClickListener {

    private EditText Etphonenumber, Etcode;
    private EditText Etnewpassword, Etverifypassword;
    private ImageView gobackimg, clearimg, showimg;
    private Button Btngetcode, Btnreset;

    private String PhoneNo, verifycode;
    private String newpassword;

    private TimeCount time;     //倒计时时间

    Handler findHandler = new SendHandler(this);

    Handler resettingHandler = new ResetHandler(this);

    private void failure() {
        new PromptDialog.Builder(this)
                .setTitle(R.string.dialog_title)
                .setViewStyle(PromptDialog.VIEW_STYLE_TITLEBAR_SKYBLUE)
                .setMessage("发生了错误")
                .setButton1("确定", new PromptDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                }).show();
    }
    private void failure(String error) {
        new PromptDialog.Builder(this)
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

    private void Registerrequest() {
        new PromptDialog.Builder(this)
                .setTitle(R.string.dialog_title)
                .setViewStyle(PromptDialog.VIEW_STYLE_TITLEBAR_SKYBLUE)
                .setMessage("密码重置成功，返回登录界面")
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
        setContentView(R.layout.activity_findpassword);
        initHeaderTitle();
        initView();
        time = new TimeCount(120000, 1000);
        initEvent();
    }

    private void initHeaderTitle() {
        gobackimg = (ImageView) findViewById(R.id.id_goback);
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("忘记密码");
        gobackimg.setOnClickListener(this);
    }

    private void initView() {
        clearimg = (ImageView) findViewById(R.id.id_clear);
        showimg = (ImageView) findViewById(R.id.id_image_show); //显示密码
        showimg.setTag(0);
        Btngetcode = (Button) findViewById(R.id.id_btn_getcode);
        Btnreset = (Button) findViewById(R.id.id_btn_resetpsw);
        Etphonenumber = (EditText) findViewById(R.id.id_et_phonenumber);
        Etcode = (EditText) findViewById(R.id.id_et_code);
        Etnewpassword = (EditText) findViewById(R.id.id_et_newpassword);
        Btnreset.setEnabled(false);
    }
    private void initEvent() {

        clearimg.setOnClickListener(this);

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
                    Map<String, String> textParams = new HashMap<String, String>();
                    textParams.put("username", PhoneNo);
                    SendRequestUtils.PostDataFromService(textParams, NetConstants.REPAIRMAN_CAPTCHA, findHandler);
                }
            }
        });
        Etcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (TextUtils.isEmpty(Etcode.getText().toString()) || TextUtils.isEmpty(Etnewpassword.getText().toString())
                        ) {
                    Btnreset.setEnabled(false);
                } else {
                    Btnreset.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Etnewpassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (TextUtils.isEmpty(Etcode.getText().toString()) || TextUtils.isEmpty(Etnewpassword.getText().toString())
                        ) {
                    Btnreset.setEnabled(false);
                } else {
                    Btnreset.setEnabled(true);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Btnreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                verifycode = Etcode.getText().toString().trim();  //获取验证码
                newpassword = Etnewpassword.getText().toString().trim();  //获取密码
                //重置密码
                String validateURL = NetConstants.REPAIRMAN_RESET_PASSWD;
                Map<String, String> textParams = new HashMap<String, String>();
                textParams.put("username", PhoneNo);
                textParams.put("password", newpassword);
                textParams.put("code", verifycode);
                SendRequestUtils.PostDataFromService(textParams, validateURL, resettingHandler);
            }
        });

        showimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tag = (Integer) v.getTag();
                switch (tag) {
                    case 0:
                        showimg.setImageResource(R.drawable.show_password_primary);
                        Etnewpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        v.setTag(1);
                        break;
                    case 1:
                        showimg.setImageResource(R.drawable.show_password_gray);
                        Etnewpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        v.setTag(0);
                        break;
                }
            }
        });
    }

    private boolean isPhone(String phoneNo) {
        Pattern pattern = Pattern.compile("1[0-9]{10}");
        Matcher matcher = pattern.matcher(phoneNo);
        if (matcher.matches()) {
            return true;
        } else {
            new PromptDialog.Builder(this)
                    .setTitle(R.string.dialog_title)
                    .setViewStyle(PromptDialog.VIEW_STYLE_TITLEBAR_SKYBLUE)
                    .setMessage("您输入手机号码不正确")
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
        switch (view.getId()) {
            case R.id.id_goback:
                finish();
                break;
            case R.id.id_clear:
                Etphonenumber.setText("");
                break;
            default:
                break;

        }
    }

    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        @Override
        public void onTick(long millisUntilFinished) {
            Btngetcode.setClickable(false);
            Btngetcode.setText(millisUntilFinished / 1000 + "秒");
        }

        @Override
        public void onFinish() {
            Btngetcode.setText("获取验证码");
            Btngetcode.setClickable(true);
        }
    }

    private static class ResetHandler extends BaseHandler<Findpassword> {

        public ResetHandler(Findpassword object) {
            super(object);
        }

        @Override
        public void onSuccess(Findpassword object, Message msg) {
            object.ResetSuccess(msg);
        }

        @Override
        public void onFailture(Findpassword object) {
            super.onFailture(object);
            object.failure();
        }
    }

    private void ResetSuccess(Message msg) {
        try {
            Gson gson = new Gson();
            BaseModel model = gson.fromJson(msg.obj.toString(), BaseModel.class);
            if (2 == model.result_code) {
                //重新登陆
            } else {
                if (model.result.equals("success")) {
                    Registerrequest();
                } else {
                    failure(model.error);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class SendHandler extends BaseHandler<Findpassword> {

        public SendHandler(Findpassword object) {
            super(object);
        }

        @Override
        public void onSuccess(Findpassword object, Message msg) {
            object.Btngetcode.setText("获取验证码");
            object.Btngetcode.setClickable(true);
        }
    }
}
