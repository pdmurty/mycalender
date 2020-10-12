package com.example.mycalender;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Geonames")
public class Geonames {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "featureid")
    public int id;
    @NonNull@ColumnInfo(name = "country_code")
    public String country_code;
    @NonNull@ColumnInfo(name = "asciiname")
    public String name;
    @NonNull@ColumnInfo(name = "admin1_code")
    public String admincode;
    @ColumnInfo( name = "latitude")
    public double latitude;
    @ColumnInfo( name = "longitude")
    public double longitude;
    @NonNull@ColumnInfo(name = "timezone")
    public String timezone;
    @ColumnInfo(name = "usercode")
    public int usercode;
    public void GeoName(){}

    public String getGeoName() {return this.name; }
}
