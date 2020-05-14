package com.example.wamg1.student.page;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wamg1.student.R;
import com.example.wamg1.student.TalkCommentActivity;
import com.example.wamg1.student.bean.Class;
import com.example.wamg1.student.bean.Student;
import com.example.wamg1.student.bean.Talk;
import com.example.wamg1.student.dataAdapter.TalkAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class FragmentFive extends Fragment {
    private String classId;
    public List<Talk> talkList=new ArrayList<>();
    public TalkAdapter talkAdapter;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private StaggeredGridLayoutManager layoutManager;
    private View view;
    private FloatingActionButton floatingActionButton;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ClassPager root = (ClassPager) getActivity();
        classId = root.classId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.class_page_five_talk, container, false);

        floatingActionButton=view.findViewById(R.id.fab_create_talk);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTalkDialog(v);
            }
        });

        recyclerView=view.findViewById(R.id.recycler_view_talk);
        layoutManager=new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        talkAdapter=new TalkAdapter(talkList);
        recyclerView.setAdapter(talkAdapter);



        initTalk();
        swipeRefresh=view.findViewById(R.id.swipe_refresh_talk);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initTalk();
            }
        });

        talkAdapter.setOnItemClickListener(new TalkAdapter.OnItemOnClickListener() {

            @Override
            public void onItemOnClick(View view, int pos) {
                Talk talk=talkList.get(pos);
                Intent intent=new Intent(getContext(), TalkCommentActivity.class);
                intent.putExtra("talk_data",talk);
                startActivity(intent);
            }
            @Override
            public void onItemLongOnClick(View view, int pos) {

            }


        });
        this.view=view;
        return view;
    }


    //弹出创建班级窗口
    public void createTalkDialog(View view){
        final ConstraintLayout layout= (ConstraintLayout) getLayoutInflater().inflate(R.layout.create_talk_dialog,null);
        AlertDialog.Builder dialog=new AlertDialog.Builder(getContext())
                .setTitle("新建问题")
                .setView(layout)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TextView textClassName=layout.findViewById(R.id.text_class_name);
                        if(TextUtils.isEmpty(textClassName.getText())){
                            Toast.makeText(getContext(), "请输入问题", Toast.LENGTH_SHORT).show();

                        }else{
                            createTalk(textClassName.getText().toString());
                        }
                    }
                });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.show();
    }
    private void createTalk(String talkQuestion) {

        Talk talk=new Talk();
        Student student= BmobUser.getCurrentUser(Student.class);
        Class mClass=new Class();
        mClass.setObjectId(classId);
        talk.setcNo(mClass);
        talk.setUser(student);
        talk.setTalk(talkQuestion);
        talk.save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                if(e==null){
                    //toast("创建数据成功：" + objectId);
                    initTalk();
                    Toast.makeText(getContext(), "创建成功!", Toast.LENGTH_SHORT).show();
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                    Toast.makeText(getContext(), "创建失败!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initTalk() {
        Class mClass=new Class();
        mClass.setObjectId(classId);
        BmobQuery<Talk> query = new BmobQuery<>();
        //查询playerName叫“比目”的数据
        query.addWhereEqualTo("cNo", mClass);
        query.include("user");
        query.order("createdAt");
        //返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(100);
        //执行查询方法
        query.findObjects(new FindListener<Talk>() {
            @Override
            public void done(List<Talk> object, BmobException e) {
                if(e==null){
                    talkList.clear();
                    talkList.addAll(object);
                    talkAdapter.notifyDataSetChanged();
                    swipeRefresh.setRefreshing(false);
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                    swipeRefresh.setRefreshing(false);

                }
            }
        });
    }


}
