package com.example.wamg1.teacher.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;

public class Student extends BmobUser {
    private String name;
    private String school;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }
}
