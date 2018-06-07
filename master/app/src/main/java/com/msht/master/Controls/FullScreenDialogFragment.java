package com.msht.master.Controls;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.msht.master.R;

import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * Created by hei123 on 11/3/2016.
 * CopyRight @hei123
 * 用于展示图片详情的Dialog
 */

public class FullScreenDialogFragment extends DialogFragment {
    private int mClickItem;//对应显示ViewPager子项的位置
    private List<String> mListImgUrls;
    private HackyViewPager mViewPager;
    private Integer[] mImgIds;//本地图片资源ID
    private Dialog mDialog;
    private Context mContext;

    public static FullScreenDialogFragment newInstance(Context context, Integer[] imgIds, int clickItem) {
        Bundle args = new Bundle();
        FullScreenDialogFragment fragment = new FullScreenDialogFragment();
        fragment.setArguments(args);
        fragment.mContext = context;
        fragment.mImgIds = imgIds;
        fragment.mClickItem = clickItem;
        return fragment;
    }

    public static FullScreenDialogFragment newInstance(Context context, List<String> listUrl, int clickItem) {
        Bundle args = new Bundle();
        FullScreenDialogFragment fragment = new FullScreenDialogFragment();
        fragment.setArguments(args);
        fragment.mContext = context;
        fragment.mListImgUrls = listUrl;
        fragment.mClickItem = clickItem;
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //mDialog = new Dialog(mContext);
        //这样才能设置为全屏
        mDialog=new Dialog(mContext, R.style.CustomDialog_fill);
        //去标题栏
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        initView();
        return mDialog;
    }
    private void initView() {
        //将Dialog设置全屏！！！
        setDlgParams();
        View view = View.inflate(mContext, R.layout.dialog_normal_layout, null);
        mViewPager = (HackyViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setBackgroundColor(0xff000000);
        initViewPager();
        mDialog.setContentView(view);
    }

    private void setDlgParams() {
        ViewGroup.LayoutParams lay = mDialog.getWindow().getAttributes();
        DisplayMetrics dm = new DisplayMetrics();
        mDialog.getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        Rect rect = new Rect();
        View view = mDialog.getWindow().getDecorView();
        view.getWindowVisibleDisplayFrame(rect);
        lay.height = dm.heightPixels;
        lay.width = dm.widthPixels;

    }

    private void initViewPager() {
        if (mImgIds != null && mImgIds.length > 0) {
//            List<View> listImgs = new ArrayList<>();
//            for (int i = 0; i < mImgIds.length; i++) {
//                ImageView iv = new ImageView(mContext);
//                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
//                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//                iv.setLayoutParams(params);
//                listImgs.add(iv);
//                iv.setImageResource(mImgIds[i]);
//            }
            if (mImgIds.length > 0) {
                MyPagerAdapter pageAdapter = new MyPagerAdapter(mImgIds);
                mViewPager.setAdapter(pageAdapter);
                mViewPager.setCurrentItem(mClickItem);
            }
        } else if (mListImgUrls != null && mListImgUrls.size() > 0) {
            MyPagerAdapter pageAdapter = new MyPagerAdapter(mListImgUrls);
            mViewPager.setAdapter(pageAdapter);
            mViewPager.setCurrentItem(mClickItem);
        }
    }


    class MyPagerAdapter extends PagerAdapter {
        int mode;
        Integer[] datas;
        List<String> stringList;

        public MyPagerAdapter(Integer[] data) {
            //表示当前位本地模式
            this.mode = 0;
            this.datas = data;
        }

        public MyPagerAdapter(List<String> stringList) {
            //表示位网络模式
            this.mode = 1;
            this.stringList = stringList;
        }
        @Override
        public int getCount() {
            switch (mode) {
                case 0:
                    return datas.length;
                case 1:
                    return stringList.size();
                default:
                    break;
            }
            return 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            PhotoView photoView = new PhotoView(container.getContext());
            photoView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    FullScreenDialogFragment.this.dismiss();
                }

                @Override
                public void onOutsidePhotoTap() {

                }
            });
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.shape_ring_loading);
            requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
            requestOptions.skipMemoryCache(false);
            Glide.with(mContext).load(mListImgUrls.get(position))
                    .apply(requestOptions).into(photoView);
           /* Glide
                    .with(mContext)
                    .load(mListImgUrls.get(position))
                    .placeholder(R.drawable.shape_ring_loading)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)//deactivate the disk cache for a request.
                    .skipMemoryCache(false)//glide will not put image in the memory cache
                    .into(photoView);*/

            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
            return photoView;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);//删除页卡
        }
    }
}
