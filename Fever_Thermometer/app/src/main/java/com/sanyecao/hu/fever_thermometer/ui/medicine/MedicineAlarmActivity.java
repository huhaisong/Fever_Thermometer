package com.sanyecao.hu.fever_thermometer.ui.medicine;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.sanyecao.hu.fever_thermometer.R;
import com.sanyecao.hu.fever_thermometer.ui.base.ActivityStack;
import com.sanyecao.hu.fever_thermometer.ui.base.ToolBarActivity;
import com.sanyecao.hu.fever_thermometer.ui.view.WheelView;
import com.sanyecao.hu.fever_thermometer.utils.PickerUtil;
import com.sanyecao.hu.fever_thermometer.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by huhaisong on 2017/8/16 16:42.
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class MedicineAlarmActivity extends ToolBarActivity implements View.OnClickListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_alarm_layout);
        initData();
        initView();
        initListener();
    }

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
    public void initData() {


        Intent intent = getIntent();
        String time = intent.getStringExtra("time");
        date = TimeUtils.string2Date(time);
        repeat_times.clear();
        space_times.clear();
        for (String item : repeattimes) {
            repeat_times.add(item);
        }
        for (String item : spacetimes) {
            space_times.add(item);
        }
    }

    @Override
    public void initView() {
        setToolBarTitle("添加提醒");
        setBarTextView("确定");

        timeLinearLayout = (LinearLayout) findViewById(R.id.time_layout);
        repeatTimeLinearLayout = (LinearLayout) findViewById(R.id.repeat_time_layout);
        spaceTimeLinearLayout = (LinearLayout) findViewById(R.id.space_time_layout);
        contentLinearLayout = (LinearLayout) findViewById(R.id.content_layout);
        timeTextView = (TextView) findViewById(R.id.tv_time);
        repeatTimeTextView = (TextView) findViewById(R.id.tv_repeat_time);
        spaceTimeTextView = (TextView) findViewById(R.id.tv_space_time);
        timeTextView.setText(TimeUtils.date2String(date));

        repeatWheelView = new WheelView(this);
        repeatWheelView.setItems(repeat_times);
        repeatWheelView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                super.onSelected(selectedIndex, item);
                repeatTime = selectedIndex;
                repeatTimeTextView.setText(repeat_times.get(selectedIndex - 1));
            }
        });
        spaceWheelView = new WheelView(this);
        spaceWheelView.setItems(space_times);
        spaceWheelView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                super.onSelected(selectedIndex, item);
                spaceTime = selectedIndex;
                spaceTimeTextView.setText(space_times.get(selectedIndex - 1));
            }
        });

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        timeAndDatePickerView = layoutInflater.inflate(R.layout.time_date_picker, null);

        datePicker = (DatePicker) timeAndDatePickerView.findViewById(R.id.dp);
        timePicker = (TimePicker) timeAndDatePickerView.findViewById(R.id.tp);
        timePicker.setOnTimeChangedListener(onTimeChangedListener);
        timePicker.setIs24HourView(true);
        PickerUtil.resizePikcer(timePicker, this);
        PickerUtil.resizePikcer(datePicker, this);
    }

    private View timeAndDatePickerView;

    @Override
    public void initListener() {
        timeLinearLayout.setOnClickListener(this);
        repeatTimeLinearLayout.setOnClickListener(this);
        spaceTimeLinearLayout.setOnClickListener(this);
        setBarTextViewListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("time", TimeUtils.date2String(date));
                setResult(RESULT_OK, resultIntent);
                ActivityStack.getInstance().popAndFinish();
            }
        });
    }

    private static final String TAG = "MedicineAlarmActivity";

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.time_layout:
                Log.e(TAG, "onClick: time_layout");
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
                Log.e(TAG, "onClick: repeat_time_layout");
                contentLinearLayout.removeAllViews();
                repeatWheelView.setSeletion(repeatTime - 1);
                contentLinearLayout.addView(repeatWheelView);
                break;
            case R.id.space_time_layout:
                Log.e(TAG, "onClick: space_time_layout");
                contentLinearLayout.removeAllViews();
                spaceWheelView.setSeletion(spaceTime - 1);
                contentLinearLayout.addView(spaceWheelView);
                break;
            default:
                break;
        }
    }
}
