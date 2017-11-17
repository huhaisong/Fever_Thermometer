package com.sanyecao.hu.fever_thermometer.ui.historyrecord;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.sanyecao.hu.fever_thermometer.R;
import com.sanyecao.hu.fever_thermometer.mode.database.a.DatabaseController;
import com.sanyecao.hu.fever_thermometer.mode.database.bean.BabyBean;
import com.sanyecao.hu.fever_thermometer.mode.database.bean.MedicineRecodeBean;
import com.sanyecao.hu.fever_thermometer.mode.database.bean.RecodeNoteBean;
import com.sanyecao.hu.fever_thermometer.ui.adapter.HistoryNoteRecodeAdapter;
import com.sanyecao.hu.fever_thermometer.ui.adapter.NameSpinnerAdapter;
import com.sanyecao.hu.fever_thermometer.ui.dialog.PictureDialog;
import com.sanyecao.hu.fever_thermometer.ui.widget.FeverTemperatureGridView;
import com.sanyecao.hu.fever_thermometer.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

import static com.sanyecao.hu.fever_thermometer.mode.database.a.DatabaseController.getmInstance;

/**
 * Created by huhaisong on 2017/8/16 17:19.
 * 历史记录界面包括：温度记录，note记录，服药记录
 */

public class HistoryRecordDetailFragment extends Fragment {

    private static HistoryRecordDetailFragment mInstance;
    private View view;
    private Spinner babySpinner;
    private FeverTemperatureGridView temperatureGridView;
    private TextView timeTextView;

    private NameSpinnerAdapter babySpinnerAdapter;
    private BabyBean mBabyBean;   //被选中的宝宝
    private List<BabyBean> babyBeanList;  //数据库里面所有的宝宝列表
    private String time;
    private HistoryNoteRecodeAdapter historyNoteRecodeAdapter;

    public synchronized static HistoryRecordDetailFragment getInstance() {
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
        ListView noteListView = (ListView) view.findViewById(R.id.listView_note);
        babySpinner = (Spinner) view.findViewById(R.id.spinner_baby);
        timeTextView = (TextView) view.findViewById(R.id.tv_time);
        temperatureGridView = (FeverTemperatureGridView) view.findViewById(R.id.gv_fever_temperature);
        temperatureGridView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (System.currentTimeMillis() - start > 1000) {
                    showPicturePop();
                    start = System.currentTimeMillis();
                }
            }
        });
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

        historyNoteRecodeAdapter = new HistoryNoteRecodeAdapter(updateNoteListData(), getContext(), inflater);
        noteListView.setAdapter(historyNoteRecodeAdapter);
    }

    private List<String> updateNoteListData() {
        List<String> noteListDatas = new ArrayList<>();
        String noteListDatasContent;
        List<RecodeNoteBean> recodeNoteBeanList = DatabaseController.getmInstance().queryAllRecodeNoteBean();
        RecodeNoteBean recodeNoteBean;
        List<MedicineRecodeBean> medicineRecodeBeanList = DatabaseController.getmInstance().queryAllMedicineRecodeBean();
        MedicineRecodeBean medicineRecodeBean;
        int aSize = recodeNoteBeanList.size();
        int bSize = medicineRecodeBeanList.size();
        if (aSize == 0 || bSize == 0)
            return noteListDatas;
        int a = 0, b = 0, k = 0;
        for (; k < aSize + bSize; k++) {
            medicineRecodeBean = medicineRecodeBeanList.get(b);
            recodeNoteBean = recodeNoteBeanList.get(a);
            if (TimeUtils.string2Milliseconds(recodeNoteBean.getTime()) < TimeUtils.string2Milliseconds(medicineRecodeBean.getDate())) {
                noteListDatasContent = recodeNoteBean.getTime() + "\n" + "Note:" + "\n" + recodeNoteBean.getContent();
                noteListDatas.add(noteListDatasContent);
                a++;
            } else {
                noteListDatasContent = medicineRecodeBean.getDate() + "\n" + DatabaseController.getmInstance().queryBabyBeanById(medicineRecodeBean.getBabyId()).getName() + "服药:" + "\n" + medicineRecodeBean.getMedicines();
                noteListDatas.add(noteListDatasContent);
                b++;
            }
            if (a == aSize) {
                for (; b < bSize; b++) {
                    medicineRecodeBean = medicineRecodeBeanList.get(b);
                    noteListDatasContent = medicineRecodeBean.getDate() + "\n" + DatabaseController.getmInstance().queryBabyBeanById(medicineRecodeBean.getBabyId()).getName() + "服药:" + "\n" + medicineRecodeBean.getMedicines();
                    noteListDatas.add(noteListDatasContent);
                }
                return noteListDatas;
            }
            if (b == bSize) {
                for (; a < aSize; a++) {
                    recodeNoteBean = recodeNoteBeanList.get(a);
                    noteListDatasContent = recodeNoteBean.getTime() + "\n" + "Note:" + "\n" + recodeNoteBean.getContent();
                    noteListDatas.add(noteListDatasContent);
                }
                return noteListDatas;
            }
        }
        return noteListDatas;
    }

    private void updateView() {
        babyBeanList = getmInstance().queryAllBabyBean();
        historyNoteRecodeAdapter.setItems(updateNoteListData());
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
        mBabyBean = getmInstance().queryBabyBeanByName(babyName);
    }

    private void showPictureDialog() {
        PictureDialog pictureDialog;
        if (mBabyBean != null) {
            pictureDialog = new PictureDialog(getContext(), time, mBabyBean.getId());
        } else {
            pictureDialog = new PictureDialog(getContext(), time, 0L);
        }
        pictureDialog.show();
    }

    private long start = 0;

    private void showPicturePop() {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_picture, null);
        final PopupWindow picturePop = new PopupWindow(contentView, LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, true);
        FeverTemperatureGridView feverTemperatureGridView = (FeverTemperatureGridView) contentView.findViewById(R.id.gv_fever_temperature_dialog);
        if (mBabyBean != null) {
            feverTemperatureGridView.setDataAndBaby(time, mBabyBean.getId());
        } else {
            feverTemperatureGridView.setDataAndBaby(time, 0L);
        }
        feverTemperatureGridView.setTouchAble(true);
        feverTemperatureGridView.setDegree(90);
        feverTemperatureGridView.setOnDoubleClick(new FeverTemperatureGridView.OnDoubleClick() {
            @Override
            public void onDoubleClick() {
                picturePop.dismiss();
            }
        });
        picturePop.setTouchable(true);
        picturePop.setFocusable(true);
        picturePop.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.color.black));
        picturePop.showAsDropDown(view.findViewById(R.id.LinearLayout));
    }
}
