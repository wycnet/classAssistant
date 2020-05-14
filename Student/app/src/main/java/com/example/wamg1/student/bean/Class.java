package com.example.wamg1.student.bean;

import cn.bmob.v3.BmobObject;

public class Class extends BmobObject {
    private Teacher tNo;
    private String cName;
    private String callTimes;

    public Teacher gettNo() {
        return tNo;
    }

    public void settNo(Teacher tNo) {
        this.tNo = tNo;
    }

    public String getcName() {
        return cName;
    }

    public void setcName(String cName) {
        this.cName = cName;
    }

    public String getCallTimes() {
        return callTimes;
    }

    public void setCallTimes(String callTimes) {
        this.callTimes = callTimes;
    }
}
