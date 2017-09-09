package com.sanyecao.hu.fever_thermometer;

import android.app.Application;

import com.sanyecao.hu.fever_thermometer.mode.database.bean.DaoMaster;
import com.sanyecao.hu.fever_thermometer.mode.database.bean.DaoSession;

import org.greenrobot.greendao.database.Database;

/**
 * Created by huhaisong on 2017/8/14 10:02.
 */

public class FeverThermometerApplication extends Application {

    private static FeverThermometerApplication mInstance;
    private static DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        this.mInstance = this;
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "fever_thermometer-db_test");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public static DaoSession getDaoSession() {
        return daoSession;
    }

    public Application getFerverThermomteterApplication() {
        return mInstance;
    }
}
