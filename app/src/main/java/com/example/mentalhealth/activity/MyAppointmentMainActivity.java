package com.example.mentalhealth.activity;

import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AlphabetIndexer;
import android.widget.BaseAdapter;

import com.example.mentalhealth.R;
import com.example.mentalhealth.bean.ApiResponse;
import com.example.mentalhealth.bean.Appointment;
import com.example.mentalhealth.databinding.ActivityMyAppointmentMainBinding;
import com.example.mentalhealth.databinding.ItemAppointmentBinding;
import com.example.mentalhealth.util.Global;
import com.example.mentalhealth.util.HttpUtil;

import java.text.SimpleDateFormat;
import java.util.List;

//用户预约列表页面
public class MyAppointmentMainActivity extends BaseActivity {
    private ActivityMyAppointmentMainBinding activityMyAppointmentMainBinding;
    private List<Appointment> appointments;//我的预约

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化页面
        activityMyAppointmentMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_my_appointment_main);
        setTitle("我的预约");
        initData();//初始化数据
        activityMyAppointmentMainBinding.lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String[] ITEM = {"删除", "取消"};
                new AlertDialog.Builder(MyAppointmentMainActivity.this).setItems(ITEM, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            //删除
                            new Thread(() -> {
                                ApiResponse<String> apiResponse = HttpUtil.deleteAppointment(appointments.get(position).uuid);//删除
                                if (apiResponse.success()) {
                                    initData();//刷新
                                }
                                toast(apiResponse.message);//提示
                            }).start();
                        }
                    }
                }).show();
            }
        });
    }

    //初始化数据
    private void initData() {
        new Thread(() -> {
            ApiResponse<List<Appointment>> apiResponse = HttpUtil.getUserAppointment(Global.user.username);//获取我的粉丝列表
            if (apiResponse.success()) {
                if (apiResponse.data.isEmpty()) {
                    toast("你还没预约了");
                    finish();
                    return;
                }
                appointments = apiResponse.data;//赋值粉丝列表
                handler.post(() -> {
                    //设置粉丝列表
                    setTitle("我的预约（" + appointments.size() + "）");
                    activityMyAppointmentMainBinding.lv.setAdapter(new MyAdapter());
                });

            } else {
                toast("获取失败，请重试");
                finish();
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
                    = DataBindingUtil.inflate(LayoutInflater.from(MyAppointmentMainActivity.this), R.layout.item_appointment, null, false);
            itemAppointmentBinding.tvName.setText(appointments.get(position).name);//姓名
            itemAppointmentBinding.tvDoctorName.setText(appointments.get(position).doctor.name);//咨询师姓名
            itemAppointmentBinding.tvContent.setText(appointments.get(position).content);//描述
            itemAppointmentBinding.tvDate.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(appointments.get(position).dtime));//日期
            itemAppointmentBinding.tvMobile.setText(appointments.get(position).mobile);//手机
            return itemAppointmentBinding.getRoot();
        }
    }
}