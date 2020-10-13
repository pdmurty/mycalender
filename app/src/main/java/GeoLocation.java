import androidx.room.ColumnInfo;

public class GeoLocation {
    @ColumnInfo(name = "asciiname")
    String name;
    @ColumnInfo(name = "longitude")
    double lon;
    @ColumnInfo(name = "latitude")
    double lat;
    @ColumnInfo(name = "offset")
    double tzOffset;

}
