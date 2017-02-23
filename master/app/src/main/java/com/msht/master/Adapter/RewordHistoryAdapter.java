package com.msht.master.Adapter;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.msht.master.Base.ListBaseAdapter;
import com.msht.master.Model.RewardModel;
import com.msht.master.R;

/**
 * Created by hei on 2016/12/14.
 * 奖励历史列表的适配器
 */

public class RewordHistoryAdapter extends ListBaseAdapter<RewardModel.RewardDetail> {
    private LayoutInflater mLayoutInflater;
    public RewordHistoryAdapter(Context context){
        mContext=context;
        mLayoutInflater = LayoutInflater.from(context);
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RewardModel.RewardDetail model = mDataList.get(position);
        RewordHistoryViewHolder viewHolder = (RewordHistoryViewHolder) holder;
        viewHolder.tv_reword_amount.setText("￥"+model.amount);
        viewHolder.tv_reword_type.setText(model.info);
        viewHolder.tv_time.setText(model.time);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RewordHistoryViewHolder(mLayoutInflater.inflate(R.layout.item_reword_history, parent, false));
    }
    public class RewordHistoryViewHolder extends RecyclerView.ViewHolder{

        private final TextView tv_reword_type;
        private final TextView tv_reword_amount;
        private final TextView tv_time;

        public RewordHistoryViewHolder(View itemView) {
            super(itemView);
            tv_reword_type = (TextView) itemView.findViewById(R.id.tv_reword_type);
            tv_reword_amount = (TextView) itemView.findViewById(R.id.tv_reword_amount);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
        }
    }
    public static class SpaceItemDecoration extends RecyclerView.ItemDecoration{
        private int space;
        public SpaceItemDecoration(int space){this.space=space;}

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if(parent.getChildAdapterPosition(view)==0){
                outRect.top=space;
            }
            outRect.bottom=space;

        }
    }
}
