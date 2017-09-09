package com.sanyecao.hu.fever_thermometer.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * Created by huhaisong on 2017/8/15 20:50.
 */

public class FragmentUtils {
    private static final String TAG = "FragmentUtils";

    public static void changeFragment(FragmentManager fragmentManager, Fragment fragment, int resourceId) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(resourceId, fragment);
        fragmentTransaction.commit();
    }

}
