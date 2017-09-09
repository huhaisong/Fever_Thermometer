package com.sanyecao.hu.fever_thermometer.ui.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sanyecao.hu.fever_thermometer.R;
import com.sanyecao.hu.fever_thermometer.ui.view.GlideCircleTransform;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by huhaisong on 2017/8/30 9:24.
 */

public class LoadActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_load);
        ImageView me = (ImageView) findViewById(R.id.iv_load_me);
        Glide.with(mContext)
                .load(R.drawable.header)
                .transform(new GlideCircleTransform(mContext))
                .into(me);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(mContext, MainActivity.class));
                ActivityStack.getInstance().popAndFinish();
            }
        }, 3000);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initView() {

    }

    @Override
    public void initListener() {

    }
}
