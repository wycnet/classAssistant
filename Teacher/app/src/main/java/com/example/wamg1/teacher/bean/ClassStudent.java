package com.example.wamg1.teacher.bean;

import cn.bmob.v3.BmobObject;

public class ClassStudent extends BmobObject {
    private Student sNo;
    private Class cNo;

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
}
