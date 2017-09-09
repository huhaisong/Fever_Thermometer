package com.sanyecao.hu.fever_thermometer.ui.medicine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sanyecao.hu.fever_thermometer.R;
import com.sanyecao.hu.fever_thermometer.ui.base.ToolBarActivity;
import com.sanyecao.hu.fever_thermometer.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by huhaisong on 2017/8/16 13:47.
 */

public class MedicineActivity extends ToolBarActivity implements View.OnClickListener {
    GestureDetector mGestureDetector;
    private FragmentManager fragmentManager;
    private ViewPager viewPager;
    private LinearLayout dotViewLayout;
    private ImageView[] dots;
    private MyFragmentPagerAdapter myFragmentPagerAdapter;
    private TextView setAlarmTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine);
        initData();
        initView();
        initListener();
    }

    @Override
    public void initData() {
        mGestureDetector = new GestureDetector(this, new MyOnGestureListener());
        fragmentManager = getSupportFragmentManager();
        ArrayList<BaseMedicineFragment> fragments = new ArrayList<>();
        fragments.add(MedicationRecordFragment.getInstance());
        fragments.add(MedicationNoteFragment.getInstance());
        MedicationRecordFragment.getInstance().setDefaultBabyBean(getIntent().getStringExtra("babyName"));
        myFragmentPagerAdapter = new MyFragmentPagerAdapter(fragmentManager, fragments);
    }

    @Override
    public void initView() {
        viewPager = (ViewPager) findViewById(R.id.medicine_viewpager);
        dotViewLayout = (LinearLayout) findViewById(R.id.viewGroup);
        setAlarmTextView = (TextView) findViewById(R.id.tv_set_alarm);
        initDots();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initDots() {
        dotViewLayout.removeAllViews();
        dots = new ImageView[2];
        for (int i = 0; i < dots.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(10, 10));
            dots[i] = imageView;
            if (i == 0) {
                dots[i].setBackgroundResource(R.drawable.page_indicator_focused);
            } else {
                dots[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
            }
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 5;
            layoutParams.rightMargin = 5;
            dotViewLayout.addView(imageView, layoutParams);
        }
    }

    private void updateDots(int selectedItem) {
        selectedItem = selectedItem % dots.length;
        for (int i = 0; i < dots.length; i++) {
            if (i == selectedItem) {
                dots[i].setBackgroundResource(R.drawable.page_indicator_focused);
            } else {
                dots[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
            }
        }
    }

    @Override
    public void initListener() {
        viewPager.setAdapter(myFragmentPagerAdapter);
        viewPager.setOnPageChangeListener(new MyOnPagerChangeListener());
//        viewPager.setCurrentItem(0);
        setAlarmTextView.setOnClickListener(this);
    }

    private static final int MEDICINEACTIVITY_RESULT_CODE = 1;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_set_alarm:
                Intent intent = new Intent(this, MedicineAlarmActivity.class);
                if (time == null) {
                    Date date = new Date();
                    date.setTime(System.currentTimeMillis());
                    time = TimeUtils.date2String(date);
                }
                intent.putExtra("time", time);
                startActivityForResult(intent, MEDICINEACTIVITY_RESULT_CODE);
                break;
            default:
                break;
        }
    }

    public void startActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
        Log.e(TAG, "startActivity: " );
    }

    private boolean isFirst = true;

    private class MyOnPagerChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (isFirst) {
                isFirst = !isFirst;
                myFragmentPagerAdapter.fragments.get(0).initToolbar();
            }
        }

        @Override
        public void onPageSelected(int position) {
            updateDots(position);
            myFragmentPagerAdapter.fragments.get(position).initToolbar();
            Log.e(TAG, "onPageSelected: ");
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            Log.e(TAG, "onPageScrollStateChanged: ");
        }
    }

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public ArrayList<BaseMedicineFragment> fragments;

        public MyFragmentPagerAdapter(FragmentManager fm, ArrayList<BaseMedicineFragment> fragments) {
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mGestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    private static final String TAG = "MedicineActivity";

    private class MyOnGestureListener implements GestureDetector.OnGestureListener {
        private int verticalMinDistanceX = 100;
        private int verticalMinDistanceY = 500;
        private int minVelocity = 0;

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1.getY() - e2.getY() > verticalMinDistanceY && Math.abs(velocityX) > minVelocity) {
                //向上滑动
            } else if (e2.getY() - e1.getY() > verticalMinDistanceY && Math.abs(velocityX) > minVelocity) {
                //向下滑动
                onBackPressed();
            } else if (e1.getX() - e2.getX() > verticalMinDistanceX && Math.abs(velocityX) > minVelocity) {
                //向左滑动
            } else if (e2.getX() - e1.getX() > verticalMinDistanceX && Math.abs(velocityX) > minVelocity) {
                //向右滑动
            }
            return false;
        }
    }

    String time;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MEDICINEACTIVITY_RESULT_CODE && resultCode == RESULT_OK) {
            time = data.getStringExtra("time");
        }
    }
}