package com.msht.master.Adapter;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.msht.master.Base.ListBaseAdapter;
import com.msht.master.Model.OrderModel;
import com.msht.master.R;

/**
 * Created by hei on 2016/12/14.
 * 我的工作订单列表的适配器
 */

public class MyWorkOrderAdapter extends ListBaseAdapter<OrderModel.OrderDetailModel> {
    private LayoutInflater mLayoutInflater;
    public MyWorkOrderAdapter(Context context){
        mContext=context.getApplicationContext();
        mLayoutInflater = LayoutInflater.from(context);
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final OrderModel.OrderDetailModel model = mDataList.get(position);
        final MyWorkOrderViewHolder viewHolder = (MyWorkOrderViewHolder) holder;
        switch (model.status){
            case "3":
                viewHolder.tv_status.setText("待接单");
                viewHolder.iv_callphone.setVisibility(View.VISIBLE);
                break;
            case "4":
                viewHolder.tv_status.setText("已转单");
                viewHolder.iv_callphone.setVisibility(View.VISIBLE);
                break;
            case "5":
                viewHolder.tv_status.setText("服务中");
                viewHolder.iv_callphone.setVisibility(View.VISIBLE);
                break;
            case "6":
                viewHolder.tv_status.setText("待支付");
                viewHolder.iv_callphone.setVisibility(View.VISIBLE);
                break;
            case "7":
                viewHolder.tv_status.setText("待评价");
                viewHolder.iv_callphone.setVisibility(View.INVISIBLE);
                break;
            case "8":
                viewHolder.tv_status.setText("已完成");
                viewHolder.iv_callphone.setVisibility(View.INVISIBLE);
                break;
        }
        viewHolder.tv_order_num.setText(model.id);
        viewHolder.tv_order_type.setText(model.category);
        viewHolder.tv_address.setText(model.address);
        viewHolder.tv_time.setText(model.time);


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyWorkOrderViewHolder(mLayoutInflater.inflate(R.layout.item_work_order, parent, false));
    }
    private class MyWorkOrderViewHolder extends RecyclerView.ViewHolder{
        private final TextView tv_order_num;
        private final TextView tv_time;
        private final TextView tv_order_type;
        private final TextView tv_address;
        private final ImageView iv_callphone;
        private final TextView tv_detail;
        private final TextView tv_status;

        public MyWorkOrderViewHolder(View itemView) {
            super(itemView);
            tv_order_num = (TextView) itemView.findViewById(R.id.tv_order_num);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_order_type = (TextView) itemView.findViewById(R.id.tv_order_type);
            tv_status = (TextView) itemView.findViewById(R.id.tv_status);
            tv_address = (TextView) itemView.findViewById(R.id.tv_address);
            iv_callphone = (ImageView) itemView.findViewById(R.id.iv_callphone);
            tv_detail = (TextView) itemView.findViewById(R.id.tv_detail);
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
