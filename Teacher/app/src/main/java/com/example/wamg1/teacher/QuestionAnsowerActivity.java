package com.example.wamg1.teacher;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.wamg1.teacher.bean.Ansower;
import com.example.wamg1.teacher.bean.Question;
import com.example.wamg1.teacher.httpOperate.QuestionGSONData;
import com.example.wamg1.teacher.logTest.LogUtil;
import com.google.gson.Gson;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class QuestionAnsowerActivity extends AppCompatActivity implements View.OnClickListener{

    private String qusetionId;
    private String question;
    private TextView QuestionTitle;
    private TextView Ansowera;
    private TextView Ansowerb;
    private TextView Ansowerc;
    private TextView Ansowerd;
    private TextView Ansower;
    private QuestionGSONData questionData;

    private Button studentRight;
    private Button studentWrong;
    private Button studentUnAnsower;


    private TextView textRignt;
    private TextView textWrong;
    private TextView textUnAnsower;
    private TextView StudentList;
    private String strRight; //答对同学
    private String strWrong; //答错同学
    private String strUnAnsower; //没有回答的同学
    private String strRightNum;  //答对的数量
    private String strWrongNum;  //答错的数量
    private String strUnAnsowerNum; //没回答的数量

    private int buttonState=1; //1,2,3
    private SwipeRefreshLayout swipeRefresh;


    private ProgressBar progressBarA;
    private ProgressBar progressBarB;
    private ProgressBar progressBarC;
    private ProgressBar progressBarD;
    private ProgressBar progressBarNull;
    private TextView textNumA;
    private TextView textNumB;
    private TextView textNumC;
    private TextView textNumD;
    private TextView textNumNull;
    private TextView textPersentA;
    private TextView textPersentB;
    private TextView textPersentC;
    private TextView textPersentD;
    private TextView textPersentNull;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_ansower);
        Intent intent=getIntent();
        qusetionId=intent.getStringExtra("question_id");
        question=intent.getStringExtra("question");
        LogUtil.e("bb",qusetionId);
        LogUtil.e("bb",question);
        Gson gson=new Gson();
        questionData=gson.fromJson(question,QuestionGSONData.class);

        QuestionTitle=findViewById(R.id.question_title);
        Ansowera=findViewById(R.id.ansowera);
        Ansowerb=findViewById(R.id.ansowerb);
        Ansowerc=findViewById(R.id.ansowerc);
        Ansowerd=findViewById(R.id.ansowerd);
        Ansower=findViewById(R.id.ansower);
        QuestionTitle.setText(questionData.getTitle());
        Ansowera.setText(questionData.getOptionA());
        Ansowerb.setText(questionData.getOptionB());
        Ansowerc.setText(questionData.getOptionC());
        Ansowerd.setText(questionData.getOptionD());
        Ansower.setText("正确答案："+questionData.getAnsower());


        textRignt=findViewById(R.id.text_right);
        textWrong=findViewById(R.id.text_wrong);
        textUnAnsower=findViewById(R.id.text_no_ansower);
        StudentList=findViewById(R.id.student_list);

        //各个选项百分比
        progressBarA=findViewById(R.id.progressBar_a);
        progressBarB=findViewById(R.id.progressBar_b);
        progressBarC=findViewById(R.id.progressBar_c);
        progressBarD=findViewById(R.id.progressBar_d);
        progressBarNull=findViewById(R.id.progressBar_null);
        textNumA=findViewById(R.id.text_num_a);
        textNumB=findViewById(R.id.text_num_b);
        textNumC=findViewById(R.id.text_num_c);
        textNumD=findViewById(R.id.text_num_d);
        textNumNull=findViewById(R.id.text_num_null);
        textPersentA=findViewById(R.id.text_percent_a);
        textPersentB=findViewById(R.id.text_percent_b);
        textPersentC=findViewById(R.id.text_percent_c);
        textPersentD=findViewById(R.id.text_percent_d);
        textPersentNull=findViewById(R.id.text_percent_null);


        studentRight=findViewById(R.id.student_right);
        studentWrong=findViewById(R.id.student_wrong);
        studentUnAnsower=findViewById(R.id.student_no_nsower);
        studentRight.setOnClickListener(this);
        studentWrong.setOnClickListener(this);
        studentUnAnsower.setOnClickListener(this);

        refeshStudent();
        //下拉刷新
        swipeRefresh=findViewById(R.id.swipe_refresh_ansower);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refeshStudent();
            }
        });

    }

    //填充数据
    private void initSudentList(){
        Question question=new Question();
        question.setObjectId(qusetionId);
        BmobQuery<Ansower> query = new BmobQuery<>();
        query.addWhereEqualTo("qNo", question);  // 查询所有学生的答案
        query.include("sNo");// 希望在查询帖子信息的同时也把学生的信息查询出来
        query.setLimit(500);
        query.order("-updatedAt");  //排序
        query.findObjects(new FindListener<Ansower>() {
            @Override
            public void done(List<Ansower> object, BmobException e) {
                if(e==null){
                    LogUtil.e("cc","成功");
                    LogUtil.e("cc",object.size()+"条"+qusetionId);
                    int right=0,wrong=0,unAnsower=0;
                    StringBuilder sbRight=new StringBuilder("");
                    StringBuilder sbWrong=new StringBuilder("");
                    StringBuilder sbUnAnsower=new StringBuilder("");

                    int numA=0,numB=0,numC=0,numD=0,numNull=0;
                    for(Ansower ans: object){

                        if((ans.getAnswer()==null)||!(ans.getAnswer())){
                            unAnsower++;
                            sbUnAnsower.append(ans.getsNo().getUsername()).append("    ").append(ans.getsNo().getName()+"\n");
                        }else if((ans.getTrue()!=null) && ans.getTrue()){
                            right++;
                            sbRight.append(ans.getsNo().getUsername()).append("    ").append(ans.getsNo().getName()+"\n");
                        }else{
                            wrong++;
                            sbWrong.append(ans.getsNo().getUsername()).append("    ").append(ans.getsNo().getName()+"\n");
                        }

                        //统计每个选项人数
                        if(ans.getAnswer()!=null && ans.getAnswer()){
                            if(ans.getOption().equals("A")){
                                numA++;
                            }else if(ans.getOption().equals("B")){
                                numB++;
                            }else if(ans.getOption().equals("C")){
                                numC++;
                            }else if(ans.getOption().equals("D")){
                                numD++;
                            }
                        }
                    }
                    strRightNum=Integer.toString(right)+"人";
                    strWrongNum=Integer.toString(wrong)+"人";
                    strUnAnsowerNum=Integer.toString(unAnsower)+"人";
                    strRight=sbRight.toString();
                    strWrong=sbWrong.toString();
                    strUnAnsower=sbUnAnsower.toString();

                    textRignt.setText(strRightNum);
                    textWrong.setText(strWrongNum);
                    textUnAnsower.setText(strUnAnsowerNum);

                    //填写ui每个选项的人数
                    numNull=unAnsower;
                    textNumA.setText((Integer.toString(numA)+"人"));
                    textNumB.setText((Integer.toString(numB)+"人"));
                    textNumC.setText((Integer.toString(numC)+"人"));
                    textNumD.setText((Integer.toString(numD)+"人"));
                    textNumNull.setText((Integer.toString(numNull)+"人"));
                    //填写百分比
                    int num=(numA+numB+numC+numD+numNull);
                    textPersentA.setText((((int)(((double)numA/num)*100))+"%"));
                    textPersentB.setText((((int)(((double)numB/num)*100))+"%"));
                    textPersentC.setText((((int)(((double)numC/num)*100))+"%"));
                    textPersentD.setText((((int)(((double)numD/num)*100))+"%"));
                    textPersentNull.setText((((int)(((double)numNull/num)*100))+"%"));
                    //填写进度条
                    progressBarA.setProgress((int)(((double)numA/num)*100));
                    progressBarB.setProgress((int)(((double)numB/num)*100));
                    progressBarC.setProgress((int)(((double)numC/num)*100));
                    progressBarD.setProgress((int)(((double)numD/num)*100));
                    progressBarNull.setProgress((int)(((double)numNull/num)*100));


                    //填写学生列表
                    if(buttonState==1){
                        StudentList.setText(strRight);
                    }else if(buttonState==2){
                        StudentList.setText(strWrong);
                    }else{
                        StudentList.setText(strUnAnsower);
                    }
                }else{
                    Log.i("bmob","失败："+e.getMessage());
                }
            }

        });
    }
    //刷新
    private void refeshStudent(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {//更新UI操作
                    @Override
                    public void run() {
                       initSudentList();//从服务器取得数据
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.student_right:  //1
                buttonState=1;
                studentRight.setTextColor(Color.RED);
                studentWrong.setTextColor(Color.BLACK);
                studentUnAnsower.setTextColor(Color.BLACK);
                StudentList.setText(strRight);
                break;
            case R.id.student_wrong:  //2
                buttonState=2;
                studentRight.setTextColor(Color.BLACK);
                studentWrong.setTextColor(Color.RED);
                studentUnAnsower.setTextColor(Color.BLACK);
                StudentList.setText(strWrong);
                break;
            case R.id.student_no_nsower: //3
                buttonState=3;
                studentRight.setTextColor(Color.BLACK);
                studentWrong.setTextColor(Color.BLACK);
                studentUnAnsower.setTextColor(Color.RED);
                StudentList.setText(strUnAnsower);
                break;
            case R.id.student_refresh:
                refeshStudent();
                break;
                default:

                    break;
        }
    }
}
