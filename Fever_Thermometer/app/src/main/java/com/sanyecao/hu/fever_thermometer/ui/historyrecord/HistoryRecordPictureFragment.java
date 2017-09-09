package com.sanyecao.hu.fever_thermometer.ui.historyrecord;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sanyecao.hu.fever_thermometer.R;

/**
 * Created by huhaisong on 2017/8/16 17:20.
 */

public class HistoryRecordPictureFragment extends Fragment {
    View view;
    private static HistoryRecordPictureFragment mInstance;

    public static HistoryRecordPictureFragment getInstance() {
        return mInstance == null ? mInstance = new HistoryRecordPictureFragment() : mInstance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_history_record_picture_layout, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void initView(View view) {

    }
}
