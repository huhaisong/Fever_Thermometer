package com.sanyecao.hu.fever_thermometer.ui.medicine_recode;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.sanyecao.hu.fever_thermometer.R;
import com.sanyecao.hu.fever_thermometer.mode.database.a.DatabaseController;
import com.sanyecao.hu.fever_thermometer.mode.database.bean.CacheMedicineBean;
import com.sanyecao.hu.fever_thermometer.ui.base.ActivityStack;
import com.sanyecao.hu.fever_thermometer.ui.base.ToolBarActivity;

/**
 * Created by huhaisong n 2017/8/31 15:50.
 */

public class MedicineItemActivity extends ToolBarActivity {
    private static final String TAG = "MedicineItemActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_item_layout);
        Log.e(TAG, "onCreate: " );
        initView();
        initListener();
    }


    @Override
    public void initData() {

    }

    private EditText editText;

    @Override
    public void initView() {
        editText = (EditText) findViewById(R.id.et_other_medicine);
        setBarTextView("确定");
        setToolBarTitle("其他药物");
    }

    @Override
    public void initListener() {
        setBarTextViewListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CacheMedicineBean cacheMedicineBean = new CacheMedicineBean();
                cacheMedicineBean.setMedicineName(editText.getText().toString());
                DatabaseController.getmInstance().addCachMedicine(cacheMedicineBean);
                ActivityStack.getInstance().popAndFinish();
            }
        });
    }
}
