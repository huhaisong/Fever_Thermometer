package com.sanyecao.hu.fever_thermometer.ui.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sanyecao.hu.fever_thermometer.R;

/**
 * Created by huhaisong on 2017/8/16 15:09.
 */

public abstract class ToolBarActivity extends BaseActivity {

    private ViewGroup mContentView;
    private TextView barTextView;
    private ImageView barImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContentView();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        barTextView = (TextView) toolbar.findViewById(R.id.toolbar_tv);
        barImageView = (ImageView) toolbar.findViewById(R.id.toolbar_iv);
        barTextView.setVisibility(View.GONE);
        barImageView.setVisibility(View.GONE);
    }

    public void setToolBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    private void initContentView() {
        ViewGroup viewGroup = (ViewGroup) findViewById(android.R.id.content);
        viewGroup.removeAllViews();
        View rootView = LayoutInflater.from(this).inflate(R.layout.activity_base_toolbar_layout, null);
        viewGroup.addView(rootView);
        mContentView = (ViewGroup) findViewById(R.id.layout_content);
    }

    //设置右边的textView
    public void setBarTextView(String string) {
        barTextView.setVisibility(View.VISIBLE);
        barTextView.setText(string);
    }

    //设置右边的imageview的图片
    public void setBarImageView(int res) {
        barImageView.setVisibility(View.VISIBLE);
        barImageView.setBackgroundResource(res);
    }

    //设置右边的textView的点击事件
    public void setBarTextViewListener(View.OnClickListener listener) {
        barTextView.setOnClickListener(listener);
    }

    //设置右边的imageview的图片的点击事件
    public void setBarImageViewListener(View.OnClickListener listener) {
        barImageView.setOnClickListener(listener);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        LayoutInflater.from(this).inflate(layoutResID, mContentView, true);
    }

    @Override
    public void setContentView(View view) {
        mContentView.addView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        mContentView.addView(view, params);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
