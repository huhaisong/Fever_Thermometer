package com.sanyecao.hu.fever_thermometer.mode.logic.temperature;

import android.content.Context;

/**
 * Created by huhaisong on 2017/8/15 16:35.
 */

public class TemperatureController {

    private int machineNum = 3;
    private Context mContext;

    public TemperatureController(Context mContext) {
        this.mContext = mContext;
    }

    public int getMachineNum() {
        return machineNum;
    }

    public void setMachineNum(int machineNum) {
        this.machineNum = machineNum;
    }
}
