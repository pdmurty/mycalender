package com.pdmurty.mycalender;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "countries")
public class Countries {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "iso")
    String CountryCode;
    @NonNull
    @ColumnInfo(name = "name")
    String countryName;
}
