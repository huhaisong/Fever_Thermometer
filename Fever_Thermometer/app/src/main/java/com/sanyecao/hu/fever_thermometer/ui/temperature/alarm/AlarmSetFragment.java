package com.sanyecao.hu.fever_thermometer.ui.temperature.alarm;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.sanyecao.hu.fever_thermometer.R;
import com.sanyecao.hu.fever_thermometer.mode.database.a.DatabaseController;
import com.sanyecao.hu.fever_thermometer.mode.database.bean.AlarmBean;
import com.sanyecao.hu.fever_thermometer.service.AlarmService;
import com.sanyecao.hu.fever_thermometer.ui.base.ActivityStack;
import com.sanyecao.hu.fever_thermometer.ui.base.BaseTollBarFragment;
import com.sanyecao.hu.fever_thermometer.ui.base.ToolBarActivity;
import com.sanyecao.hu.fever_thermometer.ui.widget.WheelView;
import com.sanyecao.hu.fever_thermometer.utils.PickerUtil;
import com.sanyecao.hu.fever_thermometer.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import static android.content.ContentValues.TAG;

/**
 * Created by huhaisong on 2017/9/15 10:57.
 */

public class AlarmSetFragment extends BaseTollBarFragment implements View.OnClickListener {
    private static AlarmSetFragment mInstance;
    private LinearLayout timeLinearLayout;
    private LinearLayout repeatTimeLinearLayout;
    private LinearLayout spaceTimeLinearLayout;
    private LinearLayout contentLinearLayout;
    private TextView timeTextView;
    private TextView repeatTimeTextView;
    private TextView spaceTimeTextView;
    private Date date;
    private int spaceTime = 4 * 2;
    private int repeatTime = 4;
    private String[] repeattimes = {"1 Time", "2 Times", "3 Times",
            "4 Times", "5 Times", "6 Times", "7 Times", "8 Times", "9 Times", "10 Times", "11 Times"
            , "12 Times"};
    private String[] spacetimes = {"0.5 Hours", "1 Hours", "1.5 Hours"
            , "2 Hours", "2.5 Hours", "3 Hours", "3.5 Hours", "4 Hours", "4.5 Hours", "5 Hours"
            , "5.5 Hours", "6 Hours", "6.5 Hours", "7 Hours", "7.5 Hours", "8 Hours", "8.5 Hours"
            , "9 Hours", "9.5 Hours", "10 Hours", "10.5 Hours", "11 Hours", "11.5 Hours", "12 Hours"};
    private ArrayList<String> repeat_times = new ArrayList<>();
    private ArrayList<String> space_times = new ArrayList<>();
    private WheelView repeatWheelView;
    private WheelView spaceWheelView;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private DatePicker.OnDateChangedListener onDateChangedListener = new DatePicker.OnDateChangedListener() {
        @Override
        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(year, monthOfYear, dayOfMonth);
            date = calendar.getTime();
            timeTextView.setText(TimeUtils.date2String(date));
        }
    };
    private TimePicker.OnTimeChangedListener onTimeChangedListener = new TimePicker.OnTimeChangedListener() {
        @Override
        public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
            date.setHours(hourOfDay);
            date.setMinutes(minute);
            timeTextView.setText(TimeUtils.date2String(date));
        }
    };

    @Override
    public void initToolbar() {
        mActivity.initBar();
        mActivity.setToolBarTitle("添加提醒");
        mActivity.setBarTextView("确定");
        mActivity.setBarTextViewListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmBean alarmBean = new AlarmBean();
                alarmBean.setTime(TimeUtils.date2String(date));
                alarmBean.setSpaceTime((float) spaceTime / 2.0f);
                alarmBean.setRepeatTime(repeatTime);
                DatabaseController.getmInstance().addAlarmBean(alarmBean);
                getContext().sendBroadcast(new Intent(AlarmService.UPDATE_ALARM_ACTION));
                ActivityStack.getInstance().popAndFinish();
            }
        });
    }

    private LayoutInflater layoutInflater;
    private ToolBarActivity mActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mActivity = (ToolBarActivity) getActivity();
        layoutInflater = inflater;
        View view = inflater.inflate(R.layout.fragment_alarm_set_layout, container, false);
        initData();
        initView(view);
        initListen();
        return view;
    }

    private void initData() {
        date = TimeUtils.getCurTimeDate();
        repeat_times.clear();
        space_times.clear();
        Collections.addAll(repeat_times, repeattimes);
        Collections.addAll(space_times, spacetimes);
    }

    private View timeAndDatePickerView;

    private void initView(View view) {
        timeLinearLayout = (LinearLayout) view.findViewById(R.id.time_layout);
        repeatTimeLinearLayout = (LinearLayout) view.findViewById(R.id.repeat_time_layout);
        spaceTimeLinearLayout = (LinearLayout) view.findViewById(R.id.space_time_layout);
        contentLinearLayout = (LinearLayout) view.findViewById(R.id.content_layout);
        timeTextView = (TextView) view.findViewById(R.id.tv_time);
        repeatTimeTextView = (TextView) view.findViewById(R.id.tv_repeat_time);
        spaceTimeTextView = (TextView) view.findViewById(R.id.tv_space_time);
        timeTextView.setText(TimeUtils.date2String(date));

        repeatWheelView = new WheelView(getContext());
        repeatWheelView.setItems(repeat_times);
        repeatWheelView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                super.onSelected(selectedIndex, item);
                repeatTime = selectedIndex;
                repeatTimeTextView.setText(repeat_times.get(selectedIndex - 1));
            }
        });
        spaceWheelView = new WheelView(getContext());
        spaceWheelView.setItems(space_times);
        spaceWheelView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                super.onSelected(selectedIndex, item);
                spaceTime = selectedIndex;
                spaceTimeTextView.setText(space_times.get(selectedIndex - 1));
            }
        });

        timeAndDatePickerView = layoutInflater.inflate(R.layout.time_date_picker, null);

        datePicker = (DatePicker) timeAndDatePickerView.findViewById(R.id.dp);
        timePicker = (TimePicker) timeAndDatePickerView.findViewById(R.id.tp);
        timePicker.setOnTimeChangedListener(onTimeChangedListener);
        timePicker.setIs24HourView(true);
        PickerUtil.resizePikcer(timePicker, getContext());
        PickerUtil.resizePikcer(datePicker, getContext());
    }

    private void initListen() {
        timeLinearLayout.setOnClickListener(this);
        repeatTimeLinearLayout.setOnClickListener(this);
        spaceTimeLinearLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.time_layout:
                contentLinearLayout.removeAllViews();
                int[] times = TimeUtils.getByDate(date);
                datePicker.init(times[0], times[1], times[2], onDateChangedListener);
                Log.e(TAG, "onClick:date.getHours() = " + date.getHours() + ",date.getMinutes() =" + date.getMinutes());
                Log.e(TAG, "onClick:date.getYear() = " + date.getYear() + ",date.getMonth() =" + date.getMonth() + ",date.getDay() = " + date.getDay());
                timePicker.setCurrentHour(times[3]);
                timePicker.setCurrentMinute(times[4]);
                contentLinearLayout.addView(timeAndDatePickerView);
                break;
            case R.id.repeat_time_layout:
                contentLinearLayout.removeAllViews();
                repeatWheelView.setSeletion(repeatTime - 1);
                contentLinearLayout.addView(repeatWheelView);
                break;
            case R.id.space_time_layout:
                contentLinearLayout.removeAllViews();
                spaceWheelView.setSeletion(spaceTime - 1);
                contentLinearLayout.addView(spaceWheelView);
                break;
            default:
                break;
        }
    }

    public static AlarmSetFragment getInstance() {
        return mInstance == null ? (mInstance = new AlarmSetFragment()) : mInstance;
    }
}
