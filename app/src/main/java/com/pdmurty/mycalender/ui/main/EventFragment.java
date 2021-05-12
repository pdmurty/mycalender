package com.pdmurty.mycalender.ui.main;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pdmurty.mycalender.Eventdays;
import com.pdmurty.mycalender.EventsDao;
import com.pdmurty.mycalender.EventsRoomDb;
import com.pdmurty.mycalender.EventsVwModel;
import com.pdmurty.mycalender.LocalManager;
import com.pdmurty.mycalender.R;

import java.util.List;

import static com.pdmurty.mycalender.ui.main.ephFragment.resource;

public class EventFragment extends Fragment {
    private static final String ARG_PAGE_NUMBER = "page_number";

    Context mContext;
    int mpageindex;
    List<Eventdays> mEventlist;
    EventsVwModel mVwmodel;
    EventsAdapter mAdapter ;
    String[] levents ;
    String[] sankrathis ;
    String[] kartharis ;
    String[] gregevents ;
    String[] wkday ;
    String thela;
    String sayam;
    String madhya;
    String rathri;
    String full;
    String udaya;
    String karthari;
    String sankranthi;
    static EventsRoomDb db;
    static EventsDao dao;
    public int myear;
    int colorPrimaryDark,colorPrimary;
    public static EventFragment newInstance(int index, int year) {
        EventFragment fragment = new EventFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_PAGE_NUMBER, index);
        fragment.setArguments(bundle);

        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVwmodel = new ViewModelProvider( requireActivity()).get(EventsVwModel.class);

        if (getArguments() != null) {
            mpageindex = getArguments().getInt(ARG_PAGE_NUMBER);

        }

        db = EventsRoomDb.getDatabase(getContext());
        dao= db.getDao();
        mContext=getContext();
        LocalManager.setLocale(mContext);
        Resources resource =getResources();
        colorPrimaryDark = resource.getColor(R.color.colorPrimaryDark);
        colorPrimary = resource.getColor(R.color.colorPrimary);

        levents= resource.getStringArray(R.array.lunarEvents);
        sankrathis= resource.getStringArray(R.array.solar_months);
        kartharis= resource.getStringArray(R.array.Nakshatras);
        gregevents= resource.getStringArray(R.array.gregevents);
        wkday= resource.getStringArray(R.array.wkdays);
        thela        = resource.getString(R.string.the);
        sayam        = resource.getString(R.string.sayam);
        madhya       = resource.getString(R.string.Madhya);
        rathri       = resource.getString(R.string.rathri);
        full         = resource.getString(R.string.full);
        udaya        = resource.getString(R.string.udayam);
        karthari = resource.getString(R.string.karthari);
        sankranthi =  resource.getString(R.string.sankranthi);

    }
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
            View root = inflater.inflate(/*R.layout.fragment_tab_demo*/
                R.layout.events_page_view
                , container, false);

         final RecyclerView recycler = root.findViewById(R.id.recylerEvts);
         mAdapter = new EventsAdapter(mContext);
        recycler.setAdapter(mAdapter);
        recycler.setLayoutManager( new LinearLayoutManager(mContext));
        mVwmodel.getYear().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer year) {
                myear = year;
                updateEvents(year,mpageindex);
            }
        });

     return root;
    }
    void updateEvents(final int year, final int month)
    {


        mVwmodel.getEventsofMonth(month,year).observe(getViewLifecycleOwner(), new Observer<List<Eventdays>>() {

            @Override
            public void onChanged(List<Eventdays> events) {

                       if( myear == year) {
                           mEventlist = events;
                           mAdapter.notifyDataSetChanged();
                       }
                    }
        });
    }

    class EventsAdapter extends RecyclerView.Adapter{

        Context parent;
        LayoutInflater m_Inflater;

        EventsAdapter(Context context){

            m_Inflater = LayoutInflater.from(context);
            parent = context;

        }


        @NonNull
        @Override
        public EventsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view_item = m_Inflater.inflate(R.layout.eventlist_item, parent,false);

            return new EventsHolder(view_item);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
              ((EventsHolder)holder).bind( mEventlist.get(position));

        }

        @Override
        public int getItemCount() {
            if(mEventlist!=null) {

                return mEventlist.size();
            }
            else return 0;
        }

        class EventsHolder extends RecyclerView.ViewHolder{

            TextView mTxtVw;
            TextView year;
            TextView month;
            TextView day;

            public EventsHolder(@NonNull View itemView) {
                super(itemView);
                day = itemView.findViewById(R.id.textwkday);
                mTxtVw = itemView.findViewById(R.id.textEvt);
            }
            public void bind(Eventdays evt) {
                day.setText(evt.day +"-" + wkday[evt.weekday]);
                if (evt.eventid < 91) {

                        if (evt.highlight == 1) mTxtVw.setTextColor(colorPrimary);
                        mTxtVw.setText(levents[evt.eventid - 1]);


                }
                else if (evt.eventid < 100) {
                    mTxtVw.setTextColor(colorPrimaryDark);
                    mTxtVw.setText(levents[evt.eventid - 1]
                            + HourToString(evt.time, 6.0) + "--"
                            + HourToString(evt.timeend, 6.0));
                }
                else if(evt.eventid < 200) {
                    if (evt.time != null)
                        mTxtVw.setText(sankrathis[evt.eventid - 100] + sankranthi + HourToString(evt.time, 6.0));
                    else
                        mTxtVw.setText(sankrathis[evt.eventid - 100]);
                }
                else if(evt.eventid < 300)
                    mTxtVw.setText(kartharis[evt.eventid - 200]+karthari +HourToString( evt.time,  6.0));
                else {
                    mTxtVw.setTextColor(colorPrimaryDark);
                    mTxtVw.setText(gregevents[evt.eventid - 400]);
                }
           }
            String HourToString(double hrs, double sunrise){


                String strPrefix = "";
                if(sunrise!=0)
                    if(hrs>24+sunrise) return full;

                if(hrs>=24) hrs-=24;
                if (hrs>=3 && hrs<sunrise){strPrefix=thela;}
                if (hrs>=sunrise && hrs<12) {strPrefix = udaya; }
                if (hrs>=12 && hrs<16) {strPrefix = madhya; }
                if (hrs>=16 && hrs<19) {strPrefix = sayam; }
                if (hrs>=19 || hrs<3 ){strPrefix = rathri; }
                if (hrs>13 ) hrs-=12;
                double min = hrs*60;
                String strhrs =strPrefix + (int) min / 60;
                strhrs += ":";
                strhrs += String.format("%02d",(int)min%60);
                return strhrs;
            }
        }
    }
}
