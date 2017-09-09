package com.sanyecao.hu.fever_thermometer.ui.historyrecord;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sanyecao.hu.fever_thermometer.R;
import com.sanyecao.hu.fever_thermometer.ui.base.MainActivity;

import java.util.ArrayList;

/**
 * Created by huhaisong on 2017/8/15 13:44.
 */

public class HistoryRecordFragment extends Fragment {
    View view;
    private static HistoryRecordFragment mInstance;
    private ViewPager viewPager;

    public static HistoryRecordFragment getInstance() {
        return mInstance == null ? mInstance = new HistoryRecordFragment() : mInstance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_history_record_layout, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        ((MainActivity) getActivity()).setMainToolBarTitle("History Record");
        viewPager = (ViewPager) view.findViewById(R.id.history_viewpager);
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(HistoryRecordDetailFragment.getInstance());
        fragments.add(HistoryRecordPictureFragment.getInstance());
        viewPager.setAdapter(new MyViewPager(getChildFragmentManager(), fragments));
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
        viewPager.setCurrentItem(0);
    }

    private class MyViewPager extends FragmentPagerAdapter {

        ArrayList<Fragment> fragments = new ArrayList<>();

        public MyViewPager(FragmentManager fm, ArrayList<Fragment> fragments) {
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

    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (position == 0) {
                ((MainActivity) getActivity()).setMainToolBarTitle("History Record");
                ((MainActivity) getActivity()).setMainToolBarTextViewContent("");
            } else if (position == 1) {
                ((MainActivity) getActivity()).setMainToolBarTitle(" ");
                ((MainActivity) getActivity()).setMainToolBarTextViewContent("保存图片");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
