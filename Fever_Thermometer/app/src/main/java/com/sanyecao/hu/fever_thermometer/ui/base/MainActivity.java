package com.sanyecao.hu.fever_thermometer.ui.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.sanyecao.hu.fever_thermometer.R;
import com.sanyecao.hu.fever_thermometer.ui.historyrecord.HistoryRecordFragment;
import com.sanyecao.hu.fever_thermometer.ui.setting.SettingFragment;
import com.sanyecao.hu.fever_thermometer.ui.temperature.TemperatureFragment;

import java.util.ArrayList;

import static com.sanyecao.hu.fever_thermometer.utils.FragmentUtils.changeFragment;

/**
 * Created by huhaisong on 2017/8/15 10:51.
 */

public class MainActivity extends DrawerActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FragmentManager fragmentManager;
    private ArrayList<MyOnTouchListener> onTouchListeners = new ArrayList<MyOnTouchListener>();

    @Override
    protected int bindMenuId() {
        return TEMPERATURE;
    }

    CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    private TextView mainToolBarTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initView();
        initListener();
    }

    @Override
    public void initData() {
        fragmentManager = getSupportFragmentManager();
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        addToolbar(toolbar);
        mainToolBarTextView = (TextView) toolbar.findViewById(R.id.tv_main_toolbar);
        collapsingToolbarLayout = ((CollapsingToolbarLayout) findViewById(R.id.collapse_toolbar_layout));
        changeFragment(fragmentManager, TemperatureFragment.getInstance(), R.id.fragment_layout);
    }

    @Override
    public void initListener() {
        bindMeunListen(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == mCurrentMenu) {
            closeDrawer();
            return true;
        }
        mCurrentMenu = item.getItemId();
        switch (item.getItemId()) {
            case TEMPERATURE:
                changeFragment(fragmentManager, TemperatureFragment.getInstance(), R.id.fragment_layout);
                break;
            case SETTING:
                changeFragment(fragmentManager, SettingFragment.getInstance(), R.id.fragment_layout);
                break;
            case HISTORY_RECORD:
                changeFragment(fragmentManager, HistoryRecordFragment.getInstance(), R.id.fragment_layout);
                break;
            default:
                closeDrawer();
                break;
        }
        closeDrawer();
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        for (MyOnTouchListener listener : onTouchListeners) {
            listener.onTouch(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    public void registerMyOnTouchListener(MyOnTouchListener myOnTouchListener) {
        onTouchListeners.add(myOnTouchListener);
    }

    public void unregisterMyOnTouchListener(MyOnTouchListener myOnTouchListener) {
        onTouchListeners.remove(myOnTouchListener);
    }

    public interface MyOnTouchListener {
        public boolean onTouch(MotionEvent ev);
    }

    private static final String TAG = "MainActivity";

    public void setMainToolBarTitle(String title) {
        Log.e(TAG, "setMainToolBarTitle: " + title);
        collapsingToolbarLayout.setTitle(title);
    }

    public void setMainToolBarTextViewContent(String s) {
        if (s == null || s.equals("")) {
            mainToolBarTextView.setVisibility(View.GONE);
            return;
        }
        mainToolBarTextView.setVisibility(View.VISIBLE);
        mainToolBarTextView.setText(s);
    }

    public void setMainToolBarTextViewListener(View.OnClickListener listener) {
        mainToolBarTextView.setOnClickListener(listener);
    }
}
