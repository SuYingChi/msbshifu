package com.msht.master.Adapter;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.msht.master.Base.ListBaseAdapter;
import com.msht.master.Model.CityModel;
import com.msht.master.Model.EnterpriseModel;
import com.msht.master.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by hong on 2018/2/8.
 */

public class EnterpriseAdapter extends BaseAdapter {
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private ArrayList<HashMap<String, String>>  mList = new ArrayList<HashMap<String, String>>();
    public EnterpriseAdapter(Context context, ArrayList<HashMap<String, String>> List){
        mContext=context;
        this.mList=List;
        mLayoutInflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        if (mList.size()!=0&&mList!=null){
            return mList.size();
        }else {
            return 0;
        }
    }
    @Override
    public Object getItem(int position) {
        if (mList.size()!=0&&mList!=null){
            return mList.get(position);
        }else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int thisposition=position;
        Holder holder;
        if(convertView==null){
            holder = new Holder();
            convertView = mLayoutInflater.inflate(R.layout.item_enterprise_list, null);
            holder.tv_name = (TextView) convertView.findViewById(R.id.id_tv_companyname);
            holder.tv_code=(TextView)convertView.findViewById(R.id.id_tv_companycode);
            convertView.setTag(holder);
        }else{
            holder = (Holder) convertView.getTag();
        }
        holder.tv_code.setText(mList.get(position).get("company_code"));
        holder.tv_name.setText(mList.get(position).get("company_name"));
        return convertView;
    }
    class Holder{
        TextView tv_name;
        TextView tv_code;
    }
}
