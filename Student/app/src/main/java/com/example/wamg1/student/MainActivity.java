package com.example.wamg1.student;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wamg1.student.bean.Class;
import com.example.wamg1.student.bean.ClassStudent;
import com.example.wamg1.student.bean.Student;
import com.example.wamg1.student.bean.Teacher;
import com.example.wamg1.student.dataAdapter.ClassAapter;
import com.example.wamg1.student.page.ClassPager;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ClassAapter classAapter;
    private List<Class> classList=new ArrayList<>();
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Student student=BmobUser.getCurrentUser(Student.class);
        View  headerView = navigationView.getHeaderView(0);
        TextView textUserName=headerView.findViewById(R.id.text_real_name);
        TextView textEmail=headerView.findViewById(R.id.text_email);
        textUserName.setText(student.getName());
        textEmail.setText(student.getEmail());


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinClassDialog();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();



        //以下添加代码
        classList=new ArrayList<>();
        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,1);
        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(gridLayoutManager);
        classAapter=new ClassAapter(classList);
        recyclerView.setAdapter(classAapter);

        refreshClass();
        //下拉刷新
        swipeRefresh=findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshClass();
            }
        });
        //添加监听
        classAapter.setOnItemClickListener(new ClassAapter.OnItemOnClickListener() {
            @Override
            public void onItemOnClick(View view, int pos) {
                Intent intent = new Intent(MainActivity.this, ClassPager.class);
                intent.putExtra("class_name", classList.get(pos).getcName());
                intent.putExtra("class_id", classList.get(pos).getObjectId());
                startActivity(intent);
            }
            @Override
            public void onItemLongOnClick(View view, int pos) {
                showPopMenu(view,pos);
            }
        });



    }



    //弹出加入班级窗口
    public void joinClassDialog(){
        final ConstraintLayout layout= (ConstraintLayout) getLayoutInflater().inflate(R.layout.jion_class_dialog,null);
        AlertDialog.Builder dialog=new AlertDialog.Builder(this)
                .setTitle("加入班级")
                .setView(layout)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TextView textClassName=layout.findViewById(R.id.text_class_name);
                        if(TextUtils.isEmpty(textClassName.getText())){
                            Toast.makeText(MainActivity.this, "请输入班级号", Toast.LENGTH_SHORT).show();

                        }else{
                            jionClass(textClassName.getText().toString());
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


    //加入班级
    private void jionClass(String name){

        final Student user= BmobUser.getCurrentUser(Student.class);
        BmobQuery<Class> query = new BmobQuery<>();
        query.getObject(name, new QueryListener<Class>() {

            @Override
            public void done(final Class object, BmobException e) {
                if(e==null){
                    //班级存在,创建班级学生
                    ClassStudent cs=new ClassStudent();
                    cs.setcNo(object);
                    cs.setsNo(user);
                    cs.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if(e==null){
                                Toast.makeText(MainActivity.this, "加入成功", Toast.LENGTH_SHORT).show();
                                classList.add(object);
                                classAapter.notifyDataSetChanged();
                            }else{

                            }
                        }
                    });

                }else{
                    //Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                    Toast.makeText(MainActivity.this, "加入失败，该班级不存在", Toast.LENGTH_SHORT).show();
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



    //填充数据
    private void initClass(){
        //读取数据
         Student student=BmobUser.getCurrentUser(Student.class);
        BmobQuery<ClassStudent> query = new BmobQuery<>();
        query.addWhereEqualTo("sNo",student);
        //返回50条数据，如果不加上这条语句，默认返回10条数据
        query.order("-createdAt");
        query.include("cNo");
        query.setLimit(50);
        //执行查询方法
        query.findObjects(new FindListener<ClassStudent>() {
            @Override
            public void done(List<ClassStudent> object, BmobException e) {
                if(e==null){
                    classList.clear();
                    for(ClassStudent cs:object){
                        classList.add(cs.getcNo());
                    }
                    classAapter.notifyDataSetChanged();
                    swipeRefresh.setRefreshing(false);
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });

    }

    //刷新方法
    private void refreshClass(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initClass();
                    }
                });
            }
        }).start();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent=new Intent(this,SettingActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
