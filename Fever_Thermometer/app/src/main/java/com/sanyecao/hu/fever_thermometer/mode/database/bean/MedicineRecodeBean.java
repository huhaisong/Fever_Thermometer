package com.sanyecao.hu.fever_thermometer.mode.database.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by huhaisong on 2017/8/29 15:48.
 * 服药记录
 */

@Entity
public class MedicineRecodeBean {
    @Id(autoincrement = true)
    private Long id;

    private String medicines; //药物名称
    private String date;      //服药时间
    private int babyId;  //宝宝名字
    @Generated(hash = 130346250)
    public MedicineRecodeBean(Long id, String medicines, String date, int babyId) {
        this.id = id;
        this.medicines = medicines;
        this.date = date;
        this.babyId = babyId;
    }
    @Generated(hash = 353335553)
    public MedicineRecodeBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getMedicines() {
        return this.medicines;
    }
    public void setMedicines(String medicines) {
        this.medicines = medicines;
    }
    public String getDate() {
        return this.date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public int getBabyId() {
        return this.babyId;
    }
    public void setBabyId(int babyId) {
        this.babyId = babyId;
    }
}
