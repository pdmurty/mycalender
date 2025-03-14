package com.pdmurty.mycalender;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "TIMEZONES")
public class Timezones {
   @PrimaryKey
   @NonNull
    @ColumnInfo(name = "name")
    String zonename;
    @ColumnInfo(name = "country_code")
    String country_code;
    @ColumnInfo(name="offset")
    double zoneoffset;
}
