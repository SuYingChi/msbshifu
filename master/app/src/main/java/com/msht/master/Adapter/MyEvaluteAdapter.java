package com.msht.master.Adapter;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.msht.master.Base.ListBaseAdapter;
import com.msht.master.Model.EvaluteModel;
import com.msht.master.R;
import com.msht.master.UIView.CircleImageView;

/**
 * Created by hei on 2016/12/14.
 * 我的评价的适配器
 */

public class MyEvaluteAdapter extends ListBaseAdapter<EvaluteModel.EvaluteDetailModel> {
    private LayoutInflater mLayoutInflater;

    public MyEvaluteAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        EvaluteModel.EvaluteDetailModel item = mDataList.get(position);
        MyEvaluteViewHolder viewHolder = (MyEvaluteViewHolder) holder;
        switch (item.eval_score) {
            case 1:
                viewHolder.img_status.setImageResource(R.drawable.start_one_h);
                break;
            case 2:
                viewHolder.img_status.setImageResource(R.drawable.start_two_h);
                break;
            case 3:
                viewHolder.img_status.setImageResource(R.drawable.start_three_h);
                break;
            case 4:
                viewHolder.img_status.setImageResource(R.drawable.start_four_h);
                break;
            case 5:
                viewHolder.img_status.setImageResource(R.drawable.start_five_h);
                break;
        }
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(R.drawable.default_portrait);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        requestOptions.skipMemoryCache(true);
        Glide.with(mContext).load(item.user_avatar).apply(requestOptions)
                .into(viewHolder.portrait);
        viewHolder.cn_username.setText(item.username);
        viewHolder.cn_eval_info.setText(item.eval_info);
        viewHolder.cn_time.setText(item.time);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyEvaluteViewHolder(mLayoutInflater.inflate(R.layout.item_my_evalute, parent, false));
    }

    public class MyEvaluteViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView portrait;
        public ImageView img_status;
        public TextView cn_username;
        public TextView cn_time;
        public TextView cn_eval_info;

        public MyEvaluteViewHolder(View itemView) {
            super(itemView);
            portrait = (CircleImageView) itemView.findViewById(R.id.id_portrait);
            img_status = (ImageView) itemView.findViewById(R.id.id_img_status);
            cn_username = (TextView) itemView.findViewById(R.id.id_tv_username);
            cn_eval_info = (TextView) itemView.findViewById(R.id.id_evalute_info);
            cn_time = (TextView) itemView.findViewById(R.id.id_evalute_time);
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
