package com.sanyecao.hu.fever_thermometer.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import static com.sanyecao.hu.fever_thermometer.service.DaemonService.GRAY_SERVICE_ID;

/**
 * Created by huhaisong on 2017/9/13 14:19.
 */

public class DaemonInnerService extends Service{

    private static final String TAG = "DaemonInnerService";
    @Override
    public void onCreate() {
        Log.i(TAG, "InnerService -> onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "InnerService -> onStartCommand");
        startForeground(GRAY_SERVICE_ID, new Notification());
        //stopForeground(true);
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "InnerService -> onDestroy");
        super.onDestroy();
    }

}
