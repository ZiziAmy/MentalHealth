package com.example.mentalhealth.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.mentalhealth.R;
import com.example.mentalhealth.bean.Post;
import com.example.mentalhealth.databinding.ItemPostBinding;
import com.example.mentalhealth.util.HttpUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

//故事列表适配器
public class PostAdapter extends BaseAdapter {
    private Context context;
    private List<Post> postList;

    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @Override
    public int getCount() {
        return postList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//格式化时间

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //加载布局
        @SuppressLint("ViewHolder") ItemPostBinding itemPostBinding =
                DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_post, null, false);
        Post post = postList.get(position);
        itemPostBinding.tvViewCount.setText("浏览量：" + post.view_count);//浏览量
        if (post.images != null) {
            String[] images = post.images.split(Post.SPLIT);///获取故事图片
            if (images.length > 0) {
                //将第一张图，作为封面
                Glide.with(context).load(HttpUtil.getImageUrl(images[0])).into(itemPostBinding.ivHeader);
            } else {
                itemPostBinding.ivHeader.setVisibility(View.GONE);//没有则隐藏封面图
            }
        } else {
            itemPostBinding.ivHeader.setVisibility(View.GONE);//没有则隐藏封面图
        }
        itemPostBinding.tvTime.setText(post.username + " 发布于 " + simpleDateFormat.format(new Date(post.create_time)));//发布日期
        itemPostBinding.tvTitle.setText(post.title);//标题
        itemPostBinding.tvContent.setText(post.content);//内容
        itemPostBinding.tvReplyCount.setText("回复数：" + post.replyList.size());//回复数量
        return itemPostBinding.getRoot();
    }
}
