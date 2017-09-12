package com.sanyecao.hu.fever_thermometer.mode.database.a;

import android.content.Context;

import com.sanyecao.hu.fever_thermometer.FeverThermometerApplication;
import com.sanyecao.hu.fever_thermometer.mode.database.bean.BabyBean;
import com.sanyecao.hu.fever_thermometer.mode.database.bean.BabyBeanDao;
import com.sanyecao.hu.fever_thermometer.mode.database.bean.CacheMedicineBean;
import com.sanyecao.hu.fever_thermometer.mode.database.bean.CacheMedicineBeanDao;
import com.sanyecao.hu.fever_thermometer.mode.database.bean.DaoSession;
import com.sanyecao.hu.fever_thermometer.mode.database.bean.MachineBean;
import com.sanyecao.hu.fever_thermometer.mode.database.bean.MachineBeanDao;
import com.sanyecao.hu.fever_thermometer.mode.database.bean.MedicineRecodeBeanDao;
import com.sanyecao.hu.fever_thermometer.mode.database.bean.RecodeNoteBean;
import com.sanyecao.hu.fever_thermometer.mode.database.bean.RecodeNoteBeanDao;
import com.sanyecao.hu.fever_thermometer.mode.database.bean.TemperatureRecodeBean;
import com.sanyecao.hu.fever_thermometer.mode.database.bean.TemperatureRecodeBeanDao;

import org.greenrobot.greendao.query.Query;

import java.util.List;

/**
 * Created by huhaisong on 2017/8/30 11:15.
 */

public class DatabaseController {

    private static final String TAG = "DatabaseController";
    private Context mContext;
    private DaoSession daoSession;
    private BabyBeanDao babyBeanDao;
    private RecodeNoteBeanDao recodeNoteBeanDao;
    private CacheMedicineBeanDao cachMedicineBeanDao;
    private TemperatureRecodeBeanDao temperatureRecodeBeanDao;
    private MedicineRecodeBeanDao medicineRecodeBeanDao;
    private MachineBeanDao machineBeanDao;
    private static DatabaseController mInstance;

    private DatabaseController() {
        init();
    }

    public static DatabaseController getmInstance() {
        return mInstance == null ? (mInstance = new DatabaseController()) : mInstance;
    }

    private void init() {
        daoSession = FeverThermometerApplication.getDaoSession();
        babyBeanDao = daoSession.getBabyBeanDao();
        recodeNoteBeanDao = daoSession.getRecodeNoteBeanDao();
        cachMedicineBeanDao = daoSession.getCacheMedicineBeanDao();
        temperatureRecodeBeanDao = daoSession.getTemperatureRecodeBeanDao();
        medicineRecodeBeanDao = daoSession.getMedicineRecodeBeanDao();
        machineBeanDao = daoSession.getMachineBeanDao();
    }

    public List<BabyBean> queryBabyBeanByMachineId(int machineId) {
        Query<BabyBean> babyBeanQuery = babyBeanDao.queryBuilder().where(BabyBeanDao.Properties.MachineId.eq(machineId)).build();
        List<BabyBean> babyBeanList = babyBeanQuery.list();
        return babyBeanList;
    }

    public BabyBean queryBabyBeanByName(String name) {
        Query<BabyBean> babyBeanQuery = babyBeanDao.queryBuilder().where(BabyBeanDao.Properties.Name.eq(name)).build();
        List<BabyBean> babyBeanList = babyBeanQuery.list();
        if (babyBeanList == null || babyBeanList.size() == 0) {
            return null;
        }
        return babyBeanList.get(0);
    }

    public List<BabyBean> getAllBabyBean() {
        Query<BabyBean> babyBeanQuery = babyBeanDao.queryBuilder().build();
        List<BabyBean> babyBeanList = babyBeanQuery.list();
        return babyBeanList;
    }

    public void updateBabyBean(BabyBean babyBean) {
        babyBeanDao.update(babyBean);
    }

    public void deleteAllBabyBean() {
        babyBeanDao.deleteAll();
    }

    public void deleteBabyBean(BabyBean babyBean) {
        babyBeanDao.delete(babyBean);
    }

    public void addRecodeNoteBean(RecodeNoteBean recodeNoteBean) {
        recodeNoteBeanDao.insert(recodeNoteBean);
    }

    public void addBabyBean(BabyBean babyBean) {
        babyBeanDao.insert(babyBean);
    }

    public void addCachMedicine(CacheMedicineBean cachMedicineBean) {
        cachMedicineBeanDao.insert(cachMedicineBean);
    }

    public void deleteCachMedicine(CacheMedicineBean cachMedicineBean) {
        cachMedicineBeanDao.delete(cachMedicineBean);
    }

    public List<CacheMedicineBean> queryCacheMedicine() {
        Query<CacheMedicineBean> query = cachMedicineBeanDao.queryBuilder().build();
        List<CacheMedicineBean> list = query.list();
        return list;
    }

    public CacheMedicineBean queryCacheMedicineByName(String name) {
        Query<CacheMedicineBean> query = cachMedicineBeanDao.queryBuilder()
                .where(CacheMedicineBeanDao.Properties.MedicineName.eq(name)).build();
        List<CacheMedicineBean> list = query.list();
        return list.get(0);
    }

    public void deleteCacheMedicineByName(String name) {
        CacheMedicineBean cacheMedicineBean = queryCacheMedicineByName(name);
        cachMedicineBeanDao.delete(cacheMedicineBean);
    }

    public List<TemperatureRecodeBean> queryTemperatureRecodeByBabyIdAndTimeOrderByTime
            (int babyId, String time) {
        Query<TemperatureRecodeBean> query = temperatureRecodeBeanDao.queryBuilder()
                .where(TemperatureRecodeBeanDao.Properties.BabyId.eq(babyId),
                        TemperatureRecodeBeanDao.Properties.Time.like(time + "%"))
                .orderAsc(TemperatureRecodeBeanDao.Properties.Time)
                .build();
        return query.list();
    }

    public void addTemperatureRecode(TemperatureRecodeBean temperatureRecodeBean) {
        temperatureRecodeBeanDao.insert(temperatureRecodeBean);
    }

    public List<RecodeNoteBean> queryAllRecodeNoteBean() {
        Query<RecodeNoteBean> recodeNoteBeanQuery = recodeNoteBeanDao.queryBuilder().build();
        return recodeNoteBeanQuery.list();
    }

    public MachineBean queryMachineById(int machineId) {
        MachineBean machineBean = machineBeanDao.queryBuilder().where(MachineBeanDao.Properties.Id.eq(machineId)).build().unique();
        return machineBean;
    }

    public MachineBean queryMachineByAdress(String machineAddress) {
        Query<MachineBean> machineBeanQuery = machineBeanDao.queryBuilder().where(MachineBeanDao.Properties.Address.eq(machineAddress)).build();
        List<MachineBean> list = machineBeanQuery.list();
        if (list.size() == 0) {
            return null;
        }
        return list.get(0);
    }

    public void addMachine(MachineBean machineBean) {
        machineBeanDao.insert(machineBean);
    }

    public void updateMachine(MachineBean machineBean) {
        machineBeanDao.update(machineBean);
    }
}
