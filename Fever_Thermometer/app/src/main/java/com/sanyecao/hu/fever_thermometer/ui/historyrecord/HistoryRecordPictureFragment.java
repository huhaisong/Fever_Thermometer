package com.sanyecao.hu.fever_thermometer.ui.historyrecord;

import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.sanyecao.hu.fever_thermometer.R;
import com.sanyecao.hu.fever_thermometer.mode.database.bean.BabyBean;
import com.sanyecao.hu.fever_thermometer.ui.adapter.NameSpinnerAdapter;
import com.sanyecao.hu.fever_thermometer.ui.base.MainActivity;
import com.sanyecao.hu.fever_thermometer.ui.widget.FeverTemperatureGridView;
import com.sanyecao.hu.fever_thermometer.utils.TimeUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import static com.sanyecao.hu.fever_thermometer.mode.database.a.DatabaseController.getmInstance;

/**
 * Created by huhaisong on 2017/8/16 17:20.
 * 用来显示并保存一个体温表图片
 */

public class HistoryRecordPictureFragment extends Fragment {
    private FeverTemperatureGridView temperatureGridView;
    private TextView timeTextView;
    private Spinner babySpinner;
    private NameSpinnerAdapter babySpinnerAdapter;
    private BabyBean mBabyBean;   //被选中的宝宝
    private List<BabyBean> babyBeanList;  //数据库里面所有的宝宝列表
    private String time;
    View view;
    private static HistoryRecordPictureFragment mInstance;

    public static HistoryRecordPictureFragment getInstance() {
        return mInstance == null ? mInstance = new HistoryRecordPictureFragment() : mInstance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_history_record_picture_layout, container, false);
        initView(view, inflater);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateView();
        ((MainActivity) getActivity()).setMainToolBarTextViewListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.setDrawingCacheEnabled(true);
                view.buildDrawingCache();
                Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
//使用Canvas，调用自定义view控件的onDraw方法，绘制图片
                Canvas canvas = new Canvas(bitmap);
                canvas.drawColor(ContextCompat.getColor(getContext(), R.color.white));
                view.draw(canvas);
                if (bitmap != null) {
                    try {
                        // 获取内置SD卡路径
                        String sdCardPath = Environment.getExternalStorageDirectory().getPath();
                        // 图片文件路径
                        if (mBabyBean != null) {
                            String filePath = sdCardPath + File.separator + time + "_" + mBabyBean.getName() + ".png";
                            File file = new File(filePath);
                            FileOutputStream os = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                            os.flush();
                            os.close();
                            Log.e(TAG, "onClick: " + filePath);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void showSucessDialog() {

    }

    private void showErrorDialog() {

    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) getActivity()).setMainToolBarTextViewListener(null);
    }

    private static final String TAG = "HistoryRecordPictureFra";

    private void initView(View view, LayoutInflater inflater) {
        babySpinner = (Spinner) view.findViewById(R.id.spinner_baby);
        timeTextView = (TextView) view.findViewById(R.id.tv_time);
        temperatureGridView = (FeverTemperatureGridView) view.findViewById(R.id.gv_fever_temperature);
        temperatureGridView.setFocusableInTouchMode(false);
        babyBeanList = getmInstance().queryAllBabyBean();
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
    }

    private void updateView() {
        babyBeanList = getmInstance().queryAllBabyBean();
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
}
