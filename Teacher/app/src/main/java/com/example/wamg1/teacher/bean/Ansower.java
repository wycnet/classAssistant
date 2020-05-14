package com.example.wamg1.teacher.bean;

import cn.bmob.v3.BmobObject;

public class Ansower extends BmobObject {
    private Question qNo;
    private Student sNo;
    private Boolean isTrue;
    private Boolean isAnswer;
    private String option;

    public Boolean getAnswer() {
        return isAnswer;
    }

    public void setAnswer(Boolean answer) {
        isAnswer = answer;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public Question getqNo() {
        return qNo;
    }

    public void setqNo(Question qNo) {
        this.qNo = qNo;
    }

    public Student getsNo() {
        return sNo;
    }

    public void setsNo(Student sNo) {
        this.sNo = sNo;
    }

    public Boolean getTrue() {
        return isTrue;
    }

    public void setTrue(Boolean aTrue) {
        isTrue = aTrue;
    }


}
