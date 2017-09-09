package com.sanyecao.hu.fever_thermometer.mode.database.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by huhaisong on 2017/9/2 16:06.
 */

@Entity
public class CacheMedicineBean {

    @Id(autoincrement = true)
    private Long id;

    private String medicineName;

    @Generated(hash = 1902117691)
    public CacheMedicineBean(Long id, String medicineName) {
        this.id = id;
        this.medicineName = medicineName;
    }

    @Generated(hash = 675894513)
    public CacheMedicineBean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMedicineName() {
        return this.medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

}
