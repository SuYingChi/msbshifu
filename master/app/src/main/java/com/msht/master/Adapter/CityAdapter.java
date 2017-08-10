package com.msht.master.Adapter;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.msht.master.Base.ListBaseAdapter;
import com.msht.master.Model.CityModel;
import com.msht.master.Model.RewardModel;
import com.msht.master.R;

/**
 * Created by hong on 2017/4/6.
 */

public class CityAdapter extends ListBaseAdapter<CityModel.CityDetail> {
    private LayoutInflater mLayoutInflater;
    public CityAdapter(Context context){
        mContext=context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CityModel.CityDetail model = mDataList.get(position);
        CityViewHolder viewHolder = (CityViewHolder) holder;
        viewHolder.tv_name.setText(model.name);
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CityAdapter.CityViewHolder(mLayoutInflater.inflate(R.layout.item_city_list, parent, false));
    }
    public class CityViewHolder extends RecyclerView.ViewHolder{
        private final TextView tv_name;
        public CityViewHolder(View itemView) {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.id_tv_city);
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
