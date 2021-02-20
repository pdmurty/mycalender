package com.example.mycalender;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Eventdays")
public class Eventdays {

    @ColumnInfo(name = "Year")
    public Integer year;
    @ColumnInfo(name = "month")
    public Integer month;
    @ColumnInfo(name = "day")
    public Integer day;
    @ColumnInfo(name = "weekday")
    public Integer weekday;
    @ColumnInfo(name = "eventid")
    public int eventid;
    @PrimaryKey
    @ColumnInfo(name = "id")
    public Integer id;
}
