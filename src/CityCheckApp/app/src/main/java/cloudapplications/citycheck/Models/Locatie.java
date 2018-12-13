package cloudapplications.citycheck.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Locatie {

    @SerializedName("id")
    @Expose
    private int Id;

    @SerializedName("lat")
    @Expose
    private double Lat;

    @SerializedName("long")
    @Expose
    private double Long;

    public Locatie(double lat, double aLong) {
        Lat = lat;
        Long = aLong;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public double getLat() {
        return Lat;
    }

    public void setLat(double lat) {
        Lat = lat;
    }

    public double getLong() {
        return Long;
    }

    public void setLong(double aLong) {
        Long = aLong;
    }
}
