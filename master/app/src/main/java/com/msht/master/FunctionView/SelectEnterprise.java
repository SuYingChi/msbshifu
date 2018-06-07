package com.msht.master.FunctionView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.recyclerview.LuRecyclerView;
import com.github.jdsjlzx.recyclerview.LuRecyclerViewAdapter;
import com.google.gson.Gson;
import com.msht.master.Adapter.CityAdapter;
import com.msht.master.Adapter.EnterpriseAdapter;
import com.msht.master.Base.BaseHandler;
import com.msht.master.Common.CommonMethod;
import com.msht.master.Constants.NetConstants;
import com.msht.master.Constants.SPConstants;
import com.msht.master.Model.CityModel;
import com.msht.master.Model.EnterpriseModel;
import com.msht.master.R;
import com.msht.master.UIView.CustomDialog;
import com.msht.master.UIView.ListViewForScrollView;
import com.msht.master.UIView.PromptDialog;
import com.msht.master.Utils.SendRequestUtils;
import com.msht.master.Utils.SharedPreferencesUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SelectEnterprise extends AppCompatActivity {
    private Button   btn_ensure;
    private EditText et_epno;
    private ListViewForScrollView mListView;
    private EnterpriseAdapter mAdapter;
    private String token,company_code;
    private ArrayList<HashMap<String, String>>  mList = new ArrayList<HashMap<String, String>>();
    private Context mContext;
    private CustomDialog customDialog;
    Handler enterprisehandler = new EnterpriseHanlder(this);
    private void getEnterpriseSuccess(Message msg) {
        try {
            JSONObject object = new JSONObject(msg.obj.toString());
            String Results=object.optString("result");
            String Error = object.optString("error");
            int result_code =object.optInt("result_code");
            JSONArray jsonArray =object.optJSONArray("data");
            if (result_code == 2) {
                CommonMethod.goLogin(new WeakReference<Activity>(this), 2);
            } else {
                if (Results.equals("success")) {
                    //获取信息成功
                    if (jsonArray.length()!=0&&jsonArray!=null){
                        mList.clear();
                        initListData(jsonArray);
                    }else {
                        mList.clear();
                        mAdapter.notifyDataSetChanged();
                        showNotice("输入有误，重新输入");
                    }
                } else {
                    Toast.makeText(mContext, Error, Toast.LENGTH_SHORT)
                            .show();
                }
            }
        } catch (Exception e) {
            //数据解析失败
        }
    }
    private void initListData(JSONArray jsonArray) {
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String company_name= jsonObject.getString("company_name");
                String company_code = jsonObject.getString("company_code");
                String ep_id = jsonObject.getString("ep_id");
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("ep_id", ep_id);
                map.put("company_code", company_code);
                map.put("company_name", company_name);
                mList.add(map);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        if (mList.size()!=0){
            mAdapter.notifyDataSetChanged();
        }
    }
    private void showNotice(String s) {
        new PromptDialog.Builder(this)
                .setTitle(R.string.dialog_title)
                .setViewStyle(PromptDialog.VIEW_STYLE_TITLEBAR_SKYBLUE)
                .setMessage(s)
                .setButton1("确定", new PromptDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_enterprise);
        mContext=this;
        token= (String) SharedPreferencesUtils.getData(getApplicationContext(), SPConstants.TOKEN,"");
        customDialog = new CustomDialog(this, "正在加载");
        initHeaderTile();
        initView();
    }
    private void initData() {
        customDialog.show();
        String validateURL = NetConstants.Enterprise_List;
        Map<String, String> textParams = new HashMap<String, String>();
        textParams.put("token", token);
        textParams.put("company_code", company_code);
        SendRequestUtils.PostDataFromService(textParams,validateURL,enterprisehandler);
    }
    private void initHeaderTile() {
        findViewById(R.id.id_goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("公司编号");
    }
    private void initView() {
        btn_ensure=(Button)findViewById(R.id.id_btn_send);
        et_epno=(EditText)findViewById(R.id.id_et_epNo);
        mListView=(ListViewForScrollView)findViewById(R.id.recycler_view);
        mAdapter = new EnterpriseAdapter(this,mList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String EP_ID=mList.get(position).get("ep_id");
                String company_code=mList.get(position).get("company_code");
                Intent detail = new Intent();
                detail.putExtra("ep_id", EP_ID);
                detail.putExtra("company_code",company_code);
                setResult(5, detail);
                finish();
            }
        });
        btn_ensure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                company_code=et_epno.getText().toString().trim();
                if (matchCode(company_code)){
                    initData();
                }
            }
        });
        et_epno.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length()>0){
                    btn_ensure.setVisibility(View.VISIBLE);
                }else {
                    btn_ensure.setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    private boolean matchCode(String company_code) {
        if (TextUtils.isEmpty(company_code)){
            Toast.makeText(mContext, "请填写企业编号", Toast.LENGTH_SHORT).show();
            return false;
        }else {
            return true;
        }
    }
    private static class EnterpriseHanlder extends BaseHandler<SelectEnterprise> {
        public EnterpriseHanlder(SelectEnterprise object) {
            super(object);
        }
        @Override
        public void onSuccess(SelectEnterprise object, Message msg) {
            object.getEnterpriseSuccess(msg);
        }
        @Override
        public void onFinilly(SelectEnterprise object) {
            object.customDialog.dismiss();
        }
    }
}
