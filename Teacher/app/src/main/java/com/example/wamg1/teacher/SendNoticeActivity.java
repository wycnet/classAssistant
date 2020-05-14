package com.example.wamg1.teacher;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wamg1.teacher.bean.Class;
import com.example.wamg1.teacher.bean.Notice;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class SendNoticeActivity extends AppCompatActivity {
    private String classId;
    private EditText editTextTitle;
    private EditText editTextNotice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_notice);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent=getIntent();
        classId=intent.getStringExtra("class_id");

        editTextNotice=findViewById(R.id.editText_notice);
        editTextTitle=findViewById(R.id.editText_title);

        Button btnSure=findViewById(R.id.btn_sure);
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(editTextTitle.getText())){
                    Toast.makeText(SendNoticeActivity.this, "请输入标题", Toast.LENGTH_SHORT).show();
                    return;
                }else if(TextUtils.isEmpty(editTextNotice.getText())){
                    Toast.makeText(SendNoticeActivity.this, "请输入内容", Toast.LENGTH_SHORT).show();
                    return;
                }
                String title=editTextTitle.getText().toString();
                String notice=editTextNotice.getText().toString();
                Class mClass=new Class();
                mClass.setObjectId(classId);
                Notice mNotice=new Notice();
                mNotice.setcNo(mClass);
                mNotice.setNotice(notice);
                mNotice.setTitle(title);
                mNotice.save(new SaveListener<String>() {

                    @Override
                    public void done(String objectId, BmobException e) {
                        if(e==null){
                            Toast.makeText(SendNoticeActivity.this, "发送成功!", Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                            Toast.makeText(SendNoticeActivity.this, "发送失败!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
