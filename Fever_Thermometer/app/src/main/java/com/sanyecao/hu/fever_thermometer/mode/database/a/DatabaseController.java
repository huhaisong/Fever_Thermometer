package com.sanyecao.hu.fever_thermometer.mode.database.a;

import android.content.Context;

import com.sanyecao.hu.fever_thermometer.FeverThermometerApplication;
import com.sanyecao.hu.fever_thermometer.mode.database.bean.AlarmBean;
import com.sanyecao.hu.fever_thermometer.mode.database.bean.AlarmBeanDao;
import com.sanyecao.hu.fever_thermometer.mode.database.bean.BabyBean;
import com.sanyecao.hu.fever_thermometer.mode.database.bean.BabyBeanDao;
import com.sanyecao.hu.fever_thermometer.mode.database.bean.CacheMedicineBean;
import com.sanyecao.hu.fever_thermometer.mode.database.bean.CacheMedicineBeanDao;
import com.sanyecao.hu.fever_thermometer.mode.database.bean.DaoSession;
import com.sanyecao.hu.fever_thermometer.mode.database.bean.MedicineRecodeBean;
import com.sanyecao.hu.fever_thermometer.mode.database.bean.MedicineRecodeBeanDao;
import com.sanyecao.hu.fever_thermometer.mode.database.bean.RecodeNoteBean;
import com.sanyecao.hu.fever_thermometer.mode.database.bean.RecodeNoteBeanDao;
import com.sanyecao.hu.fever_thermometer.mode.database.bean.TemperatureRecodeBean;
import com.sanyecao.hu.fever_thermometer.mode.database.bean.TemperatureRecodeBeanDao;

import org.greenrobot.greendao.query.Query;

import java.util.List;

/*
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
    private AlarmBeanDao alarmBeanDao;
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
        alarmBeanDao = daoSession.getAlarmBeanDao();
    }

    public BabyBean queryBabyBeanByName(String name) {
        Query<BabyBean> babyBeanQuery = babyBeanDao.queryBuilder().where(BabyBeanDao.Properties.Name.eq(name)).build();
        List<BabyBean> babyBeanList = babyBeanQuery.list();
        if (babyBeanList == null || babyBeanList.size() == 0) {
            return null;
        }
        return babyBeanList.get(0);
    }

    public BabyBean queryBabyBeanById(int id) {
        Query<BabyBean> babyBeanQuery = babyBeanDao.queryBuilder().where(BabyBeanDao.Properties.Id.eq(id)).build();
        List<BabyBean> babyBeanList = babyBeanQuery.list();
        if (babyBeanList == null || babyBeanList.size() == 0) {
            return null;
        }
        return babyBeanList.get(0);
    }

    public List<BabyBean> queryAllBabyBean() {
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

    public void addBabyBean(BabyBean babyBean) {
        babyBeanDao.insert(babyBean);
    }

    /*关于事件记录*/
    public void addRecodeNoteBean(RecodeNoteBean recodeNoteBean) {
        recodeNoteBeanDao.insert(recodeNoteBean);
    }

    public List<RecodeNoteBean> queryAllRecodeNoteBean() {
        Query<RecodeNoteBean> recodeNoteBeanQuery = recodeNoteBeanDao.queryBuilder().build();
        return recodeNoteBeanQuery.list();
    }
    public void deleteRecodeNoteBeanById(int id) {
        RecodeNoteBean recodeNoteBean = queryRecodeNoteBeanById(id);
        recodeNoteBeanDao.delete(recodeNoteBean);
    }

    public RecodeNoteBean queryRecodeNoteBeanById(int id) {
        Query<RecodeNoteBean> recodeNoteBeanQuery = recodeNoteBeanDao
                .queryBuilder().where(RecodeNoteBeanDao.Properties.Id.eq(id)).build();
        return recodeNoteBeanQuery.list().get(0);
    }
    /*关于事件记录*/

    /*关于已保存供选择的药品*/
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
    /*关于已保存供选择的药品*/

    /*关于已经记录的宝宝温度*/
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
    /*关于已经记录的宝宝温度*/

    /*关于服药记录*/
    public void addMedicineRecodeBean(MedicineRecodeBean medicineRecodeBean) {
        medicineRecodeBeanDao.insert(medicineRecodeBean);
    }

    public List<MedicineRecodeBean> queryAllMedicineRecodeBean() {
        Query<MedicineRecodeBean> medicineRecodeBeanQuery = medicineRecodeBeanDao.queryBuilder().build();
        return medicineRecodeBeanQuery.list();
    }

    public void deleteMedicineRecodeBeanById(int id) {
        MedicineRecodeBean medicineRecodeBean = queryMedicineRecodeBeanById(id);
        medicineRecodeBeanDao.delete(medicineRecodeBean);
    }

    public MedicineRecodeBean queryMedicineRecodeBeanById(int id) {
        Query<MedicineRecodeBean> medicineRecodeBeanQuery = medicineRecodeBeanDao
                .queryBuilder().where(MedicineRecodeBeanDao.Properties.Id.eq(id)).build();
        return medicineRecodeBeanQuery.list().get(0);
    }
    /*关于服药记录*/

    /* 关于闹钟提醒*/
    public void addAlarmBean(AlarmBean alarmBean) {
        alarmBeanDao.insert(alarmBean);
    }

    public List<AlarmBean> queryAllAlarmBean() {
        Query<AlarmBean> alarmBeanQuery = alarmBeanDao.queryBuilder().build();
        return alarmBeanQuery.list();
    }

    public AlarmBean queryAlarmBeanByTime(String time) {
        Query<AlarmBean> alarmBeanQuery = alarmBeanDao.queryBuilder().where(AlarmBeanDao.Properties.Time.eq(time)).build();
        List<AlarmBean> list = alarmBeanQuery.list();
        if (list.size() == 0) {
            return null;
        }
        return list.get(0);
    }

    public void deleteAlarmBeanByTime(String time) {
        AlarmBean alarmBean = queryAlarmBeanByTime(time);
        alarmBeanDao.delete(alarmBean);
    }
    /*关于闹钟提醒*/
}
