package com.msht.master.UIView;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.msht.master.R;

import java.security.acl.Group;

/**
 * Created by hong on 2016/10/24.
 */
public class ConvertOrderDialog extends Dialog {
    private Button btn_ensure,btn_cancel;
    private RadioGroup Group;
    private RadioButton radioOne,radioTwo,radiotThird;
    private RadioButton radioFour,radioFive,radioSix;
    private EditText et_Reason;
    private String  reason;
    private Context context;
    public ConvertOrderDialog(Context context) {
        super(context, R.style.PromptDialogStyle);
        this.context=context;
        setReasonDialog();
    }
    public View getReasonText(){
        return et_Reason;
    }
    public View getRadioButon(){
        return radioSix;
    }
    public String getStringText(){
        return reason;
    }
    private void setReasonDialog() {
        View view= LayoutInflater.from(context).inflate(R.layout.dialog_convert_order,null);
        Group=(RadioGroup)view.findViewById(R.id.id_Group);
        radioOne=(RadioButton)view.findViewById(R.id.id_radioOne);
        radioTwo=(RadioButton)view.findViewById(R.id.id_radioTwo);
        radiotThird=(RadioButton)view.findViewById(R.id.id_radiotThird);
        radioFour=(RadioButton)view.findViewById(R.id.id_radioFour);
        radioFive=(RadioButton)view.findViewById(R.id.id_radioFive);
        radioSix=(RadioButton)view.findViewById(R.id.id_radioSix);
        et_Reason=(EditText)view.findViewById(R.id.id_other_reason);
        btn_cancel=(Button)view.findViewById(R.id.id_cancel);
        btn_ensure=(Button)view.findViewById(R.id.id_ok);
        Group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i==radioOne.getId()){
                    reason=radioOne.getText().toString().trim();
                }else if (i==radioTwo.getId()){
                    reason=radioTwo.getText().toString().trim();
                }else if (i==radiotThird.getId()){
                    reason=radiotThird.getText().toString().trim();
                }else if (i==radioFour.getId()){
                    reason=radioFour.getText().toString().trim();
                }else if (i==radioFive.getId()){
                    reason=radioFive.getText().toString().trim();
                }else if (i==radioSix.getId()){
                    reason=et_Reason.getText().toString().trim();
                }
            }
        });
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
    public void setOnpositiveListener(View.OnClickListener listener){
        btn_ensure.setOnClickListener(listener);
    }
    public void setOnNegativeListener(View.OnClickListener listener){
        btn_cancel.setOnClickListener(listener);
    }

}
