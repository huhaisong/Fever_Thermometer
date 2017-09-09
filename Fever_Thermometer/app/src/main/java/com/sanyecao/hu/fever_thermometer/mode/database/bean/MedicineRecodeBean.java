package com.sanyecao.hu.fever_thermometer.mode.database.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.util.Date;

/**
 * Created by huhaisong on 2017/8/29 15:48.
 */

@Entity
public class MedicineRecodeBean {
    @Id(autoincrement = true)
    private Long id;

    
    private String medicines; //药物名称
    private int temperature;  //服药温度
    private Date date;      //服药时间
    private int babyId;
    @Generated(hash = 1461795323)
    public MedicineRecodeBean(Long id, String medicines, int temperature, Date date,
            int babyId) {
        this.id = id;
        this.medicines = medicines;
        this.temperature = temperature;
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
    public int getTemperature() {
        return this.temperature;
    }
    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }
    public Date getDate() {
        return this.date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public int getBabyId() {
        return this.babyId;
    }
    public void setBabyId(int babyId) {
        this.babyId = babyId;
    }
}
