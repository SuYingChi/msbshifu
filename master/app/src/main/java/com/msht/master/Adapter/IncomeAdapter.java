package com.msht.master.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.expandable.view.MyExpandableView;
import com.msht.master.Base.ListBaseAdapter;
import com.msht.master.Constants.SPConstants;
import com.msht.master.Model.IncomeModel;
import com.msht.master.R;
import com.msht.master.Utils.SharedPreferencesUtils;


/**
 * Created by hei on 2016/12/15.
 * 我的钱包界面 收入的适配器
 */

public class IncomeAdapter extends ListBaseAdapter<IncomeModel.IncomeDetail> {
    private LayoutInflater mLayoutInflater;

    public IncomeAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        IncomeModel.IncomeDetail item = mDataList.get(position);
        IncomeViewHolder viewHolder = (IncomeViewHolder) holder;
        viewHolder.item_expand.SetOrderNumber(item.order_no);
        viewHolder.item_expand.SetFinishTime(item.time);
        viewHolder.item_expand.SetRealAmount(BigDecinmals(item.real_amount) + "元");
        viewHolder.item_expand.SetMainType(item.parent_type_name);
        viewHolder.item_expand.SetorderStatus(item.order_status);
        switch (item.order_type) {
            case 1:
                viewHolder.item_expand.setImage(R.drawable.sanitary_appliance);
                break;
            case 2:
                viewHolder.item_expand.setImage(R.drawable.electronic_devices);
                break;
            case 3:
                viewHolder.item_expand.setImage(R.drawable.lanterns_h);
                break;
            case 4:
                viewHolder.item_expand.setImage(R.drawable.other_repair);
                break;
        }
        viewHolder.item_expand.SetSecondType(item.type_name);
        viewHolder.tv_plateform_fee.setText(item.plateform_fee+"元");
        viewHolder.tv_quality_assurance_fee.setText(item.quality_assurance_fee+"元");
        viewHolder.tv_total_amount.setText(item.total_amount+"元");
        viewHolder.tv_reward_amount.setText(item.reward_amount+"元");
        String ep_id=(String) SharedPreferencesUtils.getData(mContext, SPConstants.EP_ID,"-1");
        if (ep_id.equals("-1")||ep_id.equals("0")){
            viewHolder.quality_assurance_layout.setVisibility(View.VISIBLE);
        }else {
            viewHolder.quality_assurance_layout.setVisibility(View.GONE);
        }
    }
    private double BigDecinmals(double totals) {
        long lon = Math.round(totals * 100);
        double tot = lon / 100.0;
        return tot;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new IncomeViewHolder(mLayoutInflater.inflate(R.layout.item_my_wallet_main, parent, false));
    }

    private class IncomeViewHolder extends RecyclerView.ViewHolder {

        private final MyExpandableView item_expand;
        private final TextView tv_quality_assurance_fee;
        private final TextView tv_plateform_fee;
        private final TextView tv_total_amount;
        private final TextView tv_reward_amount;
        private final View     quality_assurance_layout;

        public IncomeViewHolder(View itemView) {
            super(itemView);
            item_expand = (MyExpandableView) itemView.findViewById(R.id.item_expand);
            View inflate = mLayoutInflater.inflate(R.layout.item_my_wallet_detail, null, false);
            tv_quality_assurance_fee = (TextView) inflate.findViewById(R.id.tv_quality_assurance_fee);
            tv_plateform_fee = (TextView) inflate.findViewById(R.id.tv_plateform_fee);
            tv_total_amount = (TextView) inflate.findViewById(R.id.tv_total_amount);
            tv_reward_amount=(TextView) inflate.findViewById(R.id.id_reward_amount);
            quality_assurance_layout=inflate.findViewById(R.id.assurance_fee_layout);
            item_expand.addContentView(inflate);

        }
    }
}
