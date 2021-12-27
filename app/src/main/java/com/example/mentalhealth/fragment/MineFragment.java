package com.example.mentalhealth.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.mentalhealth.R;
import com.example.mentalhealth.activity.ChangePasswordActivity;
import com.example.mentalhealth.activity.EditProfileActivity;
import com.example.mentalhealth.activity.FansActivity;
import com.example.mentalhealth.activity.LoginActivity;
import com.example.mentalhealth.activity.MyAppointmentMainActivity;
import com.example.mentalhealth.activity.MyFollowActivity;
import com.example.mentalhealth.activity.MyPostActivity;
import com.example.mentalhealth.activity.ProfileActivity;
import com.example.mentalhealth.bean.ApiResponse;
import com.example.mentalhealth.bean.Appointment;
import com.example.mentalhealth.bean.User;
import com.example.mentalhealth.databinding.FragmentMineBinding;
import com.example.mentalhealth.util.Global;
import com.example.mentalhealth.util.HttpUtil;

import java.util.List;

//我的页面
public class MineFragment extends BaseFragment implements View.OnClickListener {
    private FragmentMineBinding fragmentMineBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 界面初始化
        fragmentMineBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_mine, container, false);
        return fragmentMineBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    //初始化数据
    @SuppressLint("SetTextI18n")
    private void initData() {
        handler.post(() -> {   //设置点击事件
            fragmentMineBinding.llExit.setOnClickListener(this);
            fragmentMineBinding.llAppointment.setOnClickListener(this);
            fragmentMineBinding.llMyFollow.setOnClickListener(this);
            fragmentMineBinding.llPublish.setOnClickListener(this);
            fragmentMineBinding.llPwd.setOnClickListener(this);
            fragmentMineBinding.llSignOut.setOnClickListener(this);
            fragmentMineBinding.rlProfile.setOnClickListener(this);
            //填充页面
            fragmentMineBinding.tvUserName.setText(Global.user.username);//用户名
            Glide.with(this).load(HttpUtil.getImageUrl(Global.user.avatar))
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(fragmentMineBinding.ivHead);//显示头像
        });
        new Thread(() -> {
            //获取我的的预约
            ApiResponse<List<Appointment>> apiResponse1 = HttpUtil.getUserAppointment(Global.user.username);
            if (apiResponse1.success()) {
                handler.post(() -> {
                    fragmentMineBinding.tvAppointmentCount.setText(apiResponse1.data.size() + "");//预约数
                });
            }
            //获取我关注的人
            ApiResponse<List<User>> apiResponse = HttpUtil.getMyFollowUser(Global.user.username);
            if (apiResponse.success()) {
                handler.post(() -> {
                    fragmentMineBinding.tvMyFollowCount.setText(apiResponse.data.size() + "");//关注的人数量
                });
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.rlProfile) {
            //查看资料
            startActivity(new Intent(getActivity(), ProfileActivity.class));//进入资料页面
        } else if (v.getId() == R.id.llPublish) {
            //我发布的故事 点击事件
            startActivity(new Intent(getActivity(), MyPostActivity.class));//进入我发布故事的页面
        } else if (v.getId() == R.id.llAppointment) {
            //我的预约
            startActivity(new Intent(getActivity(), MyAppointmentMainActivity.class));//进入我的预约页面
        } else if (v.getId() == R.id.llMyFollow) {
            //我关注的
            startActivity(new Intent(getActivity(), MyFollowActivity.class));//进入我的关注页面
        } else if (v.getId() == R.id.llExit) {
            //退出
            getActivity().finish();
        } else if (v.getId() == R.id.llSignOut) {
            //注销登录
            startActivity(new Intent(getActivity(), LoginActivity.class));//进入登录页面
            getActivity().finish();
            Global.user = null;//清空本地用户信息
        } else if (v.getId() == R.id.llPwd) {
            //修改密码
            startActivity(new Intent(getActivity(), ChangePasswordActivity.class));//修改密码页面
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        initData();//刷新数据
    }
}
