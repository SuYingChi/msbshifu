package com.msht.master.Controls;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.msht.master.R;


/**
 * @author hei123
 *         any problems,please send emails to us at anytime.
 *         Email ADDRESS：1329698854@ qq.com
 *         <p>
 *             viewpager指示器
 *         先走构造函数
 *         onSizeChanged
 *         再走SetViewpager
 *         再走DISPAtchDraw
 */
public class ViewPagerIndicator extends LinearLayout {
    /**
     * 绘制画笔
     */
    private Paint mPaint;


    private Path indicatorPath;


    /**
     * 手指滑动时的偏移量
     */
    private float mTranslationX;

    /**
     * 默认的Tab数量
     */
    private static final int COUNT_DEFAULT_TAB = 4;
    /**
     * tab数量
     */
    private int mTabVisibleCount = COUNT_DEFAULT_TAB;

    private int mIndicatorHeight;
    private int mIndicatorWidth;

    /**
     * tab上的内容
     */
    private String[] mTabTitles;
    /**
     * 与之绑定的ViewPager
     */
    public ViewPager mViewPager;

    /**
     * 标题正常时的颜色
     */
    private static final int COLOR_TEXT_NORMAL = 0x77FFFFFF;
    /**
     * 标题选中时的颜色
     */
    private static final int COLOR_TEXT_HIGHLIGHTCOLOR = 0xFFFFFFFF;


    private int colorNormal = COLOR_TEXT_NORMAL;
    private int colorHigh = COLOR_TEXT_HIGHLIGHTCOLOR;


    public ViewPagerIndicator(Context context) {
        this(context, null);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 初始化画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        colorNormal=getResources().getColor(R.color.desc);
        colorHigh=getResources().getColor(R.color.colorPrimary);
        mPaint.setColor(getResources().getColor(R.color.colorAccent));
        mPaint.setStyle(Style.FILL);
        mPaint.setPathEffect(new CornerPathEffect(3));

    }

    /**
     * 绘制指示器
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        // 画笔平移到正确的位置
        canvas.translate(mTranslationX, getHeight() + 1);
        canvas.drawPath(indicatorPath,mPaint);
        canvas.restore();

        super.dispatchDraw(canvas);
    }

    /**
     * 初始化指示器的宽度
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mIndicatorWidth = w / mTabVisibleCount;
        initIndicator();
    }



    /**
     * 设置tab的标题
     *
     * @param datas
     */
    private void setTabItemTitles(String[] datas) {
        // 如果传入的list有值，则移除布局文件中设置的view
        if (datas != null && datas.length > 0) {
            this.removeAllViews();
            this.mTabTitles = datas;

            boolean flags = false;
            for (String title : mTabTitles) {
                if (flags) {
                    addView(generateDivider());
                } else {
                    flags = true;
                }
                addView(generateTextView(title));
            }
            // 设置item的click事件
            setItemClickEvent();
        }

    }

    // 设置关联的ViewPager
    public void setViewPager(ViewPager mViewPager, int pos) {
        this.mViewPager = mViewPager;

        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // 设置字体颜色高亮
                resetTextViewColor();
                highLightTextView(position*2);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
                // 滚动
                scroll(position, positionOffset);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        String[] Title = new String[mViewPager.getAdapter().getCount()];
        for (int i = 0; i < mViewPager.getAdapter().getCount(); i++) {
            Title[i] = (String) mViewPager.getAdapter().getPageTitle(i);
        }
        mTabVisibleCount=Title.length;
        setTabItemTitles(Title);

        // 设置当前页
        mViewPager.setCurrentItem(pos);
        // 高亮
        highLightTextView(pos);
    }

    /**
     * 高亮文本
     *
     * @param position
     */
    protected void highLightTextView(int position) {
        View view = getChildAt(position);
        if (view instanceof TextView) {
            ((TextView) view).setTextColor(colorHigh);
        }

    }

    /**
     * 重置文本颜色
     */
    private void resetTextViewColor() {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(colorNormal);
            }
        }
    }


    /**
     * 设置点击事件
     */
    public void setItemClickEvent() {
        int cCount = getChildCount();

        for (int i = 0; i < cCount; i++) {
            final int j = i;
            View view = getChildAt(i);
            if(view instanceof TextView){
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewPager.setCurrentItem(j/2);
                    }
                });
            }
        }
    }

    /**
     * 根据标题生成我们的TextView
     *
     * @param text
     * @return
     */
    private TextView generateTextView(String text) {
        TextView tv = new TextView(getContext());
        LayoutParams lp = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        lp.width = getScreenWidth() / mTabVisibleCount;
        lp.gravity=Gravity.CENTER;
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(colorNormal);
       // tv.setPadding(0, 30, 0, 30);
        tv.setText(text);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tv.setLayoutParams(lp);
        return tv;
    }

    private View generateDivider(){
        View view = new View(getContext());
        LayoutParams lp = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        lp.width = getRawSize(TypedValue.COMPLEX_UNIT_DIP,0.5f);
        view.setBackgroundColor(0x44444444);
        view.setLayoutParams(lp);
        return view;
    }


    /**
     * 初始化指示器的线条
     */
    private void initIndicator() {
        indicatorPath=new Path();
        mIndicatorHeight=getRawSize(TypedValue.COMPLEX_UNIT_DIP,2);
        indicatorPath.moveTo(0,0);
        indicatorPath.lineTo(mIndicatorWidth,0);
        indicatorPath.lineTo(mIndicatorWidth,-mIndicatorHeight);
        indicatorPath.lineTo(0,-mIndicatorHeight);
        indicatorPath.close();

    }


    /**
     * 指示器跟随手指滚动
     *
     * @param position
     * @param offset
     */
    public void scroll(int position, float offset) {
        // 不断改变偏移量，invalidate
        mTranslationX = getWidth() / mTabVisibleCount * (position + offset);
        invalidate();
    }


    /**
     * 获得屏幕的宽度
     *
     * @return
     */
    public int getScreenWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
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
