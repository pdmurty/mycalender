package com.pdmurty.mycalender.ui.main;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.pdmurty.mycalender.R;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class ephPager extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2};
    FragmentManager mfm;
    String[] months = {"January","February","March","April","May","June","July","August","September","October","November","December"};

    public ephPager(FragmentManager fm) {
        super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

    }
    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
       // return PlaceholderFragment.newInstance(position + 1);

        ephFragment newPager = ephFragment.newInstance(position + 1);
        return newPager;

    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        return months[position];
    }

    @Override
    public int getCount() {
       return 12;
    }


}