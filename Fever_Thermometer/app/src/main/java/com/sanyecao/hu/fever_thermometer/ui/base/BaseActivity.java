package com.sanyecao.hu.fever_thermometer.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by huhaisong on 2017/8/14 10:55.
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        ActivityStack.getInstance().push(this);
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
