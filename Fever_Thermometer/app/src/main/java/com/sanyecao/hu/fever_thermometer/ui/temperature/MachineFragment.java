package com.sanyecao.hu.fever_thermometer.ui.temperature;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
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
import com.sanyecao.hu.fever_thermometer.mode.database.bean.BabyBean;
import com.sanyecao.hu.fever_thermometer.mode.database.a.DatabaseController;
import com.sanyecao.hu.fever_thermometer.service.BluetoothService;
import com.sanyecao.hu.fever_thermometer.ui.dialog.NameEditDialog;
import com.sanyecao.hu.fever_thermometer.ui.view.CircleView;
import com.sanyecao.hu.fever_thermometer.ui.view.GlideCircleTransform;

import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Created by huhaisong on 2017/8/15 16:16.
 */

public class MachineFragment extends Fragment {

    private ImageView babyImageView;
    private TextView informationTextView;
    private TextView babyTextView;
    private int machineId;
    private BabyBean mBabyBean;
    private static final int RESULT_ON = 1;
    View view;

    public MachineFragment(int machineId) {
        this.machineId = machineId;
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

        Intent bindIntent = new Intent(getContext(), BluetoothService.class);
        getActivity().bindService(bindIntent, connection, BIND_AUTO_CREATE);
    }

    private CircleView machineCircleView;
    private static final String TAG = "MachineFragment";
    private DatabaseController mDatabaseController;

    private void initView(View view) {
        machineCircleView = (CircleView) view.findViewById(R.id.circle_view_machine);
        informationTextView = (TextView) view.findViewById(R.id.tv_information);
        machineCircleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                machineCircleView.setContent("Connecting");
            }
        });
        babyImageView = (ImageView) view.findViewById(R.id.iv_baby);
        babyTextView = (TextView) view.findViewById(R.id.tv_baby_name);
        mDatabaseController =  DatabaseController.getmInstance();
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

    private BluetoothService.BleBinder myBinder;

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder = (BluetoothService.BleBinder) service;
        }
    };

    public BabyBean getBabyBean() {
        return mBabyBean;
    }
}
