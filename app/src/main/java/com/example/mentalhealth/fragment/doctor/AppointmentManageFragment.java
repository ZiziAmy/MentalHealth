package com.example.mentalhealth.fragment.doctor;

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
import com.example.mentalhealth.databinding.FragmentAppointmentmanageBinding;
import com.example.mentalhealth.fragment.BaseFragment;
import com.example.mentalhealth.view.ViewAppointment;
import com.example.mentalhealth.view.ViewMessage;
import com.example.mentalhealth.view.ViewPost;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

//咨询师-首页
public class AppointmentManageFragment extends BaseFragment implements View.OnClickListener {
    private FragmentAppointmentmanageBinding fragmentAppointmentmanageBinding;

    public AppointmentManageFragment() {
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //初始化布局
        fragmentAppointmentmanageBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_appointmentmanage, null, false);
        return fragmentAppointmentmanageBinding.getRoot();
    }

    private ViewAppointment viewAppointment;//预约
    private ViewMessage viewMessage;//消息

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewAppointment = new ViewAppointment(getContext());//预约
        viewMessage = new ViewMessage(getContext());//消息
        //页卡内容
        List<View> views = new ArrayList<>();
        views.add(viewAppointment.getRoot());
        views.add(viewMessage.getRoot());
        //初始化页卡
        MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter(views);
        fragmentAppointmentmanageBinding.vp.setAdapter(myViewPagerAdapter);
        //初始化事件
        fragmentAppointmentmanageBinding.rb1.setOnClickListener(this);
        fragmentAppointmentmanageBinding.rb2.setOnClickListener(this);
        fragmentAppointmentmanageBinding.vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    fragmentAppointmentmanageBinding.rb1.setChecked(true);
                } else {
                    fragmentAppointmentmanageBinding.rb2.setChecked(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        viewAppointment.initData();
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
            fragmentAppointmentmanageBinding.vp.setCurrentItem(0);
        } else if (v.getId() == R.id.rb2) {
            //预约页面
            fragmentAppointmentmanageBinding.vp.setCurrentItem(1);
        }
    }
}
