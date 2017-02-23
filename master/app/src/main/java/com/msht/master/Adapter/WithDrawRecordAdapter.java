package com.msht.master.Adapter;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.msht.master.Base.ListBaseAdapter;
import com.msht.master.Model.WithDrawRecordModel;
import com.msht.master.R;

/**
 * Created by hei on 2016/12/16.
 * 提现记录的适配器
 */

public class WithDrawRecordAdapter extends ListBaseAdapter<WithDrawRecordModel.WithDrawRecodDetail> {
    private LayoutInflater mLayoutInflater;
    public WithDrawRecordAdapter(Context context){
        mContext=context;
        mLayoutInflater = LayoutInflater.from(context);
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        WithDrawRecordModel.WithDrawRecodDetail withDrawRecodDetail = mDataList.get(position);
        WithDrawRecordViewHolder viewHolder = (WithDrawRecordViewHolder) holder;
        viewHolder.item_record_withdraw_amount.setText("￥"+withDrawRecodDetail.amount);
        viewHolder.item_record_withdraw_time.setText(withDrawRecodDetail.time);
        viewHolder.tv_card_no.setText(withDrawRecodDetail.bankcard);
        switch (withDrawRecodDetail.status){
            case 1:
                viewHolder.item_record_withdraw_status.setText("已申请");
                break;
            case 2:
                viewHolder.item_record_withdraw_status.setText("已转账");
                break;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WithDrawRecordViewHolder(mLayoutInflater.inflate(R.layout.item_record_withdraw, parent, false));
    }
    public class WithDrawRecordViewHolder extends RecyclerView.ViewHolder{
        private final TextView item_record_withdraw_amount;
        private final TextView item_record_withdraw_time;
        private final TextView item_record_withdraw_status;
        private final TextView tv_card_no;

        public WithDrawRecordViewHolder(View itemView) {
            super(itemView);
            item_record_withdraw_amount = (TextView) itemView.findViewById(R.id.item_record_withdraw_amount);
            item_record_withdraw_time = (TextView) itemView.findViewById(R.id.item_record_withdraw_time);
            item_record_withdraw_status = (TextView) itemView.findViewById(R.id.item_record_withdraw_status);
            tv_card_no = (TextView) itemView.findViewById(R.id.tv_card_no);
        }
    }
    public static class SpaceItemDecoration extends RecyclerView.ItemDecoration{
        private int space;
        public SpaceItemDecoration(int space){this.space=space;}

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.bottom=space;

        }
    }
}
