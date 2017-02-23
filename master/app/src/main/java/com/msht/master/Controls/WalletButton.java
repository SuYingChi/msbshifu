package com.msht.master.Controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.msht.master.R;

/**
 * 使用在我的钱包界面的按钮
 * Created by hei123 on 2016/12/13.
 */

public class WalletButton extends RelativeLayout {

    private String title;
    private String desc;
    private Drawable drawable;
    private ImageView iv_wallet_button_img;
    private TextView tv_wallet_button_title;
    private TextView tv_wallet_button_desc;

    public WalletButton(Context context) {
        this(context,null);
    }

    public WalletButton(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }
    public WalletButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.WalletButton);
        title = ta.getString(R.styleable.WalletButton_Title);
        desc = ta.getString(R.styleable.WalletButton_desc);
        drawable = ta.getDrawable(R.styleable.WalletButton_imgsrc);
        ta.recycle();
        inflaterLayout(context);
        initView();
    }

    private void initView() {
        iv_wallet_button_img = (ImageView) findViewById(R.id.iv_wallet_button_img);
        tv_wallet_button_title = (TextView) findViewById(R.id.tv_wallet_button_title);
        tv_wallet_button_desc = (TextView) findViewById(R.id.tv_wallet_button_desc);
        iv_wallet_button_img.setClickable(false);
        if(drawable!=null&&drawable instanceof Drawable){
            iv_wallet_button_img.setImageDrawable(drawable);
        }
        tv_wallet_button_title.setText(title);
        tv_wallet_button_desc.setText(desc);
    }

    protected void inflaterLayout(Context context) {
        // 导入布局
        LayoutInflater.from(context).inflate(R.layout.layout_wallet_button, this, true);
    }

    public void SetDesc(String desc){
        tv_wallet_button_desc.setText(desc);
    }

}
