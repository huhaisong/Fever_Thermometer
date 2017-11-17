package com.sanyecao.hu.fever_thermometer.ui.temperature;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sanyecao.hu.fever_thermometer.R;
import com.sanyecao.hu.fever_thermometer.ui.widget.GlideCircleTransform;

/**
 * Created by huhaisong on 2017/11/14 10:59.
 * 这个界面用于增加baby界面
 */

public class AddMachineFragment extends Fragment {

    public AddMachineFragment(TemperatureFragment temperatureFragment) {
        this.temperatureFragment = temperatureFragment;
    }

    private TemperatureFragment temperatureFragment;
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_temperature_add_machine, container, false);
        init();
        return view;
    }

    private ImageView imageView;

    private void init() {
        imageView = (ImageView) view.findViewById(R.id.image);


        Glide.with(getContext())
                .load(R.drawable.header)
                .transform(new GlideCircleTransform(getContext()))
                .into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temperatureFragment.addBabyAndUpdate();
            }
        });
    }
}
