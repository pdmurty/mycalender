package com.example.mycalender.ui.main;

import android.app.Activity;
import android.content.Context;
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

import com.example.mycalender.Eventdays;
import com.example.mycalender.Events;
import com.example.mycalender.EventsDao;
import com.example.mycalender.EventsRoomDb;
import com.example.mycalender.EventsVwModel;
import com.example.mycalender.R;
import com.example.mycalender.ShowEvents;

import java.util.List;

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
    String[] wkday ;
    static EventsRoomDb db;
    static EventsDao dao;
    public int myear;
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
        levents= getResources().getStringArray(R.array.lunarEvents);
        sankrathis= getResources().getStringArray(R.array.solar_months);
        kartharis= getResources().getStringArray(R.array.Nakshatras);
        wkday= getResources().getStringArray(R.array.wkdays);
    }
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
            View root = inflater.inflate(/*R.layout.fragment_tab_demo*/
                R.layout.activity_show_events
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
            if(mEventlist!=null)
                return mEventlist.size();
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
                day.setText(Integer.toString(evt.day) +"-" + wkday[evt.weekday]);
                if (evt.eventid < 100)
                    mTxtVw.setText(levents[evt.eventid - 1]);
                else if(evt.eventid < 200)
                    mTxtVw.setText(sankrathis[evt.eventid - 100]+"సంక్రాంతి");

               else //(evt.eventid < 200)
                    mTxtVw.setText(kartharis[evt.eventid - 200]+"కార్తె");

            }
        }
    }
}
