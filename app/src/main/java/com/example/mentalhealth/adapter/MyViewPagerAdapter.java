package com.example.mentalhealth.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

//PagerAdapter列表适配器
public class MyViewPagerAdapter extends PagerAdapter {
    private List<View> views;

    public MyViewPagerAdapter(List<View> views) {
        this.views = views;
    }

    @Override
    //删除页卡
    public void destroyItem(ViewGroup container, int position, @NotNull Object object) {
        container.removeView(views.get(position));
    }


    @NotNull
    @Override
    //这个方法用来实例化页卡
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(views.get(position), 0);//添加页卡
        return views.get(position);
    }

    @Override
    //页卡的数量
    public int getCount() {
        return views.size();
    }

    @Override
    public boolean isViewFromObject(@NotNull View view, @NotNull Object obj) {
        return view == obj;//官方提示这样写
    }
}