package com.msht.master.UIView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;


import com.msht.master.Adapter.EnterpriseAdapter;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by hong on 2016/7/13.
 */
public class ListViewForScrollView extends ListView{
    public ListViewForScrollView(Context context) {
        super(context);
    }

    public ListViewForScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListViewForScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec=MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE>>2,MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }


}
