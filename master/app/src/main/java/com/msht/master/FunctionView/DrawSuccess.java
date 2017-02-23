package com.msht.master.FunctionView;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.msht.master.R;

public class DrawSuccess extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_success);
        Intent date=getIntent();
        String apply_time=date.getStringExtra("apply_time");
        String expect_time=date.getStringExtra("expect_time");
        TextView tv_finish=(TextView) findViewById(R.id.id_tv_finish);
        tv_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(2);
                finish();
            }
        });

    }
}
