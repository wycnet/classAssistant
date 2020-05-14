package com.example.wamg1.teacher.page;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.wamg1.teacher.BlueToothCallnameActivity;
import com.example.wamg1.teacher.CallnameFormActivity;
import com.example.wamg1.teacher.R;
import com.example.wamg1.teacher.bean.CallName;
import com.example.wamg1.teacher.bean.Class;
import com.example.wamg1.teacher.bean.ClassStudent;
import com.example.wamg1.teacher.bean.Student;
import com.example.wamg1.teacher.dataAdapter.CallnameAdapter;
import com.example.wamg1.teacher.httpOperate.CallnameDada;
import com.example.wamg1.teacher.logTest.LogUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;

public class FragmentTwo extends Fragment implements View.OnClickListener {

    private String classId;
    private String className;
    private String times;


    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private CallnameAdapter callnameAdapter;
    private List<CallnameDada> callnameList = new ArrayList<>();

    private SwipeRefreshLayout swipeRefresh;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ClassPager root = (ClassPager) getActivity();
        classId = root.classId;
        className = root.className;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.class_page_two_callname, container, false);
        // TextView txt_content = (TextView) view.findViewById(R.id.txt_content);
        // txt_content.setText("第一个Fragment");


        FloatingActionButton fab = view.findViewById(R.id.fab_addcallname_form);
        fab.setOnClickListener(this);

        recyclerView = view.findViewById(R.id.recycler_view_callname);
        layoutManager = new GridLayoutManager(view.getContext(), 1);
        //layoutManager=new LinearLayoutManager(view.getContext());
        LogUtil.d("aa", this.classId);
        recyclerView.setLayoutManager(layoutManager);
        callnameAdapter = new CallnameAdapter(callnameList);
        recyclerView.setAdapter(callnameAdapter);

        //给Adapter添加点击监听事件
        callnameAdapter.setOnItemClickListener(new CallnameAdapter.OnItemOnClickListener() {
            @Override
            public void onItemOnClick(View view, int pos) {
                CallnameDada callnameDada = callnameList.get(pos);
                Intent intent = new Intent(view.getContext(), CallnameFormActivity.class);
                intent.putExtra("times", callnameDada.getCreateTime());
                startActivity(intent);
            }

            @Override
            public void onItemLongOnClick(View view, int pos) {
                showPopMenu(view, pos); //弹出菜单
            }
        });

        //下拉刷新
        swipeRefresh = view.findViewById(R.id.swipe_refresh_callname);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshCallname();
            }
        });
        refreshCallname();
        swipeRefresh.setRefreshing(true);


        return view;
    }


    //长按弹出菜单
    public void showPopMenu(View view, final int pos) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_item_delete, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                //callnameAdapter.removeItem(pos);
                deleteCallname(pos);
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
    private void initQusetion() {
        //读取数据
        BmobQuery<CallName> query = new BmobQuery<>();
        Class mClass = new Class();
        mClass.setObjectId(this.classId);
        //query.addWhereMatchesQuery("cNo","Class",innerQuery);
        query.addWhereEqualTo("cNo", mClass);
        query.order("-createdAt");
        query.setLimit(500);
        //执行查询方法
        query.findObjects(new FindListener<CallName>() {
            @Override
            public void done(List<CallName> object, BmobException e) {
                if (e == null) {
                    LogUtil.e("hh", "数据:" + object.size() + "条");
                    //先将表清空
                    callnameList.clear();
                    //统计有多少个点名表
                    Set<String> createTimes = new TreeSet<>();
                    for (CallName callName : object) {
                        createTimes.add(callName.getTimes());
                    }
                    //创建统计信息
                    CallnameDada callnameDada;
                    for (String str : createTimes) {
                        callnameDada = new CallnameDada();
                        callnameDada.setCreateTime(str);
                        int cuqing = 0, qingjia = 0, cidao = 0, kuangke = 0;
                        for (CallName callName : object) {
                            if (callName.getTimes().equals(str)) { //状态，1到2没到3请假4迟到
                                if (callName.getState().equals("1") || callName.getState().equals("0")) {
                                    cuqing++;
                                } else if (callName.getState().equals("2")) {
                                    kuangke++;
                                } else if (callName.getState().equals("3")) {
                                    qingjia++;
                                } else if (callName.getState().equals("4")) {
                                    cidao++;
                                }
                            }

                        }

                        callnameDada.setCidao(Integer.toString(cidao));
                        callnameDada.setCuqing(Integer.toString(cuqing));
                        callnameDada.setKuangke(Integer.toString(kuangke));
                        callnameDada.setQingjia(Integer.toString(qingjia));
                        callnameList.add(callnameDada);
                    }

                    callnameAdapter.notifyDataSetChanged();
                    swipeRefresh.setRefreshing(false);


                } else {
                    swipeRefresh.setRefreshing(false);
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });

    }

    //刷新方法
    private void refreshCallname() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.e("aa", "已执行刷新");
                        initQusetion();
                    }
                });
            }
        }).start();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_addcallname_form:
//                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity())
//                        .setTitle("创建点名表")
//                        .setMessage("是否新建点名表？")
//                        .setCancelable(true)
//                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                createCallname();
//                                swipeRefresh.setRefreshing(true);
//                            }
//                        });
//                alertDialog.setNegativeButton("否", null);
//                alertDialog.show();

                final String[] items = { "普通点名表", "蓝牙点名表",};
                AlertDialog dialog = new AlertDialog.Builder(getActivity()).setTitle("选择点名类型")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which==0){
                                    createCallname();
                                    swipeRefresh.setRefreshing(true);
                                }else if(which==1){
                                    Intent intent=new Intent(getContext(), BlueToothCallnameActivity.class);
                                    intent.putExtra("class_id",classId);
                                    startActivity(intent);
                                }
                            }
                        }).create();
                dialog.show();
                break;

        }

    }

    //为每个学生创建点名记录
    private void createCallname() {
        Date date = new Date();//获得系统时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        times = simpleDateFormat.format(date);

        final Class mClass = new Class();
        mClass.setObjectId(classId);
        BmobQuery<ClassStudent> query = new BmobQuery<ClassStudent>();
        query.addWhereEqualTo("cNo", mClass);
        query.include("sNo[objectId]");
        query.setLimit(500);
        //执行查询方法
        query.findObjects(new FindListener<ClassStudent>() {
            @Override
            public void done(List<ClassStudent> object, BmobException e) {
                if (e == null) {
                    //toast("查询成功：共"+object.size()+"条数据。");
                    List<BmobObject> callnameLsit = new ArrayList<>();
                    for (ClassStudent cs : object) {
                        Student mStudent = cs.getsNo();
                        CallName mCallName = new CallName();
                        mCallName.setsNo(mStudent);
                        mCallName.setcNo(mClass);
                        mCallName.setState("0");
                        mCallName.setTimes(times);
                        callnameLsit.add(mCallName);
                    }

                    //批量添加
                    new BmobBatch().insertBatch(callnameLsit).doBatch(new QueryListListener<BatchResult>() {

                        @Override
                        public void done(List<BatchResult> o, BmobException e) {
                            if (e == null) {
                                for (int i = 0; i < o.size(); i++) {
                                    BatchResult result = o.get(i);
                                    BmobException ex = result.getError();
                                    if (ex == null) {
                                        LogUtil.e("gg", "第" + i + "个数据批量添加成功：" + result.getCreatedAt() + "," + result.getObjectId() + "," + result.getUpdatedAt());
                                    } else {
                                        LogUtil.e("gg", "第" + i + "个数据批量添加失败：" + ex.getMessage() + "," + ex.getErrorCode());
                                    }

                                    //添加成功后刷新
                                    refreshCallname();
                                }
                            } else {
                                swipeRefresh.setRefreshing(false);
                                Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                            }
                        }
                    });
                } else {
                    swipeRefresh.setRefreshing(false);
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    //删除点名表，并且删除点名记录
    private void deleteCallname(final int position) {
        //CallName mCallName=new CallName();
        //mCallName.setTimes(callnameList.get(position).getCreateTime());
        //final List<CallName> callNames=new ArrayList<>();
        String times = callnameList.get(position).getCreateTime();
        //先查找再删除
        BmobQuery<CallName> query = new BmobQuery<>();
        query.addWhereEqualTo("times", times);
        query.addQueryKeys("objectId");
        query.setLimit(500);
        query.findObjects(new FindListener<CallName>() {
            @Override
            public void done(List<CallName> list, BmobException e) {
                if (e == null) {
                    //下面进行删除操作
                    List<BmobObject> objects=new ArrayList<>();
                    objects.addAll(list);
                    new BmobBatch().deleteBatch(objects).doBatch(new QueryListListener<BatchResult>() {

                        @Override
                        public void done(List<BatchResult> o, BmobException e) {
                            if(e==null){
                                for(int i=0;i<o.size();i++){
                                    BatchResult result = o.get(i);
                                    BmobException ex =result.getError();
                                    if(ex==null){
                                        // log("第"+i+"个数据批量删除成功");
                                    }else{
                                        Toast.makeText(getContext(), "删除失败", Toast.LENGTH_SHORT).show();
                                        return;
                                        //log("第"+i+"个数据批量删除失败："+ex.getMessage()+","+ex.getErrorCode());
                                    }
                                }
                                Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                                //成功后将Adapter中数据删除
                                callnameAdapter.removeItem(position);
                            }else{
                                Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                                Toast.makeText(getContext(), "删除失败", Toast.LENGTH_SHORT).show();

                            }
                        }

                    });

                } else {
                    Toast.makeText(getContext(), "服务器删除操作失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}