package com.pdmurty.mycalender;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "admincodes")
public class Admins {
    @NonNull
    @ColumnInfo(name= "admin1code")
    String admin1code;
    @PrimaryKey
    @NonNull
    @ColumnInfo(name ="id")
    int id;
    @NonNull
    @ColumnInfo(name = "country")
    String country;
    @NonNull
    @ColumnInfo(name = "asciiname")
    String adminname;

}
