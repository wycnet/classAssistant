package com.example.wamg1.teacher.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class Homework extends BmobObject {
    private String hName;
    private Class cNo;
    private BmobFile file;

    public String gethName() {
        return hName;
    }

    public void sethName(String hName) {
        this.hName = hName;
    }

    public Class getcNo() {
        return cNo;
    }

    public void setcNo(Class cNo) {
        this.cNo = cNo;
    }

    public BmobFile getFile() {
        return file;
    }

    public void setFile(BmobFile file) {
        this.file = file;
    }
}
