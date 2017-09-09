package com.sanyecao.hu.fever_thermometer.ui.historyrecord;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.sanyecao.hu.fever_thermometer.R;
import com.sanyecao.hu.fever_thermometer.mode.database.a.DatabaseController;
import com.sanyecao.hu.fever_thermometer.mode.database.bean.BabyBean;
import com.sanyecao.hu.fever_thermometer.ui.medicine.NameSpinnerAdapter;
import com.sanyecao.hu.fever_thermometer.ui.view.FeverTemperatureGridView;
import com.sanyecao.hu.fever_thermometer.utils.TimeUtils;

import java.util.List;

/**
 * Created by huhaisong on 2017/8/16 17:19.
 */

public class HistoryRecordDetailFragment extends Fragment {

    private static HistoryRecordDetailFragment mInstance;
    private View view;
    private ListView noteListView;
    private Spinner babySpinner;
    private FeverTemperatureGridView temperatureGridView;
    private TextView timeTextView;

    private NameSpinnerAdapter babySpinnerAdapter;
    private BabyBean mBabyBean;   //被选中的宝宝
    private List<BabyBean> babyBeanList;  //数据库里面所有的宝宝列表
    private String time;
    private NoteRecodeAdapter noteRecodeAdapter;

    public static HistoryRecordDetailFragment getInstance() {
        return mInstance == null ? mInstance = new HistoryRecordDetailFragment() : mInstance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_history_record_detail_layout, container, false);
        initView(view, inflater);
        return view;
    }

    private void initView(View view, LayoutInflater inflater) {
        noteListView = (ListView) view.findViewById(R.id.listView_note);
        babySpinner = (Spinner) view.findViewById(R.id.spinner_baby);
        timeTextView = (TextView) view.findViewById(R.id.tv_time);
        temperatureGridView = (FeverTemperatureGridView) view.findViewById(R.id.gv_fever_temperature);
        babyBeanList = DatabaseController.getmInstance().getAllBabyBean();
        babySpinnerAdapter = new NameSpinnerAdapter(babyBeanList, getContext(), inflater);
        babySpinner.setAdapter(babySpinnerAdapter);
        babySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mBabyBean = babyBeanList.get(position);
                updateView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 直接创建一个DatePickerDialog对话框实例，并将它显示出来
                new DatePickerDialog(getContext(),
                        // 绑定监听器
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                time = TimeUtils.getTimeString(year, monthOfYear, dayOfMonth);
                                updateView();
                            }
                        }
                        // 设置初始日期
                        , Integer.valueOf(time.split("-")[0])
                        , Integer.valueOf(time.split("-")[1]) - 1
                        , Integer.valueOf(time.split("-")[2]))
                        .show();
            }
        });
        time = TimeUtils.getCurTimeString().split(" ")[0];
        timeTextView.setText(time);

        noteRecodeAdapter = new NoteRecodeAdapter(DatabaseController.getmInstance().queryAllRecodeNoteBean(), getContext(), inflater);
        noteListView.setAdapter(noteRecodeAdapter);
    }

    private void updateView() {
        babyBeanList = DatabaseController.getmInstance().getAllBabyBean();
        noteRecodeAdapter.setItems(DatabaseController.getmInstance().queryAllRecodeNoteBean());
        babySpinnerAdapter.setItems(babyBeanList);
        if (mBabyBean != null) {
            temperatureGridView.setDataAndBaby(time, mBabyBean.getId());
            for (int i = 0; i < babyBeanList.size(); i++) {
                if (mBabyBean.equals(babyBeanList.get(i))) {
                    babySpinner.setSelection(i);
                    break;
                }
            }
        } else {
            temperatureGridView.setDataAndBaby(time, 0L);
        }
        timeTextView.setText(time);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateView();
    }

    public void setDefaultBabyBean(String babyName) {
        mBabyBean = DatabaseController.getmInstance().queryBabyBeanByName(babyName);
    }
}
