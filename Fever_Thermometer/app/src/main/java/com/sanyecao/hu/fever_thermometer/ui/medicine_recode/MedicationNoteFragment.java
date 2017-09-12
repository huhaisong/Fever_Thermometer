package com.sanyecao.hu.fever_thermometer.ui.medicine_recode;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.sanyecao.hu.fever_thermometer.R;
import com.sanyecao.hu.fever_thermometer.mode.database.a.DatabaseController;
import com.sanyecao.hu.fever_thermometer.mode.database.bean.RecodeNoteBean;
import com.sanyecao.hu.fever_thermometer.ui.base.ToolBarActivity;
import com.sanyecao.hu.fever_thermometer.utils.TimeUtils;

import java.util.Date;

/**
 * Created by huhaisong on 2017/8/16 15:47.
 */

public class MedicationNoteFragment extends BaseMedicineFragment {
    View view;
    private static MedicationNoteFragment mInstance;
    private ToolBarActivity mActivity;
    private TextView timeTextView;
    private EditText noteEditText;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            timeTextView.setText(TimeUtils.date2String(new Date()));
            handler.sendEmptyMessageDelayed(0, 60 * 1000);
            return false;
        }
    });

    public static MedicationNoteFragment getInstance() {
        return mInstance == null ? mInstance = new MedicationNoteFragment() : mInstance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mActivity = (ToolBarActivity) getActivity();
        view = inflater.inflate(R.layout.fragment_medicine_note, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        timeTextView = (TextView) view.findViewById(R.id.tv_time);
        noteEditText = (EditText) view.findViewById(R.id.et_note);
        timeTextView.setText(TimeUtils.date2String(new Date()));
        handler.sendEmptyMessageDelayed(0, 60 * 1000);
    }

    public void initToolbar() {
        mActivity.setBarTextView("确定");
        mActivity.setToolBarTitle("添加事件记录");
        mActivity.setBarTextViewListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: ");
                DatabaseController databaseController =  DatabaseController.getmInstance();
                RecodeNoteBean recodeNoteBean = new RecodeNoteBean();
                recodeNoteBean.setContent(noteEditText.getText().toString());
                recodeNoteBean.setTime(timeTextView.getText().toString());
                databaseController.addRecodeNoteBean(recodeNoteBean);
                mActivity.finish();
            }
        });
    }

    private static final String TAG = "MedicationNoteFragment";
}
