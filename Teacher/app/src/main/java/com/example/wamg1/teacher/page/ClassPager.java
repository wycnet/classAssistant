package com.example.wamg1.teacher.page;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.wamg1.teacher.R;

public class ClassPager extends AppCompatActivity {
    public String classId;
    public String className;
    public MyFragmentPagerAdapter adapter;
    public ViewPager viewPager;
    public TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.class_page);
        //传递班级号和班级名
        Intent intent=getIntent();
        className=intent.getStringExtra("class_name");
        classId=intent.getStringExtra("class_id");

        TextView textClassName=findViewById(R.id.text_class_name);
        textClassName.setText((className+" "+classId));
        //Fragment+ViewPager+FragmentViewPager组合的使用
        viewPager =  findViewById(R.id.viewPager);
        adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(),
                this);
        viewPager.setAdapter(adapter);
        //TabLayout
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }
}
