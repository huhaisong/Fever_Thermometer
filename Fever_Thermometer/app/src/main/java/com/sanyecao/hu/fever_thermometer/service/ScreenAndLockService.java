package com.sanyecao.hu.fever_thermometer.service;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

/**
 * Created by huhaisong on 2017/9/19 19:34.
 */

public class ScreenAndLockService extends Service {
    // 键盘管理器
    KeyguardManager mKeyguardManager;
    // 键盘锁
    private KeyguardManager.KeyguardLock mKeyguardLock;
    // 电源管理器
    private PowerManager mPowerManager;
    // 唤醒锁
    private PowerManager.WakeLock mWakeLock;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    private static final String TAG = "ScreenAndLockService";
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate: " );
        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.e(TAG, "onStart: 1" );
        // 点亮亮屏
        mWakeLock = mPowerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "Tag");
        mWakeLock.acquire();
        // 初始化键盘锁
        mKeyguardLock = mKeyguardManager.newKeyguardLock("");
        // 键盘解锁
        mKeyguardLock.disableKeyguard();
        Log.e(TAG, "onStart: 2" );
    }

    //一定要释放唤醒锁和恢复键盘
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mWakeLock != null) {
            Log.e(TAG, "onDestroy: 1 终止服务,释放唤醒锁" );
            mWakeLock.release();
            mWakeLock = null;
        }
        if (mKeyguardLock != null) {
            Log.e(TAG, "onDestroy: 2 终止服务,重新锁键盘" );
            mKeyguardLock.reenableKeyguard();
        }
    }
}
