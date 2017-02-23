package com.msht.master.Adapter;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.msht.master.Base.ListBaseAdapter;
import com.msht.master.Model.CertificateModel;
import com.msht.master.Model.InformModel;
import com.msht.master.Model.MyDataModel;
import com.msht.master.R;

/**
 * Created by hei on 2016/12/30.
 * 证书的适配器
 */

public class CertificateAdapter extends ListBaseAdapter<CertificateModel.CertificateDetail> {
    private LayoutInflater mLayoutInflater;

    public CertificateAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CertificateModel.CertificateDetail certificateDetail = mDataList.get(position);
        CertificateViewHolder viewHolder = (CertificateViewHolder) holder;
        viewHolder.id_tv_cert.setText(certificateDetail.name);
        viewHolder.id_tv_time.setText(certificateDetail.effective_time);
        viewHolder.tv_cert_num.setText(TextUtils.isEmpty(certificateDetail.number)?"无":certificateDetail.number);
        Glide
                .with(mContext)
                .load(certificateDetail.img)
                .error(R.drawable.shape_ring_loading)
                .placeholder(R.drawable.shape_ring_loading)
                .diskCacheStrategy(DiskCacheStrategy.ALL)//deactivate the disk cache for a request.
                .skipMemoryCache(false)//glide will not put image in the memory cache
                .thumbnail(0.1f)
                .into(((CertificateViewHolder) holder).iv_cert);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CertificateViewHolder(mLayoutInflater.inflate(R.layout.item_cert_book, parent, false));
    }

    public class CertificateViewHolder extends RecyclerView.ViewHolder {


        private final TextView id_tv_cert;
        private final TextView id_tv_time;
        private final TextView tv_cert_num;
        private final ImageView iv_cert;

        public CertificateViewHolder(View itemView) {
            super(itemView);
            id_tv_cert = (TextView) itemView.findViewById(R.id.id_tv_cert);
            id_tv_time = (TextView) itemView.findViewById(R.id.id_tv_time);
            tv_cert_num = (TextView) itemView.findViewById(R.id.tv_cert_num);
            iv_cert = (ImageView) itemView.findViewById(R.id.iv_cert);
        }
    }

    public static class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if(parent.getChildAdapterPosition(view)==0){
                outRect.top=space;
            }
            outRect.bottom = space;

        }
    }
}
