package com.example.wamg1.teacher;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.util.TimeUtils;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wamg1.teacher.Test.CreatTestDate;
import com.example.wamg1.teacher.bean.Class;
import com.example.wamg1.teacher.bean.ClassStudent;
import com.example.wamg1.teacher.bean.Teacher;
import com.example.wamg1.teacher.dataAdapter.ClassAapter;
import com.example.wamg1.teacher.dataAdapter.InflaterData;
import com.example.wamg1.teacher.httpOperate.ClassData;
import com.example.wamg1.teacher.page.ClassPager;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private List<Class> classList;
    private ClassAapter classAapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               //弹出Dialog输入班级名
                createClassDialog(view);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //设置信息
        Teacher teacher=BmobUser.getCurrentUser(Teacher.class);
        View  headerView = navigationView.getHeaderView(0);
        TextView textUserName=headerView.findViewById(R.id.text_real_name);
        TextView textEmail=headerView.findViewById(R.id.text_email);
        textUserName.setText(teacher.getName());
        textEmail.setText(teacher.getEmail());



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


    //弹出创建班级窗口
    public void createClassDialog(View view){
        final ConstraintLayout layout= (ConstraintLayout) getLayoutInflater().inflate(R.layout.create_class_dialog,null);
        AlertDialog.Builder dialog=new AlertDialog.Builder(this)
                .setTitle("新建班级")
                .setView(layout)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TextView textClassName=layout.findViewById(R.id.text_class_name);
                        if(TextUtils.isEmpty(textClassName.getText())){
                            Toast.makeText(MainActivity.this, "请输入班级名", Toast.LENGTH_SHORT).show();

                        }else{
                            createClass(textClassName.getText().toString());
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


    //创建班级
    private void createClass(String name){
        Class mClass=new Class();
        mClass.setcName(name);
        Teacher user=BmobUser.getCurrentUser(Teacher.class);
        mClass.settNo(user);
        mClass.save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                if(e==null){
                    Toast.makeText(MainActivity.this, "创建成功", Toast.LENGTH_SHORT).show();
                    swipeRefresh.setRefreshing(true);
                    refreshClass();
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
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
                deleteClass(pos);
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


    //删除班级
    private void deleteClass(final int pos){
        final Class mClass=classList.get(pos);
        mClass.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    //Log.i("bmob","成功");
                    //删除该班的学，查找，再删除
                    BmobQuery<ClassStudent> query = new BmobQuery<>();
                    query.addWhereEqualTo("cNo", mClass);
                    query.setLimit(500);
                    //执行查询方法
                    query.findObjects(new FindListener<ClassStudent>() {
                        @Override
                        public void done(List<ClassStudent> object, BmobException e) {
                            if(e==null){
                               //找到后删除
                                List<BmobObject> classSutentList=new ArrayList<>();
                                classSutentList.addAll(object);
                                new BmobBatch().deleteBatch(classSutentList).doBatch(new QueryListListener<BatchResult>() {

                                    @Override
                                    public void done(List<BatchResult> o, BmobException e) {
                                        if(e==null){
                                            for(int i=0;i<o.size();i++){
                                                BatchResult result = o.get(i);
                                                BmobException ex =result.getError();
                                                if(ex==null){
                                                   // log("第"+i+"个数据批量删除成功");
                                                }else{
                                                    //log("第"+i+"个数据批量删除失败："+ex.getMessage()+","+ex.getErrorCode());
                                                   // Toast.makeText(MainActivity.this, "有部分学生没有删除", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                            //到此彻底删除
                                            classAapter.removeItem(pos);
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

    //填充数据
    private void initClass(){
        //读取数据
        Teacher teacher=BmobUser.getCurrentUser(Teacher.class);
        BmobQuery<Class> query = new BmobQuery<>();
        query.addWhereEqualTo("tNo",teacher);
        //返回50条数据，如果不加上这条语句，默认返回10条数据
        query.order("-createdAt");
        query.setLimit(50);
        //执行查询方法
        query.findObjects(new FindListener<Class>() {
            @Override
            public void done(List<Class> object, BmobException e) {
                if(e==null){
                    classList.clear();
                    classList.addAll(object);
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

    //权限申请
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Bmob.initialize(this, "878c52b7f35cc2c5ee9ccd9539ac26d6");

                }
                break;
        }
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

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
