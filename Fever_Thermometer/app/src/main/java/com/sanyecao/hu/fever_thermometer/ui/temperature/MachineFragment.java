package com.sanyecao.hu.fever_thermometer.ui.temperature;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sanyecao.hu.fever_thermometer.R;
import com.sanyecao.hu.fever_thermometer.mode.database.a.DatabaseController;
import com.sanyecao.hu.fever_thermometer.mode.database.bean.BabyBean;
import com.sanyecao.hu.fever_thermometer.mode.database.bean.TemperatureRecodeBean;
import com.sanyecao.hu.fever_thermometer.ui.dialog.InformationDialog;
import com.sanyecao.hu.fever_thermometer.ui.dialog.NameEditDialog;
import com.sanyecao.hu.fever_thermometer.ui.temperature.alarm.AlarmActivity;
import com.sanyecao.hu.fever_thermometer.ui.temperature.medicine.MedicineActivity;
import com.sanyecao.hu.fever_thermometer.ui.widget.CircleView;
import com.sanyecao.hu.fever_thermometer.ui.widget.GlideCircleTransform;
import com.sanyecao.hu.fever_thermometer.utils.TimeUtils;

import static android.app.Activity.RESULT_OK;
import static com.sanyecao.hu.fever_thermometer.ui.temperature.TemperatureFragment.myBinder;

/**
 * Created by huhaisong on 2017/8/15 16:16.
 * 蓝牙温度计设备碎片
 */

public class MachineFragment extends Fragment {

    public static final int MESSAGE_UPDATE_TEMPERATURE = 3;
    public static final int MESSAGE_UPDATE_BATTERY_LEVEL = 2;
    public static final int MESSAGE_UPDATE_CONNECTED_STATE = 1;
    public static final int MESSAGE_CONNECT_OUT_TIME = 4;
    private ImageView babyImageView;
    private TextView informationTextView;
    private TextView babyTextView;
    private ImageView alarmImageView;
    private ImageView medicineRecodeImageView;
    private BabyBean mBabyBean;
    private TemperatureFragment temperatureFragment;
    private View view;

    public MachineFragment(TemperatureFragment temperatureFragment, BabyBean babyBean) {
        this.temperatureFragment = temperatureFragment;
        this.mBabyBean = babyBean;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_temperature_machine, container, false);
        initView(view);
        initListen();
        initData();
        return view;
    }

    private void initData() {
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        getActivity().registerReceiver(myBroadcastReceiver, filter);
    }

    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_ON) {
                informationTextView.setVisibility(View.GONE);
            } else if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_OFF) {
                informationTextView.setVisibility(View.VISIBLE);
            }
        }
    };

    private CircleView machineCircleView;
    private static final String TAG = "MachineFragment";
    private DatabaseController mDatabaseController;

    private void initView(View view) {
        machineCircleView = (CircleView) view.findViewById(R.id.circle_view_machine);
        machineCircleView.setClickable(true);
        informationTextView = (TextView) view.findViewById(R.id.tv_information);
        informationTextView.setVisibility(View.GONE);
        if (myBinder != null) {
            if (!myBinder.getEnableState())
                informationTextView.setVisibility(View.VISIBLE);
        }
        alarmImageView = (ImageView) view.findViewById(R.id.iv_alarm);
        medicineRecodeImageView = (ImageView) view.findViewById(R.id.iv_medicine_recode);
        babyImageView = (ImageView) view.findViewById(R.id.iv_baby);
        babyTextView = (TextView) view.findViewById(R.id.tv_baby_name);
        mDatabaseController = DatabaseController.getmInstance();

        if (mBabyBean != null)
            babyTextView.setText(mBabyBean.getName());
        else {
            babyTextView.setText("Baby");
            mBabyBean = new BabyBean();
            mBabyBean.setName("Baby");
            mDatabaseController.addBabyBean(mBabyBean);
            mBabyBean = mDatabaseController.queryBabyBeanByName("Baby");
        }

        if (mBabyBean.getImage_url() != null)
            Glide.with(getContext())
                    .load(mBabyBean.getImage_url())
                    .error(R.drawable.header)
                    .transform(new GlideCircleTransform(getContext()))
                    .into(babyImageView);
    }

    private void initListen() {
        babyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NameEditDialog(getContext(), mBabyBean == null ? "Baby" : mBabyBean.getName(), new NameEditDialog.OnDialogListen() {
                    @Override
                    public void onEnsure(String name) {
                        mBabyBean.setName(name);
                        mDatabaseController.updateBabyBean(mBabyBean);
                        babyTextView.setText(name);
                    }

                    @Override
                    public void onCancel() {
                    }
                }).show();
            }
        });

        babyImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, Activity.RESULT_FIRST_USER);
            }
        });

        machineCircleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myBinder != null) {
                    if (isConnected) {
                        machineCircleView.setContent("Unconnected");
                        machineCircleView.setCircleColor(ContextCompat.getColor(getContext(), R.color.blue));
                        myBinder.closeBluetoothService();
                        temperatureFragment.viewPager.setScrollble(true);
                        isConnected = false;
                    } else {
                        Log.e(TAG, "onClick: myBinder ！= null");
                        handler.sendEmptyMessageDelayed(MESSAGE_CONNECT_OUT_TIME, 30 * 1000);
                        machineCircleView.setCircleColor(ContextCompat.getColor(getContext(), R.color.blue));
                        machineCircleView.setContent("Connecting");
                        myBinder.connectMachine(handler);
                    }
                }
            }
        });

        medicineRecodeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMedicineRecode();
            }
        });

        alarmImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAlarm();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult:resultCode = " + resultCode + ",requestCode = " + requestCode);
        if (requestCode == Activity.RESULT_FIRST_USER && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            mBabyBean.setImage_url(selectedImage.toString());
            mDatabaseController.updateBabyBean(mBabyBean);
            Glide.with(getContext())
                    .load(selectedImage)
                    .error(R.drawable.header)
                    .transform(new GlideCircleTransform(getContext()))
                    .into(babyImageView);
        }
    }

    private boolean isConnected = false;

    public boolean isConnected() {
        return isConnected;
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_UPDATE_BATTERY_LEVEL:
                    int battery = msg.arg1;
                    break;
                case MESSAGE_UPDATE_CONNECTED_STATE:
                    machineCircleView.setContent("connected");
                    break;
                case MESSAGE_UPDATE_TEMPERATURE:
                    handler.removeMessages(MESSAGE_CONNECT_OUT_TIME);
                    isConnected = true;
                    Bundle bundle = msg.getData();
                    double temperature = bundle.getDouble("temperature");
                    temperatureFragment.viewPager.setScrollble(false);
                    updateTemperature((float) temperature);
                    break;
                case MESSAGE_CONNECT_OUT_TIME:
                    machineCircleView.setContent("Unconnected");
                    myBinder.stopServiceScan();
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    private void updateTemperature(float temperature) {
        int r, b;
        int g = 30;
        float sub = temperature - 35;
        if (sub < 1.5) {
            r = 0;
            b = 255;
        } else if (sub > 3) {
            r = 255;
            b = 0;
            highTemperatureDialog();
        } else {
            r = (int) (85 * sub);
            b = 30;
        }
        machineCircleView.setContent(temperature + " ℃");
        machineCircleView.setCircleColor(Color.rgb(r, g, b));
        TemperatureRecodeBean temperatureRecodeBean = new TemperatureRecodeBean();
        temperatureRecodeBean.setBabyId(Integer.valueOf(mBabyBean.getId() + ""));
        temperatureRecodeBean.setTime(TimeUtils.getCurTimeString());
        temperatureRecodeBean.setTemperature(temperature);
        DatabaseController.getmInstance().addTemperatureRecode(temperatureRecodeBean);
    }

    private void highTemperatureDialog() {
        InformationDialog informationDialog = new InformationDialog(getContext(), "High Temperature!");
        informationDialog.show();
    }

    public void goToMedicineRecode() {
        Intent intent = new Intent(getContext(), MedicineActivity.class);
        intent.putExtra("babyName", mBabyBean.getName());
        startActivity(intent);
    }

    public void goToAlarm() {
        Intent intent = new Intent(getContext(), AlarmActivity.class);
        startActivity(intent);
    }
}
