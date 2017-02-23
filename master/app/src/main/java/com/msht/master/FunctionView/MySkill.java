package com.msht.master.FunctionView;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.msht.master.Base.BaseHandler;
import com.msht.master.Common.CommonMethod;
import com.msht.master.Constants.NetConstants;
import com.msht.master.Constants.SPConstants;
import com.msht.master.Controls.FullyLinearLayoutManager;
import com.msht.master.Adapter.SkilllMainTypeAdapter;
import com.msht.master.Model.BaseModel;
import com.msht.master.Model.DistrictModel;
import com.msht.master.Model.RepairCategoryModel;
import com.msht.master.R;
import com.msht.master.UIView.CustomDialog;
import com.msht.master.UIView.PromptDialog;
import com.msht.master.Utils.AppToast;
import com.msht.master.Utils.SendRequestUtils;
import com.msht.master.Utils.SharedPreferencesUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hei on 2016/12/30.
 */

public class MySkill extends AppCompatActivity {

    private CustomDialog customDialog;
    private String token;
    private RecyclerView mRecyclerView;
    private Button btn_send;
    private ArrayList<Integer> selectedSkill = new ArrayList<>();

    private SkilllMainTypeAdapter skilllMainTypeAdapter;

    private Handler mySkillHandler = new SkillHandler(this);
    private Handler reviseSkillHandler = new ReviseSkillHandler(this);
    private TextView tv_isvalid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myskill);
        initHeaderTitle();
        token = (String) SharedPreferencesUtils.getData(getApplicationContext(), SPConstants.TOKEN, "");
        customDialog = new CustomDialog(this, "正在加载");
        initView();
        getSkillData();
        initEvent();
    }

    private void initEvent() {
        skilllMainTypeAdapter.SetOnChagedListener(new SkilllMainTypeAdapter.OnChangedListener() {
            @Override
            public void Changed(View view, int mainPosition, int secondPosition) {
                btn_send.setVisibility(View.VISIBLE);
            }
        });
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                String skillid = result.toString();
                if (TextUtils.isEmpty(skillid)) {
                    AppToast.makeShortToast(getApplicationContext(), "您没有选择任何技能");
                    return;
                } else {
                    request(skillid);
                }
            }
        });
    }

    private void request(final String skillid) {
        new PromptDialog.Builder(this)
                .setTitle(R.string.dialog_title)
                .setViewStyle(PromptDialog.VIEW_STYLE_TITLEBAR_SKYBLUE)
                .setMessage("您是否确认修改技能？审核期间将不能修改技能哦~")
                .setButton1("取消", new PromptDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setButton2("确定", new PromptDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        customDialog.show();
                        String validateURL = NetConstants.MODIFY_CATEGORY;
                        Map<String, String> textParams = new HashMap<String, String>();
                        textParams.put("token", token);
                        textParams.put("categories", skillid);
                        SendRequestUtils.PostDataFromService(textParams, validateURL, reviseSkillHandler);
                        dialog.dismiss();
                    }

                })
                .show();
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_my_skill);
        mRecyclerView.setLayoutManager(new FullyLinearLayoutManager(getApplicationContext()));
        skilllMainTypeAdapter = new SkilllMainTypeAdapter(getApplicationContext());
        mRecyclerView.setAdapter(skilllMainTypeAdapter);
        btn_send = (Button) findViewById(R.id.btn_send);
        tv_isvalid = (TextView) findViewById(R.id.tv_isvalid);
    }

    private void getSkillData() {
        customDialog.show();
        //全部维修目录 包含师傅选择
        String skillURL = NetConstants.REPAIR_CATEGORY_REPAIRMAN_ALL;
        Map<String, String> textParams = new HashMap<String, String>();
        textParams.put("token", token);
        SendRequestUtils.PostDataFromService(textParams, skillURL, mySkillHandler);
    }

    private void initHeaderTitle() {
        findViewById(R.id.id_goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView) findViewById(R.id.tv_title)).setText("我的技能");
    }

    private static class SkillHandler extends BaseHandler<MySkill> {

        public SkillHandler(MySkill object) {
            super(object);
        }

        @Override
        public void onSuccess(MySkill object, Message msg) {
            object.getSkillDataSuccess(msg);
        }

        @Override
        public void onFinilly(MySkill object) {
            object.customDialog.dismiss();
        }
    }

    private void getSkillDataSuccess(Message msg) {
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
                    boolean validStatus = getValidStatus(data);
                    if(validStatus){
                        tv_isvalid.setVisibility(View.VISIBLE);
                        btn_send.setEnabled(false);
                    }else{
                        tv_isvalid.setVisibility(View.GONE);
                        btn_send.setEnabled(true);
                    }
                    skilllMainTypeAdapter.addAll(data);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean getValidStatus(ArrayList<RepairCategoryModel.MainCategory> dataList) {
        for (RepairCategoryModel.MainCategory data : dataList) {
            ArrayList<RepairCategoryModel.MainCategory.ChildCategory> childList = data.child;
            for (RepairCategoryModel.MainCategory.ChildCategory child : childList) {
                if(child.selected==1){
                    if(child.valid==0){
                        return true;
                    }
                }
                if(child.selected==3){
                    return true;
                }
            }
        }
        return false;
    }

    private static class ReviseSkillHandler extends BaseHandler<MySkill> {

        public ReviseSkillHandler(MySkill object) {
            super(object);
        }

        @Override
        public void onSuccess(MySkill object, Message msg) {
            object.ReviseSkillSuccess(msg);
        }

        @Override
        public void onFinilly(MySkill object) {
            object.customDialog.dismiss();
        }
    }

    private void ReviseSkillSuccess(Message msg) {
        try {
            Gson gson = new Gson();
            BaseModel model = gson.fromJson(msg.obj.toString(), BaseModel.class);
            if (2 == model.result_code) {
                //重新登陆
                CommonMethod.goLogin(new WeakReference<Activity>(this),1);
            } else {
                if (model.result.equals("success")) {
                    new PromptDialog.Builder(this)
                            .setTitle(R.string.dialog_title)
                            .setViewStyle(PromptDialog.VIEW_STYLE_TITLEBAR_SKYBLUE)
                            .setMessage("您的技能修改已提交,请等待审核")
                            .setButton1("确定", new PromptDialog.OnClickListener() {

                                @Override
                                public void onClick(Dialog dialog, int which) {
                                    dialog.dismiss();
                                    finish();

                                }
                            }).show();
                } else {
                    //失败
                    CommonMethod.faifure(new WeakReference<Activity>(this),model.error);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
