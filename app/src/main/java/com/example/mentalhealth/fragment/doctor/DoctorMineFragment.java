package com.example.mentalhealth.fragment.doctor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.example.mentalhealth.databinding.FragmentDoctorMineBinding;
import com.example.mentalhealth.fragment.BaseFragment;
import com.example.mentalhealth.util.Global;
import com.example.mentalhealth.util.HttpUtil;

import java.util.List;

//咨询师-我的页面
public class DoctorMineFragment extends BaseFragment implements View.OnClickListener {
    private FragmentDoctorMineBinding fragmentDoctorMineBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 界面初始化
        fragmentDoctorMineBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_doctor_mine, container, false);
        return fragmentDoctorMineBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    //初始化数据
    @SuppressLint("SetTextI18n")
    private void initData() {
        handler.post(() -> {   //设置点击事件
            fragmentDoctorMineBinding.llExit.setOnClickListener(this);
            fragmentDoctorMineBinding.llFollowMe.setOnClickListener(this);
            fragmentDoctorMineBinding.llPwd.setOnClickListener(this);
            fragmentDoctorMineBinding.llSignOut.setOnClickListener(this);
            fragmentDoctorMineBinding.rlProfile.setOnClickListener(this);
            //填充页面
            fragmentDoctorMineBinding.tvUserName.setText(Global.user.username);//用户名
            Glide.with(this).load(HttpUtil.getImageUrl(Global.user.avatar))
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(fragmentDoctorMineBinding.ivHead);//显示头像

        });
        new Thread(() -> {
            //获取我的粉丝数
            ApiResponse<List<User>> apiResponse1 = HttpUtil.getFollowMeUser(Global.user.username);
            if (apiResponse1.success()) {
                handler.post(() -> {
                    fragmentDoctorMineBinding.tvFollowMeCount.setText(apiResponse1.data.size() + "");//粉丝数
                });
            }
            //点赞数量
            ApiResponse<Integer> apiResponse = HttpUtil.likeCount(Global.user.username);
            if (apiResponse.success()) {
                handler.post(() -> {
                    fragmentDoctorMineBinding.tvLikeCount.setText(apiResponse.data + "");//点赞数量
                });
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.rlProfile) {
            //查看资料
            startActivity(new Intent(getActivity(), ProfileActivity.class));//进入资料页面
        } else if (v.getId() == R.id.llFollowMe) {
            //粉丝列表
            startActivity(new Intent(getActivity(), FansActivity.class));//进入粉丝列表页面
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
