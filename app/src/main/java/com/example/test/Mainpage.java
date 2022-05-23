package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
public class Mainpage extends AppCompatActivity {
    private Button GPS_location, Enter_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);
        GPS_location=(Button) findViewById(R.id.gps_location);
        Enter_location=(Button) findViewById(R.id.enter_location);
        //匿名类的方式来完成监听器
        GPS_location.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1= new Intent();
                //setClass函数的第一个参数树context对象
                //Context是一个类，Activity是conetxt类的子类，也就是说所有activity对象都可以向上转型为Context对象
                //setclass函数的第二个参数是class对象，在当前场景瞎，应该传入需要被启动的ACtivity的class对象
                intent1.setClass(Mainpage.this,GetGPStoSearch.class);
                startActivity(intent1);
            }
        });
        Enter_location.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2= new Intent();
                intent2.setClass(Mainpage.this,MainActivity.class);
                startActivity(intent2);
            }
        });
    }
}