package com.example.mycalender.ui.main;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycalender.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2};
    private static ArrayList<PlaceholderFragment> fragmentList =
            new ArrayList<>();
    private final Context mContext;
    int myear;
    FragmentManager mfm;
    String[] months = {"January","February","March","April","May","June","July","August","September","October","November","December"};

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mfm = fm;
        mContext = context;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void OnYearChange(int year){
        setYear(year);

        int fgsize = fragmentList.size();
        for(int i =0; i<fgsize;++i){
            fragmentList.get(i).OnYearChange(year);

        }
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
       // return PlaceholderFragment.newInstance(position + 1);

        PlaceholderFragment newPager = PlaceholderFragment.newInstance(position + 1, myear);
        fragmentList.add(newPager);

        return newPager;

    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        //return mContext.getResources().getString(TAB_TITLES[position]);
        return months[position];//"TAB " + (position + 1);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 12;
    }

    public void setYear(int year) {
        Log.d("eph", "pager_setyear - " + year);
        PlaceholderFragment.myear = year;
    }
}