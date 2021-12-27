package com.example.mentalhealth.bean;


//预约
public class Appointment {
    public String uuid;
    public String username;//用户名
    public String doctor_username;//咨询师用户id
    public String name;//姓名
    public String mobile;//手机
    public long dtime;//预约时间
    public String content;//症状

    public User doctor;//咨询师信息
}
