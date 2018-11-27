package cloudapplications.citycheck;
import android.location.Location;
import java.util.List;

public class DoelLocatie {



    //doel variabele
    private int id;
    private String name;
    private Location loc;
    private String vragen;
    //loc variable
    private Double Lat;
    private Double Long;

    DoelLocatie(String name, double Lat, double Long, Location loc, String vragen){
        this.name = name;
        this.Lat = Lat;
        this.Long = Long;
        this.loc = loc;
        this.vragen = vragen;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getVragen() {
        return vragen;
    }
    public void setVragen(String vragen) {
        this.vragen = vragen;
    }

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
