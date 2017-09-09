package com.sanyecao.hu.fever_thermometer.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.sanyecao.hu.fever_thermometer.R;

/**
 * Created by huhaisong on 2017/8/30 9:42.
 */

public class NameEditDialog extends Dialog {

    private Context mContext;
    private String name;
    private OnDialogListen onDialogListen;

    public NameEditDialog(Context context, String name, OnDialogListen listen) {
        super(context);
        this.mContext = context;
        this.name = name;
        this.onDialogListen = listen;
    }

    public NameEditDialog(Context context, OnDialogListen listen) {
        super(context);
        this.mContext = context;
        this.onDialogListen = listen;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(name);
    }


    private void init(String name) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        final View view = inflater.inflate(R.layout.dialog_name_edit, null);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //去除title
        setCancelable(true); //设置点击白色不消失
        setContentView(view);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = 800;
        params.height = 600;
        getWindow().setAttributes(params);
        final EditText nameEditText;
        nameEditText = (EditText) view.findViewById(R.id.et_name);
        if (name != null) {
            nameEditText.setText(name);
        } else {
            nameEditText.setText("Baby");
        }
        view.findViewById(R.id.bt_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (onDialogListen != null) {
                    onDialogListen.onCancel();
                }
            }
        });
        view.findViewById(R.id.bt_ensure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (onDialogListen != null) {
                    onDialogListen.onEnsure(nameEditText.getText().toString());
                }
            }
        });
    }

    public interface OnDialogListen {
        void onEnsure(String name);

        void onCancel();
    }
}
