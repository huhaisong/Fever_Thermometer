package com.sanyecao.hu.fever_thermometer.ui.medicine;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.sanyecao.hu.fever_thermometer.R;
import com.sanyecao.hu.fever_thermometer.mode.database.a.DatabaseController;
import com.sanyecao.hu.fever_thermometer.mode.database.bean.BabyBean;
import com.sanyecao.hu.fever_thermometer.mode.database.bean.CacheMedicineBean;
import com.sanyecao.hu.fever_thermometer.ui.base.ToolBarActivity;
import com.sanyecao.hu.fever_thermometer.utils.TimeUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * Created by huhaisong on 2017/8/16 15:46.
 * 药物提醒fragment
 */

public class MedicationRecordFragment extends BaseMedicineFragment {

    private static final String TAG = "MedicationRecordFragmen";

    @SuppressLint("StaticFieldLeak")
    private static MedicationRecordFragment mInstance; //单列
    private ToolBarActivity mActivity;
    private TextView timeTextView;
    private List<CacheMedicineBean> cacheMedicineBeenList;  //供选择的药物列表
    private ScrollView containScrollView;  //用来装载药物列表的scrollview
    private Spinner nameSpinner;  //用来供用户选择不同的用户
    private NameSpinnerAdapter nameSpinnerAdapter;
    private BabyBean mBabyBean;   //被选中的宝宝
    private List<BabyBean> babyBeanList;  //数据库里面所有的宝宝列表
    private HashSet<String> selectedMedicines = new HashSet<>();  //被选中的药物

    public static MedicationRecordFragment getInstance() {
        return mInstance == null ? mInstance = new MedicationRecordFragment() : mInstance;
    }

    //用来更新时间
    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (timeTextView != null)
                timeTextView.setText(TimeUtils.date2String(new Date()));
            handler.sendEmptyMessageDelayed(0, 60 * 1000);
            return false;
        }
    });

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mActivity = (ToolBarActivity) getActivity();
        View view = inflater.inflate(R.layout.fragment_medicine_record, container, false);
        initData(inflater);
        initView(view);
        return view;
    }

    private void initData(LayoutInflater inflater) {
        babyBeanList = DatabaseController.getmInstance().getAllBabyBean();
        nameSpinnerAdapter = new NameSpinnerAdapter(babyBeanList, mActivity, inflater);
    }

    private void initView(View view) {
        containScrollView = (ScrollView) view.findViewById(R.id.sv_contain);
        TextView addMedicineTextView = (TextView) view.findViewById(R.id.tv_add_other_medicine);
        addMedicineTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getContext(), MedicineItemActivity.class);
                getContext().startActivity(intent);
//                ((MedicineActivity) mActivity).startActivity(MedicineItemActivity.class);
            }
        });
        nameSpinner = (Spinner) view.findViewById(R.id.spinner_name);
        nameSpinner.setAdapter(nameSpinnerAdapter);
        nameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mBabyBean = babyBeanList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        timeTextView = (TextView) view.findViewById(R.id.tv_time);
        timeTextView.setText(TimeUtils.date2String(new Date()));
        handler.sendEmptyMessageDelayed(0, 60 * 1000);
    }


    //更新将被选择的药物列表
    private void updateCacheMedicineContainView() {
        cacheMedicineBeenList = DatabaseController.getmInstance().queryCacheMedicine();
        containScrollView.removeAllViews();
        LinearLayout linearLayout = new LinearLayout(mActivity);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        if (cacheMedicineBeenList.size() == 0)
            return;
        for (CacheMedicineBean item : cacheMedicineBeenList) {
            final CheckBox checkBox = new CheckBox(mActivity);
            checkBox.setPadding(10, 10, 10, 10);
            checkBox.setText(item.getMedicineName());
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked)
                        selectedMedicines.add(buttonView.getText().toString());
                    else
                        selectedMedicines.remove(buttonView.getText().toString());
                }
            });
            checkBox.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    dialog(v);
                    return false;
                }

                protected void dialog(View v) {

                    final Dialog dialog = new Dialog(mActivity);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    LinearLayout linearLayout = new LinearLayout(mActivity);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    linearLayout.setLayoutParams(layoutParams);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    linearLayout.setGravity(Gravity.CENTER);
                    TextView textView = new TextView(mActivity);
                    textView.setText("删除 " + ((CompoundButton) v).getText());
                    textView.setPadding(20, 20, 20, 20);
                    textView.setGravity(Gravity.CENTER);
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.e(TAG, "onItemSelected: ");
                            DatabaseController.getmInstance().deleteCacheMedicineByName(checkBox.getText().toString());
                            updateCacheMedicineContainView();
                            dialog.dismiss();
                        }
                    });
                    linearLayout.addView(textView);
                    dialog.setContentView(linearLayout);
                    Window dialogWindow = dialog.getWindow();
                    WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                    dialogWindow.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                    dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
                    lp.dimAmount = 0f;
                    int[] location = new int[2];
                    v.getLocationInWindow(location);
                    lp.x = location[0] + v.getWidth();
                    lp.y = location[1];
                    dialogWindow.setAttributes(lp);
                    dialog.show();
                }
            });
            linearLayout.addView(checkBox);
            View view = new View(mActivity);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
            view.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.black));
            linearLayout.addView(view);
        }
        containScrollView.addView(linearLayout);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateView();
    }

    //更新这个fragment的界面
    private void updateView() {
        babyBeanList = DatabaseController.getmInstance().getAllBabyBean();
        nameSpinnerAdapter.setItems(babyBeanList);
        if (mBabyBean != null)
            for (int i = 0; i < babyBeanList.size(); i++) {
                if (mBabyBean.equals(babyBeanList.get(i))) {
                    nameSpinner.setSelection(i);
                    break;
                }
            }
        updateCacheMedicineContainView();
    }

    //这个fragment需要更新的toolbar
    public void initToolbar() {
        mActivity.setBarTextView("确定");
        mActivity.setToolBarTitle("添加服药记录");
        mActivity.setBarTextViewListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: ");
                for (String item : selectedMedicines) {
                    Log.e(TAG, "onClick: " + item);
                }
                Log.e(TAG, "onClick: " + selectedMedicines.toString());
            }
        });
    }

    public void setDefaultBabyBean(String babyName) {
        mBabyBean = DatabaseController.getmInstance().queryBabyBeanByName(babyName);
    }
}
