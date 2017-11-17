package com.sanyecao.hu.fever_thermometer.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.sanyecao.hu.fever_thermometer.ui.temperature.alarm.AlarmActuality;

/**
 * Created by huhaisong on 2017/9/13 17:44.
 */

public class AlarmReceive extends BroadcastReceiver {
    private static final String TAG = "AlarmReceive";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "onReceive: ");
        Intent intent1 = new Intent(context, AlarmActuality.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);
    }
}
