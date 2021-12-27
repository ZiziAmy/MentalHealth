package com.example.mentalhealth.activity;


import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.Manifest;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.example.mentalhealth.MyApplication;
import com.example.mentalhealth.R;
import com.example.mentalhealth.bean.ApiResponse;
import com.example.mentalhealth.bean.OCRResult;
import com.example.mentalhealth.bean.User;
import com.example.mentalhealth.bean.Words;
import com.example.mentalhealth.databinding.ActivityOcrResultBinding;
import com.example.mentalhealth.util.Global;
import com.example.mentalhealth.util.HttpUtil;
import com.google.gson.Gson;

//OCR结果页面
public class OCRResultActivity extends BaseActivity implements View.OnClickListener {
    private ActivityOcrResultBinding activityOcrResultBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化布局
        activityOcrResultBinding = DataBindingUtil.setContentView(this, R.layout.activity_ocr_result);
        setTitle("文字识别结果");
        String json=getIntent().getStringExtra("json");//获取传递的结果
        OCRResult ocrResult=new Gson().fromJson(json,OCRResult.class);//识别结果对象
        StringBuilder stringBuilder=new StringBuilder();
        for(Words words:ocrResult.words_result){
            stringBuilder.append(words.words);
        }
        activityOcrResultBinding.etResult.setText(stringBuilder.toString());//设置识别内容到文本框


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnCopy) {
            //复制
            ClipboardManager cmb = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setText(activityOcrResultBinding.etResult.getText().toString());
            MyApplication.toast("识别结果复制成功!");
        } else if (v.getId() == R.id.btnBack) {
            //返回
            finish();
        }
    }
}
