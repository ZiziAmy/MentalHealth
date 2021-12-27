package com.example.mentalhealth.activity;

import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;

import com.example.mentalhealth.R;
import com.example.mentalhealth.bean.ApiResponse;
import com.example.mentalhealth.bean.User;
import com.example.mentalhealth.databinding.ActivityChangePasswordBinding;
import com.example.mentalhealth.util.Global;
import com.example.mentalhealth.util.HttpUtil;

//修改密码页面
public class ChangePasswordActivity extends BaseActivity implements View.OnClickListener {
    private ActivityChangePasswordBinding activityChangePasswordBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化布局
        activityChangePasswordBinding = DataBindingUtil.setContentView(this, R.layout.activity_change_password);
        setTitle("修改密码");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnBack) {
            //返回
            finish();
        } else if (v.getId() == R.id.btnChange) {
            //修改密码
            //原密码
            String oldPwd = activityChangePasswordBinding.etOldPwd.getText().toString();
            //新密码
            String newPwd1 = activityChangePasswordBinding.etPwd.getText().toString();
            String newPwd2 = activityChangePasswordBinding.etPWd1.getText().toString();

            if (!newPwd1.equals(newPwd2)) {
                toast("两次密码输入不一致");
                return;
            }
            if (!oldPwd.equals(Global.user.password)) {
                toast("原密码不正确");
                return;
            }
            new Thread(() -> {
                //修改密码
                ApiResponse<User> userApiResponse = HttpUtil.changePassword(Global.user.username, oldPwd, newPwd1);
                if (userApiResponse.success()) {
                    Global.user = userApiResponse.data;//赋值最新用户信息
                    finish();//修改成功，关闭页面
                }
                toast(userApiResponse.message);//提示信息
            }).start();
        }
    }
}