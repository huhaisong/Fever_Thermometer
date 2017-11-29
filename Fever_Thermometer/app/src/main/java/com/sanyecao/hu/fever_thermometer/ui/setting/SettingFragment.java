package com.sanyecao.hu.fever_thermometer.ui.setting;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.sanyecao.hu.fever_thermometer.R;
import com.sanyecao.hu.fever_thermometer.ui.base.MainActivity;
import com.sanyecao.hu.fever_thermometer.ui.widget.WheelView;
import com.sanyecao.hu.fever_thermometer.utils.SPUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.sanyecao.hu.fever_thermometer.comman.Config.ALARM_MODEL_KEY;
import static com.sanyecao.hu.fever_thermometer.comman.Config.SETTING_SP_KEY;
import static com.sanyecao.hu.fever_thermometer.comman.Config.TEMPERATURE_DOWN_KEY;
import static com.sanyecao.hu.fever_thermometer.comman.Config.TEMPERATURE_MEASURE_KEY;
import static com.sanyecao.hu.fever_thermometer.comman.Config.TEMPERATURE_UP_KEY;
import static com.sanyecao.hu.fever_thermometer.comman.Config.VOLUME_KEY;

/**
 * Created by huhaisong on 2017/8/15 13:44.
 */

public class SettingFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "SettingFragment";
    View view;
    private static SettingFragment mInstance;
    private static final int ALARM_MODEL_MESSAGE = 0;
    private static final int TEMPERATURE_MEASURE_MESSAGE = 1;
    private static final int TEMPERATURE_UP_MESSAGE = 2;
    private static final int TEMPERATURE_DOWN_MESSAGE = 3;
    private static final int DELAYED_TIME = 1000;


    public static SettingFragment getInstance() {
        return mInstance = new SettingFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_setting_layout, container, false);
        initData();
        initView(view);
        return view;
    }

    private Handler handler;

    //view
    private TextView alarmModelTextView;
    private TextView temperatureUpTextView;
    private TextView temperatureDownTextView;
    private TextView temperatureMeasureTextView;
    private Switch remoteSwASwitch;
    private WheelView alarmModelWheelView;
    private WheelView temperatureWheelView;
    private WheelView temperatureMeasureWheelView;
    private List<String> alarmModelList;
    private List<String> temperatureMeasureList;
    private List<String> temperatureList;
    private LinearLayout contentLinearLayout;
    private SeekBar volumSeekBar;

    //data
    private int temperatureMeasureId = 0;
    private int temperatureUpId = 0;
    private int temperatureDownId = 0;
    private int alarmModelId = 0;
    private SPUtils spUtils;
    private int volume = 0;

    private void initData() {
        if (alarmModelList == null || alarmModelList.size() == 0)
            alarmModelList = new ArrayList<>();
        alarmModelList.add("智能报警");
        alarmModelList.add("上下限报警");
        alarmModelList.add("关闭报警");
        if (temperatureMeasureList == null || temperatureMeasureList.size() == 0)
            temperatureMeasureList = new ArrayList<>();
        temperatureMeasureList.add("℃");
        temperatureMeasureList.add("℉");

        if (temperatureList == null || temperatureList.size() == 0)
            temperatureList = new ArrayList<>();

        for (int i = 0; i < 27; i++) {
            DecimalFormat df = new DecimalFormat(".##");
            String st = df.format(35.4 + i / 10.0f);
            temperatureList.add(st);
        }

        spUtils = new SPUtils(getContext(), SETTING_SP_KEY);
        temperatureUpId = spUtils.getInt(TEMPERATURE_UP_KEY, 20);
        temperatureDownId = spUtils.getInt(TEMPERATURE_DOWN_KEY, 4);
        temperatureMeasureId = spUtils.getInt(TEMPERATURE_MEASURE_KEY, 0);
        alarmModelId = spUtils.getInt(ALARM_MODEL_KEY, 0) % 3;
        volume = spUtils.getInt(VOLUME_KEY, 0);

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case ALARM_MODEL_MESSAGE:
                        spUtils.putInt(ALARM_MODEL_KEY, alarmModelId);
                        break;
                    case TEMPERATURE_MEASURE_MESSAGE:
                        spUtils.putInt(TEMPERATURE_MEASURE_KEY, temperatureMeasureId);
                        break;
                    case TEMPERATURE_UP_MESSAGE:
                        spUtils.putInt(TEMPERATURE_UP_KEY, temperatureUpId);
                        break;
                    case TEMPERATURE_DOWN_MESSAGE:
                        spUtils.putInt(TEMPERATURE_DOWN_KEY, temperatureDownId);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    private void initView(View view) {
        ((MainActivity) getActivity()).setMainToolBarTitle("Setting");
        alarmModelTextView = (TextView) view.findViewById(R.id.tv_alarm_switch_model);
        temperatureDownTextView = (TextView) view.findViewById(R.id.tv_down_temperature);
        temperatureUpTextView = (TextView) view.findViewById(R.id.tv_up_temperature);
        temperatureMeasureTextView = (TextView) view.findViewById(R.id.tv_temperature_unit_of_measure);
        contentLinearLayout = (LinearLayout) view.findViewById(R.id.content_layout);
        remoteSwASwitch = (Switch) view.findViewById(R.id.switch_remote_listen);
        volumSeekBar = (SeekBar) view.findViewById(R.id.volume_seekBar);

        volumSeekBar.setProgress(volume);

        alarmModelWheelView = new WheelView(getContext());
        alarmModelWheelView.setHorizontalPadding(60);
        alarmModelWheelView.setVerticalPadding(10);
        alarmModelWheelView.setTheTextSize(20);
        alarmModelWheelView.setItems(alarmModelList);
        alarmModelWheelView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                super.onSelected(selectedIndex, item);
                alarmModelId = selectedIndex - 1;
                handler.removeMessages(ALARM_MODEL_MESSAGE);
                handler.sendEmptyMessageDelayed(ALARM_MODEL_MESSAGE, DELAYED_TIME);
                alarmModelTextView.setText("报警开关：" + alarmModelList.get(selectedIndex - 1));
            }
        });
        alarmModelTextView.setText("报警开关：" + alarmModelList.get(alarmModelId));

        temperatureMeasureWheelView = new WheelView(getContext());
        temperatureMeasureWheelView.setHorizontalPadding(30);
        temperatureMeasureWheelView.setVerticalPadding(10);
        temperatureMeasureWheelView.setTheTextSize(20);
        temperatureMeasureWheelView.setItems(temperatureMeasureList);
        temperatureMeasureWheelView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                super.onSelected(selectedIndex, item);
                temperatureMeasureId = selectedIndex - 1;
                handler.removeMessages(TEMPERATURE_MEASURE_MESSAGE);
                handler.sendEmptyMessageDelayed(TEMPERATURE_MEASURE_MESSAGE, DELAYED_TIME);
                temperatureMeasureTextView.setText(temperatureMeasureList.get(selectedIndex - 1));
            }
        });
        temperatureMeasureTextView.setText(temperatureMeasureList.get(temperatureMeasureId));

        temperatureWheelView = new WheelView(getContext());
        temperatureWheelView.setHorizontalPadding(30);
        temperatureWheelView.setVerticalPadding(10);
        temperatureWheelView.setTheTextSize(20);
        temperatureWheelView.setItems(temperatureList);
        temperatureDownTextView.setText(temperatureList.get(temperatureDownId));
        temperatureUpTextView.setText(temperatureList.get(temperatureUpId));
        initListen();
    }

    private void initListen() {
        alarmModelTextView.setOnClickListener(this);
        temperatureDownTextView.setOnClickListener(this);
        temperatureUpTextView.setOnClickListener(this);
        temperatureMeasureTextView.setOnClickListener(this);
        volumSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.e(TAG, "onProgressChanged: ");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.e(TAG, "onStartTrackingTouch: ");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                spUtils.putInt(VOLUME_KEY, seekBar.getProgress());
                Log.e(TAG, "onStopTrackingTouch: " + seekBar.getProgress());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_alarm_switch_model:
                contentLinearLayout.removeAllViews();
                alarmModelWheelView.setSeletion(alarmModelId);
                contentLinearLayout.addView(alarmModelWheelView);
                break;
            case R.id.tv_down_temperature:
                contentLinearLayout.removeAllViews();
                temperatureWheelView.setSeletion(temperatureDownId);
                temperatureWheelView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
                    @Override
                    public void onSelected(int selectedIndex, String item) {
                        super.onSelected(selectedIndex, item);
                        temperatureDownId = selectedIndex - 1;
                        handler.removeMessages(TEMPERATURE_DOWN_MESSAGE);
                        handler.sendEmptyMessageDelayed(TEMPERATURE_DOWN_MESSAGE, DELAYED_TIME);
                        temperatureDownTextView.setText(temperatureList.get(selectedIndex - 1));
                    }
                });
                contentLinearLayout.addView(temperatureWheelView);
                break;
            case R.id.tv_up_temperature:
                contentLinearLayout.removeAllViews();
                temperatureWheelView.setSeletion(temperatureUpId);
                temperatureWheelView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
                    @Override
                    public void onSelected(int selectedIndex, String item) {
                        super.onSelected(selectedIndex, item);
                        temperatureUpId = selectedIndex - 1;
                        handler.removeMessages(TEMPERATURE_UP_MESSAGE);
                        handler.sendEmptyMessageDelayed(TEMPERATURE_UP_MESSAGE, DELAYED_TIME);
                        temperatureUpTextView.setText(temperatureList.get(selectedIndex - 1));
                    }
                });
                contentLinearLayout.addView(temperatureWheelView);
                break;
            case R.id.tv_temperature_unit_of_measure:
                contentLinearLayout.removeAllViews();
                temperatureMeasureWheelView.setSeletion(temperatureMeasureId);
                contentLinearLayout.addView(temperatureMeasureWheelView);
                break;
            default:
                break;
        }
    }
}
