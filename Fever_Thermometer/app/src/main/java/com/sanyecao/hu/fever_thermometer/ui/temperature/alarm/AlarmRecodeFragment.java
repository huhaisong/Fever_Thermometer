package com.sanyecao.hu.fever_thermometer.ui.temperature.alarm;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sanyecao.hu.fever_thermometer.R;
import com.sanyecao.hu.fever_thermometer.mode.database.bean.AlarmBean;
import com.sanyecao.hu.fever_thermometer.service.AlarmService;
import com.sanyecao.hu.fever_thermometer.ui.base.BaseTollBarFragment;
import com.sanyecao.hu.fever_thermometer.ui.base.ToolBarActivity;

import java.util.List;

import static com.sanyecao.hu.fever_thermometer.mode.database.a.DatabaseController.getmInstance;

/**
 * Created by huhaisong on 2017/9/15 10:57.
 */

public class AlarmRecodeFragment extends BaseTollBarFragment {

    private static AlarmRecodeFragment mInstance;
    private ToolBarActivity mActivity;
    private static final String TAG = "AlarmRecodeFragment";
    private List<AlarmBean> alarmBeanList;
    private ScrollView scrollView;
    private LayoutInflater layoutInflater;

    @Override
    public void initToolbar() {
        mActivity.initBar();
        mActivity.setToolBarTitle("提醒列表");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mActivity = (ToolBarActivity) getActivity();
        layoutInflater = inflater;
        View view = inflater.inflate(R.layout.fragment_alarm_recode_layout, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        alarmBeanList = getmInstance().queryAllAlarmBean();
        scrollView = (ScrollView) view.findViewById(R.id.scrollView);
        updateAlarmRecodeContainView();
    }

    private void updateAlarmRecodeContainView() {
        alarmBeanList = getmInstance().queryAllAlarmBean();
        scrollView.removeAllViews();
        LinearLayout linearLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        if (alarmBeanList.size() == 0)
            return;
        for (AlarmBean item : alarmBeanList) {
            final LinearLayout linearLayoutItem = (LinearLayout) layoutInflater.inflate(R.layout.alarm_recode_item_layout, null);
            ((TextView) linearLayoutItem.findViewById(R.id.tv_time)).setText(item.getTime());
            ((TextView) linearLayoutItem.findViewById(R.id.tv_space_time)).setText(item.getSpaceTime() + "");
            ((TextView) linearLayoutItem.findViewById(R.id.tv_repeat_time)).setText(item.getRepeatTime() + "");
            linearLayoutItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    dialog(v);
                    return false;
                }

                protected void dialog(View v) {
                    final Dialog dialog = new Dialog(getContext());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    LinearLayout linearLayout = new LinearLayout(getContext());
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    linearLayout.setLayoutParams(layoutParams);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    linearLayout.setGravity(Gravity.CENTER);
                    TextView textView = new TextView(getContext());
                    textView.setText("删除此闹钟！");
                    textView.setPadding(20, 20, 20, 20);
                    textView.setGravity(Gravity.CENTER);
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.e(TAG, "onItemSelected: ");
                            getmInstance().deleteAlarmBeanByTime(
                                    ((TextView) linearLayoutItem.findViewById(R.id.tv_time)).getText().toString());
                            updateAlarmRecodeContainView();
                            getActivity().sendBroadcast(new Intent(AlarmService.UPDATE_ALARM_ACTION));
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
            linearLayout.addView(linearLayoutItem);
        }
        scrollView.addView(linearLayout);
    }

    public static AlarmRecodeFragment getInstance() {
        return mInstance == null ? (mInstance = new AlarmRecodeFragment()) : mInstance;
    }
}
