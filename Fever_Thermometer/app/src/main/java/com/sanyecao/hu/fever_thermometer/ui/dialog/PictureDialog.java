package com.sanyecao.hu.fever_thermometer.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.sanyecao.hu.fever_thermometer.R;
import com.sanyecao.hu.fever_thermometer.ui.widget.FeverTemperatureGridView;

/**
 * Created by huhaisong on 2017/11/10 15:21.
 */

public class PictureDialog extends Dialog {

    private String time;
    private Long babyId;
    private Context mContext;

    public PictureDialog(Context context, String time, Long babyId) {
        super(context);
        this.mContext = context;
        this.time = time;
        this.babyId = babyId;
    }

    public PictureDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(time, babyId);
    }

    private static final String TAG = "PictureDialog";

    private void init(String time, Long babyId) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        final View view = inflater.inflate(R.layout.dialog_picture, null);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //去除title
        setCancelable(true); //设置点击白色不消失
        setContentView(view);
        Log.e(TAG, "init: ");
//        WindowManager.LayoutParams params = getWindow().getAttributes();
//        getWindow().setAttributes(params);

        final FeverTemperatureGridView feverTemperatureGridView = (FeverTemperatureGridView) view.findViewById(R.id.gv_fever_temperature_dialog);

        feverTemperatureGridView.setDataAndBaby(time, babyId);

       /* feverTemperatureGridView.setOnTouchListener(new View.OnTouchListener() {

        });*/
    }
}
