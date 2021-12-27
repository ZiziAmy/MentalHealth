package com.example.mentalhealth.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.mentalhealth.R;
import com.example.mentalhealth.databinding.ActivityProfileBinding;
import com.example.mentalhealth.util.Global;
import com.example.mentalhealth.util.HttpUtil;

//个人资料页面
public class ProfileActivity extends BaseActivity implements OnClickListener {
    private ActivityProfileBinding profileBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化布局
        profileBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        setTitle("个人资料");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //填充页面
        profileBinding.tvUserName.setText(Global.user.username);//用户名
        Glide.with(this).load(HttpUtil.getImageUrl(Global.user.avatar))
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(profileBinding.ivHead);//显示头像
        profileBinding.tvGender.setText(Global.user.gender == 0 ? "男" : "女");//性别
        profileBinding.tvMail.setText(Global.user.mail);//电子邮件
        profileBinding.tvMobile.setText(Global.user.phone);//电话
        if (Global.user.user_type == 1) {
            //咨询师
            profileBinding.tvEdu.setText(Global.user.edu);//教育背景
            profileBinding.tvGood.setText(Global.user.good);//擅长领域
            profileBinding.tvStyle.setText(Global.user.style);//咨询风格
            profileBinding.tvHistory.setText(Global.user.history);//工作经验
            profileBinding.tvPrice.setText(Global.user.price);//价格
            profileBinding.tvName.setText(Global.user.name);//姓名
            profileBinding.tvLevel.setText(Global.user.level);//专业等级
        } else {
            profileBinding.llDoctor.setVisibility(View.GONE);//隐藏咨询师属性
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnBack) {
            //返回
            finish();
        } else if (v.getId() == R.id.btnChange) {
            //修改资料
            startActivity(new Intent(this, EditProfileActivity.class));//进入资料修改页面
        }
    }
}