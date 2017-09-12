package com.sanyecao.hu.fever_thermometer.mode.database.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by huhaisong on 2017/8/29 15:50.
 */

@Entity
public class MachineBean {

    @Id(autoincrement = true)
    private Long id;
    private int machineId;

    private String address;  //设备地址

    @Generated(hash = 1144255544)
    public MachineBean(Long id, int machineId, String address) {
        this.id = id;
        this.machineId = machineId;
        this.address = address;
    }

    @Generated(hash = 786892830)
    public MachineBean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getMachineId() {
        return this.machineId;
    }

    public void setMachineId(int machineId) {
        this.machineId = machineId;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "MachineBean{" +
                "id=" + id +
                ", machineId=" + machineId +
                ", address='" + address + '\'' +
                '}';
    }
}
