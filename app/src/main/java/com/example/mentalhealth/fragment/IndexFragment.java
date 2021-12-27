package com.example.mentalhealth.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.mentalhealth.R;
import com.example.mentalhealth.activity.DoctorActivity;
import com.example.mentalhealth.activity.PostDetailActivity;
import com.example.mentalhealth.activity.ViewDoctorActivity;
import com.example.mentalhealth.adapter.DoctorAdapter;
import com.example.mentalhealth.adapter.MyViewPagerAdapter;
import com.example.mentalhealth.bean.ApiResponse;
import com.example.mentalhealth.bean.Banner;
import com.example.mentalhealth.bean.User;
import com.example.mentalhealth.databinding.FragmentIndexBinding;
import com.example.mentalhealth.databinding.ItemBannerBinding;
import com.example.mentalhealth.util.HttpUtil;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

//主页
public class IndexFragment extends BaseFragment {
    private FragmentIndexBinding fragmentIndexBinding;
    private int currentPage = 0;//当前页
    private final int TIME = 3000;//页卡切换任务3s

    public IndexFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //初始化布局
        fragmentIndexBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_index, null, false);
        //return与这个binding相关联的最外层View
        return fragmentIndexBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();//初始化数据
        //顶部页卡切换事件
        fragmentIndexBinding.vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            //用手指滑动翻页的时候，如果翻动成功了（滑动的距离够长），手指抬起来就会立即执行这个方法，position就是当前滑动到的页面。
            public void onPageSelected(int position) {
                //banner页面切换事件
                currentPage = position;
                changePoint();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        //下拉刷新控件，设置刷新监听，重新读取数据进行初始化
        fragmentIndexBinding.srl.setOnRefreshListener(this::initData);
        //咨询师列表点击事件
        fragmentIndexBinding.lv
                .setOnItemClickListener((parent, view1, position, id) -> {
                    //进入咨询师详情页面
                    Intent intent = new Intent(getActivity(), ViewDoctorActivity.class);
                    intent.putExtra("username", userList.get(position).username);//将预览数据传递过去
                    startActivity(intent);
                });
    }

    private List<User> userList = new ArrayList<>();//咨询师列表

    //初始化数据
    private void initData() {
        //初始化顶部广告
        new Thread(this::initBanner).start();
        //获取推荐咨询师
        new Thread(() -> {
            ApiResponse<List<User>> apiResponse = HttpUtil.getDoctors();//获取咨询师
            // 其实post方法post过去的是一段代码，相当于将这个Runable体放入消息队列中，那么looper拿取的即为这段代码去交给handler来处理
            handler.post(() -> {
                fragmentIndexBinding.srl.setRefreshing(false);//去除下拉刷新状态
            });
            if (apiResponse.success()) {
                if (apiResponse.data == null || apiResponse.data.isEmpty()) {
                    toast("暂时还没有人咨询师加入");
                } else {
                    userList = apiResponse.data;
                    handler.post(() -> {
                        fragmentIndexBinding.lv.setAdapter(new DoctorAdapter(getContext(), userList));//刷新咨询师列表
                    });
                }
            }
        }).start();
    }

    private List<View> bannerViews;//轮播广告图的视图列表
    WebView webView;
    ProgressDialog mProgressDialog;


    //初始化顶部轮播图片
    private void initBanner() {
//        //创建ProgressDialog对象
//        mProgressDialog = new ProgressDialog(getContext());
//        //创建WebView对象
//        webView = new WebView(getContext());
//        //对网页界面的设置
//        webView.setWebViewClient(new WebViewClient(){
//            //使网页在当前页面中显示
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                webView.loadUrl(url);
//                return true;
//            }
//
//            //通知主程序页面开始加载，并显示进度条
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                super.onPageStarted(view, url, favicon);
//                mProgressDialog.show();
//            }
//
//            //通知主程序页面加载结束，并隐藏进度条
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(view, url);
//                mProgressDialog.hide();
//            }
//        });



        ApiResponse<List<Banner>> apiResponse = HttpUtil.getBanners();//请求获取轮播图片
        if (apiResponse.success()) {//获取成功
            bannerViews = new ArrayList<>();
            handler.post(() -> {//开始加载
                handler.removeCallbacks(runnable);//移除切换页卡的任务
                for (Banner banner : apiResponse.data) {
                    //循环构造广告图
                    //加载广告图布局
                    ItemBannerBinding itemBannerBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.item_banner, null, false);
                    Glide.with(getContext()).load(HttpUtil.HOST + banner.image_path).into(itemBannerBinding.iv);//加载网络图片
//                    itemBannerBinding.getRoot().setOnClickListener( new View.OnClickListener(){
//                        public void onClick(View v) { //使用匿名的Button实例
//                            handler.post(() -> {
//                                webView.getSettings().setJavaScriptEnabled(true);
//                                //加载网页
//                                webView.loadUrl("http://www.baidu.com/");
//                                //显示网页
//                                Activity c= (Activity) getContext();
//                                c.setContentView(webView);
//                            });
//                            ///获得一个webView对象并设置支持JavaScript
//
//                            //抽象接口的内部方法的实现
//                        } });

                    bannerViews.add(itemBannerBinding.getRoot());
                }
                fragmentIndexBinding.vp.setAdapter(new MyViewPagerAdapter(bannerViews));//设置顶部图片列表适配器
                handler.postDelayed(runnable, TIME);//开启页卡切换任务
            });
        } else {
            toast("广告图片获取失败，请重新进入本页面");
        }
    }


    //更新顶部页卡4个小圆点
    private void changePoint() {
        switch (currentPage) {
            case 0: {
                fragmentIndexBinding.rb1.setChecked(true);
                break;
            }
            case 1: {
                fragmentIndexBinding.rb2.setChecked(true);
                break;
            }
            case 2: {
                fragmentIndexBinding.rb3.setChecked(true);
                break;
            }
            case 3: {
                fragmentIndexBinding.rb4.setChecked(true);
                break;
            }
        }
    }

    //页卡定时切换
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            currentPage = currentPage + 1 > 3 ? 0 : currentPage + 1;//切换页面
            fragmentIndexBinding.vp.setCurrentItem(currentPage);
            changePoint();//更新小圆点
            handler.postDelayed(this, TIME);//3s一切换
        }
    };


    //页面销毁事件
    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);//移除页卡切换任务
    }
}
