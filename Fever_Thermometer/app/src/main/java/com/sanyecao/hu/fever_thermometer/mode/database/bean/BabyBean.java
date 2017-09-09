package com.sanyecao.hu.fever_thermometer.mode.database.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by huhaisong on 2017/8/29 15:37.
 */

@Entity
public class BabyBean {

    @Id(autoincrement = true)
    private Long id;

    private String image_url;  //宝宝头像url地址
    private int machineId;
    private String name;      //宝宝名字
    @Generated(hash = 657580815)
    public BabyBean(Long id, String image_url, int machineId, String name) {
        this.id = id;
        this.image_url = image_url;
        this.machineId = machineId;
        this.name = name;
    }

    public BabyBean(String image_url, int machineId, String name) {
        this.image_url = image_url;
        this.machineId = machineId;
        this.name = name;
    }

    @Generated(hash = 1071921734)
    public BabyBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getImage_url() {
        return this.image_url;
    }
    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
    public int getMachineId() {
        return this.machineId;
    }
    public void setMachineId(int machineId) {
        this.machineId = machineId;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
