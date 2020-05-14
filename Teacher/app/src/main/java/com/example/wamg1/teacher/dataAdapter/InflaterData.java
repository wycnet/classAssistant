package com.example.wamg1.teacher.dataAdapter;

import android.util.Log;

import com.example.wamg1.teacher.bean.Class;

import java.util.ArrayList;
import java.util.List;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class InflaterData {


    public static List<Class> mClasses=new ArrayList<>();
    public static ClassAapter inflaterClass(){

        BmobQuery<Class> query = new BmobQuery<>();
//查询playerName叫“比目”的数据
        //query.addWhereEqualTo("playerName", "比目");
//返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(500);
//执行查询方法
        query.findObjects(new FindListener<Class>() {

            @Override
            public void done(List<Class> object, BmobException e) {
                if(e==null){
                    //toast("查询成功：共"+object.size()+"条数据。");
                    mClasses.addAll(object);
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }


        });

        Log.i("bmob", "inflaterClass: "+mClasses.toString());

       return new ClassAapter(mClasses);

    }
}
