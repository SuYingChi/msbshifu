package com.msht.master.Adapter;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.msht.master.Base.ListBaseAdapter;
import com.msht.master.Model.QualityAssuranceModel;
import com.msht.master.R;

/**
 * Created by hei on 2016/12/23.
 * 质保金的适配器
 */

public class QualityAssuranceAdapter extends ListBaseAdapter<QualityAssuranceModel.QualityAssuranceDetail> {
    private LayoutInflater mLayoutInflater;

    public QualityAssuranceAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        QualityAssuranceModel.QualityAssuranceDetail qualityAssuranceDetail = mDataList.get(position);
        QualityAssuranceViewHolder viewHolder = (QualityAssuranceViewHolder) holder;
        viewHolder.tv_time.setText(qualityAssuranceDetail.time);
        viewHolder.tv_info.setText(qualityAssuranceDetail.info);
        viewHolder.tv_amount.setText(qualityAssuranceDetail.amount+"元");
       switch (qualityAssuranceDetail.type){
           case 1:
               viewHolder.tv_type.setText("增加");
               break;
           case 2:
               viewHolder.tv_type.setText("扣除");
               break;
       }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new QualityAssuranceViewHolder(mLayoutInflater.inflate(R.layout.item_quality_assurance, parent, false));
    }

    public class QualityAssuranceViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_type;
        public TextView tv_time;
        public TextView tv_info;
        public TextView tv_amount;


        public QualityAssuranceViewHolder(View itemView) {
            super(itemView);
            tv_type = (TextView) itemView.findViewById(R.id.tv_type);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_info = (TextView) itemView.findViewById(R.id.tv_info);
            tv_amount = (TextView) itemView.findViewById(R.id.tv_amount);
        }
    }

    public static class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.bottom = space;

        }
    }
}
