package com.pdmurty.mycalender;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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
        ApplyWindowInsets();
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
    void ApplyWindowInsets(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getWindow().setStatusBarContrastEnforced(true);
        }
        if (Build.VERSION.SDK_INT >= 36) {
            // Additional configuration for Android 16 (API level 36)
            View decorView = getWindow().getDecorView();
// Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);

            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            );
        }
        View tv = findViewById(R.id.rlayout);
        ViewCompat.setOnApplyWindowInsetsListener(
                tv, (v, windowInsets) -> {
                    Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
                    // Apply the insets as a margin to the view. This solution sets only the
                    // bottom, left, and right dimensions, but you can apply whichever insets are
                    // appropriate to your layout. You can also update the view padding if that's
                    // more appropriate.
                    ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
                    //mlp.leftMargin = insets.left;
                    //mlp.bottomMargin = insets.bottom;
                    mlp.topMargin = insets.top;
                    v.setLayoutParams(mlp);

                    // Return CONSUMED if you don't want the window insets to keep passing
                    // down to descendant views.
                    return WindowInsetsCompat.CONSUMED;
                }


        );

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
