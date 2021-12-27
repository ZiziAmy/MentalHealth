package com.example.mentalhealth.fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.example.mentalhealth.R;
import com.example.mentalhealth.adapter.MyViewPagerAdapter;
import com.example.mentalhealth.databinding.FragmentMessageBinding;
import com.example.mentalhealth.view.ViewMessage;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

//信息页面
public class MessageFragment extends BaseFragment implements View.OnClickListener {
    private FragmentMessageBinding fragmentMessageBinding;

    public MessageFragment() {
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //初始化布局
        fragmentMessageBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_message, null, false);
        return fragmentMessageBinding.getRoot();
    }

    private ViewMessage viewMessage;//消息

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewMessage = new ViewMessage(getContext());//消息
        //页卡内容
        List<View> views = new ArrayList<>();
        views.add(viewMessage.getRoot());
        //初始化页卡
        MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter(views);
        fragmentMessageBinding.vp.setAdapter(myViewPagerAdapter);
    }


    @Override
    public void onResume() {
        super.onResume();
        viewMessage.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        viewMessage.onStop();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.rb1) {
            //消息页面
            fragmentMessageBinding.vp.setCurrentItem(0);
        } else if (v.getId() == R.id.rb2) {
            //故事会页面
            fragmentMessageBinding.vp.setCurrentItem(1);
        }
    }
}
