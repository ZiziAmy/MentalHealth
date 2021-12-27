package com.example.mentalhealth.activity;

import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.example.mentalhealth.R;
import com.example.mentalhealth.databinding.ActivityDoctorBinding;
import com.example.mentalhealth.fragment.doctor.AppointmentManageFragment;
import com.example.mentalhealth.fragment.doctor.DoctorMineFragment;

//咨询师的主页
public class DoctorActivity extends BaseActivity implements View.OnClickListener {
    private ActivityDoctorBinding activityDoctorBinding;//界面对象
    private View[] buttons;//底部按钮数组，用于切换变色等等
    private AppointmentManageFragment appointmentManageFragment;//预约管理
    private DoctorMineFragment doctorMineFragment;//我的

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //布局初始化
        activityDoctorBinding = DataBindingUtil.setContentView(this, R.layout.activity_doctor);
        //初始化导航按钮
        buttons = new View[2];
        buttons[0] = activityDoctorBinding.ll1;
        buttons[1] = activityDoctorBinding.ll2;
        loadAppointmentManage();//默认加载故事管理页面
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll1) {
            //首页
            loadAppointmentManage();//加载首页
        } else if (v.getId() == R.id.ll2) {
            //我的
            loadDoctorMine();
        }
    }

    //预约管理
    private void loadAppointmentManage() {
        //切换页面
        getSupportFragmentManager().beginTransaction().replace(R.id.fl, appointmentManageFragment = new AppointmentManageFragment()).commit();
        setTitle("接待");//设置标题
        changeNavigation(0);//修改导航按钮颜色
    }

    //我的
    private void loadDoctorMine() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fl, doctorMineFragment = new DoctorMineFragment()).commit();
        setTitle("我的");
        changeNavigation(1);
    }


    //处理导航按钮选中颜色
    private void changeNavigation(int pos) {
        //修改导航按钮状态
        for (int i = 0; i < buttons.length; i++) {
            if (pos == i) {
                //选中的导航，变色
                buttons[i].setBackgroundResource(R.color.blue2);
            } else {
                //没选中的导航，不变色
                buttons[i].setBackgroundResource(R.color.blue1);
            }
        }
    }
}
