package com.example.wamg1.student.page;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.wamg1.student.R;
import com.example.wamg1.student.bean.Ansower;
import com.example.wamg1.student.bean.Class;
import com.example.wamg1.student.bean.Question;
import com.example.wamg1.student.bean.Student;
import com.example.wamg1.student.dataAdapter.QuestionAdapter;
import com.example.wamg1.student.httpOperate.QuestionGSONData;
import com.example.wamg1.student.logTest.LogUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;


public class FragmentOne extends Fragment {

    private String classId;
    private String className;
    public List<Ansower> ansowerList=new ArrayList<>();
    public QuestionAdapter questionAdapter;
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
        className=root.className;


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.class_page_one_question, container, false);

        floatingActionButton=view.findViewById(R.id.fab_create_question);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });


        recyclerView=view.findViewById(R.id.recycler_view_qusetion);
        layoutManager=new GridLayoutManager(view.getContext(),1);
        //layoutManager=new LinearLayoutManager(view.getContext());
        //LogUtil.d("aa",this.classId);
        recyclerView.setLayoutManager(layoutManager);
        questionAdapter=new QuestionAdapter(ansowerList);
        recyclerView.setAdapter(questionAdapter);

        refreshQuestion();
        swipeRefresh=view.findViewById(R.id.swipe_refresh_question);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshQuestion();
            }
        });

        questionAdapter.setOnItemClickListener(new QuestionAdapter.OnItemOnClickListener() {

            @Override
            public void onItemOnClick(View view, int pos) {
                //Question question=ansowerList.get(pos).getqNo();
                Ansower ansower=ansowerList.get(pos);
                //在这里弹出单选dialog
                if(ansower.getAnswer()!=null && ansower.getAnswer()){
                    Toast.makeText(getContext(), "您已回答!", Toast.LENGTH_SHORT).show();
                }else{

                    showSingleChoiceDialog(ansower,pos);
                }

            }

            @Override
            public void onItemLongOnClick(View view, int pos) {

            }
        });
        this.view=view;
        return view;
    }

    private int choise;
    private void showSingleChoiceDialog(final Ansower ansower, final int pos) {
        choise=-1;
        final QuestionGSONData questionGSONData=parseJson(ansower.getqNo().getqFile());
        String[] items={questionGSONData.getOptionA(),questionGSONData.getOptionB(),questionGSONData.getOptionC(),
            questionGSONData.getOptionD()};

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity())
                .setTitle(questionGSONData.getTitle());

        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        choise=which;
                    }
                })
        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                char ch=(char)('A'+choise);
                String option=ch+"";
                ansower.setOption(option);
                ansower.setAnswer(true);
                Boolean isRight=false;
                if(option.equals(questionGSONData.getAnsower())){
                    isRight=true;
                }
                ansower.setTrue(isRight);
               saveAnsower(ansower,pos);

            }
        });
        builder.create().show();
    }

    //保存答题记录
    private void saveAnsower(Ansower ansower, final int pos){
        Ansower ansowerUpdate=new Ansower();
        ansowerUpdate.setValue("isTrue",ansower.getTrue());
        ansowerUpdate.setValue("isAnswer",ansower.getAnswer());
        ansowerUpdate.setValue("option",ansower.getOption());
        ansower.update(ansower.getObjectId(), new UpdateListener() {

            @Override
            public void done(BmobException e) {
                if(e==null){
                    //Log.i("bmob","更新成功");
                    questionAdapter.notifyItemChanged(pos);
                }else{
                    Log.i("bmob","更新失败："+e.getMessage()+","+e.getErrorCode());
                }
            }

        });


    }

    //解析json
    private QuestionGSONData parseJson(String jsonData){
        Gson gson=new Gson();
        QuestionGSONData question=gson.fromJson(jsonData,QuestionGSONData.class);
        return question;
    }


    //填充数据
    private void initQusetion() {
        //读取数据
     //   BmobQuery<Question> query = new BmobQuery<>();
        Class mClass=new Class();
        mClass.setObjectId(this.classId);
        BmobQuery<Ansower> query = new BmobQuery<>();
        BmobQuery<Question> innerQuery = new BmobQuery<>();
        innerQuery.addWhereEqualTo("cNo", mClass);
        // 第一个参数为评论表中的帖子字段名post
        // 第二个参数为Post字段的表名，也可以直接用"Post"字符串的形式
        // 第三个参数为内部查询条件
        query.addWhereMatchesQuery("qNo", "Question", innerQuery);
        query.addWhereEqualTo("sNo", BmobUser.getCurrentUser(Student.class));
        query.include("qNo");
        query.order("-createdAt");
        query.findObjects(new FindListener<Ansower>() {
            @Override
            public void done(List<Ansower> object,BmobException e) {
                if(e==null){
                    LogUtil.e("qq",object.size()+"条");
                    ansowerList.clear();
                    ansowerList.addAll(object);
                    questionAdapter.notifyDataSetChanged();
                    swipeRefresh.setRefreshing(false);
                }else{
                    Log.i("bmob","失败："+e.getMessage());
                }
            }
        });
    }

    //刷新方法
    private void refreshQuestion() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initQusetion();
                    }
                });
            }
        }).start();
    }



}