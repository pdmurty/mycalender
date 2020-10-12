package com.example.mycalender;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Geonames.class,Countries.class,Admins.class, Timzones.class },
                                  version = 2,exportSchema = false)
public abstract class GeoRoomDb extends RoomDatabase {
    public abstract GeoDao getDao();
    private static Context appContext;
    private static GeoRoomDb Instance;
    public static GeoRoomDb getDatabase(Context context){
        if(Instance == null){
            appContext = context.getApplicationContext();
                      Instance = Room.databaseBuilder(context.getApplicationContext(),GeoRoomDb.class,"geonames")
                       .createFromAsset("databases/geonames.db")
                       .addMigrations(new Migration(1,2) {
                        @Override
                        public void migrate(@NonNull SupportSQLiteDatabase database) {
                        int version = database.getVersion();
                        Log.d("Roomdb", "migarted - " + version);

                        }
                    })
                    .build();
        }

        return Instance;
    }

}
