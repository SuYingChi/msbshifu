package com.msht.master.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.msht.master.Base.ListBaseAdapter;
import com.msht.master.Model.DistrictModel;
import com.msht.master.R;

/**
 * Created by hei on 2017/1/5.
 * 地区的适配器
 */

public class DistrictAdapter extends ListBaseAdapter<DistrictModel.DistrictDetail> {
    private LayoutInflater mLayoutInflater;
    public DistrictAdapter(Context context){
        mContext=context;
        mLayoutInflater = LayoutInflater.from(context);
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final DistrictModel.DistrictDetail districtDetail = mDataList.get(position);
        final DistrictViewHolder viewHolder = (DistrictViewHolder) holder;
        viewHolder.cb_district.setText(districtDetail.name);
        viewHolder.cb_district.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                districtDetail.isSelected=isChecked?1:0;
                viewHolder.cb_district.setTextColor(isChecked? mContext.getResources().getColor(R.color.colorPrimary):mContext.getResources().getColor(R.color.desc));
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DistrictViewHolder(mLayoutInflater.inflate(R.layout.item_district, parent, false));
    }
    public class DistrictViewHolder extends RecyclerView.ViewHolder{


        private final CheckBox cb_district;

        public DistrictViewHolder(View itemView) {
            super(itemView);
            cb_district = (CheckBox) itemView.findViewById(R.id.cb_district);
        }
    }
}
