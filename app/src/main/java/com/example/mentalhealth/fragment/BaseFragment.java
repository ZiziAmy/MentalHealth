package com.example.mentalhealth.fragment;

import android.os.Handler;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

//基础Fragment
public class BaseFragment extends Fragment {
    //每一个fragment都有一个handler属性
    public Handler handler = new Handler();

    //Toast
    public void toast(String msg) {
        handler.post(() -> {
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        });
    }
}
