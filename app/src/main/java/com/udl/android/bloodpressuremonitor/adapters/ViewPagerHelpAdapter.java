package com.udl.android.bloodpressuremonitor.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by adrian on 20/3/15.
 */
public class ViewPagerHelpAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragmentspager;


    public ViewPagerHelpAdapter(FragmentManager manager, List<Fragment> fragments){
        super(manager);
        this.fragmentspager = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentspager.get(position);
    }

    @Override
    public int getCount() {
        return fragmentspager.size();
    }

}
