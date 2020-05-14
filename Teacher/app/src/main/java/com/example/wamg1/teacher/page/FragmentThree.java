package com.example.wamg1.teacher.page;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.example.wamg1.teacher.CheckHomework;
import com.example.wamg1.teacher.HomeworkPickActivity;
import com.example.wamg1.teacher.R;
import com.example.wamg1.teacher.bean.Class;
import com.example.wamg1.teacher.bean.ClassStudent;
import com.example.wamg1.teacher.bean.Homework;
import com.example.wamg1.teacher.bean.SubmitWork;
import com.example.wamg1.teacher.dataAdapter.HomeworkAdapter;
import com.example.wamg1.teacher.dataAdapter.QuestionAdapter;
import com.example.wamg1.teacher.httpOperate.HomeworkListData;
import com.example.wamg1.teacher.logTest.LogUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
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

        //新加按钮
        FloatingActionButton fab = view.findViewById(R.id.fab_homework);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(),HomeworkPickActivity.class);
                intent.putExtra("class_id",classId);
                startActivity(intent);
            }
        });

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
                Intent intent=new Intent(getContext(), CheckHomework.class);
                intent.putExtra("homework_id",homeworkList.get(pos).getHomeworkId());
                startActivity(intent);
            }

            @Override
            public void onItemLongOnClick(View view, int pos) {
                showPopMenu(view,pos);
            }
        });
        return view;
    }


    //长按弹出菜单
    public void showPopMenu(View view, final int pos) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_item_delete, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                //callnameAdapter.removeItem(pos);
                deleteHomework(pos);
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

    private void deleteHomework(final int pos) {
        //删除操作
        Homework homework=new Homework();
        homework.setObjectId(homeworkList.get(pos).getHomeworkId());
        homework.delete(new UpdateListener() {

            @Override
            public void done(BmobException e) {
                if(e==null){
                    //Log.i("bmob","成功");
                    homeworkAdapter.removeItem(pos);
                    Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
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
                        homeworkList.add(homeworkListData);
                    }
                    countStudent(classId,homeworkList);

                    homeworkAdapter.notifyDataSetChanged();
                    swipeRefresh.setRefreshing(false);
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }

    private void countYijiao(final Homework homework, final HomeworkListData homeworkListData, final int countStu, final int position){
        BmobQuery<SubmitWork> query = new BmobQuery<>();
        query.addWhereEqualTo("hNo", homework);
        query.addWhereEqualTo("isSub",true);
        query.count(SubmitWork.class, new CountListener() {
            @Override
            public void done(Integer count, BmobException e) {
                if(e==null){
                    //toast("count对象个数为："+count);
                    homeworkListData.setYijiaCount(count.toString());
                    int countWeijiao=countStu-(count.intValue());
                    homeworkListData.setWeijiaoCount(Integer.toString(countWeijiao));
                    //homeworkAdapter.notifyDataSetChanged();
                    homeworkAdapter.notifyItemChanged(position);
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }

    private void countStudent(String classId, final List<HomeworkListData> homeworkList){
        Class aClass=new Class();
        aClass.setObjectId(classId);
        BmobQuery<ClassStudent> query = new BmobQuery<>();
        query.addWhereEqualTo("cNo", aClass);
        query.count(ClassStudent.class, new CountListener() {
            @Override
            public void done(Integer count, BmobException e) {
                if(e==null){
                    //toast("count对象个数为："+count);
//                    int cuntStu=count;
//                    countYijiao(homework,homeworkListData,cuntStu);
                    int position=0;
                    for(HomeworkListData listData:homeworkList){
                        Homework mHomework=new Homework();
                        mHomework.setObjectId(listData.getHomeworkId());
                        //统计人数
                        countYijiao(mHomework,listData,count,position);
                        position++;
                    }
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }


}
