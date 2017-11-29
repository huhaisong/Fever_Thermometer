package com.sanyecao.hu.fever_thermometer.ui.base;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.sanyecao.hu.fever_thermometer.R;
import com.sanyecao.hu.fever_thermometer.ui.widget.GlideCircleTransform;
import com.sanyecao.hu.fever_thermometer.utils.ToastUtils;

import java.lang.reflect.Field;

/**
 * Created by huhaisong on 2017/8/14 15:48.
 */

public abstract class DrawerActivity extends BaseActivity {

    private LinearLayout mContentView;
    private long mFirstClickTime;
    protected int mCurrentMenu;
    private DrawerLayout mDrawerLayout;
    protected static final int TEMPERATURE = R.id.main_temperature;
    protected static final int SETTING = R.id.main_setting;
    protected static final int HISTORY_RECORD = R.id.main_history_record;
    protected NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        initContentView();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        setDrawerLeftEdgeSize(this, mDrawerLayout, 0.15f);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        ImageView mHeadImg = (ImageView) mNavigationView.getHeaderView(0).findViewById(R.id.iv_nav_head);

        mCurrentMenu = bindMenuId();
        if (mCurrentMenu != 0) {
            mNavigationView.setCheckedItem(mCurrentMenu);
        }
        mNavigationView.setItemTextColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.gray)));
        Glide.with(mContext)
                .load(R.drawable.header)
                .transform(new GlideCircleTransform(mContext))
                .into(mHeadImg);
    }

    private void initContentView() {
        ViewGroup viewGroup = (ViewGroup) findViewById(android.R.id.content);
        viewGroup.removeAllViews();
        View rootView = LayoutInflater.from(this).inflate(R.layout.drawer_layout, null);
        viewGroup.addView(rootView);
        mContentView = (LinearLayout) rootView.findViewById(R.id.layout_content);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        LayoutInflater.from(this).inflate(layoutResID, mContentView, true);
    }

    @Override
    public void setContentView(View view) {
        mContentView.addView(view);
    }

    private static final String TAG = "DrawerActivity";

    @Override
    public void onBackPressed() {
        //区分主菜单页面和子菜单页面
        if (mCurrentMenu != 0) {
            if (!closeDrawer()) {
                long curClickTime = System.currentTimeMillis();
                if ((curClickTime - mFirstClickTime) < 1000) {
//                    ActivityStack.getInstance().cleanAll();
                    Intent home = new Intent(Intent.ACTION_MAIN);
                    home.addCategory(Intent.CATEGORY_HOME);
                    startActivity(home);
                } else {
                    ToastUtils.show(this, "再点击一次将退出应用");
                    mFirstClickTime = curClickTime;
                }
            }
        } else {
            super.onBackPressed();
        }
    }


    protected boolean closeDrawer() {
        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
            return true;
        }
        return false;
    }


    protected void addToolbar(Toolbar toolbar) {
        if (toolbar == null) {
            return;
        }
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, getDrawerLayout(), toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        getDrawerLayout().addDrawerListener(toggle);
        toggle.syncState();
    }

    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    protected abstract int bindMenuId();

    protected void bindMeunListen(NavigationView.OnNavigationItemSelectedListener listener) {
        mNavigationView.setNavigationItemSelectedListener(listener);
    }


    private void setDrawerLeftEdgeSize(Activity activity, DrawerLayout drawerLayout, float displayWidthPercentage) {
        if (activity == null || drawerLayout == null)
            return;
        try {
            // find ViewDragHelper and set it accessible
            Field leftDraggerField = drawerLayout.getClass().getDeclaredField("mLeftDragger");
            leftDraggerField.setAccessible(true);
            ViewDragHelper leftDragger = (ViewDragHelper) leftDraggerField.get(drawerLayout);
            // find edgesize and set is accessible
            Field edgeSizeField = leftDragger.getClass().getDeclaredField("mEdgeSize");
            edgeSizeField.setAccessible(true);
            int edgeSize = edgeSizeField.getInt(leftDragger);
            // set new edgesize
            // Point displaySize = new Point();
            DisplayMetrics dm = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
            edgeSizeField.setInt(leftDragger, Math.max(edgeSize, (int) (dm.widthPixels * displayWidthPercentage)));
        } catch (NoSuchFieldException e) {
            Log.e("NoSuchFieldException", e.getMessage().toString());
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
            Log.e("IllegalAccessException", e.getMessage().toString());
        }
    }
}
