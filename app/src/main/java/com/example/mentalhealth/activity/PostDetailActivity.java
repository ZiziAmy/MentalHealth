package com.example.mentalhealth.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.mentalhealth.MyApplication;
import com.example.mentalhealth.R;
import com.example.mentalhealth.bean.ApiResponse;
import com.example.mentalhealth.bean.OCRResult;
import com.example.mentalhealth.bean.Post;
import com.example.mentalhealth.bean.Reply;
import com.example.mentalhealth.databinding.ActivityPostDetailBinding;
import com.example.mentalhealth.databinding.ItemReplyBinding;
import com.example.mentalhealth.util.Global;
import com.example.mentalhealth.util.HttpUtil;
import com.example.mentalhealth.util.LoadingDialog;
import com.example.mentalhealth.util.ocr.OCRUtil;
import com.google.gson.Gson;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

//故事详情页面
public class PostDetailActivity extends BaseActivity implements View.OnClickListener {
    private Post post;//故事
    private ActivityPostDetailBinding activityPostDetailBinding;
    private String post_uuid;//故事id
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//格式化时间

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityPostDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_post_detail);
        setTitle("故事详情");
        post_uuid = getIntent().getStringExtra("uuid");//获取传递的故事id
        initData();//初始化数据
    }

    //初始化数据
    @SuppressLint("SetTextI18n")
    private void initData() {
        new Thread(() -> {
            ApiResponse<Post> postApiResponse = HttpUtil.viewPost(post_uuid);
            if (!postApiResponse.success()) {
                toast("获取故事详情失败，请重试");
                finish();
                return;
            }
            post = postApiResponse.data;//得到故事详情
            //获取故事的回复列表
            ApiResponse<List<Reply>> apiResponse = HttpUtil.getReplyByPostUUID(post.uuid);
            if (apiResponse.success()) {
                handler.post(() -> {
                    //填充故事详情
                    activityPostDetailBinding.llImages.removeAllViews();//移除图片
                    activityPostDetailBinding.tvContent.setText(post.content);//内容
                    activityPostDetailBinding.tvTitle.setText(post.title);//标题
                    activityPostDetailBinding.tvViewCount.setText("浏览量：" + post.view_count);//浏览量
                    activityPostDetailBinding.tvReplyCount.setText("回复数：" + post.replyList.size());//回复数
                    activityPostDetailBinding.tvNickName.setText(post.username);//用户名
                    //获取发布故事的用户头像
                    Glide.with(this).load(HttpUtil.getImageUrl(post.avatar))
                            .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                            .into(activityPostDetailBinding.ivHead);//显示头像
                    activityPostDetailBinding.tvTime.setText("发布于　" + simpleDateFormat.format(new Date(post.create_time)));
                    if (post.images != null) {
                        //如果图片不是null
                        String[] images = post.images.split(Post.SPLIT);//获取图片
                        //布局参数
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams.topMargin = 5;
                        layoutParams.leftMargin = 3;
                        layoutParams.rightMargin = 3;
                        for (String path : images) {
                            //加载图片布局
                            ImageView imageView = new ImageView(PostDetailActivity.this);//构造图片容器
                            //图片长按事件
                            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {

                                    //图片点击事件
                                    new AlertDialog.Builder(PostDetailActivity.this).setItems(new String[]{"保存", "文字识别"}, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //对话框选择事件
                                            if (which == 0) {
                                                //保存图片到本地
                                                saveImage(path);//保存图片
                                            } else {
                                                //orc文字识别
                                                ocr(path);
                                            }
                                        }
                                    }).create().show();
                                    return true;
                                }
                            });
                            Glide.with(PostDetailActivity.this).load(HttpUtil.getImageUrl(path)).into(imageView);//加载图片
                            activityPostDetailBinding.llImages.addView(imageView, layoutParams);//将图片添加到布局
                        }
                    }
                    //构造回复列表
                    activityPostDetailBinding.llReply.removeAllViews();//先清空
                    int i = 1;//楼层号
                    for (Reply reply : apiResponse.data) {
                        //添加所有回复视图到布局中
                        activityPostDetailBinding.llReply.addView(createView(i, reply));
                        i++;
                    }

                });

            } else {
                toast("故事详情获取失败，请重试");
            }
        }).start();
    }


    //创建回复视图
    @SuppressLint("SetTextI18n")
    private View createView(int i, Reply reply) {
        ItemReplyBinding itemReplyBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.item_reply, null, false);
        itemReplyBinding.tvContent.setText(reply.content);//回复的内容
        itemReplyBinding.tvLevel.setText(i + "楼");//楼层
        itemReplyBinding.tvTime.setText(reply.username + " 回复于 " + simpleDateFormat.format(new Date(reply.create_time)));//回复时间
        return itemReplyBinding.getRoot();
    }

    //保存图片
    private void saveImage(String path) {
        new Thread(() -> {
            //todo 看
            FutureTarget<File> future = Glide
                    .with(PostDetailActivity.this)
                    .load(HttpUtil.getImageUrl(path))
                    .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);

            try {
                File file = future.get();
                String newFileName = "/sdcard/Pictures/" + System.currentTimeMillis() + ".jpg";
                file.renameTo(new File(newFileName));
                try {
                    //发送广播更新相册
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri uri = Uri.fromFile(new File(newFileName));
                    intent.setData(uri);
                    sendBroadcast(intent);
                } catch (Exception ignored) {

                }

                MyApplication.toast("成功保存到" + newFileName);//提示
            } catch (Exception e) {
                e.printStackTrace();
                MyApplication.toast("保存失败，请检查是否赋予了读写储存权限");
            }
        }).start();
    }

    //文字识别
    private void ocr(String path) {
        LoadingDialog.show(this, "文字识别中...");
        new Thread(() -> {
            //Loads the original unmodified data into the cache and returns a java.util.concurrent.Future that can be used to retrieve the cache File containing the data.
            FutureTarget<File> future = Glide
                    .with(PostDetailActivity.this)
                    .load(HttpUtil.getImageUrl(path))
                    .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);

            try {
                File file = future.get();
                //传入缓存结果，返回识别结果
                OCRResult ocrResult = OCRUtil.accurateBasic(file.getPath());//识别结果
                if (ocrResult.words_result_num <= 0) {
                    MyApplication.toast("未在图片中识别到文字");
                } else {
                    //进入新界面
                    Intent intent = new Intent(PostDetailActivity.this, OCRResultActivity.class);
                    intent.putExtra("json", new Gson().toJson(ocrResult));
                    startActivity(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
                MyApplication.toast("文字识别出错，请检查网络设置");
            }
            //关闭对话框
            activityPostDetailBinding.llReply.post(LoadingDialog::dismiss);
        }).start();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvReply) {
            //回复点击事件
            EditText editText = new EditText(this);//构造文本框
            editText.setMaxLines(8);//最多8行
            editText.setTextColor(Color.BLACK);//黑色字体
            editText.setLines(8);//8行
            editText.setHint("在这里输入回复的内容");
            editText.setPadding(10, 10, 10, 10);
            editText.setGravity(Gravity.TOP | Gravity.START);//文字位置
            AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.DialogTheme).setNegativeButton("取消", null).setPositiveButton("回复", (dialog, which) -> {
                String content = editText.getText().toString();
                if (content.equals("")) {
                    toast("请输入要回复的内容");
                    return;
                }
                Reply reply = new Reply();//构造回复数据
                reply.username = Global.user.username;//用户名
                reply.post_uuid = post.uuid;//故事id
                reply.content = content;//内容
                new Thread(() -> {
                    ApiResponse<String> apiResponse = HttpUtil.reply(reply);//回复
                    if (apiResponse.success()) {
                        toast("回复成功");
                        initData();//刷新回复列表
                    } else {
                        toast("网络问题，请重试");
                    }
                }).start();
            }).create();
            alertDialog.setView(editText);//设置文本框到对话框
            alertDialog.setTitle("回复");
            alertDialog.show();//显示回复对话框
        } else if (v.getId() == R.id.ivBack) {
            //返回
            finish();
        }
    }
}