package com.example.mentalhealth.activity;

import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
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
import com.example.mentalhealth.databinding.ActivityFansBinding;
import com.example.mentalhealth.databinding.ItemFansBinding;
import com.example.mentalhealth.util.Global;
import com.example.mentalhealth.util.HttpUtil;

import java.util.List;

//我的粉丝
public class FansActivity extends BaseActivity {
    private ActivityFansBinding activityFansBinding;
    private List<User> users;//我的粉丝

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化页面
        activityFansBinding = DataBindingUtil.setContentView(this, R.layout.activity_fans);
        setTitle("我的粉丝");
        initData();//初始化数据
    }

    //初始化数据
    private void initData() {
        new Thread(() -> {
            ApiResponse<List<User>> apiResponse = HttpUtil.getFollowMeUser(Global.user.username);//获取我的粉丝列表
            if (apiResponse.success()) {
                if (apiResponse.data.isEmpty()) {
                    toast("你还没有粉丝了");
                    finish();
                    return;
                }
                users = apiResponse.data;//赋值粉丝列表
                handler.post(() -> {
                    //设置粉丝列表
                    setTitle("我的粉丝（" + users.size() + "）");
                    activityFansBinding.lv.setAdapter(new MyAdapter());
                });

            } else {
                toast("获取失败，请重试");
                finish();
            }
        }).start();
    }

    //粉丝列表适配器
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
            @SuppressLint("ViewHolder") ItemFansBinding itemFansBinding = DataBindingUtil.inflate(LayoutInflater.from(FansActivity.this), R.layout.item_fans, null, false);
            Glide.with(FansActivity.this).load(HttpUtil.getImageUrl(users.get(position).avatar))
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(itemFansBinding.ivImage);//显示头像
            itemFansBinding.tvUserName.setText(users.get(position).username);//用户名
            itemFansBinding.btnRemove.setOnClickListener(v -> {
                //移除粉丝事件
                new Thread(() -> {
                    //移除粉丝
                    ApiResponse<String> apiResponse = HttpUtil.removeFollowMe(Global.user.username, users.get(position).username);
                    toast(apiResponse.message);//提示
                    initData();//刷新列表
                }).start();
            });
            return itemFansBinding.getRoot();
        }
    }
}