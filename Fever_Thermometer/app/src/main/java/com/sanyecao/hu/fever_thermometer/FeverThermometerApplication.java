package com.sanyecao.hu.fever_thermometer;

import android.app.Application;
import android.util.Log;

import com.sanyecao.hu.fever_thermometer.mode.database.bean.DaoMaster;
import com.sanyecao.hu.fever_thermometer.mode.database.bean.DaoSession;

import org.greenrobot.greendao.database.Database;

/**
 * Created by huhaisong on 2017/8/14 10:02.
 */

public class FeverThermometerApplication extends Application {

    private static FeverThermometerApplication mInstance;
    private static DaoSession daoSession;
    private static final String TAG = "FeverThermometerApplica";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate: ");
        this.mInstance = this;
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "fever_thermometer-db_test2");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public static DaoSession getDaoSession() {
        return daoSession;
    }

    public static FeverThermometerApplication getInstance() {
        return mInstance;
    }

    public int getScreenWidth() {

        return 0;
    }

    public int getScreenHeight() {

        return 0;
    }
}
