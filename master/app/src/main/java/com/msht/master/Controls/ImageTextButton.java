package com.msht.master.Controls;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.msht.master.R;


/**
 * Created by hei123 on 11/11/2016.
 * CopyRight @hei123
 * 图标+文字+箭头的按钮
 */

public class ImageTextButton extends RelativeLayout {

    /**
     * 是否具有右边的图片
     */
    private boolean hasRightImage = false;

    private boolean hasLeftImage = false;

    /**
     * 左边显示的图片资源
     */
    private Drawable imageLeft;
    /**
     * 右边显示的图片资源
     */
    private Drawable imageRight;
    /**
     * 在中间显示的文字
     */
    private String text;
    /**
     * 中间显示的文字的颜色 默认为黑色
     */
    private int textColor = 0xFF000000;

    /**
     * 左边的ImageView
     */
    private ImageView leftImageView;
    /**
     * 中间显示的textview
     */
    private TextView textView;
    /**
     * 右边的imageview
     */
    private ImageView rightImageView;

    public ImageTextButton(Context context) {
        this(context, null);
    }

    public ImageTextButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageTextButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ImageTextButton);
        hasRightImage = ta.getBoolean(R.styleable.ImageTextButton_HasRightImage, false);
        if (ta.hasValue(R.styleable.ImageTextButton_ImageLeft)) {
            hasLeftImage=true;
            imageLeft = ta.getDrawable(R.styleable.ImageTextButton_ImageLeft);
        }

        imageRight = ta.getDrawable(R.styleable.ImageTextButton_ImageRight);
        text = ta.getString(R.styleable.ImageTextButton_text);
        textColor = ta.getColor(R.styleable.ImageTextButton_textColor, 0xFF000000);
        ta.recycle();
        initView(context);
        initLayout();
    }


    /**
     * 初始化元素的布局
     */
    private void initLayout() {
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = getRawSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        layoutParams.width = getRawSize(TypedValue.COMPLEX_UNIT_DIP, 25);
        layoutParams.height = getRawSize(TypedValue.COMPLEX_UNIT_DIP, 25);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        this.addView(leftImageView, layoutParams);
        if(!hasLeftImage){
            leftImageView.setVisibility(GONE);
        }
        LayoutParams textparams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textparams.addRule(RIGHT_OF, R.id.left_image);
        textparams.addRule(RelativeLayout.CENTER_VERTICAL);
        textparams.leftMargin = getRawSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        textparams.bottomMargin = getRawSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        textparams.topMargin = getRawSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        this.addView(textView, textparams);
        LayoutParams right_image = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        right_image.addRule(ALIGN_PARENT_RIGHT);
        right_image.addRule(CENTER_VERTICAL);
        right_image.rightMargin = getRawSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        this.addView(rightImageView, right_image);
    }

    /**
     * 初始化布局元素
     *
     * @param context
     */
    private void initView(Context context) {
        leftImageView = new ImageView(context);
        leftImageView.setId(R.id.left_image);
        leftImageView.setClickable(false);
        //设置src
        if(imageLeft!=null){
            setImageLeftResource(imageLeft);
        }
        textView = new TextView(context);
        setTextAndSize(text, 16);
        setTextColor(textColor);
        textView.setId(R.id.textView);
        rightImageView = new ImageView(context);
        rightImageView.setId(R.id.right_view);
        rightImageView.setClickable(false);
        if(imageRight!=null){
            setImageRightResource(imageRight);
        }

    }

    /**
     * 设置左边图片的src
     *
     * @param resourceId
     */
    public void setImageLeftResource(int resourceId) {
        leftImageView.setImageResource(resourceId);
    }

    /**
     * 设置左边图片的src
     *
     * @param drawable
     */
    public void setImageLeftResource(Drawable drawable) {
        leftImageView.setImageDrawable(drawable);
    }

    /**
     * 设置右边图片的src
     *
     * @param resourceId
     */
    public void setImageRightResource(int resourceId) {
        rightImageView.setImageResource(resourceId);
    }

    /**
     * 设置右边图片的src
     *
     * @param drawable
     */
    public void setImageRightResource(Drawable drawable) {
        rightImageView.setImageDrawable(drawable);
    }

    /**
     * 设置文字内容和大小
     *
     * @param text
     * @param sp
     */
    public void setTextAndSize(String text, int sp) {
        textView.setText(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
    }

    public void setTextColor(int textColor) {
        textView.setTextColor(textColor);
    }


    public int getRawSize(int unit, float size) {
        Context c = getContext();
        Resources r;

        if (c == null)
            r = Resources.getSystem();
        else
            r = c.getResources();

        return (int) TypedValue.applyDimension(unit, size, r.getDisplayMetrics());
    }

}
