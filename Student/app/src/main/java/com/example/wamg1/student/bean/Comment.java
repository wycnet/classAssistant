package com.example.wamg1.student.bean;

import cn.bmob.v3.BmobObject;


public class Comment extends BmobObject {
    private Student user;
    private String comment;
    private Talk talk;

    public Student getUser() {
        return user;
    }

    public void setUser(Student user) {
        this.user = user;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Talk getTalk() {
        return talk;
    }

    public void setTalk(Talk talk) {
        this.talk = talk;
    }
}
