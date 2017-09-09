package com.sanyecao.hu.fever_thermometer.ui.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sanyecao.hu.fever_thermometer.R;
import com.sanyecao.hu.fever_thermometer.ui.base.MainActivity;

/**
 * Created by huhaisong on 2017/8/15 13:44.
 */

public class SettingFragment extends Fragment {

    View view;
    private static SettingFragment mInstance;

    public static SettingFragment getInstance() {
        return mInstance == null ? mInstance = new SettingFragment() : mInstance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_setting_layout , container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        ((MainActivity)getActivity()).setMainToolBarTitle("Setting");
    }
}
