package com.example.mycalender;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.List;

public class EventsVwModel extends AndroidViewModel {


    private MutableLiveData<Integer> mIndex = new MutableLiveData<>();
    private MutableLiveData<Integer> mYear = new MutableLiveData<>();
    private LiveData<Integer> mYearret = Transformations.map(mYear, new Function<Integer, Integer>() {
        @Override
        public Integer apply(Integer input) {
            return input ;
        }
    });
    private LiveData<Integer> mTab = Transformations.map(mIndex, new Function<Integer, Integer>() {
        @Override
        public Integer apply(Integer input) {
            return input ;
        }
    });

    public void setIndex(int index) {
        mIndex.setValue(index);
    }


    public void setYear(int year) {
        mYear.setValue(year);
    }

    public LiveData<Integer> getYear() {

        return mYearret;
    }
    public LiveData<Integer> getTab() {

        return mTab;
    }

    private LiveData<List<Events>> mAllEvents;
    private EventsRepository mrepo;


    public EventsVwModel(@NonNull Application application) {
        super(application);
        mrepo = new EventsRepository(application);
        mAllEvents = mrepo.getmAllEvents();
    }

    public LiveData<List<Events>> getmAllEvents() {return mAllEvents;}
    LiveData<Integer> getEventCount(int year){ return mrepo.getEventCount(year);}
    public LiveData<List<Eventdays>> getEventsofMonth(int month, int year){


        return mrepo.getEventsofMonth(month,year);
    }
}
