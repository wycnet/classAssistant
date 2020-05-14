package com.example.wamg1.teacher;

import android.app.Fragment;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.wamg1.teacher.bean.Comment;
import com.example.wamg1.teacher.bean.Student;
import com.example.wamg1.teacher.bean.Talk;
import com.example.wamg1.teacher.dataAdapter.CommentAdapter;
import com.example.wamg1.teacher.dataAdapter.TalkAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class  TalkCommentActivity extends AppCompatActivity {

    private Talk talk=null;

    public List<Comment> commentList=new ArrayList<>();
    public CommentAdapter commentAdapter;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private StaggeredGridLayoutManager layoutManager;

    private EditText textInpot;
    private View btnSend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk_comment);

        Intent intent=getIntent();
        talk=(Talk)intent.getSerializableExtra("talk_data");

        textInpot=findViewById(R.id.text_input);
        btnSend=findViewById(R.id.btn_send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(textInpot.getText())){
                    sendComment();
                }
            }
        });
        TextView textName=findViewById(R.id.text_name);
        TextView textTime=findViewById(R.id.text_time);
        TextView textTalk=findViewById(R.id.text_talk);

        if(talk!=null){
            if(talk.getUser()!=null){
                textName.setText(talk.getUser().getName());
            }
            textTime.setText(talk.getCreatedAt());
            textTalk.setText(talk.getTalk());
        }


        recyclerView=findViewById(R.id.recycler_view_comment);
        layoutManager=new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(layoutManager);
        commentAdapter=new CommentAdapter(commentList);
        recyclerView.setAdapter(commentAdapter);

        initComment();
        swipeRefresh=findViewById(R.id.swipe_refresh_comment);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initComment();
            }
        });


    }

    private void sendComment() {
        String sendComment=textInpot.getText().toString();
        Student student= BmobUser.getCurrentUser(Student.class);
        Talk mTalk=new Talk();
        mTalk.setObjectId(talk.getObjectId());
        Comment comment=new Comment();
        comment.setTalk(mTalk);
        comment.setUser(student);
        comment.setComment(sendComment);
        comment.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    textInpot.setText(null);
                    initComment();
                }else{

                }
            }
        });
    }

    private void initComment(){
        Talk mTalk=new Talk();
        mTalk.setObjectId(talk.getObjectId());
        BmobQuery<Comment> query = new BmobQuery<>();
        query.addWhereEqualTo("talk", mTalk);
        query.include("user[name]");
        //返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(500);
        //执行查询方法
        query.findObjects(new FindListener<Comment>() {
            @Override
            public void done(List<Comment> object, BmobException e) {
                if(e==null){
                    commentList.clear();
                    commentList.addAll(object);
                    commentAdapter.notifyDataSetChanged();
                    swipeRefresh.setRefreshing(false);
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                    swipeRefresh.setRefreshing(false);

                }
            }
        });
    }

}
