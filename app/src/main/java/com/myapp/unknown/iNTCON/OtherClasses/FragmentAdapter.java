package com.myapp.unknown.iNTCON.OtherClasses;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by UNKNOWN on 10/3/2016.
 */
public class FragmentAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> fragmentList = new ArrayList<>();
    private final List<String> tabTitleList = new ArrayList<>();
    private final List<Boolean> boolen = new ArrayList<>();

    public FragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(Fragment fragment, String tabTitle){
        fragmentList.add(fragment);
        tabTitleList.add(tabTitle);
        boolen.add(false);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {

        if(boolen.get(position))
        {
            return tabTitleList.get(position);
        }

        return null;
    }
}
