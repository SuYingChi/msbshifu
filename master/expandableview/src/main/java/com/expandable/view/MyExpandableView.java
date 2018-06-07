package com.expandable.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hei on 2016/12/15.
 */

public class MyExpandableView extends RelativeLayout {
    private static final int DURATION = 400;
    private float DEGREES=180f;
    private RelativeLayout clickableLayout;
    private LinearLayout contentLayout;
    private List<ViewGroup> outsideContentLayoutList;
    private int outsideContentHeight = 0;
    private ImageView iv_item_image;
    private ImageView rightIcon;
    private ValueAnimator animator;
    private RotateAnimation rotateAnimator;
    private TextView tv_order_num;
    private TextView tv_finish_time;
    private TextView tv_main_type;
    private TextView tv_order_status;
    private TextView tv_second_type;
    private TextView tv_real_amount;
    public MyExpandableView(Context context) {
        super(context);
        init();
    }

    public MyExpandableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyExpandableView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    private void init() {
        inflate(getContext(), R.layout.my_expandable_view, this);
        outsideContentLayoutList = new ArrayList<>();
        tv_order_num = (TextView) findViewById(R.id.tv_order_num);
        tv_finish_time = (TextView) findViewById(R.id.tv_finish_time);
        tv_main_type = (TextView) findViewById(R.id.tv_main_type);
        tv_second_type = (TextView) findViewById(R.id.tv_second_type);
        tv_real_amount = (TextView) findViewById(R.id.tv_real_amount);
        tv_order_status=(TextView) findViewById(R.id.id_order_status);
        clickableLayout = (RelativeLayout) findViewById(R.id.expandable_view_clickable_content);
        contentLayout = (LinearLayout) findViewById(R.id.expandable_view_content_layout);
        iv_item_image = (ImageView) findViewById(R.id.iv_item_image);
        rightIcon = (ImageView) findViewById(R.id.expandable_view_right_icon);
        contentLayout.setVisibility(GONE);
        clickableLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contentLayout.isShown()) {
                    collapse();
                } else {
                    expand();
                }
            }
        });
        //Add onPreDrawListener
        contentLayout.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        contentLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                        contentLayout.setVisibility(View.GONE);

                        final int widthSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                        final int heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                        contentLayout.measure(widthSpec, heightSpec);

                        animator = slideAnimator(0, contentLayout.getMeasuredHeight());
                        return true;
                    }
                });
    }
    public void SetOrderNumber(String text){
        tv_order_num.setText(text);
    }
    public void SetFinishTime(String text){
        tv_finish_time.setText(text);
    }
    public void SetMainType(String text){
        tv_main_type.setText(text);
    }
    public void SetSecondType(String text){
        tv_second_type.setText(text);
    }
    public void SetRealAmount(String text){tv_real_amount.setText(text);}
    public void SetorderStatus(String text){
        tv_order_status.setText(text);
    }
    public void setImage(int leftResId){
        if (leftResId == 0) {
            iv_item_image.setVisibility(GONE);
        } else {
            iv_item_image.setImageResource(leftResId);
        }
    }

    /**
     * Set the new Height of the visible content layout.
     * @param newHeight
     */
    public void setVisibleLayoutHeight(int newHeight) {
        this.clickableLayout.getLayoutParams().height = newHeight;
    }

    /**
     * Set the parent's ViewGroup
     * @param outsideContentLayout
     */
    public void setOutsideContentLayout(ViewGroup outsideContentLayout) {
        this.outsideContentLayoutList.add(outsideContentLayout);
    }

    /**
     * Set the parent's ViewGroup, one or more than one.
     * @param params
     */
    public void setOutsideContentLayout(ViewGroup... params) {
        for (int i = 0; i < params.length; i++) {
            this.outsideContentLayoutList.add(params[i]);
        }
    }


    /**
     * Returns the Content LinearLayout, the LinearLayout that expands or collapse in a fashion way.
     * @return The Content LinearLayout
     */
    public LinearLayout getContentLayout() {
        return this.contentLayout;
    }

    /**
     * Add a view into the Content LinearLayout, the LinearLayout that expands or collapse in a fashion way.
     * @param newContentView
     */
    public void addContentView(View newContentView) {
        contentLayout.addView(newContentView);
        contentLayout.invalidate();
    }

    /**
     * Expand animation to display the discoverable content.
     */
    public void expand() {
        //set Visible
        contentLayout.setVisibility(View.VISIBLE);
        int x = rightIcon.getMeasuredWidth() / 2;
        int y = rightIcon.getMeasuredHeight() / 2;

        rotateAnimator = new RotateAnimation(0f, DEGREES, x, y);
        rotateAnimator.setInterpolator(new LinearInterpolator());
        rotateAnimator.setRepeatCount(Animation.ABSOLUTE);
        rotateAnimator.setFillAfter(true);
        rotateAnimator.setDuration(DURATION);
        rightIcon.startAnimation(rotateAnimator);
        animator.start();
    }

    /**
     * Collapse animation to hide the discoverable content.
     */
    public void collapse() {
        int finalHeight = contentLayout.getHeight();

        int x = rightIcon.getMeasuredWidth() / 2;
        int y = rightIcon.getMeasuredHeight() / 2;

        rotateAnimator = new RotateAnimation(DEGREES, 0f, x, y);
        rotateAnimator.setInterpolator(new LinearInterpolator());
        rotateAnimator.setRepeatCount(Animation.ABSOLUTE);
        rotateAnimator.setFillAfter(true);
        rotateAnimator.setDuration(DURATION);

        ValueAnimator mAnimator = slideAnimator(finalHeight, 0);

        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                contentLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        rightIcon.startAnimation(rotateAnimator);
        mAnimator.start();
    }


    private ValueAnimator slideAnimator(int start, int end) {

        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.setDuration(DURATION);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (Integer) valueAnimator.getAnimatedValue();

                ViewGroup.LayoutParams layoutParams = contentLayout.getLayoutParams();
                layoutParams.height = value;

                contentLayout.setLayoutParams(layoutParams);
                contentLayout.invalidate();

                if (outsideContentLayoutList != null && !outsideContentLayoutList.isEmpty()) {

                    for (ViewGroup outsideParam : outsideContentLayoutList) {
                        ViewGroup.LayoutParams params = outsideParam.getLayoutParams();

                        if (outsideContentHeight == 0) {
                            outsideContentHeight = params.height;
                        }

                        params.height = outsideContentHeight + value;
                        outsideParam.setLayoutParams(params);
                        outsideParam.invalidate();
                    }
                }
            }
        });
        return animator;
    }
}
