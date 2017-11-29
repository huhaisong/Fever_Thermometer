package com.sanyecao.hu.fever_thermometer.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sanyecao.hu.fever_thermometer.permissions.Nammu;
import com.sanyecao.hu.fever_thermometer.permissions.PermissionCallback;

/**
 * Created by huhaisong on 2017/8/14 10:55.
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected Context mContext;
    String[] permissions = new String[]{android.Manifest.permission.READ_CONTACTS, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除title
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//去掉Activity上面的状态栏
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.mContext = this;
        ActivityStack.getInstance().push(this);
        Nammu.askForPermission(this, permissions, new PermissionCallback() {
            @Override
            public void permissionGranted() {
            }

            @Override
            public void permissionRefused() {
            }
        });
    }

    @Override
    protected void onDestroy() {
        ActivityStack.getInstance().remove(this);
        super.onDestroy();
    }

    public abstract void initData();

    public abstract void initView();

    public abstract void initListener();
}
