package com.example.mentalhealth.bean;


//应答类，ApiResponse是一个泛型数据，所有与服务器通信需要应答的请求，服务器统一返回此类型数据，可以减少很多数据判断的工作量
public class ApiResponse<T> {

    public int code = 0;//应答码
    public String message;//提示信息
    public T data;//泛型数据，根据不同接口，返回不同类型数据

    public ApiResponse() {
    }

    //默认为0，即code值没有被改变时成功应答
    public boolean success() {
        return code == 0;
    }

    public ApiResponse<T> netError() {
        this.message = "网络错误，请重试";
        this.code = -2;
        return this;
    }

}
