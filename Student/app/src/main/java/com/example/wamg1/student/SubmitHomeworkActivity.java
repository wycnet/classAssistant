package com.example.wamg1.student;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wamg1.student.bean.Ansower;
import com.example.wamg1.student.bean.Class;
import com.example.wamg1.student.bean.Homework;
import com.example.wamg1.student.bean.Student;
import com.example.wamg1.student.bean.SubmitWork;
import com.example.wamg1.student.httpOperate.UriToPathUtil;
import com.example.wamg1.student.logTest.LogUtil;
import com.example.wamg1.student.uiTools.DownloadProgressButton;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class SubmitHomeworkActivity extends AppCompatActivity implements View.OnClickListener{
    private String classId;
    private String homeworkId;
    private TextView textTitle;
    private TextView textFile;
    private DownloadProgressButton downloadButton;
    private BmobFile homeworkFile=null;
    private File saveFile=null;

    private TextView textUploadFile;
    private ProgressBar progressBar;
    private Button btnUpload;
    private Button btnOpenFile;
    private String filePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_homework);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

        Intent intent=getIntent();
        classId=intent.getStringExtra("class_id");
        homeworkId=intent.getStringExtra("homework_id");

        textUploadFile=findViewById(R.id.text_file_upload);
        btnOpenFile=findViewById(R.id.button_open_file);
        btnUpload=findViewById(R.id.button_upload_file);
        progressBar=findViewById(R.id.progressBar);
        btnUpload.setOnClickListener(this);
        btnOpenFile.setOnClickListener(this);

        textTitle=findViewById(R.id.text_title);
        textFile=findViewById(R.id.text_file);
        downloadButton=findViewById(R.id.button_download);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager
                .PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }

        initHomework();

        downloadButton.setEnablePause(false);
        downloadButton.setOnDownLoadClickListener(new DownloadProgressButton.OnDownLoadClickListener() {
            @Override
            public void clickDownload() {
                //下载
                downloadFile(homeworkFile);
            }

            @Override
            public void clickPause() { //暂停
                //sub.unsubscribe();
            }

            @Override
            public void clickResume() {
                clickDownload();
            }

            @Override
            public void clickFinish() {//完成
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setAction(Intent.ACTION_VIEW);
                Uri uri=Uri.fromFile(saveFile);
                String fileName=homeworkFile.getFilename();
                String type="application/"+fileName.substring(fileName.lastIndexOf(".")+1);
                intent.setDataAndType(uri, "*/*");
                startActivity(intent);
                Intent.createChooser(intent, "请选择对应的软件打开该附件！");
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_open_file:
                openFileExplor();
                break;
            case R.id.button_upload_file:
                createAnsower();
                break;

        }
    }


    //新建作业
    private void createAnsower() {
        if(filePath==null){
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
                    homework.setObjectId(homeworkId);
                    Student student=BmobUser.getCurrentUser(Student.class);
                    final SubmitWork submitWork=new SubmitWork();
                    submitWork.sethNo(homework);
                    submitWork.setsNo(student);
                    submitWork.setjFile(file);
                    submitWork.setSub(true);
                    submitWork.setScore(null);
                    //先检查是否存在
                    BmobQuery<SubmitWork> query = new BmobQuery<SubmitWork>();
                    query.addWhereEqualTo("sNo", student);
                    query.addWhereEqualTo("hNo",homework);
                    //返回50条数据，如果不加上这条语句，默认返回10条数据
                    query.setLimit(1);
                    //执行查询方法
                    query.findObjects(new FindListener<SubmitWork>() {
                        @Override
                        public void done(List<SubmitWork> object, BmobException e) {
                            if(e==null){
                                if(object.size()!=0){//已存在
                                    submitWork.update(object.get(0).getObjectId(), new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                           if(e==null){
                                               Toast.makeText(SubmitHomeworkActivity.this, "更新成功！", Toast.LENGTH_SHORT).show();
                                           }else{
                                               Toast.makeText(SubmitHomeworkActivity.this, "更新失败", Toast.LENGTH_SHORT).show();
                                               Log.e("done:submit","更新失败" );
                                           }
                                        }
                                    });
                                }else{//不存在
                                    submitWork.save(new SaveListener<String>() {

                                        @Override
                                        public void done(String objectId, BmobException e) {
                                            if(e==null){
                                                Toast.makeText(SubmitHomeworkActivity.this, "提交成功！", Toast.LENGTH_SHORT).show();
                                            }else{
                                                Log.i("bmob:update","失败："+e.getMessage()+","+e.getErrorCode());
                                                Toast.makeText(SubmitHomeworkActivity.this, "提交失败", Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });
                                }
                            }else{
                                Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                                Toast.makeText(SubmitHomeworkActivity.this, "出错！", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }else{
                    // toast("上传文件失败：" + e.getMessage());
                    Toast.makeText(SubmitHomeworkActivity.this, "上传文件失败", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onProgress(Integer value) {
                // 返回的上传进度（百分比）
                progressBar.setProgress(value);
                btnUpload.setClickable(false);
                if(value==0||value==100){
                    progressBar.setVisibility(View.INVISIBLE);
                }else {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        });



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
            filePath= UriToPathUtil.getRealFilePath(this,uri);
            textUploadFile.setText(filePath.substring(filePath.lastIndexOf("/")));
            //File file = new File(filePath);
            //Toast.makeText(this, file.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    //下载作业
    private void  downloadFile(BmobFile file){
        if(homeworkFile==null){
            return;
        }
        //允许设置下载文件的存储路径，默认下载文件的目录为：context.getApplicationContext().getCacheDir()+"/bmob/"
        saveFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .getPath(), file.getFilename());
        Toast.makeText(this, ""+saveFile.getPath(), Toast.LENGTH_SHORT).show();
        file.download(saveFile, new DownloadFileListener() {
            @Override
            public void onStart() {
                //toast("开始下载...");
            }

            @Override
            public void done(String savePath,BmobException e) {
                if(e==null){
                    //toast("下载成功,保存路径:"+savePath);
                }else{
                   //toast("下载失败："+e.getErrorCode()+","+e.getMessage());
                    LogUtil.e("下载",e.getMessage()+" "+e.getErrorCode());
                }
            }

            @Override
            public void onProgress(Integer value, long newworkSpeed) {
               // Log.i("bmob","下载进度："+value+","+newworkSpeed);
                downloadButton.setProgress(value);
            }

        });
    }


    //加载作业
    private void initHomework(){
        BmobQuery<Homework> query = new BmobQuery<>();
        query.getObject(homeworkId, new QueryListener<Homework>() {

            @Override
            public void done(Homework object, BmobException e) {
                if(e==null){
                    homeworkFile=object.getFile();
                    textTitle.setText(object.gethName());
                    textFile.setText(homeworkFile.getFilename());
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                    Toast.makeText(SubmitHomeworkActivity.this, "加载数据失败", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }


   //加载答案
    private void initAnsower(){
        Homework mHomework=new Homework();
        mHomework.setObjectId(homeworkId);
        Student student= BmobUser.getCurrentUser(Student.class);
        BmobQuery<Ansower> query = new BmobQuery<>();
        query.addWhereEqualTo("hNo", mHomework);
        query.addWhereEqualTo("sNo",student);
        //返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(1);
        //执行查询方法
        query.findObjects(new FindListener<Ansower>() {
            @Override
            public void done(List<Ansower> object, BmobException e) {
                if(e==null){
                    //toast("查询成功：共"+object.size()+"条数据。");
                   //如果存在
                    if(object.size()!=0){

                    }
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0 && grantResults[0]!=PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "拒绝权限将无法使用！", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;

        }
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

