package com.example.mycalender.ui.main;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class EventsPager extends FragmentPagerAdapter {
    String[] months = {"January","February","March","April","May","June","July","August","September","October","November","December"};
    public EventsPager( FragmentManager fm) {
        super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        EventFragment newPager = EventFragment.newInstance(position + 1, 2020);
        return newPager;
    }

    @Override
    public int getCount() {
        return 12;
    }
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return months[position];
    }
}
