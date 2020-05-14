package com.example.wamg1.teacher;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wamg1.teacher.bean.Class;
import com.example.wamg1.teacher.bean.ClassStudent;
import com.example.wamg1.teacher.bean.Homework;
import com.example.wamg1.teacher.bean.SubmitWork;
import com.example.wamg1.teacher.httpOperate.UriToPathUtil;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

public class HomeworkPickActivity extends AppCompatActivity implements View.OnClickListener{
    private String classId;
    private TextView textFileUrl;
    private TextView textTitle;
    private String filePath;
    private ProgressBar progressBarSendFile;

    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homework_pick);

        Intent intent=getIntent();
        classId=intent.getStringExtra("class_id");

        progressBarSendFile=findViewById(R.id.progressBar_sendfile);
        progressBarSendFile.setVisibility(View.INVISIBLE);
        textTitle=findViewById(R.id.text_homework);
        textFileUrl=findViewById(R.id.text_file_url);
        Button btnPickLocalFile=findViewById(R.id.button_localFile);
        Button btnPickNetFile=findViewById(R.id.button_netFile);
        btnSubmit=findViewById(R.id.button_submit);

        btnPickLocalFile.setOnClickListener(this);
        btnPickNetFile.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
    }





    //新建作业
    private void createHomework() {
        if(filePath==null){
            return;
        }
        if(TextUtils.isEmpty(textTitle.getText())){
            Toast.makeText(this, "请输入题目", Toast.LENGTH_SHORT).show();
            return;
        }
        File mfile=new File(filePath);
        final BmobFile file=new BmobFile(mfile);
        //上传文件
        file.uploadblock(new UploadFileListener() {

            @Override
            public void done(BmobException e) {
                if(e==null){
                    //bmobFile.getFileUrl()--返回的上传文件的完整地址
                   // toast("上传文件成功:" + bmobFile.getFileUrl());
                    // BmobFile bmobFile=file.getFileUrl();
                    Homework homework=new Homework();
                    Class mClass=new Class();
                    mClass.setObjectId(classId);
                    homework.setcNo(mClass);
                    homework.setFile(file);
                    homework.sethName(textTitle.getText().toString());
                    //注意：不能调用gameScore.setObjectId("")方法
                    homework.save(new SaveListener<String>() {
                        @Override
                        public void done(String objectId, BmobException e) {
                            if(e==null){
                                //toast("创建数据成功：" + objectId);
                                Toast.makeText(HomeworkPickActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                                createStudentHomework(objectId);//创建学生答题记录
                                btnSubmit.setClickable(true);
                                progressBarSendFile.setVisibility(View.INVISIBLE);
                            }else{
                                Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                            }
                        }
                    });
                }else{
                   // toast("上传文件失败：" + e.getMessage());
                    Toast.makeText(HomeworkPickActivity.this, "上传文件失败", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onProgress(Integer value) {
                // 返回的上传进度（百分比）
                progressBarSendFile.setProgress(value);
                btnSubmit.setClickable(false);
                if(value==0||value==100){
                    progressBarSendFile.setVisibility(View.INVISIBLE);
                }else {
                    progressBarSendFile.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    //为每个学生添加交作业记录
    private void createStudentHomework(String homeworkId){
        final Homework homework=new Homework();
        homework.setObjectId(homeworkId);
        final List<BmobObject> submitWorkList=new ArrayList<>();

        //查询本班所有学生
        Class mClass=new Class();
        mClass.setObjectId(classId);
        BmobQuery<ClassStudent> query=new BmobQuery<>();
        query.addWhereEqualTo("cNo",mClass);
        query.include("sNo");
        query.setLimit(200);
        query.findObjects(new FindListener<ClassStudent>() {
            @Override
            public void done(List<ClassStudent> list, BmobException e) {
                if(e==null){
                    //查询成功
                    SubmitWork submitWork;
                   for(ClassStudent cs:list){
                       submitWork=new SubmitWork();
                       submitWork.setsNo(cs.getsNo());
                       submitWork.sethNo(homework);
                       submitWork.setSub(false);
                       submitWorkList.add(submitWork);
                   }
                   //然后批量新建
                    new BmobBatch().insertBatch(submitWorkList).doBatch(new QueryListListener<BatchResult>() {
                        @Override
                        public void done(List<BatchResult> o, BmobException e) {
                            if(e==null){
                                //新建完成

                            }else{
                                Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                            }
                        }
                    });
                }else{

                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_localFile:
                if(ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }else {
                    openFileExplor();
                }
                break;
            case R.id.button_netFile:

                break;
            case R.id.button_submit:
                createHomework();
                break;
        }

    }



    private void openFileExplor(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {//是否选择，没选择就不会继续
            Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
            filePath=UriToPathUtil.getRealFilePath(this,uri);
            textFileUrl.setText(filePath);
            //File file = new File(filePath);
            //Toast.makeText(this, file.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    openFileExplor();
                }else{
                    Toast.makeText(this, "你取消了权限申请!", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }
}
