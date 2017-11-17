package com.sanyecao.hu.fever_thermometer.mode.database.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by huhaisong on 2017/9/13 15:46.
 * 设置的吃药闹钟，用于提醒用户给宝宝吃药
 */
@Entity
public class AlarmBean {

    @Id(autoincrement = true)
    private Long id;

    private String time;

    private float spaceTime ;
    private int repeatTime;

    @Generated(hash = 1114481208)
    public AlarmBean(Long id, String time, float spaceTime, int repeatTime) {
        this.id = id;
        this.time = time;
        this.spaceTime = spaceTime;
        this.repeatTime = repeatTime;
    }

    @Generated(hash = 1834917355)
    public AlarmBean() {
    }

    @Override
    public String toString() {
        return "AlarmBean{" +
                "id=" + id +
                ", time='" + time + '\'' +
                ", spaceTime=" + spaceTime +
                ", repeatTime=" + repeatTime +
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

    public float getSpaceTime() {
        return this.spaceTime;
    }

    public void setSpaceTime(float spaceTime) {
        this.spaceTime = spaceTime;
    }

    public int getRepeatTime() {
        return this.repeatTime;
    }

    public void setRepeatTime(int repeatTime) {
        this.repeatTime = repeatTime;
    }
}
