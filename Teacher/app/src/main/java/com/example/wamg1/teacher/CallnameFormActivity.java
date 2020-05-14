package com.example.wamg1.teacher;

import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.wamg1.teacher.R;
import com.example.wamg1.teacher.bean.CallName;
import com.example.wamg1.teacher.dataAdapter.CallnameFormAdapter;
import com.example.wamg1.teacher.httpOperate.CallnameDada;
import com.example.wamg1.teacher.logTest.LogUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class CallnameFormActivity extends AppCompatActivity {
    private String times;

    private TextView textCuqing;
    private TextView textKuangke;
    private TextView textQingjia;
    private TextView textCidao;

    private CallnameDada mCallnameDada=new CallnameDada();

    private List<CallName> mCallNameList=new ArrayList<>();

    private CallnameFormAdapter callnameFormAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callname_form);

        Intent intent = getIntent();
        times = intent.getStringExtra("times");

        textCuqing = findViewById(R.id.text_cuqing);
        textKuangke = findViewById(R.id.text_kuangke);
        textCidao = findViewById(R.id.text_cidao);
        textQingjia=findViewById(R.id.text_qingjia);


        RecyclerView recyclerView=findViewById(R.id.recycleview_callname_form);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        //recyclerView.addItemDecoration(new MyItemDecoration());
        callnameFormAdapter=new CallnameFormAdapter(this,mCallNameList);
        recyclerView.setAdapter(callnameFormAdapter);

        initCallnamForm(0);

    }




    public void initCallnamForm(final int flag){

        BmobQuery<CallName> query = new BmobQuery<CallName>();
        //查询playerName叫“比目”的数据
        query.addWhereEqualTo("times", times);
        //返回50条数据，如果不加上这条语句，默认返回10条数据
        query.include("sNo");
        query.setLimit(500);
        //执行查询方法
        query.findObjects(new FindListener<CallName>() {
            @Override
            public void done(List<CallName> object, BmobException e) {
                if(e==null){
                    //toast("查询成功：共"+object.size()+"条数据。");
                    int cuqing=0,cidao=0,qingjia=0,kuangke=0;
                    for(CallName callName:object){
                        if(callName.getState().equals("1")||callName.getState().equals("0")){
                            cuqing++;
                        }else  if(callName.getState().equals("2")){
                            kuangke++;
                        }else if(callName.getState().equals("3")){
                            qingjia++;
                        }else  if(callName.getState().equals("4")){
                            cidao++;
                        }

                       // LogUtil.e("hh",callName.getsNo().getUsername()+" ");
                    }

                    mCallnameDada.setQingjia(Integer.toString(qingjia));
                    mCallnameDada.setCidao(Integer.toString(cidao));
                    mCallnameDada.setCuqing(Integer.toString(cuqing));
                    mCallnameDada.setKuangke(Integer.toString(kuangke));

                    mCallNameList.clear();
                    mCallNameList.addAll(object);

                    //可以在此处刷新UI

                    if(flag==0){
                        refrushForm();
                        refrushCount();
                    }else if(flag==1){
                        refrushCount();
                    }

                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }


    //刷新统计信息
    public void refrushCount(){
        textKuangke.setText(mCallnameDada.getKuangke());
        textCuqing.setText(mCallnameDada.getCuqing());
        textQingjia.setText(mCallnameDada.getQingjia());
        textCidao.setText(mCallnameDada.getCidao());
    }

    //刷新名单
    public void refrushForm(){
        callnameFormAdapter.notifyDataSetChanged();
    }

    //更新服务器点名表
    public   void updateCallname(int position,int state){
        CallName mCallName=mCallNameList.get(position);
        mCallName.setState(Integer.toString(state));
        mCallName.update(mCallName.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    //Log.i("bmob","更新成功");
                    rufrushCount();
                }else{
                    Log.i("bmob","更新失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }

    public void rufrushCount(){
        int cuqing=0,cidao=0,qingjia=0,kuangke=0;
        for(CallName callName:mCallNameList){
            if(callName.getState().equals("1")||callName.getState().equals("0")){
                cuqing++;
            }else  if(callName.getState().equals("2")){
                kuangke++;
            }else if(callName.getState().equals("3")){
                qingjia++;
            }else  if(callName.getState().equals("4")){
                cidao++;
            }

            // LogUtil.e("hh",callName.getsNo().getUsername()+" ");
        }

        mCallnameDada.setQingjia(Integer.toString(qingjia));
        mCallnameDada.setCidao(Integer.toString(cidao));
        mCallnameDada.setCuqing(Integer.toString(cuqing));
        mCallnameDada.setKuangke(Integer.toString(kuangke));

        refrushCount();
    }



}