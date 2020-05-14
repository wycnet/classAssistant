package com.example.wamg1.teacher.Test;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.wamg1.teacher.MainActivity;
import com.example.wamg1.teacher.bean.Class;
import com.example.wamg1.teacher.bean.ClassStudent;
import com.example.wamg1.teacher.bean.Student;
import com.example.wamg1.teacher.bean.Teacher;
import com.example.wamg1.teacher.logTest.LogUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class CreatTestDate{




    public static void startClass(){
//        Student student;
//        int sNo=10001;
//        int a=1;
//        String sName="王小二";
//        String sEmail="11@qq.com";
//        String school="合肥工业大学";
        Class student;
        Teacher tNo;
        String cName="测试专用";
        String callTimes="1";
        tNo=new Teacher();
        tNo.setObjectId("00aa0d3e1c");

        List<BmobObject> persons = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            student=new Class();
            student.settNo(tNo);
            student.setCallTimes(callTimes);
            student.setcName(cName);
            persons.add(student);
        }

        new BmobBatch().insertBatch(persons).doBatch(new QueryListListener<BatchResult>() {

            @Override
            public void done(List<BatchResult> o, BmobException e) {
                if(e==null){
                    for(int i=0;i<o.size();i++){
                        BatchResult result = o.get(i);
                        BmobException ex =result.getError();
                        if(ex==null){
                            //log("第"+i+"个数据批量添加成功："+result.getCreatedAt()+","+result.getObjectId()+","+result.getUpdatedAt());
                            Log.i("bmob","第"+i+"个数据批量添加成功："+result.getCreatedAt()+","+result.getObjectId()+","+result.getUpdatedAt());
                        }else{
                            //log("第"+i+"个数据批量添加失败："+ex.getMessage()+","+ex.getErrorCode());
                            Log.i("bmob", "第"+i+"个数据批量添加失败："+ex.getMessage()+","+ex.getErrorCode());
                        }
                    }
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }
//    public static void start2(){
//        Student student;
//        int sNo=10001;
//        int a=1;
//        String sName="王小二";
//        String sEmail="11@qq.com";
//        String school="合肥工业大学";
//
//        List<BmobObject> persons = new ArrayList<>();
//        for (int i = 0; i < 30; i++) {
//            student=new Student();
//            student.setsNo(Integer.toString(sNo++));
//            student.setsName(sName+Integer.toString(a++));
//            student.setsEmail(sEmail);
//            student.setSchool(school);
//
//            persons.add(student);
//        }
//
//        new BmobBatch().insertBatch(persons).doBatch(new QueryListListener<BatchResult>() {
//
//            @Override
//            public void done(List<BatchResult> o, BmobException e) {
//                if(e==null){
//                    for(int i=0;i<o.size();i++){
//                        BatchResult result = o.get(i);
//                        BmobException ex =result.getError();
//                        if(ex==null){
//                            //log("第"+i+"个数据批量添加成功："+result.getCreatedAt()+","+result.getObjectId()+","+result.getUpdatedAt());
//                            Log.i("bmob","第"+i+"个数据批量添加成功："+result.getCreatedAt()+","+result.getObjectId()+","+result.getUpdatedAt());
//                        }else{
//                            //log("第"+i+"个数据批量添加失败："+ex.getMessage()+","+ex.getErrorCode());
//                            Log.i("bmob", "第"+i+"个数据批量添加失败："+ex.getMessage()+","+ex.getErrorCode());
//                        }
//                    }
//                }else{
//                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
//                }
//            }
//        });
//    }
//    public static void start(){
//        Student student;
//        int sNo=10001;
//        int a=1;
//        String sName="王小二";
//        String sEmail="11@qq.com";
//        String school="合肥工业大学";
//
//        for(int i=0;i<30;i++){
//            student=new Student();
//            student.setsNo(Integer.toString(sNo++));
//            student.setsName(sName+Integer.toString(a++));
//            student.setsEmail(sEmail);
//            student.setSchool(school);
//            student.save(new SaveListener<String>() {
//
//                @Override
//                public void done(String objectId, BmobException e) {
//                    if(e==null){
//                        //toast("创建数据成功：" + objectId);
//                        Log.i("bmob", "done: 创建数据成功");
//                    }else{
//                        Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
//                    }
//                }
//            });
//        }
//    }

    public static void createAnsower(){
    }



    public static void createStudent(){

        int sNo=2014217218;
        for(int i=0;i<30;i++){
            Student mStudent=new Student();
            mStudent.setUsername(""+(sNo++));
            mStudent.setPassword("123456");
            mStudent.signUp(new SaveListener<Student>() {
                @Override
                public void done(Student student, BmobException e) {
                    if(e==null){
                        LogUtil.e("ff",""+student.getUsername()+"注册cg");
                    }
                }
            });
        }
    }

    public static void createStudentName(){
        int sNo=2014217218;
        String str="龙催荣 付源梓 李坤 蔡有城 李华 胡阳 刘新月 徐珑刀 朋帆 董丙冰 刘康 刘超凡 刘小娜 丁平 鲍旭丹 沈浩 韩承村 缪乃阳 汪崟灿 董博文 胡圣杰 桂毓灵 陈志谋 杨旭 厉子凡 陈华园 翁兆峰 高垚 丁凯旋 于宵宵 凡双根 马云婷 章文 洪炎 杨海娇 马仲军 刘志";
        final String[] name=str.split(" ");
        for(int i=0;i<30;i++){
            BmobQuery<Student> query = new BmobQuery<>();
            //查询playerName叫“比目”的数据
            query.addWhereEqualTo("username", Integer.toString(sNo++));
            query.setLimit(1);
            //执行查询方法
            final int finalI = i;
            query.findObjects(new FindListener<Student>() {
                @Override
                public void done(List<Student> object, BmobException e) {
                    if(e==null){
                        if(object.size()==1){
                            Student student=new Student();
                            student.setName(name[finalI]);
                            LogUtil.e("name",name[finalI]);
                            String id=object.get(0).getObjectId();
                            student.update(id, new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if(e==null){

                                    }else{
                                        Log.i("bmob","更新失败："+e.getMessage()+","+e.getErrorCode());
                                    }
                                }
                            });
                        }
                    }

                }
            });
        }


    }

    public static void createCLassStudent(){
        int sNo=2014217218;
        for(int i=0;i<30;i++){
            BmobQuery<Student> query = new BmobQuery<>();
            //查询playerName叫“比目”的数据
            query.addWhereEqualTo("username", Integer.toString(sNo++));
            //返回50条数据，如果不加上这条语句，默认返回10条数据
            query.setLimit(50);
            //执行查询方法
            query.findObjects(new FindListener<Student>() {
                @Override
                public void done(List<Student> object, BmobException e) {
                    if(e==null){
                        LogUtil.e("ff","成功"+object.get(0).getUsername());
                        //查询成功后就存储
                        Class mClass=new Class();
                        mClass.setObjectId("3face04b1e");
                        Student mStudent=object.get(0);
                        ClassStudent mClassStudent=new ClassStudent();
                        mClassStudent.setsNo(mStudent);
                        mClassStudent.setcNo(mClass);
                        mClassStudent.save(new SaveListener<String>() {

                            @Override
                            public void done(String objectId,BmobException e) {
                                if(e==null){
                                    Log.i("bmob","保存成功");
                                }else{
                                    Log.i("bmob","保存失败："+e.getMessage());
                                }
                            }
                        });

                    }else{
                        Log.e("ff","失败："+e.getMessage()+","+e.getErrorCode());
                    }
                }
            });


        }


    }
}
