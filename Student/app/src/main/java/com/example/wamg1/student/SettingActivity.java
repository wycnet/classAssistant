package com.example.wamg1.student;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wamg1.student.bean.Student;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;


public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private Student student;

    private TextView textName;
    private TextView textEmail;

    private TextView textSno;
    private TextView textCollege;
    private TextView textMajor;
    private TextView textEmail2;

    private View layoutSno;
    private View layoutCollege;
    private View layoutMajor;
    private View layoutEmail;
    private View layoutPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        student= BmobUser.getCurrentUser(Student.class);
        textName=findViewById(R.id.text_name);
        textEmail=findViewById(R.id.email);

        textSno=findViewById(R.id.text_sno);
        textCollege=findViewById(R.id.text_college);
        textMajor=findViewById(R.id.text_major);
        textEmail2=findViewById(R.id.text_email);

        layoutSno=findViewById(R.id.layout_sno);
        layoutCollege=findViewById(R.id.layout_colloge);
        layoutMajor=findViewById(R.id.layout_major);
        layoutEmail=findViewById(R.id.layout_email);
        layoutPassword=findViewById(R.id.layout_password);
        layoutSno.setOnClickListener(this);
        layoutCollege.setOnClickListener(this);
        layoutMajor.setOnClickListener(this);
        layoutEmail.setOnClickListener(this);
        layoutPassword.setOnClickListener(this);

        initData();
    }

    private void initData(){
        textName.setText(student.getName());
        textEmail.setText(student.getEmail());
        textSno.setText(student.getUsername());
        textCollege.setText(student.getCollege());
        textMajor.setText(student.getMajor());
        textEmail2.setText(student.getEmail());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_sno:

                break;

            case R.id.layout_colloge:
                showDialog("学院",1);
                break;
            case R.id.layout_major:
                showDialog("专业",1);
                break;
            case R.id.layout_email:
                showDialog("邮箱",1);
                break;
            case R.id.layout_password:
                showDialog("密码",2);
                break;
            case R.id.button_out:
                BmobUser.logOut();   //清除缓存用户对象
                Toast.makeText(this, "已经注销", Toast.LENGTH_SHORT).show();
                Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                break;
        }
    }

    private void showDialog(final String title, int fialogStytle){
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        if(fialogStytle==1){
           // final ConstraintLayout layout= (ConstraintLayout) getLayoutInflater().inflate(R.layout.dialog_setting,null);
            final TextInputLayout layout=new TextInputLayout(this);
            layout.setPadding(40,20,40,5);
            final TextInputEditText textClassName=new TextInputEditText(this);
            textClassName.setHint(title);
            layout.addView(textClassName);
            dialog.setTitle("修改"+title);
            dialog.setView(layout );
            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if(!TextUtils.isEmpty(textClassName.getText())){
                                String value=textClassName.getText().toString();
                                Student stu=new Student();
                                switch (title){
                                    case "学院":
                                        stu.setCollege(value);
                                        saveUserInfo(stu,textCollege,value);
                                        break;
                                    case "专业":
                                        stu.setMajor(value);
                                        saveUserInfo(stu,textMajor,value);
                                        break;
                                    case "邮箱":
                                        stu.setEmail(value);
                                        saveUserInfo(stu,textEmail2,value);

                                }

                            }
                        }
                    });
            dialog.setNegativeButton("取消", null);
        }else if(fialogStytle==2){
            LinearLayout layout=new LinearLayout(this);
            layout.setPadding(40,20,40,5);
            layout.setOrientation(LinearLayout.VERTICAL);
            final TextInputLayout layout1=new TextInputLayout(this);
            final TextInputEditText textName1=new TextInputEditText(this);
            textName1.setHint("旧密码");
            layout1.addView(textName1);
            final TextInputLayout layout2=new TextInputLayout(this);
            final TextInputEditText textName2=new TextInputEditText(this);
            textName2.setHint("新密码");
            layout2.addView(textName2);
            final TextInputLayout layout3=new TextInputLayout(this);
            final TextInputEditText textName3=new TextInputEditText(this);
            textName3.setHint("确认密码");
            layout3.addView(textName3);
            layout.addView(layout1);
            layout.addView(layout2);
            layout.addView(layout3);

            dialog.setTitle("修改"+title)
                    .setView(layout)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if(!TextUtils.isEmpty(textName1.getText()) && !TextUtils.isEmpty(textName2.getText())
                                    && !TextUtils.isEmpty(textName3.getText())){
                                String oldPassword=textName1.getText().toString();
                                String newPassword=textName2.getText().toString();
                                String surePassword=textName3.getText().toString();
                                if(newPassword.equals(surePassword)){
                                    changePassword(oldPassword,newPassword);
                                }

                            }
                        }
                    });
            dialog.setNegativeButton("取消", null);
        }
        dialog.show();

    }

    private  void changePassword(String oldpasswd,String newpasswd){
        BmobUser.updateCurrentUserPassword(oldpasswd, newpasswd, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Toast.makeText(SettingActivity.this, "密码修改成功，可以用新密码进行登录啦", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(SettingActivity.this, "密码修改失败", Toast.LENGTH_SHORT).show();
                }
            }

        });

    }

    private void saveUserInfo(Student student, final TextView view, final String value){
        student.update(this.student.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    view.setText(value);
                }else{

                }
            }
        });
    }
}
