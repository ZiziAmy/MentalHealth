package com.example.mentalhealth.bean;

import java.util.List;

//消息列表
public class NewMessage {
    public User user;//用户
    public List<Messages> messagesList;//消息列表
    public List<Messages> unReadMessagesList;//未读消息列表
    public long lastMessageTime = 0;//最新消息的时间
}
