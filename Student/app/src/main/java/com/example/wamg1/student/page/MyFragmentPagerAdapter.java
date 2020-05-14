package com.example.wamg1.student.page;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {


    private FragmentOne myFragment1 = null;
    private FragmentTwo myFragment2 = null;
    private FragmentThree myFragment3 = null;
    private FragmentFour myFragment4 = null;
    private FragmentFive myFragment5 = null;

    public final int COUNT = 5;
    private String[] titles = new String[]{"提问", "点名", "作业","公告","讨论"};
    private Context context;

    public MyFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        myFragment1 = new FragmentOne();
        myFragment2 = new FragmentTwo();
        myFragment3 = new FragmentThree();
        myFragment4 = new FragmentFour();
        myFragment5 = new FragmentFive();
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        //return PageFragment.newInstance(position + 1);
        Fragment fragment = null;
        switch (position+1) {
            case 1:
                fragment = myFragment1;
                break;
            case 2:
                fragment = myFragment2;
                break;
            case 3:
                fragment = myFragment3;
                break;
            case 4:
                fragment = myFragment4;
                break;
            case 5:
                fragment = myFragment5;
                break;
        }
        return fragment;

    }

    @Override
    public int getCount() {
        return COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

}
