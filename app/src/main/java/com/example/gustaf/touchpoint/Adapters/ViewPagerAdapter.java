package com.example.gustaf.touchpoint.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Adapter for adding fragments into a viewpager. Also used for enabling different animations
 * between fragments.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {
    private final ArrayList<Fragment>             fragments = new ArrayList<>();
    private final ArrayList<String>               tabTitles = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager fm){
        super(fm);
    }

    public void addFragments(Fragment fragment, String title){
        fragments.add(fragment);
        tabTitles.add(title);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position){
        return tabTitles.get(position);
    }
}
