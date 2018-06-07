package com.msht.master.FunctionView;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.msht.master.R;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoViewAttacher;

public class EnlargePicActivity extends AppCompatActivity {
    private PhotoViewAttacher attacher;
    private ImageView ivPic,deleteimg,backimg;
    private ArrayList<String> imgPaths;
    private int position=0;
    private TextView tv_num;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enlarge_pic);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            imgPaths = bundle.getStringArrayList("imgPaths");
            position=bundle.getInt("position");

        }
        initView();
        intEvent();
    }

    private void intEvent() {
        backimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        deleteimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }
    private void initView() {
        backimg=(ImageView)findViewById(R.id.id_gobackimg);
        tv_num=(TextView)findViewById(R.id.id_text);
        deleteimg=(ImageView)findViewById(R.id.id_delete_img);
        ivPic=(ImageView)findViewById(R.id.iv_pic);
        attacher = new PhotoViewAttacher(ivPic);
        tv_num.setText((position+1)+"/"+imgPaths.size());
        Glide.with(this).asBitmap().load(imgPaths.get(position)).into(new SimpleTarget<Bitmap>(){
            @Override
            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                ivPic.setImageBitmap(resource);
                attacher.update();
            }
        });
        attacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                // hideOrShowToolBar();
            }

            @Override
            public void onOutsidePhotoTap() {
                // hideOrShowToolBar();
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        attacher.cleanup();
        //  ButterKnife.unbind(this);
    }
}
