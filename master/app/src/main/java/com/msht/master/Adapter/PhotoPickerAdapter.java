package com.msht.master.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;


import com.github.siyamed.shapeimageview.mask.PorterShapeImageView;
import com.msht.master.R;
import com.msht.master.Utils.BitmapUtil;

import java.util.ArrayList;

/**
 *
 */
public class PhotoPickerAdapter extends BaseAdapter {
    private ArrayList<String> listPath;
    public PhotoPickerAdapter(ArrayList<String> listPath) {
        this.listPath = listPath;
    }
    @Override
    public int getCount() {
        if (listPath.size() == 9) {
            return listPath.size();
        } else {
            return listPath.size() + 1;
        }
    }

    @Override
    public Object getItem(int position) {
        return listPath.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_gridview, parent, false);
            holder = new ViewHolder();
            holder.image = (PorterShapeImageView) convertView
                    .findViewById(R.id.id_repair_img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (position == listPath.size()) {
            holder.image.setImageResource(R.drawable.icon_addpic_unfocused);
            if (position == 9) {
                holder.image.setVisibility(View.GONE);
            }
        } else {
            holder.image.setImageBitmap(BitmapUtil.decodeSampledBitmapFromFile(listPath.get(position), 500, 500));
        }
        return convertView;
    }


    public static class ViewHolder {
        PorterShapeImageView image;
    }
}
