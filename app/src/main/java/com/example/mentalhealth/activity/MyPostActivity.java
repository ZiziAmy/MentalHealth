package com.example.mentalhealth.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.example.mentalhealth.R;
import com.example.mentalhealth.adapter.PostAdapter;
import com.example.mentalhealth.bean.ApiResponse;
import com.example.mentalhealth.bean.Post;
import com.example.mentalhealth.databinding.ActivityMyPostBinding;
import com.example.mentalhealth.util.Global;
import com.example.mentalhealth.util.HttpUtil;

import java.util.List;

//我发布的故事
public class MyPostActivity extends BaseActivity {
    private ActivityMyPostBinding activityMyPostBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //界面初始化
        activityMyPostBinding = DataBindingUtil.setContentView(this, R.layout.activity_my_post);
        setTitle("我发布的故事");
        initData();//初始化数据
        activityMyPostBinding.lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //点击事件，进入故事详情
                Intent intent = new Intent(MyPostActivity.this, PostDetailActivity.class);
                intent.putExtra("uuid", postList.get(position).uuid);//传递uuid
                startActivity(intent);//进入页面
            }
        });
        activityMyPostBinding.lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String[] MENU = {"删除", "取消"};
                //显示对话框
                new AlertDialog.Builder(MyPostActivity.this, R.style.DialogTheme).setItems(MENU, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            new Thread(() -> {
                                ApiResponse<String> apiResponse = HttpUtil.delete(postList.get(position).uuid);//删除故事
                                toast(apiResponse.message);//提示
                                if (apiResponse.success()) {
                                    initData();//刷新故事列表
                                }
                            }).start();
                        }
                    }
                }).show();
                return true;
            }
        });
    }

    private List<Post> postList;//故事列表

    //初始化数据
    private void initData() {
        new Thread(() -> {
            ApiResponse<List<Post>> apiResponse = HttpUtil.getMyPost(Global.user.username);//获取我发布的故事
            if (apiResponse.success()) {
                if (apiResponse.data.isEmpty()) {
                    toast("你还没有发过故事");
                    finish();
                    return;
                }
                postList = apiResponse.data;//赋值故事列表
                handler.post(() -> {
                    //设置故事列表
                    setTitle("我发布的故事（" + postList.size() + "）");
                    activityMyPostBinding.lv.setAdapter(new PostAdapter(MyPostActivity.this, postList));
                });

            } else {
                toast("获取失败，请重试");
                finish();
            }
        }).start();
    }
}