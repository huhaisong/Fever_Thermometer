package com.sanyecao.hu.fever_thermometer.ui.temperature.medicine;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
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
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.sanyecao.hu.fever_thermometer.R;
import com.sanyecao.hu.fever_thermometer.mode.database.a.DatabaseController;
import com.sanyecao.hu.fever_thermometer.mode.database.bean.BabyBean;
import com.sanyecao.hu.fever_thermometer.mode.database.bean.CacheMedicineBean;
import com.sanyecao.hu.fever_thermometer.mode.database.bean.MedicineRecodeBean;
import com.sanyecao.hu.fever_thermometer.ui.base.BaseTollBarFragment;
import com.sanyecao.hu.fever_thermometer.ui.adapter.NameSpinnerAdapter;
import com.sanyecao.hu.fever_thermometer.ui.base.ToolBarActivity;
import com.sanyecao.hu.fever_thermometer.ui.dialog.InformationDialog;
import com.sanyecao.hu.fever_thermometer.utils.StringUtils;
import com.sanyecao.hu.fever_thermometer.utils.TimeUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * Created by huhaisong on 2017/8/16 15:46.
 * 药物提醒fragment
 */

public class MedicationRecordFragment extends BaseTollBarFragment {

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
    private String time;
    private String[] medicines = new String[]{"同仁堂的小儿感冒颗粒", "儿研所的牛磺酸颗粒"
            , "王氏保赤丸", "同仁堂小儿清肺口服液", "神威药业小儿清肺化痰颗粒", "小儿宝泰康", "思密达", "小儿咽扁冲剂"};

    public static MedicationRecordFragment getInstance() {
        return mInstance = new MedicationRecordFragment();
    }

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
        babyBeanList = DatabaseController.getmInstance().queryAllBabyBean();
        nameSpinnerAdapter = new NameSpinnerAdapter(babyBeanList, mActivity, inflater);
        time = TimeUtils.getCurTimeString();
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
        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Log.e(TAG, "onClick: ");
                timeTextView.setClickable(false);
                Calendar c1 = Calendar.getInstance();
                Dialog dateDialog = new MyDatePicker(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Log.e(TAG, "onDateSet: ");
                        time = TimeUtils.getTimeString(year, month, dayOfMonth);
                        Calendar c = Calendar.getInstance();
                        Dialog timeDialog = new MyTimePicker(getContext(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                time = TimeUtils.getTimeString(time, hourOfDay, minute);
                                timeTextView.setText(time);
                            }
                        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);
                        timeDialog.setCanceledOnTouchOutside(false);
                        timeDialog.show();
                    }
                }, c1.get(Calendar.YEAR), c1.get(Calendar.MONTH), c1.get(Calendar.DAY_OF_MONTH));
                dateDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        timeTextView.setClickable(true);
                    }
                });
                dateDialog.setCanceledOnTouchOutside(false);
                dateDialog.show();
            }
        });
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
        if (cacheMedicineBeenList.size() == 0) {
            for (String item : medicines) {
                CacheMedicineBean cacheMedicineBean = new CacheMedicineBean();
                cacheMedicineBean.setMedicineName(item);
                DatabaseController.getmInstance().addCachMedicine(cacheMedicineBean);
                cacheMedicineBeenList.add(cacheMedicineBean);
            }
        }
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
        babyBeanList = DatabaseController.getmInstance().queryAllBabyBean();
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
        mActivity.initBar();
        mActivity.setBarTextView("确定");
        mActivity.setToolBarTitle("添加服药记录");
        mActivity.setBarTextViewListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedMedicines.size() != 0)
                    showMessageDialog("操作成功", new InformationDialog.OnEnsureListen() {
                        @Override
                        public void onEnsureListen() {
                            MedicineRecodeBean medicineRecodeBean = new MedicineRecodeBean();
                            medicineRecodeBean.setBabyId(Long.valueOf(mBabyBean.getId()).intValue());
                            medicineRecodeBean.setDate(time);
                            medicineRecodeBean.setMedicines(StringUtils.medicinesHashSetToString(selectedMedicines));
                            DatabaseController.getmInstance().addMedicineRecodeBean(medicineRecodeBean);
                            for (String item : selectedMedicines) {
                                Log.e(TAG, "onClick: " + item);
                            }
                            selectedMedicines.clear();
                            updateCacheMedicineContainView();
                        }
                    });
                else
                    showMessageDialog("您没有选择任何药物", null);

            }
        });
    }

    public void setDefaultBabyBean(String babyName) {
        mBabyBean = DatabaseController.getmInstance().queryBabyBeanByName(babyName);
    }

    private class MyDatePicker extends DatePickerDialog {

        public MyDatePicker(@NonNull Context context) {
            super(context);
        }

        public MyDatePicker(@NonNull Context context, @StyleRes int themeResId) {
            super(context, themeResId);
        }

        public MyDatePicker(@NonNull Context context, @Nullable OnDateSetListener listener, int year, int month, int dayOfMonth) {
            super(context, listener, year, month, dayOfMonth);
        }

        public MyDatePicker(@NonNull Context context, @StyleRes int themeResId, @Nullable OnDateSetListener listener, int year, int monthOfYear, int dayOfMonth) {
            super(context, themeResId, listener, year, monthOfYear, dayOfMonth);
        }

        @Override
        protected void onStop() {

        }
    }

    private class MyTimePicker extends TimePickerDialog {

        public MyTimePicker(Context context, OnTimeSetListener listener, int hourOfDay, int minute, boolean is24HourView) {
            super(context, listener, hourOfDay, minute, is24HourView);
        }

        public MyTimePicker(Context context, int themeResId, OnTimeSetListener listener, int hourOfDay, int minute, boolean is24HourView) {
            super(context, themeResId, listener, hourOfDay, minute, is24HourView);
        }

        @Override
        protected void onStop() {

        }
    }

    private void showMessageDialog(String string, InformationDialog.OnEnsureListen onEnsureListen) {
        InformationDialog informationDialog = new InformationDialog(getContext(), string);
        informationDialog.setOnEnsureListen(onEnsureListen);
        informationDialog.setCanceledOnTouchOutside(false);
        informationDialog.show();
    }
}
