package com.msht.master.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.mask.PorterShapeImageView;
import com.msht.master.Adapter.PhotoAdapter;
import com.msht.master.Base.ListBaseAdapter;
import com.msht.master.Controls.FullyGridLayoutManager;
import com.msht.master.Model.RepairCategoryModel;
import com.msht.master.R;

import java.util.ArrayList;

/**
 * Created by hei on 2016/12/30.
 * 技能列表的适配器
 */

public class SkilllMainTypeAdapter extends ListBaseAdapter<RepairCategoryModel.MainCategory> {
    private LayoutInflater mLayoutInflater;
    private boolean isValid=false;//是否正在审核中

    public SkilllMainTypeAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder,final int position) {
        isValid=getValidStatus(mDataList);
        RepairCategoryModel.MainCategory mainCategory = mDataList.get(position);
        MainViewHolder viewHolder = (MainViewHolder) holder;
        switch (mainCategory.id){
            case 1:
                viewHolder.iv_type.setImageResource(R.drawable.sanitary_appliance);
                break;
            case 2:
                viewHolder.iv_type.setImageResource(R.drawable.electronic_devices);
                break;
            case 3:
                viewHolder.iv_type.setImageResource(R.drawable.lanterns_h);
                break;
            case 4:
                viewHolder.iv_type.setImageResource(R.drawable.other_repair);
                break;
        }
        viewHolder.tv_type_name.setText(mainCategory.name);
        SecondAdapter secondAdapter = new SecondAdapter(mContext,isValid);
        secondAdapter.SetOnItemCheckChangedListener(new SecondAdapter.OnItemCheckChangedListener() {
            @Override
            public void CheckChanged(View view, int secondPosition) {
                if(listener!=null){
                    listener.Changed(view,position,secondPosition);
                }
            }
        });
        viewHolder.recycler_second_type.setLayoutManager(new FullyGridLayoutManager(mContext,4));
        viewHolder.recycler_second_type.setAdapter(secondAdapter);
        secondAdapter.addAll(mainCategory.child);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MainViewHolder(mLayoutInflater.inflate(R.layout.item_skill_main_type, parent, false));
    }

    private class MainViewHolder extends RecyclerView.ViewHolder {


        private final ImageView iv_type;
        private final TextView tv_type_name;
        private final RecyclerView recycler_second_type;

        public MainViewHolder(View itemView) {
            super(itemView);
            iv_type = (ImageView) itemView.findViewById(R.id.iv_type);
            tv_type_name = (TextView) itemView.findViewById(R.id.tv_type_name);
            recycler_second_type = (RecyclerView) itemView.findViewById(R.id.recycler_second_type);
        }
    }

    public OnChangedListener listener;
    public void SetOnChagedListener(OnChangedListener listener){
        this.listener=listener;
    }

   public interface OnChangedListener{
       void Changed(View view,int mainPosition,int secondPosition);
   }


    /**
     * 次级列表的适配器
     */
    private static class SecondAdapter extends ListBaseAdapter<RepairCategoryModel.MainCategory.ChildCategory>{
        private LayoutInflater mLayoutInflater;
        private boolean isValid;

        /**
         *
         * @param context
         * @param isValid 指示是否正在审核
         */
        public SecondAdapter(Context context,boolean isValid) {
            this.isValid=isValid;
            mContext = context;
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            final RepairCategoryModel.MainCategory.ChildCategory childCategory = mDataList.get(position);
            final SecondViewHolder viewHolder = (SecondViewHolder) holder;
            viewHolder.cb_skill.setText(childCategory.name);
            viewHolder.cb_skill.setChecked(childCategory.selected==1);
            //viewHolder.cb_skill.setTextColor(childCategory.selected==1? mContext.getResources().getColor(R.color.colorPrimary):mContext.getResources().getColor(R.color.desc));
            viewHolder.cb_skill.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //设置是否选中状态
                    childCategory.selected=isChecked?1:0;
                    //设置checkbox的颜色
                    //viewHolder.cb_skill.setTextColor(isChecked? mContext.getResources().getColor(R.color.colorPrimary):mContext.getResources().getColor(R.color.desc));
                    if(listener!=null){
                        listener.CheckChanged(buttonView,position);
                    }
                }
            });
            //正在审核时设置checkbox不可用 颜色为灰色
            viewHolder.cb_skill.setEnabled(!isValid);
            if(isValid){
                viewHolder.cb_skill.setTextColor(Color.GRAY);
            }
        }
        private OnItemCheckChangedListener listener;

        public void SetOnItemCheckChangedListener(OnItemCheckChangedListener listener) {
            this.listener = listener;
        }

        public interface OnItemCheckChangedListener {
            void CheckChanged(View view, int secondPosition);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SecondViewHolder(mLayoutInflater.inflate(R.layout.item_skill_second_type,parent,false));
        }
    }
    private static class SecondViewHolder extends RecyclerView.ViewHolder {

        private final CheckBox cb_skill;

        public SecondViewHolder(View itemView) {
            super(itemView);
            cb_skill = (CheckBox) itemView.findViewById(R.id.cb_skill);
        }
    }

    /**
     * 获取是否正在审核状态
     * @param dataList
     * @return true正在审核
     */
    private  boolean getValidStatus(ArrayList<RepairCategoryModel.MainCategory> dataList) {
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
}
