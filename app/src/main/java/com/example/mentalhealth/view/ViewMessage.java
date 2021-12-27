package com.example.mentalhealth.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;

import androidx.databinding.DataBindingUtil;

import com.example.mentalhealth.R;
import com.example.mentalhealth.activity.ChatActivity;
import com.example.mentalhealth.adapter.ChatItemAdapter;
import com.example.mentalhealth.bean.ApiResponse;
import com.example.mentalhealth.bean.NewMessage;
import com.example.mentalhealth.databinding.ViewMessageBinding;
import com.example.mentalhealth.util.Global;
import com.example.mentalhealth.util.HttpUtil;

import java.util.List;


//消息
public class ViewMessage {
    private final ViewMessageBinding fragmentMessageBinding;
    private final Context context;

    public ViewMessage(Context context) {
        this.context = context;
        fragmentMessageBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.view_message, null, false);
        fragmentMessageBinding.lv.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("user_name", newMessages.get(position).user.username);//传递对方用户名
            intent.putExtra("name", newMessages.get(position).user.name);//传递对方用户名
            context.startActivity(intent);//进入聊天页面
        });
        fragmentMessageBinding.lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //删除消息
                String[] MENU = new String[]{"删除", "取消"};
                final String username = newMessages.get(position).user.username;
                new AlertDialog.Builder(context).setItems(MENU, (dialog, which) -> {
                    if (which == 0) {
                        //删除
                        new Thread(() -> {
                            HttpUtil.deleteMessage(Global.user.username, username);//删除消息
                            initData();//刷新
                        }).start();
                    }
                }).show();
                return true;
            }
        });
//        initData();
    }

    public View getRoot() {
        return fragmentMessageBinding.getRoot();
    }

    private boolean status = false;//刷新状态


    private List<NewMessage> newMessages;//聊天及消息列表

    //初始化数据
    private void initData() {
        if (status) {
            return;
        }
        status = true;
        new Thread(() -> {
            ApiResponse<List<NewMessage>> apiResponse = HttpUtil.getMyMessageList(Global.user.username);//获取我的消息列表
            if (apiResponse.success()) {
                newMessages = apiResponse.data;
                handler.post(() -> {
                    fragmentMessageBinding.lv.setAdapter(new ChatItemAdapter(context, apiResponse.data));//刷新消息列表
                });
            }
            status = false;
        }).start();
    }


    public void onResume() {
        handler.post(runnable);//开启消息任务
    }

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            initData();
            handler.postDelayed(this, 2000);//2s后刷新消息
        }
    };
    private Handler handler = new Handler();

    public void onStop() {
        handler.removeCallbacks(runnable);//移除消息任务
    }
}
