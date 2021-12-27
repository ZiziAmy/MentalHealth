package com.example.mentalhealth;

import android.app.Application;
import android.os.Handler;
import android.widget.Toast;


//程序入口
public class MyApplication extends Application {
    public static MyApplication myApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
    }

    private final Handler handler = new Handler();

    //toast提示
    public static void toast(String msg) {
        myApplication.showMessage(msg);
    }

    //进行toast提示
    public void showMessage(String msg) {
        handler.post(() -> {
            Toast.makeText(MyApplication.this, msg, Toast.LENGTH_SHORT).show();
        });
    }
}
