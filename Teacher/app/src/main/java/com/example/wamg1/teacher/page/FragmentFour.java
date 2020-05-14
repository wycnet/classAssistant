package com.example.wamg1.teacher.page;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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

import com.example.wamg1.teacher.QuestionAnsowerActivity;
import com.example.wamg1.teacher.R;
import com.example.wamg1.teacher.SendNoticeActivity;
import com.example.wamg1.teacher.bean.Class;
import com.example.wamg1.teacher.bean.Notice;
import com.example.wamg1.teacher.bean.Question;
import com.example.wamg1.teacher.dataAdapter.NoticeAdapter;
import com.example.wamg1.teacher.dataAdapter.QuestionAdapter;
import com.example.wamg1.teacher.logTest.LogUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class FragmentFour extends Fragment {

    private String classId;
    public List<Notice> noticeList=new ArrayList<>();
    public NoticeAdapter noticeAdapter;
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
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.class_page_four_notice, container, false);

        floatingActionButton=view.findViewById(R.id.fab_create_notice);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), SendNoticeActivity.class);
                intent.putExtra("class_id",classId);
                startActivity(intent);
            }
        });

        recyclerView=view.findViewById(R.id.recycler_view_notice);
        layoutManager=new GridLayoutManager(view.getContext(),1);
        recyclerView.setLayoutManager(layoutManager);
        noticeAdapter=new NoticeAdapter(noticeList);
        recyclerView.setAdapter(noticeAdapter);
        initNotice();
        swipeRefresh=view.findViewById(R.id.swipe_refresh_notice);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initNotice();
            }
        });

        noticeAdapter.setOnItemClickListener(new NoticeAdapter.OnItemOnClickListener() {

            @Override
            public void onItemOnClick(View view, int pos) {

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


    //填充数据
    private void initNotice() {
        //读取数据
        BmobQuery<Notice> query = new BmobQuery<>();
        Class mClass=new Class();
        mClass.setObjectId(this.classId);
        //query.addWhereMatchesQuery("cNo","Class",innerQuery);
        query.addWhereEqualTo("cNo",mClass);
        query.order("-createdAt");
       // query.include("cNo");
        query.setLimit(500);
        //执行查询方法
        query.findObjects(new FindListener<Notice>() {
            @Override
            public void done(List<Notice> object, BmobException e) {
                if (e == null) {
                    LogUtil.e("aa","数据:"+object.size()+"条");
                    noticeList.clear();
                    noticeList.addAll(object);
                    noticeAdapter.notifyDataSetChanged();
                    swipeRefresh.setRefreshing(false);
                } else {
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });

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
        Notice notice=noticeList.get(pos);
        notice.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    // Log.i("bmob","成功");
                    Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                    noticeAdapter.removeItem(pos);
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                    Toast.makeText(getContext(), "删除失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
