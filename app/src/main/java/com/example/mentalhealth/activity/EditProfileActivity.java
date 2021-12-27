package com.example.mentalhealth.activity;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.mentalhealth.R;
import com.example.mentalhealth.bean.ApiResponse;
import com.example.mentalhealth.bean.User;
import com.example.mentalhealth.databinding.ActivityEdiProfileBinding;
import com.example.mentalhealth.util.GlideEngine;
import com.example.mentalhealth.util.Global;
import com.example.mentalhealth.util.HttpUtil;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.List;

//修改资料
public class EditProfileActivity extends BaseActivity implements View.OnClickListener {

    private ActivityEdiProfileBinding activityEdiProfileBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化布局
        activityEdiProfileBinding = DataBindingUtil.setContentView(this, R.layout.activity_edi_profile);
        setTitle("资料编辑");//设置标题
        if (Global.user.gender == 0) {
            //男
            activityEdiProfileBinding.rb1.setChecked(true);
        } else {
            //女
            activityEdiProfileBinding.rb2.setChecked(true);
        }
        activityEdiProfileBinding.etMobile.setText(Global.user.phone);//手机号
        activityEdiProfileBinding.etMail.setText(Global.user.mail);//邮箱
        Glide.with(this).load(HttpUtil.getImageUrl(Global.user.avatar))
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(activityEdiProfileBinding.ivHead);//显示头像
        if (Global.user.user_type == 0) {
            activityEdiProfileBinding.llDoctor.setVisibility(View.GONE);//隐藏咨询师布局
        }
        if (Global.user.user_type == 1) {
            activityEdiProfileBinding.llDoctor.setVisibility(View.VISIBLE);//显示咨询师页面

            activityEdiProfileBinding.etLevel.setText(Global.user.level);//专业等级
            activityEdiProfileBinding.etEdu.setText(Global.user.edu);//教育背景
            activityEdiProfileBinding.etRealName.setText(Global.user.name);//姓名
            activityEdiProfileBinding.etGood.setText(Global.user.good);//擅长
            activityEdiProfileBinding.etHistory.setText(Global.user.history);//工作经验
            activityEdiProfileBinding.etPrice.setText(Global.user.price);//价格
            activityEdiProfileBinding.etStyle.setText(Global.user.style);//风格
        }
    }

    private String filename = "";//头像

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnOK) {
            //按钮
            //获取的信息
            String mobile = activityEdiProfileBinding.etMobile.getText().toString();//电话
            String mail = activityEdiProfileBinding.etMail.getText().toString();//邮箱

            //咨询师属性
            String name = activityEdiProfileBinding.etRealName.getText().toString();//姓名
            String level = activityEdiProfileBinding.etLevel.getText().toString();//专业等级
            String edu = activityEdiProfileBinding.etEdu.getText().toString();//教育背景
            String good = activityEdiProfileBinding.etGood.getText().toString();//擅长领域
            String style = activityEdiProfileBinding.etStyle.getText().toString();//咨询风格
            String history = activityEdiProfileBinding.etHistory.getText().toString();//工作经验
            String price = activityEdiProfileBinding.etPrice.getText().toString();//咨询价格

            //判断输入信息完整性
            if (TextUtils.isEmpty(mobile) || TextUtils.isEmpty(mail)) {
                toast("请将资料填写完整");
                return;
            }
            if (Global.user.user_type == 1) {
                //咨询师，还需判断其他内容
                //判断输入信息完整性
                if (TextUtils.isEmpty(name) | TextUtils.isEmpty(level) || TextUtils.isEmpty(edu) || TextUtils.isEmpty(good)
                        || TextUtils.isEmpty(style) || TextUtils.isEmpty(history) || TextUtils.isEmpty(price)) {
                    toast("请将资料填写完整");
                    return;
                }
            }
            //进行数据构造
            User user = new User();
            user.gender = activityEdiProfileBinding.rb1.isChecked() ? 0 : 1;//性别
            user.mail = mail;
            user.username = Global.user.username;//用户名
            user.phone = mobile;
            user.password = Global.user.password;//密码
            user.user_type = Global.user.user_type;//类型
            user.avatar = Global.user.avatar;//头像
            if (Global.user.user_type == 1) {
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
                //先上传头像
                if (!filename.equals("")) {
                    ApiResponse<String> avatarResponse = HttpUtil.upload(filename);
                    if (avatarResponse.success()) {
                        user.avatar = avatarResponse.data;//得到头像地址
                        ApiResponse<User> apiResponse = HttpUtil.update(user);//提交
                        if (apiResponse.success()) {
                            Global.user = apiResponse.data;
                            finish();//成功，关闭当前界面
                        }
                        toast(apiResponse.message);//提示服务器的应答
                    } else {
                        toast(avatarResponse.message);//上传不成功
                    }
                } else {
                    //不需要改头像
                    ApiResponse<User> apiResponse = HttpUtil.update(user);//更新资料
                    if (apiResponse.success()) {
                        Global.user = apiResponse.data;
                        finish();//成功，关闭当前界面
                    }
                    toast(apiResponse.message);//提示服务器的应答
                }

            }).start();

        } else if (v.getId() == R.id.ivBack) {
            //返回
            finish();
        } else if (v.getId() == R.id.rlHead || v.getId() == R.id.ivHead || v.getId() == R.id.tvHead) {
            //头像选择事件
            //选择头像
            PictureSelector.create(this)
                    .openGallery(PictureMimeType.ofImage())
                    .maxSelectNum(1)//最多选择一张
                    .isEnableCrop(true).showCropGrid(true).withAspectRatio(1, 1)//启用裁剪
                    .imageEngine(GlideEngine.createGlideEngine())//加载引擎
                    .forResult(PictureConfig.CHOOSE_REQUEST);//启动
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PictureConfig.CHOOSE_REQUEST) {// 图片选择结果回调
            List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
            if (selectList.size() == 1) {
                filename = selectList.get(0).getCutPath();//头像路径
                Glide.with(this).load(filename)
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .into(activityEdiProfileBinding.ivHead);//显示头像
            }
        }
    }
}