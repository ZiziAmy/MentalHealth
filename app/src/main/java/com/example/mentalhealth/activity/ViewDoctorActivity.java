package com.example.mentalhealth.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.mentalhealth.R;
import com.example.mentalhealth.bean.ApiResponse;
import com.example.mentalhealth.bean.Comments;
import com.example.mentalhealth.bean.Reply;
import com.example.mentalhealth.bean.User;
import com.example.mentalhealth.databinding.ActivityViewDoctorBinding;
import com.example.mentalhealth.databinding.ItemCommentsBinding;
import com.example.mentalhealth.util.Global;
import com.example.mentalhealth.util.HttpUtil;

import java.text.SimpleDateFormat;
import java.util.List;

//查看咨询师页面
public class ViewDoctorActivity extends BaseActivity implements View.OnClickListener {
    private ActivityViewDoctorBinding activityViewDoctorBinding;
    private String username;//咨询师用户名

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化布局
        activityViewDoctorBinding = DataBindingUtil.setContentView(this, R.layout.activity_view_doctor);
        setTitle("咨询师详情");
        username = getIntent().getStringExtra("username");
        initData();//初始化数据
    }

    private User user;//咨询师

    //初始化
    @SuppressLint("SetTextI18n")
    private void initData() {
        new Thread(() -> {
            ApiResponse<User> apiResponse = HttpUtil.getDoctor(username);
            initFollow();//初始化关注
            initLikeCount();//初始化点赞
            initComments();//初始化评价
            if (apiResponse.success()) {
                user = apiResponse.data;
                handler.post(() -> {
                    //更新页面
                    Glide.with(this).load(HttpUtil.getImageUrl(user.avatar))
                            .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                            .into(activityViewDoctorBinding.ivHead);//显示头像
                    activityViewDoctorBinding.tvEdu.setText("教育背景：" + user.edu);
                    activityViewDoctorBinding.tvGood.setText("擅长领域：" + user.good);
                    activityViewDoctorBinding.tvHistory.setText("工作经验：" + user.history);
                    activityViewDoctorBinding.tvPrice.setText("咨询价格：" + user.price);
                    activityViewDoctorBinding.tvName.setText(user.name);//姓名
                    activityViewDoctorBinding.tvLevel.setText("专业等级：" + user.level);
                    activityViewDoctorBinding.tvStyle.setText("咨询风格：" + user.style);
                });
            } else {
                finish();//关闭页面
                toast(apiResponse.message);//提示错误信息
            }
        }).start();
    }

    private boolean isFollow = false;//关注状态

    //初始化关注
    private void initFollow() {
        new Thread(() -> {
            ApiResponse<Boolean> apiResponse = HttpUtil.isFollow(Global.user.username, username);//查询关注状态
            if (apiResponse.success()) {
                isFollow = apiResponse.data;//关注状态
                handler.post(() -> {
                    if (isFollow) {
                        //关注
                        activityViewDoctorBinding.btnFollow.setText("取消关注");
                    } else {
                        activityViewDoctorBinding.btnFollow.setText("关注");
                    }
                });

            } else {
                toast(apiResponse.message);//提示错误信息
            }
            ApiResponse<List<User>> apiResponse1 = HttpUtil.getFollowMeUser(username);//获取关注人数
            if (apiResponse1.success() && apiResponse1.data != null) {
                handler.post(() -> {
                    activityViewDoctorBinding.tvFollowCount.setText(apiResponse1.data.size() + "");
                });
            }
        }).start();
    }

    private boolean isLike = false;//点赞状态

    //初始化点赞数量
    @SuppressLint("SetTextI18n")
    private void initLikeCount() {
        new Thread(() -> {
            ApiResponse<Integer> apiResponse = HttpUtil.likeCount(username);//查询
            if (apiResponse.success() && apiResponse.data != null) {
                handler.post(() -> {
                    activityViewDoctorBinding.tvLikeCount.setText(apiResponse.data + "");//设置数量
                });
            }
            ApiResponse<Boolean> apiResponse1 = HttpUtil.isLike(username, Global.user.username);
            if (apiResponse1.success()) {
                isLike = apiResponse1.data;//点赞状态
                handler.post(() -> {
                    if (isLike) {
                        activityViewDoctorBinding.btnLike.setText("取消点赞");
                    } else {
                        activityViewDoctorBinding.btnLike.setText("点赞");
                    }
                });
            }
        }).start();
    }


    //初始化评价列表
    @SuppressLint("SimpleDateFormat")
    private void initComments() {
        handler.post(() -> {
            activityViewDoctorBinding.llComments.removeAllViews();//先清空列表
        });
        new Thread(() -> {
            ApiResponse<List<Comments>> apiResponse = HttpUtil.getComments(username);//获取评价列表
            handler.post(() -> {
                if (apiResponse.success() && apiResponse.data != null) {
                    if (apiResponse.data.isEmpty()) {
                        activityViewDoctorBinding.tvNo.setVisibility(View.VISIBLE);//显示提示没有评价
                    } else {
                        activityViewDoctorBinding.tvNo.setVisibility(View.GONE);//隐藏提示没有评价
                        //开始构造评价列表
                        for (Comments comments : apiResponse.data) {
                            //加载布局
                            ItemCommentsBinding itemCommentsBinding = DataBindingUtil.inflate(LayoutInflater.from(ViewDoctorActivity.this), R.layout.item_comments, null, false);
                            itemCommentsBinding.tvContent.setText(comments.content);//内容
                            itemCommentsBinding.tvUserName.setText(comments.username);//用户名
                            itemCommentsBinding.tvTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(comments.create_time));//时间
                            Glide.with(this).load(HttpUtil.getImageUrl(comments.avatar))
                                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                                    .into(itemCommentsBinding.ivHead);//显示头像
                            activityViewDoctorBinding.llComments.addView(itemCommentsBinding.getRoot());//加入布局
                        }
                    }
                } else {
                    toast(apiResponse.message);//提示错误信息
                }
            });
        }).start();
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnChat) {
            //私聊
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("user_name", username);//传递咨询师用户名
            intent.putExtra("name", user.name);//传递咨询师姓名
            startActivity(intent);//进入聊天页面
        } else if (v.getId() == R.id.btnFollow) {
            //关注/取消关注
            new Thread(() -> {
                if (!isFollow) {
                    HttpUtil.follow(Global.user.username, username);//关注
                } else {
                    HttpUtil.removeFollow(Global.user.username, username);//取消关注
                }
                initData();//刷新状态
            }).start();
        } else if (v.getId() == R.id.btnComment) {
            //评价点击事件
            EditText editText = new EditText(this);//构造文本框
            editText.setMaxLines(8);//最多8行
            editText.setTextColor(Color.BLACK);//黑色字体
            editText.setLines(8);//8行
            editText.setMaxEms(100);
            editText.setHint("在这里输入评价内容");
            editText.setPadding(10, 10, 10, 10);
            editText.setGravity(Gravity.TOP | Gravity.START);//文字位置
            AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.DialogTheme).setNegativeButton("取消", null).setPositiveButton("评价", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String content = editText.getText().toString();
                    if (content.equals("")) {
                        toast("请输入评价内容");
                        return;
                    }
                    //构造评价内容
                    Comments comments = new Comments();
                    comments.doctor_username = username;
                    comments.username = Global.user.username;
                    comments.content = content;
                    new Thread(() -> {
                        ApiResponse<String> apiResponse = HttpUtil.addComments(comments);//发表评价
                        if (apiResponse.success()) {
                            toast("发表成功");
                            initData();//刷新
                        } else {
                            toast("网络问题，请重试");
                        }
                    }).start();
                }
            }).create();
            alertDialog.setView(editText);//设置文本框到对话框
            alertDialog.setTitle("发表评价");
            alertDialog.show();//显示对话框
        } else if (v.getId() == R.id.btnLike) {
            //点赞/取消点赞
            new Thread(() -> {
                ApiResponse<String> apiResponse;
                new Thread(() -> {
                    if (isLike) {
                        //取消点赞
                        HttpUtil.unLike(username, Global.user.username);
                    } else {
                        //点赞
                        HttpUtil.like(username, Global.user.username);
                    }
                    initData();//刷新数据
                }).start();
            }).start();
        } else if (v.getId() == R.id.btnAppointment) {
            //预约
            Intent intent = new Intent(this, AppointmentActivity.class);
            intent.putExtra("doctor_username", username);
            startActivity(intent);
        }
    }

}