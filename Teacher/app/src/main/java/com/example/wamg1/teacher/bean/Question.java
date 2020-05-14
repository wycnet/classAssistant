package com.example.wamg1.teacher.bean;

import android.view.MotionEvent;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class Question extends BmobObject {
    private String qNo;
    private String qFile;
    private Class cNo;

    public String getqNo() {
        return qNo;
    }

    public void setqNo(String qNo) {
        this.qNo = qNo;
    }

    public String  getqFile() {
        return qFile;
    }

    public void setqFile(String qFile) {
        this.qFile = qFile;
    }

    public Class getcNo() {
        return cNo;
    }

    public void setcNo(Class cNo) {
        this.cNo = cNo;
    }
}
