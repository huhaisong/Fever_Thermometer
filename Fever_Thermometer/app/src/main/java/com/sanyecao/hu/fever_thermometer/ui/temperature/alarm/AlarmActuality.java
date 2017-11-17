package com.sanyecao.hu.fever_thermometer.ui.temperature.alarm;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.sanyecao.hu.fever_thermometer.R;

import java.io.IOException;

/**
 * Created by huhaisong on 2017/9/19 10:15.
 */

public class AlarmActuality extends Activity {

    private Button button;

    private static final String TAG = "AlarmActuality";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_actuality_layout);
        Log.e(TAG, "onCreate: 1");
        lightScreen();
        Log.e(TAG, "onCreate: 2");
        initData();
        initView();
        initListener();
    }

    public void initData() {
    }

    public void initView() {
        button = (Button) findViewById(R.id.bt);
    }

    public void initListener() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        playRing();
    }

    PowerManager.WakeLock wakeLock;

    private void lightScreen() {
        PowerManager powerManager = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "");
        wakeLock.acquire();
        KeyguardManager mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

        // 初始化键盘锁
        KeyguardManager.KeyguardLock mKeyguardLock = mKeyguardManager.newKeyguardLock("");
        // 键盘解锁
        mKeyguardLock.disableKeyguard();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (player != null)
            player.stop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        wakeLock.release();
    }

    MediaPlayer player = null;

    private MediaPlayer playRing() {
        try {
            if (player != null) {
                player.stop();
                player = null;
            }
            player = new MediaPlayer();
            AssetManager assetManager = getAssets();
            AssetFileDescriptor fileDescriptor = assetManager.openFd("alarm.mp3");
            player.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getStartOffset());
            player.prepare();
            player.setLooping(true);
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return player;
    }
}
