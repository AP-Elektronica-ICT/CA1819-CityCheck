package cloudapplications.citycheck;
import android.location.Location;
import java.util.List;

public class DoelLocatie {

    //doel variabele
    private String name;
    private Location loc;
    //loc variable
    private Double Lat;
    private Double Long;


    public String getVragen() {
        return vragen;
    }

    public void setVragen(String vragen) {
        this.vragen = vragen;
    }

    private String vragen;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLat() {
        return Lat;
    }

    public void setLat(Double lat) {
        Lat = lat;
    }

    public Double getLong() {
        return Long;
    }

    public void setLong(Double aLong) {
        Long = aLong;
    }

    public Location getLoc() {
        return loc;
    }

    public void setLoc(Location loc) {
        this.loc = loc;
    }





}
