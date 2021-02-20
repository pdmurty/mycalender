package com.example.mycalender;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.mycalender.ui.main.SectionsPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalcEphemeris extends AppCompatActivity
        implements GetYear.OnButtonDone{

    int year;
    DialogFragment dlg;
    SectionsPagerAdapter pager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_demo );
        pager = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(pager);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        pager.setYear(year);

        Button yrTxt = findViewById(R.id.year);
        yrTxt.setText(String.valueOf(year));
        findViewById(R.id.year).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowYearDlg();
            }
        });

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
        pager.OnYearChange(year);
        dlg.dismiss();

    }


}
