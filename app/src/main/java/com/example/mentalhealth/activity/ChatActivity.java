package com.example.mentalhealth.activity;

import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;


import com.example.mentalhealth.R;
import com.example.mentalhealth.adapter.ChatAdapter;
import com.example.mentalhealth.bean.ApiResponse;
import com.example.mentalhealth.bean.Messages;
import com.example.mentalhealth.databinding.ActivityChatBinding;
import com.example.mentalhealth.util.Global;
import com.example.mentalhealth.util.HttpUtil;

import java.util.List;

//聊天界面
public class ChatActivity extends BaseActivity implements View.OnClickListener {
    private ActivityChatBinding activityChatBinding;
    private String receive_username;//对方名字

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化布局
        activityChatBinding = DataBindingUtil.setContentView(this, R.layout.activity_chat);
        //从intent中取值
        receive_username = getIntent().getStringExtra("user_name");//对方名
        String name = getIntent().getStringExtra("name");//对方姓名
        if (Global.user.user_type == 1) {
            //是咨询师时
            //用户昵称
            setTitle("与" + receive_username + "的聊天");
        } else {
            //用户时显示
            //咨询师真名
            setTitle("与" + name + "的聊天");
        }
        handler.post(runnable);//开启消息监听任务
    }

    private List<Messages> messagesList;//聊天记录
    private boolean isRun = false;//是否在刷新
    private ChatAdapter chatAdapter;//聊天列表适配器
    private int last = 0;

    //初始化数据
    private void initData() {
        //正在刷新则返回
        if (isRun) {
            return;
        }
        isRun = true;
        new Thread(() -> {
            HttpUtil.read(Global.user.username, receive_username);//清除未读消息
            ApiResponse<List<Messages>> apiResponse = HttpUtil.getMessages(Global.user.username, receive_username);//获取与对方的消息
            if (apiResponse.success()) {
                messagesList = apiResponse.data;//聊天记录
                handler.post(() -> {
                    if (chatAdapter == null) {
                        chatAdapter = new ChatAdapter(ChatActivity.this, messagesList);
                        //给listView设置adapter
                        activityChatBinding.lv.setAdapter(chatAdapter);
                    } else {
                        chatAdapter.setNewData(messagesList);//设置消息列表
                        chatAdapter.notifyDataSetChanged();//通知activity刷新聊天记录（listview）
                    }


                    //最后一条记录的位置
                    if (last != messagesList.size()) {
                        activityChatBinding.lv.setSelection(messagesList.size() - 1);//切换最后一条记录
                    }
                    last = messagesList.size();
                });
            }
            isRun = false;
        }).start();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnSend) {
            String txt = activityChatBinding.etContent.getText().toString();//待发送的内容
            if (!txt.equals("")) {
                Messages messages = new Messages();//构造消息
                messages.content = txt;//内容
                messages.receive_user_id = receive_username;//收消息人
                messages.send_user_id = Global.user.username;//发消息人
                new Thread(() -> {
                    ApiResponse<String> apiResponse = HttpUtil.sendMessages(messages);
                    if (!apiResponse.success()) {
                        //没发成功，提示
                        toast(apiResponse.message);
                    } else {
                        handler.post(() -> {
                            activityChatBinding.etContent.setText("");//清空文本框
                        });
                        initData();//刷新消息列表
                    }
                }).start();
            }

        }
    }

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            initData();
            handler.postDelayed(this, 2000);//2s后刷新消息
        }
    };

    @Override
    public void finish() {
        super.finish();
        handler.removeCallbacks(runnable);//移除消息监听任务
    }
}