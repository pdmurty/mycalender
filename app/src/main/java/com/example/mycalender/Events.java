package com.example.mycalender;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Events")
public class Events {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "eventid")
    public int event;
    @ColumnInfo(name = "lunarmonth")
    public Integer lunarmonth;
    @ColumnInfo(name = "thithi")
    public Integer thithi;
    @ColumnInfo( name = "solarmonth")
    public Integer solarmonth;
    @ColumnInfo( name = "nakshatra")
    public Integer nakshatra;
    @ColumnInfo(name = "year")
    public Integer year;
    @ColumnInfo(name = "month")
    public Integer month;
    @ColumnInfo(name = "day")
    public Integer day;
    @ColumnInfo(name = "eventtype")
    public Integer eventtype;

}
