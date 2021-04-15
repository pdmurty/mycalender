package com.pdmurty.mycalender;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Events.class,Eventdays.class},version = 2,exportSchema = false)
public abstract class EventsRoomDb extends RoomDatabase {
    public abstract EventsDao getDao();
    private static Context appContext;
    private static EventsRoomDb Instance;
    public static EventsRoomDb getDatabase(Context context){

        if(Instance == null){
            appContext = context.getApplicationContext();
            Instance = Room.databaseBuilder(context.getApplicationContext(),EventsRoomDb.class,"events")
                    .createFromAsset("databases/events.db")
                    .addMigrations(new Migration(1,2) {
                        @Override
                        public void migrate(@NonNull SupportSQLiteDatabase database) {
                            int version = database.getVersion();


                        }
                    })
                    .build();
        }

        return Instance;
    }

}
