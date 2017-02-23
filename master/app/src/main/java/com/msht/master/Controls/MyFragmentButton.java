package com.msht.master.Controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.msht.master.R;


/**
 * Created by hei on 2016/12/19.
 * 我的界面的按钮
 */

public class MyFragmentButton extends RelativeLayout {

    private String title;
    private Drawable drawable;
    private ImageView iv_my_fragment_button;
    private TextView tv_my_fragment_button;

    public MyFragmentButton(Context context) {
        this(context,null);
    }

    public MyFragmentButton(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyFragmentButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MyFragmentButton);
        title = ta.getString(R.styleable.MyFragmentButton_title2);
        drawable = ta.getDrawable(R.styleable.MyFragmentButton_img);
        ta.recycle();
        inflaterLayout(context);
        initView();
    }
    protected void inflaterLayout(Context context) {
        // 导入布局
        LayoutInflater.from(context).inflate(R.layout.layout_my_fragment_button, this, true);
    }
    private void initView() {
        iv_my_fragment_button = (ImageView) findViewById(R.id.iv_my_fragment_button);
        tv_my_fragment_button = (TextView) findViewById(R.id.tv_my_fragment_button);
        iv_my_fragment_button.setClickable(false);
        if(drawable!=null&&drawable instanceof Drawable){
            iv_my_fragment_button.setImageDrawable(drawable);
        }
        tv_my_fragment_button.setText(title);
    }
}
