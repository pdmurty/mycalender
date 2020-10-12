package com.example.mycalender;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;

public class GeoRepository {
    private static GeoDao mgeoDao;
    private static GeoRoomDb db;
    private LiveData<List<Geonames>> mAllNames;
    GeoRepository(Context context){
         db = GeoRoomDb.getDatabase(context);
         mgeoDao = db.getDao();
         mAllNames = mgeoDao.getAllNames();
        }

     public LiveData<List<Geonames>>getAllNames(){ return mAllNames;}

     public LiveData<List<Countries>>getAllCountries(){
        return mgeoDao.getCountries();}

        public LiveData<List<Geonames>> getAllnames(String country_code,String admin, String like) {
            return mgeoDao.getAllNames(country_code, admin, like);
        }

        public LiveData<List<Admins>> getAllAdmins(String country_code) {
            return mgeoDao.getAllAdmins(country_code);
        }
    public LiveData< List<GeoDao.GeoLoc>> getgeolocs (String country , String admin, String like){
        return mgeoDao.getgeolocs (country , admin, like);
    }
}
