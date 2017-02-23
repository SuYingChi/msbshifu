package com.msht.master.UIView;


import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;

import com.msht.master.R;

import java.lang.reflect.Field;

/**
 * Created by hong on 2016/10/27.
 */
public class DatePickerDialog extends AlertDialog implements DialogInterface.OnClickListener, OnDateChangedListener {

    private static final String START_YEAR = "start_year";
    private static final String START_MONTH = "start_month";
    private static final String START_DAY = "start_day";

    private DatePicker mDatePickerStart=null;
    private OnDateSetListener mCallBack=null;

    protected DatePickerDialog(@NonNull Context context) {
        super(context);
    }

    protected DatePickerDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    protected DatePickerDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
    public interface OnDateSetListener {
        void onDateSet(DatePicker startDatePicker, int startYear, int startMonthOfYear, int startDayOfMonth);
    }
    public DatePickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        this(context, 0, callBack, year, monthOfYear, dayOfMonth);
    }
    public DatePickerDialog(Context context, int theme, OnDateSetListener callBack, int year, int monthOfYear,
                            int dayOfMonth) {
        super(context, theme);
        mCallBack = callBack;

        Context themeContext = getContext();
        setTitle("选择月份");
        setButton(BUTTON_POSITIVE, "确 定", this);
        setButton(BUTTON_NEGATIVE, "取 消", this);
        setIcon(0);
        LayoutInflater inflater = (LayoutInflater) themeContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_date_picker, null);
        setView(view);
        mDatePickerStart = (DatePicker) view.findViewById(R.id.datePickerStart);
        mDatePickerStart.init(year, monthOfYear, dayOfMonth, this);
        hideDay(mDatePickerStart);
    }

    private void hideDay(DatePicker mDatePicker) {
        try {
            /* 处理android5.0以上的特殊情况 */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int daySpinnerId = Resources.getSystem().getIdentifier("day", "id", "android");
                if (daySpinnerId != 0) {
                    View daySpinner = mDatePicker.findViewById(daySpinnerId);
                    if (daySpinner != null) {
                        daySpinner.setVisibility(View.GONE);
                    }
                }
            } else {
                Field[] datePickerfFields = mDatePicker.getClass().getDeclaredFields();
                for (Field datePickerField : datePickerfFields) {
                    if ("mDaySpinner".equals(datePickerField.getName()) || ("mDayPicker").equals(datePickerField.getName())) {
                        datePickerField.setAccessible(true);
                        Object dayPicker = new Object();
                        try {
                            dayPicker = datePickerField.get(mDatePicker);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        }
                        ((View) dayPicker).setVisibility(View.GONE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClick(DialogInterface dialog, int which) {
        if (which == BUTTON_POSITIVE)
            tryNotifyDateSet();
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int month, int day) {
        if (view.getId() == R.id.datePickerStart)
            mDatePickerStart.init(year, month, day, this);
    }

    public DatePicker getDatePickerStart() {
        return mDatePickerStart;
    }

    public void updateStartDate(int year, int monthOfYear, int dayOfMonth) {
        mDatePickerStart.updateDate(year, monthOfYear, dayOfMonth);
    }


    private void tryNotifyDateSet() {
        if (mCallBack != null) {
            mDatePickerStart.clearFocus();
            mCallBack.onDateSet(mDatePickerStart, mDatePickerStart.getYear(), mDatePickerStart.getMonth(),
                    mDatePickerStart.getDayOfMonth());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }
    @Override
    public Bundle onSaveInstanceState() {
        Bundle state = super.onSaveInstanceState();
        state.putInt(START_YEAR, mDatePickerStart.getYear());
        state.putInt(START_MONTH, mDatePickerStart.getMonth());
        state.putInt(START_DAY, mDatePickerStart.getDayOfMonth());
        return state;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int start_year = savedInstanceState.getInt(START_YEAR);
        int start_month = savedInstanceState.getInt(START_MONTH);
        int start_day = savedInstanceState.getInt(START_DAY);
        mDatePickerStart.init(start_year, start_month, start_day, this);
    }
}
