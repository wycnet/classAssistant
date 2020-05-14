package com.example.wamg1.student.page;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.wamg1.student.R;
import com.example.wamg1.student.bean.Student;
import com.example.wamg1.student.logTest.LogUtil;

import java.util.TimerTask;

import cn.bmob.v3.BmobUser;

public class FragmentTwo extends Fragment {

    private String classId;
    private String className;

    private ProgressBar progressBar;
    private Button btnStart;

    private boolean isStart;

    private String blueToothOldName;
    private BluetoothAdapter mAdapter;


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
        progressBar=view.findViewById(R.id.progress);
        btnStart=view.findViewById(R.id.button_start);

        isStart=false;
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isStart){
                    openBludeTooth();
                    progressBar.setVisibility(View.VISIBLE);
                    isStart=true;
                    btnStart.setText("结束");
                }else{
                    progressBar.setVisibility(View.INVISIBLE);
                    isStart=false;
                    btnStart.setText("开始");
                }

            }
        });
        return view;
    }


    private void openBludeTooth(){
        final String sNo= BmobUser.getCurrentUser(Student.class).getObjectId();
        mAdapter= BluetoothAdapter.getDefaultAdapter();
        if(mAdapter==null){
            Toast.makeText(getContext(), "该设备没有蓝牙!", Toast.LENGTH_SHORT).show();
            return;
        }
        blueToothOldName=mAdapter.getName();

        if(!mAdapter.isEnabled()){

            //弹出对话框提示用户是后打开
            Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enabler, 1);
            //不做提示，强行打开，此方法需要权限<uses-permissionandroid:name="android.permission.BLUETOOTH_ADMIN" />
//            if(mAdapter.isEnabled()){
//                mAdapter.enable();
//           }
        }

        while (!mAdapter.isEnabled()){
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        Boolean isSetName=mAdapter.setName(sNo);



    }

    private void closeBlueTooth(){

    }

}