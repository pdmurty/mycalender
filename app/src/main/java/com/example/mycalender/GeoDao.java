package com.example.mycalender;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;
@Dao
public interface GeoDao {
    @Insert( onConflict = OnConflictStrategy.IGNORE)
    public void insert(Geonames name);
    @Delete
    public void delete(Geonames name);
    @Query("DELETE FROM Geonames")
    public void deleteAll();
    @Query("SELECT * FROM GEONAMES where country_code = 'IN' ")
    public LiveData<List<Geonames>> getAllNames();
    @Query("SELECT * FROM GEONAMES where country_code = :country " +
            " AND  admin1_code = :admin" +
            " AND asciiname like :like" +
            " order by asciiname asc ")
    public LiveData<List<Geonames>> getAllNames(String country, String admin, String like);
    @Query("SELECT * FROM COUNTRIES")
    LiveData<List<Countries>> getCountries();
    @Query("SELECT * FROM ADMINCODES where country = :country_code order by asciiname asc ")
    public LiveData<List<Admins>> getAllAdmins( String country_code);
    @Query("SELECT * FROM TIMEZONES")
    public LiveData<List<Timezones>> getTimeZones();
    @Query(" select g.asciiname as name, g.latitude as lat, g.longitude as lon , tz.`offset` as `offset` " +
            " from geonames g,  TIMEZONES tz  " +
            " where g.timezone = tz.name  " +
            " AND g.country_code = :country " +
            " AND g.admin1_code = :admin" +
            " AND g.asciiname like :like" +
            " order by asciiname asc ")
    public LiveData< List<GeoLoc>> getgeolocs (String country , String admin, String like);
    static class GeoLoc{
        public String name;
        public double lat;
        public double lon;
        public double offset;
    }

}
