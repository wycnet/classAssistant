package com.example.wamg1.teacher;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TimeUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.wamg1.teacher.bean.Teacher;
import com.example.wamg1.teacher.logTest.LogUtil;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class RegisterActivity extends AppCompatActivity {

    private EditText textTNo;
    private EditText textTName;
    private EditText textTEmail;
    private EditText textPassword;
    private TextView textRegisterInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        textTNo=findViewById(R.id.text_tno);
        textTName=findViewById(R.id.text_tname);
        textTEmail=findViewById(R.id.text_tEmail);
        textPassword=findViewById(R.id.text_password);
        textRegisterInfo=findViewById(R.id.text_register_info);
        Button buttonRegister=findViewById(R.id.button_sure);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

    }

    private void register(){

        if(!checkText()){
            return;
        }
        Teacher bu = new Teacher();
        bu.setUsername(textTNo.getText().toString());
        bu.setPassword(textPassword.getText().toString());
        bu.setEmail(textTEmail.getText().toString());
        bu.setName(textTName.getText().toString());
        //注意：不能用save方法进行注册
        bu.signUp(new SaveListener<Teacher>() {
            @Override
            public void done(Teacher s, BmobException e) {
                if(e==null){
                    //toast("注册成功:" +s.toString());
                    LogUtil.e("ee","注册成功");

                    AlertDialog.Builder dialog=new AlertDialog.Builder(RegisterActivity.this)
                            .setTitle("注册成功")
                            .setMessage("点击确定返回登陆界面")
                            .setCancelable(false)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                    }
                            });
                    dialog.show();
                 }else{
                    //loge(e);
                    //Log.e("注册失败",e.getMessage());
                    textRegisterInfo.setTextColor(Color.RED);

                    if(e.getMessage().contains("username")){
                        textRegisterInfo.setText("该用户已注册！");
                    }else if(e.getMessage().contains("email")){
                        textRegisterInfo.setText("该邮箱已注册!");
                    }
                }
            }
        });

    }


    private boolean checkText(){
        textRegisterInfo.setTextColor(Color.RED);
        if(TextUtils.isEmpty(textTNo.getText())){
            textRegisterInfo.setText("教师号不能为空！");

        } else if(TextUtils.isEmpty(textTName.getText())){
            textRegisterInfo.setText("姓名不能未空!");
        } else if(TextUtils.isEmpty(textTEmail.getText())){
            textRegisterInfo.setText("邮箱不能为空！");
        }else if(TextUtils.isEmpty(textPassword.getText())){
            textRegisterInfo.setText("密码号不能为空！");
        }else{
            return true;
        }

        return false;
    }


}
