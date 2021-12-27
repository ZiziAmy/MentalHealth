package com.example.mentalhealth.util;

import android.app.ProgressDialog;
import android.content.Context;

import com.example.mentalhealth.R;

//等待对话框
public class LoadingDialog {
    private static LoadingDialog loadingDialog;
    private Context context;
    private ProgressDialog progressDialog;

    private LoadingDialog(Context context, String msg) {
        this.context = context;
        progressDialog = new ProgressDialog(context, R.style.DialogTheme);
        progressDialog.setCancelable(false);//不可取消
        progressDialog.setMessage(msg);//设置提示内容
        progressDialog.setCanceledOnTouchOutside(false);//不可点击外部关闭
    }

    //显示对话框
    public static void show(Context context, String msg) {
        if (loadingDialog != null && loadingDialog.isShow()) {
            loadingDialog.close();//先关闭
        }
        loadingDialog = new LoadingDialog(context, msg);
        loadingDialog.progressDialog.show();//显示对话框
    }

    //关闭对话框
    public static void dismiss() {
        try {
            loadingDialog.close();
        } catch (Exception ignored) {
        }
    }

    //关闭
    public void close() {
        try {
            progressDialog.dismiss();
        } catch (Exception ignored) {
        }
    }

    //是否显示状态
    public boolean isShow() {
        return loadingDialog.progressDialog != null && loadingDialog.progressDialog.isShowing();
    }
}
