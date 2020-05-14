package com.example.wamg1.student.page;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.example.wamg1.student.R;
import com.example.wamg1.student.SubmitHomeworkActivity;
import com.example.wamg1.student.bean.Class;
import com.example.wamg1.student.bean.ClassStudent;
import com.example.wamg1.student.bean.Homework;
import com.example.wamg1.student.bean.Student;
import com.example.wamg1.student.bean.SubmitWork;
import com.example.wamg1.student.dataAdapter.HomeworkAdapter;
import com.example.wamg1.student.httpOperate.HomeworkListData;
import com.example.wamg1.student.logTest.LogUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;


public class FragmentThree extends Fragment {

    private String classId;
    private List<HomeworkListData> homeworkList=new ArrayList<>();
    private HomeworkAdapter homeworkAdapter;

    private SwipeRefreshLayout swipeRefresh;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ClassPager root = (ClassPager) getActivity();
        classId = root.classId;
        LogUtil.e("aa", this.classId);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.class_page_shree_homework, container, false);
        // TextView txt_content = (TextView) view.findViewById(R.id.txt_content);
        // txt_content.setText("第一个Fragment");


        RecyclerView recyclerView=view.findViewById(R.id.recycler_view_homework);

        GridLayoutManager layoutManager=new GridLayoutManager(view.getContext(),1);
        //layoutManager=new LinearLayoutManager(view.getContext());
        LogUtil.d("aa",this.classId);
        recyclerView.setLayoutManager(layoutManager);
        homeworkAdapter=new HomeworkAdapter(homeworkList);
        recyclerView.setAdapter(homeworkAdapter);

        swipeRefresh=view.findViewById(R.id.swipe_refresh_homework);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        refreshHomework();
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initHomework();
            }
        });
        //this.view=view;

        homeworkAdapter.setOnItemClickListener(new HomeworkAdapter.OnItemOnClickListener() {
            @Override
            public void onItemOnClick(View view, int pos) {
                Intent intent=new Intent(getContext(),SubmitHomeworkActivity.class);
                intent.putExtra("class_id",classId);
                intent.putExtra("homework_id",homeworkList.get(pos).getHomeworkId());
                startActivity(intent);
            }

            @Override
            public void onItemLongOnClick(View view, int pos) {

            }
        });
        return view;
    }





    //刷新数据
    private void refreshHomework(){
        swipeRefresh.setRefreshing(true);
        initHomework();
    }
    //
    private void initHomework() {
        BmobQuery<Homework> query = new BmobQuery<>();
        Class mClass=new Class();
        mClass.setObjectId(this.classId);
        query.addWhereEqualTo("cNo", mClass);
        //返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(500);
        //执行查询方法
        query.findObjects(new FindListener<Homework>() {
            @Override
            public void done(List<Homework> object, BmobException e) {
                if(e==null){
                    HomeworkListData homeworkListData;
                    homeworkList.clear();
                    //toast("查询成功：共"+object.size()+"条数据。");
                    for (Homework mHomework : object) {
                        homeworkListData=new HomeworkListData();
                        homeworkListData.setTitle(mHomework.gethName());
                        homeworkListData.setHomeworkId(mHomework.getObjectId());
                        homeworkListData.setSub(false);
                        homeworkList.add(homeworkListData);
                    }
                    submitState(classId,homeworkList);

                    homeworkAdapter.notifyDataSetChanged();
                    swipeRefresh.setRefreshing(false);
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }

    private void submitState(String classId, final List<HomeworkListData> homeworkList) {
        Student student= BmobUser.getCurrentUser(Student.class);
        Homework mHomework=null;
        int pos=0;
        for(final HomeworkListData homeworkListData: homeworkList){
            mHomework=new Homework();
            mHomework.setObjectId(homeworkListData.getHomeworkId());
            BmobQuery<SubmitWork> query=new BmobQuery<>();
            query.addWhereEqualTo("sNo",student);
            query.addWhereEqualTo("hNo",mHomework);
            query.setLimit(1);
            final int finalPos = pos;
            query.findObjects(new FindListener<SubmitWork>() {
                @Override
                public void done(List<SubmitWork> list, BmobException e) {
                    if(e==null){
                       if((list.size()==1) && (list.get(0).getSub())){
                           homeworkListData.setSub(true);
                           homeworkListData.setScore(list.get(0).getScore());
                           homeworkAdapter.notifyItemChanged(finalPos);
                       }
                    }else{
                        Toast.makeText(getActivity(), homeworkListData.getTitle()+"加载出错", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            pos++;
        }
    }


}
