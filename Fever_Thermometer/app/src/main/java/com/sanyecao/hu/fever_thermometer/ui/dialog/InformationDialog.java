package com.sanyecao.hu.fever_thermometer.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.sanyecao.hu.fever_thermometer.R;

/**
 * Created by huhaisong on 2017/9/12 16:49.
 */

public class InformationDialog extends Dialog {

    private Context mContext;
    private String content;
    private OnEnsureListen onEnsureListen;

    public InformationDialog(Context context, String content) {
        super(context);
        this.mContext = context;
        this.content = content;
    }

    public InformationDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    protected InformationDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public interface OnEnsureListen {
        void onEnsureListen();
    }

    public void setOnEnsureListen(OnEnsureListen onEnsureListen) {
        this.onEnsureListen = onEnsureListen;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }


    private void init() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        final View view = inflater.inflate(R.layout.dialog_information, null);
        TextView textView = (TextView) view.findViewById(R.id.content);
        textView.setText(content);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //去除title
        setContentView(view);
//        WindowManager.LayoutParams params = getWindow().getAttributes();
//        params.width = 700;
//        params.height = 500;
//        getWindow().setAttributes(params);
        view.findViewById(R.id.tv_ensure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onEnsureListen != null)
                    onEnsureListen.onEnsureListen();
                dismiss();
            }
        });
    }
}
