package com.example.mentalhealth.activity;


import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.example.mentalhealth.MyApplication;
import com.example.mentalhealth.R;
import com.example.mentalhealth.bean.ApiResponse;
import com.example.mentalhealth.bean.User;
import com.example.mentalhealth.databinding.ActivityLoginBinding;
import com.example.mentalhealth.util.Global;
import com.example.mentalhealth.util.HttpUtil;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

//登录页面
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private ActivityLoginBinding activityLoginBinding;
    private boolean mailLogin = false;//邮箱登录

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化布局
        activityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        //设置标题
        setTitle("用户登录");
        //测试数据
//        activityLoginBinding.etName.setText("test");
//        activityLoginBinding.etPwd.setText("123456");
        //权限检测
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) != PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
        ) != PERMISSION_GRANTED
        ) {
            //申请读写和相机权限
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    1
            );
        }

    }

    @Override
    public void onClick(View v) {
        //登录按钮
        if (v.getId() == R.id.btnLogin) {
            //获取用户输入的用户名和密码
            String username = activityLoginBinding.etName.getText().toString() + "";
            String password = activityLoginBinding.etPwd.getText().toString() + "";

            //构造登录数据
            User user = new User();
            //TODO 联网操作，不可以在UI主线程中进行，所以通过子线程进行，后面涉及相关的联网操作，都使用此类型方法，不再赘述
            new Thread(() -> {
                ApiResponse<User> userApiResponse;
                if (mailLogin) {
                    //邮箱验证码登录
                    //TODO ApiResponse是一个泛型数据，所有与服务器通信需要应答的请求，服务器统一返回此类型数据，可以减少很多数据判断的工作量
                    user.mail = activityLoginBinding.etName.getText().toString();//邮箱
                    user.mail_code = activityLoginBinding.etCode.getText().toString();//验证码
                    userApiResponse = HttpUtil.login_email(user);//邮箱验证码登录
                } else {
                    //常规密码登录
                    user.username = username;
                    user.password = password;
                    userApiResponse = HttpUtil.login(user);//常规登录
                }

                if (userApiResponse.success()) {//TODO 通过success()判断服务器的处理结果，成功，则有数据，数据从data可以得到
                    //登录成功
                    //进行用户身份判断
                    if (activityLoginBinding.rb2.isChecked()) {
                        //选择了咨询师身份
                        if (userApiResponse.data.user_type == 1) {
                            //判断是咨询师
                            Global.user = userApiResponse.data;//缓存用户信息
                            finish();
                            //进入咨询师页面
                            startActivity(new Intent(LoginActivity.this, DoctorActivity.class));
                        } else {
                            toast("你不是咨询师，无法登录，请以用户身份登录");
                        }
                    } else {
                        //用户身份
                        if (userApiResponse.data.user_type != 0) {
                            toast("你是咨询师，请以咨询师身份登录");
                            return;
                        }
                        Global.user = userApiResponse.data;//缓存用户信息
                        finish();
                        //进入普通用户页面
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }
                } else {
                    toast(userApiResponse.message);//提示错误信息
                }
            }).start();
        } else if (v.getId() == R.id.tvReg) {
            //注册按钮，进入注册页面
            startActivity(new Intent(this, RegisterActivity.class));
        } else if (v.getId() == R.id.btnMailLogin) {
            //邮箱登录切换按钮
            if (mailLogin) {
                //切换回密码登录
                activityLoginBinding.etName.setHint("请输入用户名");
                activityLoginBinding.etPwd.setVisibility(View.VISIBLE);//显示密码框
                activityLoginBinding.llCode.setVisibility(View.GONE);//隐藏验证码
                activityLoginBinding.btnMailLogin.setText("账号密码登录");
                mailLogin=false;
            } else {
                activityLoginBinding.etName.setHint("请输入邮箱地址");
                activityLoginBinding.llCode.setVisibility(View.VISIBLE);//显示验证码
                activityLoginBinding.btnMailLogin.setText("邮箱验证码登录");
                activityLoginBinding.etPwd.setVisibility(View.GONE);//隐藏示密码框
                activityLoginBinding.txPassword.setVisibility(View.GONE);//隐藏密码字样
                mailLogin=true;
            }
        } else if (v.getId() == R.id.btnCode) {
            //获取验证码
            sendCode();
        }
    }

    //发送验证码
    private void sendCode() {
        //在子线程中进行联网操作
        new Thread(() -> {
            // 邮箱验证规则
            String regEx = "[a-zA-Z0-9_-]+@\\w+\\.[a-z]+(\\.[a-z]+)?";
            // 编译正则表达式
            Pattern pattern = Pattern.compile(regEx);
            Matcher matcher = pattern.matcher(activityLoginBinding.etName.getText().toString());
            if (matcher.matches()) {

                //传入的是null，邮箱（此时中用户名栏中打的是邮箱）
                ApiResponse<String> apiResponse = HttpUtil.getCode(null, activityLoginBinding.etName.getText().toString());
                if (apiResponse.success()) {
                    //发送成功
                    second = TOTAL;
                    handler.post(runnable);
                }
                MyApplication.toast(apiResponse.message);//提示
            } else {
                MyApplication.toast("请输入正确的邮箱地址");
            }

        }).start();
    }

    private final Handler handler = new Handler();
    private int second = 0;//倒计时
    private final int TOTAL = 60;//总倒计时

    //handler.post中runnable在主线程中执行
    //倒计时
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            second--;
            activityLoginBinding.btnCode.post(() -> {
                //更新按钮倒计时
                if (second == 0) {
                    activityLoginBinding.btnCode.setEnabled(true);
                    activityLoginBinding.btnCode.setText("获取验证码");
                } else {
                    activityLoginBinding.btnCode.setEnabled(false);
                    activityLoginBinding.btnCode.setText("倒计时" + second + "s");
                    //不会阻塞主线程
                    handler.postDelayed(this, 1000);//循环
                }
            });
        }
    };
}
