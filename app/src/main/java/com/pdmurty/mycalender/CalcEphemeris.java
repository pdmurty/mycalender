package com.pdmurty.mycalender;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.pdmurty.mycalender.ui.main.ephPager;
import com.google.android.material.tabs.TabLayout;

import java.util.Calendar;

public class CalcEphemeris extends AppCompatActivity
        implements GetYear.OnButtonDone{

    int year;
    DialogFragment dlg;
    ephPager pager;
    EventsVwModel mVwmodel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabbed_activity);
        pager = new ephPager( getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(pager);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        Button yrTxt = findViewById(R.id.year);
        yrTxt.setText(String.valueOf(year));
        findViewById(R.id.year).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowYearDlg();
            }
        });
        mVwmodel= new ViewModelProvider(this).get(EventsVwModel.class);
        mVwmodel.setYear(year);
 }

    private void ShowYearDlg() {
        dlg = new GetYear();
        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentByTag("Set Year") == null)
            dlg.show(getSupportFragmentManager(), "Set Year");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void OnGoButtonClicked(int year, int month) {
        Button yrTxt = findViewById(R.id.year);
        yrTxt.setText(String.valueOf(year));
        mVwmodel.setYear(year);
        dlg.dismiss();

    }


}
