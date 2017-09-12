package com.sanyecao.hu.fever_thermometer.ui.temperature;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sanyecao.hu.fever_thermometer.R;
import com.sanyecao.hu.fever_thermometer.mode.logic.temperature.TemperatureController;
import com.sanyecao.hu.fever_thermometer.service.BluetoothService;
import com.sanyecao.hu.fever_thermometer.ui.base.MainActivity;
import com.sanyecao.hu.fever_thermometer.ui.view.MyViewPager;

import java.util.ArrayList;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Created by huhaisong on 2017/8/15 13:44.
 */

@TargetApi(Build.VERSION_CODES.M)
public class TemperatureFragment extends Fragment {
    private static TemperatureFragment mInstance;
    private Context mContext;
    private ImageView[] dots;
    private LinearLayout dotViewLayout;
    public MyViewPager viewPager;
    private TemperatureController temperatureController;
    private ArrayList<MachineFragment> machineFragments;
    private MyOnPagerChangeListener myOnPagerChangeListener;
    private MainActivity.MyOnTouchListener myOnTouchListener;
    private TemperatureAdapter temperatureAdapter;

    public static TemperatureFragment getInstance() {
        return mInstance == null ? mInstance = new TemperatureFragment() : mInstance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_temperature_layout, container, false);
        initData();
        initView(rootView);
        initListen();
        return rootView;
    }

    public static BluetoothService.BleBinder myBinder;

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder = (BluetoothService.BleBinder) service;
        }
    };

    private void initData() {

        Intent bindIntent = new Intent(getContext(), BluetoothService.class);
        getActivity().bindService(bindIntent, connection, BIND_AUTO_CREATE);

        final GestureDetector mGestureDetector = new GestureDetector(getActivity(), new MyOnGestureListener());
        myOnTouchListener = new MainActivity.MyOnTouchListener() {
            @Override
            public boolean onTouch(MotionEvent ev) {
                return mGestureDetector.onTouchEvent(ev);
            }
        };
        temperatureController = new TemperatureController(mContext);
        if (machineFragments != null)
            machineFragments.clear();
        else
            machineFragments = new ArrayList<>();
        for (int i = 0; i < temperatureController.getMachineNum(); i++) {
            MachineFragment machineFragment = new MachineFragment(i, TemperatureFragment.this);
            machineFragments.add(machineFragment);
        }
        myOnPagerChangeListener = new MyOnPagerChangeListener();
        temperatureAdapter = new TemperatureAdapter(getChildFragmentManager(), machineFragments);
    }

    private void initView(View view) {
        dotViewLayout = (LinearLayout) view.findViewById(R.id.viewGroup);
        viewPager = (MyViewPager) view.findViewById(R.id.temperature_viewpager);
        viewPager.setScrollble(true);
        initDots();
        ((MainActivity) getActivity()).setMainToolBarTitle("temperature");
    }

    private void initListen() {
        viewPager.setAdapter(temperatureAdapter);
        viewPager.setOnPageChangeListener(myOnPagerChangeListener);
        viewPager.setCurrentItem(0);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).registerMyOnTouchListener(myOnTouchListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) getActivity()).unregisterMyOnTouchListener(myOnTouchListener);
    }

    private void initDots() {
        dotViewLayout.removeAllViews();
        dots = new ImageView[temperatureController.getMachineNum()];
        for (int i = 0; i < dots.length; i++) {
            ImageView imageView = new ImageView(getActivity());
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
        for (int i = 0; i < dots.length; i++) {
            if (i == selectedItem) {
                dots[i].setBackgroundResource(R.drawable.page_indicator_focused);
            } else {
                dots[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
            }
        }
    }

    private static final String TAG = "TemperatureFragment";

    private class MyOnPagerChangeListener implements ViewPager.OnPageChangeListener {
        private boolean flag = false;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            updateDots(position % temperatureController.getMachineNum());
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            Log.d("vivi", "onPageScrollStateChanged: " + state);
            switch (state) {
                case ViewPager.SCROLL_STATE_DRAGGING:
                    //拖的时候才进入下一页
                    flag = false;
                    Log.e("vivi", "SCROLL_STATE_DRAGGING: " + ViewPager.SCROLL_STATE_DRAGGING);
                    break;
                case ViewPager.SCROLL_STATE_SETTLING:
                    flag = true;
                    Log.e("vivi", "SCROLL_STATE_SETTLING: " + ViewPager.SCROLL_STATE_SETTLING);
                    break;
                case ViewPager.SCROLL_STATE_IDLE:
                    Log.e("vivi", "SCROLL_STATE_IDLE: " + ViewPager.SCROLL_STATE_IDLE + "  mViewPager.getCurrentItem() " + viewPager.getCurrentItem());
                    //判断是不是最后一页，同是是不是拖的状态
                    if (viewPager.getCurrentItem() == temperatureAdapter.getCount() - 1 && !flag) {
                        Log.e(TAG, "onPageScrollStateChanged: add new machine");
                        // overridePendingTransition(0, 0);
                    }
                    flag = true;
                    break;
            }
        }
    }

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
                Log.e(TAG, "向上滑动 ");

            } else if (e2.getY() - e1.getY() > verticalMinDistanceY && Math.abs(velocityX) > minVelocity) {
                //向下滑动
                Log.e(TAG, "向下滑动 ");
            } else if (e1.getX() - e2.getX() > verticalMinDistanceX && Math.abs(velocityX) > minVelocity) {
                //向左滑动
                Log.e(TAG, "向左滑动 ");
            } else if (e2.getX() - e1.getX() > verticalMinDistanceX && Math.abs(velocityX) > minVelocity) {
                //向右滑动
                Log.e(TAG, "向右滑动 ");
            }
            return false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (myBinder != null)
            myBinder.closeBluetoothService();
        getActivity().unbindService(connection);
    }
}