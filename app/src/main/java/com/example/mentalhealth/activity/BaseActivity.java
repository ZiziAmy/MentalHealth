package com.example.mentalhealth.activity;

import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


//基础activity
public class BaseActivity extends AppCompatActivity {
    public Handler handler = new Handler();

    //用于显示提示信息
    public void toast(String msg){
        handler.post(()->{
            Toast.makeText(BaseActivity.this,msg,Toast.LENGTH_SHORT).show();
        });
    }
}
