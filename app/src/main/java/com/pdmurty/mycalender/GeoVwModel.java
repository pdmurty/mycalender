package com.pdmurty.mycalender;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class GeoVwModel extends AndroidViewModel {
    private LiveData<List<Geonames>> mAllnames;
    private GeoRepository mrepo;
    public GeoVwModel(Application application) {
        super(application);
        mrepo = new GeoRepository(application);
        mAllnames = mrepo.getAllNames();

    }
    public LiveData<List<Geonames>> getAllnames() {return mAllnames;}
    public LiveData<List<Geonames>> getAllnames(String Country_code, String admin, String like) {
        return mrepo.getAllnames(Country_code, admin, like);
    }
    public LiveData<List<Countries>>getAllCountries(){
        return mrepo.getAllCountries();
    }

    public LiveData<List<Admins>> getAllAdmins(String countrycode) {
        return mrepo.getAllAdmins(countrycode);
    }
    public  LiveData<List<GeoDao.GeoLoc>> getgeolocs (String country , String admin, String like) {
        return mrepo.getgeolocs(country, admin, like);
    }
}
