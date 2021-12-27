package com.example.mentalhealth.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.databinding.DataBindingUtil;

import com.example.mentalhealth.MyApplication;
import com.example.mentalhealth.R;
import com.example.mentalhealth.activity.MyAppointmentMainActivity;
import com.example.mentalhealth.bean.ApiResponse;
import com.example.mentalhealth.bean.Appointment;
import com.example.mentalhealth.databinding.ItemAppointmentBinding;
import com.example.mentalhealth.databinding.ViewAppointmentBinding;
import com.example.mentalhealth.util.Global;
import com.example.mentalhealth.util.HttpUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

//咨询师预约页面
public class ViewAppointment {
    private final ViewAppointmentBinding viewAppointmentBinding;
    private final Context context;

    public ViewAppointment(Context context) {
        this.context = context;
        //初始化布局
        viewAppointmentBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.view_appointment, null, false);
    }

    public View getRoot() {
        return viewAppointmentBinding.getRoot();
    }


    private List<Appointment> appointments = new ArrayList<>();//故事列表

    //Toast提示
    private void toast(String msg) {
        MyApplication.toast(msg);
    }

    private final Handler handler = new Handler();

    //初始化数据
    public void initData() {
        new Thread(() -> {
            ApiResponse<List<Appointment>> apiResponse = HttpUtil.getAppointment(Global.user.username);
            if (apiResponse.success()) {
                //获取成功
                if (apiResponse.data == null || apiResponse.data.isEmpty()) {
                    toast("暂时还没有人预约了");
                } else {
                    appointments = apiResponse.data;
                    handler.post(() -> {
                        viewAppointmentBinding.lv.setAdapter(new MyAdapter());//更新列表
                    });
                }
            }
        }).start();
    }


    //预约列表适配器
    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return appointments.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @SuppressLint("SimpleDateFormat")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //加载布局
            @SuppressLint("ViewHolder") ItemAppointmentBinding itemAppointmentBinding
                    = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_appointment, null, false);
            itemAppointmentBinding.tvName.setText(appointments.get(position).name);//姓名
            itemAppointmentBinding.tvDoctorName.setText(appointments.get(position).doctor.name);//咨询师姓名
            itemAppointmentBinding.tvContent.setText(appointments.get(position).content);//描述
            itemAppointmentBinding.tvDate.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(appointments.get(position).dtime));//日期
            itemAppointmentBinding.tvMobile.setText(appointments.get(position).mobile);//手机
            return itemAppointmentBinding.getRoot();
        }
    }
}
