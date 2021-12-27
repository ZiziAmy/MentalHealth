package com.example.mentalhealth.bean;

//消息
public class Messages {
    public String uuid;//主键

    public String receive_user_id;//接收人
    public String send_user_id;//发送人
    public int status;//0未读，1已读
    public String content;//消息内容
    public long create_time;//发送时间
}
