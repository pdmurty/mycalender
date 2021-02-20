package com.example.mycalender.ui.main;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class PageViewModel extends ViewModel {

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


}