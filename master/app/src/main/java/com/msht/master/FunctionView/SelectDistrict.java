package com.msht.master.FunctionView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.msht.master.Adapter.DistrictAdapter;
import com.msht.master.Base.BaseHandler;
import com.msht.master.Common.CommonMethod;
import com.msht.master.Constants.NetConstants;
import com.msht.master.Constants.SPConstants;
import com.msht.master.Constants.VariableUtil;
import com.msht.master.Controls.FullyGridLayoutManager;
import com.msht.master.Controls.FullyLinearLayoutManager;
import com.msht.master.Controls.MyRecyclerView;
import com.msht.master.Model.DistrictModel;
import com.msht.master.R;
import com.msht.master.UIView.CustomDialog;
import com.msht.master.UIView.PromptDialog;
import com.msht.master.Utils.SendRequestUtils;
import com.msht.master.Utils.SharedPreferencesUtils;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class SelectDistrict extends AppCompatActivity {
    private MyRecyclerView mDistrict;
    private Button btn_ensure;
    private DistrictAdapter districtAdapter;
    private String token,mCityId="1";
    private ArrayList<Integer> selectedDistrict = new ArrayList<>();
    private ArrayList<String> DistrictName = new ArrayList<>();
    private CustomDialog customDialog;
    Handler regionhandler = new RegionHanlder(this);

    /**
     * 获取区域信息成功
     */
    private void getRegionSuccess(Message msg) {
        try {
            Gson gson = new Gson();
            DistrictModel districtModel = gson.fromJson(msg.obj.toString(), DistrictModel.class);
            int result_code = districtModel.result_code;
            String Results = districtModel.result;
            String Error = districtModel.error;
            if (result_code == 2) {
                CommonMethod.goLogin(new WeakReference<Activity>(this), 2);
            } else {
                if (Results.equals("success")) {
                    //获取信息成功
                    districtAdapter.addAll(districtModel.data);
                } else {
                    Toast.makeText(SelectDistrict.this, Error, Toast.LENGTH_SHORT)
                            .show();
                }
            }
        } catch (Exception e) {
            //数据解析失败
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_district);
        initHeardTitle();
        Intent data=getIntent();
        mCityId=data.getStringExtra("cityid");
        token = (String) SharedPreferencesUtils.getData(getApplicationContext(), SPConstants.TOKEN, "");
        customDialog = new CustomDialog(this, "正在加载");
        mDistrict=(MyRecyclerView)findViewById(R.id.id_district_view);
        btn_ensure=(Button)findViewById(R.id.id_btn_send);
        mDistrict.setLayoutManager(new LinearLayoutManager(this));
        districtAdapter = new DistrictAdapter(this);
        mDistrict.setAdapter(districtAdapter);
        initData();
        btn_ensure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String districtId = getDistrictId();
                String districtName=getDistrictName();
                if (matchjudge(districtId)){
                    Intent intent=new Intent();
                    intent.putExtra("districtId",districtId);
                    intent.putExtra("districtName",districtName);
                    setResult(4, intent);
                    finish();
                }
            }
        });
    }

    private boolean matchjudge(String districtId) {
        if (districtId.equals("")) {
            new PromptDialog.Builder(this)
                    .setTitle(R.string.dialog_title)
                    .setViewStyle(PromptDialog.VIEW_STYLE_TITLEBAR_SKYBLUE)
                    .setMessage("请选择接单区域")
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

    private void initHeardTitle() {
        findViewById(R.id.id_goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("可接单区域");
    }

    private void initData() {
        customDialog.show();
        String areaurl = NetConstants.SELECT_DISTRICT;
        String districtUrl = "";
        try {
            districtUrl = areaurl + "?city_id=" + URLEncoder.encode(mCityId, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        SendRequestUtils.GetDataFromService(districtUrl, regionhandler);

    }
    private String getDistrictId() {
        selectedDistrict.clear();
        VariableUtil.selectedPos.clear();
        List<DistrictModel.DistrictDetail> dataList = districtAdapter.getDataList();
        int pos=0;
        for (DistrictModel.DistrictDetail data : dataList) {
            if (data.isSelected == 1) {
                selectedDistrict.add(data.id);
                VariableUtil.selectedPos.add(pos);   //用于记录选择了哪个区域pos
            }else {
                VariableUtil.selectedPos.add(-1);
            }
            pos++;
        }
        StringBuilder result = new StringBuilder();
        boolean flag = false;
        for (int item : selectedDistrict) {
            if (flag) {
                result.append(",");
            } else {
                flag = true;
            }
            result.append(item);
        }
        return result.toString();
    }
    private String getDistrictName() {
        DistrictName.clear();
        List<DistrictModel.DistrictDetail> dataList = districtAdapter.getDataList();
        for (DistrictModel.DistrictDetail data : dataList) {
            if (data.isSelected == 1) {
                DistrictName.add(data.name);
            }
        }
        StringBuilder result = new StringBuilder();
        boolean flag = false;
        for (String item : DistrictName) {
            if (flag) {
                result.append(",");
            } else {
                flag = true;
            }
            result.append(item);
        }
        return result.toString();

    }
    private static class RegionHanlder extends BaseHandler<SelectDistrict> {

        public RegionHanlder(SelectDistrict object) {
            super(object);
        }

        @Override
        public void onSuccess(SelectDistrict object, Message msg) {
            object.getRegionSuccess(msg);
        }

        @Override
        public void onFinilly(SelectDistrict object) {
            object.customDialog.dismiss();
        }
    }
}
