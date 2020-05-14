package com.example.wamg1.student.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class SubmitWork extends BmobObject {
    private Student sNo;
    private Homework hNo;
    private String score;
    private Boolean isSub;
    private BmobFile jFile;

    public Student getsNo() {
        return sNo;
    }

    public void setsNo(Student sNo) {
        this.sNo = sNo;
    }

    public Homework gethNo() {
        return hNo;
    }

    public void sethNo(Homework hNo) {
        this.hNo = hNo;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public Boolean getSub() {
        return isSub;
    }

    public void setSub(Boolean sub) {
        isSub = sub;
    }

    public BmobFile getjFile() {
        return jFile;
    }

    public void setjFile(BmobFile jFile) {
        this.jFile = jFile;
    }
}
