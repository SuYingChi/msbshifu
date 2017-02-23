package com.msht.master.FunctionView;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.msht.master.HtmlWeb.SeePrice;
import com.msht.master.R;
import com.msht.master.Utils.NetWorkUtil;

public class PriceBook extends AppCompatActivity implements View.OnClickListener {
    private ImageView backimg;
    private RelativeLayout Rwiring,Rwaterpipe,Rsanitary,Rclosestool,Rbathroom;
    private RelativeLayout Raircondition,Rwashing,Refrigerator,Rcalorifier;
    private RelativeLayout Rcomputer,Rgasoven,Rhotteaspirante;
    private RelativeLayout Rdisinfection,Rdianlu,Rsocket,Rlenterns,Ropenlock;
    private RelativeLayout Rpipeline,Rpunching,Rfurniture,Rdoor,Rwindow;
    private RelativeLayout Rhardware,Rburglarmesh;
    private RelativeLayout Rair_clean,Rgasoven_clean,Rcalor_clean;
    private RelativeLayout Rhottea_clean,Refrige_clean,Rwashing_clean;
    private String Id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_book);
        initHeaderTitle();

        initView();
        initEvent();
    }

    private void initHeaderTitle() {
        backimg=(ImageView)findViewById(R.id.id_goback);
        backimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("价格手册");
    }

    private void initView() {
        Rwiring=(RelativeLayout)findViewById(R.id.id_layout_wiring);
        Rwaterpipe=(RelativeLayout)findViewById(R.id.id_layout_waterpipe);
        Rsanitary=(RelativeLayout)findViewById(R.id.id_layout_sanitary);
        Rclosestool=(RelativeLayout)findViewById(R.id.id_layout_closestool);
        Rbathroom=(RelativeLayout)findViewById(R.id.id_li_bathroom_cabinet);
        Raircondition=(RelativeLayout)findViewById(R.id.id_layout_aircondition);
        Rwashing=(RelativeLayout)findViewById(R.id.id_layout_washing);
        Refrigerator=(RelativeLayout)findViewById(R.id.id_layout_refrigerator);
        Rcalorifier=(RelativeLayout)findViewById(R.id.id_layout_calorifier);
        Rcomputer=(RelativeLayout)findViewById(R.id.id_layout_computer);
        Rgasoven=(RelativeLayout)findViewById(R.id.id_layout_gasoven);
        Rhotteaspirante=(RelativeLayout)findViewById(R.id.id_layout_hotteaspirante);
        Rdisinfection=(RelativeLayout)findViewById(R.id.id_li_disinfection_cab);
        Rdianlu=(RelativeLayout)findViewById(R.id.id_layout_dianlu);
        Rsocket=(RelativeLayout)findViewById(R.id.id_layout_socket);
        Rlenterns=(RelativeLayout)findViewById(R.id.id_layout_lenterns);
        Ropenlock=(RelativeLayout)findViewById(R.id.id_layout_openlock);
        Rpipeline=(RelativeLayout)findViewById(R.id.id_layout_pipeline);
        Rpunching=(RelativeLayout)findViewById(R.id.id_layout_punching);
        Rfurniture=(RelativeLayout)findViewById(R.id.id_layout_furniture);
        Rdoor=(RelativeLayout)findViewById(R.id.id_layout_door);
        Rwindow=(RelativeLayout)findViewById(R.id.id_layout_window);
        Rhardware=(RelativeLayout)findViewById(R.id.id_layout_hardware);
        Rburglarmesh=(RelativeLayout)findViewById(R.id.id_layout_burglarmesh);

        Rair_clean=(RelativeLayout)findViewById(R.id.id_re_aircondition_clean);
        Rgasoven_clean=(RelativeLayout)findViewById(R.id.id_re_gasoven_lean);
        Rcalor_clean=(RelativeLayout)findViewById(R.id.id_re_hotwater_clean);
        Rhottea_clean=(RelativeLayout)findViewById(R.id.id_re_hotteaspirante_clean);
        Refrige_clean=(RelativeLayout)findViewById(R.id.id_re_refrigerator_clean);
        Rwashing_clean=(RelativeLayout)findViewById(R.id.id_re_washing_clean);

    }

    private void initEvent() {

        Rwiring.setOnClickListener(this);
        Rwaterpipe.setOnClickListener(this);
        Rsanitary.setOnClickListener(this);
        Rclosestool.setOnClickListener(this);
        Raircondition.setOnClickListener(this);
        Rbathroom.setOnClickListener(this);
        Rwashing.setOnClickListener(this);
        Refrigerator.setOnClickListener(this);
        Rcalorifier.setOnClickListener(this);
        Rcomputer.setOnClickListener(this);
        Rgasoven.setOnClickListener(this);
        Rhotteaspirante.setOnClickListener(this);
        Rdisinfection.setOnClickListener(this);
        Rdianlu.setOnClickListener(this);
        Rsocket.setOnClickListener(this);
        Rlenterns.setOnClickListener(this);
        Ropenlock.setOnClickListener(this);
        Rpipeline.setOnClickListener(this);
        Rpunching.setOnClickListener(this);
        Rfurniture.setOnClickListener(this);
        Rdoor.setOnClickListener(this);
        Rwindow.setOnClickListener(this);
        Rhardware.setOnClickListener(this);
        Rburglarmesh.setOnClickListener(this);

        Rair_clean.setOnClickListener(this);;
        Rgasoven_clean.setOnClickListener(this);;
        Rcalor_clean.setOnClickListener(this);;
        Rhottea_clean.setOnClickListener(this);;
        Refrige_clean.setOnClickListener(this);;
        Rwashing_clean.setOnClickListener(this);;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.id_layout_wiring:
                Id="5";
                seeprice(Id);
                break;
            case R.id.id_layout_waterpipe:
                Id="6";
                seeprice(Id);
                break;
            case R.id.id_layout_sanitary:
                Id="7";
                seeprice(Id);
                break;
            case R.id.id_layout_closestool:     //马桶
                Id="8";
                seeprice(Id);
                break;
            case R.id.id_li_bathroom_cabinet:
                Id="9";
                seeprice(Id);
                break;
            case R.id.id_layout_gasoven:        //燃气灶
                Id="10";
                seeprice(Id);
                break;
            case R.id.id_layout_calorifier:     //热水器
                Id="11";
                seeprice(Id);
                break;
            case R.id.id_layout_hotteaspirante: //油烟
                Id="12";
                seeprice(Id);
                break;
            case R.id.id_li_disinfection_cab:   //消毒柜
                Id="13";
                seeprice(Id);
                break;
            case R.id.id_layout_computer:      //电脑
                Id="14";
                seeprice(Id);
                break;
            case R.id.id_layout_aircondition:  //空调
                Id="15";
                seeprice(Id);
                break;
            case R.id.id_layout_washing:       //洗衣机
                Id="16";
                seeprice(Id);
                break;
            case R.id.id_layout_refrigerator:
                Id="17";
                seeprice(Id);
                break;
            case R.id.id_layout_lenterns:
                Id="18";
                seeprice(Id);
                break;
            case R.id.id_layout_socket:
                Id="19";
                seeprice(Id);
                break;
            case R.id.id_layout_dianlu:
                Id="20";
                seeprice(Id);
                break;
            case R.id.id_layout_openlock:      //开锁换锁
                Id="21";
                seeprice(Id);
                break;
            case R.id.id_layout_pipeline:     //管道疏通
                Id="22";
                seeprice(Id);
                break;
            case R.id.id_layout_punching:     //墙面打孔
                Id="24";
                seeprice(Id);
                break;
            case R.id.id_layout_furniture:
                Id="25";
                seeprice(Id);
                break;
            case R.id.id_layout_door:
                Id="26";
                seeprice(Id);
                break;
            case R.id.id_layout_window:
                Id="27";
                seeprice(Id);
                break;
            case R.id.id_layout_hardware:
                Id="28";
                seeprice(Id);
                break;
            case R.id.id_layout_burglarmesh:       //防盗网
                Id="29";
                seeprice(Id);
                break;
            case R.id.id_re_gasoven_lean:        //燃气灶清洗
                Id="30";
                seeprice(Id);
                break;
            case R.id.id_re_hotwater_clean:
                Id="31";
                seeprice(Id);
                break;
            case R.id.id_re_hotteaspirante_clean:
                Id="32";
                seeprice(Id);
                break;
            case R.id.id_re_aircondition_clean:
                Id="33";
                seeprice(Id);
                break;
            case R.id.id_re_refrigerator_clean:
                Id="34";
                seeprice(Id);
                break;
            case R.id.id_re_washing_clean:
                Id="35";
                seeprice(Id);
                break;
            default:
                break;
        }
    }
    private void seeprice(String id) {
        Intent see=new Intent(this,SeePrice.class);
        see.putExtra("Id",id);
        startActivity(see);
    }
}
