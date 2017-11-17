package com.sanyecao.hu.fever_thermometer.mode.database.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by huhaisong on 2017/8/29 15:49.
 * 温度记录
 */

@Entity
public class TemperatureRecodeBean {
    @Id(autoincrement = true)
    private Long id;

    private String time;//时间
    private float Temperature;//温度
    private int babyId;//宝宝Id

    @Generated(hash = 2087239777)
    public TemperatureRecodeBean(Long id, String time, float Temperature,
            int babyId) {
        this.id = id;
        this.time = time;
        this.Temperature = Temperature;
        this.babyId = babyId;
    }

    @Generated(hash = 393173144)
    public TemperatureRecodeBean() {
    }

    @Override
    public String toString() {
        return "TemperatureRecodeBean{" +
                "id=" + id +
                ", time='" + time + '\'' +
                ", Temperature=" + Temperature +
                ", babyId=" + babyId +
                '}';
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public float getTemperature() {
        return this.Temperature;
    }

    public void setTemperature(float Temperature) {
        this.Temperature = Temperature;
    }

    public int getBabyId() {
        return this.babyId;
    }

    public void setBabyId(int babyId) {
        this.babyId = babyId;
    }
}
