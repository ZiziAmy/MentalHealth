package com.example.mentalhealth.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.example.mentalhealth.R;
import com.example.mentalhealth.bean.ApiResponse;
import com.example.mentalhealth.bean.Appointment;
import com.example.mentalhealth.databinding.ActivityAppointmentBinding;
import com.example.mentalhealth.util.Global;
import com.example.mentalhealth.util.HttpUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;

//预约界面
public class AppointmentActivity extends BaseActivity implements View.OnClickListener {

    private ActivityAppointmentBinding activityAppointmentBinding;
    private String doctor_username;//咨询师id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化界面
        activityAppointmentBinding = DataBindingUtil.setContentView(this, R.layout.activity_appointment);
        setTitle("预约");
        doctor_username = getIntent().getStringExtra("doctor_username");
        //日期获取焦点事件
        activityAppointmentBinding.etDate.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                onClick(v);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.etDate) {
            //选择日期
            showDatePicker();
        } else if (v.getId() == R.id.btnBack) {
            //返回
            finish();
        } else if (v.getId() == R.id.btnConfirm) {
            //预约
            String name = activityAppointmentBinding.etName.getText().toString();
            String mobile = activityAppointmentBinding.etMobile.getText().toString();
            String content = activityAppointmentBinding.etContent.getText().toString();
            if (name.equals("") || mobile.equals("") || content.equals("") || time == -1) {
                toast("请将信息填写完整");
                return;
            }
            Appointment appointment = new Appointment();
            appointment.doctor_username = doctor_username;//咨询师
            appointment.username = Global.user.username;//当前用户
            appointment.name=name;//姓名
            appointment.dtime = time;//时间
            appointment.mobile = mobile;//手机
            appointment.content = content;//症状
            new Thread(() -> {
                ApiResponse<String> apiResponse = HttpUtil.addAppointment(appointment);//预约
                if (apiResponse.success()) {
                    finish();//关闭
                }
                toast(apiResponse.message);//提示
            }).start();
        }
    }

    private int year, month, dayOfMonth;//年月日
    private long time = -1;//预约时间

    //日期选择
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();//日期
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            AppointmentActivity.this.year = year;
            AppointmentActivity.this.month = month;
            AppointmentActivity.this.dayOfMonth = dayOfMonth;
            showTImePicker();//继续选择时间
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());//最小日期今天
        datePickerDialog.show();
    }

    //时间选择
    private void showTImePicker() {
        Calendar calendar = Calendar.getInstance();//日期时间
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);//秒
            calendar.set(Calendar.MILLISECOND, 0);//毫秒
            time = calendar.getTimeInMillis();//设置预约时间
            //设置文本框
            activityAppointmentBinding.etDate.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }
}