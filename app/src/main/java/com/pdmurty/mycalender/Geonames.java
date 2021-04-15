package com.pdmurty.mycalender;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Geonames")
public class Geonames {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "featureid")
    public Integer id;
    @NonNull@ColumnInfo(name = "country_code")
    public String country_code;
    @NonNull@ColumnInfo(name = "asciiname")
    public String name;
    @NonNull@ColumnInfo(name = "admin1_code")
    public String admincode;
    @NonNull@ColumnInfo( name = "latitude")
    public Double latitude;
    @ColumnInfo( name = "longitude")
    @NonNull public Double longitude;
    @NonNull@ColumnInfo(name = "timezone")
    public String timezone;
    @NonNull@ColumnInfo(name = "usercode" )
    public Integer usercode;
    public void GeoName(){}
    public String getGeoName() {return this.name; }
}
