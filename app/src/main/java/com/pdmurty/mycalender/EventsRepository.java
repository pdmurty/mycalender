package com.pdmurty.mycalender;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;

public class EventsRepository {
    private static EventsDao mevtDao;
    private static EventsRoomDb db;
    private LiveData<List<Events>> mAllEvents;
    EventsRepository(Context context){
        Log.d("Roomdb", "repo ");
        db = EventsRoomDb.getDatabase(context);
        mevtDao = db.getDao();
        mAllEvents = mevtDao.getAllEvents();
    }
    LiveData<Integer> getEventCount(int year){
        return mevtDao.getEventCount(year);
    }
    LiveData<List<Eventdays>> getEventsofMonth(int mpageindex, int year){
        return mevtDao.getEventsofMonth(mpageindex,year);
    }
    public LiveData<List<Events>>getmAllEvents(){ return mAllEvents;}
}
