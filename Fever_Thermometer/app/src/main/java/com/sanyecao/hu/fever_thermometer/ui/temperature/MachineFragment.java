package com.sanyecao.hu.fever_thermometer.ui.temperature;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.sanyecao.hu.fever_thermometer.ui.alarm.AlarmActivity;
import com.sanyecao.hu.fever_thermometer.ui.dialog.NameEditDialog;
import com.sanyecao.hu.fever_thermometer.ui.medicine_recode.MedicineActivity;
import com.sanyecao.hu.fever_thermometer.ui.view.CircleView;
import com.sanyecao.hu.fever_thermometer.ui.view.GlideCircleTransform;

import java.util.List;

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
    public static final int MESSAGE_CONNECTE_OUT_TIME = 4;
    private ImageView babyImageView;
    private TextView informationTextView;
    private TextView babyTextView;
    private ImageView alarmImageView;
    private ImageView medicineRecodeImageView;
    private int machineId;
    private BabyBean mBabyBean;
    private TemperatureFragment temperatureFragment;
    View view;

    public MachineFragment(int machineId, TemperatureFragment temperatureFragment) {
        this.machineId = machineId;
        this.temperatureFragment = temperatureFragment;
    }

    public void setMachineId(int machineId) {
        this.machineId = machineId;
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

    }

    private CircleView machineCircleView;
    private static final String TAG = "MachineFragment";
    private DatabaseController mDatabaseController;

    private void initView(View view) {
        machineCircleView = (CircleView) view.findViewById(R.id.circle_view_machine);
        machineCircleView.setClickable(true);
        informationTextView = (TextView) view.findViewById(R.id.tv_information);
        alarmImageView = (ImageView) view.findViewById(R.id.iv_alarm);
        medicineRecodeImageView = (ImageView) view.findViewById(R.id.iv_medicine_recode);
        babyImageView = (ImageView) view.findViewById(R.id.iv_baby);
        babyTextView = (TextView) view.findViewById(R.id.tv_baby_name);
        mDatabaseController = DatabaseController.getmInstance();
        List<BabyBean> babyBeanList = mDatabaseController.queryBabyBeanByMachineId(machineId);

        if (babyBeanList != null && babyBeanList.size() > 0) {
            mBabyBean = babyBeanList.get(0);
        }

        if (mBabyBean != null)
            babyTextView.setText(mBabyBean.getName());
        else {
            babyTextView.setText("Baby");
            mBabyBean = new BabyBean();
            mBabyBean.setMachineId(machineId);
            mBabyBean.setName("Baby");
            mDatabaseController.addBabyBean(mBabyBean);
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
                        myBinder.closeBluetoothService();
                        temperatureFragment.viewPager.setScrollble(true);
                        isConnected = false;
                    } else {
                        Log.e(TAG, "onClick: myBinder ！= null");
                        handler.sendEmptyMessageDelayed(MESSAGE_CONNECTE_OUT_TIME, 30 * 1000);
                        machineCircleView.setContent("Connecting");
                        myBinder.connectMachine(handler, machineId);
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
                    handler.removeMessages(MESSAGE_CONNECTE_OUT_TIME);
                    Bundle bundle = msg.getData();
                    double temperature = bundle.getDouble("temperature");
                    machineCircleView.setContent(temperature + " ℃");
                    isConnected = true;
                    temperatureFragment.viewPager.setScrollble(false);
                    break;
                case MESSAGE_CONNECTE_OUT_TIME:
                    machineCircleView.setContent("Unconnected");
                    myBinder.stopServiceScan();
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    private void updateTemperature() {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
