package com.example.mentalhealth.bean;


import java.util.List;

public class Post {
    public static final String SPLIT = "--------TE--------";//图片分隔符


    public String uuid;//主键
    public String title;//标题
    public String content;//内容
    public String username;//发布人

    public String images;//图片
    public long create_time;//发布时间
    public int view_count;//浏览数

    public String avatar;//发布人昵称
    public int like;//点赞数

    public List<Reply> replyList;//回复列表

    public Post() {

    }

}

