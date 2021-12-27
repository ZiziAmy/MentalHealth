package com.example.mentalhealth.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.mentalhealth.MyApplication;
import com.example.mentalhealth.R;
import com.example.mentalhealth.bean.ApiResponse;
import com.example.mentalhealth.bean.User;
import com.example.mentalhealth.databinding.ActivityRegisterBinding;
import com.example.mentalhealth.util.GlideEngine;
import com.example.mentalhealth.util.HttpUtil;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//注册页面
public class RegisterActivity extends BaseActivity implements OnClickListener {

    private ActivityRegisterBinding activityRegisterBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化布局
        activityRegisterBinding = DataBindingUtil.setContentView(this, R.layout.activity_register);
        setTitle("用户注册");//设置标题
    }

    private String filename = "";//头像

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.rbUser) {
            //用户
            activityRegisterBinding.llDoctor.setVisibility(View.GONE);
        } else if (v.getId() == R.id.rbDoctor) {
            //咨询师
            activityRegisterBinding.llDoctor.setVisibility(View.VISIBLE);
        } else if (v.getId() == R.id.btnOK) {
            //注册按钮
            //获取注册的信息
            String username = activityRegisterBinding.etName.getText().toString();//用户名
            String mobile = activityRegisterBinding.etMobile.getText().toString();//电话
            String mail = activityRegisterBinding.etMail.getText().toString();//邮箱
            String pwd1 = activityRegisterBinding.etPwd1.getText().toString();//密码
            String pwd2 = activityRegisterBinding.etPwd2.getText().toString();

            //咨询师属性
            String name = activityRegisterBinding.etRealName.getText().toString();//姓名
            String level = activityRegisterBinding.etLevel.getText().toString();//专业等级
            String edu = activityRegisterBinding.etEdu.getText().toString();//教育背景
            String good = activityRegisterBinding.etGood.getText().toString();//擅长领域
            String style = activityRegisterBinding.etStyle.getText().toString();//咨询风格
            String history = activityRegisterBinding.etHistory.getText().toString();//工作经验
            String price = activityRegisterBinding.etPrice.getText().toString();//咨询价格
            String code=activityRegisterBinding.etCode.getText().toString();//验证码
            //判断输入信息完整性
            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(mobile) || TextUtils.isEmpty(mail) || TextUtils.isEmpty(filename) || TextUtils.isEmpty(pwd1) || TextUtils.isEmpty(pwd2)) {
                toast("请将资料填写完整");
                return;
            }
            if(TextUtils.isEmpty(code)){
                toast("请填写验证码");
                return;
            }
            if (activityRegisterBinding.rbDoctor.isChecked()) {
                //咨询师，还需判断其他内容
                //判断输入信息完整性
                if (TextUtils.isEmpty(name) | TextUtils.isEmpty(level) || TextUtils.isEmpty(edu) || TextUtils.isEmpty(good)
                        || TextUtils.isEmpty(style) || TextUtils.isEmpty(history) || TextUtils.isEmpty(price)) {
                    toast("请将资料填写完整");
                    return;
                }
            }
            //校验两次密码一致性
            if (pwd1.equals(pwd2)) {
                //进行注册数据构造
                User user = new User();
                user.username = username;
                user.gender = activityRegisterBinding.rb1.isChecked() ? 0 : 1;//性别
                user.password = pwd1;
                user.mail = mail;
                user.phone = mobile;
                user.user_type = activityRegisterBinding.rbUser.isChecked() ? 0 : 1;//类型
                user.mail_code=code;
                if (activityRegisterBinding.rbDoctor.isChecked()) {
                    //咨询师的字段填充
                    user.style = style;
                    user.name = name;
                    user.edu = edu;
                    user.history = history;
                    user.price = price;
                    user.good = good;
                    user.level = level;
                }
                new Thread(() -> {
                    //先上传头像，filename为裁剪后的本地路径
                    ApiResponse<String> avatarResponse = HttpUtil.upload(filename);
                    //上传成功时
                    if (avatarResponse.success()) {
                        ////得到头像在服务器中的名称
                        user.avatar = avatarResponse.data;
                        ApiResponse<String> apiResponse = HttpUtil.register(user);//提交注册
                        if (apiResponse.success()) {
                            finish();//注册成功，关闭当前界面
                        }
                        toast(apiResponse.message);//提示服务器的应答
                    } else {
                        toast(avatarResponse.message);//上传不成功
                    }

                }).start();
            } else {
                //两次输入的密码不一致
                toast("两次密码不一样");
            }
        } else if (v.getId() == R.id.ivBack) {
            //返回
            finish();
            //三个按钮的点击事件
        } else if (v.getId() == R.id.rlHead || v.getId() == R.id.ivHead || v.getId() == R.id.tvHead) {
            //头像选择事件
            //选择头像
            PictureSelector.create(this)
                    .openGallery(PictureMimeType.ofImage())//选择图片
                    .maxSelectNum(1)//最多选择一张
                    .isEnableCrop(true).showCropGrid(true).withAspectRatio(1, 1)//启用裁剪
                    .imageEngine(GlideEngine.createGlideEngine())//加载引擎
                    .forResult(PictureConfig.CHOOSE_REQUEST);//启动应答
        } else if (v.getId() == R.id.btnCode) {
            //发送验证码
            sendCode();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    if (selectList.size() == 1) {
                        filename = selectList.get(0).getCutPath();//头像裁剪后的路径
                        //图片加载框架Glide
                        Glide.with(this).load(filename)
                                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                                .into(activityRegisterBinding.ivHead);//显示头像
                    }
                    break;
                default:
                    break;
            }
        }
    }

    //发送验证码
    private void sendCode() {
        new Thread(() -> {
            String username = activityRegisterBinding.etName.getText().toString();
            if (username.equals("")) {
                MyApplication.toast("请输入用户名");
            } else {
                // 邮箱验证规则
                String regEx = "[a-zA-Z0-9_-]+@\\w+\\.[a-z]+(\\.[a-z]+)?";
                // 编译正则表达式
                Pattern pattern = Pattern.compile(regEx);
                Matcher matcher = pattern.matcher(activityRegisterBinding.etMail.getText().toString());
                if (matcher.matches()) {
                    //传入的是用户名，邮箱
                    ApiResponse<String> apiResponse = HttpUtil.getCode(username, activityRegisterBinding.etMail.getText().toString());
                    if (apiResponse.success()) {
                        //发送成功
                        second = TOTAL;
                        //更新界面
                        handler.post(runnable);
                    }
                    MyApplication.toast(apiResponse.message);//提示
                } else {
                    MyApplication.toast("请输入正确的邮箱地址");
                }
            }

        }).start();
    }

    private final Handler handler = new Handler();
    private int second = 0;//倒计时
    private final int TOTAL = 60;//总倒计时

    //倒计时
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            second--;
            activityRegisterBinding.btnCode.post(() -> {
                //更新按钮倒计时
                if (second == 0) {
                    activityRegisterBinding.btnCode.setEnabled(true);
                    activityRegisterBinding.btnCode.setText("获取验证码");
                } else {
                    activityRegisterBinding.btnCode.setEnabled(false);
                    activityRegisterBinding.btnCode.setText("倒计时" + second + "s");
                    handler.postDelayed(this, 1000);//循环
                }
            });
        }
    };
}