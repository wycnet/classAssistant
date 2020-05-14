package com.example.wamg1.teacher;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.wamg1.teacher.Test.CreatTestDate;
import com.example.wamg1.teacher.bean.Teacher;
import com.example.wamg1.teacher.logTest.LogUtil;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;

public class LoginMainActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView textUsername;
    private TextView textPassword;
    private TextView textLoginInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main);

        textUsername=findViewById(R.id.text_username);
        textPassword=findViewById(R.id.text_password);
        textLoginInfo=findViewById(R.id.text_login_info);
        Button buttonLogin;
        Button buttonRegister;
        buttonRegister=findViewById(R.id.button_register);
        buttonLogin=findViewById(R.id.button_login);

        //bmob初始化!!!!非常重要
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)!=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]
                    {Manifest.permission.READ_PHONE_STATE},1);
        }else{
            Bmob.initialize(this, "878c52b7f35cc2c5ee9ccd9539ac26d6");

        }


   //     判断是否已登陆
        BmobUser bmobUser = BmobUser.getCurrentUser();
        if(bmobUser != null){
            Intent intent=new Intent(this,MainActivity.class);
            startActivity(intent);
            finish();
        }

        //监听登陆
        buttonLogin.setOnClickListener(this);
        buttonRegister.setOnClickListener(this);

        login("001","123456");
    }


    private void login(String username,String password){

        BmobUser.loginByAccount(username, password, new LogInListener<BmobUser>() {

            @Override
            public void done(BmobUser user, BmobException e) {
                if(user!=null){
                    LogUtil.e("dd","用户登陆成功");
                    Intent intent=new Intent(LoginMainActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    textLoginInfo.setText("登陆失败，请确认用户名和密码");
                    textLoginInfo.setTextColor(Color.RED);
                    textLoginInfo.setVisibility(View.VISIBLE);

                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_login:
                textLoginInfo.setText("正在登陆...");
                textLoginInfo.setVisibility(View.VISIBLE);
                login(textUsername.getText().toString(),textPassword.getText().toString());
                break;
            case R.id.button_register:
                Intent intent=new Intent(this,RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }

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


}
