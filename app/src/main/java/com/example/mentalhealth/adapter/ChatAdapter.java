package com.example.mentalhealth.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.databinding.DataBindingUtil;


import com.example.mentalhealth.R;
import com.example.mentalhealth.bean.Messages;
import com.example.mentalhealth.databinding.ItemChatLeftBinding;
import com.example.mentalhealth.databinding.ItemChatRightBinding;
import com.example.mentalhealth.util.Global;

import java.util.List;

//聊天中消息适配器
public class ChatAdapter extends BaseAdapter {
    //上下文 此处为创建adapter的activity
    private Context context;
    private List<Messages> messages;

    public ChatAdapter(Context context, List<Messages> messages) {
        this.messages = messages;
        this.context = context;
    }

    public void setNewData(List<Messages> messages) {
        this.messages = messages;
    }

    @Override
    //getCount方法是程序在加载显示到ui上时就要先读取的，这里获得的值决定了listview显示多少行
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    //对每一条消息来说要怎么显示
    public View getView(int position, View convertView, ViewGroup parent) {
        //获取消息位置
        Messages message = messages.get(position);
        if (Global.user.username.equals(message.send_user_id)) {
            //本人
            //通过上下文（activity）得到inflater实例
            ItemChatRightBinding itemChatRightBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_chat_right, null, false);
            //从binding中获取控件设置消息内容
            itemChatRightBinding.tvContent.setText(message.content);//消息内容
            return itemChatRightBinding.getRoot();
        } else {
            //对方
            //从context中返回inflater并与布局界面绑定
            ItemChatLeftBinding itemChatLeftBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_chat_left, null, false);
            itemChatLeftBinding.tvContent.setText(message.content);//消息内容
            //绘制好后的view最后return返回给getView方法
            return itemChatLeftBinding.getRoot();
        }
    }
}
