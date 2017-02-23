package com.msht.master.Adapter;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.mask.PorterShapeImageView;
import com.msht.master.Base.ListBaseAdapter;
import com.msht.master.Model.BankCardModel;
import com.msht.master.R;

/**
 * Created by hei on 2016/12/29.
 * 银行卡的适配器
 */

public class BankCardAdapter extends ListBaseAdapter<BankCardModel.BankCardDetail> {
    private LayoutInflater mLayoutInflater;
    private OnItemClickListener listener;

    public BankCardAdapter(Context context) {
        mContext =context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        BankCardModel.BankCardDetail bankCardDetail = mDataList.get(position);
        BankCardViewHolder viewHolder = (BankCardViewHolder) holder;
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(v, position);
                }
            }
        });
        viewHolder.tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onDeleteClick(v,position);
                }
            }
        });
        //viewHolder.tv_bank.setText(bankCardDetail.bank);
        viewHolder.tv_card.setText("**** **** **** " + bankCardDetail.card);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BankCardViewHolder(mLayoutInflater.inflate(R.layout.item_bank_card, parent, false));
    }

    public class BankCardViewHolder extends RecyclerView.ViewHolder {
        //public TextView tv_bank;
        public TextView tv_card;
        public TextView tv_delete;

        public BankCardViewHolder(View itemView) {
            super(itemView);
            //tv_bank = (TextView) itemView.findViewById(R.id.id_tv_info);
            tv_card = (TextView) itemView.findViewById(R.id.id_tv_bankcard);
            tv_delete = (TextView) itemView.findViewById(R.id.tv_delete);

        }
    }

    public static class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = space;
            }
            outRect.bottom = space;

        }
    }

    public void SetOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onClick(View view, int position);

        void onDeleteClick(View view, int position);
    }
}
