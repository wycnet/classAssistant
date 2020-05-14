 package com.example.wamg1.teacher;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wamg1.teacher.bean.SubmitWork;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

 public class GradeHomeworkActivity extends AppCompatActivity implements View.OnClickListener {

     private List<SubmitWork> submitWorkList;
     private int homeworkPos;
     private BmobFile bmobFile=null;

     private TextView textSnoAndSname;
     private TextView textScore;
     private TextView textFile;
     private Button btnBack;
     private Button btnNext;
     private Button btnSure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade_homework);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent=getIntent();
        homeworkPos=intent.getIntExtra("submit_homework_pos",0);
        submitWorkList=(List<SubmitWork>) intent.getSerializableExtra("sub_homework_list");

        textFile=findViewById(R.id.text_file);
        textScore=findViewById(R.id.editText_score);
        textSnoAndSname=findViewById(R.id.text_sno_and_sname);
        btnBack=findViewById(R.id.button_back);
        btnNext=findViewById(R.id.button_next);
        btnSure=findViewById(R.id.button_sure);
        btnSure.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        textFile.setOnClickListener(this);
        
        initData();
        
    }


    //
    private void initData(){
        SubmitWork mSubmitWork=submitWorkList.get(homeworkPos);
      textSnoAndSname.setText((mSubmitWork.getsNo().getUsername()+" "+mSubmitWork.getsNo().getName()));
      textScore.setText(mSubmitWork.getScore());
      if(mSubmitWork.getSub()!=null && mSubmitWork.getSub()){
          bmobFile=mSubmitWork.getjFile();
          textFile.setText(bmobFile.getFilename());
      }


    }

    //保存当前分数
    private void saveData(){
        String score=submitWorkList.get(homeworkPos).getScore();
        String value=textScore.getText().toString();
        if(!TextUtils.isEmpty(textScore.getText()) && !value.equals(score)){
            submitWorkList.get(homeworkPos).setScore(value);
            SubmitWork mSubmitWork=new SubmitWork();
            mSubmitWork.setScore(value);
            mSubmitWork.update(submitWorkList.get(homeworkPos).getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {

                }
            });

        }
    }
     @Override
     protected void onDestroy() {//保存分数
         super.onDestroy();
         saveData();
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

     @Override
     public void onClick(View v) {
         switch (v.getId()){
             case R.id.button_back:
                 if(homeworkPos>0){
                     saveData();
                     homeworkPos--;
                     initData();
                 }else{
                     Toast.makeText(this,"已经是第一个", Toast.LENGTH_SHORT).show();
                 }
                 break;
             case R.id.button_next:
                 if(homeworkPos<submitWorkList.size()-1){
                     saveData();
                     homeworkPos++;
                     initData();
                 }else{
                     Toast.makeText(this, "已经是最后一个", Toast.LENGTH_SHORT).show();
                 }
                 break;
             case R.id.button_sure:
                 finish();
                 break;
             case R.id.text_file:
                 if(bmobFile==null){
                     return;
                 }
                 Intent intent=new Intent(this,FileViewActivity.class);
                 intent.putExtra("file_url",bmobFile.getFileUrl());
                 startActivity(intent);
                 break;
         }
     }
 }
