package com.example.mentalhealth.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.example.mentalhealth.R;
import com.example.mentalhealth.databinding.ActivityMainBinding;
import com.example.mentalhealth.fragment.IndexFragment;
import com.example.mentalhealth.fragment.MineFragment;
import com.example.mentalhealth.fragment.MessageFragment;
import com.example.mentalhealth.fragment.StoryFragment;

//主页
public class MainActivity extends BaseActivity implements View.OnClickListener {
    private ActivityMainBinding activityMainBinding;//界面对象
    private View[] buttons;//底部按钮数组，用于切换变色等等
    private IndexFragment indexFragment;//首页
    private MessageFragment messageFragment;//信息
    private MineFragment mineFragment;//我的
    private StoryFragment storyFragment;//故事

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //布局初始化，绑定一个activity与布局
        //一个自动生成的类的对象，名字是布局界面+Binding
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        //初始化导航按钮
        buttons = new View[4];
        buttons[0] = activityMainBinding.ll1;
        buttons[1] = activityMainBinding.ll2;
        buttons[2] = activityMainBinding.ll3;
        buttons[3] = activityMainBinding.ll4;

        loadIndex();//默认加载主页
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll1) {
            //首页
            loadIndex();//加载首页
        } else if (v.getId() == R.id.ll2) {
            //信息
            loadMessage();
        } else if (v.getId() == R.id.ll3) {
            //故事
            loadStory();
        }else if (v.getId() == R.id.ll4) {
            //我的
            loadMine();
        }
    }

    //首页
    private void loadIndex() {
        //切换页面到首页
        //transaction事务,用来添加，移除，替换fragment,最后要commit
        getSupportFragmentManager().beginTransaction().replace(R.id.fl, indexFragment = new IndexFragment()).commit();
        setTitle("首页");//设置标题
        changeNavigation(0);//修改导航按钮颜色
    }

    //信息
    private void loadMessage() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fl, messageFragment = new MessageFragment()).commit();
        setTitle("信息");
        changeNavigation(1);
    }

    //个人
    private void loadMine() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fl, mineFragment = new MineFragment()).commit();
        setTitle("我的");
        changeNavigation(3);
    }
    //个人
    private void loadStory() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fl, storyFragment = new StoryFragment()).commit();
        setTitle("故事");
        changeNavigation(2);
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



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 666 && indexFragment != null) {
            //是首页的回调方法
            indexFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

}
