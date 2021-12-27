package com.example.mentalhealth.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.databinding.DataBindingUtil;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.mentalhealth.R;
import com.example.mentalhealth.bean.NewMessage;
import com.example.mentalhealth.databinding.ItemFriendBinding;
import com.example.mentalhealth.util.Global;
import com.example.mentalhealth.util.HttpUtil;

import java.util.List;

//聊天列表适配器
public class ChatItemAdapter extends BaseAdapter {
    private Context context;
    private List<NewMessage> newMessages;

    public ChatItemAdapter(Context context, List<NewMessage> newMessages) {
        this.context = context;
        this.newMessages = newMessages;
    }

    @Override
    //获取消息条数
    public int getCount() {
        return newMessages.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        @SuppressLint("ViewHolder")
        ItemFriendBinding itemFriendBinding
                = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_friend, null, false);
        if (Global.user.user_type == 1) {
            //当前是咨询师
            itemFriendBinding.tvName.setText(newMessages.get(position).user.username);//用户
        } else {
            //当前是用户
            itemFriendBinding.tvName.setText(newMessages.get(position).user.name);//咨询师名字
        }

        //未读消息队列非空时
        if (newMessages.get(position).unReadMessagesList != null && !newMessages.get(position).unReadMessagesList.isEmpty()) {
            itemFriendBinding.tvUnRead.setText(newMessages.get(position).unReadMessagesList.size() + "");//未读消息数量
            itemFriendBinding.tvUnRead.setVisibility(View.VISIBLE);//显示未读消息数量
        } else {
            itemFriendBinding.tvUnRead.setVisibility(View.GONE);//隐藏未读消息数量
        }
        //load(url),此处url为图片在服务器中地址
        Glide.with(context).load(HttpUtil.getImageUrl(newMessages.get(position).user.avatar))
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(itemFriendBinding.ivHead);//显示头像
        if (newMessages.get(position).messagesList != null && !newMessages.get(position).messagesList.isEmpty()) {
            //展示最后一条消息
            itemFriendBinding.tvMessage.setText(newMessages.get(position).messagesList.get(newMessages.get(position).messagesList.size() - 1).content);
        }
        return itemFriendBinding.getRoot();
    }
}
