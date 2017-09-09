package com.sanyecao.hu.fever_thermometer.mode.database.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by huhaisong on 2017/9/2 15:02.
 */
@Entity
public class RecodeNoteBean {

    @Id(autoincrement = true)
    private Long id;
    private String content;
    private String time;
    @Generated(hash = 870430648)
    public RecodeNoteBean(Long id, String content, String time) {
        this.id = id;
        this.content = content;
        this.time = time;
    }
    @Generated(hash = 1478778423)
    public RecodeNoteBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }
}
