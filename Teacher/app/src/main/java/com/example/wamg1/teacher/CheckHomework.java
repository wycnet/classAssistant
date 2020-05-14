package com.example.wamg1.teacher;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.wamg1.teacher.bean.Homework;
import com.example.wamg1.teacher.bean.SubmitWork;
import com.example.wamg1.teacher.dataAdapter.CallnameAdapter;
import com.example.wamg1.teacher.dataAdapter.SubHomeworkListAdapter;
import com.example.wamg1.teacher.logTest.LogUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class CheckHomework extends AppCompatActivity {
    private String homeworkId;

    private List<SubmitWork> submitWorkList=new ArrayList<>();

    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private SubHomeworkListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_homework);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Intent intent=getIntent();
        homeworkId=intent.getStringExtra("homework_id");



        recyclerView = findViewById(R.id.recycler_view_check_homework);
        layoutManager = new GridLayoutManager(this, 1);
        //layoutManager=new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new SubHomeworkListAdapter(submitWorkList);
        recyclerView.setAdapter(adapter);

        initData();
        swipeRefresh=findViewById(R.id.swipe_refresh_check_homework);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
            }
        });

        adapter.setOnItemClickListener(new SubHomeworkListAdapter.OnItemOnClickListener() {
            @Override
            public void onItemOnClick(View view, int pos) {

                Bundle bundle = new Bundle();
                bundle.putSerializable("sub_homework_list", (Serializable) submitWorkList);
                Intent intent=new Intent(CheckHomework.this,GradeHomeworkActivity.class);
                intent.putExtra("submit_homework_pos",pos);
                intent.putExtras(bundle);
                startActivity(intent);


            }

            @Override
            public void onItemLongOnClick(View view, int pos) {

            }
        });

    }



    //初始化数据
    private void initData(){
        Homework homework=new Homework();
        homework.setObjectId(homeworkId);

        BmobQuery<SubmitWork> query = new BmobQuery<>();
        query.addWhereEqualTo("hNo", homework);
        query.include("sNo,hNo");
        query.setLimit(500);
        //执行查询方法
        query.findObjects(new FindListener<SubmitWork>() {
            @Override
            public void done(List<SubmitWork> object, BmobException e) {
                if(e==null){
                    LogUtil.e("xx","查到"+object.size()+"条");
                    submitWorkList.clear();
                    submitWorkList.addAll(object);
                    adapter.notifyDataSetChanged();
                    swipeRefresh.setRefreshing(false);
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
