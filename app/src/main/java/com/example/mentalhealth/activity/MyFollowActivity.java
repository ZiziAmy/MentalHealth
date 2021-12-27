package com.example.mentalhealth.activity;

import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.mentalhealth.R;
import com.example.mentalhealth.bean.ApiResponse;
import com.example.mentalhealth.bean.User;
import com.example.mentalhealth.databinding.ActivityMyFollowBinding;
import com.example.mentalhealth.databinding.ItemFollowBinding;
import com.example.mentalhealth.util.Global;
import com.example.mentalhealth.util.HttpUtil;

import java.util.List;

//我关注的人列表
public class MyFollowActivity extends BaseActivity {
    private ActivityMyFollowBinding activityMyFollowBinding;
    private List<User> users;//我关注的人列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化布局
        activityMyFollowBinding = DataBindingUtil.setContentView(this, R.layout.activity_my_follow);
        setTitle("我的关注");
        initData();//初始化数据
    }

    //初始化数据
    private void initData() {
        new Thread(() -> {
            ApiResponse<List<User>> apiResponse = HttpUtil.getMyFollowUser(Global.user.username);//获取我的关注列表
            if (apiResponse.success()) {
                if (apiResponse.data.isEmpty()) {
                    toast("你还没有关注的人了");
                    finish();
                    return;
                }
                users = apiResponse.data;//赋值关注列表
                handler.post(() -> {
                    setTitle("我的关注（" + users.size() + "）");//设置标题
                    //设置关注列表
                    activityMyFollowBinding.lv.setAdapter(new MyAdapter());
                });

            } else {
                toast("获取失败，请重试");
                finish();
            }
        }).start();
    }

    //关注列表适配器
    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return users.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //加载布局
            @SuppressLint("ViewHolder") ItemFollowBinding itemFollowBinding = DataBindingUtil.inflate(LayoutInflater.from(MyFollowActivity.this), R.layout.item_follow, null, false);
            Glide.with(MyFollowActivity.this).load(HttpUtil.getImageUrl(users.get(position).avatar))
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(itemFollowBinding.ivHead);//显示头像
            itemFollowBinding.tvName.setText(users.get(position).name);//咨询师姓名
            itemFollowBinding.btnRemove.setOnClickListener(v -> {
                //取消关注事件
                new Thread(() -> {
                    //取消关注
                    ApiResponse<String> apiResponse = HttpUtil.removeFollow(Global.user.username, users.get(position).username);
                    toast(apiResponse.message);//提示
                    initData();//刷新列表
                }).start();
            });
            itemFollowBinding.getRoot().setOnClickListener(v -> {
                //进入咨询师详情页面
                Intent intent = new Intent(MyFollowActivity.this, ViewDoctorActivity.class);
                intent.putExtra("username", users.get(position).username);//将预览数据传递过去
                startActivity(intent);
            });
            return itemFollowBinding.getRoot();
        }
    }
}