package com.sanyecao.hu.fever_thermometer.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.sanyecao.hu.fever_thermometer.mode.database.a.DatabaseController;
import com.sanyecao.hu.fever_thermometer.mode.database.bean.AlarmBean;
import com.sanyecao.hu.fever_thermometer.utils.TimeUtils;

import java.util.Calendar;
import java.util.List;

public class AlarmService extends Service {
    public static final String UPDATE_ALARM_ACTION = "android.intent.action.UPDATE_ALARM";
    private static final String TAG = "AlarmService";
    private List<AlarmBean> times;
    private MyBroadCast myBroadCast = new MyBroadCast();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate: ");
        IntentFilter intentFilter = new IntentFilter(UPDATE_ALARM_ACTION);
        registerReceiver(myBroadCast, intentFilter);
        initData();
        loop = true;
        startCheckTime();
    }

    private void initData() {
        times = DatabaseController.getmInstance().queryAllAlarmBean();
        for (AlarmBean item : times) {
            Log.e(TAG, "initData: " + item.toString());
        }
    }

    public void startCheckTime() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (loop) {
                    try {
                        Thread.sleep(10 * 1000);
                        checkAlarm();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void checkAlarm() {
        long sub;
        Calendar calendar;
        for (AlarmBean item : times) {
            Log.e(TAG, "checkAlarm: " + item.toString());
            calendar = Calendar.getInstance();
            calendar.setTimeInMillis(TimeUtils.string2Milliseconds(item.getTime()));
            sub = System.currentTimeMillis() - calendar.getTimeInMillis();
            for (int i = 0; i < item.getRepeatTime(); i++) {
                if (Math.abs(sub - item.getSpaceTime() * i * 60 * 60 * 1000) <= 5 * 1000) {
                    Intent restartIntent = new Intent("android.intent.action.ALARM_ON");
                    sendBroadcast(restartIntent);
                    Log.e(TAG, "checkAlarm: " + item.toString());
                    TimeUtils.showTime(calendar.getTimeInMillis(), "is on alarm");
                    return;
                }
            }
        }
    }

    private boolean loop = true;

    @Override
    public void onDestroy() {
        loop = false;
        unregisterReceiver(myBroadCast);
        Log.e(TAG, "onDestroy: ");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class MyBroadCast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "onReceive: ");
            initData();
        }
    }
}