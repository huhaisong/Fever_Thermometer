package com.dasu.ganhuo.ui.load;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dasu.ganhuo.R;
import com.dasu.ganhuo.ui.base.ActivityStack;
import com.dasu.ganhuo.ui.base.BaseActivity;
import com.dasu.ganhuo.ui.home.HomeActivity;
import com.dasu.ganhuo.ui.view.GlideCircleTransform;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by dasu on 2017/4/14.
 */

public class LoadActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_load);
        ImageView me = (ImageView) findViewById(R.id.iv_load_me);
        Glide.with(mContext)
                .load(R.drawable.img_me)
                .transform(new GlideCircleTransform(mContext))
                .into(me);
//        new LoadController(this).loadBaseData();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onLoadFinish();
            }
        }, 3000);
    }

    public void onLoadFinish() {
        startActivity(new Intent(mContext, HomeActivity.class));
        Log.e(TAG, "onLoadFinish: ");
        ActivityStack.getInstance().popAndFinishActivity();
    }

    private static final String TAG = "LoadActivity";

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: ");
    }
}
