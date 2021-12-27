package com.example.mentalhealth.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.mentalhealth.R;
import com.example.mentalhealth.activity.ChatActivity;
import com.example.mentalhealth.activity.ViewDoctorActivity;
import com.example.mentalhealth.bean.User;
import com.example.mentalhealth.databinding.ItemDoctorBinding;
import com.example.mentalhealth.util.HttpUtil;

import java.util.List;

//咨询师列表适配器
public class DoctorAdapter extends BaseAdapter {
    private final Context context;
    private final List<User> users;

    public DoctorAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

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


    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //加载布局
        @SuppressLint("ViewHolder") ItemDoctorBinding itemDoctorBinding =
                DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_doctor, null, false);
        User user = users.get(position);
        itemDoctorBinding.tvName.setText(user.name);//姓名
        itemDoctorBinding.tvPrice.setText(user.price);//浏览量
        //头像
        Glide.with(context).load(HttpUtil.getImageUrl(user.avatar)).into(itemDoctorBinding.ivHeader);
        itemDoctorBinding.tvLevel.setText(user.level);//专业等级
        itemDoctorBinding.tvServiceCount.setText("服务次数：" + user.service_count);//服务次数
        itemDoctorBinding.tvStyle.setText("风格：" + user.style);//咨询风格
        itemDoctorBinding.tvChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //私聊
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("user_name", users.get(position).username);//传递咨询师用户名
                intent.putExtra("name", users.get(position).name);//传递咨询师姓名
                context.startActivity(intent);//进入聊天页面
            }
        });
        itemDoctorBinding.getRoot().setOnClickListener(v -> {
            //进入咨询师详情页面
            Intent intent = new Intent(context, ViewDoctorActivity.class);
            intent.putExtra("username", users.get(position).username);//将预览数据传递过去
            context.startActivity(intent);
        });
        return itemDoctorBinding.getRoot();
    }
}
