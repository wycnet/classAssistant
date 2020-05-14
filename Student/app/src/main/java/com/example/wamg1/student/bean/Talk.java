package com.example.wamg1.student.bean;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

public class Talk extends BmobObject implements Serializable{
    private String talk;
    private Student user;
    private Class cNo;


    public String getTalk() {
        return talk;
    }

    public void setTalk(String talk) {
        this.talk = talk;
    }

    public Student getUser() {
        return user;
    }

    public void setUser(Student user) {
        this.user = user;
    }

    public Class getcNo() {
        return cNo;
    }

    public void setcNo(Class cNo) {
        this.cNo = cNo;
    }
}
