package com.example.wamg1.teacher.bean;

import cn.bmob.v3.BmobObject;

public class Notice extends BmobObject{
    private String title;
    private String notice;
    private Class cNo;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public Class getcNo() {
        return cNo;
    }

    public void setcNo(Class cNo) {
        this.cNo = cNo;
    }
}
