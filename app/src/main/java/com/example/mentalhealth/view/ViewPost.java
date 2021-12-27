package com.example.mentalhealth.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.example.mentalhealth.MyApplication;
import com.example.mentalhealth.R;
import com.example.mentalhealth.activity.MyFollowActivity;
import com.example.mentalhealth.activity.PostActivity;
import com.example.mentalhealth.activity.PostDetailActivity;
import com.example.mentalhealth.adapter.PostAdapter;
import com.example.mentalhealth.bean.ApiResponse;
import com.example.mentalhealth.bean.Post;
import com.example.mentalhealth.databinding.ViewPostBinding;
import com.example.mentalhealth.util.HttpUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

//故事会页面
public class ViewPost implements View.OnClickListener {
    private final ViewPostBinding fragmentSearchBinding;
    private final Context context;

    //构造器
    public ViewPost(Context context) {
        this.context = context;
        //初始化布局
        fragmentSearchBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.view_post, null, false);
        fragmentSearchBinding.btnPublish.setOnClickListener(this);//发布按钮事件
        //故事点击事件
        fragmentSearchBinding.lv.setOnItemClickListener((parent, view1, position, id) -> {
            Intent intent = new Intent(context, PostDetailActivity.class);
            //在intent中传参，传入post的uuid
            intent.putExtra("uuid", postList.get(position).uuid);//传递数据
            context.startActivity(intent);//进入故事详情页面
        });
    }

    public View getRoot() {
        return fragmentSearchBinding.getRoot();
    }


    private List<Post> postList = new ArrayList<>();//故事列表

    //Toast提示
    private void toast(String msg) {
        MyApplication.toast(msg);
    }

    private final Handler handler = new Handler();

    //初始化数据
    public void initData() {
        new Thread(() -> {
            ApiResponse<List<Post>> apiResponse = HttpUtil.getAllPost();
            if (apiResponse.success()) {
                //获取成功
                if (apiResponse.data == null || apiResponse.data.isEmpty()) {
                    toast("还没有人发布过故事");
                } else {
                    postList = apiResponse.data;
                    handler.post(() -> {
                        ////使用adapter显示数据，更新故事列表
                        fragmentSearchBinding.lv.setAdapter(new PostAdapter(context, postList));
                    });
                }
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnPublish) {
            //发布
            context.startActivity(new Intent(context, PostActivity.class));
        }
    }
}
