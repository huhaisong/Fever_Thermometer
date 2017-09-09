package com.sanyecao.hu.fever_thermometer.ui.base;

import android.app.Activity;

import java.util.Stack;

/**
 * Created by huhaisong on 2017/8/14 11:30.
 */

public class ActivityStack {
    private static Stack<Activity> mActivityStack;
    private static ActivityStack mInstance;

    public static ActivityStack getInstance() {
        return mInstance == null ? (mInstance = new ActivityStack()) : mInstance;
    }

    public void push(Activity activity) {
        if (mActivityStack == null)
            mActivityStack = new Stack<>();
        mActivityStack.push(activity);
    }

    public void remove(Activity activity) {
        if (mActivityStack != null && activity != null)
            mActivityStack.remove(activity);
    }

    public void popAndFinish(Activity activity) {
        if (activity != null && mActivityStack != null) {
            mActivityStack.remove(activity);
            activity.finish();
            activity = null;
        }
        if (activity != null)
            activity.finish();
    }

    public void popAndFinish() {
        if (mActivityStack != null && mActivityStack.size() <= 0) {
            return;
        }
        Activity activity = mActivityStack.lastElement();
        popAndFinish(activity);
    }

    public Activity currentActivity() {
        return mActivityStack.lastElement();
    }

    public void cleanAll() {
        for (int i = mActivityStack.size() - 1; i >= 0; i--) {
            popAndFinish(mActivityStack.get(i));
        }
    }

    public int size() {
        return mActivityStack == null ? 0 : mActivityStack.size();
    }
}
