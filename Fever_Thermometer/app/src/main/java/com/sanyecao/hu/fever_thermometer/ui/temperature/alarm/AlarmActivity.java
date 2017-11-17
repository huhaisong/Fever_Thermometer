package com.sanyecao.hu.fever_thermometer.ui.temperature.alarm;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.sanyecao.hu.fever_thermometer.R;
import com.sanyecao.hu.fever_thermometer.ui.base.BaseTollBarFragment;
import com.sanyecao.hu.fever_thermometer.ui.base.ToolBarActivity;

import java.util.ArrayList;

/**
 * Created by huhaisong on 2017/8/16 16:42.
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class AlarmActivity extends ToolBarActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_layout);
        initData();
        initView();
        initListener();
    }

    private AlarmRecodeFragment alarmRecodeFragment;
    private AlarmSetFragment alarmSetFragment;
    private MyViewPagerAdapter myViewPagerAdapter;

    @Override
    public void initData() {
        alarmRecodeFragment = AlarmRecodeFragment.getInstance();
        alarmSetFragment = AlarmSetFragment.getInstance();
        ArrayList<BaseTollBarFragment> fragments = new ArrayList<>();
        fragments.add(alarmSetFragment);
        fragments.add(alarmRecodeFragment);
        myViewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager(), fragments);
    }

    private ViewPager viewPager;

    @Override
    public void initView() {
        viewPager = (ViewPager) findViewById(R.id.alarm_viewpager);
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
    }

    @Override
    public void initListener() {

    }

    private static final String TAG = "AlarmActivity";

    @Override
    public void onClick(View v) {

    }


    private class MyViewPagerAdapter extends FragmentPagerAdapter {

        ArrayList<BaseTollBarFragment> fragments;

        public MyViewPagerAdapter(FragmentManager fm, ArrayList<BaseTollBarFragment> fragments) {
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

    private boolean isFirst = true;

    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (isFirst) {
                isFirst = false;
                myViewPagerAdapter.fragments.get(0).initToolbar();
            }
        }

        @Override
        public void onPageSelected(int position) {
            myViewPagerAdapter.fragments.get(position).initToolbar();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
