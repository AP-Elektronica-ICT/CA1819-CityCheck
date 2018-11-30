package cloudapplications.citycheck.Models;

public class Locatie {
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

    private int Id;
    private double Lat;
    private double Long;
}
