package com.example.wamg1.teacher.bean;

import cn.bmob.v3.BmobObject;

public class CallName extends BmobObject {
    private String callNo;
    private String times;
    private Student sNo;
    private Class cNo;
    private String state;

    public String getCallNo() {
        return callNo;
    }

    public void setCallNo(String callNo) {
        this.callNo = callNo;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public Student getsNo() {
        return sNo;
    }

    public void setsNo(Student sNo) {
        this.sNo = sNo;
    }

    public Class getcNo() {
        return cNo;
    }

    public void setcNo(Class cNo) {
        this.cNo = cNo;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
