package com.pdmurty.mycalender;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pdmurty.mycalender.ui.main.EventsPager;
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
    Button  yrBut;
    TextView  yrTxt;
    int maxdays[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    ProgressBar pbar;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int year = 2020;
        LocalManager.setLocale(this);
        setContentView(R.layout.tabbed_activity);
        levents = getResources().getStringArray(R.array.lunarEvents);
        eventDb = EventsRoomDb.getDatabase(this);
        eventsdao = eventDb.getDao();
        pbar = findViewById(R.id.progress);
        pbar.setVisibility(View.GONE);
        pager = new EventsPager( getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(pager);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        EventsAync eventsAync = new EventsAync();
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        yrTxt = findViewById(R.id.yeartxt);
        yrTxt.setText(R.string.events_header);
        yrBut = findViewById(R.id.year);
        yrBut.setText(String.valueOf(year));
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
        double dTzoffset;
        List<Events> pendingEvents;
        boolean newpendingevent = false;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbar.setVisibility(View.VISIBLE);
            yrTxt.setText(R.string.prog_header);
            int tzone =MainActivity.mPreferences.getInt("KEY_TZONE",19800000);
            dTzoffset =  (double) tzone /3600000;  //default Tzone set to INDIA
        }

        protected Integer doInBackground(Integer... years) {
            updateEvents(years[0].intValue());
            return years[0];
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            yrBut.setText(result.toString());
            yrTxt.setText(R.string.events_header);
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
            double[] dblprev=null;
            double[] dblsank;
            double[] dblkarthari;
            String txt;
            boolean vikuntafound=false;
            double[] dbleclarr; // arr of 7*5 elements 7 for each eclips, 2 solar eclips + 3 lunar if exist.
            // 1.type, 2.year,3.month,4.day,5.weekday,6.time begin, 7. time end

            int pos = 0;
            int maxday;
            double[] geo = new double[3];
            Eventdays cevt ;
            Eventdays vinkuntaevt = new Eventdays();
            mEventlist =  eventsdao.getAllEventsD();
            int listSize = mEventlist.size();
            listEventdays = new ArrayList<Eventdays>();
            pendingEvents = new ArrayList<Events>();
            /////////////////// initiate swiss ephimiris LIB
            Swlib.SWSetSidmode(1, 0, 0);
            Events tmpevt;
            ////////////////eclips///////////////////////
            int eclskip;
            dbleclarr=Swlib.CalcSolarAndLunarEclipse(year, dTzoffset);
            for(int ecl=0;ecl<5;++ecl){
                eclskip=7*ecl;
               if(dbleclarr[eclskip]==0) continue;;
                cevt = new Eventdays();
                cevt.day = (int)dbleclarr[3+eclskip];//day;
                cevt.month = (int)dbleclarr[2+eclskip];//month + 1;
                cevt.year = (int)dbleclarr[1+eclskip];
                cevt.time = dbleclarr[5+eclskip];
                cevt.timeend = dbleclarr[6+eclskip];
                if(ecl<2)
                    cevt.eventid = 91;
                else
                    cevt.eventid = 92;
                cevt.weekday = (int)dbleclarr[4+eclskip];
                listEventdays.add(cevt);
             }

            ////////////////////////////////////////////

            dbl = Swlib.WritePanchang(year, 0, 1, dTzoffset);
            int lm =(int) dbl[13];
            int ld = (int) dbl[0];
            tmpevt = mEventlist.get(0);


            while(lm>tmpevt.lunarmonth || ld>tmpevt.thithi) {
                mEventlist.remove(0);
                if(tmpevt.event!=0)
                mEventlist.add(tmpevt);
                tmpevt = mEventlist.get(0);

            }

            dblkarthari = Swlib.SWeCalcKarthariDays(year, 5.5);
            int skip=0;
            for(int i=0 ;i<27;++i){

                skip=i*5;
                cevt = new Eventdays();
                cevt.day = (int)dblkarthari[1+skip];//day;
                cevt.month = (int)dblkarthari[4+skip];//month + 1;
                cevt.year = year;
                cevt.time = dblkarthari[2+skip];
                cevt.eventid = 200+(int)dblkarthari[3+skip];//mEventlist.get(pos).event;
                cevt.weekday = (int)dblkarthari[skip];
                listEventdays.add(cevt);
            }
            /////////////////


            for (int month = 0; month < 12 && pos < listSize; ++month) {
                dblsank =Swlib.SWeCalcNextSankaranthi(year,month,dTzoffset);
                cevt = new Eventdays();
                cevt.day = (int)dblsank[1];//day;
                cevt.month = month + 1;
                cevt.year = year;
                cevt.time = dblsank[2];
                cevt.eventid = 100+(int)dblsank[3];//mEventlist.get(pos).event;
                cevt.weekday = (int)dblsank[0];
                listEventdays.add(cevt);
                if((int)dblsank[3]==9)DoMakara(dblsank,year,month);
                maxday = maxdays[month];
                if (year % 4 == 0 && month == 1) ++maxday;
                for (int day = 1; day <= maxday && pos < listSize; ++day) {
                    dbl = Swlib.WritePanchang(year, month, day, dTzoffset);
                AddGregEvent(year,month,day,(int)dbl[9]);
                    if(pendingEvents.size()!=0){
                      int event = DoPendingEvents(dbl);
                      if(event!=-1){
                          cevt = new Eventdays();
                          cevt.day = day;
                          cevt.month = month + 1;
                          cevt.year = year;
                          cevt.eventid = event;
                          cevt.weekday = (int) dbl[9];
                          cevt.highlight=1;
                          listEventdays.add(cevt);
                      }
                    }
                    while (NextEvent(pos, dbl)) {
                        if(!newpendingevent) {
                            cevt = new Eventdays();
                            cevt.day = day;
                            cevt.month = month + 1;
                            cevt.year = year;
                            cevt.eventid = mEventlist.get(pos).event;
                            cevt.weekday = (int) dbl[9];
                            cevt.highlight = mEventlist.get(pos).highlight;
                            Events curevt =mEventlist.get(pos);
                            if(dblprev!=null){
                                if(curevt.eventtype==3  ){
                                    if(dbl[1]<12.0) {
                                        cevt.day = day-1;
                                        if(cevt.day==0){
                                            cevt.month = month;
                                            cevt.day = maxdays[month-1];
                                        }
                                        cevt.weekday = (int)dblprev[9];
                                      }
                                }
                                if(curevt.eventtype==4){
                                    if(dbl[1]<dbl[8]) {
                                        cevt.day = day-1;
                                        if(cevt.day==0){
                                            cevt.month = month;
                                            cevt.day = maxdays[month-1];
                                        }
                                        cevt.weekday = (int)dblprev[9];
                                    }
                                }
                                if(curevt.eventtype==5) {
                                if (dbl[1] < 24.0) {
                                    cevt.day = day - 1;
                                    if (cevt.day == 0) {
                                        cevt.month = month;
                                        cevt.day = maxdays[month - 1];
                                    }
                                    cevt.weekday = (int) dblprev[9];
                                }
                            }
                            }
                            if(month==0 && day<14 ) // vikunta ekadhasi
                            if(mEventlist.get(pos).thithi==10||mEventlist.get(pos).thithi==25)
                            {   cevt.eventid =90; cevt.highlight=1; vikuntafound = true;}
                            if(!vikuntafound && month==11 && day>14 )
                            if(mEventlist.get(pos).thithi==10||mEventlist.get(pos).thithi==25)
                            { cevt.eventid =90;cevt.highlight=1;}
                            if( month==11 && day==31)  // special condition 2 vikunta in 1 year.
                                if(mEventlist.get(pos).thithi==10||mEventlist.get(pos).thithi==25)
                                { cevt.eventid =90;cevt.highlight=1;}
                            listEventdays.add(cevt);
                        }
                        ++pos;
                        newpendingevent = false;


                        publishProgress((int)(pos*100/listSize));
                    }

                    dblprev= dbl;
                } //end of day

            }// end of month
            eventsdao.UpdateEventdays(listEventdays);


        }

        private void DoMakara(double[] dblsank, int year, int month) {

            Eventdays cevt = new Eventdays();
            cevt.day = (int)dblsank[1]-1;//day;
            cevt.month = month + 1;
            cevt.year = year;
            //cevt.time = dblsank[2];
            cevt.eventid = 112;
            cevt.weekday = (int)dblsank[0]-1;
            if(cevt.weekday<0)cevt.weekday+=7;
            if(cevt.weekday>6)cevt.weekday%=7;
            listEventdays.add(cevt);
            cevt = new Eventdays();
            cevt.day = (int)dblsank[1]+1;//day;
            cevt.month = month + 1;
            cevt.year = year;
            //cevt.time = dblsank[2];
            cevt.eventid = 113;
            cevt.weekday = (int)dblsank[0]+1;
            if(cevt.weekday<0)cevt.weekday+=7;
            if(cevt.weekday>6)cevt.weekday%=7;
            listEventdays.add(cevt);
        }

        private void AddGregEvent(int year, int month, int day, int weekday) {
           Eventdays cevt = new Eventdays();
            cevt.highlight=2;
            if (month==0 && day==1){
                cevt.year=year;
                cevt.month=month+1;
                cevt.day=day;
                cevt.weekday=weekday;
                cevt.eventid=400;
                listEventdays.add(cevt);

            }
            if (month==0 && day==26){
                cevt.year=year;
                cevt.month=month+1;
                cevt.day=day;
                cevt.weekday=weekday;
                cevt.eventid=401;
                listEventdays.add(cevt);
             //   Log.d("EVT","addevt=401");
            }
            if (month==7 && day==15){
                cevt.year=year;
                cevt.month=month+1;
                cevt.day=day;
                cevt.weekday=weekday;
                cevt.eventid=402;
                listEventdays.add(cevt);
            }
            if (month==9 && day==2){
                cevt.year=year;
                cevt.month=month+1;
                cevt.day=day;
                cevt.weekday=weekday;
                cevt.eventid=403;
                listEventdays.add(cevt);
            }
            if (month==11 && day==25){
                cevt.year=year;
                cevt.month=month+1;
                cevt.day=day;
                cevt.weekday=weekday;
                cevt.eventid=404;
                listEventdays.add(cevt);
            }

        }

        private int DoPendingEvents(double[] dbl) {
            boolean found=false;
            int pos=0;

            Events cEvent = pendingEvents.get(pos);
            switch (cEvent.eventtype) {
                case 0:
                    found = doLunar(cEvent, dbl);
                    break;
                case 1:
                    found = donakshatra(cEvent, dbl);
                    break;
                case 2:
                    found = doWeek(cEvent, dbl);
                    break;
            }
            if(found){

                pendingEvents.remove(pos);
                return cEvent.event;

            }
            return -1;
        }

        private boolean NextEvent(int pos, double[] dbl) {
            boolean found = false;

            if (pos < mEventlist.size()) {
                Events cevt = mEventlist.get(pos);
                switch (cevt.eventtype) {
                    case 0:
                    case 3:
                    case 4:
                    case 5:
                        found = doLunar(cevt, dbl);
                        break;
                    case 1:
                        if(! donakshatra(cevt, dbl)){
                            pendingEvents.add(cevt);
                            newpendingevent = true;
                        }
                        //break;
                        return true;
                    case 2:
                        if(! doWeek(cevt, dbl)) {
                            pendingEvents.add(cevt);
                            newpendingevent = true;
                        }
                        return true;
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