package com.msht.master.Controls;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.RadioButton;

import com.msht.master.R;

/**
 * Created by hei on 2016/12/29.
 * 用于tab切换的radiobutton
 */

public class MyRadioButton extends RadioButton {
    public MyRadioButton(Context context) {
        this(context,null);
    }

    public MyRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }



    public MyRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setCompoundDrawablesWithIntrinsicBounds(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
        if(left != null){
            left.setBounds(0,0,getRawSize(TypedValue.COMPLEX_UNIT_DIP,45),getRawSize(TypedValue.COMPLEX_UNIT_DIP,45));
        }
        setCompoundDrawables(left,top,right,bottom);
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
