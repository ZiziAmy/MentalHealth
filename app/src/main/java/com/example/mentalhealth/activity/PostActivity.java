package com.example.mentalhealth.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.example.mentalhealth.R;
import com.example.mentalhealth.bean.ApiResponse;
import com.example.mentalhealth.bean.Post;
import com.example.mentalhealth.databinding.ActivityPostBinding;
import com.example.mentalhealth.databinding.ItemImageBinding;
import com.example.mentalhealth.util.GlideEngine;
import com.example.mentalhealth.util.Global;
import com.example.mentalhealth.util.HttpUtil;
import com.example.mentalhealth.util.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

//发布故事页面
public class PostActivity extends BaseActivity implements OnClickListener {
    private ActivityPostBinding activityPostBinding;
    //待上传的图片本地路径
    private final List<String> images = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化界面
        activityPostBinding = DataBindingUtil.setContentView(this, R.layout.activity_post);
        setTitle("发布你的故事");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnAddImage) {
            //添加图片按钮
            PictureSelector.create(this)
                    .openGallery(PictureMimeType.ofImage())
                    .maxSelectNum(10)//最多选择10张
                    .imageEngine(GlideEngine.createGlideEngine())//加载引擎
                    .forResult(PictureConfig.CHOOSE_REQUEST);//启动
        } else if (v.getId() == R.id.btnPost) {
            //发布
            String title = activityPostBinding.etTitle.getText().toString();
            String content = activityPostBinding.etContent.getText().toString();
            if (title.equals("")) {
                toast("请输入标题");
                return;
            }
            if (content.equals("")) {
                toast("请输入内容");
                return;
            }
            //显示对话框
            LoadingDialog.show(this, "发布中...");
            new Thread(() -> {
                //上传图片
                final List<String> imageNames = new ArrayList<>();
                for (String path : images) {
                    ApiResponse<String> apiResponse = HttpUtil.upload(path);//上传图片
                    if (!apiResponse.success()) {
                        toast("图片上传失败，请重试");
                        handler.post(LoadingDialog::dismiss);//关闭对话框
                        return;
                    }
                    //将上传后的图片地址加入列表(此时地址已经变成服务器地址)
                    imageNames.add(apiResponse.data);
                }
                //把所有图片在服务器中的path以分隔符加到同一个string中去
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < imageNames.size(); i++) {
                    if (i != imageNames.size() - 1) {
                        stringBuilder.append(imageNames.get(i)).append("--------TE--------");//加入图片分隔符
                    } else {
                        stringBuilder.append(imageNames.get(i));//不加入图片分隔符
                    }
                }
                //构造发布数据
                Post post = new Post();
                post.title = title;//标题
                post.images=stringBuilder.toString();//图片path连起来的长字符串
                post.username = Global.user.username;//发布人
                post.content = content;//内容
                ApiResponse<String> apiResponse = HttpUtil.post(post);//发布
                if (apiResponse.success()) {
                    setResult(RESULT_OK);
                    finish();
                } else {
                    toast("网络错误，请重试");
                }
                handler.post(LoadingDialog::dismiss);//关闭对话框
            }).start();

        } else if (v.getId() == R.id.ivBack) {
            //返回
            finish();
        }
    }


    //将selectlist中的图片路径写到images中
    private void addImage(List<LocalMedia> localMedia) {
        for (LocalMedia lm : localMedia) {
            //将选择的图片的本地路径，加入到待上传列表
            images.add(lm.getRealPath());
        }
        showImageList();//显示图片
    }

    //显示选择的图片列表
    private void showImageList() {
        activityPostBinding.llImages.removeAllViews();//先移除所有图片
        //循环添加图片
        for (String path : images) {
            //加载图片布局
            ItemImageBinding imageBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.item_image, null, false);
            //使用Glide从本地加载图片
            Glide.with(this).load(path).into(imageBinding.ivImage);//加载图片
            activityPostBinding.llImages.addView(imageBinding.getRoot());//添加到列表
            imageBinding.getRoot().setOnClickListener(v -> {
                //图片的单击事件
                String[] MENU = {"删除", "取消"};//选项
                //显示对话框AlertDialog
                new AlertDialog.Builder(this).setItems(MENU, (dialog, which) -> {
                    if (which == 0) {
                        //删除
                        List<String> tmp = new ArrayList<>();
                        tmp.add(path);
                        images.removeAll(tmp);//移除
                        showImageList();//刷新照片列表
                    }
                }).show();
            });
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
                    addImage(selectList);
                    break;
                default:
                    break;
            }
        }
    }

}