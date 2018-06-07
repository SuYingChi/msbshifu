package com.msht.master.Adapter;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.siyamed.shapeimageview.mask.PorterShapeImageView;
import com.msht.master.Controls.FullScreenDialogFragment;
import com.msht.master.R;

import java.util.ArrayList;

/**
 * Created by hei on 2016/12/22.
 * 订单详情的照片展示的适配器
 */

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {
    private ArrayList<String> listPath = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public PhotoAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PhotoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_gridview, parent, false));
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.Click(v, position);
                }
                FullScreenDialogFragment.newInstance(mContext,listPath,position)
                        .show(((AppCompatActivity)mContext).getSupportFragmentManager(),"photoadapter");
            }
        });
        String imgurl = listPath.get(position);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(R.drawable.shape_ring_loading);
        requestOptions.placeholder(R.drawable.shape_ring_loading);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        requestOptions.skipMemoryCache(false);
        Glide.with(mContext).load(imgurl).apply(requestOptions).thumbnail(0.5f).into(holder.image);

    }

    public void SetImageUrlList(ArrayList<String> list) {
        this.listPath.clear();
        this.listPath = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return listPath.size();
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        PorterShapeImageView image;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            image = (PorterShapeImageView) itemView
                    .findViewById(R.id.id_repair_img);
        }
    }

    private OnItemClickListener listener;

    public void SetOnItemClickedListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void Click(View view, int position);
    }
}
