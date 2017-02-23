package com.msht.master.Adapter;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.msht.master.Base.ListBaseAdapter;
import com.msht.master.Model.InformModel;
import com.msht.master.R;

/**
 * Created by hei on 2016/12/14.
 * 我的消息界面 消息适配器
 */

public class InformAdapter extends ListBaseAdapter<InformModel.InformDetailModel> {
    private LayoutInflater mLayoutInflater;
    public InformAdapter(Context context){
        mContext=context;
        mLayoutInflater = LayoutInflater.from(context);
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        InformModel.InformDetailModel informDetailModel = mDataList.get(position);
        InformViewHolder viewHolder = (InformViewHolder) holder;
        viewHolder.cn_info.setText(informDetailModel.info);
        viewHolder.cn_time.setText(informDetailModel.time);
        viewHolder.tv_type.setText(informDetailModel.type_info);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new InformViewHolder(mLayoutInflater.inflate(R.layout.item_inform_info, parent, false));
    }
    public class InformViewHolder extends RecyclerView.ViewHolder{
        public TextView cn_info;
        public TextView cn_time;
        private final TextView tv_type;

        public InformViewHolder(View itemView) {
            super(itemView);
            cn_info = (TextView) itemView.findViewById(R.id.id_tv_info);
            cn_time = (TextView) itemView.findViewById(R.id.id_time);
            tv_type = (TextView) itemView.findViewById(R.id.tv_type);
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
