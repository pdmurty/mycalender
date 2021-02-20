package com.example.mycalender;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mycalender.ui.main.EventsPager;
import com.example.mycalender.ui.main.SectionsPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ShowEvents extends AppCompatActivity
        implements GetYear.OnButtonDone{
    List<Events> mEventlist;
    List<Eventdays> listEventdays;
    RecyclerView recycler;
    EventsVwModel mVwmodel;
    Button progressTv;
    TextView hdr;
    DialogFragment dlg;
    EventsRoomDb eventDb;
    EventsDao eventsdao;
    String[] levents;
    EventsPager pager;
    Button yrTxt;
    int maxdays[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    ProgressBar pbar;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int year = 2020;
        setContentView(R.layout.activity_tab_demo);
        levents = getResources().getStringArray(R.array.lunarEvents);
        eventDb = EventsRoomDb.getDatabase(this);
        eventsdao = eventDb.getDao();
        pbar = findViewById(R.id.progress);
       // pbar.setVisibility(View.GONE);
        pager = new EventsPager( getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(pager);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        EventsAync eventsAync = new EventsAync();
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        yrTxt = findViewById(R.id.year);
        yrTxt.setText(String.valueOf(year));
        findViewById(R.id.year).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowYearDlg();
            }
        });
        mVwmodel= new ViewModelProvider(this).get(EventsVwModel.class);
        mVwmodel.setYear(year);
       if( !MainActivity.mPreferences.getBoolean("Year"+year,false))
           eventsAync.execute(year);
    }

    @Override
    public void OnGoButtonClicked(int year, int month) {
        Button yrTxt = findViewById(R.id.year);

        dlg.dismiss();

        if( MainActivity.mPreferences.getBoolean("Year"+year,false)) {

            mVwmodel.setYear(year);
            yrTxt.setText(String.valueOf(year));
        }
        else{
            EventsAync eventsAync = new EventsAync();
            eventsAync.execute(year);
        }

    }
    private void ShowYearDlg() {
        dlg = new GetYear();
        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentByTag("Set Year") == null)
            dlg.show(fm, "Set Year");
    }




    class EventsAync extends AsyncTask<Integer, Integer, Integer> {


        int mday, mmonth;
        int mcount =2;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbar.setVisibility(View.VISIBLE);
        }

        protected Integer doInBackground(Integer... years) {
            updateEvents(years[0].intValue());
            return years[0];
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            Log.d("postexec", "result="+ result.intValue());
            yrTxt.setText(result.toString());
            MainActivity.mPreferences.edit()
                    .putBoolean("Year"+result.toString(), true)
                    .commit();
            mVwmodel.setYear(result.intValue());
            pbar.setVisibility(View.GONE);

        }



        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values[0]);
            pbar.setProgress(values[0]);


        }

        void updateEvents(int year) {
            double[] dbl;
            double[] dblsank;
            double[] dblkarthari;
            String txt;

            int pos = 0;
            int maxday;
            Eventdays cevt ;
            mEventlist =  eventsdao.getAllEventsD();
            int listSize = mEventlist.size();
            listEventdays = new ArrayList<Eventdays>();
            ///////////////////
            Swlib.SWSetSidmode(1, 0, 0);
            Events tmpevt;
            dbl = Swlib.WritePanchang(year, 0, 1, 5.5);
            int lm =(int) dbl[13];
            int ld = (int) dbl[0];
            tmpevt = mEventlist.get(0);

            if(lm>=tmpevt.lunarmonth && ld>tmpevt.thithi) {

                mEventlist.remove(0);
                mEventlist.add(tmpevt);
                tmpevt = mEventlist.get(0);
            }
            dblkarthari = Swlib.SWeCalcKarthariDays(year, 5.5);
            int skip=0;
            for(int i=0 ;i<27;++i){

                skip=i*5;
                //Log.d("kar","kar="+dblkarthari[3+skip]+"wk="+dblkarthari[0+skip]+"m="+dblkarthari[4+skip]+":d="+dblkarthari[1+skip]+":t="+dblkarthari[2+skip]);
                cevt = new Eventdays();
                cevt.day = (int)dblkarthari[1+skip];//day;
                cevt.month = (int)dblkarthari[4+skip];//month + 1;
                cevt.year = year;
                cevt.eventid = 200+(int)dblkarthari[3+skip];//mEventlist.get(pos).event;
                cevt.weekday = (int)dblkarthari[skip];
                listEventdays.add(cevt);
            }
            /////////////////


            for (int month = 0; month < 12 && pos < listSize; ++month) {
                dblsank =Swlib.SWeCalcSankaranthiAndKartari(year,month);
                cevt = new Eventdays();
                cevt.day = (int)dblsank[1];//day;
                cevt.month = month + 1;
                cevt.year = year;
                cevt.eventid = 100+(int)dblsank[3];//mEventlist.get(pos).event;
                cevt.weekday = (int)dblsank[0];
                listEventdays.add(cevt);
                Log.d("sank","wk="+dblsank[0]+":d="+dblsank[1]+":t="+dblsank[2]+"sk="+dblsank[3]);
                maxday = maxdays[month];
                if (year % 4 == 0 && month == 1) ++maxday;
                for (int day = 1; day <= maxday && pos < listSize; ++day) {
                    dbl = Swlib.WritePanchang(year, month, day, 5.5);

                    while (NextEvent(pos, dbl)) {
                        cevt = new Eventdays();
                        cevt.day = day;
                        cevt.month = month + 1;
                        cevt.year = year;
                        cevt.eventid = mEventlist.get(pos).event;
                        cevt.weekday = (int)dbl[9];
                        listEventdays.add(cevt);

                        ++pos;

                        publishProgress((int)(pos*100/listSize));
                    }

                } //end of day

            }// end of month
            eventsdao.UpdateEventdays(listEventdays);


        }

        private boolean NextEvent(int pos, double[] dbl) {
            boolean found = false;

            if (pos < mEventlist.size()) {
                Events cevt = mEventlist.get(pos);

                switch (cevt.eventtype) {
                    case 0:
                        found = doLunar(cevt, dbl);
                        break;
                    case 1:
                        found = donakshatra(cevt, dbl);
                        break;
                    case 2:
                        found = doWeek(cevt, dbl);
                        break;
                }
            }


            return found;
        }

        private boolean doWeek(Events cevt, double[] dbl) {
            if ((int) dbl[13] == dbl[13])
                if ((int) dbl[13] == cevt.lunarmonth && (int) dbl[9] == cevt.thithi) {
                    if ((int) dbl[0] >7 && (int) dbl[0] <= 14) {
                        return true;
                    }
                }
            return false;
        }

        private boolean donakshatra(Events cevt, double[] dbl) {
            if ((int) dbl[13] == dbl[13])
                if (cevt.thithi == null && (int) dbl[13] == cevt.lunarmonth && (int) dbl[2] == cevt.nakshatra) {
                    return true;
                }
            return false;
        }

        private boolean doLunar(Events cevt, double[] dbl) {
            int kshaythithi = 0;
            if (dbl[6] != -1.1111) kshaythithi = (int)dbl[0] + 1;
            if ((int) dbl[13] == dbl[13])
                if ((int) dbl[13] == cevt.lunarmonth &&
                        ((int) dbl[0] == cevt.thithi || kshaythithi == cevt.thithi)) {
                    return true;
                }
            return false;
        }
    }


}