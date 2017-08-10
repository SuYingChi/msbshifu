package com.msht.master.Adapter;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.jdsjlzx.recyclerview.LuRecyclerViewAdapter;
import com.msht.master.Base.ListBaseAdapter;
import com.msht.master.Model.PriceCategoryModel;
import com.msht.master.R;

/**
 * Created by hong on 2017/4/19.
 */

public class PriceBookAdapter extends ListBaseAdapter<PriceCategoryModel.PriceCategory> {
    private LuRecyclerViewAdapter luRecyclerViewAdapter;
    private LayoutInflater mLayoutInflater;
    private   SecondAdapter secondAdapter;
    public OnItemSelectListener listener;
    public void SetOnItemSelectListener(OnItemSelectListener listener){
        this.listener=listener;
    }
    public interface OnItemSelectListener{
        void ItemSelectClick(View view,int secondPosition,PriceCategoryModel.PriceCategory.ChildCategory model);
    }
    public PriceBookAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        PriceCategoryModel.PriceCategory mainCategory = mDataList.get(position);
        MainViewHolder viewHolder = (MainViewHolder) holder;
        viewHolder.tv_type_name.setText(mainCategory.name);
        secondAdapter = new SecondAdapter(mContext);
        luRecyclerViewAdapter = new LuRecyclerViewAdapter(secondAdapter);
        viewHolder.recycler_second_type.addItemDecoration(new SecondAdapter.SpaceItemDecoration(16));
        viewHolder.recycler_second_type.setAdapter(secondAdapter);
        viewHolder.recycler_second_type.setLayoutManager(new LinearLayoutManager(mContext));
        secondAdapter.addAll(mainCategory.child);
        secondAdapter.SetOnItemClickListener(new SecondAdapter.OnItemClickTypeListener() {
            @Override
            public void setOnItemClick(View view, int secondPosition, PriceCategoryModel.PriceCategory.ChildCategory model) {
                if(listener!=null){
                    listener.ItemSelectClick(view,secondPosition,model);
                }
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PriceBookAdapter.MainViewHolder(mLayoutInflater.inflate(R.layout.item_pricebook_main_type, parent, false));
    }
    private class MainViewHolder extends RecyclerView.ViewHolder {

        private final TextView tv_type_name;
        private final RecyclerView recycler_second_type;



        public MainViewHolder(View itemView) {
            super(itemView);
            tv_type_name = (TextView) itemView.findViewById(R.id.tv_type_name);
            recycler_second_type = (RecyclerView) itemView.findViewById(R.id.recycler_second_type);
        }
    }


    private static class SecondAdapter extends ListBaseAdapter<PriceCategoryModel.PriceCategory.ChildCategory>{
        private LayoutInflater mLayoutInflater;

        /**
         *
         * @param context
         *
         */
        private OnItemClickTypeListener listener;
        public interface OnItemClickTypeListener {
            void setOnItemClick(View view, int secondPosition,PriceCategoryModel.PriceCategory.ChildCategory model);
        }
        public void SetOnItemClickListener( OnItemClickTypeListener listener) {
            this.listener = listener;
        }
        public SecondAdapter(Context context) {
            mContext = context;
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            final PriceCategoryModel.PriceCategory.ChildCategory childCategory = mDataList.get(position);
            final PriceBookAdapter.SecondViewHolder viewHolder = (PriceBookAdapter.SecondViewHolder) holder;
            viewHolder.tv_secondtype.setText(childCategory.name);
            viewHolder.tv_secondtype.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PriceCategoryModel.PriceCategory.ChildCategory model=getDataList().get(position);
                    if(listener!=null){
                        listener.setOnItemClick(v,position,model);
                    }
                }
            });
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new PriceBookAdapter.SecondViewHolder(mLayoutInflater.inflate(R.layout.item_price_type,parent,false));
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
    private static class SecondViewHolder extends RecyclerView.ViewHolder {
        private final TextView tv_secondtype;
        public SecondViewHolder(View itemView) {
            super(itemView);
            tv_secondtype = (TextView) itemView.findViewById(R.id.id_second_type);
        }
    }

}
