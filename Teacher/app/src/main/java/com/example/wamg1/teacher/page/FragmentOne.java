package com.example.wamg1.teacher.page;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.example.wamg1.teacher.QuestionAnsowerActivity;
import com.example.wamg1.teacher.R;
import com.example.wamg1.teacher.bean.Ansower;
import com.example.wamg1.teacher.bean.Class;
import com.example.wamg1.teacher.bean.ClassStudent;
import com.example.wamg1.teacher.bean.Question;
import com.example.wamg1.teacher.dataAdapter.QuestionAdapter;
import com.example.wamg1.teacher.httpOperate.QuestionGSONData;
import com.example.wamg1.teacher.httpOperate.UriToPathUtil;
import com.example.wamg1.teacher.logTest.LogUtil;
import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class FragmentOne extends Fragment {

    private String classId;
    public List<Question> qustionList=new ArrayList<>();
    public QuestionAdapter questionAdapter;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private View view;

    private FloatingActionButton floatingActionButton;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ClassPager root = (ClassPager) getActivity();
        classId = root.classId;
        //LogUtil.e("aa", this.classId);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.class_page_one_question, container, false);

        floatingActionButton=view.findViewById(R.id.fab_create_question);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        recyclerView=view.findViewById(R.id.recycler_view_qusetion);
        layoutManager=new GridLayoutManager(view.getContext(),1);
        //layoutManager=new LinearLayoutManager(view.getContext());
        //LogUtil.d("aa",this.classId);
        recyclerView.setLayoutManager(layoutManager);
        questionAdapter=new QuestionAdapter(qustionList);
        recyclerView.setAdapter(questionAdapter);
        refreshQuestion();
        swipeRefresh=view.findViewById(R.id.swipe_refresh_question);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshQuestion();
            }
        });

        questionAdapter.setOnItemClickListener(new QuestionAdapter.OnItemOnClickListener() {

            @Override
            public void onItemOnClick(View view, int pos) {
                Question question=qustionList.get(pos);
                Intent intent=new Intent(getContext(), QuestionAnsowerActivity.class);
                intent.putExtra("question_id",question.getObjectId());
                intent.putExtra("question",question.getqFile());
                LogUtil.e("bb",question.getqFile()+" ;"+question.getObjectId());
                startActivity(intent);
            }
            @Override
            public void onItemLongOnClick(View view, int pos) {
                //长按删除
                showPopMenu(view,pos);
            }


        });
        this.view=view;
        return view;
    }

    //弹出dialog
    private void showDialog() {
        AlertDialog.Builder dialog=new AlertDialog.Builder(getContext())
                .setTitle("发布问题")
                .setMessage("发布一个新问题？");
        dialog.setNegativeButton("否",null);
        dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }else {
                    openFileExplor();
                }
                //swipeRefresh.setRefreshing(true);
            }
        });
        dialog.show();
    }


    private void openFileExplor(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent,1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {//是否选择，没选择就不会继续
            Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
            String filePath= UriToPathUtil.getRealFilePath(getContext(),uri);
            sureCreate(filePath);
            //File file = new File(filePath);
            //Toast.makeText(this, file.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private String codeCharset(String fileName) throws IOException{
        BufferedInputStream in=new BufferedInputStream(new FileInputStream(fileName));
        int p=(in.read()<<8)+in.read();
        String code=null;
        switch (p){
            case 0xefbb:
                code="UTF-8";
                break;
            case 0xfffe:
                code="Unicode";
                break;
            case 0xfeff:
                code="UTF-16BE";
                break;
                default:
                    code="GBK";
                    break;
        }
        return code;
    }

    private void sureCreate(final String filePath) {
        new AlertDialog.Builder(getContext())
        .setTitle("确定发布？")
        .setMessage(filePath.substring(filePath.lastIndexOf("/")+1))
        .setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //发布问题的代码
                File file=new File(filePath);
                QuestionGSONData questionGSONData=new QuestionGSONData();
                Scanner scan=null;
                try {
                    scan=new Scanner(new FileInputStream(file),codeCharset(filePath));
                    String line="";
                    while(scan.hasNext()){
                        if((line=scan.nextLine())!=null){
                            if(line.trim().length()==1){
                                questionGSONData.setAnsower(line.trim());
                            }else if(line.trim().charAt(0)=='A'){
                                questionGSONData.setOptionA(line.trim());
                            }else if(line.trim().charAt(0)=='B'){
                                questionGSONData.setOptionB(line.trim());
                            }else if(line.trim().charAt(0)=='C'){
                                questionGSONData.setOptionC(line.trim());
                            }else if(line.trim().charAt(0)=='D'){
                                questionGSONData.setOptionD(line.trim());
                            }else if(line.trim().length()>1){
                                questionGSONData.setTitle(line.trim());
                            }
                        }
                        LogUtil.e("qq",line);
                    }

                    //生成GSON
                    String questionGson=new Gson().toJson(questionGSONData);
                    createQuestion(questionGson);
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if(scan!=null)
                    scan.close();
                }
            }
        })
        .setNegativeButton("否", null)
        .create()
        .show();
    }

    //创建问题
    private void createQuestion(String questionGson) {
        Question mQuestion=new Question();
        mQuestion.setqFile(questionGson);
        final Class mClass=new Class();
        mClass.setObjectId(classId);
        mQuestion.setcNo(mClass);
        mQuestion.save(new SaveListener<String>() {
            @Override
            public void done(final String objectId, BmobException e) {
                if(e==null){
                    //toast("创建数据成功：" + objectId);
                    //查询该班的所有学生
                    BmobQuery<ClassStudent> query = new BmobQuery<>();
                    query.addWhereEqualTo("cNo", mClass);
                    //返回50条数据，如果不加上这条语句，默认返回10条数据
                    query.setLimit(500);
                    //执行查询方法
                    query.findObjects(new FindListener<ClassStudent>() {
                        @Override
                        public void done(List<ClassStudent> object, BmobException e) {
                            if(e==null){
                                //toast("查询成功：共"+object.size()+"条数据。");
                                //查到班级学后为每个学生创建答题记录
                                List<BmobObject> ansowers=new ArrayList<>();
                                Ansower mAnsower;
                                Question question;
                                question=new Question();
                                question.setObjectId(objectId);
                                for(ClassStudent cs:object){
                                    mAnsower=new Ansower();
                                    mAnsower.setsNo(cs.getsNo());
                                    mAnsower.setqNo(question);
                                    mAnsower.setAnswer(false);
                                    ansowers.add(mAnsower);
                                }
                                new BmobBatch().insertBatch(ansowers).doBatch(new QueryListListener<BatchResult>() {

                                    @Override
                                    public void done(List<BatchResult> o, BmobException e) {
                                        if(e==null){
                                            for(int i=0;i<o.size();i++){
                                                BatchResult result = o.get(i);
                                                BmobException ex =result.getError();
                                                if(ex==null){
                                                   // log("第"+i+"个数据批量添加成功："+result.getCreatedAt()+","+result.getObjectId()+","+result.getUpdatedAt());
                                                }else{
                                                   // log("第"+i+"个数据批量添加失败："+ex.getMessage()+","+ex.getErrorCode());
                                                }
                                            }
                                            //到此创建完成
                                            Toast.makeText(getContext(), "创建成功", Toast.LENGTH_SHORT).show();
                                            refreshQuestion();
                                            swipeRefresh.setRefreshing(true);
                                        }else{
                                            Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                                        }
                                    }
                                });
                            }else{
                                Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                            }
                        }
                    });

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
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    openFileExplor();
                }else{
                    Toast.makeText(getContext(), "你取消了权限申请!", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }


    //长按弹出菜单
    public void showPopMenu(View view, final int pos) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_item_delete, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                //callnameAdapter.removeItem(pos);
                deleteQuestion(pos);
                return false;
            }
        });
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                //Toast.makeText(getApplicationContext(), "关闭PopupMenu", Toast.LENGTH_SHORT).show();
            }
        });
        popupMenu.show();
    }

    //删除问题
    private void deleteQuestion(final int pos){
        Question mQuestion=qustionList.get(pos);
        mQuestion.delete(new UpdateListener() {

            @Override
            public void done(BmobException e) {
                if(e==null){
                   // Log.i("bmob","成功");
                    Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                    deleteAnsower(pos);
                    questionAdapter.removeItem(pos);
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                    Toast.makeText(getContext(), "删除失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //删除学生的答题记录
    private void deleteAnsower(int pos){
        Question question=qustionList.get(pos);
        //查找记录
        BmobQuery<Ansower> query = new BmobQuery<>();
        query.addWhereEqualTo("qNo", question);
        //返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(500);
        //执行查询方法
        query.findObjects(new FindListener<Ansower>() {
            @Override
            public void done(List<Ansower> object, BmobException e) {
                if(e==null){
                    //查找成功后删除
                    //第二种方式：v3.5.0开始提供
                    List<BmobObject> ansowerObject=new ArrayList<>();
                    for(Ansower ansower:object){
                        ansowerObject.add((BmobObject)ansower);
                    }
                    new BmobBatch().deleteBatch(ansowerObject).doBatch(new QueryListListener<BatchResult>() {

                        @Override
                        public void done(List<BatchResult> o, BmobException e) {
                            if(e==null){
                                for(int i=0;i<o.size();i++){
                                    BatchResult result = o.get(i);
                                    BmobException ex =result.getError();
                                    if(ex==null){
                                        //log("第"+i+"个数据批量删除成功");
                                    }else{
                                        //log("第"+i+"个数据批量删除失败："+ex.getMessage()+","+ex.getErrorCode());
                                    }
                                }
                                //到此删除彻底成功
                            }else{
                                Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                            }
                        }
                    });
                }else{
                    Toast.makeText(getContext(), "删除失败", Toast.LENGTH_SHORT).show();
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }


    //填充数据
    private void initQusetion() {
        //读取数据
        BmobQuery<Question> query = new BmobQuery<>();
        Class mClass=new Class();
        mClass.setObjectId(this.classId);
        //query.addWhereMatchesQuery("cNo","Class",innerQuery);
        query.addWhereEqualTo("cNo",mClass);
        query.order("-createdAt");
        query.include("cNo");
        query.setLimit(500);
        //执行查询方法
        query.findObjects(new FindListener<Question>() {
            @Override
            public void done(List<Question> object, BmobException e) {
                if (e == null) {
                    LogUtil.e("aa","数据:"+object.size()+"条");
                    qustionList.clear();
                    qustionList.addAll(object);
                    questionAdapter.notifyDataSetChanged();
                    swipeRefresh.setRefreshing(false);
                } else {
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });

    }

    //刷新方法
    private void refreshQuestion() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.e("aa","已执行刷新");
                        initQusetion();
                    }
                });
            }
        }).start();
    }

}