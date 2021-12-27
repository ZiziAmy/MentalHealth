package com.example.mentalhealth.util;

import com.example.mentalhealth.bean.ApiResponse;
import com.example.mentalhealth.bean.Appointment;
import com.example.mentalhealth.bean.Banner;
import com.example.mentalhealth.bean.Comments;
import com.example.mentalhealth.bean.Messages;
import com.example.mentalhealth.bean.NewMessage;
import com.example.mentalhealth.bean.Post;
import com.example.mentalhealth.bean.Reply;
import com.example.mentalhealth.bean.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//与服务器通信类
public class HttpUtil {
    //服务器地址，用静态变量表示，因为java server部署在了自己电脑上，电脑所处的局域网ip会变，所以在此声明一个变量地址，便于统一管理
    public static final String HOST = "http://192.168.43.174:8081";

    //注册
    //TODO 后面都是以下面方法为基础写的http通信，后面不再赘述相关说明了
    public static ApiResponse<String> register(User user) {
        try {
            //客户端
            OkHttpClient okHttpClient = new OkHttpClient();
            //Post的请求方法，先要构造RequestBody，将Post的数据放入其中
            //此处通过Gson序列化
            //application/json作为请求头，用来告诉服务端消息主体是序列化的JSON字符串
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), new Gson().toJson(user));
            //post请求构造
            final Request request = new Request.Builder()
                    .url(HOST + "/user/register").post(requestBody)//
                    .build();
            final Call call = okHttpClient.newCall(request);//拿到call
            Response response = call.execute();//用call开始请求。得到response对象
            //由于应答的数据可能是List 数组等，所以，都是用他TypeToken方式进行应答类型转换
            Type type = new TypeToken<ApiResponse<String>>() {
            }.getType();
            //将应答数据的json(Strinig)类型转化为ApiResponse并返回
            return new Gson().fromJson(response.body().string(), type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ApiResponse<String>().netError();//默认返回错误应答
    }

    //登录
    public static ApiResponse<User> login(User user) {
        try {
            //客户端
            OkHttpClient okHttpClient = new OkHttpClient();
            //请求数据
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), new Gson().toJson(user));
            // post请求构造，
            final Request request = new Request.Builder()
                    .url(HOST + "/user/login").post(requestBody)
                    .build();
            final Call call = okHttpClient.newCall(request);
            Response response = call.execute();//开始请求
            Type type = new TypeToken<ApiResponse<User>>() {
            }.getType();//应答类型转换
            return new Gson().fromJson(response.body().string(), type);//返回
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ApiResponse<User>().netError();
    }

    //邮箱验证码登录
    public static ApiResponse<User> login_email(User user) {
        try {
            //客户端
            OkHttpClient okHttpClient = new OkHttpClient();
            //请求数据
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), new Gson().toJson(user));
            // post请求构造，
            final Request request = new Request.Builder()
                    .url(HOST + "/user/login_email").post(requestBody)
                    .build();
            final Call call = okHttpClient.newCall(request);
            Response response = call.execute();//开始请求
            Type type = new TypeToken<ApiResponse<User>>() {
            }.getType();//应答类型转换
            return new Gson().fromJson(response.body().string(), type);//返回
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ApiResponse<User>().netError();
    }

    //更新资料
    public static ApiResponse<User> update(User user) {
        try {
            //客户端
            OkHttpClient okHttpClient = new OkHttpClient();
            //Post的请求方法，先要构造RequestBody，将Post的数据放入其中
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), new Gson().toJson(user));
            //post请求构造
            final Request request = new Request.Builder()
                    .url(HOST + "/user/update").post(requestBody)//
                    .build();
            final Call call = okHttpClient.newCall(request);//拿到call
            Response response = call.execute();//用call开始请求。得到response对象
            Type type = new TypeToken<ApiResponse<User>>() {
            }.getType();//由于应答的数据可能是List 数组等，所以，都是用他TypeToken方式进行应答类型转换
            return new Gson().fromJson(response.body().string(), type);//将应答数据的json转化为ApiResponse并返回
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ApiResponse<User>().netError();//默认返回错误应答
    }

    //修改密码资料
    public static ApiResponse<User> changePassword(String username, String oldpwd, String newpwd) {
        try {
            //客户端
            OkHttpClient okHttpClient = new OkHttpClient();
            final Request request = new Request.Builder()
                    .url(HOST + "/user/changePassword?username=" + username + "&oldpwd=" + oldpwd + "&newpwd=" + newpwd).get()//
                    .build();
            final Call call = okHttpClient.newCall(request);//拿到call
            Response response = call.execute();//用call开始请求。得到response对象
            Type type = new TypeToken<ApiResponse<User>>() {
            }.getType();//由于应答的数据可能是List 数组等，所以，都是用他TypeToken方式进行应答类型转换
            return new Gson().fromJson(response.body().string(), type);//将应答数据的json转化为ApiResponse并返回
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ApiResponse<User>().netError();//默认返回错误应答
    }

    //获取首页轮播图
    public static ApiResponse<List<Banner>> getBanners() {
        try {
            //客户端
            OkHttpClient okHttpClient = new OkHttpClient();
            final Request request = new Request.Builder()
                    .url(HOST + "/app/index/getBanners").get()
                    .build();
            final Call call = okHttpClient.newCall(request);
            Response response = call.execute();//开始请求
            Type type = new TypeToken<ApiResponse<List<Banner>>>() {
            }.getType();//应答类型转换
            return new Gson().fromJson(response.body().string(), type);//返回
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ApiResponse<List<Banner>>().netError();
    }

    //获取咨询师列表
    public static ApiResponse<List<User>> getDoctors() {
        try {
            //客户端
            OkHttpClient okHttpClient = new OkHttpClient();
            final Request request = new Request.Builder()
                    .url(HOST + "/app/index/getDoctors").get()
                    .build();
            final Call call = okHttpClient.newCall(request);
            Response response = call.execute();//开始请求
            Type type = new TypeToken<ApiResponse<List<User>>>() {
            }.getType();//应答类型转换
            return new Gson().fromJson(response.body().string(), type);//返回
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ApiResponse<List<User>>().netError();
    }

    //获取咨询师详情
    public static ApiResponse<User> getDoctor(String username) {
        try {
            //客户端
            OkHttpClient okHttpClient = new OkHttpClient();
            final Request request = new Request.Builder()
                    .url(HOST + "/app/index/getDoctor?username=" + username).get()
                    .build();
            final Call call = okHttpClient.newCall(request);
            Response response = call.execute();//开始请求
            Type type = new TypeToken<ApiResponse<User>>() {
            }.getType();//应答类型转换
            return new Gson().fromJson(response.body().string(), type);//返回
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ApiResponse<User>().netError();
    }

    //预约
    public static ApiResponse<String> addAppointment(Appointment appointment) {
        try {
            //客户端
            OkHttpClient okHttpClient = new OkHttpClient();
            //请求数据
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), new Gson().toJson(appointment));
            // post请求构造，
            final Request request = new Request.Builder()
                    .url(HOST + "/app/index/addAppointment").post(requestBody)
                    .build();
            final Call call = okHttpClient.newCall(request);
            Response response = call.execute();//开始请求
            Type type = new TypeToken<ApiResponse<String>>() {
            }.getType();//应答类型转换
            return new Gson().fromJson(response.body().string(), type);//返回
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ApiResponse<String>().netError();
    }

    //发布故事
    public static ApiResponse<String> post(Post post) {
        try {
            //客户端
            OkHttpClient okHttpClient = new OkHttpClient();
            //请求数据
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), new Gson().toJson(post));
            //post请求构造
            final Request request = new Request.Builder()
                    .url(HOST + "/app/post/post").post(requestBody)
                    .build();
            final Call call = okHttpClient.newCall(request);
            Response response = call.execute();//开始请求
            Type type = new TypeToken<ApiResponse<String>>() {
            }.getType();//应答类型转换
            return new Gson().fromJson(response.body().string(), type);//返回
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ApiResponse<String>().netError();
    }

    //回复
    public static ApiResponse<String> reply(Reply reply) {
        try {
            //客户端
            OkHttpClient okHttpClient = new OkHttpClient();
            //请求数据
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), new Gson().toJson(reply));
            //post请求构造
            final Request request = new Request.Builder()
                    .url(HOST + "/app/post/reply").post(requestBody)
                    .build();
            final Call call = okHttpClient.newCall(request);
            Response response = call.execute();//开始请求
            Type type = new TypeToken<ApiResponse<String>>() {
            }.getType();//应答类型转换
            return new Gson().fromJson(response.body().string(), type);//返回
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ApiResponse<String>().netError();
    }

    //发布评价
    public static ApiResponse<String> addComments(Comments comments) {
        try {
            //客户端
            OkHttpClient okHttpClient = new OkHttpClient();
            //请求数据
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), new Gson().toJson(comments));
            //post请求构造
            final Request request = new Request.Builder()
                    .url(HOST + "/app/index/addComments").post(requestBody)
                    .build();
            final Call call = okHttpClient.newCall(request);
            Response response = call.execute();//开始请求
            Type type = new TypeToken<ApiResponse<String>>() {
            }.getType();//应答类型转换
            return new Gson().fromJson(response.body().string(), type);//返回
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ApiResponse<String>().netError();
    }

    //获取所有评价
    public static ApiResponse<List<Comments>> getComments(String doctor_username) {
        try {
            //客户端
            OkHttpClient okHttpClient = new OkHttpClient();
            final Request request = new Request.Builder()
                    .url(HOST + "/app/index/getComments?doctor_username=" + doctor_username).get()
                    .build();
            final Call call = okHttpClient.newCall(request);
            Response response = call.execute();//开始请求
            Type type = new TypeToken<ApiResponse<List<Comments>>>() {
            }.getType();//应答类型转换
            return new Gson().fromJson(response.body().string(), type);//返回
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ApiResponse<List<Comments>>().netError();
    }

    //获取所有故事
    public static ApiResponse<List<Post>> getAllPost() {
        try {
            //客户端
            OkHttpClient okHttpClient = new OkHttpClient();
            final Request request = new Request.Builder()
                    .url(HOST + "/app/post/getAllPost").get()
                    .build();
            final Call call = okHttpClient.newCall(request);
            Response response = call.execute();//开始请求
            Type type = new TypeToken<ApiResponse<List<Post>>>() {
            }.getType();//应答类型转换
            return new Gson().fromJson(response.body().string(), type);//返回
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ApiResponse<List<Post>>().netError();
    }

    //查询关注状态
    public static ApiResponse<Boolean> isFollow(String username, String follow_username) {
        try {
            //客户端
            OkHttpClient okHttpClient = new OkHttpClient();

            final Request request = new Request.Builder()
                    .url(HOST + "/user/isFollow?username=" + username + "&follow_username=" + follow_username).get()
                    .build();
            final Call call = okHttpClient.newCall(request);
            Response response = call.execute();//开始请求
            Type type = new TypeToken<ApiResponse<Boolean>>() {
            }.getType();//应答类型转换
            return new Gson().fromJson(response.body().string(), type);//返回
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ApiResponse<Boolean>().netError();
    }

    //关注某用户
    public static ApiResponse<String> follow(String username, String follow_username) {
        try {
            //客户端
            OkHttpClient okHttpClient = new OkHttpClient();

            final Request request = new Request.Builder()
                    .url(HOST + "/user/follow?username=" + username + "&follow_username=" + follow_username).get()
                    .build();
            final Call call = okHttpClient.newCall(request);
            Response response = call.execute();//开始请求
            Type type = new TypeToken<ApiResponse<String>>() {
            }.getType();//应答类型转换
            return new Gson().fromJson(response.body().string(), type);//返回
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ApiResponse<String>().netError();
    }

    //取消关注某用户
    public static ApiResponse<String> removeFollow(String username, String follow_username) {
        try {
            //客户端
            OkHttpClient okHttpClient = new OkHttpClient();

            final Request request = new Request.Builder()
                    .url(HOST + "/user/removeFollow?username=" + username + "&follow_username=" + follow_username).get()
                    .build();
            final Call call = okHttpClient.newCall(request);
            Response response = call.execute();//开始请求
            Type type = new TypeToken<ApiResponse<String>>() {
            }.getType();//应答类型转换
            return new Gson().fromJson(response.body().string(), type);//返回
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ApiResponse<String>().netError();
    }


    //移除粉丝
    public static ApiResponse<String> removeFollowMe(String username, String follow_username) {
        try {
            //客户端
            OkHttpClient okHttpClient = new OkHttpClient();

            final Request request = new Request.Builder()
                    .url(HOST + "/user/removeFollowMe?username=" + username + "&follow_username=" + follow_username).get()
                    .build();
            final Call call = okHttpClient.newCall(request);
            Response response = call.execute();//开始请求
            Type type = new TypeToken<ApiResponse<String>>() {
            }.getType();//应答类型转换
            return new Gson().fromJson(response.body().string(), type);//返回
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ApiResponse<String>().netError();
    }


    //获取粉丝列表
    public static ApiResponse<List<User>> getFollowMeUser(String username) {
        try {
            //客户端
            OkHttpClient okHttpClient = new OkHttpClient();

            final Request request = new Request.Builder()
                    .url(HOST + "/user/getFollowMeUser?username=" + username).get()
                    .build();
            final Call call = okHttpClient.newCall(request);
            Response response = call.execute();//开始请求
            Type type = new TypeToken<ApiResponse<List<User>>>() {
            }.getType();//应答类型转换
            return new Gson().fromJson(response.body().string(), type);//返回
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ApiResponse<List<User>>().netError();
    }


    //获取我关注的人列表
    public static ApiResponse<List<User>> getMyFollowUser(String username) {
        try {
            //客户端
            OkHttpClient okHttpClient = new OkHttpClient();

            final Request request = new Request.Builder()
                    .url(HOST + "/user/getMyFollowUser?username=" + username).get()
                    .build();
            final Call call = okHttpClient.newCall(request);
            Response response = call.execute();//开始请求
            Type type = new TypeToken<ApiResponse<List<User>>>() {
            }.getType();//应答类型转换
            return new Gson().fromJson(response.body().string(), type);//返回
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ApiResponse<List<User>>().netError();
    }

    //用户获取我的预约
    public static ApiResponse<List<Appointment>> getUserAppointment(String username) {
        try {
            //客户端
            OkHttpClient okHttpClient = new OkHttpClient();

            final Request request = new Request.Builder()
                    .url(HOST + "/app/index/getUserAppointment?username=" + username).get()
                    .build();
            final Call call = okHttpClient.newCall(request);
            Response response = call.execute();//开始请求
            Type type = new TypeToken<ApiResponse<List<Appointment>>>() {
            }.getType();//应答类型转换
            return new Gson().fromJson(response.body().string(), type);//返回
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ApiResponse<List<Appointment>>().netError();
    }

    //咨询师获取我的预约
    public static ApiResponse<List<Appointment>> getAppointment(String doctor_username) {
        try {
            //客户端
            OkHttpClient okHttpClient = new OkHttpClient();

            final Request request = new Request.Builder()
                    .url(HOST + "/app/index/getAppointment?doctor_username=" + doctor_username).get()
                    .build();
            final Call call = okHttpClient.newCall(request);
            Response response = call.execute();//开始请求
            Type type = new TypeToken<ApiResponse<List<Appointment>>>() {
            }.getType();//应答类型转换
            return new Gson().fromJson(response.body().string(), type);//返回
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ApiResponse<List<Appointment>>().netError();
    }

    //删除预约
    public static ApiResponse<String> deleteAppointment(String uuid) {
        try {
            //客户端
            OkHttpClient okHttpClient = new OkHttpClient();

            final Request request = new Request.Builder()
                    .url(HOST + "/app/index/deleteAppointment?uuid=" + uuid).get()
                    .build();
            final Call call = okHttpClient.newCall(request);
            Response response = call.execute();//开始请求
            Type type = new TypeToken<ApiResponse<String>>() {
            }.getType();//应答类型转换
            return new Gson().fromJson(response.body().string(), type);//返回
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ApiResponse<String>().netError();
    }

    //获取我发布的故事
    public static ApiResponse<List<Post>> getMyPost(String username) {
        try {
            //客户端
            OkHttpClient okHttpClient = new OkHttpClient();

            final Request request = new Request.Builder()
                    .url(HOST + "/app/post/getMyPost?username=" + username).get()
                    .build();
            final Call call = okHttpClient.newCall(request);
            Response response = call.execute();//开始请求
            Type type = new TypeToken<ApiResponse<List<Post>>>() {
            }.getType();//应答类型转换
            return new Gson().fromJson(response.body().string(), type);//返回
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ApiResponse<List<Post>>().netError();
    }

    //删除故事
    public static ApiResponse<String> delete(String post_uuid) {
        try {
            //客户端
            OkHttpClient okHttpClient = new OkHttpClient();

            final Request request = new Request.Builder()
                    .url(HOST + "/app/post/delete?post_uuid=" + post_uuid).get()
                    .build();
            final Call call = okHttpClient.newCall(request);
            Response response = call.execute();//开始请求
            Type type = new TypeToken<ApiResponse<String>>() {
            }.getType();//应答类型转换
            return new Gson().fromJson(response.body().string(), type);//返回
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ApiResponse<String>().netError();
    }

    //获取故事所有回复
    public static ApiResponse<List<Reply>> getReplyByPostUUID(String post_uuid) {
        try {
            //客户端
            OkHttpClient okHttpClient = new OkHttpClient();

            final Request request = new Request.Builder()
                    .url(HOST + "/app/post/getReplyByPostUUID?post_uuid=" + post_uuid).get()
                    .build();
            final Call call = okHttpClient.newCall(request);
            Response response = call.execute();//开始请求
            Type type = new TypeToken<ApiResponse<List<Reply>>>() {
            }.getType();//应答类型转换
            return new Gson().fromJson(response.body().string(), type);//返回
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ApiResponse<List<Reply>>().netError();
    }

    //获取故事详情
    public static ApiResponse<Post> viewPost(String post_uuid) {
        try {
            //客户端
            OkHttpClient okHttpClient = new OkHttpClient();

            final Request request = new Request.Builder()
                    .url(HOST + "/app/post/view?post_uuid=" + post_uuid).get()
                    .build();
            final Call call = okHttpClient.newCall(request);

            Response response = call.execute();//开始请求
            Type type = new TypeToken<ApiResponse<Post>>() {
            }.getType();//应答类型转换
            return new Gson().fromJson(response.body().string(), type);//返回
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ApiResponse<Post>().netError();
    }


    //查询点赞状态
    public static ApiResponse<Boolean> isLike(String doctor_username, String username) {
        try {
            //客户端
            OkHttpClient okHttpClient = new OkHttpClient();

            final Request request = new Request.Builder()
                    .url(HOST + "/user/isLike?doctor_username=" + doctor_username + "&username=" + username).get()
                    .build();
            final Call call = okHttpClient.newCall(request);
            Response response = call.execute();//开始请求
            Type type = new TypeToken<ApiResponse<Boolean>>() {
            }.getType();//应答类型转换
            return new Gson().fromJson(response.body().string(), type);//返回
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ApiResponse<Boolean>().netError();
    }

    //点赞数量查询
    public static ApiResponse<Integer> likeCount(String doctor_username) {
        try {
            //客户端
            OkHttpClient okHttpClient = new OkHttpClient();

            final Request request = new Request.Builder()
                    .url(HOST + "/user/likeCount?doctor_username=" + doctor_username).get()
                    .build();
            final Call call = okHttpClient.newCall(request);
            Response response = call.execute();//开始请求
            Type type = new TypeToken<ApiResponse<Integer>>() {
            }.getType();//应答类型转换
            return new Gson().fromJson(response.body().string(), type);//返回
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ApiResponse<Integer>().netError();
    }

    //点赞
    public static ApiResponse<String> like(String doctor_username, String username) {
        try {
            //客户端
            OkHttpClient okHttpClient = new OkHttpClient();

            final Request request = new Request.Builder()
                    .url(HOST + "/user/like?doctor_username=" + doctor_username + "&username=" + username).get()
                    .build();
            final Call call = okHttpClient.newCall(request);
            Response response = call.execute();//开始请求
            Type type = new TypeToken<ApiResponse<String>>() {
            }.getType();//应答类型转换
            return new Gson().fromJson(response.body().string(), type);//返回
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ApiResponse<String>().netError();
    }

    //取消点赞
    public static ApiResponse<String> unLike(String doctor_username, String username) {
        try {
            //客户端
            OkHttpClient okHttpClient = new OkHttpClient();
            final Request request = new Request.Builder()
                    .url(HOST + "/user/unLike?doctor_username=" + doctor_username + "&username=" + username).get()
                    .build();
            final Call call = okHttpClient.newCall(request);
            Response response = call.execute();//开始请求
            Type type = new TypeToken<ApiResponse<String>>() {
            }.getType();//应答类型转换
            return new Gson().fromJson(response.body().string(), type);//返回
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ApiResponse<String>().netError();
    }

    //获取消息列表
    public static ApiResponse<List<NewMessage>> getMyMessageList(String user_name) {
        try {
            //客户端
            OkHttpClient okHttpClient = new OkHttpClient();
            //post请求构造
            final Request request = new Request.Builder()
                    .url(HOST + "/app/message/getMyMessageList?user_name=" + user_name).get()
                    .build();
            final Call call = okHttpClient.newCall(request);
            Response response = call.execute();//开始请求
            Type type = new TypeToken<ApiResponse<List<NewMessage>>>() {
            }.getType();//应答类型转换
            return new Gson().fromJson(response.body().string(), type);//返回
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ApiResponse<List<NewMessage>>().netError();
    }


    //将消息标记已读
    public static ApiResponse<String> read(String user_name, String friend_name) {
        try {
            //客户端
            OkHttpClient okHttpClient = new OkHttpClient();
            //post请求构造
            final Request request = new Request.Builder()
                    .url(HOST + "/app/message/read?user_name=" + user_name + "&friend_name=" + friend_name).get()
                    .build();
            final Call call = okHttpClient.newCall(request);
            Response response = call.execute();//开始请求
            Type type = new TypeToken<ApiResponse<String>>() {
            }.getType();//应答类型转换
            return new Gson().fromJson(response.body().string(), type);//返回
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ApiResponse<String>().netError();
    }

    //删除消息
    public static ApiResponse<String> deleteMessage(String user_name, String friend_name) {
        try {
            //客户端
            OkHttpClient okHttpClient = new OkHttpClient();
            //post请求构造
            final Request request = new Request.Builder()
                    .url(HOST + "/app/message/deleteMessage?user_name=" + user_name + "&friend_name=" + friend_name).get()
                    .build();
            final Call call = okHttpClient.newCall(request);
            Response response = call.execute();//开始请求
            Type type = new TypeToken<ApiResponse<String>>() {
            }.getType();//应答类型转换
            return new Gson().fromJson(response.body().string(), type);//返回
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ApiResponse<String>().netError();
    }


    //获取与某人的未读消息
    public static ApiResponse<List<Messages>> getUnReadMessages(String user_name, String friend_name) {
        try {
            //客户端
            OkHttpClient okHttpClient = new OkHttpClient();
            //post请求构造
            final Request request = new Request.Builder()
                    .url(HOST + "/app/message/getUnReadMessages?user_name=" + user_name + "&friend_name=" + friend_name).get()
                    .build();
            final Call call = okHttpClient.newCall(request);
            Response response = call.execute();//开始请求
            Type type = new TypeToken<ApiResponse<List<Messages>>>() {
            }.getType();//应答类型转换
            return new Gson().fromJson(response.body().string(), type);//返回
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ApiResponse<List<Messages>>().netError();
    }

    //获取与某人的消息
    public static ApiResponse<List<Messages>> getMessages(String user_name, String friend_name) {
        try {
            //客户端
            OkHttpClient okHttpClient = new OkHttpClient();
            //post请求构造
            final Request request = new Request.Builder()
                    .url(HOST + "/app/message/getMessages?user_name=" + user_name + "&friend_name=" + friend_name).get()
                    .build();
            final Call call = okHttpClient.newCall(request);
            Response response = call.execute();//开始请求
            Type type = new TypeToken<ApiResponse<List<Messages>>>() {
            }.getType();//应答类型转换
            return new Gson().fromJson(response.body().string(), type);//返回
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ApiResponse<List<Messages>>().netError();
    }

    //发送验证码
    public static ApiResponse<String> getCode(String username, String email) {
        try {
            //客户端
            OkHttpClient okHttpClient = new OkHttpClient();
            //post请求构造
            final Request request;
            if (username == null) {
                request = new Request.Builder()
                        .url(HOST + "/user/getCode?email=" + email).get()
                        .build();
            } else {
                request = new Request.Builder()
                        .url(HOST + "/user/getCode?username=" + username + "&email=" + email).get()
                        .build();
            }
            final Call call = okHttpClient.newCall(request);
            Response response = call.execute();//开始请求
            Type type = new TypeToken<ApiResponse<String>>() {
            }.getType();//应答类型转换
            return new Gson().fromJson(response.body().string(), type);//返回
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ApiResponse<String>().netError();
    }


    //发消息
    public static ApiResponse<String> sendMessages(Messages messages) {
        try {
            //客户端
            OkHttpClient okHttpClient = new OkHttpClient();
            //请求数据
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), new Gson().toJson(messages));
            //post请求构造
            final Request request = new Request.Builder()
                    .url(HOST + "/app/message/sendMessages").post(requestBody)
                    .build();
            final Call call = okHttpClient.newCall(request);
            Response response = call.execute();//开始请求
            Type type = new TypeToken<ApiResponse<String>>() {
            }.getType();//应答类型转换
            return new Gson().fromJson(response.body().string(), type);//返回
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ApiResponse<String>().netError();
    }

    //上传图片
    public static ApiResponse<String> upload(String filename) {
        try {
            OkHttpClient okHttpClient = new OkHttpClient();
            //上传图片使用MultipartBody
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("file", filename,
                            //application/octet-stream用于提交二进制文件
                            RequestBody.create(MediaType.parse("application/octet-stream"),
                                    new File(filename)))
                    .build();
            final Request request = new Request.Builder().
                    url(HOST + "/image/upload").post(body)
                    .build();
            final Call call = okHttpClient.newCall(request);
            Response response = call.execute();
            Type type = new TypeToken<ApiResponse<String>>() {
            }.getType();
            return new Gson().fromJson(response.body().string(), type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ApiResponse<String>().netError();
    }

    //根据图片名，构造请求服务器的图片路径
    public static String getImageUrl(String filename) {
        return HOST + "/image/file/" + filename;
    }
}
