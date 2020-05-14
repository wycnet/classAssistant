package com.example.wamg1.teacher;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wamg1.teacher.bean.CallName;
import com.example.wamg1.teacher.bean.Class;
import com.example.wamg1.teacher.bean.ClassStudent;
import com.example.wamg1.teacher.bean.Student;
import com.example.wamg1.teacher.logTest.LogUtil;
import com.example.wamg1.teacher.uiTools.RadarView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;

public class BlueToothCallnameActivity extends AppCompatActivity {
    private static final int REQUEST_ACCESS_COARSE_LOCATION =111 ;
    private String classId;
    private Button btnStart;
    private TextView textStudentNum;
    private TextView textCount;
    private List<String> classStudentList=new ArrayList<>();
    private Set<String> studentSearch=new HashSet<>();
    private RadarView radarView;

    private Button btn_frash;

    private Boolean starFlag=false;

    // 创建一个接收ACTION_FOUND广播的BroadcastReceiver
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // 发现设备
           LogUtil.e("zz","接收到广播"+action);
            switch (action) {
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    LogUtil.e("zz",device.getName());
                    if(isFind2(device.getName())){
                        studentSearch.add(device.getName());
                        textCount.setText("已到人数"+studentSearch.size());
                    }
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:


                    break;
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_tooth_callname);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent=getIntent();
        classId=intent.getStringExtra("class_id");

        radarView=findViewById(R.id.radar);
        textCount=findViewById(R.id.text_count);
        textStudentNum=findViewById(R.id.text_student_num);
        btnStart=findViewById(R.id.btn_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!starFlag){
                    starFlag=true;
                    btnStart.setText("结束");
                    startCallname();
                    radarView.start();
                }else{
                   createCallname();
                   radarView.stop();
                }

            }
        });

        btn_frash=findViewById(R.id.button_frash);
        btn_frash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mAdapter.isDiscovering()){
                    mAdapter.startDiscovery();
                }
            }
        });
        classStudentNum();

        IntentFilter iFilter = new IntentFilter(
                BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        registerReceiver(mReceiver, iFilter);

        // 注册BroadcastReceiver
        iFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        // 注册一个广播接收者，开启查找蓝牙设备意图后将结果以广播的形式返回
        registerReceiver(mReceiver, iFilter);

        iFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, iFilter);

        if(Build.VERSION.SDK_INT>=23){
            //判断是否有权限
            if (ContextCompat.checkSelfPermission( BlueToothCallnameActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
                //请求权限
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_ACCESS_COARSE_LOCATION);
                //向用户解释，为什么要申请该权限
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_CONTACTS)) {
                    Toast.makeText(BlueToothCallnameActivity.this,"shouldShowRequestPermissionRationale", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }





    private BluetoothAdapter mAdapter;
    private void startCallname() {
        mAdapter= BluetoothAdapter.getDefaultAdapter();
        if(mAdapter==null){
            Toast.makeText(this, "该设备没有蓝牙!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!mAdapter.isEnabled()){
            //弹出对话框提示用户是后打开
            Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enabler, 1);
            //不做提示，强行打开，此方法需要权限<uses-permissionandroid:name="android.permission.BLUETOOTH_ADMIN" />
//            if(mAdapter.isEnabled()){
//                mAdapter.enable();
//            }
        }

        if (mAdapter.isDiscovering()) {
            mAdapter.cancelDiscovery();
        }

        while(!mAdapter.startDiscovery()){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Toast.makeText(this,  "开始", Toast.LENGTH_SHORT).show();

        //new MyThread().start();

    }



    private class MyThread extends Thread{
        @Override
        public void run() {
            while(true){
                if (!mAdapter.isDiscovering()) {
                    mAdapter.startDiscovery();
                    try {
                        sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    //创建点名信息
    private void createCallname(){
        Date date = new Date();//获得系统时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final String times = simpleDateFormat.format(date);
        List<BmobObject> callnameList = new ArrayList<BmobObject>();
        for (String sNo:classStudentList) {
            CallName callName=new CallName();
            Student student=new Student();
            student.setObjectId(sNo);
            callName.setsNo(student);
            Class aClass=new Class();
            aClass.setObjectId(classId);
            callName.setcNo(aClass);
            if(isFind(sNo)){
                callName.setState("1");
            }else{
                callName.setState("2");
            }
            callName.setTimes(times);
            callnameList.add(callName);
        }
        new BmobBatch().insertBatch(callnameList).doBatch(new QueryListListener<BatchResult>() {
            @Override
            public void done(List<BatchResult> o, BmobException e) {
                if(e==null){
                    for(int i=0;i<o.size();i++){
                        BatchResult result = o.get(i);
                        BmobException ex =result.getError();
                        if(ex==null){
                        }else{
                            Log.i("bmob","第"+i+"个数据批量添加失败："+ex.getMessage()+","+ex.getErrorCode());
                        }
                    }
                    Toast.makeText(BlueToothCallnameActivity.this, "点名创建成功", Toast.LENGTH_SHORT).show();
                    finish();//结束activity;
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }


    //查找该学生是否到
    private boolean isFind(String strSno){
        for(String str:studentSearch){
            if(str.equals(strSno)){
                return true;
            }
        }
        return false;
    }

    //查找该学生是否在班级中
    private boolean isFind2(String sNo){
        for(String str:classStudentList){
            if(str.equals(sNo)){
                return true;
            }
        }
        return false;
    }

        //班级人数
    private void classStudentNum(){
        BmobQuery<ClassStudent> query = new BmobQuery<>();
        Class aClass=new Class();
        aClass.setObjectId(classId);
        query.addWhereEqualTo("cNo", aClass);
        query.include("sNo");
        query.findObjects(new FindListener<ClassStudent>() {
            @Override
            public void done(List<ClassStudent> list, BmobException e) {
                if(e==null){
                    textStudentNum.setText(("总人数"+list.size()));
                    classStudentList.clear();
                    for (ClassStudent cs: list){
                        classStudentList.add(cs.getsNo().getObjectId());
                    }
                }else{

                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
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

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        // TODO Auto-generated method stub
        if (requestCode == REQUEST_ACCESS_COARSE_LOCATION) {
            if (permissions[0] .equals(Manifest.permission.ACCESS_COARSE_LOCATION)
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用户同意使用该权限
            } else {
                // 用户不同意，向用户展示该权限作用
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    //showTipDialog("用来扫描附件蓝牙设备的权限，请手动开启！");
                    return;
                }
            }
        }
    }



}
