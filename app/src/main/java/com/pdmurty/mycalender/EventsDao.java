package com.pdmurty.mycalender;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface EventsDao {
    @Query("SELECT * FROM EVENTS")
    LiveData<List<Events>> getAllEvents();
    @Query("SELECT * FROM EVENTS")
    List<Events> getAllEventsD();

    @Update
    int UpdateEvents(List<Events> events);

    @Query("SELECT COUNT(*) FROM EVENTDAYS where year = :year" )
    LiveData<Integer> getEventCount(int year);

    @Query("SELECT * FROM EVENTDAYS where month= :month AND year = :year order by day")
    LiveData<List<Eventdays>> getEventsofMonth(int month,int year);

    @Insert
    void UpdateEventdays(List<Eventdays> listEventdays);

}
