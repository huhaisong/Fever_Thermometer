package com.sanyecao.hu.fever_thermometer.ui.temperature;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by huhaisong on 2017/8/15 16:19.
 */

public class TemperatureAdapter extends FragmentPagerAdapter {


    ArrayList<MachineFragment> fragments;

    public TemperatureAdapter(FragmentManager fm, ArrayList<MachineFragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
