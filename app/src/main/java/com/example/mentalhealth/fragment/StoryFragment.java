package com.example.mentalhealth.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.ViewPager;

import com.example.mentalhealth.R;
import com.example.mentalhealth.adapter.MyViewPagerAdapter;
import com.example.mentalhealth.databinding.FragmentStoryBinding;
import com.example.mentalhealth.view.ViewMessage;
import com.example.mentalhealth.view.ViewPost;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

//信息页面
public class StoryFragment extends BaseFragment {
    private FragmentStoryBinding fragmentStoryBinding;

    public StoryFragment() {
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //初始化布局
        fragmentStoryBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_story, null, false);
        return fragmentStoryBinding.getRoot();
    }

    private ViewPost viewPost;//故事会

    //Called immediately after onCreateView(LayoutInflater, ViewGroup, Bundle) has returned
    //story和message模块其实不用页卡
    //本来是为了滑动，在一个fragment中存放两个view
    //现在没有了滑动直接在fragment中实现就好
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPost = new ViewPost(getContext());//故事会
        //页卡内容
        //viewList是一个View数组，盛装上面的VIEW
        List<View> views = new ArrayList<>();
        views.add(viewPost.getRoot());
        //初始化页卡
        MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter(views);
        fragmentStoryBinding.vp.setAdapter(myViewPagerAdapter);
    }


    //回到此fragment时重新加载故事页面中的故事
    @Override
    public void onResume() {
        super.onResume();
        viewPost.initData();
    }

}
