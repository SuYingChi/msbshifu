package com.msht.master.UIView;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.msht.master.R;

/**
 * Created by hong on 2017/4/10.
 */

public class InputTextDialog extends Dialog {
    private TextView title;
    private EditText Ecustomer;
    private RelativeLayout  Rnext;
    private RelativeLayout Rcancel;
    private Context context;
    public InputTextDialog(Context context) {
        super(context, R.style.PromptDialogStyle);
        this.context=context;
        setDialog();
    }
    private void setDialog() {
        View view= LayoutInflater.from(context).inflate(R.layout.dialog_input_text,null);
        title=(TextView)view.findViewById(R.id.id_tv_title);
        Ecustomer=(EditText)view.findViewById(R.id.id_et_text);
        Rnext=(RelativeLayout)view.findViewById(R.id.id_re_ensure);
        Rcancel=(RelativeLayout)view.findViewById(R.id.id_re_cancel);
        super.setContentView(view);
    }
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }
    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
    }
    @Override
    public void setContentView(View view) {
        super.setContentView(view);
    }
    public View getTitle(){
        return title;
    }
    public View getEditText(){return  Ecustomer;}
    public void setOnNextListener(View.OnClickListener listener){
        Rnext.setOnClickListener(listener);
    }
    public void setOnNegativeListener(View.OnClickListener listener){
        Rcancel.setOnClickListener(listener);
    }
}
